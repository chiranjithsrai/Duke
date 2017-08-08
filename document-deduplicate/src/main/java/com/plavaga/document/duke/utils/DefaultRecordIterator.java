
package com.plavaga.document.duke.utils;

import java.util.Iterator;

import com.plavaga.document.duke.Record;
import com.plavaga.document.duke.RecordIterator;

public class DefaultRecordIterator extends RecordIterator {
  private Iterator<Record> it;
  
  public DefaultRecordIterator(Iterator<Record> it) {
    this.it = it;
  }

  public boolean hasNext() {
    return it.hasNext();
  }

  public Record next() {
    return it.next();
  }  
}