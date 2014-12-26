package com.choicemaker.cmit.oaba.util;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.core.base.Thresholds;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaJob;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.ServerConfigurationController;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersControllerBean;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersEntity;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.ServerConfigurationControllerBean;
import com.choicemaker.cmit.OabaTestController;
import com.choicemaker.cmit.utils.EntityManagerUtils;
import com.choicemaker.cmit.utils.TestEntities;

/**
 * An EJB used to test TransitivityJob beans within container-defined
 * transactions.
 * 
 * @author rphall
 */
@Stateless
public class OabaTestControllerBean implements OabaTestController {

	private static final String DEFAULT_MODEL_NAME = "FakeModelConfig";
	private static final String UNDERSCORE = "_";

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaParametersControllerBean paramsController;

	@EJB
	private ServerConfigurationController serverController;

	/**
	 * Synthesizes the name of a fake modelId configuration using the specified
	 * tag which may be null
	 */
	@Override
	public String createRandomModelConfigurationName(String tag) {
		if (tag == null) {
			tag = DEFAULT_MODEL_NAME;
		}
		tag = tag.trim();
		if (tag.isEmpty()) {
			tag = DEFAULT_MODEL_NAME;
		}
		StringBuilder sb = new StringBuilder(tag);
		if (!tag.endsWith(UNDERSCORE)) {
			sb.append(UNDERSCORE);
		}
		sb.append(UUID.randomUUID().toString());
		String retVal = sb.toString();
		return retVal;
	}

	@Override
	public Thresholds createRandomThresholds() {
		Random random = new Random(new Date().getTime());
		float low = random.nextFloat();
		float highRange = 1.0f - low;
		float f = random.nextFloat();
		float high = low + f * highRange;
		Thresholds retVal = new Thresholds(low, high);
		return retVal;
	}

	@Override
	public OabaParametersEntity createBatchParameters(String tag,
			TestEntities te) {
		if (te == null) {
			throw new IllegalArgumentException("null test entities");
		}
		Thresholds thresholds = createRandomThresholds();
		PersistableRecordSource stage =
			EntityManagerUtils.createFakePersistableRecordSource(tag);
		OabaLinkageType task = EntityManagerUtils.createRandomOabaTask();
		PersistableRecordSource master =
			EntityManagerUtils.createFakePersistableRecordSource(tag, task);
		OabaParametersEntity retVal =
			new OabaParametersEntity(createRandomModelConfigurationName(tag),
					thresholds.getDifferThreshold(),
					thresholds.getMatchThreshold(), stage, master, task);
		paramsController.save(retVal);
		te.add(retVal);
		return retVal;
	}

	@Override
	public ServerConfiguration getDefaultServerConfiguration() {
		String hostName = ServerConfigurationControllerBean.computeHostName();
		final boolean computeFallback = true;
		ServerConfiguration retVal =
			serverController.getDefaultConfiguration(hostName, computeFallback);
		assert retVal != null;
		assert retVal.getId() != ServerConfigurationControllerBean.INVALID_ID;
		return retVal;
	}

//	public OabaParameters createPersistentOabaParameters(String tag,
//			TestEntities te) {
//		if (te == null) {
//			throw new IllegalArgumentException("null test entities");
//		}
//		return EntityManagerUtils.createPersistentOabaParameters(em, tag, te);
//	}
//
//	/**
//	 * An externalId for the returned OabaJob is synthesized using the specified
//	 * tag
//	 */
//	public OabaJob createPersistentOabaJobBean(String tag, TestEntities te) {
//		return createPersistentOabaJobBean(te,
//				EntityManagerUtils.createExternalId(tag));
//	}
//
//	/**
//	 * The specified externalId is assigned without alteration to the returned
//	 * OabaJob
//	 */
//	public OabaJob createPersistentOabaJobBean(TestEntities te, String extId) {
//		ServerConfiguration sc = getDefaultServerConfiguration();
//		return createPersistentOabaJobBean(sc, em, te, extId);
//	}
//
//	/**
//	 * Creates a persistent instance of OabaParametersEntity. An externalId for
//	 * the returned OabaJob is synthesized using the specified tag.
//	 */
//	public static OabaJobEntity createPersistentOabaJobBean(
//			ServerConfiguration sc, EntityManager em, String tag,
//			TestEntities te) {
//		return createPersistentOabaJobBean(sc, em, te, EntityManagerUtils.createExternalId(tag));
//	}
//
//	/**
//	 * Creates a persistent instance of OabaParametersEntity. The specified
//	 * externalId is assigned without alteration to the returned OabaJob.
//	 */
//	public static OabaJobEntity createPersistentOabaJobBean(
//			ServerConfiguration sc, EntityManager em, TestEntities te,
//			String extId) {
//		if (te == null) {
//			throw new IllegalArgumentException("null test entities");
//		}
//		OabaParametersEntity params = EntityManagerUtils.
//			createPersistentOabaParameters(em, null, te);
//		OabaSettingsEntity settings = EntityManagerUtils.
//			createPersistentOabaSettings(em, null, te);
//		OabaJobEntity retVal = new OabaJobEntity(params, settings, sc, extId);
//		em.persist(retVal);
//		te.add(retVal);
//		return retVal;
//	}

	@Override
	public void removeTestEntities(TestEntities te) {
		EntityManagerUtils.removeTestEntities(em, te);
	}

	@Override
	public List<OabaParameters> findAllOabaParameters() {
		return EntityManagerUtils.findAllOabaParameters(em);
	}

	@Override
	public List<OabaJob> findAllOabaJobs() {
		return EntityManagerUtils.findAllOabaJobs(em);
	}

	@Override
	public List<OabaProcessing> findAllOabaProcessing() {
		return EntityManagerUtils.findAllOabaProcessing(em);
	}

}
