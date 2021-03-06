/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.cm.io.db.postgres2.dbom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

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
import com.choicemaker.cm.io.db.base.DbReader;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.base.DbView;
import com.choicemaker.e2.CMPlatformRunnable;
import com.choicemaker.util.Precondition;

/**
 * Writes a Sql Server script (Postgres_Custom_Objects.txt) that creates DB
 * objects (views) based on the schemas of models specified in the
 * productionModels section of a CM Analyzer configuration file.
 */
public class PostgresDbObjectMaker implements CMPlatformRunnable, ObjectMaker {

	private static final Logger logger =
		Logger.getLogger(PostgresDbObjectMaker.class.getName());

	public static final String VIEW_NAME_TEMPLATE = "vw_cmt_%s_r_%s";

	public static final String MULTIKEY_TEMPLATE = "%s:r:%s:Postgres";

	@Override
	public Object run(Object args) throws Exception {
		CommandLineArguments cla = new CommandLineArguments();
		cla.addExtensions();
		cla.addArgument("-output");
		cla.enter((String[]) args);
		main(new String[] {
				cla.getArgument("-conf"), cla.getArgument("-log"),
				cla.getArgument("-output") });
		return null;
	}

	public static void main(String[] args) throws Exception {
		XmlConfigurator.getInstance().init(args[0], args[1], false, false);

		// TODO FIXME replace default compiler with configurable compiler
		CompilerFactory factory = CompilerFactory.getInstance();
		ICompiler compiler = factory.getDefaultCompiler();

		final boolean fromResource = false;
		ProbabilityModelsXmlConf.loadProductionProbabilityModels(compiler,
				fromResource);
		Properties unused = new Properties();
		Writer w = new FileWriter(args[2]);
		processAllModels(unused, w, true);
		w.close();
	}

	@Override
	public void generateObjects(File outDir) throws IOException {
		File outFile =
			new File(outDir, "Postgres_Custom_Objects.txt").getAbsoluteFile();
		Writer w = new FileWriter(outFile);
		Properties unused = new Properties();
		processAllModels(unused, w, true);
		w.close();
	}

	public static String[] getAllModels(final Properties p) throws IOException {
		StringWriter w = new StringWriter();
		processAllModels(p, w, false);
		StringTokenizer st =
			new StringTokenizer(w.toString(), Constants.LINE_SEPARATOR);
		String[] res = new String[st.countTokens()];
		for (int i = 0; i < res.length; i++) {
			res[i] = st.nextToken() + ";";
		}
		return res;
	}

	public static Properties processAllModels(final Properties p,
			final Writer w, final boolean insertGo) throws IOException {
		ImmutableProbabilityModel[] models = PMManager.getModels();

		// This sort makes the output repeatable, independent of plugin order
		ModelUtils.sort(models);

		// Create just one view definition for each unique combination
		// of schema name and database configuration
		Set<String> uniqueKeys = new LinkedHashSet<>();

		for (int i = 0; i < models.length; i++) {
			final ImmutableProbabilityModel model = models[i];
			final DbAccessor dbAccessor = (DbAccessor) model.getAccessor();
			final String schemaName = model.getAccessor().getSchemaName();
			String[] dbcNames = dbAccessor.getDbConfigurations();
			Arrays.sort(dbcNames);
			for (int j = 0; j < dbcNames.length; j++) {
				String dbcName = dbcNames[j];
				String multiKey = createMultiKey(schemaName, dbcName);
				boolean isUnique = uniqueKeys.add(multiKey);
				if (isUnique) {
					logger.fine("defining DB objects for " + multiKey);
					createObjects(p, w, model, dbcName, insertGo);
				} else {
					logger.fine("DB objects already defined for " + multiKey);
				}
			}
		}
		return p;
	}

	public static String createViewBaseName(String schemaName,
			String databaseConfigurationName) {
		Precondition.assertNonEmptyString(schemaName);
		Precondition.assertNonEmptyString(databaseConfigurationName);

		String retVal = String.format(VIEW_NAME_TEMPLATE, schemaName,
				databaseConfigurationName);
		return retVal;
	}

