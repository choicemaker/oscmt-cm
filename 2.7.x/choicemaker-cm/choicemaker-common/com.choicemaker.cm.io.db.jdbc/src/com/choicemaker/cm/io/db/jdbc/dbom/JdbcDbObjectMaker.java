/*
 * Created on Feb 15, 2005
 *
 */
package com.choicemaker.cm.io.db.jdbc.dbom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.PMManager;
import com.choicemaker.cm.core.util.ObjectMaker;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbField;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.base.DbView;

/**
 * @author pcheung
 *
 */
public class JdbcDbObjectMaker implements ObjectMaker /* IPlatformRunnable */ {


	public static void processAllModels(Writer w, boolean insertGo) throws IOException {
		IProbabilityModel[] models = PMManager.getModels();
		for (int i=0; i<models.length; i++) {
			createObjects(w, models[i], insertGo);
		}
	}


	public void generateObjects(File outDir) throws IOException {
		File outFile = new File(outDir, "Jdbc_Custom_Objects.txt");
		Writer w = new FileWriter(outFile);
		Set alreadyHandledRetrieval = new HashSet();
		Set alreadyHandledBlocking = new HashSet();
		IProbabilityModel[] models = PMManager.getModels();
		for (int i=0; i<models.length; i++) {
			IProbabilityModel model = models[i];
			String key = model.getAccessor().getSchemaName() + "|" + model.properties().get("dbConfiguration");
			if (alreadyHandledRetrieval.add(key)) {
				createObjects(w, model, false);
				
				System.out.println ("key: " + key);
			}

		}
		w.close();
	}


	public static String[] getAllModels() throws IOException {
		StringWriter w = new StringWriter();
		processAllModels(w, false);
		StringTokenizer st = new StringTokenizer(w.toString(), Constants.LINE_SEPARATOR);
		String[] res = new String[st.countTokens()];
		for (int i = 0; i < res.length; i++) {
			res[i] = st.nextToken();
		}
		return res;
	}


	public static void createObjects(Writer w, ImmutableProbabilityModel model, boolean insertGo) throws IOException {
		String dbConfiguration = (String) model.properties().get("dbConfiguration");
		if (dbConfiguration != null) {
			Accessor accessor = model.getAccessor();
			if (accessor instanceof DbAccessor) {
				String viewBase = "vw_cmt_" + accessor.getSchemaName() + "_r_" + dbConfiguration;
				String dbConf = accessor.getSchemaName() + ":r:" + dbConfiguration;
				// 		w.write("DELETE FROM TB_CMT_CURSORS WHERE config = '" +
				// 			dbConf + "'" + Constants.LINE_SEPARATOR);
				// 		w.write("Go" + Constants.LINE_SEPARATOR);
				DbReaderSequential dbr = ((DbAccessor) accessor).getDbReaderSequential(dbConfiguration);
				DbView[] views = dbr.getViews();
				String masterId = dbr.getMasterId();
				StringBuffer multi = new StringBuffer(4000);
				
				for (int i = 0; i < views.length; ++i) {
					String viewName = viewBase + i;
					
					DbView v = views[i];
					boolean first = i == 0 || v.number != views[i - 1].number;
					boolean more = i + 1 < views.length && v.number == views[i + 1].number;
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
					multi.append("SELECT * FROM " + viewName + " WHERE " + masterId + " IN " + "(SELECT ID FROM session.ids)");
					if (!more) {
						if (!first) {
							multi.append(")");
						}
						if (v.orderBy.length > 0) {
							multi.append(" ORDER BY ");
							multi.append(getOrderBy(v));
							multi.append(";");
						}
					}
					w.write(
						"DROP VIEW " + viewName + ";" 
						+ Constants.LINE_SEPARATOR
					);

					w.write("CREATE VIEW " + viewName + " AS SELECT ");
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
				String multiStr = multi.toString();
				//w.write("INSERT INTO TB_CMT_CURSORS VALUES('" + dbConf + "','" + multiStr + "');" + Constants.LINE_SEPARATOR);
				model.properties().put(dbConf + ":JDBC", multiStr);
			}
		}
	}



	public static String getMultiQuery(ImmutableProbabilityModel model, String dbConfiguration) {
		Accessor accessor = model.getAccessor();
		DbReaderSequential dbr = ((DbAccessor)accessor).getDbReaderSequential(dbConfiguration);
		String viewBase = "vw_cmt_" + accessor.getSchemaName() + "_r_" + dbConfiguration;
		DbView[] views = dbr.getViews();
		String masterId = dbr.getMasterId();
		StringBuffer multi = new StringBuffer(4000);
		for (int i = 0; i < views.length; ++i) {
			String viewName = viewBase + i;
			DbView v = views[i];
			boolean first = i == 0 || v.number != views[i - 1].number;
			boolean more = i + 1 < views.length && v.number == views[i + 1].number;
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
			multi.append("SELECT * FROM " + viewName + " WHERE " + masterId + " IN (SELECT ID FROM session.ids)");
			if (!more) {
				if (!first) {
					multi.append(")");
				}
				if (v.orderBy.length > 0) {
					multi.append(" ORDER BY ");
					multi.append(getOrderBy(v));
					multi.append(";");
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


	// FIXME remove these methods or move them to a test or application class
	// that isn't part of this plugin, so that this plugin (which is used by
	// CM Server) doesn't require the compiler plugin
//	public Object run(Object args) throws Exception {
//		CommandLineArguments cla = new CommandLineArguments();
//		cla.addExtensions();
//		cla.addArgument("-output");
//		cla.enter((String[])args);
//		main(new String[] {cla.getArgument("-conf"), cla.getArgument("-log"), cla.getArgument("-output")});
//		return null;
//	}
//
//
//	public static void main(String[] args) throws Exception {
//		XmlConfigurator.init(args[0], args[1], false, false);
//
//		CompilerFactory factory = CompilerFactory.getInstance ();
//		ICompiler compiler = factory.getDefaultCompiler();
//
//		ProbabilityModelsXmlConf.loadProductionProbabilityModels(compiler);
//		JdbcDbObjectMaker dom = new JdbcDbObjectMaker ();
//		dom.generateObjects (new File (args[2]));
//	}


}
