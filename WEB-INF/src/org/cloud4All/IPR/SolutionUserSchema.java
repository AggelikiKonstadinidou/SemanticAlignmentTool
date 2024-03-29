package org.cloud4All.IPR;

import org.cloud4All.Solution;

public class SolutionUserSchema {

	private String userId = "";
	private EULA eula;
	private String refersToSolution = "";

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the eula
	 */
	public EULA getEula() {
		return eula;
	}

	/**
	 * @param eula
	 *            the eula to set
	 */
	public void setEula(EULA eula) {
		this.eula = eula;
	}

	/**
	 * @return the refersToSolution
	 */
	public String getRefersToSolution() {
		return refersToSolution;
	}

	/**
	 * @param refersToSolution
	 *            the refersToSolution to set
	 */
	public void setRefersToSolution(String refersToSolution) {
		this.refersToSolution = refersToSolution;
	}

	public SolutionUserSchema clone() {
		SolutionUserSchema schema = new SolutionUserSchema();
		schema.setEula(this.getEula().clone());
		schema.setRefersToSolution(this.getRefersToSolution());
		schema.setUserId(this.getUserId());
		return schema;
	}

}
