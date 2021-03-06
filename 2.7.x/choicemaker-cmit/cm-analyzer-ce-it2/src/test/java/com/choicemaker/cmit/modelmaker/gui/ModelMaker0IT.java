/*******************************************************************************
 * Copyright (c) 2003, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cmit.modelmaker.gui;

import static com.choicemaker.cm.modelmaker.gui.ModelMaker.EXIT_OK;
import static com.choicemaker.cm.modelmaker.gui.ModelMaker.PLUGIN_APPLICATION_ID;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.e2.CMPlatformRunnable;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.e2.platform.InstallablePlatform;

public class ModelMaker0IT {

	private static final Logger logger = Logger.getLogger(ModelMaker0IT.class
			.getName());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		EmbeddedPlatform.install();
	}

	private ModelMaker modelMaker;

	@Before
	public void setUp() throws Exception {

		logger.info("Starting setUp()");

		// Instantiate ModelMaker
		CMPlatformRunnable cmpr =
			InstallablePlatform.getInstance().loaderGetRunnable(
					PLUGIN_APPLICATION_ID);
		logger.info("InstallablePlatform.loaderGetRunnable(): " + cmpr);
		assertTrue(cmpr instanceof ModelMaker);
		this.modelMaker = (ModelMaker) cmpr;

		// Prepare, but do not display, the ModelMaker GUI
		String[] args1 = ModelMakerUtils.getModelMakerRunArgs();
		this.modelMaker.startup(args1);
		logger.info("ModelMaker GUI prepared (but not displayed)");

		logger.info("setUp() complete");
	}

	@After
	public void tearDown() throws Exception {
		logger.info("Starting tearDown()");
		if (modelMaker != null) {
			modelMaker.programExit(EXIT_OK);
		}
		logger.info("tearDown() complete");
	}

	@Test
	public void testModelMakerIsReady() throws Exception {
		logger.info("testModelMakerIsReady");
		logger.info("starting test");
		assertTrue(this.modelMaker != null);
		boolean retVal = modelMaker.isReady();
		assertTrue(retVal);
		logger.info("test completed");
	}

}
