package org.cloud4All.ontology;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;

import org.cloud4All.Solution;
import org.cloud4All.Utilities;

public class ProductForJSON {

	private ArrayList<ProductForJSON> sources = new ArrayList<ProductForJSON>();
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
	private Editions editions;

	public Editions getEditions() {
		return editions;
	}

	public void setEditions(Editions editions) {
		this.editions = editions;
	}

	public ArrayList<ProductForJSON> getSources() {
		return sources;
	}

	public void setSources(ArrayList<ProductForJSON> sources) {
		this.sources = sources;
	}

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

	public class Ontologies {
		private iso9999 iso9999;

		public iso9999 getIso9999() {
			return iso9999;
		}

		public void setIso9999(iso9999 iso9999) {
			this.iso9999 = iso9999;
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

		public void setIsoCodesOptional(
				ArrayList<IsoCodePrimary> isoCodesOptional) {
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
		private double FeatureId;
		private String FeatureName;
		private String FeatureParentName;
		private double ValueMin;
		private double ValueMax;

		public double getFeatureId() {
			return FeatureId;
		}

		public void setFeatureId(double featureId) {
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

		public double getValueMin() {
			return ValueMin;
		}

		public void setValueMin(double valueMin) {
			ValueMin = valueMin;
		}

		public double getValueMax() {
			return ValueMax;
		}

		public void setValueMax(double valueMax) {
			ValueMax = valueMax;
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

	public class AllRecords {
		private List<ProductForJSON> records = new ArrayList<ProductForJSON>();

		public List<ProductForJSON> getRecords() {
			return records;
		}

		public void setRecords(List<ProductForJSON> records) {
			this.records = records;
		}

	}

	public ArrayList<Solution> convertProductForJSON() {
		Solution sol = null;
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		ArrayList<Solution> solutions = new ArrayList<Solution>();

		if (this.getSources() != null && this.getSources().size() > 0) {

			// TODO add the part of editions
			// settings handlers, OS etc
			Edition edition = null;
			String operatingSystem = "";
			String settingsHandler = "";
			String capabilities = "";
			String startCommand = "";
			String stopCommand = "";
			if (this.getEditions() != null
					&& this.getEditions().getEditions().size() > 0) {

				edition = this.getEditions().getEditions().get(0);

				// get operating system
				if (edition.getContexts().getOS() != null
						&& edition.getContexts().getOS().size() > 0)
					operatingSystem = edition.getContexts().getOS().get(0)
							.getId();

				// get settingsHandler, capabilities
				if (edition.getSettingsHandlers() != null
						&& edition.getSettingsHandlers().size() > 0) {
					settingsHandler = edition.getSettingsHandlers().get(0)
							.getType();

					if (!settingsHandler.isEmpty())
						settingsHandler = Utilities
								.convertUnifiedListingHandlerToOntologyHandler(settingsHandler);

					ArrayList<String> capblities = edition
							.getSettingsHandlers().get(0).getCapabilities();
					for (String s : capblities) {
						capabilities = capabilities.concat(s + ",");
					}

				}

				// get lifecycle manager (start and stop command)
				if (edition.getLifecycleManager() != null) {
					if (edition.getLifecycleManager().getStart() != null
							&& edition.getLifecycleManager().getStart().size() > 0)
						startCommand = edition.getLifecycleManager().getStart()
								.get(0).getType();

					if (edition.getLifecycleManager().getStop() != null
							&& edition.getLifecycleManager().getStop().size() > 0)
						stopCommand = edition.getLifecycleManager().getStop()
								.get(0).getType();

				}

			}

			for (ProductForJSON temp : this.getSources()) {

				sol = convertProductToSolution(temp, ontologyInstance);

				sol.setCapabilities(capabilities);
				sol.setStartCommand(startCommand);
				sol.setStopCommand(stopCommand);
				sol.setHandlerType(settingsHandler);

				// add operating system to EASTIN properties
				if (!operatingSystem.isEmpty()) {
					boolean flag = false;
					for (ETNACluster cl : sol.getClustersToShowInView()) {
						if (cl.getName().equals("Operating Systems")) {

							sol.getClustersToShow().remove(cl);
							if (!cl.getSelectedProperties().contains(
									operatingSystem))
								cl.getSelectedProperties().add(operatingSystem);

							sol.getClustersToShow().add(cl.clone());
							flag = true;
							break;
						}
					}

					if (!flag) {
						int index = 0;
						for (ETNACluster cl : sol.getClustersToHide()) {
							if (cl.getName().equals("Operating Systems")) {

								index = sol.getClustersToHide().indexOf(cl);
								break;
							}
						}

						ETNACluster cluster = sol.getClustersToHide()
								.get(index);
						cluster.getSelectedProperties().add(operatingSystem);
						sol.getClustersToShow().add(cluster.clone());
						sol.getClustersToShowInView().add(cluster.clone());
						sol.getClustersToHide().remove(index);
					}
				}

				if (sol.getHandlerType().isEmpty())
					sol.setHandlerType("No");

				solutions.add(sol);
			}
		} else {

			sol = convertProductToSolution(this, ontologyInstance);

			if (sol.getHandlerType().isEmpty())
				sol.setHandlerType("No");
			

			if (!sol.getStatus().equalsIgnoreCase("deleted"))
				solutions.add(sol);
		}
		return solutions;

	}

	public Solution convertProductToSolution(ProductForJSON temp,
			OntologyInstance ontologyInstance) {
		Solution sol = new Solution();
		try {
			if (temp.getName() != null)
				sol.setName(temp.getName());

			if (temp.getUid() != null)
				sol.setId(temp.getUid());

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
			
			
			if (temp.getStatus() != null)
				sol.setStatus(temp.getStatus());
				

			// get eastin properties
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
					ArrayList<String> alreadyFoundClusters = new ArrayList<String>();
					boolean flag = false;
					for (Feature ft : properties) {
						String clusterName = ft.getFeatureParentName();

						// add the cluster if its new
						if (!alreadyFoundClusters.contains(clusterName)) {
							alreadyFoundClusters.add(clusterName);
							flag = true;
						}
						String selectedPropertyName = ft.getFeatureName();

						// find the cluster in clustersOriginal and add the
						// property
						// because its the first time is being used
						if (flag)
							for (ETNACluster cl : ontologyInstance
									.getEtnaClusterOriginal()) {
								if (cl.getName().equalsIgnoreCase(clusterName)) {

									clustersToShow.add(cl);
									ETNACluster clusterClone = cl.clone();

									if (clusterClone.isSingleSelection())
										clusterClone
												.setSelectedProperty(selectedPropertyName);
									else
										clusterClone.getSelectedProperties()
												.add(selectedPropertyName);

									sol.getClustersToShowInView().add(
											clusterClone);
									sol.getClustersToShow().add(clusterClone);
									break;

								}
							}

						// the cluster has already been used
						// look for it in the clustersToShowLists and add the
						// property
						if (!flag) {
							for (ETNACluster cl : sol.getClustersToShowInView()) {
								if (cl.getName().equals(clusterName)) {

									if (!cl.isSingleSelection()) {
										if (!cl.getSelectedProperties()
												.contains(selectedPropertyName))
											cl.getSelectedProperties().add(
													selectedPropertyName);
									} else
										cl.setSelectedProperty(selectedPropertyName);

									break;
								}
							}

							for (ETNACluster cl : sol.getClustersToShow()) {
								if (cl.getName().equals(clusterName)) {

									if (!cl.isSingleSelection()) {
										if (!cl.getSelectedProperties()
												.contains(selectedPropertyName))
											cl.getSelectedProperties().add(
													selectedPropertyName);
									} else
										cl.setSelectedProperty(selectedPropertyName);

									break;
								}
							}
						}

						flag = false;
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

			for (String s : ontologyInstance.getCategoriesNames()) {
				if (s.equalsIgnoreCase(categoryName)) {
					sol.setOntologyCategory(s);
					break;
				}
			}

			// get secondary categories

			ArrayList<String> secondaryCategories = new ArrayList<String>();

			if (temp.getSourceData() != null
					&& temp.getSourceData().getIsoCodesOptional() != null) {
				for (IsoCodePrimary code : temp.getSourceData()
						.getIsoCodesOptional()) {
					secondaryCategories.add(code.getName().replace(" ", ""));
				}
			}

			ArrayList<String> ontologySecondaryCategories = new ArrayList<String>();
			for (String s1 : secondaryCategories) {
				for (String s2 : secondaryCategories) {
					if (s1.equalsIgnoreCase(s2)) {
						ontologySecondaryCategories.add(s2);
						break;
					}
				}
			}

			sol.setCategories(ontologySecondaryCategories);

			if (temp.getSourceData() != null
					&& temp.getSourceData().getThumbnailImageUrl() != null)
				sol.setImageUrl(temp.getSourceData().getThumbnailImageUrl());

			// get dates
			String date = "";
			String updateDate = "";

			if (temp.getSourceData() != null
					&& temp.getSourceData().getInsertDate() != null) {
				date = temp.getSourceData().getInsertDate();

				if (date.length() > 0)
					date = date.substring(0, date.indexOf("T"));
			}

			if (temp.getSourceData() != null
					&& temp.getSourceData().getLastUpdateDate() != null) {
				updateDate = temp.getSourceData().getLastUpdateDate();

				if (date.length() > 0)
					updateDate = updateDate.substring(0,
							updateDate.indexOf("T"));
			}

			if (!date.isEmpty()) {
				Date insertDate = convertStringToDate(date);
				sol.setDate(insertDate);
			}

			if (!updateDate.isEmpty()) {
				Date lastUpdateDate = convertStringToDate(updateDate);
				sol.setUpdateDate(lastUpdateDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sol;
	}

	public Date convertStringToDate(String stringDate) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date convertedDate = null;
		try {
			if (!stringDate.contains("Jan"))
				convertedDate = (Date) formatter.parse(stringDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertedDate;
	}

}
