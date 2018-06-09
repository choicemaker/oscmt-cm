// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.cm.custom.mci.gend.internal.MciRecords;
import com.choicemaker.cm.core.*;
import com.choicemaker.cm.core.base.*;
import com.choicemaker.cm.core.util.*;
import com.choicemaker.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JLabel;
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
public class RelImpl____descriptor implements com.choicemaker.cm.core.Descriptor {
public static com.choicemaker.cm.core.Descriptor instance = new RelImpl____descriptor();
private static HashMap m;
private static ColumnDefinition[] cols = {
new ColumnDefinition("last_date_rptd", "last_date_rptd", 100, JLabel.CENTER),
new ColumnDefinition("apt_no", "apt_no", 100, JLabel.CENTER),
new ColumnDefinition("< cleanAptNo >", "cleanAptNo", 100, JLabel.CENTER),
new ColumnDefinition("addr_type_cd", "addr_type_cd", 100, JLabel.CENTER),
new ColumnDefinition("raw_street_name", "raw_street_name", 100, JLabel.CENTER),
new ColumnDefinition("< parsedHouseNumber >", "parsedHouseNumber", 100, JLabel.CENTER),
new ColumnDefinition("< parsedStreetName >", "parsedStreetName", 100, JLabel.CENTER),
new ColumnDefinition("< parsedApartment >", "parsedApartment", 100, JLabel.CENTER),
new ColumnDefinition("< parsedPoBox >", "parsedPoBox", 100, JLabel.CENTER),
new ColumnDefinition("< phoneAsStreet >", "phoneAsStreet", 100, JLabel.CENTER),
new ColumnDefinition("phone_no", "phone_no", 100, JLabel.CENTER),
new ColumnDefinition("< clean_phone_no >", "clean_phone_no", 100, JLabel.CENTER)};
private static com.choicemaker.cm.core.Descriptor[] children = {
};
public boolean[] getEditable(DerivedSource src) {
return new boolean[] {
true,
true,
!__src1.includes(src),
true,
true,
!__src1.includes(src),
!__src1.includes(src),
!__src1.includes(src),
!__src1.includes(src),
!__src1.includes(src),
true,
!__src1.includes(src)};
}
private static DerivedSource __src1 = DerivedSource.valueOf("all");
public String getName() {
return "rel";
}
public String getRecordName() {
return "rel";
}
public boolean isStackable() {
return true;
}
public ColumnDefinition[] getColumnDefinitions() {
return cols;
}
public Descriptor[] getChildren() {
return children;
}
public Record[][] getChildRecords(Record ri) {
return null;
}
public String getValueAsString(Record ri, int row, int col) {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
for(int i1 = 0; i1 < r0.address.length; ++i1) {
AddressImpl r1 = r0.address[i1];
if(cur + r1.rel.length <= row) {
cur += r1.rel.length;
} else {
switch(col) {
case 0:
return r1.rel[row-cur].last_date_rptd == null ? null : DateHelper.formatDisplay(r1.rel[row-cur].last_date_rptd);
case 1:
return r1.rel[row-cur].apt_no;
case 2:
return r1.rel[row-cur].cleanAptNo;
case 3:
return r1.rel[row-cur].addr_type_cd;
case 4:
return r1.rel[row-cur].raw_street_name;
case 5:
return r1.rel[row-cur].parsedHouseNumber;
case 6:
return r1.rel[row-cur].parsedStreetName;
case 7:
return r1.rel[row-cur].parsedApartment;
case 8:
return r1.rel[row-cur].parsedPoBox;
case 9:
return r1.rel[row-cur].phoneAsStreet;
case 10:
return r1.rel[row-cur].phone_no;
case 11:
return r1.rel[row-cur].clean_phone_no;
default:
throw new IndexOutOfBoundsException();
}
}
}
throw new IndexOutOfBoundsException();
}
public Object getValue(Record ri, int row, int col) {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
for(int i1 = 0; i1 < r0.address.length; ++i1) {
AddressImpl r1 = r0.address[i1];
if(cur + r1.rel.length <= row) {
cur += r1.rel.length;
} else {
switch(col) {
case 0:
return r1.rel[row-cur].last_date_rptd;
case 1:
return r1.rel[row-cur].apt_no;
case 2:
return r1.rel[row-cur].cleanAptNo;
case 3:
return r1.rel[row-cur].addr_type_cd;
case 4:
return r1.rel[row-cur].raw_street_name;
case 5:
return r1.rel[row-cur].parsedHouseNumber;
case 6:
return r1.rel[row-cur].parsedStreetName;
case 7:
return r1.rel[row-cur].parsedApartment;
case 8:
return r1.rel[row-cur].parsedPoBox;
case 9:
return r1.rel[row-cur].phoneAsStreet;
case 10:
return r1.rel[row-cur].phone_no;
case 11:
return r1.rel[row-cur].clean_phone_no;
default:
throw new IndexOutOfBoundsException();
}
}
}
throw new IndexOutOfBoundsException();
}
public Class getHandledClass() {
return RelImpl.class;
}
public boolean setValue(Record ri, int row, int col, String value) {
try {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
for(int i1 = 0; i1 < r0.address.length; ++i1) {
AddressImpl r1 = r0.address[i1];
if(cur + r1.rel.length <= row) {
cur += r1.rel.length;
} else {
switch(col) {
case 0:
r1.rel[row-cur].last_date_rptd = DateHelper.parse(value);
break;
case 1:
r1.rel[row-cur].apt_no = value;
break;
case 2:
r1.rel[row-cur].cleanAptNo = value;
break;
case 3:
r1.rel[row-cur].addr_type_cd = value;
break;
case 4:
r1.rel[row-cur].raw_street_name = value;
break;
case 5:
r1.rel[row-cur].parsedHouseNumber = value;
break;
case 6:
r1.rel[row-cur].parsedStreetName = value;
break;
case 7:
r1.rel[row-cur].parsedApartment = value;
break;
case 8:
r1.rel[row-cur].parsedPoBox = value;
break;
case 9:
r1.rel[row-cur].phoneAsStreet = value;
break;
case 10:
r1.rel[row-cur].phone_no = value;
break;
case 11:
r1.rel[row-cur].clean_phone_no = value;
break;
default:
throw new IndexOutOfBoundsException();
}
return true;}
}
throw new IndexOutOfBoundsException();
} catch(Exception ex) {
return false;
}
}
public boolean getValidity(Record ri, int row, int col) {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
for(int i1 = 0; i1 < r0.address.length; ++i1) {
AddressImpl r1 = r0.address[i1];
if(cur + r1.rel.length <= row) {
cur += r1.rel.length;
} else {
switch(col) {
case 0:
return r1.rel[row-cur].__v_last_date_rptd;
case 1:
return r1.rel[row-cur].__v_apt_no;
case 2:
return r1.rel[row-cur].__v_cleanAptNo;
case 3:
return r1.rel[row-cur].__v_addr_type_cd;
case 4:
return r1.rel[row-cur].__v_raw_street_name;
case 5:
return r1.rel[row-cur].__v_parsedHouseNumber;
case 6:
return r1.rel[row-cur].__v_parsedStreetName;
case 7:
return r1.rel[row-cur].__v_parsedApartment;
case 8:
return r1.rel[row-cur].__v_parsedPoBox;
case 9:
return r1.rel[row-cur].__v_phoneAsStreet;
case 10:
return r1.rel[row-cur].__v_phone_no;
case 11:
return r1.rel[row-cur].__v_clean_phone_no;
default:
throw new IndexOutOfBoundsException();
}
}
}
throw new IndexOutOfBoundsException();
}
public void addRow(int row, boolean above, Record ri) {
PatientImpl r0 = (PatientImpl)ri;
if(!above) {
++row;
};
int cur = 0;
if(r0.address.length == 0) {
r0.address = new AddressImpl[1];
r0.address[0] = AddressImpl.instance();
}
for(int i1 = 0; i1 < r0.address.length; ++i1) {
AddressImpl r1 = r0.address[i1];
if(cur + r1.rel.length < row || (above && cur + r1.rel.length == row && row != 0)) {
cur += r1.rel.length;
} else {
RelImpl[] tmp = new RelImpl[r1.rel.length + 1];
System.arraycopy(r1.rel, 0, tmp, 0, (row - cur));
tmp[(row - cur)] = RelImpl.instance();
System.arraycopy(r1.rel, (row - cur), tmp, (row - cur) + 1, tmp.length - 1 - (row - cur));
r1.rel = tmp;
return;}
}
throw new IndexOutOfBoundsException();
}
public void deleteRow(Record ri, int row) {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
for(int i1 = 0; i1 < r0.address.length; ++i1) {
AddressImpl r1 = r0.address[i1];
if(cur + r1.rel.length <= row) {
cur += r1.rel.length;
} else {
RelImpl[] tmp = new RelImpl[r1.rel.length - 1];
System.arraycopy(r1.rel, 0, tmp, 0, (row - cur));
System.arraycopy(r1.rel, (row - cur) + 1, tmp, (row - cur), tmp.length - (row - cur));
r1.rel = tmp;
return;}
}
throw new IndexOutOfBoundsException();
}
public int getColumnCount() {
return cols.length;
}
public int getRowCount(Record ri) {
PatientImpl r0 = (PatientImpl)ri;
int num = 0;
for(int i1 = 0; i1 < r0.address.length; ++i1) {
AddressImpl r1 = r0.address[i1];
num += r1.rel.length;
}
return num;
}

public int getColumnIndexByName(String name) {
if(m == null) {
m = new HashMap(cols.length);
for(int i = 0; i < cols.length; ++i) {
m.put(cols[i].getFieldName(), new Integer(i));
}
}
Object o = m.get(name);
if(o == null) {
return -1;
} else {
return ((Integer)o).intValue();
}
}
}
