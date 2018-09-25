/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cms.ejb;

import static com.choicemaker.cms.ejb.UrmJobJPA.DISCRIMINATOR_VALUE;
import static com.choicemaker.cms.ejb.UrmJobJPA.JPQL_URM_FIND_ALL;
import static com.choicemaker.cms.ejb.UrmJobJPA.JPQL_URM_FIND_ALL_BY_URM_ID;
import static com.choicemaker.cms.ejb.UrmJobJPA.QN_URM_FIND_ALL;
import static com.choicemaker.cms.ejb.UrmJobJPA.QN_URM_FIND_ALL_BY_URM_ID;

import java.io.File;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobRigor;
import com.choicemaker.cm.batch.ejb.BatchJobEntity;

/**
 * A UrmJobEntity is a type of BatchJob that tracks the progress of a
 * (long-running) transitivity analysis process. Transitivity jobs use the match
 * results of an OABA BatchJob, so a transitivity job is always associated with
 * exactly one OABA BatchJob. The id of the OABA BatchJob is tracked by the
 * value of the {@link #getBatchParentId() transaction parent id} field.
 *
 * @author pcheung (original version)
 * @author rphall (migrated to JPA 2.0)
 */
@NamedQueries({
		@NamedQuery(name = QN_URM_FIND_ALL, query = JPQL_URM_FIND_ALL),
		@NamedQuery(name = QN_URM_FIND_ALL_BY_URM_ID,
				query = JPQL_URM_FIND_ALL_BY_URM_ID) })
@Entity
@DiscriminatorValue(value = DISCRIMINATOR_VALUE)
public class UrmJobEntity extends BatchJobEntity /*implements IControl*/ {

	private static final long serialVersionUID = 271L;

	/** Required by JPA; do not invoke directly */
	protected UrmJobEntity() {
		super();
	}

	// -- Constructors

	public UrmJobEntity(String externalId) {
		this(DISCRIMINATOR_VALUE, PersistentObject.NONPERSISTENT_ID, PersistentObject.NONPERSISTENT_ID, PersistentObject.NONPERSISTENT_ID,
				externalId, randomTransactionId(), PersistentObject.NONPERSISTENT_ID,
				PersistentObject.NONPERSISTENT_ID, BatchJob.DEFAULT_RIGOR);
	}

	public UrmJobEntity(BatchJob o) {
		this(DISCRIMINATOR_VALUE, o.getParametersId(), o.getSettingsId(), o
				.getServerId(), o.getExternalId(), o.getTransactionId(), o
				.getBatchParentId(), o.getUrmId(), o.getBatchJobRigor());
		this.workingDirectory = o.getWorkingDirectory().getAbsolutePath();
	}

	protected UrmJobEntity(String type, long paramsId, long settingsId,
			long serverId, String externalId, long tid, long parentId,
			long urmid, BatchJobRigor bjr) {
		super(type, paramsId, settingsId, serverId, externalId, tid, parentId,
				urmid, bjr);
	}

	void setWorkingDirectory(File workingDir) {
		final String wd = workingDir == null ? "null" : workingDir.toString();
		if (workingDir == null || !workingDir.exists()
				|| !workingDir.isDirectory()) {
			String msg =
				"Working directory '" + wd
						+ "' is null, does not exist, or is not a directory";
			throw new IllegalArgumentException(msg);
		}
		if (!workingDir.canRead() || !workingDir.canWrite()) {
			String msg =
				"Working directory '" + wd
						+ "' is not readable or not writeable";
			throw new IllegalArgumentException(msg);
		}
		this.workingDirectory = workingDir.getAbsolutePath();
	}

} // UrmJobEntity

