/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import static com.choicemaker.cm.oaba.core.Constants.EXPORT_FIELD_SEPARATOR;
import static com.choicemaker.cm.oaba.core.Constants.LINE_SEPARATOR;
import static com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT.BINARY;
import static com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT.STRING;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

/**
 * This object writes MatchRecord objects to file.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({ "rawtypes" })
public class MatchRecord2Sink extends BaseFileSink implements IMatchRecord2Sink {

	/**
	 * This creates a new match record sink. By default it does not append to
	 * existing file, but overwrites it.
	 */
	@Deprecated
	public MatchRecord2Sink(String fileName, int type) {
		super(fileName, EXTERNAL_DATA_FORMAT.fromSymbol(type));
	}

	public MatchRecord2Sink(String fileName, EXTERNAL_DATA_FORMAT type) {
		super(fileName, type);
	}

	@Override
	public void writeMatches(List matches) throws BlockingException {
		int size = matches.size();
		for (int i = 0; i < size; i++) {
			MatchRecord2 match = (MatchRecord2) matches.get(i);
			writeMatch(match);
		}
	}

	@Override
	public void writeMatches(Collection c) throws BlockingException {
		Iterator it = c.iterator();
		writeMatches(it);
	}

	@Override
	public void writeMatches(Iterator it) throws BlockingException {
		while (it.hasNext()) {
			MatchRecord2 match = (MatchRecord2) it.next();
			writeMatch(match);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void writeMatch(MatchRecord2 match) throws BlockingException {
		try {
			if (type == STRING) {
				fw.write(getOutputString(match));
			} else {
				assert type == BINARY;
				RECORD_ID_TYPE dataType =
					RECORD_ID_TYPE.fromInstance(match.getRecordID1());
				final int dataTypeSymbol = dataType.getIntValue();
				dos.writeInt(dataTypeSymbol);
				writeID(match.getRecordID1(), dataType);

				RECORD_ID_TYPE dataType2 =
					RECORD_ID_TYPE.fromInstance(match.getRecordID2());
				if (dataType2 != dataType) {
					throw new IllegalArgumentException(
							"Inconsistent record identifiers");
				}
				dos.writeInt(dataTypeSymbol);
				writeID(match.getRecordID2(), dataType);

				dos.writeFloat(match.getProbability());
				dos.writeChar(match.getMatchType().toSingleChar());
				dos.writeChar(match.getRecord2Role().symbol);

				String str = match.getNotesAsDelimitedString();
				if (str == null || str.equals("")) {
					dos.writeInt(0);
				} else {
					dos.writeInt(str.length());
					dos.writeChars(str);
				}
			}
			count++;
		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	/**
	 * This method returns a string representation of MatchRecord2 to get
	 * written to a FileWriter.
	 */
	public static String getOutputString(MatchRecord2 match) {
		StringBuffer sb = new StringBuffer();
		@SuppressWarnings("unchecked")
		char dataType =
			RECORD_ID_TYPE.fromInstance(match.getRecordID1()).getCharSymbol();
		sb.append(dataType);
		sb.append(EXPORT_FIELD_SEPARATOR);
		sb.append(match.getRecordID1().toString());
		sb.append(EXPORT_FIELD_SEPARATOR);

		@SuppressWarnings("unchecked")
		char dataType2 =
			RECORD_ID_TYPE.fromInstance(match.getRecordID2()).getCharSymbol();
		if (dataType2 != dataType) {
			throw new IllegalArgumentException(
					"Inconsistent record identifiers");
		}
		sb.append(dataType);
		sb.append(EXPORT_FIELD_SEPARATOR);
		sb.append(match.getRecordID2().toString());
		sb.append(EXPORT_FIELD_SEPARATOR);

		String str = match.getNotesAsDelimitedString();
		if (str == null)
			str = "";
		else
			str = EXPORT_FIELD_SEPARATOR + str;

		sb.append(match.getProbability());
		sb.append(EXPORT_FIELD_SEPARATOR);
		sb.append(match.getMatchType().toSingleChar());
		sb.append(EXPORT_FIELD_SEPARATOR);
		sb.append(match.getRecord2Role().symbol);
		sb.append(str);
		sb.append(LINE_SEPARATOR);

		return sb.toString();
	}

	private <T extends Comparable<T>> void writeID(T c, RECORD_ID_TYPE dataType)
			throws IOException {
		if (type == STRING) {
			fw.write(c.toString() + EXPORT_FIELD_SEPARATOR);
		} else {
			assert type == BINARY;
			switch (dataType) {
			case TYPE_INTEGER:
				dos.writeInt(((Integer) c).intValue());
				break;
			case TYPE_LONG:
				dos.writeLong(((Long) c).longValue());
				break;
			case TYPE_STRING: {
				String S = (String) c;
				dos.writeInt(S.length());
				dos.writeChars(S);
			}
				break;
			default:
				assert dataType == null;
				throw new IllegalArgumentException("invalid data type: "
						+ dataType);
			}
		}
	}

}
