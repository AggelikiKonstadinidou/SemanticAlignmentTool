package org.cloud4All.ontology;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.EnumeratedClass;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntResource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.cloud4All.ApplicationCategories;
import org.cloud4All.Item;
import org.cloud4All.ProposedApplicationCategory;
import org.cloud4All.Setting;
import org.cloud4All.Solution;
import org.cloud4All.Utilities;
import org.cloud4All.IPR.CommercialCostSchema;
import org.cloud4All.IPR.DiscountSchema;
import org.cloud4All.IPR.EULA;
import org.cloud4All.IPR.PerCountryReputation;
import org.cloud4All.IPR.PerCountrySolutionUsage;
import org.cloud4All.IPR.PerVendorSolutionUsage;
import org.cloud4All.IPR.ReputationSchema;
import org.cloud4All.IPR.SLA;
import org.cloud4All.IPR.SolutionAccessInfoForUsers;
import org.cloud4All.IPR.SolutionAccessInfoForVendors;
import org.cloud4All.IPR.SolutionLicense;
import org.cloud4All.IPR.SolutionTrialSchema;
import org.cloud4All.IPR.SolutionUsageStatisticsSchema;
import org.cloud4All.IPR.SolutionUserFeedback;
import org.cloud4All.IPR.SolutionUserSchema;
import org.cloud4All.IPR.Vendor;
import org.cloud4All.utils.StringComparison;

import com.liferay.portal.model.User;

public class Ontology implements Serializable {

	private OntModel ontologyModel;
	String ontologyFileName = "semanticFrameworkOfContentAndSolutions.owl";
	String ontologyPath = "";
	String SOURCE = "http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl";
	String NS = SOURCE + "#";
	public static Set<String> uris = new HashSet<String>();
	private static boolean bDeletingCluster = false;

	public void loadOntology() {

		FacesContext context = FacesContext.getCurrentInstance();

		String path = ((ServletContext) context.getExternalContext()
				.getContext()).getRealPath(ontologyFileName);
		System.out.println(path);
		try {
			ontologyPath = path;
			ontologyModel = ModelFactory.createOntologyModel();
			InputStream in = FileManager.get().open(ontologyPath);
			if (in == null) {
				throw new IllegalArgumentException("File: " + ontologyPath
						+ " not found");
			}

			ontologyModel.read(in, "");
			calculateUris();

		} catch (Exception e) {
			System.out.println("myexception");
			e.printStackTrace();
		}

	}

	private void calculateUris() {
		// load uris
		for (StmtIterator i = ontologyModel.listStatements(); i.hasNext();) {
			Statement s = i.next();
			if (!s.getSubject().isAnon()) {
				uris.add(s.getSubject().getURI().toLowerCase());

			}
			uris.add(s.getPredicate().getURI().toLowerCase());
			if (s.getObject().isResource() && !s.getResource().isAnon()) {
				uris.add(s.getResource().getURI().toLowerCase());

			}
		}

	}

	public String getNS() {
		return NS;
	}

	public List<String> getOntologyClasses() {

		List<String> returnClasses = new ArrayList<String>();
		ExtendedIterator classes = ontologyModel.listClasses();
		while (classes.hasNext()) {
			OntClass essaClasse = (OntClass) classes.next();
			if (essaClasse.getLocalName() != null) {
				returnClasses.add(essaClasse.getLocalName());
			}
			for (Iterator i = essaClasse.listSubClasses(true); i.hasNext();) {
				OntClass c = (OntClass) i.next();
				if (c.getLocalName() != null) {
					returnClasses.add(c.getLocalName());
				}
			}
		}
		return returnClasses;
	}

	public List<ApplicationCategories> getAllSolutionChildren() {

		List<ApplicationCategories> returnClasses = new ArrayList<ApplicationCategories>();
		returnClasses.add(new ApplicationCategories());

		OntClass solutions = ontologyModel.getOntClass(NS + "Solutions");
		for (Iterator<OntClass> i = solutions.listSubClasses(); i.hasNext();) {
			OntClass c = i.next();
			ApplicationCategories applicationCategories = new ApplicationCategories();
			applicationCategories.setName(c.getLocalName());
			applicationCategories.setDescription(c.getLabel(null));
			returnClasses.add(applicationCategories);
		}
		return returnClasses;
	}

	public List<String> getSolutionChildren() {

		List<String> returnClasses = new ArrayList<String>();
		returnClasses.add("");

		OntClass solutions = ontologyModel.getOntClass(NS + "Solutions");
		for (Iterator<OntClass> i = solutions.listSubClasses(true); i.hasNext();) {
			OntClass c = i.next();

			returnClasses.add(Utilities.splitCamelCase(c.getLocalName()));
		}
		return returnClasses;
	}

	public List<String> getChildrenOfConcept(String conceptName) {

		List<String> returnClasses = new ArrayList<String>();
		returnClasses.add("");

		OntClass solutions = ontologyModel.getOntClass(NS + conceptName);
		for (Iterator<OntClass> i = solutions.listSubClasses(true); i.hasNext();) {
			OntClass c = i.next();

			returnClasses.add(Utilities.splitCamelCase(c.getLocalName()));
		}
		return returnClasses;
	}
	
	public ArrayList<Solution> loadAllServices() {
		ArrayList<Solution> services = new ArrayList<Solution>();

		// fill "allSolutions" ArrayList
		ExtendedIterator classes = ontologyModel.listClasses();
		OntClass solutionsClass = null;
		OntClass servicesClass = null;
		while (classes.hasNext()) {
			OntClass tmpClass = (OntClass) classes.next();
			if (tmpClass.getLocalName() != null) {

				boolean isASubclassOfServices = false;
				// get parent class
				for (Iterator<OntClass> i = tmpClass.listSuperClasses(false); i
						.hasNext();) {
					OntClass tmpRootClass = i.next();

					// get the classes that are under the "Service" class
					if (tmpRootClass.getLocalName().equals("Service")) {
						servicesClass = tmpClass;
						isASubclassOfServices = true;
					}

					// break if both classes have been found
					if (isASubclassOfServices)
						break;
				}

				// load services
				if (isASubclassOfServices) {

					ArrayList<IndividualImpl> instances = (ArrayList<IndividualImpl>) servicesClass
							.listInstances(true).toList();

					for (int i = 0; i < instances.size(); i++) {
						IndividualImpl tmpInstance = instances.get(i);

						Solution tmpSolution = new Solution();
						tmpSolution.setOntologyCategory(tmpInstance
								.getOntClass().getLocalName());
						tmpSolution.setName(tmpInstance.getLocalName());
						tmpSolution.setId("com.certh.service.synthesis."+tmpInstance
								.getPropertyValue(
										ontologyModel.getProperty(NS,
												"serviceId")).asLiteral()
								.getValue().toString());

						// get service input parameters
						ObjectProperty hasInput = ontologyModel
								.getObjectProperty(NS + "hasInput");
						ObjectProperty hasOutput = ontologyModel
								.getObjectProperty(NS + "hasOutput");
						// get the registry term to witch is mapped the input
						// parameter
						ObjectProperty parameterIsMappedToTerm = ontologyModel
								.getObjectProperty(NS
										+ "parameterIsMappedToTerm");
						DatatypeProperty RegistryTerm_hasID = ontologyModel
								.getDatatypeProperty(NS + "RegistryTerm_hasID");

						if (tmpInstance.getPropertyResourceValue(hasInput) != null) {

							StmtIterator it2 = tmpInstance
									.listProperties(hasInput);
							while (it2.hasNext()) {

								Setting tmpSetting = new Setting();
								StatementImpl st = (StatementImpl) it2.next();
								Resource r = st.getResource();
								Individual ind = ontologyModel.getIndividual(r
										.getURI());

								// name, id of parameter
								tmpSetting.setName(st.getObject().asResource()
										.getLocalName());
								tmpSetting.setId(st.getObject().asResource()
										.getLocalName());

								// mapped to registry term
								if (ind.getPropertyResourceValue(parameterIsMappedToTerm) != null) {

									StmtIterator it3 = ind
											.listProperties(parameterIsMappedToTerm);
									while (it3.hasNext()) {
										StatementImpl termSt = (StatementImpl) it3
												.next();
										Resource res = termSt.getResource();
										Individual termInd = ontologyModel
												.getIndividual(res.getURI());
										tmpSetting.setMapping(termInd
												.getPropertyValue(
														RegistryTerm_hasID)
												.asLiteral().getString());
										if (!tmpSetting.getMapping().isEmpty()){
											tmpSetting.setHasMapping(true);
										    tmpSetting.setExactMatching(true);
										}
										else 
											tmpSetting.setHasMapping(false);

									}

								}
								tmpSetting.setComments("input_parameter");
								if(tmpSetting.getName().contains("Url")
										|| tmpSetting.getName().contains("url"))
									tmpSetting.setValueSpace("url");
								tmpSolution.getSettings().add(tmpSetting);

							}
						}

						if (tmpInstance.getPropertyResourceValue(hasOutput) != null) {

							StmtIterator it2 = tmpInstance
									.listProperties(hasOutput);
							while (it2.hasNext()) {

								Setting tmpSetting = new Setting();
								StatementImpl st = (StatementImpl) it2.next();
								Resource r = st.getResource();
								Individual ind = ontologyModel.getIndividual(r
										.getURI());

								// name, id of parameter
								// name, id of parameter
								tmpSetting.setName(st.getObject().asResource()
										.getLocalName());
								tmpSetting.setId(st.getObject().asResource()
										.getLocalName());

								// mapped to registry term
								if (ind.getPropertyResourceValue(parameterIsMappedToTerm) != null) {

									StmtIterator it3 = ind
											.listProperties(parameterIsMappedToTerm);
									while (it3.hasNext()) {
										StatementImpl termSt = (StatementImpl) it3
												.next();
										Resource res = termSt.getResource();
										Individual termInd = ontologyModel
												.getIndividual(res.getURI());
										tmpSetting.setMapping(termInd
												.getPropertyValue(
														RegistryTerm_hasID)
												.asLiteral().getString());
										
										if (!tmpSetting.getMapping().isEmpty()){
											tmpSetting.setHasMapping(true);
										    tmpSetting.setExactMatching(true);
										}
										else 
											tmpSetting.setHasMapping(false);

									}

								}
								tmpSetting.setComments("output_parameter");
								tmpSetting.setValueSpace("String");
								if(tmpSetting.getName().contains("Url")
										|| tmpSetting.getName().contains("url"))
									tmpSetting.setValueSpace("url");
									
								tmpSolution.getSettings().add(tmpSetting);

							}
						}
						services.add(tmpSolution);
					}
				}
			}
		}
		
		
		return services;

	}

	public List<String> getPropertiesOfConcept(String conceptName) {
		List<String> list = new ArrayList<String>();
		OntClass solutions = ontologyModel.getOntClass(NS + conceptName);
		Iterator propIt = solutions.listDeclaredProperties(false);
		while (propIt.hasNext()) {
			OntProperty prop = (OntProperty) propIt.next();
			list.add(prop.getLocalName());
		}
		return list;
	}

	public SortedMap getSolutionsMatchingConcepts(String text) {
		// String text1=splitCamelCase(text);
		// String[] arr1=text1.split(" ");
		SortedMap map = new TreeMap();
		List<ApplicationCategories> list = getAllSolutionChildren();
		for (int i = 0; i < list.size(); i++) {
			map.put(StringComparison
					.CompareStrings(list.get(i).getName(), text), list.get(i));
		}
		return map;
	}

