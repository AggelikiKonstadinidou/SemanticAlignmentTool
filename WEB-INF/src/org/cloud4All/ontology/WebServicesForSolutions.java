package org.cloud4All.ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.net.ssl.HostnameVerifier;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.cloud4All.Solution;
import org.cloud4All.Utilities;
import org.cloud4All.ontology.ProductForJSON.AllRecords;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@ManagedBean(name = "webServicesForSolutions")
@SessionScoped
public class WebServicesForSolutions {

	public static String PRODUCTS_URL = "http://ul.gpii.net/api/products";
	private static String PRODUCT_URL = "http://ul.gpii.net/api/product";
	public static String signInForUnifiedListingApi = "http://ul.gpii.net/api/user/login";
	public static String SOURCE = "SolutionsOntologyFinal";

	public static void main(String args[]) throws ClientProtocolException,
			IOException {

	}

	public static HashMap<String, Solution> getWebServiceProducts(String url,String cookies)
			throws IOException, InterruptedException {

		HashMap<String, Solution> hashMap = new HashMap<String, Solution>();
		ArrayList<Solution> deletedSolutions = new ArrayList<Solution>();
	//	String result = Utilities.getResultOfHTTPget(url);
		String result = WebServicesForTerms.getRequestWithCurl(url, cookies);
		System.out.println(result);

		Gson gson = new Gson();

		Type type = new TypeToken<AllRecords>() {
		}.getType();

		AllRecords products = (AllRecords) gson.fromJson(result, type);

		ArrayList<Solution> solutions = null;
		for (ProductForJSON temp : products.getRecords()) {

			solutions = new ArrayList<Solution>();

			solutions = temp.convertProductForJSON();

			for (Solution sol : solutions) {

				if (sol.getOntologyCategory() != null && sol.getName() != null
						&& sol.getId() != null)
					if (!sol.getName().isEmpty() && !sol.getId().isEmpty()
							&& !sol.getStatus().equalsIgnoreCase("deleted"))
						hashMap.put(sol.getId(), sol);

				if (sol.getStatus().equals("deleted"))
					deletedSolutions.add(sol);
			}
		}

		return hashMap;

	}

	public static void solutionHTTPPostRequest(Solution sol, String cookies) {

		try {

			
			DefaultHttpClient httpClient = Utilities.getClient();

			HttpPost post = new HttpPost(PRODUCT_URL);
			// test source, existing database

			String sourceRecord = "";
			String editions = "";
			// empty iso codes for categories
			String code = "";
			String secondCatgrCodes = "";

			// String unifiedRecord = "{ \"source\": \"" + source
			// + "\", \"uid\" : \"" + source + sol.getId()
			// + "\", \"sid\": \"" + sol.getId() + "\", \"name\": \""
			// + sol.getName() + "\", \"description\": \""
			// + sol.getDescription()
			// + "\", \"manufacturer\":{ \"name\": \""
			// + sol.getManufacturerName() + "\", \"country\" : \""
			// + sol.getManufacturerCountry() + "\"}, \"status\" : \""
			// + status + "\", \"sources\": [" + sourceRecord
			// + "], \"editions\": " + editions
			// + "\"ontologies\": { \"iso9999\" :{ \"primaryCode\": \""
			// + code + "\", \"secondaryCodes\": [" + secondCatgrCodes
			// + "]} }";
			//
			// editions = getEditionsJson(sol);

			String properties = getPropertiesJson(sol);

			String secondaryCategories = "";
			if (sol.getCategories().size() > 0)
				secondaryCategories = getSecondaryCategories(sol
						.getCategories());
			String categoryName = Utilities.splitCamelCase(sol
					.getOntologyCategory());
			String currentDate = Utilities.getSystemCurrentDate() + "T"
					+ Utilities.getSystemCurrentTime() + "+02:00";

			String date = Utilities.convertDateToFormat(sol.getDate());
			String updateDate = Utilities.convertDateToFormat(sol
					.getUpdateDate());

			String mySourceRecord = "{\"source\":         \"" + SOURCE + "\","
					+ "\"sid\":            \"" + sol.getId() + "\","
					+ "\"name\":           \"" + sol.getName() + "\","
					+ "\"description\":    \"" + sol.getDescription() + "\","
					+ "\"manufacturer\":     {" + "\"name\":       \""
					+ sol.getManufacturerName() + "\","
					+ "\"address\":    \"\"," + "\"postalCode\": \"\","
					+ "\"cityTown\":   \"\"," + "\"country\":    \""
					+ sol.getManufacturerCountry() + "\","
					+ "\"phone\":      \"\"," + "\"email\":      \"\","
					+ "\"url\":        \"\"" + "},"
					+ "\"status\":         \"new\","
					+ "\"language\":        \"en_us\","
					+ "\"sourceData\":     {"
					+ "\"ManufacturerAddress\": \"\","
					+ "\"ManufacturerPostalCode\": \"\","
					+ "\"ManufacturerTown\": \"\","
					+ "\"ManufacturerCountry\": \""
					+ sol.getManufacturerCountry() + "\","
					+ "\"ManufacturerPhone\": \"\","
					+ "\"ManufacturerEmail\": \"\","
					+ "\"ManufacturerWebSiteUrl\": \""
					+ sol.getManufacturerWebsite() + "\"," + "\"ImageUrl\": \""
					+ sol.getImageUrl() + "\","
					+ "\"EnglishDescription\": \"\","
					+ "\"OriginalUrl\": \"\"," + "\"EnglishUrl\": \"\","
					+ "\"Features\": [" + properties + "],"
					+ "\"Database\": \"" + SOURCE + "\","
					+ "\"ProductCode\": \"" + sol.getId() + "\","
					+ "\"IsoCodePrimary\": {" + "\"Code\": \"\","
					+ "\"Name\": \"" + categoryName + "\"" + "},"
					+ "\"IsoCodesOptional\": [" + secondaryCategories + "],"
					+ "\"CommercialName\": \"" + sol.getName() + "\","
					+ "\"ManufacturerOriginalFullName\": \""
					+ sol.getManufacturerName() + "\"," + "\"InsertDate\": \""
					+ date + "\"," + "\"LastUpdateDate\": \"" + updateDate
					+ "\"," + "\"ThumbnailImageUrl\": \"" + sol.getImageUrl()
					+ "\"," + "\"SimilarityLevel\": 0" + "},"
					+ "\"updated\":        \"" + currentDate
					+ "\", \"uid\": \"" + sol.getId() + "\"}";

			post.addHeader(HttpHeaders.COOKIE, cookies);

			post.setEntity(new StringEntity(mySourceRecord, ContentType
					.create("application/json")));

			HttpResponse response = httpClient.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(result.toString());
		} catch (Exception e) {

		}
	}

