
package com.plavaga.document.duke.matchers;

import java.util.Set;

import com.plavaga.document.duke.Configuration;
import com.plavaga.document.duke.EquivalenceClassDatabase;
import com.plavaga.document.duke.Property;
import com.plavaga.document.duke.Record;

/**
 * Writes recorded matches to an EquivalenceClassDatabase.
 */
public class ClassDatabaseMatchListener extends AbstractMatchListener {
  private Configuration config;
  protected EquivalenceClassDatabase classdb;

  public ClassDatabaseMatchListener(Configuration config,
                                    EquivalenceClassDatabase classdb) {
    this.config = config;
    this.classdb = classdb;
  }
  
  public Set<Object> matches(Record r1, Record r2, double confidence) {
    String id1 = getIdentity(r1);
    String id2 = getIdentity(r2);
    classdb.addLink(id1, id2);
		return null;
  }

  public void batchDone() {
    classdb.commit();
  }
  
  private String getIdentity(Record r) {
    for (Property p : config.getIdentityProperties())
      for (String v : r.getValues(p.getName()))
        return v;
    throw new RuntimeException("No identity found in record [" +
                               PrintMatchListener.toString(r) + "]");
  }
  
}