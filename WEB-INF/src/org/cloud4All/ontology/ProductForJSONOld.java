package org.cloud4All.ontology;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.cloud4All.Solution;

public class ProductForJSONOld {

		private String source;
		private String uid;
		private String sid;
		private String name;
		private String description;
		private Manufacturer manufacturer;
		private String status;
		private String language;
		private ArrayList<SourceRecord> sources = new ArrayList<SourceRecord>();
		private Editions editions;
		private Ontologies ontologies;
		private String updated;

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Manufacturer getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(Manufacturer manufacturer) {
			this.manufacturer = manufacturer;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public ArrayList<SourceRecord> getSources() {
			return sources;
		}

		public void setSources(ArrayList<SourceRecord> sources) {
			this.sources = sources;
		}

		public Editions getEditions() {
			return editions;
		}

		public void setEditions(Editions editions) {
			this.editions = editions;
		}

		public Ontologies getOntologies() {
			return ontologies;
		}

		public void setOntologies(Ontologies ontologies) {
			this.ontologies = ontologies;
		}

		public String getUpdated() {
			return updated;
		}

		public void setUpdated(String updated) {
			this.updated = updated;
		}
		
		@Override
		public String toString() {
			return "ProductRecord [source=" + source + ", uid=" + uid
					+ ", sid=" + sid + ", name=" + name + ", description="
					+ description + ", manufacturer=" + manufacturer
					+ ", status=" + status + ", language=" + language
					+ ", sources=" + sources + ", editions=" + editions
					+ ", ontologies=" + ontologies + ", updated=" + updated
					+ "]";
		}

		public ArrayList<Solution> convertProductForJSON(){
			Solution sol = null;
			FacesContext context = FacesContext.getCurrentInstance();
			OntologyInstance ontologyInstance = (OntologyInstance) context
					.getApplication().evaluateExpressionGet(context,
							"#{ontologyInstance}", OntologyInstance.class);
			ArrayList<Solution> solutions = new ArrayList<Solution>();
			
			for (SourceRecord temp : this.getSources()) {

				// get solution info
				sol = new Solution();
				
			if (temp.getName() != null)
				sol.setName(temp.getName());

			if (temp.getSid() != null)
				sol.setId(temp.getSid());

			if (temp.getDescription() != null)
				sol.setDescription(temp.getDescription());

			if (temp.getSourceData() != null
					&& temp.getSourceData().getManufacturerCountry() != null)
				sol.setManufacturerCountry(temp.getSourceData()
						.getManufacturerCountry());

			if (temp.getManufacturer() != null
					&& temp.getManufacturer().getName() != null)
				sol.setManufacturerName(temp.getManufacturer().getName());

			if (temp.getSourceData() != null
					&& temp.getSourceData().getManufacturerWebSiteUrl() != null)
				sol.setManufacturerWebsite(temp.getSourceData()
						.getManufacturerWebSiteUrl());

				//get eastin properties
			if (temp.getSourceData() != null
					&& temp.getSourceData().getFeatures() != null) {
				
				ArrayList<Feature> properties = temp.getSourceData()
						.getFeatures();
				if (properties.size() > 0) {

					sol.setClustersToShowInView(new ArrayList<ETNACluster>());
					sol.setClustersToShow(new ArrayList<ETNACluster>());
					sol.setClustersToHide(new ArrayList<ETNACluster>());

					// prepare clusters for the solutions
					// clusters to show and clusters to show in view
					ArrayList<ETNACluster> clustersToShow = new ArrayList<ETNACluster>();
					for (Feature ft : properties) {
						String clusterName = ft.getFeatureParentName();
						String selectedPropertyName = ft.getFeatureName();

						// find the cluster
						for (ETNACluster cl : ontologyInstance
								.getEtnaClusterOriginal()) {
							if (cl.getName().equalsIgnoreCase(clusterName)) {

								clustersToShow.add(cl);
								ETNACluster clusterClone = cl.clone();
								// check the selected properties
								for (EASTINProperty prop : clusterClone
										.getAttributesToShowInView()) {
									if (prop.getName().equalsIgnoreCase(
											selectedPropertyName)) {
										clusterClone.getSelectedProperties()
												.add(selectedPropertyName);
									}
								}

								sol.getClustersToShowInView().add(clusterClone);
								sol.getClustersToShow().add(clusterClone);

							}
						}
					}
					// prepare clusters to hide
					for (ETNACluster cl : ontologyInstance
							.getEtnaClusterOriginal()) {
						if (!clustersToShow.contains(cl)) {
							sol.getClustersToHide().add(cl);
						}
					}

				} else {
					// if properties are empty, all clusters are hidden
					for (ETNACluster cl : ontologyInstance
							.getEtnaClusterOriginal()) {
						sol.getClustersToHide().add(cl.clone());
					}
				}
			}
			
			
			// get primary category of the application
			String categoryName = "";

			if (temp.getSourceData() != null
					&& temp.getSourceData().getIsoCodePrimary() != null)
				categoryName = temp.getSourceData().getIsoCodePrimary()
						.getName().replace(" ", "");

			sol.setOntologyCategory(categoryName);

			// get secondary categories

			ArrayList<String> secondaryCategories = new ArrayList<String>();

			if (temp.getSourceData().getIsoCodesOptional() != null) {
				for (IsoCodePrimary code : temp.getSourceData()
						.getIsoCodesOptional()) {
					secondaryCategories.add(code.getName().replace(" ", ""));
				}
			}
			sol.setCategories(secondaryCategories);

			if (temp.getSourceData().getThumbnailImageUrl() != null)
				sol.setImageUrl(temp.getSourceData().getThumbnailImageUrl());

			// get dates
			String date = "";
			String updateDate = "";

			if (temp.getSourceData().getInsertDate() != null) {
				date = temp.getSourceData().getInsertDate();
				date = date.substring(0, date.indexOf("T"));
			}

			if (temp.getSourceData().getLastUpdateDate() != null) {
				updateDate = temp.getSourceData().getLastUpdateDate();
				updateDate = updateDate.substring(0, updateDate.indexOf("T"));
			}

			Date insertDate = convertStringToDate(date);
			Date lastUpdateDate = convertStringToDate(updateDate);
			sol.setDate(insertDate);
			sol.setUpdateDate(lastUpdateDate);

			// add solution to the list
			solutions.add(sol);
			}
			return solutions;

		}

	public class Ontologies {
		private iso9999 iso9999;

		public iso9999 getIso9999() {
			return iso9999;
		}

		public void setIso9999(iso9999 iso9999) {
			this.iso9999 = iso9999;
		}

	}

	public class iso9999 {
		private IsoCodePrimary IsoCodePrimary;
		private ArrayList<IsoCodePrimary> IsoCodesOptional = new ArrayList<IsoCodePrimary>();

		public IsoCodePrimary getIsoCodePrimary() {
			return IsoCodePrimary;
		}

		public void setIsoCodePrimary(IsoCodePrimary isoCodePrimary) {
			IsoCodePrimary = isoCodePrimary;
		}

		public ArrayList<IsoCodePrimary> getIsoCodesOptional() {
			return IsoCodesOptional;
		}

		public void setIsoCodesOptional(ArrayList<IsoCodePrimary> isoCodesOptional) {
			IsoCodesOptional = isoCodesOptional;
		}

		
	}
	
	public class IsoCodePrimary {
		private String Code;
		private String Name;

		public String getCode() {
			return Code;
		}

		public void setCode(String Code) {
			this.Code = Code;
		}

		public String getName() {
			return Name;
		}

		public void setName(String Name) {
			this.Name = Name;
		}

	}

	public class Editions {
		private ArrayList<Edition> editions = new ArrayList<Edition>();

		public ArrayList<Edition> getEditions() {
			return editions;
		}

		public void setEditions(ArrayList<Edition> editions) {
			this.editions = editions;
		}

	}

	public class Edition {
		private Contexts contexts;
		private ArrayList<SettingsHandler> settingsHandlers = new ArrayList<SettingsHandler>();
		private LifecycleManager lifecycleManager;

		public Contexts getContexts() {
			return contexts;
		}

		public void setContexts(Contexts contexts) {
			this.contexts = contexts;
		}

		public ArrayList<SettingsHandler> getSettingsHandlers() {
			return settingsHandlers;
		}

		public void setSettingsHandlers(
				ArrayList<SettingsHandler> settingsHandlers) {
			this.settingsHandlers = settingsHandlers;
		}

		public LifecycleManager getLifecycleManager() {
			return lifecycleManager;
		}

		public void setLifecycleManager(LifecycleManager lifecycleManager) {
			this.lifecycleManager = lifecycleManager;
		}

	}

	public class LifecycleManager {
		private ArrayList<Start> start = new ArrayList<Start>();
		private ArrayList<Stop> stop = new ArrayList<Stop>();

		public ArrayList<Start> getStart() {
			return start;
		}

		public void setStart(ArrayList<Start> start) {
			this.start = start;
		}

		public ArrayList<Stop> getStop() {
			return stop;
		}

		public void setStop(ArrayList<Stop> stop) {
			this.stop = stop;
		}

	}

	public class Start {
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public class Stop {
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public class SettingsHandler {
		private String type;
		private ArrayList<String> capabilities = new ArrayList<String>();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public ArrayList<String> getCapabilities() {
			return capabilities;
		}

		public void setCapabilities(ArrayList<String> capabilities) {
			this.capabilities = capabilities;
		}
	}

	public class Contexts {
		private ArrayList<OS> OS = new ArrayList<OS>();

		public ArrayList<OS> getOS() {
			return OS;
		}

		public void setOS(ArrayList<OS> oS) {
			OS = oS;
		}
	}

	public class OS {
		private String id;
		private String version;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

	}

	public class SourceRecord {
		private String source;
		private String sid;
		private String uid;
		private String name;
		private String description;
		private Manufacturer manufacturer;
		private Ontologies ontologies;
		private String status;
		private String language;
		private SourceData sourceData;
		private String updated;

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Manufacturer getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(Manufacturer manufacturer) {
			this.manufacturer = manufacturer;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public SourceData getSourceData() {
			return sourceData;
		}

		public void setSourceData(SourceData sourceData) {
			this.sourceData = sourceData;
		}

		public String getUpdated() {
			return updated;
		}

		public void setUpdated(String updated) {
			this.updated = updated;
		}

		public Ontologies getOntologies() {
			return ontologies;
		}

		public void setOntologies(Ontologies ontologies) {
			this.ontologies = ontologies;
		}
		

	}

	public class SourceData {
		private String ManufacturerAddress;
		private String ManufacturerPostalCode;
		private String ManufacturerTown;
		private String ManufacturerCountry;
		private String ManufacturerPhone;
		private String ManufacturerEmail;
		private String ManufacturerWebSiteUrl;
		private String ImageUrl;
		private String EnglishDescription;
		private String OriginalUrl;
		private String EnglishUrl;
		private ArrayList<Feature> Features = new ArrayList<Feature>();
		private String Database;
		private String ProductCode;
		private IsoCodePrimary IsoCodePrimary;
		private ArrayList<IsoCodePrimary> IsoCodesOptional = new ArrayList<IsoCodePrimary>();
		private String CommercialName;
		private String ManufacturerOriginalFullName;
		private String InsertDate;
		private String LastUpdateDate;
		private String ThumbnailImageUrl;
		private String SimilarityLevel;

		public String getManufacturerAddress() {
			return ManufacturerAddress;
		}

		public void setManufacturerAddress(String manufacturerAddress) {
			ManufacturerAddress = manufacturerAddress;
		}

		public String getManufacturerPostalCode() {
			return ManufacturerPostalCode;
		}

		public void setManufacturerPostalCode(String manufacturerPostalCode) {
			ManufacturerPostalCode = manufacturerPostalCode;
		}

		public String getManufacturerTown() {
			return ManufacturerTown;
		}

		public void setManufacturerTown(String manufacturerTown) {
			ManufacturerTown = manufacturerTown;
		}

		public String getManufacturerCountry() {
			return ManufacturerCountry;
		}

		public void setManufacturerCountry(String manufacturerCountry) {
			ManufacturerCountry = manufacturerCountry;
		}

		public String getManufacturerPhone() {
			return ManufacturerPhone;
		}

		public void setManufacturerPhone(String manufacturerPhone) {
			ManufacturerPhone = manufacturerPhone;
		}

		public String getManufacturerEmail() {
			return ManufacturerEmail;
		}

		public void setManufacturerEmail(String manufacturerEmail) {
			ManufacturerEmail = manufacturerEmail;
		}

		public String getManufacturerWebSiteUrl() {
			return ManufacturerWebSiteUrl;
		}

		public void setManufacturerWebSiteUrl(String manufacturerWebSiteUrl) {
			ManufacturerWebSiteUrl = manufacturerWebSiteUrl;
		}

		public String getImageUrl() {
			return ImageUrl;
		}

		public void setImageUrl(String imageUrl) {
			ImageUrl = imageUrl;
		}

		public String getEnglishDescription() {
			return EnglishDescription;
		}

		public void setEnglishDescription(String englishDescription) {
			EnglishDescription = englishDescription;
		}

		public String getOriginalUrl() {
			return OriginalUrl;
		}

		public void setOriginalUrl(String originalUrl) {
			OriginalUrl = originalUrl;
		}

		public String getEnglishUrl() {
			return EnglishUrl;
		}

		public void setEnglishUrl(String englishUrl) {
			EnglishUrl = englishUrl;
		}


		public ArrayList<Feature> getFeatures() {
			return Features;
		}

		public void setFeatures(ArrayList<Feature> features) {
			Features = features;
		}

		public String getDatabase() {
			return Database;
		}

		public void setDatabase(String database) {
			Database = database;
		}

		public String getProductCode() {
			return ProductCode;
		}

		public void setProductCode(String productCode) {
			ProductCode = productCode;
		}

		public IsoCodePrimary getIsoCodePrimary() {
			return IsoCodePrimary;
		}

		public void setIsoCodePrimary(IsoCodePrimary isoCodePrimary) {
			IsoCodePrimary = isoCodePrimary;
		}

		public ArrayList<IsoCodePrimary> getIsoCodesOptional() {
			return IsoCodesOptional;
		}

		public void setIsoCodesOptional(
				ArrayList<IsoCodePrimary> isoCodesOptional) {
			IsoCodesOptional = isoCodesOptional;
		}

		public String getCommercialName() {
			return CommercialName;
		}

		public void setCommercialName(String commercialName) {
			CommercialName = commercialName;
		}

		public String getManufacturerOriginalFullName() {
			return ManufacturerOriginalFullName;
		}

		public void setManufacturerOriginalFullName(
				String manufacturerOriginalFullName) {
			ManufacturerOriginalFullName = manufacturerOriginalFullName;
		}

		public String getInsertDate() {
			return InsertDate;
		}

		public void setInsertDate(String insertDate) {
			InsertDate = insertDate;
		}

		public String getLastUpdateDate() {
			return LastUpdateDate;
		}

		public void setLastUpdateDate(String lastUpdateDate) {
			LastUpdateDate = lastUpdateDate;
		}

		public String getThumbnailImageUrl() {
			return ThumbnailImageUrl;
		}

		public void setThumbnailImageUrl(String thumbnailImageUrl) {
			ThumbnailImageUrl = thumbnailImageUrl;
		}

		public String getSimilarityLevel() {
			return SimilarityLevel;
		}

		public void setSimilarityLevel(String similarityLevel) {
			SimilarityLevel = similarityLevel;
		}

	}

	public class Feature {
		private Integer FeatureId;
		private String FeatureName;
		private String FeatureParentName;
		private Integer ValueMin;
		private Integer ValueMax;

		public Integer getFeatureId() {
			return FeatureId;
		}

		public void setFeatureId(Integer featureId) {
			FeatureId = featureId;
		}

		public String getFeatureName() {
			return FeatureName;
		}

		public void setFeatureName(String featureName) {
			FeatureName = featureName;
		}

		public String getFeatureParentName() {
			return FeatureParentName;
		}

		public void setFeatureParentName(String featureParentName) {
			FeatureParentName = featureParentName;
		}

		public Integer getValueMin() {
			return ValueMin;
		}

		public void setValueMin(Integer valueMin) {
			ValueMin = valueMin;
		}

		public Integer getValueMax() {
			return ValueMax;
		}

		public void setValueMax(Integer valueMax) {
			ValueMax = valueMax;
		}

	}

	public class Manufacturer {
		private String name;
		private String address;
		private String postalCode;
		private String cityTown;
		private String provinceRegion;
		private String country;
		private String phone;
		private String email;
		private String url;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getPostalCode() {
			return postalCode;
		}

		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}

		public String getCityTown() {
			return cityTown;
		}

		public void setCityTown(String cityTown) {
			this.cityTown = cityTown;
		}

		public String getProvinceRegion() {
			return provinceRegion;
		}

		public void setProvinceRegion(String provinceRegion) {
			this.provinceRegion = provinceRegion;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Manufacturer [name=" + name + ", address=" + address
					+ ", postalCode=" + postalCode + ", cityTown=" + cityTown
					+ ", provinceRegion=" + provinceRegion + ", country="
					+ country + ", phone=" + phone + ", email=" + email
					+ ", url=" + url + "]";
		}

	}
	
	public class AllRecords {
		private List<ProductForJSONOld> records = new ArrayList<ProductForJSONOld>();

		public List<ProductForJSONOld> getRecords() {
			return records;
		}

		public void setRecords(List<ProductForJSONOld> records) {
			this.records = records;
		}

	}
	
	public Date convertStringToDate(String stringDate){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date convertedDate = null;
		try {
			convertedDate = (Date) formatter.parse(stringDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertedDate;
	}
	
	public static void main(String args[]){
		
		
		String stringdate = "2013-04-22T00:00:00+02:00";
		stringdate = stringdate.substring(0, stringdate.indexOf("T"));
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date convertedDate = null;
		try {
			 convertedDate = (Date) formatter.parse(stringdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
				Locale.ENGLISH);
        System.out.println(sdf2.format(convertedDate));
      //  System.out.println(sdf2.format(date));


	}

}