	public static String getSecondaryCategories(List<String> categories) {
		String secondaryCategories = "";
		ArrayList<String> list = new ArrayList<String>();
		String temp = "";
		String code = "";

		for (String s : categories) {
			temp = "{ \"Code\": \"" + code + "\", \"Name\": \"" + s + "\"}";
			list.add(temp);
		}

		for (String s : list) {
			secondaryCategories = secondaryCategories.concat(s + ",");
		}

		if (!secondaryCategories.isEmpty())
			secondaryCategories = secondaryCategories.substring(0,
					secondaryCategories.lastIndexOf(","));

		return secondaryCategories;
	}

	public static String getPropertiesJson(Solution sol) {
		String properties = "";
		String id = "";
		String feature = "";
		ArrayList<String> featuresArray = new ArrayList<String>();

		// create a string properties that contains the json block features
		// for eastin properties of the application
		for (ETNACluster cl : sol.getClustersToShowInView()) {
			if (!cl.isSingleSelection()) {
				for (String s : cl.getSelectedProperties()) {
					id = "";
					for (EASTINProperty prop : cl.getAllproperties()) {
						if (prop.getName().equals(s)) {
							id = prop.getId();
							break;
						}
					}
					feature = "{\"FeatureId\": \"" + id
							+ "\", \"FeatureName\":\"" + s
							+ "\", \"FeatureParentName\":\"" + cl.getName()
							+ "\", \"ValueMin\": 0, \"ValueMax\":0}";
					featuresArray.add(feature);

				}
			} else {

				for (EASTINProperty prop : cl.getAllproperties()) {
					if (prop.getName().equals(cl.getSelectedProperty())) {
						id = prop.getId();
						break;
					}
				}

				feature = "{\"FeatureId\": \"" + id + "\", \"FeatureName\":\""
						+ cl.getSelectedProperty()
						+ "\", \"FeatureParentName\":\"" + cl.getName()
						+ "\", \"ValueMin\": 0, \"ValueMax\":0}";

				featuresArray.add(feature);

			}

		}

		for (String s : featuresArray) {
			properties = properties.concat(s + ",");
		}

		if (!properties.isEmpty())
			properties = properties.substring(0, properties.lastIndexOf(","));

		return properties;
	}

