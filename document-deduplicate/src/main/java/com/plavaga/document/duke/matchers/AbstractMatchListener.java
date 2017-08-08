
package com.plavaga.document.duke.matchers;

import java.util.Set;

import com.plavaga.document.duke.Record;

/**
 * Convenience implementation with dummy methods, since most implementations
 * will only implement matches().
 */
public abstract class AbstractMatchListener implements MatchListener {

	public void batchReady(int size) {
	}

	public void batchDone() {
	}

	public Set<Object> matches(Record r1, Record r2, double confidence) {
		return null;
	}

	public void matchesPerhaps(Record r1, Record r2, double confidence) {
	}

	public void noMatchFor(Record record) {
	}

	public void startProcessing() {
	}

	public void endProcessing() {
	}

}