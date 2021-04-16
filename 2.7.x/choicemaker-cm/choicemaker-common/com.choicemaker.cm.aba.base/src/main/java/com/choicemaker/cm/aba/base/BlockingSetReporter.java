/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.base;

import java.util.Iterator;

import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.aba.IBlockingSet;
import com.choicemaker.cm.aba.IBlockingValue;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.report.ReporterPlugin;
import com.choicemaker.cm.core.util.XmlOutput;

/**
 * Description
 *
 * @author Martin Buechi
 */
public class BlockingSetReporter implements ReporterPlugin {
	private final AutomatedBlocker blocker;

	public BlockingSetReporter(AutomatedBlocker blocker) {
		this.blocker = blocker;
	}

	@Override
	public void report(StringBuffer b, boolean newLines) {
		b.append("<blocking>");
		if (newLines)
			b.append(Constants.LINE_SEPARATOR);
		Iterator<IBlockingSet> iBlockingSets =
			blocker.getBlockingSets().iterator();
		while (iBlockingSets.hasNext()) {
			IBlockingSet bs = iBlockingSets.next();
			b.append("<bs ec=\"").append(bs.getExpectedCount()).append("\">");
			if (newLines)
				b.append(Constants.LINE_SEPARATOR);
			int size = bs.numFields();
			for (int j = 0; j < size; ++j) {
				IBlockingValue bv = bs.getBlockingValue(j);
				b.append("<bv n=\"").append(bv.getBlockingField().getNumber());
				b.append("\" v=\"")
						.append(XmlOutput
								.escapeAttributeEntities(bv.getValue()))
						.append("\"/>");
				if (newLines)
					b.append(Constants.LINE_SEPARATOR);
			}
			b.append("</bs>");
			if (newLines)
				b.append(Constants.LINE_SEPARATOR);
		}
		b.append("</blocking>");
		if (newLines)
			b.append(Constants.LINE_SEPARATOR);
	}
}
