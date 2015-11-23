package org.cloud4All.IPR;

import com.google.gson.JsonObject;

public class SolutionTrialSchema {

	private String limitedFunctionalityDescription = "";
	private boolean offersFullFunctionalityDuringTrial;
	private int durationInDays = 0;
	private int durationInUsages = 0;
	
	public JsonObject getJsonSchema(){
		JsonObject solutionTrialSchema = new JsonObject();

		if (!this.getLimitedFunctionalityDescription().isEmpty()
				|| this.getDurationInDays() != 0
				|| this.getDurationInUsages() != 0) {

			solutionTrialSchema.addProperty(
					"Limited_Functionality_Description",
					this.getLimitedFunctionalityDescription());
			solutionTrialSchema.addProperty(
					"Offers_Full_Functionality_During_Trial",
					this.isOffersFullFunctionalityDuringTrial());
			solutionTrialSchema.addProperty("Duration_In_Days",
					this.getDurationInDays());
			solutionTrialSchema.addProperty("Duration_In_Usages",
					this.getDurationInUsages());
			return solutionTrialSchema;

		} else
			return null;
		
	}

	/**
	 * @return the limitedFunctionalityDescription
	 */
	public String getLimitedFunctionalityDescription() {
		return limitedFunctionalityDescription;
	}

	/**
	 * @param limitedFunctionalityDescription
	 *            the limitedFunctionalityDescription to set
	 */
	public void setLimitedFunctionalityDescription(
			String limitedFunctionalityDescription) {
		this.limitedFunctionalityDescription = limitedFunctionalityDescription;
	}

	/**
	 * @return the offersFullFunctionalityDuringTrial
	 */
	public boolean isOffersFullFunctionalityDuringTrial() {
		return offersFullFunctionalityDuringTrial;
	}

	/**
	 * @param offersFullFunctionalityDuringTrial
	 *            the offersFullFunctionalityDuringTrial to set
	 */
	public void setOffersFullFunctionalityDuringTrial(
			boolean offersFullFunctionalityDuringTrial) {
		this.offersFullFunctionalityDuringTrial = offersFullFunctionalityDuringTrial;
	}

	/**
	 * @return the durationInDays
	 */
	public int getDurationInDays() {
		return durationInDays;
	}

	/**
	 * @param durationInDays
	 *            the durationInDays to set
	 */
	public void setDurationInDays(int durationInDays) {
		this.durationInDays = durationInDays;
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

	public SolutionTrialSchema clone() {
		SolutionTrialSchema schema = new SolutionTrialSchema();
		schema.setDurationInDays(this.getDurationInDays());
		schema.setDurationInUsages(this.getDurationInUsages());
		schema.setLimitedFunctionalityDescription(this
				.getLimitedFunctionalityDescription());
		schema.setOffersFullFunctionalityDuringTrial(this
				.isOffersFullFunctionalityDuringTrial());
		return schema;
	}

}
