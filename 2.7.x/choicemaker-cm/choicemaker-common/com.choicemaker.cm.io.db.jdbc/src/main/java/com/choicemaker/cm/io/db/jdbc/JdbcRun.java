/*******************************************************************************
 * Copyright (c) 2003, 2017 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
/*
 * Created on Feb 10, 2005
 *
 */
package com.choicemaker.cm.io.db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//import com.jdbc.jdbc.Driver;

/**
 * Connection tester object. 
 * 
 * @author pcheung
 *
 */
public class JdbcRun {

	public JdbcRun () {
	}
	
	public static void test () {
		
		try {
			Class.forName("com.jdbc.jdbc.Driver");
			
			//type 2 client.  make sure my_es_db is already cataloged.			
			Connection conn = DriverManager.getConnection
				("jdbc:jdbc://noho.choicemaker.com/es?user=root&password=CHANGEME");
			
			PreparedStatement stmt = conn.prepareStatement( "select count(*) from sid_master" );
			
			ResultSet rs = stmt.executeQuery ();
			
			while (rs.next()) {
				System.out.println (rs.getInt(1));
			}

			rs.close();
			stmt.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	public static void test2 () {
		try {
			Class.forName("com.jdbc.jdbc.Driver");
			
			//type 2 client.  make sure my_es_db is already cataloged.			
			Connection conn = DriverManager.getConnection
				("jdbc:jdbc://noho.choicemaker.com/es?user=root&password=CHANGEME");
			
			PreparedStatement stmt = conn.prepareStatement
				("select state_student_id, cmt_student_fn_upper from cmt_master where state_student_id = '2'");
			
			ResultSet rs = stmt.executeQuery ();
			
			while (rs.next()) {
				System.out.println ("!" + rs.getString(1) + "!");
				System.out.println ("!" + rs.getString(2) + "!");
			}

			rs.close();
			stmt.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	public static void main (String [] args) {
		test2 ();
	}


}
