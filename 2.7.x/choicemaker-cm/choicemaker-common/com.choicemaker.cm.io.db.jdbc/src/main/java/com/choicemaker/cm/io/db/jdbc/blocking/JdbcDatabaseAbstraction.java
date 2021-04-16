/*
 * Created on Feb 15, 2005
 *
 */
package com.choicemaker.cm.io.db.jdbc.blocking;

import com.choicemaker.cm.aba.base.db.DatabaseAbstraction;

/**
 * @author pcheung
 *
 */
public class JdbcDatabaseAbstraction implements DatabaseAbstraction {

	public String getSetDateFormatExpression() {
//		return "bind @db2ubind.lst datetime ISO blocking all grant public";

		//this is completely not needed since DB by default uses yyyy-mm-dd as input.
		//we just need some valid SQL here.
		return "SELECT current date FROM sysibm.sysdummy1";
	}

	public String getSysdateExpression() {
		return "SYSDATE ()";
	}

	public String getDateFieldExpression(String field) {
		return field;
	}

}
