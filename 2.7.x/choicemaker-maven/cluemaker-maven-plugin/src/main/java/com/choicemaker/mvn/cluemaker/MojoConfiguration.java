package com.choicemaker.mvn.cluemaker;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.compiler.ICompiler;
import com.choicemaker.cm.core.configure.ChoiceMakerConfiguration;

public class MojoConfiguration implements ChoiceMakerConfiguration {

	// private final MavenProject project;
	private final File cluemakerDirectory;
	private final File generatedSourceDirectory;
	private final File compiledCodeDirectory;
	private final List<Artifact> artifacts;

	private String classpath;

	public MojoConfiguration(MavenProject p, File cluemakerDir, File generatedSrcDir, File compiledCodeDir, List<Artifact> a) {
		if (cluemakerDir == null) {
			throw new IllegalArgumentException("null ClueMaker directory");
		}
		if (generatedSrcDir == null) {
			throw new IllegalArgumentException("null generated-source directory");
		}
		if (compiledCodeDir == null) {
			throw new IllegalArgumentException("null compiled-code directory");
		}
		if (a == null) {
			throw new IllegalArgumentException("null artifact list");
		}
		this.cluemakerDirectory = cluemakerDir;
		this.generatedSourceDirectory = generatedSrcDir;
		this.compiledCodeDirectory = compiledCodeDir;
		this.artifacts = a;
	}

	@Override
	public String getClassPath() {
		if (classpath == null) {
			classpath = MojoConfigurationUtils.computeClasspath(artifacts);
		}
		assert classpath != null;
		return classpath;
	}

	@Override
	public String getGeneratedSourceRoot() {
		return this.generatedSourceDirectory.getAbsolutePath();
	}

	@Override
	public String getClueMakerSourceRoot() {
		return this.cluemakerDirectory.getAbsolutePath();
	}

	@Override
	public String getCompiledCodeRoot() {
		return this.compiledCodeDirectory.getAbsolutePath();
	}

	// -- Not yet implemented

	@Override
	public String getPackagedCodeRoot() {
		throw new Error("not yet implemented");
	}

	@Override
	public void deleteGeneratedCode() {
		throw new Error("not yet implemented");
	}

	@Override
	public ICompiler getChoiceMakerCompiler() {
		throw new Error("not yet implemented");
	}

	@Override
	public ClassLoader getClassLoader() {
		throw new Error("not yet implemented");
	}

	@Override
	public String getFileName() {
		throw new Error("not yet implemented");
	}

	@Override
	public String getJavaDocClasspath() {
		throw new Error("not yet implemented");
	}

	@Override
	public ClassLoader getRmiClassLoader() {
		throw new Error("not yet implemented");
	}

	@Override
	public File getWorkingDirectory() {
		throw new Error("not yet implemented");
	}

	@Override
	public void reloadClasses() throws XmlConfException {
		throw new Error("not yet implemented");
	}

//	@Override
//	public MachineLearnerPersistence getMachineLearnerPersistence(
//			MachineLearner model) {
//		throw new Error("not yet implemented");
//	}

//	@Override
//	public ProbabilityModelPersistence getModelPersistence(
//			ImmutableProbabilityModel model) {
//		throw new Error("not yet implemented");
//	}

//	@Override
//	@SuppressWarnings("rawtypes")
//	public List getProbabilityModelConfigurations() {
//		throw new Error("not yet implemented");
//	}

//	@Override
//	public String getReloadClassPath() {
//		throw new Error("not yet implemented");
//	}

//	@Override
//	public String toXml() {
//		throw new Error("not yet implemented");
//	}

}
