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
public class FrozenImpl implements BaseRecord, Frozen {
private static Logger logger = Logger.getLogger(com.choicemaker.cm.custom.mci.gend.internal.MciRecords.FrozenImpl.class.getName());
public static FrozenImpl[] __zeroArray = new FrozenImpl[0];
public PatientImpl outer;
public PatientBase getOuter() {
return outer;
}
public void setOuter(PatientBase outer) {
this.outer = (PatientImpl)outer;
}
public boolean __v_src_system_id;
public String src_system_id;
public boolean isSrc_system_idValid() {
return __v_src_system_id;
}
public String getSrc_system_id() {
return src_system_id;
}
public FrozenImpl(FrozenBase __o) {
src_system_id = __o.getSrc_system_id();
}
public FrozenImpl() {
}
public void computeValidityAndDerived(DerivedSource __src) {
java.lang.String __tmpStr;
try {
__v_src_system_id = StringUtils.nonEmptyString(src_system_id);
} catch(Exception __ex) {
logger.severe("Computing validity and derived of FrozenImpl" + __ex);
}
}
public void resetValidityAndDerived(DerivedSource __src) {
}
public static FrozenImpl instance() {
FrozenImpl tmpInstance = new FrozenImpl();
return tmpInstance;
}
}
