/**
 * Class Name:			DocumentDeDuplication
 * Created On:			11:06:55 AM, 04-Oct-2016
 *
 * Copyright (c) 2012 Plavaga Software Solutions (P) Ltd. All rights reserved.
 *
 * Use is subject to license terms.
 */

package com.plavaga.document.deduplicate;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author chiranjithsrai
 *
 */
public class DocumentDeDuplication {

	private static final int	PORT			= constants.PORT;
	private static final String	SERVER_ADDRESS	= constants.SERVER_ADDRESS;
	private static final String	CORE_DB			= constants.CORE_DB;
	private static final String	DATABASE		= constants.DATABASE;
	static MongoClient			mongoClient;
	static DB					db;

	/**
	 *
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		Date start = new Date();
		configureMongo();
		copyDocuments();
		DeDuplicate deduplicate = new DeDuplicate();
		List<Map<String, Object>> templateList = readTemplates();
		for (Map<String, Object> template : templateList) {
			if (template.get("_id").toString().equalsIgnoreCase("568150a773feb012212fdb97")) {
				JSONObject duplicateKeySetJson = getdeduplicateKeySet(template);
				Map<String, Object> propertyThreasholdMap = generateQueryStringObject(duplicateKeySetJson, null);
				Object id = template.get(constants.METADATA_ID);
				JSONObject query = new JSONObject();
				query.put(constants.METADATA + '.' + constants.TEMPLATE_ID, id);

				Set<Object> duplicateDoculentSet = deduplicate.deDuplicate(query.toString(), propertyThreasholdMap, DATABASE);

				for (Object object : duplicateDoculentSet) {
					Map<String, Object> duplicateDoculent = (Map<String, Object>) object;
					Set<String> idSet = (Set<String>) duplicateDoculent.get("ID");
					String parentId = getParentDocId(idSet);
					deduplicateDocument(parentId, idSet);
				}
			}
		}
		Date end = new Date();
		System.out.println("deduplication time :: " + (end.getTime() - start.getTime()));

	}

	/**
	 *
	 * @param parentId
	 * @param idSet
	 */
	private static void deduplicateDocument(String parentId, Set<String> idSet) {

		if (parentId != "") {
			db = mongoClient.getDB(DATABASE);
			DBCollection dbCollection = db.getCollection(constants.COLLECTION);
			DB db2 = mongoClient.getDB(DATABASE + "_deduplicate");
			DBCollection db2Collection = db2.getCollection(constants.COLLECTION);

			idSet.remove(parentId);
			for (String id : idSet) {
				DBObject query = new BasicDBObject();
				query.put(constants.METADATA_ID, new ObjectId(id));
				DBObject object = dbCollection.findOne(query);
				DBObject metadataObject = (DBObject) object.get(constants.METADATA);
				metadataObject.put(constants.DUPLICATE_DOCID, parentId);
				db2Collection.update(query, object);
			}
		}
	}

	private static void copyDocuments() {

		db = mongoClient.getDB(DATABASE);

		DBCollection dbCollection = db.getCollection(constants.COLLECTION);
		DB db2 = mongoClient.getDB(DATABASE + "_deduplicate");
		db2.dropDatabase();
		DBCollection db2Collection = db2.getCollection(constants.COLLECTION);
		DBCursor cursor = dbCollection.find();
		while (cursor.hasNext()) {
			try {
				DBObject object = cursor.next();
				db2Collection.insert(object);
			}
			catch (Exception e) {

			}
		}
	}

	/**
	 *
	 * @param idSet
	 * @return String
	 */
	private static String getParentDocId(Set<String> idSet) {

		db = mongoClient.getDB(DATABASE);
		DBCollection dbCollection = db.getCollection(constants.COLLECTION);
		DBObject query = new BasicDBObject();
		query.put(constants.METADATA + "." + constants.WORKFLOW_STATUS, constants.PUBLISHED);
		Set<ObjectId> idObjectSet = new LinkedHashSet<ObjectId>();
		for (String id : idSet) {
			idObjectSet.add(new ObjectId(id));
		}
		query.put(constants.METADATA_ID, new BasicDBObject("$in", idObjectSet));
		DBCursor cursor = dbCollection.find(query);

		if (!(cursor.size() > 0)) {
			query.put(constants.METADATA + "." + constants.WORKFLOW_STATUS, constants.SCANNED);
			cursor = dbCollection.find(query);
		}

		DBObject orderBy = new BasicDBObject();
		orderBy.put(constants.METADATA + "." + constants.CREATED_ON, 1);
		cursor.sort(orderBy);
		String oldId = "";
		if (cursor.hasNext()) {
			DBObject object = cursor.next();
			Map<String, Object> objectAsMap = object.toMap();
			oldId = objectAsMap.get(constants.METADATA_ID).toString();
		}
		return oldId;
	}

	/**
	 *
	 */
	private static void configureMongo() {

		mongoClient = new MongoClient(SERVER_ADDRESS, PORT);
	}

	/**
	 *
	 * @return List<Map<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> readTemplates() {

		db = mongoClient.getDB(CORE_DB);
		DBCollection dbCollection = db.getCollection(constants.TEMPLATES);
		DBCursor cursor = dbCollection.find(new BasicDBObject());

		List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
		while (cursor.hasNext()) {
			DBObject object = cursor.next();
			Map<String, Object> objectAsMap = object.toMap();
			results.add(objectAsMap);
		}
		return results;
	}

	/**
	 *
	 * @param template
	 * @return JSONObject
	 */
	private static JSONObject getdeduplicateKeySet(Map<String, Object> template) {

		JSONObject templateBodyJsonObject = new JSONObject(template.get(constants.BODY).toString());
		Map<String, Object> result = getKeySet(templateBodyJsonObject);
		return new JSONObject(result);
	}

	/**
	 *
	 * @param queryMap
	 * @return Map<String, Object>
	 */
	private static Map<String, Object> getKeySet(JSONObject queryMap) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		Iterator<?> allKeys = queryMap.keys();
		while (allKeys.hasNext()) {
			String key = allKeys.next().toString();
			if (constants.DEDUPLICATION_THRESHOLD.equalsIgnoreCase(key)) {
				resultMap.put(key, queryMap.get(key));
			}
			else if (queryMap.get(key) instanceof JSONObject) {

				JSONObject object = queryMap.getJSONObject(key);
				Map<String, Object> result = getKeySet(object);
				JSONObject resultJson = new JSONObject(result);

				if (resultJson.length() > 0) {
					if (!constants.PROPERTIES.equalsIgnoreCase(key)) {
						resultMap.put(key, result);
					}
					else {
						resultMap.putAll(result);
					}
				}

			}
		}
		return resultMap;
	}

	/**
	 *
	 * @param queryObject
	 * @param keySet
	 * @return Map<String, Object>
	 */
	private static Map<String, Object> generateQueryStringObject(JSONObject queryObject, String keySet) {

		Map<String, Object> query = new HashMap<String, Object>();
		Iterator<?> allKeys = queryObject.keys();
		while (allKeys.hasNext()) {
			String key = allKeys.next().toString();
			String keyValue = null;
			if (keySet != null) {
				if (!constants.DEDUPLICATION_THRESHOLD.equalsIgnoreCase(key)) {
					keyValue = keySet + '.' + key;
				}
				else {
					keyValue = keySet;
				}
			}
			else {
				keyValue = key;
			}
			if (queryObject.get(key) instanceof JSONObject) {
				query.putAll(generateQueryStringObject(queryObject.getJSONObject(key), keyValue));
			}
			else {
				query.put(keyValue, queryObject.optString(key));
			}
		}
		return query;
	}
}
