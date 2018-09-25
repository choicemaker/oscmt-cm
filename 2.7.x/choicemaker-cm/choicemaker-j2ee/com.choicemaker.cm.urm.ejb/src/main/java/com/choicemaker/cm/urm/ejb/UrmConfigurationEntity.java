package com.choicemaker.cm.urm.ejb;

import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.CN_CMS_CONF_NAME;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.CN_ID;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.CN_URM_CONF_NAME;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.ID_GENERATOR_VALUE_COLUMN_NAME;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.JPQL_URMCONFIG_FIND_ALL;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.JPQL_URMCONFIG_FIND_BY_NAME;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.QN_URMCONFIG_FIND_ALL;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.QN_URMCONFIG_FIND_BY_NAME;
import static com.choicemaker.cm.urm.ejb.UrmConfigurationJPA.TABLE_NAME;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.batch.ejb.AbstractPersistentObject;
import com.choicemaker.cm.urm.api.UrmConfiguration;

@NamedQueries({
		@NamedQuery(name = QN_URMCONFIG_FIND_ALL,
				query = JPQL_URMCONFIG_FIND_ALL),
		@NamedQuery(name = QN_URMCONFIG_FIND_BY_NAME,
				query = JPQL_URMCONFIG_FIND_BY_NAME) })
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
public class UrmConfigurationEntity extends AbstractPersistentObject
		implements Serializable, UrmConfiguration {

	private static final long serialVersionUID = 271L;

	// -- Basic persistence

	@Id
	@Column(name = CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	protected long configurationId = DEFAULT_CONFIGURATIONID;

	@Column(name = CN_URM_CONF_NAME, unique = true, nullable = false)
	protected String urmConfigurationName = DEFAULT_URMCONFIGURATIONNAME;

	@Column(name = CN_CMS_CONF_NAME, nullable = false)
	protected String namedConfigurationName = DEFAULT_CMSCONFIGURATIONNAME;

	public UrmConfigurationEntity() {
	}

	public UrmConfigurationEntity(UrmConfiguration nc) {
		this.setUrmConfigurationName(nc.getUrmConfigurationName());
		this.setNamedConfigurationName(nc.getCmsConfigurationName());
	}

	// -- Accessors

	@Override
	public long getId() {
		return configurationId;
	}

	@Override
	public String getUrmConfigurationName() {
		return urmConfigurationName;
	}

	@Override
	public String getCmsConfigurationName() {
		return namedConfigurationName;
	}

	// -- Manipulators

	public void setUrmConfigurationName(String name) {
		this.urmConfigurationName = name;
	}

	public void setNamedConfigurationName(String name) {
		this.namedConfigurationName = name;
	}

	// -- Identity

	public boolean equalsIgnorePersistenceId(UrmConfiguration uc) {
		if (this == uc) {
			return true;
		}
		if (uc == null) {
			return false;
		}
		if (getCmsConfigurationName() == null) {
			if (uc.getCmsConfigurationName() != null)
				return false;
		} else if (!getCmsConfigurationName().equals(uc.getCmsConfigurationName()))
			return false;
		if (getUrmConfigurationName() == null) {
			if (uc.getUrmConfigurationName() != null)
				return false;
		} else if (!getUrmConfigurationName().equals(uc.getUrmConfigurationName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UrmConfigurationEntity [UUID=" + getUUID()
				+ ", urmConfigurationName=" + urmConfigurationName
				+ ", namedConfigurationName=" + namedConfigurationName + ", id="
				+ getId() + "]";
	}

}
