package org.cloud4All;

import java.util.ArrayList;
import java.util.List;

import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Element;

import com.google.gson.JsonObject;

public class Setting {

	private String name = "";
	private String value = "";
	private String mapping = "";
	private String mappingId = "";
	private List<String> sortedMappings = new ArrayList<String>();
	private List<Item> sortedMappingsObjects = new ArrayList<Item>();
	private String description = "";
	private String valueSpace = "";
	private String comments = "";
	private String type = "";
	private boolean exactMatching = true;
	private boolean hasMapping = true;
	private String constraints = "";
	private String id = "";
	private String parentInstanceURI = "";
	private String currentURI = "";
	private boolean appliedLive = false;
	private String solutionId = "";

	public Setting() {
		name = "";
		value = "";
		mapping = "";
		mappingId = "";
		sortedMappings = new ArrayList<String>();
		sortedMappingsObjects = new ArrayList<Item>();
		description = "";
		valueSpace = "";
		type = "string";
		comments = "";
		exactMatching = true;
		hasMapping = true;
		constraints = "";
		id = "";
		parentInstanceURI = "";
		currentURI = "";
		appliedLive = false;
		solutionId = "";
	}

	public String getMapping() {

		return mapping;
	}

	public void setMapping(String mapping) {

		if (mapping != null) {

			this.mapping = mapping;

		}
		// sortedMappings.remove(mapping);
		// sortedMappings.add(0, mapping);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Setting cloneSetting() {
		Setting set = new Setting();
		set.setMapping(this.getMapping());
		set.setMappingId(this.mappingId);
		set.setName(this.getName());
		set.setValue(this.getValue());
		set.setComments(this.getComments());
		set.setDescription(this.getDescription());
		set.setValueSpace(this.getValueSpace());
		set.setExactMatching(this.isExactMatching());
		set.setHasMapping(this.ishasMapping());
		set.setConstraints(this.constraints);
		set.setId(this.getId());
		set.setType(this.getType());
		set.setParentInstanceURI(this.getParentInstanceURI());
		set.setCurrentURI(this.getCurrentURI());
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < this.getSortedMappings().size(); i++) {
			list.add(this.getSortedMappings().get(i));
		}
		set.setSortedMappings(list);
		
		List<Item> objectsList = new ArrayList<Item>();
		for (int i = 0; i < this.getSortedMappingsObjects().size(); i++) {
			objectsList.add(this.getSortedMappingsObjects().get(i));
		}
		set.setSortedMappingsObjects(objectsList);
		
		set.setAppliedLive(this.appliedLive);
		return set;
	}

	public List<String> getSortedMappings() {
		return sortedMappings;
	}

	public void setSortedMappings(List<String> sortedMappings) {
		this.sortedMappings = sortedMappings;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isExactMatching() {
		return exactMatching;
	}

	public void setExactMatching(boolean exactMatching) {
		this.exactMatching = exactMatching;
	}

	public boolean ishasMapping() {
		return hasMapping;
	}

	public void setHasMapping(boolean hasMapping) {
		this.hasMapping = hasMapping;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public boolean isHasMapping() {
		return hasMapping;
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
	 * @return the parentInstanceURI
	 */
	public String getParentInstanceURI() {
		return parentInstanceURI;
	}

	/**
	 * @param parentInstanceURI
	 *            the parentInstanceURI to set
	 */
	public void setParentInstanceURI(String parentInstanceURI) {
		this.parentInstanceURI = parentInstanceURI;
	}

	/**
	 * @return the currentURI
	 */
	public String getCurrentURI() {
		return currentURI;
	}

	/**
	 * @param currentURI
	 *            the currentURI to set
	 */
	public void setCurrentURI(String currentURI) {
		this.currentURI = currentURI;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public boolean isAppliedLive() {
		return appliedLive;
	}

	public void setAppliedLive(boolean appliedLive) {
		this.appliedLive = appliedLive;
	}
	

	public List<Item> getSortedMappingsObjects() {
		return sortedMappingsObjects;
	}

	public void setSortedMappingsObjects(List<Item> sortedMappingsObjects) {
		this.sortedMappingsObjects = sortedMappingsObjects;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			Setting set = (Setting) obj;
			if (this.getComments().equals(set.getComments())
					&& this.getConstraints().equals(set.getConstraints())
					&& this.getCurrentURI().equals(set.getCurrentURI())
					&& this.getDescription().equals(set.getDescription())
					&& this.getId().equals(set.getId())
					&& this.getMapping().equals(set.getMapping())
					&& this.getName().equals(set.getName())
					&& this.getParentInstanceURI().equals(
							set.getParentInstanceURI())
					&& this.getType().equals(set.getType())
					&& this.getValue().equals(set.getValue())
					&& this.getValueSpace().equals(set.getValueSpace())) {
				return true;
			} else
				return false;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public JsonObject getJsonSchema(){
		JsonObject setting = new JsonObject();
		
		if(!this.getId().isEmpty())
			setting.addProperty("setting_id", this.getId().replace("\"", "'"));

		if(!this.getName().isEmpty())
		setting.addProperty("setting_name", this.getName().replace("\"", "'"));
		
		if(!this.getDescription().isEmpty())
		setting.addProperty("setting_description", this.getDescription().replace("\"", "'"));
		
		if(!this.getValueSpace().isEmpty())
		setting.addProperty("setting_value_space", this.getValueSpace().replace("\"", "'"));
		
		if(!this.getType().isEmpty())
		setting.addProperty("setting_type", this.getType().replace("\"", "'"));
		
		if(!this.getValue().isEmpty())
		setting.addProperty("setting_default_value", this.getValue().replace("\"", "'"));
		
		setting.addProperty("setting_is_mapped", this.isHasMapping());
		
		if(!this.getMapping().isEmpty())
		setting.addProperty("mapped_common_term", this.getMapping().replace("\"", "'"));
		
		setting.addProperty("exact_matching", this.isExactMatching());
		
		if(!this.getComments().isEmpty())
		setting.addProperty("mapping_comments", this.getComments().replace("\"", "'"));
		
		if(!this.getConstraints().isEmpty())
		setting.addProperty("setting_constraints_limitations",
				this.getConstraints().replace("\"", "'"));
		setting.addProperty("setting_can_be_applied_live", this.isAppliedLive());

		return setting;
	}
	
	public boolean testEquals(Object obj) {

		if (!this.getName().equals(((Setting) obj).getName())
				|| !this.getSolutionId()
						.equals(((Setting) obj).getSolutionId())
				|| !this.getMappingId().equals(((Setting) obj).getMappingId())) {
			return false;
		}

		return true;

	}

}
