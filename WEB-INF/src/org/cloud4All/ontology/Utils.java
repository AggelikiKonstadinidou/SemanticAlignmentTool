package org.cloud4All.ontology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.cloud4All.ManageRegistryBean;
import org.cloud4All.Setting;
import org.cloud4All.Solution;
import org.cloud4All.ontology.MapTerm.Record;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@ManagedBean(name = "utils")
@ApplicationScoped
public class Utils {
	private OntologyInstance ontologyInstance;
	private HashMap<String, RegistryTerm> ws_terms = new HashMap<String, RegistryTerm>();
	private HashMap<String, RegistryTerm> ont_terms = new HashMap<String, RegistryTerm>();
	private HashMap<String, RegistryTerm> ws_aliases = new HashMap<String, RegistryTerm>();
	private HashMap<String, RegistryTerm> ont_aliases = new HashMap<String, RegistryTerm>();
	private ArrayList<RegistryTerm> termsForInsert = new ArrayList<RegistryTerm>();
	private ArrayList<RegistryTerm> proposedTermsForInsert = new ArrayList<RegistryTerm>();
	private ArrayList<RegistryTerm> termsForUpdate = new ArrayList<RegistryTerm>();
	private ArrayList<RegistryTerm> termsForRemove = new ArrayList<RegistryTerm>();
	private ArrayList<RegistryTerm> checkedTerms = new ArrayList<RegistryTerm>();
	private boolean flag = false;
	private HashMap<String, Solution> webServiceSolutions = new HashMap<String, Solution>();
	private HashMap<String, Solution> ontologySolutions = new HashMap<String, Solution>();
	private ArrayList<Solution> solutionsForInsert = new ArrayList<Solution>();
	private ArrayList<Solution> solutionsForUpdate = new ArrayList<Solution>();
	private ArrayList<Solution> solutionsForRemove = new ArrayList<Solution>();
	private ArrayList<Solution> solutionsToPost = new ArrayList<Solution>();
	private ArrayList<RegistryTerm> registryTermsToPost = new ArrayList<RegistryTerm>();

	public static void main(String args[]) throws ClientProtocolException,
			IOException {

	}

	public void synchronize() {

		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);

			termsForInsert = new ArrayList<RegistryTerm>();
			proposedTermsForInsert = new ArrayList<RegistryTerm>();
			termsForUpdate = new ArrayList<RegistryTerm>();
			termsForRemove = new ArrayList<RegistryTerm>();
			checkedTerms = new ArrayList<RegistryTerm>();
			registryTermsToPost = new ArrayList<RegistryTerm>();

			String cookies = WebServicesForTerms
					.loginAndGetCookiesWithCurl(WebServicesForTerms.signInForDictionaryTermsApi);

			// get hashmap with web service terms
			ws_terms = WebServicesForTerms
					.getWebServiceRegistryTerms(WebServicesForTerms.ALL_TERMS_WITH_CHILDREN_URL,cookies);

			// create hashmap with all ontology terms
			ont_terms = new HashMap<String, RegistryTerm>();
			for (RegistryTerm term : ontologyInstance.getRegistryTerms()) {
				ont_terms.put(term.getId(), term);
			}
			for (RegistryTerm term : ontologyInstance
					.getProposedRegistryTerms()) {
				ont_terms.put(term.getId(), term);
			}

			// get hashmap with web service aliases
			ws_aliases = WebServicesForTerms
					.getWebServiceRegistryTerms(WebServicesForTerms.ALIAS_URL,cookies);

			flag = false;
			if (ws_aliases == null || ws_terms == null)
				flag = true;

			// create a hashmap with all ontology aliases
			ont_aliases = new HashMap<String, RegistryTerm>();
			for (RegistryTerm term : ontologyInstance.getRegistryAliases()) {
				ont_aliases.put(term.getId(), term);
			}
			for (RegistryTerm term : ontologyInstance
					.getProposedRegistryAliases()) {
				ont_aliases.put(term.getId(), term);
			}

			// check aliases and terms for any changes
			// get hashmaps with ids that were not found in web service
			// and will be post

			test(ws_aliases, ont_aliases);

			test(ws_terms, ont_terms);

			// POST our terms that were not found in PTD
			for (RegistryTerm term : registryTermsToPost) {
				if (!term.getId().equals("speechRate")
						&& !term.getId().equals("dUMMY")) {
					// TODO
					//WebServicesForTerms.postRegistryTermWithCurl(cookies, term);
				}

			}

			for (RegistryTerm term : registryTermsToPost) {
				// System.out.println(term.getId());
			}

