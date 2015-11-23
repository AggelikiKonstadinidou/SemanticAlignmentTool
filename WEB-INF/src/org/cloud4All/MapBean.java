package org.cloud4All;

import java.io.BufferedReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.cloud4All.IPR.PerCountryReputation;
import org.cloud4All.IPR.PerCountrySolutionUsage;
import org.cloud4All.ontology.OntologyInstance;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@ManagedBean(name = "mapBean")
@SessionScoped
public class MapBean implements Serializable {

	private MapModel advancedModel;

	private Marker marker;
	private Solution selectedSolution = new Solution();

	public MapBean() {
		advancedModel = new DefaultMapModel();
		getStatistics();

	}

	public void getStatistics() {
		advancedModel = new DefaultMapModel();
		// load countries from selected solution
		Solution sol = getSelectedSolution();
		for (int i = 0; i < sol.getUsageStatistics().getUsagePerCountry()
				.size(); i++) {
			PerCountrySolutionUsage usage = sol.getUsageStatistics()
					.getUsagePerCountry().get(i);
			String country = usage.getForCountry();

			CountryCodes countries = new CountryCodes();

			String code = countries.getCode(country);
			// call web service to get coordinates
			Coordinates coor = getCoordinates(country, code);
			LatLng coord1 = new LatLng(coor.getLatitude(), coor.getLongitude());
			String eol = System.getProperty("line.separator");
			String str = usage.getForCountry() + eol + "Total usages: "
					+ Integer.toString(usage.getNrOfUsages());
			str = str + eol + "Number of usages: "
					+ Integer.toString(usage.getNrOfUsers());
			advancedModel.addOverlay(new Marker(coord1, str));
		}
	}

	public void getStatisticsForVendorReputation() {
		advancedModel = new DefaultMapModel();
		// load countries from selected solution
		FacesContext context = FacesContext.getCurrentInstance();

		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		for (int i = 0; i < userBean.getVendorObj().getReputation()
				.getPerCountryReputation().size(); i++) {
			PerCountryReputation usage = userBean.getVendorObj()
					.getReputation().getPerCountryReputation().get(i);
			String country = usage.getForCountry();
			CountryCodes countries = new CountryCodes();

			String code = countries.getCode(country);
			// call web service to get coordinates
			Coordinates coor = getCoordinates(country, code);
			LatLng coord1 = new LatLng(coor.getLatitude(), coor.getLongitude());
			String eol = System.getProperty("line.separator");
			String str = usage.getForCountry() + eol + "Reputation score: "
					+ Float.toString(usage.getReputationScore());

			advancedModel.addOverlay(new Marker(coord1, str));
		}
	}

	public class Coordinates {
		private double longitude;
		private double latitude;

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

	}

	public Coordinates getCoordinates(String country, String countryCode) {
		Coordinates c = new Coordinates();
		try {
			System.out.println("Requesting for " + country);
			String request = "http://ws.geonames.org/search?country="
					+ countryCode + "&name=" + country.replace(" ", "%20")
					+ "&maxRows=1";
			URL url = new URL(request);
			System.out.println(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			String str = "";
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				str = str + output + "\n";
			}
			// parse xml
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(str));
				Document doc = builder.parse(is);

				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("geoname");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						c.setLongitude(Double.parseDouble(eElement
								.getElementsByTagName("lng").item(0)
								.getTextContent()));
						c.setLatitude(Double.parseDouble(eElement
								.getElementsByTagName("lat").item(0)
								.getTextContent()));
						conn.disconnect();
						return c;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return c;
	}

	public MapModel getAdvancedModel() {
		return advancedModel;
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		marker = (Marker) event.getOverlay();
	}

	public Marker getMarker() {
		return marker;
	}

	public Solution getSelectedSolution() {
		return selectedSolution;
	}

	public void setSelectedSolution(Solution selectedSolution) {
		this.selectedSolution = selectedSolution;
		if (selectedSolution != null) {
			getStatistics();
		}
	}

	public void setAdvancedModel(MapModel advancedModel) {
		this.advancedModel = advancedModel;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

}
