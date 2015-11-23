package org.cloud4All;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.cloud4All.ontology.EASTINProperty;
import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyClass;
import org.cloud4All.ontology.OntologyInstance;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import weka.core.parser.java_cup.internal_error;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.model.User;

@ManagedBean(name = "manageEASTINProposalsBean")
@SessionScoped
public class ManageEASTINProposalsBean {

	private ETNACluster selectedCluster = new ETNACluster();
	private ETNACluster selectedClusterClone = new ETNACluster();
	private OntologyInstance ontologyInstance;
	private EASTINProperty selectedItem = new EASTINProperty();
	private ProposedApplicationCategory selectedProposedApplicationCategory = new ProposedApplicationCategory();
	private ProposedApplicationCategory selectedProposedApplicationCategoryClone = new ProposedApplicationCategory();
	private List<EASTINProperty> proposedEASTINItems = new ArrayList<EASTINProperty>();
	private EASTINProperty selectedEASTINItem = new EASTINProperty();
	private EASTINProperty selectedEASTINItemClone = new EASTINProperty();

	private String selectedCategory = "";

	private DefaultTreeNode root;
	private String selectedParentCategory = "";
	private String selectedProposedCategory = "";
	private String proposedCategoryParent = "";

	private TreeNode selectedParentNode = null;
	private String rejectReason = "";

	public ManageEASTINProposalsBean() {
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
	}

	public ETNACluster getSelectedCluster() {
		return selectedCluster;
	}

	public void setSelectedCluster(ETNACluster selectedCluster) {
		this.selectedCluster = selectedCluster;
		this.selectedClusterClone = this.selectedCluster.clone();
	}

	public void addItemToCluster() {
		EASTINProperty it = new EASTINProperty();
		it.setType("attribute");
		selectedClusterClone.getAllproperties().add(it);

	}

	public ETNACluster getSelectedClusterClone() {
		return selectedClusterClone;
	}

	public void setSelectedClusterClone(ETNACluster selectedClusterClone) {
		this.selectedClusterClone = selectedClusterClone;
	}

