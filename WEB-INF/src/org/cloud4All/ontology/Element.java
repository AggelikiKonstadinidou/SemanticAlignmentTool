package org.cloud4All.ontology;

public class Element {
	private String optionName;
	private String optionValue;
	private boolean booleanValue;

	public Element(String optionName, String optionValue) {
		super();
		this.optionName = optionName;
		this.optionValue = optionValue;
	}
	

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}
	
	public Element clone() {
		Element element = new Element(this.optionName, this.optionValue);
		return element;

	}
	
	public void printMe(){
		System.out.println(toString());
	}

	@Override
	public String toString() {
		return "Element [optionName=" + optionName + ", optionValue="
				+ optionValue + "]";
	}


	public boolean isBooleanValue() {
		if (this.getOptionValue().equals("true"))
			return true;
		else
			return false;
	}


	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
		if(this.optionName.equals("AllowNumberSignComments")||
				this.optionName.equals("AllowSubSections"))
		if (this.booleanValue)
			this.optionValue = "true";
		else
			this.optionValue = "false";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getOptionName().equals(((Element) obj).getOptionName())
				&& this.getOptionValue().equals(
						((Element) obj).getOptionValue())) {
			return true;
		} else
			return false;
	}
	

}
