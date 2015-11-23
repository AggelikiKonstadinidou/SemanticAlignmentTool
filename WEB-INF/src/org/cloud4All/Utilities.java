package org.cloud4All;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.cloud4All.ontology.ETNACluster;

public class Utilities {
	private static HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

	/**
	 * this method checks the size of the attributes toHideList and measures
	 * toHideList to set the flag of showing or not more cluster items
	 * 
	 * @param list
	 * @return
	 */
	public static List<List<ETNACluster>> setShowItemsFlagsForClusters(
			List<List<ETNACluster>> list) {

		for (List<ETNACluster> tempList : list) {
			for (ETNACluster temp : tempList) {
				if (temp.getAttributesToHide().size() > 0
						|| temp.getMeasuresToHide().size() > 0)
					temp.setHasToHideItems(true);

				temp.setShowItems(true);

			}
		}

		return list;
	}

	public static String getSystemCurrentDate() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		return dateFormat.format(date);
	}
	
	public static String getSystemCurrentTime() {

		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();

		return dateFormat.format(date);
	}
	
	public static String convertDateToFormat(Date date){
		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null)
			return formatter.format(date) + "T" + "00:00:00+02:00";
		else
			return "";
		
	}

	public static String convertSelectedHandlerType(String handlerType) {
		String convertedType = "";

		if (handlerType.equals("No")) {
			convertedType = "gpii.settingsHandlers.noSettings";
		} else if (handlerType.equals("Ini")) {
			convertedType = "gpii.settingsHandlers.INISettingsHandler.set";
		} else if (handlerType.equals("Registry")) {
			convertedType = "gpii.windows.registrySettingsHandler.set";
		} else if (handlerType.equals("Json")) {
			convertedType = "gpii.settingsHandlers.JSONSettingsHandler.set";
		} else if (handlerType.equals("Spi")) {
			convertedType = "gpii.windows.spiSettingsHandler.set";
		} else if (handlerType.equals("Xml")) {
			convertedType = "gpii.settingsHandlers.XMLHandler.set";
		} else if (handlerType.equals("G")) {
			convertedType = "gpii.gsettings.set";
		} else if (handlerType.equals("Orca")) {
			convertedType = "gpii.orca.set";
		} else if (handlerType.equals("Alsa")) {
			convertedType = "gpii.alsa.set";
		} else if (handlerType.equals("Xrandr")) {
			convertedType = "gpii.xrandr.set";
		} else if (handlerType.equals("Android Persistent Configuration")) {
			convertedType = "gpii.androidPersistentConfiguration.set";
		} else if (handlerType.equals("Android Audio Manager")) {
			convertedType = "gpii.androidAudioManager.setVolume";
		} else if (handlerType.equals("Android")) {
			convertedType = "gpii.androidSettings.set";
		}

		return convertedType;
	}

	public static String convertUnifiedListingHandlerToOntologyHandler(
			String handlerType) {
		String convertedType = "";

		if (handlerType.equals("gpii.settingsHandlers.noSettings")) {
			convertedType = "No";
		} else if (handlerType
				.equals("gpii.settingsHandlers.INISettingsHandler.set")) {
			convertedType = "Ini";
		} else if (handlerType
				.equals("gpii.windows.registrySettingsHandler.set")) {
			convertedType = "Registry";
		} else if (handlerType
				.equals("gpii.settingsHandlers.JSONSettingsHandler.set")) {
			convertedType = "Json";
		} else if (handlerType.equals("gpii.windows.spiSettingsHandler.set")) {
			convertedType = "Spi";
		} else if (handlerType.equals("gpii.settingsHandlers.XMLHandler.set")) {
			convertedType = "Xml";
		} else if (handlerType.equals("gpii.gsettings.set")) {
			convertedType = "G";
		} else if (handlerType.equals("gpii.orca.set")) {
			convertedType = "Orca";
		} else if (handlerType.equals("gpii.alsa.set")) {
			convertedType = "Alsa";
		} else if (handlerType.equals("gpii.xrandr.set")) {
			convertedType = "Xrandr";
		} else if (handlerType
				.equals("gpii.androidPersistentConfiguration.set")) {
			convertedType = "Android Persistent Configuration";
		} else if (handlerType.equals("gpii.androidAudioManager.setVolume")) {
			convertedType = "Android Audio Manager";
		} else if (handlerType.equals("gpii.androidSettings.set")) {
			convertedType = "Android";
		}

		return convertedType;
	}

	public static List<String> getDefaultOptionsForHandler(String handlerType) {
		List<String> options = new ArrayList<String>();

		List<String> ini = Arrays.asList("Path", "AllowNumberSignComments",
				"allowSubSections");
		List<String> registry = Arrays.asList("Path", "HKey", "Magnification",
				"Invert", "FollowFocus", "FollowCaret", "FollowMouse",
				"MagnificationMode", "Arrow", "Hand", "Help", "AppStarting",
				"No", "NWPen", "SizeAll", "SizeNESW", "SizeNS", "SizeNWSE",
				"SizeWE", "UpArrow", "Wait");
		List<String> json = Arrays.asList("Path");
		List<String> spi = Arrays.asList("GetAction", "SetAction", "UiParam",
				"Type", "Name");
		List<String> g = Arrays.asList("Schema");
		List<String> xml = Arrays.asList("Filename", "Encoding", "XmlTag",
				"Map", "Type", "InputPath", "Key");
		List<String> orca = Arrays.asList("User");
		List<String> android = Arrays.asList("SettingType");

		if (handlerType.equals("Ini")) {
			return ini;
		} else if (handlerType.equals("Registry")) {
			return registry;
		} else if (handlerType.equals("Json")) {
			return json;
		} else if (handlerType.equals("Spi")) {
			return spi;
		} else if (handlerType.equals("Xml")) {
			return xml;
		} else if (handlerType.equals("G")) {
			return g;
		} else if (handlerType.equals("Orca")) {
			return orca;
		} else if (handlerType.equals("Android")) {
			return android;
		} else
			return options;

	}
	
	static public String splitCamelCase(String s) {
		return s.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}
	
	public static String getHTTPGet(String link, String cookies)
			throws ClientProtocolException, IOException {

		DefaultHttpClient httpClient = Utilities.getClient();

		HttpGet get = new HttpGet(link);

		get.addHeader(HttpHeaders.COOKIE, cookies);
		get.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");

		HttpResponse response = httpClient.execute(get);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
	
	public static String getResultOfHTTPget(String link) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(link);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
			conn.disconnect();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("error in HTTP get!");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("error in HTTP get!");
		}

		return result;

	}
	
	
	public static DefaultHttpClient getClient() {

		DefaultHttpClient httpClient = null;
		HttpClient client = new DefaultHttpClient();

		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory
				.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		registry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));

		SingleClientConnManager mgr = new SingleClientConnManager(
				client.getParams(), registry);
		httpClient = new DefaultHttpClient(mgr, client.getParams());

		return httpClient;
	}

}
