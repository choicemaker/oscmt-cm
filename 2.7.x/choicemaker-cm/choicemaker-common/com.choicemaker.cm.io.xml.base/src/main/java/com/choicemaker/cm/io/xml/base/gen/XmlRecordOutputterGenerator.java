/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base.gen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.gen.CoreTags;
import com.choicemaker.cm.core.gen.GenException;
import com.choicemaker.cm.core.gen.GeneratorHelper;
import com.choicemaker.cm.core.gen.GeneratorPlugin;
import com.choicemaker.cm.core.gen.IGenerator;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class XmlRecordOutputterGenerator implements GeneratorPlugin {
	private boolean report;

	private final static int NONE = 0;
	private final static int SELECT = 1;
	private final static int NODE_TYPE = 2;
	private final static int ALL = 3;

	private static int reportLevel(String s) {
		if (s == null) {
			return NONE;
		} else {
			s = s.intern();
			if ("select" == s) {
				return SELECT;
			} else if ("nodeType" == s) {
				return NODE_TYPE;
			} else if ("all" == s) {
				return ALL;
			} else {
				return NONE;
			}
		}
	}

	private static DerivedSource src = DerivedSource.valueOf("xml");

	public XmlRecordOutputterGenerator(boolean report) {
		this.report = report;
	}

	public static XmlRecordOutputterGenerator instance = new XmlRecordOutputterGenerator(false);
	public static XmlRecordOutputterGenerator reportinstance = new XmlRecordOutputterGenerator(true);

	@Override
	public void generate(IGenerator g) throws GenException {
		g.addAccessorImport("import com.choicemaker.cm.io.xml.base.*;" + Constants.LINE_SEPARATOR);
		String className = g.getSchemaName();
		String packageName = g.getPackage();
		String directoryName = g.getSourceCodePackageRoot();
		if (report) {
			className += "XmlReporter";
			g.addAccessorBody(
				"private static transient Object reportOutputter = null;" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("public synchronized Object getReportOutputter() {" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("if(reportOutputter == null) {" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("reportOutputter = new " + packageName + "." + className + "();" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("}" + Constants.LINE_SEPARATOR);			
			g.addAccessorBody("return reportOutputter;" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("}" + Constants.LINE_SEPARATOR);
		} else {
			className += "XmlRecordOutputter";
			packageName += ".xml";
			directoryName += File.separator + "xml";
			g.addAccessorBody("private static transient Object xmlRecordOutputter = null;" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("public synchronized Object getXmlRecordOutputter() {" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("if(xmlRecordOutputter == null) {" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("xmlRecordOutputter = new " + packageName + "." + className + "();" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("}" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("return xmlRecordOutputter;" + Constants.LINE_SEPARATOR);
			g.addAccessorBody("}" + Constants.LINE_SEPARATOR);
		}
		try {
			String fileName = directoryName + File.separator + className + ".java";
			g.addGeneratedFile(fileName);
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			Writer w = new OutputStreamWriter(new BufferedOutputStream(fs));
			w.write("// Generated by ChoiceMaker. Do not edit." + Constants.LINE_SEPARATOR);
			w.write("package " + packageName + ";" + Constants.LINE_SEPARATOR);
			w.write("import java.util.logging.*;" + Constants.LINE_SEPARATOR);
			w.write("import java.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import java.io.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.base.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.io.xml.base.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import " + g.getPackage() + ".*;" + Constants.LINE_SEPARATOR);
			w.write(g.getImports());
			w.write("public final class " + className + " implements XmlRecordOutputter {" + Constants.LINE_SEPARATOR);
			w.write("private static Logger logger = Logger.getLogger(" + packageName + "." + className + ".class.getName());" + Constants.LINE_SEPARATOR);
			w.write("public void put(Writer w, Record r) throws IOException {" + Constants.LINE_SEPARATOR);
			Element rootRecord = g.getRootRecord();
			int rl = NONE;
			Element reportNodeType = GeneratorHelper.getNodeTypeExt(rootRecord, "report");
			if (reportNodeType != null) {
				rl = reportLevel(reportNodeType.getAttributeValue("include"));
			}
			boolean anyoutput = !report || rl != NONE;
			if (anyoutput) {
				String rootRecordClassName = rootRecord.getAttributeValue("className");
				w.write("put_" + rootRecordClassName + "(w, (" + rootRecordClassName + ")r);" + Constants.LINE_SEPARATOR);
			}
			w.write("}" + Constants.LINE_SEPARATOR);
			if (anyoutput) {
				createRecordOutputters(g, w, rootRecord, rl);
			}
			w.write("}" + Constants.LINE_SEPARATOR);
			w.flush();
			fs.close();
		} catch (IOException ex) {
			throw new GenException("Problem writing file.", ex);
		}
	}

	private void createRecordOutputters(IGenerator g, Writer w, Element r, int rl) throws IOException {
		String recordName = r.getAttributeValue("name");
		String className = r.getAttributeValue("className");
		w.write("private void put_" + className + "(Writer w, " + className + " r) throws IOException {" + Constants.LINE_SEPARATOR);
		w.write("w.write(\"<" + recordName + "\");" + Constants.LINE_SEPARATOR);
		List fields = new ArrayList(r.getChildren("field"));
		if (report && rl == SELECT) {
			Iterator i = fields.iterator();
			while (i.hasNext()) {
				Element reportField = ((Element) i.next()).getChild("reportField");
				if (reportField == null || !"true".equals(reportField.getAttributeValue(CoreTags.USE))) {
					i.remove();
				}
			}
		}
		GeneratorHelper.filterFields(fields, src, "xmlField");
		Iterator i = fields.iterator();
		while (i.hasNext()) {
			Element f = (Element) i.next();
			String fieldName = f.getAttributeValue("name");
			w.write("XmlOutput.writeAttribute(w, \"" + fieldName + "\", r." + fieldName + ");" + Constants.LINE_SEPARATOR);
		}
		List records = new ArrayList(r.getChildren(CoreTags.NODE_TYPE));
		if (report && rl != ALL) {
			i = records.iterator();
			while (i.hasNext()) {
				Element reportNodeType = ((Element) i.next()).getChild("reportNodeType");
				if (reportNodeType == null || reportLevel(reportNodeType.getAttributeValue("include")) == NONE) {
					i.remove();
				}
			}
		}
		if (records.isEmpty()) {
			w.write("w.write(\"/>\\n\");" + Constants.LINE_SEPARATOR);
		} else {
			w.write("w.write(\">\\n\");" + Constants.LINE_SEPARATOR);
			i = records.iterator();
			while (i.hasNext()) {
				Element n = (Element) i.next();
				String name = n.getAttributeValue("name");
				w.write("for(int i = 0; i < r." + name + ".length; ++i) {" + Constants.LINE_SEPARATOR);
				w.write("put_" + n.getAttributeValue("className") + "(w, r." + name + "[i]);" + Constants.LINE_SEPARATOR);
				w.write("}" + Constants.LINE_SEPARATOR);
			}
			w.write("w.write(\"</" + recordName + ">\\n\");" + Constants.LINE_SEPARATOR);
		}
		w.write("}" + Constants.LINE_SEPARATOR);
		i = records.iterator();
		while (i.hasNext()) {
			Element nr = (Element) i.next();
			if (rl != ALL) {
				Element reportNodeType = nr.getChild("reportNodeType");
				if (reportNodeType != null) {
					rl = reportLevel(reportNodeType.getAttributeValue("include"));
				}
			}
			createRecordOutputters(g, w, nr, rl);
		}
	}
}
