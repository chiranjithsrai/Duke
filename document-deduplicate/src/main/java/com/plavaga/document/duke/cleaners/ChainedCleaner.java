
package com.plavaga.document.duke.cleaners;

import com.plavaga.document.duke.Cleaner;

/**
 * Internal cleaner used to implement chaining of multiple cleaners.
 * Basically, if you list multiple cleaners in the cleaner=""
 * attribute in the configuration file, it gets turned into a
 * ChainedCleaner that runs all the cleaners in sequence.
 */
public class ChainedCleaner implements Cleaner {
  private Cleaner[] cleaners;

  public ChainedCleaner(Cleaner[] cleaners) {
    this.cleaners = cleaners;
  }
  
  public String clean(String value) {
    for (int ix = 0; ix < cleaners.length; ix++) {
      if (value == null || value.equals(""))
        return null;

      value = cleaners[ix].clean(value);
    }
    return value;
  }
}