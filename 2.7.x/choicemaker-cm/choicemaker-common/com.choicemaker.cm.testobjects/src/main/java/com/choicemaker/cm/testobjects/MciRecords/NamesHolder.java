// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.cm.custom.mci.gend.MciRecords;
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
/**
 * Generated holder class for the node type names. See package documentation for details.
 */
public class NamesHolder implements com.choicemaker.cm.custom.mci.gend.MciRecords.NamesBase, java.io.Serializable {
/** Default constructor. Initializes all all arrays for nested record to zero length arrays and all other values to their defaults (0/null). */public NamesHolder() {
}
/** Zero length array to be used by outer node class. */
public static final NamesHolder[] ZERO_ARRAY = new NamesHolder[0];
private PatientBase outer;
/**
 * Returns the outer node.
 * @return  The outer node.
*/
public PatientBase getOuter() {
return outer;
}
/**
 * Sets the outer node. This method should only be called by generated classes.
 * @param  outer  The outer node.
*/
public void setOuter(PatientBase outer) {
this.outer = outer;
}
private boolean first_nameValid;
protected String first_name;
/**
 * Returns whether the value of the field first_name is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field first_name is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isFirst_nameValid() {
return first_nameValid;
}
/**
 * Returns the value of first_name.
 * @return  The value of first_name.
*/
public String getFirst_name() {
return first_name;
}
/**
 * Sets the value of first_name.
 * @param  __v  The value of first_name.
*/
public void setFirst_name(String __v) {
this.first_name = __v;
}
private boolean middle_nameValid;
protected String middle_name;
/**
 * Returns whether the value of the field middle_name is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field middle_name is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isMiddle_nameValid() {
return middle_nameValid;
}
/**
 * Returns the value of middle_name.
 * @return  The value of middle_name.
*/
public String getMiddle_name() {
return middle_name;
}
/**
 * Sets the value of middle_name.
 * @param  __v  The value of middle_name.
*/
public void setMiddle_name(String __v) {
this.middle_name = __v;
}
private boolean last_nameValid;
protected String last_name;
/**
 * Returns whether the value of the field last_name is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field last_name is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isLast_nameValid() {
return last_nameValid;
}
/**
 * Returns the value of last_name.
 * @return  The value of last_name.
*/
public String getLast_name() {
return last_name;
}
/**
 * Sets the value of last_name.
 * @param  __v  The value of last_name.
*/
public void setLast_name(String __v) {
this.last_name = __v;
}
private boolean dobValid;
protected Date dob;
/**
 * Returns whether the value of the field dob is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field dob is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isDobValid() {
return dobValid;
}
/**
 * Returns the value of dob.
 * @return  The value of dob.
*/
public Date getDob() {
return dob;
}
/**
 * Sets the value of dob.
 * @param  __v  The value of dob.
*/
public void setDob(Date __v) {
this.dob = __v;
}
private boolean sex_cdValid;
protected char sex_cd;
/**
 * Returns whether the value of the field sex_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field sex_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isSex_cdValid() {
return sex_cdValid;
}
/**
 * Returns the value of sex_cd.
 * @return  The value of sex_cd.
*/
public char getSex_cd() {
return sex_cd;
}
/**
 * Sets the value of sex_cd.
 * @param  __v  The value of sex_cd.
*/
public void setSex_cd(char __v) {
this.sex_cd = __v;
}
private boolean facility_idValid;
protected String facility_id;
/**
 * Returns whether the value of the field facility_id is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field facility_id is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isFacility_idValid() {
return facility_idValid;
}
/**
 * Returns the value of facility_id.
 * @return  The value of facility_id.
*/
public String getFacility_id() {
return facility_id;
}
/**
 * Sets the value of facility_id.
 * @param  __v  The value of facility_id.
*/
public void setFacility_id(String __v) {
this.facility_id = __v;
}
/** Copy constructor. Performs a deep copy of the nodes, but not the values.
 * @param  __o  The node to copy.
*/
public NamesHolder(NamesBase __o) {
first_name = __o.getFirst_name();
first_nameValid = __o.isFirst_nameValid();
middle_name = __o.getMiddle_name();
middle_nameValid = __o.isMiddle_nameValid();
last_name = __o.getLast_name();
last_nameValid = __o.isLast_nameValid();
dob = __o.getDob();
dobValid = __o.isDobValid();
sex_cd = __o.getSex_cd();
sex_cdValid = __o.isSex_cdValid();
facility_id = __o.getFacility_id();
facility_idValid = __o.isFacility_idValid();
}
}
