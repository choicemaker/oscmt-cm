package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator.MINIMUM_VALID_INDEX;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.api.MutableRecordIdTranslatorLocal;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.core.MutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

@SuppressWarnings("rawtypes")
public abstract class AbstractRecordIdTranslator
		implements MutableRecordIdTranslatorLocal {

	protected static final Logger log =
		Logger.getLogger(AbstractRecordIdTranslator.class.getName());

	/** A magic value indicating that the split() method has not been invoked */
	protected static final int NOT_SPLIT = 0;

	/**
	 * An enumeration of states for a translator: mutable, immutable and
	 * inconsistent.
	 * <ul>
	 * <li><em><strong>MUTABLE</strong></em><br/>
	 * A translator is constructed in the mutable state. While it is mutable, it
	 * may be used to translate record ids to internal indices; this type of
	 * translation is called a <em>forward</em> translation.</li>
	 * <li><em><strong>IMMUTABLE</strong></em><br/>
	 * After all forward translations are completed, a translator may be used
	 * for <em>reverse lookups</em> of internal indices from record ids. Once
	 * the first reverse lookup is invoked, a translator switches to an
	 * immutable state. In the immutable state, it can only be used for reverse
	 * lookups, not for forward translations. A translator also switches to the
	 * immutable state when it is
	 * {@link #save(BatchJob, MutableRecordIdTranslator) saved} to a database
	 * </li>
	 * <li><em><strong>INCONSISTENT</strong></em><br/>
	 * If a forward translation is attempted after the initialization of reverse
	 * lookups, the translator state is marked as inconsistent and an
	 * IllegalStateException is thrown.</li>
	 * </ul>
	 */
	public static enum TRANSLATOR_STATE {
		MUTABLE, IMMUTABLE
	}

	/**
	 * A non-null record-id sink can be UNKNOWN, OPEN or CLOSED. (The UNKNOWN
	 * state is the state when a sink is obtained from a factory, since there's
	 * no way to check the state directly.) These states are a one-way rachet,
	 * from UNKNOWN to OPEN to CLOSED, with no other transition possible.
	 */
	protected static enum SINK_STATE {
		UNKNOWN, OPEN, CLOSED
	}

	private TRANSLATOR_STATE translatorState = TRANSLATOR_STATE.MUTABLE;

	/** A flag indicating whether cached translator files are retained */
	private final boolean keepFiles;

	private final BatchJob batchJob;

	private final IRecordIdSinkSourceFactory rFactory;

	/** Typically a source of staging records */
	private final IRecordIdSink sink1;

	/** Typically a source of master records */
	private final IRecordIdSink sink2;

	/** The state of sink1 */
	private SINK_STATE sink1State;

	/** The state of sink2 */
	private SINK_STATE sink2State;

	/** Number of ids written to sink1 */
	private int count1;

	/** Number of ids written to sink2 */
	private int count2;

	/** The type of record ids handled by this translator */
	private RECORD_ID_TYPE recordIdType;

	/** The next available internal index to which a record id may be mapped */
	private int currentIndex = MINIMUM_VALID_INDEX - 1;

	/**
	 * This is the internal index at which the indices for records from the
	 * second record source start. If this value is NOT_SPLIT, it means there is
	 * only 1 record source.
	 */
	private int splitIndex = NOT_SPLIT;

	private final Map<Comparable, Integer> seen = new HashMap<>();

	public AbstractRecordIdTranslator(BatchJob job,
			IRecordIdSinkSourceFactory factory, IRecordIdSink s1,
			IRecordIdSink s2, boolean doKeepFiles) throws BlockingException {
		if (job == null || factory == null || s1 == null || s2 == null) {
			throw new IllegalArgumentException("null argument");
		}
		this.keepFiles = doKeepFiles;
		if (s1.exists()) {
			String msg = "translator cache already exists: " + s1;
			log.info(msg);
			if (!isKeepFiles()) {
				throw new IllegalArgumentException(msg);
			}
		}
		if (s2.exists()) {
			String msg = "translator cache already exists: " + s2;
			log.info(msg);
			if (!isKeepFiles()) {
				throw new IllegalArgumentException(msg);
			}
		}
		this.batchJob = job;
		this.rFactory = factory;
		this.sink1 = s1;
		log.info("Sink 1: " + sink1);
		this.setSink1State(SINK_STATE.UNKNOWN);
		this.sink2 = s2;
		log.info("Sink 2: " + sink2);
		this.setSink2State(SINK_STATE.UNKNOWN);
	}

	protected TRANSLATOR_STATE getTranslatorState() {
		return translatorState;
	}

	protected void setTranslatorState(TRANSLATOR_STATE translatorState) {
		this.translatorState = translatorState;
	}

	protected SINK_STATE getSink1State() {
		return sink1State;
	}

	protected void setSink1State(SINK_STATE sink1State) {
		this.sink1State = sink1State;
	}

	protected SINK_STATE getSink2State() {
		return sink2State;
	}

	protected void setSink2State(SINK_STATE sink2State) {
		this.sink2State = sink2State;
	}

	protected int getCount1() {
		return count1;
	}

	protected void setCount1(int count1) {
		this.count1 = count1;
	}

	protected int getCount2() {
		return count2;
	}

	protected void setCount2(int count2) {
		this.count2 = count2;
	}

	protected int getCurrentIndex() {
		return currentIndex;
	}

	protected void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	protected boolean isKeepFiles() {
		return keepFiles;
	}

	protected IRecordIdSinkSourceFactory getrFactory() {
		return rFactory;
	}

	protected Map<Comparable, Integer> getSeen() {
		return seen;
	}

	@Override
	public BatchJob getBatchJob() {
		return batchJob;
	}

	IRecordIdSinkSourceFactory getFactory() {
		return getrFactory();
	}

	IRecordIdSink getSink1() {
		return sink1;
	}

	IRecordIdSink getSink2() {
		return sink2;
	}

	@Override
	public RECORD_ID_TYPE getRecordIdType() {
		return recordIdType;
	}

	protected void _setRecordIdType(RECORD_ID_TYPE dataType) {
		this.recordIdType = dataType;
	}

	@Override
	public int getSplitIndex() {
		return splitIndex;
	}

	@Override
	public boolean isSplit() {
		return getSplitIndex() != NOT_SPLIT;
	}

	protected void _setSplitIndex(int splitIndex) {
		this.splitIndex = splitIndex;
	}

	@Override
	public String toString() {
		return "MutableRecordIdTranslatorImpl [recordIdType="
				+ getRecordIdType() + ", translatorState="
				+ getTranslatorState() + ", currentIndex=" + getCurrentIndex()
				+ ", splitIndex=" + getSplitIndex() + ", sink1=" + getSink1()
				+ ", sink1State=" + getSink1State() + ", count1=" + getCount1()
				+ ", sink2=" + getSink2() + ", sink2State=" + getSink2State()
				+ ", count2=" + getCount2() + "]";
	}

}