	public void applyChanges() {
		// check if already exists

		int k = ontologyInstance.getProposedEASTINProperties().indexOf(
				selectedCluster);
		for (int i = 0; i < ontologyInstance.getProposedEASTINProperties()
				.size(); i++) {
			if (ontologyInstance
					.getProposedEASTINProperties()
					.get(i)
					.getName()
					.toLowerCase()
					.trim()
					.equals(this.selectedClusterClone.getName().toLowerCase()
							.trim())
					&& i != k) {
				FacesContext.getCurrentInstance().addMessage(
						"ProposalMessage",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"The cluster name already exists", ""));
				return;
			}
		}
		if (this.selectedClusterClone.getProperties().size() == 0
				&& this.selectedClusterClone.getMeasureProperties().size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					"ProposalMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please add at least one item in the group", ""));
			return;
		}
		if (this.selectedClusterClone.getName().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"ProposalMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide the name of the group", ""));
			return;
		}
		if (this.selectedClusterClone.getDescription().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"ProposalMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide a short description of the group",
							""));
			return;
		}

		// check if children have the same name
		for (int i = 0; i < selectedClusterClone.getAllproperties().size(); i++) {
			String str1 = selectedClusterClone.getAllproperties().get(i)
					.getName().trim();
			for (int j = i + 1; j < selectedClusterClone.getAllproperties()
					.size(); j++) {
				String str2 = selectedClusterClone.getAllproperties().get(j)
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

		for (int i = 0; i < selectedClusterClone.getAllproperties().size(); i++) {
			EASTINProperty prop = selectedClusterClone.getAllproperties()
					.get(i);
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
		selectedClusterClone.setProperties(new ArrayList<EASTINProperty>());
		selectedClusterClone
				.setMeasureProperties(new ArrayList<EASTINProperty>());
		
		String proponentName = selectedCluster.getAllproperties().get(0).getProponentName();
		String proponentEmail = selectedCluster.getAllproperties().get(0).getProponentEmail();
		for (int i = 0; i < selectedClusterClone.getAllproperties().size(); i++) {
			EASTINProperty prop = selectedClusterClone.getAllproperties()
					.get(i);
			//set to all properties the proponents name and email
			prop.setProponentEmail(proponentEmail);
			prop.setProponentName(proponentName);
			
			if (prop.getType().equals("Attribute")) {
				selectedClusterClone.getProperties().add(prop);
			} else
				selectedClusterClone.getMeasureProperties().add(prop);
		}
		int pos = ontologyInstance.getProposedEASTINProperties().indexOf(
				selectedCluster);
		if (pos != -1) {

			ontologyInstance.getProposedEASTINProperties().remove(
					selectedCluster);
			ontologyInstance.getProposedEASTINPropertiesOriginal().remove(
					selectedCluster);
			selectedClusterClone.setEdit(true);
			ontologyInstance.getProposedEASTINProperties().add(pos,
					selectedClusterClone);
			ontologyInstance.getProposedEASTINPropertiesOriginal().add(pos,
					selectedClusterClone);

		}

		// apply changes to clusters of all solutions
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			for (int j = 0; j < sol.getEtnaProperties().size(); j++) {
				ETNACluster cluster = sol.getEtnaProperties().get(j);
				if (cluster.getName().equals(selectedCluster.getName())) {
					sol.getEtnaProperties().remove(j);
					sol.getEtnaProperties().add(j, selectedClusterClone);
					break;
				}
			}
		}
		
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			for (int j = 0; j < sol.getProposedClusters().size(); j++) {
				ETNACluster cluster = sol.getProposedClusters().get(j);
				if (cluster.getName().equals(selectedCluster.getName())) {
					sol.getProposedClusters().remove(j);
					sol.getProposedClusters().add(j, selectedClusterClone);
					break;
				}
			}
		}
		
		
		init();
		RequestContext rc = RequestContext.getCurrentInstance();

		rc.update("form");
		rc.update("categoryPproposals");
		rc.execute("editClusterDialog.hide()");
	}

	public void applyChangesForCategory() {
		FacesContext context2 = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context2
				.getApplication().evaluateExpressionGet(context2,
						"#{ontologyInstance}", OntologyInstance.class);
		// check if proposal name already exists
		int k = ontologyInstance.getProposedApplicationCategories().indexOf(
				selectedProposedApplicationCategory);
		for (int i = 0; i < ontologyInstance.getProposedApplicationCategories()
				.size(); i++) {
			if (ontologyInstance
					.getProposedApplicationCategories()
					.get(i)
					.getName()
					.trim()
					.toLowerCase()
					.equals(selectedProposedApplicationCategoryClone.getName()
							.trim().toLowerCase())
					&& i != k) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("ProposalMessage3", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This proposed category name already exists", ""));
				return;
			}
		}
		if (selectedProposedApplicationCategoryClone.getName().trim()
				.toLowerCase().equals("Solutions")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("ProposalMessage3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"This category name already exists", ""));
			return;
		}
		if (Character.isDigit(selectedProposedApplicationCategoryClone
				.getName().trim().charAt(0))) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("ProposalMessage3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Category name cannot start with number", ""));
			return;
		}
		List<ApplicationCategories> list = ontologyInstance.getOntology()
				.getAllSolutionChildren();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i)
					.getName()
					.trim()
					.toLowerCase()
					.equals(selectedProposedApplicationCategoryClone.getName()
							.trim().toLowerCase())) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("ProposalMessage3", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This category name already exists", ""));
				return;
			}
		}
		if (selectedProposedApplicationCategoryClone.getName().trim()
				.equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("ProposalMessage3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the proposed category name", ""));
			return;
		}
		if (selectedProposedApplicationCategoryClone.getName().trim()
				.equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("ProposalMessage3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the proposed category description", ""));
			return;
		}
		int pos = ontologyInstance.getProposedApplicationCategories().indexOf(
				selectedProposedApplicationCategory);
		if (pos != -1) {
			
			selectedProposedApplicationCategoryClone.setEdit(true);
//			if (!selectedProposedApplicationCategory.getName().equals(
//					selectedProposedApplicationCategoryClone.getName()))
				selectedProposedApplicationCategoryClone
						.setOldcategory(selectedProposedApplicationCategory);
			
			ontologyInstance.getProposedApplicationCategories().remove(pos);
			ontologyInstance.getProposedApplicationCategoriesOriginal().remove(
					pos);
			ontologyInstance.getProposedApplicationCategories().add(pos,
					selectedProposedApplicationCategoryClone);
			ontologyInstance.getProposedApplicationCategoriesOriginal().add(
					pos, selectedProposedApplicationCategoryClone);
			init();

		}
		
		for(Solution sol : ontologyInstance.getSolutions()){
			if(sol.getOntologyCategory().equals(selectedProposedApplicationCategory.getName()))
				sol.setOntologyCategory(selectedProposedApplicationCategoryClone.getName());
		}
		
		
		RequestContext rc = RequestContext.getCurrentInstance();

		rc.update("form");
		rc.execute("editCategoryDialog.hide()");

	}

	public void acceptCategory() {

		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);

		ontologyInstance.getOntology().acceptCategory(
				selectedProposedApplicationCategoryClone,
				selectedProposedApplicationCategoryClone.getParent());
		
		int pos = ontologyInstance.getProposedApplicationCategories().indexOf(
				selectedProposedApplicationCategory);
		if (pos != -1) {
			ontologyInstance.getProposedApplicationCategories().remove(pos);
			ontologyInstance.getProposedApplicationCategoriesOriginal().remove(
					pos);
		}

		ontologyInstance.setSolutionClassesStructured(ontologyInstance
				.getOntology().getSolutionsClassesStructured());
		init();
	}

	public void acceptCategoryWithEmail() {
		// send email
		sendInformEmailForCategory(selectedProposedApplicationCategory,
				selectedProposedApplicationCategory.getProponentEmail());
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
	
		ontologyInstance.getOntology().acceptCategory(
				selectedProposedApplicationCategory,
				selectedProposedApplicationCategory.getParent());
		
		int pos = ontologyInstance.getProposedApplicationCategories().indexOf(
				selectedProposedApplicationCategory);
		if (pos != -1) {
			ontologyInstance.getProposedApplicationCategories().remove(pos);
			ontologyInstance.getProposedApplicationCategoriesOriginal().remove(
					pos);

		}
		ontologyInstance.setSolutionClassesStructured(ontologyInstance
				.getOntology().getSolutionsClassesStructured());
		init();
	}

	public void deleteCategory() {
		// send email to proponent
		try {
			String emailAddress = selectedProposedApplicationCategoryClone
					.getProponentEmail();
			final String username = "cloud4All.SAT@gmail.com";
			final String password = "cloud1@3";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("cloud4All.SAT@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddress));
			message.setSubject("Application type rejected");
			message.setText("An administrator has rejected your application type named '"
					+ selectedProposedApplicationCategoryClone.getName()
					+ "' for the following reason:\n" + rejectReason);
			Transport.send(message);

		} catch (Exception e) {

		}
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
		
		//update category of solutions with category same with rejected category
		//delete rejected category and add its solutions under "Solutions"
		//1 step : add old solution uri to deleted uris
		//2 step : create new uri for solution in order to update them
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			String uri = "";
			if (sol.getOntologyCategory().equalsIgnoreCase(
					selectedProposedApplicationCategoryClone.getName())) {

				// remove old uri
				ontologyInstance.getOldSolutionUris().add(sol.getOntologyURI());

				// change category
				ontologyInstance.getSolutions().get(i)
						.setOntologyCategory("Solutions");

				// set new uri
				uri = sol.getName().trim().replace(" ", "_")
						.replaceAll("[^\\p{L}\\p{Nd}]", "");
				
				// check if uri exists
				if (Character.isDigit(uri.charAt(0))) {
					uri = "app";
				}
				uri = ontologyInstance.getOntology().changeUri(uri);
				
				sol.setOntologyURI(ontologyInstance.getOntology().getNS() + uri);

			}
		}

//		ontologyInstance.getOntology().deleteCategoryClass(
//				selectedProposedApplicationCategory);

		ontologyInstance.getProposedApplicationCategories().remove(
				selectedProposedApplicationCategory);

		ontologyInstance.getProposedApplicationCategoriesOriginal().remove(
				selectedProposedApplicationCategory);
		init();

	}

	public void removeItem() {

		if (selectedItem.getType().toLowerCase().equals("attribute"))
			selectedClusterClone.getProperties().remove(selectedItem);
		else
			selectedClusterClone.getMeasureProperties().remove(selectedItem);

		selectedClusterClone.getAllproperties().remove(selectedItem);
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.update("test:editClusterDialog");
	}

	public EASTINProperty getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(EASTINProperty selectedItem) {
		this.selectedItem = selectedItem;
	}

	public void acceptCluster() {
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
		ontologyInstance.getProposedEASTINProperties().remove(selectedCluster);
		ontologyInstance.getProposedEASTINPropertiesOriginal().remove(
				selectedCluster);
		
		ETNACluster acceptedCluster = selectedCluster.clone();
		ontologyInstance.getEtnaClusterOriginal().add(acceptedCluster);
	 
	    
	    //update solutions, in case change happened before save
		//remove cluster from proposed, add it to etnaproperties 
		
		
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {

			Solution sol = ontologyInstance.getSolutions().get(i);
			for (int j = 0; j < sol.getProposedClusters().size(); j++) {
				ETNACluster cluster = sol.getProposedClusters().get(j);
				if (cluster.getName().equals(selectedCluster.getName())) {
					sol.getProposedClusters().remove(j);
					break;
				}
			}

			boolean flag = false;
			for (ETNACluster cl : sol.getEtnaProperties()) {
				if (cl.getName().equals(selectedCluster.getName())) {
					flag = true;
					break;
				}

			}

			if (!flag) {
				sol.getEtnaProperties().add(acceptedCluster);
			}

		}

	    //add cluster to list with accepted clusters
		ontologyInstance.getClustersToBeAccepted().add(selectedClusterClone);
	
		init();
	}

	public void acceptClusterWithEmail() {
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
		// send email
		sendInformEmailForCluster(selectedClusterClone, selectedClusterClone
				.getProperties().get(0).getProponentEmail());
		
		ontologyInstance.getProposedEASTINProperties().remove(selectedCluster);
		ontologyInstance.getProposedEASTINPropertiesOriginal().remove(
				selectedCluster);
		ETNACluster acceptedCluster = selectedCluster.clone();
		ontologyInstance.getEtnaClusterOriginal().add(acceptedCluster);
	    
		// update solutions, in case change happened before save
		// remove cluster from proposed, add it to etnaproperties
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {

			Solution sol = ontologyInstance.getSolutions().get(i);
			for (int j = 0; j < sol.getProposedClusters().size(); j++) {
				ETNACluster cluster = sol.getProposedClusters().get(j);
				if (cluster.getName().equals(selectedCluster.getName())) {
					sol.getProposedClusters().remove(j);
					break;
				}
			}

			boolean flag = false;
			for (ETNACluster cl : sol.getEtnaProperties()) {
				if (cl.getName().equals(selectedCluster.getName())) {
					flag = true;
					break;
				}

			}

			if (!flag) {
				sol.getEtnaProperties().add(acceptedCluster);
			}

		}

	
	    //add cluster to list with accepted clusters
		ontologyInstance.getClustersToBeAccepted().add(selectedClusterClone);
		init();
	}

	void sendInformEmailForCategory(ProposedApplicationCategory cat,
			String emailAddress) {
		// String adminEmail = PortalUtil.getPortalProperties().getProperty(
		// "admin.email.from.address");
		try {
			final String username = "cloud4All.SAT@gmail.com";
			final String password = "cloud1@3";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("cloud4All.SAT@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddress));
			message.setSubject("Taxonomy group approval");

			message.setText("An administrator has approved your proposed category named: "
					+ cat.getName());

			Transport.send(message);

		} catch (Exception e) {

		}
	}

	void sendInformEmailForCluster(ETNACluster cluster, String emailAddress) {
		// String adminEmail = PortalUtil.getPortalProperties().getProperty(
		// "admin.email.from.address");
		try {
			final String username = "cloud4All.SAT@gmail.com";
			final String password = "cloud1@3";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("cloud4All.SAT@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddress));
			message.setSubject("Taxonomy group approval");

			message.setText("An administrator has approved your taxonomy group named: "
					+ cluster.getName());

			Transport.send(message);

		} catch (Exception e) {

		}
	}

	public void deleteCluster() {
		// send email to proponent
		try {
			String emailAddress = selectedClusterClone.getProponentEmail();
			final String username = "cloud4All.SAT@gmail.com";
			final String password = "cloud1@3";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("cloud4All.SAT@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddress));
			message.setSubject("Taxonomy group rejected");
			message.setText("An administrator has rejected your taxonomy group named '"
					+ selectedClusterClone.getName()
					+ "' for the following reason:\n" + rejectReason);
			Transport.send(message);

		} catch (Exception e) {

		}
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);

		ontologyInstance.getProposedEASTINProperties().remove(selectedCluster);
		ontologyInstance.getProposedEASTINPropertiesOriginal().remove(
				selectedCluster);

		// delete cluster from all solutions
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			for (int j = 0; j < sol.getEtnaProperties().size(); j++) {
				ETNACluster cluster = sol.getEtnaProperties().get(j);
				if (cluster.getName().equals(selectedCluster.getName())) {
					sol.getEtnaProperties().remove(j);
					break;
				}
			}
		}
		
		 //update solutions, in case change happened before save
	    for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			for (int j = 0; j < sol.getProposedClusters().size(); j++) {
				ETNACluster cluster = sol.getProposedClusters().get(j);
				if (cluster.getName().equals(selectedCluster.getName())) {
					sol.getProposedClusters().remove(j);
					break;
				}
			}
		}

		init();

	}

	public void init() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();

			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);
			
			// merge lists
			ontologyInstance
					.setAllProposedItems(new ArrayList<EASTINProperty>());
			// check in order to keep "refers to solution name"
			// nobody knows why is lost
			for (EASTINProperty p1 : ontologyInstance.getProposedEASTINItems()) {
				for (EASTINProperty p2 : ontologyInstance
						.getProposedEASTINItemsOriginal()) {
					if (p1.toString().equals(p2.toString())) {
						p2.setAllSolutionsString(p1.getAllSolutionsString());
						p2.setRefersToSolutionID(p1.getRefersToSolutionID());
						p2.setRefersToSolutionName(p1.getRefersToSolutionName());
					}
				}
			}


			for (EASTINProperty p1 : ontologyInstance
					.getProposedMeasureEASTINItems()) {
				for (EASTINProperty p2 : ontologyInstance
						.getProposedMeasureEASTINItemsOriginal()) {
					if (p1.toString().equals(p2.toString())) {
						p2.setAllSolutionsString(p1.getAllSolutionsString());
						p2.setRefersToSolutionID(p1.getRefersToSolutionID());
						p2.setRefersToSolutionName(p1.getRefersToSolutionName());
					}
				}
			}

			for (EASTINProperty p : ontologyInstance
					.getProposedEASTINItemsOriginal())
				if(!ontologyInstance.getAllProposedItems().contains(p))
				ontologyInstance.getAllProposedItems().add(p);

			for (EASTINProperty p : ontologyInstance
					.getProposedMeasureEASTINItemsOriginal())
				if(!ontologyInstance.getAllProposedItems().contains(p))
				ontologyInstance.getAllProposedItems().add(p);
			
			//code to avoid inserting clusters that were not saved
			//-------------------
			List<ETNACluster> list = new ArrayList<ETNACluster>();
			for(ETNACluster c : ontologyInstance.getProposedEASTINPropertiesOriginal()){
				list.add(c);
			}
			
			ontologyInstance.setProposedEASTINProperties(list);
			//---------------------------------------------------

			// remove empty cluster
			for (int i = ontologyInstance.getProposedEASTINProperties().size() - 1; i >= 0; i--)
				if (ontologyInstance.getProposedEASTINProperties().get(i)
						.getName().equals("")) {

					ontologyInstance.getProposedEASTINProperties().remove(i);
				}

			for (int i = 0; i < ontologyInstance.getProposedEASTINProperties()
					.size(); i++) {
				ETNACluster cl = ontologyInstance.getProposedEASTINProperties()
						.get(i);
				for (int j = 0; j < ontologyInstance.getSolutions().size(); j++) {
					if (cl.getAllproperties().size() != 0
							&& ontologyInstance
									.getSolutions()
									.get(j)
									.getOntologyURI()
									.equals(cl.getAllproperties().get(0)
											.getRefersToSolution())) {
						for (int kk = 0; kk < cl.getAllproperties().size(); kk++) {
							cl.getAllproperties()
									.get(kk)
									.setRefersToSolutionID(
											ontologyInstance.getSolutions()
													.get(j).getId());
							cl.getAllproperties()
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
										.equals(cl.getAllproperties().get(kk)
												.getProponentEmail())) {
									cl.getAllproperties()
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
			root = new DefaultTreeNode("Root", null);
			root = createOntologyTree(ontologyInstance.getOntology(), root);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public DefaultTreeNode createOntologyTree(Ontology ontology,
			DefaultTreeNode root) {
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		List<OntologyClass> list = ontologyInstance
				.getSolutionClassesStructured();

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

	public ProposedApplicationCategory getSelectedProposedApplicationCategory() {
		return selectedProposedApplicationCategory;
	}

	public void setSelectedProposedApplicationCategory(
			ProposedApplicationCategory selectedProposedApplicationCategory) {
		this.selectedCategory = "";
		this.selectedProposedApplicationCategory = selectedProposedApplicationCategory;
		this.selectedProposedApplicationCategoryClone = selectedProposedApplicationCategory
				.clone();
	}

	public ProposedApplicationCategory getSelectedProposedApplicationCategoryClone() {
		return selectedProposedApplicationCategoryClone;
	}

	public void setSelectedProposedApplicationCategoryClone(
			ProposedApplicationCategory selectedProposedApplicationCategoryClone) {
		this.selectedProposedApplicationCategoryClone = selectedProposedApplicationCategoryClone;
	}

	public List<EASTINProperty> getProposedEASTINItems() {
		return proposedEASTINItems;
	}

	public void setProposedEASTINItems(List<EASTINProperty> proposedEASTINItems) {
		this.proposedEASTINItems = proposedEASTINItems;
	}

	public EASTINProperty getSelectedEASTINItem() {
		return selectedEASTINItem;
	}

	public void setSelectedEASTINItem(EASTINProperty selectedEASTINItem) {

		this.selectedEASTINItem = selectedEASTINItem;
		this.selectedEASTINItemClone = selectedEASTINItem.clone();
	}

	public EASTINProperty getSelectedEASTINItemClone() {
		return selectedEASTINItemClone;
	}

	public void setSelectedEASTINItemClone(
			EASTINProperty selectedEASTINItemClone) {
		this.selectedEASTINItemClone = selectedEASTINItemClone;
	}

	public void applyItemChanges() {
		// check if already exists in the same cluster
		//find cluster
		ETNACluster cluster2=null;
		for(int i=0;i<ontologyInstance.getEtnaClusterOriginal().size();i++){
			if(ontologyInstance.getEtnaClusterOriginal().get(i).getName().equals(selectedEASTINItemClone.getBelongsToCluster())){
				cluster2=ontologyInstance.getEtnaClusterOriginal().get(i);
				break;
			}
		}
		if(cluster2!=null){			
			for(int i=0;i<cluster2.getAllproperties().size();i++){
				if(selectedEASTINItemClone.getName().equals(cluster2.getAllproperties().get(i).getName())){
					FacesContext.getCurrentInstance().addMessage(
							"ProposalItemMessage",
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"The item name already exists in the cluster", ""));
					return;
				}
			}
		}
		int position=ontologyInstance.getAllProposedItems().indexOf(selectedEASTINItem);
		//check in existing proposals
		for(int i=0;i<ontologyInstance.getAllProposedItems().size();i++){
			EASTINProperty pr=ontologyInstance.getAllProposedItems().get(i);
			if(pr.getName().equals(selectedEASTINItemClone.getName())&&pr.getBelongsToCluster().equals(selectedEASTINItemClone.getBelongsToCluster())&&i!=position){
				FacesContext.getCurrentInstance().addMessage(
						"ProposalItemMessage",
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"This item name has already been proposed", ""));
				return;
			}
		}
		
		
	if (selectedEASTINItemClone.getName().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"ProposalItemMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide the name of the item", ""));
			// selectedEASTINItemClone.setName(selectedEASTINItem.getName());
			return;
		}
		if (selectedEASTINItemClone.getDefinition().trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(
					"ProposalItemMessage",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Please provide the description of the item", ""));
			// selectedEASTINItemClone.setDefinition(selectedEASTINItem.getDefinition());
			return;
		}

		int pos = ontologyInstance.getAllProposedItems().indexOf(
				selectedEASTINItem);

		if (pos != -1) {

			ontologyInstance.getAllProposedItems().remove(selectedEASTINItem);

			// remove from items and original items

			if (selectedEASTINItem.getType().toLowerCase().equals("attribute")) {
				ontologyInstance.getProposedEASTINItems().remove(
						selectedEASTINItem);
				ontologyInstance.getProposedEASTINItemsOriginal().remove(
						selectedEASTINItem);

			} else {
				ontologyInstance.getProposedMeasureEASTINItems().remove(
						selectedEASTINItem);
				ontologyInstance.getProposedMeasureEASTINItemsOriginal()
						.remove(selectedEASTINItem);

			}

			// add clone item (the new item) to items and original items
			// according to its type
			selectedEASTINItemClone.setEdit(true);
		//	selectedEASTINItemClone.setProposed(true);
			if (selectedEASTINItemClone.getType().toLowerCase()
					.equals("attribute")) {
				ontologyInstance.getProposedEASTINItems().add(
						selectedEASTINItemClone);
				ontologyInstance.getProposedEASTINItemsOriginal().add(
						selectedEASTINItemClone);
			} else {
				ontologyInstance.getProposedMeasureEASTINItems().add(
						selectedEASTINItemClone);
				ontologyInstance.getProposedMeasureEASTINItemsOriginal().add(
						selectedEASTINItemClone);
			}

			// apply changes to clusters of all solutions
			for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
				Solution sol = ontologyInstance.getSolutions().get(i);
				boolean flag = false;
				for (int j = 0; j < sol.getEtnaProperties().size(); j++) {

					ETNACluster cluster = sol.getEtnaProperties().get(j);
					for (int kk = 0; kk < cluster.getProperties().size(); kk++) {

						EASTINProperty prop = cluster.getProperties().get(kk);

						if (prop.getName().equals(selectedEASTINItem.getName())
								&& selectedEASTINItem.getBelongsToCluster()
										.equals(prop.getBelongsToCluster())) {

							// remove old item

							cluster.getProperties().remove(kk);
							flag = true;

							break;
						}
					}
					
					if(flag)
						break;
					
					for (int kk = 0; kk < cluster.getMeasureProperties().size(); kk++) {
						EASTINProperty prop = cluster.getMeasureProperties()
								.get(kk);
						if (prop.getName().equals(selectedEASTINItem.getName())
								&& selectedEASTINItem.getBelongsToCluster()
										.equals(prop.getBelongsToCluster())) {
							// remove old item
							cluster.getMeasureProperties().remove(kk);
							flag = true;

							break;
						}
					}
					
					if(flag)
						break;
				}
				
				//the solution contained the proposed item
				//apply changes 
				if(flag){
					
				// add clone item in the correct cluster	
				for (ETNACluster cl : sol.getEtnaProperties()) {
					if (cl.getName().equals(
							selectedEASTINItemClone.getBelongsToCluster())) {

						cl.getAllproperties().add(selectedEASTINItemClone);

						if (selectedEASTINItemClone.getType().equalsIgnoreCase(
								"measure"))
							cl.getMeasureProperties().add(
									selectedEASTINItemClone);
						else
							cl.getProperties().add(selectedEASTINItemClone);
					}
				}
				
				if(selectedEASTINItem.getType().equalsIgnoreCase("attribute"))
					sol.getProposedAttributeClusterItems().remove(selectedEASTINItem);
				else
					sol.getProposedMeasureClusterItems().remove(selectedEASTINItem);
				
				if(selectedEASTINItemClone.getType().equalsIgnoreCase("attribute"))
					sol.getProposedAttributeClusterItems().add(selectedEASTINItemClone);
				else
					sol.getProposedMeasureClusterItems().add(selectedEASTINItemClone);
				
				
				}
			}
			

			init();

		}
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.update("form");
		rc.update("editItemDialog");
		rc.execute("editItemDialog.hide()");

	}

	public void rejectItem() {
		// send email to proponent
		try {
			String emailAddress = selectedEASTINItemClone.getProponentEmail();
			final String username = "cloud4All.SAT@gmail.com";
			final String password = "cloud1@3";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("cloud4All.SAT@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddress));
			message.setSubject("Taxonomy item rejected");
			message.setText("An administrator has rejected your taxonomy item named '"
					+ selectedEASTINItemClone.getName()
					+ "' for the following reason:\n" + rejectReason);
			Transport.send(message);

		} catch (Exception e) {

		}

		int pos = ontologyInstance.getAllProposedItems().indexOf(
				selectedEASTINItem);
		int pos2 = ontologyInstance.getProposedEASTINItemsOriginal().indexOf(
				selectedEASTINItem);
		int pos3 = ontologyInstance.getProposedMeasureEASTINItemsOriginal()
				.indexOf(selectedEASTINItem);

		if (pos != -1) {

			// remove rejected item from all lists and apply the changes to
			// solutions

			ontologyInstance.getAllProposedItems().remove(pos);

			if (selectedEASTINItem.getType().toLowerCase().equals("attribute")
					&& pos2 != -1)
				ontologyInstance.getProposedEASTINItemsOriginal().remove(pos2);
			else
				ontologyInstance.getProposedMeasureEASTINItemsOriginal()
						.remove(pos3);

			// apply changes to all solutions
			for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
				Solution sol = ontologyInstance.getSolutions().get(i);
				for (int j = 0; j < sol.getEtnaProperties().size(); j++) {

					ETNACluster cluster = sol.getEtnaProperties().get(j);
					for (int kk = 0; kk < cluster.getProperties().size(); kk++) {
						EASTINProperty prop = cluster.getProperties().get(kk);
						if (prop.getName().equals(selectedEASTINItem.getName())
								&& selectedEASTINItem.getBelongsToCluster()
										.equals(prop.getBelongsToCluster())) {
							cluster.getProperties().remove(kk);
							break;
						}
					}
					for (int kk = 0; kk < cluster.getMeasureProperties().size(); kk++) {
						EASTINProperty prop = cluster.getMeasureProperties()
								.get(kk);
						if (prop.getName().equals(selectedEASTINItem.getName())
								&& selectedEASTINItem.getBelongsToCluster()
										.equals(prop.getBelongsToCluster())) {
							cluster.getMeasureProperties().remove(kk);
							break;
						}
					}
				}
			}
			
			//update proposals in solutions, in case change happened before saving
			for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
				Solution sol = ontologyInstance.getSolutions().get(i);
				
				sol.getProposedAttributeClusterItems().remove(selectedEASTINItem);
				sol.getProposedMeasureClusterItems().remove(selectedEASTINItem);
			}

			init();

		}
	}

	public void clearRejectReason() {
		rejectReason = "";
	}

	public void approveItem() {
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
		
		ontologyInstance.getItemsToBeAccepted().add(selectedEASTINItem);
		
		
		EASTINProperty newproperty = selectedEASTINItem.clone();
		newproperty.setProposed(false);
		
		for(ETNACluster cl : ontologyInstance.getEtnaClusterOriginal()){
			if(cl.getName().equals(selectedEASTINItem.getBelongsToCluster())){
				if(selectedEASTINItem.getType().equalsIgnoreCase("attribute"))
					cl.getProperties().add(newproperty);
				else
					cl.getMeasureProperties().add(newproperty);
				
			}
		}
		
		List<ETNACluster> clusters = new ArrayList<ETNACluster>();
		for(ETNACluster c : ontologyInstance.getEtnaClusterOriginal()){
			clusters.add(c.clone());
		}
		ontologyInstance.setEtnaCluster(clusters);
		
		
		 ontologyInstance.getAllProposedItems().remove(selectedEASTINItem);
		 ontologyInstance.getProposedEASTINItemsOriginal().remove(
		 selectedEASTINItem);
		 ontologyInstance.getProposedMeasureEASTINItemsOriginal().remove(
				 selectedEASTINItem);

		// apply changes to clusters of all solutions
		EASTINProperty itemToAdd=selectedEASTINItem.clone();
		itemToAdd.setProposed(false);
		
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			
			Solution sol = ontologyInstance.getSolutions().get(i);
			boolean flag = false;
			for (int j = 0; j < sol.getEtnaProperties().size(); j++) {

				ETNACluster cluster = sol.getEtnaProperties().get(j);
				for (int kk = 0; kk < cluster.getProperties().size(); kk++) {
					EASTINProperty prop = cluster.getProperties().get(kk);
					if (prop.getName().equals(selectedEASTINItem.getName())
							&& selectedEASTINItem.getBelongsToCluster().equals(
									prop.getBelongsToCluster())&&prop.isProposed()) {
						cluster.getProperties().remove(kk);
						flag = true;
					}					
				}
				for (int kk = 0; kk < cluster.getMeasureProperties().size(); kk++) {
					EASTINProperty prop = cluster.getMeasureProperties()
							.get(kk);
					if (prop.getName().equals(selectedEASTINItem.getName())
							&& selectedEASTINItem.getBelongsToCluster().equals(
									prop.getBelongsToCluster())&&prop.isProposed()) {
						cluster.getMeasureProperties().remove(kk);	
						flag = true;
					}
				}

			}
			
			if(flag)
				for(ETNACluster cluster : sol.getEtnaProperties()){
					if(cluster.getName().equals(itemToAdd.getBelongsToCluster())){
						if (itemToAdd.getType().equalsIgnoreCase("attribute")) {
							cluster.getProperties().add(itemToAdd);
						} else {
							cluster.getMeasureProperties().add(itemToAdd);
						}
					}
				}
			
		
		}
		
		//update solutions 
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			for (int j = 0; j < sol.getEtnaProperties().size(); j++) {

				ETNACluster cluster = sol.getEtnaProperties().get(j);
				if(cluster.getName().equals(itemToAdd.getBelongsToCluster())){
					if(itemToAdd.getType().equalsIgnoreCase("attribute")
							&& !cluster.getProperties().contains(itemToAdd)){
						cluster.getProperties().add(itemToAdd);
					}else if(!itemToAdd.getType().equalsIgnoreCase("attribute")
							&& !cluster.getMeasureProperties().contains(itemToAdd)){
						cluster.getMeasureProperties().add(itemToAdd);
					}
				}
			}
			
			
		}
			
		//update proposals in solutions, in case change happened before saving
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			
			sol.getProposedAttributeClusterItems().remove(selectedEASTINItem);
			sol.getProposedMeasureClusterItems().remove(selectedEASTINItem);
		}
		
		if (ontologyInstance.getHashmapOfProperties().containsKey(
				selectedEASTINItem.getName())) {
			ontologyInstance.getHashmapOfProperties().remove(
					selectedEASTINItem.getName());
			selectedEASTINItem.setFound(true);
			ontologyInstance.getHashmapOfProperties().put(
					selectedEASTINItem.getName(), selectedEASTINItem);
		}
		
		init();
	}

	public void approveItemWithEmail() {
		FacesContext context = FacesContext.getCurrentInstance();

		ontologyInstance = (OntologyInstance) context.getApplication()
				.evaluateExpressionGet(context, "#{ontologyInstance}",
						OntologyInstance.class);
		// selectedClusterClone=selectedCluster.clone();
		// send email
		sendInformEmailForItem(selectedEASTINItemClone,
				selectedEASTINItemClone.getProponentEmail());
		ontologyInstance.getItemsToBeAccepted().add(selectedEASTINItem);
		
		
		EASTINProperty newproperty = selectedEASTINItem.clone();
		newproperty.setProposed(false);
		
		for(ETNACluster cl : ontologyInstance.getEtnaClusterOriginal()){
			if(cl.getName().equals(selectedEASTINItem.getBelongsToCluster())){
				if(selectedEASTINItem.getType().equalsIgnoreCase("attribute"))
					cl.getProperties().add(newproperty);
				else
					cl.getMeasureProperties().add(newproperty);
				
			}
		}
		
		List<ETNACluster> clusters = new ArrayList<ETNACluster>();
		for(ETNACluster c : ontologyInstance.getEtnaClusterOriginal()){
			clusters.add(c.clone());
		}
		ontologyInstance.setEtnaCluster(clusters);
		
		
		 ontologyInstance.getAllProposedItems().remove(selectedEASTINItem);
		 ontologyInstance.getProposedEASTINItemsOriginal().remove(
		 selectedEASTINItem);
		 ontologyInstance.getProposedMeasureEASTINItemsOriginal().remove(
				 selectedEASTINItem);

		// apply changes to clusters of all solutions
		EASTINProperty itemToAdd=selectedEASTINItem.clone();
		itemToAdd.setProposed(false);
		
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			
			Solution sol = ontologyInstance.getSolutions().get(i);
			boolean flag = false;
			for (int j = 0; j < sol.getEtnaProperties().size(); j++) {

				ETNACluster cluster = sol.getEtnaProperties().get(j);
				for (int kk = 0; kk < cluster.getProperties().size(); kk++) {
					EASTINProperty prop = cluster.getProperties().get(kk);
					if (prop.getName().equals(selectedEASTINItem.getName())
							&& selectedEASTINItem.getBelongsToCluster().equals(
									prop.getBelongsToCluster())&&prop.isProposed()) {
						cluster.getProperties().remove(kk);
						flag = true;
					}					
				}
				for (int kk = 0; kk < cluster.getMeasureProperties().size(); kk++) {
					EASTINProperty prop = cluster.getMeasureProperties()
							.get(kk);
					if (prop.getName().equals(selectedEASTINItem.getName())
							&& selectedEASTINItem.getBelongsToCluster().equals(
									prop.getBelongsToCluster())&&prop.isProposed()) {
						cluster.getMeasureProperties().remove(kk);	
						flag = true;
					}
				}

			}
			
			if(flag)
				for(ETNACluster cluster : sol.getEtnaProperties()){
					if(cluster.getName().equals(itemToAdd.getBelongsToCluster())){
						if (itemToAdd.getType().equalsIgnoreCase("attribute")) {
							cluster.getProperties().add(itemToAdd);
						} else {
							cluster.getMeasureProperties().add(itemToAdd);
						}
					}
				}
			
		
		}
			
		//update proposals in solutions, in case change happened before saving
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			Solution sol = ontologyInstance.getSolutions().get(i);
			
			sol.getProposedAttributeClusterItems().remove(selectedEASTINItem);
			sol.getProposedMeasureClusterItems().remove(selectedEASTINItem);
		}
		
		if (ontologyInstance.getHashmapOfProperties().containsKey(
				selectedEASTINItem.getName())) {
			ontologyInstance.getHashmapOfProperties().remove(
					selectedEASTINItem.getName());
			selectedEASTINItem.setFound(true);
			ontologyInstance.getHashmapOfProperties().put(
					selectedEASTINItem.getName(), selectedEASTINItem);
		}
		init();
	}

	void sendInformEmailForItem(EASTINProperty item, String emailAddress) {
		// String adminEmail = PortalUtil.getPortalProperties().getProperty(
		// "admin.email.from.address");
		try {
			final String username = "cloud4All.SAT@gmail.com";
			final String password = "cloud1@3";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("cloud4All.SAT@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddress));
			message.setSubject("Taxonomy group approval");
			message.setText("An administrator has approved your taxonomy item named: "
					+ item.getName());
			Transport.send(message);

		} catch (Exception e) {

		}
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public void setSelectedNode(TreeNode selectedNode) {

		selectedCategory = (String) selectedNode.getData();
		this.selectedParentNode = selectedNode;
	}

	private void clearSelections(TreeNode root) {
		List<TreeNode> tree = root.getChildren();
		for (TreeNode node : tree) {
			node.setSelected(false);
			node.setExpanded(false);
			clearSelections(node);
		}
		this.selectedParentNode = null;
	}

	public void onNodeSelect(NodeSelectEvent event) {
		selectedParentCategory = (String) event.getTreeNode().getData();
		root.setSelected(false);
		clearSelections(root);
		selectItemInTree(root, selectedParentCategory);

	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	public void createParentOntologyTree() {
		proposedCategoryParent = selectedProposedApplicationCategoryClone
				.getParent();
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);

		root = createOntologyTree(ontologyInstance.getOntology(), root);
		selectedProposedCategory = "";
		root.setSelected(false);
		clearSelections(root);
		selectedParentNode = null;
		selectItemInTree(root, proposedCategoryParent);

	}

	public void resetSelection() {
		FacesContext context = FacesContext.getCurrentInstance();
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		root = createOntologyTree(ontologyInstance.getOntology(), root);
		selectedProposedCategory = "";
		selectedParentNode = null;
		clearSelections(root);
		selectItemInTree(root, proposedCategoryParent);

	}

	public OntologyInstance getOntologyInstance() {
		return ontologyInstance;
	}

	public void setOntologyInstance(OntologyInstance ontologyInstance) {
		this.ontologyInstance = ontologyInstance;
	}

	public String getSelectedParentCategory() {
		return selectedParentCategory;
	}

	public void setSelectedParentCategory(String selectedParentCategory) {
		this.selectedParentCategory = selectedParentCategory;
	}

	public String getSelectedProposedCategory() {
		return selectedProposedCategory;
	}

	public void setSelectedProposedCategory(String selectedProposedCategory) {
		this.selectedProposedCategory = selectedProposedCategory;
	}

	public String getProposedCategoryParent() {
		return proposedCategoryParent;
	}

	public void setProposedCategoryParent(String proposedCategoryParent) {
		this.proposedCategoryParent = proposedCategoryParent;
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

			selectedParentNode = root;
			return;
		} else
			for (int i = 0; i < root.getChildCount(); i++) {
				selectItemInTree(root.getChildren().get(i), nodeName);
			}
	}

	public void changeCategory() {
		selectedProposedApplicationCategoryClone
				.setParent(selectedParentCategory);
	}

	public TreeNode getSelectedParentNode() {
		return selectedParentNode;
	}

	public void setSelectedParentNode(TreeNode selectedParentNode) {
		this.selectedParentNode = selectedParentNode;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

}
