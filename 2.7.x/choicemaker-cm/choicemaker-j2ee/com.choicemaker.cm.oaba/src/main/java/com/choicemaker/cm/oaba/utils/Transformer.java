/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.utils;

import java.util.ArrayList;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.BlockSet;
import com.choicemaker.cm.oaba.core.ComparisonArray;
import com.choicemaker.cm.oaba.core.IComparisonArraySink;
import com.choicemaker.cm.oaba.core.IComparisonArraySinkSourceFactory;
import com.choicemaker.cm.oaba.core.IIDSet;
import com.choicemaker.cm.oaba.core.ITransformer;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.util.LongArrayList;

/**
 * This object takes an array or tree of internal id and transforms them back to
 * stage and master IDs.
 * 
 * @author pcheung
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class Transformer implements ITransformer {

	private ImmutableRecordIdTranslator translator;
	private IComparisonArraySinkSourceFactory cFactory;
	private IComparisonArraySink cOut = null;

	public Transformer(ImmutableRecordIdTranslator translator,
			IComparisonArraySinkSourceFactory cFactory)
			throws BlockingException {

		this.translator = translator;
		this.cFactory = cFactory;
	}

	@Override
	public void init() throws BlockingException {
		cOut = cFactory.getNextSink();
		cOut.open();
	}

	@Override
	public int getSplitIndex() {
		return translator.getSplitIndex();
	}

	@Override
	public void useNextSink() throws BlockingException {
		cOut.close();
		cOut = cFactory.getNextSink();
		cOut.open();
	}

	@Override
	public void transform(IIDSet bs) throws BlockingException {
		if (bs instanceof BlockSet) {
			transformBlockSet((BlockSet) bs);
		} else {
			throw new BlockingException(
					"Expecting instanceof BlockSet, but got " + bs.getClass());
		}
	}

	private void transformBlockSet(BlockSet bs) throws BlockingException {
		LongArrayList block = bs.getRecordIDs();

		// set up comparison group
		ArrayList stage = new ArrayList();
		ArrayList master = new ArrayList();
		RECORD_ID_TYPE stageType = null;
		RECORD_ID_TYPE masterType = null;

		// add to the set of distinct record ids
		for (int i = 0; i < block.size(); i++) {
			// get the original record id
			Comparable comp = translator.reverseLookup((int) block.get(i));

			if (!translator.isSplit()) {
				// only staging record source

				if (stage.size() == 0) {
					stageType = RECORD_ID_TYPE.fromInstance(comp);
					masterType = stageType;
				}
				stage.add(comp);

			} else {
				// two record sources
				if (block.get(i) < translator.getSplitIndex()) {
					// stage
					if (stage.size() == 0) {
						stageType = RECORD_ID_TYPE.fromInstance(comp);
						// Assume the masterType is the same
						// until it is changed explicitly (maybe no master ids)
						masterType = stageType;
					}
					stage.add(comp);
				} else {
					// master
					if (master.size() == 0) {
						masterType = RECORD_ID_TYPE.fromInstance(comp);
					}
					master.add(comp);
				}
			}
		}
		ComparisonArray cg =
			new ComparisonArray(stage, master, stageType, masterType);
		cOut.writeComparisonArray(cg);

	}

	@Override
	public void close() throws BlockingException {
		cOut.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ITransformer#cleanUp
	 * ()
	 */
	@Override
	public void cleanUp() throws BlockingException {
		cOut.remove();
	}

}
