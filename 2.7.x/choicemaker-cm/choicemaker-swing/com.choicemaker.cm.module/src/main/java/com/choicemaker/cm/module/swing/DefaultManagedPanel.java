/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module.swing;

import javax.swing.text.Document;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.module.IModule;
import com.choicemaker.cm.module.IModuleController;
import com.choicemaker.cm.module.IModuleController.IUserInterface;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.platform.CMPlatformUtils;

/**
 * Panel that loads a configuration of {@link IModule} from the
 * registry, or uses an instance of {@link #DefaultModule} if no
 * module configuration exists in the registry.
 * @author rphall
 */
public class DefaultManagedPanel extends AbstractTabbedPanel implements IModelMakerAware {

	private static final long serialVersionUID = 1L;
	private final IModuleController module;
	private Object modelMakerObject;
	
	@Override
	public String getTabName() {
		return DefaultManagedPanel.class.getName();
	}

	public DefaultManagedPanel(Document d, Object mmo) {
		super();
		// Fail fast
		if (d == null || mmo == null) {
			throw new IllegalArgumentException("null argument");
		}
		this.modelMakerObject = mmo;
		this.module = createModule(d);
		IUserInterface ui = this.module.getUserInterface();
		if (ui instanceof IPanelControl) {
			IPanelControl ipc = (IPanelControl) ui;
			ipc.setManagedPanel(this);
		}
	}

	/**
	 * Create an instance of an {@link #IModule} configuration from the
	 * PluginRegistry. The configuration must be unique within the registry. If
	 * no configuration exists within the register, an instance of
	 * {@link #DefaultModule} is returned
	 * 
	 * @return a non-null instance of a IModule configuration.
	 */
	public IModuleController createModule(Document d) {

		// Create a default builder
		IModuleController retVal = new DefaultModuleController(d);

		// Replace the default with a configured instance, if one exists
		CMExtensionPoint pt =
			CMPlatformUtils
					.getPluginRegistry()
					.getExtensionPoint(
							ChoiceMakerExtensionPoint.CM_MODELMAKER_PLUGGABLECONTROLLER);
		if (pt != null) {
			CMExtension[] extensions = pt.getExtensions();
			if (extensions.length > 1) {
				throw new Error("too many extensions of IModule: "
						+ extensions.length);
			} else if (extensions.length == 1) {
				CMExtension extension = extensions[0];
				CMConfigurationElement[] els =
					extension.getConfigurationElements();
				if (els.length != 1) {
					throw new Error(
							"too many configurations of the default module: "
									+ extensions.length);
				}
				CMConfigurationElement element = els[0];
				try {
					retVal =
						(IModuleController) element
								.createExecutableExtension("class");
					if (retVal instanceof IModelMakerAware) {
						IModelMakerAware mma = (IModelMakerAware) retVal;
						mma.setModelMakerObject(this.getModelMakerObject());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					String msg =
						"Error creating default configuration: "
								+ ex.getMessage();
					throw new Error(msg, ex);
				}
			}
		}

		return retVal;
	}

	public IModuleController getModule() {
		return module;
	}

	@Override
	public void setModelMakerObject(Object modelMakerObject) {
		this.modelMakerObject = modelMakerObject;
	}

	@Override
	public Object getModelMakerObject() {
		return modelMakerObject;
	}

}