	public SortedMap getMatchingConceptsOfEtnaItems(List<ETNACluster> list,
			String text) {

		SortedMap map = new TreeMap();

		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).getAllproperties().size(); j++) {
				EASTINProperty prop = list.get(i).getAllproperties().get(j);
				double score = StringComparison.CompareStrings(prop.getName(),
						text);
				List<EASTINProperty> tmp = (List<EASTINProperty>) map
						.get(score);
				if (tmp == null) {
					tmp = new ArrayList<EASTINProperty>();
					tmp.add(prop);
					map.put(score, tmp);
				} else {
					tmp.add(prop);
					map.put(score, tmp);
				}
			}
		}
		return map;
	}

	public List<OntologyClass> getOntologyClassesStructured() {
		List<OntologyClass> list = new ArrayList<OntologyClass>();
		ExtendedIterator classes = ontologyModel.listHierarchyRootClasses();
		while (classes.hasNext()) {
			OntClass essaClasse = (OntClass) classes.next();
			String vClasse = essaClasse.getLocalName().toString();
			List<OntologyClass> l = new ArrayList<OntologyClass>();
			if (essaClasse.hasSubClass()) {
				for (Iterator i = essaClasse.listSubClasses(true); i.hasNext();) {
					OntClass c = (OntClass) i.next();
					l.add(getConceptChildrenStructured(c));
				}
			}
			OntologyClass on = new OntologyClass();
			on.setClassName(vClasse);
			on.setChildren(l);
			list.add(on);
		}
		return list;
	}

	public List<OntologyClass> getProposedSolutionsClassesStructured() {
		List<OntologyClass> list = new ArrayList<OntologyClass>();
		ExtendedIterator classes = ontologyModel.listHierarchyRootClasses();

		OntClass essaClasse = ontologyModel.getOntClass(NS
				+ "Solutions_Proposed");

		String vClasse = essaClasse.getLocalName().toString();
		List<OntologyClass> l = new ArrayList<OntologyClass>();
		if (essaClasse.hasSubClass()) {
			for (Iterator i = essaClasse.listSubClasses(true); i.hasNext();) {
				OntClass c = (OntClass) i.next();
				l.add(getConceptChildrenStructured(c));
			}
		}
		OntologyClass on = new OntologyClass();
		on.setClassName(vClasse);
		on.setChildren(l);
		list.add(on);

		return list;
	}

	public List<OntologyClass> getSolutionsClassesStructured() {
		List<OntologyClass> list = new ArrayList<OntologyClass>();
		ExtendedIterator classes = ontologyModel.listHierarchyRootClasses();

		OntClass essaClasse = ontologyModel.getOntClass(NS + "Solutions");

		String vClasse = essaClasse.getLocalName().toString();
		List<OntologyClass> l = new ArrayList<OntologyClass>();
		if (essaClasse.hasSubClass()) {
			for (Iterator i = essaClasse.listSubClasses(true); i.hasNext();) {
				OntClass c = (OntClass) i.next();
				l.add(getConceptChildrenStructured(c));
			}
		}
		OntologyClass on = new OntologyClass();
		on.setClassName(vClasse);
		on.setChildren(l);
		on.setEastinItems(new ArrayList<String>());
		list.add(on);

		return list;
	}

	public OntologyClass getConceptChildrenStructured(OntClass c) {
		List<OntologyClass> l = new ArrayList<OntologyClass>();
		if (c.hasSubClass()) {
			for (Iterator i = c.listSubClasses(true); i.hasNext();) {
				OntClass cc = (OntClass) i.next();
				l.add(getConceptChildrenStructured(cc));
			}
		}
		OntologyClass on = new OntologyClass();
		on.setClassName(c.getLocalName());
		on.setChildren(l);

		String eastinItems = c.getComment("en");
		String[] splitted = null;
		on.setEastinItems(new ArrayList<String>());
		if (!eastinItems.equals("empty")) {
			splitted = eastinItems.split(",");

			for (int i = 0; i < splitted.length; i++) {
				on.getEastinItems().add(splitted[i]);
			}
		}

		return on;
	}

	public Individual addIndividual(String className, String individualName) {
		try {
			OntClass cl = ontologyModel.getOntClass(NS + className);
			if (cl == null) {
				cl = ontologyModel.createClass(NS + className);
			}
			Individual l1 = ontologyModel.createIndividual(NS + individualName,
					cl);
			uris.add(l1.getURI().toLowerCase());

			return l1;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public List<String> getInstancesOfClass(String text) {
		List<String> list = new ArrayList<String>();
		OntClass cl = ontologyModel.getOntClass(NS + text);

		ArrayList<IndividualImpl> it = (ArrayList<IndividualImpl>) cl
				.listInstances().toList();
		for (int i = 0; i < it.size(); i++) {
			IndividualImpl in = it.get(i);

			list.add(in.getLocalName());

		}
		return list;
	}

	public List<IndividualImpl> getInstancesOfClassAsIndividuals(String text) {
		List<IndividualImpl> list = new ArrayList<IndividualImpl>();
		OntClass cl = ontologyModel.getOntClass(NS + text);
		if (cl != null) {
			Iterator it = cl.listInstances();
			while (it.hasNext()) {
				IndividualImpl in = (IndividualImpl) it.next();

				list.add(in);

			}

		}
		return list;
	}

	public List<String> getSubClassesAsStrings(String text) {
		List<String> list2 = new ArrayList<String>();

		OntClass cl = ontologyModel.getOntClass(NS + text);
		Iterator it = cl.listInstances();
		while (it.hasNext()) {

			IndividualImpl in = (IndividualImpl) it.next();
			list2.add(in.getLocalName());

		}

		return list2;
	}

	// my test method getSettingsOfInstance
	public List<Setting> getSettingsOfInstance(String text) {
		List<Setting> settings = new ArrayList<Setting>();
		IndividualImpl solutionInd = (IndividualImpl) ontologyModel
				.getIndividual(text);

		// find individuals that contain settings of solution

		ObjectProperty dProperty = ontologyModel.getObjectProperty(NS
				+ "hasSolutionSpecificSettings");
		if (solutionInd.getPropertyResourceValue(dProperty) != null) {

			ArrayList<RDFNode> outerIterator = (ArrayList<RDFNode>) solutionInd
					.listPropertyValues(dProperty).toList();
			for (RDFNode rdf : outerIterator) {

				Resource r = rdf.asResource();

				IndividualImpl settingsInd = (IndividualImpl) ontologyModel
						.getIndividual(r.getURI());

				ArrayList<Statement> iterate = (ArrayList<Statement>) settingsInd
						.listProperties().toList();
				for (Statement s : iterate) {

					if (s.getObject().isLiteral()) {

						Setting set = new Setting();
						String name = s.getPredicate().getLocalName();
						if (!name.endsWith("_isMappedToRegTerm")
								&& !name.endsWith("_hasDescription")
								&& !name.endsWith("_hasCommentsForMapping")
								&& !name.endsWith("_hasDefaultValue")
								&& !name.endsWith("_hasValueSpace")
								&& !name.endsWith("_isExactMatching")
								&& !name.endsWith("_hasConstraints")
								&& !name.endsWith("_hasName")
								&& !name.endsWith("_hasID")
								&& !name.startsWith("EASTIN__")
								&& !name.endsWith("appliedLive")
								&& !name.endsWith("_hasType")) {
							set.setName(s.getPredicate().getLocalName());
							// System.out.println(s.getPredicate().getLocalName());
							set.setValue(s.getLiteral().getLexicalForm());
							set.setConstraints("");
							set.setParentInstanceURI(settingsInd.getURI());
							set.setCurrentURI(s.getPredicate().toString());
							settings.add(set);
						}

					}
				}

				// search property mapping
				for (Setting set : settings) {
					String setUri = set.getName();
					String mappedTo = setUri + "_isMappedToRegTerm";
					String hasDescription = setUri + "_hasDescription".trim();
					String hasCommentsForMapping = setUri
							+ "_hasCommentsForMapping".trim();
					String hasDefaultValue = setUri + "_hasDefaultValue".trim();
					String hasValueSpace = setUri + "_hasValueSpace".trim();
					String isExactMatching = setUri + "_isExactMatching".trim();
					String hasConstraints = setUri + "_hasConstraints".trim();
					String hasName = setUri + "_hasName".trim();
					String hasId = setUri + "_hasID".trim();
					String appliedLive = setUri + "appliedLive".trim();
					// String hasType = setUri + "_hasType".trim();

					for (Statement s : iterate) {
						if (!s.getObject().isLiteral()) {
							if (mappedTo
									.equals(s.getPredicate().getLocalName())) {
								RegistryTerm term = getRegistryOntologyTerm(s
										.getObject().toString().split("#")[1]);
								set.setMapping(term.getName());
								set.setMappingId(term.getId());

							}
						} else {
							if (hasDescription.equals(s.getPredicate()
									.getLocalName())) {

								set.setDescription(s.getString());

							} else if (hasCommentsForMapping.equals(s
									.getPredicate().getLocalName())) {
								set.setComments(s.getString());

							} else if (hasDefaultValue.equals(s.getPredicate()
									.getLocalName())) {
								set.setValue(s.getString());

							} else if (hasId.equals(s.getPredicate()
									.getLocalName())) {
								set.setId(s.getString());

							} else if (hasValueSpace.equals(s.getPredicate()
									.getLocalName())) {
								set.setValueSpace(s.getString());

							} else if (isExactMatching.equals(s.getPredicate()
									.getLocalName())) {
								set.setExactMatching(s.getBoolean());

							} else if (mappedTo.equals(s.getPredicate()
									.getLocalName())) {
								set.setMapping("null");
								set.setHasMapping(false);
								set.setExactMatching(false);

							} else if (hasConstraints.equals(s.getPredicate()
									.getLocalName())) {
								set.setConstraints(s.getString());

							} else if (hasName.equals(s.getPredicate()
									.getLocalName())) {
								set.setName(s.getString());

							} else if (appliedLive.equals(s.getPredicate()
									.getLocalName())) {
								set.setAppliedLive(s.getBoolean());

							}
						}

					}

				}
			}

		}

		return settings;
	}

	public List<List> getInstancesOfRegistryClass(String ClassName) {
		List<List> list = new ArrayList<List>();
		List<Item> itemsList = new ArrayList<Item>();
		List<String> namesList = new ArrayList<String>();

		OntClass cl = ontologyModel.getOntClass(NS + ClassName);
		Iterator it = cl.listInstances();
		while (it.hasNext()) {
			IndividualImpl in = (IndividualImpl) it.next();

			DatatypeProperty hasName = ontologyModel.getDatatypeProperty(NS
					+ "RegistryTerm_hasName");
			DatatypeProperty hasDescription = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDescription");
			if (hasDescription != null
					&& in.getPropertyValue(hasDescription) != null
					&& hasName != null && in.getPropertyValue(hasName) != null) {

				String name = in.getPropertyValue(hasName).asLiteral()
						.getString();
				String description = "Description: empty";
				if (!in.getPropertyValue(hasDescription).asLiteral()
						.getString().isEmpty())
					description = "Description: "
							+ in.getPropertyValue(hasDescription).asLiteral()
									.getString();

				Item item = new Item(name, description);
				itemsList.add(item);
				namesList.add(name);

			}

		}
		list.add(itemsList);
		list.add(namesList);
		return list;
	}

	public List<RegistryTerm> getInstancesOfTerms(String className) {
		List<RegistryTerm> list = new ArrayList<RegistryTerm>();
		try {
			OntClass cl = ontologyModel.getOntClass(NS + className);
			DatatypeProperty hasDescriptionProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDescription");
			DatatypeProperty hasValueSpaceProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasValueSpace");
			DatatypeProperty hasTypeProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasType");
			DatatypeProperty hasDefaultValueProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDefaultValue");
			DatatypeProperty hasNameProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasName");
			DatatypeProperty hasIDProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasID");
			DatatypeProperty hasNotesProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasNotes");
			ObjectProperty hasAliasProperty = ontologyModel
					.getObjectProperty(NS + "RegistryTerm_hasAlias");

			ArrayList<IndividualImpl> it = (ArrayList<IndividualImpl>) cl
					.listInstances().toList();
			for (int i = 0; i < it.size(); i++) {

				IndividualImpl in = it.get(i);
				uris.add(in.getURI().toLowerCase());

				if (in.getOntClass().getLocalName().equals(className)) {

					RegistryTerm registryTerm = new RegistryTerm();

					// get name
					if (hasNameProperty != null
							&& in.getPropertyValue(hasNameProperty) != null) {
						registryTerm.setName(in
								.getPropertyValue(hasNameProperty).asLiteral()
								.getString());

					}
					// get description
					if (hasDescriptionProperty != null
							&& in.getPropertyValue(hasDescriptionProperty) != null) {
						registryTerm.setDescription(in
								.getPropertyValue(hasDescriptionProperty)
								.asLiteral().getString());

					}
					// get value space
					if (hasValueSpaceProperty != null
							&& in.getPropertyValue(hasValueSpaceProperty) != null) {
						registryTerm.setValueSpace(in
								.getPropertyValue(hasValueSpaceProperty)
								.asLiteral().getString());
					}
					// get default value
					if (hasDefaultValueProperty != null
							&& in.getPropertyValue(hasDefaultValueProperty) != null) {
						registryTerm.setDefaultValue(in
								.getPropertyValue(hasDefaultValueProperty)
								.asLiteral().getString());
					}
					// get type
					if (hasTypeProperty != null
							&& in.getPropertyValue(hasTypeProperty) != null) {
						registryTerm.setType(in
								.getPropertyValue(hasTypeProperty).asLiteral()
								.getString());
					}
					// get id
					if (hasIDProperty != null
							&& in.getPropertyValue(hasIDProperty) != null) {
						registryTerm.setId(in.getPropertyValue(hasIDProperty)
								.asLiteral().getString());
					}
					// get notes
					if (hasNotesProperty != null
							&& in.getPropertyValue(hasNotesProperty) != null) {
						registryTerm.setNotes(in
								.getPropertyValue(hasNotesProperty).asLiteral()
								.getString());
					}

					String aliasString = "";

					if (in.getPropertyResourceValue(hasAliasProperty) != null) {
						StmtIterator it2 = in.listProperties(hasAliasProperty);
						while (it2.hasNext()) {
							StatementImpl st = (StatementImpl) it2.next();
							Resource r = st.getResource();
							Individual ind = ontologyModel.getIndividual(r
									.getURI());
							uris.add(ind.getURI().toLowerCase());
							if (hasNameProperty != null
									&& ind.getPropertyValue(hasNameProperty) != null) {

								String name = ind
										.getPropertyValue(hasNameProperty)
										.asLiteral().getString();
								// empty name
								if (name.isEmpty())
									name = "undefined name";

								registryTerm.getAlias().add(
										name
												+ "(ID = "
												+ ind.getPropertyValue(
														hasIDProperty)
														.asLiteral()
														.getString() + ")");
								aliasString = aliasString.concat(ind
										.getPropertyValue(hasIDProperty)
										.asLiteral().getString()
										+ "#");

							}

							// registryTerm.getAlias().add(st.getLocalName());
						}
					}

					String checkString = "";
					if (!registryTerm.getName().isEmpty())
						checkString = registryTerm.getName() + "#";

					if (!registryTerm.getDescription().isEmpty())
						checkString = checkString.concat(registryTerm
								.getDescription() + "#");

					if (!registryTerm.getNotes().isEmpty())
						checkString = checkString.concat(registryTerm
								.getNotes() + "#");

					if (!registryTerm.getId().isEmpty())
						checkString = checkString.concat(registryTerm.getId()
								+ "#");

					if (!registryTerm.getValueSpace().isEmpty())
						checkString = checkString.concat(registryTerm
								.getValueSpace() + "#");

					registryTerm.setCheck(checkString);

					String status = "accepted";
					if (className.equals("ProposedRegistryTerms"))
						status = "proposed";
					registryTerm.setFound(false);

					registryTerm.setStatus(status);

					registryTerm.setCheck(registryTerm.getCheck()
							+ registryTerm.getStatus() + "#");

					registryTerm.setAliasString(aliasString);

					registryTerm.setInd(in);
					registryTerm.setOntologyURI(in.getURI());

					list.add(registryTerm);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public List<String> getInstancesOfProposedRegistryClass() {
		List<String> list = new ArrayList<String>();
		OntClass cl = ontologyModel.getOntClass(NS + "ProposedRegistryTerms");
		Iterator it = cl.listInstances();
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "RegistryTerm_hasName");
		while (it.hasNext()) {
			IndividualImpl in = (IndividualImpl) it.next();
			if (dProperty != null && in.getPropertyValue(dProperty) != null) {
				list.add(in.getPropertyValue(dProperty).asLiteral().getString());

			}

		}
		return list;
	}

	public IndividualImpl getInstanceOfClass(String text, String individualName) {
		OntClass cl = ontologyModel.getOntClass(NS + text);
		Iterator it = cl.listInstances();
		while (it.hasNext()) {
			IndividualImpl in = (IndividualImpl) it.next();
			if (in.getLocalName().equals(individualName)) {
				return in;
			}
		}
		return null;
	}

	public String getRegistryTermByName(String name,
			List<IndividualImpl> listWithIndividualsOfProposedRegistry,
			List<IndividualImpl> listWithIndividualsOfRegistry) {
		// find property individual
		DatatypeProperty RegistryTerm_hasName = ontologyModel
				.getDatatypeProperty(NS + "RegistryTerm_hasName");
		if (!name.equals("")) {

			for (int j = 0; j < listWithIndividualsOfRegistry.size(); j++) {
				IndividualImpl in = listWithIndividualsOfRegistry.get(j);

				if (in.getPropertyValue(RegistryTerm_hasName) != null) {
					if (in.getPropertyValue(RegistryTerm_hasName).asLiteral()
							.getString().equals(name)) {
						return in.getLocalName();

					}
				}
			}

			for (int j = 0; j < listWithIndividualsOfProposedRegistry.size(); j++) {
				IndividualImpl in = listWithIndividualsOfProposedRegistry
						.get(j);

				if (in.getPropertyValue(RegistryTerm_hasName) != null) {
					if (in.getPropertyValue(RegistryTerm_hasName).asLiteral()
							.getString().equals(name)) {
						return in.getLocalName();

					}
				}
			}
		}
		return "";
	}

	public String getRegistryOntologyTermByName(String name) {
		// find property individual

		DatatypeProperty RegistryTerm_hasName = ontologyModel
				.getDatatypeProperty(NS + "RegistryTerm_hasName");
		List<IndividualImpl> list = getInstancesOfClassAsIndividuals("Registry");
		for (int j = 0; j < list.size(); j++) {
			IndividualImpl in = list.get(j);
			if (in.getLocalName().equals(name)) {

				if (in.getPropertyValue(RegistryTerm_hasName) != null) {
					return in.getPropertyValue(RegistryTerm_hasName)
							.asLiteral().getString();
				}
			}

		}
		list = getInstancesOfClassAsIndividuals("ProposedRegistryTerms");
		for (int j = 0; j < list.size(); j++) {
			IndividualImpl in = list.get(j);
			if (in.getLocalName().equals(name)) {

				if (in.getPropertyValue(RegistryTerm_hasName) != null) {
					return in.getPropertyValue(RegistryTerm_hasName)
							.asLiteral().getString();
				}
			}

		}
		return "";
	}

	public RegistryTerm getRegistryOntologyTerm(String name) {
		// find property individual

		RegistryTerm term = new RegistryTerm();

		DatatypeProperty RegistryTerm_hasID = ontologyModel
				.getDatatypeProperty(NS + "RegistryTerm_hasID");
		DatatypeProperty RegistryTerm_hasName = ontologyModel
				.getDatatypeProperty(NS + "RegistryTerm_hasName");
		List<IndividualImpl> list = getInstancesOfClassAsIndividuals("Registry");
		for (int j = 0; j < list.size(); j++) {
			IndividualImpl in = list.get(j);
			if (in.getLocalName().equals(name)) {

				if (in.getPropertyValue(RegistryTerm_hasName) != null)
					term.setName(in.getPropertyValue(RegistryTerm_hasName)
							.asLiteral().getString());

				if (in.getPropertyValue(RegistryTerm_hasID) != null)
					term.setId(in.getPropertyValue(RegistryTerm_hasID)
							.asLiteral().getString());

				return term;

			}

		}
		list = getInstancesOfClassAsIndividuals("ProposedRegistryTerms");
		for (int j = 0; j < list.size(); j++) {
			IndividualImpl in = list.get(j);
			if (in.getLocalName().equals(name)) {

				if (in.getPropertyValue(RegistryTerm_hasName) != null)
					term.setName(in.getPropertyValue(RegistryTerm_hasName)
							.asLiteral().getString());

				if (in.getPropertyValue(RegistryTerm_hasID) != null)
					term.setId(in.getPropertyValue(RegistryTerm_hasID)
							.asLiteral().getString());

				return term;
			}

		}
		return term;
	}

	public void addInstanceObjectProperty(IndividualImpl indName,
			String propertyName, String classParentName, String value) {
		try {
			ObjectProperty prop = ontologyModel.getObjectProperty(NS
					+ propertyName);
			IndividualImpl ind = getInstanceOfClass(classParentName, value);
			if (ind == null) {
				ind = getInstanceOfClass("ProposedRegistryTerms", value);
			}
			indName.addProperty(prop, ind);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void addInstanceDatatypePropertyValue(IndividualImpl indName,
			String propertyName, String value) {
		try {
			DatatypeProperty prop = ontologyModel.getDatatypeProperty(NS
					+ propertyName);
			if (value != null) {
				indName.addProperty(prop, value);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void addInstanceDatatypePropertyDoubleValue(IndividualImpl indName,
			String propertyName, double value) {
		try {
			DatatypeProperty prop = ontologyModel.getDatatypeProperty(NS
					+ propertyName);

			indName.addProperty(prop, value);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addInstanceDatatypePropertyFloatValue(IndividualImpl indName,
			String propertyName, float value) {
		try {
			DatatypeProperty prop = ontologyModel.getDatatypeProperty(NS
					+ propertyName);

			indName.addProperty(prop, value);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addInstanceDatatypePropertyIntegerValue(IndividualImpl indName,
			String propertyName, int value) {
		try {
			DatatypeProperty prop = ontologyModel.getDatatypeProperty(NS
					+ propertyName);

			indName.addProperty(prop, value);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addNewBooleanSettingsDatatypeProperty(String name,
			boolean value, IndividualImpl ind) {
		try {
			DatatypeProperty prop = ontologyModel.createDatatypeProperty(NS
					+ name.replace(" ", ""));
			prop.addRange(XSD.xboolean);
			prop.addDomain(ontologyModel
					.getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
			uris.add(prop.getURI().toLowerCase());

			ind.addProperty(prop,
					ontologyModel.createTypedLiteral((Boolean) value));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addNewSettingsDatatypeProperty(String name, String value,
			IndividualImpl ind) {
		try {
			DatatypeProperty prop = ontologyModel.createDatatypeProperty(NS
					+ name.replace(" ", ""));
			prop.addRange(XSD.xstring);
			prop.addDomain(ontologyModel
					.getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
			uris.add(prop.getURI().toLowerCase());
			if (value != null)
				ind.addProperty(prop, value);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addNewSettingObjectProperty(String name) {
		try {
			ObjectProperty prop = ontologyModel.createObjectProperty(NS + name);
			prop.addDomain(ontologyModel
					.getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
			prop.addRange(ontologyModel
					.getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Registry"));
			uris.add(prop.getURI().toLowerCase());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<RegistryTerm> insertRegistryTerms(List<RegistryTerm> list,
			String className) {

		List<RegistryTerm> newTermsList = new ArrayList<RegistryTerm>();
		try {
			DatatypeProperty hasDescriptionProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDescription");
			DatatypeProperty hasValueSpaceProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasValueSpace");
			DatatypeProperty hasTypeProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasType");
			DatatypeProperty hasDefaultValueProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDefaultValue");
			DatatypeProperty hasNameProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasName");
			DatatypeProperty hasIDProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasID");
			DatatypeProperty hasNotesProperty = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasNotes");

			int i = 0;
			for (RegistryTerm term : list) {
				i++;
				String str = term.getId().replaceAll("[^\\p{Alpha}]", "");

				str = str.replace(" ", "_");
				// check if uri exists
				str = changeUri(str);

				OntClass cl = ontologyModel.getOntClass(NS + className);

				IndividualImpl ind = (IndividualImpl) ontologyModel
						.createIndividual(NS + str, cl);
				uris.add(ind.getURI().toLowerCase());

				System.out.println(i + " " + ind);

				ind.addProperty(hasDescriptionProperty, term.getDescription());
				ind.addProperty(hasValueSpaceProperty, term.getValueSpace());
				ind.addProperty(hasTypeProperty, term.getType());
				ind.addProperty(hasDefaultValueProperty, term.getDefaultValue());
				ind.addProperty(hasNameProperty, term.getName());
				ind.addProperty(hasIDProperty, term.getId());
				ind.addProperty(hasNotesProperty, term.getNotes());

				term.setOntologyURI(ind.getURI());
				term.setInd(ind);
				newTermsList.add(term);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newTermsList;

	}

	public void saveAliases(RegistryTerm term, List<RegistryTerm> list) {
		ObjectProperty hasAliasProperty = ontologyModel.getObjectProperty(NS
				+ "RegistryTerm_hasAlias");
		String aliasId = "";
		for (int i = 0; i < term.getAlias().size(); i++) {
			// find individual
			aliasId = term.getAlias().get(i).split("ID =")[1].replace(")", "")
					.trim();
			for (int j = 0; j < list.size(); j++) {
				if (aliasId.equals(list.get(j).getId())) {

					term.getInd().addProperty(hasAliasProperty,
							list.get(j).getInd());

				}
			}
		}
	}

	public void updateRegistryTerm(RegistryTerm newterm, RegistryTerm oldterm,
			String className) {

		try {

			DatatypeProperty RegistryTerm_hasDescription = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDescription");
			DatatypeProperty RegistryTerm_hasValueSpace = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasValueSpace");
			DatatypeProperty RegistryTerm_hasType = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasType");
			DatatypeProperty RegistryTerm_hasDefaultValue = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDefaultValue");
			DatatypeProperty RegistryTerm_hasName = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasName");
			DatatypeProperty RegistryTerm_hasID = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasID");
			DatatypeProperty RegistryTerm_hasNotes = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasNotes");
			ObjectProperty oproperty = ontologyModel.getObjectProperty(NS
					+ "RegistryTerm_hasAlias");

			if (newterm.isEdit()) {

				IndividualImpl ind;
				if (newterm.getStatus().equals(oldterm.getStatus())) {
					ind = (IndividualImpl) ontologyModel.getIndividual(oldterm
							.getOntologyURI());
					System.out.println("existing " + ind);
					// remove old properties
					ind.removeAll(RegistryTerm_hasDescription);
					ind.removeAll(RegistryTerm_hasValueSpace);
					ind.removeAll(RegistryTerm_hasType);
					ind.removeAll(RegistryTerm_hasDefaultValue);
					ind.removeAll(RegistryTerm_hasName);
					ind.removeAll(RegistryTerm_hasID);
					ind.removeAll(RegistryTerm_hasNotes);
					ind.removeAll(oproperty);

				} else {

					// if status has changed, the term is no longer proposed, it
					// has
					// been accepted
					String str = newterm.getId()
							.replaceAll("[^\\p{Alpha}]", "");
					str = str.replace(" ", "_");
					// check if uri exists

					str = changeUri(str);

					OntClass cl = ontologyModel.getOntClass(NS + "Registry");

					ind = (IndividualImpl) ontologyModel.createIndividual(NS
							+ str, cl);

					uris.add(ind.getURI().toLowerCase());
					System.out.println("accept " + ind);
				}

				// set new properties
				if (newterm.getDescription() != null)
					ind.addProperty(RegistryTerm_hasDescription,
							newterm.getDescription());

				if (newterm.getValueSpace() != null)
					ind.addProperty(RegistryTerm_hasValueSpace,
							newterm.getValueSpace());

				if (newterm.getType() != null)
					ind.addProperty(RegistryTerm_hasType, newterm.getType());

				if (newterm.getDefaultValue() != null)
					ind.addProperty(RegistryTerm_hasDefaultValue,
							newterm.getDefaultValue());

				if (newterm.getName() != null)
					ind.addProperty(RegistryTerm_hasName, newterm.getName());

				if (newterm.getId() != null)
					ind.addProperty(RegistryTerm_hasID, newterm.getId());

				if (newterm.getNotes() != null)
					ind.addProperty(RegistryTerm_hasNotes, newterm.getNotes());

				// set aliases
				for (int i = 0; i < newterm.getAlias().size(); i++) {

					String aliasId = newterm.getAlias().get(i).split("ID =")[1]
							.replace(")", "").trim();

					// find individual
					OntClass cl = ontologyModel.getOntClass(NS + "Registry");
					Iterator it = cl.listInstances();
					boolean notFound = true;
					while (it.hasNext() && notFound) {
						IndividualImpl in = (IndividualImpl) it.next();

						if (RegistryTerm_hasID != null
								&& in.getPropertyValue(RegistryTerm_hasID) != null) {
							if (aliasId.equals(in
									.getPropertyValue(RegistryTerm_hasID)
									.asLiteral().getString())) {
								ind.addProperty(oproperty, in);
								notFound = false;
								break;
							}
						}
					}
					cl = ontologyModel
							.getOntClass(NS + "ProposedRegistryTerms");
					it = cl.listInstances();

					while (it.hasNext() && notFound) {
						IndividualImpl in = (IndividualImpl) it.next();

						if (RegistryTerm_hasID != null
								&& in.getPropertyValue(RegistryTerm_hasID) != null) {
							if (aliasId.equals(in
									.getPropertyValue(RegistryTerm_hasID)
									.asLiteral().getString())) {
								ind.addProperty(oproperty, in);
								notFound = false;
								break;
							}
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<RegistryTerm> saveRegistryTerms(List<RegistryTerm> list) {

		List<RegistryTerm> newTermsList = new ArrayList<RegistryTerm>();
		try {
			DatatypeProperty RegistryTerm_hasDescription = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDescription");
			DatatypeProperty RegistryTerm_hasValueSpace = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasValueSpace");
			DatatypeProperty RegistryTerm_hasType = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasType");
			DatatypeProperty RegistryTerm_hasDefaultValue = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasDefaultValue");
			DatatypeProperty RegistryTerm_hasName = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasName");
			DatatypeProperty RegistryTerm_hasID = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasID");
			DatatypeProperty RegistryTerm_hasNotes = ontologyModel
					.getDatatypeProperty(NS + "RegistryTerm_hasNotes");

			for (RegistryTerm term : list) {

				String str = "";
				if (!term.getId().isEmpty())
					str = term.getId().replaceAll("[^\\p{Alpha}]", "");
				else
					str = term.getName().replace("[^\\p{Alpha}]", "");

				str = str.replace(" ", "_");
				// check if uri exists

				str = changeUri(str);
				String className = "";
				if (term.getStatus().equals("accepted"))
					className = "Registry";
				else
					className = "ProposedRegistryTerms";

				OntClass cl = ontologyModel.getOntClass(NS + className);
				IndividualImpl ind = (IndividualImpl) ontologyModel
						.createIndividual(NS + str, cl);

				uris.add(ind.getURI().toLowerCase());

				System.out.println(ind + " " + list.indexOf(term));

				ind.addProperty(RegistryTerm_hasDescription,
						term.getDescription());
				ind.addProperty(RegistryTerm_hasValueSpace,
						term.getValueSpace());
				ind.addProperty(RegistryTerm_hasType, term.getType());
				ind.addProperty(RegistryTerm_hasDefaultValue,
						term.getDefaultValue());
				ind.addProperty(RegistryTerm_hasName, term.getName());
				ind.addProperty(RegistryTerm_hasID, term.getId());
				ind.addProperty(RegistryTerm_hasNotes, term.getNotes());

				// for (int i = 0; i < term.getAlias().size(); i++) {
				//
				// String aliasId =
				// term.getAlias().get(i).split("ID =")[1].replace(")",
				// "").trim();
				//
				// // find individual
				// OntClass cl = ontologyModel.getOntClass(NS + "Registry");
				// Iterator it = cl.listInstances();
				// boolean notFound = true;
				// while (it.hasNext() && notFound) {
				// IndividualImpl in = (IndividualImpl) it.next();
				//
				// if (RegistryTerm_hasID != null
				// && in.getPropertyValue(RegistryTerm_hasID) != null) {
				// if (aliasId
				// .equals(in.getPropertyValue(RegistryTerm_hasID)
				// .asLiteral().getString())) {
				// ind.addProperty(oproperty, in);
				// notFound = false;
				// break;
				// }
				// }
				// }
				// cl = ontologyModel
				// .getOntClass(NS + "ProposedRegistryTerms");
				// it = cl.listInstances();
				//
				// while (it.hasNext() && notFound) {
				// IndividualImpl in = (IndividualImpl) it.next();
				//
				// if (RegistryTerm_hasID != null
				// && in.getPropertyValue(RegistryTerm_hasID) != null) {
				// if (aliasId
				// .equals(in.getPropertyValue(RegistryTerm_hasID)
				// .asLiteral().getString())) {
				// ind.addProperty(oproperty, in);
				// notFound = false;
				// break;
				// }
				// }
				// }
				// }
				term.setOntologyURI(ind.getURI());
				term.setInd(ind);
				newTermsList.add(term);
			}
			// saveToOWL();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newTermsList;
	}

	public void deleteIndividual(IndividualImpl ind) {
		try {
			ind.remove();
			System.out.println("deleted " + ind.getURI());
			uris.remove(ind.getURI());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public OntModel getOntologyModel() {
		return ontologyModel;
	}

	public void saveToOWL() {
		try {
			System.out.println("saveToOWL is called..");

			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd(HH_mm)");
			Calendar cal = Calendar.getInstance();
			System.out.println(dateFormat.format(cal.getTime()));
			String date = String.valueOf(dateFormat.format(cal.getTime()));

			// create a new backup file
			String path = ontologyPath.replace(
					"\\semanticFrameworkOfContentAndSolutions.owl", "");

			String newFileName = path
					+ "\\semanticFrameworkOfContentAndSolutions_" + date
					+ ".owl";

			File file = new File(newFileName);

			OutputStream out = new FileOutputStream(file);

			if (out != null && !out.toString().isEmpty()) {
				ontologyModel.write(out, "RDF/XML-ABBREV"); // readable rdf/xml
			}
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<List<EASTINProperty>> getETNATaxonomyProposed() {
		List<List<EASTINProperty>> res = new ArrayList<List<EASTINProperty>>();

		List<EASTINProperty> attributeProperties = new ArrayList<EASTINProperty>();
		List<EASTINProperty> measureProperties = new ArrayList<EASTINProperty>();
		List<String> list = new ArrayList<String>();

		OntClass solutions = ontologyModel.getOntClass(NS + "EASTIN_Taxonomy");
		DatatypeProperty isProposed = ontologyModel.getDatatypeProperty(NS
				+ "isProposed");
		DatatypeProperty ETNA_ID = ontologyModel.getDatatypeProperty(NS
				+ "ETNA_ID");
		DatatypeProperty EASTIN_Type = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Type");
		DatatypeProperty EASTIN_Name = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Name");
		DatatypeProperty EASTIN_Definition = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_Definition");
		DatatypeProperty EASTIN_RefersToSolutionName = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_RefersToSolutionName");
		DatatypeProperty EASTIN_RefersToSolutionID = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_RefersToSolutionID");
		DatatypeProperty EASTIN_ProponentName = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_ProponentName");
		DatatypeProperty EASTIN_ProponentEmail = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_ProponentEmail");
		DatatypeProperty EASTIN_AllSolutionsString = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_AllSolutionsString");
		DatatypeProperty ETNA_relatedToTypeOfApplication = ontologyModel
				.getDatatypeProperty(NS + "ETNA_relatedToTypeOfApplication");

		for (Iterator<OntClass> i = solutions.listSubClasses(false); i
				.hasNext();) {
			OntClass c = i.next();

			list.add(c.getLocalName());
		}

		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i);
			ETNACluster cluster = new ETNACluster();
			cluster.setOntologyName(list.get(i));
			cluster.setName(Utilities.splitCamelCase(name
					.replace("EASTIN_", "")));
			if (name.equals("EASTIN_SoftwareLicencePolicy")
					|| name.equals("EASTIN_SoftwarePricePolicy")) {
				cluster.setSingleSelection(true);
			} else
				cluster.setSingleSelection(false);
			List<IndividualImpl> instanceList = getInstancesOfClassAsIndividuals(name);
			for (int j = 0; j < instanceList.size(); j++) {
				EASTINProperty eastinProperty = new EASTINProperty();
				IndividualImpl ind = instanceList.get(j);
				eastinProperty.setInd(ind.getURI());
				eastinProperty.setProposed(true);

				if (ind.getPropertyValue(isProposed) != null
						&& ind.getPropertyValue(isProposed).asLiteral()
								.getString().equals("true")) {

					if (ind.getPropertyValue(ETNA_ID) != null) {
						eastinProperty.setId(ind.getPropertyValue(ETNA_ID)
								.asLiteral().getString());
						// System.out.println("id "+eastinProperty.getId());
					}

					if (ind.getPropertyValue(EASTIN_Type) != null) {
						eastinProperty.setType(ind
								.getPropertyValue(EASTIN_Type).asLiteral()
								.getString());
						// System.out.println("type "+eastinProperty.getType());
					}

					if (ind.getPropertyValue(EASTIN_Name) != null) {
						eastinProperty.setName(ind
								.getPropertyValue(EASTIN_Name).asLiteral()
								.getString());
						// System.out.println("name "+eastinProperty.getName());
					}

					if (ind.getPropertyValue(EASTIN_Definition) != null) {
						eastinProperty.setDefinition(ind
								.getPropertyValue(EASTIN_Definition)
								.asLiteral().getString());
						// System.out.println("def "+eastinProperty.getDefinition());
					}
					// refersToResolutionName
					if (ind.getPropertyValue(EASTIN_RefersToSolutionName) != null) {
						eastinProperty.setRefersToSolutionName(ind
								.getPropertyValue(EASTIN_RefersToSolutionName)
								.asLiteral().getString());
						// System.out.println("solutionName "+eastinProperty.getRefersToSolutionName());
					}
					// refersToSolutionID
					if (ind.getPropertyValue(EASTIN_RefersToSolutionID) != null) {
						eastinProperty.setRefersToSolutionID(ind
								.getPropertyValue(EASTIN_RefersToSolutionID)
								.asLiteral().getString());
						// System.out.println("solutionID "+eastinProperty.getRefersToSolutionID());
					}
					// proponentName
					if (ind.getPropertyValue(EASTIN_ProponentName) != null) {
						eastinProperty.setProponentName(ind
								.getPropertyValue(EASTIN_ProponentName)
								.asLiteral().getString());
						// System.out.println("proponentName "+eastinProperty.getProponentName());
					}
					// proponentEmail
					if (ind.getPropertyValue(EASTIN_ProponentEmail) != null) {
						eastinProperty.setProponentEmail(ind
								.getPropertyValue(EASTIN_ProponentEmail)
								.asLiteral().getString());
						// System.out.println("proponentEmail "+eastinProperty.getProponentEmail());
					}

					if (ind.getPropertyValue(EASTIN_AllSolutionsString) != null) {
						eastinProperty.setAllSolutionsString(ind
								.getPropertyValue(EASTIN_AllSolutionsString)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(ETNA_relatedToTypeOfApplication) != null) {
						eastinProperty.setRelatedToTypeOfApplication(ind
								.getPropertyValue(
										ETNA_relatedToTypeOfApplication)
								.asLiteral().getString());

					}

					eastinProperty.setBelongsToCluster(cluster.getName());

					if (eastinProperty.getType().toLowerCase()
							.equals("attribute")) {
						// check if it already exists
						boolean found = false;
						for (int k = 0; k < attributeProperties.size(); k++) {
							EASTINProperty p = attributeProperties.get(k);
							if ((p.getName().equals(eastinProperty.getName())
									&& p.getType().equals(
											eastinProperty.getType()) && p
									.getBelongsToCluster().equals(
											eastinProperty
													.getBelongsToCluster())))
								found = true;
						}
						if (!found)
							attributeProperties.add(eastinProperty);
					} else {
						boolean found = false;
						for (int k = 0; k < measureProperties.size(); k++) {
							EASTINProperty p = measureProperties.get(k);
							if ((p.getName().equals(eastinProperty.getName())
									&& p.getType().equals(
											eastinProperty.getType()) && p
									.getBelongsToCluster().equals(
											eastinProperty
													.getBelongsToCluster())))
								found = true;
						}
						if (!found)
							measureProperties.add(eastinProperty);
					}
					// System.out.println("@@@@@@");
				}
			}

		}

		res.add(attributeProperties);
		res.add(measureProperties);

		return res;
	}

	public List<ETNACluster> getETNATaxonomy() {
		List<ETNACluster> res = new ArrayList<ETNACluster>();
		List<String> list = new ArrayList<String>();

		OntClass solutions = ontologyModel.getOntClass(NS + "EASTIN_Taxonomy");
		DatatypeProperty isProposed = ontologyModel.getDatatypeProperty(NS
				+ "isProposed");
		DatatypeProperty ETNA_ID = ontologyModel.getDatatypeProperty(NS
				+ "ETNA_ID");
		DatatypeProperty EASTIN_Type = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Type");
		DatatypeProperty EASTIN_Name = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Name");
		DatatypeProperty ETNA_SubdivisionScopeNote = ontologyModel
				.getDatatypeProperty(NS + "ETNA_SubdivisionScopeNote");
		DatatypeProperty EASTIN_AllSolutionsString = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_AllSolutionsString");
		DatatypeProperty ETNA_relatedToTypeOfApplication = ontologyModel
				.getDatatypeProperty(NS + "ETNA_relatedToTypeOfApplication");
		Individual classInd = null;
		String comment = "";

		List<OntClass> subClasses = solutions.listSubClasses().toList();
		for (OntClass temp : subClasses) {

			classInd = temp.asIndividual();

			comment = classInd.getComment("EN");

			if (!temp.getLocalName().equals("EASTIN_Subdivision"))
				list.add(temp.getLocalName() + "@@" + comment);

		}

		for (int i = 0; i < list.size(); i++) {

			String[] splitted = list.get(i).split("@@");
			String name = splitted[0];
			ETNACluster cluster = new ETNACluster();
			cluster.setOntologyName(name);
			cluster.setName(Utilities.splitCamelCase(name
					.replace("EASTIN_", "")));

			if (!splitted[1].equals("null"))
				cluster.setDescription(splitted[1]);

			if (name.equals("EASTIN_SoftwareLicencePolicy")
					|| name.equals("EASTIN_SoftwarePricePolicy")) {
				cluster.setSingleSelection(true);
			} else
				cluster.setSingleSelection(false);

			List<IndividualImpl> instanceList = getInstancesOfClassAsIndividuals(name);
			for (int j = 0; j < instanceList.size(); j++) {
				EASTINProperty eastinProperty = new EASTINProperty();
				IndividualImpl ind = instanceList.get(j);
				eastinProperty.setInd(ind.getURI());

				if (ind.getPropertyValue(isProposed) == null
						|| ind.getPropertyValue(isProposed).asLiteral()
								.getString().equals("false")) {

					if (ind.getPropertyValue(ETNA_ID) != null) {
						eastinProperty.setId(ind.getPropertyValue(ETNA_ID)
								.asLiteral().getString());
						// System.out.println("id "+eastinProperty.getId());
					}

					if (ind.getPropertyValue(EASTIN_Type) != null) {
						eastinProperty.setType(ind
								.getPropertyValue(EASTIN_Type).asLiteral()
								.getString());
						// System.out.println("type "+eastinProperty.getType());
					}

					if (ind.getPropertyValue(EASTIN_Name) != null) {
						eastinProperty.setName(ind
								.getPropertyValue(EASTIN_Name).asLiteral()
								.getString());
						// System.out.println("name "+eastinProperty.getName());
					}

					if (ind.getPropertyValue(ETNA_SubdivisionScopeNote) != null) {
						eastinProperty.setDefinition(ind
								.getPropertyValue(ETNA_SubdivisionScopeNote)
								.asLiteral().getString());
						// System.out.println("def "+eastinProperty.getDefinition());
					}

					if (ind.getPropertyValue(EASTIN_AllSolutionsString) != null) {
						eastinProperty.setAllSolutionsString(ind
								.getPropertyValue(EASTIN_AllSolutionsString)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(ETNA_relatedToTypeOfApplication) != null) {
						eastinProperty.setRelatedToTypeOfApplication(ind
								.getPropertyValue(
										ETNA_relatedToTypeOfApplication)
								.asLiteral().getString());

					}

					eastinProperty.setBelongsToCluster(cluster.getName());
					if (eastinProperty.getType().toLowerCase()
							.equals("attribute")) {
						cluster.getProperties().add(eastinProperty);
						cluster.getAttributesToShow().add(eastinProperty);
						cluster.getAttributesToShowInView().add(eastinProperty);
					} else {
						cluster.getMeasureProperties().add(eastinProperty);
						cluster.getMeasuresToShow().add(eastinProperty);
						cluster.getMeasuresToShowInView().add(eastinProperty);
					}

					cluster.getAllproperties().add(eastinProperty);

				}
			}
			res.add(cluster);
		}
		return res;
	}

	public void addNewETNAProperty(IndividualImpl ind, ETNACluster cl,
			boolean save) {
		try {
			// save measures

			List<EASTINProperty> measures = new ArrayList<EASTINProperty>();
			for (EASTINProperty prop : cl.getMeasuresToShowInView())
				if (!prop.isProposed())
					measures.add(prop);

			for (EASTINProperty prop : cl.getMeasuresToHide())
				measures.add(prop);

			for (int i = 0; i < measures.size(); i++) {
				EASTINProperty property = measures.get(i);
				IndividualImpl ind2 = (IndividualImpl) ontologyModel
						.getIndividual(property.getInd());
				addInstanceDatatypePropertyValue(ind, ind2.getLocalName()
						.replace("_", "__"), property.getValue());
			}

			// save attributes
			ObjectProperty prop = ontologyModel.getObjectProperty(NS
					+ cl.getOntologyName().replace("_", "__"));
			DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
					+ "EASTIN_Name");

			if (cl.isSingleSelection()) {
				// find property individual
				List<IndividualImpl> list = getInstancesOfClassAsIndividuals(cl
						.getOntologyName());
				for (int j = 0; j < list.size(); j++) {
					IndividualImpl in = list.get(j);

					if (in.getPropertyValue(dProperty) != null) {
						if (in.getPropertyValue(dProperty).asLiteral()
								.getString().equals(cl.getSelectedProperty())) {
							ind.addProperty(prop, in);
							break;
						}
					}
				}
			} else {
				List<IndividualImpl> list = getInstancesOfClassAsIndividuals(cl
						.getOntologyName());
				for (int i = 0; i < cl.getSelectedProperties().size(); i++) {
					String property = cl.getSelectedProperties().get(i);
					// find property individual

					for (int j = 0; j < list.size(); j++) {
						IndividualImpl in = list.get(j);

						if (in.getPropertyValue(dProperty) != null) {
							if (in.getPropertyValue(dProperty).asLiteral()
									.getString().equals(property)) {
								ind.addProperty(prop, in);
								break;
							}
						}
					}
				}
			}

			if (save) {
				OutputStream out = new FileOutputStream(ontologyPath);
				ontologyModel.write(out, "RDF/XML-ABBREV"); // readable
				// rdf/xml
				out.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<String> loadETNAProperty(String solutionName,
			ETNACluster property) {

		// get Solution individual
		ArrayList<String> list = new ArrayList<String>();
		try {

			OntClass cl = ontologyModel.getOntClass(NS + "Solutions");
			Iterator it = cl.listInstances();
			while (it.hasNext()) {
				IndividualImpl solutionInd = (IndividualImpl) it.next();

				if (solutionName.equalsIgnoreCase(solutionInd.getURI())) {
					// find settings individual
					ObjectProperty oProperty = ontologyModel
							.getObjectProperty(NS
									+ "hasSolutionSpecificSettings");
					if (solutionInd.getPropertyResourceValue(oProperty) != null) {
						Resource r1 = solutionInd
								.getPropertyResourceValue(oProperty);
						// System.out.println(r.getURI());
						IndividualImpl ind = (IndividualImpl) ontologyModel
								.getIndividual(r1.getURI());

						for (int i = 0; i < property.getMeasureProperties()
								.size(); i++) {
							EASTINProperty prop = property
									.getMeasureProperties().get(i);
							IndividualImpl ind2 = (IndividualImpl) ontologyModel
									.getIndividual(prop.getInd());
							DatatypeProperty dProperty = ontologyModel
									.getDatatypeProperty(NS
											+ ind2.getLocalName().replace("_",
													"__"));
							// if(dProperty==null){
							// //create new datatypeProperty
							// dProperty = ontologyModel
							// .createDatatypeProperty(NS
							// + "EASTIN__"+prop.getName().replace(" ", ""));
							//
							// dProperty.addRange(XSD.xstring);
							// dProperty.addDomain(ontologyModel
							// .getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
							// uris.add(dProperty.getURI().toLowerCase());
							// }
							if (dProperty != null)
								if (ind.getPropertyValue(dProperty) != null) {
									// System.out.println(dProperty.toString());
									prop.setValue(ind
											.getPropertyValue(dProperty)
											.asLiteral().getString());
								}

						}

						// update lists with measures in order to contain the
						// values
						// of the measures
						property.setMeasuresToShow(new ArrayList<EASTINProperty>());
						property.setMeasuresToShowInView(new ArrayList<EASTINProperty>());
						for (EASTINProperty ep : property
								.getMeasureProperties()) {
							property.getMeasuresToShow().add(ep.clone());
							property.getMeasuresToShowInView().add(ep.clone());
						}

						// get selected properties (attributes) for the cluster
						Property prop = ontologyModel
								.getProperty(NS
										+ property.getOntologyName().replace(
												"_", "__"));

						// if (property.getName().contains("Operating"))
						// System.out.println(ind.listPropertyValues(prop)
						// .toList().size());
						List<RDFNode> nodeIt = ind.listPropertyValues(prop)
								.toList();
						// NodeIterator nodeIt = ind.listPropertyValues(prop);
						// while (nodeIt.hasNext()) {

						if (nodeIt.size() > 0)
							for (RDFNode rd : nodeIt) {

								// RDFNode node = (RDFNode) nodeIt.next();
								// Resource r = node.asResource();
								Resource r = rd.asResource();
								boolean flag = false;
								if (r != null) {
									// get individual of resource
									OntClass cl2 = ontologyModel.getOntClass(NS
											+ "EASTIN_Taxonomy");
									Iterator it2 = cl2.listInstances();
									while (it2.hasNext()) {
										IndividualImpl in = (IndividualImpl) it2
												.next();
										if (in.getLocalName().equals(
												r.getLocalName())) {
											DatatypeProperty dProperty = ontologyModel
													.getDatatypeProperty(NS
															+ "EASTIN_Name");
											if (in.getPropertyValue(dProperty) != null) {
												if (property
														.isSingleSelection()) {
													property.setSelectedProperty(in
															.getPropertyValue(
																	dProperty)
															.asLiteral()
															.getString());
													flag = true;

												} else {

													if (!list.contains(in
															.getPropertyValue(
																	dProperty)
															.asLiteral()
															.getString()))
														list.add(in
																.getPropertyValue(
																		dProperty)
																.asLiteral()
																.getString());
													flag = true;

												}
											}

										}

										if (flag)
											break;
									}
								}

							}

						return list;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public boolean uriExists(String text) {
		if (text.contains(NS)) {
			if (uris.contains(text.toLowerCase())) {
				return true;
			} else
				return false;
		} else if (uris.contains(NS.toLowerCase() + text.toLowerCase())) {
			return true;
		} else
			return false;

	}

	public String changeUri(String str) {
		String res = str;
		Random randomGenerator = new Random();
		boolean found = true;
		while (found) {
			if (uriExists(res)) {
				res = str + "_" + randomGenerator.nextInt(10000);
				found = true;
			} else
				found = false;
		}
		return res;
	}

	// public String saveSolution(Solution solution, Solution originalSolution)
	// {
	//
	// DatatypeProperty hasSolutionName = ontologyModel.getDatatypeProperty(NS
	// + "hasSolutionName");
	// DatatypeProperty hasSolutionDescription =
	// ontologyModel.getDatatypeProperty(NS
	// + "hasSolutionDescription");
	// DatatypeProperty id = ontologyModel.getDatatypeProperty(NS
	// + "id");
	// DatatypeProperty BasicInfo_ManufacturerName =
	// ontologyModel.getDatatypeProperty(NS
	// + "BasicInfo_ManufacturerName");
	// DatatypeProperty BasicInfo_InsertDate =
	// ontologyModel.getDatatypeProperty(NS
	// + "BasicInfo_InsertDate");
	// DatatypeProperty BasicInfo_LatestUpdate =
	// ontologyModel.getDatatypeProperty(NS
	// + "BasicInfo_LatestUpdate");
	// DatatypeProperty BasicInfo_ManufacturerCountry =
	// ontologyModel.getDatatypeProperty(NS
	// + "BasicInfo_ManufacturerCountry");
	// DatatypeProperty BasicInfo_ManufacturerWebsite =
	// ontologyModel.getDatatypeProperty(NS
	// + "BasicInfo_ManufacturerWebsite");
	// DatatypeProperty BasicInfo_Image = ontologyModel.getDatatypeProperty(NS
	// + "BasicInfo_Image");
	// DatatypeProperty BasicInfo_DownloadOrPurchaseWebpage =
	// ontologyModel.getDatatypeProperty(NS
	// + "BasicInfo_DownloadOrPurchaseWebpage");
	// DatatypeProperty hasConstraints = ontologyModel.getDatatypeProperty(NS
	// + "hasConstraints");
	// DatatypeProperty hasStartCommand = ontologyModel.getDatatypeProperty(NS
	// + "hasStartCommand");
	// DatatypeProperty hasStopCommand = ontologyModel.getDatatypeProperty(NS
	// + "hasStopCommand");
	// DatatypeProperty hasHandlerType = ontologyModel.getDatatypeProperty(NS
	// + "hasHandlerType");
	// DatatypeProperty hasOptions = ontologyModel.getDatatypeProperty(NS
	// + "hasOptions");
	//
	// try {
	//
	// String str ="";
	// if(solution.getOntologyURI().isEmpty()){
	// str = solution.getName().trim().replace(" ", "_")
	// .replaceAll("[^\\p{L}\\p{Nd}]", "");
	//
	// // check if uri exists
	// if (Character.isDigit(str.charAt(0))) {
	// str = "app";
	// }
	// str = changeUri(str);
	// }else
	// str = solution
	// .getOntologyURI()
	// .replace(NS,"");
	//
	// // addIndividual(solution.getOntologyCategory().replace(" ", ""), str);
	// //
	// // IndividualImpl solutionInd = getInstanceOfClass(solution
	// // .getOntologyCategory().replace(" ", ""), str);
	// //
	//
	// OntClass className = ontologyModel.getOntClass(NS +
	// solution.getOntologyCategory().replace(" ", ""));
	// IndividualImpl solutionInd = (IndividualImpl) ontologyModel
	// .createIndividual(NS + str, className);
	// // add property values
	//
	// solutionInd.addProperty(hasSolutionName, solution.getName());
	// solutionInd.addProperty(hasSolutionDescription,
	// solution.getDescription());
	// solutionInd.addProperty(id, solution.getId());
	// solutionInd.addProperty(hasStartCommand, solution.getStartCommand());
	// solutionInd.addProperty(hasStopCommand, solution.getStopCommand());
	// solutionInd.addProperty(hasHandlerType, solution.getHandlerType());
	//
	//
	// //concat options with values
	// String optionsDetails = "";
	// for (Element el : solution.getOptions()) {
	// if (el.getOptionValue().isEmpty())
	// el.setOptionValue("empty");
	//
	// optionsDetails = optionsDetails.concat(el.getOptionName()
	// + "__" + el.getOptionValue() + "$$");
	// }
	// solutionInd.addProperty(hasOptions, optionsDetails);
	//
	//
	// // add technical details instance
	//
	// String str2 = str + "_TechnicalDetails";
	// str2 = changeUri(str2);
	// IndividualImpl technicalDetailsInd = (IndividualImpl) addIndividual(
	// "TechnicalDetailsDatasetForProductDescription", str2);
	//
	// ObjectProperty prop2 = ontologyModel.getObjectProperty(NS
	// + "hasTechnicalDetails");
	//
	// solutionInd.addProperty(prop2, technicalDetailsInd);
	//
	// technicalDetailsInd.addProperty(BasicInfo_ManufacturerName,
	// solution.getManufacturerName());
	//
	// if (solution.getDate() != null) {
	// SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
	// Locale.ENGLISH);
	// technicalDetailsInd.addProperty(BasicInfo_InsertDate,
	// sdf2.format(solution.getDate()));
	//
	// }
	// if (solution.getUpdateDate() != null) {
	// SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
	// Locale.ENGLISH);
	// technicalDetailsInd.addProperty(BasicInfo_LatestUpdate,
	// sdf2.format(solution.getUpdateDate()));
	// }
	//
	// technicalDetailsInd.addProperty(BasicInfo_ManufacturerCountry,
	// solution.getManufacturerCountry());
	// technicalDetailsInd.addProperty(BasicInfo_ManufacturerWebsite,
	// solution.getManufacturerWebsite());
	// technicalDetailsInd.addProperty(BasicInfo_Image,
	// solution.getImageUrl());
	// technicalDetailsInd.addProperty(BasicInfo_DownloadOrPurchaseWebpage,
	// solution.getDownloadPage());
	//
	// if (solution.getConstraints().equals(
	// "e.g. needs JRE 1.6.2 or later"))
	// solution.setConstraints("");
	//
	// technicalDetailsInd.addProperty(hasConstraints,
	// solution.getConstraints());
	//
	// // save proposed EASTIN clusters
	// for (int i = 0; i < solution.getProposedClusters().size(); i++) {
	// createEASTINCluster(solution.getProposedClusters().get(i),
	// solution.getName(), true);
	// }
	//
	// List<String> parentURIs = new ArrayList<String>();
	// // check if settings belong to an instance, if yes it is an update
	// // of existing application
	// for (int i = 0; i < solution.getSettings().size(); i++) {
	// Setting set = solution.getSettings().get(i);
	// if (!set.getParentInstanceURI().equals("")) {
	// if (!parentURIs.contains(set.getParentInstanceURI()))
	// parentURIs.add(set.getParentInstanceURI());
	// } else {
	// String str1 = NS + str + "_Instance";
	// if (!parentURIs.contains(str1))
	// parentURIs.add(str1);
	// }
	// }
	//
	// // create settings instance
	// // create settings subclass
	// OntClass cl = ontologyModel.getOntClass(NS
	// + solutionInd.getOntClass().getLocalName() + "Settings");
	// if (cl == null) {
	// OntClass cl2 = ontologyModel.getOntClass(NS + "Settings");
	// cl = ontologyModel
	// .createClass(NS
	// + solutionInd.getOntClass().getLocalName()
	// + "Settings");
	// cl2.addSubClass(cl);
	//
	// }
	// // create setting instances
	// List<IndividualImpl> parentInstances = new ArrayList<IndividualImpl>();
	// for (int i = 0; i < parentURIs.size(); i++) {
	// IndividualImpl ind = (IndividualImpl) cl
	// .createIndividual(parentURIs.get(i));
	// parentInstances.add(ind);
	// uris.add(ind.getURI().toLowerCase());
	//
	// }
	// if (parentURIs.contains(NS + str + "_Instance")) {
	// parentInstances.add((IndividualImpl) cl.createIndividual(NS
	// + str + "_Instance"));
	// uris.add((NS + str + "_Instance").toLowerCase());
	//
	// }
	// // add settings properties
	// for (int i = 0; i < parentInstances.size(); i++) {
	// ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
	// + "hasSolutionSpecificSettings");
	//
	// solutionInd.addProperty(prop3, parentInstances.get(i));
	//
	// }
	//
	// System.out.println(solution.getSettings().size());
	// for (int i = 0; i < solution.getSettings().size(); i++) {
	// Setting set = solution.getSettings().get(i);
	// System.out.println(i + " " + set.getName());
	// // find setting instance
	// IndividualImpl settingInstance = null;
	// if (set.getParentInstanceURI().equals("")) {
	// settingInstance = (IndividualImpl) ontologyModel
	// .getIndividual(NS + str + "_Instance");
	//
	// } else
	// for (int j = 0; j < parentInstances.size(); j++) {
	// if (set.getParentInstanceURI().equals(
	// parentInstances.get(j).getURI())) {
	// settingInstance = parentInstances.get(j);
	// break;
	// }
	// }
	// {
	//
	// set.setName(set.getName().replace(" ", ""));
	//
	// String setUri = set.getName();
	// // check if uri exists
	// if (set.getCurrentURI().equals(""))
	// setUri = changeUri(setUri);
	// else
	// setUri = set.getCurrentURI().replace(NS, "");
	// String ontProp = set.getMapping();
	// // find ontology name for registry term
	//
	// if (set.ishasMapping()) {
	// String mappingOntologyName = getRegistryTermByName(ontProp);
	//
	// String datatypePropertyName = setUri
	// + "_isMappedToRegTerm";
	// addNewSettingObjectProperty(datatypePropertyName);
	//
	// addInstanceObjectProperty(settingInstance,
	// datatypePropertyName, "Registry",
	// mappingOntologyName);
	//
	// }
	// // save values
	//
	// String value = set.getValue();
	// // addNewSettingsDatatypeProperty(setUri, false);
	// DatatypeProperty prop33 = ontologyModel
	// .createDatatypeProperty(NS
	// + setUri.replace(" ", ""));
	// if (set.getType().equals("boolean")) {
	// prop33.addRange(XSD.xboolean);
	// } else if (set.getType().equals("double")) {
	// prop33.addRange(XSD.xdouble);
	// } else if (set.getType().equals("integer")) {
	// prop33.addRange(XSD.xint);
	// } else
	// prop33.addRange(XSD.xstring);
	// prop33.addDomain(ontologyModel
	// .getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
	// uris.add(prop33.getURI().toLowerCase());
	//
	// addInstanceDatatypePropertyValue(settingInstance, setUri,
	// value);
	// // save name
	//
	// addNewSettingsDatatypeProperty(setUri + "_hasName");
	// addInstanceDatatypePropertyValue(settingInstance, setUri
	// + "_hasName", set.getName());
	// // save description
	//
	// if (!set.getDescription().equals("")) {
	// addNewSettingsDatatypeProperty(setUri
	// + "_hasDescription");
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_hasDescription",
	// set.getDescription());
	// }
	//
	// // save value space
	// if (!set.getValueSpace().equals("")) {
	// addNewSettingsDatatypeProperty(setUri
	// + "_hasValueSpace");
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_hasValueSpace", set.getValueSpace());
	// }
	// // save type
	// if (!set.getType().equals("")) {
	// addNewSettingsDatatypeProperty(setUri + "_hasType");
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_hasType", set.getType());
	// }
	// // save id
	// if (!set.getId().equals("")) {
	// addNewSettingsDatatypeProperty(setUri + "_hasID");
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_hasID", set.getId());
	// }
	//
	// // save constraints
	// if (!set.getConstraints().equals("")) {
	// if (set.getConstraints()
	// .equals("e.g. MagnifierPosition=fullScreen requires AERO Design Windows 7 to be ON"))
	// {
	// set.setConstraints("");
	// }
	//
	// addNewSettingsDatatypeProperty(setUri
	// + "_hasConstraints");
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_hasConstraints",
	// set.getConstraints());
	//
	// }
	//
	// //TODO
	//
	// addNewBooleanSettingsDatatypeProperty(setUri
	// + "appliedLive");
	//
	// DatatypeProperty prop = ontologyModel
	// .createDatatypeProperty(NS + setUri
	// + "appliedLive".replace(" ", ""));
	//
	//
	// settingInstance.addProperty(prop,ontologyModel
	// .createTypedLiteral((Boolean) set
	// .isAppliedLive()));
	//
	//
	//
	// if (set.ishasMapping()) {
	// // save is exact matching
	// addNewBooleanSettingsDatatypeProperty(setUri
	// + "_isExactMatching");
	//
	// prop = ontologyModel
	// .createDatatypeProperty(NS + setUri
	// + "_isExactMatching".replace(" ", ""));
	//
	// settingInstance.addProperty(prop, ontologyModel
	// .createTypedLiteral((Boolean) set
	// .isExactMatching()));
	// // save comments
	//
	// if (!set.isExactMatching()) {
	// addNewSettingsDatatypeProperty(setUri
	// + "_hasCommentsForMapping");
	//
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_hasCommentsForMapping",
	// set.getComments());
	// } else {
	// addNewSettingsDatatypeProperty(setUri
	// + "_hasCommentsForMapping");
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_hasCommentsForMapping", "");
	// }
	//
	// } else {
	// addNewSettingsDatatypeProperty(setUri
	// + "_isMappedToRegTerm");
	// addInstanceDatatypePropertyValue(settingInstance,
	// setUri + "_isMappedToRegTerm", "null");
	//
	// }
	//
	// }
	//
	// }
	//
	// // create settings instance if it does not exist
	// if (parentInstances.size() == 0) {
	// String p = str + "Settings";
	// p = changeUri(p);
	// IndividualImpl ind = (IndividualImpl) cl.createIndividual(NS
	// + p);
	// parentInstances.add(ind);
	// uris.add(ind.getURI().toLowerCase());
	// ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
	// + "hasSolutionSpecificSettings");
	//
	// solutionInd.addProperty(prop3, ind);
	// }
	//
	// for (int i = 0; i < solution.getEtnaProperties().size(); i++) {
	// ETNACluster cl2 = solution.getEtnaProperties().get(i);
	// for (int k = 0; k < solution.getProposedAttributeClusterItems()
	// .size(); k++) {
	// if (cl2.getName().equalsIgnoreCase(
	// solution.getProposedAttributeClusterItems().get(k)
	// .getBelongsToCluster())) {
	// addNewETNAPropertyInCluster(cl2, solution
	// .getProposedAttributeClusterItems().get(k));
	// }
	// }
	// // save proposed EASTIN Cluster Items
	// for (int k = 0; k < solution.getProposedMeasureClusterItems()
	// .size(); k++) {
	// if (cl2.getName().equalsIgnoreCase(
	// solution.getProposedMeasureClusterItems().get(k)
	// .getBelongsToCluster())) {
	// addNewETNAPropertyInCluster(cl2, solution
	// .getProposedMeasureClusterItems().get(k));
	// }
	// }
	//
	// for (int j = 0; j < parentInstances.size(); j++)
	// addNewETNAProperty(parentInstances.get(j), cl2, false);
	//
	//
	//
	//
	// }
	//
	// // save solution access info for vendors
	//
	// saveAccessInfoForVendors(solutionInd, solution);
	// saveAccessInfoForUsers(solutionInd, solution);
	//
	// // save vendor
	// if (solution.getVendor() != null
	// && !solution.getVendor().equals("")) {
	// ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
	// + "hasSolutionVendor");
	// Individual vendorInd = ontologyModel.getIndividual(solution
	// .getVendor());
	// if (vendorInd != null)
	// solutionInd.addProperty(prop3, vendorInd);
	// }
	// // save SLAs
	//
	// // saveSLAs(solutionInd, solution);
	//
	// // save to owl
	// // saveToOWL();
	// // Runtime.getRuntime().gc();
	//
	// System.out.println("saved");
	//
	// return str;
	//
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// FacesMessage msg = new FacesMessage("Error",
	// "An error occured while saving solution");
	//
	// FacesContext.getCurrentInstance().addMessage(null, msg);
	// // JOptionPane.showMessageDialog(null,
	// // "An error occured while saving solution", "", 1);
	// }
	// return "";
	// }

	public String saveSolution(Solution solution, Solution originalSolution,
			ArrayList<SettingsHandler> settingsHandlers) {

		DatatypeProperty hasSolutionName = ontologyModel.getDatatypeProperty(NS
				+ "hasSolutionName");
		DatatypeProperty hasSolutionDescription = ontologyModel
				.getDatatypeProperty(NS + "hasSolutionDescription");
		DatatypeProperty id = ontologyModel.getDatatypeProperty(NS + "id");
		DatatypeProperty BasicInfo_ManufacturerName = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_ManufacturerName");
		DatatypeProperty BasicInfo_InsertDate = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_InsertDate");
		DatatypeProperty BasicInfo_LatestUpdate = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_LatestUpdate");
		DatatypeProperty BasicInfo_ManufacturerCountry = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_ManufacturerCountry");
		DatatypeProperty BasicInfo_ManufacturerWebsite = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_ManufacturerWebsite");
		DatatypeProperty BasicInfo_Image = ontologyModel.getDatatypeProperty(NS
				+ "BasicInfo_Image");
		DatatypeProperty BasicInfo_DownloadOrPurchaseWebpage = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_DownloadOrPurchaseWebpage");
		DatatypeProperty hasConstraints = ontologyModel.getDatatypeProperty(NS
				+ "hasConstraints");
		DatatypeProperty hasStartCommand = ontologyModel.getDatatypeProperty(NS
				+ "hasStartCommand");
		DatatypeProperty hasStopCommand = ontologyModel.getDatatypeProperty(NS
				+ "hasStopCommand");
		DatatypeProperty hasCapabilities = ontologyModel.getDatatypeProperty(NS
				+ "hasCapabilities");
		DatatypeProperty hasCapabilitiesTransformations = ontologyModel
				.getDatatypeProperty(NS + "hasCapabilitiesTransformations");
		DatatypeProperty hasCategories = ontologyModel.getDatatypeProperty(NS
				+ "hasCategories");

		List<IndividualImpl> listWithIndividualsOfRegistry = getInstancesOfClassAsIndividuals("Registry");

		List<IndividualImpl> listWithIndividualsOfProposedRegistry = getInstancesOfClassAsIndividuals("ProposedRegistryTerms");

		Resource stringSource = XSD.xstring;
		Resource booleanSource = XSD.xboolean;
		Literal literalFalse = ontologyModel
				.createTypedLiteral((Boolean) false);
		Literal literalTrue = ontologyModel.createTypedLiteral((Boolean) true);

		try {

			String str = "";
			if (solution.getOntologyURI().isEmpty()) {
				str = solution.getName().trim().replace(" ", "_")
						.replaceAll("[^\\p{L}\\p{Nd}]", "");

				// check if uri exists
				if (Character.isDigit(str.charAt(0))) {
					str = "app";
				}
				str = changeUri(str);
			} else
				str = solution.getOntologyURI().replace(NS, "");

			if (solution.getOntologyCategory() == null
					|| solution.getOntologyCategory().isEmpty())
				solution.setOntologyCategory("Solutions");

			OntClass className = ontologyModel.getOntClass(NS
					+ solution.getOntologyCategory().replace(" ", ""));
			IndividualImpl solutionInd = (IndividualImpl) ontologyModel
					.createIndividual(NS + str, className);
			// add property values

			solutionInd.addProperty(hasSolutionName, solution.getName().trim());
			solutionInd.addProperty(hasSolutionDescription, solution
					.getDescription().trim());
			solutionInd.addProperty(id, solution.getId().trim());
			solutionInd.addProperty(hasStartCommand, solution.getStartCommand()
					.trim());
			solutionInd.addProperty(hasStopCommand, solution.getStopCommand()
					.trim());
			solutionInd.addProperty(hasCapabilities, solution.getCapabilities()
					.trim().replace("\r\n", ""));
			solutionInd.addProperty(hasCapabilitiesTransformations, solution
					.getCapabilitiesTransformations().trim());

			String categories = "";
			for (String s : solution.getCategories()) {
				categories = categories.concat(s + ",");
			}

			if (solution.getCategories().size() > 0) {
				categories = categories.substring(0, categories.length() - 1);
				solutionInd.addProperty(hasCategories, categories);
			}

			// add technical details instance

			String str2 = str + "_TechnicalDetails";
			str2 = changeUri(str2);
			IndividualImpl technicalDetailsInd = (IndividualImpl) addIndividual(
					"TechnicalDetailsDatasetForProductDescription", str2);

			ObjectProperty prop2 = ontologyModel.getObjectProperty(NS
					+ "hasTechnicalDetails");

			solutionInd.addProperty(prop2, technicalDetailsInd);

			technicalDetailsInd.addProperty(BasicInfo_ManufacturerName,
					solution.getManufacturerName());

			if (solution.getDate() != null) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
						Locale.ENGLISH);
				technicalDetailsInd.addProperty(BasicInfo_InsertDate,
						sdf2.format(solution.getDate()));

			}
			if (solution.getUpdateDate() != null) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
						Locale.ENGLISH);
				technicalDetailsInd.addProperty(BasicInfo_LatestUpdate,
						sdf2.format(solution.getUpdateDate()));
			}

			technicalDetailsInd.addProperty(BasicInfo_ManufacturerCountry,
					solution.getManufacturerCountry());
			technicalDetailsInd.addProperty(BasicInfo_ManufacturerWebsite,
					solution.getManufacturerWebsite());
			technicalDetailsInd.addProperty(BasicInfo_Image,
					solution.getImageUrl());
			technicalDetailsInd.addProperty(
					BasicInfo_DownloadOrPurchaseWebpage,
					solution.getDownloadPage());

			if (solution.getConstraints().equals(
					"e.g. needs JRE 1.6.2 or later"))
				solution.setConstraints("");

			technicalDetailsInd.addProperty(hasConstraints,
					solution.getConstraints());

			saveHandlerForSolution(solution, solutionInd, str, settingsHandlers);

			// save proposed EASTIN clusters
			for (int i = 0; i < solution.getProposedClusters().size(); i++) {
				createEASTINCluster(solution.getProposedClusters().get(i),
						solution.getName(), true);
			}

			List<String> parentURIs = new ArrayList<String>();
			// check if settings belong to an instance, if yes it is an update
			// of existing application
			for (int i = 0; i < solution.getSettings().size(); i++) {
				// Setting set = solution.getSettings().get(i);
				// if (!set.getParentInstanceURI().equals("")) {
				// if (!parentURIs.contains(set.getParentInstanceURI()))
				// parentURIs.add(set.getParentInstanceURI());
				// } else {
				String str1 = NS + str + "_Instance";
				if (!parentURIs.contains(str1))
					parentURIs.add(str1);
				// }
			}

			// create settings instance
			// create settings subclass
			OntClass cl = ontologyModel.getOntClass(NS
					+ solutionInd.getOntClass().getLocalName() + "Settings");
			if (cl == null) {
				OntClass cl2 = ontologyModel.getOntClass(NS + "Settings");
				cl = ontologyModel
						.createClass(NS
								+ solutionInd.getOntClass().getLocalName()
								+ "Settings");
				cl2.addSubClass(cl);

			}

			OntClass solutionClass = ontologyModel.createClass(NS
					+ solutionInd.getLocalName() + "Settings");
			cl.addSubClass(solutionClass);

			// create setting instances
			List<IndividualImpl> parentInstances = new ArrayList<IndividualImpl>();
			for (int i = 0; i < parentURIs.size(); i++) {

				if (parentURIs.get(i).contains(NS + str + "_Instance")) {
					IndividualImpl ind = (IndividualImpl) solutionClass
							.createIndividual(parentURIs.get(i));
					parentInstances.add(ind);
					uris.add(ind.getURI().toLowerCase());
				}

			}
			// if (parentURIs.contains(NS + str + "_Instance")) {
			// parentInstances.add((IndividualImpl) solutionClass
			// .createIndividual(NS + str + "Setting_Instance"));
			// uris.add((NS + str + "_Instance").toLowerCase());
			//
			// }
			// add settings properties
			for (int i = 0; i < parentInstances.size(); i++) {
				ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
						+ "hasSolutionSpecificSettings");

				solutionInd.addProperty(prop3, parentInstances.get(i));

			}
			Resource domainSetting = ontologyModel.getResource(solutionClass
					.getURI());
			IndividualImpl settingInstance = null;

			settingInstance = (IndividualImpl) ontologyModel.getIndividual(NS
					+ str + "_Instance");

			System.out.println(solution.getSettings().size());
			for (int i = 0; i < solution.getSettings().size(); i++) {
				Setting set = solution.getSettings().get(i);
				System.out.println(i + " " + set.getName());
				// find setting instance
				if (i % 100 == 0)
					System.gc();

				// if (!set.getParentInstanceURI().equals("")) {
				//
				// for (int j = 0; j < parentInstances.size(); j++) {
				// if (set.getParentInstanceURI().equals(
				// parentInstances.get(j).getURI())) {
				// settingInstance = parentInstances.get(j);
				// break;
				// }
				// }
				// }
				{

					// set.setName(set.getName().replace(" ", ""));

					String setUri = set.getName().replace(" ", "")
							.replaceAll("[^\\p{L}\\p{Nd}]", "");
					// check if uri exists
					if (set.getCurrentURI().equals(""))
						setUri = changeUri(setUri);
					else
						setUri = set.getCurrentURI().replace(NS, "");
					String ontProp = set.getMapping();
					// find ontology name for registry term

					if (set.ishasMapping()) {

						String mappingOntologyName = getRegistryTermByName(
								ontProp, listWithIndividualsOfProposedRegistry,
								listWithIndividualsOfRegistry);

						String datatypePropertyName = setUri
								+ "_isMappedToRegTerm";
						addNewSettingObjectProperty(datatypePropertyName);

						addInstanceObjectProperty(settingInstance,
								datatypePropertyName, "Registry",
								mappingOntologyName);

					}
					// save values

					String value = set.getValue();
					// addNewSettingsDatatypeProperty(setUri, false);
					DatatypeProperty prop33 = ontologyModel
							.createDatatypeProperty(NS
									+ setUri.replace(" ", ""));
					if (set.getType().equals("boolean")) {
						prop33.addRange(XSD.xboolean);
					} else if (set.getType().equals("double")) {
						prop33.addRange(XSD.xdouble);
					} else if (set.getType().equals("integer")) {
						prop33.addRange(XSD.xint);
					} else
						prop33.addRange(XSD.xstring);
					prop33.addDomain(domainSetting);
					uris.add(prop33.getURI().toLowerCase());

					// DatatypeProperty prop =
					// ontologyModel.getDatatypeProperty(NS
					// + setUri);
					if (value != null)
						settingInstance.addProperty(prop33, value);

					// save name

					DatatypeProperty prop = ontologyModel
							.createDatatypeProperty(NS + setUri
									+ "_hasName".replace(" ", ""));
					prop.addRange(stringSource);
					prop.addDomain(domainSetting);
					uris.add(prop.getURI().toLowerCase());
					if (set.getName() != null)
						settingInstance.addProperty(prop, set.getName());

					// save description

					if (!set.getDescription().equals("")) {
						prop = ontologyModel.createDatatypeProperty(NS + setUri
								+ "_hasDescription".replace(" ", ""));
						prop.addRange(stringSource);
						prop.addDomain(domainSetting);
						uris.add(prop.getURI().toLowerCase());

						if (set.getDescription() != null)
							settingInstance.addProperty(prop,
									set.getDescription());

					}

					// save value space
					if (!set.getValueSpace().equals("")) {
						prop = ontologyModel.createDatatypeProperty(NS + setUri
								+ "_hasValueSpace".replace(" ", ""));
						prop.addRange(stringSource);
						prop.addDomain(domainSetting);
						uris.add(prop.getURI().toLowerCase());

						if (set.getValueSpace() != null)
							settingInstance.addProperty(prop,
									set.getValueSpace());

					}

					// save type
					if (!set.getType().equals("")) {
						prop = ontologyModel.createDatatypeProperty(NS + setUri
								+ "_hasType".replace(" ", ""));
						prop.addRange(stringSource);
						prop.addDomain(domainSetting);
						uris.add(prop.getURI().toLowerCase());

						if (set.getType() != null)
							settingInstance.addProperty(prop, set.getType());

					}
					// save id
					if (!set.getId().equals("")) {
						prop = ontologyModel.createDatatypeProperty(NS + setUri
								+ "_hasID".replace(" ", ""));
						prop.addRange(stringSource);
						prop.addDomain(domainSetting);
						uris.add(prop.getURI().toLowerCase());

						if (set.getId() != null)
							settingInstance.addProperty(prop, set.getId());

					}

					// save constraints
					if (!set.getConstraints().equals("")) {
						if (set.getConstraints()
								.equals("e.g. MagnifierPosition=fullScreen requires AERO Design Windows 7 to be ON")) {
							set.setConstraints("");
						}

						prop = ontologyModel.createDatatypeProperty(NS + setUri
								+ "_hasConstraints".replace(" ", ""));
						prop.addRange(stringSource);
						prop.addDomain(domainSetting);
						uris.add(prop.getURI().toLowerCase());

						if (set.getConstraints() != null)
							settingInstance.addProperty(prop,
									set.getConstraints());

					}

					// save applied live
					prop = ontologyModel.createDatatypeProperty(NS + setUri
							+ "appliedLive".replace(" ", ""));
					prop.addRange(booleanSource);
					prop.addDomain(domainSetting);
					uris.add(prop.getURI().toLowerCase());

					if (set.isAppliedLive())
						settingInstance.addProperty(prop, literalTrue);
					else
						settingInstance.addProperty(prop, literalFalse);

					if (set.ishasMapping()) {
						// save is exact matching

						prop = ontologyModel.createDatatypeProperty(NS + setUri
								+ "_isExactMatching".replace(" ", ""));
						prop.addRange(booleanSource);
						prop.addDomain(domainSetting);
						uris.add(prop.getURI().toLowerCase());

						if (set.isExactMatching())
							settingInstance.addProperty(prop, literalTrue);
						else
							settingInstance.addProperty(prop, literalFalse);

						// save comments

						if (!set.isExactMatching()) {
							prop = ontologyModel
									.createDatatypeProperty(NS
											+ setUri
											+ "_hasCommentsForMapping".replace(
													" ", ""));
							prop.addRange(stringSource);
							prop.addDomain(domainSetting);
							uris.add(prop.getURI().toLowerCase());

							if (set.getComments() != null)
								settingInstance.addProperty(prop,
										set.getComments());

						} else {

							prop = ontologyModel
									.createDatatypeProperty(NS
											+ setUri
											+ "_hasCommentsForMapping".replace(
													" ", ""));
							prop.addRange(stringSource);
							prop.addDomain(domainSetting);
							uris.add(prop.getURI().toLowerCase());

							settingInstance.addProperty(prop, "");

						}

					} else {
						prop = ontologyModel.createDatatypeProperty(NS + setUri
								+ "_isMappedToRegTerm".replace(" ", ""));
						prop.addRange(stringSource);
						prop.addDomain(domainSetting);
						uris.add(prop.getURI().toLowerCase());

						settingInstance.addProperty(prop, "null");

					}

				}

			}

			// create settings instance if it does not exist
			if (parentInstances.size() == 0) {
				String p = str + "_Instance";
				p = changeUri(p);
				IndividualImpl ind = (IndividualImpl) solutionClass
						.createIndividual(NS + p);
				parentInstances.add(ind);
				uris.add(ind.getURI().toLowerCase());
				ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
						+ "hasSolutionSpecificSettings");

				solutionInd.addProperty(prop3, ind);
			}

			// before changes we used solution.getEtnaProperties
			// now we use shown and hidden lists with clusters, because these
			// lists contain information
			List<ETNACluster> etnaProperties = new ArrayList<ETNACluster>();
			for (ETNACluster temp : solution.getClustersToShowInView()) {
				etnaProperties.add(temp);
			}

			for (ETNACluster temp : solution.getClustersToHide()) {
				etnaProperties.add(temp);
			}

			for (ETNACluster cl2 : etnaProperties) {

				for (int k = 0; k < solution.getProposedAttributeClusterItems()
						.size(); k++) {
					if (cl2.getName().equalsIgnoreCase(
							solution.getProposedAttributeClusterItems().get(k)
									.getBelongsToCluster())) {
						addNewETNAPropertyInCluster(cl2, solution
								.getProposedAttributeClusterItems().get(k));
					}
				}
				// save proposed EASTIN Cluster Items
				for (int k = 0; k < solution.getProposedMeasureClusterItems()
						.size(); k++) {
					if (cl2.getName().equalsIgnoreCase(
							solution.getProposedMeasureClusterItems().get(k)
									.getBelongsToCluster())) {
						addNewETNAPropertyInCluster(cl2, solution
								.getProposedMeasureClusterItems().get(k));
					}
				}

				for (int j = 0; j < parentInstances.size(); j++)
					addNewETNAProperty(parentInstances.get(j), cl2, false);

			}

			// save solution access info for vendors

			saveAccessInfoForVendors(solutionInd, solution);
			saveAccessInfoForUsers(solutionInd, solution);

			// save vendor
			if (solution.getVendor() != null
					&& !solution.getVendor().equals("")) {
				ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
						+ "hasSolutionVendor");
				Individual vendorInd = ontologyModel.getIndividual(solution
						.getVendor());
				if (vendorInd != null)
					solutionInd.addProperty(prop3, vendorInd);
			}
			// save SLAs

			// saveSLAs(solutionInd, solution);

			// save to owl
			// saveToOWL();
			// Runtime.getRuntime().gc();

			System.out.println("saved");

			return str;

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesMessage msg = new FacesMessage("Error",
					"An error occured while saving solution");

			FacesContext.getCurrentInstance().addMessage(null, msg);
			// JOptionPane.showMessageDialog(null,
			// "An error occured while saving solution", "", 1);
		}
		return "";
	}

	private void saveAccessInfoForVendors(IndividualImpl solutionInd,
			Solution solution) {
		// create instance for solution access info

		OntClass solAccess = ontologyModel.getOntClass(NS
				+ "SolutionAccessInfoForVendors");
		if (solAccess != null) {
			String uri = solution.getName().trim().replace(" ", "_")
					+ "_SolutionAccessInfoForVendors";
			// check if uri exists

			uri = changeUri(uri);
			IndividualImpl solutionAccessInfoForVendorsInd = (IndividualImpl) solAccess
					.createIndividual(NS + uri);
			uris.add(solutionAccessInfoForVendorsInd.getURI().toLowerCase());
			ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
					+ "hasSolutionAccessInfoForVendors");

			solutionInd.addProperty(prop3, solutionAccessInfoForVendorsInd);
			addInstanceDatatypePropertyValue(solutionAccessInfoForVendorsInd,
					"hasDescription", solution.getAccessInfoForVendors()
							.getDescription());
			addInstanceDatatypePropertyValue(solutionAccessInfoForVendorsInd,
					"hasURLforAccess", solution.getAccessInfoForVendors()
							.getURLForAccess());
			addInstanceDatatypePropertyDoubleValue(
					solutionAccessInfoForVendorsInd, "hasMaxDiscount", solution
							.getAccessInfoForVendors().getMaxDiscount());

			// add license ind
			OntClass solutionLicenseClass = ontologyModel.getOntClass(NS
					+ "SolutionLicense");
			if (solutionLicenseClass != null) {
				String uriForLicense = solution.getName().trim()
						.replace(" ", "_")
						+ "_SolutionLicense";
				uriForLicense = changeUri(uriForLicense);
				IndividualImpl solutionLicenseInd = (IndividualImpl) solutionLicenseClass
						.createIndividual(NS + uriForLicense);
				uris.add(solutionLicenseInd.getURI().toLowerCase());
				ObjectProperty prop4 = ontologyModel.getObjectProperty(NS
						+ "hasLicense");

				solutionAccessInfoForVendorsInd.addProperty(prop4,
						solutionLicenseInd);

				addInstanceDatatypePropertyValue(solutionLicenseInd,
						"hasLicenseDescription", solution
								.getAccessInfoForVendors().getLicense()
								.getLicenseDescription());
				addInstanceDatatypePropertyValue(solutionLicenseInd,
						"hasLicenseName", solution.getAccessInfoForVendors()
								.getLicense().getLicenseName());
				addInstanceDatatypePropertyValue(
						solutionLicenseInd,
						"isProprietary",
						String.valueOf(solution.getAccessInfoForVendors()
								.getLicense().isProprietary()));
			}
			// add commercial cost schema
			OntClass commercialCostSchemaClass = ontologyModel.getOntClass(NS
					+ "CommercialCostSchema");
			if (commercialCostSchemaClass != null) {
				String uriForCommercialCostSchema = solution.getName().trim()
						.replace(" ", "_")
						+ "_CommercialCostSchema";
				uriForCommercialCostSchema = changeUri(uriForCommercialCostSchema);
				IndividualImpl commercialCostSchemaInd = (IndividualImpl) commercialCostSchemaClass
						.createIndividual(NS + uriForCommercialCostSchema);
				uris.add(commercialCostSchemaInd.getURI().toLowerCase());
				ObjectProperty prop4 = ontologyModel.getObjectProperty(NS
						+ "hasCommercialCostSchema");

				solutionAccessInfoForVendorsInd.addProperty(prop4,
						commercialCostSchemaInd);
				addInstanceDatatypePropertyDoubleValue(commercialCostSchemaInd,
						"hasCommercialCost", solution.getAccessInfoForVendors()
								.getCommercialCostSchema().getCommercialCost());
				addInstanceDatatypePropertyValue(commercialCostSchemaInd,
						"hasCommercialCostCurrency", solution
								.getAccessInfoForVendors()
								.getCommercialCostSchema()
								.getCommercialCostCurrency());
				addInstanceDatatypePropertyValue(commercialCostSchemaInd,
						"hasCostPaymentChargeType", solution
								.getAccessInfoForVendors()
								.getCommercialCostSchema()
								.getCostPaymentChargeType());
				// add trial schema
				OntClass solutionTrialSchemaClass = ontologyModel
						.getOntClass(NS + "SolutionTrialSchema");
				if (solutionTrialSchemaClass != null) {
					String uriForSolutionTrialSchema = solution.getName()
							.trim().replace(" ", "_")
							+ "_SolutionTrialSchema";
					uriForSolutionTrialSchema = changeUri(uriForSolutionTrialSchema);
					IndividualImpl solutionTrialSchemaInd = (IndividualImpl) solutionTrialSchemaClass
							.createIndividual(NS + uriForSolutionTrialSchema);
					uris.add(solutionTrialSchemaInd.getURI().toLowerCase());
					ObjectProperty prop5 = ontologyModel.getObjectProperty(NS
							+ "hasTrialSchema");

					commercialCostSchemaInd.addProperty(prop5,
							solutionTrialSchemaInd);
					addInstanceDatatypePropertyIntegerValue(
							solutionTrialSchemaInd, "hasDurationInDays",
							solution.getAccessInfoForVendors()
									.getCommercialCostSchema().getTrialSchema()
									.getDurationInDays());
					addInstanceDatatypePropertyValue(solutionTrialSchemaInd,
							"hasLimitedFunctionalityDescription", solution
									.getAccessInfoForVendors()
									.getCommercialCostSchema().getTrialSchema()
									.getLimitedFunctionalityDescription());
					addInstanceDatatypePropertyIntegerValue(
							solutionTrialSchemaInd, "hasDurationInUsages",
							solution.getAccessInfoForVendors()
									.getCommercialCostSchema().getTrialSchema()
									.getDurationInUsages());
					addInstanceDatatypePropertyValue(solutionTrialSchemaInd,
							"offersFullFunctionalityDuringTrial", solution
									.getAccessInfoForVendors()
									.getCommercialCostSchema().getTrialSchema()
									.getLimitedFunctionalityDescription());

				}
				// add discounts

				OntClass discountIfUsedWithOtherSolutionClass = ontologyModel
						.getOntClass(NS + "DiscountSchema");
				for (int i = 0; i < solution.getAccessInfoForVendors()
						.getCommercialCostSchema()
						.getDiscountIfUsedWithOtherSolution().size(); i++) {
					DiscountSchema discountSchema = solution
							.getAccessInfoForVendors()
							.getCommercialCostSchema()
							.getDiscountIfUsedWithOtherSolution().get(i);
					String uriForDiscountSchema = solution.getName().trim()
							.replace(" ", "_")
							+ "_DiscountSchema" + i;
					uriForDiscountSchema = changeUri(uriForDiscountSchema);
					IndividualImpl discountSchemaInd = (IndividualImpl) discountIfUsedWithOtherSolutionClass
							.createIndividual(NS + uriForDiscountSchema);
					uris.add(discountSchemaInd.getURI().toLowerCase());
					ObjectProperty prop5 = ontologyModel.getObjectProperty(NS
							+ "hasDiscountIfUsedWithOtherSolution");

					commercialCostSchemaInd.addProperty(prop5,
							discountSchemaInd);
					addInstanceDatatypePropertyIntegerValue(discountSchemaInd,
							"hasDiscount", discountSchema.getDiscount());
					addInstanceDatatypePropertyValue(discountSchemaInd,
							"hasDiscountReason",
							discountSchema.getDiscountReason());
					if (discountSchema.getPairedSolution() != null
							&& discountSchema.getPairedSolution()
									.getOntologyURI() != null
							&& !discountSchema.getPairedSolution()
									.getOntologyURI().equals("")) {
						IndividualImpl pairedSolutionInd = (IndividualImpl) ontologyModel
								.getIndividual(discountSchema
										.getPairedSolution().getOntologyURI());
						ObjectProperty prop6 = ontologyModel
								.getObjectProperty(NS + "hasPairedSolution");

						discountSchemaInd.addProperty(prop6, pairedSolutionInd);
					}

				}

			}

		}

	}

	private void saveAccessInfoForUsers(IndividualImpl solutionInd,
			Solution solution) {
		// create instance for solution access info

		OntClass solAccess = ontologyModel.getOntClass(NS
				+ "SolutionAccessInfoForUsers");
		if (solAccess != null) {
			String uri = solution.getName().trim().replace(" ", "_")
					+ "_SolutionAccessInfoForUsers";
			// check if uri exists

			uri = changeUri(uri);
			IndividualImpl solutionAccessInfoForUsersInd = (IndividualImpl) solAccess
					.createIndividual(NS + uri);
			uris.add(solutionAccessInfoForUsersInd.getURI().toLowerCase());
			ObjectProperty prop3 = ontologyModel.getObjectProperty(NS
					+ "hasSolutionAccessInfoForUsers");

			solutionInd.addProperty(prop3, solutionAccessInfoForUsersInd);
			addInstanceDatatypePropertyValue(solutionAccessInfoForUsersInd,
					"hasDescription", solution.getAccessInfoForUsers()
							.getDescription());
			addInstanceDatatypePropertyValue(solutionAccessInfoForUsersInd,
					"hasURLforAccess", solution.getAccessInfoForUsers()
							.getURLForAccess());
			addInstanceDatatypePropertyDoubleValue(
					solutionAccessInfoForUsersInd, "hasMaxDiscount", solution
							.getAccessInfoForUsers().getMaxDiscount());
			for (int i = 0; i < solution.getAccessInfoForUsers()
					.getValidForCountries().size(); i++) {
				addInstanceDatatypePropertyValue(solutionAccessInfoForUsersInd,
						"isValidForCountries", solution.getAccessInfoForUsers()
								.getValidForCountries().get(i));
			}
			// add license ind
			OntClass solutionLicenseClass = ontologyModel.getOntClass(NS
					+ "SolutionLicense");
			if (solutionLicenseClass != null) {
				String uriForLicense = solution.getName().trim()
						.replace(" ", "_")
						+ "_SolutionLicense";
				uriForLicense = changeUri(uriForLicense);
				IndividualImpl solutionLicenseInd = (IndividualImpl) solutionLicenseClass
						.createIndividual(NS + uriForLicense);
				uris.add(solutionLicenseInd.getURI().toLowerCase());
				ObjectProperty prop4 = ontologyModel.getObjectProperty(NS
						+ "hasLicense");

				solutionAccessInfoForUsersInd.addProperty(prop4,
						solutionLicenseInd);

				addInstanceDatatypePropertyValue(solutionLicenseInd,
						"hasLicenseDescription", solution
								.getAccessInfoForUsers().getLicense()
								.getLicenseDescription());
				addInstanceDatatypePropertyValue(solutionLicenseInd,
						"hasLicenseName", solution.getAccessInfoForUsers()
								.getLicense().getLicenseName());
				addInstanceDatatypePropertyValue(
						solutionLicenseInd,
						"isProprietary",
						String.valueOf(solution.getAccessInfoForUsers()
								.getLicense().isProprietary()));
			}
			// add commercial cost schema
			OntClass commercialCostSchemaClass = ontologyModel.getOntClass(NS
					+ "CommercialCostSchema");
			if (commercialCostSchemaClass != null) {
				String uriForCommercialCostSchema = solution.getName().trim()
						.replace(" ", "_")
						+ "_CommercialCostSchema";
				uriForCommercialCostSchema = changeUri(uriForCommercialCostSchema);
				IndividualImpl commercialCostSchemaInd = (IndividualImpl) commercialCostSchemaClass
						.createIndividual(NS + uriForCommercialCostSchema);
				uris.add(commercialCostSchemaInd.getURI().toLowerCase());
				ObjectProperty prop4 = ontologyModel.getObjectProperty(NS
						+ "hasCommercialCostSchema");

				solutionAccessInfoForUsersInd.addProperty(prop4,
						commercialCostSchemaInd);
				addInstanceDatatypePropertyDoubleValue(commercialCostSchemaInd,
						"hasCommercialCost", solution.getAccessInfoForUsers()
								.getCommercialCostSchema().getCommercialCost());
				addInstanceDatatypePropertyValue(commercialCostSchemaInd,
						"hasCommercialCostCurrency", solution
								.getAccessInfoForUsers()
								.getCommercialCostSchema()
								.getCommercialCostCurrency());
				addInstanceDatatypePropertyValue(commercialCostSchemaInd,
						"hasCostPaymentChargeType", solution
								.getAccessInfoForUsers()
								.getCommercialCostSchema()
								.getCostPaymentChargeType());
				// add trial schema
				OntClass solutionTrialSchemaClass = ontologyModel
						.getOntClass(NS + "SolutionTrialSchema");
				if (solutionTrialSchemaClass != null) {
					String uriForSolutionTrialSchema = solution.getName()
							.trim().replace(" ", "_")
							+ "_SolutionTrialSchema";
					uriForSolutionTrialSchema = changeUri(uriForSolutionTrialSchema);
					IndividualImpl solutionTrialSchemaInd = (IndividualImpl) solutionTrialSchemaClass
							.createIndividual(NS + uriForSolutionTrialSchema);
					uris.add(solutionTrialSchemaInd.getURI().toLowerCase());
					ObjectProperty prop5 = ontologyModel.getObjectProperty(NS
							+ "hasTrialSchema");

					commercialCostSchemaInd.addProperty(prop5,
							solutionTrialSchemaInd);
					addInstanceDatatypePropertyIntegerValue(
							solutionTrialSchemaInd, "hasDurationInDays",
							solution.getAccessInfoForUsers()
									.getCommercialCostSchema().getTrialSchema()
									.getDurationInDays());
					addInstanceDatatypePropertyValue(solutionTrialSchemaInd,
							"hasLimitedFunctionalityDescription", solution
									.getAccessInfoForUsers()
									.getCommercialCostSchema().getTrialSchema()
									.getLimitedFunctionalityDescription());
					addInstanceDatatypePropertyIntegerValue(
							solutionTrialSchemaInd, "hasDurationInUsages",
							solution.getAccessInfoForUsers()
									.getCommercialCostSchema().getTrialSchema()
									.getDurationInUsages());
					addInstanceDatatypePropertyValue(solutionTrialSchemaInd,
							"offersFullFunctionalityDuringTrial", solution
									.getAccessInfoForUsers()
									.getCommercialCostSchema().getTrialSchema()
									.getLimitedFunctionalityDescription());

				}
				// add discounts

				OntClass discountIfUsedWithOtherSolutionClass = ontologyModel
						.getOntClass(NS + "DiscountSchema");
				for (int i = 0; i < solution.getAccessInfoForUsers()
						.getCommercialCostSchema()
						.getDiscountIfUsedWithOtherSolution().size(); i++) {
					DiscountSchema discountSchema = solution
							.getAccessInfoForUsers().getCommercialCostSchema()
							.getDiscountIfUsedWithOtherSolution().get(i);
					String uriForDiscountSchema = solution.getName().trim()
							.replace(" ", "_")
							+ "_DiscountSchema" + i;
					uriForDiscountSchema = changeUri(uriForDiscountSchema);
					IndividualImpl discountSchemaInd = (IndividualImpl) discountIfUsedWithOtherSolutionClass
							.createIndividual(NS + uriForDiscountSchema);
					uris.add(discountSchemaInd.getURI().toLowerCase());
					ObjectProperty prop5 = ontologyModel.getObjectProperty(NS
							+ "hasDiscountIfUsedWithOtherSolution");

					commercialCostSchemaInd.addProperty(prop5,
							discountSchemaInd);
					addInstanceDatatypePropertyIntegerValue(discountSchemaInd,
							"hasDiscount", discountSchema.getDiscount());
					addInstanceDatatypePropertyValue(discountSchemaInd,
							"hasDiscountReason",
							discountSchema.getDiscountReason());
					if (discountSchema.getPairedSolution() != null
							&& discountSchema.getPairedSolution()
									.getOntologyURI() != null
							&& !discountSchema.getPairedSolution()
									.getOntologyURI().equals("")) {
						IndividualImpl pairedSolutionInd = (IndividualImpl) ontologyModel
								.getIndividual(discountSchema
										.getPairedSolution().getOntologyURI());
						ObjectProperty prop6 = ontologyModel
								.getObjectProperty(NS + "hasPairedSolution");

						discountSchemaInd.addProperty(prop6, pairedSolutionInd);
					}

				}

			}

		}

	}

	public List<String> getAllowedValuesOfDatatypeProperty(String propertyName) {
		List<String> res = new ArrayList<String>();
		try {
			DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
					+ propertyName);
			DataRange genderRange = dProperty.getRange().as(DataRange.class);
			for (ExtendedIterator<Literal> i = genderRange.listOneOf(); i
					.hasNext();) {
				Literal l = i.next();
				res.add(l.getLexicalForm());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public SolutionAccessInfoForVendors loadAccessInfoForVendors(String uri) {
		SolutionAccessInfoForVendors accessInfoForVendors = new SolutionAccessInfoForVendors();

		// get Solution individual
		try {
			OntClass cl = ontologyModel.getOntClass(NS + "Solutions");
			ObjectProperty hasSolutionAccessInfoForVendors = ontologyModel
					.getObjectProperty(NS + "hasSolutionAccessInfoForVendors");
			DatatypeProperty hasDescription = ontologyModel
					.getDatatypeProperty(NS + "hasDescription");
			DatatypeProperty hasMaxDiscount = ontologyModel
					.getDatatypeProperty(NS + "hasMaxDiscount");
			DatatypeProperty hasURLforAccess = ontologyModel
					.getDatatypeProperty(NS + "hasURLforAccess");
			ObjectProperty hasLicense = ontologyModel.getObjectProperty(NS
					+ "hasLicense");
			ObjectProperty hasCommercialCostSchema = ontologyModel
					.getObjectProperty(NS + "hasCommercialCostSchema");
			DatatypeProperty hasCommercialCost = ontologyModel
					.getDatatypeProperty(NS + "hasCommercialCost");
			DatatypeProperty hasCommercialCostCurrency = ontologyModel
					.getDatatypeProperty(NS + "hasCommercialCostCurrency");
			DatatypeProperty hasCostPaymentChargeType = ontologyModel
					.getDatatypeProperty(NS + "hasCostPaymentChargeType");
			ObjectProperty hasTrialSchema = ontologyModel.getObjectProperty(NS
					+ "hasTrialSchema");
			DatatypeProperty hasDurationInDays = ontologyModel
					.getDatatypeProperty(NS + "hasDurationInDays");
			DatatypeProperty hasLimitedFunctionalityDescription = ontologyModel
					.getDatatypeProperty(NS
							+ "hasLimitedFunctionalityDescription");
			DatatypeProperty hasDurationInUsages = ontologyModel
					.getDatatypeProperty(NS + "hasDurationInUsages");
			DatatypeProperty offersFullFunctionalityDuringTrial = ontologyModel
					.getDatatypeProperty(NS
							+ "offersFullFunctionalityDuringTrial");
			ObjectProperty hasDiscountIfUsedWithOtherSolution = ontologyModel
					.getObjectProperty(NS
							+ "hasDiscountIfUsedWithOtherSolution");

			Iterator it = cl.listInstances();
			while (it.hasNext()) {
				IndividualImpl solutionInd = (IndividualImpl) it.next();

				if (uri.equalsIgnoreCase(solutionInd.getURI())) {
					// find settings individual

					if (solutionInd
							.getPropertyResourceValue(hasSolutionAccessInfoForVendors) != null) {
						Resource r1 = solutionInd
								.getPropertyResourceValue(hasSolutionAccessInfoForVendors);
						// System.out.println(r.getURI());
						IndividualImpl ind = (IndividualImpl) ontologyModel
								.getIndividual(r1.getURI());

						if (ind.getPropertyValue(hasDescription) != null) {
							accessInfoForVendors.setDescription(ind
									.getPropertyValue(hasDescription)
									.asLiteral().getString());
						}

						if (ind.getPropertyValue(hasMaxDiscount) != null) {
							accessInfoForVendors.setMaxDiscount(ind
									.getPropertyValue(hasMaxDiscount)
									.asLiteral().getFloat());
						}

						if (ind.getPropertyValue(hasURLforAccess) != null) {
							accessInfoForVendors.setURLForAccess(ind
									.getPropertyValue(hasURLforAccess)
									.asLiteral().getString());
						}
						// load license

						if (ind.getPropertyResourceValue(hasLicense) != null) {
							Resource r2 = ind
									.getPropertyResourceValue(hasLicense);
							accessInfoForVendors
									.setLicense(loadSolutionLicense(r2.getURI()));
						}

						// load commercial cost schema
						if (ind.getPropertyResourceValue(hasCommercialCostSchema) != null) {
							Resource r2 = ind
									.getPropertyResourceValue(hasCommercialCostSchema);
							IndividualImpl commercialCostSchemaInd = (IndividualImpl) ontologyModel
									.getIndividual(r2.getURI());
							CommercialCostSchema commercialCostSchema = new CommercialCostSchema();
							if (commercialCostSchemaInd
									.getPropertyValue(hasCommercialCost) != null) {
								commercialCostSchema
										.setCommercialCost(commercialCostSchemaInd
												.getPropertyValue(
														hasCommercialCost)
												.asLiteral().getFloat());
							}

							if (commercialCostSchemaInd
									.getPropertyValue(hasCommercialCostCurrency) != null) {
								commercialCostSchema
										.setCommercialCostCurrency(commercialCostSchemaInd
												.getPropertyValue(
														hasCommercialCostCurrency)
												.asLiteral().getString());
							}

							if (commercialCostSchemaInd
									.getPropertyValue(hasCostPaymentChargeType) != null) {
								commercialCostSchema
										.setCostPaymentChargeType(commercialCostSchemaInd
												.getPropertyValue(
														hasCostPaymentChargeType)
												.asLiteral().getString());
							}
							// load trial schema
							if (commercialCostSchemaInd
									.getPropertyResourceValue(hasTrialSchema) != null) {
								Resource r3 = commercialCostSchemaInd
										.getPropertyResourceValue(hasTrialSchema);
								IndividualImpl trialSchemaInd = (IndividualImpl) ontologyModel
										.getIndividual(r3.getURI());
								SolutionTrialSchema trialSchema = new SolutionTrialSchema();

								if (trialSchemaInd
										.getPropertyValue(hasDurationInDays) != null) {
									trialSchema
											.setDurationInDays(trialSchemaInd
													.getPropertyValue(
															hasDurationInDays)
													.asLiteral().getInt());
								}

								if (trialSchemaInd
										.getPropertyValue(hasLimitedFunctionalityDescription) != null) {
									trialSchema
											.setLimitedFunctionalityDescription(trialSchemaInd
													.getPropertyValue(
															hasLimitedFunctionalityDescription)
													.asLiteral().getString());
								}

								if (trialSchemaInd
										.getPropertyValue(hasDurationInUsages) != null) {
									trialSchema
											.setDurationInUsages(trialSchemaInd
													.getPropertyValue(
															hasDurationInUsages)
													.asLiteral().getInt());
								}

								if (trialSchemaInd
										.getPropertyValue(offersFullFunctionalityDuringTrial) != null) {
									try {
										// TODO does not return the right
										// property value
										trialSchema
												.setOffersFullFunctionalityDuringTrial(trialSchemaInd
														.getPropertyValue(
																offersFullFunctionalityDuringTrial)
														.asLiteral()
														.getBoolean());
									} catch (Exception ex) {
										// ex.printStackTrace();
									}
								}
								commercialCostSchema
										.setTrialSchema(trialSchema);
							}
							// load discount solutions
							if (commercialCostSchemaInd
									.getPropertyResourceValue(hasDiscountIfUsedWithOtherSolution) != null) {

								NodeIterator it2 = commercialCostSchemaInd
										.listPropertyValues(hasDiscountIfUsedWithOtherSolution);

								while (it2.hasNext()) {
									Resource r = it2.next().asResource();
									commercialCostSchema
											.getDiscountIfUsedWithOtherSolution()
											.add(loadDiscountSchema(r.getURI()));

								}
							}

							accessInfoForVendors
									.setCommercialCostSchema(commercialCostSchema);
						}

					}
					return accessInfoForVendors;
				}
			}
			return accessInfoForVendors;
		} catch (Exception ex) {
			ex.printStackTrace();
			return accessInfoForVendors;
		}

	}

	public DiscountSchema loadDiscountSchema(String uri) {
		DiscountSchema schema = new DiscountSchema();
		IndividualImpl discountSchemasInd = (IndividualImpl) ontologyModel
				.getIndividual(uri);
		DiscountSchema discountSchema = new DiscountSchema();
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasDiscount");
		if (discountSchemasInd.getPropertyValue(dProperty) != null) {
			discountSchema.setDiscount(discountSchemasInd
					.getPropertyValue(dProperty).asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasDiscountReason");
		if (discountSchemasInd.getPropertyValue(dProperty) != null) {
			discountSchema.setDiscountReason(discountSchemasInd
					.getPropertyValue(dProperty).asLiteral().getString());
		}
		// paired solution
		ObjectProperty oProperty = ontologyModel.getObjectProperty(NS
				+ "hasPairedSolution");
		if (discountSchemasInd.getPropertyResourceValue(oProperty) != null) {
			Resource r1 = discountSchemasInd
					.getPropertyResourceValue(oProperty);
			discountSchema.setPairedSolutionString(r1.getURI());
		}

		return schema;

	}

	public SolutionAccessInfoForUsers loadAccessInfoForUsers(String uri) {
		SolutionAccessInfoForUsers accessInfoForVendors = new SolutionAccessInfoForUsers();

		// get Solution individual
		try {
			OntClass cl = ontologyModel.getOntClass(NS + "Solutions");
			ObjectProperty hasSolutionAccessInfoForVendors = ontologyModel
					.getObjectProperty(NS + "hasSolutionAccessInfoForVendors");
			DatatypeProperty hasDescription = ontologyModel
					.getDatatypeProperty(NS + "hasDescription");
			DatatypeProperty hasMaxDiscount = ontologyModel
					.getDatatypeProperty(NS + "hasMaxDiscount");
			DatatypeProperty hasURLforAccess = ontologyModel
					.getDatatypeProperty(NS + "hasURLforAccess");
			ObjectProperty hasLicense = ontologyModel.getObjectProperty(NS
					+ "hasLicense");
			DatatypeProperty hasLicenseDescription = ontologyModel
					.getDatatypeProperty(NS + "hasLicenseDescription");
			DatatypeProperty hasLicenseName = ontologyModel
					.getDatatypeProperty(NS + "hasLicenseName");
			DatatypeProperty isProprietary = ontologyModel
					.getDatatypeProperty(NS + "isProprietary");
			ObjectProperty hasCommercialCostSchema = ontologyModel
					.getObjectProperty(NS + "hasCommercialCostSchema");
			DatatypeProperty hasCommercialCost = ontologyModel
					.getDatatypeProperty(NS + "hasCommercialCost");
			DatatypeProperty hasCommercialCostCurrency = ontologyModel
					.getDatatypeProperty(NS + "hasCommercialCostCurrency");
			DatatypeProperty hasCostPaymentChargeType = ontologyModel
					.getDatatypeProperty(NS + "hasCostPaymentChargeType");
			ObjectProperty hasTrialSchema = ontologyModel.getObjectProperty(NS
					+ "hasTrialSchema");
			DatatypeProperty hasDurationInDays = ontologyModel
					.getDatatypeProperty(NS + "hasDurationInDays");
			DatatypeProperty hasLimitedFunctionalityDescription = ontologyModel
					.getDatatypeProperty(NS
							+ "hasLimitedFunctionalityDescription");
			DatatypeProperty hasDurationInUsages = ontologyModel
					.getDatatypeProperty(NS + "hasDurationInUsages");
			DatatypeProperty offersFullFunctionalityDuringTrial = ontologyModel
					.getDatatypeProperty(NS
							+ "offersFullFunctionalityDuringTrial");
			ObjectProperty hasDiscountIfUsedWithOtherSolution = ontologyModel
					.getObjectProperty(NS
							+ "hasDiscountIfUsedWithOtherSolution");
			DatatypeProperty hasDiscount = ontologyModel.getDatatypeProperty(NS
					+ "hasDiscount");
			DatatypeProperty hasDiscountReason = ontologyModel
					.getDatatypeProperty(NS + "hasDiscountReason");

			Iterator it = cl.listInstances();
			while (it.hasNext()) {
				IndividualImpl solutionInd = (IndividualImpl) it.next();

				if (uri.equalsIgnoreCase(solutionInd.getURI())) {
					// find settings individual

					if (solutionInd
							.getPropertyResourceValue(hasSolutionAccessInfoForVendors) != null) {
						Resource r1 = solutionInd
								.getPropertyResourceValue(hasSolutionAccessInfoForVendors);
						// System.out.println(r.getURI());
						IndividualImpl ind = (IndividualImpl) ontologyModel
								.getIndividual(r1.getURI());

						if (ind.getPropertyValue(hasDescription) != null) {
							accessInfoForVendors.setDescription(ind
									.getPropertyValue(hasDescription)
									.asLiteral().getString());
						}

						if (ind.getPropertyValue(hasMaxDiscount) != null) {
							accessInfoForVendors.setMaxDiscount(ind
									.getPropertyValue(hasMaxDiscount)
									.asLiteral().getFloat());
						}

						if (ind.getPropertyValue(hasURLforAccess) != null) {
							accessInfoForVendors.setURLForAccess(ind
									.getPropertyValue(hasURLforAccess)
									.asLiteral().getString());
						}
						// load license

						if (ind.getPropertyResourceValue(hasLicense) != null) {
							Resource r2 = ind
									.getPropertyResourceValue(hasLicense);
							IndividualImpl licenseInd = (IndividualImpl) ontologyModel
									.getIndividual(r2.getURI());
							SolutionLicense license = new SolutionLicense();

							if (licenseInd
									.getPropertyValue(hasLicenseDescription) != null) {
								license.setLicenseDescription(licenseInd
										.getPropertyValue(hasLicenseDescription)
										.asLiteral().getString());
							}

							if (licenseInd.getPropertyValue(hasLicenseName) != null) {
								license.setLicenseName(licenseInd
										.getPropertyValue(hasLicenseName)
										.asLiteral().getString());
							}

							if (licenseInd.getPropertyValue(isProprietary) != null) {
								license.setProprietary(licenseInd
										.getPropertyValue(isProprietary)
										.asLiteral().getBoolean());
							}
							accessInfoForVendors.setLicense(license);

						}

						// load commercial cost schema
						if (ind.getPropertyResourceValue(hasCommercialCostSchema) != null) {
							Resource r2 = ind
									.getPropertyResourceValue(hasCommercialCostSchema);
							IndividualImpl commercialCostSchemaInd = (IndividualImpl) ontologyModel
									.getIndividual(r2.getURI());
							CommercialCostSchema commercialCostSchema = new CommercialCostSchema();

							if (commercialCostSchemaInd
									.getPropertyValue(hasCommercialCost) != null) {
								commercialCostSchema
										.setCommercialCost(commercialCostSchemaInd
												.getPropertyValue(
														hasCommercialCost)
												.asLiteral().getFloat());
							}

							if (commercialCostSchemaInd
									.getPropertyValue(hasCommercialCostCurrency) != null) {
								commercialCostSchema
										.setCommercialCostCurrency(commercialCostSchemaInd
												.getPropertyValue(
														hasCommercialCostCurrency)
												.asLiteral().getString());
							}

							if (commercialCostSchemaInd
									.getPropertyValue(hasCostPaymentChargeType) != null) {
								commercialCostSchema
										.setCostPaymentChargeType(commercialCostSchemaInd
												.getPropertyValue(
														hasCostPaymentChargeType)
												.asLiteral().getString());
							}
							// load trial schema

							if (commercialCostSchemaInd
									.getPropertyResourceValue(hasTrialSchema) != null) {
								Resource r3 = commercialCostSchemaInd
										.getPropertyResourceValue(hasTrialSchema);
								IndividualImpl trialSchemaInd = (IndividualImpl) ontologyModel
										.getIndividual(r3.getURI());
								SolutionTrialSchema trialSchema = new SolutionTrialSchema();

								if (trialSchemaInd
										.getPropertyValue(hasDurationInDays) != null) {
									trialSchema
											.setDurationInDays(trialSchemaInd
													.getPropertyValue(
															hasDurationInDays)
													.asLiteral().getInt());
								}

								if (trialSchemaInd
										.getPropertyValue(hasLimitedFunctionalityDescription) != null) {
									trialSchema
											.setLimitedFunctionalityDescription(trialSchemaInd
													.getPropertyValue(
															hasLimitedFunctionalityDescription)
													.asLiteral().getString());
								}

								if (trialSchemaInd
										.getPropertyValue(hasDurationInUsages) != null) {
									trialSchema
											.setDurationInUsages(trialSchemaInd
													.getPropertyValue(
															hasDurationInUsages)
													.asLiteral().getInt());
								}

								if (trialSchemaInd
										.getPropertyValue(offersFullFunctionalityDuringTrial) != null) {
									try {
										// TODO does not return the right
										// property value
										trialSchema
												.setOffersFullFunctionalityDuringTrial(trialSchemaInd
														.getPropertyValue(
																offersFullFunctionalityDuringTrial)
														.asLiteral()
														.getBoolean());
									} catch (Exception ex) {
										// ex.printStackTrace();
									}
								}
								commercialCostSchema
										.setTrialSchema(trialSchema);
							}
							// load discount solutions

							if (commercialCostSchemaInd
									.getPropertyResourceValue(hasDiscountIfUsedWithOtherSolution) != null) {

								NodeIterator it2 = commercialCostSchemaInd
										.listPropertyValues(hasDiscountIfUsedWithOtherSolution);

								while (it2.hasNext()) {
									Resource r = it2.next().asResource();
									// System.out.println(r.getURI());
									IndividualImpl discountSchemasInd = (IndividualImpl) ontologyModel
											.getIndividual(r.getURI());
									DiscountSchema discountSchema = new DiscountSchema();

									if (discountSchemasInd
											.getPropertyValue(hasDiscount) != null) {
										discountSchema
												.setDiscount(discountSchemasInd
														.getPropertyValue(
																hasDiscount)
														.asLiteral().getInt());
									}

									if (discountSchemasInd
											.getPropertyValue(hasDiscountReason) != null) {
										discountSchema
												.setDiscountReason(discountSchemasInd
														.getPropertyValue(
																hasDiscountReason)
														.asLiteral()
														.getString());
									}
									// TODO paired solution is missing
									commercialCostSchema
											.getDiscountIfUsedWithOtherSolution()
											.add(discountSchema);
								}
							}

							accessInfoForVendors
									.setCommercialCostSchema(commercialCostSchema);
						}

					}
					return accessInfoForVendors;
				}
			}
			return accessInfoForVendors;
		} catch (Exception ex) {
			ex.printStackTrace();
			return accessInfoForVendors;
		}

	}

	public List<Vendor> loadAllVendors() {
		List<Vendor> vendors = new ArrayList<Vendor>();
		OntClass cl = ontologyModel.getOntClass(NS + "SolutionVendors");
		DatatypeProperty hasContactDetails = ontologyModel
				.getDatatypeProperty(NS + "hasContactDetails");
		DatatypeProperty hasSolutionVendorName = ontologyModel
				.getDatatypeProperty(NS + "hasSolutionVendorName");
		DatatypeProperty hasCountry = ontologyModel.getDatatypeProperty(NS
				+ "hasCountry");
		DatatypeProperty hasVendorDescription = ontologyModel
				.getDatatypeProperty(NS + "hasVendorDescription");
		ObjectProperty hasVendorReputation = ontologyModel.getObjectProperty(NS
				+ "hasVendorReputation");
		ObjectProperty hasEndUserLicenseAgreements = ontologyModel
				.getObjectProperty(NS + "hasEndUserLicenseAgreements");

		// Iterator it = cl.listInstances();
		// while (it.hasNext()) {
		// IndividualImpl vendorInd = (IndividualImpl) it.next();
		ArrayList<IndividualImpl> it = (ArrayList<IndividualImpl>) cl
				.listInstances().toList();
		for (int i = 0; i < it.size(); i++) {

			IndividualImpl vendorInd = it.get(i);
			Vendor vendor = new Vendor();
			uris.add(vendorInd.getURI().toLowerCase());

			if (vendorInd.getPropertyValue(hasContactDetails) != null) {
				vendor.setContactDetails(vendorInd
						.getPropertyValue(hasContactDetails).asLiteral()
						.getString());
			}

			if (vendorInd.getPropertyValue(hasSolutionVendorName) != null) {
				vendor.setVendorName(vendorInd
						.getPropertyValue(hasSolutionVendorName).asLiteral()
						.getString());
			}

			if (vendorInd.getPropertyValue(hasCountry) != null) {
				vendor.setCountry(vendorInd.getPropertyValue(hasCountry)
						.asLiteral().getString());
			}

			if (vendorInd.getPropertyValue(hasVendorDescription) != null) {
				vendor.setDescription(vendorInd
						.getPropertyValue(hasVendorDescription).asLiteral()
						.getString());
			}

			if (vendorInd.getPropertyResourceValue(hasVendorReputation) != null) {
				Resource r1 = vendorInd
						.getPropertyResourceValue(hasVendorReputation);
				// System.out.println(r.getURI());

				vendor.setReputation(loadVendorReputation(r1.getURI()));
			}
			// load eulas
			if (vendorInd.listProperties(hasEndUserLicenseAgreements).hasNext()) {
				StmtIterator it2 = vendorInd
						.listProperties(hasEndUserLicenseAgreements);
				while (it2.hasNext()) {
					StatementImpl st = (StatementImpl) it2.next();
					Resource r = st.getResource();
					vendor.getEulas().add(loadEULA(r.getURI()));

				}
			}

			vendor.setOntologyURI(vendorInd.getURI());
			vendors.add(vendor);

		}
		return vendors;
	}

	public Vendor loadVendorByURI(String uri) {
		Vendor vendor = new Vendor();

		IndividualImpl vendorInd = (IndividualImpl) ontologyModel
				.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasContactDetails");
		if (vendorInd.getPropertyValue(dProperty) != null) {

			vendor.setContactDetails(vendorInd.getPropertyValue(dProperty)
					.asLiteral().getString());

			// name
			dProperty = ontologyModel.getDatatypeProperty(NS
					+ "hasSolutionVendorName");
			if (vendorInd.getPropertyValue(dProperty) != null) {
				vendor.setVendorName(vendorInd.getPropertyValue(dProperty)
						.asLiteral().getString());

			}
			// country
			dProperty = ontologyModel.getDatatypeProperty(NS + "hasCountry");
			if (vendorInd.getPropertyValue(dProperty) != null) {
				vendor.setCountry(vendorInd.getPropertyValue(dProperty)
						.asLiteral().getString());
			}
			dProperty = ontologyModel.getDatatypeProperty(NS
					+ "hasVendorDescription");
			if (vendorInd.getPropertyValue(dProperty) != null) {
				vendor.setDescription(vendorInd.getPropertyValue(dProperty)
						.asLiteral().getString());
			}
			ObjectProperty oProperty = ontologyModel.getObjectProperty(NS
					+ "hasVendorReputation");
			if (vendorInd.getPropertyResourceValue(oProperty) != null) {
				Resource r1 = vendorInd.getPropertyResourceValue(oProperty);

				vendor.setReputation(loadVendorReputation(r1.getURI()));
			}
			// load eulas
			oProperty = ontologyModel.getObjectProperty(NS
					+ "hasEndUserLicenseAgreements");
			if (vendorInd.listProperties(oProperty).hasNext()) {
				StmtIterator it2 = vendorInd.listProperties(oProperty);
				while (it2.hasNext()) {
					StatementImpl st = (StatementImpl) it2.next();
					Resource r = st.getResource();
					vendor.getEulas().add(loadEULA(r.getURI()));

				}
			}

			vendor.setOntologyURI(vendorInd.getURI());
			return vendor;

		}
		return vendor;
	}

	public Vendor loadVendor(String email) {
		Vendor vendor = new Vendor();
		OntClass cl = ontologyModel.getOntClass(NS + "SolutionVendors");
		DatatypeProperty hasContactDetails = ontologyModel
				.getDatatypeProperty(NS + "hasContactDetails");
		DatatypeProperty hasSolutionVendorName = ontologyModel
				.getDatatypeProperty(NS + "hasSolutionVendorName");
		DatatypeProperty hasCountry = ontologyModel.getDatatypeProperty(NS
				+ "hasCountry");
		DatatypeProperty hasVendorDescription = ontologyModel
				.getDatatypeProperty(NS + "hasVendorDescription");
		ObjectProperty hasVendorReputation = ontologyModel.getObjectProperty(NS
				+ "hasVendorReputation");
		ObjectProperty hasEndUserLicenseAgreements = ontologyModel
				.getObjectProperty(NS + "hasEndUserLicenseAgreements");

		Iterator it = cl.listInstances();
		while (it.hasNext()) {
			IndividualImpl vendorInd = (IndividualImpl) it.next();

			if (vendorInd.getPropertyValue(hasContactDetails) != null) {
				if (vendorInd.getPropertyValue(hasContactDetails).asLiteral()
						.getString().equalsIgnoreCase(email)) {
					vendor.setContactDetails(vendorInd
							.getPropertyValue(hasContactDetails).asLiteral()
							.getString());

					if (vendorInd.getPropertyValue(hasSolutionVendorName) != null) {
						vendor.setVendorName(vendorInd
								.getPropertyValue(hasSolutionVendorName)
								.asLiteral().getString());
					}

					if (vendorInd.getPropertyValue(hasCountry) != null) {
						vendor.setCountry(vendorInd
								.getPropertyValue(hasCountry).asLiteral()
								.getString());
					}

					if (vendorInd.getPropertyValue(hasVendorDescription) != null) {
						vendor.setDescription(vendorInd
								.getPropertyValue(hasVendorDescription)
								.asLiteral().getString());
					}

					if (vendorInd.getPropertyResourceValue(hasVendorReputation) != null) {
						Resource r1 = vendorInd
								.getPropertyResourceValue(hasVendorReputation);
						// System.out.println(r.getURI());

						vendor.setReputation(loadVendorReputation(r1.getURI()));
					}
					// load eulas

					if (vendorInd.listProperties(hasEndUserLicenseAgreements)
							.hasNext()) {
						StmtIterator it2 = vendorInd
								.listProperties(hasEndUserLicenseAgreements);
						while (it2.hasNext()) {
							StatementImpl st = (StatementImpl) it2.next();
							Resource r = st.getResource();
							vendor.getEulas().add(loadEULA(r.getURI()));

						}
					}

					vendor.setOntologyURI(vendorInd.getURI());
					return vendor;
				}
			}
		}
		return null;
	}

	public EULA loadEULA(String uri) {
		EULA eula = new EULA();
		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(uri);
		eula.setOntologyURI(ind.getURI());
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasEULA_costCurrency");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setEULA_costCurrency(ind.getPropertyValue(dProperty)
					.asLiteral().getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasEULA_CostPaymentChargeType");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setEULA_costPaymentChargeType(ind.getPropertyValue(dProperty)
					.asLiteral().getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasEULA_StartDate");
		if (ind.getPropertyValue(dProperty) != null) {
			String string = ind.getPropertyValue(dProperty).asLiteral()
					.getString();
			try {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				Date date = (Date) sdf1.parse(string);
				// Date date = (Date) new
				// SimpleDateFormat("MMMM d, yyyy",
				// Locale.ENGLISH).parse(string);
				eula.setEULA_StartDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasEULA_EndDate");
		if (ind.getPropertyValue(dProperty) != null) {
			String string = ind.getPropertyValue(dProperty).asLiteral()
					.getString();
			try {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				Date date = (Date) sdf1.parse(string);
				// Date date = (Date) new
				// SimpleDateFormat("MMMM d, yyyy",
				// Locale.ENGLISH).parse(string);
				eula.setEULA_EndDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasStatus");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setStatus(ind.getPropertyValue(dProperty).asLiteral()
					.getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasDurationInUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setDurationInUsages(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasDurationInUsagesSpent");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setDurationInUsagesSpent(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasUserWithTotalTimeOfLoyaltyToVendorInDays");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setUserWithTotalTimeOfLoyaltyToVendorInDays(ind
					.getPropertyValue(dProperty).asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasEULA_cost");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setEULA_cost(ind.getPropertyValue(dProperty).asLiteral()
					.getFloat());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasEULA_costAfterDiscount");
		if (ind.getPropertyValue(dProperty) != null) {
			eula.setEULA_costAfterDiscount(ind.getPropertyValue(dProperty)
					.asLiteral().getFloat());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "isValidForCountries");
		if (ind.listPropertyValues(dProperty).hasNext()) {
			NodeIterator it2 = ind.listPropertyValues(dProperty);
			while (it2.hasNext()) {
				try {
					Literal r = it2.next().asLiteral();
					eula.getValidForCountries().add(r.getString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		ObjectProperty oProperty = ontologyModel.getObjectProperty(NS
				+ "hasSolutionLicense");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			Resource r1 = ind.getPropertyResourceValue(oProperty);
			eula.setSolutionLicense(loadSolutionLicense(r1.getURI()));

		}
		// TODO load vendor
		oProperty = ontologyModel.getObjectProperty(NS + "hasDiscountSchema");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			NodeIterator it2 = ind.listPropertyValues(oProperty);
			while (it2.hasNext()) {
				Resource r = it2.next().asResource();
				eula.getDiscountSchema().add(loadDiscountSchema(r.getURI()));

			}
		}
		oProperty = ontologyModel.getObjectProperty(NS
				+ "hasAccompanyingSolutions");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			NodeIterator it2 = ind.listPropertyValues(oProperty);
			while (it2.hasNext()) {
				Resource r = it2.next().asResource();
				eula.getAccompanyingSolutions().add(r.getURI());

			}
		}
		return eula;
	}

	public void addVendor(User user) {

		String str = user.getEmailAddress().split("@")[0];
		// check if uri exists

		str = changeUri(str);

		addIndividual("SolutionVendors", str);

		IndividualImpl vendorInd = getInstanceOfClass("SolutionVendors", str);
		// add property values

		addInstanceDatatypePropertyValue(vendorInd, "hasSolutionVendorName",
				user.getFullName());
		addInstanceDatatypePropertyValue(vendorInd, "hasContactDetails",
				user.getEmailAddress());
		// saveToOWL();
	}

	public SolutionUsageStatisticsSchema loadSolutionUsageStatisticsSchema(
			String uri) {
		SolutionUsageStatisticsSchema schema = new SolutionUsageStatisticsSchema();
		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasTotalNrOfSuccessfulUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			schema.setTotalNrOfSuccessfulUsages(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasTotalNrOfUnsuccessfulUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			schema.setTotalNrOfUnsuccessfulUsages(ind
					.getPropertyValue(dProperty).asLiteral().getInt());
		}
		dProperty = ontologyModel
				.getDatatypeProperty(NS + "hasTotalNrOfUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			schema.setTotalNrOfUsages(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasTotalNrOfUsers");
		if (ind.getPropertyValue(dProperty) != null) {
			schema.setTotalNrOfUsers(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasUsagePercentageInCategory");
		if (ind.getPropertyValue(dProperty) != null) {
			schema.setUsagePercentageInCategory(ind.getPropertyValue(dProperty)
					.asLiteral().getFloat());
		}
		ObjectProperty oProperty = ontologyModel.getObjectProperty(NS
				+ "hasOverallUserFeedback");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			Resource r1 = ind.getPropertyResourceValue(oProperty);
			schema.setSolutionUserFeedback(loadSolutionUserFeedback(r1.getURI()));

		}
		oProperty = ontologyModel.getObjectProperty(NS + "hasUsagePerCountry");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			NodeIterator it2 = ind.listPropertyValues(oProperty);
			while (it2.hasNext()) {
				Resource r = it2.next().asResource();
				schema.getUsagePerCountry().add(
						loadPerCountrySolutionUsage(r.getURI()));

			}
		}
		oProperty = ontologyModel.getObjectProperty(NS + "hasUsagePerVendor");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			NodeIterator it2 = ind.listPropertyValues(oProperty);
			while (it2.hasNext()) {
				Resource r = it2.next().asResource();
				schema.getUsagePerVendor().add(
						loadPerVendorCountrySolutionUsage(r.getURI()));

			}
		}
		oProperty = ontologyModel.getObjectProperty(NS
				+ "hasUserFeedbackPerCountry");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			NodeIterator it2 = ind.listPropertyValues(oProperty);
			while (it2.hasNext()) {
				Resource r = it2.next().asResource();
				schema.getUserFeedbackPerCountry().add(
						loadSolutionUserFeedback(r.getURI()));

			}
		}
		oProperty = ontologyModel.getObjectProperty(NS
				+ "hasUserFeedbackPerVendor");
		if (ind.getPropertyResourceValue(oProperty) != null) {
			NodeIterator it2 = ind.listPropertyValues(oProperty);
			while (it2.hasNext()) {
				Resource r = it2.next().asResource();
				schema.getUserFeedbackPerVendor().add(
						loadSolutionUserFeedback(r.getURI()));

			}
		}
		return schema;
	}

	public PerVendorSolutionUsage loadPerVendorCountrySolutionUsage(String uri) {
		PerVendorSolutionUsage usage = new PerVendorSolutionUsage();
		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasNrOfSuccessfulUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfSuccessfulUsages(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasNrOfUnsuccessfulUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfUnsuccessfulUsages(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasNrOfUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfUsages(ind.getPropertyValue(dProperty).asLiteral()
					.getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasNrOfUsers");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfUsers(ind.getPropertyValue(dProperty).asLiteral()
					.getInt());
		}
		// TODO vendor loading is missing
		return usage;
	}

	public PerCountrySolutionUsage loadPerCountrySolutionUsage(String uri) {
		PerCountrySolutionUsage usage = new PerCountrySolutionUsage();
		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "forCountry");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setForCountry(ind.getPropertyValue(dProperty).asLiteral()
					.getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasNrOfSuccessfulUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfSuccessfullUsages(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasNrOfUnsuccessfulUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfUnsuccessfulUsages(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasNrOfUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfUsages(ind.getPropertyValue(dProperty).asLiteral()
					.getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasNrOfUsers");
		if (ind.getPropertyValue(dProperty) != null) {
			usage.setNrOfUsers(ind.getPropertyValue(dProperty).asLiteral()
					.getInt());
		}
		return usage;
	}

	public SolutionUserFeedback loadSolutionUserFeedback(String uri) {
		SolutionUserFeedback feedback = new SolutionUserFeedback();
		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "forCountry");
		if (ind.getPropertyValue(dProperty) != null) {
			feedback.setForCountry(ind.getPropertyValue(dProperty).asLiteral()
					.getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasAverageRating");
		if (ind.getPropertyValue(dProperty) != null) {
			feedback.setAverageRating(ind.getPropertyValue(dProperty)
					.asLiteral().getFloat());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasNrOfNegativeRatings");
		if (ind.getPropertyValue(dProperty) != null) {
			feedback.setNrOfNegativeRatings(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasNrOfPositiveRatings");
		if (ind.getPropertyValue(dProperty) != null) {
			feedback.setNrOfPositiveRatings(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasTotalNumberOfRatings");
		if (ind.getPropertyValue(dProperty) != null) {
			feedback.setTotalNumberOfRatings(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		// TODO Vendor loading is missing
		return feedback;
	}

	public List<Solution> loadAllSolutions(List<ETNACluster> etnaCluster) {
		final List<Solution> solutions = new ArrayList<Solution>();

		OntClass cl = ontologyModel.getOntClass(NS + "Solutions");
		DatatypeProperty hasSolutionName = ontologyModel.getDatatypeProperty(NS
				+ "hasSolutionName");
		DatatypeProperty hasStartCommand = ontologyModel.getDatatypeProperty(NS
				+ "hasStartCommand");
		DatatypeProperty hasStopCommand = ontologyModel.getDatatypeProperty(NS
				+ "hasStopCommand");
		DatatypeProperty hasSolutionDescription = ontologyModel
				.getDatatypeProperty(NS + "hasSolutionDescription");
		DatatypeProperty id = ontologyModel.getDatatypeProperty(NS + "id");
		DatatypeProperty hasConstraints = ontologyModel.getDatatypeProperty(NS
				+ "hasConstraints");
		ObjectProperty hasSolutionVendor = ontologyModel.getObjectProperty(NS
				+ "hasSolutionVendor");
		DatatypeProperty hasSolutionVendorName = ontologyModel
				.getDatatypeProperty(NS + "hasSolutionVendorName");
		ObjectProperty hasTechnicalDetails = ontologyModel.getObjectProperty(NS
				+ "hasTechnicalDetails");
		DatatypeProperty BasicInfo_ManufacturerName = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_ManufacturerName");
		DatatypeProperty BasicInfo_InsertDate = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_InsertDate");
		DatatypeProperty BasicInfo_LatestUpdate = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_LatestUpdate");
		DatatypeProperty BasicInfo_ManufacturerCountry = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_ManufacturerCountry");
		DatatypeProperty BasicInfo_ManufacturerWebsite = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_ManufacturerWebsite");
		DatatypeProperty BasicInfo_Image = ontologyModel.getDatatypeProperty(NS
				+ "BasicInfo_Image");
		DatatypeProperty BasicInfo_DOwnloadOrPurchaseWebpage = ontologyModel
				.getDatatypeProperty(NS + "BasicInfo_DownloadOrPurchaseWebpage");
		ObjectProperty hasSolutionLevelAgreements = ontologyModel
				.getObjectProperty(NS + "hasSolutionLevelAgreements");
		ObjectProperty hasUsers = ontologyModel.getObjectProperty(NS
				+ "hasUsers");
		ObjectProperty hasUsageStatistics = ontologyModel.getObjectProperty(NS
				+ "hasUsageStatistics");
		ObjectProperty hasSolutionReputation = ontologyModel
				.getObjectProperty(NS + "hasSolutionReputation");
		ObjectProperty hasPerCountryReputation = ontologyModel
				.getObjectProperty(NS + "hasPerCountryReputation");
		DatatypeProperty hasOverallReputationScore = ontologyModel
				.getDatatypeProperty(NS + "hasOverallReputationScore");
		DatatypeProperty hasCategories = ontologyModel.getDatatypeProperty(NS
				+ "hasCategories");

		// datatypes and object properties for handlers and their options
		ObjectProperty hasHandlerType = ontologyModel.getObjectProperty(NS
				+ "hasSettingsHandler");
		ObjectProperty settingsHandlerHasName = ontologyModel
				.getObjectProperty(NS + "settingsHandlerHasName");
		ObjectProperty settingHandlerHasValues = ontologyModel
				.getObjectProperty(NS + "settingsHandlerHasValues");

		DatatypeProperty hasCapabilities = ontologyModel.getDatatypeProperty(NS
				+ "hasCapabilities");
		DatatypeProperty hasCapabilitiesTransformations = ontologyModel
				.getDatatypeProperty(NS + "hasCapabilitiesTransformations");

		// create lists with terms' names and items terms (name, description)
		// for mapping
		List<List> termsLists = getInstancesOfRegistryClass("Registry");
		List<String> registryNamesList = termsLists.get(1);
		List<Item> registryItemsList = termsLists.get(0);

		termsLists = getInstancesOfRegistryClass("ProposedRegistryTerms");
		registryNamesList.addAll(termsLists.get(1));
		registryItemsList.addAll(termsLists.get(0));

		ArrayList<IndividualImpl> it = (ArrayList<IndividualImpl>) cl
				.listInstances().toList();
		for (IndividualImpl in : it) {

			List<ETNACluster> solutionETNAClusters = new ArrayList<ETNACluster>();
			for (ETNACluster cluster : etnaCluster) {
				solutionETNAClusters.add(cluster.clone());
			}
			// IndividualImpl in = it.get(kk);

			// System.out.println("next");
			Solution sol = new Solution();
			sol.setOntologyURI(NS + in.getLocalName());

			if (in.getPropertyValue(hasSolutionName) != null) {
				sol.setName(in.getPropertyValue(hasSolutionName).asLiteral()
						.getString());
			}
			if (sol.getName() == null || sol.getName().equals("")) {
				sol.setName(in.getLocalName());
			}

			if (in.getPropertyValue(hasSolutionDescription) != null) {
				sol.setDescription(in.getPropertyValue(hasSolutionDescription)
						.asLiteral().getString());

				if (sol.getDescription() == null
						|| sol.getDescription().equals("null"))
					sol.setDescription("");
			}

			if (in.getPropertyValue(hasStartCommand) != null) {
				sol.setStartCommand(in.getPropertyValue(hasStartCommand)
						.asLiteral().getString());
			}

			if (in.getPropertyValue(hasStopCommand) != null) {
				sol.setStopCommand(in.getPropertyValue(hasStopCommand)
						.asLiteral().getString());
			}

			if (in.getPropertyValue(hasCapabilities) != null) {
				sol.setCapabilities(in.getPropertyValue(hasCapabilities)
						.asLiteral().getString());
			}

			if (in.getPropertyValue(hasCapabilitiesTransformations) != null) {
				sol.setCapabilitiesTransformations(in
						.getPropertyValue(hasCapabilitiesTransformations)
						.asLiteral().getString());
			}

			if (in.getPropertyValue(hasCategories) != null) {
				List<String> categoriesNames = new ArrayList<String>();
				String allCategoriesString = in.getPropertyValue(hasCategories)
						.asLiteral().getString();
				String[] splitted = allCategoriesString.split(",");
				for (int i = 0; i < splitted.length; i++) {
					categoriesNames.add(splitted[i]);
				}

				sol.setCategories(categoriesNames);

			}

			// get handler type and handler options for solution
			loadHandlersDataForSolution(sol, hasHandlerType,
					settingsHandlerHasName, settingHandlerHasValues, in);

			if (in.getPropertyValue(id) != null) {
				sol.setId(in.getPropertyValue(id).asLiteral().getString());
			}

			if (in.getPropertyValue(hasConstraints) != null) {
				sol.setConstraints(in.getPropertyValue(hasConstraints)
						.asLiteral().getString());
			} else {
				sol.setConstraints("");
			}

			// load vendor

			if (in.getPropertyValue(hasSolutionVendor) != null) {
				Resource r = in.getPropertyResourceValue(hasSolutionVendor);
				// load vendor
				sol.setVendor(r.getURI());
				// get name of vendor
				IndividualImpl vendorInd = (IndividualImpl) ontologyModel
						.getIndividual(r.getURI());
				if (vendorInd.getPropertyValue(hasSolutionVendorName) != null) {
					sol.setVendorName(vendorInd
							.getPropertyValue(hasSolutionVendorName)
							.asLiteral().getString());
				}
			}

			// load technical details
			try {

				if (in.getPropertyValue(hasTechnicalDetails) != null) {
					Resource r = in
							.getPropertyResourceValue(hasTechnicalDetails);
					// System.out.println(r.getURI());
					IndividualImpl technicalDetails = (IndividualImpl) ontologyModel
							.getIndividual(r.getURI());

					if (technicalDetails
							.getPropertyValue(BasicInfo_ManufacturerName) != null) {
						sol.setManufacturerName(technicalDetails
								.getPropertyValue(BasicInfo_ManufacturerName)
								.asLiteral().getString());
					}

					if (technicalDetails.getPropertyValue(BasicInfo_InsertDate) != null) {
						String string = technicalDetails
								.getPropertyValue(BasicInfo_InsertDate)
								.asLiteral().getString();
						try {
							SimpleDateFormat sdf1 = new SimpleDateFormat(
									"yyyy-MM-dd");
							Date date = (Date) sdf1.parse(string);
							// Date date = (Date) new
							// SimpleDateFormat("MMMM d, yyyy",
							// Locale.ENGLISH).parse(string);
							sol.setDate(date);
						} catch (ParseException e) {

							e.printStackTrace();
						}

					}

					if (technicalDetails
							.getPropertyValue(BasicInfo_LatestUpdate) != null) {
						String string = technicalDetails
								.getPropertyValue(BasicInfo_LatestUpdate)
								.asLiteral().getString();
						try {
							SimpleDateFormat sdf1 = new SimpleDateFormat(
									"yyyy-MM-dd");
							Date date = (Date) sdf1.parse(string);
							// Date date = (Date) new
							// SimpleDateFormat("MMMM d yyyy",
							// Locale.ENGLISH).parse(string);
							sol.setUpdateDate(date);
						} catch (ParseException e) {

							e.printStackTrace();
						}

					}

					if (technicalDetails
							.getPropertyValue(BasicInfo_ManufacturerCountry) != null) {
						sol.setManufacturerCountry(technicalDetails
								.getPropertyValue(BasicInfo_ManufacturerCountry)
								.asLiteral().getString());
					}

					if (technicalDetails
							.getPropertyValue(BasicInfo_ManufacturerWebsite) != null) {
						sol.setManufacturerWebsite(technicalDetails
								.getPropertyValue(BasicInfo_ManufacturerWebsite)
								.asLiteral().getString());
					}

					if (technicalDetails.getPropertyValue(BasicInfo_Image) != null) {
						sol.setImageUrl(technicalDetails
								.getPropertyValue(BasicInfo_Image).asLiteral()
								.getString());
					}

					if (technicalDetails
							.getPropertyValue(BasicInfo_DOwnloadOrPurchaseWebpage) != null) {
						sol.setDownloadPage(technicalDetails
								.getPropertyValue(
										BasicInfo_DOwnloadOrPurchaseWebpage)
								.asLiteral().getString());
					}
				}
			} catch (Exception ex) {

			}
			sol.setOntologyCategory(in.getOntClass().toString().split("#")[1]);
			// load settings
			List<Setting> list = getSettingsOfInstance(sol.getOntologyURI());
			// List<Setting> list = new ArrayList<Setting>();

			for (int i = list.size() - 1; i >= 0; i--) {

				if (list.get(i).getMapping().equals("")) {
					// list.remove(i);
					// list.get(i).setMapping("");
					list.get(i).setHasMapping(false);
					list.get(i).setSortedMappings(registryNamesList);
					list.get(i).setSortedMappingsObjects(registryItemsList);
					list.get(i).setExactMatching(false);
				} else {
					if (!list.get(i).ishasMapping())
						list.get(i).setMapping("");
					list.get(i).setSortedMappings(registryNamesList);
					list.get(i).setSortedMappingsObjects(registryItemsList);

				}
			}
			for (int i = list.size() - 1; i >= 0; i--) {
				if (list.get(i).getName().equals("hasSettingsName")
						|| list.get(i).getName().equals("hasSettingValue")
						|| list.get(i).getName()
								.equals("hasSettingDescription")
						|| list.get(i).getName().equals("hasName"))
					list.remove(i);
			}
			for (int i = list.size() - 1; i >= 0; i--) {

				if (list.get(i).getMapping().equals("null")) {
					list.get(i).setMapping("");
					list.get(i).setExactMatching(false);
				}
			}

			sol.setSettings(list);
			// load ETNA properties

			List<ETNACluster> clustersToShow = new ArrayList<ETNACluster>();
			List<ETNACluster> clustersToHide = new ArrayList<ETNACluster>();
			List<ETNACluster> clustersToShowInView = new ArrayList<ETNACluster>();
			for (int i = 0; i < solutionETNAClusters.size(); i++) {
				ETNACluster cl2 = solutionETNAClusters.get(i);
				ArrayList<String> selectedValues = loadETNAProperty(
						sol.getOntologyURI(), cl2);

				cl2.setSelectedProperties(selectedValues);

				if (selectedValues.size() > 0) {
					clustersToShow.add(cl2.clone());
					clustersToShowInView.add(cl2.clone());
				} else if (cl2.getMeasureProperties().size() > 0) {
					for (EASTINProperty prop : cl2.getMeasureProperties()) {
						if (!prop.getValue().isEmpty()
								&& !prop.getValue().equals("0")
								&& !prop.getValue().equals("0.0")) {
							clustersToShow.add(cl2.clone());
							clustersToShowInView.add(cl2.clone());
							break;
						}
					}
				} else {
					clustersToHide.add(cl2.clone());
				}

			}
			sol.setClustersToShow(clustersToShow);
			sol.setClustersToShowInView(clustersToShowInView);
			sol.setClustersToHide(clustersToHide);

			sol.setEtnaProperties(solutionETNAClusters);
			// load accessinfo for vendors

			sol.setAccessInfoForVendors(loadAccessInfoForVendors(sol
					.getOntologyURI()));
			sol.setAccessInfoForUsers(loadAccessInfoForUsers(sol
					.getOntologyURI()));

			// load slas

			if (in.listPropertyValues(hasSolutionLevelAgreements).hasNext()) {

				NodeIterator it2 = in
						.listPropertyValues(hasSolutionLevelAgreements);

				while (it2.hasNext()) {

					try {
						Resource r = it2.next().asResource();
						// System.out.println(r.getURI());
						IndividualImpl slaInd = (IndividualImpl) ontologyModel
								.getIndividual(r.getURI());
						sol.getSLAs().add(loadSLA(slaInd));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			// load users
			if (in.listPropertyValues(hasUsers).hasNext()) {

				NodeIterator it2 = in.listPropertyValues(hasUsers);

				while (it2.hasNext()) {
					try {
						Resource r = it2.next().asResource();
						sol.getUsers().add(loadUser(r.getURI()));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			// load usage statistics
			if (in.getPropertyValue(hasUsageStatistics) != null) {
				Resource r = in.getPropertyResourceValue(hasUsageStatistics);
				sol.setUsageStatistics(loadSolutionUsageStatisticsSchema(r
						.getURI()));

			}
			// load reputation
			if (in.getPropertyValue(hasSolutionReputation) != null) {
				Resource r = in.getPropertyResourceValue(hasSolutionReputation);
				// System.out.println(r.getURI());
				IndividualImpl reputationInd = (IndividualImpl) ontologyModel
						.getIndividual(r.getURI());
				if (reputationInd.listPropertyValues(hasPerCountryReputation)
						.hasNext()) {
					NodeIterator it2 = in
							.listPropertyValues(hasPerCountryReputation);
					while (it2.hasNext()) {
						Resource r2 = it2.next().asResource();
						sol.getReputation().getPerCountryReputation()
								.add(loadPerCountryReputation(r2.getURI()));
					}
				}

				if (reputationInd.getPropertyValue(hasOverallReputationScore) != null) {
					sol.getReputation()
							.setOverallReputationScore(
									reputationInd
											.getPropertyValue(
													hasOverallReputationScore)
											.asLiteral().getFloat());
				}
			}

			solutions.add(sol);
		}

		return solutions;
	}

	public SolutionUserSchema loadUser(String uri) {
		SolutionUserSchema user = new SolutionUserSchema();
		IndividualImpl userInd = (IndividualImpl) ontologyModel
				.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasUserID");
		if (userInd.getPropertyValue(dProperty) != null) {
			user.setUserId(userInd.getPropertyValue(dProperty).asLiteral()
					.getString());
		}
		ObjectProperty oProperty = ontologyModel.getObjectProperty(NS
				+ "hasEndUserLicenseAgreement");
		if (userInd.getPropertyValue(oProperty) != null) {
			user.setEula(loadEULA(userInd.getPropertyValue(oProperty)
					.asResource().getURI()));
		}
		oProperty = ontologyModel.getObjectProperty(NS + "refersToSolution");
		if (userInd.getPropertyValue(oProperty) != null) {
			user.setRefersToSolution(userInd.getPropertyValue(oProperty)
					.asResource().getURI());
		}
		return user;
	}

	public PerCountryReputation loadPerCountryReputation(String uri) {
		PerCountryReputation rep = new PerCountryReputation();
		IndividualImpl reputationInd = (IndividualImpl) ontologyModel
				.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "forCountry");
		if (reputationInd.getPropertyValue(dProperty) != null) {
			rep.setForCountry(reputationInd.getPropertyValue(dProperty)
					.asLiteral().getString());
		}
		dProperty = ontologyModel
				.getDatatypeProperty(NS + "hasReputationScore");
		if (reputationInd.getPropertyValue(dProperty) != null) {
			rep.setReputationScore(reputationInd.getPropertyValue(dProperty)
					.asLiteral().getFloat());
		}
		return rep;
	}

	public SLA loadSLA(IndividualImpl ind) {
		SLA sla = new SLA();
		sla.setOntologyURI(ind.getURI());
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasDurationInUsages");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setDurationInUsages(ind.getPropertyValue(dProperty).asLiteral()
					.getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasDurationInUsagesSpent");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setDurationInUsagesSpent(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel
				.getDatatypeProperty(NS + "hasDurationInUsers");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setDurationInUsers(ind.getPropertyValue(dProperty).asLiteral()
					.getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasDurationInUsersSpent");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setDurationInUsersSpent(ind.getPropertyValue(dProperty)
					.asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasSLA_Cost");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setSLA_Cost(ind.getPropertyValue(dProperty).asLiteral()
					.getFloat());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasSLA_CostAfterDiscount");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setSLA_CostAfterDiscount(ind.getPropertyValue(dProperty)
					.asLiteral().getFloat());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasSLA_CostCurrency");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setSLA_CostCurrency(ind.getPropertyValue(dProperty).asLiteral()
					.getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasSLA_CostPaymentChargeType");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setSLA_CostPaymentChargeType(ind.getPropertyValue(dProperty)
					.asLiteral().getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasSLA_StartDate");
		if (ind.getPropertyValue(dProperty) != null) {
			String string = ind.getPropertyValue(dProperty).asLiteral()
					.getString();
			try {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				Date date = (Date) sdf1.parse(string);
				// Date date = (Date) new
				// SimpleDateFormat("MMMM d, yyyy",
				// Locale.ENGLISH).parse(string);
				sla.setSLA_StartDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasSLA_EndDate");
		if (ind.getPropertyValue(dProperty) != null) {
			String string = ind.getPropertyValue(dProperty).asLiteral()
					.getString();
			try {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				Date date = (Date) sdf1.parse(string);
				// Date date = (Date) new
				// SimpleDateFormat("MMMM d, yyyy",
				// Locale.ENGLISH).parse(string);
				sla.setSLA_EndDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasStatus");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setHasStatus(ind.getPropertyValue(dProperty).asLiteral()
					.getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasVendorWithTotalTimeOfLoyaltyToOwnerInDays");
		if (ind.getPropertyValue(dProperty) != null) {
			sla.setVendorWithTotalTimeOfLoyaltyToOwn(ind
					.getPropertyValue(dProperty).asLiteral().getInt());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS
				+ "isValidForCountries");
		if (ind.listPropertyValues(dProperty).hasNext()) {
			NodeIterator it2 = ind.listPropertyValues(dProperty);
			while (it2.hasNext()) {
				try {
					Literal r = it2.next().asLiteral();
					sla.getValidForCountries().add(r.getString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		ObjectProperty oProperty = ontologyModel.getObjectProperty(NS
				+ "hasOwnerOfAgreement");
		if (ind.getPropertyValue(oProperty) != null) {
			sla.setOwnerOfAgreement(ind.getPropertyValue(oProperty)
					.asResource().getURI());
		}
		oProperty = ontologyModel.getObjectProperty(NS + "hasSolutionLicense");
		if (ind.getPropertyValue(oProperty) != null) {
			Resource r = ind.getPropertyValue(oProperty).asResource();
			sla.setSolutionLicense(loadSolutionLicense(r.getURI()));
		}
		oProperty = ontologyModel
				.getObjectProperty(NS + "hasVendorOfAgreement");
		if (ind.getPropertyValue(oProperty) != null) {
			Resource r = ind.getPropertyValue(oProperty).asResource();
			sla.setVendorOfAgreement(r.getURI());
		}
		// TODO load accompanying solutions
		ObjectProperty oProperty4 = ontologyModel.getObjectProperty(NS
				+ "hasDiscountSchema");
		if (ind.listPropertyValues(oProperty4).hasNext()) {

			NodeIterator it2 = ind.listPropertyValues(oProperty4);

			while (it2.hasNext()) {
				try {
					Resource r = it2.next().asResource();

					sla.getDiscountSchema().add(loadDiscountSchema(r.getURI()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return sla;
	}

	public SolutionLicense loadSolutionLicense(String uri) {
		SolutionLicense license = new SolutionLicense();
		IndividualImpl licenseInd = (IndividualImpl) ontologyModel
				.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasLicenseDescription");
		if (licenseInd.getPropertyValue(dProperty) != null) {
			license.setLicenseDescription(licenseInd
					.getPropertyValue(dProperty).asLiteral().getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "hasLicenseName");
		if (licenseInd.getPropertyValue(dProperty) != null) {
			license.setLicenseName(licenseInd.getPropertyValue(dProperty)
					.asLiteral().getString());
		}
		dProperty = ontologyModel.getDatatypeProperty(NS + "isProprietary");
		if (licenseInd.getPropertyValue(dProperty) != null) {
			license.setProprietary(licenseInd.getPropertyValue(dProperty)
					.asLiteral().getBoolean());
		}
		return license;
	}

	public void saveVendor(Vendor ven) {

		IndividualImpl vendorInd = (IndividualImpl) ontologyModel
				.getIndividual(ven.getOntologyURI());

		addInstanceDatatypePropertyValue(vendorInd, "hasCountry",
				ven.getCountry());
		addInstanceDatatypePropertyValue(vendorInd, "hasVendorDescription",
				ven.getDescription());
		// saveToOWL();

	}

	public ReputationSchema loadVendorReputation(String uri) {
		ReputationSchema reputation = new ReputationSchema();
		IndividualImpl reputationInd = (IndividualImpl) ontologyModel
				.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasOverallReputationScore");
		if (reputationInd.getPropertyValue(dProperty) != null) {
			reputation.setOverallReputationScore(reputationInd
					.getPropertyValue(dProperty).asLiteral().getFloat());
		}
		ObjectProperty oProperty4 = ontologyModel.getObjectProperty(NS
				+ "hasPerCountryReputation");
		if (reputationInd.listPropertyValues(oProperty4).hasNext()) {

			NodeIterator it2 = reputationInd.listPropertyValues(oProperty4);

			while (it2.hasNext()) {
				try {
					Resource r = it2.next().asResource();
					reputation.getPerCountryReputation().add(
							loadPerCountryReputation(r.getURI()));

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return reputation;

	}

	public void saveSLA(SLA sla, Solution solution) {

		OntClass solAccess = ontologyModel.getOntClass(NS
				+ "SolutionLevelAgreement");
		if (solAccess != null) {
			String indicativeURI = solution.getName().trim().replace(" ", "_")
					+ "SLA";
			String uri = indicativeURI;
			// check if uri exists

			uri = changeUri(uri);
			IndividualImpl slaInd = (IndividualImpl) solAccess
					.createIndividual(NS + uri);
			uris.add(slaInd.getURI().toLowerCase());
			sla.setOntologyURI(slaInd.getURI());
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			if (sla.getSLA_StartDate() != null)
				addInstanceDatatypePropertyValue(slaInd, "hasSLA_StartDate",
						sdf2.format(sla.getSLA_StartDate()));
			if (sla.getSLA_EndDate() != null)
				addInstanceDatatypePropertyValue(slaInd, "hasSLA_StartDate",
						sdf2.format(sla.getSLA_EndDate()));
			for (int j = 0; j < sla.getValidForCountries().size(); j++) {
				addInstanceDatatypePropertyValue(slaInd, "isValidForCountries",
						sla.getValidForCountries().get(j));
			}
			addInstanceDatatypePropertyIntegerValue(slaInd,
					"hasDurationInUsers", sla.getDurationInUsers());
			addInstanceDatatypePropertyIntegerValue(slaInd,
					"hasDurationInUsages", sla.getDurationInUsages());
			addInstanceDatatypePropertyDoubleValue(slaInd, "hasSLA_Cost",
					sla.getSLA_Cost());
			addInstanceDatatypePropertyValue(slaInd, "hasSLA_CostCurrency",
					sla.getSLA_CostCurrency());
			addInstanceDatatypePropertyValue(slaInd,
					"hasSLA_CostPaymentChargeType",
					sla.getSLA_CostPaymentChargeType());
			addInstanceDatatypePropertyValue(slaInd, "hasStatus",
					sla.getHasStatus());
			// addInstanceDatatypePropertyValue(slaInd, "hasStatus",
			// sla.getSolutionLicense(), false);
			OntClass solAccess2 = ontologyModel.getOntClass(NS
					+ "SolutionLicense");
			String uri2 = indicativeURI + "_SLASolutionLicense";
			uri2 = changeUri(uri2);
			IndividualImpl slaLicenseInd = (IndividualImpl) solAccess2
					.createIndividual(NS + uri2);
			uris.add(slaLicenseInd.getURI().toLowerCase());
			addInstanceDatatypePropertyValue(slaLicenseInd,
					"hasLicenseDescription", sla.getSolutionLicense()
							.getLicenseDescription());
			ObjectProperty prop = ontologyModel.getObjectProperty(NS
					+ "hasSolutionLicense");

			slaInd.addProperty(prop, slaLicenseInd);

			OntClass discountIfUsedWithOtherSolutionClass = ontologyModel
					.getOntClass(NS + "DiscountSchema");
			for (int i = 0; i < sla.getDiscountSchema().size(); i++) {
				DiscountSchema discountSchema = sla.getDiscountSchema().get(i);
				String uriForDiscountSchema = indicativeURI
						+ "_SLADiscountSchema" + i;
				uriForDiscountSchema = changeUri(uriForDiscountSchema);
				IndividualImpl discountSchemaInd = (IndividualImpl) discountIfUsedWithOtherSolutionClass
						.createIndividual(NS + uriForDiscountSchema);
				uris.add(discountSchemaInd.getURI().toLowerCase());
				ObjectProperty prop5 = ontologyModel.getObjectProperty(NS
						+ "hasDiscountIfUsedWithOtherSolution");

				slaInd.addProperty(prop5, discountSchemaInd);
				addInstanceDatatypePropertyIntegerValue(discountSchemaInd,
						"hasDiscount", discountSchema.getDiscount());
				addInstanceDatatypePropertyValue(discountSchemaInd,
						"hasDiscountReason", discountSchema.getDiscountReason());
				// find paired solution
				// TODO not working yet

			}
			ObjectProperty prop2 = ontologyModel.getObjectProperty(NS
					+ "hasSolutionLevelAgreements");

			ontologyModel.getIndividual(solution.getOntologyURI()).addProperty(
					prop2, slaInd);
			// add instance also to user profile
			IndividualImpl vendorInd = (IndividualImpl) ontologyModel
					.getIndividual(solution.getVendor());
			if (vendorInd != null) {
				prop2 = ontologyModel.getObjectProperty(NS
						+ "hasSolutionLevelAgreementsAsVendor");
				vendorInd.addProperty(prop2, slaInd);
			}
			// saveToOWL();
		}
	}

	public void saveEULA(EULA eula, Solution solution) {

		OntClass solAccess = ontologyModel.getOntClass(NS
				+ "EndUserLicenseAgreement");
		if (solAccess != null) {
			String indicativeURI = solution.getName().trim().replace(" ", "_")
					+ "EULA";
			String uri = indicativeURI;
			// check if uri exists

			uri = changeUri(uri);
			IndividualImpl eulaInd = (IndividualImpl) solAccess
					.createIndividual(NS + uri);
			uris.add(eulaInd.getURI().toLowerCase());
			eula.setOntologyURI(eulaInd.getURI());
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			if (eula.getEULA_StartDate() != null)
				addInstanceDatatypePropertyValue(eulaInd, "hasEULA_StartDate",
						sdf2.format(eula.getEULA_StartDate()));
			if (eula.getEULA_EndDate() != null)
				addInstanceDatatypePropertyValue(eulaInd, "hasEULA_EndDate",
						sdf2.format(eula.getEULA_EndDate()));
			for (int j = 0; j < eula.getValidForCountries().size(); j++) {
				addInstanceDatatypePropertyValue(eulaInd,
						"isValidForCountries",
						eula.getValidForCountries().get(j));
			}

			addInstanceDatatypePropertyIntegerValue(eulaInd,
					"hasDurationInUsages", eula.getDurationInUsages());
			addInstanceDatatypePropertyFloatValue(eulaInd, "hasEULA_cost",
					eula.getEULA_cost());
			addInstanceDatatypePropertyValue(eulaInd, "hasEULA_costCurrency",
					eula.getEULA_costCurrency());
			addInstanceDatatypePropertyValue(eulaInd,
					"hasEULA_CostPaymentChargeType",
					eula.getEULA_costPaymentChargeType());
			addInstanceDatatypePropertyValue(eulaInd, "hasStatus",
					eula.getStatus());
			// addInstanceDatatypePropertyValue(slaInd, "hasStatus",
			// sla.getSolutionLicense(), false);
			OntClass solAccess2 = ontologyModel.getOntClass(NS
					+ "SolutionLicense");
			String uri2 = indicativeURI + "_SLASolutionLicense";
			uri2 = changeUri(uri2);
			IndividualImpl slaLicenseInd = (IndividualImpl) solAccess2
					.createIndividual(NS + uri2);
			uris.add(slaLicenseInd.getURI().toLowerCase());
			addInstanceDatatypePropertyValue(slaLicenseInd,
					"hasLicenseDescription", eula.getSolutionLicense()
							.getLicenseDescription());
			ObjectProperty prop = ontologyModel.getObjectProperty(NS
					+ "hasSolutionLicense");

			eulaInd.addProperty(prop, slaLicenseInd);

			OntClass discountIfUsedWithOtherSolutionClass = ontologyModel
					.getOntClass(NS + "DiscountSchema");
			for (int i = 0; i < eula.getDiscountSchema().size(); i++) {
				DiscountSchema discountSchema = eula.getDiscountSchema().get(i);
				String uriForDiscountSchema = indicativeURI
						+ "_EULADiscountSchema" + i;
				uriForDiscountSchema = changeUri(uriForDiscountSchema);
				IndividualImpl discountSchemaInd = (IndividualImpl) discountIfUsedWithOtherSolutionClass
						.createIndividual(NS + uriForDiscountSchema);
				uris.add(discountSchemaInd.getURI().toLowerCase());
				ObjectProperty prop5 = ontologyModel.getObjectProperty(NS
						+ "hasDiscountIfUsedWithOtherSolution");

				eulaInd.addProperty(prop5, discountSchemaInd);
				addInstanceDatatypePropertyIntegerValue(discountSchemaInd,
						"hasDiscount", discountSchema.getDiscount());
				addInstanceDatatypePropertyValue(discountSchemaInd,
						"hasDiscountReason", discountSchema.getDiscountReason());
				// find paired solution
				// TODO not working yet

			}
			ObjectProperty prop2 = ontologyModel.getObjectProperty(NS
					+ "hasSolutionLevelAgreements");

			ontologyModel.getIndividual(solution.getOntologyURI()).addProperty(
					prop2, eulaInd);
			// add instance also to user profile
			IndividualImpl vendorInd = (IndividualImpl) ontologyModel
					.getIndividual(solution.getVendor());
			if (vendorInd != null) {
				prop2 = ontologyModel.getObjectProperty(NS
						+ "hasEndUserLicenseAgreements");
				vendorInd.addProperty(prop2, eulaInd);
			}
			// saveToOWL();
		}
	}

	public void createSolutionUserSchema(SolutionUserSchema userSchema) {
		OntClass solAccess = ontologyModel.getOntClass(NS
				+ "SolutionUserSchema");
		if (solAccess != null) {
			String indicativeURI = "SolutionUserSchema_1";
			String uri = indicativeURI;
			// check if uri exists

			uri = changeUri(uri);
			IndividualImpl ind = (IndividualImpl) solAccess.createIndividual(NS
					+ uri);
			addInstanceDatatypePropertyValue(ind, "hasUserID",
					userSchema.getUserId());
			ObjectProperty prop5 = ontologyModel.getObjectProperty(NS
					+ "hasEndUserLicenseAgreement");

			ind.addProperty(prop5, ontologyModel.getIndividual(userSchema
					.getEula().getOntologyURI()));
			prop5 = ontologyModel.getObjectProperty(NS + "refersToSolution");

			ind.addProperty(prop5, ontologyModel.getIndividual(userSchema
					.getRefersToSolution()));
			// saveToOWL();
		}

	}

	public List<SolutionUserSchema> loadAllSolutionUserSchemas() {
		List<SolutionUserSchema> res = new ArrayList<SolutionUserSchema>();
		OntClass cl = ontologyModel.getOntClass(NS + "SolutionUserSchema");
		DatatypeProperty hasUserID = ontologyModel.getDatatypeProperty(NS
				+ "hasUserID");
		ObjectProperty hasEndUserLicenseAgreement = ontologyModel
				.getObjectProperty(NS + "hasEndUserLicenseAgreement");
		ObjectProperty refersToSolution = ontologyModel.getObjectProperty(NS
				+ "refersToSolution");

		// Iterator it = cl.listInstances();
		// while (it.hasNext()) {
		// IndividualImpl ind = (IndividualImpl) it.next();
		ArrayList<IndividualImpl> it = (ArrayList<IndividualImpl>) cl
				.listInstances().toList();
		for (int i = 0; i < it.size(); i++) {

			IndividualImpl ind = it.get(i);

			SolutionUserSchema schema = new SolutionUserSchema();

			if (ind.getPropertyValue(hasUserID) != null) {
				schema.setUserId(ind.getPropertyValue(hasUserID).asLiteral()
						.getString());
			}

			if (ind.getPropertyResourceValue(hasEndUserLicenseAgreement) != null) {
				Resource r1 = ind
						.getPropertyResourceValue(hasEndUserLicenseAgreement);
				schema.setEula(loadEULA(r1.getURI()));
			}

			if (ind.getPropertyResourceValue(refersToSolution) != null) {
				Resource r1 = ind.getPropertyResourceValue(refersToSolution);
				schema.setRefersToSolution(r1.getURI());
			}
			res.add(schema);
		}

		return res;

	}

	public String getSolutionNameByURI(String uri) {
		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(uri);
		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "hasSolutionName");
		if (ind.getPropertyValue(dProperty) != null) {
			return ind.getPropertyValue(dProperty).asLiteral().getString();
		}
		return "";
	}

	public String getUserEmailByURI(String uri) {
		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(uri);
		if (ind != null) {
			DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
					+ "hasUserID");
			if (ind.getPropertyValue(dProperty) != null) {
				return ind.getPropertyValue(dProperty).asLiteral().getString();
			}
		}
		return "";
	}

	public Set<String> getUris() {
		return uris;
	}

	public void setUris(Set<String> uris) {
		this.uris = uris;
	}

	public void createEASTINCluster(ETNACluster cluster,
			String refersToSolution, boolean isProposed) {

		OntClass cl = null;
		if (isProposed)
			cl = ontologyModel.createClass(NS + "EASTIN_Taxonomy_Proposed");
		else
			cl = ontologyModel.createClass(NS + "EASTIN_Taxonomy");

		String uri = "EASTIN_" + cluster.getName().trim();
		// uri = changeUri(uri);

		OntClass clusterClass = ontologyModel.createClass(NS + uri);
		uris.add(clusterClass.getURI().toLowerCase());
		cl.addSubClass(clusterClass);
		Literal description = ontologyModel.createTypedLiteral(
				cluster.getDescription(), XSDDatatype.XSDstring);

		clusterClass.addComment(description);
		// add attributes, measure properties to all_properties
		cluster.getAllproperties().clear();
		cluster.getAllproperties().addAll(cluster.getProperties());
		cluster.getAllproperties().addAll(cluster.getMeasureProperties());

		// create instances for all properties
		for (int i = 0; i < cluster.getAllproperties().size(); i++) {
			EASTINProperty prop = cluster.getAllproperties().get(i);
			// create instances
			String str = changeUri(prop.getName().trim());
			IndividualImpl l1 = (IndividualImpl) ontologyModel
					.createIndividual(NS + "EASTIN_" + str, clusterClass);
			uris.add(l1.getURI().toLowerCase());
			addInstanceDatatypePropertyValue(l1, "EASTIN_Type", prop.getType());
			addInstanceDatatypePropertyValue(l1, "EASTIN_Name", prop.getName());
			addInstanceDatatypePropertyValue(l1, "EASTIN_Definition",
					prop.getDefinition());
			addInstanceDatatypePropertyValue(l1, "EASTIN_RefersToSolution",
					refersToSolution);
			addInstanceDatatypePropertyValue(l1, "EASTIN_ProponentEmail",
					prop.getProponentEmail());
			addInstanceDatatypePropertyValue(l1, "EASTIN_ProponentName",
					prop.getProponentName());
			addInstanceDatatypePropertyValue(l1, "EASTIN_RefersToSolutionName",
					prop.getRefersToSolutionName());
			addInstanceDatatypePropertyValue(l1, "EASTIN_RefersToSolutionID",
					prop.getRefersToSolutionID());
			addInstanceDatatypePropertyValue(l1, "EASTIN_AllSolutionsString",
					cluster.getAllProposalsString());

			if (prop.getType().equalsIgnoreCase("measure")) {

				DatatypeProperty dProp = ontologyModel
						.createDatatypeProperty(NS + "EASTIN__" + str);

				dProp.addRange(XSD.xstring);
				dProp.addDomain(ontologyModel
						.getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
				uris.add(dProp.getURI().toLowerCase());

			}
			prop.setInd(l1.getURI());

		}

		// saveToOWL();
	}

	public List<ETNACluster> loadAllProposedEASTINProperties() {
		List<ETNACluster> proposedEASTINProperties = new ArrayList<ETNACluster>();
		ETNACluster cluster1 = new ETNACluster();
		cluster1.setName("");
		proposedEASTINProperties.add(cluster1);
		OntClass cl = ontologyModel
				.getOntClass(NS + "EASTIN_Taxonomy_Proposed");
		DatatypeProperty ETNA_ID = ontologyModel.getDatatypeProperty(NS
				+ "ETNA_ID");
		DatatypeProperty EASTIN_Type = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Type");
		DatatypeProperty EASTIN_Name = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Name");
		DatatypeProperty EASTIN_Definition = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_Definition");
		DatatypeProperty EASTIN_RefersToSolution = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_RefersToSolution");
		DatatypeProperty EASTIN_ProponentEmail = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_ProponentEmail");
		DatatypeProperty EASTIN_ProponentName = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_ProponentName");
		DatatypeProperty EASTIN_RefersToSolutionID = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_RefersToSolutionID");
		DatatypeProperty EASTIN_RefersToSolutionName = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_RefersToSolutionName");
		DatatypeProperty EASTIN_AllSolutionsString = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_AllSolutionsString");

		if (cl != null) {
			for (Iterator<OntClass> i = cl.listSubClasses(false); i.hasNext();) {
				ETNACluster cluster = new ETNACluster();
				OntClass c = i.next();
				if (c.getLocalName().contains("EASTIN_"))
					cluster.setName(c.getLocalName().replace("EASTIN_", ""));
				else
					cluster.setName(c.getLocalName());
				cluster.setDescription(c.getComment(null));
				Iterator it = c.listInstances();
				while (it.hasNext()) {
					EASTINProperty eastinProperty = new EASTINProperty();
					IndividualImpl ind = (IndividualImpl) it.next();

					if (ind.getPropertyValue(ETNA_ID) != null) {
						eastinProperty.setId(ind.getPropertyValue(ETNA_ID)
								.asLiteral().getString());
					}

					if (ind.getPropertyValue(EASTIN_Type) != null) {
						eastinProperty.setType(ind
								.getPropertyValue(EASTIN_Type).asLiteral()
								.getString());

					}

					if (ind.getPropertyValue(EASTIN_Name) != null) {
						eastinProperty.setName(ind
								.getPropertyValue(EASTIN_Name).asLiteral()
								.getString());

					}

					if (ind.getPropertyValue(EASTIN_Definition) != null) {
						eastinProperty.setDefinition(ind
								.getPropertyValue(EASTIN_Definition)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(EASTIN_RefersToSolution) != null) {
						eastinProperty.setRefersToSolution(ind
								.getPropertyValue(EASTIN_RefersToSolution)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(EASTIN_ProponentEmail) != null) {
						eastinProperty.setProponentEmail(ind
								.getPropertyValue(EASTIN_ProponentEmail)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(EASTIN_ProponentName) != null) {
						eastinProperty.setProponentName(ind
								.getPropertyValue(EASTIN_ProponentName)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(EASTIN_RefersToSolutionID) != null) {
						eastinProperty.setRefersToSolutionID(ind
								.getPropertyValue(EASTIN_RefersToSolutionID)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(EASTIN_RefersToSolutionName) != null) {
						eastinProperty.setRefersToSolutionName(ind
								.getPropertyValue(EASTIN_RefersToSolutionName)
								.asLiteral().getString());

					}

					if (ind.getPropertyValue(EASTIN_AllSolutionsString) != null) {
						eastinProperty.setAllSolutionsString(ind
								.getPropertyValue(EASTIN_AllSolutionsString)
								.asLiteral().getString());

					}

					if (eastinProperty.getType().toLowerCase()
							.equals("attribute")) {
						cluster.getProperties().add(eastinProperty);
						cluster.getAttributesToShow().add(eastinProperty);
						cluster.getAttributesToShowInView().add(eastinProperty);
					} else {
						System.out.println("measure found");
						cluster.getMeasureProperties().add(eastinProperty);
						cluster.getMeasuresToShow().add(eastinProperty);
						cluster.getMeasuresToShowInView().add(eastinProperty);
					}

					cluster.getAllproperties().add(eastinProperty);

				}
				if (cluster.getAllproperties().size() > 0) {
					cluster.setProponentEmail(cluster.getAllproperties().get(0)
							.getProponentEmail());
					cluster.setProponentName(cluster.getAllproperties().get(0)
							.getProponentName());
					cluster.setProposedForProduct(cluster.getAllproperties()
							.get(0).getRefersToSolutionName());
					cluster.setProposedForProductID(cluster.getAllproperties()
							.get(0).getRefersToSolutionID());
					cluster.setAllProposalsString(cluster.getAllproperties()
							.get(0).getAllSolutionsString());
				}

				proposedEASTINProperties.add(cluster);

			}
		}
		return proposedEASTINProperties;
	}

	public void removeProposedEtnaCluster(ETNACluster cluster) {

		OntClass cl = ontologyModel
				.getOntClass(NS + "EASTIN_Taxonomy_Proposed");
		if (cl != null) {
			Iterator<OntClass> i = cl.listSubClasses(false);
			OntClass cur = null;
			while (i.hasNext()) {
				cur = i.next();
				System.out.print("cur " + cur.getLocalName() + "\n");
				if (cluster.getName().equals(
						cur.getLocalName().split("EASTIN_")[1])) {
					break;
				}
			}
			if (cur != null) {
				// cur.remove();
				// remove all individuals
				List<IndividualImpl> list = new ArrayList<IndividualImpl>();
				Iterator it2 = cur.listInstances();
				while (it2.hasNext()) {
					IndividualImpl in = (IndividualImpl) it2.next();
					list.add(in);
				}

				// remove datatypes for properties that are measures
				String type = "";
				DatatypeProperty dProperty = null;
				for (int j = list.size() - 1; j >= 0; j--) {
					IndividualImpl in = list.get(j);
					dProperty = ontologyModel.getDatatypeProperty(NS
							+ "EASTIN_Type");
					type = in.getPropertyValue(dProperty).asLiteral()
							.getString();
					if (type.equalsIgnoreCase("measure")) {
						String uri = in.getURI();
						// System.out.println(uri);
						uri = uri.replace("EASTIN_", "EASTIN__");
						// System.out.println(uri);
						DatatypeProperty d = ontologyModel
								.getDatatypeProperty(uri);
						d.remove();

					}

				}

				// remove properties(attributes/measures)
				for (int j = list.size() - 1; j >= 0; j--) {
					list.get(j).remove();
				}

				cur.remove();
				// cl.removeSubClass(cur);
				// saveToOWL();
				System.out.print("removed " + cur.getLocalName() + "\n");
				uris.remove(cur.getURI().toLowerCase());
			}
		}

	}

	public OntClass addProposedApplicationCategory(String name,
			String description, String parent) {
		OntClass cl = ontologyModel.getOntClass(NS + "Solutions_Proposed");
		if (cl != null) {
			// check if category name exists
			String str = name.trim().replace(" ", "_");

			OntClass cl2 = ontologyModel.createClass(NS + str);
			uris.add(cl2.getURI().toLowerCase());
			cl2.setComment("empty", "en");
			cl2.setLabel(description + "@@" + parent, null);
			cl.addSubClass(cl2);
			// saveToOWL();
			return cl2;
		}
		return null;
	}

	public List<ProposedApplicationCategory> loadAllProposedApplicationCategories() {

		List<ProposedApplicationCategory> list = new ArrayList<ProposedApplicationCategory>();
		// add one empty
		// list.add(new ProposedApplicationCategory());
		OntClass cl = ontologyModel.getOntClass(NS + "Solutions_Proposed");
		DatatypeProperty hasSolutionName = ontologyModel.getDatatypeProperty(NS
				+ "hasSolutionName");
		ObjectProperty hasSolutionVendor = ontologyModel.getObjectProperty(NS
				+ "hasSolutionVendor");

		if (cl != null) {
			ExtendedIterator<OntClass> it = cl.listSubClasses();
			while (it.hasNext()) {
				OntClass subClass = it.next();
				ProposedApplicationCategory prop = new ProposedApplicationCategory();
				prop.setUri(subClass.getURI());
				prop.setName(subClass.getLocalName());
				String[] tmp = subClass.getLabel(null).split("@@");
				prop.setDescription(tmp[0]);
				if (tmp.length == 2)
					prop.setParent(tmp[1]);
				String uri = "";
				boolean flag = true;
				String allSolutions = "";
				Iterator it2 = subClass.listInstances();
				while (it2.hasNext()) {
					IndividualImpl in = (IndividualImpl) it2.next();

					if (in.getPropertyValue(hasSolutionName) != null) {
						prop.getRefersToSolutions().add(
								in.getPropertyValue(hasSolutionName)
										.asLiteral().getString());
						allSolutions = allSolutions.concat(in
								.getPropertyValue(hasSolutionName).asLiteral()
								.getString()
								+ ",");
					}

					// use flag to get only the name of the first proponent
					if (in.getPropertyResourceValue(hasSolutionVendor) != null
							&& flag) {
						Resource r1 = in
								.getPropertyResourceValue(hasSolutionVendor);

						uri = r1.getURI();
						flag = false;
					}

				}

				// load the vendor
				Vendor vendor = null;
				if (!uri.isEmpty()) {
					vendor = loadVendorByURI(uri);

					prop.setProponentName(vendor.getVendorName());
					prop.setProponentEmail(vendor.getContactDetails());
				} else {

					prop.setProponentName("Administrator");
					prop.setProponentEmail("test_admin@cloud4all.org");
				}

				// replace the last comma
				if (!allSolutions.isEmpty() && allSolutions.contains(",")) {
					StringBuilder b = new StringBuilder(allSolutions);
					b.replace(allSolutions.lastIndexOf(","),
							allSolutions.lastIndexOf(",") + 1, "");
					allSolutions = b.toString();
				}
				// add proposed category to the list
				prop.setAll_solutions_string(allSolutions);
				list.add(prop);
			}
		}

		return list;
	}

	public void acceptCategory(ProposedApplicationCategory cat, String category) {

		if (cat.isEdit())
			applyChangeInProposedApplicationCategory(cat, cat.getOldcategory());

		OntClass categoryClass = ontologyModel.getOntClass(NS
				+ cat.getName().trim());
		OntClass cl = ontologyModel.getOntClass(NS + category);
		cl.addSubClass(categoryClass);
		OntClass cl3 = ontologyModel.getOntClass(NS + "Solutions_Proposed");
		cl3.removeSubClass(categoryClass);
		saveToOWL();
	}

	public void deleteCategory(ProposedApplicationCategory cat) {

		try {
			OntClass categoryClass = ontologyModel.getOntClass(NS
					+ cat.getName().trim());

			if (categoryClass != null) {
				OntClass cl = ontologyModel.getOntClass(NS + "Solutions");

				OntClass cl3 = ontologyModel.getOntClass(NS
						+ "Solutions_Proposed");
				// transfer individuals
				List<IndividualImpl> list = new ArrayList<IndividualImpl>();
				Iterator it2 = categoryClass.listInstances();
				while (it2.hasNext()) {
					IndividualImpl in = (IndividualImpl) it2.next();
					list.add(in);
				}
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setOntClass(cl);
				}
				cl3.removeSubClass(categoryClass);
				categoryClass.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteCategoryClass(ProposedApplicationCategory cat) {
		try {

			if (cat.isEdit())
				applyChangeInProposedApplicationCategory(cat,
						cat.getOldcategory());

			OntClass categoryClass = ontologyModel.getOntClass(NS
					+ cat.getName().trim());

			OntClass proposedClasses = ontologyModel.getOntClass(NS
					+ "Solutions_Proposed");

			categoryClass.remove();

			// if(categoryClass!=null)
			// proposedClasses.removeSubClass(categoryClass);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean applyChangeInProposedApplicationCategory(
			ProposedApplicationCategory newcat, ProposedApplicationCategory cat) {

		boolean flag = false;
		OntClass originalClass = ontologyModel.getOntClass(NS
				+ cat.getName().trim());
		if (!newcat.getName().trim().equalsIgnoreCase(cat.getName().trim())) {

			OntClass cl = ontologyModel.createClass(NS
					+ newcat.getName().trim().replace(" ", "_"));

			cl.setComment(newcat.getDescription() + "@@" + newcat.getParent(),
					null);
			uris.add(cl.getURI().toLowerCase());
			// transfer individuals
			OntClass cl3 = ontologyModel.getOntClass(NS + "Solutions_Proposed");
			List<IndividualImpl> list = new ArrayList<IndividualImpl>();
			Iterator it2 = originalClass.listInstances();
			while (it2.hasNext()) {
				IndividualImpl in = (IndividualImpl) it2.next();
				list.add(in);
				// in.setOntClass(cl);
			}
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setOntClass(cl);
			}
			cl.setSuperClass(cl3);
			uris.remove(originalClass.getURI());
			cl3.removeSubClass(originalClass);
			originalClass.remove();
			newcat.setUri(cl.getURI());
		} else {

			flag = true;
			originalClass.setComment(
					newcat.getDescription() + "@@" + newcat.getParent(), null);
		}

		return flag;
	}

	public void applyChangeInProposedEASTINItem(EASTINProperty newcat,
			EASTINProperty cat) {
		System.out.println(cat.getBelongsToCluster());
		// find uri of etna cluster
		OntClass mainClass = ontologyModel.getOntClass(NS + "EASTIN_Taxonomy");
		DatatypeProperty EASTIN_Name = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Name");
		DatatypeProperty EASTIN_Definition = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_Definition");
		DatatypeProperty EASTIN_Type = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Type");
		DatatypeProperty EASTIN_AllSolutionsString = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_AllSolutionsString");

		ExtendedIterator<OntClass> it = mainClass.listSubClasses();
		OntClass cluster = null;
		while (it.hasNext()) {
			OntClass cl = it.next();
			if (Utilities.splitCamelCase(
					cl.getLocalName().replace("EASTIN_", "")).equals(
					cat.getBelongsToCluster())) {
				cluster = cl;
				break;
			}
		}
		// find old individual
		if (cluster != null) {
			ExtendedIterator it2 = cluster.listInstances();
			IndividualImpl in = null;
			while (it2.hasNext()) {
				IndividualImpl ind = (IndividualImpl) it2.next();

				if (ind.getPropertyValue(EASTIN_Name) != null) {
					if (ind.getPropertyValue(EASTIN_Name).asLiteral()
							.getString().equalsIgnoreCase(cat.getName())) {
						in = ind;
						break;
					}
				}
			}
			if (in != null) {

				in.removeAll(EASTIN_Name);
				in.removeAll(EASTIN_Definition);
				in.removeAll(EASTIN_Type);
				in.addProperty(EASTIN_Name, newcat.getName());
				in.addProperty(EASTIN_Definition, newcat.getDefinition());
				in.addProperty(EASTIN_Type, newcat.getType());
				in.addProperty(EASTIN_AllSolutionsString,
						newcat.getAllSolutionsString());

				// if item became measure create a new datatype
				if (newcat.getType().equalsIgnoreCase("measure")
						&& cat.getType().equalsIgnoreCase("attribute")) {

					String string = in.getURI().replace("EASTIN_", "EASTIN__");
					DatatypeProperty dProp = ontologyModel
							.createDatatypeProperty(string);

					dProp.addRange(XSD.xstring);
					dProp.addDomain(ontologyModel
							.getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
					uris.add(dProp.getURI().toLowerCase());

				}

				// if item became attribute
				// delete datatype
				if (cat.getType().equalsIgnoreCase("measure")
						&& newcat.getType().equalsIgnoreCase("attribute")) {
					DatatypeProperty dProp = ontologyModel
							.getDatatypeProperty(NS + "EASTIN__"
									+ cat.getName().trim().replace(" ", "_"));

					dProp.remove();
				}

				it = mainClass.listSubClasses();
				cluster = null;
				while (it.hasNext()) {
					OntClass cl = it.next();
					if (Utilities.splitCamelCase(
							cl.getLocalName().replace("EASTIN_", "")).equals(
							newcat.getBelongsToCluster())) {
						cluster = cl;
						break;
					}
				}
				in.setOntClass(cluster);

			}

		}
	}

	public void deleteEASTINItem(EASTINProperty cat) {
		System.out.println(cat.getBelongsToCluster());
		// find uri of etna cluster
		OntClass mainClass = ontologyModel.getOntClass(NS + "EASTIN_Taxonomy");
		DatatypeProperty EASTIN_Name = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Name");

		ExtendedIterator<OntClass> it = mainClass.listSubClasses();
		OntClass cluster = null;
		while (it.hasNext()) {
			OntClass cl = it.next();
			if (Utilities.splitCamelCase(
					cl.getLocalName().replace("EASTIN_", "")).equals(
					cat.getBelongsToCluster())) {
				cluster = cl;
				break;
			}
		}
		// find individual
		if (cluster != null) {
			ExtendedIterator it2 = cluster.listInstances();
			IndividualImpl in = null;
			while (it2.hasNext()) {
				IndividualImpl ind = (IndividualImpl) it2.next();

				if (ind.getPropertyValue(EASTIN_Name) != null) {
					if (ind.getPropertyValue(EASTIN_Name).asLiteral()
							.getString().equalsIgnoreCase(cat.getName())) {
						in = ind;
						break;
					}
				}
			}

			DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
					+ "isProposed");
			// remove the item only if its still proposed
			if (in.getPropertyValue(dProperty) != null
					&& in.getPropertyValue(dProperty).asLiteral().getString()
							.equals("true")) {

				// remove datatype for measures (same name with property)
				if (cat.getType().equalsIgnoreCase("measure")) {
					String uri = in.getURI();
					System.out.println(uri);
					uri = uri.replace("EASTIN_", "EASTIN__");
					System.out.println(uri);
					DatatypeProperty d = ontologyModel.getDatatypeProperty(uri);
					d.remove();
					uris.remove(uri.toLowerCase());

				}

				// remove property(attribute/measure)
				in.remove();
				System.out.println("remove " + cat.getInd());

			}
			// saveToOWL();
		}
	}

	public String loadEASTINPropertyCluster(EASTINProperty prop) {
		try {
			IndividualImpl ind = (IndividualImpl) ontologyModel
					.getIndividual(prop.getInd());
			OntClass cl = ind.getOntClass();
			return Utilities
					.splitCamelCase(cl.getLocalName().split("EASTIN_")[1]);
		} catch (Exception ex) {
			return "";
		}

	}

	public void addNewETNAPropertyInCluster(ETNACluster cluster,
			EASTINProperty item) {
		// find uri of etna cluster

		OntClass mainClass = ontologyModel.getOntClass(NS + "EASTIN_Taxonomy");
		DatatypeProperty EASTIN_Name = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Name");
		DatatypeProperty EASTIN_Type = ontologyModel.getDatatypeProperty(NS
				+ "EASTIN_Type");
		DatatypeProperty isProposed = ontologyModel.getDatatypeProperty(NS
				+ "isProposed");
		DatatypeProperty EASTIN_Definition = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_Definition");
		DatatypeProperty EASTIN_RefersToSolutionName = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_RefersToSolutionName");
		DatatypeProperty EASTIN_RefersToSolutionID = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_RefersToSolutionID");
		DatatypeProperty EASTIN_ProponentName = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_ProponentName");
		DatatypeProperty EASTIN_ProponentEmail = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_ProponentEmail");
		DatatypeProperty EASTIN_AllSolutionsString = ontologyModel
				.getDatatypeProperty(NS + "EASTIN_AllSolutionsString");
		DatatypeProperty ETNA_relatedToTypeOfApplication = ontologyModel
				.getDatatypeProperty(NS + "ETNA_relatedToTypeOfApplication");

		ExtendedIterator<OntClass> it = mainClass.listSubClasses();
		String clusterUri = "";
		while (it.hasNext()) {
			OntClass cl = it.next();
			if (cl.getLocalName().equals(cluster.getOntologyName())) {
				clusterUri = cl.getURI();
				break;
			}
		}
		OntClass clusterClass = ontologyModel.getOntClass(clusterUri);
		// check if already exists
		boolean found = false;
		Iterator it2 = clusterClass.listInstances();
		while (it2.hasNext()) {
			IndividualImpl in = (IndividualImpl) it2.next();

			if (EASTIN_Name != null && in.getPropertyValue(EASTIN_Name) != null) {
				if (in.getPropertyValue(EASTIN_Name).asLiteral().getString()
						.equals(item.getName())) {

					if (EASTIN_Type != null
							&& in.getPropertyValue(EASTIN_Type) != null) {
						if (in.getPropertyValue(EASTIN_Type).asLiteral()
								.getString().equals(item.getType())) {
							found = true;
							break;
						}
					}
				}
			}
		}
		if (!found) {

			String str = NS + "EASTIN_"
					+ item.getName().trim().replace(" ", "_");
			str = changeUri(str);
			IndividualImpl ind = (IndividualImpl) ontologyModel
					.createIndividual(str, clusterClass);
			uris.add(ind.getURI().toLowerCase());
			ind.addProperty(isProposed, "true");
			ind.addProperty(EASTIN_Definition, item.getDefinition());
			ind.addProperty(EASTIN_Name, item.getName());
			ind.addProperty(EASTIN_RefersToSolutionName,
					item.getRefersToSolutionName());
			ind.addProperty(EASTIN_RefersToSolutionID,
					item.getRefersToSolutionID());
			ind.addProperty(EASTIN_ProponentName, item.getProponentName());
			ind.addProperty(EASTIN_ProponentEmail, item.getProponentEmail());
			ind.addProperty(EASTIN_Type, item.getType());
			ind.addProperty(EASTIN_AllSolutionsString,
					item.getAllSolutionsString());
			ind.addProperty(ETNA_relatedToTypeOfApplication,
					item.getRelatedToTypeOfApplication());

			if (item.getType().equalsIgnoreCase("measure")) {

				// create new datatypeProperty
				str = str.replace("EASTIN_", "EASTIN__");
				DatatypeProperty dProperty = ontologyModel
						.createDatatypeProperty(str);

				dProperty.addRange(XSD.xstring);
				dProperty
						.addDomain(ontologyModel
								.getResource("http://www.cloud4all.eu/SemanticFrameworkForContentAndSolutions.owl#Settings"));
				uris.add(dProperty.getURI().toLowerCase());

			}
			item.setInd(ind.getURI());
			item.setBelongsToCluster(cluster.getName());
		}
	}

	public void acceptProposedEASTINItem(EASTINProperty prop) {

		// if item does not exist, create it
		if (prop.getInd() == null) {
			ETNACluster c = new ETNACluster();
			c.setOntologyName("EASTIN_"
					+ prop.getBelongsToCluster().replace(" ", ""));
			addNewETNAPropertyInCluster(c, prop);
		}

		IndividualImpl ind = (IndividualImpl) ontologyModel.getIndividual(prop
				.getInd());

		DatatypeProperty dProperty = ontologyModel.getDatatypeProperty(NS
				+ "isProposed");
		if (ind.getPropertyValue(dProperty) != null
				&& ind.getPropertyValue(dProperty).asLiteral().getString()
						.equals("true")) {
			dProperty = ontologyModel.getDatatypeProperty(NS + "isProposed");
			ind.removeAll(dProperty);
			dProperty = ontologyModel.getDatatypeProperty(NS + "EASTIN_Type");
			if (ind.getPropertyValue(dProperty) != null) {
				addInstanceDatatypePropertyValue(ind, "EASTIN_Type",
						prop.getType());
			}
			addInstanceDatatypePropertyValue(ind, "isProposed", "false");
			// saveToOWL();
			return;

		}
	}

	public void removeOldSolution(String s) {

		try {

			System.out.println("remove " + s);
			uris.remove(s.toLowerCase());
			IndividualImpl ind = (IndividualImpl) getOntologyModel()
					.getIndividual(s);
			if (ind != null) {

				// remove settingsHandlers instance
				Individual settingsHandlerInstance = ontologyModel
						.getIndividual(s + "_SettingsHandlersInst");

				if (settingsHandlerInstance != null)
					settingsHandlerInstance.remove();

				Individual settingsHandlerValuesInst = ontologyModel
						.getIndividual(s + "_SettingsHandlersValuesInst");
				if (settingsHandlerValuesInst != null)
					settingsHandlerValuesInst.remove();

				// remove from Settings class, the solutionNameSettings class
				OntClass cl = ontologyModel.getOntClass(NS + ind.getLocalName()
						+ "Settings");

				if (cl != null)
					cl.remove();

				// find individuals that contain settings of solution
				ObjectProperty dProperty = getOntologyModel()
						.getObjectProperty(
								getNS() + "hasSolutionSpecificSettings");

				if (ind.getPropertyValue(dProperty) != null) {
					NodeIterator it = ind.listPropertyValues(dProperty);
					List<RDFNode> l = it.toList();
					for (int i = l.size() - 1; i >= 0; i--) {

						Resource r = l.get(i).asResource();
						Individual settingsInd = getOntologyModel()
								.getIndividual(r.getURI());
						settingsInd.remove();
					}
				}

				// find individual that contains technical details of solution
				dProperty = getOntologyModel().getObjectProperty(
						getNS() + "hasTechnicalDetails");

				if (ind.getPropertyResourceValue(dProperty) != null) {
					Resource r = ind.getPropertyResourceValue(dProperty);

					IndividualImpl settingsInd = (IndividualImpl) getOntologyModel()
							.getIndividual(r.getURI());
					settingsInd.remove();
				}

				// find individual that contains solution access info for users
				dProperty = getOntologyModel().getObjectProperty(
						getNS() + "hasSolutionAccessInfoForVendors");
				if (ind.getPropertyResourceValue(dProperty) != null) {
					Resource r = ind.getPropertyResourceValue(dProperty);
					// System.out.println(r.getURI());
					IndividualImpl settingsInd = (IndividualImpl) getOntologyModel()
							.getIndividual(r.getURI());
					dProperty = getOntologyModel().getObjectProperty(
							getNS() + "hasCommercialCostSchema");
					if (settingsInd.getPropertyResourceValue(dProperty) != null) {
						Resource r2 = settingsInd
								.getPropertyResourceValue(dProperty);

						IndividualImpl settingsInd2 = (IndividualImpl) getOntologyModel()
								.getIndividual(r2.getURI());
						dProperty = getOntologyModel().getObjectProperty(
								getNS() + "hasTrialSchema");
						if (settingsInd2.getPropertyResourceValue(dProperty) != null) {
							Resource r3 = settingsInd2
									.getPropertyResourceValue(dProperty);

							IndividualImpl settingsInd3 = (IndividualImpl) getOntologyModel()
									.getIndividual(r3.getURI());
							settingsInd3.remove();
						}
						dProperty = getOntologyModel().getObjectProperty(
								getNS() + "hasDiscountIfUsedWithOtherSolution");
						if (settingsInd2.getPropertyResourceValue(dProperty) != null) {
							NodeIterator it2 = settingsInd2
									.listPropertyValues(dProperty);
							List<RDFNode> l2 = it2.toList();
							for (int i = l2.size() - 1; i >= 0; i--) {

								Resource r3 = l2.get(i).asResource();
								IndividualImpl settingsInd3 = (IndividualImpl) getOntologyModel()
										.getIndividual(r3.getURI());
								settingsInd3.remove();
							}
						}

						settingsInd2.remove();
					}
					dProperty = getOntologyModel().getObjectProperty(
							getNS() + "hasLicense");
					if (settingsInd.getPropertyResourceValue(dProperty) != null) {
						Resource r2 = settingsInd
								.getPropertyResourceValue(dProperty);
						// System.out.println(r.getURI());
						IndividualImpl settingsInd2 = (IndividualImpl) getOntologyModel()
								.getIndividual(r2.getURI());
						settingsInd2.remove();
					}

					settingsInd.remove();
				}
			}
			if (ind != null)
				ind.remove();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// save handler type and handler properties for a solution
	public void saveHandlerForSolution(Solution sol, IndividualImpl solInd,
			String str, ArrayList<SettingsHandler> settingsHandlers) {

		// get properties
		ObjectProperty hasSettingsHandler = ontologyModel.getObjectProperty(NS
				+ "hasSettingsHandler");
		ObjectProperty settingsHandlerHasName = ontologyModel
				.getObjectProperty(NS + "settingsHandlerHasName");
		ObjectProperty settingHandlerHasValues = ontologyModel
				.getObjectProperty(NS + "settingsHandlerHasValues");
		String handlerName = sol.getHandlerType().replace(" ", "");

		// create settingsHandlersInstance
		OntClass SettingsHandlersClass = ontologyModel.getOntClass(NS
				+ "SettingsHandlers");
		IndividualImpl settingsHandlersInstance = (IndividualImpl) ontologyModel
				.createIndividual(NS + str + "_SettingsHandlersInst",
						SettingsHandlersClass);

		Individual settingHandlerInd = settingsHandlersInstance.asIndividual();

		Individual handlerInd = ontologyModel.getIndividual(NS + "Type_"
				+ handlerName);

		settingHandlerInd.addProperty(settingsHandlerHasName, handlerInd);

		// create settingsHandlersValuesInstance
		OntClass SettingsHandlersValuesClass = ontologyModel.getOntClass(NS
				+ "Values_" + handlerName);
		IndividualImpl settingsHandlersValuesInstance = (IndividualImpl) ontologyModel
				.createIndividual(NS + str + "_SettingsHandlersValuesInst",
						SettingsHandlersValuesClass);

		int index = -1;
		for (SettingsHandler temp : settingsHandlers) {
			if (temp.getHandlerName().equalsIgnoreCase(handlerName)) {
				index = settingsHandlers.indexOf(temp);
				break;
			}

		}

		List<Element> options = new ArrayList<Element>();
		if (index != -1)
			options = settingsHandlers.get(index).getHandlersOptions();

		Resource domain = ontologyModel.getResource(SettingsHandlersValuesClass
				.getURI());

		DatatypeProperty prop = null;
		boolean flag = false;
		// save options values
		for (Element el : sol.getOptions()) {

			// check if element is an empty element
			if (!el.getOptionName().isEmpty() && !el.getOptionValue().isEmpty()) {

				if (el.getOptionValue().equalsIgnoreCase("true")
						|| el.getOptionValue().equalsIgnoreCase("false"))
					flag = true;

				String optionName = el.getOptionName().trim().replace(" ", "_")
						.replaceAll("[^\\p{L}\\p{Nd}]", "");
				boolean existing = false;
				for (Element s : options) {
					if (s.getOptionName().toLowerCase()
							.contains("hasvalue_" + optionName.toLowerCase())) {

						if ((flag && s.getOptionValue().contains("boolean"))
								|| (!flag && !s.getOptionValue().contains(
										"boolean"))) {
							optionName = s.getOptionName();
							existing = true;
							break;
						}
					}
				}

				if (existing) {

					// find the existing property
					prop = ontologyModel.getDatatypeProperty(NS + optionName);

				} else {

					String range = "string";
					if (flag)
						range = "boolean";

					if (index != -1)
						settingsHandlers.get(index).getHandlersOptions()
								.add(new Element(optionName, range));
					// property does not exist
					String changeUri = changeUri("hasValue_" + optionName);
					changeUri = NS + changeUri;

					prop = ontologyModel.createDatatypeProperty(changeUri);

					// add range according to the value
					if (flag)
						prop.addRange(XSD.xboolean);
					else
						prop.addRange(XSD.xstring);

					prop.addDomain(domain);

					uris.add(prop.getURI().toLowerCase());

				}

				if (prop == null) {
					String range = "string";
					if (flag)
						range = "boolean";

					if (index != -1)
						settingsHandlers.get(index).getHandlersOptions()
								.add(new Element(optionName, range));
					// property does not exist
					String changeUri = changeUri("hasValue_" + optionName);
					changeUri = NS + changeUri;

					prop = ontologyModel.createDatatypeProperty(changeUri);

					// add range according to the value
					if (flag)
						prop.addRange(XSD.xboolean);
					else
						prop.addRange(XSD.xstring);

					prop.addDomain(domain);

					uris.add(prop.getURI().toLowerCase());
				}

				// add boolean value
				if (flag) {
					if (el.getOptionValue().equals("false"))
						flag = false;
					settingsHandlersValuesInstance.addProperty(prop,
							ontologyModel.createTypedLiteral((Boolean) flag));
				} else {
					// add String value
					settingsHandlersValuesInstance.addProperty(prop, el
							.getOptionValue().trim());

				}

				flag = false;
			}

		}

		settingsHandlersInstance.addProperty(settingHandlerHasValues,
				settingsHandlersValuesInstance);

		// add hasSettingsHandler property to solution
		solInd.addProperty(hasSettingsHandler, settingsHandlersInstance);
	}

	// load handler type and handler options for a solution
	public void loadHandlersDataForSolution(Solution sol,
			ObjectProperty hasHandlerType,
			ObjectProperty settingHandlerHasName,
			ObjectProperty settingHandlersHasValues, Individual in) {

		String handlerType = "";
		Resource handlerTypeInd = null;

		if (in.getPropertyResourceValue(hasHandlerType) != null) {
			handlerTypeInd = in.getPropertyResourceValue(hasHandlerType);

			String handlerUri = handlerTypeInd.getURI();

			if (handlerUri != null && !handlerUri.isEmpty()) {

				if (handlerTypeInd
						.getPropertyResourceValue(settingHandlerHasName) != null)
					handlerType = handlerTypeInd
							.getPropertyResourceValue(settingHandlerHasName)
							.getLocalName().replace("Type_", "");

				sol.setHandlerType(Utilities.splitCamelCase(handlerTypeInd
						.getPropertyResourceValue(settingHandlerHasName)
						.getLocalName().replace("Type_", "")));

				Resource handlerIndValues = handlerTypeInd
						.getPropertyResourceValue(settingHandlersHasValues);

				String handlerValuesUri = handlerIndValues.getURI();

				Individual handlerValuesInd = ontologyModel
						.getIndividual(handlerValuesUri);

				List<Statement> properties = handlerValuesInd.listProperties()
						.toList();

				List<Element> options = new ArrayList<Element>();
				Element el = null;
				for (Statement st : properties) {

					if (st.getObject().isLiteral()) {

						String optionName = st.getPredicate().getLocalName()
								.replace("hasValue_", "");

						DatatypeProperty property = ontologyModel
								.getDatatypeProperty(NS
										+ st.getPredicate().getLocalName());

						String domain = property.getDomain().toString();
						// check domain again, sth evil happens
						if (domain.contains("Values_" + handlerType)
								|| !domain.contains(NS)) {

							if (optionName.contains("_")) {
								optionName = optionName.substring(0,
										optionName.indexOf("_"));
							}

							el = new Element(
									optionName.toLowerCase(),
									st.getLiteral()
											.getLexicalForm()
											.replace(
													"^^http://www.w3.org/2001/XMLSchema#string",
													""));
							options.add(el);
						}
					}

				}

				if (options.size() == 0) {
					el = new Element("", "");
					options.add(el);
				}

				sol.setOptions(options);

			}

		}

	}

	public ArrayList<SettingsHandler> loadSettingsHandlers() {
		ArrayList<SettingsHandler> list = new ArrayList<SettingsHandler>();

		OntClass parentClass = ontologyModel.getOntClass(NS
				+ "SettingsHandlersValues");
		List<OntClass> subClasses = parentClass.listSubClasses().toList();
		SettingsHandler setHandler = null;
		String name = "";
		ArrayList<Element> properties;

		for (OntClass subCl : subClasses) {

			setHandler = new SettingsHandler();

			name = subCl.getLocalName().replace("Values_", "");
			setHandler.setHandlerName(name);
			setHandler.setRealName(Utilities.splitCamelCase(name));
			setHandler.setExportName(Utilities
					.convertSelectedHandlerType(setHandler.getRealName()));

			properties = new ArrayList<Element>();

			// list the instances of the subclass
			ArrayList<ExtendedIterator> instances = (ArrayList<ExtendedIterator>) subCl
					.listInstances().toList();

			for (int i = 0; i < instances.size(); i++) {

				Individual inst = (Individual) instances.get(i);

				List<Statement> Instproperties = inst.listProperties().toList();

				// list the properties of the instance
				for (Statement st : Instproperties) {

					if (st.getObject().isLiteral()) {

						Element el = new Element("", "");
						String optionName = st.getPredicate().getLocalName();

						DatatypeProperty property = ontologyModel
								.getDatatypeProperty(NS
										+ st.getPredicate().getLocalName());

						String domain = property.getDomain().toString();
						// check the domain, sth evil happens
						// when listing properties, add the property only if
						// its domain is equal to the subclass name
						if (domain.contains(subCl.getLocalName())
								|| !domain.contains(NS)) {

							if (!uris.contains(st.getPredicate().getURI()
									.toLowerCase()))
								uris.add(st.getResource().getURI()
										.toLowerCase());

							String range = "string";
							if (st.getLiteral().getLexicalForm().equals("true")
									|| st.getLiteral().getLexicalForm()
											.equals("false"))
								range = "boolean";

							el.setOptionName(optionName);
							el.setOptionValue(range);
							if (!properties.contains(el))
								properties.add(el);

						}
					}

				}

			}

			setHandler.setHandlersOptions(properties);

			list.add(setHandler);

		}

		return list;

	}

}
