/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cloud4All.ontology;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author kgiannou
 */
public class OntologyClass {

	private String className;
	private List<OntologyClass> children;
	private List<String> eastinItems = new ArrayList<String>();

	public List<OntologyClass> getChildren() {
		return children;
	}

	public void setChildren(List<OntologyClass> children) {
		this.children = children;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getEastinItems() {
		return eastinItems;
	}

	public void setEastinItems(List<String> eastinItems) {
		this.eastinItems = eastinItems;
	}
	
	

}
