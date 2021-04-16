package com.choicemaker.cm.core.gen;

import java.util.List;

public interface IGeneratorPluginFactory {
	/** Look up generator plugins */
	List<GeneratorPlugin> lookupGeneratorPlugins() throws GenException;
}
