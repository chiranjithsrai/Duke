
package com.plavaga.document.duke.genetic;

import com.plavaga.document.duke.Configuration;
import com.plavaga.document.duke.Property;

/**
 * Sets the low probability.
 */
public class LowProbabilityAspect extends FloatAspect {
  private Property prop;

  public LowProbabilityAspect(Property prop) {
    this.prop = prop;
  }

  public void setRandomly(GeneticConfiguration cfg) {
    Configuration config = cfg.getConfiguration();
    Property p = config.getPropertyByName(prop.getName());
    double new_value = drift(config.getThreshold(), 0.5, 0.0);
    p.setLowProbability(new_value);
  }

  public void setFromOther(GeneticConfiguration cfg1,
                           GeneticConfiguration cfg2) {
    Configuration config = cfg1.getConfiguration();
    Configuration other = cfg2.getConfiguration();

    Property p1 = config.getPropertyByName(prop.getName());
    Property p2 = other.getPropertyByName(prop.getName());
    p1.setLowProbability(p2.getLowProbability());
  }
  
}
