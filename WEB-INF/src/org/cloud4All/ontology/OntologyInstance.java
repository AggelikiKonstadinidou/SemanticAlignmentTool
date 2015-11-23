package org.cloud4All.ontology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.cloud4All.ProposedApplicationCategory;
import org.cloud4All.Setting;
import org.cloud4All.Solution;
import org.cloud4All.UserBean;
import org.cloud4All.step2Bean;
import org.cloud4All.IPR.DiscountSchema;
import org.cloud4All.IPR.EULA;
import org.cloud4All.IPR.SLA;
import org.cloud4All.IPR.SolutionUserSchema;
import org.cloud4All.IPR.Vendor;
import org.primefaces.context.RequestContext;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.sparql.util.Context;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.portlet.PortletProps;

@ManagedBean(name = "ontologyInstance", eager = true)
@ApplicationScoped
public class OntologyInstance {

	private List<Solution> solutions = new ArrayList<Solution>();
	private List<RegistryTerm> registryTerms = new ArrayList<RegistryTerm>();
	private List<RegistryTerm> proposedRegistryTerms = new ArrayList<RegistryTerm>();
	private Ontology ontology;
	private List<OntologyClass> solutionClassesStructured;
	private List<String> registryTermsAsStrings = new ArrayList<String>();
	private List<String> platformList = new ArrayList<String>();
	private List<String> devicesList = new ArrayList<String>();
	private List<String> countriesList = new ArrayList<String>();
	private List<String> commercialCostCurrency = new ArrayList<String>();
	private List<String> costPaymentChargeType = new ArrayList<String>();
	private List<Vendor> vendors = new ArrayList<Vendor>();
	private Map<String, Integer> vendorsUsers = new HashMap<String, Integer>();
	private static Map<String, Solution> solutionsMap;
	private static Map<String, EASTINProperty> EASTINPropertiesMap;
	private static Map<String, ETNACluster> clusterPropertiesMap;
	private List<SolutionUserSchema> solutionUserSchemas = new ArrayList<SolutionUserSchema>();
	private List<ETNACluster> proposedEASTINProperties = new ArrayList<ETNACluster>();
	private List<ETNACluster> proposedEASTINPropertiesOriginal = new ArrayList<ETNACluster>();
	private List<ETNACluster> etnaCluster = new ArrayList<ETNACluster>();
	private List<ETNACluster> etnaClusterOriginal = new ArrayList<ETNACluster>();
	private List<ETNACluster> clustersToShowInView = new ArrayList<ETNACluster>();
	private List<ETNACluster> shownClusters = new ArrayList<ETNACluster>();
	private List<ETNACluster> hiddenClusters = new ArrayList<ETNACluster>();

	private List<ProposedApplicationCategory> proposedApplicationCategories = new ArrayList<ProposedApplicationCategory>();
	private List<ProposedApplicationCategory> proposedApplicationCategoriesOriginal = new ArrayList<ProposedApplicationCategory>();
	// proposed attribute items
	private List<EASTINProperty> proposedEASTINItems = new ArrayList<EASTINProperty>();
	// proposed measure items
	private volatile List<EASTINProperty> proposedMeasureEASTINItems = new ArrayList<EASTINProperty>();
	private volatile List<EASTINProperty> proposedMeasureEASTINItemsOriginal = new ArrayList<EASTINProperty>();
	// all proposed items
	private List<EASTINProperty> allProposedItems = new ArrayList<EASTINProperty>();
	private List<EASTINProperty> proposedEASTINItemsOriginal = new ArrayList<EASTINProperty>();
	private HashMap<String, RegistryTerm> ontologyTerms = new HashMap<String, RegistryTerm>();
	private HashMap<String, RegistryTerm> ontologyAliases = new HashMap<String, RegistryTerm>();
	private List<RegistryTerm> registryAliases = new ArrayList<RegistryTerm>();
	private List<RegistryTerm> proposedRegistryAliases = new ArrayList<RegistryTerm>();
	private HashMap<String, Solution> solutionsHashmap = new HashMap<String, Solution>();
	private HashMap<String, ProposedApplicationCategory> proposedApplicationCategoriesMap = new HashMap<String, ProposedApplicationCategory>();
	private List<String> oldSolutionUris = new ArrayList<String>();
	private List<ETNACluster> clustersToBeAccepted = new ArrayList<ETNACluster>();
	private List<EASTINProperty> itemsToBeAccepted = new ArrayList<EASTINProperty>();
	private List<SLA> sla = new ArrayList<SLA>();
	private List<EULA> eula = new ArrayList<EULA>();
	private List<SolutionUserSchema> userSchemaForInsert = new ArrayList<SolutionUserSchema>();
	private HashMap<String, ETNACluster> hashmapOfClusters = new HashMap<String, ETNACluster>();
	private HashMap<String, EASTINProperty> hashmapOfProperties = new HashMap<String, EASTINProperty>();
	private String numberOfWebServiceTerms = "";
	private String numberOfWebServiceProposedTerms = "";
	private CounterThread thread;
	private ArrayList<SettingsHandler> settingsHandlers = new ArrayList<SettingsHandler>();
	private ArrayList<String> settingsHandlersNames = new ArrayList<String>();
	private ArrayList<String> categoriesNames = new ArrayList<String>();
	private ArrayList<Solution> services = new ArrayList<Solution>();

