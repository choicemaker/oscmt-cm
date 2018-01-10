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
package com.choicemaker.cm.compiler.gen;

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
import com.choicemaker.cm.core.gen.CoreTags;
import com.choicemaker.cm.core.gen.DisplayTags;
import com.choicemaker.cm.core.gen.GenException;
import com.choicemaker.cm.core.gen.GeneratorHelper;
import com.choicemaker.cm.core.gen.GeneratorPlugin;
import com.choicemaker.cm.core.gen.IGenerator;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class GuiDescriptorGenerator implements GeneratorPlugin {

	public static final String SUFFIX = "____descriptor";

	public static GuiDescriptorGenerator instance = new GuiDescriptorGenerator();

	public void generate(IGenerator g) throws GenException {
		createDescriptor(g, g.getRootRecord(), new ArrayList());
	}

	private void createDescriptor(IGenerator g, Element r, List ancestors) throws GenException {
		try {
			String holderClassName = r.getAttributeValue(CoreTags.CLASS_NAME);
			String className = holderClassName + SUFFIX;
			String fileName = g.getSourceCodePackageRoot() + File.separator + className + CoreTags.JAVA_EXTENSION;
			g.addGeneratedFile(fileName);
			FileOutputStream fs = new FileOutputStream(new File(fileName).getAbsoluteFile());
			Writer w = new OutputStreamWriter(new BufferedOutputStream(fs));
			w.write("// Generated by ChoiceMaker. Do not edit." + Constants.LINE_SEPARATOR);
			w.write("package " + g.getPackage() + ";" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.client.api.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.base.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.cm.core.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import com.choicemaker.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import java.util.ArrayList;" + Constants.LINE_SEPARATOR);
			w.write("import java.util.HashMap;" + Constants.LINE_SEPARATOR);
			w.write("import javax.swing.JLabel;" + Constants.LINE_SEPARATOR);
			w.write(g.getImports());
			// next line added by Arturo. Martin made Descriptor extend Serializable instead.
			//			w.write("public class " + className + " implements java.io.Serializable, com.choicemaker.cm.core.Descriptor {" + Constants.LINE_SEPARATOR);
			w.write(
				"public class "
					+ className
					+ " implements com.choicemaker.cm.core.Descriptor {"
					+ Constants.LINE_SEPARATOR);
			w.write(
				"public static com.choicemaker.cm.core.Descriptor instance = new "
					+ className
					+ "();"
					+ Constants.LINE_SEPARATOR);
			w.write("private static HashMap m;" + Constants.LINE_SEPARATOR);
			w.write("private static ColumnDefinition[] cols = {" + Constants.LINE_SEPARATOR);
			List fields = new ArrayList(r.getChildren(CoreTags.FIELD));
			Iterator iFields = fields.iterator();
			while (iFields.hasNext()) {
				Element f = (Element) iFields.next();
				Element displayField = f.getChild(DisplayTags.DISPLAY_FIELD);
				// 2014-04-24 rphall: Commented out unused local variable.
//				Element derivedField = f.getChild(CoreTags.DERIVED);
				if (displayField != null && !GeneratorHelper.getBooleanAttribute(displayField, CoreTags.USE, true) ||
					GeneratorHelper.isNodeInitScope(f)) {
					iFields.remove();
				} else {
					String fieldName = displayField != null ? displayField.getAttributeValue(CoreTags.NAME) : null;
					if (fieldName == null) {
						fieldName = f.getAttributeValue(CoreTags.NAME);
						if (f.getChild(CoreTags.DERIVED) != null) {
							fieldName = "< " + fieldName + " >";
						}
					}
					w.write(
						"new ColumnDefinition(\""
							+ fieldName
							+ "\", \""
							+ f.getAttributeValue(CoreTags.NAME)
							+ "\", 100, JLabel.CENTER)");
					if (iFields.hasNext()) {
						w.write("," + Constants.LINE_SEPARATOR);
					}
				}
			}
			w.write("};" + Constants.LINE_SEPARATOR);
			w.write("private static com.choicemaker.cm.core.Descriptor[] children = {" + Constants.LINE_SEPARATOR);
			List records = new ArrayList(r.getChildren(CoreTags.NODE_TYPE));
			Iterator iRecords = records.iterator();
			boolean first = true;
			while (iRecords.hasNext()) {
				Element nr = (Element) iRecords.next();
				// todo: support nodeType attribute
				if (first) {
					first = false;
				} else {
					w.write("," + Constants.LINE_SEPARATOR);
				}
				String name = nr.getAttributeValue(CoreTags.CLASS_NAME) + SUFFIX;
				w.write(name + ".instance");
			}
			w.write("};" + Constants.LINE_SEPARATOR);
			SrcNames srcNames = new SrcNames();
			w.write("public boolean[] getEditable(DerivedSource src) {" + Constants.LINE_SEPARATOR);
			w.write("return new boolean[] {" + Constants.LINE_SEPARATOR);
			first = true;
			iFields = fields.iterator();
			while (iFields.hasNext()) {
				Element f = (Element) iFields.next();
				if (first) {
					first = false;
				} else {
					w.write("," + Constants.LINE_SEPARATOR);
				}
				Element d = f.getChild(CoreTags.DERIVED);
				if (d != null) {
					int srcId = srcNames.getId(d.getAttributeValue(CoreTags.SRC));
					w.write("!__src" + srcId + ".includes(src)");
				} else {
					w.write("true");
				}
			}
			w.write("};" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write(srcNames.getDeclarations());
			w.write("public String getName() {" + Constants.LINE_SEPARATOR);
			Element displayNodeType = GeneratorHelper.getNodeTypeExt(r, DisplayTags.DISPLAY);
			String displayName = displayNodeType != null ? displayNodeType.getAttributeValue(CoreTags.NAME) : null;
			if (displayName == null) {
				displayName = r.getAttributeValue(CoreTags.NAME);
			}
			w.write("return \"" + displayName + "\";" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public String getRecordName() {" + Constants.LINE_SEPARATOR);
			w.write("return \"" + r.getAttributeValue(CoreTags.NAME) + "\";" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public boolean isStackable() {" + Constants.LINE_SEPARATOR);
			w.write("return " + (ancestors.isEmpty() ? "false" : "true") + ";" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public ColumnDefinition[] getColumnDefinitions() {" + Constants.LINE_SEPARATOR);
			w.write("return cols;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public Descriptor[] getChildren() {" + Constants.LINE_SEPARATOR);
			w.write("return children;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public Record[][] getChildRecords(Record ri) {" + Constants.LINE_SEPARATOR);
			w.write("return null;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public String getValueAsString(Record ri, int row, int col) {" + Constants.LINE_SEPARATOR);
			createGetMethod(g, r, ancestors, holderClassName, w, fields, STRING_GETTER_INSTANCE);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public Object getValue(Record ri, int row, int col) {" + Constants.LINE_SEPARATOR);
			createGetMethod(g, r, ancestors, holderClassName, w, fields, GETTER_INSTANCE);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public Class getHandledClass() {" + Constants.LINE_SEPARATOR);
			w.write("return " + holderClassName + ".class;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);

			w.write("public boolean setValue(Record ri, int row, int col, String value) {" + Constants.LINE_SEPARATOR);
			w.write("try {" + Constants.LINE_SEPARATOR);
			if (ancestors.isEmpty()) {
				w.write("if(row == 0) {" + Constants.LINE_SEPARATOR);
				w.write(holderClassName + " r = (" + holderClassName + ")ri;" + Constants.LINE_SEPARATOR);
				SETTER_INSTANCE.write(g, w, r, fields, "r", null, null);
				w.write("} else {" + Constants.LINE_SEPARATOR);
				w.write("throw new IndexOutOfBoundsException();" + Constants.LINE_SEPARATOR);
				w.write("}" + Constants.LINE_SEPARATOR);
			} else {
				writeAccess(g, w, r, ancestors, fields, SETTER_INSTANCE);
			}
			w.write("} catch(Exception ex) {" + Constants.LINE_SEPARATOR);
			w.write("return false;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);

			w.write("public boolean getValidity(Record ri, int row, int col) {" + Constants.LINE_SEPARATOR);
			if (ancestors.isEmpty()) {
				w.write("if(row == 0) {" + Constants.LINE_SEPARATOR);
				w.write(holderClassName + " r = (" + holderClassName + ")ri;" + Constants.LINE_SEPARATOR);
				VALIDITY_INSTANCE.write(g, w, r, fields, "r", null, null);
				w.write("} else {" + Constants.LINE_SEPARATOR);
				w.write("throw new IndexOutOfBoundsException();" + Constants.LINE_SEPARATOR);
				w.write("}" + Constants.LINE_SEPARATOR);
			} else {
				writeAccess(g, w, r, ancestors, fields, VALIDITY_INSTANCE);
			}
			w.write("}" + Constants.LINE_SEPARATOR);

			w.write("public void addRow(int row, boolean above, Record ri) {" + Constants.LINE_SEPARATOR);
			if (ancestors.isEmpty()) {
				w.write(
					"throw new UnsupportedOperationException(\"Unable to delete row. Data not stacked.\");"
						+ Constants.LINE_SEPARATOR);
			} else {
				writeAccess(g, w, r, ancestors, fields, ADDER_INSTANCE);
			}
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public void deleteRow(Record ri, int row) {" + Constants.LINE_SEPARATOR);
			if (ancestors.isEmpty()) {
				w.write(
					"throw new UnsupportedOperationException(\"Unable to add row. Data not stacked.\");"
						+ Constants.LINE_SEPARATOR);
			} else {
				writeAccess(g, w, r, ancestors, fields, DELETER_INSTANCE);
			}
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public int getColumnCount() {" + Constants.LINE_SEPARATOR);
			w.write("return cols.length;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("public int getRowCount(Record ri) {" + Constants.LINE_SEPARATOR);
			createGetRowCount(g, w, r, ancestors);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write(Constants.LINE_SEPARATOR);
			w.write("public int getColumnIndexByName(String name) {" + Constants.LINE_SEPARATOR);
			w.write("if(m == null) {" + Constants.LINE_SEPARATOR);
			w.write("m = new HashMap(cols.length);" + Constants.LINE_SEPARATOR);
			w.write("for(int i = 0; i < cols.length; ++i) {" + Constants.LINE_SEPARATOR);
			w.write("m.put(cols[i].getFieldName(), new Integer(i));" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("Object o = m.get(name);" + Constants.LINE_SEPARATOR);
			w.write("if(o == null) {" + Constants.LINE_SEPARATOR);
			w.write("return -1;" + Constants.LINE_SEPARATOR);
			w.write("} else {" + Constants.LINE_SEPARATOR);
			w.write("return ((Integer)o).intValue();" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.flush();
			fs.close();
			if (!records.isEmpty()) {
				ancestors.add(r);
				iRecords = records.iterator();
				while (iRecords.hasNext()) {
					createDescriptor(g, (Element) iRecords.next(), ancestors);
				}
				ancestors.remove(ancestors.size() - 1);
			}
		} catch (IOException ex) {
			throw new GenException("Problem writing file.", ex);
		}
	}

	private void createGetMethod(
		IGenerator g,
		Element r,
		List ancestors,
		String holderClassName,
		Writer w,
		List fields,
		Inside pp)
		throws IOException {
		if (ancestors.isEmpty()) {
			w.write("if(row == 0) {" + Constants.LINE_SEPARATOR);
			w.write(holderClassName + " r = (" + holderClassName + ")ri;" + Constants.LINE_SEPARATOR);
			STRING_GETTER_INSTANCE.write(g, w, r, fields, "r", null, null);
			w.write("} else {" + Constants.LINE_SEPARATOR);
			w.write("throw new IndexOutOfBoundsException();" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
		} else {
			writeAccess(g, w, r, ancestors, fields, pp);
		}
	}

	private void createGetRowCount(IGenerator g, Writer w, Element r, List ancestors) throws IOException {
		if (ancestors.isEmpty()) {
			w.write("return 1;" + Constants.LINE_SEPARATOR);
		} else {
			Element p = (Element) ancestors.get(0);
			String className = p.getAttributeValue(CoreTags.CLASS_NAME);
			w.write(className + " r0 = (" + className + ")ri;" + Constants.LINE_SEPARATOR);
			w.write("int num = 0;" + Constants.LINE_SEPARATOR);
			int i = 0;
			while (i < ancestors.size() - 1) {
				++i;
				Element e = (Element) ancestors.get(i);
				String name = e.getAttributeValue(CoreTags.NAME);
				w.write(
					"for(int i"
						+ i
						+ " = 0; i"
						+ i
						+ " < r"
						+ (i - 1)
						+ "."
						+ name
						+ ".length; ++i"
						+ i
						+ ") {"
						+ Constants.LINE_SEPARATOR);
				className = e.getAttributeValue("className");
				w.write(
					className + " r" + i + " = r" + (i - 1) + "." + name + "[i" + i + "];" + Constants.LINE_SEPARATOR);
			}
			w.write("num += r" + i + "." + r.getAttributeValue(CoreTags.NAME) + ".length;" + Constants.LINE_SEPARATOR);
			for (int j = 0; j < ancestors.size() - 1; ++j) {
				w.write("}" + Constants.LINE_SEPARATOR);
			}
			w.write("return num;" + Constants.LINE_SEPARATOR);
		}
	}

	private void writeAccess(IGenerator g, Writer w, Element r, List ancestors, List fields, Inside pp)
		throws IOException {
		Element p = (Element) ancestors.get(0);
		String className = p.getAttributeValue(CoreTags.CLASS_NAME);
		w.write(className + " r0 = (" + className + ")ri;" + Constants.LINE_SEPARATOR);
		pp.adjust(w);
		w.write("int cur = 0;" + Constants.LINE_SEPARATOR);
		int i = 0;
		while (i < ancestors.size() - 1) {
			++i;
			Element e = (Element) ancestors.get(i);
			pp.insert(w, i - 1, e);
			String name = e.getAttributeValue(CoreTags.NAME);
			w.write(
				"for(int i"
					+ i
					+ " = 0; i"
					+ i
					+ " < r"
					+ (i - 1)
					+ "."
					+ name
					+ ".length; ++i"
					+ i
					+ ") {"
					+ Constants.LINE_SEPARATOR);
			className = e.getAttributeValue(CoreTags.CLASS_NAME);
			w.write(className + " r" + i + " = r" + (i - 1) + "." + name + "[i" + i + "];" + Constants.LINE_SEPARATOR);
		}
		Element e = r;
		String name = e.getAttributeValue(CoreTags.NAME);
		w.write("if(");
		pp.condition(w, i, name);
		//cur + r" + i + "." + name + ".length <= row
		w.write(") {" + Constants.LINE_SEPARATOR);
		w.write("cur += r" + i + "." + name + ".length;" + Constants.LINE_SEPARATOR);
		w.write("} else {" + Constants.LINE_SEPARATOR);
		pp.write(g, w, r, fields, "r" + i + "." + name + "[row-cur]", "r" + i + "." + name, "(row - cur)");
		w.write("}" + Constants.LINE_SEPARATOR);
		for (int j = 0; j < ancestors.size() - 1; ++j) {
			w.write("}" + Constants.LINE_SEPARATOR);
		}
		w.write("throw new IndexOutOfBoundsException();" + Constants.LINE_SEPARATOR);
	}

	private static Getter STRING_GETTER_INSTANCE = new Getter(true);
	private static Getter GETTER_INSTANCE = new Getter(false);
	private static Setter SETTER_INSTANCE = new Setter();
	private static Validity VALIDITY_INSTANCE = new Validity();
	private static Deleter DELETER_INSTANCE = new Deleter();
	private static Adder ADDER_INSTANCE = new Adder();

	private static abstract class Inside {
		abstract void write(IGenerator g, Writer w, Element r, List fields, String fld, String rcrd, String idx)
			throws IOException;
		void adjust(Writer w) throws IOException {
		}
		void condition(Writer w, int i, String name) throws IOException {
			w.write("cur + r" + i + "." + name + ".length <= row");
		}
		void insert(Writer w, int i, Element e) throws IOException {
		}
	}

	private static class Getter extends Inside {
		private boolean asString;

		Getter(boolean asString) {
			this.asString = asString;
		}

		void write(IGenerator g, Writer w, Element r, List fields, String fld, String rcrd, String idx)
			throws IOException {
			w.write("switch(col) {" + Constants.LINE_SEPARATOR);
			for (int i = 0; i < fields.size(); ++i) {
				w.write("case " + i + ":" + Constants.LINE_SEPARATOR);
				w.write("return ");
				Element e = (Element) fields.get(i);
				String type = e.getAttributeValue("type").intern();
				String name = fld + "." + e.getAttributeValue(CoreTags.NAME);
				if (asString) {
					if (GeneratorHelper.isPrimitiveType(type)) {
						if (type == "char") {
							w.write(name + " == '\\0' ? \"\" : ");
						}
						w.write("String.valueOf(" + name + ");" + Constants.LINE_SEPARATOR);
					} else {
						if (type == "String" || type == "java.lang.String") {
							w.write(name + ";" + Constants.LINE_SEPARATOR);
						} else if (type == "Date" || type == "java.util.Date") {
							w.write(
								name + " == null ? null : DateHelper.formatDisplay(" + name + ");" + Constants.LINE_SEPARATOR);
						} else {
							w.write(name + " == null ? null : " + name + ".toString();" + Constants.LINE_SEPARATOR);
						}
					}
				} else {
					if (GeneratorHelper.isPrimitiveType(type)) {
						w.write(GeneratorHelper.getObjectExpr(type, name) + ";" + Constants.LINE_SEPARATOR);
					} else {
						w.write(name + ";" + Constants.LINE_SEPARATOR);
					}
				}
			}
			w.write("default:" + Constants.LINE_SEPARATOR);
			w.write("throw new IndexOutOfBoundsException();" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
		}
	}

	private static class Validity extends Inside {
		void write(IGenerator g, Writer w, Element r, List fields, String fld, String rcrd, String idx)
			throws IOException {
			w.write("switch(col) {" + Constants.LINE_SEPARATOR);
			for (int i = 0; i < fields.size(); ++i) {
				w.write("case " + i + ":" + Constants.LINE_SEPARATOR);
				w.write("return ");
				Element e = (Element) fields.get(i);
				String name = fld + "." + "__v_" + e.getAttributeValue("name");
				w.write(name + ";" + Constants.LINE_SEPARATOR);
			}
			w.write("default:" + Constants.LINE_SEPARATOR);
			w.write("throw new IndexOutOfBoundsException();" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
		}
	}

	private static class Setter extends Inside {
		void write(IGenerator g, Writer w, Element r, List fields, String fld, String rcrd, String idx)
			throws IOException {
			w.write("switch(col) {" + Constants.LINE_SEPARATOR);
			for (int i = 0; i < fields.size(); ++i) {
				w.write("case " + i + ":" + Constants.LINE_SEPARATOR);
				Element e = (Element) fields.get(i);
				w.write(
					fld
						+ "."
						+ e.getAttributeValue(CoreTags.NAME)
						+ " = "
						+ GeneratorHelper.getFromStringConversionExpression(
							e.getAttributeValue(CoreTags.TYPE),
							"value",
							false,
							g.isIntern(),
							false)
						+ ";"
						+ Constants.LINE_SEPARATOR);
				w.write("break;" + Constants.LINE_SEPARATOR);
			}
			w.write("default:" + Constants.LINE_SEPARATOR);
			w.write("throw new IndexOutOfBoundsException();" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			if (fields.size() > 0) {
				w.write("return true;");
			}
		}
	}

	private static class Adder extends Inside {
		void adjust(Writer w) throws IOException {
			w.write("if(!above) {" + Constants.LINE_SEPARATOR);
			w.write("++row;" + Constants.LINE_SEPARATOR);
			w.write("};" + Constants.LINE_SEPARATOR);
		}

		void condition(Writer w, int i, String name) throws IOException {
			String lhs = "cur + r" + i + "." + name + ".length";
			w.write(lhs + " < row || (above && " + lhs + " == row && row != 0)");
		}

		void insert(Writer w, int i, Element e) throws IOException {
			String name = e.getAttributeValue(CoreTags.NAME);
			String className = e.getAttributeValue(CoreTags.CLASS_NAME);
			String rec = "r" + i + "." + name;
			w.write("if(" + rec + ".length == 0) {" + Constants.LINE_SEPARATOR);
			w.write(rec + " = new " + className + "[1];" + Constants.LINE_SEPARATOR);
			w.write(rec + "[0] = " + className + ".instance();" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
		}

		void write(IGenerator g, Writer w, Element r, List fields, String fld, String rcrd, String idx)
			throws IOException {
			String className = r.getAttributeValue(CoreTags.CLASS_NAME);
			w.write(className + "[] tmp = new " + className + "[" + rcrd + ".length + 1];" + Constants.LINE_SEPARATOR);
			w.write("System.arraycopy(" + rcrd + ", 0, tmp, 0, " + idx + ");" + Constants.LINE_SEPARATOR);
			w.write("tmp[" + idx + "] = " + className + ".instance();" + Constants.LINE_SEPARATOR);
			w.write(
				"System.arraycopy("
					+ rcrd
					+ ", "
					+ idx
					+ ", tmp, "
					+ idx
					+ " + 1, tmp.length - 1 - "
					+ idx
					+ ");"
					+ Constants.LINE_SEPARATOR);
			w.write(rcrd + " = tmp;" + Constants.LINE_SEPARATOR);
			w.write("return;");
		}
	}

	private static class Deleter extends Inside {
		void write(IGenerator g, Writer w, Element r, List fields, String fld, String rcrd, String idx)
			throws IOException {
			String className = r.getAttributeValue(CoreTags.CLASS_NAME);
			w.write(className + "[] tmp = new " + className + "[" + rcrd + ".length - 1];" + Constants.LINE_SEPARATOR);
			w.write("System.arraycopy(" + rcrd + ", 0, tmp, 0, " + idx + ");" + Constants.LINE_SEPARATOR);
			w.write(
				"System.arraycopy("
					+ rcrd
					+ ", "
					+ idx
					+ " + 1, tmp, "
					+ idx
					+ ", tmp.length - "
					+ idx
					+ ");"
					+ Constants.LINE_SEPARATOR);
			w.write(rcrd + " = tmp;" + Constants.LINE_SEPARATOR);
			w.write("return;");
		}
	}

}
