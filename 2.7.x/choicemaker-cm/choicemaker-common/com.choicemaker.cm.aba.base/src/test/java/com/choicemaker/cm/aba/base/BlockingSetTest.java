package com.choicemaker.cm.aba.base;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.choicemaker.cm.aba.IBlockingField;
import com.choicemaker.cm.aba.IBlockingValue;
import com.choicemaker.cm.aba.IDbTable;

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
		final String group = null;
		final IBlockingValue[][] base = new IBlockingValue[][] {
				new IBlockingValue[] {} };
		final int tableSize = 386576;

		int dbfNumber = 2;
		String dbfName = "GIVNAMES";
		String dbfType = "String";
		DbField dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
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
		bfNumber = 4;
		bf = new BlockingField(bfNumber, queryField, dbField, group);
		value = "19570720";
		bv = new BlockingValue(bf, value, base);
		bv.setCount(77);
		bv.setTableSize(tableSize);
		bvList.add(bv);

		dbfNumber = 0;
		dbfName = "SURNAME";
		dbfType = "String";
		dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		bfNumber = 0;
		bf = new BlockingField(bfNumber, queryField, dbField, group);
		value = "tolmie";
		bv = new BlockingValue(bf, value, base);
		bv.setCount(101);
		bv.setTableSize(tableSize);
		bvList.add(bv);

		dbfNumber = 6;
		dbfName = "PCODE";
		dbfType = "String";
		dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		bfNumber = 6;
		bf = new BlockingField(bfNumber, queryField, dbField, group);
		value = "2022";
		bv = new BlockingValue(bf, value, base);
		bv.setCount(4229);
		bv.setTableSize(tableSize);
		bvList.add(bv);

		IBlockingValue[] retVal = (IBlockingValue[]) bvList.toArray();
		return retVal;
	}

	public IBlockingValue[] createInsufficientBlockingValues() {

		List<IBlockingValue> bvList = new ArrayList<>();

		// Common values
		final QueryField queryField = new QueryField();
		final String dbtName = "master";
		final int dbtNumber = 0;
		final String uniqueDbtId = "PATIENTID";
		final IDbTable dbfTable = new DbTable(dbtName, dbtNumber, uniqueDbtId);
		final int defaultCount = 29;
		final String group = null;
		final IBlockingValue[][] base = new IBlockingValue[][] {
				new IBlockingValue[] {} };
		final int tableSize = 386576;

		int dbfNumber = 2;
		String dbfName = "GIVNAMES";
		String dbfType = "String";
		DbField dbField =
			new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
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
		bfNumber = 9;
		bf = new BlockingField(bfNumber, queryField, dbField, group);
		value = "Q521";
		bv = new BlockingValue(bf, value, base);
		bv.setCount(29);
		bv.setTableSize(tableSize);
		bvList.add(bv);

		// dbfNumber = 4;
		// dbfName = "DOB";
		// dbfType = "String";
		// dbField =
		// new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		// bfNumber = 4;
		// bf = new BlockingField(bfNumber, queryField, dbField, group);
		// value = "19570720";
		// bv = new BlockingValue(bf, value, base);
		// bv.setCount(77);
		// bv.setTableSize(tableSize);
		// bvList.add(bv);
		//
		// dbfNumber = 0;
		// dbfName = "SURNAME";
		// dbfType = "String";
		// dbField =
		// new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		// bfNumber = 0;
		// bf = new BlockingField(bfNumber, queryField, dbField, group);
		// value = "tolmie";
		// bv = new BlockingValue(bf, value, base);
		// bv.setCount(101);
		// bv.setTableSize(tableSize);
		// bvList.add(bv);
		//
		// dbfNumber = 6;
		// dbfName = "PCODE";
		// dbfType = "String";
		// dbField =
		// new DbField(dbfNumber, dbfName, dbfType, dbfTable, defaultCount);
		// bfNumber = 6;
		// bf = new BlockingField(bfNumber, queryField, dbField, group);
		// value = "2022";
		// bv = new BlockingValue(bf, value, base);
		// bv.setCount(4229);
		// bv.setTableSize(tableSize);
		// bvList.add(bv);

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
		@SuppressWarnings("unused")
		IBlockingValue ibv1 = new BlockingValue(bf, value, base);

	}

	@Test
	public void testAddInsufficientBlockingValues() {
		// fail("Not yet implemented");
	}

}
