/*
 * Created on Feb 10, 2005
 *
 */
package com.choicemaker.cm.io.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//import com.mysql.jdbc.Driver;

/**
 * Connection tester object. 
 * 
 * @author pcheung
 *
 */
public class MySQLRun {

	public MySQLRun () {
	}
	
	public static void test () {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			//type 2 client.  make sure my_es_db is already cataloged.			
			Connection conn = DriverManager.getConnection
				("jdbc:mysql://noho.choicemaker.com/es?user=root&password=CHANGEME");
			
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
			Class.forName("com.mysql.jdbc.Driver");
			
			//type 2 client.  make sure my_es_db is already cataloged.			
			Connection conn = DriverManager.getConnection
				("jdbc:mysql://noho.choicemaker.com/es?user=root&password=CHANGEME");
			
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
