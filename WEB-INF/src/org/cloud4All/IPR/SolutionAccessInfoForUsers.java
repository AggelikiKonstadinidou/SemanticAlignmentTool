package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class SolutionAccessInfoForUsers {

	private String description = "";
	private String URLForAccess = "";
	private float maxDiscount;
	private CommercialCostSchema commercialCostSchema = new CommercialCostSchema();
	private SolutionLicense license = new SolutionLicense();
	private Vendor solutionVendor = new Vendor();
	private List<String> validForCountries = new ArrayList<String>();
	
	public JsonObject getJsonSchema(){
		JsonObject solutionAccessInfoForUsers = new JsonObject();
		
		boolean flag = false;
		if (!this.getDescription().isEmpty()
				|| !this.getURLForAccess().isEmpty()
				|| this.getMaxDiscount() != 0)
//				|| !this.getSolutionVendor().getVendorName().isEmpty()
//				|| !this.getValidForCountries().toString().isEmpty())
			flag = true;
			
		if (flag) {
			solutionAccessInfoForUsers.addProperty("Description",
					this.getDescription());
			solutionAccessInfoForUsers.addProperty("URL",
					this.getURLForAccess());
			solutionAccessInfoForUsers.addProperty("Max_Discount",
					this.getMaxDiscount());

			JsonObject commercialCostSchema = this.getCommercialCostSchema()
					.getJsonSchema();
			
			if(commercialCostSchema!=null)
			solutionAccessInfoForUsers.add("Commercial_Cost_Schema",
					commercialCostSchema);
			
			
			solutionAccessInfoForUsers.addProperty("Solution_Vendor", this
					.getSolutionVendor().getVendorName());
			solutionAccessInfoForUsers.addProperty("Countries", this
					.getValidForCountries().toString());
			return solutionAccessInfoForUsers;
		} else
			return null;

	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the uRLForAccess
	 */
	public String getURLForAccess() {
		return URLForAccess;
	}

	/**
	 * @param uRLForAccess
	 *            the uRLForAccess to set
	 */
	public void setURLForAccess(String uRLForAccess) {
		URLForAccess = uRLForAccess;
	}

	/**
	 * @return the maxDiscount
	 */
	public float getMaxDiscount() {
		return maxDiscount;
	}

	/**
	 * @param maxDiscount
	 *            the maxDiscount to set
	 */
	public void setMaxDiscount(float maxDiscount) {
		this.maxDiscount = maxDiscount;
	}

	/**
	 * @return the commercialCostSchema
	 */
	public CommercialCostSchema getCommercialCostSchema() {
		return commercialCostSchema;
	}

	/**
	 * @param commercialCostSchema
	 *            the commercialCostSchema to set
	 */
	public void setCommercialCostSchema(
			CommercialCostSchema commercialCostSchema) {
		this.commercialCostSchema = commercialCostSchema;
	}

	/**
	 * @return the license
	 */
	public SolutionLicense getLicense() {
		return license;
	}

	/**
	 * @param license
	 *            the license to set
	 */
	public void setLicense(SolutionLicense license) {
		this.license = license;
	}

	/**
	 * @return the solutionVendor
	 */
	public Vendor getSolutionVendor() {
		return solutionVendor;
	}

	/**
	 * @param solutionVendor
	 *            the solutionVendor to set
	 */
	public void setSolutionVendor(Vendor solutionVendor) {
		this.solutionVendor = solutionVendor;
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

	public SolutionAccessInfoForUsers clone() {
		SolutionAccessInfoForUsers access = new SolutionAccessInfoForUsers();
		access.setDescription(this.getDescription());
		access.setMaxDiscount(this.getMaxDiscount());
		access.setURLForAccess(this.getURLForAccess());
		access.setCommercialCostSchema(this.getCommercialCostSchema().clone());
		access.setLicense(this.getLicense().clone());
		for (int i = 0; i < this.getValidForCountries().size(); i++) {
			access.getValidForCountries().add(
					this.getValidForCountries().get(i));
		}

		// TODO vendor is missing
		return access;
	}

}
