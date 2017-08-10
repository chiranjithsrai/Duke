/**
 * Class Name:			Geocoding
 * Created On:			2:59:02 PM, 09-Aug-2017
 *
 * Copyright (c) 2012 Plavaga Software Solutions (P) Ltd. All rights reserved.
 *
 * Use is subject to license terms.
 */

package com.plavaga.document.deduplicate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class LatitudeAndLongitudeWithPincode {

	public static void main(String[] args) {

		System.setProperty("java.net.useSystemProxies", "true");
		String address = "Plavaga Software solutions pvt. ltd";
		try {
			getLatLongPositions(address);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getLatLongPositions(String address) throws Exception {

		int responseCode = 0;
		String api = "http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=true";
		System.out.println("URL : " + api);
		URL url = new URL(api);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.connect();
		responseCode = httpConnection.getResponseCode();
		if (responseCode == 200) {

			BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println("Response : -- " + response.toString());

			JSONObject resultJson = new JSONObject(response.toString());

			JSONArray resultArray = resultJson.getJSONArray("results");

			System.out.println("resultArray : -- " + resultArray.toString());

			for (int i = 0; i < resultArray.length(); i++) {

				System.out.println("input address : " + address);

				System.out.println("===================================================");
				System.out.println("\nformatted_address : " + resultArray.getJSONObject(i).getString("formatted_address"));
				System.out.println("\ntypes : " + resultArray.getJSONObject(i).get("types"));
				System.out.println("location : " + resultArray.getJSONObject(i).getJSONObject("geometry").get("location"));

				JSONArray addressComponents = resultArray.getJSONObject(i).getJSONArray("address_components");
				for (int j = 0; j < addressComponents.length(); j++) {
					System.out.println("\n\ntypes : " + addressComponents.getJSONObject(j).get("types"));
					System.out.println("short_name : " + addressComponents.getJSONObject(j).getString("short_name"));
					System.out.println("long_name : " + addressComponents.getJSONObject(j).getString("long_name"));
				}

				System.out.println("===================================================");
			}

			/*
			 * XML formed data DocumentBuilder builder =
			 * DocumentBuilderFactory.newInstance().newDocumentBuilder();
			 * Document document =
			 * builder.parse(httpConnection.getInputStream()); XPathFactory
			 * xPathfactory = XPathFactory.newInstance(); XPath xpath =
			 * xPathfactory.newXPath(); XPathExpression expr =
			 * xpath.compile("/GeocodeResponse/status"); String status =
			 * (String) expr.evaluate(document, XPathConstants.STRING); if
			 * (status.equals("OK")) { expr =
			 * xpath.compile("//geometry/location/lat"); String latitude =
			 * (String) expr.evaluate(document, XPathConstants.STRING); expr =
			 * xpath.compile("//geometry/location/lng"); String longitude =
			 * (String) expr.evaluate(document, XPathConstants.STRING); return
			 * new String[] { latitude, longitude }; } else { throw new
			 * Exception("Error from the API - response status: " + status); }
			 */
		}
	}
}
