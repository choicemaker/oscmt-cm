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
 * Generated base interface for the node type rel. See package documentation for details.
 */
public interface RelBase extends java.io.Serializable {
/**
 * Returns the outer node.
 * @return  The outer node.
*/
public AddressBase getOuter();
/**
 * Sets the outer node. This method should only be called by generated classes.
 * @param  outer  The outer node.
*/
public void setOuter(AddressBase outer);
/**
 * Returns whether the value of the field last_date_rptd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field last_date_rptd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isLast_date_rptdValid();
/**
 * Returns the value of last_date_rptd.
 * @return  The value of last_date_rptd.
*/
public Date getLast_date_rptd();
/**
 * Returns whether the value of the field apt_no is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field apt_no is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isApt_noValid();
/**
 * Returns the value of apt_no.
 * @return  The value of apt_no.
*/
public String getApt_no();
/**
 * Returns whether the value of the field addr_type_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field addr_type_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isAddr_type_cdValid();
/**
 * Returns the value of addr_type_cd.
 * @return  The value of addr_type_cd.
*/
public String getAddr_type_cd();
/**
 * Returns whether the value of the field raw_street_name is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field raw_street_name is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isRaw_street_nameValid();
/**
 * Returns the value of raw_street_name.
 * @return  The value of raw_street_name.
*/
public String getRaw_street_name();
/**
 * Returns whether the value of the field phone_no is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field phone_no is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isPhone_noValid();
/**
 * Returns the value of phone_no.
 * @return  The value of phone_no.
*/
public String getPhone_no();
}
