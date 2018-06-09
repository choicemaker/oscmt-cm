// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.cm.custom.mci.gend.internal.MciRecords;
import com.choicemaker.cm.core.*;
import com.choicemaker.cm.core.base.*;
import java.util.logging.*;
import com.choicemaker.util.StringUtils;
import com.choicemaker.cm.custom.mci.encryption.*;
import com.choicemaker.cm.custom.mci.matching.*;
import com.choicemaker.cm.custom.mci.nameparser.*;
import com.choicemaker.cm.custom.mci.validation.*;
import com.choicemaker.cm.matching.en.*;
import com.choicemaker.cm.matching.en.us.*;
import com.choicemaker.cm.matching.gen.*;
import com.choicemaker.cm.validation.eclipse.*;
import com.choicemaker.cm.validation.eclipse.impl.*;
import java.util.*;
import com.choicemaker.cm.custom.mci.gend.MciRecords.*;
public class ContactsImpl implements BaseRecord, Contacts {
private static Logger logger = Logger.getLogger(com.choicemaker.cm.custom.mci.gend.internal.MciRecords.ContactsImpl.class.getName());
public static ContactsImpl[] __zeroArray = new ContactsImpl[0];
public PatientImpl outer;
public PatientBase getOuter() {
return outer;
}
public void setOuter(PatientBase outer) {
this.outer = (PatientImpl)outer;
}
public boolean __v_relationship_cd;
public boolean __v_last_name;
public boolean __v_first_name;
public boolean __v_parsedContactName;
public boolean __v_nameF;
public boolean __v_nameM;
public boolean __v_nameL;
public boolean __v_nameMaidn;
public boolean __v_nameT;
public boolean __v_sex_cd;
public boolean __v_phone_no_h;
public boolean __v_clean_phone_no_h;
public boolean __v_phone_no_w;
public boolean __v_clean_phone_no_w;
public String relationship_cd;
public boolean isRelationship_cdValid() {
return __v_relationship_cd;
}
public String getRelationship_cd() {
return relationship_cd;
}
public String last_name;
public boolean isLast_nameValid() {
return __v_last_name;
}
public String getLast_name() {
return last_name;
}
public String first_name;
public boolean isFirst_nameValid() {
return __v_first_name;
}
public String getFirst_name() {
return first_name;
}
public NameParser parsedContactName;
public boolean isParsedContactNameValid() {
return __v_parsedContactName;
}
public NameParser getParsedContactName() {
return parsedContactName;
}
public String nameF;
public boolean isNameFValid() {
return __v_nameF;
}
public String getNameF() {
return nameF;
}
public String nameM;
public boolean isNameMValid() {
return __v_nameM;
}
public String getNameM() {
return nameM;
}
public String nameL;
public boolean isNameLValid() {
return __v_nameL;
}
public String getNameL() {
return nameL;
}
public String nameMaidn;
public boolean isNameMaidnValid() {
return __v_nameMaidn;
}
public String getNameMaidn() {
return nameMaidn;
}
public String nameT;
public boolean isNameTValid() {
return __v_nameT;
}
public String getNameT() {
return nameT;
}
public char sex_cd;
public boolean isSex_cdValid() {
return __v_sex_cd;
}
public char getSex_cd() {
return sex_cd;
}
public String phone_no_h;
public boolean isPhone_no_hValid() {
return __v_phone_no_h;
}
public String getPhone_no_h() {
return phone_no_h;
}
public String clean_phone_no_h;
public boolean isClean_phone_no_hValid() {
return __v_clean_phone_no_h;
}
public String getClean_phone_no_h() {
return clean_phone_no_h;
}
public String phone_no_w;
public boolean isPhone_no_wValid() {
return __v_phone_no_w;
}
public String getPhone_no_w() {
return phone_no_w;
}
public String clean_phone_no_w;
public boolean isClean_phone_no_wValid() {
return __v_clean_phone_no_w;
}
public String getClean_phone_no_w() {
return clean_phone_no_w;
}
public ContactsImpl(ContactsBase __o) {
relationship_cd = __o.getRelationship_cd();
last_name = __o.getLast_name();
first_name = __o.getFirst_name();
sex_cd = __o.getSex_cd();
phone_no_h = __o.getPhone_no_h();
phone_no_w = __o.getPhone_no_w();
}
public ContactsImpl() {
}
public void computeValidityAndDerived(DerivedSource __src) {
java.lang.String __tmpStr;
try {
__v_relationship_cd = StringUtils.nonEmptyString(relationship_cd) && !relationship_cd.equals("21");
__v_last_name = Validators.isValid("mciRawContactLastNameValidator",last_name);
__v_first_name = Validators.isValid("mciRawContactFirstNameValidator",first_name);
if(__src1.includes(__src)) {
if(__v_last_name || __v_first_name) {
parsedContactName = new NameParser(first_name, "", last_name);
__v_parsedContactName = parsedContactName != null;
}
} else {
__v_parsedContactName = parsedContactName != null;
}
if(__src1.includes(__src)) {
if(__v_parsedContactName) {
nameF = parsedContactName.getFirstName();
__v_nameF = StringUtils.nonEmptyString(nameF);
}
} else {
__v_nameF = StringUtils.nonEmptyString(nameF);
}
if(__src1.includes(__src)) {
if(__v_parsedContactName) {
nameM = parsedContactName.getMiddleNames();
__v_nameM = StringUtils.nonEmptyString(nameM);
}
} else {
__v_nameM = StringUtils.nonEmptyString(nameM);
}
if(__src1.includes(__src)) {
if(__v_parsedContactName) {
nameL = parsedContactName.getLastName();
__v_nameL = StringUtils.nonEmptyString(nameL);
}
} else {
__v_nameL = StringUtils.nonEmptyString(nameL);
}
if(__src1.includes(__src)) {
if(__v_parsedContactName) {
nameMaidn = parsedContactName.getPotentialMaidenName();
__v_nameMaidn = StringUtils.nonEmptyString(nameMaidn);
}
} else {
__v_nameMaidn = StringUtils.nonEmptyString(nameMaidn);
}
if(__src1.includes(__src)) {
if(__v_parsedContactName) {
nameT = parsedContactName.getTitles();
__v_nameT = StringUtils.nonEmptyString(nameT);
}
} else {
__v_nameT = StringUtils.nonEmptyString(nameT);
}
__v_sex_cd = sex_cd != '\0' && sex_cd != 'U';
__v_phone_no_h = StringUtils.nonEmptyString(phone_no_h) && !phone_no_h.endsWith("000");
if(__src1.includes(__src)) {
if(__v_phone_no_h) {
clean_phone_no_h = PhoneUtils.clean(phone_no_h);
__v_clean_phone_no_h = StringUtils.nonEmptyString(clean_phone_no_h);
}
} else {
__v_clean_phone_no_h = StringUtils.nonEmptyString(clean_phone_no_h);
}
__v_phone_no_w = StringUtils.nonEmptyString(phone_no_w) && !phone_no_w.endsWith("000");
if(__src1.includes(__src)) {
if(__v_phone_no_w) {
clean_phone_no_w = PhoneUtils.clean(phone_no_w);
__v_clean_phone_no_w = StringUtils.nonEmptyString(clean_phone_no_w);
}
} else {
__v_clean_phone_no_w = StringUtils.nonEmptyString(clean_phone_no_w);
}
} catch(Exception __ex) {
logger.severe("Computing validity and derived of ContactsImpl" + __ex);
}
}
public void resetValidityAndDerived(DerivedSource __src) {
if(__src1.includes(__src)) {
parsedContactName = null;
__v_parsedContactName = false;
}
if(__src1.includes(__src)) {
nameF = null;
__v_nameF = false;
}
if(__src1.includes(__src)) {
nameM = null;
__v_nameM = false;
}
if(__src1.includes(__src)) {
nameL = null;
__v_nameL = false;
}
if(__src1.includes(__src)) {
nameMaidn = null;
__v_nameMaidn = false;
}
if(__src1.includes(__src)) {
nameT = null;
__v_nameT = false;
}
if(__src1.includes(__src)) {
clean_phone_no_h = null;
__v_clean_phone_no_h = false;
}
if(__src1.includes(__src)) {
clean_phone_no_w = null;
__v_clean_phone_no_w = false;
}
}
public static ContactsImpl instance() {
ContactsImpl tmpInstance = new ContactsImpl();
return tmpInstance;
}
private static DerivedSource __src1 = DerivedSource.valueOf("all");
}
