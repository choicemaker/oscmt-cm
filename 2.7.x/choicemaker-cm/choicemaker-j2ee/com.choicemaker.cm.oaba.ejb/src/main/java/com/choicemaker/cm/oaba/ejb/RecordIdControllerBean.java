/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.RecordIdTranslationJPA.PN_TRANSLATEDID_DELETE_BY_JOBID_JOBID;
import static com.choicemaker.cm.oaba.ejb.RecordIdTranslationJPA.QN_TRANSLATEDID_DELETE_BY_JOBID;
import static com.choicemaker.cm.oaba.ejb.RecordIdTranslationJPA.QN_TRANSLATEDID_FIND_ALL;
import static com.choicemaker.cm.oaba.utils.RecordTransferLogging.logTransferRate;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.ejb.BatchJobFileUtils;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.oaba.api.DbRecordIdTranslator;
import com.choicemaker.cm.oaba.api.ImmutableRecordIdTranslatorLocal;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.api.RecordIdTranslation;
import com.choicemaker.cm.oaba.core.IRecordIdFactory;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IRecordIdSource;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.MutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.ejb.util.DbRecordIdTranslatorFactory;

@Stateless
@TransactionAttribute(REQUIRED)
public class RecordIdControllerBean implements RecordIdController {

	/**
	 * Specifies how record-id translations are computed.
	 * <ul>
	 * <li>APPLICATION: computed by the application</li>
	 * <li>DATABASE: computed in the database. Managed by code that is
	 * registered with a
	 * {@link com.choicemaker.cm.oaba.ejb.util.DbRecordIdTranslatorFactory
	 * translator factory}</li>
	 * <li>CUSTOM: computed by custom code specified by a System property</li
	 * </ul>
	 */
	public static enum TRANSLATION_TYPE {
		APPLICATION, DATABASE, CUSTOM
	}

	private static final Logger logger =
		Logger.getLogger(RecordIdControllerBean.class.getName());

	public static final String SOURCE =
		RecordIdController.class.getSimpleName();

	public static final String BASENAME_RECORDID_TRANSLATOR = "translator";

	public static final String BASENAME_RECORDID_STORE = "recordID";

	/**
	 * Name of a System property (true/false) that controls whether reflective
	 * persistence is used. Default value is
	 * {@link #DEFAULT_REFLECTIVE_PERSISTENCE}. This setting is ignored if
	 * record-id translations are computed in the database.
	 */
	public static final String PN_REFLECTIVE_PERISTENCE =
		"oaba.record-id.persistence";

	public static final boolean DEFAULT_REFLECTIVE_PERSISTENCE = false;

	/**
	 * Name of a System property that controls the batch size when translations
	 * are uploaded to a database. If this property is missing or null or not an
	 * integer value, then the batch size is set to
	 * {@link #DEFAULT_TRANSLATION_BATCH_SIZE}. If this property is zero or
	 * negative, then the batch size is set to {@link Integer#MAX_VALUE}.
	 */
	public static final String PN_TRANSLATION_BATCH_SIZE =
		"oaba.record-id.batchsize";

	public static final int DEFAULT_TRANSLATION_BATCH_SIZE = 1500;

	/**
	 * Name of a System property that specifies the class of the custom code for
	 * translating record-id values to int aliases.
	 */
	public static final String PN_TRANSLATION_CLASS =
		"oaba.record-id.custom-class";

	/**
	 * Name of a System property (true/false) that controls whether reflective
	 * persistence is used. Default value is
	 * {@link #DEFAULT_REFLECTIVE_PERSISTENCE}. This setting is ignored if
	 * record-id translations are computed in the database.
	 */
	public static final String PN_TRANSLATION_TYPE =
		"oaba.record-id.translation";

	public static TRANSLATION_TYPE DEFAULT_TRANSLATION_TYPE =
		TRANSLATION_TYPE.APPLICATION;

	/**
	 * The index in the {@link #createRecordIdTypeQuery(BatchJob)
	 * RecordIdTypeQuery} of the RECORD_ID_TYPE field
	 */
	protected static final int QUERY_INDEX_RECORD_ID_TYPE = 1;

