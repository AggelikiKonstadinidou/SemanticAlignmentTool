package org.cloud4All.IPR;

import com.google.gson.JsonObject;

public class SolutionUserFeedback {

	private String forCountry = "";
	private int nrOfNegativeRatings = 0;
	private int nrOfPositiveRatings = 0;
	private int totalNumberOfRatings = 0;
	private float averageRating = 0;
	private Vendor vendor;

	/**
	 * @return the forCountry
	 */
	public String getForCountry() {
		return forCountry;
	}

	/**
	 * @param forCountry
	 *            the forCountry to set
	 */
	public void setForCountry(String forCountry) {
		this.forCountry = forCountry;
	}

	/**
	 * @return the nrOfNegativeRatings
	 */
	public int getNrOfNegativeRatings() {
		return nrOfNegativeRatings;
	}

	/**
	 * @param nrOfNegativeRatings
	 *            the nrOfNegativeRatings to set
	 */
	public void setNrOfNegativeRatings(int nrOfNegativeRatings) {
		this.nrOfNegativeRatings = nrOfNegativeRatings;
	}

	/**
	 * @return the nrOfPositiveRatings
	 */
	public int getNrOfPositiveRatings() {
		return nrOfPositiveRatings;
	}

	/**
	 * @param nrOfPositiveRatings
	 *            the nrOfPositiveRatings to set
	 */
	public void setNrOfPositiveRatings(int nrOfPositiveRatings) {
		this.nrOfPositiveRatings = nrOfPositiveRatings;
	}

	/**
	 * @return the totalNumberOfRatings
	 */
	public int getTotalNumberOfRatings() {
		return totalNumberOfRatings;
	}

	/**
	 * @param totalNumberOfRatings
	 *            the totalNumberOfRatings to set
	 */
	public void setTotalNumberOfRatings(int totalNumberOfRatings) {
		this.totalNumberOfRatings = totalNumberOfRatings;
	}

	/**
	 * @return the averageRating
	 */
	public float getAverageRating() {
		return averageRating;
	}

	/**
	 * @param averageRating
	 *            the averageRating to set
	 */
	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}

	/**
	 * @return the vendor
	 */
	public Vendor getVendor() {
		return vendor;
	}

	/**
	 * @param vendor
	 *            the vendor to set
	 */
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public SolutionUserFeedback clone() {
		SolutionUserFeedback userFeedback = new SolutionUserFeedback();
		userFeedback.setAverageRating(this.getAverageRating());
		userFeedback.setForCountry(this.getForCountry());
		userFeedback.setNrOfNegativeRatings(this.getNrOfNegativeRatings());
		userFeedback.setNrOfPositiveRatings(this.getNrOfPositiveRatings());
		userFeedback.setTotalNumberOfRatings(this.getTotalNumberOfRatings());
		// TODO clone vendor
		// userFeedback.setVendor(this.getVendor().clone());
		return userFeedback;
	}
	
	public JsonObject getJsonSchema(){
		JsonObject solutionUserFeedback = new JsonObject();

		if (!this.getForCountry().isEmpty()
				|| this.getNrOfNegativeRatings() != 0
				|| this.getNrOfPositiveRatings() != 0
				|| this.getTotalNumberOfRatings() != 0
				|| this.getAverageRating() != 0) {
			solutionUserFeedback.addProperty("Country", this.getForCountry());
			solutionUserFeedback.addProperty("Number_Of_Negative_Ratings",
					this.getNrOfNegativeRatings());
			solutionUserFeedback.addProperty("Number_Of_Positive_Ratings",
					this.getNrOfPositiveRatings());
			solutionUserFeedback.addProperty("Total_Number_Of_Ratings",
					this.getTotalNumberOfRatings());
			solutionUserFeedback.addProperty("Average_Rating",
					this.getAverageRating());

			return solutionUserFeedback;
		} else
			return null;
	}

}
