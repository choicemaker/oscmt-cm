/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.ClueDesc;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.ModelAttributeNames;
import com.choicemaker.cm.core.ModelConfigurationException;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.base.MutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.compiler.CompilationArguments;
import com.choicemaker.cm.core.compiler.CompilerException;
import com.choicemaker.cm.core.compiler.ICompiler;
import com.choicemaker.util.ArrayHelper;
import com.choicemaker.util.FileUtilities;

/**
 * Handling of probability models in XML configuration.
 *
 * @author    Martin Buechi
 */
public class ProbabilityModelsXmlConf {

	private static final Logger logger =
		Logger.getLogger(ProbabilityModelsXmlConf.class.getName());

	static String createAttributeErrorMessage(Element e, String an, String av) {
		StringBuffer sb = new StringBuffer();
		sb.append("Unable to set attribute named '").append(an);
		sb.append("' with value '").append(av);
		sb.append("' on element '").append(e.getName()).append("'");
		return sb.toString();
	}

	/**
	 * 
	 * @param e The element for which an attribute will be set
	 * @param an The attribute name
	 * @param av The attribute value
	 */
	static void setAttribute(Element e, String an, String av) {
		if (e == null || an == null) {
			// Unexpected
			String msg = createAttributeErrorMessage(e,an,av);
			logger.severe(msg + ": null element or attribute name");
		} else if (av == null) {
			// Not unexpected
			String msg = createAttributeErrorMessage(e,an,av);
			logger.fine(msg);
		} else {
			try {
				e.setAttribute(an, av);
			} catch (Exception x) {
				// Unexpected
				String msg = createAttributeErrorMessage(e,an,av) + ": " + x.toString();
				logger.severe(msg);
			}
		}
	}

	public static void saveModel(ImmutableProbabilityModel model)
		throws ModelConfigurationException {
		Element m = new Element("ProbabilityModel");
		//setAttribute(m,ModelAttributeNames.AN_CLUE_FILE_NAME, model.getClueFileName());
		setAttribute(m,ModelAttributeNames.AN_CLUE_FILE_NAME, model.getClueFilePath());
		String ts = model.getTrainingSource();
		setAttribute(m,ModelAttributeNames.AN_TRAINING_SOURCE, ts == null ? "" : ts);
		setAttribute(m,
			ModelAttributeNames.AN_TRAINED_WITH_HOLDS,
			String.valueOf(model.isTrainedWithHolds()));
		Date lt = model.getLastTrainingDate();
		setAttribute(m,
			ModelAttributeNames.AN_LAST_TRAINING_DATE,
			lt == null ? "" : String.valueOf(lt.getTime()));
		setAttribute(m,
			ModelAttributeNames.AN_FIRING_THRESHOLD,
			String.valueOf(model.getFiringThreshold()));
		String userName = model.getUserName();
		if (userName == null)
			userName = "";
		setAttribute(m,ModelAttributeNames.AN_USER_NAME, userName);
		setAttribute(m,
			ModelAttributeNames.AN_ENABLE_ALL_CLUES_BEFORE_TRAINING,
			String.valueOf(model.isEnableAllCluesBeforeTraining()));
		setAttribute(m,
			ModelAttributeNames.AN_ENABLE_ALL_RULES_BEFORE_TRAINING,
			String.valueOf(model.isEnableAllRulesBeforeTraining()));

		// AJW 1/8/04: the actual accessor class is a dynamic proxy...
		Accessor acc = model.getAccessor();
		setAttribute(m,ModelAttributeNames.AN_ACCESSOR_CLASS, model.getAccessorClassName());

		MlModelConf mlc = model.getMachineLearner().getModelConf();
		Element mle = new Element("machineLearner");
		mle.setAttribute("class", mlc.getExtensionPointId());
		m.addContent(mle);
		try {
			mlc.saveMachineLearner(mle);
			boolean[] cluesToEvaluate = model.getCluesToEvaluate();
			ClueDesc[] clueDesc = acc.getClueSet().getClueDesc();
			for (int i = 0; i < clueDesc.length; ++i) {
				Element c = new Element("clue");
				m.addContent(c);
				c.setAttribute("name", clueDesc[i].getName());
				c.setAttribute("evaluate", String.valueOf(cluesToEvaluate[i]));
				mlc.saveClue(c, i);
			}
			String fileName = model.getModelFilePath();
			FileOutputStream fs =
				new FileOutputStream(new File(fileName).getAbsoluteFile());
			Format format = Format.getPrettyFormat();
			XMLOutputter o = new XMLOutputter(format);
			//o.setTextNormalize(true);
			o.output(m, fs);
			fs.close();
		} catch (Exception ex) {
			throw new ModelConfigurationException("Problem saving model: "
					+ ex.toString(), ex);
		}
	}