	/**
	 * Updates and returns the specified properties <code>p</code> with SQL DDL
	 * statements that create the views used by the Postgres database accessor.
	 *
	 * @param p
	 *            a non-null Properties instance, which is update and returned.
	 * @param w
	 *            an output writer to which SQL DDL statements are also written.
	 * @param model
	 *            an non-null probability model
	 * @param dbConfiguration
	 *            the name of a database configuration specified by the model
	 *            record-layout schema
	 * @param insertGo
	 *            a flag indicating with Postgres <code>GO</code> directives
	 *            should be written to the output writer
	 * @return a modified copy of the input properties
	 * @throws IOException
	 */
	public static Properties createObjects(final Properties p, final Writer w,
			final ImmutableProbabilityModel model, final String dbConfiguration,
			final boolean insertGo) throws IOException {
		if (p == null || w == null || model == null) {
			String msg = "null argument";
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}
		if (dbConfiguration == null || dbConfiguration.trim().isEmpty()) {
			logger.warning("null or blank database configuration");
		} else {
			Accessor accessor = model.getAccessor();
			if (accessor instanceof DbAccessor) {
				String viewBase = createViewBaseName(accessor.getSchemaName(),
						dbConfiguration);
				DbReaderSequential dbr = ((DbAccessor) accessor)
						.getDbReaderSequential(dbConfiguration);
				DbView[] views = dbr.getViews();
				String masterId = dbr.getMasterId();
				StringBuffer multi = new StringBuffer(4000);
				for (int i = 0; i < views.length; ++i) {
					String viewName = viewBase + i;
					DbView v = views[i];
					boolean first = i == 0 || v.number != views[i - 1].number;
					boolean more =
						i + 1 < views.length && v.number == views[i + 1].number;
					if (first) {
						if (i != 0) {
							multi.append(Constants.LINE_SEPARATOR);
						}
						if (more) {
							multi.append("SELECT * FROM (");
						}
					} else {
						multi.append(" UNION ");
					}
					multi.append("SELECT * FROM " + viewName + " WHERE "
							+ masterId + " IN " + "(SELECT ID FROM ids)");
					if (!more) {
						if (!first) {
							multi.append(") AS A");
						}
						if (v.orderBy.length > 0) {
							multi.append(" ORDER BY ");
							multi.append(getOrderBy(v));
						}
						multi.append(";");
					}
					w.write("CREATE OR REPLACE VIEW <CHANGEME_SCHEMA>."
							+ viewName + " AS SELECT ");
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
					w.write(Constants.LINE_SEPARATOR + (insertGo
							? "Go" + Constants.LINE_SEPARATOR : ""));
				}
				String multiKey =
					createMultiKey(accessor.getSchemaName(), dbConfiguration);
				String multiStrValue = multi.toString();
				p.setProperty(multiKey, multiStrValue);
			}
		}
		return p;
	}

	public static String getMultiKey(ImmutableProbabilityModel model,
			String dbConfiguration) {
		String retVal = createMultiKey(model.getAccessor().getSchemaName(),
				dbConfiguration);
		return retVal;
	}

	public static String getMultiKey(DbReader dbReader) {
		return dbReader.getName() + ":Postgres";
	}

	public static String createMultiKey(String schemaName,
			String databaseConfigurationName) {
		Precondition.assertNonEmptyString(schemaName);
		Precondition.assertNonEmptyString(databaseConfigurationName);

		String retVal = String.format(MULTIKEY_TEMPLATE, schemaName,
				databaseConfigurationName);
		return retVal;
	}

	public static String getMultiQuery(ImmutableProbabilityModel model,
			String dbConfiguration) {
		Accessor accessor = model.getAccessor();
		DbReaderSequential dbr =
			((DbAccessor) accessor).getDbReaderSequential(dbConfiguration);
		String viewBase =
			createViewBaseName(accessor.getSchemaName(), dbConfiguration);
		DbView[] views = dbr.getViews();
		String masterId = dbr.getMasterId();
		StringBuffer multi = new StringBuffer(4000);
		for (int i = 0; i < views.length; ++i) {
			String viewName = viewBase + i;
			DbView v = views[i];
			boolean first = i == 0 || v.number != views[i - 1].number;
			boolean more =
				i + 1 < views.length && v.number == views[i + 1].number;
			if (first) {
				if (i != 0) {
					multi.append(Constants.LINE_SEPARATOR);
				}
				if (more) {
					multi.append("SELECT * FROM (");
				}
			} else {
				multi.append(" UNION ");
			}
			multi.append("SELECT * FROM " + viewName + " WHERE " + masterId
					+ " IN (SELECT ID FROM ids)");
			if (!more) {
				if (!first) {
					multi.append(") AS A");
				}
				if (v.orderBy.length > 0) {
					multi.append(" ORDER BY ");
					multi.append(getOrderBy(v));
				}
			}
		}
		return multi.toString();
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
