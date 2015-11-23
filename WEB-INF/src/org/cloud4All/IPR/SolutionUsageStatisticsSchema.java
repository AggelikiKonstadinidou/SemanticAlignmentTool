package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class SolutionUsageStatisticsSchema {

	private int totalNrOfSuccessfulUsages = 0;
	private int totalNrOfUnsuccessfulUsages = 0;
	private int totalNrOfUsages = 0;
	private int totalNrOfUsers = 0;
	private float usagePercentageInCategory = 0;
	private SolutionUserFeedback solutionUserFeedback = new SolutionUserFeedback();
	private List<PerCountrySolutionUsage> usagePerCountry = new ArrayList<PerCountrySolutionUsage>();
	private List<PerVendorSolutionUsage> usagePerVendor = new ArrayList<PerVendorSolutionUsage>();
	private List<SolutionUserFeedback> userFeedbackPerCountry = new ArrayList<SolutionUserFeedback>();
	private List<SolutionUserFeedback> userFeedbackPerVendor = new ArrayList<SolutionUserFeedback>();

	/**
	 * @return the totalNrOfSuccessfulUsages
	 */
	public int getTotalNrOfSuccessfulUsages() {
		return totalNrOfSuccessfulUsages;
	}

	/**
	 * @param totalNrOfSuccessfulUsages
	 *            the totalNrOfSuccessfulUsages to set
	 */
	public void setTotalNrOfSuccessfulUsages(int totalNrOfSuccessfulUsages) {
		this.totalNrOfSuccessfulUsages = totalNrOfSuccessfulUsages;
	}

	/**
	 * @return the totalNrOfUnsuccessfulUsages
	 */
	public int getTotalNrOfUnsuccessfulUsages() {
		return totalNrOfUnsuccessfulUsages;
	}

	/**
	 * @param totalNrOfUnsuccessfulUsages
	 *            the totalNrOfUnsuccessfulUsages to set
	 */
	public void setTotalNrOfUnsuccessfulUsages(int totalNrOfUnsuccessfulUsages) {
		this.totalNrOfUnsuccessfulUsages = totalNrOfUnsuccessfulUsages;
	}

	/**
	 * @return the totalNrOfUsages
	 */
	public int getTotalNrOfUsages() {
		return totalNrOfUsages;
	}

	/**
	 * @param totalNrOfUsages
	 *            the totalNrOfUsages to set
	 */
	public void setTotalNrOfUsages(int totalNrOfUsages) {
		this.totalNrOfUsages = totalNrOfUsages;
	}

	/**
	 * @return the totalNrOfUsers
	 */
	public int getTotalNrOfUsers() {
		return totalNrOfUsers;
	}

	/**
	 * @param totalNrOfUsers
	 *            the totalNrOfUsers to set
	 */
	public void setTotalNrOfUsers(int totalNrOfUsers) {
		this.totalNrOfUsers = totalNrOfUsers;
	}

	public float getUsagePercentageInCategory() {
		return usagePercentageInCategory;
	}

	public void setUsagePercentageInCategory(float usagePercentageInCategory) {
		this.usagePercentageInCategory = usagePercentageInCategory;
	}

	/**
	 * @return the solutionUserFeedback
	 */
	public SolutionUserFeedback getSolutionUserFeedback() {
		return solutionUserFeedback;
	}

	/**
	 * @param solutionUserFeedback
	 *            the solutionUserFeedback to set
	 */
	public void setSolutionUserFeedback(
			SolutionUserFeedback solutionUserFeedback) {
		this.solutionUserFeedback = solutionUserFeedback;
	}

	/**
	 * @return the usagePerCountry
	 */
	public List<PerCountrySolutionUsage> getUsagePerCountry() {
		return usagePerCountry;
	}

	/**
	 * @param usagePerCountry
	 *            the usagePerCountry to set
	 */
	public void setUsagePerCountry(List<PerCountrySolutionUsage> usagePerCountry) {
		this.usagePerCountry = usagePerCountry;
	}

	/**
	 * @return the usagePerVendor
	 */
	public List<PerVendorSolutionUsage> getUsagePerVendor() {
		return usagePerVendor;
	}

	/**
	 * @param usagePerVendor
	 *            the usagePerVendor to set
	 */
	public void setUsagePerVendor(List<PerVendorSolutionUsage> usagePerVendor) {
		this.usagePerVendor = usagePerVendor;
	}

	/**
	 * @return the userFeedbackPerCountry
	 */
	public List<SolutionUserFeedback> getUserFeedbackPerCountry() {
		return userFeedbackPerCountry;
	}

	/**
	 * @param userFeedbackPerCountry
	 *            the userFeedbackPerCountry to set
	 */
	public void setUserFeedbackPerCountry(
			List<SolutionUserFeedback> userFeedbackPerCountry) {
		this.userFeedbackPerCountry = userFeedbackPerCountry;
	}

	/**
	 * @return the userFeedbackPerVendor
	 */
	public List<SolutionUserFeedback> getUserFeedbackPerVendor() {
		return userFeedbackPerVendor;
	}

	/**
	 * @param userFeedbackPerVendor
	 *            the userFeedbackPerVendor to set
	 */
	public void setUserFeedbackPerVendor(
			List<SolutionUserFeedback> userFeedbackPerVendor) {
		this.userFeedbackPerVendor = userFeedbackPerVendor;
	}

	public SolutionUsageStatisticsSchema clone() {
		SolutionUsageStatisticsSchema usage = new SolutionUsageStatisticsSchema();
		usage.setTotalNrOfSuccessfulUsages(this.getTotalNrOfSuccessfulUsages());
		usage.setTotalNrOfUnsuccessfulUsages(this
				.getTotalNrOfUnsuccessfulUsages());
		usage.setTotalNrOfUsages(this.getTotalNrOfUsages());
		usage.setTotalNrOfUsers(this.getTotalNrOfUsers());
		usage.setUsagePercentageInCategory(this.getUsagePercentageInCategory());
		usage.setSolutionUserFeedback(this.getSolutionUserFeedback().clone());
		for (int i = 0; i < this.getUsagePerCountry().size(); i++) {
			usage.getUsagePerCountry().add(
					this.getUsagePerCountry().get(i).clone());
		}
		for (int i = 0; i < this.getUsagePerVendor().size(); i++) {
			usage.getUsagePerVendor().add(
					this.getUsagePerVendor().get(i).clone());
		}
		for (int i = 0; i < this.getUserFeedbackPerCountry().size(); i++) {
			usage.getUserFeedbackPerCountry().add(
					this.getUserFeedbackPerCountry().get(i).clone());
		}
		for (int i = 0; i < this.getUserFeedbackPerVendor().size(); i++) {
			usage.getUserFeedbackPerVendor().add(
					this.getUserFeedbackPerVendor().get(i).clone());
		}

		return usage;
	}
	
	public JsonObject getJsonSchema(){
		JsonObject solUsageSchema = new JsonObject();
		
		
		if (this.getTotalNrOfSuccessfulUsages() != 0
				|| this.getTotalNrOfUnsuccessfulUsages() != 0
				|| this.getTotalNrOfUsages() != 0
				|| this.getTotalNrOfUsers() != 0
				|| this.getUsagePercentageInCategory() != 0) {

			solUsageSchema.addProperty("Total_Number_Of_Successful_Usages",
					this.getTotalNrOfSuccessfulUsages());
			solUsageSchema.addProperty("Total_Number_Of_Unsuccessful_Usages",
					this.getTotalNrOfUnsuccessfulUsages());
			solUsageSchema.addProperty("Total_Number_Of_Usages",
					this.getTotalNrOfUsages());
			solUsageSchema.addProperty("Total_Number_Of_Users",
					this.getTotalNrOfUsers());
			solUsageSchema.addProperty("Usage_Percentage_In_Category",
					this.getUsagePercentageInCategory());

			// TODO vendor ,usagePerCountry,usagePerVendor,
			// userFeedbackPerCountry
			// userFeedbackPerVendor
			JsonObject solutionUserFeedback = this.getSolutionUserFeedback()
					.getJsonSchema();

			if (solutionUserFeedback != null)
				solUsageSchema.add("Solution_User_Feedback",
						solutionUserFeedback);

			return solUsageSchema;
		} else
			return null;
	}
}
