/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.cmit.utils.j2ee;

import java.util.UUID;

import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.batch.ejb.AbstractPersistentObject;

public class FakePersistableRecordSource extends AbstractPersistentObject
		implements PersistableRecordSource {

	public static final String TYPE = "FAKE";

	private static final long serialVersionUID = 271L;

	public static String createFakeNameStem(String tag) {
		if (tag == null) {
			tag = EntityManagerUtils.DEFAULT_RECORDSOURCE_TAG;
		}
		tag = tag.trim();
		if (tag.isEmpty()) {
			tag = EntityManagerUtils.DEFAULT_RECORDSOURCE_TAG;
		}
		return tag;
	}

	public static String createFakeRecordSourceName(String tag) {
		String nameStem = createFakeNameStem(tag);
		StringBuilder sb = new StringBuilder(nameStem);
		if (tag.startsWith(EntityManagerUtils.PREFIX_FAKE_RECORDSOURCE)) {
			sb.append(tag);
		} else {
			sb.append(EntityManagerUtils.PREFIX_FAKE_RECORDSOURCE);
			sb.append(tag);
		}
		sb.append(EntityManagerUtils.TAG_DELIMITER);
		sb.append(UUID.randomUUID().toString());
		final String retVal = sb.toString();
		return retVal;
	}

	public static String createFakeRecordSourceFileName(String tag) {
		String nameStem = createFakeNameStem(tag);
		StringBuilder sb = new StringBuilder(nameStem);
		if (tag.startsWith(EntityManagerUtils.PREFIX_FAKE_RECORDSOURCE_FILE)) {
			sb.append(tag);
		} else {
			sb.append(EntityManagerUtils.PREFIX_FAKE_RECORDSOURCE_FILE);
			sb.append(tag);
		}
		sb.append(EntityManagerUtils.TAG_DELIMITER);
		sb.append(UUID.randomUUID().toString());
		final String retVal = sb.toString();
		return retVal;
	}

	private final String name;
	private final String fileName;

	public FakePersistableRecordSource(String tag) {
		this.name = createFakeRecordSourceName(tag);
		this.fileName = createFakeRecordSourceFileName(tag);
	}

	@Override
	public String toString() {
		return "FakeSerialRecordSource [name=" + name + "]";
	}

	@Override
	public long getId() {
		return UUID.randomUUID().hashCode();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	public String getFileName() {
		return fileName;
	}

}