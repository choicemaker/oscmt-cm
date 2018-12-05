package com.choicemaker.cm.batch.ejb;

import static com.choicemaker.cm.batch.ejb.IndexedPropertyJPA.*;

import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.IndexedProperty;

@NamedQueries({
		@NamedQuery(name = QN_IDXPROP_FINDALL, query = JPQL_IDXPROP_FINDALL),
		@NamedQuery(name = QN_IDXPROP_FIND_BY_JOB_PNAME,
				query = JPQL_IDXPROP_FIND_BY_JOB_PNAME),
		@NamedQuery(name = QN_IDXPROP_FINDALL_BY_JOB,
				query = JPQL_IDXPROP_FINDALL_BY_JOB),
		@NamedQuery(name = QN_IDXPROP_DELETE_BY_JOB,
				query = JPQL_IDXPROP_DELETE_BY_JOB) })
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
public class IndexedPropertyEntity extends AbstractPersistentObject
		implements IndexedProperty {

	private static final long serialVersionUID = 1L;

	private static final Logger logger =
		Logger.getLogger(IndexedPropertyEntity.class.getName());

	public static final String INVALID_NAME = null;
	public static final String INVALID_VALUE = null;

	// -- Instance data

	@Id
	@Column(name = CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	private long id;

	@Column(name = CN_JOB_ID)
	private final long jobId;

	@Column(name = CN_NAME)
	private final String name;

	@Column(name = CN_VALUE)
	private String value;

	// -- Constructors

	protected IndexedPropertyEntity() {
		this.id = PersistentObject.NONPERSISTENT_ID;
		this.jobId = PersistentObject.NONPERSISTENT_ID;
		this.name = INVALID_NAME;
		this.value = INVALID_VALUE;
	}

	public IndexedPropertyEntity(BatchJob job, final String pn,
			final String pv) {
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid job: " + job);
		}
		if (pn == null || !pn.equals(pn.trim()) || pn.isEmpty()) {
			throw new IllegalArgumentException(
					"invalid property name: '" + pn + "'");
		}
		final String stdName = pn.toUpperCase();
		if (!pn.equals(stdName)) {
			logger.warning("Converting property name '" + pn
					+ "' to upper-case '" + stdName + "'");
		}

		this.jobId = job.getId();
		this.name = stdName;
		updateValue(pv);
	}

	// -- Modifiers

	@Override
	public void updateValue(String s) {
		if (s == null) {
			throw new IllegalArgumentException(
					"invalid property value: '" + s + "'");
		}
		if (s.trim().isEmpty()) {
			logger.warning("Blank value for '" + name + "'");
		}
		this.value = s;
	}

	// -- Accessors

	public IndexedPropertyEntity(IndexedProperty p) {
		this.id = p.getId();
		this.jobId = p.getJobId();
		this.name = p.getName();
		this.value = p.getValue();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public long getJobId() {
		return jobId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "IndexedPropertyEntity [id=" + getId() + ", jobId="
				+ getJobId() + ", name=" + getName() + ", value=" + getValue()
				+ "]";
	}

}
