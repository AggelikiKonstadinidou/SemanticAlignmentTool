package org.cloud4All;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringEscapeUtils;
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
import org.cloud4All.IPR.SolutionAccessInfoForUsers;
import org.cloud4All.IPR.SolutionAccessInfoForVendors;
import org.cloud4All.IPR.Vendor;
import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Element;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyClass;
import org.cloud4All.ontology.OntologyInstance;
import org.cloud4All.ontology.Utils;
import org.cloud4All.utils.StringComparison;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.slider.Slider;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import sun.org.mozilla.javascript.internal.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.faces.context.FacesContext;
import javax.swing.event.ChangeEvent;

import com.google.gson.reflect.*;
import com.sun.faces.component.visit.FullVisitContext;
import com.sun.faces.taglib.html_basic.InputHiddenTag;

@ManagedBean(name = "step2Bean")
@SessionScoped
public class step2Bean {

	private CommandButton button;
	private String applicationDescription = "";
	private String selectedCategory = "";
	private String cloneSelectedCategory = "";
	private String applicationName = "";
	private String constraints = "e.g. needs JRE 1.6.2 or later";
	private String manufacturerName = "";
	private Date date;
	private Date updateDate;
	private String manufacturerCountry = "";
	private String manufacturerWebsite = "";
	private String imageUrl = "";
	private String downloadPage = "";
	private boolean disableImportButton = true;
	private boolean categoryProposed = false;
	private String id = "";
	private List<Solution> relevantSolutions = new ArrayList<Solution>();
	private Solution selectedRelevantSolution = new Solution();
	private boolean solutionNameExists = true;
	private boolean showEditInDialog = true;
	private List<EASTINApplication> eastinApplications = new ArrayList<EASTINApplication>();
	private EASTINApplication selectedEastinApplication = new EASTINApplication();
	private OntologyInstance ontologyInstance;
	private String dialogEmptyMessage = "";
	private ETNACluster proposedCluster = new ETNACluster();
	private List<ETNACluster> proposedClusters = new ArrayList<ETNACluster>();
	private EASTINProperty proposedItemToRemove = new EASTINProperty();
	private List<EASTINProperty> proposedClusterItems = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> proposedMeasureClusterItems = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> proposedClusterItemsMatches = new ArrayList<EASTINProperty>();
	private ETNACluster activeETNACluster = new ETNACluster();
	private ETNACluster selectedProposedCluster = new ETNACluster();
	private EASTINProperty relevantItem = new EASTINProperty();
	private String activeTabIndex = "-1";
	private boolean proposedClusterFlag = false;
	private DefaultTreeNode root;
	private TreeNode selectedNode = null;
	private String handlerType = "";
	private String commandForStart = "";
	private String commandForStop = "";
	private List<Element> options = new ArrayList<Element>();
	private String capabilities = "";
	private String capabilitiesTransformations = "";
	private Element elementForRemove = new Element("", "");
	private String oldApplicationName = "";
	private boolean checkForName = false;
	private boolean nameAdded = false;
	private List<String> categoriesList = new ArrayList<String>();
	private boolean clustersShowFlag = true;

	public step2Bean() {
		super();
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Step1Bean step1Bean = (Step1Bean) context.getApplication()
					.evaluateExpressionGet(context, "#{step1Bean}",
							Step1Bean.class);
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);
			proposedClusterFlag = false;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Element getElementForRemove() {
		return elementForRemove;
	}

	public void setElementForRemove(Element elementForRemove) {
		this.elementForRemove = elementForRemove;
	}

	public CommandButton getButton() {
		return button;
	}

	public void setButton(CommandButton button) {
		this.button = button;
	}

