package org.cloud4All;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.jena.atlas.event.EventListener;
import org.cloud4All.IPR.SolutionAccessInfoForUsers;
import org.cloud4All.IPR.SolutionAccessInfoForVendors;
import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyInstance;
import org.cloud4All.ontology.RegistryTerm;
import org.cloud4All.ontology.WebServicesForSolutions;
import org.cloud4All.ontology.WebServicesForTerms;
import org.cloud4All.utils.StringComparison;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;

import com.hp.hpl.jena.ontology.OntClass;

@ManagedBean(name = "step3Bean")
@SessionScoped
public class step3Bean {

	private Setting newSetting = new Setting();
	private List<Setting> settings = new ArrayList<Setting>();
	private Setting selectedSetting = new Setting();
	private Setting clonedSelectedSetting = new Setting();
	private boolean formCompleted3 = false;
	private List<Solution> userSolutions = new ArrayList<Solution>();
	private Solution selectedSolution = new Solution();
	private List<Setting> relevantSettings = new ArrayList<Setting>();
	private Setting selectedRelevantSetting = new Setting();
	private boolean settingNameExists = false;
	private boolean showEditInDialog = true;
	private String solutionURI = "";
	private OntologyInstance ontologyInstance;
	private List<String> settingsTypes = new ArrayList<String>();
	private String oldSettingName = "";
	private boolean checkForRelevantSetting = false;
	private boolean flag = false;

	public step3Bean() {
		super();
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
		settingsTypes.add("string");
		settingsTypes.add("boolean");
		settingsTypes.add("integer");
		settingsTypes.add("double");

	}

