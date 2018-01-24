/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle.dbom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.choicemaker.cm.aba.BlockingAccessor;
import com.choicemaker.cm.compiler.impl.CompilerFactory;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.compiler.ICompiler;
import com.choicemaker.cm.core.util.CommandLineArguments;
import com.choicemaker.cm.core.util.ModelUtils;
import com.choicemaker.cm.core.util.ObjectMaker;
import com.choicemaker.cm.core.xmlconf.ProbabilityModelsXmlConf;
import com.choicemaker.cm.core.xmlconf.XmlConfigurator;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbField;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.io.db.base.DbView;
import com.choicemaker.e2.CMPlatformRunnable;

public class DbDbObjectMaker implements CMPlatformRunnable, ObjectMaker {
	private static final int SINGLE = 0;
	private static final int SINGLE_UNION = 1;
	private static final int MULTI = 2;

	public Object run(Object args) throws Exception {
		CommandLineArguments cla = new CommandLineArguments();
		cla.addExtensions();
		cla.addArgument("-output");
		cla.enter((String[]) args);
		main(new String[] { cla.getArgument("-conf"), cla.getArgument("-log"), cla.getArgument("-output")});
		return null;
	}

	public static void main(String[] args) throws Exception {
		XmlConfigurator.getInstance().init(args[0], args[1], false, false);

		// TODO FIXME replace default compiler with configurable compiler
		CompilerFactory factory = CompilerFactory.getInstance ();
		ICompiler compiler = factory.getDefaultCompiler();

		final boolean fromResource = false;
		ProbabilityModelsXmlConf.loadProductionProbabilityModels(compiler,
				fromResource);
		DbDbObjectMaker dbom = new DbDbObjectMaker();
		dbom.generateObjects(new File(args[2]));
	}

	public void generateObjects(File outDir) throws IOException {
		File outFile = new File(outDir, "Oracle_Custom_Objects.txt");
		Writer w = new FileWriter(outFile);
		Set alreadyHandledRetrieval = new HashSet();
		Set alreadyHandledBlocking = new HashSet();
		ImmutableProbabilityModel[] iModels = PMManager.getModels();

		// This sort makes the output repeatable, independent of plugin order
		ModelUtils.sort(iModels);

		for (int i = 0; i < iModels.length; i++) {
			ImmutableProbabilityModel model = iModels[i];
			final DbAccessor dbAccessor = (DbAccessor) model.getAccessor();
			String[] dbcNames = dbAccessor.getDbConfigurations();
			Arrays.sort(dbcNames);
			for (int k = 0; k < dbcNames.length; k++) {
				final String dcName = dbcNames[k];
				String key = model.getAccessor().getSchemaName() + "|" + dcName;
				if (alreadyHandledRetrieval.add(key)) {
					createRetrievalObjects(model, dcName, w);
				}
				BlockingAccessor bAccessor =
					(BlockingAccessor) model.getAccessor();
				String[] bcNames = bAccessor.getBlockingConfigurations();
				Arrays.sort(bcNames);
				for (int j = 0; j < bcNames.length; j++) {
					final String bcName = bcNames[j];
					key =
						model.getAccessor().getSchemaName() + "|" + bcName
								+ "|" + dcName;
					if (alreadyHandledBlocking.add(key)) {
						createBlockingObjects(model, dcName, bcName, w);
					}
				}
			}
		}
		w.close();
	}

	private static void createBlockingObjects(ImmutableProbabilityModel model,
			String dbConfiguration, String blockingConfiguration ,Writer w) throws IOException {
		Accessor accessor = model.getAccessor();
		if (dbConfiguration != null && blockingConfiguration != null && accessor instanceof DbAccessor) {
			String config = accessor.getSchemaName() + ":b:" + blockingConfiguration + ":" + dbConfiguration;
			w.write(
				"DELETE FROM tb_cmt_config WHERE Config='"
					+ config
					+ "' AND Name='MASTERID';"
					+ Constants.LINE_SEPARATOR);
			String masterId = ((DbAccessor)accessor).getDbReaderParallel(dbConfiguration).getMasterId();
			w.write(
				"INSERT INTO tb_cmt_config (Config, Name, Value) VALUES('" + config + "','MASTERID','" +
					masterId + "');" + Constants.LINE_SEPARATOR);
		}
	}

