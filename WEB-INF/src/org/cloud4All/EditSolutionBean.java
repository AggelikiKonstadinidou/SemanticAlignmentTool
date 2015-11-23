package org.cloud4All;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.lang.WordUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.cloud4All.EASTINApplication.Features;
import org.cloud4All.IPR.DiscountSchema;
import org.cloud4All.IPR.EULA;
import org.cloud4All.IPR.SLA;
import org.cloud4All.IPR.SolutionAccessInfoForUsers;
import org.cloud4All.IPR.SolutionAccessInfoForVendors;
import org.cloud4All.IPR.SolutionUserSchema;
import org.cloud4All.IPR.Vendor;
import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyClass;
import org.cloud4All.ontology.OntologyInstance;
import org.cloud4All.ontology.RegistryTerm;
import org.cloud4All.ontology.WebServicesForSolutions;
import org.cloud4All.ontology.WebServicesForTerms;
import org.cloud4All.utils.StringComparison;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.SlideEndEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.model.User;

@ManagedBean(name = "editSolutionBean")
@SessionScoped
public class EditSolutionBean {

	private Setting newSetting = new Setting();
	private boolean showInnerPanel = false;
	private CommandButton button;
	private boolean showMappingPanel = false;
	private boolean showNewSettingPanel = false;
	private String selectedMapping = "";
	private List<String> commercialCostCurrency = new ArrayList<String>();
	private List<String> costPaymentChargeType = new ArrayList<String>();
	private List<Solution> solutionsListAsStrings = new ArrayList<Solution>();
	private double totalDiscount = 0;
	private double costAfterDiscount = 0;
	private SLA sla = new SLA();
	private EULA eula = new EULA();
	private OntologyInstance ontologyInstance;
	private List<String> settingsTypes = new ArrayList<String>();
	private List<EASTINApplication> eastinApplications = new ArrayList<EASTINApplication>();
	private EASTINApplication selectedEastinApplication = new EASTINApplication();
	private String dialogEmptyMessage = "";
	private TreeNode selectedNode = null;
	private DefaultTreeNode root;
	private String selectedCategory = "";
	private boolean categoryProposed = false;
	private String activeTabIndex="-1";
	private List<Solution> userSolutions = new ArrayList<Solution>();
	private Solution selectedSolution = new Solution();
	private String oldSettingName = "";
	private boolean flag = false;
	private ArrayList<Setting> relevantSettings = new ArrayList<Setting>();
	private boolean checkForRelevantSetting = false;
	private Setting selectedRelevantSetting = new Setting();
	private boolean showEditInDialog = false;
	private boolean settingNameExists = false;

	public EditSolutionBean() {
		super();
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);
			
