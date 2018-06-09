// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.cm.testobjects.mci.MciRecords;
import com.choicemaker.util.StringUtils;









import java.util.*;
/**
 * Generated holder class for the node type rel. See package documentation for details.
 */
public class RelHolder implements com.choicemaker.cm.testobjects.mci.MciRecords.RelBase, java.io.Serializable {
/** Default constructor. Initializes all all arrays for nested record to zero length arrays and all other values to their defaults (0/null). */public RelHolder() {
}
/** Zero length array to be used by outer node class. */
public static final RelHolder[] ZERO_ARRAY = new RelHolder[0];
private AddressBase outer;
/**
 * Returns the outer node.
 * @return  The outer node.
*/
public AddressBase getOuter() {
return outer;
}
/**

 * @param  outer  The outer node.
*/
public void setOuter(AddressBase outer) {
this.outer = outer;
}
private boolean last_date_rptdValid;
protected Date last_date_rptd;
/**
 * Returns whether the value of the field last_date_rptd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field last_date_rptd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isLast_date_rptdValid() {
return last_date_rptdValid;
}
/**
 * Returns the value of last_date_rptd.
 * @return  The value of last_date_rptd.
*/
public Date getLast_date_rptd() {
return last_date_rptd;
}
/**

 * @param  __v  The value of last_date_rptd.
*/
public void setLast_date_rptd(Date __v) {
this.last_date_rptd = __v;
}
private boolean apt_noValid;
protected String apt_no;
/**
 * Returns whether the value of the field apt_no is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field apt_no is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isApt_noValid() {
return apt_noValid;
}
/**
 * Returns the value of apt_no.
 * @return  The value of apt_no.
*/
public String getApt_no() {
return apt_no;
}
/**

 * @param  __v  The value of apt_no.
*/
public void setApt_no(String __v) {
this.apt_no = __v;
}
private boolean addr_type_cdValid;
protected String addr_type_cd;
/**
 * Returns whether the value of the field addr_type_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field addr_type_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isAddr_type_cdValid() {
return addr_type_cdValid;
}
/**
 * Returns the value of addr_type_cd.
 * @return  The value of addr_type_cd.
*/
public String getAddr_type_cd() {
return addr_type_cd;
}
/**

 * @param  __v  The value of addr_type_cd.
*/
public void setAddr_type_cd(String __v) {
this.addr_type_cd = __v;
}
private boolean raw_street_nameValid;
protected String raw_street_name;
/**
 * Returns whether the value of the field raw_street_name is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field raw_street_name is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isRaw_street_nameValid() {
return raw_street_nameValid;
}
/**
 * Returns the value of raw_street_name.
 * @return  The value of raw_street_name.
*/
public String getRaw_street_name() {
return raw_street_name;
}
/**

 * @param  __v  The value of raw_street_name.
*/
public void setRaw_street_name(String __v) {
this.raw_street_name = __v;
}
private boolean phone_noValid;
protected String phone_no;
/**
 * Returns whether the value of the field phone_no is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field phone_no is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isPhone_noValid() {
return phone_noValid;
}
/**
 * Returns the value of phone_no.
 * @return  The value of phone_no.
*/
public String getPhone_no() {
return phone_no;
}
/**

 * @param  __v  The value of phone_no.
*/
public void setPhone_no(String __v) {
this.phone_no = __v;
}
/** Copy constructor. Performs a deep copy of the nodes, but not the values.
 * @param  __o  The node to copy.
*/
public RelHolder(RelBase __o) {
last_date_rptd = __o.getLast_date_rptd();
last_date_rptdValid = __o.isLast_date_rptdValid();
apt_no = __o.getApt_no();
apt_noValid = __o.isApt_noValid();
addr_type_cd = __o.getAddr_type_cd();
addr_type_cdValid = __o.isAddr_type_cdValid();
raw_street_name = __o.getRaw_street_name();
raw_street_nameValid = __o.isRaw_street_nameValid();
phone_no = __o.getPhone_no();
phone_noValid = __o.isPhone_noValid();
}
}
