package org.cloud4All.ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.net.ssl.HostnameVerifier;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.cloud4All.Setting;
import org.cloud4All.Utilities;
import org.cloud4All.ontology.MapTerm.Record;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WebServicesForTerms {
	public static String ALL_TERMS_WITH_CHILDREN_URL = "https://terms.raisingthefloor.org/api/terms";
	private static String COMMON_URL = "https://terms.raisingthefloor.org/api/record";
	public static String ALIAS_URL = "https://terms.raisingthefloor.org/api/aliases?limit=-1";
	public static String signInForDictionaryTermsApi = "https://terms.raisingthefloor.org/api/user/signin";
	private static String cURLin183 = "C:\\Program Files\\Git\\bin\\";
	private static String mycURL = "C:\\Program Files\\cURL\\bin\\";
	private static String usingCURL = cURLin183;

	public static HashMap<String, RegistryTerm> getWebServiceRegistryTerms(
			String url,String cookies) throws ClientProtocolException, IOException, InterruptedException {
		// list with terms with status = deleted
		ArrayList<RegistryTerm> deletedTerms = new ArrayList<RegistryTerm>();

		//String result = Utilities.getResultOfHTTPget(url);
		String result = WebServicesForTerms.getRequestWithCurl(url, cookies);

		Gson gson = new Gson();

		Type type = new TypeToken<RegistryTermForJSONNew.AllRecords>() {
		}.getType();

		 System.out.println(result);

		RegistryTermForJSONNew.AllRecords allRecords = (RegistryTermForJSONNew.AllRecords) gson
				.fromJson(result.toString(), type);

		// create registry term objects for ontology

		HashMap<String, RegistryTerm> ws_terms = new HashMap<String, RegistryTerm>();
		RegistryTerm rterm = null;

		for (RegistryTermForJSONNew.Record temp : allRecords.getRecords()) {

			// convert RegistryTermJSON to Registry term
			rterm = temp.convertJSONTermToOntologyTerm();

			// add registry term in the hashMap
			if (!rterm.getId().isEmpty() && rterm.getId() != null
					&& !rterm.getStatus().equals("deleted"))
				ws_terms.put(rterm.getId(), rterm);

			else if (rterm.getStatus().equals("deleted"))
				deletedTerms.add(rterm);

		}

		 System.out.println(ws_terms.size());
		return ws_terms;
	}

	public static HashMap<String, Setting> getWebServiceAliases(String url,String cookies)
			throws ClientProtocolException, IOException, InterruptedException {
		// list with terms with status = deleted
		ArrayList<Setting> deletedTerms = new ArrayList<Setting>();

	//	String result = Utilities.getResultOfHTTPget(url);
		String result = WebServicesForTerms.getRequestWithCurl(url, cookies);

		Gson gson = new Gson();

		Type type = new TypeToken<RegistryTermForJSONNew.AllAliases>() {
		}.getType();

		// System.out.println(result);

		RegistryTermForJSONNew.AllAliases allRecords = (RegistryTermForJSONNew.AllAliases) gson
				.fromJson(result.toString(), type);

		// create registry term objects for ontology

		HashMap<String, Setting> ws_terms = new HashMap<String, Setting>();
		Setting sett = null;

		for (RegistryTermForJSONNew.Record.Alias temp : allRecords.getRecords()) {

			// System.out.println(temp.toString());

			sett = temp.convertJSONAlias();

			if (sett.getId() != null && sett.getSolutionId() != null)
				if (!sett.getId().isEmpty() && !sett.getSolutionId().isEmpty())
					ws_terms.put(sett.getId(), sett);

		}

		// System.out.println(ws_terms.size());
		return ws_terms;
	}

	public static void updateRegistryTernHTTP(RegistryTerm term) {

		try {

			String cookies = loginAndGetCookies(signInForDictionaryTermsApi);

			DefaultHttpClient httpClient = Utilities.getClient();

			HttpPut put = new HttpPut(COMMON_URL);

			put.addHeader("cookie", cookies);

			String status = "";
			ArrayList<String> aliasesArray = new ArrayList<String>();
			String jsonTerm = "";
			for (RegistryTerm temp : term.getAliasesTerms()) {

				if (temp.getStatus().equals("accepted"))
					status = "active";
				else
					status = "unreviewed";

				jsonTerm = "{\"uniqueId\":\"" + temp.getId() + "\",\"type\":\""
						+ "alias" + "\",\"aliasOf\":\"" + term.getId()
						+ "\",\"termLabel\":\"" + temp.getName()
						+ "\",\"notes\":\"" + temp.getNotes()
						+ "\",\"status\":\"" + status + "\" }";
				aliasesArray.add(jsonTerm);
			}

			jsonTerm = "";
			for (String s : aliasesArray) {
				jsonTerm = jsonTerm.concat(s + ",");
			}

			if (!jsonTerm.isEmpty())
				jsonTerm = jsonTerm.substring(0, jsonTerm.lastIndexOf(",")); // -1

			if (term.getStatus().equals("accepted"))
				status = "active";
			else
				status = "unreviewed";

			put.setEntity(new StringEntity("{\"uniqueId\":\"" + term.getId()
					+ "\",\"type\":\"" + "term" + "\",\"termLabel\":\""
					+ term.getName() + "\", \"definition\":\""
					+ term.getDescription() + "\",\"notes\":\""
					+ term.getNotes() + "\", \"aliases\":[" + jsonTerm
					+ "], \"valueSpace\":\"" + term.getValueSpace()
					+ "\",\"status\":\"" + status + "\" }", ContentType
					.create("application/json")));

			HttpResponse response = httpClient.execute(put);
			System.out
					.println("\nSending 'PUT' request to URL : " + COMMON_URL);
			System.out.println("Put parameters : " + put.getEntity());
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

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

	public static void insertRegistryTerm(RegistryTerm term, String cookies) {

		try {

			if (cookies.isEmpty())
				cookies = loginAndGetCookies(signInForDictionaryTermsApi);

			DefaultHttpClient httpClient = Utilities.getClient();

			HttpPost post = new HttpPost(COMMON_URL);

			post.addHeader("cookie", cookies);

			ArrayList<String> aliasesArray = new ArrayList<String>();
			String status = "unreviewed";
			String jsonTerm = "";
			for (RegistryTerm temp : term.getAliasesTerms()) {

				jsonTerm = "{\"uniqueId\":\"" + temp.getId() + "\",\"type\":\""
						+ "alias" + "\",\"aliasOf\":\"" + term.getId()
						+ "\",\"termLabel\":\"" + temp.getName()
						+ "\",\"notes\":\"" + temp.getNotes()
						+ "\",\"status\":\"" + status + "\" }";
				aliasesArray.add(jsonTerm);
			}

			jsonTerm = "";
			for (String s : aliasesArray) {
				jsonTerm = jsonTerm.concat(s + ",");
			}

			if (!jsonTerm.isEmpty())
				jsonTerm = jsonTerm.substring(0, jsonTerm.lastIndexOf(",")); // -1

			post.setEntity(new StringEntity("{\"uniqueId\":\"" + term.getId()
					+ "\",\"type\":\"" + "term" + "\",\"defaultvalue\":\""
					+ term.getDefaultValue() + "\",\"termLabel\":\""
					+ term.getName() + "\", \"definition\":\""
					+ term.getDescription() + "\",\"notes\":\""
					+ term.getNotes() + "\", \"aliases\":[" + jsonTerm
					+ "], \"valueSpace\":\"" + term.getValueSpace()
					+ "\",\"status\":\"" + status + "\" }", ContentType
					.create("application/json")));

			HttpResponse response = httpClient.execute(post);
			System.out.println("\nSending 'POST' request to URL : "
					+ COMMON_URL);
			// System.out.println("Post parameters : " + post.getEntity());
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void insertSetting(Setting setting, String ulUri,
			String cookies, String solID) {

		try {

			if (cookies.isEmpty())
				cookies = loginAndGetCookies(signInForDictionaryTermsApi);

			DefaultHttpClient httpClient = Utilities.getClient();

			HttpPost post = new HttpPost(COMMON_URL);

			String notes = "";
			post.addHeader("cookie", cookies);
			post.setEntity(new StringEntity("{\"uniqueId\":\"" + solID + "_"
					+ setting.getId() + "\",\"type\":\"" + "alias"
					+ "\",\"termLabel\":\"" + setting.getName()
					+ "\",\"notes\":\"" + notes + "\", \"ulUri\":\"" + ulUri
					+ "\",\"status\":\"" + "unreviewed" + "\", \"aliasOf\": \""
					+ setting.getMappingId() + "\"}", ContentType
					.create("application/json")));

			HttpResponse response = httpClient.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO
	public static void main(String args[]) throws IOException, InterruptedException{
		String cookies = loginAndGetCookiesWithCurl(WebServicesForTerms.signInForDictionaryTermsApi);
		getRequestWithCurl(WebServicesForTerms.ALL_TERMS_WITH_CHILDREN_URL,
				cookies);
	//	String cookies2 = loginAndGetCookiesWithCurl(WebServicesForSolutions.signInForUnifiedListingApi);

	//	getRequestWithCurl(WebServicesForSolutions.PRODUCTS_URL, cookies2);

	}

	public static String loginAndGetCookies(String url) {
		String cookies = "";

		try {

			DefaultHttpClient httpClient = Utilities.getClient();

			// log in
			HttpPost post = new HttpPost(url);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("name", "aggeliki"));
			urlParameters.add(new BasicNameValuePair("password", "1234"));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = httpClient.execute(post);

			// System.out.println("Sending 'POST' request to URL : "
			// + " https://terms.raisingthefloor.org/api/user/signin");
			// System.out.println("Post parameters : " + post.getEntity());
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			post.releaseConnection();
			Header[] headers = response.getAllHeaders();

			// get cookies from headers of response
			for (Header header : headers) {
				// System.out.println(" --> " + header.getName() + ":"
				// + header.getValue());
				if (header.getName().equalsIgnoreCase("Set-Cookie"))
					cookies = header.getValue();
			}

			System.out.println(cookies);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return cookies;

	}
	
	public static String loginAndGetCookiesWithCurl(String url) throws IOException {
		String cookies = "";
		ProcessBuilder p = null;
		
		if (url.contains("ul.gpii.net"))
			p = new ProcessBuilder(usingCURL + "curl.exe", "-v", "-X", "POST",
					"--data", "username=aggeliki", "--data", "password=1234",
					url);
		else
			p = new ProcessBuilder(usingCURL + "curl.exe", "-v", "-X", "POST",
					"--data", "name=aggeliki", "--data", "password=1234", url);
		
		
		final Process shell = p.start();
		InputStream errorStream = shell.getErrorStream();
		InputStream shellIn = shell.getInputStream();

		// read error Stream
		Reader r = new InputStreamReader(errorStream);
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[4 * 1024];
		int len;
		while ((len = r.read(chars)) >= 0) {
			sb.append(chars, 0, len);
		}

		// split cookies from response
		String[] splitted = sb.toString().split("<");
		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i].contains("Set-Cookie") ||
					splitted[i].contains("set-cookie")) {
				cookies = splitted[i];
				break;
			}
		}

		// System.out.println(sb);

		// read Shell in
		Reader r2 = new InputStreamReader(shellIn);
		StringBuilder sb2 = new StringBuilder();
		char[] chars2 = new char[4 * 1024];
		int len2;
		while ((len2 = r2.read(chars2)) >= 0) {
			sb2.append(chars2, 0, len2);
		}

		System.out.println(sb2);
		cookies = cookies.replace("Set-Cookie: ", "").trim();
		cookies = cookies.replace("set-cookie: ", "").trim();
		System.out.println("Cookies: " + cookies);

		return cookies;
	}


	public static void postSettingWithCurl(String cookies, Setting setting,
			String ulUri, String solID) throws IOException {

		String notes = "";
		String json = "{\"uniqueId\":\"" + solID + "_" + setting.getId()
				+ "\",\"type\":\"" + "alias" + "\",\"termLabel\":\""
				+ setting.getName() + "\",\"notes\":\"" + notes
				+ "\", \"ulUri\":\"" + ulUri + "\",\"status\":\""
				+ "unreviewed" + "\", \"aliasOf\": \"" + setting.getMappingId()
				+ "\"}";

		File file = new File(usingCURL + "recordSAT.json");
		FileUtils.writeStringToFile(file, json);

		ProcessBuilder p = new ProcessBuilder(usingCURL + "curl.exe", "-v",
				"-X", "POST", "-b", cookies.trim(), "--data", "@" + usingCURL
						+ "recordSAT.json", "-H",
				"Content-Type: application/json",
				"https://terms.raisingthefloor.org/api/record/");

		final Process shell = p.start();
		InputStream errorStream = shell.getErrorStream();
		InputStream shellIn = shell.getInputStream();

		Reader r = new InputStreamReader(errorStream);
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[4 * 1024];
		int len;
		while ((len = r.read(chars)) >= 0) {
			sb.append(chars, 0, len);
		}

		// System.out.println(sb);
		// split cookies from response
		String responseResult = "";
		String[] splitted = sb.toString().split("<");
		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i].contains(" HTTP/1.1 200 OK")) {
				responseResult = splitted[i];
				break;
			}
		}

		if (!responseResult.contains("200 OK"))
			System.out.println("error in POST");
		else
			System.out.println("Response result: " + responseResult);
	}
	
	public static String getRequestWithCurl(String url, String cookies)
			throws IOException, InterruptedException {

		ProcessBuilder p = new ProcessBuilder(usingCURL + "curl.exe", "-X",
				"GET", "-b", cookies.trim(), "-H",
				"Content-Type: application/json", url);

		final Process shell = p.start();

		InputStream errorStream = shell.getErrorStream();
		InputStream inputStream = shell.getInputStream();

		Reader r = new InputStreamReader(inputStream);
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[4 * 1024];
		int len;
		while ((len = r.read(chars)) >= 0) {
			sb.append(chars, 0, len);
		}
		
		System.out.println(sb.toString());
		String result = sb.toString();
		if(result.contains("\"ok\":true")||result.contains("\"ok\":\"true\""))
				System.out.println("Response result: ok:true");
		else
			System.out.println("error occured in request");
		
		return result;
	}

	public static void postRegistryTermWithCurl(String cookies,
			RegistryTerm term) throws IOException {

		ArrayList<String> aliasesArray = new ArrayList<String>();
		String status = "unreviewed";
		String jsonTerm = "";
		for (RegistryTerm temp : term.getAliasesTerms()) {

			jsonTerm = "{\"uniqueId\":\"" + temp.getId() + "\",\"type\":\""
					+ "alias" + "\",\"aliasOf\":\"" + term.getId()
					+ "\",\"termLabel\":\"" + temp.getName()
					+ "\",\"notes\":\"" + temp.getNotes() + "\",\"status\":\""
					+ status + "\" }";
			aliasesArray.add(jsonTerm);
		}

		jsonTerm = "";
		for (String s : aliasesArray) {
			jsonTerm = jsonTerm.concat(s + ",");
		}

		if (!jsonTerm.isEmpty())
			jsonTerm = jsonTerm.substring(0, jsonTerm.lastIndexOf(",")); // -1

		String json = "{\"uniqueId\":\"" + term.getId() + "\",\"type\":\""
				+ "term" + "\",\"defaultvalue\":\"" + term.getDefaultValue()
				+ "\",\"termLabel\":\"" + term.getName()
				+ "\", \"definition\":\"" + term.getDescription()
				+ "\",\"notes\":\"" + term.getNotes() + "\", \"aliases\":["
				+ jsonTerm + "], \"valueSpace\":\"" + term.getValueSpace()
				+ "\",\"status\":\"" + status + "\" }";

		File file = new File(usingCURL + "recordSAT.json");
		FileUtils.writeStringToFile(file, json);

		ProcessBuilder p = new ProcessBuilder(usingCURL + "curl.exe", "-v",
				"-X", "POST", "-b", cookies.trim(), "--data", "@" + usingCURL
						+ "recordSAT.json", "-H",
				"Content-Type: application/json",
				"https://terms.raisingthefloor.org/api/record/");

		final Process shell = p.start();
		InputStream errorStream = shell.getErrorStream();
		InputStream shellIn = shell.getInputStream();

		Reader r = new InputStreamReader(errorStream);
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[4 * 1024];
		int len;
		while ((len = r.read(chars)) >= 0) {
			sb.append(chars, 0, len);
		}

		// System.out.println(sb);
		// split cookies from response
		String responseResult = "";
		String[] splitted = sb.toString().split("<");
		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i].contains(" HTTP/1.1 200 OK")) {
				responseResult = splitted[i];
				break;
			}
		}

		if (!responseResult.contains("200 OK"))
			System.out.println("error in POST");
		else
			System.out.println("Response result: " + responseResult);
	}

	public static void deleteRecordWithCurl(String cookies, String id)
			throws IOException {

		ProcessBuilder p = new ProcessBuilder(usingCURL + "curl.exe", "-v",
				"-X", "DELETE", "-b", cookies.trim(),
				"https://terms.raisingthefloor.org/api/record/" + id);

		final Process shell = p.start();
		InputStream errorStream = shell.getErrorStream();
		InputStream shellIn = shell.getInputStream();

		Reader r = new InputStreamReader(errorStream);
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[4 * 1024];
		int len;
		while ((len = r.read(chars)) >= 0) {
			sb.append(chars, 0, len);
		}

		// System.out.println(sb);
		// split cookies from response
		String responseResult = "";
		String[] splitted = sb.toString().split("<");
		for (int i = 0; i < splitted.length; i++) {
			if (splitted[i].contains(" HTTP/1.1 200 OK")) {
				responseResult = splitted[i];
				break;
			}
		}

		if (!responseResult.contains("200 OK"))
			System.out.println("error in POST");
		else
			System.out.println("Response result: " + responseResult);
	}

	

	public static void httpDeleteRequestForTerm(String id, String cookies) {

		try {

			if (cookies.isEmpty()) {
				cookies = loginAndGetCookies(signInForDictionaryTermsApi);
			}

			DefaultHttpClient httpClient = Utilities.getClient();

			HttpDelete delete = new HttpDelete(
					"https://terms.raisingthefloor.org/api/record/" + id);

			delete.addHeader("cookie", cookies);

			HttpResponse response = httpClient.execute(delete);
			System.out.println("\nSending 'DELETE' request to URL : "
					+ COMMON_URL);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

		} catch (Exception e) {

		}
	}

}
