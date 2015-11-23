package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cloud4All.Solution;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EULA {

	private String EULA_costCurrency = "";
	private String EULA_costPaymentChargeType = "";
	private Date EULA_EndDate;
	private Date EULA_StartDate;
	private String status = "";
	private int durationInUsages;
	private int durationInUsagesSpent;
	private int userWithTotalTimeOfLoyaltyToVendorInDays;
	private float EULA_cost;
	private float EULA_costAfterDiscount;
	private SolutionLicense solutionLicense = new SolutionLicense();
	private Vendor vendorOfAgreement;
	private List<String> validForCountries = new ArrayList<String>();
	private List<String> accompanyingSolutions = new ArrayList<String>();
	private List<DiscountSchema> discountSchema = new ArrayList<DiscountSchema>();
	private String ontologyURI = "";
	private String user = "";
	private String refersToSolution = "";
	private Solution solution;

	/**
	 * @return the eULA_costCurrency
	 */
	public String getEULA_costCurrency() {
		return EULA_costCurrency;
	}

	/**
	 * @param eULA_costCurrency
	 *            the eULA_costCurrency to set
	 */
	public void setEULA_costCurrency(String eULA_costCurrency) {
		EULA_costCurrency = eULA_costCurrency;
	}

	/**
	 * @return the eULA_costPaymentChargeType
	 */
	public String getEULA_costPaymentChargeType() {
		return EULA_costPaymentChargeType;
	}

	/**
	 * @param eULA_costPaymentChargeType
	 *            the eULA_costPaymentChargeType to set
	 */
	public void setEULA_costPaymentChargeType(String eULA_costPaymentChargeType) {
		EULA_costPaymentChargeType = eULA_costPaymentChargeType;
	}

	/**
	 * @return the eULA_EndDate
	 */
	public Date getEULA_EndDate() {
		return EULA_EndDate;
	}

	/**
	 * @param eULA_EndDate
	 *            the eULA_EndDate to set
	 */
	public void setEULA_EndDate(Date eULA_EndDate) {
		EULA_EndDate = eULA_EndDate;
	}

	/**
	 * @return the eULA_StartDate
	 */
	public Date getEULA_StartDate() {
		return EULA_StartDate;
	}

	/**
	 * @param eULA_StartDate
	 *            the eULA_StartDate to set
	 */
	public void setEULA_StartDate(Date eULA_StartDate) {
		EULA_StartDate = eULA_StartDate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the durationInUsages
	 */
	public int getDurationInUsages() {
		return durationInUsages;
	}

	/**
	 * @param durationInUsages
	 *            the durationInUsages to set
	 */
	public void setDurationInUsages(int durationInUsages) {
		this.durationInUsages = durationInUsages;
	}

	/**
	 * @return the durationInUsagesSpent
	 */
	public int getDurationInUsagesSpent() {
		return durationInUsagesSpent;
	}

	/**
	 * @param durationInUsagesSpent
	 *            the durationInUsagesSpent to set
	 */
	public void setDurationInUsagesSpent(int durationInUsagesSpent) {
		this.durationInUsagesSpent = durationInUsagesSpent;
	}

	/**
	 * @return the userWithTotalTimeOfLoyaltyToVendorInDays
	 */
	public int getUserWithTotalTimeOfLoyaltyToVendorInDays() {
		return userWithTotalTimeOfLoyaltyToVendorInDays;
	}

	/**
	 * @param userWithTotalTimeOfLoyaltyToVendorInDays
	 *            the userWithTotalTimeOfLoyaltyToVendorInDays to set
	 */
	public void setUserWithTotalTimeOfLoyaltyToVendorInDays(
			int userWithTotalTimeOfLoyaltyToVendorInDays) {
		this.userWithTotalTimeOfLoyaltyToVendorInDays = userWithTotalTimeOfLoyaltyToVendorInDays;
	}

	/**
	 * @return the eULA_cost
	 */
	public float getEULA_cost() {
		return EULA_cost;
	}

	/**
	 * @param eULA_cost
	 *            the eULA_cost to set
	 */
	public void setEULA_cost(float eULA_cost) {
		EULA_cost = eULA_cost;
	}

	/**
	 * @return the eULA_costAfterDiscount
	 */
	public float getEULA_costAfterDiscount() {
		return EULA_costAfterDiscount;
	}

	/**
	 * @param eULA_costAfterDiscount
	 *            the eULA_costAfterDiscount to set
	 */
	public void setEULA_costAfterDiscount(float eULA_costAfterDiscount) {
		EULA_costAfterDiscount = eULA_costAfterDiscount;
	}

	/**
	 * @return the solutionLicense
	 */
	public SolutionLicense getSolutionLicense() {
		return solutionLicense;
	}

	/**
	 * @param solutionLicense
	 *            the solutionLicense to set
	 */
	public void setSolutionLicense(SolutionLicense solutionLicense) {
		this.solutionLicense = solutionLicense;
	}

	/**
	 * @return the vendorOfAgreement
	 */
	public Vendor getVendorOfAgreement() {
		return vendorOfAgreement;
	}

	/**
	 * @param vendorOfAgreement
	 *            the vendorOfAgreement to set
	 */
	public void setVendorOfAgreement(Vendor vendorOfAgreement) {
		this.vendorOfAgreement = vendorOfAgreement;
	}

	/**
	 * @return the validForCountries
	 */
	public List<String> getValidForCountries() {
		return validForCountries;
	}

	/**
	 * @param validForCountries
	 *            the validForCountries to set
	 */
	public void setValidForCountries(List<String> validForCountries) {
		this.validForCountries = validForCountries;
	}

	/**
	 * @return the accompanyingSolutions
	 */
	public List<String> getAccompanyingSolutions() {
		return accompanyingSolutions;
	}

	/**
	 * @param accompanyingSolutions
	 *            the accompanyingSolutions to set
	 */
	public void setAccompanyingSolutions(List<String> accompanyingSolutions) {
		this.accompanyingSolutions = accompanyingSolutions;
	}

	/**
	 * @return the discountSchema
	 */
	public List<DiscountSchema> getDiscountSchema() {
		return discountSchema;
	}

	/**
	 * @param discountSchema
	 *            the discountSchema to set
	 */
	public void setDiscountSchema(List<DiscountSchema> discountSchema) {
		this.discountSchema = discountSchema;
	}

	public EULA clone() {
		EULA eula = new EULA();
		eula.setEULA_costCurrency(this.getEULA_costCurrency());
		eula.setEULA_costPaymentChargeType(this.getEULA_costPaymentChargeType());
		eula.setEULA_StartDate(this.getEULA_StartDate());
		eula.setStatus(this.getStatus());
		eula.setDurationInUsages(this.getDurationInUsages());
		eula.setDurationInUsagesSpent(this.getDurationInUsagesSpent());
		eula.setUserWithTotalTimeOfLoyaltyToVendorInDays(this
				.getUserWithTotalTimeOfLoyaltyToVendorInDays());
		eula.setEULA_cost(this.getEULA_cost());
		eula.setEULA_costAfterDiscount(this.getEULA_costAfterDiscount());
		eula.setSolutionLicense(this.getSolutionLicense().clone());
		eula.setOntologyURI(this.getOntologyURI());
		eula.setRefersToSolution(this.getRefersToSolution());
		eula.setUser(this.getUser());
		// TODO clone vendor of agreement
		for (int i = 0; i < this.getValidForCountries().size(); i++) {
			eula.getValidForCountries().add(this.getValidForCountries().get(i));
		}

		for (int i = 0; i < this.getAccompanyingSolutions().size(); i++) {
			eula.getAccompanyingSolutions().add(
					this.getAccompanyingSolutions().get(i));
		}
		for (int i = 0; i < this.getDiscountSchema().size(); i++) {
			eula.getDiscountSchema().add(this.getDiscountSchema().get(i));
		}

		return eula;

	}

	public String getOntologyURI() {
		return ontologyURI;
	}

	public void setOntologyURI(String ontologyURI) {
		this.ontologyURI = ontologyURI;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRefersToSolution() {
		return refersToSolution;
	}

	public void setRefersToSolution(String refersToSolution) {
		this.refersToSolution = refersToSolution;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	
	public JsonObject getJsonSchema(){
		JsonObject eulaobj = new JsonObject();

		String endDate = "";
		if (this.getEULA_EndDate() != null)
			endDate = String.valueOf(this.getEULA_EndDate());

		String startDate = "";
		if (this.getEULA_StartDate() != null)
			startDate = String
					.valueOf(this.getEULA_StartDate());

		eulaobj.addProperty("EULA_Cost_Currency",
				this.getEULA_costCurrency());
		eulaobj.addProperty("EULA_Cost_Payment_Charge_Type",
				this.getEULA_costPaymentChargeType());
		eulaobj.addProperty("EULA_End_Date", endDate);
		eulaobj.addProperty("EULA_Start_Date", startDate);
		eulaobj.addProperty("Status", this.getStatus());
		eulaobj.addProperty("Duration_In_Usages",
				this.getDurationInUsages());
		eulaobj.addProperty("Duration_In_Usages_Spent",
				this.getDurationInUsagesSpent());
		eulaobj.addProperty(
				"User_With_Total_Time_Of_Loyalty_To_Vendor_In_Days",
				this.getUserWithTotalTimeOfLoyaltyToVendorInDays());
		eulaobj.addProperty("EULA_Cost", this.getEULA_cost());
		eulaobj.addProperty("EULA_Cost_After_Discount",
				this.getEULA_costAfterDiscount());
		
		JsonObject solutionLicense = this.getSolutionLicense().getJsonSchema();
		
		JsonObject countries = new JsonObject();
		countries.addProperty("Valid_for_countries", this.getValidForCountries().toString());
		
		// accompanyingSolutions list
		JsonArray accompanyingSolutions = new JsonArray();
		for(String tempSol: this.getAccompanyingSolutions()){
			JsonObject accompanyingSolution = new JsonObject();
			accompanyingSolution.addProperty("Application_Name", tempSol);
			accompanyingSolutions.add(accompanyingSolution);
		}
		
		// discountSchema list
		JsonArray discountSchemas = new JsonArray();
		for (DiscountSchema tempschema : this.getDiscountSchema()) {
			// TODO not finished
			JsonObject schema = tempschema.getJsonSchema();
			discountSchemas.add(schema);

		}
		
		eulaobj.add("Solution_License", solutionLicense);
		eulaobj.add("Countries", countries);
		eulaobj.add("Accompanying_Solutions", accompanyingSolutions);
		eulaobj.add("Discount_Schemas", discountSchemas);
		
		return eulaobj;
	}
	
	

}
