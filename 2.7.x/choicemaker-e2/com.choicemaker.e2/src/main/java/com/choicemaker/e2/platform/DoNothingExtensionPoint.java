package com.choicemaker.e2.platform;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.CMPluginDescriptor;

public class DoNothingExtensionPoint implements CMExtensionPoint {

	private static final DoNothingExtensionPoint instance =
		new DoNothingExtensionPoint();

	public static DoNothingExtensionPoint getInstance() {
		return instance;
	}

	private DoNothingExtensionPoint() {
	}

	@Override
	public CMConfigurationElement[] getConfigurationElements() {
		return new CMConfigurationElement[0];
	}

	@Override
	public CMPluginDescriptor getDeclaringPluginDescriptor() {
		return null;
	}

	@Override
	public CMExtension getExtension(String extensionId) {
		return null;
	}

	@Override
	public CMExtension[] getExtensions() {
		return new CMExtension[0];
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public String getSchemaReference() {
		return null;
	}

	@Override
	public String getSimpleIdentifier() {
		return null;
	}

	@Override
	public String getUniqueIdentifier() {
		return null;
	}

}
