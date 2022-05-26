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
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.13 at 11:39:10 AM EDT 
//


package com.choicemaker.cm.base.ModelConfiguration;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.choicemaker.cm.base.ModelConfiguration package. 
 * <p>An ObjectFactory2 allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory2 {


    /**
     * Create a new ObjectFactory2 that can be used to create new instances of schema derived classes for package: com.choicemaker.cm.base.ModelConfiguration
     * 
     */
    public ObjectFactory2() {
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 }
     * 
     */
    public CmModelConfiguration2 createCmModelConfiguration2() {
        return new CmModelConfiguration2();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Properties }
     * 
     */
    public CmModelConfiguration2 .Properties createCmModelConfiguration2Properties() {
        return new CmModelConfiguration2 .Properties();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Matching }
     * 
     */
    public CmModelConfiguration2 .Matching createCmModelConfiguration2Matching() {
        return new CmModelConfiguration2 .Matching();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Datasources }
     * 
     */
    public CmModelConfiguration2 .Datasources createCmModelConfiguration2Datasources() {
        return new CmModelConfiguration2 .Datasources();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Model }
     * 
     */
    public CmModelConfiguration2 .Model createCmModelConfiguration2Model() {
        return new CmModelConfiguration2 .Model();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .DatabaseConfiguration }
     * 
     */
    public CmModelConfiguration2 .DatabaseConfiguration createCmModelConfiguration2DatabaseConfiguration() {
        return new CmModelConfiguration2 .DatabaseConfiguration();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .BlockingConfiguration }
     * 
     */
    public CmModelConfiguration2 .BlockingConfiguration createCmModelConfiguration2BlockingConfiguration() {
        return new CmModelConfiguration2 .BlockingConfiguration();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Transitivity }
     * 
     */
    public CmModelConfiguration2 .Transitivity createCmModelConfiguration2Transitivity() {
        return new CmModelConfiguration2 .Transitivity();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Properties.Property }
     * 
     */
    public CmModelConfiguration2 .Properties.Property createCmModelConfiguration2PropertiesProperty() {
        return new CmModelConfiguration2 .Properties.Property();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Matching.Thresholds }
     * 
     */
    public CmModelConfiguration2 .Matching.Thresholds createCmModelConfiguration2MatchingThresholds() {
        return new CmModelConfiguration2 .Matching.Thresholds();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Matching.SingleRecordMatching }
     * 
     */
    public CmModelConfiguration2 .Matching.SingleRecordMatching createCmModelConfiguration2MatchingSingleRecordMatching() {
        return new CmModelConfiguration2 .Matching.SingleRecordMatching();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Matching.BatchRecordMatching }
     * 
     */
    public CmModelConfiguration2 .Matching.BatchRecordMatching createCmModelConfiguration2MatchingBatchRecordMatching() {
        return new CmModelConfiguration2 .Matching.BatchRecordMatching();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Datasources.Staging }
     * 
     */
    public CmModelConfiguration2 .Datasources.Staging createCmModelConfiguration2DatasourcesStaging() {
        return new CmModelConfiguration2 .Datasources.Staging();
    }

    /**
     * Create an instance of {@link CmModelConfiguration2 .Datasources.Master }
     * 
     */
    public CmModelConfiguration2 .Datasources.Master createCmModelConfiguration2DatasourcesMaster() {
        return new CmModelConfiguration2 .Datasources.Master();
    }

}