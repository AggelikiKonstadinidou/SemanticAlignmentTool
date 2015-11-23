package org.cloud4All.ontology;

import java.util.ArrayList;
import java.util.List;

public class ETNACluster implements Comparable{

	private String name;
	private String ontologyName;
	private List<EASTINProperty> properties = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> measureProperties = new ArrayList<EASTINProperty>();
	private List<String> selectedProperties = new ArrayList<String>();
	private String selectedProperty = "";
	private boolean singleSelection = false;
	private String description = "";
	private String itemProposalName = "";
	private String itemProposalDescription = "";
	private String itemProposalType ="";
	private boolean itemProposed = false;
	private EASTINProperty selectedProposedItem = new EASTINProperty();
	private List<EASTINProperty> allproperties = new ArrayList<EASTINProperty>();
	private String proponentName = "";
	private String proponentEmail = "";
	private String proposedForProduct = "";
	private String proposedForProductID = "";
	private String allProposalsString = "";
	private boolean found = false;
	private boolean edit = false;
	private List<EASTINProperty> attributesToShow = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> attributesToHide = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> measuresToShow = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> measuresToHide = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> attributesToShowInView = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> measuresToShowInView = new ArrayList<EASTINProperty>();
	private boolean flag = false;
	private boolean showItems = false; //if the show button is pressed
	private boolean hasToHideItems = false;

	public ETNACluster() {
		this.name = "";
		this.ontologyName = "";
		this.properties = new ArrayList<EASTINProperty>();
		this.measureProperties = new ArrayList<EASTINProperty>();
		this.singleSelection = false;
		this.selectedProperties = new ArrayList<String>();
		this.description = "";
		this.selectedProperty = "";
		this.allproperties = new ArrayList<EASTINProperty>();
		this.allProposalsString = "";
		this.flag = false;
		this.attributesToShow = new ArrayList<EASTINProperty>();
		this.attributesToHide = new ArrayList<EASTINProperty>();
		this.measuresToShow = new ArrayList<EASTINProperty>();
		this.measuresToHide = new ArrayList<EASTINProperty>();
		this.showItems = false;
		this.hasToHideItems = false;

	}

	public String getAllProposalsString() {
		return allProposalsString;
	}

	public void setAllProposalsString(String allProposalsString) {
		this.allProposalsString = allProposalsString;
	}

	public List<EASTINProperty> getAllproperties() {
		return allproperties;
	}

	public void setAllproperties(List<EASTINProperty> allproperties) {
		this.allproperties = allproperties;
	}

	public List<EASTINProperty> getMeasureProperties() {
		return measureProperties;
	}

	public void setMeasureProperties(List<EASTINProperty> measureProperties) {
		this.measureProperties = measureProperties;
	}

	public String getItemProposalType() {
		return itemProposalType;
	}

	public void setItemProposalType(String itemProposalType) {
		this.itemProposalType = itemProposalType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EASTINProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<EASTINProperty> properties) {
		this.properties = properties;
	}

	public List<String> getSelectedProperties() {
		return selectedProperties;
	}

