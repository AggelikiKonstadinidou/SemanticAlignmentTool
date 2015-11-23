package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class SolutionAccessInfoForVendors {

	private String description = "";
	private String URLForAccess = "";
	private float maxDiscount;
	private CommercialCostSchema commercialCostSchema = new CommercialCostSchema();
	private SolutionLicense license = new SolutionLicense();
	private Vendor solutionOwner = new Vendor();
	
	public JsonObject getJsonSchema(){
		JsonObject solutionAccessInfoForVendors = new JsonObject();

		boolean flag = false;
		if (!this.getDescription().isEmpty()
				|| !this.getURLForAccess().isEmpty()
				|| this.getMaxDiscount() != 0
				|| !this.getSolutionOwner().getVendorName().isEmpty())
			flag = true;

		if (flag) {
			
			solutionAccessInfoForVendors.addProperty("Description",
					this.getDescription());
			solutionAccessInfoForVendors.addProperty("URL",
					this.getURLForAccess());
			solutionAccessInfoForVendors.addProperty("Max_Discount",
					this.getMaxDiscount());

			JsonObject commercialCostSchema = this.getCommercialCostSchema()
					.getJsonSchema();

			if (commercialCostSchema != null)
				solutionAccessInfoForVendors.add("Commercial_Cost_Schema",
						commercialCostSchema);

			JsonObject license = this.getLicense().getJsonSchema();

			if (license != null)
				solutionAccessInfoForVendors.add("Solution_License", license);

			solutionAccessInfoForVendors.addProperty("Solution_Owner", this
					.getSolutionOwner().getVendorName());
			
			return solutionAccessInfoForVendors;
			
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
	 * @return the solutionOwner
	 */
	public Vendor getSolutionOwner() {
		return solutionOwner;
	}

	/**
	 * @param solutionOwner
	 *            the solutionOwner to set
	 */
	public void setSolutionOwner(Vendor solutionOwner) {
		this.solutionOwner = solutionOwner;
	}

	public SolutionAccessInfoForVendors clone() {
		SolutionAccessInfoForVendors access = new SolutionAccessInfoForVendors();
		access.setDescription(this.getDescription());
		access.setMaxDiscount(this.getMaxDiscount());
		access.setURLForAccess(this.getURLForAccess());
		access.setCommercialCostSchema(this.getCommercialCostSchema().clone());
		access.setLicense(this.getLicense().clone());

		// TODO vendor is missing
		return access;
	}
	
	

}
