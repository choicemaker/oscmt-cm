// Generated by ChoiceMaker. Do not edit.
package com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person2.blocking;
import java.util.logging.*;
import java.util.*;
import com.choicemaker.cm.core.*;
import com.choicemaker.cm.core.base.*;
import com.choicemaker.cm.aba.*;
import com.choicemaker.cm.aba.base.*;
import com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person2.*;
import java.util.Date;
import com.choicemaker.util.StringUtils;
import com.choicemaker.cm.validation.eclipse.impl.Validators;
public final class Person2__defaultAutomated__BlockingConfiguration extends BlockingConfiguration {
private static Logger logger = Logger.getLogger(com.choicemaker.demo.simple_person_matching.gendata.gend.internal.Person2.blocking.Person2__defaultAutomated__BlockingConfiguration.class.getName());
public Person2__defaultAutomated__BlockingConfiguration(String dbConf) {
int dbConfIndex;
if("default".equals(dbConf)) {
dbConfIndex = 0;
 } else {
throw new IllegalArgumentException("dbConf: " + dbConf);
}
dbTables = dbConfigurations[dbConfIndex].dbts;
dbFields = dbConfigurations[dbConfIndex].dbfs;
blockingFields = dbConfigurations[dbConfIndex].bfs;
name = dbConfigurations[dbConfIndex].name;
}
public IBlockingValue[] createBlockingValues(Record q) {
init(NUM_BLOCKING_FIELDS);
addPersonImpl((PersonImpl)q);
return unionValues();
}
private void addPersonImpl(PersonImpl r) {
__l0 = r.__v_recordId? addField(0, String.valueOf(r.recordId), null) : null;
__l1 = r.__v_ssn? addField(1, r.ssn, null) : null;
__l2 = r.__v_firstName? addField(2, r.firstName, null) : null;
__l3 = r.__v_lastName? addField(3, r.lastName, null) : null;
__l4 = r.__v_streetName? addField(4, r.streetName, null) : null;
__l5 = r.__v_city? addField(5, r.city, null) : null;
__l6 = r.__v_state? addField(6, r.state, null) : null;
__l7 = r.__v_zip? addField(7, r.zip, null) : null;
}
private static final BlockingConfiguration.DbConfiguration[] dbConfigurations = new BlockingConfiguration.DbConfiguration[1];
static {
QueryField[] qfs;
DbTable[] dbts;
DbField[] dbfs;
BlockingField[] bfs;
qfs = new QueryField[] {
new QueryField(),
new QueryField(),
new QueryField(),
new QueryField(),
new QueryField(),
new QueryField(),
new QueryField(),
new QueryField()
};
dbts = new DbTable[] {
new DbTable("person", 0, "recordId")
};
dbfs = new DbField[] {
new DbField(0, "record_id", "int", dbts[0], 1),
new DbField(1, "ssn", "String", dbts[0], 10),
new DbField(2, "first_name", "String", dbts[0], 10),
new DbField(3, "last_name", "String", dbts[0], 10),
new DbField(4, "street_name", "String", dbts[0], 10),
new DbField(5, "city", "String", dbts[0], 10),
new DbField(6, "state_code", "String", dbts[0], 10),
new DbField(7, "zip_code", "String", dbts[0], 10)
};
bfs = new BlockingField[] {
new BlockingField(0, qfs[0], dbfs[0], ""),
new BlockingField(1, qfs[1], dbfs[1], ""),
new BlockingField(2, qfs[2], dbfs[2], ""),
new BlockingField(3, qfs[3], dbfs[3], ""),
new BlockingField(4, qfs[4], dbfs[4], ""),
new BlockingField(5, qfs[5], dbfs[5], ""),
new BlockingField(6, qfs[6], dbfs[6], ""),
new BlockingField(7, qfs[7], dbfs[7], "")
};
dbConfigurations[0] = new DbConfiguration("Person2:b:defaultAutomated:default", qfs, dbts, dbfs, bfs);
};
private static final int NUM_BLOCKING_FIELDS = 8;
private IBlockingValue __l0;
private IBlockingValue __l1;
private IBlockingValue __l2;
private IBlockingValue __l3;
private IBlockingValue __l4;
private IBlockingValue __l5;
private IBlockingValue __l6;
private IBlockingValue __l7;
static {
}
}
