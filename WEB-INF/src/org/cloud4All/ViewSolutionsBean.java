package org.cloud4All;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.cloud4All.IPR.DiscountSchema;
import org.cloud4All.IPR.Vendor;
import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Element;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyClass;
import org.cloud4All.ontology.OntologyInstance;
import org.cloud4All.ontology.RegistryTerm;
import org.cloud4All.ontology.Utils;
import org.cloud4All.utils.StringComparison;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "viewSolutionsBean")
@SessionScoped
public class ViewSolutionsBean {

	private DefaultTreeNode root;
	private TreeNode selectedNode = null;
	private List<Solution> solutions = new ArrayList<Solution>();
	private Solution selectedSolution = new Solution();
	private Solution clonedSelectedSolution = new Solution();
	private boolean butDisabled = true;
	private String searchApplicationName;
	private String searchbyApplicationName;
	private Setting selectedSetting = new Setting();
	private Setting clonedSelectedSetting = new Setting();
	private RegistryTerm proposedRegistryTerm = new RegistryTerm();
	private boolean showSolutionsPanel = false;
	private boolean formCompleted = true;
	private boolean disableImportButton = true;
	private OntologyInstance ontologyInstance;
	private ETNACluster proposedCluster = new ETNACluster();
	private List<ETNACluster> proposedClusters = new ArrayList<ETNACluster>();
	private ETNACluster activeETNACluster = new ETNACluster();
	private List<EASTINProperty> proposedClusterItemsMatches = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> proposedClusterItems = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> proposedMeasureClusterItems = new ArrayList<EASTINProperty>();
	private ETNACluster selectedProposedCluster = new ETNACluster();
	private EASTINProperty proposedItemToRemove = new EASTINProperty();
	private EASTINProperty relevantItem = new EASTINProperty();
	private String type = "";
	private boolean proposedClusterFlag = false;
	private Element elementForRemove = new Element("", "");
	private boolean clustersShowFlag = true;

	public ViewSolutionsBean() {
		super();

		FacesContext context2 = FacesContext.getCurrentInstance();
		ontologyInstance = (OntologyInstance) context2.getApplication()
				.evaluateExpressionGet(context2, "#{ontologyInstance}",
						OntologyInstance.class);
		createOntologyTree(ontologyInstance.getOntology());
		root = (DefaultTreeNode) root.getChildren().get(0);
		proposedClusterFlag = false;

	}

	private void createOntologyTree(Ontology ontology) {
		// List<OntologyClass> list = ontology.getSolutionsClassesStructured();
		List<OntologyClass> list = ontologyInstance
				.getSolutionClassesStructured();
		OntologyClass cl = list.get(0);
		root = new DefaultTreeNode(cl.getClassName(), null);

		getTreeNodeOfConcept(cl, root);

	}

	private DefaultTreeNode getTreeNodeOfConcept(OntologyClass cl, TreeNode root) {

		DefaultTreeNode node = new DefaultTreeNode(cl.getClassName(), root);
		for (int i = 0; i < cl.getChildren().size(); i++) {
			getTreeNodeOfConcept(cl.getChildren().get(i), node);
		}
		return node;

	}

	public void onNodeSelect() {

		solutions = new ArrayList<Solution>();

		// get List with all children classes
		List<String> nodeChildren = getAllChildren(selectedNode);

		for (int j = 0; j < ontologyInstance.getSolutions().size(); j++) {
			if (nodeChildren.contains(ontologyInstance.getSolutions().get(j)
					.getOntologyCategory()))
				solutions.add(ontologyInstance.getSolutions().get(j));
		}

		butDisabled = true;
		showSolutionsPanel = true;

	}

	private List<String> getAllChildren(TreeNode node) {
		List<String> res = new ArrayList<String>();
		if (node != null) {
			res.add(node.getData().toString());
			for (int i = 0; i < node.getChildCount(); i++) {
				List<String> tmp = getAllChildren(node.getChildren().get(i));
				res.addAll(tmp);
			}
		}
		return res;

	}

	public List<String> getChildrenOfConcept(TreeNode root) {
		List<String> res = new ArrayList<String>();
		res.add(root.getData().toString());
		for (int i = 0; i < root.getChildCount(); i++) {
			List<String> tmp = getChildrenOfConcept(root.getChildren().get(i));
			res.addAll(tmp);
		}
		return res;
	}

