package org.cloud4All.ontology;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.impl.IndividualImpl;

public class RegistryTerm implements Comparable{

	private String name = "";
	private String description = "";
	private String valueSpace = "";
	private String type = "";
	private String defaultValue = "";
	private IndividualImpl ind = null;
	private String ontologyURI = "";
	private String id = "";
	private String notes = "";
	private List<String> alias = new ArrayList<String>();
	//not sure, check string (hash)
	private String check = "";
	//indicates if the term is proposed or accepted
	private String status = "";
	private String aliasString = "";
	private boolean found = false;
	private boolean edit = false;
	private ArrayList<RegistryTerm> aliasesTerms = new ArrayList<RegistryTerm>();
	private String solutionId = "";
	private String aliasOf = "";

	public RegistryTerm() {

		this.name = "";
		this.description = "";
		this.valueSpace = "";
		this.type = "";
		this.defaultValue = "";
		this.ontologyURI = "";
		this.id = "";
		this.notes = "";
		alias = new ArrayList<String>();
	    this.check = "";
	    this.status = "";
	    this.aliasString = "";
	    this.edit = false;
	    aliasesTerms = new ArrayList<RegistryTerm>();
	}
	
	public String getSolutionId() {
		return solutionId;
	}
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}
	public String getCheck() {
		return check;
	}

    public void setCheck(String check) {
		this.check = check;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getValueSpace() {
		return valueSpace;
	}

	public void setValueSpace(String valueSpace) {

		this.valueSpace = valueSpace;
	}

	public IndividualImpl getInd() {
		return ind;
	}

	public void setInd(IndividualImpl ind) {
		this.ind = ind;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {

		this.defaultValue = defaultValue;
	}

	public String getOntologyURI() {
		return ontologyURI;
	}

	public void setOntologyURI(String ontologyURI) {
		this.ontologyURI = ontologyURI;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the alias
	 */
	public List<String> getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(List<String> alias) {
		this.alias = alias;
	}

	public RegistryTerm clone() {
		RegistryTerm reg = new RegistryTerm();
		reg.setDefaultValue(this.getDefaultValue());
		reg.setDescription(this.getDescription());
		reg.setName(this.getName());
		reg.setType(this.getType());
		reg.setValueSpace(this.getValueSpace());
		reg.setOntologyURI(this.getOntologyURI());
		reg.setInd(this.getInd());
		reg.setId(this.id);
		reg.setNotes(this.getNotes());
		reg.setStatus(this.getStatus());
		for (int i = 0; i < this.getAlias().size(); i++) {
			reg.getAlias().add(this.getAlias().get(i));
		}
		reg.setCheck("");
		reg.setAliasString("");
		
		for(int i=0; i< this.getAliasesTerms().size(); i++){
			reg.getAliasesTerms().add(this.getAliasesTerms().get(i).clone());
		}
		return reg;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		if(this.getName().isEmpty())
			 this.setName("undefined name");
		
		return this.getName()+" (ID= "+this.getId()+")";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAliasString() {
		return aliasString;
	}

	public void setAliasString(String aliasString) {
		this.aliasString = aliasString;
	}
	
	public RegistryTermForJSONNew convertRegistryTermToJSONTerm(){
		RegistryTermForJSONNew jsonTerm = new RegistryTermForJSONNew();
		
		jsonTerm.getRecord().setUniqueId(this.getId());
		jsonTerm.getRecord().setValueSpace(this.getValueSpace());
		jsonTerm.getRecord().setNotes(this.getNotes());
		jsonTerm.getRecord().setStatus(this.getStatus());
		jsonTerm.getRecord().setTermLabel(this.getName());
		jsonTerm.getRecord().setType(this.getType());
		jsonTerm.getRecord().setDefinition(this.getDescription());
		
		return jsonTerm;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	@Override
	public int compareTo(Object obj) {

		int number = this.getId().toLowerCase().compareTo(
				((RegistryTerm) obj).getId().toLowerCase());

		return number;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public ArrayList<RegistryTerm> getAliasesTerms() {
		return aliasesTerms;
	}

	public void setAliasesTerms(ArrayList<RegistryTerm> aliasesTerms) {
		this.aliasesTerms = aliasesTerms;
	}

	public String getAliasOf() {
		return aliasOf;
	}

	public void setAliasOf(String aliasOf) {
		this.aliasOf = aliasOf;
	}
	
	

}
