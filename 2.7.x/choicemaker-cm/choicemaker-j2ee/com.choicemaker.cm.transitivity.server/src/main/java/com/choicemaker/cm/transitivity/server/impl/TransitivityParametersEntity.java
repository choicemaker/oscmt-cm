package com.choicemaker.cm.transitivity.server.impl;

import static com.choicemaker.cm.transitivity.server.impl.TransitivityParametersJPA.DV_TRANS;
import static com.choicemaker.cm.transitivity.server.impl.TransitivityParametersJPA.JPQL_TRANSPARAMETERS_FIND_ALL;
import static com.choicemaker.cm.transitivity.server.impl.TransitivityParametersJPA.QN_TRANSPARAMETERS_FIND_ALL;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.IGraphProperty;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaJob;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.AbstractParametersEntity;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersEntity;

@NamedQuery(name = QN_TRANSPARAMETERS_FIND_ALL,
		query = JPQL_TRANSPARAMETERS_FIND_ALL)
@Entity
@DiscriminatorValue(value = DV_TRANS)
public class TransitivityParametersEntity extends AbstractParametersEntity
		implements TransitivityParameters {

	private static final long serialVersionUID = 271L;

	public static boolean isPersistent(TransitivityParameters p) {
		return p != null && p.getId() != NONPERSISTENT_ID;
	}

	public static final String OABA_ONLY_DUMP_TAG = "TP/BP PRECEDESSOR";

	public static final String COMMON_DUMP_TAG = "TP/BP";

	public static final String TRANS_ONLY_DUMP_TAG = "TP";

	/**
	 * Dumps the transitivity parameters and the parameters of an associated
	 * OABA job.
	 * 
	 * @param tp
	 *            must be non-null
	 * @param oabaJob
	 * @param predecessorParams
	 *            (may be null)
	 */
	public static String dump(TransitivityParameters tp, OabaJob oabaJob,
			OabaParameters predecessorParams) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		if (tp == null) {
			pw.println("null transitivity parameters");
		} else {
			pw.println("Transitivity parameters (TP)");
			pw.println("TP: Result format: " + tp.getAnalysisResultFormat());
			pw.println("TP: Graph property: " + tp.getGraphProperty());
			Long predecessorId = oabaJob == null ? null : oabaJob.getId();
			pw.println("TP: OABA predecessor: " + predecessorId);
			dumpDifferences(pw, tp, predecessorParams);
		}
		String retVal = sw.toString();
		return retVal;
	}

	protected static void dumpDifferences(PrintWriter pw,
			TransitivityParameters tp, OabaParameters predecessorParams) {
		assert pw != null;
		assert tp != null;
		final OabaParameters tpOaba = tp.asOabaParameters();
		if (predecessorParams != null) {
			if (predecessorParams.equals(tpOaba)) {
				String s = OabaParametersEntity.dump(COMMON_DUMP_TAG, tpOaba);
				pw.println(s);
			} else {
				String s =
					OabaParametersEntity.dump(OABA_ONLY_DUMP_TAG,
							predecessorParams);
				pw.println(s);
				OabaParametersEntity.dump(TRANS_ONLY_DUMP_TAG, tpOaba);
				pw.println(s);
			}
		} else {
			pw.println(TRANS_ONLY_DUMP_TAG + ": null OABA precedessor params");
			String s = OabaParametersEntity.dump(TRANS_ONLY_DUMP_TAG, tpOaba);
			pw.println(s);
		}
	}

	protected TransitivityParametersEntity() {
		super();
	}

	public TransitivityParametersEntity(String modelConfigurationName,
			float differThreshold, float matchThreshold,
			PersistableRecordSource stage, PersistableRecordSource master) {
		this(modelConfigurationName, differThreshold, matchThreshold, stage,
				master, DEFAULT_RESULT_FORMAT, DEFAULT_GRAPH_PROPERTY_NAME);
	}

	public TransitivityParametersEntity(String modelConfigurationName,
			float differThreshold, float matchThreshold,
			PersistableRecordSource stage, PersistableRecordSource master,
			AnalysisResultFormat format, String graphPropertyName) {
		super(DV_TRANS, modelConfigurationName, differThreshold,
				matchThreshold, stage.getId(), stage.getType(),
				master == null ? null : master.getId(), master == null ? null
						: master.getType(),
				OabaLinkageType.TRANSITIVITY_ANALYSIS, format == null ? null
						: format.name(), graphPropertyName);
		if (format == null) {
			throw new IllegalArgumentException("null analysis-result format");
		}
		if (graphPropertyName == null) {
			throw new IllegalArgumentException("null graph-property name");
		}
	}

	public TransitivityParametersEntity(TransitivityParameters tp) {
		super(DV_TRANS, tp.getModelConfigurationName(), tp.getLowThreshold(),
				tp.getHighThreshold(), tp.getStageRsId(), tp.getStageRsType(),
				tp.getMasterRsId(), tp.getMasterRsType(),
				OabaLinkageType.TRANSITIVITY_ANALYSIS, tp
						.getAnalysisResultFormat().name(), tp
						.getGraphProperty().getName());
		if (tp.getGraphProperty() == null) {
			throw new IllegalArgumentException("null graph-property");
		}
	}

	// HACK FIXME REMOVEME
	public TransitivityParametersEntity(long persistenceId, TransitivityParameters tp) {
		super(persistenceId, DV_TRANS, tp.getModelConfigurationName(), tp.getLowThreshold(),
				tp.getHighThreshold(), tp.getStageRsId(), tp.getStageRsType(),
				tp.getMasterRsId(), tp.getMasterRsType(),
				OabaLinkageType.TRANSITIVITY_ANALYSIS, tp
						.getAnalysisResultFormat().name(), tp
						.getGraphProperty().getName());
		if (tp.getGraphProperty() == null) {
			throw new IllegalArgumentException("null graph-property");
		}
	}
	// HACK FIXME REMOVEME

	public TransitivityParametersEntity(OabaParameters p,
			AnalysisResultFormat format, String graphPropertyName) {
		super(DV_TRANS, p.getModelConfigurationName(), p.getLowThreshold(), p
				.getHighThreshold(), p.getStageRsId(), p.getStageRsType(), p
				.getMasterRsId(), p.getMasterRsType(),
				OabaLinkageType.TRANSITIVITY_ANALYSIS, format.name(),
				graphPropertyName);
		if (graphPropertyName == null) {
			throw new IllegalArgumentException("null graph-property");
		}
	}

	@Override
	public OabaParameters asOabaParameters() {
		return new OabaParametersEntity(getModelConfigurationName(),
				getLowThreshold(), getHighThreshold(), getStageRsId(),
				getStageRsType(), getMasterRsId(), getMasterRsType(),
				OabaLinkageType.TRANSITIVITY_ANALYSIS);
	}

	@Override
	public String getModelConfigurationName() {
		return this.modelConfigName;
	}

	@Override
	public long getStageRsId() {
		return this.stageRsId;
	}

	@Override
	public String getStageRsType() {
		return this.stageRsType;
	}

	@Override
	public Long getMasterRsId() {
		return this.masterRsId;
	}

	@Override
	public String getMasterRsType() {
		return this.masterRsType;
	}

	@Override
	public AnalysisResultFormat getAnalysisResultFormat() {
		return AnalysisResultFormat.valueOf(format);
	}

	@Override
	public IGraphProperty getGraphProperty() {
		return new IGraphProperty() {

			private static final long serialVersionUID = 271L;

			@Override
			public String getName() {
				return graph;
			}

			@Override
			public String toString() {
				return getName();
			}

		};
	}

	@Override
	public String toString() {
		return "TransitivityParametersEntity [id=" + id + ", modelId="
				+ modelConfigName + ", lowThreshold=" + lowThreshold
				+ ", highThreshold=" + highThreshold + ", graph=" + graph + "]";
	}

}
