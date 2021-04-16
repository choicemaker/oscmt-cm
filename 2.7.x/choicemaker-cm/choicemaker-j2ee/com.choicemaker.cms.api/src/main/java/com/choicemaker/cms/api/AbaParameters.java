package com.choicemaker.cms.api;

import java.io.Serializable;

public interface AbaParameters extends Serializable {

	/** Default id value for non-persistent parameters */
	long NONPERSISTENT_PARAMETERS_ID = 0;

	/**
	 * The persistence identifier for an instance. If the value is
	 * {@link #NONPERSISTENT_PARAMETERS_ID}, then the parameters are not
	 * persistent.
	 */
	long getId();

	boolean isPersistent();

	/**
	 * The fully qualified name of a class that converts database access objects
	 * into server-side representations of database records, and vice versa.
	 */
	String getDatabaseAccessorName();

	/**
	 * The fully qualified name of a class the selects rows from database tables
	 * as specified by the record-layout schema of the probability model named
	 * by {@link #getModelConfigurationName()}
	 */
	String getDatabaseReaderName();

	/**
	 * The probability threshold above which a pair of records is scored as a
	 * MATCH decision.
	 */
	float getHighThreshold();

	/**
	 * The probability threshold below which a pair of records is scored as a
	 * DIFFER decision.
	 */
	float getLowThreshold();

	/** The plugin identifier of a matching model */
	String getModelConfigurationName();

	/**
	 * The name of a blocking configuration, defined by the model record-layout
	 * schema, that specifies which ChoiceMaker fields are used by the ABA and
	 * OABA matching services to retrieve record for matching from the reference
	 * database.
	 */
	String getQueryToReferenceBlockingConfiguration();

	/**
	 * The name of a mapping, defined by the model record-layout schema, that
	 * specifies the correspondence of ChoiceMaker fields to database columns.
	 */
	String getReferenceDatabaseConfiguration();

	/**
	 * The JNDI name of JDBC DataSource used to connect to the reference
	 * database
	 */
	String getReferenceDatasource();

	/**
	 * The name of a view that pulls record ids from the reference database.
	 * Currently, the view definition may be specified by
	 * {@link #getReferenceSelectionViewAsSQL()}.
	 */
	String getReferenceSelectionView();

	/**
	 * The definition of a SQL selection statement that pulls record ids from
	 * the reference database. The selection statement must have the form:
	 * 
	 * <pre>
	 * SELECT {column} AS ID FROM {table} WHERE {selection criteria}
	 * </pre>
	 * 
	 * This method is deprecated, and will be replaced by
	 * {@link #getReferenceSelectionView()} in future releases.
	 * 
	 * @deprecated
	 */
	@Deprecated
	String getReferenceSelectionViewAsSQL();

}
