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
 * Generated base interface for the node type frozen. See package documentation for details.
 */
public interface FrozenBase extends java.io.Serializable {
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
 * Returns whether the value of the field src_system_id is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field src_system_id is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isSrc_system_idValid();
/**
 * Returns the value of src_system_id.
 * @return  The value of src_system_id.
*/
public String getSrc_system_id();
}
