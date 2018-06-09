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
public class MothersImpl____descriptor implements com.choicemaker.cm.core.Descriptor {
public static com.choicemaker.cm.core.Descriptor instance = new MothersImpl____descriptor();
private static HashMap m;
private static ColumnDefinition[] cols = {
new ColumnDefinition("mothers_maiden_name", "mothers_maiden_name", 100, JLabel.CENTER),
new ColumnDefinition("mothers_dob", "mothers_dob", 100, JLabel.CENTER)};
private static com.choicemaker.cm.core.Descriptor[] children = {
};
public boolean[] getEditable(DerivedSource src) {
return new boolean[] {
true,
true};
}
public String getName() {
return "mothers";
}
public String getRecordName() {
return "mothers";
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
if(cur + r0.mothers.length <= row) {
cur += r0.mothers.length;
} else {
switch(col) {
case 0:
return r0.mothers[row-cur].mothers_maiden_name;
case 1:
return r0.mothers[row-cur].mothers_dob == null ? null : DateHelper.formatDisplay(r0.mothers[row-cur].mothers_dob);
default:
throw new IndexOutOfBoundsException();
}
}
throw new IndexOutOfBoundsException();
}
public Object getValue(Record ri, int row, int col) {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
if(cur + r0.mothers.length <= row) {
cur += r0.mothers.length;
} else {
switch(col) {
case 0:
return r0.mothers[row-cur].mothers_maiden_name;
case 1:
return r0.mothers[row-cur].mothers_dob;
default:
throw new IndexOutOfBoundsException();
}
}
throw new IndexOutOfBoundsException();
}
public Class getHandledClass() {
return MothersImpl.class;
}
public boolean setValue(Record ri, int row, int col, String value) {
try {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
if(cur + r0.mothers.length <= row) {
cur += r0.mothers.length;
} else {
switch(col) {
case 0:
r0.mothers[row-cur].mothers_maiden_name = value;
break;
case 1:
r0.mothers[row-cur].mothers_dob = DateHelper.parse(value);
break;
default:
throw new IndexOutOfBoundsException();
}
return true;}
throw new IndexOutOfBoundsException();
} catch(Exception ex) {
return false;
}
}
public boolean getValidity(Record ri, int row, int col) {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
if(cur + r0.mothers.length <= row) {
cur += r0.mothers.length;
} else {
switch(col) {
case 0:
return r0.mothers[row-cur].__v_mothers_maiden_name;
case 1:
return r0.mothers[row-cur].__v_mothers_dob;
default:
throw new IndexOutOfBoundsException();
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
if(cur + r0.mothers.length < row || (above && cur + r0.mothers.length == row && row != 0)) {
cur += r0.mothers.length;
} else {
MothersImpl[] tmp = new MothersImpl[r0.mothers.length + 1];
System.arraycopy(r0.mothers, 0, tmp, 0, (row - cur));
tmp[(row - cur)] = MothersImpl.instance();
System.arraycopy(r0.mothers, (row - cur), tmp, (row - cur) + 1, tmp.length - 1 - (row - cur));
r0.mothers = tmp;
return;}
throw new IndexOutOfBoundsException();
}
public void deleteRow(Record ri, int row) {
PatientImpl r0 = (PatientImpl)ri;
int cur = 0;
if(cur + r0.mothers.length <= row) {
cur += r0.mothers.length;
} else {
MothersImpl[] tmp = new MothersImpl[r0.mothers.length - 1];
System.arraycopy(r0.mothers, 0, tmp, 0, (row - cur));
System.arraycopy(r0.mothers, (row - cur) + 1, tmp, (row - cur), tmp.length - (row - cur));
r0.mothers = tmp;
return;}
throw new IndexOutOfBoundsException();
}
public int getColumnCount() {
return cols.length;
}
public int getRowCount(Record ri) {
PatientImpl r0 = (PatientImpl)ri;
int num = 0;
num += r0.mothers.length;
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
