package org.cloud4All.ontology;

import com.hp.hpl.jena.ontology.impl.IndividualImpl;

public class EASTINProperty implements Comparable{

	private String ind;
	private String name = "";
	private String type="";
	private String definition="";
	private String id="";
	private String value="";
	private String refersToSolution = "";
	private String proponentName = "";
	private String proponentEmail = "";
	private String refersToSolutionName = "";
	private String refersToSolutionID = "";
	private String belongsToCluster = "";
	private boolean isProposed = false;
	private String allSolutionsString = "";
	private boolean found = false;
	private boolean edit = false;
	private String proposedString = "";
	private String relatedToTypeOfApplication = "";

	public String getProposedString() {
		return proposedString;
	}

	public void setProposedString() {
		if (this.isProposed)
			this.proposedString = "true";
		else
			this.proposedString = "false";
	}

	public String getInd() {
		return ind;
	}

	public void setInd(String ind) {
		this.ind = ind;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if (name.equals(""))
			return "None";
		else {
			if (belongsToCluster.equals(""))
				return name;
			else
				return name + " (" +type+" in '"+ belongsToCluster + "')";
		}
	}
	
	
	
	

	public String getAllSolutionsString() {
		return allSolutionsString;
	}

	public void setAllSolutionsString(String allSolutionsString) {
		this.allSolutionsString = allSolutionsString;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.getName().equals(((EASTINProperty) obj).getName())
				&& this.getBelongsToCluster().equals(
						((EASTINProperty) obj).getBelongsToCluster())
				&& this.getType().equals(((EASTINProperty) obj).getType())) {
			return true;
		} else
			return false;
	}

	public EASTINProperty clone() {
		EASTINProperty pr = new EASTINProperty();
		pr.setDefinition(this.getDefinition());
		pr.setId(this.getId());
		pr.setName(this.getName());
		pr.setType(this.getType());
		pr.setValue(this.getValue());
		pr.setProponentEmail(this.getProponentEmail());
		pr.setProponentName(this.getProponentName());
		pr.setRefersToSolution(this.getRefersToSolution());
		pr.setRefersToSolutionID(this.getRefersToSolutionID());
		pr.setRefersToSolutionName(this.getRefersToSolutionName());
		pr.setBelongsToCluster(this.getBelongsToCluster());
		pr.setProposed(this.isProposed);
		pr.setInd(this.getInd());
		pr.setAllSolutionsString(this.allSolutionsString);
		pr.setRelatedToTypeOfApplication(this.relatedToTypeOfApplication);
		return pr;
	}
	

	public String getRefersToSolution() {
		return refersToSolution;
	}

	public void setRefersToSolution(String refersToSolution) {
		this.refersToSolution = refersToSolution;
	}

	public String getProponentName() {
		return proponentName;
	}

	public void setProponentName(String proponentName) {
		this.proponentName = proponentName;
	}

	public String getProponentEmail() {
		return proponentEmail;
	}

	public void setProponentEmail(String proponentEmail) {
		this.proponentEmail = proponentEmail;
	}

	public String getRefersToSolutionName() {
		return refersToSolutionName;
	}

	public void setRefersToSolutionName(String refersToSolutionName) {
		this.refersToSolutionName = refersToSolutionName;
	}

	public String getRefersToSolutionID() {
		return refersToSolutionID;
	}

	public void setRefersToSolutionID(String refersToSolutionID) {
		this.refersToSolutionID = refersToSolutionID;
	}

	public String getBelongsToCluster() {
		return belongsToCluster;
	}

	public void setBelongsToCluster(String belongsToCluster) {
		this.belongsToCluster = belongsToCluster;
	}

	public boolean isProposed() {
		return isProposed;
	}

	public void setProposed(boolean isProposed) {
		this.isProposed = isProposed;
		setProposedString();
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	@Override
	public int compareTo(Object obj) {
		int number = this.getName().toLowerCase()
				.compareTo(((EASTINProperty) obj).getName().toLowerCase());
		return number;
	}

	public String getRelatedToTypeOfApplication() {
		return relatedToTypeOfApplication;
	}

	public void setRelatedToTypeOfApplication(String relatedToTypeOfApplication) {
		this.relatedToTypeOfApplication = relatedToTypeOfApplication;
	}
	
	

}
