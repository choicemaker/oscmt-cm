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
 * Generated base interface for the node type patient. See package documentation for details.
 */
public interface PatientBase extends java.io.Serializable {
/**
 * Returns whether the value of the field mci_id is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field mci_id is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isMci_idValid();
/**
 * Returns the value of mci_id.
 * @return  The value of mci_id.
*/
public int getMci_id();
/**
 * Returns whether the value of the field facility_id is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field facility_id is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isFacility_idValid();
/**
 * Returns the value of facility_id.
 * @return  The value of facility_id.
*/
public String getFacility_id();
/**
 * Returns whether the value of the field language_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field language_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isLanguage_cdValid();
/**
 * Returns the value of language_cd.
 * @return  The value of language_cd.
*/
public String getLanguage_cd();
/**
 * Returns whether the value of the field birth_country_cd is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field birth_country_cd is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isBirth_country_cdValid();
/**
 * Returns the value of birth_country_cd.
 * @return  The value of birth_country_cd.
*/
public String getBirth_country_cd();
/**
 * Returns whether the value of the field multi_birth_ind is valid according to the validity predicate in the ChoiceMaker schema.
 * @return  Whether the value of the field multi_birth_ind is valid according to the validity predicate in the ChoiceMaker schema.
*/
public boolean isMulti_birth_indValid();
/**
 * Returns the value of multi_birth_ind.
 * @return  The value of multi_birth_ind.
*/
public char getMulti_birth_ind();
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
/**
 * Returns the nested nodes of type names.
 * @return  The nested nodes of type names.
*/
public NamesBase[] getNames();
/**
 * Returns the nested names at the specified index.
 * @param  __index  The index.
 * @return  The nested names at the specified index.
*/
public NamesBase getNames(int __index);
/**
 * Returns the nested nodes of type ethnicity.
 * @return  The nested nodes of type ethnicity.
*/
public EthnicityBase[] getEthnicity();
/**
 * Returns the nested ethnicity at the specified index.
 * @param  __index  The index.
 * @return  The nested ethnicity at the specified index.
*/
public EthnicityBase getEthnicity(int __index);
/**
 * Returns the nested nodes of type race.
 * @return  The nested nodes of type race.
*/
public RaceBase[] getRace();
/**
 * Returns the nested race at the specified index.
 * @param  __index  The index.
 * @return  The nested race at the specified index.
*/
public RaceBase getRace(int __index);
/**
 * Returns the nested nodes of type ids.
 * @return  The nested nodes of type ids.
*/
public IdsBase[] getIds();
/**
 * Returns the nested ids at the specified index.
 * @param  __index  The index.
 * @return  The nested ids at the specified index.
*/
public IdsBase getIds(int __index);
/**
 * Returns the nested nodes of type address.
 * @return  The nested nodes of type address.
*/
public AddressBase[] getAddress();
/**
 * Returns the nested address at the specified index.
 * @param  __index  The index.
 * @return  The nested address at the specified index.
*/
public AddressBase getAddress(int __index);
/**
 * Returns the nested nodes of type contacts.
 * @return  The nested nodes of type contacts.
*/
public ContactsBase[] getContacts();
/**
 * Returns the nested contacts at the specified index.
 * @param  __index  The index.
 * @return  The nested contacts at the specified index.
*/
public ContactsBase getContacts(int __index);
/**
 * Returns the nested nodes of type mothers.
 * @return  The nested nodes of type mothers.
*/
public MothersBase[] getMothers();
/**
 * Returns the nested mothers at the specified index.
 * @param  __index  The index.
 * @return  The nested mothers at the specified index.
*/
public MothersBase getMothers(int __index);
/**
 * Returns the nested nodes of type events.
 * @return  The nested nodes of type events.
*/
public EventsBase[] getEvents();
/**
 * Returns the nested events at the specified index.
 * @param  __index  The index.
 * @return  The nested events at the specified index.
*/
public EventsBase getEvents(int __index);
/**
 * Returns the nested nodes of type providers.
 * @return  The nested nodes of type providers.
*/
public ProvidersBase[] getProviders();
/**
 * Returns the nested providers at the specified index.
 * @param  __index  The index.
 * @return  The nested providers at the specified index.
*/
public ProvidersBase getProviders(int __index);
/**
 * Returns the nested nodes of type frozen.
 * @return  The nested nodes of type frozen.
*/
public FrozenBase[] getFrozen();
/**
 * Returns the nested frozen at the specified index.
 * @param  __index  The index.
 * @return  The nested frozen at the specified index.
*/
public FrozenBase getFrozen(int __index);
/**
 * Returns the nested nodes of type indexAndOutstanding.
 * @return  The nested nodes of type indexAndOutstanding.
*/
public IndexAndOutstandingBase[] getIndexAndOutstanding();
/**
 * Returns the nested indexAndOutstanding at the specified index.
 * @param  __index  The index.
 * @return  The nested indexAndOutstanding at the specified index.
*/
public IndexAndOutstandingBase getIndexAndOutstanding(int __index);
}
