/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.xmlconf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.PropertyNames;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.compiler.ICompiler;
import com.choicemaker.cm.core.configure.ChoiceMakerConfiguration;
import com.choicemaker.cm.core.configure.ChoiceMakerConfigurator;
import com.choicemaker.cm.core.configure.MachineLearnerPersistence;
import com.choicemaker.cm.core.configure.ProbabilityModelPersistence;
import com.choicemaker.cm.core.gen.Eclipse2GeneratorPluginFactory;
import com.choicemaker.cm.core.report.Reporter;
import com.choicemaker.cm.core.util.FileUtilities;

/**
 * XML configuration file reader.
 *
 * A ChoiceMaker configuration file consists of the following three types of elements:
 * <ul>
 *   <li>An arbitrary number of modules. Modules are self-initializing, that is, they
 *       list the class to be used for their initialization. All modules are initialized
 *       by calling <code>initModules</code>, which is also called by <code>init</code>.
 *       An example of a module is the file collections, e.g., the file containing generic
 *       first names, such as "BABY".
 *   </li>
 *   <li>Static initialization components. It is the client code's responsibility to call
 *       the specific initializers for these components. The <code>OracleConnectionCacheXmlConf</code>
 *       is an example of such an initializer.
 *   </li>
 * </ul>
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.2 $ $Date: 2010/03/24 18:57:16 $
 */
public class XmlConfigurator implements ChoiceMakerConfigurator, ChoiceMakerConfiguration {

	private static final Logger logger = Logger.getLogger(XmlConfigurator.class);

	/** The XML document read as configuration file. */
	Document document;
	ClassLoader classLoader;
	URL[] reloadClassPath;
	boolean reload;
	ClassLoader reloadClassLoader;
	String fileName;

	public File wdir;

	private static XmlConfigurator instance = new XmlConfigurator();

	public static XmlConfigurator getInstance() {
		return instance;
	}

	void initReports() {
		List reporters = new ArrayList();
		IExtensionPoint reporterExts = Platform.getPluginRegistry().getExtensionPoint("com.choicemaker.cm.core.reporter");
		List reporterConfigs = getCore().getChildren("reporter");
		for (Iterator iReporterConfigs = reporterConfigs.iterator(); iReporterConfigs.hasNext();) {
			Element reporterConfig = (Element) iReporterConfigs.next();
			try {
				String ext = reporterConfig.getAttributeValue("extension");
				Reporter reporter = (Reporter) reporterExts.getExtension(ext).getConfigurationElements()[0].createExecutableExtension("class");
				Method[] methods = reporter.getClass().getMethods();
				HashMap methodMap = new HashMap();
				for (int i = 0; i < methods.length; i++) {
					methodMap.put(methods[i].getName(), methods[i]);
				}
				List properties = reporterConfig.getChildren("property");
				for (Iterator iProperties = properties.iterator(); iProperties.hasNext();) {
					Element property = (Element) iProperties.next();
					String name = property.getAttributeValue("name");
					name = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
					String value = property.getAttributeValue("value");
					set((Method) methodMap.get(name), reporter, value);
				}
				reporter.open();
				reporters.add(reporter);
			} catch (Exception ex) {
				logger.error("Configuring reporter", ex);
			}
		}
		PMManager.setGlobalReporters((Reporter[]) reporters.toArray(new Reporter[reporters.size()]));
	}

	void set(Method method, Object obj, String value) throws Exception {
		Class paramType = method.getParameterTypes()[0];
		Object param = value;
		logger.info(obj.getClass().getName() + "." + method.getName() + "(" + value + ")");
		if (paramType == boolean.class || paramType == Boolean.class) {
			param = Boolean.valueOf(value);
		} else if (paramType == int.class || paramType == Integer.class) {
			param = new Integer(value);
		}
		method.invoke(obj, new Object[] { param });
	}

	void initInstallableGeneratorPluginFactory() {
		Class c = Eclipse2GeneratorPluginFactory.class;
		String className = c.getName();
		System.setProperty(
				PropertyNames.INSTALLABLE_GENERATOR_PLUGIN_FACTORY,
				className);
	}