			// remove old terms and aliases from lists
			for (RegistryTerm term : termsForRemove) {
				boolean flag = false;
				for (RegistryTerm temp : ontologyInstance.getRegistryTerms()) {
					if (!term.getNotes().equals("ALIAS"))
						if (temp.getId().equals(term.getId())) {
							int i = ontologyInstance.getRegistryTerms()
									.indexOf(temp);
							ontologyInstance.getRegistryTerms().remove(i);
							flag = true;
							break;
						} else if (temp.getId().equals(term.getId())) {
							int i = ontologyInstance.getRegistryAliases()
									.indexOf(temp);
							ontologyInstance.getRegistryAliases().remove(i);
							flag = true;
							break;
						}

				}

				if (!flag)
					for (RegistryTerm temp : ontologyInstance
							.getProposedRegistryTerms()) {
						if (!term.getNotes().equals("ALIAS"))
							if (temp.getId().equals(term.getId())) {
								int i = ontologyInstance
										.getProposedRegistryTerms().indexOf(
												temp);
								ontologyInstance.getProposedRegistryTerms()
										.remove(i);
								flag = true;
								break;
							} else if (temp.getId().equals(term.getId())) {
								int i = ontologyInstance
										.getProposedRegistryAliases().indexOf(
												temp);
								ontologyInstance.getProposedRegistryAliases()
										.remove(i);
								flag = true;
								break;
							}
					}
			}

			// insert updated terms and aliases to lists
			for (RegistryTerm term : termsForUpdate) {
				if (term.getStatus().equals("accepted")
						&& !term.getNotes().equals("ALIAS"))
					ontologyInstance.getRegistryTerms().add(term);
				else if (!term.getStatus().equals("accepted")
						&& !term.getNotes().equals("ALIAS"))
					ontologyInstance.getProposedRegistryTerms().add(term);
				else if (term.getStatus().equals("accepted")
						&& term.getNotes().equals("ALIAS"))
					ontologyInstance.getRegistryAliases().add(term);
				else if (!term.getStatus().equals("accepted")
						&& term.getNotes().equals("ALIAS"))
					ontologyInstance.getProposedRegistryAliases().add(term);

			}

			// insert new terms and aliases to lists
			for (RegistryTerm term : termsForInsert) {
				if (!term.getNotes().equals("ALIAS"))
					ontologyInstance.getRegistryTerms().add(term);
				else
					ontologyInstance.getRegistryAliases().add(term);
			}

			for (RegistryTerm term : proposedTermsForInsert) {
				if (!term.getNotes().equals("ALIAS"))
					ontologyInstance.getProposedRegistryTerms().add(term);
				else
					ontologyInstance.getProposedRegistryAliases().add(term);
			}

			// separate aliases from terms
			ArrayList<RegistryTerm> realTerms = new ArrayList<RegistryTerm>();
			for (RegistryTerm term : this.getTermsForInsert()) {
				if (!term.getNotes().equalsIgnoreCase("ALIAS"))
					realTerms.add(term);
			}

			// set values to show them when sync finishes
			ontologyInstance.setNumberOfWebServiceTerms(String
					.valueOf(realTerms.size()));
			ontologyInstance.setNumberOfWebServiceProposedTerms(String
					.valueOf(this.getProposedTermsForInsert().size()));

