package com.choicemaker.cm.matching.en.us;

public class InputAndExpectedTestData {
	
	private static final InputAndExpected[] _testData = new InputAndExpected[] {
		new InputAndExpected("", "", "", new ParsedName0())
	};
	
	public static InputAndExpected[] getTestData() {
		final int length = _testData.length;
		InputAndExpected[] retVal = new InputAndExpected[length];
		System.arraycopy(_testData, 0, retVal, 0, length);
		return retVal;
	}

	private InputAndExpectedTestData() {
	}

}
