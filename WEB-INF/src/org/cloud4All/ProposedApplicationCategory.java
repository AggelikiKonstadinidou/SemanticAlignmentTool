package org.cloud4All;

import java.util.ArrayList;
import java.util.List;

public class ProposedApplicationCategory implements Comparable{

	private String uri = "";
	private String name = "";
	private String description = "";
	private String all_solutions_string;
	private List<String> refersToSolutions = new ArrayList<String>();
	private String proponentEmail = "";
	private String proponentName = "";
	private String proposedForProduct = "";
	private String proposedForProductID = "";
	private String parent = "";
	private boolean found = false;
	private ProposedApplicationCategory oldcategory = null;
	private boolean edit = false;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	@Override
	public String toString() {
		return name;
	}

	public List<String> getRefersToSolutions() {
		return refersToSolutions;
	}

	public void setRefersToSolutions(List<String> refersToSolutions) {
		this.refersToSolutions = refersToSolutions;
	}

	public String getProponentEmail() {
		return proponentEmail;
	}

	public void setProponentEmail(String proponentEmail) {
		this.proponentEmail = proponentEmail;
	}

	public String getProponentName() {
		return proponentName;
	}

	public void setProponentName(String proponentName) {
		this.proponentName = proponentName;
	}

	public String getProposedForProduct() {
		return proposedForProduct;
	}

	public void setProposedForProduct(String proposedForProduct) {
		this.proposedForProduct = proposedForProduct;
	}

	public ProposedApplicationCategory clone() {
		ProposedApplicationCategory cat = new ProposedApplicationCategory();
		cat.setDescription(this.getDescription());
		cat.setName(this.getName());
		cat.setUri(this.getUri());
		cat.setProponentEmail(this.getProponentEmail());
		cat.setProponentName(this.getProponentName());
		cat.setProposedForProduct(this.getProposedForProduct());
		cat.setAll_solutions_string(this.getAll_solutions_string());
		cat.setParent(this.getParent());
		cat.setEdit(this.edit);
		cat.setOldcategory(this.oldcategory);
		cat.setProposedForProductID(this.getProposedForProductID());
		for (int i = 0; i < this.getRefersToSolutions().size(); i++)
			cat.getRefersToSolutions().add(this.getRefersToSolutions().get(i));
		return cat;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getAll_solutions_string() {
		return all_solutions_string;
	}

	public void setAll_solutions_string(String all_solutions_string) {
		this.all_solutions_string = all_solutions_string;
	}

	public String getProposedForProductID() {
		return proposedForProductID;
	}

	public void setProposedForProductID(String proposedForProductID) {
		this.proposedForProductID = proposedForProductID;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public ProposedApplicationCategory getOldcategory() {
		return oldcategory;
	}

	public void setOldcategory(ProposedApplicationCategory oldcategory) {
		this.oldcategory = oldcategory;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	@Override
	public int compareTo(Object obj) {
		int number = this.getName().toLowerCase().compareTo(
				((ProposedApplicationCategory) obj).getName().toLowerCase());
		return number;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			ProposedApplicationCategory cat = (ProposedApplicationCategory) obj;
			if (this.getName().equals(cat.getName())
					&& this.getDescription().equals(cat.getDescription())
					&& this.getProposedForProduct().equals(cat.getProposedForProduct())
					&& this.getAll_solutions_string().equals(cat.getAll_solutions_string())
					&& this.getUri().equals(cat.getUri())) {
				return true;
			} else
				return false;
		} catch (Exception ex) {
			return false;
		}
	}
	
	

}