	public static IProbabilityModel readModel(String fileName, InputStream is,
			ICompiler compiler, Writer statusOutput,
			ClassLoader customClassLoader, boolean allowCompile)
			throws ModelConfigurationException {

		// Preconditions
		if (is == null) {
			throw new IllegalArgumentException("null input stream");
		}
		if (compiler == null) {
			throw new IllegalArgumentException("null compiler");
		}
		if (statusOutput == null) {
			throw new IllegalArgumentException("null writer");
		}

		Document document = null;
		SAXBuilder builder = XmlParserFactory.createSAXBuilder(false);
		try {
			document = builder.build(is);
		} catch (Exception ex) {
			throw new ModelConfigurationException("Internal error: "
					+ ex.toString(), ex);
		}
		Element m = document.getRootElement();
		String clueFileName =
			m.getAttributeValue(ModelAttributeNames.AN_CLUE_FILE_NAME);
		String trainingSource =
			m.getAttributeValue(ModelAttributeNames.AN_TRAINING_SOURCE);
		if (trainingSource == null)
			trainingSource = "";
		String ltd =
			m.getAttributeValue(ModelAttributeNames.AN_LAST_TRAINING_DATE);
		boolean trainedWithHolds =
			"true".equals(m
					.getAttributeValue(ModelAttributeNames.AN_TRAINED_WITH_HOLDS));
		Date lastTrainingDate =
			ltd == null || ltd.length() == 0 ? null : new Date(
					Long.parseLong(ltd));
		String ft =
			m.getAttributeValue(ModelAttributeNames.AN_FIRING_THRESHOLD);
		int firingThreshold = 3;
		if (ft != null)
			firingThreshold = Integer.parseInt(ft);
		boolean enableAllCluesBeforeTraining =
			"true".equals(m
					.getAttributeValue(ModelAttributeNames.AN_ENABLE_ALL_CLUES_BEFORE_TRAINING));
		boolean enableAllRulesBeforeTraining =
			"true".equals(m
					.getAttributeValue(ModelAttributeNames.AN_ENABLE_ALL_RULES_BEFORE_TRAINING));
		String userName = m.getAttributeValue(ModelAttributeNames.AN_USER_NAME);
		String accessorName =
			m.getAttributeValue(ModelAttributeNames.AN_ACCESSOR_CLASS);
		if (accessorName == null || accessorName.trim().isEmpty()) {
			throw new ModelConfigurationException("model does not define an accessor name");
		}
		Accessor accessor = null;

		ClassLoader classLoader = customClassLoader;
		if (classLoader == null || XmlConfigurator.getInstance().isReload()) {
			try {
				classLoader = XmlConfigurator.getInstance().reload();
			} catch (XmlConfException e) {
				throw new ModelConfigurationException(e.toString(), e);
			}
		}
		logger.fine("classLoader == " + customClassLoader);

		MutableProbabilityModel retVal = null;
		try {
			if (allowCompile && classLoader instanceof URLClassLoader) {
				String resourcePath = accessorName.replace('.', '/') + ".class";
				URL resourceUrl =
					((URLClassLoader) classLoader).findResource(resourcePath);
				if (resourceUrl == null) {
					final String accessorName0 = accessorName;
					CompilationArguments arguments = new CompilationArguments();
					// String[] args = { clueFileName };
					String[] args =
						{ FileUtilities.getAbsoluteFile(
								new File(fileName).getParentFile(),
								clueFileName).toString() };
					arguments.enter(args);
					accessorName = compiler.compile(arguments, statusOutput);
					if (accessorName == null) {
						throw new CompilerException("Compilation error: "
								+ statusOutput.toString());
					}
					if (!accessorName.equals(accessorName0)) {
						String msg = "Accessor name has changed from '"
								+ accessorName0 + "' to '" + accessorName
								+ "'. Save the model to update the accessor name.";
						logger.warning(msg);
					}
				}
			}
			if (classLoader == null) {
				throw new ModelConfigurationException("no class loader defined");
			}
			accessor = PMManager.createAccessor(accessorName, classLoader);
			if (accessor == null) {
				throw new ModelConfigurationException(
						"unable to create an accessor (" + accessorName + ")");
			}
			ClueDesc[] clueDesc = accessor.getClueSet().getClueDesc();
			if (clueDesc == null) {
				throw new ModelConfigurationException("no clue descriptors");
			}
			Map<String, Integer> cm = new HashMap<>();
			for (int i = 0; i < clueDesc.length; ++i) {
				cm.put(clueDesc[i].getName(), new Integer(i));
			}
			boolean[] cluesToEvaluate =
				ArrayHelper.getTrueArray(clueDesc.length);
			List<Element> cl = m.getChildren("clue");
			int[] clueNums = new int[cl.size()];
			int i = 0;
			Iterator<Element> iCl = cl.iterator();
			while (iCl.hasNext()) {
				Element c = iCl.next();
				String name = c.getAttributeValue("name");
				Object o = cm.get(name);
				if (o != null) {
					int index = ((Integer) o).intValue();
					clueNums[i] = index;
					cluesToEvaluate[index] =
						Boolean.valueOf(c.getAttributeValue("evaluate"))
								.booleanValue();
				} else {
					clueNums[i] = -1;
				}
				++i;
			}
			Element mle = m.getChild("machineLearner");
			MachineLearner ml = null;
			String name = mle.getAttributeValue("class");
			MlModelConf mc =
				(MlModelConf) ExtensionPointMapper.getInstance(
						ChoiceMakerExtensionPoint.CM_CORE_MACHINELEARNER, name);
			ml = mc.readMachineLearner(mle, accessor, cl, clueNums);
			retVal =
				new MutableProbabilityModel(fileName, clueFileName, accessor, ml,
						cluesToEvaluate, trainingSource, trainedWithHolds,
						lastTrainingDate);
			retVal.setFiringThreshold(firingThreshold);
			retVal.setEnableAllCluesBeforeTraining(enableAllCluesBeforeTraining);
			retVal.setEnableAllRulesBeforeTraining(enableAllRulesBeforeTraining);
			retVal.setUserName(userName);
		} catch (Exception e) {
			throw new ModelConfigurationException(e.toString(), e);
		}
		assert retVal != null;
		return retVal;
	}

