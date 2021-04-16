/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator.INVALID_INDEX;
import static com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator.MINIMUM_VALID_INDEX;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

/**
 * A cached collection of translated record identifiers. (Persistent
 * translations are maintained by
 * {@link com.choicemaker.cm.oaba.ejb.RecordIdControllerBean
 * RecordIdControllerBean}).
 * 
 * @author rphall
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class DefaultRecordIdTranslator extends AbstractRecordIdTranslator {

	public DefaultRecordIdTranslator(BatchJob job,
			IRecordIdSinkSourceFactory factory, IRecordIdSink s1,
			IRecordIdSink s2, boolean doKeepFiles) throws BlockingException {
		super(job, factory, s1, s2, doKeepFiles);
	}

	@Override
	public void cleanUp() throws BlockingException {
		if (getSink1().exists()) {
			getSink1().flush();
			getSink1().close();
			setSink1State(SINK_STATE.CLOSED);
			if (!isKeepFiles()) {
				getSink1().remove();
			}
			setSink1State(null);
		}
		if (getSink2().exists()) {
			getSink2().flush();
			getSink2().close();
			setSink2State(SINK_STATE.CLOSED);
			if (!isKeepFiles()) {
				getSink2().remove();
			}
			setSink2State(null);
		}
	}

	@Override
	public void close() throws BlockingException {
		log.fine("close(): translatorState == " + getTranslatorState());
		getSeen().clear();
		if (!isClosed()) {
			// Check first sink
			if (getSink1().exists() && getSink1State() == SINK_STATE.OPEN) {
				log.info("Writing ids to sink1: " + getCount1());
				getSink1().flush();
				log.info("Closing sink1: " + getSink1());
				getSink1().close();
				setSink1State(SINK_STATE.CLOSED);

			} else {
				log.fine("Sink1 already closed: " + getSink1());
			}

			// Check second sink
			if (getSink2().exists() && getSink2State() == SINK_STATE.OPEN) {
				log.info("Writing ids to sink2: " + getCount2());
				getSink2().flush();
				log.info("Closing sink2: " + getSink2());
				getSink2().close();
				setSink2State(SINK_STATE.CLOSED);

			} else {
				log.info("Sink2 does not exist: " + getSink2());
			}

			this.setTranslatorState(TRANSLATOR_STATE.IMMUTABLE);
		} else {
			log.warning("translator is already closed");
		}

		String msg = "close(): " + isClosed() + ", translatorState == "
				+ getTranslatorState();
		if (!isClosed()) {
			log.severe(msg);
		} else {
			log.info(msg);
		}
		assert isClosed();
	}

	@Override
	public void open() throws BlockingException {
		if (isClosed()) {
			throw new IllegalStateException(
					"invalid translator state: " + getTranslatorState());
		}
		setCurrentIndex(MINIMUM_VALID_INDEX - 1);
		log.info("Opening sink1: " + getSink1());
		getSink1().open();
		setSink1State(SINK_STATE.OPEN);
		getSeen().clear();
	}

	@Override
	public void split() throws BlockingException {
		if (getSplitIndex() == NOT_SPLIT) {
			_setSplitIndex(getCurrentIndex() + 1);
			getSeen().clear();
			log.info("Writing ids to sink1: " + getCount1());
			getSink1().flush();
			log.info("Closing sink1: " + getSink1());
			getSink1().close();
			setSink1State(SINK_STATE.CLOSED);
			log.info("Opening sink2: " + getSink2());
			getSink2().open();
			setSink2State(SINK_STATE.OPEN);
		} else {
			log.warning(
					"Split method invoked on a previously split translator");
		}
	}

	@Override
	public int translate(Comparable o) throws BlockingException {
		if (isClosed()) {
			throw new IllegalStateException("translator is no longer mutable");
		}
		int retVal;
		if (o == null) {
			retVal = INVALID_INDEX;
			log.warning(
					"translating null record id to an invalid internal index ("
							+ retVal + ")");

		} else {
			Integer i = getSeen().get(o);
			if (i != null) {
				retVal = i.intValue();

			} else {
				setCurrentIndex(getCurrentIndex() + 1);
				retVal = getCurrentIndex();
				getSeen().put(o, retVal);

				// figure out the id type for the sinks
				if (getCurrentIndex() == 0) {
					_setRecordIdType(RECORD_ID_TYPE.fromInstance(o));
					getSink1().setRecordIDType(getRecordIdType());
					getSink2().setRecordIDType(getRecordIdType());
				}
				assert getRecordIdType() == RECORD_ID_TYPE.fromInstance(o);
				assert getRecordIdType() == getSink1().getRecordIdType();
				assert getRecordIdType() == getSink2().getRecordIdType();

				if (getSplitIndex() == NOT_SPLIT) {
					getSink1().writeRecordID(o);
					setCount1(getCount1() + 1);
				} else {
					getSink2().writeRecordID(o);
					setCount2(getCount2() + 1);
				}

			}
		}
		return retVal;
	}

	public boolean isClosed() {
		log.finer("isClosed(): translatorState: " + getTranslatorState());
		boolean retVal;
		if (this.getTranslatorState() == TRANSLATOR_STATE.MUTABLE) {
			retVal = false;
		} else {
			assert this.getTranslatorState() == TRANSLATOR_STATE.IMMUTABLE;
			retVal = true;
		}
		log.finer("isClosed() == " + retVal);
		return retVal;
	}

	public boolean doTranslatorCachesExist() {
		return this.getSink1().exists() || this.getSink1().exists();
	}

}
