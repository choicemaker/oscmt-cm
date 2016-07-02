package com.choicemaker.cm.core.configure;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.jdom.Document;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.compiler.ICompiler;
import com.choicemaker.cm.core.compiler.InstallableCompiler;
import com.choicemaker.util.FileUtilities;

class ListBackedConfiguration implements ChoiceMakerConfiguration {

//	private static final FileUtils FILE_UTILS = FileUtils.newFileUtils();

	private static final Logger logger = Logger
			.getLogger(ListBackedConfiguration.class.getName());

	private static final String SOURCE_DIRECTORY = "src";

	private static final String CLASSES_DIRECTORY = "classes";

	private static final String PACKAGES_DIRECTORY = "out";

	/** The class path */
	private final String classpath;

//	/** The directory where ClueMaker code is located */
//	private final String cluemakerRoot;

	/** The directory where generated code is created */
	private final String generatedSourceRoot;

	/** An absolute path to the configuration file */
	private final String filePath;

	/** The working directory for the configuration */
	private final File workingDirectory;

	/**
	 *
	 * @param filePath
	 *            an absolute or relative path to the configuration file name.
	 * @throws XmlConfException
	 */
	public ListBackedConfiguration(String fileName) throws XmlConfException {
		this.filePath = new File(fileName).getAbsolutePath();

		Document document = ConfigurationUtils.readConfigurationFile(this.filePath);
		this.workingDirectory = ConfigurationUtils.getWorkingDirectory(this.filePath, document);
		System.setProperty(ConfigurationUtils.SYSTEM_USER_DIR, this.workingDirectory.toString());
		this.classpath = ConfigurationUtils.getClassPath(this.workingDirectory, document);
		this.generatedSourceRoot = ConfigurationUtils.getCodeRoot(this.workingDirectory, document);
	}

	@Override
	public void deleteGeneratedCode() {
		File f = new File(getGeneratedSourceRoot()).getAbsoluteFile();
		if (f.exists()) {
			logger.info("Deleting generatedSourceRoot('" + f.getAbsoluteFile() + "')");
			FileUtilities.removeChildren(f);
		}
		f = new File(getCompiledCodeRoot()).getAbsoluteFile();
		if (f.exists()) {
			logger.info("Deleting generatedSourceRoot('" + f.getAbsoluteFile() + "')");
			FileUtilities.removeChildren(f);
		}
		f = new File(getPackagedCodeRoot()).getAbsoluteFile();
		if (f.exists()) {
			logger.info("Deleting generatedSourceRoot('" + f.getAbsoluteFile() + "')");
			FileUtilities.removeChildren(f);
		}
	}

	@Override
	public ICompiler getChoiceMakerCompiler() {
		return InstallableCompiler.getInstance();
	}

	@Override
	public ClassLoader getClassLoader() {
		return ListBackedConfiguration.class.getClassLoader();
	}

	@Override
	public String getClassPath() {
		return this.classpath;
	}

	protected String getCodeRoot() {
		return this.generatedSourceRoot;
	}

	@Override
	public String getFileName() {
		return filePath;
	}

	@Override
	public String getJavaDocClasspath() {
		return getClassPath();
	}

	@Override
	public MachineLearnerPersistence getMachineLearnerPersistence(
			MachineLearner model) {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	@Override
	public ProbabilityModelPersistence getModelPersistence(
			ImmutableProbabilityModel model) {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	@Override
	public List getProbabilityModelConfigurations() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	@Override
	public String getReloadClassPath() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	@Override
	public ClassLoader getRmiClassLoader() {
		return getClassLoader();
	}

	@Override
	public File getWorkingDirectory() {
		return this.workingDirectory;
	}

	@Override
	public void reloadClasses() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	@Override
	public String toXml() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	@Override
	public String getClueMakerSourceRoot() {
		// FIXME non-functional method stub
		throw new Error("not yet implemented");
	}

	@Override
	public String getGeneratedSourceRoot() {
		return getCodeRoot() + File.separator + SOURCE_DIRECTORY;
	}

	@Override
	public String getCompiledCodeRoot() {
		return getCodeRoot() + File.separator + CLASSES_DIRECTORY;
	}

	@Override
	public String getPackagedCodeRoot() {
		return getCodeRoot() + File.separator + PACKAGES_DIRECTORY;
	}

}