	public void onNodeSelect2() {
		solutions = new ArrayList<Solution>();
		// get children
		List<String> list = getChildrenOfConcept(selectedNode);
		// List<String> list = ontologyInstance.getOntology()
		// .getSubClassesAsStrings(
		// selectedNode.getData().toString());
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			if (list.contains(ontologyInstance.getSolutions().get(i)
					.getOntologyCategory())) {
				solutions.add(ontologyInstance.getSolutions().get(i));
			}
		}

	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {

		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {

		this.selectedNode = selectedNode;
		if (selectedNode != null) {

			// clear tree selection
			root.setSelected(false);
			clearSelections(root);
			// expand nodes until root
			TreeNode tmp = this.selectedNode.getParent();
			while (tmp != null) {
				tmp.setExpanded(true);
				tmp = tmp.getParent();
			}
			this.selectedNode.setSelected(true);

			onNodeSelect2();
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

	public List<Solution> getSolutions() {
		return solutions;
	}

	public void setSolutions(List<Solution> solutions) {
		this.solutions = solutions;
	}

	public Solution getSelectedSolution() {
		return selectedSolution;
	}

	public void setSelectedSolution(Solution selectedSolution) {

		this.clonedSelectedSolution = selectedSolution;
		if (selectedSolution != null)
			this.selectedSolution = selectedSolution.cloneSolution();
		else
			this.selectedSolution = null;

	}

	public void onRowSelect(SelectEvent event) {
		butDisabled = false;
	}

	public void onRowUnselect(SelectEvent event) {
		butDisabled = true;
	}

	public boolean isButDisabled() {
		return butDisabled;
	}

	public void setButDisabled(boolean butDisabled) {
		this.butDisabled = butDisabled;
	}

	public void showMoreClusterItems() {

		for (EASTINProperty prop : activeETNACluster.getAttributesToHide()) {
			if (!activeETNACluster.getAttributesToShowInView().contains(prop))
				activeETNACluster.getAttributesToShowInView().add(prop.clone());
		}

		for (EASTINProperty prop : activeETNACluster.getMeasuresToHide()) {
			if (!activeETNACluster.getMeasuresToShowInView().contains(prop))
				activeETNACluster.getMeasuresToShowInView().add(prop.clone());
		}

		this.activeETNACluster.setShowItems(false);

	}

	public void hideClusterItems() {

		for (EASTINProperty prop : activeETNACluster.getAttributesToHide()) {
			if (activeETNACluster.getAttributesToShowInView().contains(prop))
				activeETNACluster.getAttributesToShowInView().remove(prop);
		}

		for (EASTINProperty prop : activeETNACluster.getMeasuresToHide()) {
			if (activeETNACluster.getMeasuresToShowInView().contains(prop))
				activeETNACluster.getMeasuresToShowInView().remove(prop);
		}

		this.activeETNACluster.setShowItems(true);

	}

	public void showMoreClusters() {

		FacesContext context = FacesContext.getCurrentInstance();
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);

		for (ETNACluster cluster : selectedSolution.getClustersToHide()) {
			if (!selectedSolution.getClustersToShowInView().contains(cluster))
				selectedSolution.getClustersToShowInView().add(cluster.clone());
		}
		this.clustersShowFlag = false;
		editSolutionBean.setActiveTabIndex("-1");

	}

	public void hideClusters() {

		FacesContext context = FacesContext.getCurrentInstance();
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);

		for (ETNACluster cluster : selectedSolution.getClustersToHide()) {
			if (selectedSolution.getClustersToShowInView().contains(cluster))
				selectedSolution.getClustersToShowInView().remove(cluster);
		}

		this.clustersShowFlag = true;
		editSolutionBean.setActiveTabIndex("-1");
	}

	public void searchResults() {

		System.out.print("searchResults");
		ApplicationCategories match = searchOntologyMatching();

		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {

			if (match != null)
				if (node.getData().toString().toLowerCase()
						.contains(match.getName().toLowerCase())) {

					setSelectedNode(node);
					showSolutionsPanel = true;
					break;
				} else {
					TreeNode tmp = searchNodeInChildren(node, match.getName());
					if (tmp != null) {

						break;
					}
				}
		}

		onNodeSelect();

	}

	private TreeNode searchNodeInChildren(TreeNode root, String match) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			if (node.getData().toString().toLowerCase()
					.contains(match.toLowerCase())) {

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

	private ApplicationCategories searchOntologyMatching() {

		String str = cameliseString(searchApplicationName);
		if (!str.equals("") && str != null) {
			SortedMap map = ontologyInstance.getOntology()
					.getSolutionsMatchingConcepts(str);

			return ((ApplicationCategories) map.get(map.lastKey()));

		} else {
			return null;
		}

	}

	private String cameliseString(String str) {
		StringBuffer result = new StringBuffer(str.length());
		// String strl = str.toLowerCase();
		boolean bMustCapitalize = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= 'a' && c <= 'z') {
				if (bMustCapitalize) {
					result.append(str.substring(i, i + 1).toUpperCase());
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

	public String getSearchApplicationName() {
		return searchApplicationName;
	}

	public void setSearchApplicationName(String searchApplicationName) {
		this.searchApplicationName = searchApplicationName;
	}

	public String getSearchbyApplicationName() {
		return searchbyApplicationName;
	}

	public void setSearchbyApplicationName(String searchbyApplicationName) {
		this.searchbyApplicationName = searchbyApplicationName;
	}

	public void searchResultsByName() {
		System.out.print("searchResultsByName");
		if (!searchbyApplicationName.equals("")) {
			solutions = new ArrayList<Solution>();
			List<Solution> solutions2 = new ArrayList<Solution>();
			List<Solution> list = ontologyInstance.getSolutions();
			SortedMap<Double, Solution> map = new TreeMap<Double, Solution>();
			for (int i = 0; i < list.size(); i++) {
				/*
				 * Double score = StringComparison.CompareStrings(
				 * searchbyApplicationName, list.get(i).getName());
				 * if(StringComparison.CompareStrings(searchbyApplicationName,
				 * list.get(i).getOntologyCategory())>0.5){ score+=.2; } if
				 * (score > 0.3) map.put(score, list.get(i));
				 */
				// solutions.add(list.get(i));
				if (list.get(i).getName().toLowerCase()
						.contains(searchbyApplicationName.toLowerCase())) {
					solutions.add(list.get(i));
				} else {
					Double score = StringComparison.CompareStrings(
							searchbyApplicationName, list.get(i).getName());
					if (StringComparison.CompareStrings(
							searchbyApplicationName, list.get(i)
									.getOntologyCategory()) > 0.5) {
						score += .2;
					}
					if (score > 0.3)
						map.put(score, list.get(i));
				}
			}
			for (Map.Entry<Double, Solution> entry : map.entrySet()) {

				solutions2.add(entry.getValue());
			}
			for (int i = solutions2.size() - 1; i >= 0; i--) {
				solutions.add(solutions2.get(i));
			}
			showSolutionsPanel = true;
		}
		root.setSelected(false);
		clearSelections(root);
		// onNodeSelect(null);
	}

	public void test() {
		try {

			FacesContext context = FacesContext.getCurrentInstance();
			EditSolutionBean editSolutionBean = (EditSolutionBean) context
					.getApplication().evaluateExpressionGet(context,
							"#{editSolutionBean}", EditSolutionBean.class);

			if (editSolutionBean != null) {

				proposedCluster = new ETNACluster();
				EASTINProperty it = new EASTINProperty();
				it.setType("attribute");
				proposedCluster.getAllproperties().add(it);

				// initializations

				proposedClusterItemsMatches = new ArrayList<EASTINProperty>();
				proposedClusters = new ArrayList<ETNACluster>();
				proposedMeasureClusterItems = new ArrayList<EASTINProperty>();
				proposedClusterItems = new ArrayList<EASTINProperty>();

				List<ETNACluster> cluster = new ArrayList<ETNACluster>();
				for (int i = 0; i < ontologyInstance.getEtnaClusterOriginal()
						.size(); i++) {
					cluster.add(ontologyInstance.getEtnaClusterOriginal()
							.get(i).clone());
				}
				ontologyInstance.setEtnaCluster(cluster);

				ontologyInstance
						.setProposedEASTINItems(new ArrayList<EASTINProperty>());
				for (int i = 0; i < ontologyInstance
						.getProposedEASTINItemsOriginal().size(); i++) {
					ontologyInstance.getProposedEASTINItems().add(
							ontologyInstance.getProposedEASTINItemsOriginal()
									.get(i));
				}
				ontologyInstance
						.setProposedMeasureEASTINItems(new ArrayList<EASTINProperty>());
				for (int i = 0; i < ontologyInstance
						.getProposedMeasureEASTINItemsOriginal().size(); i++) {
					ontologyInstance.getProposedMeasureEASTINItems().add(
							ontologyInstance
									.getProposedMeasureEASTINItemsOriginal()
									.get(i));
				}

				ontologyInstance.getAllProposedItems().clear();
				ontologyInstance.getAllProposedItems().add(0,
						new EASTINProperty());

				for (EASTINProperty p : ontologyInstance
						.getProposedEASTINItemsOriginal())
					if (!ontologyInstance.getAllProposedItems().contains(p))
						ontologyInstance.getAllProposedItems().add(p);

				for (EASTINProperty p : ontologyInstance
						.getProposedMeasureEASTINItemsOriginal())
					if (!ontologyInstance.getAllProposedItems().contains(p))
						ontologyInstance.getAllProposedItems().add(p);

				ontologyInstance
						.setEASTINPropertiesMap(new LinkedHashMap<String, EASTINProperty>());

				for (int i = 0; i < selectedSolution.getEtnaProperties().size(); i++) {

					selectedSolution.getEtnaProperties().get(i)
							.setSelectedProposedItem(

							ontologyInstance.getAllProposedItems().get(0));

					selectedSolution.getEtnaProperties().get(i)
							.setItemProposalDescription("");
					selectedSolution.getEtnaProperties().get(i)
							.setItemProposalName("");
				}
				// attributes
				for (int i = 0; i < ontologyInstance.getProposedEASTINItems()
						.size(); i++) {
					ontologyInstance.getEASTINPropertiesMap().put(
							ontologyInstance.getProposedEASTINItems().get(i)
									.getName(),
							ontologyInstance.getProposedEASTINItems().get(i));
				}
				// measures
				for (int i = 0; i < ontologyInstance
						.getProposedMeasureEASTINItems().size(); i++) {
					ontologyInstance.getEASTINPropertiesMap().put(
							ontologyInstance.getProposedMeasureEASTINItems()
									.get(i).getName(),
							ontologyInstance.getProposedMeasureEASTINItems()
									.get(i));
				}

				ETNACluster clus = new ETNACluster();
				if (ontologyInstance.getProposedEASTINProperties().size() == 0)
					ontologyInstance.getProposedEASTINProperties().add(0, clus);

				if (ontologyInstance.getProposedEASTINProperties().size() > 0)
					if (!ontologyInstance.getProposedEASTINProperties().get(0)
							.getName().isEmpty())
						ontologyInstance.getProposedEASTINProperties().add(0,
								clus);

				// prepare clusters for solutions
				updateListsToContainProposedItemsAndClusters();
				// set showItems flag for clusters
				List<List<ETNACluster>> superList = new ArrayList<List<ETNACluster>>();
				List<List<ETNACluster>> tempSuperList = new ArrayList<List<ETNACluster>>();

				// add lists of clusters to superList
				superList.add(selectedSolution.getClustersToShowInView());
				superList.add(selectedSolution.getClustersToShow());
				superList.add(selectedSolution.getClustersToHide());

				tempSuperList = Utilities
						.setShowItemsFlagsForClusters(superList);

				selectedSolution.setClustersToShowInView(tempSuperList.get(0));
				selectedSolution.setClustersToShow(tempSuperList.get(1));
				selectedSolution.setClustersToHide(tempSuperList.get(2));

				this.setClustersShowFlag(false);
				if (selectedSolution.getClustersToHide().size() > 0)
					this.setClustersShowFlag(true);

				activeETNACluster = null;
				editSolutionBean.setActiveTabIndex("-1");
				// check if category is proposed
				editSolutionBean
						.setCategoryProposed(!checkIfCategoryIsNotProposed(
								ontologyInstance.getSolutionClassesStructured(),
								selectedSolution.getOntologyCategory()));

				editSolutionBean.setNewSetting(new Setting());
				editSolutionBean.setRelevantSettings(new ArrayList<Setting>());
				editSolutionBean.setSelectedRelevantSetting(new Setting());
				editSolutionBean.setOldSettingName("");
				this.setClustersShowFlag(true);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateListsToContainProposedItemsAndClusters() {
		// add proposed attributes to lists
		boolean flag = false;
		for (EASTINProperty prop : ontologyInstance
				.getProposedEASTINItemsOriginal()) {
			if (prop.getRefersToSolutionName().equals(
					selectedSolution.getName())
					|| prop.getAllSolutionsString().contains(
							selectedSolution.getName())) {

				for (ETNACluster cl : selectedSolution.getClustersToShow()) {
					if (cl.getName().equals(prop.getBelongsToCluster())
							&& !cl.getAttributesToShow().contains(prop)) {

						cl.getAttributesToShow().add(prop.clone());
						cl.getAttributesToShowInView().add(prop.clone());
						flag = true;
						break;
					}

				}

				if (!flag)
					for (ETNACluster cl : selectedSolution.getClustersToHide()) {
						if (cl.getName().equals(prop.getBelongsToCluster())
								&& !cl.getAttributesToShow().contains(prop)) {

							cl.getAttributesToShow().add(prop.clone());
							cl.getAttributesToShowInView().add(prop.clone());
							selectedSolution.getClustersToShow()
									.add(cl.clone());
							flag = true;
							break;
						}

					}

				flag = false;

			}
		}
		// add proposed measures
		for (EASTINProperty prop : ontologyInstance
				.getProposedMeasureEASTINItemsOriginal()) {
			if (prop.getRefersToSolutionName().equals(
					selectedSolution.getName())
					|| prop.getAllSolutionsString().contains(
							selectedSolution.getName())) {

				for (ETNACluster cl : selectedSolution.getClustersToShow()) {
					if (cl.getName().equals(prop.getBelongsToCluster())
							&& !cl.getMeasuresToShow().contains(prop)) {
						cl.getMeasuresToShow().add(prop.clone());
						cl.getMeasuresToShowInView().add(prop.clone());
						flag = true;
						break;
					}

				}

				if (!flag)
					for (ETNACluster cl : selectedSolution.getClustersToHide()) {
						if (cl.getName().equals(prop.getBelongsToCluster())
								&& !cl.getMeasuresToShow().contains(prop)) {
							cl.getMeasuresToShow().add(prop.clone());
							cl.getMeasuresToShowInView().add(prop.clone());
							selectedSolution.getClustersToShow()
									.add(cl.clone());
							flag = true;
							break;
						}

					}
			}
		}

		// add proposed clusters
		for (ETNACluster cl : ontologyInstance
				.getProposedEASTINPropertiesOriginal()) {

			if (!selectedSolution.getClustersToShow().contains(cl))
				if (cl.getProposedForProduct().equals(
						selectedSolution.getName())) {
					selectedSolution.getClustersToShow().add(cl.clone());
				} else if (cl.getAllproperties().size() > 0) {
					if (cl.getAllproperties().get(0).getAllSolutionsString()
							.contains(selectedSolution.getName()))
						selectedSolution.getClustersToShow().add(cl.clone());
				}

		}

		// update clusters to show in view
		selectedSolution.setClustersToShowInView(new ArrayList<ETNACluster>());
		for (ETNACluster cl : selectedSolution.getClustersToShow()) {
			selectedSolution.getClustersToShowInView().add(cl.clone());
		}
	}

	public boolean checkIfCategoryIsNotProposed(List<OntologyClass> list,
			String name) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getClassName().equalsIgnoreCase(name))
				return true;
			boolean check = checkIfCategoryIsNotProposed(list.get(i)
					.getChildren(), name);
			if (check)
				return true;
		}
		return false;
	}

	public Setting getSelectedSetting() {
		if (selectedSetting != null)
			return selectedSetting;
		else
			return new Setting();
	}

	public void setSelectedSetting(Setting selectedSetting) {
		type = "";
		if (selectedSetting != null) {
			this.selectedSetting = selectedSetting;
			this.clonedSelectedSetting = this.selectedSetting.cloneSetting();
			type = selectedSetting.getType();
			FacesContext context = FacesContext.getCurrentInstance();
			EditSolutionBean editSolutionBean = (EditSolutionBean) context
					.getApplication().evaluateExpressionGet(context,
							"#{editSolutionBean}", EditSolutionBean.class);
			editSolutionBean.setSelectedMapping(selectedSetting.getMapping());
		}
	}

	public RegistryTerm getProposedRegistryTerm() {
		return proposedRegistryTerm;
	}

	public void setProposedRegistryTerm(RegistryTerm proposedRegistryTerm) {
		this.proposedRegistryTerm = proposedRegistryTerm;
	}

	public boolean isShowSolutionsPanel() {
		return showSolutionsPanel;
	}

	public void setShowSolutionsPanel(boolean showSolutionsPanel) {
		this.showSolutionsPanel = showSolutionsPanel;
	}

	public void clearPageSelections() {
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);

		viewSolutionsBean.setSelectedNode(null);
		viewSolutionsBean.getRoot().setExpanded(false);
		viewSolutionsBean.getRoot().setSelected(false);
		clearSelections(viewSolutionsBean.getRoot());
		viewSolutionsBean.setSelectedSolution(null);
		viewSolutionsBean.setClonedSelectedSolution(null);
		viewSolutionsBean.setButDisabled(true);
		viewSolutionsBean.setSolutions(new ArrayList<Solution>());

		// ontologyInstance.setProposedApplicationCategories(ontologyInstance
		// .getOntology().loadAllProposedApplicationCategories());
		for (int i = ontologyInstance.getProposedApplicationCategories().size() - 1; i >= 0; i--) {
			boolean found = false;
			for (int j = 0; j < ontologyInstance
					.getProposedApplicationCategoriesOriginal().size(); j++) {
				if (ontologyInstance
						.getProposedApplicationCategories()
						.get(i)
						.getName()
						.equals(ontologyInstance
								.getProposedApplicationCategoriesOriginal()
								.get(j).getName())) {
					found = true;
				}
			}
			if (!found) {
				ontologyInstance.getProposedApplicationCategories().remove(i);
			}
		}
		searchApplicationName = "";
		searchbyApplicationName = "";
		showSolutionsPanel = false;
		createOntologyTree(ontologyInstance.getOntology());
		root = (DefaultTreeNode) root.getChildren().get(0);
	}

	public boolean isFormCompleted() {
		return formCompleted;
	}

	public void setFormCompleted(boolean formCompleted) {
		this.formCompleted = formCompleted;
	}

	public void onKeyRelease() {

		if (!this.proposedRegistryTerm.getDefaultValue().replace(" ", "")
				.equals("")
				&& !this.proposedRegistryTerm.getDescription().replace(" ", "")
						.equals("")
				&& !this.proposedRegistryTerm.getName().replace(" ", "")
						.equals("")

				&& !this.proposedRegistryTerm.getValueSpace().replace(" ", "")
						.equals("")) {
			formCompleted = false;
		} else
			formCompleted = true;
	}

	public Solution getClonedSelectedSolution() {
		return clonedSelectedSolution;
	}

	public void setClonedSelectedSolution(Solution clonedSelectedSolution) {
		this.clonedSelectedSolution = clonedSelectedSolution;
	}

	public Setting getClonedSelectedSetting() {
		return clonedSelectedSetting;
	}

	public void setClonedSelectedSetting(Setting clonedSelectedSetting) {
		this.clonedSelectedSetting = clonedSelectedSetting;
	}

	public ETNACluster getProposedCluster() {
		return proposedCluster;
	}

	public void setProposedCluster(ETNACluster proposedCluster) {
		this.proposedCluster = proposedCluster;
	}

	public List<ETNACluster> getProposedClusters() {
		return proposedClusters;
	}

	public void setProposedClusters(List<ETNACluster> proposedClusters) {
		this.proposedClusters = proposedClusters;
	}

	public ETNACluster getActiveETNACluster() {
		return activeETNACluster;
	}

	public void setActiveETNACluster(ETNACluster activeETNACluster) {
		this.activeETNACluster = activeETNACluster;
	}

	public List<EASTINProperty> getProposedClusterItemsMatches() {
		return proposedClusterItemsMatches;
	}

	public void setProposedClusterItemsMatches(
			List<EASTINProperty> proposedClusterItemsMatches) {
		this.proposedClusterItemsMatches = proposedClusterItemsMatches;
	}

	public List<EASTINProperty> getProposedClusterItems() {
		return proposedClusterItems;
	}

	public void setProposedClusterItems(
			List<EASTINProperty> proposedClusterItems) {
		this.proposedClusterItems = proposedClusterItems;
	}

	public void onTabChange(TabChangeEvent event) {

		System.out.println(((ETNACluster) event.getData()).getName());

		for (int i = 0; i < selectedSolution.getClustersToShowInView().size(); i++) {
			if (selectedSolution.getClustersToShowInView().get(i).getName()
					.equals(((ETNACluster) event.getData()).getName())) {

				activeETNACluster = selectedSolution.getClustersToShowInView()
						.get(i);

				return;
			}
		}

	}

	public void searchRelevantETNAItems() {
		try {
			if (proposedClusterItemsMatches != null) {
				proposedClusterItemsMatches.clear();
			}
			for (int i = 0; i < activeETNACluster.getAllproperties().size(); i++) {

				if (activeETNACluster
						.getAllproperties()
						.get(i)
						.getName()
						.trim()
						.equalsIgnoreCase(
								activeETNACluster.getItemProposalName().trim())) {
					FacesContext.getCurrentInstance().addMessage(
							"ItemProposalMessage",
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"This proposed item name already exists",
									""));
					return;
				}
			}

			if (activeETNACluster.getItemProposalName().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(
						"ItemProposalMessage",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Please provide a name for the proposed item",
								""));
				return;
			}
			if (activeETNACluster.getItemProposalDescription().trim()
					.equals("")) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								"ItemProposalMessage",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Please provide a description for the proposed item",
										""));
				return;
			}
			proposedClusterItemsMatches = new ArrayList<EASTINProperty>();
			SortedMap map = ontologyInstance.getOntology()
					.getMatchingConceptsOfEtnaItems(
							selectedSolution.getEtnaProperties(),
							activeETNACluster.getItemProposalName());

