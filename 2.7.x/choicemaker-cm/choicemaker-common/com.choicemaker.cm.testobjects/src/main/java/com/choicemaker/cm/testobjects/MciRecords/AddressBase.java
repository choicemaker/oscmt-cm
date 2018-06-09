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
 * Generated base interface for the node type address. See package documentation for details.
 */
public interface AddressBase extends java.io.Serializable {
/**
 * Returns the outer node.
 * @return  The outer node.
*/
public PatientBase getOuter();
/**
 * Sets the outer node. This method should only be called by generated classes.
 * @param  outer  The outer node.
*/
public void setOuter(PatientBase outer);
/**
 * Returns whether the value of the field addr_id is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field addr_id is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isAddr_idValid();
/**
 * Returns the value of addr_id.
 * @return  The value of addr_id.
*/
public long getAddr_id();
/**
 * Returns whether the value of the field boro_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field boro_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isBoro_cdValid();
/**
 * Returns the value of boro_cd.
 * @return  The value of boro_cd.
*/
public char getBoro_cd();
/**
 * Returns whether the value of the field bin is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field bin is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isBinValid();
/**
 * Returns the value of bin.
 * @return  The value of bin.
*/
public long getBin();
/**
 * Returns whether the value of the field house_no is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field house_no is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isHouse_noValid();
/**
 * Returns the value of house_no.
 * @return  The value of house_no.
*/
public String getHouse_no();
/**
 * Returns whether the value of the field street_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field street_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isStreet_cdValid();
/**
 * Returns the value of street_cd.
 * @return  The value of street_cd.
*/
public String getStreet_cd();
/**
 * Returns whether the value of the field street_name is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field street_name is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isStreet_nameValid();
/**
 * Returns the value of street_name.
 * @return  The value of street_name.
*/
public String getStreet_name();
/**
 * Returns whether the value of the field city_name is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field city_name is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isCity_nameValid();
/**
 * Returns the value of city_name.
 * @return  The value of city_name.
*/
public String getCity_name();
/**
 * Returns whether the value of the field state_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field state_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isState_cdValid();
/**
 * Returns the value of state_cd.
 * @return  The value of state_cd.
*/
public String getState_cd();
/**
 * Returns whether the value of the field zipcode is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field zipcode is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isZipcodeValid();
/**
 * Returns the value of zipcode.
 * @return  The value of zipcode.
*/
public String getZipcode();
/**
 * Returns the nested nodes of type rel.
 * @return  The nested nodes of type rel.
*/
public RelBase[] getRel();
/**
 * Returns the nested rel at the specified index.
 * @param  __index  The index.
 * @return  The nested rel at the specified index.
*/
public RelBase getRel(int __index);
}
