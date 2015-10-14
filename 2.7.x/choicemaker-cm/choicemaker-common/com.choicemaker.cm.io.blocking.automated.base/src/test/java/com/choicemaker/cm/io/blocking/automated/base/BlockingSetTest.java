package com.choicemaker.cm.io.blocking.automated.base;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.choicemaker.cm.io.blocking.automated.IBlockingField;
import com.choicemaker.cm.io.blocking.automated.IBlockingValue;
import com.choicemaker.cm.io.blocking.automated.IDbTable;

public class BlockingSetTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public IBlockingValue[] createSufficientBlockingValues() {

		List<IBlockingValue> bvList = new ArrayList<>();

		// Common values
		final QueryField queryField = new QueryField();
		final String dbtName = "master";
		final int dbtNumber = 0;
		final String uniqueDbtId = "PATIENTID";
		final IDbTable dbfTable = new DbTable(dbtName, dbtNumber, uniqueDbtId);
		final int defaultCount = 29;
		final IBlockingValue[][] base =
				new IBlockingValue[][] { new IBlockingValue[] {} };
		final int tableSize = 386576;

		int dbfNumber = 2;
		String dbfName = "GIVNAMES";
		String dbfType = "String";
		DbField dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		String group = null;
		int bfNumber = 2;
		IBlockingField bf =
			new BlockingField(bfNumber, queryField, dbField, group);
		String value = "alistair james";
		BlockingValue bv = new BlockingValue(bf, value, base);
		bv.setTableSize(tableSize);
		bv.setCount(29);
		bvList.add(bv);

		dbfNumber = 9;
		dbfName = "cmtSoundexBareLocality";
		dbfType = "String";
		dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		group = null;
		bfNumber = 9;
		bf = new BlockingField(bfNumber, queryField, dbField, group);
		value = "Q521";
		bv = new BlockingValue(bf, value, base);
		bv.setCount(29);
		bv.setTableSize(tableSize);
		bvList.add(bv);

		dbfNumber = 4;
		dbfName = "DOB";
		dbfType = "String";
		dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		group = null;
		bfNumber = 4;
		bf = new BlockingField(bfNumber, queryField, dbField, group);
		value = "19570720";
		bv = new BlockingValue(bf, value, base);
		bv.setCount(77);
		bv.setTableSize(tableSize);
		bvList.add(bv);

		IBlockingValue[] retVal = (IBlockingValue[]) bvList.toArray();
		return retVal;
	}

	@Test
	public void testAddSufficientBlockingValues() {

		QueryField queryField = new QueryField();
		String dbtName = null;
		int dbtNumber = 0;
		String uniqueDbtId = null;
		IDbTable dbfTable = new DbTable(dbtName, dbtNumber, uniqueDbtId);
		int dbfNumber = 9;
		String dbfName = null;
		String dbfType = null;
		int defaultCount = 29;
		DbField dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		String group = null;
		int bfNumber = 0;
		IBlockingField bf =
			new BlockingField(bfNumber, queryField, dbField, group);
		String value = null;
		IBlockingValue[][] base = null;
		IBlockingValue ibv1 = new BlockingValue(bf, value, base);

	}

	@Test
	public void testAddInsufficientBlockingValues() {
		// fail("Not yet implemented");
	}

}
