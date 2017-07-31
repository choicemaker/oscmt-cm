package com.choicemaker.util.dates;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DateUtils2Test {

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

	@Test
	public void testYearsApart() {
		
		final Date baseDate = DateUtils2.getDate(1971, 1, 26);
		Date testDate;
		int expected;
		int computed;
		
		testDate = DateUtils2.getDate(1971, 1, 27);
		computed = (int) DateUtils2.yearsApart(testDate,baseDate);
		expected = 0;
		assertTrue(expected == computed);
		
		testDate = DateUtils2.getDate(1972, 1, 27);
		computed = (int) DateUtils2.yearsApart(testDate,baseDate);
		expected = 1;
		assertTrue(expected == computed);
		
		testDate = DateUtils2.getDate(1970, 1, 27);
		computed = (int) DateUtils2.yearsApart(testDate,baseDate);
		expected = 0;
		assertTrue(expected == computed);
		
		testDate = DateUtils2.getDate(1970, 1, 25);
		computed = (int) DateUtils2.yearsApart(testDate,baseDate);
		expected = 1;
		assertTrue(expected == computed);
		
	}

}
