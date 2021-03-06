
package com.plavaga.document.duke.matchers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.plavaga.document.duke.Property;
import com.plavaga.document.duke.Record;

/**
 * Match listener which prints events to standard out. Used by the command-line
 * client. Also contains utility methods for printing records nicely.
 */
public class PrintMatchListener extends AbstractMatchListener {

	private int				matches;
	private int				records;
	private int				nonmatches;	// only counted in record linkage mode
	private boolean			showmatches;
	private boolean			progress;
	private boolean			linkage;	// means there's a separate indexing
										// step
	private List<Property>	properties;

	/**
	 * Creates a new listener.
	 *
	 * @param showmatches
	 *            Whether to display matches. (On cmd-line: --showmatches)
	 * @param showmaybe
	 *            Whether to display maybe-matches. --showmaybe
	 * @param progress
	 *            Whether to display progress reports. --progress
	 * @param linkage
	 *            True iff in record linkage mode.
	 * @param pretty
	 *            Whether to pretty-print records (not compact).
	 */
	public PrintMatchListener(boolean showmatches, boolean showmaybe, boolean progress, boolean linkage, List<Property> properties, boolean pretty) {
		this.matches = 0;
		this.records = 0;
		this.showmatches = showmatches;
		this.progress = progress;
		this.linkage = linkage;
		this.properties = properties;
	}

	public int getMatchCount() {

		return matches;
	}

	@Override
	public void batchReady(int size) {

		records += size;
		if (progress) {
			System.out.println("Records: " + records);
		}
	}

	@Override
	public Set<Object> matches(Record r1, Record r2, double confidence) {

		Set<Object> resultObject = new LinkedHashSet();
		matches++;
		if (showmatches) {
			// if (pretty)
			resultObject = prettyCompare(r1, r2, confidence, "\nMATCH", properties);
			/*
			 * else show(r1, r2, confidence, "\nMATCH", properties);
			 */
		}
		if (matches % 1000 == 0 && progress) {
			System.out.println("" + matches + "  matches");
		}
		return resultObject;
	}

	@Override
	public void endProcessing() {

		if (progress) {
			System.out.println("");
			System.out.println("Total records: " + records);
			System.out.println("Total matches: " + matches);
			System.out.println("Total non-matches: " + nonmatches);
		}
	}

	@Override
	public void noMatchFor(Record record) {

		nonmatches++;
		if (showmatches && linkage) {
			System.out.println("\nNO MATCH FOR:\n" + toString(record, properties));
		}
	}

	// =====

	public static Set<Object> show(Record r1, Record r2, double confidence, String heading, List<Property> props, boolean pretty) {

		// if (pretty)
		return prettyCompare(r1, r2, confidence, heading, props);
		/*
		 * else return show(r1, r2, confidence, heading, props);
		 */ }

	// mostly used in error messages
	public static String toString(Record r) {

		StringBuffer buf = new StringBuffer();
		for (String p : r.getProperties()) {
			Collection<String> vs = r.getValues(p);
			if (vs == null || vs.isEmpty()) {
				continue;
			}

			buf.append(p + ": ");
			for (String v : vs) {
				buf.append("'" + v + "', ");
			}
		}

		// buf.append(";;; " + r);
		return buf.toString();
	}

	public static String toString(Record r, List<Property> props) {

		StringBuffer buf = new StringBuffer();
		for (Property p : props) {
			Collection<String> vs = r.getValues(p.getName());
			if (vs == null || vs.isEmpty()) {
				continue;
			}

			buf.append(p.getName() + ": ");
			for (String v : vs) {
				buf.append("'" + v + "', ");
			}
		}

		// buf.append(";;; " + r);
		return buf.toString();
	}

	public static Set<Object> prettyCompare(Record r1, Record r2, double confidence, String heading, List<Property> props) {

		Set<Object> resultObject = new LinkedHashSet();
		Map<String, Object> resultMapObject = new HashMap<String, Object>();

		System.out.println(heading + " " + confidence);

		for (Property p : props) {
			String prop = p.getName();
			if ((r1.getValues(prop) == null || r1.getValues(prop).isEmpty()) && (r2.getValues(prop) == null || r2.getValues(prop).isEmpty())) {
				continue;
			}
			Set<String> listObject = new TreeSet<String>();
			listObject.add(value(r1, prop));
			listObject.add(value(r2, prop));
			resultMapObject.put(prop, listObject);
			System.out.println(prop);
			System.out.println(" " + value(r1, prop));
			System.out.println(" " + value(r2, prop));

		}
		resultObject.add(resultMapObject);
		return resultObject;
	}

	public static void prettyPrint(Record r, List<Property> props) {

		for (Property p : props) {
			String prop = p.getName();
			if (r.getValues(prop) == null || r.getValues(prop).isEmpty()) {
				continue;
			}

			System.out.println(prop + ": " + value(r, prop));
		}
	}

	public static void htmlCompare(Record r1, Record r2, double confidence, String heading, List<Property> props) {

		System.out.println("<p>" + heading + " " + confidence + "</p>");

		System.out.println("<table>");
		for (Property p : props) {
			String prop = p.getName();
			if ((r1.getValues(prop) == null || r1.getValues(prop).isEmpty()) && (r2.getValues(prop) == null || r2.getValues(prop).isEmpty())) {
				continue;
			}

			System.out.println("<tr><td>" + prop);
			System.out.println("<td>" + value(r1, prop));
			System.out.println("<td>" + value(r2, prop));
		}
		System.out.println("</table>");
	}

	private static String value(Record r, String p) {

		Collection<String> vs = r.getValues(p);
		if (vs == null) {
			return "<null>";
		}
		if (vs.isEmpty()) {
			return "<null>";
		}

		StringBuffer buf = new StringBuffer();
		for (String v : vs) {
			// buf.append("'");
			buf.append(v);
			// buf.append("'");
		}

		return buf.toString();
	}
}
