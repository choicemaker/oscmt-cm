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

import org.jdom.Element;

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
 * @version   $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:02:58 $
 */
public class XmlReaderGenerator implements GeneratorPlugin {
	private static DerivedSource src = DerivedSource.valueOf("xml");
	public static XmlReaderGenerator instance = new XmlReaderGenerator();

	public void generate(IGenerator g) throws GenException {
		String className = g.getSchemaName() + "XmlReader";
		String packageName = g.getPackage() + ".xml";
		g.addAccessorBody("public Object getXmlReader() {" + Constants.LINE_SEPARATOR);
		g.addAccessorBody("return new " + packageName + "." + className + "();" + Constants.LINE_SEPARATOR);
		g.addAccessorBody("}" + Constants.LINE_SEPARATOR);
		try {
			String directoryName = g.getSourceCodePackageRoot() + File.separator + "xml";
			String fileName = directoryName + File.separator + className + ".java";
			g.addGeneratedFile(fileName);
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			Writer w = new OutputStreamWriter(new BufferedOutputStream(fs));
			w.write("// Generated by ChoiceMaker. Do not edit." + Constants.LINE_SEPARATOR);
			w.write("package " + packageName + ";" + Constants.LINE_SEPARATOR);
			w.write("import org.apache.log4j.*;" + Constants.LINE_SEPARATOR);
			w.write("import java.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import org.xml.sax.*;" + Constants.LINE_SEPARATOR);
			w.write("import org.xml.sax.helpers.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.io.xml.base.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import " + g.getPackage() + ".*;" + Constants.LINE_SEPARATOR);
			w.write(g.getImports());
			w.write("public final class " + className + " extends XmlReader {" + Constants.LINE_SEPARATOR);
			w.write(
				"private static Logger logger = Logger.getLogger("
					+ packageName
					+ "."
					+ className
					+ ".class);"
					+ Constants.LINE_SEPARATOR);
			// could do optimization for case where stacking depth == 1
			w.write(
				"private int[] typeStack = new int[" + (g.getStackingDepth() + 4) + "];" + Constants.LINE_SEPARATOR);
			w.write("private int top = 0;" + Constants.LINE_SEPARATOR);
			w.write("private int unknown = 0;" + Constants.LINE_SEPARATOR);
			varDeclarations(w, g.getRootRecord());
			w.write("private RecordHandler rh;" + Constants.LINE_SEPARATOR);
			w.write("private static DerivedSource src = DerivedSource.valueOf(\"xml\");" + Constants.LINE_SEPARATOR);
			w.write("public void open(RecordHandler rh) {" + Constants.LINE_SEPARATOR);
			w.write("this.rh = rh;" + Constants.LINE_SEPARATOR);
			w.write("typeStack[top] = -1;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write(
				"public void startElement (String uri, String localName, String qName, Attributes atts) throws SAXException {"
					+ Constants.LINE_SEPARATOR);
			w.write("String __tmpStr;" + Constants.LINE_SEPARATOR);
			w.write("if(unknown > 0) {" + Constants.LINE_SEPARATOR);
			w.write("++unknown;" + Constants.LINE_SEPARATOR);
			w.write("} else {" + Constants.LINE_SEPARATOR);
			w.write("String elementName = qName.intern();" + Constants.LINE_SEPARATOR);
			w.write("switch(typeStack[top]) {" + Constants.LINE_SEPARATOR);
			int unknownAttribute = CoreTags.IGNORE;
			int unknownElement = CoreTags.IGNORE;
			Element xmlDef = GeneratorHelper.getGlobalExt(g.getRootElement(), "xml");
			if (xmlDef != null) {
				unknownAttribute = GeneratorHelper.getUnknown(xmlDef, "unknownAttribute", CoreTags.IGNORE);
				unknownElement = GeneratorHelper.getUnknown(xmlDef, "unknownElement", CoreTags.IGNORE);
			}
			parsingBegin(g, w, g.getRootElement(), unknownAttribute, unknownElement);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public void endElement (String uri, String localName, String qName) throws SAXException {" + Constants.LINE_SEPARATOR);
			w.write("if(unknown > 0) {" + Constants.LINE_SEPARATOR);
			w.write("--unknown;" + Constants.LINE_SEPARATOR);
			w.write("} else {" + Constants.LINE_SEPARATOR);
			w.write("switch(typeStack[top]) {" + Constants.LINE_SEPARATOR);
			parsingEnd(w, g.getRootRecord());
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("--top;" + Constants.LINE_SEPARATOR);
			w.write("if(top == 0) {" + Constants.LINE_SEPARATOR);
			w.write(
				"o__"
					+ g.getRootRecord().getAttributeValue("className")
					+ ".computeValidityAndDerived(src);"
					+ Constants.LINE_SEPARATOR);
			w.write(
				"rh.handleRecord(o__"
					+ g.getRootRecord().getAttributeValue("className")
					+ ");"
					+ Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.flush();
			fs.close();
		} catch (IOException ex) {
			throw new GenException("Problem writing file.", ex);
		}
	}

	private void parsingEnd(Writer w, Element r) throws IOException {
		String className = r.getAttributeValue("className");
		w.write("case " + r.getAttributeValue("recordNumber") + ":" + Constants.LINE_SEPARATOR);
		List records = r.getChildren(CoreTags.NODE_TYPE);
		Iterator ir = records.iterator();
		while (ir.hasNext()) {
			Element e = (Element) ir.next();
			String eClassName = e.getAttributeValue("className");
			w.write(
				"l__"
					+ eClassName
					+ ".toArray(o__"
					+ className
					+ "."
					+ e.getAttributeValue("name")
					+ " = new "
					+ eClassName
					+ "[l__"
					+ eClassName
					+ ".size()]);"
					+ Constants.LINE_SEPARATOR);
			w.write("l__" + eClassName + ".clear();" + Constants.LINE_SEPARATOR);
		}
		w.write("break;" + Constants.LINE_SEPARATOR);
		ir = records.iterator();
		while (ir.hasNext()) {
			Element e = (Element) ir.next();
			if (e.getChild(CoreTags.NODE_TYPE) != null) {
				parsingEnd(w, e);
			}
		}
	}

	private void parsingBegin(IGenerator g, Writer w, Element outer, int unknownAttribute, int unknownElement)
		throws IOException {
		int recordNumber = Integer.parseInt(outer.getAttributeValue("recordNumber"));
		w.write("case " + recordNumber + ":" + Constants.LINE_SEPARATOR);
		List records = outer.getChildren(CoreTags.NODE_TYPE);
		Iterator ir = records.iterator();
		while (ir.hasNext()) {
			Element r = (Element) ir.next();
			String className = r.getAttributeValue("className");
			w.write("if(elementName == \"" + r.getAttributeValue("name") + "\") {" + Constants.LINE_SEPARATOR);
			w.write("typeStack[++top] = " + r.getAttributeValue("recordNumber") + ";" + Constants.LINE_SEPARATOR);
			boolean isLeafRecord = r.getAttributeValue("isLeafRecord").equals("true");
			if (isLeafRecord && recordNumber != -1) {
				w.write(className + " ");
			}
			w.write("o__" + className + " = new " + className + "();" + Constants.LINE_SEPARATOR);
			if (recordNumber != -1) {
				w.write(
					"o__"
						+ className
						+ ".outer = o__"
						+ outer.getAttributeValue("className")
						+ ";"
						+ Constants.LINE_SEPARATOR);
				w.write("l__" + className + ".add(o__" + className + ");" + Constants.LINE_SEPARATOR);
			}
			List fields = new ArrayList(r.getChildren("field"));
			GeneratorHelper.filterFields(fields, src, "xmlField");
			if (!fields.isEmpty()) {
				w.write("int len = atts.getLength();" + Constants.LINE_SEPARATOR);
				w.write("for(int i = 0; i < len; ++i) {" + Constants.LINE_SEPARATOR);
				w.write("String attributeName = atts.getQName(i).intern();" + Constants.LINE_SEPARATOR);
				Iterator iFields = fields.iterator();
				while (iFields.hasNext()) {
					Element f = (Element) iFields.next();
					String fieldName = f.getAttributeValue("name");
					w.write("if(attributeName == \"" + fieldName + "\") {" + Constants.LINE_SEPARATOR);
					w.write(
						"o__"
							+ className
							+ "."
							+ fieldName
							+ " = "
							+ GeneratorHelper.getFromStringConversionExpression(
								f.getAttributeValue("type"),
								"atts.getValue(i)",
								false,
								g.isIntern(),
								true)
							+ ";"
							+ Constants.LINE_SEPARATOR);
					w.write("} else ");
				}
				w.write("{" + Constants.LINE_SEPARATOR);
				if (unknownAttribute != CoreTags.IGNORE) {
					w.write(
						"logger.warn(\"Unknown attribute ["
							+ r.getAttributeValue("name")
							+ "]: \" + attributeName);"
							+ Constants.LINE_SEPARATOR);
					if (unknownAttribute == CoreTags.EXCEPTION) {
						w.write(
							"throw new SAXException(\"Unknown attribute ["
								+ r.getAttributeValue("name")
								+ "]: \" + attributeName);"
								+ Constants.LINE_SEPARATOR);
					}
				}
				w.write("}" + Constants.LINE_SEPARATOR);
				w.write("}" + Constants.LINE_SEPARATOR);
			}
			if (ir.hasNext()) {
				w.write("} else ");
			} else {
				w.write("} else {" + Constants.LINE_SEPARATOR);
				w.write("++unknown;" + Constants.LINE_SEPARATOR);
				if (unknownElement != CoreTags.IGNORE) {
					w.write(
						"logger.warn(\"Invalid element ["
							+ outer.getAttributeValue("name")
							+ "]: \" + elementName);"
							+ Constants.LINE_SEPARATOR);
					if (unknownElement == CoreTags.EXCEPTION) {
						w.write(
							"throw new SAXException(\"Invalid element: \" + elementName);" + Constants.LINE_SEPARATOR);
					}
				}
				w.write("}" + Constants.LINE_SEPARATOR);
				w.write("break;" + Constants.LINE_SEPARATOR);
			}
		}
		ir = records.iterator();
		while (ir.hasNext()) {
			Element e = (Element) ir.next();
			if (e.getChild(CoreTags.NODE_TYPE) != null) {
				parsingBegin(g, w, e, unknownAttribute, unknownElement);
			}
		}
	}

	private void varDeclarations(Writer w, Element e) throws IOException {
		String className = e.getAttributeValue("className");
		w.write("private LinkedList l__" + className + " = new LinkedList();" + Constants.LINE_SEPARATOR);
		List nestedRecords = e.getChildren(CoreTags.NODE_TYPE);
		if (!nestedRecords.isEmpty() || e.getAttributeValue("recordNumber").equals("0")) {
			w.write("private " + className + " o__" + className + ";" + Constants.LINE_SEPARATOR);
		}
		Iterator i = nestedRecords.iterator();
		while (i.hasNext()) {
			varDeclarations(w, (Element) i.next());
		}
	}
}