	public static String getEditionsJson(Solution sol) {
		String editions = "";

		String OS = "";
		// find operating system, default schema (only one selected os)
		for (ETNACluster cl : sol.getClustersToShowInView()) {
			if (cl.getName().equalsIgnoreCase("Operating System")) {
				OS = cl.getSelectedProperties().get(0);
				break;
			}
		}

		String settingsHandler = Utilities.convertSelectedHandlerType(sol
				.getHandlerType());

		editions = "{ \"default\": { \"contexts\": { \"OS\": [ { \"id\": \""
				+ OS
				+ "\", \"version\": \"\" }] }, \"settingsHandlers\": [ { \"type\": \""
				+ settingsHandler + "\", \"capabilities\":"
				+ sol.getCapabilities()
				+ "} ], \"lifecycleManager\": { \"start\": [ { \"command\": \""
				+ sol.getStartCommand() + "\"}], \"stop\": [ { \"command\": \""
				+ sol.getStopCommand() + "\"}]}";

		return editions;
	}

	public static void solutionHTTPPutRequest(Solution sol) {

		try {
			String cookies = WebServicesForTerms
					.loginAndGetCookies(signInForUnifiedListingApi);

			DefaultHttpClient httpClient = Utilities.getClient();

			HttpPut put = new HttpPut(PRODUCT_URL);

			String properties = getPropertiesJson(sol);

			String secondaryCategories = "";
			if (sol.getCategories().size() > 0)
				secondaryCategories = getSecondaryCategories(sol
						.getCategories());
			String categoryName = Utilities.splitCamelCase(sol
					.getOntologyCategory());
			String currentDate = Utilities.getSystemCurrentDate() + "T"
					+ Utilities.getSystemCurrentTime() + "+02:00";

			String date = Utilities.convertDateToFormat(sol.getDate());
			String updateDate = Utilities.convertDateToFormat(sol
					.getUpdateDate());

			String mySourceRecord = "{\"source\":         \"" + SOURCE + "\","
					+ "\"sid\":            \"" + sol.getId() + "\","
					+ "\"name\":           \"" + sol.getName() + "\","
					+ "\"description\":    \"" + sol.getDescription() + "\","
					+ "\"manufacturer\":     {" + "\"name\":       \""
					+ sol.getManufacturerName() + "\","
					+ "\"address\":    \"\"," + "\"postalCode\": \"\","
					+ "\"cityTown\":   \"\"," + "\"country\":    \""
					+ sol.getManufacturerCountry() + "\","
					+ "\"phone\":      \"\"," + "\"email\":      \"\","
					+ "\"url\":        \"\"" + "},"
					+ "\"status\":         \"new\","
					+ "\"language\":        \"en_us\","
					+ "\"sourceData\":     {"
					+ "\"ManufacturerAddress\": \"\","
					+ "\"ManufacturerPostalCode\": \"\","
					+ "\"ManufacturerTown\": \"\","
					+ "\"ManufacturerCountry\": \""
					+ sol.getManufacturerCountry() + "\","
					+ "\"ManufacturerPhone\": \"\","
					+ "\"ManufacturerEmail\": \"\","
					+ "\"ManufacturerWebSiteUrl\": \""
					+ sol.getManufacturerWebsite() + "\"," + "\"ImageUrl\": \""
					+ sol.getImageUrl() + "\","
					+ "\"EnglishDescription\": \"\","
					+ "\"OriginalUrl\": \"\"," + "\"EnglishUrl\": \"\","
					+ "\"Features\": [" + properties + "],"
					+ "\"Database\": \"" + SOURCE + "\","
					+ "\"ProductCode\": \"" + sol.getId() + "\","
					+ "\"IsoCodePrimary\": {" + "\"Code\": \"\","
					+ "\"Name\": \"" + categoryName + "\"" + "},"
					+ "\"IsoCodesOptional\": [" + secondaryCategories + "],"
					+ "\"CommercialName\": \"" + sol.getName() + "\","
					+ "\"ManufacturerOriginalFullName\": \""
					+ sol.getManufacturerName() + "\"," + "\"InsertDate\": \""
					+ date + "\"," + "\"LastUpdateDate\": \"" + updateDate
					+ "\"," + "\"ThumbnailImageUrl\": \"" + sol.getImageUrl()
					+ "\"," + "\"SimilarityLevel\": 0" + "},"
					+ "\"updated\":        \"" + currentDate + "\"}";

			put.addHeader(HttpHeaders.COOKIE, cookies);

			put.setEntity(new StringEntity(mySourceRecord, ContentType
					.create("application/json")));

			HttpResponse response = httpClient.execute(put);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(result.toString());
		} catch (Exception e) {

		}
	}

	public static void httpDeleteRequestForSolution(String id) {

		try {
			String cookies = WebServicesForTerms
					.loginAndGetCookies(signInForUnifiedListingApi);

			DefaultHttpClient httpClient = Utilities.getClient();

			HttpDelete delete = new HttpDelete(PRODUCT_URL + "/" + SOURCE + "/"
					+ id);

			delete.addHeader("cookie", cookies);

			HttpResponse response = httpClient.execute(delete);
			System.out.println("\nSending 'DELETE' request to URL : "
					+ PRODUCT_URL);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
