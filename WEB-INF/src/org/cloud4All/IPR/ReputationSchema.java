package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ReputationSchema {

	private float overallReputationScore = 0;

	private List<PerCountryReputation> perCountryReputation = new ArrayList<PerCountryReputation>();

	/**
	 * @return the overallReputationScore
	 */
	public float getOverallReputationScore() {
		return overallReputationScore;
	}

	/**
	 * @param overallReputationScore
	 *            the overallReputationScore to set
	 */
	public void setOverallReputationScore(float overallReputationScore) {
		this.overallReputationScore = overallReputationScore;
	}

	/**
	 * @return the perCountryReputation
	 */
	public List<PerCountryReputation> getPerCountryReputation() {
		return perCountryReputation;
	}

	/**
	 * @param perCountryReputation
	 *            the perCountryReputation to set
	 */
	public void setPerCountryReputation(
			List<PerCountryReputation> perCountryReputation) {
		this.perCountryReputation = perCountryReputation;
	}

	public ReputationSchema clone() {
		ReputationSchema rep = new ReputationSchema();
		rep.setOverallReputationScore(this.getOverallReputationScore());
		for (int i = 0; i < this.getPerCountryReputation().size(); i++) {
			rep.getPerCountryReputation().add(
					this.getPerCountryReputation().get(i).clone());

		}
		return rep;
	}
	
	public JsonObject getJsonSchema(){
		JsonObject reputationObj = new JsonObject();
		boolean flag = false;

		if (this.getOverallReputationScore() != 0) {
			reputationObj.addProperty("Overall_Reputation_Score",
					this.getOverallReputationScore());
			flag = true;
		}

		JsonArray reputationPerCountryArray = new JsonArray();
		for (PerCountryReputation tempReputation : this
				.getPerCountryReputation()) {
			JsonObject reputationPerCountry = tempReputation
					.getPerCountryReputationJsonObject();
			reputationPerCountryArray.add(reputationPerCountry);
		}

		if (this.getPerCountryReputation().size() != 0) {
			reputationObj.add("Per_Country_Reputation",
					reputationPerCountryArray);
			flag = true;
		}

		if (flag)
			return reputationObj;
		else
			return null;
	}

}
