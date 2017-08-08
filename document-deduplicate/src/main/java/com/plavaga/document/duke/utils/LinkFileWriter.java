
package com.plavaga.document.duke.utils;

import java.util.Collection;

import com.plavaga.document.duke.Configuration;
import com.plavaga.document.duke.DukeException;
import com.plavaga.document.duke.Property;
import com.plavaga.document.duke.Record;

import java.io.Writer;
import java.io.IOException;

/**
 * Utility class for writing link files. The format is
 * _,id,id,confidence, where the first character is either '+' or '-'.
 * @since 1.1
 */
public class LinkFileWriter {
  private Writer out;
  private Collection<Property> idprops;

  public LinkFileWriter(Writer out) {
    this(out, null);
  }
  
  public LinkFileWriter(Writer out, Configuration config) {
    this.out = out;
    if (config != null)
      this.idprops = config.getIdentityProperties();
  }

  public void write(Record r1, Record r2, boolean match, double confidence)
    throws IOException {
    write(getid(r1), getid(r2), match, confidence);
  }

  public void write(String id1, String id2, boolean match, double confidence)
    throws IOException {
    out.write("" + (match ? "+," : "-,") + id1 + ',' + id2 + ',' + confidence +
              "\n");
  }
  
  private String getid(Record r) {
    for (Property p : idprops) {
      String v = r.getValue(p.getName());
      if (v == null)
        continue;

      return v;
    }

    throw new DukeException("No identity for record " + r);
  }
}