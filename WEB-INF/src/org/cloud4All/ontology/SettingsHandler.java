package org.cloud4All.ontology;

import java.util.ArrayList;

public class SettingsHandler {
	
	private String handlerName="";
	private ArrayList<Element> handlersOptions;
	private String realName = "";
	private String exportName = "";
	
	public String getHandlerName() {
		return handlerName;
	}
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
	public ArrayList<Element> getHandlersOptions() {
		return handlersOptions;
	}
	public void setHandlersOptions(ArrayList<Element> handlersOptions) {
		this.handlersOptions = handlersOptions;
	}
	public SettingsHandler() {
		super();
		this.handlerName = "";
		this.realName = "";
		this.exportName = "";
		this.handlersOptions = new ArrayList<Element>();
	}
	
	public SettingsHandler clone(){
		SettingsHandler setHand = new SettingsHandler();
		setHand.setExportName(this.exportName);
		setHand.setHandlerName(this.handlerName);
		setHand.setRealName(this.realName);
		setHand.setHandlersOptions(this.handlersOptions);
		return setHand;
		
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getExportName() {
		return exportName;
	}
	public void setExportName(String exportName) {
		this.exportName = exportName;
	}
	
}