	private static void createRetrievalObjects(ImmutableProbabilityModel model,
			String dbConfiguration, Writer w) throws IOException {
		if (dbConfiguration != null) {
			Accessor accessor = model.getAccessor();
			if (accessor instanceof DbAccessor) {
				String viewBase = "vw_cmt_" + accessor.getSchemaName() + "_r_" + dbConfiguration;
				String dbConf = accessor.getSchemaName() + ":r:" + dbConfiguration;
				w.write("DELETE FROM TB_CMT_CURSORS WHERE config = '" + dbConf + "';" + Constants.LINE_SEPARATOR);
				w.write("DELETE FROM TB_CMT_CONFIG WHERE config = '" + dbConf + "';" + Constants.LINE_SEPARATOR);
				DbReaderParallel dbr = ((DbAccessor) accessor).getDbReaderParallel(dbConfiguration);
				DbView[] views = dbr.getViews();
				String masterId = dbr.getMasterId();
				w.write(
					"INSERT INTO TB_CMT_CONFIG VALUES('"
						+ dbConf
						+ "', 'MASTERID', '"
						+ masterId
						+ "');"
						+ Constants.LINE_SEPARATOR);
				StringBuffer multi = new StringBuffer(4000);
				String single = null;
				String orderby = null;
				int approach;
				if (views.length == 1) {
					approach = SINGLE;
					DbView v = views[0];
					single = "'SELECT * FROM " + viewBase + "0 WHERE " + masterId + " IN '";
					orderby = "'" + getOrderBy(v) + "'";
				} else if (views[views.length - 1].number == 0) {
					approach = SINGLE_UNION;
				} else {
					approach = MULTI;
					multi.append("SELECT");
				}
				for (int i = 0; i < views.length; ++i) {
					String viewName = viewBase + i;
					DbView v = views[i];
					boolean first = i == 0 || v.number != views[i - 1].number;
					boolean more = i + 1 < views.length && v.number == views[i + 1].number;
					if (first) {
						if (i != 0) {
							multi.append(",");
						}
						if (approach == MULTI) {
							multi.append(" CURSOR(");
						}
						if (more) {
							multi.append("SELECT * FROM(");
						}
					} else {
						multi.append(" UNION ");
					}
					multi.append(
						"SELECT /*+ RULE */ * FROM "
							+ viewName
							+ " WHERE "
							+ masterId
							+ " IN "
							+ "(SELECT ID FROM tb_cmt_temp_ids)");
					if (!more) {
						if (!first) {
							multi.append(")");
						}
						if (v.orderBy.length > 0) {
							multi.append(" ORDER BY ");
							multi.append(getOrderBy(v));
						}
						if (approach == MULTI) {
							multi.append(")");
						}
					}
					w.write("CREATE OR REPLACE VIEW " + viewName + " AS SELECT ");
					for (int j = 0; j < v.fields.length; ++j) {
						DbField f = v.fields[j];
						if (j != 0)
							w.write(",");
						if (f.table != null)
							w.write(f.table + ".");
						w.write(f.name);
					}
					w.write(" FROM " + v.from);
					if (v.where != null)
						w.write(" WHERE " + v.where);
					w.write(";" + Constants.LINE_SEPARATOR);
				}
				if (approach == MULTI) {
					multi.append(" FROM DUAL");
				}
				w.write(
					"INSERT INTO TB_CMT_CURSORS VALUES('"
						+ dbConf
						+ "','"
						+ multi
						+ "', "
						+ single
						+ ","
						+ orderby
						+ ",'"
						+ (approach == SINGLE ? "D" : "T")
						+ "',"
						+ views.length
						+ ");"
						+ Constants.LINE_SEPARATOR);
			}
		}
	}

	private static String getOrderBy(DbView v) {
		StringBuffer ob = new StringBuffer();
		for (int j = 0; j < v.orderBy.length; ++j) {
			if (j != 0)
				ob.append(",");
			ob.append(v.orderBy[j].name);
		}
		return ob.toString();
	}
}
