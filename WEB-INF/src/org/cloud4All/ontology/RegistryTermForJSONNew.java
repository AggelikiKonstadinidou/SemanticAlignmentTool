package org.cloud4All.ontology;
import java.util.ArrayList;
import java.util.List;

import org.cloud4All.Setting;

public class RegistryTermForJSONNew {
	
	private Record record = new Record();
	

	public Record getRecord() {
		return record;
	}
	public void setRecord(Record record) {
		this.record = record;
	}
	
	public class Record{
		
		private String solutionId = "";
		private String uniqueId = "";
		private String type = "";
		private String status = "";
		private String valueSpace = "";
		private String defaultValue = "";
		private String termLabel = "";
		private String definition = "";
		private String notes = "";
		private List<Alias> aliases = new ArrayList<Alias>();
		private Alias alias = new Alias();
	
		public Alias getAlias() {
			return alias;
		}
		public void setAlias(Alias alias) {
			this.alias = alias;
		}
		public String getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(String solutionId) {
			this.solutionId = solutionId;
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getValueSpace() {
			return valueSpace;
		}
		public void setValueSpace(String valueSpace) {
			this.valueSpace = valueSpace;
		}
		public String getTermLabel() {
			return termLabel;
		}
		public void setTermLabel(String termLabel) {
			this.termLabel = termLabel;
		}
		public String getDefinition() {
			return definition;
		}
		public void setDefinition(String definition) {
			this.definition = definition;
		}
		
		public List<Alias> getAliases() {
			return aliases;
		}
		public void setAliases(List<Alias> aliases) {
			this.aliases = aliases;
		}
		
		public String getNotes() {
			return notes;
		}
		public void setNotes(String notes) {
			this.notes = notes;
		}
		
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		//this method converts a RegistryTermForJSON in a RegistryTerm for ontology Model
		public RegistryTerm convertJSONTermToOntologyTerm() {
			RegistryTerm rterm = new RegistryTerm();

			String checkString = "";
			String status = "deleted";
			if (this.getStatus().equals("active"))
				status = "accepted";
			else if (this.getStatus().equals("unreviewed")
					|| this.getStatus().equals("candidate"))
				status = "proposed";
			// ignore this situation
			// else if(this.getStatus().equals("deleted"))
			// typeOfTerm = "";

			if (this.type.equalsIgnoreCase("ALIAS")){
				this.notes = "ALIAS";
			}

			if (this.getTermLabel() != null) {
				rterm.setName(this.getTermLabel());
				checkString = rterm.getName() + "#";
			}

			if (this.getDefinition() != null) {
				rterm.setDescription(this.getDefinition());
				checkString = checkString.concat(rterm.getDescription() + "#");
			}

			if (this.getNotes() != null) {
				rterm.setNotes(this.getNotes());
				checkString = checkString.concat(rterm.getNotes() + "#");
			}
			// rterm.setType(typeOfTerm);

			if (this.getUniqueId() != null) {
				rterm.setId(this.getUniqueId());
				checkString = checkString.concat(rterm.getId() + "#");
			}

			if (this.getValueSpace() != null) {
				rterm.setValueSpace(this.getValueSpace());
				checkString = checkString.concat(rterm.getValueSpace() + "#");
			}

			if (!status.isEmpty()) {
				rterm.setStatus(status);
				checkString = checkString.concat(rterm.getStatus() + "#");
			}
			
			if (this.getDefaultValue() != null)
				rterm.setDefaultValue(this.getDefaultValue());

			rterm.setCheck(checkString);

			String aliasString = "";
			ArrayList<RegistryTerm> aliasesArray = new ArrayList<RegistryTerm>();
			if (this.getAliases() != null)
				for (RegistryTermForJSONNew.Record.Alias alias : this
						.getAliases()) {
					RegistryTerm aliasTerm = new RegistryTerm();

					if (alias.getTermLabel() == null)
						alias.setTermLabel("");

					String name = "undefined name";

					if (!alias.getTermLabel().isEmpty())
						name = alias.getTermLabel();

					rterm.getAlias().add(
							name + "(ID = " + alias.getUniqueId() + ")");

					aliasTerm.setName(name);

					if (alias.getNotes() != null)
						aliasTerm.setNotes(this.getNotes());

					if (alias.getStatus() != null)
						aliasTerm.setStatus(alias.getStatus());

					if (alias.getUniqueId() != null)
						aliasTerm.setId(this.getUniqueId());

					aliasString = aliasString.concat(alias.getUniqueId() + "#");
					aliasesArray.add(aliasTerm);
				}
			// rterm.printMe();
			rterm.setAliasesTerms(aliasesArray);
			rterm.setAliasString(aliasString);

			return rterm;
		}

		@Override
		public String toString() {
			return "Record [uniqueId=" + uniqueId + ", type=" + type
					+ ", status=" + status + ", valueSpace=" + valueSpace
					+ ", termLabel=" + termLabel + ", definition=" + definition
					+ ", notes=" + notes + ", aliases=" + aliases + "]";
		}

		public class Alias{
			private String uniqueId = "";
			private String type = "";
			private String status = "";
			private String notes = "";
			private String aliasOf = "";
			private String termLabel = "";
			private String ulUri = "";
			
			
			public String getUlUri() {
				return ulUri;
			}
			public void setUlUri(String ulUri) {
				this.ulUri = ulUri;
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
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
			public String getNotes() {
				return notes;
			}
			public void setNotes(String notes) {
				this.notes = notes;
			}
			public String getAliasOf() {
				return aliasOf;
			}
			public void setAliasOf(String aliasOf) {
				this.aliasOf = aliasOf;
			}
			public String getTermLabel() {
				return termLabel;
			}
			public void setTermLabel(String termLabel) {
				this.termLabel = termLabel;
			}
			@Override
			public String toString() {
				return "Alias [uniqueId=" + uniqueId + ", type=" + type
						+ ", status=" + status + ", notes=" + notes
						+ ", aliasOf=" + aliasOf + ", termLabel=" + termLabel
						+ "]";
			}
			public Setting convertJSONAlias() {
				Setting sett = new Setting();
				String status = "deleted";
				if (this.getStatus().equals("active"))
					status = "accepted";
				else if (this.getStatus().equals("unreviewed")
						|| this.getStatus().equals("candidate"))
					status = "proposed";

				if (this.getTermLabel() != null)
					sett.setName(this.getTermLabel());

				if (this.getUniqueId() != null)
					sett.setId(this.getUniqueId());

				if (this.getAliasOf() != null)
					sett.setMappingId(this.getAliasOf());
				
				if(this.getUlUri()!=null)
					sett.setSolutionId(this.getUlUri());

				return sett;
			}
			
			
			
			
		}
		
		

	}

	@Override
	public String toString() {
		return "RegistryTermForJSON [record=" + record + "]";
	}
	
	public void printMe(){
		System.out.println(toString());
	}
	
	public class AllRecords{
	private List<RegistryTermForJSONNew.Record> records = new ArrayList<RegistryTermForJSONNew.Record>();

	public List<RegistryTermForJSONNew.Record> getRecords() {
		return records;
	}

	public void setRecords(List<RegistryTermForJSONNew.Record> records) {
		this.records = records;
	}
	
	}
	
	public class AllAliases{
		private List<RegistryTermForJSONNew.Record.Alias> records = new ArrayList<RegistryTermForJSONNew.Record.Alias>();

		public List<RegistryTermForJSONNew.Record.Alias> getRecords() {
			return records;
		}

		public void setRecords(List<RegistryTermForJSONNew.Record.Alias> records) {
			this.records = records;
		}
		
		}

	
}