			for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
				solutionsListAsStrings.add(ontologyInstance.getSolutions().get(
						i));
			}

			solutionsListAsStrings.add(new Solution());
			commercialCostCurrency = ontologyInstance.getOntology()
					.getAllowedValuesOfDatatypeProperty(
							"hasCommercialCostCurrency");
			costPaymentChargeType = ontologyInstance.getOntology()
					.getAllowedValuesOfDatatypeProperty(
							"hasCostPaymentChargeType");

			newSetting.setSortedMappings(ontologyInstance
					.getRegistryTermsAsStrings());
			settingsTypes = new ArrayList<String>();
			settingsTypes.add("string");
			settingsTypes.add("boolean");
			settingsTypes.add("integer");
			settingsTypes.add("double");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	//AjaxBehaviorEvent e
	public void searchForRelevantSettings(ActionEvent event) {
		
		System.out.println("searching");
		
		if (this.newSetting.getName().trim().isEmpty()) {

			FacesContext
					.getCurrentInstance()
					.addMessage(
							"gridOfSettingMessage",
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

			this.oldSettingName = this.newSetting.getName();
			flag = false;
			showEditInDialog = true;
		}

	}

	public void onRowSelect() {
		showEditInDialog = false;
	}

	public void useExistingSetting() {
		newSetting = this.selectedRelevantSetting.cloneSetting();
		showEditInDialog = true;
		this.oldSettingName = "";
	}

	public void test(ActionEvent event) {

		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);

		viewSolutionsBean.setSelectedNode(null);
		viewSolutionsBean.getRoot().setExpanded(false);
		viewSolutionsBean.getRoot().setSelected(false);
		clearSelections(viewSolutionsBean.getRoot());
		viewSolutionsBean.setSelectedSolution(null);
		viewSolutionsBean.setButDisabled(true);
		viewSolutionsBean.setSolutions(new ArrayList<Solution>());
		viewSolutionsBean.setSearchApplicationName("");
		viewSolutionsBean.setSearchbyApplicationName("");
		viewSolutionsBean.setShowSolutionsPanel(false);

	}

	public Setting getNewSetting() {
		return newSetting;
	}

	public void setNewSetting(Setting newSetting) {
		this.newSetting = newSetting;
	}

	public void alignNewSetting() {

		SortedMap map = new TreeMap();
		for (int j = 0; j < ontologyInstance.getRegistryTermsAsStrings().size(); j++) {
			map.put(ontologyInstance.getRegistryTermsAsStrings().get(j),
					StringComparison.CompareStrings(newSetting.getName(),
							ontologyInstance.getRegistryTermsAsStrings().get(j)));
		}
		String[] arr = new String[map.size()];
		SortedSet map2 = entriesSortedByValues(map);
		// obtain an Iterator for Collection
		Iterator itr = map2.iterator();
		// iterate through TreeMap values iterator
		int j = map.size() - 1;
		List<String> sortedMappings = new ArrayList<String>();
		while (itr.hasNext()) {
			arr[j] = itr.next().toString().split("=")[0];

			j--;
		}
		for (int i = 0; i < arr.length; i++) {
			sortedMappings.add(arr[i]);
		}

		newSetting.setSortedMappings(sortedMappings);
		newSetting.setMapping(sortedMappings.get(0));
		showInnerPanel = true;

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

	public void addProposedTerm() {
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);

		// check if values are empty
		if (viewSolutionsBean.getProposedRegistryTerm().getName().trim()
				.equals("")) {
			context.addMessage("messages2", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term name", ""));
		}
		if (viewSolutionsBean.getProposedRegistryTerm().getId().trim()
				.equals("")) {
			context.addMessage("messages2", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term id", ""));
		}
		if (viewSolutionsBean.getProposedRegistryTerm().getDescription().trim()
				.equals("")) {
			context.addMessage("messages2", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term description", ""));
		}
		if (viewSolutionsBean.getProposedRegistryTerm().getValueSpace().trim()
				.equals("")) {
			context.addMessage("messages2", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term value space", ""));
		}
		if (viewSolutionsBean.getProposedRegistryTerm().getDefaultValue()
				.trim().equals("")) {
			context.addMessage("messages2", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term default value", ""));
		}
		if (viewSolutionsBean.getProposedRegistryTerm().getName().trim()
				.equals("")
				|| viewSolutionsBean.getProposedRegistryTerm().getDescription()
						.trim().equals("")
				|| viewSolutionsBean.getProposedRegistryTerm().getValueSpace()
						.trim().equals("")
				|| viewSolutionsBean.getProposedRegistryTerm()
						.getDefaultValue().trim().equals("")) {
			return;
		}

		// check if default value matches with type
		if (viewSolutionsBean.getProposedRegistryTerm().getType()
				.equals("integer")) {
			try {
				Integer.parseInt(viewSolutionsBean.getProposedRegistryTerm()
						.getDefaultValue().trim());
			} catch (Exception ex) {

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ viewSolutionsBean.getProposedRegistryTerm()
										.getDefaultValue().trim()
								+ "\" is not of type \""
								+ viewSolutionsBean.getProposedRegistryTerm()
										.getType() + "\"", ""));
				return;
			}
		} else if (viewSolutionsBean.getProposedRegistryTerm().getType()
				.equals("boolean")) {
			try {
				if (!viewSolutionsBean.getProposedRegistryTerm()
						.getDefaultValue().trim().toLowerCase().equals("true")
						&& !viewSolutionsBean.getProposedRegistryTerm()
								.getDefaultValue().trim().toLowerCase()
								.equals("false")) {

					context.addMessage("messages2", new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "The default value \""
									+ viewSolutionsBean
											.getProposedRegistryTerm()
											.getDefaultValue().trim()
									+ "\" is not of type \""
									+ viewSolutionsBean
											.getProposedRegistryTerm()
											.getType() + "\"", ""));
					return;
				}

			} catch (Exception ex) {

			}
		} else if (viewSolutionsBean.getProposedRegistryTerm().getType()
				.equals("double")) {
			try {
				Double.parseDouble(viewSolutionsBean.getProposedRegistryTerm()
						.getDefaultValue().trim());
			} catch (Exception ex) {

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ viewSolutionsBean.getProposedRegistryTerm()
										.getDefaultValue().trim()
								+ "\" is not of type \""
								+ viewSolutionsBean.getProposedRegistryTerm()
										.getType() + "\"", ""));
				return;
			}
		}

		// get all registry terms
		List<RegistryTerm> list = ontologyInstance.getRegistryTerms();
		List<RegistryTerm> list2 = ontologyInstance.getProposedRegistryTerms();
		
		for (int i = 0; i < list.size(); i++) {
			if (list
					.get(i)
					.getId()
					.trim()
					.equalsIgnoreCase(
							viewSolutionsBean.getProposedRegistryTerm()
									.getId().trim())) {

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term id already exists", ""));
				// formCompleted = true;
				return;
			}
		}
		
		for (int i = 0; i < list2.size(); i++) {
			if (list2
					.get(i)
					.getId()
					.trim()
					.equalsIgnoreCase(
							viewSolutionsBean.getProposedRegistryTerm()
									.getId().trim())) {

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term id already exists", ""));
				// formCompleted = true;
				return;
			}
		}


		viewSolutionsBean.getSelectedSetting().getSortedMappings()
				.add(0, viewSolutionsBean.getProposedRegistryTerm().getName());
		Item item = new Item(viewSolutionsBean.getProposedRegistryTerm()
				.getName(), viewSolutionsBean.getProposedRegistryTerm()
				.getDescription());
		viewSolutionsBean.getSelectedSetting().getSortedMappingsObjects()
				.add(0, item);
		viewSolutionsBean.getSelectedSetting().setMapping(
				viewSolutionsBean.getProposedRegistryTerm().getName());

		viewSolutionsBean.getSelectedSetting().setHasMapping(true);

		// add new registry term to all settings
		for (int i = 0; i < viewSolutionsBean.getSelectedSolution()
				.getSettings().size(); i++) {
			Setting set = viewSolutionsBean.getSelectedSolution().getSettings()
					.get(i);
			if (set != viewSolutionsBean.getSelectedSetting()) {
				set.getSortedMappings().add(
						viewSolutionsBean.getProposedRegistryTerm().getName());
				set.getSortedMappingsObjects().add(item);
			}
		}
		ontologyInstance.getProposedRegistryTerms().add(
				viewSolutionsBean.getProposedRegistryTerm());
		viewSolutionsBean.setProposedRegistryTerm(new RegistryTerm());

		try {
			RequestContext rc = RequestContext.getCurrentInstance();
			rc.execute("settingDialog2.hide()");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void addProposedTerm2() {
		newSetting.getSortedMappings().add(0, newSetting.getMapping());
		ontologyInstance.getOntology().addIndividual("ProposedRegistryTerms",
				newSetting.getMapping());

	}

	public boolean isShowInnerPanel() {
		return showInnerPanel;
	}

	public void setShowInnerPanel(boolean showInnerPanel) {
		this.showInnerPanel = showInnerPanel;
	}

	public void deleteSelectedSetting(ActionEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);

		viewSolutionsBean.getSelectedSolution().getSettings()
				.remove(viewSolutionsBean.getSelectedSetting());
	}

	public void addNewSetting(ActionEvent event) {
		this.newSetting = new Setting();
		this.oldSettingName = "";
		this.setRelevantSettings(new ArrayList<Setting>());

	}

	public CommandButton getButton() {
		return button;
	}

	public void setButton(CommandButton button) {
		this.button = button;
	}

	public void alignSetting(ActionEvent event) {

		SortedMap map = new TreeMap();
		for (int j = 0; j < ontologyInstance.getRegistryTermsAsStrings().size(); j++) {
			map.put(ontologyInstance.getRegistryTermsAsStrings().get(j),
					StringComparison.CompareStrings(newSetting.getName(),
							ontologyInstance.getRegistryTermsAsStrings().get(j)));
		}
		SortedSet map2 = entriesSortedByValues(map);
		Iterator itr = map2.iterator();
		String str = "";
		List<String> list = new ArrayList<String>();
		while (itr.hasNext()) {
			str = itr.next().toString().split("=")[0];
			list.add(str);
		}
		
		newSetting.setMapping(str);
		List<String> list2 = reverseList(list);
		newSetting.setSortedMappings(list2);
		showMappingPanel = true;
	}

	private List reverseList(List myList) {
		List invertedList = new ArrayList();
		for (int i = myList.size() - 1; i >= 0; i--) {
			invertedList.add(myList.get(i));
		}
		return invertedList;
	}

	public boolean isShowMappingPanel() {
		return showMappingPanel;
	}

	public void setShowMappingPanel(boolean showMappingPanel) {
		this.showMappingPanel = showMappingPanel;
	}

	public boolean isShowNewSettingPanel() {
		return showNewSettingPanel;
	}

	public void setShowNewSettingPanel(boolean showNewSettingPanel) {
		this.showNewSettingPanel = showNewSettingPanel;
	}

	public void newSettingPressed(ActionEvent event) {

		newSetting = new Setting();
		showNewSettingPanel = true;

	}

	public void saveSolution() {
		FacesContext context = FacesContext.getCurrentInstance();
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		if (viewSolutionsBean.getSelectedSolution().getName().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"applicationName",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide the Application name", ""));
			return;
		}
		
		if (viewSolutionsBean.getSelectedSolution().getId().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"applicationName",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide the Application id", ""));
			return;
		}

		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		String proponentEmail = "";
		String proponentName = "";

		if (userBean.isAdmin()) {
			proponentEmail = "test_admin@cloud4all.org";
			proponentName = "Administrator";
		} else {
			proponentEmail = userBean.getVendorObj().getContactDetails();
			proponentName = userBean.getVendorObj().getVendorName();
		}

		viewSolutionsBean.getSelectedSolution().setVendor(proponentName);
		viewSolutionsBean.getSelectedSolution().setVendorName(proponentName);
		viewSolutionsBean.getClonedSelectedSolution().setVendor(proponentName);
		viewSolutionsBean.getClonedSelectedSolution().setVendorName(
				proponentName);
		
		//keep vendor's ontology URI, to have access in editing the solution
		if (!userBean.isAdmin()) {
			viewSolutionsBean.getSelectedSolution().setVendor(
					userBean.getVendorObj().getOntologyURI());
			viewSolutionsBean.getClonedSelectedSolution().setVendor(
					userBean.getVendorObj().getOntologyURI());
		}
		
		// fill in application id and name in proposed clusters
		for (int i = 0; i < viewSolutionsBean.getProposedClusters().size(); i++) {
			ETNACluster clus = viewSolutionsBean.getProposedClusters().get(i);

			clus.setProponentEmail(proponentEmail);
			clus.setProponentName(proponentName);

			clus.setAllProposalsString(viewSolutionsBean.getSelectedSolution()
					.getName());
			clus.setProposedForProduct(viewSolutionsBean.getSelectedSolution()
					.getName());
			clus.setProposedForProductID(viewSolutionsBean
					.getSelectedSolution().getId());

			clus.getAllproperties().clear();
			clus.getAllproperties().addAll(clus.getProperties());
			clus.getAllproperties().addAll(clus.getMeasureProperties());

			for (int j = 0; j < clus.getAllproperties().size(); j++) {
				EASTINProperty prop = clus.getAllproperties().get(j);
				prop.setRefersToSolutionName(viewSolutionsBean
						.getSelectedSolution().getName());
				prop.setRefersToSolutionID(viewSolutionsBean
						.getSelectedSolution().getId());

				prop.setProponentEmail(proponentEmail);
				prop.setProponentName(proponentName);

			}

		}

		String oldUri = "";
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			if (ontologyInstance
					.getSolutions()
					.get(i)
					.getOntologyURI()
					.equals(viewSolutionsBean.getSelectedSolution()
							.getOntologyURI())) {
				oldUri = ontologyInstance.getSolutions().get(i)
						.getOntologyURI();
				break;
			}
		}

		
		ontologyInstance.getOldSolutionUris().add(oldUri);
		// !!!
		viewSolutionsBean.getSelectedSolution().setProposedClusters(
				viewSolutionsBean.getProposedClusters());
		viewSolutionsBean.getSelectedSolution()
				.setProposedAttributeClusterItems(
						viewSolutionsBean.getProposedClusterItems());
		viewSolutionsBean.getSelectedSolution().setProposedMeasureClusterItems(
				viewSolutionsBean.getProposedMeasureClusterItems());
		// keep clusters for changes in view
		// define clusters to hide/show. Keep in view only clusters that
		// contain selected items
		// , proposed items or imported clusters
		List<ETNACluster> clustersToShow = new ArrayList<ETNACluster>();
		List<ETNACluster> clustersToHide = new ArrayList<ETNACluster>();
		boolean containsProposedItems = false;
		boolean isImportedCluster = false;
		boolean measureHasValue = false;
		for (ETNACluster cl : viewSolutionsBean.getSelectedSolution()
				.getClustersToShowInView()) {

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
			
			for(EASTINProperty prop: cl.getMeasuresToShowInView()){
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
		
		int index = -1;
		for (ETNACluster cl : clustersToShow) {

			for (ETNACluster cl2 : viewSolutionsBean.getSelectedSolution()
					.getClustersToHide()) {
				if (cl.getName().equals(cl2.getName())) {
					index = viewSolutionsBean.getSelectedSolution()
							.getClustersToHide().indexOf(cl2);
					break;
				}
			}

			if (index != -1)
				viewSolutionsBean.getSelectedSolution().getClustersToHide()
						.remove(index);
			index = -1;

		}

		for (ETNACluster cl : viewSolutionsBean.getSelectedSolution()
				.getClustersToHide()) {
			if (!clustersToHide.contains(cl))
				clustersToHide.add(cl);
		}
		
		viewSolutionsBean.getSelectedSolution().setClustersToHide(
				new ArrayList<ETNACluster>());
		viewSolutionsBean.getSelectedSolution().setClustersToShow(
				new ArrayList<ETNACluster>());
		viewSolutionsBean.getSelectedSolution().setClustersToShowInView(
				new ArrayList<ETNACluster>());
		for (ETNACluster cl : clustersToHide) {
			viewSolutionsBean.getSelectedSolution().getClustersToHide()
					.add(cl.clone());
		}

		for (ETNACluster cl : clustersToShow) {
			viewSolutionsBean.getSelectedSolution().getClustersToShow()
					.add(cl.clone());
			viewSolutionsBean.getSelectedSolution().getClustersToShowInView()
					.add(cl.clone());
		}
		
	
		// get the new uri and set it to the solution
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		String uri = viewSolutionsBean.getSelectedSolution().getName().trim()
				.replace(" ", "_").replaceAll("[^\\p{L}\\p{Nd}]", "");

		// check if uri exists
		if (Character.isDigit(uri.charAt(0))) {
			uri = "app";
		}
		uri = ontologyInstance.getOntology().changeUri(uri);
		// ----------------------------------------------------

		viewSolutionsBean.getSelectedSolution().setOntologyURI(
				ontologyInstance.getOntology().getNS() + uri);
		viewSolutionsBean.getClonedSelectedSolution().setOntologyURI(
				ontologyInstance.getOntology().getNS() + uri);
		
		
		// update uri in solutions list
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			if (ontologyInstance.getSolutions().get(i).getOntologyURI()
					.equals(oldUri)) {
				ontologyInstance
						.getSolutions()
						.get(i)
						.setOntologyURI(
								ontologyInstance.getOntology().getNS() + uri);
			}
		}

		// remove from ontology list
		
		// update proposed for product
		for (ETNACluster c : viewSolutionsBean.getSelectedSolution()
				.getClustersToShowInView()) { //getETNAProperties
			for (ETNACluster temp : ontologyInstance
					.getProposedEASTINProperties()) {

				if (c.getName().trim().equalsIgnoreCase(temp.getName().trim())
						&& !viewSolutionsBean.getSelectedSolution().getName()
								.equalsIgnoreCase(temp.getProposedForProduct())) {
					// update string with proposed Product names
					temp.setAllProposalsString(temp.getAllProposalsString()
							.concat(","
									+ viewSolutionsBean.getSelectedSolution()
											.getName()));

				}
			}
			
			// update proposed for porduct for properties
			for (EASTINProperty prop : c.getAttributesToShowInView()) {
				for (EASTINProperty tempProp : ontologyInstance
						.getProposedEASTINItems()) {
					if (prop.getName().equalsIgnoreCase(tempProp.getName())
							&& c.getName().equalsIgnoreCase(
									tempProp.getBelongsToCluster())
							&& !viewSolutionsBean
									.getSelectedSolution()
									.getName()
									.equalsIgnoreCase(
											tempProp.getRefersToSolutionName())) {
						tempProp.setEdit(true);
						tempProp.setAllSolutionsString(tempProp
								.getAllSolutionsString().concat(
										","
												+ viewSolutionsBean
														.getSelectedSolution()
														.getName()));
					}
				}
			}

			for (EASTINProperty prop : c.getMeasuresToShowInView()) {
				for (EASTINProperty tempProp : ontologyInstance
						.getProposedMeasureEASTINItems()) {
					if (prop.getName().equalsIgnoreCase(tempProp.getName())
							&& c.getName().equalsIgnoreCase(
									tempProp.getBelongsToCluster())
							&& !viewSolutionsBean
									.getSelectedSolution()
									.getName()
									.equalsIgnoreCase(
											tempProp.getRefersToSolutionName())) {
						tempProp.setEdit(true);
						tempProp.setAllSolutionsString(tempProp
								.getAllSolutionsString().concat(
										","
												+ viewSolutionsBean
														.getSelectedSolution()
														.getName()));
					}
				}
			}
		}

		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			if (ontologyInstance
					.getSolutions()
					.get(i)
					.getOntologyURI()
					.equals(viewSolutionsBean.getClonedSelectedSolution()
							.getOntologyURI())) {
				ontologyInstance.getSolutions().remove(i);
				ontologyInstance.getSolutions().add(i,
						viewSolutionsBean.getSelectedSolution());
				break;
			}
		}

		
		
		for (ETNACluster cluster : viewSolutionsBean.getProposedClusters()) {
			if (!ontologyInstance.getProposedEASTINPropertiesOriginal()
					.contains(cluster))
				ontologyInstance.getProposedEASTINPropertiesOriginal().add(
						cluster);
		}

		for (EASTINProperty prop : viewSolutionsBean.getProposedClusterItems()) {
			if (!ontologyInstance.getProposedEASTINItemsOriginal().contains(
					prop))
				ontologyInstance.getProposedEASTINItemsOriginal().add(prop);
		}

		for (EASTINProperty prop : viewSolutionsBean
				.getProposedMeasureClusterItems()) {
			if (!ontologyInstance.getProposedMeasureEASTINItemsOriginal()
					.contains(prop))
				ontologyInstance.getProposedMeasureEASTINItemsOriginal().add(
						prop);
		}

		
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
				if ((solution.getVendor().equals("Admin") && solution
						.getVendorName().equals("Admin"))
						|| (solution.getVendor().equals("Administrator") && solution
								.getVendorName().equals("Administrator"))) {
					this.getUserSolutions().add(solution.clone());
				}
			}

		}
		editSolutionBean.setActiveTabIndex("-1");
		
		viewSolutionsBean.setClustersShowFlag(false);
		if (viewSolutionsBean.getSelectedSolution().getClustersToHide().size() > 0)
			viewSolutionsBean.setClustersShowFlag(true);
		
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("savedDialog.show()");
		
		// TODO update the solution in the unified Listing
		// WebServicesForSolutions.solutionHTTPPutRequest(viewSolutionsBean.getSelectedSolution());
		// for(Setting sett :
		// viewSolutionsBean.getSelectedSolution().getSettings()){
		// TODO update setting
		// }
		
	}
	

	
	public void editUsersSelectedSolution(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);

		editSolutionBean.setActiveTabIndex("-1");
		
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
		viewSolutionsBean.getActiveETNACluster().setItemProposalDescription("");
		viewSolutionsBean.getActiveETNACluster().setItemProposalName("");
		viewSolutionsBean.getActiveETNACluster().setItemProposalType(
				"Attribute");
		viewSolutionsBean.setActiveETNACluster(ontologyInstance
				.getEtnaCluster().get(0));

		
	}

	public void deleteSolution() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();

			ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
					.getApplication().evaluateExpressionGet(context,
							"#{viewSolutionsBean}", ViewSolutionsBean.class);

    		ontologyInstance.getOldSolutionUris().add(viewSolutionsBean.getClonedSelectedSolution()
									.getOntologyURI());
			
			ontologyInstance.getSolutions().remove(
					viewSolutionsBean.getClonedSelectedSolution());
			
			String solutionName = viewSolutionsBean.getClonedSelectedSolution()
					.getName();
			List<ETNACluster> clustersForRemove = new ArrayList<ETNACluster>();

			// update all proposals string for proposed clusters and remove proposed
			//clusters with proposalsString equals to solution name

				for (ETNACluster temp : ontologyInstance
						.getProposedEASTINPropertiesOriginal()) {
					if (temp.getAllProposalsString().equalsIgnoreCase(
							solutionName)) {

						clustersForRemove.add(temp);

					} else if (temp.getAllProposalsString().contains(
							solutionName + ",")) {

						temp.setEdit(true);
						temp.setAllProposalsString(temp.getAllProposalsString().replace(
								solutionName + ",", ""));

					} else if (temp.getAllProposalsString().contains(
							"," + solutionName)) {
						
						temp.setEdit(true);
						temp.setAllProposalsString(temp.getAllProposalsString().replace(
								"," + solutionName, ""));
					}
				}

			

			//update lists
			for (ETNACluster temp : clustersForRemove) {
				ontologyInstance.getProposedEASTINPropertiesOriginal().remove(
						temp);
				ontologyInstance.getProposedEASTINProperties().remove(temp);
			}

			List<ETNACluster> clusters = new ArrayList<ETNACluster>();
			for (ETNACluster temp : ontologyInstance
					.getProposedEASTINPropertiesOriginal()) {
				clusters.add(temp);
			}

			ontologyInstance.setProposedEASTINProperties(clusters);
				
			//delete proposed cluster Items, update lists
			List<EASTINProperty> all_items = new ArrayList<EASTINProperty>();
			all_items.addAll(viewSolutionsBean.getClonedSelectedSolution().getProposedAttributeClusterItems());
			all_items.addAll(viewSolutionsBean.getClonedSelectedSolution().getProposedMeasureClusterItems());
			
			for (int i = 0; i < all_items.size(); i++) {
				EASTINProperty prop = all_items.get(i);

				ontologyInstance.getAllProposedItems().remove(prop);

				if (prop.getType().equalsIgnoreCase("attribute")){
					ontologyInstance.getProposedEASTINItems().remove(
							prop);
					ontologyInstance.getProposedEASTINItemsOriginal().remove(
							prop);
				}
				else{
					ontologyInstance.getProposedMeasureEASTINItems()
					.remove(prop);
					ontologyInstance.getProposedMeasureEASTINItemsOriginal()
							.remove(prop);
				}
			}

			viewSolutionsBean.getRoot().setExpanded(false);
			viewSolutionsBean.getRoot().setSelected(false);
			clearSelections(viewSolutionsBean.getRoot());
			viewSolutionsBean.setSearchApplicationName("");
			viewSolutionsBean.setSolutions(new ArrayList<Solution>());
			viewSolutionsBean.setShowSolutionsPanel(false);
			
			/**
			 * // delete application from Unified Listing
			 * WebService.deleteHTTPRequest(viewSolutionsBean
			 * .getClonedSelectedSolution().getId(),
			 * "https://registry.gpii.net/api/product/");
			 **/
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void clearSelections(TreeNode root) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			node.setSelected(false);
			node.setExpanded(false);
			clearSelections(node);
		}
	}

	public void test2() {

		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		viewSolutionsBean.getClonedSelectedSetting().setType(
				viewSolutionsBean.getType());
		// check if setting name already exist
		for (int i = 0; i < viewSolutionsBean.getSelectedSolution()
				.getSettings().size(); i++) {

			if (i != viewSolutionsBean.getSelectedSolution().getSettings()
					.indexOf(viewSolutionsBean.getSelectedSetting())
					&& viewSolutionsBean
							.getSelectedSolution()
							.getSettings()
							.get(i)
							.getName()
							.trim()
							.equalsIgnoreCase(
									viewSolutionsBean
											.getClonedSelectedSetting()
											.getName().trim())) {

				FacesContext
						.getCurrentInstance()
						.addMessage(
								"messages3",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"This setting name already exists in the application",
										""));
				return;
			}
		}

		if (viewSolutionsBean.getClonedSelectedSetting().getName().trim()
				.equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages3",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting name", ""));
		if (viewSolutionsBean.getClonedSelectedSetting().getDescription()
				.trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages3",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting description", ""));
		if (viewSolutionsBean.getClonedSelectedSetting().getValueSpace().trim()
				.equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages3",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting value space", ""));
		if (viewSolutionsBean.getClonedSelectedSetting().getValue().trim()
				.equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"messages3",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting default value", ""));

		if (viewSolutionsBean.getClonedSelectedSetting().getName().trim()
				.equals("")
				|| viewSolutionsBean.getClonedSelectedSetting().getValue()
						.trim().equals("")
				|| viewSolutionsBean.getClonedSelectedSetting()
						.getDescription().trim().equals("")
				|| viewSolutionsBean.getClonedSelectedSetting().getValueSpace()
						.trim().equals("")) {

			return;
		}

		// check if default value matches with type
		if (viewSolutionsBean.getClonedSelectedSetting().getType()
				.equals("integer")) {
			try {
				Integer.parseInt(viewSolutionsBean.getClonedSelectedSetting()
						.getValue().trim());
			} catch (Exception ex) {

				context.addMessage("messages3", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ viewSolutionsBean.getClonedSelectedSetting()
										.getValue().trim()
								+ "\" is not of type \""
								+ viewSolutionsBean.getClonedSelectedSetting()
										.getType() + "\"", ""));
				return;
			}
		} else if (viewSolutionsBean.getClonedSelectedSetting().getType()
				.equals("boolean")) {
			try {
				if (!viewSolutionsBean.getClonedSelectedSetting().getValue()
						.trim().toLowerCase().equals("true")
						&& !viewSolutionsBean.getClonedSelectedSetting()
								.getValue().trim().toLowerCase()
								.equals("false")) {

					context.addMessage("messages3", new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "The default value \""
									+ viewSolutionsBean
											.getClonedSelectedSetting()
											.getValue().trim()
									+ "\" is not of type \""
									+ viewSolutionsBean
											.getClonedSelectedSetting()
											.getType() + "\"", ""));
					return;
				}

			} catch (Exception ex) {

			}
		} else if (viewSolutionsBean.getClonedSelectedSetting().getType()
				.equals("double")) {
			try {
				Double.parseDouble(viewSolutionsBean.getClonedSelectedSetting()
						.getValue().trim());
			} catch (Exception ex) {

				context.addMessage("messages3", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ viewSolutionsBean.getClonedSelectedSetting()
										.getValue().trim()
								+ "\" is not of type \""
								+ viewSolutionsBean.getClonedSelectedSetting()
										.getType() + "\"", ""));
				return;
			}
		}

		viewSolutionsBean.getClonedSelectedSetting()
				.setMapping(selectedMapping);
		int i = viewSolutionsBean.getSelectedSolution().getSettings()
				.indexOf(viewSolutionsBean.getSelectedSetting());
		viewSolutionsBean.getSelectedSolution().getSettings()
				.remove(viewSolutionsBean.getSelectedSetting());
		viewSolutionsBean.getSelectedSolution().getSettings()
				.add(i, viewSolutionsBean.getClonedSelectedSetting());
		// selectedSetting=clonedSelectedSetting;
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("settingDialog.hide()");

		if (!viewSolutionsBean.getSelectedSetting().ishasMapping()) {
			viewSolutionsBean.getSelectedSetting().setComments("");
			viewSolutionsBean.getSelectedSetting().setMapping("");
			viewSolutionsBean.getSelectedSetting().setExactMatching(false);
		}
	}

	public String getSelectedMapping() {
		return selectedMapping;
	}

	public void setSelectedMapping(String selectedMapping) {
		this.selectedMapping = selectedMapping;
	}

	public void previousPressed() {

	}

	public void addNewSetting2() {
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		// check if setting name already exist
		for (int i = 0; i < viewSolutionsBean.getSelectedSolution()
				.getSettings().size(); i++) {
			if (viewSolutionsBean.getSelectedSolution().getSettings().get(i)
					.getName().trim()
					.equalsIgnoreCase(newSetting.getName().trim())) {

				FacesContext
						.getCurrentInstance()
						.addMessage(
								"gridOfSettingMessage",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"This setting name already exists in the application",
										""));
				return;
			}
		}

		if (newSetting.getName().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"gridOfSettingMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting name", ""));
		if (newSetting.getDescription().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"gridOfSettingMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting description", ""));
		if (newSetting.getValueSpace().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"gridOfSettingMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting value space", ""));
		if (newSetting.getValue().trim().equals(""))
			FacesContext.getCurrentInstance().addMessage(
					"gridOfSettingMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide setting default value", ""));

		if (newSetting.getName().trim().equals("")
				|| newSetting.getValue().trim().equals("")
				|| newSetting.getDescription().trim().equals("")
				|| newSetting.getValueSpace().trim().equals("")) {

			return;
		}
		// check if default value matches with type
		if (newSetting.getType().equals("integer")) {
			try {
				Integer.parseInt(newSetting.getValue().trim());
			} catch (Exception ex) {

				context.addMessage("gridOfSettingMessage", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ newSetting.getValue().trim()
								+ "\" is not of type \"" + newSetting.getType()
								+ "\"", ""));
				return;
			}
		} else if (newSetting.getType().equals("boolean")) {
			try {
				if (!newSetting.getValue().trim().toLowerCase().equals("true")
						&& !newSetting.getValue().trim().toLowerCase()
								.equals("false")) {

					context.addMessage("gridOfSettingMessage",
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"The default value \""
											+ newSetting.getValue().trim()
											+ "\" is not of type \""
											+ newSetting.getType() + "\"", ""));
					return;
				}

			} catch (Exception ex) {

			}
		} else if (newSetting.getType().equals("double")) {
			try {
				Double.parseDouble(newSetting.getValue().trim());
			} catch (Exception ex) {

				context.addMessage("gridOfSettingMessage", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ newSetting.getValue().trim()
								+ "\" is not of type \"" + newSetting.getType()
								+ "\"", ""));
				return;
			}
		}

		SortedMap map = new TreeMap();
		for (int j = 0; j < ontologyInstance.getRegistryTermsAsStrings().size(); j++) {
			map.put(ontologyInstance.getRegistryTermsAsStrings().get(j),
					StringComparison.CompareStrings(newSetting.getName(),
							ontologyInstance.getRegistryTermsAsStrings().get(j)));
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

		if (newSetting
				.getConstraints()
				.equals("e.g. MagnifierPosition=fullScreen requires AERO Design Windows 7 to be ON")) {
			newSetting.setConstraints("");
		}
		newSetting.setComments("");

		viewSolutionsBean.getSelectedSolution().getSettings()
				.add(newSetting.cloneSetting());

		newSetting = new Setting();
		relevantSettings = new ArrayList<Setting>();
		this.oldSettingName = "";
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("addNewSettingDialog.hide()");
	}

	public void clearNewRegistryTerm() {
		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		viewSolutionsBean.setProposedRegistryTerm(new RegistryTerm());
		viewSolutionsBean.setFormCompleted(true);
	}

	/**
	 * @return the solutionsListAsStrings
	 */
	public List<Solution> getSolutionsListAsStrings() {
		return solutionsListAsStrings;
	}

	/**
	 * @param solutionsListAsStrings
	 *            the solutionsListAsStrings to set
	 */
	public void setSolutionsListAsStrings(List<Solution> solutionsListAsStrings) {
		this.solutionsListAsStrings = solutionsListAsStrings;
	}

	/**
	 * @return the totalDiscount
	 */
	public double getTotalDiscount() {
		return totalDiscount;
	}

	/**
	 * @param totalDiscount
	 *            the totalDiscount to set
	 */
	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	/**
	 * @return the costAfterDiscount
	 */
	public double getCostAfterDiscount() {
		return costAfterDiscount;
	}

	/**
	 * @param costAfterDiscount
	 *            the costAfterDiscount to set
	 */
	public void setCostAfterDiscount(double costAfterDiscount) {
		this.costAfterDiscount = costAfterDiscount;
	}

	/**
	 * @return the commercialCostCurrency
	 */
	public List<String> getCommercialCostCurrency() {
		return commercialCostCurrency;
	}

	/**
	 * @param commercialCostCurrency
	 *            the commercialCostCurrency to set
	 */
	public void setCommercialCostCurrency(List<String> commercialCostCurrency) {
		this.commercialCostCurrency = commercialCostCurrency;
	}

	/**
	 * @return the costPaymentChargeType
	 */
	public List<String> getCostPaymentChargeType() {
		return costPaymentChargeType;
	}

	/**
	 * @param costPaymentChargeType
	 *            the costPaymentChargeType to set
	 */
	public void setCostPaymentChargeType(List<String> costPaymentChargeType) {
		this.costPaymentChargeType = costPaymentChargeType;
	}

	public void addDiscountSchema() {
		try {
			DiscountSchema discountSchema = new DiscountSchema();
			calculateSolutions();
			FacesContext context = FacesContext.getCurrentInstance();

			ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
					.getApplication().evaluateExpressionGet(context,
							"#{viewSolutionsBean}", ViewSolutionsBean.class);
			for (int i = 0; i < solutionsListAsStrings.size(); i++) {
				if (solutionsListAsStrings.get(i) != null)
					discountSchema.getSolutionsListAsStrings().add(
							solutionsListAsStrings.get(i));
			}
			viewSolutionsBean.getSelectedSolution().getAccessInfoForVendors()
					.getCommercialCostSchema()
					.getDiscountIfUsedWithOtherSolution().add(discountSchema);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addDiscountSchemaForUsers() {
		try {
			DiscountSchema discountSchema = new DiscountSchema();
			calculateSolutions();
			FacesContext context = FacesContext.getCurrentInstance();

			ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
					.getApplication().evaluateExpressionGet(context,
							"#{viewSolutionsBean}", ViewSolutionsBean.class);
			for (int i = 0; i < solutionsListAsStrings.size(); i++) {
				if (solutionsListAsStrings.get(i) != null)
					discountSchema.getSolutionsListAsStrings().add(
							solutionsListAsStrings.get(i));
			}
			viewSolutionsBean.getSelectedSolution().getAccessInfoForUsers()
					.getCommercialCostSchema()
					.getDiscountIfUsedWithOtherSolution().add(discountSchema);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addDiscountSchemaForVendors() {
		try {
			DiscountSchema discountSchema = new DiscountSchema();
			calculateSolutions();
			FacesContext context = FacesContext.getCurrentInstance();

			ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
					.getApplication().evaluateExpressionGet(context,
							"#{viewSolutionsBean}", ViewSolutionsBean.class);
			for (int i = 0; i < solutionsListAsStrings.size(); i++) {
				if (solutionsListAsStrings.get(i) != null)
					discountSchema.getSolutionsListAsStrings().add(
							solutionsListAsStrings.get(i));
			}
			viewSolutionsBean.getSelectedSolution().getAccessInfoForVendors()
					.getCommercialCostSchema()
					.getDiscountIfUsedWithOtherSolution().add(discountSchema);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addDiscountSchemaForSLA() {
		try {
			DiscountSchema discountSchema = new DiscountSchema();
			calculateSolutions();

			for (int i = 0; i < solutionsListAsStrings.size(); i++) {
				if (solutionsListAsStrings.get(i) != null)
					discountSchema.getSolutionsListAsStrings().add(
							solutionsListAsStrings.get(i));
			}
			sla.getDiscountSchema().add(discountSchema);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addDiscountSchemaForEULA() {
		try {
			DiscountSchema discountSchema = new DiscountSchema();
			calculateSolutions();

			for (int i = 0; i < solutionsListAsStrings.size(); i++) {
				if (solutionsListAsStrings.get(i) != null)
					discountSchema.getSolutionsListAsStrings().add(
							solutionsListAsStrings.get(i));
			}
			eula.getDiscountSchema().add(discountSchema);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void calculateSolutions() {
		solutionsListAsStrings = new ArrayList<Solution>();
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		
		if(userBean.isAdmin()==true){
			Vendor v = new Vendor();
			v.setOntologyURI("");
			userBean.setVendorObj(v);
		}
			

		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			if (userBean.getVendorObj().getOntologyURI()
					.equals(ontologyInstance.getSolutions().get(i).getVendor()))
				solutionsListAsStrings.add(ontologyInstance.getSolutions()
						.get(i).cloneSolution());
		}

		Solution sol = new Solution();
		sol.setOntologyURI("");
		sol.setName("None");
		solutionsListAsStrings.add(0, sol);
	}

	/**
	 * @return the sla
	 */
	public SLA getSla() {
		return sla;
	}

	/**
	 * @param sla
	 *            the sla to set
	 */
	public void setSla(SLA sla) {
		this.sla = sla;
	}

	public void newSLA() {
		this.sla = new SLA();
		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);

		this.sla.setDiscountSchema(viewSolutionsBean.getSelectedSolution()
				.getAccessInfoForVendors().getCommercialCostSchema()
				.getDiscountIfUsedWithOtherSolution());
		this.sla.setSLA_Cost(viewSolutionsBean.getSelectedSolution()
				.getAccessInfoForVendors().getCommercialCostSchema()
				.getCommercialCost());
		this.sla.setSLA_CostCurrency(viewSolutionsBean.getSelectedSolution()
				.getAccessInfoForVendors().getCommercialCostSchema()
				.getCommercialCostCurrency());
		this.sla.setSLA_CostPaymentChargeType(viewSolutionsBean
				.getSelectedSolution().getAccessInfoForVendors()
				.getCommercialCostSchema().getCostPaymentChargeType());
	}

	public void newEULA() {
		this.eula = new EULA();
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);

		this.eula.setDiscountSchema(viewSolutionsBean.getSelectedSolution()
				.getAccessInfoForUsers().getCommercialCostSchema()
				.getDiscountIfUsedWithOtherSolution());
		this.eula.setEULA_cost(viewSolutionsBean.getSelectedSolution()
				.getAccessInfoForUsers().getCommercialCostSchema()
				.getCommercialCost());
		this.eula.setEULA_costCurrency(viewSolutionsBean.getSelectedSolution()
				.getAccessInfoForUsers().getCommercialCostSchema()
				.getCommercialCostCurrency());
		this.eula.setEULA_costPaymentChargeType(viewSolutionsBean
				.getSelectedSolution().getAccessInfoForUsers()
				.getCommercialCostSchema().getCostPaymentChargeType());
	}

	public void addNewSLA() {
		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);

		viewSolutionsBean.getSelectedSolution().getSLAs().add(sla);
		sla.setSolution(viewSolutionsBean.getSelectedSolution());
		ontologyInstance.getSla().add(sla);
//		ontologyInstance.getOntology().saveSLA(sla,
//				viewSolutionsBean.getSelectedSolution());
	}

	public void cancelAddNewSLA() {
		sla = new SLA();
	}

	public void addNewEULA() {
		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		viewSolutionsBean.getSelectedSolution().getEULAs().add(eula);
		eula.setSolution(viewSolutionsBean.getSelectedSolution());
		ontologyInstance.getEula().add(eula);

//		ontologyInstance.getOntology().saveEULA(eula,
//				viewSolutionsBean.getSelectedSolution());
		for (int i = 0; i < ontologyInstance.getVendors().size(); i++) {
			Vendor ven = ontologyInstance.getVendors().get(i);
			if (ven.getOntologyURI().equals(
					viewSolutionsBean.getSelectedSolution().getVendor())) {
				eula.setVendorOfAgreement(ven);
				eula.getAccompanyingSolutions().add(
						viewSolutionsBean.getSelectedSolution()
								.getOntologyURI());
				ven.getEulas().add(eula);
			}
		}
		// create solution user schema
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		SolutionUserSchema userSchema = new SolutionUserSchema();
		userSchema.setEula(eula);
		userSchema.setRefersToSolution(viewSolutionsBean.getSelectedSolution()
				.getOntologyURI());
		User currentUser = LiferayFacesContext.getInstance().getUser();
		userSchema.setUserId(currentUser.getEmailAddress());
		ontologyInstance.getUserSchemaForInsert().add(userSchema);
	//	ontologyInstance.getOntology().createSolutionUserSchema(userSchema);
		ontologyInstance.getSolutionUserSchemas().add(userSchema);
	}

	public void cancelAddNewEULA() {
		eula = new EULA();
	}

	public EULA getEula() {
		return eula;
	}

	public void setEula(EULA eula) {
		this.eula = eula;
	}

	public List<String> getSettingsTypes() {
		return settingsTypes;
	}

	public void setSettingsTypes(List<String> settingsTypes) {
		this.settingsTypes = settingsTypes;
	}


	public void loadEastinProperties() {
		System.out.println("call web service");
		dialogEmptyMessage = "No relevant applications found";
		eastinApplications = new ArrayList<EASTINApplication>();
		
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		
		// call eastin web service
		if (!viewSolutionsBean.getSelectedSolution().getName().equals("")
				|| !viewSolutionsBean.getSelectedSolution()
						.getManufacturerName().trim().equals("")) {
			try {

				URL url;
				HttpURLConnection conn;
				BufferedReader rd;
				String line;
				String result = "";
				String link = "http://webservices.eastin.eu/cloud4all/searches/products/listsimilarity?commercialName="
						+ viewSolutionsBean.getSelectedSolution().getName()
								.trim()
						+ "&manufacturer="
						+ viewSolutionsBean.getSelectedSolution()
								.getManufacturerName().trim();
				link = link.replace(" ", "%20");
				
				url = new URL(link);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
				conn.disconnect();
			
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();

				Type listType = new TypeToken<List<EASTINApplication>>() {
				}.getType();
				List<EASTINApplication> tmp = new ArrayList<EASTINApplication>();
				tmp = (List<EASTINApplication>) gson.fromJson(result, listType);

				for (int j = 0; j < tmp.size(); j++) {
					result = "";
					String url2 = "http://webservices.eastin.eu/cloud4all/searches/products/detail/"
							+ URLEncoder.encode(tmp.get(j).getDatabase(), "UTF-8")
							+ "/"
							+ URLEncoder.encode(tmp.get(j).getProductCode(), "UTF-8");
					url2 = url2.replace("+", "%20");
					System.out.println(url2);
					url = new URL(url2);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					rd = new BufferedReader(new InputStreamReader(
							conn.getInputStream()));
					while ((line = rd.readLine()) != null) {
						result += line;
					}
					gson = new Gson();
					parser = new JsonParser();
					Type listType2 = new TypeToken<EASTINApplication>() {
					}.getType();

					eastinApplications.add((EASTINApplication) gson.fromJson(
							result, listType2));

				}
				rd.close();
				conn.disconnect();
				
				for (int i = 0; i < eastinApplications.size(); i++) {
					try {
						EASTINApplication app = eastinApplications.get(i);

						String epochString = app.getInsertDate();

						epochString = epochString.substring(
								epochString.indexOf("(") + 1,
								epochString.indexOf("+"));

						// epochString = epochString.substring(6, 19);
						long epoch = Long.parseLong(epochString);
						Date expiry = new Date(epoch);
						SimpleDateFormat sdf2 = new SimpleDateFormat(
								"dd-MM-yyyy", Locale.ENGLISH);
						app.setInsertDate(sdf2.format(expiry));

						epochString = app.getLastUpdateDate();
						epochString = epochString.substring(
								epochString.indexOf("(") + 1,
								epochString.indexOf("+"));
						epoch = Long.parseLong(epochString);
						expiry = new Date(epoch);
						sdf2 = new SimpleDateFormat("dd-MM-yyyy",
								Locale.ENGLISH);

						app.setLastUpdateDate(sdf2.format(expiry));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				dialogEmptyMessage = "Communication error with web service";
			}
		}

	}

	public List<EASTINApplication> getEastinApplications() {
		return eastinApplications;
	}

	public void setEastinApplications(List<EASTINApplication> eastinApplications) {
		this.eastinApplications = eastinApplications;
	}

	public EASTINApplication getSelectedEastinApplication() {
		return selectedEastinApplication;
	}

	public void setSelectedEastinApplication(
			EASTINApplication selectedEastinApplication) {
		this.selectedEastinApplication = selectedEastinApplication;
		importEastingApp();
	}

	public void importEastingApp() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
					.getApplication().evaluateExpressionGet(context,
							"#{viewSolutionsBean}", ViewSolutionsBean.class);
			System.out.println("importing "
					+ selectedEastinApplication.getCommercialName());
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy",
					Locale.ENGLISH);
			Date expiry = sdf2.parse(selectedEastinApplication.getInsertDate());
			viewSolutionsBean.getSelectedSolution().setDate(expiry);
			expiry = sdf2.parse(selectedEastinApplication.getLastUpdateDate());
			viewSolutionsBean.getSelectedSolution().setUpdateDate(expiry);

			viewSolutionsBean.getSelectedSolution()
					.setManufacturerName(
							selectedEastinApplication
									.getManufacturerOriginalFullName());
			viewSolutionsBean.getSelectedSolution().setImageUrl(
					selectedEastinApplication.getThumbnailImageUrl());
			if (selectedEastinApplication.getManufacturerCountry() != null
					&& !selectedEastinApplication.getManufacturerCountry()
							.equals("null"))
				viewSolutionsBean.getSelectedSolution().setManufacturerCountry(
						WordUtils.capitalizeFully(selectedEastinApplication
								.getManufacturerCountry()));
			else
				viewSolutionsBean.getSelectedSolution().setManufacturerCountry(
						"");

			viewSolutionsBean.getSelectedSolution().setManufacturerWebsite(
					selectedEastinApplication.getManufacturerWebSiteUrl());
			viewSolutionsBean.getSelectedSolution().setDownloadPage(
					selectedEastinApplication.getEnglishUrl());
			viewSolutionsBean.getSelectedSolution().setDescription(
					selectedEastinApplication.getEnglishDescription());

			viewSolutionsBean.getSelectedSolution().setEtnaProperties(
					ontologyInstance.getOntology().getETNATaxonomy());

			for (int i = 0; i < selectedEastinApplication.getFeatures().size(); i++) {
				Features f = selectedEastinApplication.getFeatures().get(i);
				for (int j = 0; j < viewSolutionsBean.getSelectedSolution()
						.getEtnaProperties().size(); j++) {
					ETNACluster etna = viewSolutionsBean.getSelectedSolution()
							.getEtnaProperties().get(j);
					if (f.getFeatureParentName().toLowerCase()
							.equals(etna.getName().toLowerCase())) {

						etna.getSelectedProperties().add(f.getFeatureName());

						if (etna.isSingleSelection()) {
							etna.setSelectedProperty(f.getFeatureName());
						}
						break;
					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getDialogEmptyMessage() {
		return dialogEmptyMessage;
	}

	public void setDialogEmptyMessage(String dialogEmptyMessage) {
		this.dialogEmptyMessage = dialogEmptyMessage;
	}

	public void changeCategory() {
		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		viewSolutionsBean.getSelectedSolution().setOntologyCategory(
				selectedCategory);
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null) {
			// clear tree selection
			root.setSelected(false);
			clearSelections2(root);

			// set new selection
			this.selectedNode = selectedNode;

			// expand nodes until root
			TreeNode tmp = this.selectedNode.getParent();

			while (tmp != null) {
				tmp.setExpanded(true);
				tmp = tmp.getParent();
			}
			this.selectedNode.setSelected(true);

			tmp = selectedNode;
			tmp.setSelected(true);
			this.selectedNode = selectedNode;

		}

	}

	private void clearSelections2(TreeNode root) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			node.setSelected(false);
			node.setExpanded(false);
			clearSelections(node);
		}
		this.selectedNode = null;
	}

	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
		if (selectedNode != null)
			selectedCategory = (String) selectedNode.getData();
		else
			selectedCategory = "";
		setSelectedNode(selectedNode);

	}

	public void createOntologyTree(Ontology ontology) {
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		List<OntologyClass> list = ontologyInstance.getSolutionClassesStructured();
		OntologyClass cl = list.get(0);
		root = new DefaultTreeNode(cl.getClassName(), null);

		getTreeNodeOfConcept(cl, root);
		root = (DefaultTreeNode) root.getChildren().get(0);
	}

	private DefaultTreeNode getTreeNodeOfConcept(OntologyClass cl, TreeNode root) {
		DefaultTreeNode node = new DefaultTreeNode(cl.getClassName(), root);
		for (int i = 0; i < cl.getChildren().size(); i++) {
			getTreeNodeOfConcept(cl.getChildren().get(i), node);
		}
		return node;

	}

	public void calculateCategoriesTree() {

		root = new DefaultTreeNode("Root", null);
		createOntologyTree(ontologyInstance.getOntology());
		FacesContext context = FacesContext.getCurrentInstance();

		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		selectItemInTree(root, viewSolutionsBean.getSelectedSolution()
				.getOntologyCategory());
	}

	public void expandallParents(TreeNode root) {
		root.setExpanded(true);
		if (root.getParent() != null) {
			expandallParents(root.getParent());
		}
	}

	public void selectItemInTree(TreeNode root, String nodeName) {
		root.setSelected(false);
		if (root.getData().toString().equals(nodeName)) {
			root.setSelected(true);
			expandallParents(root);

			selectedNode = root;
			return;
		} else
			for (int i = 0; i < root.getChildCount(); i++) {
				selectItemInTree(root.getChildren().get(i), nodeName);
			}
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public boolean isCategoryProposed() {
		return categoryProposed;
	}

	public void setCategoryProposed(boolean categoryProposed) {
		this.categoryProposed = categoryProposed;
	}

	public String getActiveTabIndex() {
		return activeTabIndex;
	}

	public void setActiveTabIndex(String activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}

	public void cancelChangeCategory(){
		selectedCategory="";
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
		editUsersSelectedSolution();
	}

	public String getOldSettingName() {
		return oldSettingName;
	}

	public void setOldSettingName(String oldSettingName) {
		this.oldSettingName = oldSettingName;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public ArrayList<Setting> getRelevantSettings() {
		return relevantSettings;
	}

	public void setRelevantSettings(ArrayList<Setting> relevantSettings) {
		this.relevantSettings = relevantSettings;
	}

	public boolean isCheckForRelevantSetting() {
		return checkForRelevantSetting;
	}

	public void setCheckForRelevantSetting(boolean checkForRelevantSetting) {
		this.checkForRelevantSetting = checkForRelevantSetting;
	}

	public Setting getSelectedRelevantSetting() {
		return selectedRelevantSetting;
	}

	public void setSelectedRelevantSetting(Setting selectedRelevantSetting) {
		this.selectedRelevantSetting = selectedRelevantSetting;
	}

	public boolean isShowEditInDialog() {
		return showEditInDialog;
	}

	public void setShowEditInDialog(boolean showEditInDialog) {
		this.showEditInDialog = showEditInDialog;
	}

	public boolean isSettingNameExists() {
		return settingNameExists;
	}

	public void setSettingNameExists(boolean settingNameExists) {
		this.settingNameExists = settingNameExists;
	}
	
}
