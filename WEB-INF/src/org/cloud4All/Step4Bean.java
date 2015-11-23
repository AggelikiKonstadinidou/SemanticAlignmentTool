package org.cloud4All;

import java.io.IOException;
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

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;

@ManagedBean(name = "step4Bean")
@SessionScoped
public class Step4Bean {
	private List<String> registryItemsList = new ArrayList<String>();

	private List<Setting> settings;
	private Setting selectedSetting = new Setting();
	private String newRegistryTerm = "";
	private DataTable dataTable = null;
	private RegistryTerm proposedRegistryTerm;
	private String solutionURI = "";
	private OntologyInstance ontologyInstance;
	private List<Solution> userSolutions = new ArrayList<Solution>();
	private Solution selectedSolution = new Solution();

	public Step4Bean() {
		super();
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Step1Bean step1Bean = (Step1Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step1Bean}",
							Step1Bean.class);
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);

			step3Bean step3Bean = (step3Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step3Bean}",
							step3Bean.class);

			settings = step3Bean.getSettings();
			proposedRegistryTerm = new RegistryTerm();
			proposedRegistryTerm.setDefaultValue(" ");
			proposedRegistryTerm.setDescription(" ");
			proposedRegistryTerm.setName(" ");
			proposedRegistryTerm.setType(" ");
			proposedRegistryTerm.setValueSpace(" ");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

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

	public List<String> getRegistryItemsList() {
		return registryItemsList;
	}

	public void setRegistryItemsList(List<String> registryItemsList) {
		this.registryItemsList = registryItemsList;
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}

	public Setting getSelectedSetting() {

		if (selectedSetting != null) {

			return selectedSetting;
		} else
			return new Setting();
	}

	public void setSelectedSetting(Setting selectedSetting) {
		this.selectedSetting = selectedSetting;

	}

	public String getNewRegistryTerm() {
		return newRegistryTerm;
	}

	public void setNewRegistryTerm(String newRegistryTerm) {
		this.newRegistryTerm = newRegistryTerm;
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
			step3Bean step3Bean = (step3Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step3Bean}",
							step3Bean.class);

			Step4Bean step4Bean = (Step4Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step4Bean}",
							Step4Bean.class);
			UserBean userBean = (UserBean) context.getApplication()
					.evaluateExpressionGet(context, "#{userBean}",
							UserBean.class);

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
			} else {
				// find proposed category
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
			sol.setSettings(step4Bean.getSettings());
			sol.setId(step2Bean.getId());
			sol.setConstraints(step2Bean.getConstraints());

			sol.setEtnaProperties(ontologyInstance.getEtnaCluster());
			// new view
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

			sol.setManufacturerCountry(step2Bean.getManufacturerCountry());
			sol.setManufacturerName(step2Bean.getManufacturerName());
			sol.setManufacturerWebsite(step2Bean.getManufacturerWebsite());
			sol.setDate(step2Bean.getDate());
			sol.setUpdateDate(step2Bean.getUpdateDate());
			sol.setImageUrl(step2Bean.getImageUrl());
			sol.setDownloadPage(step2Bean.getDownloadPage());

			for (int i = 0; i < step2Bean.getProposedClusters().size(); i++) {
				for (int j = 0; j < step2Bean.getProposedClusters().get(i)
						.getProperties().size(); j++) {
					if (userBean.isAdmin()) {
						step2Bean.getProposedClusters().get(i).getProperties()
								.get(j)
								.setProponentEmail("test_admin@cloud4all.org");
						step2Bean.getProposedClusters().get(i).getProperties()
								.get(j).setProponentName("Administrator");
					} else {
						step2Bean
								.getProposedClusters()
								.get(i)
								.getProperties()
								.get(j)
								.setProponentEmail(
										userBean.getVendorObj()
												.getContactDetails());
						step2Bean
								.getProposedClusters()
								.get(i)
								.getProperties()
								.get(j)
								.setProponentName(
										userBean.getVendorObj().getVendorName());
					}
				}
			}
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

			// update allSolutionsString, allProposalsString for proposed
			// clusters
			// and proposed cluster items
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
			ontologyInstance.getProposedEASTINPropertiesOriginal().addAll(
					step2Bean.getProposedClusters());

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

			String uri = sol.getName().trim().replace(" ", "_")
					.replaceAll("[^\\p{L}\\p{Nd}]", "");

			// check if uri exists
			if (Character.isDigit(uri.charAt(0))) {
				uri = "app";
			}
			uri = ontologyInstance.getOntology().changeUri(uri);
			solutionURI = uri;
			sol.setOntologyURI(solutionURI);

			for (int i = 0; i < sol.getProposedClusters().size(); i++) {
				ETNACluster cl = sol.getProposedClusters().get(i);
				for (int j = 0; j < cl.getProperties().size(); j++) {
					cl.getProperties().get(j).setRefersToSolution(solutionURI);
				}
			}
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

	public void onRowSelect(SelectEvent event) {
		proposedRegistryTerm = new RegistryTerm();
	}

	public void addProposedTerm() {
		// check if values are empty
		if (this.proposedRegistryTerm.getName().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term name", ""));

		}
		if (this.proposedRegistryTerm.getDescription().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term description", ""));

		}
		if (this.proposedRegistryTerm.getValueSpace().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term value space", ""));

		}
		if (this.proposedRegistryTerm.getDefaultValue().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term default value", ""));

		}
		if (this.proposedRegistryTerm.getName().trim().equals("")
				|| this.proposedRegistryTerm.getDescription().trim().equals("")
				|| this.proposedRegistryTerm.getValueSpace().trim().equals("")
				|| this.proposedRegistryTerm.getDefaultValue().trim()
						.equals("")) {
			return;
		}

		// check if default value matches with type
		if (this.proposedRegistryTerm.getType().equals("integer")) {
			try {
				Integer.parseInt(this.proposedRegistryTerm.getDefaultValue()
						.trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ this.proposedRegistryTerm.getDefaultValue()
										.trim() + "\" is not of type \""
								+ this.proposedRegistryTerm.getType() + "\"",
						""));
				return;
			}
		} else if (this.proposedRegistryTerm.getType().equals("boolean")) {
			try {
				if (!this.proposedRegistryTerm.getDefaultValue().trim()
						.toLowerCase().equals("true")
						&& !this.proposedRegistryTerm.getDefaultValue().trim()
								.toLowerCase().equals("false")) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage("messages2", new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "The default value \""
									+ this.proposedRegistryTerm
											.getDefaultValue().trim()
									+ "\" is not of type \""
									+ this.proposedRegistryTerm.getType()
									+ "\"", ""));
					return;
				}

			} catch (Exception ex) {

			}
		} else if (this.proposedRegistryTerm.getType().equals("double")) {
			try {
				Double.parseDouble(this.proposedRegistryTerm.getDefaultValue()
						.trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "The default value \""
								+ this.proposedRegistryTerm.getDefaultValue()
										.trim() + "\" is not of type \""
								+ this.proposedRegistryTerm.getType() + "\"",
						""));
				return;
			}
		}

		// get all registry terms
		List<RegistryTerm> list1 = ontologyInstance.getRegistryTerms();
		List<RegistryTerm> list2 = ontologyInstance.getProposedRegistryTerms();

		for (int i = 0; i < list1.size(); i++) {
			if (list1.get(i).getId().trim()
					.equalsIgnoreCase(proposedRegistryTerm.getId().trim())
					&& !list1.get(i).getOntologyURI()
							.equals(proposedRegistryTerm.getOntologyURI())) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term name already exists", ""));

				return;
			}
		}
		for (int i = 0; i < list2.size(); i++) {
			if (list2.get(i).getId().trim()
					.equalsIgnoreCase(proposedRegistryTerm.getId().trim())
					&& !list2.get(i).getOntologyURI()
							.equals(proposedRegistryTerm.getOntologyURI())) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term name already exists", ""));

				return;
			}
		}

		if (selectedSetting != null) {
			// ontologyInstance.getOntology().saveRegistryTerm(
			// proposedRegistryTerm,"ProposedRegistryTerms");
			ontologyInstance.getProposedRegistryTerms().add(
					proposedRegistryTerm);
			selectedSetting.getSortedMappings().add(0,
					proposedRegistryTerm.getName());
			selectedSetting.setMapping(proposedRegistryTerm.getName());
			selectedSetting.setHasMapping(true);
			// ontology.addIndividual("ProposedRegistryTerms",
			// selectedSetting.getMapping());
		}
		// add new registry term to all settings
		for (int i = 0; i < settings.size(); i++) {
			Setting set = settings.get(i);
			if (set != selectedSetting) {
				set.getSortedMappings().add(proposedRegistryTerm.getName());
			}
		}

		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("settingDialog2.hide()");
	}

	public void test() {
		dataTable.reset();
		FacesContext context = FacesContext.getCurrentInstance();
		step3Bean step3Bean = (step3Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step3Bean}", step3Bean.class);

		step3Bean.setNewSetting(new Setting());

	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public RegistryTerm getProposedRegistryTerm() {

		if (proposedRegistryTerm != null)
			return proposedRegistryTerm;
		else
			return new RegistryTerm();
	}

	public void setProposedRegistryTerm(RegistryTerm proposedRegistryTerm) {
		this.proposedRegistryTerm = proposedRegistryTerm;
	}

	public void mpee(ActionEvent actionEvent) {
		if (!selectedSetting.ishasMapping()) {
			selectedSetting.setComments("");
			selectedSetting.setMapping("");
			selectedSetting.setExactMatching(false);
		}
	}

	public void proposeNewSetting(ActionEvent actionEvent) {
		proposedRegistryTerm = new RegistryTerm();
	}

	public void clearComment() {
		if (selectedSetting.isExactMatching()) {
			selectedSetting.setComments("");
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

		step3Bean step3Bean = (step3Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step3Bean}", step3Bean.class);
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
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

		sol.setOntologyURI(solutionURI);
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
		sol.setAccessInfoForUsers(step2bBean.getAccessInfoForUsers());
		sol.setAccessInfoForVendors(step2bBean.getAccessInfoForVendors());

		for (int i = 0; i < sol.getProposedClusters().size(); i++) {
			ETNACluster cl = sol.getProposedClusters().get(i);
			for (int j = 0; j < ontologyInstance.getSolutions().size(); j++) {
				if (ontologyInstance
						.getSolutions()
						.get(j)
						.getOntologyURI()
						.equals(cl.getProperties().get(0).getRefersToSolution())) {
					for (int k = 0; k < cl.getProperties().size(); k++) {
						cl.getProperties()
								.get(k)
								.setRefersToSolutionID(
										ontologyInstance.getSolutions().get(j)
												.getId());
						cl.getProperties()
								.get(k)
								.setRefersToSolutionName(
										ontologyInstance.getSolutions().get(j)
												.getName());
						if (userBean.getVendorObj() != null) {
							cl.getProperties()
									.get(k)
									.setProponentEmail(
											userBean.getVendorObj()
													.getContactDetails());
							cl.getProperties()
									.get(k)
									.setProponentName(
											userBean.getVendorObj()
													.getVendorName());
						}
					}
				}

				if (ontologyInstance
						.getSolutions()
						.get(j)
						.getOntologyURI()
						.equals(cl.getMeasureProperties().get(0)
								.getRefersToSolution())) {
					for (int k = 0; k < cl.getMeasureProperties().size(); k++) {
						cl.getMeasureProperties()
								.get(k)
								.setRefersToSolutionID(
										ontologyInstance.getSolutions().get(j)
												.getId());
						cl.getMeasureProperties()
								.get(k)
								.setRefersToSolutionName(
										ontologyInstance.getSolutions().get(j)
												.getName());
						if (userBean.getVendorObj() != null) {
							cl.getMeasureProperties()
									.get(k)
									.setProponentEmail(
											userBean.getVendorObj()
													.getContactDetails());
							cl.getMeasureProperties()
									.get(k)
									.setProponentName(
											userBean.getVendorObj()
													.getVendorName());
						}
					}
				}
			}

			ontologyInstance.getProposedEASTINProperties().add(cl);
		}

		ontologyInstance.getOntology().getUris().add(solutionURI.toLowerCase());
		ontologyInstance.getClustersToShowInView().get(0)
				.setItemProposalName("");
		ontologyInstance.getClustersToShowInView().get(0)
				.setItemProposalDescription("");
		ontologyInstance.getClustersToShowInView().get(0)
				.setItemProposalType("Attribute");
		ontologyInstance.getClustersToShowInView().get(0)
				.setSelectedProposedItem(null);

		step1Bean.setSelectedNode(null);
		step2Bean.setApplicationDescription("");
		step2Bean.setApplicationName("");
		step2Bean.setCommandForStart("");
		step2Bean.setCommandForStop("");
		step2Bean.setHandlerType("");

		step3Bean.setSettings(new ArrayList<Setting>());
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
		step2Bean.setProposedCluster(new ETNACluster());

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

		// TODO add solution to Unified Listing
		// add solution setting to raising the floor org
		String cookiesUL = WebServicesForTerms
				.loginAndGetCookiesWithCurl(WebServicesForSolutions.signInForUnifiedListingApi);
		// TODO add solution to unified listing
		WebServicesForSolutions.solutionHTTPPostRequest(sol, cookiesUL);

//		String cookies = WebServicesForTerms.loginAndGetCookiesWithCurl();
//		WebServicesForSolutions.solutionHTTPPostRequest(sol, "");
//
//		for (Setting sett : sol.getSettings()) {
//			sett.setSolutionId(sol.getId());
//
//			for (RegistryTerm term : ontologyInstance.getRegistryTerms()) {
//				if (term.getName().equalsIgnoreCase(sett.getMapping())) {
//					sett.setMappingId(term.getId());
//				}
//			}
//
//			if (!sett.getId().isEmpty()) {
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
	}

}
