package com.plavaga.document.deduplicate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.plavaga.document.duke.Configuration;
import com.plavaga.document.duke.ConfigurationImpl;
import com.plavaga.document.duke.Processor;
import com.plavaga.document.duke.Property;
import com.plavaga.document.duke.PropertyImpl;
import com.plavaga.document.duke.cleaners.LowerCaseNormalizeCleaner;
import com.plavaga.document.duke.cleaners.PhoneNumberCleaner;
import com.plavaga.document.duke.comparators.WeightedLevenshtein;
import com.plavaga.document.duke.datasources.Column;
import com.plavaga.document.duke.matchers.PrintMatchListener;
import com.plavaga.document.duke.mongodb.MongoDBDataSource;

/**
 * @author chiranjithsrai
 *
 */
public class DeDuplicate {

	private static final int	PORT			= constants.PORT;
	private static final String	SERVER_ADDRESS	= constants.SERVER_ADDRESS;

	/**
	 *
	 * @param query
	 * @param propertyThreasholdMap
	 * @param database
	 * @return Set<Object>
	 */
	public Set<Object> deDuplicate(String query, Map<String, Object> propertyThreasholdMap, String database) {

		// ExactComparator ex = new ExactComparator();
		WeightedLevenshtein weighted = new WeightedLevenshtein();
		List<Property> props = new LinkedList<Property>();
		props.add(new PropertyImpl("ID"));

		Set<String> keySet = propertyThreasholdMap.keySet();

		for (String key : keySet) {
			double high = Double.parseDouble(propertyThreasholdMap.get(key).toString());
			double low = 0.0;
			props.add(new PropertyImpl(key, weighted, low, high));
		}

		Configuration config = new ConfigurationImpl();
		((ConfigurationImpl) config).setProperties(props);
		((ConfigurationImpl) config).setThreshold(0.9);
		((ConfigurationImpl) config).setMaybeThreshold(0.9);

		MongoDBDataSource mongo = new MongoDBDataSource();
		mongo.setServerAddress(SERVER_ADDRESS);
		mongo.setPortNumber(PORT);
		mongo.setDatabase(database);
		mongo.setCollection(constants.COLLECTION);
		mongo.setQuery(query);
		LowerCaseNormalizeCleaner cleaner = new LowerCaseNormalizeCleaner();
		mongo.addColumn(new Column(constants.METADATA_ID, "ID", null, cleaner));
		for (String key : keySet) {

			if (key.equalsIgnoreCase("data.phones.mobile")) {
				mongo.addColumn(new Column(key, key, null, new PhoneNumberCleaner()));
			}
			else {
				mongo.addColumn(new Column(key, key, null, cleaner));
			}

		}
		((ConfigurationImpl) config).addDataSource(0, mongo);

		// Configuration config =
		// ConfigLoader.load("src/main/resources/properties.xml");
		Processor proc = new Processor(config);
		proc.addMatchListener(new PrintMatchListener(true, true, true, false, config.getProperties(), true));

		Set<Object> resultObject = proc.deduplicate();

		Set<Object> filteredResult = proc.filterDuplicateDocuments(resultObject);

		proc.close();
		return filteredResult;
	}
}
