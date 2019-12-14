package com.choicemaker.cm.io.xml.base;

import java.io.IOException;
import java.io.Writer;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.util.Precondition;

/**
 * Writes marked record pairs to a specified Writer, not necessarily a
 * FileWriter.
 * 
 * @author rphall
 *
 */
public class XmlMarkedRecordPairSink2 extends XmlMarkedRecordPairSink {

	public XmlMarkedRecordPairSink2(Writer w, ImmutableProbabilityModel model) {
		super(model);
		Precondition.assertNonNullArgument("writer must be non-null", w);
		this.setWriter(w);
	}

	@Override
	public void close() throws IOException, XmlDiagnosticException {
		finishRootEntity();
		getWriter().flush();
	}

	@Override
	public void open() throws IOException {
		setRecordOutputter(((XmlAccessor) getModel().getAccessor())
				.getXmlRecordOutputter());
		getWriter().write("<?xml version=\"1.0\" encoding=\"" + getEncoding()
				+ "\"?>" + Constants.LINE_SEPARATOR);
		startRootEntity();
		getWriter().flush();
	}

	@Override
	public void setRawXmlFileName(String fn) {
	}

}