			System.out.println("total registry terms "
					+ ontologyInstance.getRegistryTerms().size());
			System.out.println("total proposed registry terms "
					+ ontologyInstance.getProposedRegistryTerms().size());
		} catch (Exception e) {
			System.out.println("error in service!");
		}

	}

	public void test(HashMap<String, RegistryTerm> ws,
			HashMap<String, RegistryTerm> ont) {
		checkedTerms = new ArrayList<RegistryTerm>();
		boolean flag = false;
		// check if our ids are contained in his ids
		Iterator it1 = ont.entrySet().iterator();
		while (it1.hasNext()) {
			Map.Entry pairs1 = (Map.Entry) it1.next();

			RegistryTerm reg1 = (RegistryTerm) pairs1.getValue();

			Iterator it2 = ws.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry pairs2 = (Map.Entry) it2.next();

				RegistryTerm reg2 = (RegistryTerm) pairs2.getValue();

				if (reg1.getId().equalsIgnoreCase(reg2.getId())) {
					flag = true;
					break;
				}
			}

			if (!flag)
				registryTermsToPost.add(reg1);

			flag = false;
		}

		Iterator it = ws.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();

			RegistryTerm ws_term = (RegistryTerm) pairs.getValue();

			// check if ws_term is contained in ontology terms
			if (ont.containsKey(pairs.getKey())) {

				// RegistryTerm ont_term = (RegistryTerm)
				// ont.get(pairs.getKey());
				//
				// // System.out.println("web "+ws_term.getAliasString());
				// // System.out.println("ont "+ont_term.getAliasString());
				//
				// boolean flag = true;
				// String[] parts = ws_term.getAliasString().split("#");
				// for (String s : parts) {
				// if (!ont_term.getAliasString().contains(s))
				// flag = false;
				// }
				//
				// // check if something has changed
				// if (!ws_term.getCheck().equals(ont_term.getCheck()) || !flag)
				// {
				//
				// ws_term.setInd(ont_term.getInd());
				// ws_term.setOntologyURI(ont_term.getOntologyURI());
				//
				// termsForUpdate.add(ws_term);
				// termsForRemove.add(ont_term);
				//
				// }
				//
				// // remove from ontology terms, the checked term
				// // ont.remove(ont_term.getId());
				// checkedTerms.add(ont_term);

			} else {
				// this is a new term
				// check if it is proposed or accepted
				// if (ws_term.getStatus().equals("accepted"))
				termsForInsert.add(ws_term);
				// else
				// proposedTermsForInsert.add(ws_term);
			}

		}

		// it = ont.entrySet().iterator();
		// boolean flag = false;
		// while (it.hasNext()) {
		// Map.Entry pairs = (Map.Entry) it.next();
		// flag = false;
		// for (RegistryTerm term : checkedTerms) {
		// if (pairs.getKey().equals(term.getId())) {
		// flag = true;
		// break;
		// }
		// }
		//
		// if (!flag)
		// termsForRemove.add((RegistryTerm) pairs.getValue());
		//
		// }

	}

	public void compareSolutions(HashMap<String, Solution> ws,
			HashMap<String, Solution> ont) {

		// check if our ids are contained in his ids
		Iterator it1 = ont.entrySet().iterator();
		while (it1.hasNext()) {
			Map.Entry pairs = (Map.Entry) it1.next();

			Solution sol = (Solution) pairs.getValue();

			if (!ws.containsKey(pairs.getKey())) {
				solutionsToPost.add(sol);
			}
		}

		System.out.println("solutions for POST: " + solutionsToPost.size());

		// compare our solutions with his solutions
		boolean flag = true;
		Iterator it = ws.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();

			Solution webSolution = (Solution) pairs.getValue();

			// check if webSolution is contained in ontology solutions
			if (webSolution.getStatus().equals("deleted")) {
				solutionsForRemove.add(webSolution);
			} else if (ont.containsKey(pairs.getKey())) {

				Solution ontSolution = (Solution) ont.get(pairs.getKey());

				// check general information for solution
				flag = ontSolution.testEquals(webSolution);

				// check settings of the two solutions
				// TODO

				// if(flag)
				// for (Setting ontSett : ontSolution.getSettings()) {
				// for (Setting webSett : webSolution.getSettings()) {
				//
				// if (ontSett.getId().equals(webSett.getId())) {
				//
				// if (!ontSett.getName()
				// .equals(webSett.getName())
				// || !ontSett.getMapping().equals(
				// webSett.getMapping())) {
				// flag = false;
				// break;
				// }
				// }
				// }
				//
				// if (!flag)
				// break;
				// }

				if (!flag) {
					solutionsForUpdate.add(webSolution);
					solutionsForRemove.add(ontSolution);
				}

				flag = true;
				// checkedSolutions.add(ontSolution);

			} else {
				// this is a new solution
				solutionsForInsert.add(webSolution);

			}

		}

		System.out.println("solutions to insert: " + solutionsForInsert.size());

	}

	public ArrayList<RegistryTerm> getTermsForInsert() {
		return termsForInsert;
	}

	public void setTermsForInsert(ArrayList<RegistryTerm> termsForInsert) {
		this.termsForInsert = termsForInsert;
	}

	public ArrayList<RegistryTerm> getProposedTermsForInsert() {
		return proposedTermsForInsert;
	}

	public void setProposedTermsForInsert(
			ArrayList<RegistryTerm> proposedTermsForInsert) {
		this.proposedTermsForInsert = proposedTermsForInsert;
	}

	public String getTermsForInsertSize() {
		return String.valueOf(this.getTermsForInsert().size());
	}

	public String getProposedTermsForInsertSize() {
		return String.valueOf(this.getProposedTermsForInsert().size());
	}

	public void synchronizeSolutions() {

		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);

			// get cookies for connections

			String cookiesUL = WebServicesForTerms
					.loginAndGetCookiesWithCurl(WebServicesForSolutions.signInForUnifiedListingApi);
			String cookiesPTD = WebServicesForTerms
					.loginAndGetCookiesWithCurl(WebServicesForTerms.signInForDictionaryTermsApi);

			solutionsForInsert = new ArrayList<Solution>();
			solutionsForUpdate = new ArrayList<Solution>();
			solutionsForRemove = new ArrayList<Solution>();
			solutionsToPost = new ArrayList<Solution>();

			// get hashmap with web service solutions
			webServiceSolutions = WebServicesForSolutions
					.getWebServiceProducts(WebServicesForSolutions.PRODUCTS_URL,cookiesUL);

			HashMap<String, Setting> settings = WebServicesForTerms
					.getWebServiceAliases(WebServicesForTerms.ALIAS_URL,cookiesPTD);

			addWebSettingsToWebSolutions(settings,
					ontologyInstance.getRegistryTerms());

			// create hashmap with all ontology solutions
			ontologySolutions = new HashMap<String, Solution>();
			for (Solution sol : ontologyInstance.getSolutions()) {
				ontologySolutions.put(sol.getId(), sol);
			}

			// check ontology solutions and web service solutions
			compareSolutions(webServiceSolutions, ontologySolutions);

			// POST solutions that are not contained in the Unified Listing

			for (Solution sol : solutionsToPost) {

				if (sol.getId() != null)
					if (!sol.getId().trim().isEmpty()
							&& !sol.getId().contains(
									"com.certh.service.synthesis")) {

						if (sol.getManufacturerCountry().equalsIgnoreCase(
								"Afghanistan"))
							sol.setManufacturerCountry("");

						WebServicesForSolutions.solutionHTTPPostRequest(sol,
								cookiesUL);

						for (Setting sett : sol.getSettings()) {
							if (!sett.getId().trim().isEmpty()
									&& sett.getId() != null) {

								WebServicesForTerms.postSettingWithCurl(
										cookiesPTD, sett,
										"http://ul.gpii.net/api/product/unified/"
												+ sol.getId(), sol.getId());

							}
						}
					}

			}

			// remove old solutions from ontologySolutions
			ArrayList<String> indexesForRemove = new ArrayList<String>();
			for (Solution term : solutionsForRemove) {

				for (Solution temp : ontologyInstance.getSolutions()) {

					if (temp.getId().equals(term.getId())) {
						int i = ontologyInstance.getSolutions().indexOf(temp);
						ontologyInstance.getOldSolutionUris().add(
								temp.getOntologyURI());
						indexesForRemove.add(Integer.toString(i));

						break;
					}
				}
			}

			for (String temp : indexesForRemove) {
				ontologyInstance.getSolutions().remove(Integer.parseInt(temp));
			}

			String solutionURI = "";
			String uri = "";
			// insert updated solutions to ontologySolutions
			for (Solution sol : solutionsForUpdate) {
				uri = sol.getName().trim().replace(" ", "_")
						.replaceAll("[^\\p{L}\\p{Nd}]", "");

				// check if uri exists
				if (Character.isDigit(uri.charAt(0))) {
					uri = "app";
				}
				uri = ontologyInstance.getOntology().changeUri(uri);
				solutionURI = ontologyInstance.getOntology().getNS() + uri;
				sol.setOntologyURI(solutionURI);
				ontologyInstance.getSolutions().add(sol);
			}

			// insert new solutions to ontologySolutions
			for (Solution sol : solutionsForInsert) {
				uri = sol.getName().trim().replace(" ", "_")
						.replaceAll("[^\\p{L}\\p{Nd}]", "");

				// check if uri exists
				if (Character.isDigit(uri.charAt(0))) {
					uri = "app";
				}
				uri = ontologyInstance.getOntology().changeUri(uri);
				solutionURI = ontologyInstance.getOntology().getNS() + uri;
				sol.setOntologyURI(solutionURI);
				ontologyInstance.getSolutions().add(sol);
			}

		} catch (Exception e) {
			System.out.println("error in service!");
		}

	}

	public void addWebSettingsToWebSolutions(HashMap<String, Setting> settings,
			List<RegistryTerm> registryTerms) {
		Iterator it = webServiceSolutions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Solution sol = (Solution) pairs.getValue();

			Iterator it2 = settings.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry pairs2 = (Map.Entry) it2.next();

				Setting sett = (Setting) pairs2.getValue();

				String uluri = "http://ul.gpii.net/api/product/unified/"
						+ sol.getId();
				if (uluri.equalsIgnoreCase(sett.getSolutionId())) {

					for (RegistryTerm term : registryTerms) {
						if (term.getId().equalsIgnoreCase(sett.getMappingId())) {
							sett.setMapping(term.getName());
							break;
						}
					}
					sol.getSettings().add(sett);
				}

			}
		}

	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
