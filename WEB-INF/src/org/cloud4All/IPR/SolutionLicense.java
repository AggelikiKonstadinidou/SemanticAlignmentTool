package org.cloud4All.IPR;

import com.google.gson.JsonObject;

public class SolutionLicense {

	private String licenseDescription = "";
	private String licenseName = "";
	private boolean proprietary = false;

	/**
	 * @return the licenseDescription
	 */
	public String getLicenseDescription() {
		return licenseDescription;
	}

	/**
	 * @param licenseDescription
	 *            the licenseDescription to set
	 */
	public void setLicenseDescription(String licenseDescription) {
		this.licenseDescription = licenseDescription;
	}

	/**
	 * @return the licenseName
	 */
	public String getLicenseName() {
		return licenseName;
	}

	/**
	 * @param licenseName
	 *            the licenseName to set
	 */
	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}

	/**
	 * @return the proprietary
	 */
	public boolean isProprietary() {
		return proprietary;
	}

	/**
	 * @param proprietary
	 *            the proprietary to set
	 */
	public void setProprietary(boolean proprietary) {
		this.proprietary = proprietary;
	}

	public SolutionLicense clone() {
		SolutionLicense license = new SolutionLicense();
		license.setLicenseDescription(this.getLicenseDescription());
		license.setLicenseName(this.getLicenseName());
		license.setProprietary(this.isProprietary());
		return license;
	}
	
	public JsonObject getJsonSchema(){
		JsonObject solutionLicense = new JsonObject();

		if (!this.getLicenseName().isEmpty()
				|| !this.getLicenseDescription().isEmpty()) {
			solutionLicense.addProperty("License_Name", this.getLicenseName());
			solutionLicense.addProperty("License_Description",
					this.getLicenseDescription());
			solutionLicense.addProperty("Proprietary", this.isProprietary());
			return solutionLicense;
		} else
			return null;
	}

}
