package org.cloud4All;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.cloud4All.IPR.SolutionAccessInfoForUsers;
import org.cloud4All.IPR.SolutionAccessInfoForVendors;
import org.cloud4All.IPR.Vendor;
import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Element;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyClass;
import org.cloud4All.ontology.OntologyInstance;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.hp.hpl.jena.ontology.OntClass;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.model.User;

@ManagedBean(name = "step1Bean")
@SessionScoped
public class Step1Bean {

	private boolean skip;
    private List<String> categoryEastinItems = new ArrayList<String>();
	private String searchApplicationName;
	private DefaultTreeNode root;
	private DefaultTreeNode parentRoot;
	private TreeNode selectedNode = null;
	private TreeNode selectedParentNode = null;
	private String selectedCategory = "";
	private String selectedParentCategory = "";
	private CommandLink button;
	private boolean formCompleted = true;
	private boolean resultsRendered = false;
	private List<ApplicationCategories> searchResults = new ArrayList<ApplicationCategories>();
	private ApplicationCategories selectedResult = null;
	private String proposedName = "";
	private String proposedDescription = "";
	private String selectedProposedCategory = "";
	private String proposedCategoryParent = "";
	private boolean proposed = false;
	private boolean flag = false;
	private String typeOfSolution = "Software";
	

