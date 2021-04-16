// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person;
import com.choicemaker.client.api.*;
import com.choicemaker.cm.core.*;
import com.choicemaker.cm.core.base.*;
import java.util.logging.*;
import java.util.Date;
import com.choicemaker.util.StringUtils;
import com.choicemaker.demo.simple_person_matching.gendata.gend.Person.*;
public class PersonImpl implements Record, Person {
private static Logger logger = Logger.getLogger(com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person.PersonImpl.class.getName());
private com.choicemaker.cm.core.DerivedSource __src;
public DerivedSource getDerivedSource() {
return __src;
}
public void computeValidityAndDerived() {
resetValidityAndDerived(__src);
computeValidityAndDerived(__src);
}
public Integer getId() {
return new Integer(recordId);
}
public boolean __v_linkage_role;
public boolean __v_entityId;
public boolean __v_recordId;
public boolean __v_ssn;
public boolean __v_firstName;
public boolean __v_middleName;
public boolean __v_lastName;
public boolean __v_streetNumber;
public boolean __v_streetName;
public boolean __v_apartmentNumber;
public boolean __v_city;
public boolean __v_state;
public boolean __v_zip;
public String linkage_role;
public boolean isLinkage_roleValid() {
return __v_linkage_role;
}
public String getLinkage_role() {
return linkage_role;
}
public int entityId;
public boolean isEntityIdValid() {
return __v_entityId;
}
public int getEntityId() {
return entityId;
}
public int recordId;
public boolean isRecordIdValid() {
return __v_recordId;
}
public int getRecordId() {
return recordId;
}
public String ssn;
public boolean isSsnValid() {
return __v_ssn;
}
public String getSsn() {
return ssn;
}
public String firstName;
public boolean isFirstNameValid() {
return __v_firstName;
}
public String getFirstName() {
return firstName;
}
public String middleName;
public boolean isMiddleNameValid() {
return __v_middleName;
}
public String getMiddleName() {
return middleName;
}
public String lastName;
public boolean isLastNameValid() {
return __v_lastName;
}
public String getLastName() {
return lastName;
}
public String streetNumber;
public boolean isStreetNumberValid() {
return __v_streetNumber;
}
public String getStreetNumber() {
return streetNumber;
}
public String streetName;
public boolean isStreetNameValid() {
return __v_streetName;
}
public String getStreetName() {
return streetName;
}
public String apartmentNumber;
public boolean isApartmentNumberValid() {
return __v_apartmentNumber;
}
public String getApartmentNumber() {
return apartmentNumber;
}
public String city;
public boolean isCityValid() {
return __v_city;
}
public String getCity() {
return city;
}
public String state;
public boolean isStateValid() {
return __v_state;
}
public String getState() {
return state;
}
public String zip;
public boolean isZipValid() {
return __v_zip;
}
public String getZip() {
return zip;
}
public PersonImpl(PersonBase __o) {
linkage_role = __o.getLinkage_role();
entityId = __o.getEntityId();
recordId = __o.getRecordId();
ssn = __o.getSsn();
firstName = __o.getFirstName();
middleName = __o.getMiddleName();
lastName = __o.getLastName();
streetNumber = __o.getStreetNumber();
streetName = __o.getStreetName();
apartmentNumber = __o.getApartmentNumber();
city = __o.getCity();
state = __o.getState();
zip = __o.getZip();
}
public PersonImpl() {
}
public void computeValidityAndDerived(DerivedSource __src) {
java.lang.String __tmpStr;
this.__src = __src;
try {
__v_linkage_role = "M".equals(linkage_role) || "S".equals(linkage_role);
__v_entityId = true;
__v_recordId = true;
__v_ssn = StringUtils.nonEmptyString(ssn) && !(new Integer(0).equals(Integer.valueOf(ssn)));
__v_firstName = StringUtils.nonEmptyString(firstName);
__v_middleName = StringUtils.nonEmptyString(middleName);
__v_lastName = StringUtils.nonEmptyString(lastName);
__v_streetNumber = StringUtils.nonEmptyString(streetNumber);
__v_streetName = StringUtils.nonEmptyString(streetName);
__v_apartmentNumber = StringUtils.nonEmptyString(apartmentNumber);
__v_city = StringUtils.nonEmptyString(city);
__v_state = StringUtils.nonEmptyString(state);
__v_zip = StringUtils.nonEmptyString(zip);
} catch(Exception __ex) {
logger.severe("Computing validity and derived of PersonImpl" + __ex);
}
}
public void resetValidityAndDerived(DerivedSource __src) {
}
public static PersonImpl instance() {
PersonImpl tmpInstance = new PersonImpl();
return tmpInstance;
}
}
