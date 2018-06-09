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
public class ProvidersImpl implements BaseRecord, Providers {
private static Logger logger = Logger.getLogger(com.choicemaker.cm.custom.mci.gend.internal.MciRecords.ProvidersImpl.class.getName());
public static ProvidersImpl[] __zeroArray = new ProvidersImpl[0];
public PatientImpl outer;
public PatientBase getOuter() {
return outer;
}
public void setOuter(PatientBase outer) {
this.outer = (PatientImpl)outer;
}
public boolean __v_provider_id;
public boolean __v_facility_id;
public String provider_id;
public boolean isProvider_idValid() {
return __v_provider_id;
}
public String getProvider_id() {
return provider_id;
}
public String facility_id;
public boolean isFacility_idValid() {
return __v_facility_id;
}
public String getFacility_id() {
return facility_id;
}
public ProvidersImpl(ProvidersBase __o) {
provider_id = __o.getProvider_id();
facility_id = __o.getFacility_id();
}
public ProvidersImpl() {
}
public void computeValidityAndDerived(DerivedSource __src) {
java.lang.String __tmpStr;
try {
__v_provider_id = StringUtils.nonEmptyString(provider_id) && !provider_id.equals("-9");
__v_facility_id = StringUtils.nonEmptyString(facility_id);
} catch(Exception __ex) {
logger.severe("Computing validity and derived of ProvidersImpl" + __ex);
}
}
public void resetValidityAndDerived(DerivedSource __src) {
}
public static ProvidersImpl instance() {
ProvidersImpl tmpInstance = new ProvidersImpl();
return tmpInstance;
}
}
