package org.cloud4All;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.cloud4All.IPR.EULA;
import org.cloud4All.IPR.ReputationSchema;
import org.cloud4All.IPR.SLA;
import org.cloud4All.IPR.SolutionAccessInfoForUsers;
import org.cloud4All.IPR.SolutionAccessInfoForVendors;
import org.cloud4All.IPR.SolutionUsageStatisticsSchema;
import org.cloud4All.IPR.SolutionUserSchema;
import org.cloud4All.IPR.Vendor;
import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Solution {

	private String ontologyCategory;
	private String name;
	private String description;
	private String id;
	private List<Setting> settings;
	private String constraints;
	private List<ETNACluster> etnaProperties;
	private List<ETNACluster> clustersToShow = new ArrayList<ETNACluster>();
	private List<ETNACluster> clustersToHide = new ArrayList<ETNACluster>();
	private List<ETNACluster> clustersToShowInView = new ArrayList<ETNACluster>();
	private String manufacturerName;
	private Date date;
	private Date updateDate;
	private String manufacturerCountry;
	private String manufacturerWebsite;
	private String imageUrl;
	private String downloadPage;
	private String ontologyURI;
	private String vendorName = "";
	private SolutionAccessInfoForVendors accessInfoForVendors = new SolutionAccessInfoForVendors();
	private SolutionAccessInfoForUsers accessInfoForUsers = new SolutionAccessInfoForUsers();
	private List<SLA> SLAs = new ArrayList<SLA>();
	private List<EULA> EULAs = new ArrayList<EULA>();
	private String vendor = "";
	private SolutionUsageStatisticsSchema usageStatistics = new SolutionUsageStatisticsSchema();
	private ReputationSchema reputation = new ReputationSchema();
	private List<SolutionUserSchema> users = new ArrayList<SolutionUserSchema>();
	private int numberOfUsers = 0;
	private float vendorReputationScore;
	private List<ETNACluster> proposedClusters = new ArrayList<ETNACluster>();
	private List<EASTINProperty> proposedAttributeClusterItems = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> proposedMeasureClusterItems = new ArrayList<EASTINProperty>();
	private String startCommand = "";
	private String stopCommand = "";
	private String handlerType = "";
	private List<Element> options = new ArrayList<Element>();
	private String capabilities ="";
	private String capabilitiesTransformations = "";
	private List<String> categories = new ArrayList<String>();
	private String status = "";
	private String check = "";
	
	public Solution() {

		id = "";
		description = "";
		manufacturerName = "";
		manufacturerCountry = "";
		manufacturerWebsite = "";
		imageUrl = "";
		downloadPage = "";
		constraints = "";
		settings = new ArrayList<Setting>();
		etnaProperties = new ArrayList<ETNACluster>();
		ontologyURI = "";
		vendorName = "";
		accessInfoForVendors = new SolutionAccessInfoForVendors();
		accessInfoForUsers = new SolutionAccessInfoForUsers();
		SLAs = new ArrayList<SLA>();
		vendor = "";
		usageStatistics = new SolutionUsageStatisticsSchema();
		reputation = new ReputationSchema();
		startCommand = "";
		stopCommand = "";
		handlerType = "";
		options = new ArrayList<Element>();
		capabilities = "";
		capabilitiesTransformations = "";
		check = "";
		
	}
	
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOntologyCategory() {
		return ontologyCategory;
	}

	public void setOntologyCategory(String ontologyCategory) {
		this.ontologyCategory = ontologyCategory;
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public List<ETNACluster> getEtnaProperties() {
		return etnaProperties;
	}

	public void setEtnaProperties(List<ETNACluster> etnaProperties) {
		this.etnaProperties = etnaProperties;
	}

	public Solution cloneSolution() {
		Solution sol = new Solution();
		try {
			sol.setName(this.getName());
			sol.setDescription(this.getDescription());
			sol.setStartCommand(this.getStartCommand());
			sol.setStopCommand(this.getStopCommand());
			sol.setHandlerType(this.getHandlerType());
			sol.setOntologyCategory(this.getOntologyCategory());
			sol.setId(this.getId());
			sol.setManufacturerCountry(this.getManufacturerCountry());
			sol.setManufacturerName(this.getManufacturerName());
			sol.setManufacturerWebsite(this.getManufacturerWebsite());
			sol.setImageUrl(this.getImageUrl());
			sol.setDownloadPage(this.getDownloadPage());
			sol.setVendorName(this.getVendorName());
			if (this.getDate() != null)
				sol.setDate((Date) this.getDate().clone());
			if (this.getUpdateDate() != null)
				sol.setUpdateDate((Date) this.getUpdateDate().clone());

			sol.setConstraints(this.constraints);
			for (int i = 0; i < this.getSettings().size(); i++) {
				sol.getSettings().add(this.getSettings().get(i).cloneSetting());
			}
			for (int i = 0; i < etnaProperties.size(); i++) {
				ETNACluster cl = etnaProperties.get(i);
				sol.getEtnaProperties().add(cl.clone());
			}
			sol.setOntologyURI(this.getOntologyURI());
			sol.setAccessInfoForVendors(this.getAccessInfoForVendors().clone());
			sol.setAccessInfoForUsers(this.getAccessInfoForUsers().clone());
			for (int i = 0; i < this.getSLAs().size(); i++) {
				sol.getSLAs().add(this.getSLAs().get(i).clone());
			}
			for (int i = 0; i < this.getEULAs().size(); i++) {
				sol.getEULAs().add(this.getEULAs().get(i).clone());
			}
			sol.setVendor(this.getVendor());

			sol.setUsageStatistics(this.getUsageStatistics().clone());

			sol.setReputation(this.getReputation().clone());
			for (int i = 0; i < this.getUsers().size(); i++) {
				sol.getUsers().add(this.getUsers().get(i).clone());
			}
			sol.setNumberOfUsers(this.getNumberOfUsers());
			
			for(int i = 0; i < this.getOptions().size(); i++){
				Element element = this.getOptions().get(i);
				sol.getOptions().add(element.clone());
			}
			
			sol.setCapabilities(this.getCapabilities());
			sol.setCapabilitiesTransformations(this.getCapabilitiesTransformations());
			sol.setCheck(this.getCheck());
			sol.setStatus(this.getStatus());
//			for(int i = 0; i< this.getProposedAttributeClusterItems().size(); i++){
//				EASTINProperty p = this.getProposedAttributeClusterItems().get(i);
//				sol.getProposedAttributeClusterItems().add(p);
//			}
//			
//			
//			for(int i = 0; i< this.getProposedMeasureClusterItems().size(); i++){
//				EASTINProperty p = this.getProposedMeasureClusterItems().get(i);
//				sol.getProposedMeasureClusterItems().add(p);
//			}
//			
//			for(int i = 0; i< this.getProposedClusters().size(); i++){
//				ETNACluster c = this.getProposedClusters().get(i);
//				sol.getProposedClusters().add(c);
//			}
			
			for (int i = 0; i < this.getClustersToShow().size(); i++) {
				sol.getClustersToShow().add(this.getClustersToShow().get(i).clone());
			}
			
			for (int i = 0; i < this.getClustersToHide().size(); i++) {
				sol.getClustersToHide().add(this.getClustersToHide().get(i).clone());
			}
			
			for (int i = 0; i < this.getClustersToShowInView().size(); i++) {
				sol.getClustersToShowInView().add(this.getClustersToShowInView().get(i).clone());
			}
			
			sol.setCategories(new ArrayList<String>());
			for(String s : this.getCategories()){
				sol.getCategories().add(s);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sol;
	}
	
	
	public Solution clone() {
		Solution sol = new Solution();
		try {
			sol.setName(this.getName());
			sol.setDescription(this.getDescription());
			sol.setStartCommand(this.getStartCommand());
			sol.setStopCommand(this.getStopCommand());
			sol.setHandlerType(this.getHandlerType());
			sol.setOntologyCategory(this.getOntologyCategory());

			sol.setId(this.getId());
			sol.setManufacturerCountry(this.getManufacturerCountry());
			sol.setManufacturerName(this.getManufacturerName());
			sol.setManufacturerWebsite(this.getManufacturerWebsite());
			sol.setImageUrl(this.getImageUrl());
			sol.setDownloadPage(this.getDownloadPage());
			sol.setVendorName(this.getVendorName());
			sol.setCheck(this.getCheck());
			sol.setStatus(this.getStatus());
			if (this.getDate() != null)
				sol.setDate((Date) this.getDate().clone());
			if (this.getUpdateDate() != null)
				sol.setUpdateDate((Date) this.getUpdateDate().clone());

			sol.setConstraints(this.constraints);
			for (int i = 0; i < this.getSettings().size(); i++) {
				sol.getSettings().add(this.getSettings().get(i).cloneSetting());
			}
			for (int i = 0; i < etnaProperties.size(); i++) {
				ETNACluster cl = etnaProperties.get(i);
				sol.getEtnaProperties().add(cl.clone());
			}
			sol.setOntologyURI(this.getOntologyURI());
			sol.setAccessInfoForVendors(this.getAccessInfoForVendors().clone());
			sol.setAccessInfoForUsers(this.getAccessInfoForUsers().clone());
			for (int i = 0; i < this.getSLAs().size(); i++) {
				sol.getSLAs().add(this.getSLAs().get(i).clone());
			}
			for (int i = 0; i < this.getEULAs().size(); i++) {
				sol.getEULAs().add(this.getEULAs().get(i).clone());
			}
			sol.setVendor(this.getVendor());

			sol.setUsageStatistics(this.getUsageStatistics().clone());

			sol.setReputation(this.getReputation().clone());
			for (int i = 0; i < this.getUsers().size(); i++) {
				sol.getUsers().add(this.getUsers().get(i).clone());
			}
			sol.setNumberOfUsers(this.getNumberOfUsers());
			
			for(int i = 0; i< this.getProposedAttributeClusterItems().size(); i++){
				EASTINProperty p = this.getProposedAttributeClusterItems().get(i);
				sol.getProposedAttributeClusterItems().add(p);
			}
			
			
			for(int i = 0; i< this.getProposedMeasureClusterItems().size(); i++){
				EASTINProperty p = this.getProposedMeasureClusterItems().get(i);
				sol.getProposedMeasureClusterItems().add(p);
			}
			
			for(int i = 0; i< this.getProposedClusters().size(); i++){
				ETNACluster c = this.getProposedClusters().get(i);
				sol.getProposedClusters().add(c);
			}
			
			for(int i = 0; i < this.getOptions().size(); i++){
				Element element = this.getOptions().get(i);
				sol.getOptions().add(element.clone());
			}
			
			sol.setCapabilities(this.capabilities);
			sol.setCapabilitiesTransformations(this.capabilitiesTransformations);
			
			sol.setClustersToShow(new ArrayList<ETNACluster>());
			for(int i=0 ; i< this.getClustersToShow().size();i++){
				ETNACluster c = this.getClustersToShow().get(i);
				sol.getClustersToShow().add(c);
			}
			
			sol.setClustersToHide(new ArrayList<ETNACluster>());
			for(int i=0; i< this.getClustersToHide().size(); i++){
				ETNACluster c = this.getClustersToHide().get(i);
				sol.getClustersToHide().add(c);
			}
			
			sol.setClustersToShowInView(new ArrayList<ETNACluster>());
			for(int i=0; i< this.getClustersToShowInView().size();i++){
				ETNACluster c = this.getClustersToShowInView().get(i);
				sol.getClustersToShowInView().add(c);
			}
			
			sol.setCategories(new ArrayList<String>());
			for(String s : this.getCategories()){
				sol.getCategories().add(s);
			}
			
//			for(int i = 0; i < this.getSettings().size(); i++){
//				Setting set = this.getSettings().get(i).cloneSetting();
//				sol.getSettings().add(set);
//			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sol;
	}

	public int getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getOntologyURI() {
		return ontologyURI;
	}

	public void setOntologyURI(String ontologyURI) {
		this.ontologyURI = ontologyURI;
	}

	public String getManufacturerName() {

		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {

		this.manufacturerName = manufacturerName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getManufacturerCountry() {
		return manufacturerCountry;
	}

	public void setManufacturerCountry(String manufacturerCountry) {
		this.manufacturerCountry = manufacturerCountry;
	}

	public String getManufacturerWebsite() {
		return manufacturerWebsite;
	}

	public void setManufacturerWebsite(String manufacturerWebsite) {
		this.manufacturerWebsite = manufacturerWebsite;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDownloadPage() {
		return downloadPage;
	}

	public void setDownloadPage(String downloadPage) {
		this.downloadPage = downloadPage;
	}

	/**
	 * @return the accessInfoForVendors
	 */
	public SolutionAccessInfoForVendors getAccessInfoForVendors() {
		return accessInfoForVendors;
	}

	/**
	 * @param accessInfoForVendors
	 *            the accessInfoForVendors to set
	 */
	public void setAccessInfoForVendors(
			SolutionAccessInfoForVendors accessInfoForVendors) {
		this.accessInfoForVendors = accessInfoForVendors;
	}

	/**
	 * @return the accessInfoForUsers
	 */
	public SolutionAccessInfoForUsers getAccessInfoForUsers() {
		return accessInfoForUsers;
	}

	/**
	 * @param accessInfoForUsers
	 *            the accessInfoForUsers to set
	 */
	public void setAccessInfoForUsers(
			SolutionAccessInfoForUsers accessInfoForUsers) {
		this.accessInfoForUsers = accessInfoForUsers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.getName().equals("")) {
			return "None";
		} else
			return this.getName();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Solution) {
			Solution sol = (Solution) arg0;
			if (this.getOntologyURI().equals(sol.getOntologyURI())) {
				return true;
			} else
				return false;
		} else
			return false;
	}
	
	public boolean testEquals(Object obj) {
		
		boolean flag = true;
		
		if(this.getDate()!=null && ((Solution) obj).getDate()!=null)
			if(!this.getDate().equals(((Solution) obj).getDate()))
				flag = false;
		
		if(this.getUpdateDate()!=null && ((Solution) obj).getUpdateDate()!=null)
				 if(!this.getUpdateDate().equals(((Solution) obj).getUpdateDate()))
					 flag =false;
		
		//check strings
		if (!this.getName().equals(((Solution) obj).getName())
				|| !this.getDescription().equals(
						((Solution) obj).getDescription())
				|| !this.getManufacturerName().equals(
						((Solution) obj).getManufacturerName())
				|| !this.getManufacturerCountry().equals(
						((Solution) obj).getManufacturerCountry())
				|| !this.getManufacturerWebsite().equals(
						((Solution) obj).getManufacturerWebsite())
				|| !this.getOntologyCategory().equals(
						((Solution) obj).getOntologyCategory())
				|| !this.getImageUrl().equals(((Solution) obj).getImageUrl())
				|| !this.getCapabilities().equals(
						((Solution) obj).getCapabilities())
				|| !this.getCapabilitiesTransformations().equals(
						((Solution) obj).getCapabilitiesTransformations())
				|| !this.getStartCommand().equals(
						((Solution) obj).getStartCommand())
				|| !this.getStopCommand().equals(
						((Solution) obj).getStopCommand())
				|| !this.getConstraints().equals(
						((Solution) obj).getConstraints())
				|| !this.getHandlerType().equals(
						((Solution) obj).getHandlerType()) || !flag) {
			flag = false;
		}
		
		//check lists with objects
		//options
		
		for (Element el : ((Solution) obj).getOptions()) {
			if(!this.getOptions().contains(el))
				return false;
			
		}
		// check clusters
		
		for(ETNACluster cl: ((Solution) obj).getClustersToShowInView()){
			if(!this.clustersToShowInView.contains(cl))
				return false;
		}
		
		//check other categories
		for(String s : ((Solution) obj).getCategories()){
			if(!this.getCategories().contains(s))
				return false;
		}
		
		return true;
		
	}

	// This must return the same hashcode for every Foo object with the same
	// key.
	public int hashCode() {
		return ontologyURI != null ? this.getClass().hashCode()
				+ ontologyURI.hashCode() : super.hashCode();
	}

	/**
	 * @return the sLAs
	 */
	public List<SLA> getSLAs() {
		return SLAs;
	}

	/**
	 * @param sLAs
	 *            the sLAs to set
	 */
	public void setSLAs(List<SLA> sLAs) {
		SLAs = sLAs;
	}

	public SolutionUsageStatisticsSchema getUsageStatistics() {
		return usageStatistics;
	}

	public void setUsageStatistics(SolutionUsageStatisticsSchema usageStatistics) {
		this.usageStatistics = usageStatistics;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public ReputationSchema getReputation() {
		return reputation;
	}

	public void setReputation(ReputationSchema reputation) {
		this.reputation = reputation;
	}

	public List<EULA> getEULAs() {
		return EULAs;
	}

	public void setEULAs(List<EULA> eULAs) {
		EULAs = eULAs;
	}

	public List<SolutionUserSchema> getUsers() {
		return users;
	}

	public void setUsers(List<SolutionUserSchema> users) {
		this.users = users;
	}

	public float getVendorReputationScore() {
		return vendorReputationScore;
	}

	public void setVendorReputationScore(float vendorReputationScore) {
		this.vendorReputationScore = vendorReputationScore;
	}

	public List<ETNACluster> getProposedClusters() {
		return proposedClusters;
	}

	public void setProposedClusters(List<ETNACluster> proposedClusters) {
		this.proposedClusters = proposedClusters;
	}

	public List<EASTINProperty> getProposedAttributeClusterItems() {
		return proposedAttributeClusterItems;
	}

	public void setProposedAttributeClusterItems(
			List<EASTINProperty> proposedAttributeClusterItems) {
		this.proposedAttributeClusterItems = proposedAttributeClusterItems;
	}

	public List<EASTINProperty> getProposedMeasureClusterItems() {
		return proposedMeasureClusterItems;
	}

	public void setProposedMeasureClusterItems(
			List<EASTINProperty> proposedMeasureClusterItems) {
		this.proposedMeasureClusterItems = proposedMeasureClusterItems;
	}
	
	public JsonObject getSolutionJsonObject(){
		if (this.getDescription() == null)
			this.setDescription("");

		String date = "";
		String updateDate = "";
		if (this.getDate() != null)
			date = String.valueOf(this.getDate());

		if (this.getUpdateDate() != null)
			updateDate = String.valueOf(this.getUpdateDate());

		// create json object for solution
		JsonObject solution = new JsonObject();

		solution.addProperty("Application_Name", this.getName());
		solution.addProperty("Unified_Listing_ID", this.getId());
		solution.addProperty("Category", this.getOntologyCategory());
		solution.addProperty("Application_description", this.getDescription());
		solution.addProperty("Constraints/limitations", this.getConstraints());
		solution.addProperty("Insert_date", date);
		solution.addProperty("Latest_update_date", updateDate);
		solution.addProperty("Manufacturer_Name", this.getManufacturerName());
		solution.addProperty("Manufacturer_Country",
				this.getManufacturerCountry());
		solution.addProperty("Manufacturer_Website",
				this.getManufacturerWebsite());
		solution.addProperty("Thumbnail", this.getImageUrl());
		solution.addProperty("Original_source_of_information",
				this.getDownloadPage());
		solution.addProperty("Vendor_name", this.getVendorName());

		// SETTINGS
		JsonArray settings = new JsonArray();
		
		for (Setting set : this.getSettings()) {

			JsonObject setting = set.getJsonSchema();
			settings.add(setting);
		}

		// SLAS
		JsonArray slas = new JsonArray();
		
		for (SLA temp : this.getSLAs()) {

			JsonArray slaArray = temp.getJsonSchema();
			slas.add(slaArray);

		}

		// REPUTATION
		JsonObject reputationObj = this.getReputation()
				.getJsonSchema();

		// USAGESTATISTICS, not finished
		JsonObject solUsageSchema = this.getUsageStatistics()
				.getJsonSchema();

		// EULAS
		JsonArray eulas = new JsonArray();
		
		for (EULA temp : this.getEULAs()) {

			JsonObject eulaobj = temp.getJsonSchema();
			eulas.add(eulaobj);

		}
		
		// Solution_Access_Info_For_Vendors
		JsonObject accessInfoForVendors = this.getAccessInfoForVendors()
				.getJsonSchema();

		// Solution_Access_Info_For_Users
		JsonObject accessInfoForUsers = this.getAccessInfoForUsers()
				.getJsonSchema();

		if (this.getSettings().size()!=0)
			solution.add("Settings", settings);
		
		if (this.getSLAs().size()!=0)
			solution.add("SLAs", slas);
		
		if (reputationObj != null)
			solution.add("Reputation", reputationObj);
		
		if(solUsageSchema!=null)
		solution.add("Solution_Usage_Statistics_Schema", solUsageSchema);
		
		if (this.getEULAs().size()!=0)
			solution.add("EULAs", eulas);
		
		
		if (accessInfoForVendors != null)
			solution.add("Solution_Access_Info_For_Vendors",
					accessInfoForVendors);

		if (accessInfoForUsers != null)
			solution.add("Solution_Access_Info_For_Users", accessInfoForUsers);

		return solution;

	}
	
	public JsonArray getSolutionSelectedPropertiesJsonSchema(){
		JsonArray clusters = new JsonArray();
		boolean flag = false;
		boolean secondFlag = false;
		for (ETNACluster tempCl : this.getEtnaProperties()) {

			JsonObject clusterObj = new JsonObject();


			if (tempCl.getSelectedProperty() != null) {

				if (!tempCl.getSelectedProperty().isEmpty()) {
					clusterObj.addProperty("Attributes", tempCl.getSelectedProperty());
					
					flag = true;

				}
			}

			boolean measuresFlag = false;
			JsonObject measures = new JsonObject();
			for (EASTINProperty mprop : tempCl.getMeasureProperties()) {
				if (!mprop.getValue().isEmpty()) {
					measures.addProperty(mprop.getName(),
							mprop.getValue());
					flag = true;
					measuresFlag = true;

				}

			}

			JsonObject cluster = new JsonObject();
			
			if (tempCl.getSelectedProperties().size() != 0) {
				clusterObj.addProperty("Attributes", tempCl
						.getSelectedProperties().toString());
				flag = true;
			}

			if (tempCl.getMeasureProperties().size()!=0 && measuresFlag)
				clusterObj.add("Measures", measures);

			

			if (flag) {
				cluster.add(tempCl.getName(), clusterObj);
				clusters.add(cluster);
				secondFlag = true;
			}

			flag = false;

		}
		
		if (secondFlag)
			return clusters;
		else
			return null;
	}

	public String getStartCommand() {
		return startCommand;
	}

	public void setStartCommand(String startCommand) {
		this.startCommand = startCommand;
	}

	public String getStopCommand() {
		return stopCommand;
	}

	public void setStopCommand(String stopCommand) {
		this.stopCommand = stopCommand;
	}

	public String getHandlerType() {
		return handlerType;
	}

	public void setHandlerType(String handlerType) {
		this.handlerType = handlerType;
	}

	public List<Element> getOptions() {
		return options;
	}

	public void setOptions(List<Element> options) {
		this.options = options;
	}

	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	public String getCapabilitiesTransformations() {
		return capabilitiesTransformations;
	}

	public void setCapabilitiesTransformations(String capabilitiesTransformations) {
		this.capabilitiesTransformations = capabilitiesTransformations;
	}

	public List<ETNACluster> getClustersToShow() {
		return clustersToShow;
	}

	public void setClustersToShow(List<ETNACluster> clustersToShow) {
		this.clustersToShow = clustersToShow;
	}

	public List<ETNACluster> getClustersToHide() {
		return clustersToHide;
	}

	public void setClustersToHide(List<ETNACluster> clustersToHide) {
		this.clustersToHide = clustersToHide;
	}

	public List<ETNACluster> getClustersToShowInView() {
		return clustersToShowInView;
	}

	public void setClustersToShowInView(List<ETNACluster> clustersToShowInView) {
		this.clustersToShowInView = clustersToShowInView;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

}