	public Step1Bean() {
		super();
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			OntologyInstance ontologyInstance = (OntologyInstance) context
					.getApplication().evaluateExpressionGet(context,
							"#{ontologyInstance}", OntologyInstance.class);

			root = new DefaultTreeNode("Root", null);
			root = createOntologyTree(ontologyInstance.getOntology(), root);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void save(ActionEvent actionEvent) {
		// Persist user
	
		FacesMessage msg = new FacesMessage("Successful", "Welcome :");
		FacesContext.getCurrentInstance().addMessage(null, msg);

	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public String onFlowProcess(FlowEvent event) {

		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	public DefaultTreeNode createOntologyTree(Ontology ontology,
			DefaultTreeNode root) {
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		List<OntologyClass> list = ontologyInstance.getSolutionClassesStructured();

		OntologyClass cl = list.get(0);
		root = new DefaultTreeNode(cl.getClassName(), null);

		getTreeNodeOfConcept(cl, root);
		return (DefaultTreeNode) root.getChildren().get(0);
	}

	private DefaultTreeNode getTreeNodeOfConcept(OntologyClass cl, TreeNode root) {
		DefaultTreeNode node = new DefaultTreeNode(cl.getClassName(), root);
		for (int i = 0; i < cl.getChildren().size(); i++) {
			getTreeNodeOfConcept(cl.getChildren().get(i), node);
		}
		return node;

	}

	public TreeNode getRoot() {

		return root;
	}

	public TreeNode getSelectedNode() {

		return selectedNode;

	}

	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null) {
			// clear tree selection
			root.setSelected(false);
			clearSelections(root);

			// set new selection
			this.selectedNode = selectedNode;

			// expand nodes until root
			TreeNode tmp = this.selectedNode.getParent();

			while (tmp != null) {
				tmp.setExpanded(true);
				tmp = tmp.getParent();
			}
			this.selectedNode.setSelected(true);
			if (this.selectedNode != null) {
				formCompleted = false;
				button.setDisabled(false);
			} else {
				formCompleted = true;
				button.setDisabled(true);
			}
			tmp = selectedNode;
			tmp.setSelected(true);
			this.selectedNode = selectedNode;

		}
		// this.selectedNode.setSelected(true);

	}

	public void clearSelections(TreeNode root) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			node.setSelected(false);
			node.setExpanded(false);
			clearSelections(node);
		}
		this.selectedNode = null;
	}

	public void onNodeSelect(NodeSelectEvent event) {
		selectedCategory = (String) selectedNode.getData();
		if (this.selectedNode != null) {
			resultsRendered = false;
			formCompleted = false;
		} else
			formCompleted = true;
	}

	public void onParentNodeSelect(NodeSelectEvent event) {
		selectedParentCategory = (String) event.getTreeNode().getData();
		parentRoot.setSelected(false);
		clearSelections(parentRoot);
		selectItemInTree(parentRoot, selectedParentCategory);
	}

	public void onKeyRelease() {
		if (this.selectedNode != null) {
			formCompleted = false;
		} else
			formCompleted = true;
	}

	public void displaySelectedSingle(ActionEvent event) {
		if (selectedNode != null) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Selected", selectedNode.getData().toString());

			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public String getSearchApplicationName() {
		return searchApplicationName;
	}

	public void setSearchApplicationName(String searchApplicationName) {
		this.searchApplicationName = searchApplicationName;
	}

	public void searchResults() {
		// if (searchApplicationName.trim().equals("")){
		// selectedNode=null;
		// clearSelections(root);
		// selectItemInMainTree(root, selectedCategory);
		//
		// return;
		// }
		root.setSelected(true);
		root.setSelected(false);

		selectedNode = null;
		searchResults = new ArrayList<ApplicationCategories>();
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		SortedMap map = ontologyInstance.getOntology()
				.getSolutionsMatchingConcepts(
						cameliseString(searchApplicationName));

		Map<Double, ApplicationCategories> treeMap = new TreeMap<Double, ApplicationCategories>(
				map);
		List<String> res = new ArrayList<String>();
		Iterator i = treeMap.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			if (((Double) me.getKey()) > 0.2)
				searchResults.add((ApplicationCategories) me.getValue());
		}
		Collections.reverse(searchResults);
		if (searchResults.size() == 1
				&& searchResults.get(0).getName().equals("")) {
			searchResults.remove(0);
		}
		resultsRendered = true;
		clearSelections(root);
		formCompleted = false;
		this.button.setDisabled(true);
		// String match = searchOntologyMatching();
		//
		// List<TreeNode> tree = root.getChildren();
		// for (TreeNode node : tree) {
		// if (node.getData().toString().contains(match)) {
		//
		// setSelectedNode(node);
		// break;
		// } else {
		// TreeNode tmp = searchNodeInChildren(node, match);
		// if (tmp != null) {
		//
		// break;
		// }
		// }
		// }

	}

	private TreeNode searchNodeInChildren(TreeNode root,
			ApplicationCategories match) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			if (node.getData().toString().contains(match.getName())) {

				setSelectedNode(node);

				return node;
			} else {
				TreeNode temp = searchNodeInChildren(node, match);
				if (temp != null)
					return temp;
			}
		}
		return null;
	}

	private void searchOntologyMatching() {

		// String str = cameliseString(searchApplicationName);
		// if (!str.equals("")) {
		// SortedMap map = ontology.getSolutionsMatchingConcepts(str);
		//
		// return ((String) map.get(map.lastKey())).replace(" ", "");
		//
		// } else {
		// return "";
		// }

	}

	private String cameliseString(String str) {
		StringBuffer result = new StringBuffer(str.length());
		String strl = str.toLowerCase();
		boolean bMustCapitalize = false;
		for (int i = 0; i < strl.length(); i++) {
			char c = strl.charAt(i);
			if (c >= 'a' && c <= 'z') {
				if (bMustCapitalize) {
					result.append(strl.substring(i, i + 1).toUpperCase());
					bMustCapitalize = false;
				} else {
					result.append(c);
				}

			} else {
				bMustCapitalize = true;
			}

		}
		return result.toString();
	}

	public CommandLink getButton() {
		return button;
	}

	public void setButton(CommandLink button) {
		this.button = button;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	public boolean isFormCompleted() {
		return formCompleted;
	}

	public void setFormCompleted(boolean formCompleted) {
		this.formCompleted = formCompleted;
	}

	public void test2() {

		FacesContext context = FacesContext.getCurrentInstance();
		Step2bBean step2bBean = (Step2bBean) context.getApplication()
				.evaluateExpressionGet(context, "#{step2bBean}",
						Step2bBean.class);
		step2Bean step2Bean = (step2Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step2Bean}", step2Bean.class);
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		
		if (!selectedCategory.isEmpty()) {
			step2Bean.setSelectedCategory(selectedCategory);
			step2Bean.setCategoryProposed(false);
			step2Bean.setCloneSelectedCategory(selectedCategory);
		} else {
			step2Bean.setSelectedCategory(selectedProposedCategory);
			step2Bean.setCategoryProposed(true);
			step2Bean.setCloneSelectedCategory(selectedProposedCategory);
		}
		
		step2Bean.setNameAdded(false);
		step2Bean.setClustersShowFlag(true);
		step2Bean.setCategoriesList(new ArrayList<String>());
		step2Bean.setApplicationDescription("");
		step2Bean.setApplicationName("");
		step2Bean.setId("");
		step2Bean.setConstraints("");
		step2Bean.setCommandForStart("");
		step2Bean.setCommandForStop("");
		step2Bean.setCapabilities("");
		step2Bean.setCapabilitiesTransformations("");
		step2Bean.setOptions(new ArrayList<Element>());
        step2Bean.setHandlerType("No");
		step2Bean.setManufacturerCountry("");
		step2Bean.setManufacturerName("");
		step2Bean.setManufacturerWebsite("");
		step2Bean.setImageUrl("");
		step2Bean.setDownloadPage("");
		step2Bean.setDate(null);
		step2Bean.setUpdateDate(null);
		step2bBean = new Step2bBean();
		step2bBean.setAccessInfoForUsers(new SolutionAccessInfoForUsers());
		step2bBean.setAccessInfoForVendors(new SolutionAccessInfoForVendors());
		step2bBean.setSelectedCountries(new ArrayList<String>());
		step2Bean.setProposedCluster(new ETNACluster());
		step2Bean.setProposedClusters(new ArrayList<ETNACluster>());
		step2Bean.addItemToCluster();
		step2Bean
				.setProposedClusterItemsMatches(new ArrayList<EASTINProperty>());
		step2Bean.setProposedClusterItems(new ArrayList<EASTINProperty>());
		step2Bean.setProposedMeasureClusterItems(new ArrayList<EASTINProperty>());
//		ontologyInstance.setEtnaCluster(ontologyInstance.getOntology()
//				.getETNATaxonomy());
		List<ETNACluster> cluster=new ArrayList<ETNACluster>();
		for(int i=0;i<ontologyInstance.getEtnaClusterOriginal().size();i++){
			cluster.add(ontologyInstance.getEtnaClusterOriginal().get(i).clone());
		}
		ontologyInstance.setEtnaCluster(cluster);
		step2Bean
				.setActiveETNACluster(ontologyInstance.getEtnaCluster().get(0));
//		List<List<EASTINProperty>> list = (ontologyInstance.getOntology()
//				.getETNATaxonomyProposed());
//		ontologyInstance.setProposedEASTINItems(list.get(0));
//		ontologyInstance.setProposedMeasureEASTINItems(list.get(1));
		
		
		
		ontologyInstance
				.setProposedEASTINItems(new ArrayList<EASTINProperty>());
		for (int i = 0; i < ontologyInstance.getProposedEASTINItemsOriginal()
				.size(); i++) {
			ontologyInstance.getProposedEASTINItems().add(
					ontologyInstance.getProposedEASTINItemsOriginal().get(i));
		}
		ontologyInstance
				.setProposedMeasureEASTINItems(new ArrayList<EASTINProperty>());
		for (int i = 0; i < ontologyInstance
				.getProposedMeasureEASTINItemsOriginal().size(); i++) {
			ontologyInstance.getProposedMeasureEASTINItems().add(
					ontologyInstance.getProposedMeasureEASTINItemsOriginal()
							.get(i));
		}
		
		ontologyInstance.setAllProposedItems(new ArrayList<EASTINProperty>());
		
		EASTINProperty prop = new EASTINProperty();
		prop.setName("");
		if(!ontologyInstance.getAllProposedItems().contains(prop))
			ontologyInstance.getAllProposedItems().add(prop);

		for (EASTINProperty p : ontologyInstance
				.getProposedEASTINItemsOriginal())
			if (!ontologyInstance.getAllProposedItems().contains(p))
				ontologyInstance.getAllProposedItems().add(p);

		for (EASTINProperty p : ontologyInstance
				.getProposedMeasureEASTINItemsOriginal())
			if (!ontologyInstance.getAllProposedItems().contains(p))
				ontologyInstance.getAllProposedItems().add(p);

		for (int i = ontologyInstance.getProposedEASTINProperties().size() - 1; i >= 0; i--) {
			boolean found = false;
			for (int j = 0; j < ontologyInstance
					.getProposedEASTINPropertiesOriginal().size(); j++) {
				if (ontologyInstance
						.getProposedEASTINProperties()
						.get(i)
						.getName()
						.equals(ontologyInstance
								.getProposedEASTINPropertiesOriginal().get(j)
								.getName())) {
					found = true;
				}
			}
			if (!found) {
				ontologyInstance.getProposedEASTINProperties().remove(i);
			}
		}
		
		step2Bean.setActiveTabIndex("-1");
		prepareETNAClustersAccordingToCategory(ontologyInstance);
		
		
	}

	public void test() {
		try {

			FacesContext context = FacesContext.getCurrentInstance();
			Step1Bean step1Bean = (Step1Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step1Bean}",
							Step1Bean.class);
			FacesContext context2 = FacesContext.getCurrentInstance();
			OntologyInstance ontologyInstance = (OntologyInstance) context2
					.getApplication().evaluateExpressionGet(context2,
							"#{ontologyInstance}", OntologyInstance.class);
			step2Bean step2Bean = (step2Bean) context2
					.getApplication().evaluateExpressionGet(context2,
							"#{step2Bean}", step2Bean.class);
			step1Bean.getRoot().setSelected(true);
			step1Bean.getRoot().setSelected(false);
			step1Bean.setSearchApplicationName("");
			step1Bean.setProposedDescription("");
			step1Bean.setSelectedNode(null);
			step1Bean.setProposedName("");
			step1Bean.setSelectedProposedCategory("");
			step1Bean.setTypeOfSolution("Software");
			// ontologyInstance.setProposedApplicationCategories(ontologyInstance
			// .getOntology().loadAllProposedApplicationCategories());
//			for (int i = ontologyInstance.getProposedApplicationCategories()
//					.size() - 1; i >= 0; i--) {
//				boolean found = false;
//				for (int j = 0; j < ontologyInstance
//						.getProposedApplicationCategoriesOriginal().size(); j++) {
//					if (ontologyInstance
//							.getProposedApplicationCategories()
//							.get(i)
//							.getName()
//							.equals(ontologyInstance
//									.getProposedApplicationCategoriesOriginal()
//									.get(j).getName())) {
//						found = true;
//					}
//				}
//				if (!found) {
//					ontologyInstance.getProposedApplicationCategories().remove(
//							i);
//				}
//			}

			step1Bean.setResultsRendered(false);
			step1Bean.setFormCompleted(true);
			if (step1Bean.getButton() != null)
				step1Bean.getButton().setDisabled(true);

			ontologyInstance
					.setEASTINPropertiesMap(new LinkedHashMap<String, EASTINProperty>());

			for (int i = 0; i < ontologyInstance.getProposedEASTINItems()
					.size(); i++) {
				ontologyInstance.getEASTINPropertiesMap().put(
						ontologyInstance.getProposedEASTINItems().get(i)
								.getName(),
						ontologyInstance.getProposedEASTINItems().get(i));
			}
			
			for (int i = 0; i < ontologyInstance.getProposedMeasureEASTINItems()
					.size(); i++) {
				ontologyInstance.getEASTINPropertiesMap().put(
						ontologyInstance.getProposedMeasureEASTINItems().get(i)
								.getName(),
						ontologyInstance.getProposedMeasureEASTINItems().get(i));
			}
			
			
			
			
			
			for (int i = ontologyInstance.getProposedEASTINProperties().size() - 1; i >= 0; i--) {
				boolean found = false;
				for (int j = 0; j < ontologyInstance
						.getProposedEASTINPropertiesOriginal().size(); j++) {
					if (ontologyInstance
							.getProposedEASTINProperties()
							.get(i)
							.getName()
							.equals(ontologyInstance
									.getProposedEASTINPropertiesOriginal()
									.get(j).getName())) {
						found = true;
					}
				}
				if (!found) {
					ontologyInstance.getProposedEASTINProperties().remove(i);
				}
			}
			
			ETNACluster clus = new ETNACluster();
			if ( ontologyInstance.getProposedEASTINProperties().size()==0)
				ontologyInstance.getProposedEASTINProperties().add(0,
						clus);
			
			if(ontologyInstance.getProposedEASTINProperties().size()>0)
				if(!ontologyInstance.getProposedEASTINProperties().get(0)
					.getName().isEmpty())
					ontologyInstance.getProposedEASTINProperties().add(0,
							clus);
				
			// ETNACluster cluster1 = new ETNACluster();
			// cluster1.setName("");
			// ontologyInstance.getProposedEASTINProperties().add(0,cluster1);
			// ontologyInstance.setProposedEASTINProperties(ontologyInstance
			// .getOntology().loadAllProposedEASTINProperties());

			for (int i = 0; i < ontologyInstance.getProposedEASTINProperties()
					.size(); i++) {
				ETNACluster cl = ontologyInstance.getProposedEASTINProperties()
						.get(i);
				for (int j = 0; j < ontologyInstance.getSolutions().size(); j++) {
					if (cl.getProperties().size() != 0
							&& ontologyInstance
									.getSolutions()
									.get(j)
									.getOntologyURI()
									.equals(cl.getProperties().get(0)
											.getRefersToSolution())) {
						for (int kk = 0; kk < cl.getProperties().size(); kk++) {
							cl.getProperties()
									.get(kk)
									.setRefersToSolutionID(
											ontologyInstance.getSolutions()
													.get(j).getId());
							cl.getProperties()
									.get(kk)
									.setRefersToSolutionName(
											ontologyInstance.getSolutions()
													.get(j).getName());
							for (int ll = 0; ll < ontologyInstance.getVendors()
									.size(); ll++) {
								if (ontologyInstance
										.getVendors()
										.get(ll)
										.getContactDetails()
										.equals(cl.getProperties().get(kk)
												.getProponentEmail())) {
									cl.getProperties()
											.get(kk)
											.setProponentName(
													ontologyInstance
															.getVendors()
															.get(ll)
															.getVendorName());
								}
							}

						}
					}
				}

			}
			ontologyInstance
					.setClusterPropertiesMap(new LinkedHashMap<String, ETNACluster>());
			for (int i = 0; i < ontologyInstance.getProposedEASTINProperties()
					.size(); i++) {
				ontologyInstance.getClusterPropertiesMap().put(
						ontologyInstance.getProposedEASTINProperties().get(i)
								.getName(),
						ontologyInstance.getProposedEASTINProperties().get(i));
			}

			root = new DefaultTreeNode("Root", null);
			ontologyInstance.setSolutionClassesStructured(ontologyInstance
					.getOntology().getSolutionsClassesStructured());
			root = createOntologyTree(ontologyInstance.getOntology(), root);
			proposedCategoryParent = "";
			selectedCategory = "";
			this.proposed = false;
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	public void prepareETNAClustersAccordingToCategory(
			OntologyInstance ontologyInstance) {
		// define shown and hidden items for clusters

		String categoryName = "";
		if (!this.selectedCategory.isEmpty()) {
			categoryName = this.selectedCategory;
		}

		categoryEastinItems = new ArrayList<String>();
		flag = false;
		// get a list with the preferred iso ids (items) for the
		// selected category, if the category is proposed or has an empty list
		// takes all clusters and items in step 2
		if (!this.proposed) {
			for (OntologyClass temp : ontologyInstance
					.getSolutionClassesStructured()) {
				if (temp.getClassName().equals(categoryName)) {
					categoryEastinItems = temp.getEastinItems();
					break;
				} else {
					for (OntologyClass temp2 : temp.getChildren()) {
						if (temp2.getClassName().equals(categoryName)) {
							categoryEastinItems = temp.getEastinItems();
							break;
						} else {
							findSelectedCategoryThroughTree(temp2, categoryName);
						}

						if (flag)
							break;
					}
				}

			}
		}

		// define shown and hidden clusters, shown and hidden attributes and
		// measures
		ontologyInstance.setShownClusters(new ArrayList<ETNACluster>());
		ontologyInstance.setHiddenClusters(new ArrayList<ETNACluster>());
		ontologyInstance.setClustersToShowInView(new ArrayList<ETNACluster>());

		if (categoryEastinItems.size() > 0) {

			prepareClustersListWithFilteringAndISOPreferences(ontologyInstance);

		} else {

			prepareClustersListWithFlitering(ontologyInstance);

		}
		
		// set showItems flag for clusters
		List<List<ETNACluster>> superList = new ArrayList<List<ETNACluster>>();
		List<List<ETNACluster>> tempSuperList = new ArrayList<List<ETNACluster>>();

		// add lists of clusters to superList
		superList.add(ontologyInstance.getClustersToShowInView());
		superList.add(ontologyInstance.getShownClusters());
		superList.add(ontologyInstance.getHiddenClusters());

		tempSuperList = Utilities.setShowItemsFlagsForClusters(superList);

		ontologyInstance.setClustersToShowInView(tempSuperList.get(0));
		ontologyInstance.setShownClusters(tempSuperList.get(1));
		ontologyInstance.setHiddenClusters(tempSuperList.get(2));	
		
	}
	
	public void prepareClustersListWithFlitering(
			OntologyInstance ontologyInstance) {
		ArrayList<ETNACluster> cloneList = new ArrayList<ETNACluster>();
		for (ETNACluster cl : ontologyInstance.getEtnaClusterOriginal()) {
			ontologyInstance.getShownClusters().add(cl.clone());
			ontologyInstance.getClustersToShowInView().add(cl.clone());
			cloneList.add(cl.clone());
		}

		for (ETNACluster cl : cloneList) {

			// check attributes for the related type (both, hardware,
			// software)
			for (EASTINProperty prop : cl.getAttributesToShowInView()) {
				if (!prop.getRelatedToTypeOfApplication().equalsIgnoreCase(
						typeOfSolution)
						&& !prop.getRelatedToTypeOfApplication().equals("Both")) {
					cl.getAttributesToShow().remove(prop);
					cl.getAttributesToHide().add(prop.clone());
				}
			}

			cl.setAttributesToShowInView(new ArrayList<EASTINProperty>());
			for (EASTINProperty prop : cl.getAttributesToShow()) {
				cl.getAttributesToShowInView().add(prop.clone());
			}

			// check measures for the related type
			for (EASTINProperty prop : cl.getMeasuresToShowInView()) {
				if (!prop.getRelatedToTypeOfApplication().equalsIgnoreCase(
						typeOfSolution)
						&& !prop.getRelatedToTypeOfApplication().equals("Both")) {
					cl.getMeasuresToShow().remove(prop);
					cl.getMeasuresToHide().add(prop.clone());
				}
			}

			cl.setMeasuresToShowInView(new ArrayList<EASTINProperty>());
			for (EASTINProperty prop : cl.getMeasuresToShow()) {
				cl.getMeasuresToShowInView().add(prop.clone());
			}

			if (cl.getAttributesToShowInView().size() == 0
					&& cl.getMeasuresToShowInView().size() == 0) {

				ontologyInstance.getShownClusters().remove(cl);

				for (EASTINProperty prop : cl.getAllproperties()) {
					if (prop.getType().equalsIgnoreCase("attribute")) {
						cl.getAttributesToShow().add(prop.clone());
						cl.getAttributesToShowInView().add(prop.clone());
					} else {
						cl.getMeasuresToShow().add(prop.clone());
						cl.getMeasuresToShowInView().add(prop.clone());
					}
				}
				cl.setAttributesToHide(new ArrayList<EASTINProperty>());
				cl.setMeasuresToHide(new ArrayList<EASTINProperty>());

				ontologyInstance.getHiddenClusters().add(cl.clone());
			}
		}

		ontologyInstance.setClustersToShowInView(new ArrayList<ETNACluster>());
		for (ETNACluster cl : cloneList) {
			if (ontologyInstance.getShownClusters().contains(cl)) {
				ontologyInstance.getClustersToShowInView().add(cl.clone());
				int pos = ontologyInstance.getShownClusters().indexOf(cl);
				ontologyInstance.getShownClusters().remove(pos);
				ontologyInstance.getShownClusters().add(cl.clone());
			}
		}

	}
	
	public void prepareClustersListWithFilteringAndISOPreferences(
			OntologyInstance ontologyInstance) {
		for (ETNACluster cluster : ontologyInstance.getEtnaCluster()) {

			ETNACluster cl = cluster.clone();

			cl.setAttributesToHide(new ArrayList<EASTINProperty>());
			cl.setAttributesToShow(new ArrayList<EASTINProperty>());
			cl.setMeasuresToShow(new ArrayList<EASTINProperty>());
			cl.setMeasuresToHide(new ArrayList<EASTINProperty>());
			cl.setAttributesToShowInView(new ArrayList<EASTINProperty>());
			cl.setMeasuresToShowInView(new ArrayList<EASTINProperty>());

			// check items according to categoryEastinItems
			for (EASTINProperty prop : cl.getProperties()) {
				if (categoryEastinItems.contains(prop.getId())){
					if (prop.getRelatedToTypeOfApplication().equalsIgnoreCase(
							typeOfSolution)
							|| prop.getRelatedToTypeOfApplication().equals(
									"Both"))
						cl.getAttributesToShow().add(prop.clone());
					else
						if (!cl.getAttributesToHide().contains(prop))
							cl.getAttributesToHide().add(prop.clone());
					
				}else{
					if (!cl.getAttributesToHide().contains(prop))
						cl.getAttributesToHide().add(prop.clone());
				}
			}

			for (EASTINProperty prop : cl.getMeasureProperties()) {
				if (categoryEastinItems.contains(prop.getId())) {
					if (prop.getRelatedToTypeOfApplication().equalsIgnoreCase(
							typeOfSolution)
							|| prop.getRelatedToTypeOfApplication().equals(
									"Both"))
						cl.getMeasuresToShow().add(prop.clone());
					else if (!cl.getMeasuresToHide().contains(prop))
						cl.getMeasuresToHide().add(prop.clone());

				} else {
					if (!cl.getMeasuresToHide().contains(prop))
						cl.getMeasuresToHide().add(prop.clone());
				}
			}

			// define lists that will be shown in the view
			for (EASTINProperty prop : cl.getAttributesToShow()) {
				if (!cl.getAttributesToShowInView().contains(prop))
					cl.getAttributesToShowInView().add(prop.clone());
			}

			for (EASTINProperty prop : cl.getMeasuresToShow()) {
				if (!cl.getMeasuresToShowInView().contains(prop))
					cl.getMeasuresToShowInView().add(prop.clone());
			}
		
			// define if the cluster will be shown or not
			if (cl.getAttributesToShow().size() > 0
					|| cl.getMeasuresToShow().size() > 0) {
				ontologyInstance.getShownClusters().add(cl.clone());
				ontologyInstance.getClustersToShowInView().add(cl.clone());
			}

			else {

				// add items to lists for view, in order to show all items
				// in the expansion of all clusters
				for (EASTINProperty prop : cl.getAttributesToHide()) {
					if (!cl.getAttributesToShowInView().contains(prop)) {
						cl.getAttributesToShowInView().add(prop.clone());
						cl.getAttributesToShow().add(prop.clone());
					}
				}

				for (EASTINProperty prop : cl.getMeasuresToHide()) {
					if (!cl.getMeasuresToShowInView().contains(prop)) {
						cl.getMeasuresToShowInView().add(prop.clone());
						cl.getMeasuresToShow().add(prop.clone());
					}
				}

				cl.setMeasuresToHide(new ArrayList<EASTINProperty>());
				cl.setAttributesToHide(new ArrayList<EASTINProperty>());

				ontologyInstance.getHiddenClusters().add(cl.clone());
			}

		}
	}
	
	//recursive method, find category name in the tree with the solution categories
	public void findSelectedCategoryThroughTree(OntologyClass ont,
			String categoryName) {
		
		for (OntologyClass temp2 : ont.getChildren()) {
			if (temp2.getClassName().equals(categoryName)) {
				flag = true;
				categoryEastinItems = temp2.getEastinItems();
			} else {
				findSelectedCategoryThroughTree(temp2, categoryName);
			}
			
			if(flag)
				break;
		}
		
		if(flag)
			return;
		
	}

	/**
	 * @return the resultsRendered
	 */
	public boolean isResultsRendered() {
		return resultsRendered;
	}

	/**
	 * @param resultsRendered
	 *            the resultsRendered to set
	 */
	public void setResultsRendered(boolean resultsRendered) {
		this.resultsRendered = resultsRendered;
	}

	/**
	 * @return the searchResults
	 */
	public List<ApplicationCategories> getSearchResults() {
		return searchResults;
	}

	/**
	 * @param searchResults
	 *            the searchResults to set
	 */
	public void setSearchResults(List<ApplicationCategories> searchResults) {
		this.searchResults = searchResults;
	}

	public void onRowSelect() {
		formCompleted = true;
		this.selectedProposedCategory = "";
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			if (selectedResult != null)
				if (node.getData().toString()
						.contains(selectedResult.getName())) {

					setSelectedNode(node);
					selectedCategory = node.getData().toString();
					formCompleted = false;
					break;
				} else {
					TreeNode tmp = searchNodeInChildren(node, selectedResult);
					if (tmp != null) {
						selectedCategory = tmp.getData().toString();
						formCompleted = false;
						break;
					}
				}
		}
	}

	/**
	 * @return the selectedResult
	 */
	public ApplicationCategories getSelectedResult() {
		return selectedResult;
	}

	/**
	 * @param selectedResult
	 *            the selectedResult to set
	 */
	public void setSelectedResult(ApplicationCategories selectedResult) {
		this.selectedResult = selectedResult;
	}

	public String getProposedName() {
		return proposedName;
	}

	public void setProposedName(String proposedName) {
		this.proposedName = proposedName;
	}

	public String getProposedDescription() {
		return proposedDescription;
	}

	public void setProposedDescription(String proposedDescription) {
		this.proposedDescription = proposedDescription;
	}

	public void submitProposal() {

		FacesContext context2 = FacesContext.getCurrentInstance();

		OntologyInstance ontologyInstance = (OntologyInstance) context2
				.getApplication().evaluateExpressionGet(context2,
						"#{ontologyInstance}", OntologyInstance.class);
		UserBean userBean = (UserBean) context2.getApplication()
				.evaluateExpressionGet(context2, "#{userBean}", UserBean.class);

		// get user's information
		Vendor vendor = userBean.getVendorObj();
		if (this.proposedName.trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the proposed category name", ""));
			return;
		}
		if (this.proposedDescription.trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the proposed category description", ""));
			return;
		}
		// check if proposal name already exists
		for (int i = 0; i < ontologyInstance.getProposedApplicationCategories()
				.size(); i++) {
			if (ontologyInstance.getProposedApplicationCategories().get(i)
					.getName().trim().toLowerCase()
					.equals(this.proposedName.trim().toLowerCase())) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This proposed category name already exists", ""));
				return;
			}
		}
		if (this.proposedName.trim().toLowerCase().equals("Solutions")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"This category name already exists", ""));
			return;
		}
		List<ApplicationCategories> list = ontologyInstance.getOntology()
				.getAllSolutionChildren();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().trim().toLowerCase()
					.equals(this.proposedName.trim().toLowerCase())) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This category name already exists", ""));
				return;
			}
		}

		ProposedApplicationCategory prop = new ProposedApplicationCategory();
		// set proponent name and email for solution
		if (vendor != null) {
			prop.setProponentName(vendor.getVendorName());
			prop.setProponentEmail(vendor.getContactDetails());
		} else {
			prop.setProponentName("Administrator");
			prop.setProponentEmail("test_admin@cloud4all.org");
		}
		prop.setName(proposedName);
		prop.setDescription(proposedDescription);
		if (proposedCategoryParent.equals("")) {
			prop.setParent("Solutions");
		} else
			prop.setParent(proposedCategoryParent);
		ontologyInstance.getProposedApplicationCategories().add(prop);
		selectedProposedCategory = proposedName;
		
		this.clearSelections(root);
		this.selectedResult = null;
		this.formCompleted = false;
		this.button.setDisabled(false);
		this.proposed = true;

		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("proposalSubmittedDialog.show()");
	
	}

	public String getSelectedProposedCategory() {
		return selectedProposedCategory;
	}

	public void setSelectedProposedCategory(String selectedProposedCategory) {
		this.selectedProposedCategory = selectedProposedCategory;

	}

	public void proposedCategoryChangedListener() {

		if (selectedProposedCategory != null
				&& !selectedProposedCategory.equals("")) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			OntologyInstance ontologyInstance = (OntologyInstance) facesContext
					.getApplication().evaluateExpressionGet(facesContext,
							"#{ontologyInstance}", OntologyInstance.class);

			int index = -1;

			for (int i = 0; i < ontologyInstance
					.getProposedApplicationCategories().size(); i++) {
				if (ontologyInstance
						.getProposedApplicationCategories()
						.get(i)
						.getName()
						.trim()
						.toLowerCase()
						.equals(this.selectedProposedCategory.trim()
								.toLowerCase())) {
					index = i;
					break;
				}
			}

			if (index >= 0) {
				proposedName = ontologyInstance
						.getProposedApplicationCategories().get(index)
						.getName();
				proposedDescription = ontologyInstance
						.getProposedApplicationCategories().get(index)
						.getDescription();
				proposedCategoryParent = ontologyInstance
						.getProposedApplicationCategories().get(index)
						.getParent();
				this.proposed = true;
			} else {
				proposedName = "";
				proposedDescription = "";
				proposedCategoryParent = "";
				this.proposed = false;
			}

			this.clearSelections(root);
			this.selectedResult = null;
			this.formCompleted = false; // false enables next button
			this.button.setDisabled(false);
		} else {
			proposedName = "";
			proposedDescription = "";
			proposedCategoryParent = "";
			this.formCompleted = true; // true disables next button
			this.button.setDisabled(true);
		    this.proposed = false;
		}

	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public DefaultTreeNode getParentRoot() {
		return parentRoot;
	}

	public void setParentRoot(DefaultTreeNode parentRoot) {
		this.parentRoot = parentRoot;
	}

	public TreeNode getSelectedParentNode() {
		return selectedParentNode;
	}

	public void setSelectedParentNode(TreeNode selectedParentNode) {
		this.selectedParentNode = selectedParentNode;
	}

	public String getSelectedParentCategory() {
		return selectedParentCategory;
	}

	public void setSelectedParentCategory(String selectedParentCategory) {
		this.selectedParentCategory = selectedParentCategory;
	}

	public void createParentOntologyTree() {
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);

		parentRoot = createOntologyTree(ontologyInstance.getOntology(),
				parentRoot);
		selectedProposedCategory = "";
		parentRoot.setSelected(false);
		clearSelections(parentRoot);
		selectedParentNode = null;
		selectItemInTree(parentRoot, proposedCategoryParent);

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
			// root.getParent().setExpanded(true);
			expandallParents(root);
			selectedParentNode = root;
			return;
		} else
			for (int i = 0; i < root.getChildCount(); i++) {
				selectItemInTree(root.getChildren().get(i), nodeName);
			}
	}

	public void selectItemInMainTree(TreeNode root, String nodeName) {
		root.setSelected(false);
		if (root.getData().toString().equals(nodeName)) {
			root.setSelected(true);
			// root.getParent().setExpanded(true);
			expandallParents(root);
			selectedNode = root;
			root.setExpanded(false);
		} else
			for (int i = 0; i < root.getChildCount(); i++) {
				selectItemInMainTree(root.getChildren().get(i), nodeName);
			}
	}

	public String getProposedCategoryParent() {
		return proposedCategoryParent;
	}

	public void setProposedCategoryParent(String proposedCategoryParent) {
		this.proposedCategoryParent = proposedCategoryParent;
	}

	public void acceptParentCategory() {
		proposedCategoryParent = selectedParentCategory;
	}

	public void resetSelection() {
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		parentRoot = createOntologyTree(ontologyInstance.getOntology(),
				parentRoot);
		selectedProposedCategory = "";
		selectedParentNode = null;
		clearSelections(parentRoot);
		selectItemInTree(parentRoot, proposedCategoryParent);

	}

	public boolean isProposed() {
		return proposed;
	}

	public void setProposed(boolean proposed) {
		this.proposed = proposed;
	}

	public List<String> getCategoryEastinItems() {
		return categoryEastinItems;
	}

	public void setCategoryEastinItems(List<String> categoryEastinItems) {
		this.categoryEastinItems = categoryEastinItems;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getTypeOfSolution() {
		return typeOfSolution;
	}

	public void setTypeOfSolution(String typeOfSolution) {
		this.typeOfSolution = typeOfSolution;
	}
	
	

}