	/**
	 * Computes totalSize/batchSize, or 10, whichever is smallest and positive
	 */
	public static int computeBatchMultiple(int batchSize, int totalSize) {
		int retVal;
		if (batchSize <= 0) {
			retVal = 1;
		} else if (totalSize <= 10 * batchSize) {
			retVal = batchSize;
		} else {
			retVal = batchSize * ((totalSize/(10 * batchSize)) + 1);
		}
		assert retVal >= batchSize;
		assert retVal >= totalSize / 10;
		assert batchSize <= 0 || retVal % batchSize == 0;
		{
			String msg0 = "computeBatchMultiple(batchSize:%d,totalSize:%d): %d";
			String msg = String.format(msg0, batchSize, totalSize, retVal);
			logger.fine(msg);
		}
		return retVal;
	}

	private static Constructor<?> getConstructor(Class<?> transClass,
			Class<?> ridClass) throws BlockingException {
		assert transClass != null;
		assert ridClass != null;
		Constructor<?> retVal;
		try {
			retVal = transClass.getConstructor(BatchJob.class, ridClass,
					RECORD_SOURCE_ROLE.class, int.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			String msg0 = "Unable to create 4-parameter constructor for "
					+ "class '%s' (BatchJob, RECORD_SOURCE_ROLE, '%s', "
					+ "int): %s";
			final String transClassName = transClass.getSimpleName();
			final String ridClassName = ridClass.getSimpleName();
			String msg =
				String.format(msg0, transClassName, ridClassName, e.toString());
			throw new BlockingException(msg);
		}
		return retVal;
	}

	private static RecordIdSinkSourceFactory getRecordIDFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new RecordIdSinkSourceFactory(wd, BASENAME_RECORDID_STORE,
				BatchJobFileUtils.TEXT_SUFFIX);
	}

	/**
	 * @see #PN_TRANSLATION_BATCH_SIZE
	 */
	private static int getRecordIdTranslationBatchSize() {
		Integer retVal = Integer.getInteger(PN_TRANSLATION_BATCH_SIZE,
				DEFAULT_TRANSLATION_BATCH_SIZE);
		assert retVal != null;
		if (retVal <= 0) {
			retVal = Integer.MAX_VALUE;
		}
		return retVal;
	}

	/**
	 * This gets the factory that is used to get translator id sink and source.
	 */
	private static RecordIdSinkSourceFactory getTransIDFactory(BatchJob job) {
		String wd = BatchJobFileUtils.getWorkingDir(job);
		return new RecordIdSinkSourceFactory(wd, BASENAME_RECORDID_TRANSLATOR,
				BatchJobFileUtils.BINARY_SUFFIX);
	}

	/** Checks the system property {@link RecordIdController#PN_KEEP_FILES} */
	public static boolean isKeepFilesRequested() {
		String value = System.getProperty(PN_KEEP_FILES, "false");
		Boolean _keepFiles = Boolean.valueOf(value);
		boolean retVal = _keepFiles.booleanValue();
		return retVal;
	}

	protected static void logDuration(String source, String method, String tag,
			long duration) {
		final String msg0 = "%s.%s(%s) duration: %d (msecs)";
		String msg = String.format(msg0, source, method, tag, duration);
		logger.fine(msg);
	}