	public static void loadProductionProbabilityModels(ICompiler compiler,
			boolean fromResource) throws ModelConfigurationException {

		// Precondition
		if (compiler == null) {
			throw new IllegalArgumentException("null compiler");
		}

		logger.info("loadProductionProbabilityModels");
		Element x =
			XmlConfigurator.getInstance().getCore()
					.getChild("productionProbabilityModels");
		if (x != null) {
			@SuppressWarnings("rawtypes")
			List l = x.getChildren("model");
			@SuppressWarnings("rawtypes")
			Iterator i = l.iterator();
			while (i.hasNext()) {
				Element e = (Element) i.next();
				String name = e.getAttributeValue("name");
				String fileName = e.getAttributeValue("file");

				MutableProbabilityModel m;
				final boolean allowRecompile = !fromResource;
				final ClassLoader customCL = null;
				if (fromResource) {
					InputStream is =
						ProbabilityModelsXmlConf.class.getClassLoader()
								.getResourceAsStream("META-INF/" + fileName);
					m =
						(MutableProbabilityModel) readModel(null, is, compiler,
								new StringWriter(), customCL, allowRecompile);
				} else {
					InputStream is;
					try {
						File f = new File(fileName).getAbsoluteFile();
						is = new FileInputStream(f);
					} catch (FileNotFoundException e1) {
						String msg =
							"Unable to find '" + fileName + "': "
									+ e1.toString();
						throw new ModelConfigurationException(msg);
					}
					m =
						(MutableProbabilityModel) readModel(fileName, is, compiler,
								new StringWriter(), customCL, allowRecompile);
				}
				
				if (name != null) {
					String modelName = name.trim();
					if (!modelName.isEmpty()) {
						assert m instanceof MutableProbabilityModel;
						m.setModelName(modelName);
					}
				}

				@SuppressWarnings("rawtypes")
				List props = e.getChildren("property");
				if (props != null && props.size() > 0) {
					logger.warning("Model properties are deprecated.");
					for (@SuppressWarnings("rawtypes")
					Iterator iProps = props.iterator(); iProps.hasNext();) {
						Element p = (Element) iProps.next();
						String key = p.getAttributeValue("name").intern();
						String value = p.getAttributeValue("value").intern();
						String msg =
							"Ignoring model property/value: " + key + "/"
									+ value;
						logger.warning(msg);
					}
				}

				PMManager.addModel(m);
			}
		} else {
			logger.info("No 'productionProbabilityModels' element");
		}
	}

}