	public void addNewSetting() {
		// check if setting name already exist
		for (int i = 0; i < settings.size(); i++) {
			if (settings.get(i).getName().trim()
					.equalsIgnoreCase(newSetting.getName().trim())) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								"messages",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"This setting name already exists in the application",
										""));
				return;
			}
		}

		if (newSetting.getName().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting name", ""));
		if (newSetting.getDescription().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting description", ""));
		if (newSetting.getValueSpace().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting value space", ""));
		if (newSetting.getValue().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting default value", ""));

		if (newSetting.getName().trim().equals("")
				|| newSetting.getValue().trim().equals("")
				|| newSetting.getDescription().trim().equals("")
				|| newSetting.getValueSpace().trim().equals("")) {

			return;
		}

		// check if default value matches with type
		if (this.newSetting.getType().equals("integer")) {
			try {
				Integer.parseInt(this.newSetting.getValue().trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"The default value \""
										+ this.newSetting.getValue().trim()
										+ "\" is not of type \""
										+ this.newSetting.getType() + "\"", ""));
				return;
			}
		} else if (this.newSetting.getType().equals("boolean")) {
			try {
				if (!this.newSetting.getValue().trim().toLowerCase()
						.equals("true")
						&& !this.newSetting.getValue().trim().toLowerCase()
								.equals("false")) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage("messages",
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"The default value \""
											+ this.newSetting.getValue().trim()
											+ "\" is not of type \""
											+ this.newSetting.getType() + "\"",
									""));
					return;
				}

			} catch (Exception ex) {

			}
		} else if (this.newSetting.getType().equals("double")) {
			try {
				Double.parseDouble(this.newSetting.getValue().trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"The default value \""
										+ this.newSetting.getValue().trim()
										+ "\" is not of type \""
										+ this.newSetting.getType() + "\"", ""));
				return;
			}
		}

		List<RegistryTerm> registryItemsList = ontologyInstance
				.getRegistryTerms();

		SortedMap map = new TreeMap();
		for (int j = 0; j < registryItemsList.size(); j++) {
			map.put(registryItemsList.get(j).getName(), StringComparison
					.CompareStrings(newSetting.getName(), registryItemsList
							.get(j).getName()));
		}
		registryItemsList = ontologyInstance.getProposedRegistryTerms();
		for (int j = 0; j < registryItemsList.size(); j++) {
			map.put(registryItemsList.get(j).getName(), StringComparison
					.CompareStrings(newSetting.getName(), registryItemsList
							.get(j).getName()));
		}
		SortedSet map2 = entriesSortedByValues(map);
		Iterator itr = map2.iterator();
		String str = "";
		List<String> list = new ArrayList<String>();
		while (itr.hasNext()) {
			str = itr.next().toString().split("=")[0];

			list.add(str);
		}

		if (!map2.isEmpty()
				&& Double.parseDouble(((String) map2.last().toString())
						.split("=")[1]) > 0.4) {
			newSetting.setHasMapping(true);
			newSetting.setMapping(str);
			newSetting.setExactMatching(true);
		} else {
			newSetting.setHasMapping(false);
			newSetting.setMapping("");
			newSetting.setExactMatching(false);
		}

		// get the description of the terms
		List<String> list2 = reverseList(list);
		List<Item> newList = new ArrayList<Item>();

		List<RegistryTerm> allTerms = new ArrayList<RegistryTerm>();
		allTerms.addAll(ontologyInstance.getRegistryTerms());
		allTerms.addAll(ontologyInstance.getProposedRegistryTerms());

		String description = "Description: empty";
		Item item = null;
		for (String temp : list2) {
			for (RegistryTerm term : allTerms) {
				if (temp.equals(term.getName())) {

					if (!term.getDescription().isEmpty())
						description = "Description: " + term.getDescription();

					item = new Item(temp, description);
					newList.add(item);
					break;
				}
			}
			description = "Description: empty";
		}
		// set the new list with mapping terms that contain
		// term name and term description
		newSetting.setSortedMappings(list2);
		newSetting.setSortedMappingsObjects(newList);

		newSetting.setComments("");
		if (newSetting
				.getConstraints()
				.equals("e.g. MagnifierPosition=fullScreen requires AERO Design Windows 7 to be ON")) {
			newSetting.setConstraints("");
		}
		settings.add(newSetting.cloneSetting());

		newSetting = new Setting();
		this.oldSettingName = "";

	}

	public void searchForRelevantSettings() {

		// an to kanw me to ajax event
		/**
		 * <f:ajax event="mouseout" execute="SettingName"
		 * listener="#{step3Bean.searchForRelevantSettings}"
		 * render="relevantSettingsPanel" />
		 **/

		if (this.newSetting.getName().trim().isEmpty()) {

			FacesContext
					.getCurrentInstance()
					.addMessage(
							"messages",
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Provide setting name to search for relevant settings",
									""));
			return;
		}

		// the first time that the setting name takes a value
		if (!this.newSetting.getName().trim().isEmpty()) {
			if (this.oldSettingName.isEmpty()) {
				this.oldSettingName = this.newSetting.getName();
				flag = true;
				relevantSettings = new ArrayList<Setting>();
			}
		}

		// if its not the first time of taking value
		if (!flag)
			if (this.newSetting.getName().trim().isEmpty()
					&& this.oldSettingName.trim().isEmpty()) {
				checkForRelevantSetting = false;
				relevantSettings = new ArrayList<Setting>();

			} else if (!this.newSetting.getName().trim().isEmpty()) {

				if (this.newSetting.getName().equalsIgnoreCase(
						this.oldSettingName))
					checkForRelevantSetting = false;
				else {
					checkForRelevantSetting = true;
					relevantSettings = new ArrayList<Setting>();
				}
			}

		if (checkForRelevantSetting || flag) {
			// check relevant settings
			for (Solution sol : ontologyInstance.getSolutions()) {

				for (Setting sett : sol.getSettings()) {
					double score = StringComparison.CompareStrings(
							sett.getName(), newSetting.getName());
					if (score > 0.5)
						relevantSettings.add(sett);

				}
			}

			RequestContext rc = RequestContext.getCurrentInstance();
			rc.update("relevantSettingsPanel");
			this.oldSettingName = this.newSetting.getName();
			flag = false;
			showEditInDialog = true;
		}

	}

	public void useExistingSetting() {
		newSetting = this.selectedRelevantSetting.cloneSetting();
		showEditInDialog = true;
		this.selectedRelevantSetting = new Setting();
		this.oldSettingName = "";

	}

	public void onRowSelect() {
		showEditInDialog = false;
	}

	private List reverseList(List myList) {
		List invertedList = new ArrayList();
		for (int i = myList.size() - 1; i >= 0; i--) {
			invertedList.add(myList.get(i));
		}
		return invertedList;
	}

	<K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {

					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						int res = e1.getValue().compareTo(e2.getValue());
						return res != 0 ? res : 1; // Special fix to preserve
													// items with equal values
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}

	public Setting getSelectedSetting() {
		if (selectedSetting == null)
			return new Setting();
		else
			return selectedSetting;
	}

	public void setSelectedSetting(Setting selectedSetting) {
		this.selectedSetting = selectedSetting;
		this.clonedSelectedSetting = this.selectedSetting.cloneSetting();
		formCompleted3 = false;
	}

	public void onKeyRelease2() {
		formCompleted3 = true;
		if (!this.selectedSetting.getName().equals("")
				&& !this.selectedSetting.getValue().equals("")
				&& !this.selectedSetting.getType().equals("")
				&& !this.selectedSetting.getDescription().equals("")
				&& !this.selectedSetting.getValueSpace().equals("")) {

		}

	}

	public void checkIfSelectedSettingHasEmptyValues() {

		// check if setting name already exist
		for (int i = 0; i < settings.size(); i++) {
			if (i != settings.indexOf(selectedSetting)
					&& settings
							.get(i)
							.getName()
							.trim()
							.equalsIgnoreCase(
									clonedSelectedSetting.getName().trim())) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								"messages2",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"This setting name already exists in the application",
										""));
				return;
			}
		}

		if (clonedSelectedSetting.getName().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages2",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting name", ""));
		if (clonedSelectedSetting.getDescription().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages2",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting description", ""));
		if (clonedSelectedSetting.getValueSpace().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages2",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting value space", ""));
		if (clonedSelectedSetting.getValue().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages2",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting default value", ""));

		if (clonedSelectedSetting.getName().equals("")
				|| clonedSelectedSetting.getValue().equals("")
				|| clonedSelectedSetting.getDescription().equals("")
				|| clonedSelectedSetting.getValueSpace().equals("")) {

			return;
		}
		// check if default value matches with type
		if (clonedSelectedSetting.getType().equals("integer")) {
			try {
				Integer.parseInt(clonedSelectedSetting.getValue().trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ clonedSelectedSetting.getValue().trim()
								+ "\" is not of type \""
								+ clonedSelectedSetting.getType() + "\"", ""));
				return;
			}
		} else if (clonedSelectedSetting.getType().equals("boolean")) {
			try {
				if (!clonedSelectedSetting.getValue().trim().toLowerCase()
						.equals("true")
						&& !clonedSelectedSetting.getValue().trim()
								.toLowerCase().equals("false")) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage("messages2", new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "The default value \""
									+ clonedSelectedSetting.getValue().trim()
									+ "\" is not of type \""
									+ clonedSelectedSetting.getType() + "\"",
							""));
					return;
				}

			} catch (Exception ex) {

			}
		} else if (clonedSelectedSetting.getType().equals("double")) {
			try {
				Double.parseDouble(clonedSelectedSetting.getValue().trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ clonedSelectedSetting.getValue().trim()
								+ "\" is not of type \""
								+ clonedSelectedSetting.getType() + "\"", ""));
				return;
			}
		}

		int i = settings.indexOf(selectedSetting);
		settings.remove(selectedSetting);
		settings.add(i, clonedSelectedSetting);
		// selectedSetting=clonedSelectedSetting;
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("deleteDialog.hide()");
		rc.update("form");

	}

	public void deleteSelectedSetting() {

		settings.remove(selectedSetting);
	}

	public void onRowSelect(SelectEvent event) {

	}

	public Setting getNewSetting() {
		return newSetting;
	}

	public void setNewSetting(Setting newSetting) {
		this.newSetting = newSetting;
	}

	public void loadSettings() {
		FacesContext context = FacesContext.getCurrentInstance();
		Step4Bean step4Bean = (Step4Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step4Bean}", Step4Bean.class);
		step4Bean.setSettings(settings);
	}

	public void openDialog(SelectEvent event) {
		formCompleted3 = false;
	}

	public boolean isFormCompleted3() {
		return formCompleted3;
	}

	public void setFormCompleted3(boolean formCompleted3) {
		this.formCompleted3 = formCompleted3;
	}

	public void saveAlignment() {
		try {
			System.out.println("SAVE");
			// get beans
			FacesContext context = FacesContext.getCurrentInstance();

			Step1Bean step1Bean = (Step1Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step1Bean}",
							Step1Bean.class);

			step2Bean step2Bean = (step2Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step2Bean}",
							step2Bean.class);

			Step2bBean step2bBean = (Step2bBean) context.getApplication()
					.evaluateExpressionGet(context, "#{step2bBean}",
							Step2bBean.class);

			Solution sol = new Solution();

			sol.setName(step2Bean.getApplicationName());
			sol.setStartCommand(step2Bean.getCommandForStart());
			sol.setStopCommand(step2Bean.getCommandForStop());
			sol.setHandlerType(step2Bean.getHandlerType());
			sol.setOptions(step2Bean.getOptions());
			sol.setCapabilities(step2Bean.getCapabilities());
			sol.setCapabilitiesTransformations(step2Bean
					.getCapabilitiesTransformations());
			sol.setCategories(step2Bean.getCategoriesList());

			if (step1Bean.getSelectedNode() != null) {
				// user has not proposed category
				sol.setOntologyCategory(step2Bean.getSelectedCategory());
			} else {
				// check if proposed category already exists
				// user has proposed a category
				// check if changed category in step2
				String localName = "";
				for (int i = 0; i < ontologyInstance
						.getProposedApplicationCategories().size(); i++) {
					if (ontologyInstance.getProposedApplicationCategories()
							.get(i).getName()
							.equals(step1Bean.getSelectedProposedCategory())) {

						localName = ontologyInstance
								.getProposedApplicationCategories().get(i)
								.getName().trim().replace(" ", "_");
						// sol.setOntologyCategory(localName);

						for (int k = 0; k < ontologyInstance
								.getProposedApplicationCategories().size(); k++) {
							if (ontologyInstance
									.getProposedApplicationCategories().get(k)
									.getName().equals(localName)) {
								ontologyInstance
										.getProposedApplicationCategories()
										.get(k)
										.setProposedForProductID(
												step2Bean.getId());

								break;
							}
						}
						break;
					}
				}

				if (step1Bean.getSelectedProposedCategory().equals(
						step2Bean.getSelectedCategory()))
					sol.setOntologyCategory(localName);
				else
					sol.setOntologyCategory(step2Bean.getSelectedCategory());

			}

			for (int i = 0; i < step2Bean.getProposedClusterItems().size(); i++) {
				EASTINProperty p = step2Bean.getProposedClusterItems().get(i);
				boolean found = false;
				for (int j = 0; j < ontologyInstance
						.getProposedEASTINItemsOriginal().size(); j++) {
					EASTINProperty p2 = ontologyInstance
							.getProposedEASTINItemsOriginal().get(j);
					if (p2.getName().equals(p.getName())
							&& p2.getType().equals(p.getType())
							&& p2.getBelongsToCluster().equals(
									p.getBelongsToCluster())) {
						found = true;
					}
				}
				if (!found)
					ontologyInstance.getProposedEASTINItemsOriginal().add(p);
			}
			for (int i = 0; i < step2Bean.getProposedMeasureClusterItems()
					.size(); i++) {
				EASTINProperty p = step2Bean.getProposedMeasureClusterItems()
						.get(i);
				boolean found = false;
				for (int j = 0; j < ontologyInstance
						.getProposedMeasureEASTINItemsOriginal().size(); j++) {
					EASTINProperty p2 = ontologyInstance
							.getProposedMeasureEASTINItemsOriginal().get(j);
					if (p2.getName().equals(p.getName())
							&& p2.getType().equals(p.getType())
							&& p2.getBelongsToCluster().equals(
									p.getBelongsToCluster())) {
						found = true;
					}
				}
				if (!found)
					ontologyInstance.getProposedMeasureEASTINItemsOriginal()
							.add(p);
			}

			sol.setDescription(step2Bean.getApplicationDescription());
			sol.setSettings(settings);
			sol.setId(step2Bean.getId());
			sol.setConstraints(step2Bean.getConstraints());

			// keep clusters for changes in view
			// define clusters to hide/show. Keep in view only clusters that
			// contain selected items
			// , proposed items or imported clusters
			List<ETNACluster> clustersToShow = new ArrayList<ETNACluster>();
			List<ETNACluster> clustersToHide = new ArrayList<ETNACluster>();
			boolean containsProposedItems = false;
			boolean isImportedCluster = false;
			boolean measureHasValue = false;
			for (ETNACluster cl : ontologyInstance.getClustersToShowInView()) {

				// check if cl is an imported cluster
				for (ETNACluster temp : ontologyInstance
						.getProposedEASTINProperties()) {
					if (temp.equals(cl)) {
						isImportedCluster = true;
						break;
					}
				}

				for (EASTINProperty prop : cl.getAttributesToShowInView()) {
					if (prop.isProposed()) {
						containsProposedItems = true;
						break;
					}

				}

				for (EASTINProperty prop : cl.getMeasuresToShowInView()) {
					if (prop.isProposed()) {
						containsProposedItems = true;
						break;
					}

					if (prop.getType().equalsIgnoreCase("measure")
							&& !prop.getValue().isEmpty()) {
						measureHasValue = true;
						break;
					}
				}

				// fill lists according to selected items, proposed items or
				// imported clusters
				if (containsProposedItems
						|| measureHasValue
						|| isImportedCluster
						|| (cl.isSingleSelection() && cl.getSelectedProperty() != null)
						|| (!cl.isSingleSelection() && cl
								.getSelectedProperties().size() > 0))
					clustersToShow.add(cl.clone());
				else
					clustersToHide.add(cl.clone());

				containsProposedItems = false;
				isImportedCluster = false;
				measureHasValue = false;

			}

			for (ETNACluster cl : clustersToShow) {
				if (ontologyInstance.getHiddenClusters().contains(cl))
					ontologyInstance.getHiddenClusters().remove(cl);
			}

			for (ETNACluster cl : ontologyInstance.getHiddenClusters()) {
				if (!clustersToHide.contains(cl))
					clustersToHide.add(cl);
			}
			sol.setClustersToHide(clustersToHide);
			sol.setClustersToShow(clustersToShow);
			sol.setClustersToShowInView(clustersToShow);
			// -----
			sol.setEtnaProperties(ontologyInstance.getEtnaCluster());
			sol.setManufacturerCountry(step2Bean.getManufacturerCountry());
			sol.setManufacturerName(step2Bean.getManufacturerName());
			sol.setManufacturerWebsite(step2Bean.getManufacturerWebsite());
			sol.setDate(step2Bean.getDate());
			sol.setUpdateDate(step2Bean.getUpdateDate());
			sol.setImageUrl(step2Bean.getImageUrl());
			sol.setDownloadPage(step2Bean.getDownloadPage());
			UserBean userBean = (UserBean) context.getApplication()
					.evaluateExpressionGet(context, "#{userBean}",
							UserBean.class);
			for (int i = 0; i < step2Bean.getProposedClusters().size(); i++) {
				for (int j = 0; j < step2Bean.getProposedClusters().get(i)
						.getAllproperties().size(); j++) {
					if (userBean.isAdmin()) {
						step2Bean.getProposedClusters().get(i)
								.getAllproperties().get(j)
								.setProponentEmail("test_admin@cloud4all.org");
						step2Bean.getProposedClusters().get(i)
								.getAllproperties().get(j)
								.setProponentName("Administrator");
					} else {
						step2Bean
								.getProposedClusters()
								.get(i)
								.getAllproperties()
								.get(j)
								.setProponentEmail(
										userBean.getVendorObj()
												.getContactDetails());
						step2Bean
								.getProposedClusters()
								.get(i)
								.getAllproperties()
								.get(j)
								.setProponentName(
										userBean.getVendorObj().getVendorName());
					}
				}
			}

			// update proposed for product
			// sol.getEtnaProperties palia
			for (ETNACluster c : sol.getClustersToShowInView()) {
				for (ETNACluster temp : ontologyInstance
						.getProposedEASTINProperties()) {

					if (c.getName().trim()
							.equalsIgnoreCase(temp.getName().trim())
							&& !sol.getName().equalsIgnoreCase(
									temp.getProposedForProduct())) {
						// update string with proposed Product names
						temp.setAllProposalsString(temp.getAllProposalsString()
								.concat("," + sol.getName()));

					}
				}

				// update proposed for porduct for properties
				for (EASTINProperty prop : c.getAttributesToShowInView()) {
					for (EASTINProperty tempProp : ontologyInstance
							.getProposedEASTINItems()) {
						if (prop.getName().equalsIgnoreCase(tempProp.getName())
								&& c.getName().equalsIgnoreCase(
										tempProp.getBelongsToCluster())
								&& !sol.getName().equalsIgnoreCase(
										tempProp.getRefersToSolutionName())) {
							tempProp.setEdit(true);
							tempProp.setAllSolutionsString(tempProp
									.getAllSolutionsString().concat(
											"," + sol.getName()));
						}
					}
				}

				for (EASTINProperty prop : c.getMeasuresToShowInView()) {
					for (EASTINProperty tempProp : ontologyInstance
							.getProposedMeasureEASTINItems()) {
						if (prop.getName().equalsIgnoreCase(tempProp.getName())
								&& c.getName().equalsIgnoreCase(
										tempProp.getBelongsToCluster())
								&& !sol.getName().equalsIgnoreCase(
										tempProp.getRefersToSolutionName())) {
							tempProp.setEdit(true);
							tempProp.setAllSolutionsString(tempProp
									.getAllSolutionsString().concat(
											"," + sol.getName()));
						}
					}
				}
			}

			sol.setProposedClusters(step2Bean.getProposedClusters());

			sol.setAccessInfoForVendors(step2bBean.getAccessInfoForVendors());
			sol.setProposedAttributeClusterItems(step2Bean
					.getProposedClusterItems());
			sol.setProposedMeasureClusterItems(step2Bean
					.getProposedMeasureClusterItems());

			if (userBean.getVendorObj() != null) {
				sol.setVendor(userBean.getVendorObj().getOntologyURI());
				sol.setVendorName(userBean.getVendorObj().getVendorName());
			} else {
				sol.setVendor("Admin");
				sol.setVendorName("Admin");
			}
			sol.setAccessInfoForUsers(step2bBean.getAccessInfoForUsers());

			// solutionURI = ontologyInstance.getOntology().getNS()
			// + ontologyInstance.getOntology().saveSolution(sol, null);
			// sol.setOntologyURI(solutionURI);
			// commented out code
			String uri = sol.getName().trim().replace(" ", "_")
					.replaceAll("[^\\p{L}\\p{Nd}]", "");

			// check if uri exists
			if (Character.isDigit(uri.charAt(0))) {
				uri = "app";
			}
			uri = ontologyInstance.getOntology().changeUri(uri);
			solutionURI = ontologyInstance.getOntology().getNS() + uri;
			sol.setOntologyURI(solutionURI);

			for (int i = 0; i < sol.getProposedClusters().size(); i++) {
				ETNACluster cl = sol.getProposedClusters().get(i);
				for (int j = 0; j < cl.getAllproperties().size(); j++) {
					cl.getAllproperties().get(j)
							.setRefersToSolution(solutionURI);
				}
			}

			// --------
			if (!ontologyInstance.getSolutions().contains(sol))
				ontologyInstance.getSolutions().add(sol);
			OntologyInstance.getSolutionsMap().put(sol.getOntologyURI(), sol);
			if (userBean != null && userBean.getVendorObj() != null)
				userBean.getVendorObj().getVendorOf().add(sol);

			// create list with users solutions to show in redirect, after save
			this.setUserSolutions(new ArrayList<Solution>());
			for (Solution solution : ontologyInstance.getSolutions()) {
				if (userBean.getVendorObj() != null) {
					if (solution.getVendor().equals(
							userBean.getVendorObj().getOntologyURI())
							&& solution.getVendorName().equals(
									userBean.getVendorObj().getVendorName())) {
						this.getUserSolutions().add(solution.clone());
					}
				} else {
					if (userBean.isAdmin())
						if ((solution.getVendor().equals("Admin") && solution
								.getVendorName().equals("Admin"))
								|| (solution.getVendor()
										.equals("Administrator") && solution
										.getVendorName()
										.equals("Administrator"))) {
							this.getUserSolutions().add(solution.clone());
						}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saved() throws IOException {

		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);
		Step1Bean step1Bean = (Step1Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step1Bean}", Step1Bean.class);

		step2Bean step2Bean = (step2Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step2Bean}", step2Bean.class);
		Step2bBean step2bBean = (Step2bBean) context.getApplication()
				.evaluateExpressionGet(context, "#{step2bBean}",
						Step2bBean.class);
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		// TODO load from object

		Solution sol = new Solution();

		sol.setName(step2Bean.getApplicationName());
		sol.setStartCommand(step2Bean.getCommandForStart());
		sol.setStopCommand(step2Bean.getCommandForStop());
		sol.setHandlerType(step2Bean.getHandlerType());
		sol.setOptions(step2Bean.getOptions());
		sol.setCapabilities(step2Bean.getCapabilities());
		sol.setCapabilitiesTransformations(step2Bean
				.getCapabilitiesTransformations());
		sol.setCategories(step2Bean.getCategoriesList());

		if (step1Bean.getSelectedNode() != null) {
			sol.setOntologyCategory(step2Bean.getSelectedCategory());
			editSolutionBean.setCategoryProposed(false);

		} else {
			// editSolutionBean.setCategoryProposed(true);
			// find proposed category
			OntClass cl = null;
			for (int i = 0; i < ontologyInstance
					.getProposedApplicationCategories().size(); i++) {
				if (ontologyInstance.getProposedApplicationCategories().get(i)
						.getName()
						.equals(step1Bean.getSelectedProposedCategory())) {

					cl = ontologyInstance.getOntology()
							.addProposedApplicationCategory(
									ontologyInstance
											.getProposedApplicationCategories()
											.get(i).getName(),
									ontologyInstance
											.getProposedApplicationCategories()
											.get(i).getDescription(),
									ontologyInstance
											.getProposedApplicationCategories()
											.get(i).getParent());
					// sol.setOntologyCategory(cl.getLocalName());

					ProposedApplicationCategory cat = new ProposedApplicationCategory();
					cat.setName(ontologyInstance
							.getProposedApplicationCategories().get(i)
							.getName());
					cat.setDescription(ontologyInstance
							.getProposedApplicationCategories().get(i)
							.getDescription());
					cat.setParent(ontologyInstance
							.getProposedApplicationCategories().get(i)
							.getParent());
					cat.setAll_solutions_string(sol.getName());
					cat.setProposedForProduct(sol.getName());
					if (userBean.isAdmin()) {
						cat.setProponentEmail("test_admin@cloud4all.org");
						cat.setProponentName("Administrator");
					} else {
						cat.setProponentEmail(userBean.getVendorObj()
								.getContactDetails());
						cat.setProponentName(userBean.getVendorObj()
								.getVendorName());
					}
					ontologyInstance.getProposedApplicationCategoriesOriginal()
							.add(cat);
					for (int j = 0; j < ontologyInstance
							.getProposedApplicationCategories().size(); j++) {
						if (ontologyInstance.getProposedApplicationCategories()
								.get(j).getName().equals(cat.getName())) {

							// update "all solutions string " for a category
							// that is proposed for
							// more than one solution
							if (ontologyInstance
									.getProposedApplicationCategories().get(j)
									.getAll_solutions_string() != null)
								ontologyInstance
										.getProposedApplicationCategories()
										.get(j)
										.setAll_solutions_string(
												ontologyInstance
														.getProposedApplicationCategories()
														.get(j)
														.getAll_solutions_string()
														.concat(","
																+ sol.getName()));
							else
								ontologyInstance
										.getProposedApplicationCategories()
										.get(j)
										.setAll_solutions_string(sol.getName());

							if (ontologyInstance
									.getProposedApplicationCategories().get(j)
									.getProposedForProduct().isEmpty())
								ontologyInstance
										.getProposedApplicationCategories()
										.get(j)
										.setProposedForProduct(sol.getName());
						}
					}

					break;
				}
			}

			if (step1Bean.getSelectedProposedCategory().equals(
					step2Bean.getSelectedCategory())) {
				sol.setOntologyCategory(cl.getLocalName());
				editSolutionBean.setCategoryProposed(true);
			}

			else
				sol.setOntologyCategory(step2Bean.getSelectedCategory());

		}

		sol.setDescription(step2Bean.getApplicationDescription());

		sol.setSettings(settings);
		sol.setId(step2Bean.getId());
		if (sol.getConstraints().equals("e.g. needs JRE 1.6.2 or later")) {
			sol.setConstraints("");
		} else
			sol.setConstraints(step2Bean.getConstraints());

		List<ETNACluster> list = new ArrayList<ETNACluster>();
		for (int i = 0; i < ontologyInstance.getEtnaCluster().size(); i++) {
			list.add(ontologyInstance.getEtnaCluster().get(i).clone());
		}
		sol.setEtnaProperties(list);

		// new view
		// keep clusters for changes in view
		// define clusters to hide/show. Keep in view only clusters that
		// contain selected items
		// , proposed items or imported clusters
		List<ETNACluster> clustersToShow = new ArrayList<ETNACluster>();
		List<ETNACluster> clustersToHide = new ArrayList<ETNACluster>();
		boolean containsProposedItems = false;
		boolean isImportedCluster = false;
		boolean measureHasValue = false;
		for (ETNACluster cl : ontologyInstance.getClustersToShowInView()) {

			// check if cl is an imported cluster
			for (ETNACluster temp : ontologyInstance
					.getProposedEASTINProperties()) {
				if (temp.equals(cl)) {
					isImportedCluster = true;
					break;
				}
			}

			for (EASTINProperty prop : cl.getAttributesToShowInView()) {
				if (prop.isProposed()) {
					containsProposedItems = true;
					break;
				}

			}

			for (EASTINProperty prop : cl.getMeasuresToShowInView()) {
				if (prop.isProposed()) {
					containsProposedItems = true;
					break;
				}

				if (prop.getType().equalsIgnoreCase("measure")
						&& !prop.getValue().isEmpty()) {
					measureHasValue = true;
					break;
				}
			}

			cl.setFlag(false);
			// fill lists according to selected items, proposed items or
			// imported clusters
			if (containsProposedItems
					|| measureHasValue
					|| isImportedCluster
					|| (cl.isSingleSelection() && cl.getSelectedProperty() != null)
					|| (!cl.isSingleSelection() && cl.getSelectedProperties()
							.size() > 0))
				clustersToShow.add(cl.clone());
			else
				clustersToHide.add(cl.clone());

			containsProposedItems = false;
			isImportedCluster = false;
			measureHasValue = false;

		}

		for (ETNACluster cl : clustersToShow) {
			if (ontologyInstance.getHiddenClusters().contains(cl))
				ontologyInstance.getHiddenClusters().remove(cl);
		}

		for (ETNACluster cl : ontologyInstance.getHiddenClusters()) {
			if (!clustersToHide.contains(cl))
				clustersToHide.add(cl);
		}
		sol.setClustersToHide(clustersToHide);
		sol.setClustersToShow(clustersToShow);
		sol.setClustersToShowInView(clustersToShow);
		// -----

		sol.setManufacturerCountry(step2Bean.getManufacturerCountry());
		sol.setManufacturerName(step2Bean.getManufacturerName());
		sol.setManufacturerWebsite(step2Bean.getManufacturerWebsite());
		sol.setDate(step2Bean.getDate());
		sol.setUpdateDate(step2Bean.getUpdateDate());
		sol.setImageUrl(step2Bean.getImageUrl());
		sol.setDownloadPage(step2Bean.getDownloadPage());
		sol.setOntologyURI(solutionURI);
		sol.setAccessInfoForUsers(step2bBean.getAccessInfoForUsers());
		sol.setAccessInfoForVendors(step2bBean.getAccessInfoForVendors());
		for (int i = 0; i < step2Bean.getProposedClusters().size(); i++) {
			step2Bean.getProposedClusters().get(i)
					.setProposedForProduct(sol.getName());
			step2Bean.getProposedClusters().get(i)
					.setAllProposalsString(sol.getName());
			step2Bean.getProposedClusters().get(i)
					.setProposedForProductID(sol.getId());
			if (userBean.isAdmin()) {
				step2Bean.getProposedClusters().get(i)
						.setProponentEmail("test_admin@cloud4all.org");
				step2Bean.getProposedClusters().get(i)
						.setProponentName("Administrator");
			} else {
				step2Bean
						.getProposedClusters()
						.get(i)
						.setProponentEmail(
								userBean.getVendorObj().getContactDetails());
				step2Bean
						.getProposedClusters()
						.get(i)
						.setProponentName(
								userBean.getVendorObj().getVendorName());
			}
		}
		sol.setProposedClusters(step2Bean.getProposedClusters());
		ontologyInstance.getProposedEASTINPropertiesOriginal().addAll(
				step2Bean.getProposedClusters());

		for (int i = 0; i < sol.getProposedClusters().size(); i++) {
			ETNACluster cl = sol.getProposedClusters().get(i);
			for (int j = 0; j < ontologyInstance.getSolutions().size(); j++) {
				if (ontologyInstance
						.getSolutions()
						.get(j)
						.getOntologyURI()
						.equals(cl.getAllproperties().get(0)
								.getRefersToSolution())) {
					for (int k = 0; k < cl.getAllproperties().size(); k++) {
						cl.getAllproperties()
								.get(k)
								.setRefersToSolutionID(
										ontologyInstance.getSolutions().get(j)
												.getId());
						cl.getAllproperties()
								.get(k)
								.setRefersToSolutionName(
										ontologyInstance.getSolutions().get(j)
												.getName());
						if (userBean.getVendorObj() != null) {
							cl.getAllproperties()
									.get(k)
									.setProponentEmail(
											userBean.getVendorObj()
													.getContactDetails());
							cl.getAllproperties()
									.get(k)
									.setProponentName(
											userBean.getVendorObj()
													.getVendorName());
						}

					}
				}
			}

		}
		System.out.println(solutionURI);
		ontologyInstance.getOntology().getUris().add(solutionURI.toLowerCase());

		ontologyInstance.getClustersToShowInView().get(0)
				.setItemProposalName("");
		ontologyInstance.getClustersToShowInView().get(0)
				.setItemProposalDescription("");
		ontologyInstance.getClustersToShowInView().get(0)
				.setItemProposalType("Attribute");
		ontologyInstance.getClustersToShowInView().get(0)
				.setSelectedProposedItem(null);
		// ---

		step1Bean.setSelectedNode(null);
		step2Bean.setApplicationDescription("");
		step2Bean.setApplicationName("");
		step2Bean.setCommandForStart("");
		step2Bean.setCommandForStop("");
		step2Bean.setHandlerType("");

		settings = new ArrayList<Setting>();
		step1Bean.getRoot().setSelected(false);
		step1Bean.getRoot().setExpanded(false);
		step2Bean.setApplicationDescription("");
		step2Bean.setApplicationName("");
		step2Bean.setId("");
		step2Bean.setManufacturerCountry("");
		step2Bean.setManufacturerName("");
		step2Bean.setManufacturerWebsite("");
		step2Bean.setImageUrl("");
		step2Bean.setDownloadPage("");
		step2Bean.setDate(null);
		step2Bean.setUpdateDate(null);

		if (userBean.getVendorObj() != null) {
			sol.setVendor(userBean.getVendorObj().getOntologyURI());
			sol.setVendorName(userBean.getVendorObj().getVendorName());
		}
		settings = new ArrayList<Setting>();
		clearSelections(step1Bean.getRoot());
		step2bBean.setAccessInfoForUsers(new SolutionAccessInfoForUsers());
		step2bBean.setAccessInfoForVendors(new SolutionAccessInfoForVendors());
		editSolutionBean.setActiveTabIndex("-1");

		// set showItems flag for clusters
		List<List<ETNACluster>> superList = new ArrayList<List<ETNACluster>>();
		List<List<ETNACluster>> tempSuperList = new ArrayList<List<ETNACluster>>();

		// add lists of clusters to superList
		superList.add(sol.getClustersToShowInView());
		superList.add(sol.getClustersToShow());
		superList.add(sol.getClustersToHide());

		tempSuperList = Utilities.setShowItemsFlagsForClusters(superList);

		sol.setClustersToShowInView(tempSuperList.get(0));
		sol.setClustersToShow(tempSuperList.get(1));
		sol.setClustersToHide(tempSuperList.get(2));

		viewSolutionsBean.setClustersShowFlag(false);
		if (sol.getClustersToHide().size() > 0)
			viewSolutionsBean.setClustersShowFlag(true);

		viewSolutionsBean.setSelectedSolution(sol);
		viewSolutionsBean.setClonedSelectedSolution(sol.cloneSolution());
		editSolutionBean.setRelevantSettings(new ArrayList<Setting>());
		editSolutionBean.setSelectedRelevantSetting(new Setting());
		editSolutionBean.setOldSettingName("");

		String cookiesUL = WebServicesForTerms
				.loginAndGetCookiesWithCurl(WebServicesForSolutions.signInForUnifiedListingApi);
		// TODO add solution to unified listing
		WebServicesForSolutions.solutionHTTPPostRequest(sol, cookiesUL);
//		String cookies = WebServicesForTerms.loginAndGetCookiesWithCurl();
//
//		for (Setting sett : sol.getSettings()) {
//			sett.setSolutionId(sol.getId());
//
//			sett.setMappingId("dUMMY");
//
//			if (!sett.getId().isEmpty()) {
//
//				WebServicesForTerms
//						.postSettingWithCurl(
//								cookies,
//								sett,
//								"http://ul.gpii.net/api/product/unified/"
//										+ sol.getId(), sol.getId());
//
//			}
//
//		}

	}

	public void editUsersSelectedSolution() throws IOException {
		saved();
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);

		editSolutionBean.setActiveTabIndex("-1");
		viewSolutionsBean.getActiveETNACluster().setItemProposalDescription("");
		viewSolutionsBean.getActiveETNACluster().setItemProposalName("");
		viewSolutionsBean.getActiveETNACluster().setItemProposalType(
				"Attribute");
		viewSolutionsBean.setActiveETNACluster(ontologyInstance
				.getEtnaCluster().get(0));

		Solution sol = selectedSolution.clone();
		String category = sol.getOntologyCategory();

		// check if solution's category is proposed or accepted
		boolean flag = false;
		for (ProposedApplicationCategory prop : ontologyInstance
				.getProposedApplicationCategories()) {
			if (prop.getName().equalsIgnoreCase(category)) {
				flag = true;
				break;
			}
		}
		editSolutionBean.setCategoryProposed(flag);

		viewSolutionsBean.setSelectedSolution(sol);
		viewSolutionsBean.setClonedSelectedSolution(sol);
		viewSolutionsBean.setActiveETNACluster(viewSolutionsBean
				.getSelectedSolution().getEtnaProperties().get(0));

	}

	private void clearSelections(TreeNode root) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			node.setSelected(false);
			node.setExpanded(false);
			clearSelections(node);
		}
	}

	/**
	 * @return the clonedSelectedSetting
	 */
	public Setting getClonedSelectedSetting() {
		return clonedSelectedSetting;
	}

	/**
	 * @param clonedSelectedSetting
	 *            the clonedSelectedSetting to set
	 */
	public void setClonedSelectedSetting(Setting clonedSelectedSetting) {
		this.clonedSelectedSetting = clonedSelectedSetting;
	}

	public List<String> getSettingsTypes() {
		return settingsTypes;
	}

	public void setSettingsTypes(List<String> settingsTypes) {
		this.settingsTypes = settingsTypes;
	}

	public List<Solution> getUserSolutions() {
		return userSolutions;
	}

	public void setUserSolutions(List<Solution> userSolutions) {
		this.userSolutions = userSolutions;
	}

	public Solution getSelectedSolution() {
		return selectedSolution;
	}

	public void setSelectedSolution(Solution selectedSolution) {
		this.selectedSolution = selectedSolution;
		try {
			editUsersSelectedSolution();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Setting> getRelevantSettings() {
		return relevantSettings;
	}

	public void setRelevantSettings(List<Setting> relevantSettings) {
		this.relevantSettings = relevantSettings;
	}

	public Setting getSelectedRelevantSetting() {
		return selectedRelevantSetting;
	}

	public void setSelectedRelevantSetting(Setting selectedRelevantSetting) {
		this.selectedRelevantSetting = selectedRelevantSetting;
	}

	public boolean isSettingNameExists() {
		return settingNameExists;
	}

	public void setSettingNameExists(boolean settingNameExists) {
		this.settingNameExists = settingNameExists;
	}

	public boolean isShowEditInDialog() {
		return showEditInDialog;
	}

	public void setShowEditInDialog(boolean showEditInDialog) {
		this.showEditInDialog = showEditInDialog;
	}

	public String getOldSettingName() {
		return oldSettingName;
	}

	public void setOldSettingName(String oldSettingName) {
		this.oldSettingName = oldSettingName;
	}

	public boolean isCheckForRelevantSetting() {
		return checkForRelevantSetting;
	}

	public void setCheckForRelevantSetting(boolean checkForRelevantSetting) {
		this.checkForRelevantSetting = checkForRelevantSetting;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
