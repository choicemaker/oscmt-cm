package com.choicemaker.cm.io.flatfile.base;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class TokenizerTest {

	// @BeforeClass
	// public static void setUpBeforeClass() throws Exception {
	// }
	//
	// @AfterClass
	// public static void tearDownAfterClass() throws Exception {
	// }

	public static BufferedReader createBufferedReader(String line) {
		if (line == null) {
			line = "";
		}
		StringReader sr = new StringReader(line);
		BufferedReader retVal = new BufferedReader(sr);
		return retVal;
	}

	// @Before
	// public void setUp() throws Exception {
	// }
	//
	// @After
	// public void tearDown() throws Exception {
	// }

	@Test
	public void testIsLineAvailable() throws IOException {
		String line;
		char separator;
		BufferedReader br;
		Tokenizer t;

		line = null;
		br = createBufferedReader(line);
		t = new Tokenizer(br);
		t.readLine();
		assertFalse(t.isLineAvailable());
		assertFalse(t.lineRead());

		line = "|x|y|z|";
		br = createBufferedReader(line);
		t = new Tokenizer(br);
		t.readLine();
		assertTrue(t.isLineAvailable());
		assertTrue(t.lineRead());

		br = createBufferedReader(line);
		separator = '|';
		t = new Tokenizer(br, separator);
		t.readLine();
		assertTrue(t.isLineAvailable());
		assertTrue(t.lineRead());
	}

	@Test
	public void testSkip() throws IOException {
		String line;
		char separator;
		BufferedReader br;
		Tokenizer t;

		line = null;
		br = createBufferedReader(line);
		t = new Tokenizer(br);
		t.readLine();
		assertTrue(t.pos == 0);
		t.skip(1);
		assertTrue(t.pos == 0);

		line = "|x|y|z|";
		br = createBufferedReader(line);
		t = new Tokenizer(br);
		t.readLine();
		assertTrue(t.pos == 0);
		t.skip(1);
		assertTrue(t.pos == 0);

		br = createBufferedReader(line);
		separator = '|';
		t = new Tokenizer(br, separator);
		t.readLine();
		assertTrue(t.pos == 0);
		t.skip(1);
		assertTrue(t.pos == 1);
		t.skip(2);
		assertTrue(t.pos == 5);
		t.skip(1);
		assertTrue(t.pos == 7);
		t.skip(1);
		assertTrue(t.pos == 7);
		t.skip(2);
		assertTrue(t.pos == 7);

		line = " x y|z|";
		br = createBufferedReader(line);
		separator = '|';
		t = new Tokenizer(br, separator);
		t.readLine();
		assertTrue(t.pos == 0);
		t.skip(1);
		assertTrue(t.pos == 5);
		t.skip(1);
		assertTrue(t.pos == 7);

		line = " x y z ";
		br = createBufferedReader(line);
		separator = '|';
		t = new Tokenizer(br, separator);
		t.readLine();
		assertTrue(t.pos == 0);
		t.skip(1);
		assertTrue(t.pos == 7);
		t.skip(1);
		assertTrue(t.pos == 7);
	}

	@Test
	public void testReadTaggedLine() throws IOException {
		String line;
		char separator;
		BufferedReader br;
		Tokenizer t;
		final boolean tagged = true;
		int tagWidth;
		String expectedTag;

		// Separator overrules tag
		line = "|x|y:z:";
		separator = '|';
		tagWidth = 0;
		expectedTag = "";
		br = createBufferedReader(line);
		t = new Tokenizer(br, separator, tagged, tagWidth);
		t.readLine();
		assertTrue(expectedTag.equals(t.tag));

		line = "|x|y:z:";
		separator = ':';
		tagWidth = 0;
		expectedTag = "|x|y";
		tagWidth = expectedTag.length();
		br = createBufferedReader(line);
		t = new Tokenizer(br, separator, tagged, tagWidth);
		t.readLine();
		assertTrue(expectedTag.equals(t.tag));

		line = "|x|y|z|";
		separator = ':';
		tagWidth = 0;
		expectedTag = line;
		br = createBufferedReader(line);
		t = new Tokenizer(br, separator, tagged, tagWidth);
		t.readLine();
		assertTrue(expectedTag.equals(t.tag));

		line = "|x|y|z|";
		separator = ':';
		tagWidth = line.length() + 1;
		expectedTag = line;
		br = createBufferedReader(line);
		t = new Tokenizer(br, separator, tagged, tagWidth);
		t.readLine();
		assertTrue(expectedTag.equals(t.tag));
	}

	@Test
	public void testReadTaggedLine2() throws IOException {
		String line;
		char separator;
		BufferedReader br;
		Tokenizer t;
		final boolean tagged = true;
		int tagWidth;
		
		line = "";
		separator = ':';
		tagWidth = 0;
		br = createBufferedReader(line);
		t = new Tokenizer(br, separator, tagged, tagWidth);
		t.readLine();
		assertTrue(t.tag == null);
		assertTrue(t.wasNull());

		line = "\n";
		separator = ':';
		tagWidth = 0;
		br = createBufferedReader(line);
		t = new Tokenizer(br, separator, tagged, tagWidth);
		try {
			t.readLine();
			fail("Did not catch untagged line");
		} catch (IllegalStateException x) {
			String msg = x.getMessage();
			assertTrue(msg != null);
		} catch (Exception x) {
			fail("unexpected exception: " + x.toString());
		}

	}

}
