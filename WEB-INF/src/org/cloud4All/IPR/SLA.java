package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cloud4All.Solution;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SLA {

	private String SLA_CostCurrency = "";
	private String SLA_CostPaymentChargeType = "";
	private Date SLA_EndDate;
	private Date SLA_StartDate;
	private String hasStatus = "";
	private int durationInUsages;
	private int durationInUsagesSpent;
	private int durationInUsers;
	private int durationInUsersSpent;
	private int vendorWithTotalTimeOfLoyaltyToOwn;
	private float SLA_Cost;
	private float SLA_CostAfterDiscount;
	private String ownerOfAgreement = "";
	private SolutionLicense solutionLicense = new SolutionLicense();
	private String vendorOfAgreement = "";
	private List<String> validForCountries = new ArrayList<String>();
	private List<Solution> accompanyingSolutions = new ArrayList<Solution>();
	private List<DiscountSchema> discountSchema = new ArrayList<DiscountSchema>();
	private String ontologyURI = "";
	private Solution solution;

	public String getOntologyURI() {
		return ontologyURI;
	}

	public void setOntologyURI(String ontologyURI) {
		this.ontologyURI = ontologyURI;
	}

	/**
	 * @return the sLA_CostCurrency
	 */
	public String getSLA_CostCurrency() {
		return SLA_CostCurrency;
	}

	/**
	 * @param sLA_CostCurrency
	 *            the sLA_CostCurrency to set
	 */
	public void setSLA_CostCurrency(String sLA_CostCurrency) {
		SLA_CostCurrency = sLA_CostCurrency;
	}

	/**
	 * @return the sLA_CostPaymentChargeType
	 */
	public String getSLA_CostPaymentChargeType() {
		return SLA_CostPaymentChargeType;
	}

	/**
	 * @param sLA_CostPaymentChargeType
	 *            the sLA_CostPaymentChargeType to set
	 */
	public void setSLA_CostPaymentChargeType(String sLA_CostPaymentChargeType) {
		SLA_CostPaymentChargeType = sLA_CostPaymentChargeType;
	}

	/**
	 * @return the sLA_EndDate
	 */
	public Date getSLA_EndDate() {
		return SLA_EndDate;
	}

	/**
	 * @param sLA_EndDate
	 *            the sLA_EndDate to set
	 */
	public void setSLA_EndDate(Date sLA_EndDate) {
		SLA_EndDate = sLA_EndDate;
	}

	/**
	 * @return the sLA_StartDate
	 */
	public Date getSLA_StartDate() {
		return SLA_StartDate;
	}

	/**
	 * @param sLA_StartDate
	 *            the sLA_StartDate to set
	 */
	public void setSLA_StartDate(Date sLA_StartDate) {
		SLA_StartDate = sLA_StartDate;
	}

	/**
	 * @return the hasStatus
	 */
	public String getHasStatus() {
		return hasStatus;
	}

	/**
	 * @param hasStatus
	 *            the hasStatus to set
	 */
	public void setHasStatus(String hasStatus) {
		this.hasStatus = hasStatus;
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
	 * @return the durationInUsers
	 */
	public int getDurationInUsers() {
		return durationInUsers;
	}

	/**
	 * @param durationInUsers
	 *            the durationInUsers to set
	 */
	public void setDurationInUsers(int durationInUsers) {
		this.durationInUsers = durationInUsers;
	}

	/**
	 * @return the durationInUsersSpent
	 */
	public int getDurationInUsersSpent() {
		return durationInUsersSpent;
	}

	/**
	 * @param durationInUsersSpent
	 *            the durationInUsersSpent to set
	 */
	public void setDurationInUsersSpent(int durationInUsersSpent) {
		this.durationInUsersSpent = durationInUsersSpent;
	}

	/**
	 * @return the vendorWithTotalTimeOfLoyaltyToOwn
	 */
	public int getVendorWithTotalTimeOfLoyaltyToOwn() {
		return vendorWithTotalTimeOfLoyaltyToOwn;
	}

	/**
	 * @param vendorWithTotalTimeOfLoyaltyToOwn
	 *            the vendorWithTotalTimeOfLoyaltyToOwn to set
	 */
	public void setVendorWithTotalTimeOfLoyaltyToOwn(
			int vendorWithTotalTimeOfLoyaltyToOwn) {
		this.vendorWithTotalTimeOfLoyaltyToOwn = vendorWithTotalTimeOfLoyaltyToOwn;
	}

	/**
	 * @return the sLA_Cost
	 */
	public float getSLA_Cost() {
		return SLA_Cost;
	}

	/**
	 * @param sLA_Cost
	 *            the sLA_Cost to set
	 */
	public void setSLA_Cost(float sLA_Cost) {
		SLA_Cost = sLA_Cost;
	}

	/**
	 * @return the sLA_CostAfterDiscount
	 */
	public float getSLA_CostAfterDiscount() {
		return SLA_CostAfterDiscount;
	}

	/**
	 * @param sLA_CostAfterDiscount
	 *            the sLA_CostAfterDiscount to set
	 */
	public void setSLA_CostAfterDiscount(float sLA_CostAfterDiscount) {
		SLA_CostAfterDiscount = sLA_CostAfterDiscount;
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
	public List<Solution> getAccompanyingSolutions() {
		return accompanyingSolutions;
	}

	/**
	 * @param accompanyingSolutions
	 *            the accompanyingSolutions to set
	 */
	public void setAccompanyingSolutions(List<Solution> accompanyingSolutions) {
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

	public String getOwnerOfAgreement() {
		return ownerOfAgreement;
	}

	public void setOwnerOfAgreement(String ownerOfAgreement) {
		this.ownerOfAgreement = ownerOfAgreement;
	}

	public String getVendorOfAgreement() {
		return vendorOfAgreement;
	}

	public void setVendorOfAgreement(String vendorOfAgreement) {
		this.vendorOfAgreement = vendorOfAgreement;
	}

	public SLA clone() {
		SLA sla = new SLA();
		sla.setSLA_CostCurrency(this.getSLA_CostCurrency());
		sla.setSLA_CostPaymentChargeType(this.getSLA_CostPaymentChargeType());
		if (this.getSLA_StartDate() != null)
			sla.setSLA_StartDate((Date) this.getSLA_StartDate().clone());
		if (this.getSLA_EndDate() != null)
			sla.setSLA_EndDate((Date) this.getSLA_EndDate().clone());
		sla.setHasStatus(this.getHasStatus());
		sla.setDurationInUsages(this.getDurationInUsages());
		sla.setDurationInUsagesSpent(this.getDurationInUsagesSpent());
		sla.setDurationInUsers(this.getDurationInUsers());
		sla.setDurationInUsagesSpent(this.getDurationInUsersSpent());
		sla.setVendorWithTotalTimeOfLoyaltyToOwn(this
				.getVendorWithTotalTimeOfLoyaltyToOwn());
		sla.setSLA_Cost(this.getSLA_Cost());
		sla.setSLA_CostAfterDiscount(this.getSLA_CostAfterDiscount());

		sla.setOwnerOfAgreement(this.getOwnerOfAgreement());
		sla.setSolutionLicense(this.getSolutionLicense().clone());
		sla.setOntologyURI(this.getOntologyURI());
		sla.setVendorOfAgreement(this.getVendorOfAgreement());

		for (int i = 0; i < this.getValidForCountries().size(); i++) {
			sla.getValidForCountries().add(this.getValidForCountries().get(i));
		}

		// TODO accompanyingSolutions are not cloned

		for (int i = 0; i < this.getDiscountSchema().size(); i++) {
			sla.getDiscountSchema()
					.add(this.getDiscountSchema().get(i).clone());
		}

		return sla;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	
	public JsonArray getJsonSchema(){
		
		JsonArray slaArray = new JsonArray();
		JsonObject sla = new JsonObject();

		sla.addProperty("SLA_CostCurrency", this.getSLA_CostCurrency());
		sla.addProperty("SLA_CostPaymentChargeType",
				this.getSLA_CostPaymentChargeType());

		String endDate = "";
		if (this.getSLA_EndDate() != null)
			endDate = String.valueOf(this.getSLA_EndDate());

		String startDate = "";
		if (this.getSLA_StartDate() != null)
			startDate = String.valueOf(this.getSLA_StartDate());

		sla.addProperty("SLA_End_Date", endDate);
		sla.addProperty("SLA_Start_Date", startDate);
		sla.addProperty("Status", this.getHasStatus());
		sla.addProperty("Duration_In_Usages", this.getDurationInUsages());
		sla.addProperty("Duration_In_Usages_Spent",
				this.getDurationInUsagesSpent());
		sla.addProperty("Duration_In_Users", this.getDurationInUsers());
		sla.addProperty("Duration_In_Users_Spent",
				this.getDurationInUsersSpent());
		sla.addProperty("Vendor_With_Total_Time_Of_Loyalty_To_Own",
				this.getVendorWithTotalTimeOfLoyaltyToOwn());
		sla.addProperty("SLA_Cost", this.getSLA_Cost());
		sla.addProperty("SLA_Cost_After_Discount",
				this.getSLA_CostAfterDiscount());
		sla.addProperty("Owner_Of_Agreement", this.getOwnerOfAgreement());
		
		SolutionLicense solLic = this
				.getSolutionLicense();
		JsonObject solutionLicense = solLic.getJsonSchema();
		
		Gson gson = new Gson();
		String validForCountries = gson.toJson(this.getValidForCountries());
		JsonObject countries = new JsonObject();
		countries.addProperty("valid_for_countries", validForCountries);
		
		// accompanyingSolutions list
		JsonArray accompanyingSolutions = new JsonArray();
		for(Solution tempSol: this.getAccompanyingSolutions()){
			JsonObject accompanyingSolution = new JsonObject();
			accompanyingSolution.addProperty("Application_Name", tempSol.getName());
			accompanyingSolution.addProperty("Unified_Listing_ID", tempSol.getId());
			accompanyingSolutions.add(accompanyingSolution);
		}
		
		// discountSchema list
		JsonArray discountSchemas = new JsonArray();
		for (DiscountSchema tempschema : this.getDiscountSchema()) {
			// TODO not finished
			JsonObject schema = tempschema.getJsonSchema();
			discountSchemas.add(schema);

		}

		sla.addProperty("Vendor_Of_Agreement",
				this.getSLA_CostCurrency());
		sla.addProperty("Duration_In_Users",
				this.getSLA_CostCurrency());
		
		slaArray.add(sla);
		slaArray.add(solutionLicense);
		slaArray.add(countries);
		slaArray.add(accompanyingSolutions);
		slaArray.add(discountSchemas);

		return slaArray;
	}
	
	

}