	/**
	 * Read the XML configuration file.
	 *
	 * @throws  XmlConfException  if any error occurs.
	 */
	void readConfigurationFile() throws XmlConfException {
		SAXBuilder builder = XmlParserFactory.createSAXBuilder(false);
		try {
			document = builder.build(fileName);
		} catch (Exception ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	void setWorkingDir() throws XmlConfException {
		Element e = getCore();
		wdir = new File(fileName).getAbsoluteFile().getParentFile();
		if (e != null) {
			e = e.getChild("workingDir");
			if (e != null) {
				wdir = FileUtilities.getAbsoluteFile(wdir, e.getText());
			}
		}
		try {
			wdir = wdir.getCanonicalFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.setProperty("user.dir", wdir.toString());
	}

	void initClassLoader() throws XmlConfException {
		Element cp = getCore().getChild("classpath");
		URL[] classPath = null;
		if (cp != null) {
			try {
				classPath = FileUtilities.cpToUrls(wdir,cp.getText());
			} catch (MalformedURLException ex) {
				throw new XmlConfException("Classpath", ex);
			} catch (IOException ex) {
				throw new XmlConfException("Classpath", ex);
			}
		} else {
			classPath = new URL[0];
		}
		classLoader = new PpsClassLoader(classPath, XmlConfigurator.class
				.getClassLoader());
	}

	void initReloadClassPath() throws XmlConfException {
		reloadClassPath = new URL[0];
		Element rl = getCore().getChild("reload");
		if (rl != null) {
			Element cp = rl.getChild("classpath");
			if (cp != null) {
				try {
					reloadClassPath = FileUtilities.cpToUrls(wdir,cp.getText());
				} catch (MalformedURLException ex) {
					throw new XmlConfException("Classpath", ex);
				} catch (IOException ex) {
					throw new XmlConfException("Classpath", ex);
				}
			}
		}
	}

	/**
	 * Initializes all the modules listed in the configuration file.
	 * @throws  XmlConfException  if any error occurs.
	 */
	void initModules(List modules, ClassLoader cl) throws XmlConfException {
		try {
			Iterator i = modules.iterator();
			while (i.hasNext()) {
				Element e = (Element) i.next();
				String className = e.getAttributeValue("class");
				Class clazz = Class.forName(className, true, cl);
				XmlModuleInitializer m = (XmlModuleInitializer) clazz.getDeclaredField("instance").get(null);
				m.init(e);
			}
		} catch (ClassNotFoundException ex) {
			throw new XmlConfException("Internal error.", ex);
		} catch (IllegalAccessException ex) {
			throw new XmlConfException("Internal error.", ex);
		} catch (NoSuchFieldException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	/**
	 * Saves the XML configuration into the specified file.
	 *
	 * @param   fn  The name of the file.
	 */
	public void save(String fn) throws XmlConfException {
		fileName = new File(fn).getAbsolutePath();
		try {
			FileOutputStream fs = new FileOutputStream(fileName);
			XMLOutputter o = new XMLOutputter("    ", true);
			o.setTextNormalize(true);
			o.output(document, fs);
			fs.close();
		} catch (IOException ex) {
			throw new XmlConfException("Internal error.", ex);
		}
	}

	/**
	 * Returns the JDOM document for the configuration file.
	 *
	 * @return   the JDOM document for the configuration file.
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Returns the JDOM root element for the configuration file.
	 *
	 * @return   the JDOM root element for the configuration file.
	 */
	public Element getRoot() {
		return document.getRootElement();
	}

	Element get(String b, String t) {
		Element e = getRoot().getChild(b);
		if (e != null) {
			return e.getChild(t);
		} else {
			return null;
		}
	}

	public Element getCustom(String t) {
		return get("custom", t);
	}

	public Element getPlugin(String t) {
		return get("plugin", t);
	}

	public Element getCore() {
		return getRoot().getChild("core");
	}

	public ClassLoader getReloadClassLoader() throws XmlConfException {
		if (reloadClassLoader == null) {
			reload();
		}
		return reloadClassLoader;
	}

	public ClassLoader reload() throws XmlConfException {
		if (!reload && reloadClassLoader != null) {
			return reloadClassLoader;
		}
		reloadClassLoader = new PpsClassLoader(reloadClassPath, classLoader);
		Element rl = getCore().getChild("reload");
		if (rl != null) {
			initModules(rl.getChildren("module"), reloadClassLoader);
		}
		return reloadClassLoader;
	}

	/**
	 * A PpsClassLoader first tries to load a class using a class loader of
	 * some plugin in the Plugin Registery. Next, if the class hasn't been loaded,
	 * a PpsClassLoader relies on the method of a URLClassLoader; namely,
	 * it searches through the parent of URLClassLoader (i.e. the grandparent of
	 * this loader), and then the URLs of the URLClassLoader in the order that they
	 * are specified in the first argument of the PpsClassLoader constructor.
	 * If the class isn't found, a ClassNotFound exception is thrown.
	 */
	public static class PpsClassLoader extends URLClassLoader {

		private static final Logger log = Logger.getLogger(XmlConfigurator.class.getName() + ".PpsClassLoader");

		private ClassLoader[] parents;

		public PpsClassLoader(URL[] path, ClassLoader parent) {
			super(path, parent);
			List l = new ArrayList();
			IPluginDescriptor[] plugins = Platform.getPluginRegistry().getPluginDescriptors();
			for (int i = 0; i < plugins.length; i++) {
				log.debug("Adding classloader of '" + plugins[i].getUniqueIdentifier() + "' as loader #" + i);
				l.add(plugins[i].getPluginClassLoader());
			}
			parents = (ClassLoader[]) l.toArray(new ClassLoader[l.size()]);
		}

		/* (non-Javadoc)
		 * @see java.lang.ClassLoader#findClass(java.lang.String)
		 */
		public Class loadClass(String name) throws ClassNotFoundException {
			Class c = null;
			for (int i = 0; i < parents.length; i++) {
				try {
					c = parents[i].loadClass(name);
					log.debug("Class '" + name + "' found by '" + parents[i] + "'");
					break;
				} catch (ClassNotFoundException ex) {
					if (log.isDebugEnabled()) {
						log.debug("Class '" + name + "' not found by '" + parents[i] + "'; search continuing...");
					}
				}
			}
			if (c==null) {
				log.debug("Class '" + name + "' not found by plugin classloaders; search continuing with parent URLClassLoader.");
				c = super.findClass(name);
				if (log.isDebugEnabled()) {
					if (c!=null) {
						String msg = "Class '" + name + "' found by parent ULRClassLoader '" + super.toString() + "'";
						log.debug(msg);
					} else {
						String msg = "Class '" + name + "' not found by parent ULRClassLoader '" + super.toString() + "'";
						log.debug(msg);
					}
				}
			}
			if (c==null) {
				String msg = "Class '" + name + "' not found by PpsClassLoader.";
				log.error(msg);
				throw new ClassNotFoundException (msg);
			}
			return c;
		}
	}

	public String getRmiCodebase() {
		String res = "";
		try {
			res = FileUtilities.toAbsoluteUrlClasspath(wdir,System.getProperty("java.class.path"));
			Element e = getCore().getChild("classpath");
			if (e != null) {
				res += FileUtilities.toAbsoluteUrlClasspath(wdir,e.getText());
			}
			e = getCore().getChild("reload");
			if (e != null) {
				e = e.getChild("classpath");
				if (e != null) {
					res += FileUtilities.toAbsoluteUrlClasspath(wdir,e.getText());
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		IPluginDescriptor[] plugins = Platform.getPluginRegistry().getPluginDescriptors();
		for (int i = 0; i < plugins.length; i++) {
			URL[] ucp = ((URLClassLoader) plugins[i].getPluginClassLoader()).getURLs();
			for (int j = 0; j < ucp.length; j++) {
				res += " " + ucp[j].toString();
			}
		}
		return res;
	}

	 XmlConfigurator() {
	}

	public ClassLoader getRmiClassLoader() {
		return new PpsClassLoader(new URL[0], null);
	}

	public String getJavaDocClasspath() {
		String pathSeparator = System.getProperty("path.separator");
		String res = null;
		IPluginDescriptor[] plugins = Platform.getPluginRegistry().getPluginDescriptors();
		for (int i = 0; i < plugins.length; i++) {
			URL[] ucp = ((URLClassLoader) plugins[i].getPluginClassLoader()).getURLs();
			for (int j = 0; j < ucp.length; j++) {
				if (res == null) {
					res = "";
				} else {
					res += pathSeparator;
				}
				res += ucp[j].getPath();
			}
		}
		return res;
	}

	/**
	 * Returns the configuration file name.
	 *
	 * @return  The configuration file name.
	 */
	public String getFileName() {
		return fileName;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Initializes ChoiceMaker from an XML configuration file.
	 * Reads the XML configuration file and initializes logging and all modules.
	 *
	 * @param   fn  The name of the configuration file.
	 * @return
	 * @throws  XmlConfException  if any error occurs.
	 */
	public ChoiceMakerConfiguration init(String fn, String log4jConfName, boolean reload, boolean initGui) throws XmlConfException {
		this.reload = reload;
		initInstallableGeneratorPluginFactory();
		fileName = new File(fn).getAbsolutePath();
		readConfigurationFile();
		setWorkingDir();
		Log4jXmlConf.config(log4jConfName);
		initClassLoader();
		initReloadClassPath();
		initModules(getCore().getChildren("module"), classLoader);
		return this;
	}

	public boolean isValid() {
		return true;
	}

	public MachineLearnerPersistence getMachineLearnerPersistence(
			MachineLearner model) {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public ProbabilityModelPersistence getModelPersistence(
			ImmutableProbabilityModel model) {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public List getProbabilityModelConfigurations() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public String getClassPath() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public String getReloadClassPath() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public void reloadClasses() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public String toXml() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public ChoiceMakerConfiguration init() throws XmlConfException {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public ChoiceMakerConfiguration init(String fn, boolean reload,
			boolean initGui) throws XmlConfException {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public ICompiler getChoiceMakerCompiler() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public File getWorkingDirectory() {
		return wdir;
	}

	public String getCodeRoot() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	public void deleteGeneratedCode() {
		File f = new File(getCodeRoot()).getAbsoluteFile();
		if (f.exists()) {
			logger.info("Deleting codeRoot('" + f.getAbsoluteFile() + "')");
			FileUtilities.removeDir(f);
		}
	}

}

