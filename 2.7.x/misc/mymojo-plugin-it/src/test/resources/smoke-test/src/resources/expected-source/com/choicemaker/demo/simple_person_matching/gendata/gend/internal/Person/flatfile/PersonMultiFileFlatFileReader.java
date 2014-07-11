// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person.flatfile;
import org.apache.log4j.*;
import java.util.*;
import java.io.*;
import com.choicemaker.cm.core.*;
import com.choicemaker.cm.core.base.*;
import com.choicemaker.cm.io.flatfile.base.*;
import com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person.*;
import java.util.Date;
import com.choicemaker.util.StringUtils;
public final class PersonMultiFileFlatFileReader implements FlatFileReader {
private static Logger logger = Logger.getLogger(com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person.flatfile.PersonMultiFileFlatFileReader.class);
private Tokenizer[] tokenizer;
private boolean tagged;
private static DerivedSource src = DerivedSource.valueOf("flatfile");
private PersonImpl o__PersonImpl;
public PersonMultiFileFlatFileReader(Tokenizer[] tokenizer, boolean tagged) {
this.tokenizer = tokenizer;
this.tagged = tagged;
}
public void open() throws IOException {
getRecordPersonImpl();
}
public Record getRecord() throws IOException {
Record __res = o__PersonImpl;
__res.computeValidityAndDerived(src);
getRecordPersonImpl();
return __res;
}
private void getRecordPersonImpl() throws IOException {
if(tokenizer[1].lineRead()) {
if(tagged && tokenizer[1].tag != "0") {
throw new IOException("Illegal tag: " + tokenizer[1].tag);
}
 o__PersonImpl = new PersonImpl();
o__PersonImpl.linkage_role = tokenizer[1].nextTrimedString(32);
o__PersonImpl.entityId = tokenizer[1].nextInt(11);
o__PersonImpl.recordId = tokenizer[1].nextInt(11);
o__PersonImpl.ssn = tokenizer[1].nextTrimedString(32);
o__PersonImpl.firstName = tokenizer[1].nextTrimedString(32);
o__PersonImpl.middleName = tokenizer[1].nextTrimedString(32);
o__PersonImpl.lastName = tokenizer[1].nextTrimedString(32);
o__PersonImpl.streetNumber = tokenizer[1].nextTrimedString(32);
o__PersonImpl.streetName = tokenizer[1].nextTrimedString(32);
o__PersonImpl.apartmentNumber = tokenizer[1].nextTrimedString(32);
o__PersonImpl.city = tokenizer[1].nextTrimedString(32);
o__PersonImpl.state = tokenizer[1].nextTrimedString(32);
o__PersonImpl.zip = tokenizer[1].nextTrimedString(32);
tokenizer[1].readLine();
} else {
o__PersonImpl = null;
}
}
}