	public String getApplicationDescription() {
		return applicationDescription;
	}

	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.oldApplicationName = this.applicationName; // save old name
		this.applicationName = applicationName;
	}

	public void test3() {
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("accessInfoDialog.show()");
	}

	public boolean isCategoryProposed() {
		return categoryProposed;
	}

	public void setCategoryProposed(boolean categoryProposed) {
		this.categoryProposed = categoryProposed;
	}

	public List<String> getCategoriesList() {
		return categoriesList;
	}

	public void setCategoriesList(List<String> categoriesList) {
		this.categoriesList = categoriesList;
	}

	public void test() {
		FacesContext context = FacesContext.getCurrentInstance();
		Step1Bean step1Bean = (Step1Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step1Bean}", Step1Bean.class);

		step2Bean step2Bean = (step2Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step2Bean}", step2Bean.class);

		step2Bean.setCategoriesList(new ArrayList<String>());
		step2Bean.setApplicationDescription("");
		step2Bean.setApplicationName("");
		step2Bean.setId("");
		step2Bean.setConstraints("");

		String categoryName = "";
		categoryProposed = false;
		if (step1Bean.getSelectedCategory() != null)
			if (!step1Bean.getSelectedCategory().isEmpty()) {
				categoryName = step1Bean.getSelectedCategory();
				categoryProposed = false;
			} else {
				categoryName = step1Bean.getSelectedProposedCategory();
				categoryProposed = true;

			}

		step2Bean.setCategoryProposed(categoryProposed);
		step2Bean.setSelectedCategory(categoryName);
		step2Bean.setSelectedNode(null);

		if (step2Bean.getRoot() != null) {
			step2Bean.getRoot().setExpanded(false);
			step2Bean.getRoot().setSelected(false);
			clearSelections(step2Bean.getRoot());
		}

		elementForRemove = new Element("", "");
		Element emptyoption = new Element("", "");
		options = new ArrayList<Element>();
		options.add(emptyoption);
		step2Bean.setCapabilities("");
		step2Bean.setCapabilitiesTransformations("");
		step2Bean.setCommandForStart("");
		step2Bean.setCommandForStop("");
		step2Bean.setHandlerType("No");
		step2Bean.setManufacturerCountry("");
		step2Bean.setManufacturerName("");
		step2Bean.setManufacturerWebsite("");
		step2Bean.setImageUrl("");
		step2Bean.setDownloadPage("");
		step2Bean.setDate(null);
		step2Bean.setUpdateDate(null);

		// step1Bean.getSelectedNode().setExpanded(false);
		step1Bean.clearSelections(step1Bean.getRoot());
		step1Bean.selectItemInMainTree(step1Bean.getRoot(),
				step1Bean.getSelectedCategory());
		if (step1Bean.getSelectedNode() != null)
			step1Bean.getSelectedNode().setExpanded(false);

	}

	/**
	 * update list in order to show all cluster items
	 */
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

	/**
	 * update list in order to show only attributes and measures "to show", hide
	 * hidden items
	 **/
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

	/**
	 * update list in order to show all clusters (shown and hidden)
	 */
	public void showMoreClusters() {
		for (ETNACluster cluster : ontologyInstance.getHiddenClusters()) {
			if (!ontologyInstance.getClustersToShowInView().contains(cluster))
				ontologyInstance.getClustersToShowInView().add(cluster.clone());
		}
		this.clustersShowFlag = false;
		this.activeTabIndex = "-1";

	}

	/**
	 * update list in order to show only clusters to show (hide hidden clusters)
	 */
	public void hideClusters() {

		for (ETNACluster cl : ontologyInstance.getHiddenClusters()) {
			if (ontologyInstance.getClustersToShowInView().contains(cl))
				ontologyInstance.getClustersToShowInView().remove(cl);
		}
		this.clustersShowFlag = true;
		this.activeTabIndex = "-1";

	}

	public void test2() {

		FacesContext context = FacesContext.getCurrentInstance();
		step3Bean step3Bean = (step3Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step3Bean}", step3Bean.class);
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		
		if (this.id.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide id for the application", ""));
			return;
		}
		
		for(Solution sol : ontologyInstance.getSolutions()){
			if(sol.getId().equalsIgnoreCase(this.id.trim())){
				FacesContext.getCurrentInstance().addMessage(
						"messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"This application id already exists", ""));
				return;
			}
		}

		String proponentEmail = "";
		String proponentName = "";

		if (userBean.isAdmin()) {
			proponentEmail = "test_admin@cloud4all.org";
			proponentName = "Administrator";
		} else {

			proponentEmail = userBean.getVendorObj().getContactDetails();
			proponentName = userBean.getVendorObj().getVendorName();
		}

		// fill in application id and name in proposed clusters
		for (int i = 0; i < proposedClusters.size(); i++) {
			ETNACluster clus = proposedClusters.get(i);

			clus.setProponentEmail(proponentEmail);
			clus.setProponentName(proponentName);

			clus.setProposedForProduct(applicationName);
			clus.setAllProposalsString(applicationName);
			clus.setProposedForProductID(id);
			for (int j = 0; j < clus.getAllproperties().size(); j++) {
				EASTINProperty prop = clus.getAllproperties().get(j);
				prop.setRefersToSolutionName(applicationName);
				prop.setRefersToSolutionID(id);

				prop.setProponentEmail(proponentEmail);
				prop.setProponentName(proponentName);

			}
		}
		step3Bean.setNewSetting(new Setting());
		step3Bean.setSettings(new ArrayList<Setting>());
		step3Bean.setRelevantSettings(new ArrayList<Setting>());
		step3Bean.setOldSettingName("");
		// close opened tabs,in step2 in case of previous/next
		activeTabIndex = "-1";
	}

	public List<Solution> getRelevantSolutions() {
		return relevantSolutions;
	}

	public void setRelevantSolutions(List<Solution> relevantSolutions) {
		this.relevantSolutions = relevantSolutions;
	}

	public Solution getSelectedRelevantSolution() {
		return selectedRelevantSolution;
	}

	public void setSelectedRelevantSolution(Solution selectedRelevantSolution) {
		this.selectedRelevantSolution = selectedRelevantSolution;
	}

	public boolean isSolutionNameExists() {
		return solutionNameExists;
	}

	public void setSolutionNameExists(boolean solutionNameExists) {
		this.solutionNameExists = solutionNameExists;
	}

	public boolean isShowEditInDialog() {
		return showEditInDialog;
	}

	public void setShowEditInDialog(boolean showEditInDialog) {
		this.showEditInDialog = showEditInDialog;
	}

	public void onRowSelect() {
		showEditInDialog = false;
	}

	public void editSol() {
		FacesContext context = FacesContext.getCurrentInstance();
		ViewSolutionsBean viewSolutionsBean = (ViewSolutionsBean) context
				.getApplication().evaluateExpressionGet(context,
						"#{viewSolutionsBean}", ViewSolutionsBean.class);
		viewSolutionsBean.setSelectedSolution(selectedRelevantSolution);

		viewSolutionsBean.setActiveETNACluster(selectedRelevantSolution
				.getEtnaProperties().get(0));

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getManufacturerCountry() {
		return manufacturerCountry;
	}

	public void setManufacturerCountry(String manufacturerCountry) {
		this.manufacturerCountry = manufacturerCountry;
	}

	public String getManufacturerWebsite() {
		return manufacturerWebsite;
	}

	public void setManufacturerWebsite(String manufacturerWebsite) {
		this.manufacturerWebsite = manufacturerWebsite;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDownloadPage() {
		return downloadPage;
	}

	public void setDownloadPage(String downloadPage) {
		this.downloadPage = downloadPage;
	}

	public void loadEastinProperties() {
		System.out.println("call web service");
		dialogEmptyMessage = "No relevant applications found";
		eastinApplications = new ArrayList<EASTINApplication>();
		// call eastin web service
		if (!this.applicationName.trim().equals("")
				|| !this.manufacturerName.trim().equals("")) {
			try {

				URL url;
				HttpURLConnection conn;
				BufferedReader rd;
				String line;
				String result = "";
				String link = "http://webservices.eastin.eu/cloud4all/searches/products/listsimilarity?commercialName="
						+ this.applicationName.trim()
						+ "&manufacturer="
						+ this.manufacturerName.trim();
				link = link.replace(" ", "%20");
				link = StringEscapeUtils.escapeHtml(link);

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
							+ URLEncoder.encode(tmp.get(j).getDatabase(),
									"UTF-8")
							+ "/"
							+ URLEncoder.encode(tmp.get(j).getProductCode(),
									"UTF-8");
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

	/**
	 * @return the eastinApplications
	 */
	public List<EASTINApplication> getEastinApplications() {
		return eastinApplications;
	}

	/**
	 * @param eastinApplications
	 *            the eastinApplications to set
	 */
	public void setEastinApplications(List<EASTINApplication> eastinApplications) {
		this.eastinApplications = eastinApplications;
	}

	/**
	 * @return the selectedEastinApplication
	 */
	public EASTINApplication getSelectedEastinApplication() {
		return selectedEastinApplication;
	}

	/**
	 * @param selectedEastinApplication
	 *            the selectedEastinApplication to set
	 */
	public void setSelectedEastinApplication(
			EASTINApplication selectedEastinApplication) {
		this.selectedEastinApplication = selectedEastinApplication;
		importEastingApp();
	}

	public void importEastingApp() {
		try {
			System.out.println("importing "
					+ selectedEastinApplication.getCommercialName());
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy",
					Locale.ENGLISH);
			Date expiry = sdf2.parse(selectedEastinApplication.getInsertDate());

			date = expiry;
			expiry = sdf2.parse(selectedEastinApplication.getLastUpdateDate());
			updateDate = expiry;
			manufacturerName = selectedEastinApplication
					.getManufacturerOriginalFullName();
			imageUrl = selectedEastinApplication.getThumbnailImageUrl();
			if (selectedEastinApplication.getManufacturerCountry() != null
					&& !selectedEastinApplication.getManufacturerCountry()
							.equals("null"))
				manufacturerCountry = WordUtils
						.capitalizeFully(selectedEastinApplication
								.getManufacturerCountry());
			else
				manufacturerCountry = "";

			manufacturerWebsite = selectedEastinApplication
					.getManufacturerWebSiteUrl();
			downloadPage = selectedEastinApplication.getEnglishUrl();
			applicationDescription = selectedEastinApplication
					.getEnglishDescription();

			for (int i = 0; i < selectedEastinApplication.getFeatures().size(); i++) {
				Features f = selectedEastinApplication.getFeatures().get(i);
				for (int j = 0; j < ontologyInstance.getEtnaCluster().size(); j++) {
					ETNACluster etna = ontologyInstance.getEtnaCluster().get(j);
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

	public void showNewClusterDialog() {
		resetNewClusterProposal();
		selectedProposedCluster = ontologyInstance
				.getProposedEASTINProperties().get(0);
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
		this.proposedClusterFlag = false;
	}

	public void addOptionToList() {

		Element el = new Element("", "");
		options.add(el);

	}

	public void removeOptionFromList() {
		options.remove(elementForRemove);
	}

	public void addItemToCluster() {
		EASTINProperty it = new EASTINProperty();
		it.setType("attribute");
		proposedCluster.getAllproperties().add(it);

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
		// check if already exists in existing proposals
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
								"The cluster name has already been proposed",
								""));
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

		if (!proposedClusters.contains(proposedCluster))
			proposedClusters.add(proposedCluster);

		ontologyInstance.addClusterToClustersToShowInView(proposedCluster);
		ontologyInstance.addClusterToShownClusters(proposedCluster);

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

		// before changes
		// activeTabIndex=String.valueOf(ontologyInstance.getEtnaCluster().size()-1);
		activeTabIndex = String.valueOf(ontologyInstance
				.getClustersToShowInView().size() - 1);

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

	public void onTabChange(TabChangeEvent event) {

		System.out.println(((ETNACluster) event.getData()).getName());
		// before changes
		// for (int i = 0; i < ontologyInstance.getEtnaCluster().size(); i++) {
		// if (ontologyInstance.getEtnaCluster().get(i).getName()
		// .equals(((ETNACluster) event.getData()).getName())) {
		// activeETNACluster = ontologyInstance.getEtnaCluster().get(i);
		// return;
		// }
		// }
		for (int i = 0; i < ontologyInstance.getClustersToShowInView().size(); i++) {
			if (ontologyInstance.getClustersToShowInView().get(i).getName()
					.equals(((ETNACluster) event.getData()).getName())) {

				activeETNACluster = ontologyInstance.getClustersToShowInView()
						.get(i);
				return;

			}
		}

	}

	public void searchRelevantETNAItems() {

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
								"This proposed item name already exists", ""));
				return;
			}
		}

		if (activeETNACluster.getItemProposalName().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"ItemProposalMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide a name for the proposed item", ""));
			return;
		}
		if (activeETNACluster.getItemProposalDescription().trim().equals("")) {
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
		SortedMap map = ontologyInstance
				.getOntology()
				.getMatchingConceptsOfEtnaItems(
						ontologyInstance.getEtnaCluster(),
						cameliseString(activeETNACluster.getItemProposalName()));

		Map<Double, List<EASTINProperty>> treeMap = new TreeMap<Double, List<EASTINProperty>>(
				map);
		List<String> res = new ArrayList<String>();
		Iterator i = treeMap.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			if (((Double) me.getKey()) > 0.3) {
				List<EASTINProperty> tmp = (List<EASTINProperty>) me.getValue();
				for (int j = 0; j < tmp.size(); j++) {
					proposedClusterItemsMatches.add(tmp.get(j));
				}
			}

		}
		Collections.reverse(proposedClusterItemsMatches);
		// load cluster names
		for (int j = 0; j < proposedClusterItemsMatches.size(); j++) {
			EASTINProperty prop = proposedClusterItemsMatches.get(j);
			if (!ontologyInstance.getOntology().loadEASTINPropertyCluster(prop)
					.equals(""))
				prop.setBelongsToCluster(ontologyInstance.getOntology()
						.loadEASTINPropertyCluster(prop));

		}
		RequestContext rc = RequestContext.getCurrentInstance();

		rc.update("relatedItemsDialog");
		rc.execute("relatedItemsDialog.show()");
	}

	public ETNACluster getActiveETNACluster() {
		return activeETNACluster;
	}

	public void setActiveETNACluster(ETNACluster activeETNACluster) {
		this.activeETNACluster = activeETNACluster;
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

	public List<EASTINProperty> getProposedMeasureClusterItems() {
		return proposedMeasureClusterItems;
	}

	public void setProposedMeasureClusterItems(
			List<EASTINProperty> proposedMeasureClusterItems) {
		this.proposedMeasureClusterItems = proposedMeasureClusterItems;
	}

	public void addClusterItemProposal() {
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		Step1Bean step1Bean = (Step1Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step1Bean}", Step1Bean.class);

		EASTINProperty prop = new EASTINProperty();
		prop.setName(activeETNACluster.getItemProposalName());
		prop.setDefinition(activeETNACluster.getItemProposalDescription());
		prop.setBelongsToCluster(activeETNACluster.getName());
		prop.setRefersToSolutionName(applicationName);
		prop.setRefersToSolutionID(id);
		prop.setType(activeETNACluster.getItemProposalType());
		prop.setRelatedToTypeOfApplication(step1Bean.getTypeOfSolution());
		prop.setAllSolutionsString(applicationName);

		String type = activeETNACluster.getItemProposalType();
		if (userBean.isAdmin()) {
			prop.setProponentEmail("test_admin@cloud4all.org");
			prop.setProponentName("Administrator");
		} else {
			prop.setProponentEmail(userBean.getVendorObj().getContactDetails());
			prop.setProponentName(userBean.getVendorObj().getVendorName());
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

		// before changes
		// // add proposed item to ETNA cluster
		// for (int i = 0; i < ontologyInstance.getEtnaCluster().size(); i++) {
		// ETNACluster cluster = ontologyInstance.getEtnaCluster().get(i);
		// if (cluster.getName().equalsIgnoreCase(activeETNACluster.getName()))
		// {
		//
		// if(!cluster.getAllproperties().contains(prop))
		// cluster.getAllproperties().add(prop);
		//
		// return;
		// }
		// }

		// RequestContext rc = RequestContext.getCurrentInstance();
		// rc.execute("clusterSubmittedDialog.show()");
	}

	public ETNACluster getSelectedProposedCluster() {
		return selectedProposedCluster;
	}

	public void setSelectedProposedCluster(ETNACluster selectedProposedCluster) {
		this.selectedProposedCluster = selectedProposedCluster;
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
				this.proposedClusterFlag = true;
			}

		} else {
			resetNewClusterProposal();

		}
	}

	public void importProposal() {

		ontologyInstance.getShownClusters().add(proposedCluster);
		ontologyInstance.getClustersToShowInView().add(proposedCluster);
		selectedProposedCluster = ontologyInstance
				.getProposedEASTINProperties().get(0);
		activeTabIndex = String.valueOf(ontologyInstance
				.getClustersToShowInView().size() - 1);

	}

	public boolean isDisableImportButton() {
		return disableImportButton;
	}

	public void setDisableImportButton(boolean disableImportButton) {
		this.disableImportButton = disableImportButton;
	}

	public EASTINProperty getRelevantItem() {
		return relevantItem;
	}

	public void setRelevantItem(EASTINProperty relevantItem) {
		this.relevantItem = relevantItem;
	}

	public void selectProposedItem() {
		if (relevantItem != null) {
			for (int i = 0; i < ontologyInstance.getEtnaCluster().size(); i++) {
				ETNACluster cl = ontologyInstance.getEtnaCluster().get(i);
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
							activeTabIndex = String.valueOf(i);
							return;
						}
					}
					for (int j = 0; j < cl.getMeasureProperties().size(); j++) {
						EASTINProperty pr = cl.getMeasureProperties().get(j);
						if (pr.getName().equals(relevantItem.getName())) {
							cl.getSelectedProperties().add(pr.getName());
							activeTabIndex = String.valueOf(i);
							return;
						}
					}
				}
			}
		}

	}

	public String getActiveTabIndex() {

		return activeTabIndex;
	}

	public void setActiveTabIndex(String activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}

	public boolean isProposedClusterFlag() {
		return proposedClusterFlag;
	}

	public void setProposedClusterFlag(boolean proposedClusterFlag) {
		this.proposedClusterFlag = proposedClusterFlag;
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public void calculateCategoriesTree() {

		selectedCategory = "";
		root = new DefaultTreeNode("Root", null);
		createOntologyTree(ontologyInstance.getOntology());
		selectItemInTree(root, cloneSelectedCategory);
	}

	public void createOntologyTree(Ontology ontology) {
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		List<OntologyClass> list = ontologyInstance
				.getSolutionClassesStructured();
		OntologyClass cl = list.get(0);
		root = new DefaultTreeNode(cl.getClassName(), null);

		getTreeNodeOfConcept(cl, root);
		root = (DefaultTreeNode) root.getChildren().get(0);
	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	private DefaultTreeNode getTreeNodeOfConcept(OntologyClass cl, TreeNode root) {
		DefaultTreeNode node = new DefaultTreeNode(cl.getClassName(), root);
		for (int i = 0; i < cl.getChildren().size(); i++) {
			getTreeNodeOfConcept(cl.getChildren().get(i), node);
		}
		return node;

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

	public void expandallParents(TreeNode root) {
		root.setExpanded(true);
		if (root.getParent() != null) {
			expandallParents(root.getParent());
		}
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
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

	private void clearSelections(TreeNode root) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			node.setSelected(false);
			node.setExpanded(false);
			clearSelections(node);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
		if (selectedNode != null)
			selectedCategory = (String) selectedNode.getData();
		else
			selectedCategory = "";
		setSelectedNode(selectedNode);

	}

	public void changeCategory() {
		FacesContext context = FacesContext.getCurrentInstance();

		step2Bean step2 = (step2Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step2Bean}", step2Bean.class);
		step2.setSelectedCategory(selectedCategory);
		step2.setCloneSelectedCategory(selectedCategory);
		categoryProposed = false;

	}

	public void cancelChangeCategory() {
		selectedCategory = cloneSelectedCategory;
	}

	public String getHandlerType() {
		return this.handlerType;
	}

	public void setHandlerType(String handlerType) {
		this.handlerType = handlerType;
	}

	public String getCommandForStart() {
		return commandForStart;
	}

	public void setCommandForStart(String commandForStart) {
		this.commandForStart = commandForStart;
	}

	public String getCommandForStop() {
		return commandForStop;
	}

	public void setCommandForStop(String commandForStop) {
		this.commandForStop = commandForStop;
	}

	public List<Element> getOptions() {
		if (this.options.isEmpty()) {
			Element el = new Element("", "");
			this.options.add(el);
		}
		return this.options;
	}

	public void setOptions(List<Element> options) {
		this.options = options;
	}

	public void localeChanged(AjaxBehaviorEvent e) {
		// assign new value to handlerType
		FacesContext context = FacesContext.getCurrentInstance();
		step2Bean step2bean = (step2Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step2Bean}", step2Bean.class);
		step2bean.setHandlerType(this.handlerType);

	}

	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	public String getCapabilitiesTransformations() {
		return capabilitiesTransformations;
	}

	public void setCapabilitiesTransformations(
			String capabilitiesTransformations) {
		this.capabilitiesTransformations = capabilitiesTransformations;
	}

	public String getCloneSelectedCategory() {
		return cloneSelectedCategory;
	}

	public void setCloneSelectedCategory(String cloneSelectedCategory) {
		this.cloneSelectedCategory = cloneSelectedCategory;
	}
	
	public void idListener(AjaxBehaviorEvent e) {
		
		if (this.id.trim().equals("")) {
			nameAdded = false;
			FacesContext.getCurrentInstance().addMessage(
					"messages",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide id for the application", ""));
			return;
		}
		
		for(Solution sol : ontologyInstance.getSolutions()){
			if(sol.getId().equalsIgnoreCase(this.id.trim())){
				nameAdded = false;
				FacesContext.getCurrentInstance().addMessage(
						"messages",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"This application id already exists", ""));
				return;
			}
		}
		
		if (!this.applicationName.isEmpty())
			nameAdded = true;
	}

	// checkRelevantSolutionsOld
	public void nameListener(AjaxBehaviorEvent e) {

		// check if name
		if (this.applicationName.trim().isEmpty()
				&& this.oldApplicationName.trim().isEmpty()) {
			checkForName = false;

			nameAdded = false;

		} else if (!this.applicationName.trim().isEmpty()) {

			if (this.applicationName.equalsIgnoreCase(this.oldApplicationName))
				checkForName = false;
			else
				checkForName = true;
		}

		if (checkForName) {
			if (this.applicationName.trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Please provide the Application name", ""));
				return;
			}

			
			nameAdded = true;

			solutionNameExists = false;
			showEditInDialog = true;
			relevantSolutions = new ArrayList<Solution>();
			selectedRelevantSolution = new Solution();

			// check relevant solutions
			HashMap map = new HashMap();
			for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {

				double score = StringComparison.CompareStrings(ontologyInstance
						.getSolutions().get(i).getName(), applicationName);
				if (score > 0.4) {
					map.put(ontologyInstance.getSolutions().get(i), score);
				} else if (!ontologyInstance.getSolutions().get(i)
						.getManufacturerName().trim().equals("")) {
					score = StringComparison.CompareStrings(ontologyInstance
							.getSolutions().get(i).getManufacturerName(),
							manufacturerName);
					if (score > 0.4) {
						map.put(ontologyInstance.getSolutions().get(i), score);
					}
				}
			}

			Iterator entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				Entry thisEntry = (Entry) entries.next();
				Object key = thisEntry.getKey();
				Object value = thisEntry.getValue();
				relevantSolutions.add((Solution) key);
				if (((Solution) key).getName()
						.equalsIgnoreCase(applicationName)) {
					solutionNameExists = true;
				}
			}
			if (relevantSolutions.size() == 0)
				solutionNameExists = false;

			RequestContext rc = RequestContext.getCurrentInstance();
			rc.execute("relevantSolutionsDialog.show()");
		}

	}

	public String getOldApplicationName() {
		return oldApplicationName;
	}

	public void setOldApplicationName(String oldApplicationName) {
		this.oldApplicationName = oldApplicationName;
	}

	public boolean isCheckForName() {
		return checkForName;
	}

	public void setCheckForName(boolean checkForName) {
		this.checkForName = checkForName;
	}

	public boolean isNameAdded() {
		return nameAdded;
	}

	public void setNameAdded(boolean nameAdded) {
		this.nameAdded = nameAdded;
	}

	public boolean isClustersShowFlag() {
		return clustersShowFlag;
	}

	public void setClustersShowFlag(boolean clustersShowFlag) {
		this.clustersShowFlag = clustersShowFlag;
	}

}
