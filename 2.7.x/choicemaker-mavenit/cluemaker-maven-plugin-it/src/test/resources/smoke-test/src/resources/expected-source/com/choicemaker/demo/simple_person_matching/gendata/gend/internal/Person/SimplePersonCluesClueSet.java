// Generated by ClueMaker compiler 2.7.1
//    Source: /Users/rphall/Documents/git.nosync/oscmt-cm/2.7.x/choicemaker-mavenit/cluemaker-maven-plugin-it/target/test-classes/smoke-test/src/main/cluemaker/SimplePersonClues.clues
//    Date:   1/10/18 11:51 PM

package com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person;
import java.util.*;
import com.choicemaker.cm.core.util.*;
import com.choicemaker.cm.matching.en.*;
import com.choicemaker.cm.matching.en.us.*;
import com.choicemaker.cm.matching.gen.*;
import com.choicemaker.util.*;
import com.choicemaker.cm.core.*;
import com.choicemaker.cm.core.base.*;
import java.util.logging.*;
public class SimplePersonCluesClueSet implements com.choicemaker.cm.core.ClueSet {
   private static java.util.logging.Logger cat = java.util.logging.Logger.getLogger(SimplePersonCluesClueSet.class.getName());
   private int __evalNum;
   private int __evalNummFirstName;
   private boolean __exprmFirstName;
   private boolean getCluemFirstName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummFirstName != __evalNum))
         {
            __evalNummFirstName = __evalNum;
            __exprmFirstName = (q.__v_firstName && m.__v_firstName) && m.firstName.equals(q.firstName);
         };
      return __exprmFirstName;
   };
   private int __evalNumdFirstName;
   private boolean __exprdFirstName;
   private boolean getCluedFirstName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdFirstName != __evalNum))
         {
            __evalNumdFirstName = __evalNum;
            __exprdFirstName = (q.__v_firstName && m.__v_firstName) && (!m.firstName.equals(q.firstName));
         };
      return __exprdFirstName;
   };
   private int __evalNumdFirstNameOnlyInitialMatch;
   private boolean __exprdFirstNameOnlyInitialMatch;
   private boolean getCluedFirstNameOnlyInitialMatch(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdFirstNameOnlyInitialMatch != __evalNum))
         {
            __evalNumdFirstNameOnlyInitialMatch = __evalNum;
            __exprdFirstNameOnlyInitialMatch = (getCluemFirstName(q, m) && (q.firstName.length() == 1)) && (m.firstName.length() == 1);
         };
      return __exprdFirstNameOnlyInitialMatch;
   };
   private int __evalNummFirstNameVsInitial;
   private boolean __exprmFirstNameVsInitial;
   private boolean getCluemFirstNameVsInitial(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummFirstNameVsInitial != __evalNum))
         {
            __evalNummFirstNameVsInitial = __evalNum;
            __exprmFirstNameVsInitial = (getCluedFirstName(q, m) && ((q.firstName.length() == 1) ^ (m.firstName.length() == 1))) && (q.firstName.charAt(0) == m.firstName.charAt(0));
         };
      return __exprmFirstNameVsInitial;
   };
   private int __evalNummJaroFirstName;
   private boolean __exprmJaroFirstName;
   private boolean getCluemJaroFirstName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummJaroFirstName != __evalNum))
         {
            __evalNummJaroFirstName = __evalNum;
            __exprmJaroFirstName = (((((!getCluemFirstName(q, m)) && q.__v_firstName) && m.__v_firstName) && (q.firstName.length() >= 2)) && (m.firstName.length() >= 2)) && (Jaro.jaro(q.firstName, m.firstName) >= 0.95F);
         };
      return __exprmJaroFirstName;
   };
   private int __evalNummEditDistanceFirstName;
   private boolean __exprmEditDistanceFirstName;
   private boolean getCluemEditDistanceFirstName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummEditDistanceFirstName != __evalNum))
         {
            __evalNummEditDistanceFirstName = __evalNum;
            __exprmEditDistanceFirstName = ((((!getCluemFirstName(q, m)) && (!getCluemJaroFirstName(q, m))) && q.__v_lastName) && m.__v_lastName) && (EditDistance2.editDistance2(q.firstName, m.firstName, 1) < 2);
         };
      return __exprmEditDistanceFirstName;
   };
   private int __evalNummMiddleNameInitialOnly;
   private boolean __exprmMiddleNameInitialOnly;
   private boolean getCluemMiddleNameInitialOnly(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummMiddleNameInitialOnly != __evalNum))
         {
            __evalNummMiddleNameInitialOnly = __evalNum;
            __exprmMiddleNameInitialOnly = ((q.__v_middleName && m.__v_middleName) && m.middleName.equals(q.middleName)) && (m.middleName.length() == 1);
         };
      return __exprmMiddleNameInitialOnly;
   };
   private int __evalNummMiddleName;
   private boolean __exprmMiddleName;
   private boolean getCluemMiddleName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummMiddleName != __evalNum))
         {
            __evalNummMiddleName = __evalNum;
            __exprmMiddleName = (!getCluemMiddleNameInitialOnly(q, m)) && ((q.__v_middleName && m.__v_middleName) && m.middleName.equals(q.middleName));
         };
      return __exprmMiddleName;
   };
   private int __evalNumdMiddleName;
   private boolean __exprdMiddleName;
   private boolean getCluedMiddleName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdMiddleName != __evalNum))
         {
            __evalNumdMiddleName = __evalNum;
            __exprdMiddleName = (q.__v_middleName && m.__v_middleName) && (!m.middleName.equals(q.middleName));
         };
      return __exprdMiddleName;
   };
   private int __evalNummLastName;
   private boolean __exprmLastName;
   private boolean getCluemLastName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummLastName != __evalNum))
         {
            __evalNummLastName = __evalNum;
            __exprmLastName = (q.__v_lastName && m.__v_lastName) && m.lastName.equals(q.lastName);
         };
      return __exprmLastName;
   };
   private int __evalNumdLastName;
   private boolean __exprdLastName;
   private boolean getCluedLastName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdLastName != __evalNum))
         {
            __evalNumdLastName = __evalNum;
            __exprdLastName = (q.__v_lastName && m.__v_lastName) && (!m.lastName.equals(q.lastName));
         };
      return __exprdLastName;
   };
   private int __evalNummJaroLastName;
   private boolean __exprmJaroLastName;
   private boolean getCluemJaroLastName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummJaroLastName != __evalNum))
         {
            __evalNummJaroLastName = __evalNum;
            __exprmJaroLastName = (((((!getCluemLastName(q, m)) && q.__v_lastName) && m.__v_lastName) && (q.lastName.length() >= 2)) && (m.lastName.length() >= 2)) && (Jaro.jaro(q.lastName, m.lastName) >= 0.95F);
         };
      return __exprmJaroLastName;
   };
   private int __evalNummEditDistanceLastName;
   private boolean __exprmEditDistanceLastName;
   private boolean getCluemEditDistanceLastName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummEditDistanceLastName != __evalNum))
         {
            __evalNummEditDistanceLastName = __evalNum;
            __exprmEditDistanceLastName = ((((!getCluemLastName(q, m)) && (!getCluemJaroLastName(q, m))) && q.__v_lastName) && m.__v_lastName) && (EditDistance2.editDistance2(q.lastName, m.lastName, 1) < 2);
         };
      return __exprmEditDistanceLastName;
   };
   private boolean __swap0(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      java.lang.Object[][] qr = new java.lang.Object[2][];
      __lacc.clear();
      if (q.__v_firstName)
         __lacc.add(q.firstName);
      qr[0] = __lacc.toArray();
      __lacc.clear();
      if (q.__v_lastName)
         __lacc.add(q.lastName);
      qr[1] = __lacc.toArray();
      java.lang.Object[][] mr = new java.lang.Object[2][];
      __lacc.clear();
      if (m.__v_firstName)
         __lacc.add(m.firstName);
      mr[0] = __lacc.toArray();
      __lacc.clear();
      if (m.__v_lastName)
         __lacc.add(m.lastName);
      mr[1] = __lacc.toArray();
      return com.choicemaker.cm.core.util.Swap.swapsame(qr, mr, 2, 2);
   };
   private int __evalNummSwapFnameLname;
   private boolean __exprmSwapFnameLname;
   private boolean getCluemSwapFnameLname(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummSwapFnameLname != __evalNum))
         {
            __evalNummSwapFnameLname = __evalNum;
            __exprmSwapFnameLname = ((!getCluemFirstName(q, m)) && (!getCluemLastName(q, m))) && __swap0(q, m);
         };
      return __exprmSwapFnameLname;
   };
   private int __evalNummSwapFnameLnameApproximate;
   private boolean __exprmSwapFnameLnameApproximate;
   private boolean getCluemSwapFnameLnameApproximate(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummSwapFnameLnameApproximate != __evalNum))
         {
            __evalNummSwapFnameLnameApproximate = __evalNum;
            __exprmSwapFnameLnameApproximate = ((((!getCluemFirstName(q, m)) && (!getCluemLastName(q, m))) && (!getCluemSwapFnameLname(q, m))) && ((Jaro.jaro(q.lastName, m.firstName) >= 0.95F) && (Jaro.jaro(m.lastName, q.firstName) >= 0.95F))) || ((EditDistance2.editDistance2(q.lastName, m.firstName, 1) < 2) && (EditDistance2.editDistance2(q.firstName, m.lastName, 1) < 2));
         };
      return __exprmSwapFnameLnameApproximate;
   };
   private int __evalNummStreetNumber;
   private boolean __exprmStreetNumber;
   private boolean getCluemStreetNumber(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummStreetNumber != __evalNum))
         {
            __evalNummStreetNumber = __evalNum;
            __exprmStreetNumber = (q.__v_streetNumber && m.__v_streetNumber) && m.streetNumber.equals(q.streetNumber);
         };
      return __exprmStreetNumber;
   };
   private int __evalNumdStreetNumber;
   private boolean __exprdStreetNumber;
   private boolean getCluedStreetNumber(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdStreetNumber != __evalNum))
         {
            __evalNumdStreetNumber = __evalNum;
            __exprdStreetNumber = (q.__v_streetNumber && m.__v_streetNumber) && (!m.streetNumber.equals(q.streetNumber));
         };
      return __exprdStreetNumber;
   };
   private int __evalNummStreetName;
   private boolean __exprmStreetName;
   private boolean getCluemStreetName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummStreetName != __evalNum))
         {
            __evalNummStreetName = __evalNum;
            __exprmStreetName = (q.__v_streetName && m.__v_streetName) && m.streetName.equals(q.streetName);
         };
      return __exprmStreetName;
   };
   private int __evalNumdStreetName;
   private boolean __exprdStreetName;
   private boolean getCluedStreetName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdStreetName != __evalNum))
         {
            __evalNumdStreetName = __evalNum;
            __exprdStreetName = (q.__v_streetName && m.__v_streetName) && (!m.streetName.equals(q.streetName));
         };
      return __exprdStreetName;
   };
   private int __evalNummApproxStreetName;
   private boolean __exprmApproxStreetName;
   private boolean getCluemApproxStreetName(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummApproxStreetName != __evalNum))
         {
            __evalNummApproxStreetName = __evalNum;
            __exprmApproxStreetName = getCluedStreetName(q, m) && ((Jaro.jaro(q.streetName, m.streetName) >= 0.95F) || (EditDistance2.editDistance2(q.streetName, m.streetName, 1) < 2));
         };
      return __exprmApproxStreetName;
   };
   private int __evalNummApartmentNumber;
   private boolean __exprmApartmentNumber;
   private boolean getCluemApartmentNumber(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummApartmentNumber != __evalNum))
         {
            __evalNummApartmentNumber = __evalNum;
            __exprmApartmentNumber = (q.__v_apartmentNumber && m.__v_apartmentNumber) && m.apartmentNumber.equals(q.apartmentNumber);
         };
      return __exprmApartmentNumber;
   };
   private int __evalNumdApartmentNumber;
   private boolean __exprdApartmentNumber;
   private boolean getCluedApartmentNumber(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdApartmentNumber != __evalNum))
         {
            __evalNumdApartmentNumber = __evalNum;
            __exprdApartmentNumber = (q.__v_apartmentNumber && m.__v_apartmentNumber) && (!m.apartmentNumber.equals(q.apartmentNumber));
         };
      return __exprdApartmentNumber;
   };
   private int __evalNummstate;
   private boolean __exprmstate;
   private boolean getCluemstate(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummstate != __evalNum))
         {
            __evalNummstate = __evalNum;
            __exprmstate = (q.__v_state && m.__v_state) && m.state.equals(q.state);
         };
      return __exprmstate;
   };
   private int __evalNumdstate;
   private boolean __exprdstate;
   private boolean getCluedstate(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdstate != __evalNum))
         {
            __evalNumdstate = __evalNum;
            __exprdstate = (q.__v_state && m.__v_state) && (!m.state.equals(q.state));
         };
      return __exprdstate;
   };
   private int __evalNummSsn;
   private boolean __exprmSsn;
   private boolean getCluemSsn(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummSsn != __evalNum))
         {
            __evalNummSsn = __evalNum;
            __exprmSsn = (q.__v_ssn && m.__v_ssn) && m.ssn.equals(q.ssn);
         };
      return __exprmSsn;
   };
   private int __evalNumdSsn;
   private boolean __exprdSsn;
   private boolean getCluedSsn(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdSsn != __evalNum))
         {
            __evalNumdSsn = __evalNum;
            __exprdSsn = (q.__v_ssn && m.__v_ssn) && (!m.ssn.equals(q.ssn));
         };
      return __exprdSsn;
   };
   private int editDistanceSSN;
   int[] mApproxSsn = new int[1];
   private static int[] __mApproxSsn__idx__distance = {
      3, 
      2, 
      1, 
   };
   private int __evalNummApproxSsn;
   private boolean __exprmApproxSsn;
   private boolean getCluemApproxSsn(PersonImpl q, PersonImpl m, int distance) throws java.lang.Exception {
      if (true)
         {
            __evalNummApproxSsn = __evalNum;
            __exprmApproxSsn = getCluedSsn(q, m) && (editDistanceSSN == distance);
         };
      return __exprmApproxSsn;
   };
   private int __evalNummCity;
   private boolean __exprmCity;
   private boolean getCluemCity(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummCity != __evalNum))
         {
            __evalNummCity = __evalNum;
            __exprmCity = (q.__v_city && m.__v_city) && m.city.equals(q.city);
         };
      return __exprmCity;
   };
   private int __evalNumdCity;
   private boolean __exprdCity;
   private boolean getCluedCity(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdCity != __evalNum))
         {
            __evalNumdCity = __evalNum;
            __exprdCity = (q.__v_city && m.__v_city) && (!m.city.equals(q.city));
         };
      return __exprdCity;
   };
   private int __evalNummJaroCity;
   private boolean __exprmJaroCity;
   private boolean getCluemJaroCity(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummJaroCity != __evalNum))
         {
            __evalNummJaroCity = __evalNum;
            __exprmJaroCity = (((((!getCluemCity(q, m)) && q.__v_city) && m.__v_city) && (q.city.length() >= 2)) && (m.city.length() >= 2)) && (Jaro.jaro(q.city, m.city) >= 0.95F);
         };
      return __exprmJaroCity;
   };
   private int __evalNummZip;
   private boolean __exprmZip;
   private boolean getCluemZip(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummZip != __evalNum))
         {
            __evalNummZip = __evalNum;
            __exprmZip = (q.__v_zip && m.__v_zip) && m.zip.equals(q.zip);
         };
      return __exprmZip;
   };
   private int __evalNumdZip;
   private boolean __exprdZip;
   private boolean getCluedZip(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNumdZip != __evalNum))
         {
            __evalNumdZip = __evalNum;
            __exprdZip = (q.__v_zip && m.__v_zip) && (!m.zip.equals(q.zip));
         };
      return __exprdZip;
   };
   private int __evalNummApproxZip;
   private boolean __exprmApproxZip;
   private boolean getCluemApproxZip(PersonImpl q, PersonImpl m) throws java.lang.Exception {
      if ((__evalNummApproxZip != __evalNum))
         {
            __evalNummApproxZip = __evalNum;
            __exprmApproxZip = getCluedZip(q, m) && (EditDistance2.editDistance2(q.zip, m.zip, 1) < 2);
         };
      return __exprmApproxZip;
   };
   private java.util.ArrayList __lacc = new java.util.ArrayList();
   private static com.choicemaker.cm.core.ClueDesc[] clueDescs = {
      new com.choicemaker.cm.core.ClueDesc(0, "mFirstName", com.choicemaker.client.api.Decision.MATCH, false, 0, 28, 28), 
      new com.choicemaker.cm.core.ClueDesc(1, "dFirstName", com.choicemaker.client.api.Decision.DIFFER, false, 0, 29, 29), 
      new com.choicemaker.cm.core.ClueDesc(2, "dFirstNameOnlyInitialMatch", com.choicemaker.client.api.Decision.DIFFER, false, 0, 30, 32), 
      new com.choicemaker.cm.core.ClueDesc(3, "mFirstNameVsInitial", com.choicemaker.client.api.Decision.MATCH, false, 0, 33, 36), 
      new com.choicemaker.cm.core.ClueDesc(4, "mJaroFirstName", com.choicemaker.client.api.Decision.MATCH, false, 0, 37, 43), 
      new com.choicemaker.cm.core.ClueDesc(5, "mEditDistanceFirstName", com.choicemaker.client.api.Decision.MATCH, false, 0, 45, 48), 
      new com.choicemaker.cm.core.ClueDesc(6, "mMiddleNameInitialOnly", com.choicemaker.client.api.Decision.MATCH, false, 0, 50, 50), 
      new com.choicemaker.cm.core.ClueDesc(7, "mMiddleName", com.choicemaker.client.api.Decision.MATCH, false, 0, 52, 52), 
      new com.choicemaker.cm.core.ClueDesc(8, "dMiddleName", com.choicemaker.client.api.Decision.DIFFER, false, 0, 53, 53), 
      new com.choicemaker.cm.core.ClueDesc(9, "mLastName", com.choicemaker.client.api.Decision.MATCH, false, 0, 55, 55), 
      new com.choicemaker.cm.core.ClueDesc(10, "dLastName", com.choicemaker.client.api.Decision.DIFFER, false, 0, 56, 56), 
      new com.choicemaker.cm.core.ClueDesc(11, "mJaroLastName", com.choicemaker.client.api.Decision.MATCH, false, 0, 58, 64), 
      new com.choicemaker.cm.core.ClueDesc(12, "mEditDistanceLastName", com.choicemaker.client.api.Decision.MATCH, false, 0, 67, 70), 
      new com.choicemaker.cm.core.ClueDesc(13, "mSwapFnameLname", com.choicemaker.client.api.Decision.MATCH, false, 0, 72, 76), 
      new com.choicemaker.cm.core.ClueDesc(14, "mSwapFnameLnameApproximate", com.choicemaker.client.api.Decision.MATCH, false, 0, 78, 85), 
      new com.choicemaker.cm.core.ClueDesc(15, "mStreetNumber", com.choicemaker.client.api.Decision.MATCH, false, 0, 87, 87), 
      new com.choicemaker.cm.core.ClueDesc(16, "dStreetNumber", com.choicemaker.client.api.Decision.DIFFER, false, 0, 88, 88), 
      new com.choicemaker.cm.core.ClueDesc(17, "mStreetName", com.choicemaker.client.api.Decision.MATCH, false, 0, 89, 89), 
      new com.choicemaker.cm.core.ClueDesc(18, "dStreetName", com.choicemaker.client.api.Decision.DIFFER, false, 0, 90, 90), 
      new com.choicemaker.cm.core.ClueDesc(19, "mApproxStreetName", com.choicemaker.client.api.Decision.MATCH, false, 0, 91, 94), 
      new com.choicemaker.cm.core.ClueDesc(20, "mApartmentNumber", com.choicemaker.client.api.Decision.MATCH, false, 0, 95, 95), 
      new com.choicemaker.cm.core.ClueDesc(21, "dApartmentNumber", com.choicemaker.client.api.Decision.DIFFER, false, 0, 96, 96), 
      new com.choicemaker.cm.core.ClueDesc(22, "mstate", com.choicemaker.client.api.Decision.MATCH, false, 0, 98, 98), 
      new com.choicemaker.cm.core.ClueDesc(23, "dstate", com.choicemaker.client.api.Decision.DIFFER, false, 0, 99, 99), 
      new com.choicemaker.cm.core.ClueDesc(24, "mSsn", com.choicemaker.client.api.Decision.MATCH, false, 0, 101, 101), 
      new com.choicemaker.cm.core.ClueDesc(25, "dSsn", com.choicemaker.client.api.Decision.DIFFER, false, 0, 102, 102), 
      new com.choicemaker.cm.core.ClueDesc(26, "mApproxSsn[3]", com.choicemaker.client.api.Decision.MATCH, false, 0, 104, 108), 
      new com.choicemaker.cm.core.ClueDesc(27, "mApproxSsn[2]", com.choicemaker.client.api.Decision.MATCH, false, 0, 104, 108), 
      new com.choicemaker.cm.core.ClueDesc(28, "mApproxSsn[1]", com.choicemaker.client.api.Decision.MATCH, false, 0, 104, 108), 
      new com.choicemaker.cm.core.ClueDesc(29, "mCity", com.choicemaker.client.api.Decision.MATCH, false, 0, 113, 113), 
      new com.choicemaker.cm.core.ClueDesc(30, "dCity", com.choicemaker.client.api.Decision.DIFFER, false, 0, 114, 114), 
      new com.choicemaker.cm.core.ClueDesc(31, "mJaroCity", com.choicemaker.client.api.Decision.MATCH, false, 0, 115, 120), 
      new com.choicemaker.cm.core.ClueDesc(32, "mZip", com.choicemaker.client.api.Decision.MATCH, false, 0, 122, 122), 
      new com.choicemaker.cm.core.ClueDesc(33, "dZip", com.choicemaker.client.api.Decision.DIFFER, false, 0, 123, 123), 
      new com.choicemaker.cm.core.ClueDesc(34, "mApproxZip", com.choicemaker.client.api.Decision.MATCH, false, 0, 124, 126), 
   };
   private static int[] sizes = {
      11, 
      24, 
      0, 
      0, 
      0, 
      0, 
      0, 
   };
   public int size(com.choicemaker.client.api.Decision d) {
      return sizes[d.toInt()];
   };
   private static int aSize = 35;
   public int size() {
      return 35;
   };
   public com.choicemaker.cm.core.ClueSetType getType() {
      return com.choicemaker.cm.core.ClueSetType.BOOLEAN;
   };
   public boolean hasDecision() {
      return true;
   };
   public com.choicemaker.cm.core.ClueDesc[] getClueDesc() {
      return clueDescs;
   };
   public com.choicemaker.cm.core.ActiveClues getActiveClues(com.choicemaker.cm.core.Record qi, com.choicemaker.cm.core.Record mi, boolean[] eval) {
      __evalNum = __evalNum + 1;
      cat.fine("PersonImpl.class.getName() == " + PersonImpl.class.getName());
      cat.fine("PersonImpl.class.toString() == " + PersonImpl.class.toString());
      cat.fine("PersonImpl.class.getClassLoader().toString() == " + PersonImpl.class.getClassLoader().toString());
      PersonImpl q = (PersonImpl)qi;
      PersonImpl m = (PersonImpl)mi;
      com.choicemaker.cm.core.BooleanActiveClues a = new com.choicemaker.cm.core.BooleanActiveClues(8);
      if (eval[0])
         try {
            if (getCluemFirstName(q, m))
               {
                  a.add(0, 0);
                  cat.fine("Clue mFirstName (0) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mFirstName (0) exception: " + ex);
         };
      if (eval[1])
         try {
            if (getCluedFirstName(q, m))
               {
                  a.add(1, 0);
                  cat.fine("Clue dFirstName (1) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dFirstName (1) exception: " + ex);
         };
      if (eval[2])
         try {
            if (getCluedFirstNameOnlyInitialMatch(q, m))
               {
                  a.add(2, 0);
                  cat.fine("Clue dFirstNameOnlyInitialMatch (2) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dFirstNameOnlyInitialMatch (2) exception: " + ex);
         };
      if (eval[3])
         try {
            if (getCluemFirstNameVsInitial(q, m))
               {
                  a.add(3, 0);
                  cat.fine("Clue mFirstNameVsInitial (3) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mFirstNameVsInitial (3) exception: " + ex);
         };
      if (eval[4])
         try {
            if (getCluemJaroFirstName(q, m))
               {
                  a.add(4, 0);
                  cat.fine("Clue mJaroFirstName (4) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mJaroFirstName (4) exception: " + ex);
         };
      if (eval[5])
         try {
            if (getCluemEditDistanceFirstName(q, m))
               {
                  a.add(5, 0);
                  cat.fine("Clue mEditDistanceFirstName (5) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mEditDistanceFirstName (5) exception: " + ex);
         };
      if (eval[6])
         try {
            if (getCluemMiddleNameInitialOnly(q, m))
               {
                  a.add(6, 0);
                  cat.fine("Clue mMiddleNameInitialOnly (6) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mMiddleNameInitialOnly (6) exception: " + ex);
         };
      if (eval[7])
         try {
            if (getCluemMiddleName(q, m))
               {
                  a.add(7, 0);
                  cat.fine("Clue mMiddleName (7) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mMiddleName (7) exception: " + ex);
         };
      if (eval[8])
         try {
            if (getCluedMiddleName(q, m))
               {
                  a.add(8, 0);
                  cat.fine("Clue dMiddleName (8) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dMiddleName (8) exception: " + ex);
         };
      if (eval[9])
         try {
            if (getCluemLastName(q, m))
               {
                  a.add(9, 0);
                  cat.fine("Clue mLastName (9) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mLastName (9) exception: " + ex);
         };
      if (eval[10])
         try {
            if (getCluedLastName(q, m))
               {
                  a.add(10, 0);
                  cat.fine("Clue dLastName (10) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dLastName (10) exception: " + ex);
         };
      if (eval[11])
         try {
            if (getCluemJaroLastName(q, m))
               {
                  a.add(11, 0);
                  cat.fine("Clue mJaroLastName (11) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mJaroLastName (11) exception: " + ex);
         };
      if (eval[12])
         try {
            if (getCluemEditDistanceLastName(q, m))
               {
                  a.add(12, 0);
                  cat.fine("Clue mEditDistanceLastName (12) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mEditDistanceLastName (12) exception: " + ex);
         };
      if (eval[13])
         try {
            if (getCluemSwapFnameLname(q, m))
               {
                  a.add(13, 0);
                  cat.fine("Clue mSwapFnameLname (13) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mSwapFnameLname (13) exception: " + ex);
         };
      if (eval[14])
         try {
            if (getCluemSwapFnameLnameApproximate(q, m))
               {
                  a.add(14, 0);
                  cat.fine("Clue mSwapFnameLnameApproximate (14) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mSwapFnameLnameApproximate (14) exception: " + ex);
         };
      if (eval[15])
         try {
            if (getCluemStreetNumber(q, m))
               {
                  a.add(15, 0);
                  cat.fine("Clue mStreetNumber (15) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mStreetNumber (15) exception: " + ex);
         };
      if (eval[16])
         try {
            if (getCluedStreetNumber(q, m))
               {
                  a.add(16, 0);
                  cat.fine("Clue dStreetNumber (16) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dStreetNumber (16) exception: " + ex);
         };
      if (eval[17])
         try {
            if (getCluemStreetName(q, m))
               {
                  a.add(17, 0);
                  cat.fine("Clue mStreetName (17) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mStreetName (17) exception: " + ex);
         };
      if (eval[18])
         try {
            if (getCluedStreetName(q, m))
               {
                  a.add(18, 0);
                  cat.fine("Clue dStreetName (18) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dStreetName (18) exception: " + ex);
         };
      if (eval[19])
         try {
            if (getCluemApproxStreetName(q, m))
               {
                  a.add(19, 0);
                  cat.fine("Clue mApproxStreetName (19) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mApproxStreetName (19) exception: " + ex);
         };
      if (eval[20])
         try {
            if (getCluemApartmentNumber(q, m))
               {
                  a.add(20, 0);
                  cat.fine("Clue mApartmentNumber (20) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mApartmentNumber (20) exception: " + ex);
         };
      if (eval[21])
         try {
            if (getCluedApartmentNumber(q, m))
               {
                  a.add(21, 0);
                  cat.fine("Clue dApartmentNumber (21) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dApartmentNumber (21) exception: " + ex);
         };
      if (eval[22])
         try {
            if (getCluemstate(q, m))
               {
                  a.add(22, 0);
                  cat.fine("Clue mstate (22) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mstate (22) exception: " + ex);
         };
      if (eval[23])
         try {
            if (getCluedstate(q, m))
               {
                  a.add(23, 0);
                  cat.fine("Clue dstate (23) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dstate (23) exception: " + ex);
         };
      if (eval[24])
         try {
            if (getCluemSsn(q, m))
               {
                  a.add(24, 0);
                  cat.fine("Clue mSsn (24) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mSsn (24) exception: " + ex);
         };
      if (eval[25])
         try {
            if (getCluedSsn(q, m))
               {
                  a.add(25, 0);
                  cat.fine("Clue dSsn (25) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dSsn (25) exception: " + ex);
         };
      try {
         editDistanceSSN = EditDistance2.editDistance2(q.ssn, m.ssn, 3);
      } catch (java.lang.Exception ex) {
         cat.severe("Expression editDistanceSSN exception: " + ex);
      };
      mApproxSsn[0] = -1;
      __tmApproxSsn: for (int i0 = 0, clueNum = 0; i0 < __mApproxSsn__idx__distance.length; i0 = i0 + 1, clueNum = clueNum + 1)
         if (eval[26 + clueNum])
            try {
               if (getCluemApproxSsn(q, m, __mApproxSsn__idx__distance[i0]))
                  {
                     a.add(26 + clueNum, 0);
                     if (cat.isLoggable(java.util.logging.Level.FINE))
                        cat.fine(("Clue mApproxSsn" + (" (" + ((26 + clueNum) + ")"))) + " fired");
                     {
                        mApproxSsn[0] = i0;
                     };
                     break __tmApproxSsn;
                  };
            } catch (java.lang.Exception ex) {
               cat.severe((("Clue mApproxSsn" + (" (" + ((26 + clueNum) + ")"))) + " exception: ") + ex);
            };
      if (eval[29])
         try {
            if (getCluemCity(q, m))
               {
                  a.add(29, 0);
                  cat.fine("Clue mCity (29) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mCity (29) exception: " + ex);
         };
      if (eval[30])
         try {
            if (getCluedCity(q, m))
               {
                  a.add(30, 0);
                  cat.fine("Clue dCity (30) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dCity (30) exception: " + ex);
         };
      if (eval[31])
         try {
            if (getCluemJaroCity(q, m))
               {
                  a.add(31, 0);
                  cat.fine("Clue mJaroCity (31) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mJaroCity (31) exception: " + ex);
         };
      if (eval[32])
         try {
            if (getCluemZip(q, m))
               {
                  a.add(32, 0);
                  cat.fine("Clue mZip (32) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mZip (32) exception: " + ex);
         };
      if (eval[33])
         try {
            if (getCluedZip(q, m))
               {
                  a.add(33, 0);
                  cat.fine("Clue dZip (33) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue dZip (33) exception: " + ex);
         };
      if (eval[34])
         try {
            if (getCluemApproxZip(q, m))
               {
                  a.add(34, 0);
                  cat.fine("Clue mApproxZip (34) fired.");
               };
         } catch (java.lang.Exception ex) {
            cat.severe("Clue mApproxZip (34) exception: " + ex);
         };
      return a;
   };
}
