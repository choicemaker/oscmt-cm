package com.choicemaker.cm.core.configure;

import java.util.logging.Logger;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.compiler.ICompiler;

/**
 * A flyweight class (no instance data) that provides standard methods for
 * working with an InstallableConfigurator and the InstalledConfiguration.
 *
 * @author rphall
 *
 */
public class ConfigurationManager {

	private static final Logger logger =
		Logger.getLogger(ConfigurationManager.class.getName());

	private static final ConfigurationManager instance =
		new ConfigurationManager();

	public static final ConfigurationManager getInstance() {
		return instance;
	}

	public static void install(ChoiceMakerConfigurator configurator) {
		InstallableConfigurator.getInstance().install(configurator);
	}

	public boolean isInitialized() {
		boolean retVal = false;
		InstalledConfiguration iconf = InstalledConfiguration.getInstance();
		if (iconf != null) {
			retVal = iconf.hasDelegate();
		}
		return retVal;
	}

	private InstalledConfiguration getConfiguration() {
		return InstalledConfiguration.getInstance();
	}

	private ChoiceMakerConfigurator getConfigurator() {
		return InstallableConfigurator.getInstance();
	}

	// public ProbabilityModelPersistence getModelPersistence(
	// ImmutableProbabilityModel model) {
	// return getConfiguration().getModelPersistence(model);
	// }

	// public MachineLearnerPersistence getMachineLearnerPersistence(
	// MachineLearner model) {
	// return getConfiguration().getMachineLearnerPersistence(model);
	// }

	public ClassLoader getClassLoader() {
		return getConfiguration().getClassLoader();
	}

	// public ClassLoader getRmiClassLoader() {
	// ClassLoader retVal = null;
	// InstalledConfiguration c = getConfiguration();
	// if (c != null) {
	// retVal = c.getRmiClassLoader();
	// }
	// return retVal;
	// }

	// public List getProbabilityModelConfigurations() {
	// return getConfiguration().getProbabilityModelConfigurations();
	// }

	public String getClassPath() {
		return getConfiguration().getClassPath();
	}

	// public String getReloadClassPath() {
	// return getConfiguration().getReloadClassPath();
	// }

	public String getJavaDocClasspath() {
		return getConfiguration().getJavaDocClasspath();
	}

	public String getGeneratedSourceRoot() {
		return getConfiguration().getGeneratedSourceRoot();
	}

	public String getCompiledCodeRoot() {
		return getConfiguration().getCompiledCodeRoot();
	}

	public String getPackagedCodeRoot() {
		return getConfiguration().getPackagedCodeRoot();
	}

	public ICompiler getChoiceMakerCompiler() {
		return getConfiguration().getChoiceMakerCompiler();
	}

	public void reloadClasses() throws XmlConfException {
		getConfiguration().reloadClasses();
	}

	public void deleteGeneratedCode() {
		getConfiguration().deleteGeneratedCode();
	}

	// public String toXml() {
	// return getConfiguration().toXml();
	// }

	public String getFileName() {
		return getConfiguration().getFileName();
	}

	public void init() throws XmlConfException {
		if (isInitialized()) {
			logger.warning("Already initialized");
		}
		ChoiceMakerConfiguration cmc = getConfigurator().init();
		getConfiguration().setDelegate(cmc);

		// Postcondition
		assert isInitialized();
	}

	public void init(String fn, boolean reload, boolean initGui)
			throws XmlConfException {
		init(fn, null, reload, initGui, null);
	}

	/** @deprecated Use {@link #init(String, boolean, boolean)} */
	@Deprecated
	public void init(String fn, String unusedLogName, boolean reload,
			boolean initGui) throws XmlConfException {
		init(fn, reload, initGui, null);
	}

	/** @deprecated Use {@link #init(String, boolean, boolean, char[])} */
	@Deprecated
	public void init(String fn, String unusedLogName, boolean reload,
			boolean initGui, char[] password) throws XmlConfException {
		init(fn, reload, initGui, null);
	}

	public void init(String fn, boolean reload, boolean initGui, char[] password)
			throws XmlConfException {
		if (isInitialized()) {
			logger.warning("Already initialized");
		}

		ChoiceMakerConfiguration cmc =
			getConfigurator().init(fn, reload, initGui, password);
		getConfiguration().setDelegate(cmc);

		// Postcondition
		assert isInitialized();
	}

}
