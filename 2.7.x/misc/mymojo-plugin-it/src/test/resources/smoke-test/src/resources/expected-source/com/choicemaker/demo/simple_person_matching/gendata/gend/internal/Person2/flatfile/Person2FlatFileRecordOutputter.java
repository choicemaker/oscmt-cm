// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person2.flatfile;
import org.apache.log4j.*;
import java.util.*;
import java.io.*;
import com.choicemaker.cm.core.*;
import com.choicemaker.cm.core.base.*;
import com.choicemaker.cm.io.flatfile.base.*;
import com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person2.*;
import java.util.Date;
import com.choicemaker.util.StringUtils;
import com.choicemaker.cm.validation.eclipse.impl.Validators;
public final class Person2FlatFileRecordOutputter implements FlatFileRecordOutputter {
private static Logger logger = Logger.getLogger(com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person2.flatfile.Person2FlatFileRecordOutputter.class);
private boolean multiFile;
private boolean singleLine;
private boolean fixedLength;
private char sep;
private boolean tagged;
private int tagWidth;
private boolean filter;
private char[] lineBuffer = new char[16384];
public Person2FlatFileRecordOutputter(boolean multiFile, boolean singleLine, boolean fixedLength, char sep, boolean tagged, int tagWidth, boolean filter) {
this.multiFile = multiFile;
this.singleLine = singleLine;
this.fixedLength = fixedLength;
this.sep = sep;
this.tagged = tagged;
this.tagWidth = tagWidth;
this.filter = filter;
}
public void put(Writer[] ws, Record r) throws IOException {
put_PersonImpl(ws, (PersonImpl)r);
}
private void put_PersonImpl(Writer[] ws, PersonImpl r) throws IOException {
Writer w = ws[1];
if(tagged) {
FlatFileOutput.write(w, "0", fixedLength, sep, filter, !singleLine, tagWidth);
}
FlatFileOutput.write(w, r.linkage_role, fixedLength, sep, filter, !tagged && !singleLine,32);
FlatFileOutput.write(w, r.entityId, fixedLength, sep, filter, false,11);
FlatFileOutput.write(w, r.recordId, fixedLength, sep, filter, false,11);
FlatFileOutput.write(w, r.ssn, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.firstName, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.middleName, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.lastName, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.streetNumber, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.streetName, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.apartmentNumber, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.city, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.state, fixedLength, sep, filter, false,32);
FlatFileOutput.write(w, r.zip, fixedLength, sep, filter, false,32);
if(!singleLine) w.write("\n");
}
}
