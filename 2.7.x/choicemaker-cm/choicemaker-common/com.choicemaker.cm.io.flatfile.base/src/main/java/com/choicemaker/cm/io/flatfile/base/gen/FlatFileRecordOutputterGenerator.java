/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base.gen;

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
import com.choicemaker.cm.io.flatfile.base.FlatFileOutput;
import com.choicemaker.util.StringUtils;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class FlatFileRecordOutputterGenerator implements GeneratorPlugin {
	private boolean report;

	private final static int NONE = 0;
	private final static int SELECT = 1;
	private final static int TABLE = 2;
	private final static int ALL = 3;

	private static int reportLevel(String s) {
		if (s == null) {
			return NONE;
		} else {
			s = s.intern();
			if ("select" == s) {
				return SELECT;
			} else if ("table" == s) {
				return TABLE;
			} else if ("all" == s) {
				return ALL;
			} else {
				return NONE;
			}
		}
	}

	private static DerivedSource src = DerivedSource.valueOf("flatfile");

	public FlatFileRecordOutputterGenerator(boolean report) {
		this.report = report;
	}

	public static FlatFileRecordOutputterGenerator instance =
		new FlatFileRecordOutputterGenerator(false);

	@Override
	public void generate(IGenerator g) throws GenException {
		String className = g.getSchemaName();
		String packageName = g.getPackage();
		String directoryName = g.getSourceCodePackageRoot();
		if (report) {
			className += "FlatFileReporter";
			//			g.addAccessorBody(
			//				"private static final " + className + " flatFileReportOutputter = new " + packageName + "." + className + "();" + Constants.LINE_SEPARATOR);
			g.addAccessorBody(
				"public Object getFlatFileReportOutputter(boolean multiFile, boolean singleLine, boolean fixedLength, char sep, boolean tagged, int tagWidth, boolean filter) {"
					+ Constants.LINE_SEPARATOR);
			g.addAccessorBody(
				"return new "
					+ packageName
					+ "."
					+ className
					+ "(multiFile, singleLine, fixedLength, sep, tagged, tagWidth, filter);"
					+ Constants.LINE_SEPARATOR);
			g.addAccessorBody("}" + Constants.LINE_SEPARATOR);
		} else {
			className += "FlatFileRecordOutputter";
			packageName += ".flatfile";
			directoryName += File.separator + "flatfile";
			g.addAccessorBody(
				"public Object getFlatFileRecordOutputter(boolean multiFile, boolean singleLine, boolean fixedLength, char sep, boolean tagged, int tagWidth, boolean filter) {"
					+ Constants.LINE_SEPARATOR);
			g.addAccessorBody(
				"return new "
					+ packageName
					+ "."
					+ className
					+ "(multiFile, singleLine, fixedLength, sep, tagged, tagWidth, filter);"
					+ Constants.LINE_SEPARATOR);
			g.addAccessorBody("}" + Constants.LINE_SEPARATOR);
		}
		try {
			String fileName =
				directoryName + File.separator + className + ".java";
			g.addGeneratedFile(fileName);
			FileOutputStream fs =
				new FileOutputStream(new File(fileName).getAbsoluteFile());
			Writer w = new OutputStreamWriter(new BufferedOutputStream(fs));
			w.write(
				"// Generated by ChoiceMaker. Do not edit."
					+ Constants.LINE_SEPARATOR);
			w.write("package " + packageName + ";" + Constants.LINE_SEPARATOR);
			w.write("import java.util.logging.*;" + Constants.LINE_SEPARATOR);
			w.write("import java.util.*;" + Constants.LINE_SEPARATOR);
			w.write("import java.io.*;" + Constants.LINE_SEPARATOR);
			w.write(
				"import com.choicemaker.cm.core.*;" + Constants.LINE_SEPARATOR);
			w.write(
				"import com.choicemaker.cm.core.base.*;" + Constants.LINE_SEPARATOR);
			w.write(
				"import com.choicemaker.cm.io.flatfile.base.*;"
					+ Constants.LINE_SEPARATOR);
			w.write(
				"import " + g.getPackage() + ".*;" + Constants.LINE_SEPARATOR);
			w.write(g.getImports());
			w.write(
				"public final class "
					+ className
					+ " implements FlatFileRecordOutputter {"
					+ Constants.LINE_SEPARATOR);
			w.write(
				"private static Logger logger = Logger.getLogger("
					+ packageName
					+ "."
					+ className
					+ ".class.getName());"
					+ Constants.LINE_SEPARATOR);
			w.write("private boolean multiFile;" + Constants.LINE_SEPARATOR);
			w.write("private boolean singleLine;" + Constants.LINE_SEPARATOR);
			w.write("private boolean fixedLength;" + Constants.LINE_SEPARATOR);
			w.write("private char sep;" + Constants.LINE_SEPARATOR);
			w.write("private boolean tagged;" + Constants.LINE_SEPARATOR);
			w.write("private int tagWidth;" + Constants.LINE_SEPARATOR);
			w.write("private boolean filter;" + Constants.LINE_SEPARATOR);
			w.write(
				"private char[] lineBuffer = new char["
					+ FlatFileOutput.MAX_LINE_LENGTH
					+ "];"
					+ Constants.LINE_SEPARATOR);
			w.write(
				"public "
					+ className
					+ "(boolean multiFile, boolean singleLine, boolean fixedLength, char sep, boolean tagged, int tagWidth, boolean filter) {"
					+ Constants.LINE_SEPARATOR);
			w.write("this.multiFile = multiFile;" + Constants.LINE_SEPARATOR);
			w.write("this.singleLine = singleLine;" + Constants.LINE_SEPARATOR);
			w.write(
				"this.fixedLength = fixedLength;" + Constants.LINE_SEPARATOR);
			w.write("this.sep = sep;" + Constants.LINE_SEPARATOR);
			w.write("this.tagged = tagged;" + Constants.LINE_SEPARATOR);
			w.write("this.tagWidth = tagWidth;" + Constants.LINE_SEPARATOR);
			w.write("this.filter = filter;" + Constants.LINE_SEPARATOR);
			w.write("}" + Constants.LINE_SEPARATOR);
			w.write(
				"public void put(Writer[] ws, Record r) throws IOException {"
					+ Constants.LINE_SEPARATOR);
			Element rootRecord = g.getRootRecord();
			int rl = reportLevel(rootRecord.getAttributeValue("report"));
			boolean anyoutput = !report || rl != NONE;
			if (anyoutput) {
				String rootRecordClassName =
					rootRecord.getAttributeValue("className");
				w.write(
					"put_"
						+ rootRecordClassName
						+ "(ws, ("
						+ rootRecordClassName
						+ ")r);"
						+ Constants.LINE_SEPARATOR);
			}
			w.write("}" + Constants.LINE_SEPARATOR);
			if (anyoutput) {
				createRecordOutputters(
					g,
					w,
					rootRecord,
					rl,
					new ArrayList(),
					0);
			}
			w.write("}" + Constants.LINE_SEPARATOR);
			w.flush();
			fs.close();
		} catch (IOException ex) {
			throw new GenException("Problem writing file.", ex);
		}
	}

	private void createRecordOutputters(
		IGenerator g,
		Writer w,
		Element r,
		int rl,
		ArrayList keyFlds,
		int level)
		throws IOException {
		keyFlds = (ArrayList) keyFlds.clone();
		String recordName = r.getAttributeValue("name");
		String className = r.getAttributeValue("className");
		int recordNumber =
			Integer.parseInt(r.getAttributeValue("recordNumber"));
		boolean stacked = recordNumber != 0;
		Element ffHd = GeneratorHelper.getNodeTypeExt(r, "flatfile");
		String recordType = ffHd.getAttributeValue("tag");
		w.write(
			"private void put_"
				+ className
				+ "(Writer[] ws, "
				+ className
				+ (stacked ? "[] rs" : " r")
				+ ") throws IOException {"
				+ Constants.LINE_SEPARATOR);
		w.write(
			"Writer w = ws["
				+ (recordNumber + 1)
				+ "];"
				+ Constants.LINE_SEPARATOR);
		if (stacked) {
			w.write(
				"for(int i = 0; i < rs.length; ++i) {"
					+ Constants.LINE_SEPARATOR);
			w.write(className + " r = rs[i];" + Constants.LINE_SEPARATOR);
		}
		List fields = new ArrayList(r.getChildren("field"));
		if (report && rl == SELECT) {
			Iterator i = fields.iterator();
			while (i.hasNext()) {
				if (!"true"
					.equals(((Element) i.next()).getAttributeValue("report"))) {
					i.remove();
				}
			}
		}
		int res = GeneratorHelper.filterFields(fields, src, "flatfileField");
		switch (res) {
			case GeneratorHelper.OK :
				break;
			case GeneratorHelper.DUPLICATE_POS :
				g.error(recordName + ": Duplicate pos");
				return;
			case GeneratorHelper.POS_OUTSIDE_RANGE :
				g.error(recordName + ": pos outside range");
				return;
		}
		int lineLength = getLineLength(fields);
		boolean hasStart = lineLength != Integer.MIN_VALUE;
		if (hasStart) {
			w.write(
				"System.arraycopy(FlatFileOutput.SPACE_BUF, 0, lineBuffer, 0, "
					+ lineLength
					+ ");"
					+ Constants.LINE_SEPARATOR);
		}
		w.write("if(tagged) {" + Constants.LINE_SEPARATOR);
		w.write(
			"FlatFileOutput.write(w, "
				+ (hasStart ? "lineBuffer, " : "")
				+ "\""
				+ recordType
				+ "\", fixedLength, sep, filter, !singleLine, "
				+ (hasStart ? "0, " : "")
				+ "tagWidth);"
				+ Constants.LINE_SEPARATOR);
		w.write("}" + Constants.LINE_SEPARATOR);
		boolean firstField = true;
		if (!keyFlds.isEmpty()) {
			w.write("if(multiFile) {" + Constants.LINE_SEPARATOR);
			Iterator iK = keyFlds.iterator();
			while (iK.hasNext()) {
				KeyFld f = (KeyFld) iK.next();
				w.write(
					"FlatFileOutput.write(w, "
						+ (hasStart ? "lineBuffer, " : "")
						+ "r");
				for (int i = 0; i < level - f.level; ++i) {
					w.write(".outer");
				}
				w.write(
					"."
						+ f.fieldName
						+ ", fixedLength, sep, filter, "
						+ (firstField ? "!tagged && !singleLine" : "false")
						+ ","
						+ (hasStart ? f.start + ", " : "")
						+ f.width
						+ ");"
						+ Constants.LINE_SEPARATOR);
				firstField = false;
			}
			w.write("}" + Constants.LINE_SEPARATOR);
		}
		firstField = true;
		Iterator i = fields.iterator();
		while (i.hasNext()) {
			Element f = (Element) i.next();
			if (f != null) {
				String fieldName = f.getAttributeValue("name");
				String type = f.getAttributeValue("type");
				String width =
					String.valueOf(
						FlatFileGenerator.defaultWidth(
							f.getAttributeValue("type")));
				Element ffFld = f.getChild("flatfileField");
				char nullRepresentation =
					FlatFileGenerator.DEFAULT_NULL_REPRESENTATION;
				int start = Integer.MIN_VALUE;
				if (ffFld != null) {
					String t = ffFld.getAttributeValue("width");
					if (t != null) {
						width = t;
					}
					t = ffFld.getAttributeValue(FlatFileTags.START);
					if (t != null) {
						start = Integer.parseInt(t);
					}
					if ("true".equals(ffFld.getAttributeValue("key"))) {
						keyFlds.add(new KeyFld(fieldName, start, width, level));
					}
					String nr = ffFld.getAttributeValue("nullRepresentation");
					if (nr != null) {
						nullRepresentation = StringUtils.getChar(nr);
					}
				}
				w.write(
					"FlatFileOutput.write(w, "
						+ (hasStart ? "lineBuffer, " : "")
						+ "r."
						+ fieldName
						+ ", fixedLength, sep, filter, "
						+ (firstField
							? "!tagged && !singleLine"
								+ (level == 0 ? "" : " && !multiFile")
							: "false")
						+ ","
						+ (hasStart ? start + ", " : "")
						+ width
						+ ("char".equals(type)
							? (", '"
								+ FlatFileGenerator.charToCode(
									nullRepresentation)
								+ "'")
							: "")
						+ ");"
						+ Constants.LINE_SEPARATOR);
			// <BUGFIX author="rphall" date="2005-10-25"
			//		summary="remove condition that firstField is false">
			// <BUG author="rphall" date="2005-10-25">
			// The first field will be null only if "pos=" is specified
			// in the schema. As of 2005-10-25, this code is exercised
			// only by the NSW project. Furthermore, this code appears
			// to introduce an "off-by-one" error. The NSW project has
			// 29 fields, with patientid (the key for stacked records)
			// starting at position = 28 (zero-based). This code writes
			// an outputter that writes 27 separators between the starting
			// "0" tag and the patientid field, whereas the single file
			// flatfile reader expects 28 separators (which is the correct
			// number.
			//} else if (!firstField) {
			// </BUG>
			} else /* if (!firstField) */ {
			// </BUGFIX>
				w.write("if(!fixedLength) {" + Constants.LINE_SEPARATOR);
				w.write("w.write(sep);" + Constants.LINE_SEPARATOR);
				w.write("}" + Constants.LINE_SEPARATOR);
			}
			firstField = false;
		}
		if (hasStart) {
			w.write(
				"w.write(lineBuffer, 0, "
					+ lineLength
					+ ");"
					+ Constants.LINE_SEPARATOR);
		}
		w.write("if(!singleLine) w.write(\"\\n\");" + Constants.LINE_SEPARATOR);
		List records = new ArrayList(r.getChildren(CoreTags.NODE_TYPE));
		if (report && rl != ALL) {
			i = records.iterator();
			while (i.hasNext()) {
				if (reportLevel(((Element) i.next())
					.getAttributeValue("report"))
					== NONE) {
					i.remove();
				}
			}
		}
		i = records.iterator();
		while (i.hasNext()) {
			Element n = (Element) i.next();
			String name = n.getAttributeValue("name");
			w.write(
				"put_"
					+ n.getAttributeValue("className")
					+ "(ws, r."
					+ name
					+ ");"
					+ Constants.LINE_SEPARATOR);
		}
		w.write("}" + Constants.LINE_SEPARATOR);
		if (stacked) {
			w.write("}" + Constants.LINE_SEPARATOR);
		}
		i = records.iterator();
		while (i.hasNext()) {
			Element nr = (Element) i.next();
			if (rl != ALL) {
				rl = reportLevel(nr.getAttributeValue("report"));
			}
			createRecordOutputters(g, w, nr, rl, keyFlds, level + 1);
		}
	}
	/**
	 * Method getLineLength.
	 * @param fields
	 * @return int
	 */
	private int getLineLength(List fields) {
		int len = Integer.MIN_VALUE;
		for (Iterator iFields = fields.iterator(); iFields.hasNext();) {
			Element field = (Element) iFields.next();
			if (field != null) {
				Element ffFld = field.getChild("flatfileField");
				if (ffFld != null) {
					String start = ffFld.getAttributeValue(FlatFileTags.START);
					if (start != null) {
						String width = ffFld.getAttributeValue("width");
						int w;
						if (width == null) {
							w =
								FlatFileGenerator.defaultWidth(
									field.getAttributeValue(CoreTags.TYPE));
						} else {
							w = Integer.parseInt(width);
						}
						len = Math.max(len, Integer.parseInt(start) + w);
					} else {
						break;
					}
				} else {
					break;
				}
			}
		}
		return len;
	}

	private static class KeyFld {
		String fieldName;
		int start;
		String width;
		int level;

		KeyFld(String fieldName, int start, String width, int level) {
			this.fieldName = fieldName;
			this.width = width;
			this.level = level;
		}
	}
}
