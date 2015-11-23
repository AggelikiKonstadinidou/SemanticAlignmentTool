package org.cloud4All.ontology;

import java.util.ArrayList;
import java.util.List;


public class MapTerm {
	
    private ArrayList<Record> docs = new ArrayList<Record>();
	
	public ArrayList<Record> getRecords() {
		return docs;
	}
	public void setRecords(ArrayList<Record> docs) {
		this.docs = docs;
	}


	public class Record{
		
		
		private String uniqueId = "";
		private String type = "";
		private String satId = "";
		private String hasId = "";
		private String aliasOf = "";
	
		public String getSatId() {
			return satId;
		}
		public void setSatId(String satId) {
			this.satId = satId;
		}
		public String getHasId() {
			return hasId;
		}
		public void setHasId(String hasId) {
			this.hasId = hasId;
		}
		public String getUniqueId() {
			return uniqueId;
		}
		public void setUniqueId(String uniqueId) {
			this.uniqueId = uniqueId;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getAliasOf() {
			return aliasOf;
		}
		public void setAliasOf(String aliasOf) {
			this.aliasOf = aliasOf;
		}
		

	}
	

}
