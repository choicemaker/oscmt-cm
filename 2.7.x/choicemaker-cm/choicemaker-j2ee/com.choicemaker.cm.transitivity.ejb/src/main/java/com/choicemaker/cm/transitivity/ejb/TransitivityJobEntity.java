/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA.DISCRIMINATOR_VALUE;
import static com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA.JPQL_TRANSITIVITY_FIND_ALL;
import static com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA.JPQL_TRANSITIVITY_FIND_ALL_BY_PARENT_ID;
import static com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA.QN_TRANSITIVITY_FIND_ALL;
import static com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA.QN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID;

import java.io.File;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobRigor;
import com.choicemaker.cm.batch.ejb.BatchJobEntity;

/**
 * A TransitivityJobEntity is a type of BatchJob that tracks the progress of a
 * (long-running) transitivity analysis process. Transitivity jobs use the match
 * results of an OABA BatchJob, so a transitivity job is always associated with
 * exactly one OABA BatchJob. The id of the OABA BatchJob is tracked by the
 * value of the {@link #getBatchParentId() transaction parent id} field.
 *
 * @author pcheung (original version)
 * @author rphall (migrated to JPA 2.0)
 */
@NamedQueries({
		@NamedQuery(name = QN_TRANSITIVITY_FIND_ALL,
				query = JPQL_TRANSITIVITY_FIND_ALL),
		@NamedQuery(name = QN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID,
				query = JPQL_TRANSITIVITY_FIND_ALL_BY_PARENT_ID) })
@Entity
@DiscriminatorValue(value = DISCRIMINATOR_VALUE)
public class TransitivityJobEntity extends BatchJobEntity
/* implements IControl */ {

	private static final long serialVersionUID = 271L;

	/** Required by JPA; do not invoke directly */
	protected TransitivityJobEntity() {
		super();
	}

	// -- Constructors

	public TransitivityJobEntity(TransitivityParameters params,
			OabaSettings settings, ServerConfiguration sc, BatchJob parent,
			BatchJob urmJob, String externalId) {
		this(DISCRIMINATOR_VALUE, params.getId(), settings.getId(), sc.getId(),
				externalId, randomTransactionId(),
				parent.getId(), urmJob == null
						? PersistentObject.NONPERSISTENT_ID : urmJob.getId(),
				BatchJob.DEFAULT_RIGOR);
	}

	public TransitivityJobEntity(TransitivityParameters params,
			OabaSettings settings, ServerConfiguration sc, BatchJob parent,
			BatchJob urmJob, String externalId, BatchJobRigor bjr) {
		this(DISCRIMINATOR_VALUE, params.getId(), settings.getId(), sc.getId(),
				externalId, randomTransactionId(),
				parent.getId(), urmJob == null
						? PersistentObject.NONPERSISTENT_ID : urmJob.getId(),
				bjr);
	}

	public TransitivityJobEntity(BatchJob o) {
		this(DISCRIMINATOR_VALUE, o.getParametersId(), o.getSettingsId(),
				o.getServerId(), o.getExternalId(), o.getTransactionId(),
				o.getBatchParentId(), o.getUrmId(), o.getBatchJobRigor());
		this.workingDirectory = o.getWorkingDirectory().getAbsolutePath();
	}

	protected TransitivityJobEntity(String type, long paramsId, long settingsId,
			long serverId, String externalId, long tid, long parentId,
			long urmid, BatchJobRigor bjr) {
		super(type, paramsId, settingsId, serverId, externalId, tid, parentId,
				urmid, bjr);
	}

	void setWorkingDirectory(File workingDir) {
		final String wd = workingDir == null ? "null" : workingDir.toString();
		if (workingDir == null || !workingDir.exists()
				|| !workingDir.isDirectory()) {
			String msg = "Working directory '" + wd
					+ "' is null, does not exist, or is not a directory";
			throw new IllegalArgumentException(msg);
		}
		if (!workingDir.canRead() || !workingDir.canWrite()) {
			String msg = "Working directory '" + wd
					+ "' is not readable or not writeable";
			throw new IllegalArgumentException(msg);
		}
		this.workingDirectory = workingDir.getAbsolutePath();
	}

} // TransitivityJobEntity