			Map<Double, EASTINProperty> treeMap = new TreeMap<Double, EASTINProperty>(
					map);
			List<String> res = new ArrayList<String>();
			Iterator i = treeMap.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				if (((Double) me.getKey()) > 0.3) {
					List<EASTINProperty> tmp = (List<EASTINProperty>) me
							.getValue();
					for (int j = 0; j < tmp.size(); j++) {
						proposedClusterItemsMatches.add(tmp.get(j));
					}
				}

			}
			Collections.reverse(proposedClusterItemsMatches);
			// load cluster names
			for (int j = 0; j < proposedClusterItemsMatches.size(); j++) {
				EASTINProperty prop = proposedClusterItemsMatches.get(j);
				if (!ontologyInstance.getOntology()
						.loadEASTINPropertyCluster(prop).equals(""))
					prop.setBelongsToCluster(ontologyInstance.getOntology()
							.loadEASTINPropertyCluster(prop));

			}
			RequestContext rc = RequestContext.getCurrentInstance();

			rc.update("relatedItemsDialog");
			rc.execute("relatedItemsDialog.show()");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addClusterItemProposal() {
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);

		Vendor vendor = userBean.getVendorObj();
		EASTINProperty prop = new EASTINProperty();
		prop.setName(activeETNACluster.getItemProposalName());
		prop.setDefinition(activeETNACluster.getItemProposalDescription());
		prop.setBelongsToCluster(activeETNACluster.getName());
		prop.setRefersToSolutionName(selectedSolution.getName());
		prop.setRefersToSolutionID(selectedSolution.getId());
		prop.setType(activeETNACluster.getItemProposalType());
		prop.setAllSolutionsString(selectedSolution.getName());

		String type = activeETNACluster.getItemProposalType();

		// user is vendor
		if (userBean.getVendorObj() != null) {
			prop.setProponentName(vendor.getVendorName());
			prop.setProponentEmail(vendor.getContactDetails());
		} else {
			// user is admin
			prop.setProponentName("Administrator");
			prop.setProponentEmail("test_admin@cloud4all.org");
		}
		boolean alreadyExists = false;
		for (int i = 0; i < ontologyInstance.getProposedEASTINItems().size(); i++) {
			EASTINProperty p = ontologyInstance.getProposedEASTINItems().get(i);
			if (p.getName().equals(prop.getName())
					&& p.getType().equals(prop.getType())
					&& p.getBelongsToCluster().equals(
							prop.getBelongsToCluster())) {
				alreadyExists = true;
				break;
			}
		}
		for (int i = 0; i < ontologyInstance.getProposedMeasureEASTINItems()
				.size(); i++) {
			EASTINProperty p = ontologyInstance.getProposedMeasureEASTINItems()
					.get(i);
			if (p.getName().equals(prop.getName())
					&& p.getType().equals(prop.getType())
					&& p.getBelongsToCluster().equals(
							prop.getBelongsToCluster())) {
				alreadyExists = true;
				break;
			}
		}
		prop.setProposed(true);
		if (type.toLowerCase().equals("attribute")) {
			if (!alreadyExists)
				ontologyInstance.getProposedEASTINItems().add(prop);

			if (!activeETNACluster.getProperties().contains(prop)) {
				activeETNACluster.getProperties().add(prop);
				activeETNACluster.getAttributesToShow().add(prop);
				activeETNACluster.getAttributesToShowInView().add(prop);
			}

			if (!proposedClusterItems.contains(prop))
				proposedClusterItems.add(prop);
		} else {
			if (!alreadyExists)
				ontologyInstance.getProposedMeasureEASTINItems().add(prop);

			if (!activeETNACluster.getMeasureProperties().contains(prop)) {
				activeETNACluster.getMeasureProperties().add(prop);
				activeETNACluster.getMeasuresToShow().add(prop);
				activeETNACluster.getMeasuresToShowInView().add(prop);
			}

			if (!proposedMeasureClusterItems.contains(prop))
				proposedMeasureClusterItems.add(prop);
		}

		if (!alreadyExists)
			ontologyInstance.getAllProposedItems().add(prop);

		if (!activeETNACluster.getAllproperties().contains(prop))
			activeETNACluster.getAllproperties().add(prop);

		activeETNACluster.setSelectedProposedItem(prop);


	}

	public void showNewClusterDialog() {
		resetNewClusterProposal();

		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("newClusterDialog.show()");

	}

	public void resetNewClusterProposal() {
		proposedCluster = new ETNACluster();
		proposedCluster.setName("");
		proposedCluster.setDescription("");
		disableImportButton = true;
		addItemToCluster();
		selectedProposedCluster = proposedCluster;
		proposedClusterFlag = false;
	}

	public void addItemToCluster() {
		EASTINProperty it = new EASTINProperty();
		it.setType("attribute");
		proposedCluster.getAllproperties().add(it);

	}

	public void submitNewCluster() {

		if (this.proposedCluster.getName().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"ProposalMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide the name of the group", ""));
			return;
		}
		if (this.proposedCluster.getDescription().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"ProposalMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide a short description of the group",
							""));
			return;
		}
		// check if already exists
		for (int i = 0; i < ontologyInstance.getEtnaCluster().size(); i++) {
			if (ontologyInstance
					.getEtnaCluster()
					.get(i)
					.getName()
					.toLowerCase()
					.trim()
					.equals(this.proposedCluster.getName().toLowerCase().trim())) {
				FacesContext.getCurrentInstance().addMessage(
						"ProposalMessage",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"The cluster name already exists", ""));
				return;
			}
		}
		for (int i = 0; i < ontologyInstance.getProposedEASTINProperties()
				.size(); i++) {
			if (ontologyInstance
					.getProposedEASTINProperties()
					.get(i)
					.getName()
					.toLowerCase()
					.trim()
					.equals(this.proposedCluster.getName().toLowerCase().trim())) {
				FacesContext.getCurrentInstance().addMessage(
						"ProposalMessage",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"The cluster name already exists", ""));
				return;
			}
		}
		for (int i = 0; i < proposedClusters.size(); i++) {
			if (proposedClusters
					.get(i)
					.getName()
					.toLowerCase()
					.trim()
					.equals(this.proposedCluster.getName().toLowerCase().trim())) {
				FacesContext.getCurrentInstance().addMessage(
						"ProposalMessage",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"You have already entered a cluster with name "
										+ this.proposedCluster.getName(), ""));
				return;
			}
		}
		// check if children have the same name
		for (int i = 0; i < proposedCluster.getAllproperties().size(); i++) {
			String str1 = proposedCluster.getAllproperties().get(i).getName()
					.trim();
			for (int j = i + 1; j < proposedCluster.getAllproperties().size(); j++) {
				String str2 = proposedCluster.getAllproperties().get(j)
						.getName().trim();
				if (str1.equals(str2)) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									"ProposalMessage",
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"The item "
													+ str1
													+ " appears more than once in the cluster",
											""));
					return;
				}
			}
		}

		for (int i = 0; i < proposedCluster.getAllproperties().size(); i++) {
			EASTINProperty prop = proposedCluster.getAllproperties().get(i);
			if (prop.getName().trim().equals("")) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								"ProposalMessage",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Please provide the names of all items in the group",
										""));
				return;
			}
			if (prop.getDefinition().trim().equals("")) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								"ProposalMessage",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Please provide the descriptions of all items in the group",
										""));
				return;
			}

		}
		for (EASTINProperty p : proposedCluster.getAllproperties()) {
			if (p.getType().toLowerCase().equals("attribute")) {
				proposedCluster.getProperties().add(p);
				proposedCluster.getAttributesToShow().add(p);
				proposedCluster.getAttributesToShowInView().add(p);
			} else {
				proposedCluster.getMeasureProperties().add(p);
				proposedCluster.getMeasuresToShow().add(p);
				proposedCluster.getMeasuresToShowInView().add(p);
			}
		}

		proposedCluster.setProposedForProduct(selectedSolution.getName());
		proposedCluster.setProposedForProductID(selectedSolution.getId());

		if (!proposedClusters.contains(proposedCluster))
			proposedClusters.add(proposedCluster);

		if (!selectedSolution.getClustersToShow().contains(proposedCluster))
			selectedSolution.getClustersToShow().add(proposedCluster);

		if (!selectedSolution.getClustersToShowInView().contains(
				proposedCluster))
			selectedSolution.getClustersToShowInView().add(proposedCluster);

		if (!ontologyInstance.getEtnaCluster().contains(proposedCluster))
			ontologyInstance.getEtnaCluster().add(proposedCluster);

		if (!ontologyInstance.getProposedEASTINProperties().contains(
				proposedCluster))
			ontologyInstance.getProposedEASTINProperties().add(proposedCluster);

		ontologyInstance.getClusterPropertiesMap().put(
				proposedCluster.getName(), proposedCluster);
		// ontologyInstance.getOntology().createEASTINCluster(proposedCluster);

		resetNewClusterProposal();

		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("clusterSubmittedDialog.show()");
		FacesContext context = FacesContext.getCurrentInstance();
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);
		editSolutionBean.setActiveTabIndex(String.valueOf(selectedSolution
				.getClustersToShowInView().size() - 1));
	}

	public void proposedItemChangedListener() {
		if (activeETNACluster.getSelectedProposedItem() != null) {
			activeETNACluster.setItemProposalName(activeETNACluster
					.getSelectedProposedItem().getName());
			activeETNACluster.setItemProposalDescription(activeETNACluster
					.getSelectedProposedItem().getDefinition());
			activeETNACluster.setItemProposalType(activeETNACluster
					.getSelectedProposedItem().getType());
			activeETNACluster.setFlag(true);

		} else {
			activeETNACluster.setItemProposalName("");
			activeETNACluster.setItemProposalDescription("");
			activeETNACluster.setItemProposalType("Attribute");
			activeETNACluster.setFlag(false);

		}
	}

	public boolean isDisableImportButton() {
		return disableImportButton;
	}

	public void setDisableImportButton(boolean disableImportButton) {
		this.disableImportButton = disableImportButton;
	}

	public void selectedProposedClusterChangedListener() {
		disableImportButton = false;
		if (selectedProposedCluster != null
				&& !selectedProposedCluster.getName().equals("")) {

			proposedCluster = selectedProposedCluster;
			// check if already exists
			for (int i = 0; i < ontologyInstance.getEtnaCluster().size(); i++) {
				if (ontologyInstance.getEtnaCluster().get(i).getName().trim()
						.equalsIgnoreCase(proposedCluster.getName().trim()))
					disableImportButton = true;
				proposedClusterFlag = true;
			}

			// check if cluster has already been imported in the solution
			for (ETNACluster cluster : selectedSolution.getEtnaProperties()) {
				if (cluster.getName().trim()
						.equals(proposedCluster.getName().trim())) {
					disableImportButton = true;
					proposedClusterFlag = true;
				}
			}

		} else {
			resetNewClusterProposal();

		}
	}

	public ETNACluster getSelectedProposedCluster() {
		return selectedProposedCluster;
	}

	public void setSelectedProposedCluster(ETNACluster selectedProposedCluster) {
		this.selectedProposedCluster = selectedProposedCluster;
	}

	public void removeItemFromCluster() {
		if (proposedItemToRemove.getType().toLowerCase().equals("attribute"))
			proposedCluster.getProperties().remove(proposedItemToRemove);
		else
			proposedCluster.getMeasureProperties().remove(proposedItemToRemove);
		proposedCluster.getAllproperties().remove(proposedItemToRemove);
	}

	public EASTINProperty getProposedItemToRemove() {
		return proposedItemToRemove;
	}

	public void setProposedItemToRemove(EASTINProperty proposedItemToRemove) {
		this.proposedItemToRemove = proposedItemToRemove;
	}

	public void importProposal() {

		selectedSolution.getClustersToShow().add(proposedCluster);
		selectedSolution.getClustersToShowInView().add(proposedCluster);
		selectedProposedCluster = ontologyInstance
				.getProposedEASTINProperties().get(0);

		FacesContext context = FacesContext.getCurrentInstance();
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);

		editSolutionBean.setActiveTabIndex(String.valueOf(selectedSolution
				.getClustersToShowInView().size() - 1));

	}

	public EASTINProperty getRelevantItem() {
		return relevantItem;
	}

	public void setRelevantItem(EASTINProperty relevantItem) {
		this.relevantItem = relevantItem;
	}

	public void selectProposedItem() {
		FacesContext context = FacesContext.getCurrentInstance();
		EditSolutionBean editSolutionBean = (EditSolutionBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{editSolutionBean}", EditSolutionBean.class);
		if (relevantItem != null) {
			for (int i = 0; i < selectedSolution.getEtnaProperties().size(); i++) {
				ETNACluster cl = selectedSolution.getEtnaProperties().get(i);
				if (cl.getName().trim()
						.equals(relevantItem.getBelongsToCluster())) {
					for (int j = 0; j < cl.getProperties().size(); j++) {
						EASTINProperty pr = cl.getProperties().get(j);
						if (pr.getName().equals(relevantItem.getName())) {
							if (cl.isSingleSelection()) {
								cl.setSelectedProperty(pr.getName());
							} else {
								cl.getSelectedProperties().add(pr.getName());
							}
							editSolutionBean.setActiveTabIndex(String
									.valueOf(i));
							return;
						}
					}
					for (int j = 0; j < cl.getMeasureProperties().size(); j++) {
						EASTINProperty pr = cl.getMeasureProperties().get(j);
						if (pr.getName().equals(relevantItem.getName())) {
							cl.getSelectedProperties().add(pr.getName());
							editSolutionBean.setActiveTabIndex(String
									.valueOf(i));
							return;
						}
					}
				}
			}
		}

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<EASTINProperty> getProposedMeasureClusterItems() {
		return proposedMeasureClusterItems;
	}

	public void setProposedMeasureClusterItems(
			List<EASTINProperty> proposedMeasureClusterItems) {
		this.proposedMeasureClusterItems = proposedMeasureClusterItems;
	}

	public boolean isProposedClusterFlag() {
		return proposedClusterFlag;
	}

	public void setProposedClusterFlag(boolean proposedClusterFlag) {
		this.proposedClusterFlag = proposedClusterFlag;
	}

	public void addOptionToList() {

		Element el = new Element("", "");
		selectedSolution.getOptions().add(el);

	}

	public void removeOptionFromList() {
		selectedSolution.getOptions().remove(elementForRemove);
	}

	public Element getElementForRemove() {
		return elementForRemove;
	}

	public void setElementForRemove(Element elementForRemove) {
		this.elementForRemove = elementForRemove;
	}

	public boolean isClustersShowFlag() {
		return clustersShowFlag;
	}

	public void setClustersShowFlag(boolean clustersShowFlag) {
		this.clustersShowFlag = clustersShowFlag;
	}

}
