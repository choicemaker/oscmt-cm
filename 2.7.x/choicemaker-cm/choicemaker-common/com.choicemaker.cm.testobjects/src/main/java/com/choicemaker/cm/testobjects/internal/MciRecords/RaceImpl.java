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
public class RaceImpl implements BaseRecord, Race {
private static Logger logger = Logger.getLogger(com.choicemaker.cm.custom.mci.gend.internal.MciRecords.RaceImpl.class.getName());
public static RaceImpl[] __zeroArray = new RaceImpl[0];
public PatientImpl outer;
public PatientBase getOuter() {
return outer;
}
public void setOuter(PatientBase outer) {
this.outer = (PatientImpl)outer;
}
public boolean __v_race_cd;
public String race_cd;
public boolean isRace_cdValid() {
return __v_race_cd;
}
public String getRace_cd() {
return race_cd;
}
public RaceImpl(RaceBase __o) {
race_cd = __o.getRace_cd();
}
public RaceImpl() {
}
public void computeValidityAndDerived(DerivedSource __src) {
java.lang.String __tmpStr;
try {
__v_race_cd = StringUtils.nonEmptyString(race_cd) && !race_cd.equals("U") && !race_cd.equals("O");
} catch(Exception __ex) {
logger.severe("Computing validity and derived of RaceImpl" + __ex);
}
}
public void resetValidityAndDerived(DerivedSource __src) {
}
public static RaceImpl instance() {
RaceImpl tmpInstance = new RaceImpl();
return tmpInstance;
}
}
