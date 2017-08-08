
package com.plavaga.document.duke.genetic;

import java.io.IOException;

import com.plavaga.document.duke.InMemoryLinkDatabase;
import com.plavaga.document.duke.Link;
import com.plavaga.document.duke.LinkDatabase;
import com.plavaga.document.duke.LinkKind;
import com.plavaga.document.duke.utils.LinkDatabaseUtils;

/**
 * This oracle looks up the answer in a link file.
 */
public class LinkFileOracle implements Oracle {
  private InMemoryLinkDatabase linkdb;

  public LinkFileOracle(String testfile) throws IOException {
    this.linkdb = new InMemoryLinkDatabase();
    linkdb.setDoInference(true);
    LinkDatabaseUtils.loadTestFile(testfile, linkdb);
  }

  public LinkDatabase getLinkDatabase() {
    return linkdb;
  }

  public LinkKind getLinkKind(String id1, String id2) {
    Link link = linkdb.inferLink(id1, id2);
    if (link == null)
      return LinkKind.DIFFERENT; // we assume missing links are incorrect
    return link.getKind();
  }
}