	public OntologyInstance() {
		System.gc();
		ontology = new Ontology();
		ontology.loadOntology();
		// load all registry terms
		System.out.println("Loading registry terms...");

		// load everything from ontology

		System.out.println("Loading solutions...");
		System.out.println("0...");
		etnaCluster = ontology.getETNATaxonomy();
		etnaClusterOriginal.addAll(etnaCluster);

		settingsHandlers = ontology.loadSettingsHandlers();

		settingsHandlersNames = new ArrayList<String>();
		for (SettingsHandler temp : settingsHandlers) {
			if (!temp.getRealName().equals("No"))
				settingsHandlersNames.add(temp.getRealName());
		}

		settingsHandlersNames.add(0, "No");

		solutions = ontology.loadAllSolutions(etnaCluster);
		System.out.println("1...");
		solutionClassesStructured = ontology.getSolutionsClassesStructured();
		for (OntologyClass temp : solutionClassesStructured) {
			categoriesNames.add(temp.getClassName());
			findSelectedCategoryThroughTree(temp);
		}

		platformList = ontology.getInstancesOfClass("Platforms");
		devicesList = ontology.getInstancesOfClass("Devices");

		System.out.println("2...");
		
		//load services
		services = ontology.loadAllServices();
		System.out.println(services.size());
		for(Solution sol : services){
			System.out.println(sol.toString());
		}
		Locale l = Locale.ENGLISH;
		String[] locales = l.getISOCountries();
		for (int i = 0; i < locales.length; i++) {
			String str = locales[i];
			Locale obj = new Locale("", str);
			countriesList.add(obj.getDisplayName(l));
		}
		int k = countriesList.indexOf("Macedonia");
		countriesList.remove(k);
		countriesList.add(k, "FYROM");
		countriesList.add("All countries");
		String[] ar = countriesList.toArray(new String[countriesList.size()]);
		Arrays.sort(ar);
		System.out.println("3...");
		countriesList = new ArrayList<String>();
		for (int i = 0; i < ar.length; i++) {
			countriesList.add(ar[i]);
		}
		countriesList.add(0, "-");
		commercialCostCurrency = ontology
				.getAllowedValuesOfDatatypeProperty("hasCommercialCostCurrency");
		costPaymentChargeType = ontology
				.getAllowedValuesOfDatatypeProperty("hasCostPaymentChargeType");
		System.out.println("4...");
		// load all vendors
		vendors = ontology.loadAllVendors();
		System.out.println("5...");
		// calculate vendors users
		for (int i = 0; i < vendors.size(); i++) {
			Vendor ven = vendors.get(i);
			for (int j = 0; j < solutions.size(); j++) {
				Solution sol = solutions.get(j);
				if (sol.getVendor().equals(ven.getOntologyURI())) {
					if (vendorsUsers.containsKey(ven.getOntologyURI())) {
						int noUsers = vendorsUsers.get(ven.getOntologyURI());
						vendorsUsers.put(ven.getOntologyURI(), noUsers
								+ sol.getUsers().size());
					} else {
						vendorsUsers.put(ven.getOntologyURI(), sol.getUsers()
								.size());
					}
				}
			}
		}
		System.out.println("6...");
		for (int j = 0; j < solutions.size(); j++) {
			Solution sol = solutions.get(j);
			if (vendorsUsers.get(sol.getVendor()) != null)
				sol.setNumberOfUsers(vendorsUsers.get(sol.getVendor()));
			// load vendor reputation score
			for (int i = 0; i < vendors.size(); i++) {
				if (sol.getVendor().equals(vendors.get(i).getOntologyURI())) {
					sol.setVendorReputationScore(vendors.get(i).getReputation()
							.getOverallReputationScore());
				}
			}
		}
		System.out.println("7...");
		// load solutions map
		solutionsMap = new LinkedHashMap<String, Solution>();
		for (int i = 0; i < solutions.size(); i++) {
			solutionsMap.put(solutions.get(i).getOntologyURI(),
					solutions.get(i));
			solutionsHashmap.put(solutions.get(i).getOntologyURI(),
					solutions.get(i));
		}
		System.out.println("8...");
		// load paired solutions in discount schemas
		for (int j = 0; j < solutions.size(); j++) {
			Solution sol = solutions.get(j);
			for (int i = 0; i < sol.getAccessInfoForUsers()
					.getCommercialCostSchema()
					.getDiscountIfUsedWithOtherSolution().size(); i++) {
				DiscountSchema d = sol.getAccessInfoForUsers()
						.getCommercialCostSchema()
						.getDiscountIfUsedWithOtherSolution().get(i);
				if (d.getPairedSolutionString() != null
						&& !d.getPairedSolutionString().equals("")) {
					d.setPairedSolution(solutionsMap.get(d
							.getPairedSolutionString()));
				}
			}
		}
		System.out.println("9...");

		// load all solution user schemas
		solutionUserSchemas = ontology.loadAllSolutionUserSchemas();
		System.out.println("10...");
		proposedEASTINProperties = ontology.loadAllProposedEASTINProperties();
		proposedEASTINPropertiesOriginal.addAll(proposedEASTINProperties);

		clusterPropertiesMap = new LinkedHashMap<String, ETNACluster>();
		for (ETNACluster cl : proposedEASTINPropertiesOriginal) {
			cl.setFound(false);
			hashmapOfClusters.put(cl.getName(), cl);
			clusterPropertiesMap.put(cl.getName(), cl);
		}

		List<List<EASTINProperty>> list = ontology.getETNATaxonomyProposed();
		proposedEASTINItems = list.get(0);
		proposedEASTINItemsOriginal = list.get(0);

		proposedMeasureEASTINItems = list.get(1);
		proposedMeasureEASTINItemsOriginal = list.get(1);

		allProposedItems.addAll(proposedEASTINItems);
		allProposedItems.addAll(proposedMeasureEASTINItems);

		EASTINPropertiesMap = new LinkedHashMap<String, EASTINProperty>();

		for (EASTINProperty prop : allProposedItems) {
			prop.setFound(false);
			prop.setEdit(false);
			hashmapOfProperties.put(prop.getName(), prop);
			EASTINPropertiesMap.put(prop.getName(), prop);
		}

		System.out.println("11...");
		// update ontology uris

		// for(int i=0;i<solutions.size();i++){
		// ontology.getUris().add(solutions.get(i).getOntologyURI().toLowerCase());
		// }

		for (int i = 0; i < solutions.size(); i++) {
			for (int j = 0; j < solutions.get(i).getSettings().size(); j++) {
				Setting set = solutions.get(i).getSettings().get(j);
				String uri = set.getCurrentURI();

				DatatypeProperty dProperty = ontology.getOntologyModel()
						.getDatatypeProperty(uri);
				if (dProperty.getRange() != null) {
					String type = dProperty.getRange().toString().split("#")[1];
					if (type.equals("int")) {
						set.setType("integer");
					} else if (type.equals("boolean")) {
						set.setType("boolean");
					} else if (type.equals("float")) {
						set.setType("double");
					} else if (type.equals("double")) {
						set.setType("double");
					} else
						set.setType("string");
				} else
					System.out.println();
			}
		}
		// load all proposed application categories
		proposedApplicationCategories = ontology
				.loadAllProposedApplicationCategories();
		proposedApplicationCategoriesOriginal
				.addAll(proposedApplicationCategories);
		// fill the categories hashMap
		for (ProposedApplicationCategory cat : proposedApplicationCategories) {
			cat.setFound(false);
			proposedApplicationCategoriesMap.put(cat.getName(), cat);
		}

		// get registry and proposed registry terms (lists,hashmaps)
		List<RegistryTerm> terms = ontology.getInstancesOfTerms("Registry");
		for (RegistryTerm term : terms) {
			if (!term.getNotes().equals("ALIAS")) {
				registryTerms.add(term);
				ontologyTerms.put(term.getId(), term);
				registryTermsAsStrings.add(term.getName());
			} else {
				ontologyAliases.put(term.getId(), term);
				registryAliases.add(term);
			}
		}

		List<RegistryTerm> terms2 = ontology
				.getInstancesOfTerms("ProposedRegistryTerms");

		for (RegistryTerm term : terms2) {
			if (!term.getNotes().equals("ALIAS")) {
				proposedRegistryTerms.add(term);
				ontologyTerms.put(term.getId(), term);
				registryTermsAsStrings.add(term.getName());
			} else {
				ontologyAliases.put(term.getId(), term);
				proposedRegistryAliases.add(term);
			}

		}

		System.out.println("Finished loading from ontology");

		try {

			// // create counter thread and start it
			// thread = new CounterThread();
			// thread.start();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void findSelectedCategoryThroughTree(OntologyClass c) {
		for (OntologyClass temp2 : c.getChildren()) {
			categoriesNames.add(temp2.getClassName());
			findSelectedCategoryThroughTree(temp2);
		}
	}

	public void absoluteCheck() {

		/**
		 * define clone lists before start checking in order to have a specific
		 * instance of the ontology at the time that we start saving
		 */

		ArrayList<String> oldSolutionUrisClone = new ArrayList<String>();
		for (String s : oldSolutionUris) {
			oldSolutionUrisClone.add(s);
		}

		ArrayList<Solution> solutionsClone = new ArrayList<Solution>();
		for (Solution sol : solutions) {
			solutionsClone.add(sol.clone());
		}

		ArrayList<ProposedApplicationCategory> proposedApplicationCategoriesClone = new ArrayList<ProposedApplicationCategory>();
		for (ProposedApplicationCategory cat : proposedApplicationCategoriesOriginal) {
			proposedApplicationCategoriesClone.add(cat.clone());
			cat.setEdit(false);
		}

		ArrayList<ETNACluster> clustersToBeAcceptedClone = new ArrayList<ETNACluster>();
		for (ETNACluster c : clustersToBeAccepted) {
			clustersToBeAcceptedClone.add(c.clone());
		}

		ArrayList<ETNACluster> proposedEASTINPropertiesOriginalClone = new ArrayList<ETNACluster>();
		for (ETNACluster c : proposedEASTINPropertiesOriginal) {
			ETNACluster cloneCluster = c.clone();
			cloneCluster.setEdit(c.isEdit());
			proposedEASTINPropertiesOriginalClone.add(cloneCluster);
			c.setEdit(false);
		}

		ArrayList<EASTINProperty> itemsToBeAcceptedClone = new ArrayList<EASTINProperty>();
		for (EASTINProperty prop : itemsToBeAccepted) {
			itemsToBeAcceptedClone.add(prop.clone());
		}

		ArrayList<EASTINProperty> allProposedItemsClone = new ArrayList<EASTINProperty>();
		for (EASTINProperty prop : allProposedItems) {
			EASTINProperty cloneProperty = prop.clone();
			cloneProperty.setEdit(prop.isEdit());
			allProposedItemsClone.add(cloneProperty);
			prop.setEdit(false);
		}

		ArrayList<SLA> slaClone = new ArrayList<SLA>();
		for (SLA temp : sla) {
			slaClone.add(temp.clone());
		}

		ArrayList<EULA> eulaClone = new ArrayList<EULA>();
		for (EULA temp : eula) {
			eulaClone.add(temp.clone());
		}

		ArrayList<SolutionUserSchema> userSchemaForInsertClone = new ArrayList<SolutionUserSchema>();
		for (SolutionUserSchema temp : userSchemaForInsert) {
			userSchemaForInsertClone.add(temp.clone());
		}

		ArrayList<RegistryTerm> registryAliasesClone = new ArrayList<RegistryTerm>();
		for (RegistryTerm term : registryAliases) {
			RegistryTerm cloneTerm = term.clone();
			cloneTerm.setEdit(term.isEdit());
			registryAliasesClone.add(cloneTerm);
			term.setEdit(false);
		}

		ArrayList<RegistryTerm> proposedRegistryAliasesClone = new ArrayList<RegistryTerm>();
		for (RegistryTerm term : proposedRegistryAliases) {
			RegistryTerm cloneTerm = term.clone();
			cloneTerm.setEdit(term.isEdit());
			proposedRegistryAliasesClone.add(cloneTerm);
			term.setEdit(false);
		}

		ArrayList<RegistryTerm> registryTermsClone = new ArrayList<RegistryTerm>();
		for (RegistryTerm term : registryTerms) {
			RegistryTerm cloneTerm = term.clone();
			cloneTerm.setEdit(term.isEdit());
			registryTermsClone.add(cloneTerm);
			term.setEdit(false);
		}

		ArrayList<RegistryTerm> proposedRegistryTermsClone = new ArrayList<RegistryTerm>();
		for (RegistryTerm term : proposedRegistryTerms) {
			RegistryTerm cloneTerm = term.clone();
			cloneTerm.setEdit(term.isEdit());
			proposedRegistryTermsClone.add(cloneTerm);
			term.setEdit(false);
		}

		try {
			// // chinese style
			String str = "adminTestItem";
			getOntology().addIndividual("SolutionVendors", str);
			IndividualImpl vendorInd = getOntology().getInstanceOfClass(
					"SolutionVendors", str);
			getOntology().addInstanceDatatypePropertyValue(vendorInd,
					"hasSolutionVendorName", str);
			// // end of style

			// check solutions
			checkSolutions(oldSolutionUrisClone, solutionsClone);

			// check proposedApplicationCategories
			checkCategories(proposedApplicationCategoriesClone);

			// check proposed clusters
			checkClusters(clustersToBeAcceptedClone,
					proposedEASTINPropertiesOriginalClone);

			// check proposed eastin items
			checkItems(itemsToBeAcceptedClone, allProposedItemsClone);

			// save sla and update sla original list
			for (SLA temp : slaClone) {
				getOntology().saveSLA(temp, temp.getSolution());
			}

			for (SLA temp : slaClone) {
				sla.remove(temp);
			}

			// save eulas and update eula original list
			for (EULA temp : eulaClone) {
				getOntology().saveEULA(temp, temp.getSolution());
			}

			for (EULA temp : eulaClone) {
				eula.remove(temp);
			}

			// save solution user schemas and
			// update solutionUserSchemas original list
			for (SolutionUserSchema temp : userSchemaForInsertClone) {
				getOntology().createSolutionUserSchema(temp);
			}

			for (SolutionUserSchema temp : userSchemaForInsertClone) {
				userSchemaForInsert.remove(temp);
			}

			// check registry terms
			checkTerms(registryAliasesClone, proposedRegistryAliasesClone,
					registryTermsClone, proposedRegistryTermsClone);

			// save to owl file
			getOntology().saveToOWL();

			updateHashMapsOfInstance();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void updateHashMapsOfInstance() {

		List<ETNACluster> proposedClusters = new ArrayList<ETNACluster>();
		proposedClusters = ontology.loadAllProposedEASTINProperties();

		clusterPropertiesMap = new LinkedHashMap<String, ETNACluster>();
		hashmapOfClusters = new HashMap<String, ETNACluster>();
		for (ETNACluster cl : proposedClusters) {
			cl.setFound(false);
			hashmapOfClusters.put(cl.getName(), cl);
			clusterPropertiesMap.put(cl.getName(), cl);
		}

		List<List<EASTINProperty>> list = ontology.getETNATaxonomyProposed();
		List<EASTINProperty> proposedAttributeItems = new ArrayList<EASTINProperty>();
		List<EASTINProperty> proposedMeasureItems = new ArrayList<EASTINProperty>();
		List<EASTINProperty> allItems = new ArrayList<EASTINProperty>();

		proposedAttributeItems = list.get(0);
		proposedMeasureItems = list.get(1);
		allItems.addAll(proposedAttributeItems);
		allItems.addAll(proposedMeasureItems);

		EASTINPropertiesMap = new LinkedHashMap<String, EASTINProperty>();
		hashmapOfProperties = new HashMap<String, EASTINProperty>();

		for (EASTINProperty prop : allItems) {
			prop.setFound(false);
			prop.setEdit(false);
			hashmapOfProperties.put(prop.getName(), prop);
			EASTINPropertiesMap.put(prop.getName(), prop);
		}

		// get registry and proposed registry terms (lists,hashmaps)
		List<RegistryTerm> terms = ontology.getInstancesOfTerms("Registry");
		ontologyTerms = new HashMap<String, RegistryTerm>();
		ontologyAliases = new HashMap<String, RegistryTerm>();

		for (RegistryTerm term : terms) {
			if (!term.getNotes().equals("ALIAS")) {
				ontologyTerms.put(term.getId(), term);
				registryTermsAsStrings.add(term.getName());
			} else {
				ontologyAliases.put(term.getId(), term);
			}
		}

		List<RegistryTerm> terms2 = ontology
				.getInstancesOfTerms("ProposedRegistryTerms");
		for (RegistryTerm term : terms2) {
			if (!term.getNotes().equals("ALIAS")) {
				ontologyTerms.put(term.getId(), term);
				registryTermsAsStrings.add(term.getName());
			} else {
				ontologyAliases.put(term.getId(), term);
			}

		}

		System.out.println("save finished successfully..");
	}

	public class CounterThread extends Thread {

		// When false, (i.e. when it's a user thread),
		// the Worker thread continues to run.
		// When true, (i.e. when it's a daemon thread),
		// the Worker thread terminates when the main
		// thread terminates.
		public CounterThread() {
			setDaemon(true);
		}

		public void run() {

			while (true) {
				try {
					// 86400000
					// sleep 15 minutes, 900000
					this.sleep(86400000);// a day
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				absoluteCheck();

			}
		}
	}

	public void checkItems(ArrayList<EASTINProperty> itemsToBeAcceptedClone,
			ArrayList<EASTINProperty> allProposedItemsClone) {

		try {
			// EASTINPropertiesMap
			// make accepted cluster items that are no longer proposed

			for (EASTINProperty prop : itemsToBeAcceptedClone) {
				Iterator it = hashmapOfProperties.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					EASTINProperty oldprop = (EASTINProperty) pairs.getValue();

					if (prop.getInd() != null)
						if (prop.getInd().equals(oldprop.getInd())) {
							oldprop.setFound(true);
							if (!prop.getBelongsToCluster().equals(oldprop)
									|| !prop.getName()
											.equals(oldprop.getName())
									|| !prop.getDefinition().equals(
											oldprop.getDefinition())
									|| !prop.getType()
											.equals(oldprop.getType())) {
								getOntology().applyChangeInProposedEASTINItem(
										prop, oldprop);

							}

						}

				}

				getOntology().acceptProposedEASTINItem(prop);
			}

			// update itemsToBeAccepted
			for (EASTINProperty prop : itemsToBeAcceptedClone) {
				itemsToBeAccepted.remove(prop);
			}

			// check items
			for (EASTINProperty prop : allProposedItemsClone) {

				Iterator it = hashmapOfProperties.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					EASTINProperty oldprop = (EASTINProperty) pairs.getValue();

					// check if item already exists
					// if item does not exist in the hashmap, was saved with
					// solutions
					if (prop.getInd() != null)
						if (prop.getInd().equals(oldprop.getInd())) {

							if (!prop.isEdit())
								oldprop.setFound(true);

							if (prop.getName().equals(oldprop.getName())
									&& prop.isEdit()) {
								oldprop.setFound(true);
								getOntology().applyChangeInProposedEASTINItem(
										prop, oldprop);
							} else if (!prop.getName()
									.equals(oldprop.getName()) && prop.isEdit()) {

								ETNACluster c = null;
								for (int i = 0; i < etnaCluster.size(); i++) {
									ETNACluster cl = etnaCluster.get(i);
									if (cl.getName().equals(
											prop.getBelongsToCluster())) {
										c = cl.clone();
										break;
									}
								}
								getOntology().addNewETNAPropertyInCluster(c,
										prop);
							}

						}

				}

			}

			// delete from ontology,deleted proposed eastin properties
			Iterator it = hashmapOfProperties.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				EASTINProperty prop = (EASTINProperty) pairs.getValue();
				if (!prop.isFound())
					getOntology().deleteEASTINItem(prop);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void checkClusters(ArrayList<ETNACluster> clustersToBeAcceptedClone,
			ArrayList<ETNACluster> proposedEASTINPropertiesOriginalClone) {

		try {

			// insert accepted clusters
			for (ETNACluster c : clustersToBeAcceptedClone) {

				String refersToSolution = "";
				for (int i = 0; i < c.getAllproperties().size(); i++) {
					refersToSolution = c.getAllproperties().get(i)
							.getRefersToSolution();
					break;
				}
				// insert accepted cluster and remove proposed cluster if
				// existed
				if (hashmapOfClusters.containsKey(c.getName()) || c.isEdit())
					getOntology().removeProposedEtnaCluster(c);

				getOntology().createEASTINCluster(c, refersToSolution, false);
			}

			// remove empty cluster from list
			ETNACluster emptyCl = new ETNACluster();
			emptyCl.setName("");
			proposedEASTINPropertiesOriginalClone.remove(emptyCl);

			ArrayList<ETNACluster> clusters = new ArrayList<ETNACluster>();
			// check proposed clusters
			for (ETNACluster c : proposedEASTINPropertiesOriginalClone) {

				String refersToSolution = "";
				for (int i = 0; i < c.getAllproperties().size(); i++) {
					refersToSolution = c.getAllproperties().get(i)
							.getRefersToSolution();
					break;
				}

				if (hashmapOfClusters.containsKey(c.getName())) {

					ETNACluster oldcluster = (ETNACluster) hashmapOfClusters
							.get(c.getName());

					if (c.isEdit()) {

						getOntology().removeProposedEtnaCluster(oldcluster);
						getOntology().createEASTINCluster(c, refersToSolution,
								true);
						hashmapOfClusters.remove(c.getName());

					} else {
						hashmapOfClusters.get(c.getName()).setFound(true);
					}

				} else if (!hashmapOfClusters.containsKey(c.getName())
						&& c.isEdit()) {
					// existing cluster with changed name
					clusters.add(c);
				}

			}

			// delete from ontology,deleted proposed etna clusters
			Iterator it = hashmapOfClusters.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				ETNACluster cluster = (ETNACluster) pairs.getValue();
				if (!cluster.isFound() && !cluster.getName().isEmpty()
						&& !cluster.getDescription().isEmpty())
					getOntology().removeProposedEtnaCluster(cluster);
			}

			// insert clusters with changed name, old cluster was removed
			for (ETNACluster c : clusters) {
				String refersToSolution = "";
				for (int i = 0; i < c.getAllproperties().size(); i++) {
					refersToSolution = c.getAllproperties().get(i)
							.getRefersToSolution();
					break;
				}
				getOntology().createEASTINCluster(c, refersToSolution, true);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkSolutions(ArrayList<String> oldSolutionUrisClone,
			ArrayList<Solution> solutionsClone) {

		// remove old solutions
		for (String s : oldSolutionUrisClone) {
			getOntology().removeOldSolution(s);
		}

		// update list, remove deleted solution uris
		for (String s : oldSolutionUrisClone) {
			oldSolutionUris.remove(s);
		}

		// check solutions
		for (Solution sol : solutionsClone) {
			// if solution is new or solution was edited, save it
			if (!getSolutionsHashmap().containsKey(sol.getOntologyURI())) {
				getOntology().saveSolution(sol, null, settingsHandlers);
			}

		}

		// update solutions hash map
		solutionsHashmap = new LinkedHashMap<String, Solution>();
		for (int i = 0; i < solutionsClone.size(); i++) {
			solutionsHashmap.put(solutionsClone.get(i).getOntologyURI(),
					solutionsClone.get(i));
		}

	}

	public void checkCategories(
			ArrayList<ProposedApplicationCategory> proposedApplicationCategoriesClone) {

		try {

			// create the hashmap with already saved categories
			ArrayList<ProposedApplicationCategory> list = (ArrayList<ProposedApplicationCategory>) ontology
					.loadAllProposedApplicationCategories();
			proposedApplicationCategoriesMap = new HashMap<String, ProposedApplicationCategory>();
			for (ProposedApplicationCategory cat : list) {
				cat.setFound(false);
				proposedApplicationCategoriesMap.put(cat.getName(), cat);
			}

			// check list with proposed application categories
			for (ProposedApplicationCategory cat : proposedApplicationCategoriesClone) {
				boolean flag = false;

				if (proposedApplicationCategoriesMap.containsKey(cat.getName())) {
					// category already exists
					ProposedApplicationCategory oldcat = (ProposedApplicationCategory) proposedApplicationCategoriesMap
							.get(cat.getName());

					if (cat.isEdit()) {
						flag = getOntology()
								.applyChangeInProposedApplicationCategory(cat,
										oldcat);
						if (flag)
							proposedApplicationCategoriesMap.get(cat.getName())
									.setFound(flag);
						else
							proposedApplicationCategoriesMap.remove(oldcat);
					} else
						proposedApplicationCategoriesMap.get(cat.getName())
								.setFound(true);
				}

				else {
					// category is either new or was edited
					// edited category with changed name
					getOntology().applyChangeInProposedApplicationCategory(cat,
							cat.getOldcategory());

				}

			}

			// delete from ontology,deleted proposedApplicationCategories
			Iterator it = proposedApplicationCategoriesMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				ProposedApplicationCategory cat = (ProposedApplicationCategory) pairs
						.getValue();
				if (!cat.isFound())
					getOntology().deleteCategory(cat);
			}

			System.out.println("finished checking categories..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkTerms(ArrayList<RegistryTerm> registryAliasesClone,
			ArrayList<RegistryTerm> proposedRegistryAliasesClone,
			ArrayList<RegistryTerm> registryTermsClone,
			ArrayList<RegistryTerm> proposedRegistryTermsClone) {

		List<RegistryTerm> tempList = new ArrayList<RegistryTerm>();
		List<RegistryTerm> oldterms = new ArrayList<RegistryTerm>();

		try {
			System.out.println("start checking aliases...");
			// CHECK ALIASES
			for (RegistryTerm term : registryAliasesClone) {

				if (ontologyAliases.containsKey(term.getId())) {

					RegistryTerm oldterm = (RegistryTerm) ontologyAliases
							.get(term.getId());
					if (term.getStatus().equals(oldterm.getStatus()))
						oldterm.setFound(true);

					if (term.isEdit())
						getOntology().updateRegistryTerm(term, oldterm,
								"Registry");

					oldterms.add(term);
				} else
					// the term is new
					tempList.add(term);
			}

			for (RegistryTerm propterm : proposedRegistryAliasesClone) {
				// if term exists
				if (ontologyAliases.containsKey(propterm.getId())) {

					RegistryTerm oldterm = (RegistryTerm) ontologyAliases
							.get(propterm.getId());

					if (propterm.getStatus().equals(oldterm.getStatus()))
						oldterm.setFound(true);

					if (propterm.isEdit())
						getOntology().updateRegistryTerm(propterm, oldterm,
								"ProposedRegistryTerms");

					oldterms.add(propterm);

				} else {
					// the term is new
					tempList.add(propterm);
				}
			}

			// delete from ontology,deleted terms
			Iterator it = ontologyAliases.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				RegistryTerm term = (RegistryTerm) pairs.getValue();
				if (!term.isFound())
					getOntology().deleteIndividual(term.getInd());
				// TODO
				// delete them from web service
			}

			System.out.println("start checking general terms...");
			// CHECK GENERAL TERMS
			for (RegistryTerm term : registryTermsClone) {
				// if term exists
				if (ontologyTerms.containsKey(term.getId())) {

					RegistryTerm oldterm = (RegistryTerm) ontologyTerms
							.get(term.getId());

					if (term.getStatus().equals(oldterm.getStatus()))
						oldterm.setFound(true);

					if (term.isEdit())
						getOntology().updateRegistryTerm(term, oldterm,
								"Registry");

					oldterms.add(term);

				} else {
					// the term is new
					tempList.add(term);
				}
			}

			for (RegistryTerm propterm : proposedRegistryTermsClone) {
				// if term exists
				if (ontologyTerms.containsKey(propterm.getId())) {

					RegistryTerm oldterm = (RegistryTerm) ontologyTerms
							.get(propterm.getId());

					oldterm.setFound(true);

					if (propterm.isEdit())
						getOntology().updateRegistryTerm(propterm, oldterm,
								"ProposedRegistryTerms");

					oldterms.add(propterm);

				} else {
					// the term is new
					tempList.add(propterm);
				}
			}

			// save new terms
			List<RegistryTerm> newTerms = getOntology().saveRegistryTerms(
					tempList);
			List<RegistryTerm> allTerms = new ArrayList<RegistryTerm>();
			for (RegistryTerm term : newTerms) {
				allTerms.add(term.clone());
			}

			for (RegistryTerm term : oldterms) {
				allTerms.add(term.clone());
			}

			// save aliases for terms
			for (RegistryTerm term : newTerms) {
				if (!term.getNotes().equals("ALIAS"))
					getOntology().saveAliases(term, allTerms);
			}

			// delete from ontology,deleted terms
			it = ontologyTerms.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				RegistryTerm term = (RegistryTerm) pairs.getValue();
				if (!term.isFound())
					getOntology().deleteIndividual(term.getInd());
				// TODO
				// delete them from web service
			}

			System.out.println("finished checking terms..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void requestVendorRole() {
		// String adminEmail = PortalUtil.getPortalProperties().getProperty(
		// "admin.email.from.address");
		String adminEmail = "konstadinidou@iti.gr";
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
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("cloud4All.SAT@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(adminEmail));
			message.setSubject("Vendor role Request");
			User currentUser = LiferayFacesContext.getInstance().getUser();
			message.setText("The user" + currentUser.getFirstName() + " "
					+ currentUser.getFullName() + " with email: "
					+ currentUser.getEmailAddress() + "requested Vendor role.");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public Solution find(String key) {
		return solutionsMap.get(key);
	}

	public List<Solution> list() {
		return new ArrayList<Solution>(solutionsMap.values());
	}

	public Map<String, Solution> map() {
		return solutionsMap;
	}

	public static Map<String, Solution> getSolutionsMap() {
		return solutionsMap;
	}

	public static void setSolutionsMap(Map<String, Solution> solutionsMap) {
		OntologyInstance.solutionsMap = solutionsMap;
	}

	public List<Solution> getSolutions() {
		return solutions;
	}

	public void setSolutions(List<Solution> solutions) {
		this.solutions = solutions;
	}

	public List<RegistryTerm> getRegistryTerms() {
		Collections.sort(registryTerms);
		return registryTerms;
	}

	public void setRegistryTerms(List<RegistryTerm> registryTerms) {
		this.registryTerms = registryTerms;
	}

	public List<RegistryTerm> getProposedRegistryTerms() {
		Collections.sort(proposedRegistryTerms);
		return proposedRegistryTerms;
	}

	public void setProposedRegistryTerms(
			List<RegistryTerm> proposedRegistryTerms) {
		this.proposedRegistryTerms = proposedRegistryTerms;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public List<OntologyClass> getSolutionClassesStructured() {
		return solutionClassesStructured;
	}

	public void setSolutionClassesStructured(
			List<OntologyClass> solutionClassesStructured) {
		this.solutionClassesStructured = solutionClassesStructured;
	}

	public List<String> getRegistryTermsAsStrings() {
		return registryTermsAsStrings;
	}

	public void setRegistryTermsAsStrings(List<String> registryTermsAsStrings) {
		this.registryTermsAsStrings = registryTermsAsStrings;
	}

	public List<String> getPlatformList() {
		return platformList;
	}

	public void setPlatformList(List<String> platformList) {
		this.platformList = platformList;
	}

	public List<String> getDevicesList() {
		return devicesList;
	}

	public void setDevicesList(List<String> devicesList) {
		this.devicesList = devicesList;
	}

	public List<String> getCountriesList() {
		return countriesList;
	}

	public void setCountriesList(List<String> countriesList) {
		this.countriesList = countriesList;
	}

	public List<String> getCommercialCostCurrency() {
		return commercialCostCurrency;
	}

	public void setCommercialCostCurrency(List<String> commercialCostCurrency) {
		this.commercialCostCurrency = commercialCostCurrency;
	}

	public List<String> getCostPaymentChargeType() {
		return costPaymentChargeType;
	}

	public void setCostPaymentChargeType(List<String> costPaymentChargeType) {
		this.costPaymentChargeType = costPaymentChargeType;
	}

	public List<Vendor> getVendors() {
		return vendors;
	}

	public void setVendors(List<Vendor> vendors) {
		this.vendors = vendors;
	}

	public List<SolutionUserSchema> getSolutionUserSchemas() {
		return solutionUserSchemas;
	}

	public void setSolutionUserSchemas(
			List<SolutionUserSchema> solutionUserSchemas) {
		this.solutionUserSchemas = solutionUserSchemas;
	}

	public Map<String, Integer> getVendorsUsers() {
		return vendorsUsers;
	}

	public void setVendorsUsers(Map<String, Integer> vendorsUsers) {
		this.vendorsUsers = vendorsUsers;
	}

	public List<ETNACluster> getProposedEASTINProperties() {
		Collections.sort(proposedEASTINProperties);
		return proposedEASTINProperties;
	}

	public void setProposedEASTINProperties(
			List<ETNACluster> proposedEASTINProperties) {
		this.proposedEASTINProperties = proposedEASTINProperties;
	}

	public List<ETNACluster> getEtnaCluster() {
		return etnaCluster;
	}

	public void setEtnaCluster(List<ETNACluster> etnaCluster) {
		this.etnaCluster = etnaCluster;
	}

	public List<ProposedApplicationCategory> getProposedApplicationCategories() {
		Collections.sort(proposedApplicationCategories);
		return proposedApplicationCategories;
	}

	public void setProposedApplicationCategories(
			List<ProposedApplicationCategory> proposedApplicationCategories) {
		this.proposedApplicationCategories = proposedApplicationCategories;
	}

	public List<EASTINProperty> getProposedEASTINItems() {
		return proposedEASTINItems;
	}

	public void setProposedEASTINItems(List<EASTINProperty> proposedEASTINItems) {
		this.proposedEASTINItems = proposedEASTINItems;
	}

	public List<EASTINProperty> getProposedMeasureEASTINItems() {
		return proposedMeasureEASTINItems;
	}

	public void setProposedMeasureEASTINItems(
			List<EASTINProperty> proposedMeasureEASTINItems) {
		this.proposedMeasureEASTINItems = proposedMeasureEASTINItems;
	}

	public static Map<String, EASTINProperty> getEASTINPropertiesMap() {
		return EASTINPropertiesMap;
	}

	public static void setEASTINPropertiesMap(
			Map<String, EASTINProperty> eASTINPropertiesMap) {
		EASTINPropertiesMap = eASTINPropertiesMap;
	}

	public EASTINProperty findProperty(String key) {
		return EASTINPropertiesMap.get(key);
	}

	public static Map<String, ETNACluster> getClusterPropertiesMap() {
		return clusterPropertiesMap;
	}

	public static void setClusterPropertiesMap(
			Map<String, ETNACluster> clusterPropertiesMap) {
		OntologyInstance.clusterPropertiesMap = clusterPropertiesMap;
	}

	public ETNACluster findCluster(String key) {
		return clusterPropertiesMap.get(key);
	}

	public List<ETNACluster> getProposedEASTINPropertiesOriginal() {
		Collections.sort(proposedEASTINPropertiesOriginal);
		return proposedEASTINPropertiesOriginal;
	}

	public void setProposedEASTINPropertiesOriginal(
			List<ETNACluster> proposedEASTINPropertiesOriginal) {
		this.proposedEASTINPropertiesOriginal = proposedEASTINPropertiesOriginal;
	}

	public List<ProposedApplicationCategory> getProposedApplicationCategoriesOriginal() {
		Collections.sort(proposedApplicationCategoriesOriginal);
		return proposedApplicationCategoriesOriginal;
	}

	public void setProposedApplicationCategoriesOriginal(
			List<ProposedApplicationCategory> proposedApplicationCategoriesOriginal) {
		this.proposedApplicationCategoriesOriginal = proposedApplicationCategoriesOriginal;
	}

	public List<EASTINProperty> getProposedEASTINItemsOriginal() {
		return proposedEASTINItemsOriginal;
	}

	public void setProposedEASTINItemsOriginal(
			List<EASTINProperty> proposedEASTINItemsOriginal) {
		this.proposedEASTINItemsOriginal = proposedEASTINItemsOriginal;
	}

	public List<EASTINProperty> getAllProposedItems() {
		Collections.sort(allProposedItems);
		return allProposedItems;
	}

	public void setAllProposedItems(List<EASTINProperty> allProposedItems) {
		this.allProposedItems = allProposedItems;
	}

	public List<EASTINProperty> getProposedMeasureEASTINItemsOriginal() {
		return proposedMeasureEASTINItemsOriginal;
	}

	public void setProposedMeasureEASTINItemsOriginal(
			List<EASTINProperty> proposedMeasureEASTINItemsOriginal) {
		this.proposedMeasureEASTINItemsOriginal = proposedMeasureEASTINItemsOriginal;
	}

	public List<ETNACluster> getEtnaClusterOriginal() {
		return etnaClusterOriginal;
	}

	public void setEtnaClusterOriginal(List<ETNACluster> etnaClusterOriginal) {
		this.etnaClusterOriginal = etnaClusterOriginal;
	}

	public HashMap<String, RegistryTerm> getOntologyTerms() {
		return ontologyTerms;
	}

	public void setOntologyTerms(HashMap<String, RegistryTerm> ontologyTerms) {
		this.ontologyTerms = ontologyTerms;
	}

	public HashMap<String, RegistryTerm> getOntologyAliases() {
		return ontologyAliases;
	}

	public void setOntologyAliases(HashMap<String, RegistryTerm> ontologyAliases) {
		this.ontologyAliases = ontologyAliases;
	}

	public HashMap<String, ProposedApplicationCategory> getProposedApplicationCategoriesMap() {
		return proposedApplicationCategoriesMap;
	}

	public void setProposedApplicationCategoriesMap(
			HashMap<String, ProposedApplicationCategory> proposedApplicationCategoriesMap) {
		this.proposedApplicationCategoriesMap = proposedApplicationCategoriesMap;
	}

	public List<String> getOldSolutionUris() {
		return oldSolutionUris;
	}

	public void setOldSolutionUris(List<String> oldSolutionUris) {
		this.oldSolutionUris = oldSolutionUris;
	}

	public List<ETNACluster> getClustersToBeAccepted() {
		return clustersToBeAccepted;
	}

	public void setClustersToBeAccepted(List<ETNACluster> clustersToBeAccepted) {
		this.clustersToBeAccepted = clustersToBeAccepted;
	}

	public List<EASTINProperty> getItemsToBeAccepted() {
		return itemsToBeAccepted;
	}

	public void setItemsToBeAccepted(List<EASTINProperty> itemsToBeAccepted) {
		this.itemsToBeAccepted = itemsToBeAccepted;
	}

	public List<SLA> getSla() {
		return sla;
	}

	public void setSla(List<SLA> sla) {
		this.sla = sla;
	}

	public List<EULA> getEula() {
		return eula;
	}

	public void setEula(List<EULA> eula) {
		this.eula = eula;
	}

	public List<SolutionUserSchema> getUserSchemaForInsert() {
		return userSchemaForInsert;
	}

	public void setUserSchemaForInsert(
			List<SolutionUserSchema> userSchemaForInsert) {
		this.userSchemaForInsert = userSchemaForInsert;
	}

	public List<RegistryTerm> getRegistryAliases() {
		Collections.sort(registryAliases);
		return registryAliases;
	}

	public void setRegistryAliases(List<RegistryTerm> registryAliases) {
		this.registryAliases = registryAliases;
	}

	public List<RegistryTerm> getProposedRegistryAliases() {
		Collections.sort(proposedRegistryAliases);
		return proposedRegistryAliases;
	}

	public void setProposedRegistryAliases(
			List<RegistryTerm> proposedRegistryAliases) {
		this.proposedRegistryAliases = proposedRegistryAliases;
	}

	public HashMap<String, ETNACluster> getHashmapOfClusters() {
		return hashmapOfClusters;
	}

	public void setHashmapOfClusters(
			HashMap<String, ETNACluster> hashmapOfClusters) {
		this.hashmapOfClusters = hashmapOfClusters;
	}

	public HashMap<String, EASTINProperty> getHashmapOfProperties() {
		return hashmapOfProperties;
	}

	public void setHashmapOfProperties(
			HashMap<String, EASTINProperty> hashmapOfProperties) {
		this.hashmapOfProperties = hashmapOfProperties;
	}

	public HashMap<String, Solution> getSolutionsHashmap() {
		return solutionsHashmap;
	}

	public void setSolutionsHashmap(HashMap<String, Solution> solutionsHashmap) {
		this.solutionsHashmap = solutionsHashmap;
	}

	public String getNumberOfWebServiceTerms() {
		return numberOfWebServiceTerms;
	}

	public void setNumberOfWebServiceTerms(String numberOfWebServiceTerms) {
		this.numberOfWebServiceTerms = numberOfWebServiceTerms;
	}

	public String getNumberOfWebServiceProposedTerms() {
		return numberOfWebServiceProposedTerms;
	}

	public void setNumberOfWebServiceProposedTerms(
			String numberOfWebServiceProposedTerms) {
		this.numberOfWebServiceProposedTerms = numberOfWebServiceProposedTerms;
	}

	public void hideDialog(AjaxBehaviorEvent e) {
		e.getComponent().setInView(false);
	}

	public ArrayList<SettingsHandler> getSettingsHandlers() {
		return settingsHandlers;
	}

	public void setSettingsHandlers(ArrayList<SettingsHandler> settingsHandlers) {
		this.settingsHandlers = settingsHandlers;
	}

	public ArrayList<String> getSettingsHandlersNames() {
		return settingsHandlersNames;
	}

	public void setSettingsHandlersNames(ArrayList<String> settingsHandlersNames) {
		this.settingsHandlersNames = settingsHandlersNames;
	}

	public List<ETNACluster> getShownClusters() {
		return shownClusters;
	}

	public void setShownClusters(List<ETNACluster> shownClusters) {
		this.shownClusters = shownClusters;
	}

	public List<ETNACluster> getHiddenClusters() {
		return hiddenClusters;
	}

	public void setHiddenClusters(List<ETNACluster> hiddenClusters) {
		this.hiddenClusters = hiddenClusters;
	}

	public List<ETNACluster> getClustersToShowInView() {
		return clustersToShowInView;
	}

	public void setClustersToShowInView(List<ETNACluster> clustersToShowInView) {
		this.clustersToShowInView = clustersToShowInView;
	}

	public void addClusterToShownClusters(ETNACluster cluster) {
		if (!this.shownClusters.contains(cluster))
			shownClusters.add(cluster);
	}

	public void addClusterToClustersToShowInView(ETNACluster cluster) {
		if (!this.clustersToShowInView.contains(cluster))
			clustersToShowInView.add(cluster);
	}

	public void addClusterToHiddenClusters(ETNACluster cluster) {
		if (!this.hiddenClusters.contains(cluster))
			hiddenClusters.add(cluster);
	}

	public ArrayList<String> getCategoriesNames() {
		return categoriesNames;
	}

	public void setCategoriesNames(ArrayList<String> categoriesNames) {
		this.categoriesNames = categoriesNames;
	}

	public ArrayList<Solution> getServices() {
		return services;
	}

	public void setServices(ArrayList<Solution> services) {
		this.services = services;
	}
	

}