	public static boolean useReflectivePersistence() {
		boolean retVal = DEFAULT_REFLECTIVE_PERSISTENCE;
		String value = System.getProperty(PN_REFLECTIVE_PERISTENCE);
		if (value != null) {
			retVal = Boolean.parseBoolean(value);
		}
		return retVal;
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB(beanName = "OabaJobManagerBean")
	private OabaJobManager jobManager;

	@EJB
	private OabaParametersController paramsController;

	private boolean keepFiles = isKeepFilesRequested();

	@Override
	public MutableRecordIdTranslator<?> createMutableRecordIdTranslator(
			BatchJob job) throws BlockingException {
		return createMutableRecordIdTranslator(job, null);
	}

	@Override
	public MutableRecordIdTranslator<?> createMutableRecordIdTranslator(
			BatchJob job, DbRecordIdTranslator unused) throws BlockingException {

		logger.entering("createMutableRecordIdTranslator", job.toString());

		RecordIdSinkSourceFactory rFactory = getTransIDFactory(job);
		IRecordIdSink sink1 = rFactory.getNextSink();
		logger.finer("sink1: " + sink1);
		IRecordIdSink sink2 = rFactory.getNextSink();
		logger.finer("sink2: " + sink2);

		// Does an immutable translator already exist?
		// FIXME count translations, don't retrieve them
		List<?> translations = findTranslationImpls(job);
		if (translations != null && !translations.isEmpty()) {
			String msg =
				"Record-id translations already exist for job " + job.getId();
			throw new BlockingException(msg);
		}

		// Have the translator caches already been created?
		if (sink1.exists() || sink2.exists()) {
			logger.severe("Sink 1 already exists: " + sink1);
			if (sink1.exists() || sink2.exists()) {
				logger.severe("Sink 2 already exists: " + sink1);
			}
			File wd = job.getWorkingDirectory();
			String location = wd == null ? "unknown" : wd.getAbsolutePath();
			String msg =
				"A mutable translator appears to have been created already. "
						+ "A new translator can not be created until the "
						+ "existing caches have been removed in the working "
						+ "directory: " + location;
			throw new BlockingException(msg);
		}
		logger.finer("Sink 1: " + sink1);
		logger.finer("Sink 2: " + sink2);

		DatabaseAccessor<?> dba = null; // FIXME getDatabaseAccessor();
		MutableRecordIdTranslator<?> retVal =
			DbRecordIdTranslatorFactory.createDatabaseIdTranslator(dba, job,
					rFactory, sink1, sink2, keepFiles);
		return retVal;
	}

	protected String createRecordIdTypeQuery(BatchJob job) {
		final long jobId = job.getId();
		StringBuffer b = new StringBuffer();
		b.append("SELECT ").append(RecordIdTranslationJPA.CN_RECORD_TYPE);
		b.append(" FROM ").append(RecordIdTranslationJPA.TABLE_NAME);
		b.append(" WHERE ").append(RecordIdTranslationJPA.CN_JOB_ID);
		b.append(" = ").append(jobId);
		b.append(" GROUP BY ").append(RecordIdTranslationJPA.CN_JOB_ID);
		return b.toString();
	}

	@Override
	public int deleteTranslationsByJob(BatchJob job) {
		Query query = em.createNamedQuery(QN_TRANSLATEDID_DELETE_BY_JOBID);
		query.setParameter(PN_TRANSLATEDID_DELETE_BY_JOBID_JOBID, job.getId());
		int deletedCount = query.executeUpdate();
		return deletedCount;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public <T extends Comparable<T>> List<RecordIdTranslation<T>> findAllRecordIdTranslations() {
		Query query = em.createNamedQuery(QN_TRANSLATEDID_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<RecordIdTranslation<T>> entries = query.getResultList();
		if (entries == null) {
			entries = new ArrayList<RecordIdTranslation<T>>();
		}
		return entries;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public <T extends Comparable<T>> ImmutableRecordIdTranslatorLocal<T> findRecordIdTranslator(
			BatchJob job) throws BlockingException {
		ImmutableRecordIdTranslatorImpl irit = findTranslatorImpl(job);
		@SuppressWarnings("unchecked")
		ImmutableRecordIdTranslatorLocal<T> retVal = irit;
		return retVal;
	}

	protected <T extends Comparable<T>> List<AbstractRecordIdTranslationEntity<T>> findTranslationImpls(
			BatchJob job) throws BlockingException {

		final String METHOD = "findTranslationImpls(BatchJob)";
		final String TAG = String.format("%s.%s:", SOURCE, METHOD);

		Query query = em.createNamedQuery(
				RecordIdTranslationJPA.QN_TRANSLATEDID_FIND_BY_JOBID);
		query.setParameter(
				RecordIdTranslationJPA.PN_TRANSLATEDID_FIND_BY_JOBID_JOBID,
				job.getId());

		final long startQuery = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		List<AbstractRecordIdTranslationEntity<T>> retVal =
			query.getResultList();

		final long durationMsecs = System.currentTimeMillis() - startQuery;
		final int count = retVal.size();
		final String msg0 =
			"%s translations: %d, msecs: %d, translations/msec: %2.1f";
		logTransferRate(logger, msg0, TAG, count, durationMsecs);

		return retVal;
	}

	protected <T extends Comparable<T>> ImmutableRecordIdTranslatorImpl findTranslatorImpl(
			BatchJob job) throws BlockingException {
		logger.fine("findTranslatorImpl " + job);
		if (job == null) {
			throw new IllegalArgumentException("null batch job");
		}
		List<AbstractRecordIdTranslationEntity<T>> translations =
			findTranslationImpls(job);
		final RECORD_ID_TYPE expectedRecordIdType = null;
		ImmutableRecordIdTranslatorImpl retVal =
			ImmutableRecordIdTranslatorImpl.createTranslator(job,
					expectedRecordIdType, translations, keepFiles);
		return retVal;
	}

	protected DatabaseAccessor<?> getDatabaseAccessor(
			OabaParameters oabaParams) {
		final String METHOD = "getDatabaseAccessor(OabaParameters)";
		DatabaseAccessor<?> retVal = null;
		if (oabaParams == null) {
			assert retVal == null;

		} else {
			final String dbaName =
				paramsController.getReferenceDatabaseAccessor(oabaParams);
			if (dbaName == null) {
				assert retVal == null;

			} else {
				try {
					Class<?> dbaClass = Class.forName(dbaName);
					retVal = (DatabaseAccessor<?>) dbaClass.newInstance();
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException e) {
					String msg0 =
						"Unable to get database Accessor for '%s': Cause: %s";
					String msg = String.format(msg0, dbaName, e.toString());
					logger.warning(msg);
					assert retVal == null;
				}
			}
		}

		String msg0 = "%s.%s returns %s";
		String msg = String.format(msg0, SOURCE, METHOD,
				retVal == null ? null : retVal.getClass().getName());
		logger.fine(msg);

		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public IRecordIdSinkSourceFactory getRecordIdSinkSourceFactory(
			BatchJob job) {
		return getRecordIDFactory(job);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public RECORD_ID_TYPE getTranslatorType(BatchJob job)
			throws BlockingException {
		if (job == null) {
			throw new IllegalArgumentException("null OABA job");
		}

		// This method requires EclipseLink (it won't work for Hibernate).
		// It should be redesigned to use a RecordIdInfo class to metadata
		// about record ids, and the RecordIdInfo class should be persistent
		RECORD_ID_TYPE retVal = null;
		em.getTransaction().begin();
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = em.unwrap(Connection.class);
			connection.setReadOnly(true);
			// connection.setAutoCommit(true); // 2015-04-01a EJB3 CHANGE rphall
			String query = createRecordIdTypeQuery(job);
			logger.fine(query);

			Set<RECORD_ID_TYPE> dataTypes =
				EnumSet.noneOf(RECORD_ID_TYPE.class);
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Integer i = rs.getInt(QUERY_INDEX_RECORD_ID_TYPE);
				// JDBC: getInt(..) is 0 if the field is null
				assert i != null;
				RECORD_ID_TYPE rit = RECORD_ID_TYPE.fromValue(i);
				assert rit != null;
				dataTypes.add(rit);
			}
			if (dataTypes.isEmpty()) {
				String msg = "No translated record identifier for job " + job;
				throw new BlockingException(msg);
			} else if (dataTypes.size() > 1) {
				String msg = "Inconsistent record identifiers for job " + job
						+ "(" + dataTypes + ")";
				throw new BlockingException(msg);
			}
			assert dataTypes.size() == 1;
			retVal = dataTypes.iterator().next();
		} catch (SQLException e) {
			em.getTransaction().rollback();
			String msg = this.getClass().getSimpleName()
					+ ".getTranslatorType(BatchJob): "
					+ "unable to get record-id type: " + e;
			throw new BlockingException(msg, e);
		} finally {
			em.getTransaction().commit();
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					String msg = this.getClass().getSimpleName()
							+ ".getTranslatorType(BatchJob): "
							+ "unable to close JDBC connection: " + e;
					logger.severe(msg);
				}
			}
		}
		assert retVal != null;
		return retVal;
	}

	private void reflectPersist(Class<?> transClass, Constructor<?> transCtor,
			BatchJob job, Object recordId, RECORD_SOURCE_ROLE rsr, int index)
			throws BlockingException {
		assert transClass != null;
		assert transCtor != null;
		assert job != null;
		assert recordId != null;
		assert rsr != null;
		try {
			Object o = transCtor.newInstance(job, recordId, rsr, index);
			em.persist(transClass.cast(o));
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			String msg0 = "Unable to translation index '%d' for recordId '%s' "
					+ "(type %s) of batch job '%d': %s";
			String msg = String.format(msg0, index, recordId.toString(),
					rsr.symbol, job.getId(), e.toString());
			throw new BlockingException(msg);
		}
	}

	/**
	 * Implements
	 * {@link RecordIdController#save(BatchJob, MutableRecordIdTranslator) save}
	 * for instances of {@link DefaultRecordIdTranslator}. If the translator is
	 * not {@link DefaultRecordIdTranslator#isClosed() closed}:
	 * <ol>
	 * <li>The translator is
	 * {@link IRecordIdFactory#toImmutableTranslator(MutableRecordIdTranslator)
	 * converted} to an immutable translator.</li>
	 * <li>The mutable translator is {@link DefaultRecordIdTranslator#close()
	 * closed}.</li>
	 * <li>The translations of the immutable translator are saved in persistent
	 * storage.</li>
	 * </ol>
	 * If the mutable translator is already closed, its translations are
	 * presumed to be stored already. The translations are restored to an
	 * immutable translator which is then returned. If the translations are not
	 * found or can not be restored, an exception is thrown.
	 *
	 * @throws ClassCastException
	 *             if the specified translator is not an instance of
	 *             <code>MutableRecordIdTranslatorImpl</code>
	 * @throws BlockingException
	 *             if the translations of the translator can not be saved or
	 *             found in persistent storage.
	 */
	@Override
	public <T extends Comparable<T>> ImmutableRecordIdTranslatorLocal<T> save(
			BatchJob job, ImmutableRecordIdTranslator<T> translator)
			throws BlockingException {
		if (job == null || translator == null) {
			throw new IllegalArgumentException("null argument");
		}

		// HACK FIXME and update the Javadoc to comply with the interface
		ImmutableRecordIdTranslatorImpl impl =
			(ImmutableRecordIdTranslatorImpl) translator;
		// END HACK

		@SuppressWarnings("unchecked")
		ImmutableRecordIdTranslatorLocal<T> retVal =
			saveTranslatorImpl(job, impl);

		return retVal;
	}

	protected void saveIntegerTranslations(BatchJob job,
			ImmutableRecordIdTranslatorImpl impl, final int batchSize) {
		assert em != null;
		assert impl != null;
		assert batchSize > 0;
		final String METHOD = "saveIntegerTranslations";

		if (impl.isSplit()) {
			Integer recordId = RecordIdIntegerTranslation.RECORD_ID_PLACEHOLDER;
			int index = impl.getSplitIndex();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.SPLIT_INDEX;
			RecordIdIntegerTranslation rit =
				new RecordIdIntegerTranslation(job, recordId, rsr, index);
			em.persist(rit);
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries1 = impl.ids1_To_Indices.entrySet();
		final int bMultiple1 = computeBatchMultiple(batchSize, entries1.size());
		int count = 0;
		for (@SuppressWarnings("rawtypes")
		Map.Entry e1 : entries1) {
			Integer recordId = (Integer) e1.getKey();
			Integer index = (Integer) e1.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.STAGING;
			RecordIdIntegerTranslation rit =
				new RecordIdIntegerTranslation(job, recordId, rsr, index);
			em.persist(rit);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple1 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple1;
					String msg0 = "incremental trans1 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries2 = impl.ids2_To_Indices.entrySet();
		final int bMultiple2 = computeBatchMultiple(batchSize, entries1.size());
		for (@SuppressWarnings("rawtypes")
		Map.Entry e2 : entries2) {
			Integer recordId = (Integer) e2.getKey();
			Integer index = (Integer) e2.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.MASTER;
			RecordIdIntegerTranslation rit =
				new RecordIdIntegerTranslation(job, recordId, rsr, index);
			em.persist(rit);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple2 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple2;
					String msg0 = "incremental trans2 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}
	}

	protected void saveLongTranslations(BatchJob job,
			ImmutableRecordIdTranslatorImpl impl, final int batchSize) {
		assert em != null;
		assert impl != null;
		assert batchSize > 0;
		final String METHOD = "saveLongTranslations";

		if (impl.isSplit()) {
			Long recordId = RecordIdLongTranslation.RECORD_ID_PLACEHOLDER;
			int index = impl.getSplitIndex();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.SPLIT_INDEX;
			RecordIdLongTranslation rit =
				new RecordIdLongTranslation(job, recordId, rsr, index);
			em.persist(rit);
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries1 = impl.ids1_To_Indices.entrySet();
		final int bMultiple1 = computeBatchMultiple(batchSize, entries1.size());
		int count = 0;
		for (@SuppressWarnings("rawtypes")
		Map.Entry e1 : entries1) {
			Long recordId = (Long) e1.getKey();
			Integer index = (Integer) e1.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.STAGING;
			RecordIdLongTranslation rit =
				new RecordIdLongTranslation(job, recordId, rsr, index);
			em.persist(rit);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple1 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple1;
					String msg0 = "incremental trans1 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries2 = impl.ids2_To_Indices.entrySet();
		final int bMultiple2 = computeBatchMultiple(batchSize, entries1.size());
		for (@SuppressWarnings("rawtypes")
		Map.Entry e2 : entries2) {
			Long recordId = (Long) e2.getKey();
			Integer index = (Integer) e2.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.MASTER;
			RecordIdLongTranslation rit =
				new RecordIdLongTranslation(job, recordId, rsr, index);
			em.persist(rit);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple2 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple2;
					String msg0 = "incremental trans2 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}
	}

	protected void saveReflectedTranslations(BatchJob job,
			ImmutableRecordIdTranslatorImpl impl, final int batchSize,
			Class<?> transClass, Constructor<?> transCtor)
			throws BlockingException {
		assert em != null;
		assert impl != null;
		assert batchSize > 0;
		final String METHOD = "saveReflectedTranslations";

		if (impl.isSplit()) {
			String recordId = RecordIdStringTranslation.RECORD_ID_PLACEHOLDER;
			int index = impl.getSplitIndex();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.SPLIT_INDEX;
			reflectPersist(transClass, transCtor, job, recordId, rsr, index);
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries1 = impl.ids1_To_Indices.entrySet();
		final int bMultiple1 = computeBatchMultiple(batchSize, entries1.size());
		int count = 0;
		for (@SuppressWarnings("rawtypes")
		Map.Entry e1 : entries1) {
			String recordId = (String) e1.getKey();
			Integer index = (Integer) e1.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.STAGING;
			reflectPersist(transClass, transCtor, job, recordId, rsr, index);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple1 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple1;
					String msg0 = "incremental trans1 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries2 = impl.ids2_To_Indices.entrySet();
		final int bMultiple2 = computeBatchMultiple(batchSize, entries1.size());
		for (@SuppressWarnings("rawtypes")
		Map.Entry e2 : entries2) {
			String recordId = (String) e2.getKey();
			Integer index = (Integer) e2.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.MASTER;
			reflectPersist(transClass, transCtor, job, recordId, rsr, index);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple2 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple2;
					String msg0 = "incremental trans2 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}
	}

	protected void saveStringTranslations(BatchJob job,
			ImmutableRecordIdTranslatorImpl impl, final int batchSize) {
		assert em != null;
		assert impl != null;
		assert batchSize > 0;
		final String METHOD = "saveStringTranslations";

		if (impl.isSplit()) {
			String recordId = RecordIdStringTranslation.RECORD_ID_PLACEHOLDER;
			int index = impl.getSplitIndex();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.SPLIT_INDEX;
			RecordIdStringTranslation rit =
				new RecordIdStringTranslation(job, recordId, rsr, index);
			em.persist(rit);
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries1 = impl.ids1_To_Indices.entrySet();
		final int bMultiple1 = computeBatchMultiple(batchSize, entries1.size());
		int count = 0;
		for (@SuppressWarnings("rawtypes")
		Map.Entry e1 : entries1) {
			String recordId = (String) e1.getKey();
			Integer index = (Integer) e1.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.STAGING;
			RecordIdStringTranslation rit =
				new RecordIdStringTranslation(job, recordId, rsr, index);
			em.persist(rit);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple1 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple1;
					String msg0 = "incremental trans1 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}

		@SuppressWarnings({
				"unchecked", "rawtypes" })
		Set<Map.Entry> entries2 = impl.ids2_To_Indices.entrySet();
		final int bMultiple2 = computeBatchMultiple(batchSize, entries1.size());
		for (@SuppressWarnings("rawtypes")
		Map.Entry e2 : entries2) {
			String recordId = (String) e2.getKey();
			Integer index = (Integer) e2.getValue();
			RECORD_SOURCE_ROLE rsr = RECORD_SOURCE_ROLE.MASTER;
			RecordIdStringTranslation rit =
				new RecordIdStringTranslation(job, recordId, rsr, index);
			em.persist(rit);
			if (++count % batchSize == 0) {
				long startFlush = System.currentTimeMillis();
				em.flush();
				em.clear();
				if (count % bMultiple2 == 0) {
					final long finishFlush = System.currentTimeMillis();
					final long durationFlush = finishFlush - startFlush;
					final int flushIdx = count / bMultiple2;
					String msg0 = "incremental trans2 flush [%d]";
					String msg = String.format(msg0, flushIdx);
					logDuration(SOURCE, METHOD, msg, durationFlush);
				}
			}
		}
	}

	protected <T extends Comparable<T>> ImmutableRecordIdTranslatorImpl saveTranslatorImpl(
			BatchJob job, ImmutableRecordIdTranslatorImpl impl)
			throws BlockingException {
		final String METHOD = "saveTranslatorImpl";
		final long startSaveTranslatorImpl = System.currentTimeMillis();

		logger.fine("Saving " + job + " / " + impl);
		final int batchSize = getRecordIdTranslationBatchSize();
		logger.fine("Translation batch size: " + batchSize);
		ImmutableRecordIdTranslatorImpl retVal = null;
		// Check if the translations already exist in the database
		List<AbstractRecordIdTranslationEntity<T>> translations =
			findTranslationImpls(job);
		if (!translations.isEmpty()) {
			logger.fine("Translations: " + translations.size());
			impl.assertPersistent(translations);
			retVal = impl;
			logger.fine("Returning unaltered translator: " + retVal);

		} else if (impl.isEmpty()) {
			String msg = "Translator is empty. "
					+ "(Has the translator translated any record ids?)";
			logger.fine(msg);
			retVal = impl;
			logger.warning("No translations saved: " + retVal);

		} else if (useReflectivePersistence()) {
			logger.finest(
					"Using reflective persistence for record-id translations");
			final RECORD_ID_TYPE dataType = impl.getRecordIdType();
			Class<?> transClass;
			Class<?> ridClass;
			switch (dataType) {
			case TYPE_INTEGER:
				transClass = RecordIdIntegerTranslation.class;
				ridClass = Integer.class;
				break;
			case TYPE_LONG:
				transClass = RecordIdLongTranslation.class;
				ridClass = Long.class;
				break;
			case TYPE_STRING:
				transClass = RecordIdStringTranslation.class;
				ridClass = String.class;
				break;
			default:
				throw new Error("unexpected record source type: " + dataType);
			}
			final String transClassName = transClass.getSimpleName();
			final String ridClassName = ridClass.getSimpleName();
			logger.finest("Translation class: " + transClassName);
			logger.finest("RecordId class: " + ridClassName);

			final Constructor<?> transCtor;
			transCtor = getConstructor(transClass, ridClass);

			// Prepare for batch insert
			final long startFlush1 = System.currentTimeMillis();
			em.flush();
			em.clear();
			final long finishFlush1 = System.currentTimeMillis();
			final long durationFlush1 = finishFlush1 - startFlush1;
			logDuration(SOURCE, METHOD, "initial flush", durationFlush1);

			final long startSave = System.currentTimeMillis();
			saveReflectedTranslations(job, impl, batchSize, transClass,
					transCtor);
			logger.finest("Saved " + job + " / " + impl);
			final long finishSave = System.currentTimeMillis();
			final long durationSave = finishSave - startSave;
			logDuration(SOURCE, METHOD, "save", durationSave);

			final long startFlush2 = System.currentTimeMillis();
			em.flush();
			em.clear();
			final long finishFlush2 = System.currentTimeMillis();
			final long durationFlush2 = finishFlush2 - startFlush2;
			logDuration(SOURCE, METHOD, "final flush", durationFlush2);

			translations = findTranslationImpls(job);
			retVal = ImmutableRecordIdTranslatorImpl.createTranslator(job,
					dataType, translations, keepFiles);
			logger.fine("Reflective impl: returning new translator: " + retVal);

		} else {
			logger.finest(
					"Using default persistence for record-id translations");
			final RECORD_ID_TYPE dataType = impl.getRecordIdType();

			// Prepare for batch insert
			final long startFlush1 = System.currentTimeMillis();
			em.flush();
			em.clear();
			final long finishFlush1 = System.currentTimeMillis();
			final long durationFlush1 = finishFlush1 - startFlush1;
			logDuration(SOURCE, METHOD, "initial flush", durationFlush1);

			final long startSave = System.currentTimeMillis();
			switch (dataType) {
			case TYPE_INTEGER:
				saveIntegerTranslations(job, impl, batchSize);
				logger.fine("Saved " + job + " / " + impl);
				break;
			case TYPE_LONG:
				saveLongTranslations(job, impl, batchSize);
				logger.fine("Saved " + job + " / " + impl);
				break;
			case TYPE_STRING:
				saveStringTranslations(job, impl, batchSize);
				logger.fine("Saved " + job + " / " + impl);
				break;
			default:
				throw new Error("unexpected record source type: " + dataType);
			}
			final long finishSave = System.currentTimeMillis();
			final long durationSave = finishSave - startSave;
			logDuration(SOURCE, METHOD, "save", durationSave);

			final long startFlush2 = System.currentTimeMillis();
			em.flush();
			em.clear();
			final long finishFlush2 = System.currentTimeMillis();
			final long durationFlush2 = finishFlush2 - startFlush2;
			logDuration(SOURCE, METHOD, "final flush", durationFlush2);

			translations = findTranslationImpls(job);
			retVal = ImmutableRecordIdTranslatorImpl.createTranslator(job,
					dataType, translations, keepFiles);
			logger.fine("Default impl: returning new translator: " + retVal);

		}
		assert retVal != null;

		final long finishSaveTranslatorImpl = System.currentTimeMillis();
		final long durationSaveTranslatorImpl =
			finishSaveTranslatorImpl - startSaveTranslatorImpl;
		logDuration(SOURCE, METHOD, "method", durationSaveTranslatorImpl);

		return retVal;
	}

	/**
	 * Implements
	 * {@link RecordIdController#toImmutableTranslator(BatchJob, MutableRecordIdTranslator)
	 * save} for instances of {@link DefaultRecordIdTranslator}.
	 *
	 * @throws ClassCastException
	 *             if the specified translator is not an instance of
	 *             <code>MutableRecordIdTranslatorImpl</code>
	 * @throws BlockingException
	 *             if an immutable translator can not be created
	 */
	@Override
	public <T extends Comparable<T>> ImmutableRecordIdTranslator<T> toImmutableTranslator(
			MutableRecordIdTranslator<T> translator) throws BlockingException {
		if (translator == null) {
			throw new IllegalArgumentException("null translator");
		}
		// HACK FIXME and update the Javadoc to comply with the interface
		DefaultRecordIdTranslator impl = (DefaultRecordIdTranslator) translator;
		// END HACK
		@SuppressWarnings("unchecked")
		ImmutableRecordIdTranslator<T> retVal = toImmutableTranslatorImpl(impl);
		assert impl.isClosed();
		assert retVal != null;
		return retVal;
	}

	protected ImmutableRecordIdTranslatorImpl toImmutableTranslatorImpl(
			DefaultRecordIdTranslator mrit) throws BlockingException {

		logger.entering("toImmutableTranslatorImpl", mrit.toString());

		final BatchJob job = mrit.getBatchJob();
		assert job != null && job.isPersistent();

		ImmutableRecordIdTranslatorImpl retVal = null;
		if (mrit.isClosed() && !mrit.doTranslatorCachesExist()) {
			logger.finer("finding immutable translator");
			retVal = findTranslatorImpl(job);
			logger.finer("found immutable translator: " + retVal);

		} else {
			// In this branch, translators caches must exist, either because
			// the mutable translator is still open, or because the cache have
			// not been otherwise removed.
			assert !mrit.isClosed() || mrit.doTranslatorCachesExist();
			if (!mrit.isClosed()) {
				assert mrit.doTranslatorCachesExist();
			}
			logger.fine("constructing immutable translator");
			mrit.close();
			final BatchJob j = mrit.getBatchJob();
			final IRecordIdSource<?> s1 =
				mrit.getFactory().getSource(mrit.getSink1());
			final IRecordIdSource<?> s2 =
				mrit.getFactory().getSource(mrit.getSink2());

			// Translator caches are removed by the constructor
			// ImmutableRecordIdTranslatorImpl
			retVal = new ImmutableRecordIdTranslatorImpl(j, s1, s2, keepFiles);
			assert !mrit.doTranslatorCachesExist() || keepFiles;
			logger.fine("constructed immutable translator");

			// Save the immutable translator to persistent storage
			this.saveTranslatorImpl(job, retVal);
		}
		assert retVal != null;
		assert mrit.isClosed();
		assert !mrit.doTranslatorCachesExist() || keepFiles;

		return retVal;
	}

}
