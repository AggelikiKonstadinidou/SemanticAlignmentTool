package org.cloud4All.IPR;

import com.google.gson.JsonObject;

public class PerCountryReputation {

	private String forCountry = "";
	private float reputationScore;

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
	 * @return the reputationScore
	 */
	public float getReputationScore() {
		return reputationScore;
	}

	/**
	 * @param reputationScore
	 *            the reputationScore to set
	 */
	public void setReputationScore(float reputationScore) {
		this.reputationScore = reputationScore;
	}

	public PerCountryReputation clone() {
		PerCountryReputation rep = new PerCountryReputation();
		rep.setForCountry(this.getForCountry());
		rep.setReputationScore(this.getReputationScore());
		return rep;
	}
	
	public JsonObject getPerCountryReputationJsonObject() {
		JsonObject reputationPerCountry = new JsonObject();
		reputationPerCountry.addProperty("Country", this.getForCountry());
		reputationPerCountry.addProperty("Reputation_Score",
				this.getReputationScore());
		return reputationPerCountry;
	}

}
