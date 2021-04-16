package com.choicemaker.client.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * These are names of GraphProperty plugins that are guaranteed to be present in
 * any installation of ChoiceMaker.
 */
public interface WellKnownGraphProperties {

	/**
	 * Graph Property Name: Simply connected by MATCH relationships.
	 */
	String GPN_SCM = "CM";

	/**
	 * Graph Property Name: Biconnected by MATCH relationships.
	 */
	String GPN_BCM = "BCM";

	/**
	 * Graph Property Name: Fully connected by MATCH relationships.
	 */
	String GPN_FCM = "FCM";

	/** List of well known graph property names */
	List<String> GPN_NAMES =
		Collections.unmodifiableList(Arrays.asList(new String[] {
				GPN_SCM, GPN_BCM, GPN_FCM }));

	/**
	 * Graph Property: Simply connected by MATCH relationships.
	 */
	IGraphProperty GP_SCM = new GraphPropertyBean(GPN_SCM);

	/**
	 * Graph Property: Biconnected by MATCH relationships.
	 */
	IGraphProperty GP_BCM = new GraphPropertyBean(GPN_BCM);

	/**
	 * Graph Property: Fully connected by MATCH relationships.
	 */
	IGraphProperty GP_FCM = new GraphPropertyBean(GPN_FCM);

	/** List of well known graph properties */
	List<IGraphProperty> GPN_INSTANCES =
		Collections.unmodifiableList(Arrays.asList(new IGraphProperty[] {
				GP_SCM, GP_BCM, GP_FCM }));

}