	public void setSelectedProperties(List<String> selectedProperties) {
		this.selectedProperties = selectedProperties;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	public boolean isSingleSelection() {
		return singleSelection;
	}

	public void setSingleSelection(boolean singleSelection) {
		this.singleSelection = singleSelection;
	}

	public String getSelectedProperty() {
		return selectedProperty;
	}

	public void setSelectedProperty(String selectedProperty) {
		this.selectedProperty = selectedProperty;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ETNACluster clone() {
		ETNACluster cl = new ETNACluster();

		cl.setName(this.getName());
		cl.setDescription(this.getDescription());
		cl.setOntologyName(this.getOntologyName());
		cl.setSelectedProperty(this.getSelectedProperty());
		cl.setSingleSelection(this.isSingleSelection());
		cl.setProponentEmail(this.getProponentEmail());
		cl.setProponentName(this.getProponentName());
		cl.setProposedForProduct(this.getProposedForProduct());
		cl.setProposedForProductID(this.getProposedForProductID());
		cl.setAllProposalsString(this.allProposalsString);
		cl.setShowItems(this.showItems);
		cl.setHasToHideItems(this.hasToHideItems);
		
		for (int i = 0; i < this.getProperties().size(); i++) {
			cl.getProperties().add(this.getProperties().get(i).clone());
			cl.getAllproperties().add(this.getProperties().get(i).clone());
		}
		for (int i = 0; i < this.getMeasureProperties().size(); i++) {
			cl.getMeasureProperties().add(
					this.getMeasureProperties().get(i).clone());
			cl.getAllproperties().add(
					this.getMeasureProperties().get(i).clone());
		}
		for (int i = 0; i < this.getSelectedProperties().size(); i++) {
			cl.getSelectedProperties().add(this.getSelectedProperties().get(i));
		}
		
		cl.setFlag(this.flag);
		
		for (int i = 0; i < this.getAttributesToShow().size(); i++) {
			cl.getAttributesToShow().add(this.getAttributesToShow().get(i).clone());
		}

		for (int i = 0; i < this.getAttributesToHide().size(); i++) {
			cl.getAttributesToHide().add(this.getAttributesToHide().get(i).clone());
		}

		for (int i = 0; i < this.getMeasuresToShow().size(); i++) {
			cl.getMeasuresToShow().add(this.getMeasuresToShow().get(i).clone());
		}

		for (int i = 0; i < this.getMeasuresToHide().size(); i++) {
			cl.getMeasuresToHide().add(this.getMeasuresToHide().get(i).clone());
		}
		
		for (int i = 0; i < this.getAttributesToShowInView().size(); i++) {
			cl.getAttributesToShowInView().add(this.getAttributesToShowInView().get(i).clone());
		}
		
		for (int i = 0; i < this.getMeasuresToShowInView().size(); i++) {
			cl.getMeasuresToShowInView().add(this.getMeasuresToShowInView().get(i).clone());
		}
		
		return cl;
	}

	public String getItemProposalName() {
		return itemProposalName;
	}

	public void setItemProposalName(String itemProposalName) {
		this.itemProposalName = itemProposalName;
	}

	public String getItemProposalDescription() {
		return itemProposalDescription;
	}

	public void setItemProposalDescription(String itemProposalDescription) {
		this.itemProposalDescription = itemProposalDescription;
	}

	@Override
	public String toString() {
		if (name.equals("")) {
			return "None";
		} else
			return name;
	}

	public EASTINProperty getSelectedProposedItem() {
		return selectedProposedItem;
	}

	public void setSelectedProposedItem(EASTINProperty selectedProposedItem) {
		this.selectedProposedItem = selectedProposedItem;
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

	public String getProposedForProduct() {
		return proposedForProduct;
	}

	public void setProposedForProduct(String proposedForProduct) {
		this.proposedForProduct = proposedForProduct;
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

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	@Override
	public int compareTo(Object obj) {
		int number = this.getName().toLowerCase()
				.compareTo(((ETNACluster) obj).getName().toLowerCase());
		return number;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getName().equals(((ETNACluster) obj).getName())
				&& this.getDescription().equals(
						((ETNACluster) obj).getDescription())) {
			return true;
		} else
			return false;
	}

	public boolean isItemProposed() {
		return itemProposed;
	}

	public void setItemProposed(boolean itemProposed) {
		this.itemProposed = itemProposed;
	}

	public List<EASTINProperty> getAttributesToShow() {
		return attributesToShow;
	}

	public void setAttributesToShow(List<EASTINProperty> attributesToShow) {
		this.attributesToShow = attributesToShow;
	}

	public List<EASTINProperty> getAttributesToHide() {
		return attributesToHide;
	}

	public void setAttributesToHide(List<EASTINProperty> attributesToHide) {
		this.attributesToHide = attributesToHide;
	}

	public List<EASTINProperty> getMeasuresToShow() {
		return measuresToShow;
	}

	public void setMeasuresToShow(List<EASTINProperty> measuresToShow) {
		this.measuresToShow = measuresToShow;
	}

	public List<EASTINProperty> getMeasuresToHide() {
		return measuresToHide;
	}

	public void setMeasuresToHide(List<EASTINProperty> measuresToHide) {
		this.measuresToHide = measuresToHide;
	}

	public List<EASTINProperty> getAttributesToShowInView() {
		return attributesToShowInView;
	}

	public void setAttributesToShowInView(
			List<EASTINProperty> attributesToShowInView) {
		this.attributesToShowInView = attributesToShowInView;
	}

	public List<EASTINProperty> getMeasuresToShowInView() {
		return measuresToShowInView;
	}

	public void setMeasuresToShowInView(List<EASTINProperty> measuresToShowInView) {
		this.measuresToShowInView = measuresToShowInView;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isShowItems() {
		return showItems;
	}

	public void setShowItems(boolean showItems) {
		this.showItems = showItems;
	}
	public boolean isHasToHideItems() {
		return hasToHideItems;
	}

	public void setHasToHideItems(boolean hasToHideItems) {
		this.hasToHideItems = hasToHideItems;
	}

	
}
