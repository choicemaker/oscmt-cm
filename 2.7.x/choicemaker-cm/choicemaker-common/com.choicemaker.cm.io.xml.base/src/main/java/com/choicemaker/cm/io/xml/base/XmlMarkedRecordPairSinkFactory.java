/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import java.util.ArrayList;
import java.util.List;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.SinkFactory;
import com.choicemaker.cm.core.Source;

/**
 *
 * @author    Martin Buechi
 */

public class XmlMarkedRecordPairSinkFactory implements SinkFactory {
    private String fileNameBase;
    private String xmlFileName;
    private String extension;
    private ImmutableProbabilityModel model;
    private int num;
    private List sources;

    public XmlMarkedRecordPairSinkFactory(String fileNameBase, String xmlFileName, String extension, ImmutableProbabilityModel model) {
        this.fileNameBase = fileNameBase;
        this.xmlFileName = xmlFileName;
        this.extension = extension;
        this.model = model;
        this.sources = new ArrayList();
    }

    public Sink getSink() {
        String tName = fileNameBase + num + "." + Constants.MRPS_EXTENSION;
        String tFileName = xmlFileName + num + extension;
        ++num;
        sources.add(new XmlMarkedRecordPairSource(tName, tFileName, model));
        return new XmlMarkedRecordPairSink(tName, tFileName, model);
    }

    public Source[] getSources() {
        return (Source[]) sources.toArray(new Source[sources.size()]);
    }
}
