
package com.plavaga.document.duke.genetic;

import com.plavaga.document.duke.Configuration;
import com.plavaga.document.duke.Property;

/**
 * Sets the high probability.
 */
public class HighProbabilityAspect extends FloatAspect {
  private Property prop;

  public HighProbabilityAspect(Property prop) {
    this.prop = prop;
  }

  public void setRandomly(GeneticConfiguration cfg) {
    Configuration config = cfg.getConfiguration();
    Property p = config.getPropertyByName(prop.getName());
    double new_value = drift(config.getThreshold(), 1.0, 0.5);
    p.setHighProbability(new_value);
  }

  public void setFromOther(GeneticConfiguration cfg1,
                           GeneticConfiguration cfg2) {
    Configuration config = cfg1.getConfiguration();
    Configuration other = cfg2.getConfiguration();

    Property p1 = config.getPropertyByName(prop.getName());
    Property p2 = other.getPropertyByName(prop.getName());
    p1.setHighProbability(p2.getHighProbability());
  }
}
