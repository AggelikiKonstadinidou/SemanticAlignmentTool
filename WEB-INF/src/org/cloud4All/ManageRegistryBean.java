package org.cloud4All;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyInstance;
import org.cloud4All.ontology.RegistryTerm;
import org.cloud4All.ontology.WebServicesForTerms;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.hp.hpl.jena.ontology.impl.IndividualImpl;

@ManagedBean(name = "manageRegistryBean")
@SessionScoped
public class ManageRegistryBean {

	private RegistryTerm selectedRegistryTerm = new RegistryTerm();
	private RegistryTerm selectedProposedRegistryTerm = new RegistryTerm();
	private RegistryTerm newRegistryTerm = new RegistryTerm();
	private List<String> registryTypes = new ArrayList<String>();
	private RegistryTerm selectedRegistryTermClone = new RegistryTerm();
	private RegistryTerm selectedProposedRegistryTermClone = new RegistryTerm();
	private List<String> allRegistryTerms = new ArrayList<String>();
	private OntologyInstance ontologyInstance;

	public ManageRegistryBean() {
		super();

		FacesContext context2 = FacesContext.getCurrentInstance();
		ontologyInstance = (OntologyInstance) context2.getApplication()
				.evaluateExpressionGet(context2, "#{ontologyInstance}",
						OntologyInstance.class);

		registryTypes.add("string");
		registryTypes.add("boolean");
		registryTypes.add("integer");
		registryTypes.add("double");

	}

	public RegistryTerm getSelectedRegistryTerm() {

		return selectedRegistryTerm;

	}

	public void setSelectedRegistryTerm(RegistryTerm selectedRegistryTerm) {
		this.selectedRegistryTermClone = selectedRegistryTerm.clone();
		this.selectedRegistryTerm = selectedRegistryTerm;
		this.selectedProposedRegistryTerm = new RegistryTerm();
		this.selectedProposedRegistryTermClone = new RegistryTerm();

	}

	public void saveRegistryTerm() {
		try {

			// check if it has empty values
			if (this.selectedRegistryTermClone.getName().trim().equals("")) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please provide the registry term name", ""));

			}
			if (this.selectedRegistryTermClone.getId().trim().equals("")) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please provide the registry term id", ""));

			}
			if (this.selectedRegistryTermClone.getDescription().trim()
					.equals("")) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please provide the registry term description", ""));

			}
			if (this.selectedRegistryTermClone.getValueSpace().trim()
					.equals("")) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please provide the registry term value space", ""));

			}
			if (this.selectedRegistryTermClone.getDefaultValue().trim()
					.equals("")) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages2", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please provide the registry term default value", ""));

			}
			if (this.selectedRegistryTermClone.getName().trim().equals("")
					|| this.selectedRegistryTermClone.getDescription().trim()
							.equals("")
					|| this.selectedRegistryTermClone.getValueSpace().trim()
							.equals("")
					|| this.selectedRegistryTermClone.getDefaultValue().trim()
							.equals("")
					|| this.selectedRegistryTermClone.getId().trim().equals("")) {
				return;
			}

			// check if default value matches with type
			if (this.selectedRegistryTermClone.getType().equals("integer")) {
				try {
					Integer.parseInt(this.selectedRegistryTermClone
							.getDefaultValue().trim());
				} catch (Exception ex) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage("messages2", new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "The default value \""
									+ this.selectedRegistryTermClone
											.getDefaultValue().trim()
									+ "\" is not of type \""
									+ this.selectedRegistryTermClone.getType()
									+ "\"", ""));
					return;
				}
			} else if (this.selectedRegistryTermClone.getType().equals(
					"boolean")) {
				try {
					if (!this.selectedRegistryTermClone.getDefaultValue()
							.trim().toLowerCase().equals("true")
							&& !this.selectedRegistryTermClone
									.getDefaultValue().trim().toLowerCase()
									.equals("false")) {
						FacesContext context = FacesContext
								.getCurrentInstance();
						context.addMessage(
								"messages2",
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"The default value \""
												+ this.selectedRegistryTermClone
														.getDefaultValue()
														.trim()
												+ "\" is not of type \""
												+ this.selectedRegistryTermClone
														.getType() + "\"", ""));
						return;
					}

				} catch (Exception ex) {

				}
			} else if (this.selectedRegistryTermClone.getType()
					.equals("double")) {
				try {
					Double.parseDouble(this.selectedRegistryTermClone
							.getDefaultValue().trim());
				} catch (Exception ex) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage("messages2", new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "The default value \""
									+ this.selectedRegistryTermClone
											.getDefaultValue().trim()
									+ "\" is not of type \""
									+ this.selectedRegistryTermClone.getType()
									+ "\"", ""));
					return;
				}
			}

			// get all registry terms
			List<RegistryTerm> list1 = ontologyInstance.getRegistryTerms();
			List<RegistryTerm> list2 = ontologyInstance
					.getProposedRegistryTerms();

			for (int i = 0; i < list1.size(); i++) {
				if (list1
						.get(i)
						.getId()
						.trim()
						.equalsIgnoreCase(
								selectedRegistryTermClone.getId().trim())
						&& !selectedRegistryTerm
								.getId()
								.trim()
								.equals(selectedRegistryTermClone.getId()
										.trim())) {

					FacesContext context = FacesContext.getCurrentInstance();

					context.addMessage("messages2", new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"This registry term id already exists", ""));
					return;
				}
			}
			for (int i = 0; i < list2.size(); i++) {
				if (list2
						.get(i)
						.getId()
						.trim()
						.equalsIgnoreCase(
								selectedRegistryTermClone.getId().trim())
						&& !list2.get(i).getOntologyURI()
								.equals(selectedRegistryTerm.getOntologyURI())) {

					FacesContext context = FacesContext.getCurrentInstance();

					context.addMessage("messages2", new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"This registry term id already exists", ""));

					return;
				}
			}

			// TODO update in web service
			// WebServicesForTerms
			// .updateRegistryTernHTTP(selectedRegistryTermClone);

			int i = ontologyInstance.getRegistryTerms().indexOf(
					selectedRegistryTerm);
			ontologyInstance.getRegistryTerms().remove(selectedRegistryTerm);
			selectedRegistryTermClone.setEdit(true);
			ontologyInstance.getRegistryTerms().add(i,
					selectedRegistryTermClone);
			// update aliases list of other terms if name or id changed
			if (!selectedRegistryTerm.getName().equals(
					selectedRegistryTermClone.getName())
					|| !selectedRegistryTerm.getId().equals(
							selectedRegistryTermClone.getId())) {

				String news = selectedRegistryTermClone.getName() + "(ID = "
						+ selectedRegistryTermClone.getId() + ")";
				String name = "undefined name";
				if (!selectedRegistryTerm.getName().isEmpty())
					name = selectedRegistryTerm.getName();

				String olds = name + "(ID = " + selectedRegistryTerm.getId()
						+ ")";

				for (RegistryTerm term : ontologyInstance.getRegistryTerms()) {
					List<String> aliases = (List<String>) ((ArrayList) term
							.getAlias()).clone();
					for (String string : aliases) {
						if (string.equals(olds)) {
							term.getAlias().remove(olds);
							term.getAlias().add(news);
							break;
						}
					}
				}

				for (RegistryTerm term : ontologyInstance
						.getProposedRegistryTerms()) {
					List<String> aliases = (List<String>) ((ArrayList) term
							.getAlias()).clone();
					for (String string : aliases) {
						if (string.equals(olds)) {
							term.getAlias().remove(olds);
							term.getAlias().add(news);
							break;
						}
					}
				}

			}

			RequestContext rc = RequestContext.getCurrentInstance();
			rc.execute("registryDialog.hide()");
			rc.update("form");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saveRegistryTerm2() {
		// check if it has empty values
		if (this.selectedProposedRegistryTermClone.getName().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term name", ""));

		}
		if (this.selectedProposedRegistryTermClone.getDescription().trim()
				.equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term description", ""));

		}
		if (this.selectedProposedRegistryTermClone.getValueSpace().trim()
				.equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term value space", ""));

		}
		if (this.selectedProposedRegistryTermClone.getDefaultValue().trim()
				.equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages3", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term default value", ""));

		}
		if (this.selectedProposedRegistryTermClone.getName().trim().equals("")
				|| this.selectedProposedRegistryTermClone.getDescription()
						.trim().equals("")
				|| this.selectedProposedRegistryTermClone.getValueSpace()
						.trim().equals("")
				|| this.selectedProposedRegistryTermClone.getDefaultValue()
						.trim().equals("")
				|| this.selectedProposedRegistryTermClone.getId().trim()
						.equals("")) {
			return;
		}
		// get all registry terms
		List<RegistryTerm> list1 = ontologyInstance.getRegistryTerms();
		List<RegistryTerm> list2 = ontologyInstance.getProposedRegistryTerms();

		for (int i = 0; i < list1.size(); i++) {
			if (list1
					.get(i)
					.getId()
					.trim()
					.equalsIgnoreCase(
							selectedProposedRegistryTermClone.getId().trim())
					&& !selectedProposedRegistryTerm
							.getId()
							.trim()
							.equals(selectedProposedRegistryTermClone.getId()
									.trim())) {

				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages3", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term id already exists", ""));
				return;
			}
		}
		for (int i = 0; i < list2.size(); i++) {
			if (list2
					.get(i)
					.getId()
					.trim()
					.equalsIgnoreCase(
							selectedProposedRegistryTermClone.getId().trim())
					&& !list2
							.get(i)
							.getOntologyURI()
							.equals(selectedProposedRegistryTerm
									.getOntologyURI())) {

				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages3", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term id already exists", ""));

				return;
			}
		}

		// TODO update in web service
		// WebServicesForTerms
		// .updateRegistryTernHTTP(selectedProposedRegistryTermClone);

		int i = ontologyInstance.getProposedRegistryTerms().indexOf(
				selectedProposedRegistryTerm);
		ontologyInstance.getProposedRegistryTerms().remove(
				selectedProposedRegistryTerm);
		selectedProposedRegistryTermClone.setEdit(true);
		ontologyInstance.getProposedRegistryTerms().add(i,
				selectedProposedRegistryTermClone);

		if (!selectedProposedRegistryTerm.getName().equals(
				selectedProposedRegistryTermClone.getName())
				|| !selectedProposedRegistryTerm.getId().equals(
						selectedProposedRegistryTermClone.getId())) {

			String news = selectedProposedRegistryTermClone.getName()
					+ "(ID = " + selectedProposedRegistryTermClone.getId()
					+ ")";
			String name = "undefined name";
			if (!selectedProposedRegistryTerm.getName().isEmpty())
				name = selectedProposedRegistryTerm.getName();

			String olds = name + "(ID = "
					+ selectedProposedRegistryTerm.getId() + ")";

			for (RegistryTerm term : ontologyInstance.getRegistryTerms()) {
				List<String> aliases = (List<String>) ((ArrayList) term
						.getAlias()).clone();
				for (String string : aliases) {
					if (string.equals(olds)) {
						term.getAlias().remove(olds);
						term.getAlias().add(news);
						break;
					}
				}
			}

			for (RegistryTerm term : ontologyInstance
					.getProposedRegistryTerms()) {
				List<String> aliases = (List<String>) ((ArrayList) term
						.getAlias()).clone();
				for (String string : aliases) {
					if (string.equals(olds)) {
						term.getAlias().remove(olds);
						term.getAlias().add(news);
						break;
					}
				}
			}

		}

		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("registryDialog2.hide()");
		rc.update("form");
	}

	public RegistryTerm getSelectedProposedRegistryTerm() {

		return selectedProposedRegistryTerm;

	}

	public void setSelectedProposedRegistryTerm(
			RegistryTerm selectedProposedRegistryTerm) {
		this.selectedProposedRegistryTermClone = selectedProposedRegistryTerm
				.clone();
		this.selectedProposedRegistryTerm = selectedProposedRegistryTerm;
		this.selectedRegistryTerm = new RegistryTerm();
		this.selectedRegistryTermClone = new RegistryTerm();
	}

	public void deleteRegistryTerm() {
		try {

			ontologyInstance.getRegistryTerms().remove(selectedRegistryTerm);

			String s = "";
			if (!selectedRegistryTerm.getName().isEmpty())
				s = s.concat(selectedRegistryTerm.getName() + "(ID = ");
			else
				s = s.concat("undefined name" + "(ID = ");

			s = s.concat(selectedRegistryTerm.getId() + ")");
			// remove deleted term from aliases list of other terms
			for (RegistryTerm term : ontologyInstance.getRegistryTerms()) {
				List<String> aliases = (List<String>) ((ArrayList) term
						.getAlias()).clone();
				for (String string : aliases) {
					if (string.equals(s)) {
						term.getAlias().remove(s);
						break;
					}
				}
			}

			for (RegistryTerm term : ontologyInstance
					.getProposedRegistryTerms()) {
				List<String> aliases = (List<String>) ((ArrayList) term
						.getAlias()).clone();
				for (String string : aliases) {
					if (string.equals(s)) {
						term.getAlias().remove(s);
						break;
					}
				}
			}

			// TODO delete registry term from web service
//			String cookies = WebServicesForTerms.loginAndGetCookiesWithCurl();
//			WebServicesForTerms.deleteRecordWithCurl(cookies,
//					selectedRegistryTerm.getId());
			// update solutions
			for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
				for (int j = 0; j < ontologyInstance.getSolutions().get(i)
						.getSettings().size(); j++) {
					Setting set = ontologyInstance.getSolutions().get(i)
							.getSettings().get(j);
					if (set.getMapping().equals(selectedRegistryTerm.getName())) {
						set.setMapping("");
						set.setHasMapping(false);
						set.setExactMatching(false);
						set.setComments("");
						for (int k = set.getSortedMappings().size() - 1; k >= 0; k--) {
							if (set.getSortedMappings().get(k)
									.equals(selectedRegistryTerm.getName())) {
								set.getSortedMappings().remove(k);
							}
						}
					}
				}
			}
			selectedRegistryTerm = new RegistryTerm();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void deleteRegistryTerm2() throws IOException {

		ontologyInstance.getProposedRegistryTerms().remove(
				selectedProposedRegistryTerm);
		String s = "";
		if (!selectedProposedRegistryTerm.getName().isEmpty())
			s = s.concat(selectedProposedRegistryTerm.getName() + "(ID = ");
		else
			s = s.concat("undefined name" + "(ID = ");

		s = s.concat(selectedProposedRegistryTerm.getId() + ")");
		// remove deleted term from aliases list of other terms
		for (RegistryTerm term : ontologyInstance.getRegistryTerms()) {
			List<String> aliases = (List<String>) ((ArrayList) term.getAlias())
					.clone();
			for (String string : aliases) {
				if (string.equals(s)) {
					term.getAlias().remove(s);
					break;
				}
			}
		}

		for (RegistryTerm term : ontologyInstance.getProposedRegistryTerms()) {
			List<String> aliases = (List<String>) ((ArrayList) term.getAlias())
					.clone();
			for (String string : aliases) {
				if (string.equals(s)) {
					term.getAlias().remove(s);
					break;
				}
			}
		}

		// TODO delete registry term from web service
//		String cookies = WebServicesForTerms.loginAndGetCookiesWithCurl();
//		WebServicesForTerms.deleteRecordWithCurl(cookies,
//				selectedProposedRegistryTerm.getId());
		

		// update solutions
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			for (int j = 0; j < ontologyInstance.getSolutions().get(i)
					.getSettings().size(); j++) {
				Setting set = ontologyInstance.getSolutions().get(i)
						.getSettings().get(j);
				if (set.getMapping().equals(
						selectedProposedRegistryTerm.getName())) {
					set.setMapping("");
					set.setHasMapping(false);
					set.setExactMatching(false);
					set.setComments("");
					for (int k = set.getSortedMappings().size() - 1; k >= 0; k--) {
						if (set.getSortedMappings().get(k)
								.equals(selectedProposedRegistryTerm.getName())) {
							set.getSortedMappings().remove(k);
						}
					}
				}
			}
		}
		selectedProposedRegistryTerm = new RegistryTerm();
	}

	public void acceptProposedRegistryTerm() throws IOException {

		ontologyInstance.getProposedRegistryTerms().remove(
				selectedProposedRegistryTerm);
		selectedProposedRegistryTermClone = selectedProposedRegistryTerm
				.clone();
		selectedProposedRegistryTermClone.setEdit(true);
		selectedProposedRegistryTermClone.setStatus("accepted");
		ontologyInstance.getRegistryTerms().add(
				selectedProposedRegistryTermClone);
		// TODO update in web service
//		String cookies = WebServicesForTerms.loginAndGetCookiesWithCurl();
//		WebServicesForTerms.postRegistryTermWithCurl(cookies,
//				selectedProposedRegistryTermClone);

	}

	public RegistryTerm getNewRegistryTerm() {
		if (newRegistryTerm != null)
			return newRegistryTerm;
		else
			return new RegistryTerm();
	}

	public void setNewRegistryTerm(RegistryTerm newRegistryTerm) {
		this.newRegistryTerm = newRegistryTerm;
	}

	public void addNewRegistryTerm(ActionEvent event) {
		newRegistryTerm = new RegistryTerm();
		newRegistryTerm.setDefaultValue("");
		newRegistryTerm.setDescription("");
		newRegistryTerm.setName("");
		newRegistryTerm.setType("");
		newRegistryTerm.setValueSpace("");
		newRegistryTerm.setStatus("accepted");

	}

	public void addRegistryTerm(ActionEvent event) throws IOException {
		// check if it has empty values
		if (this.newRegistryTerm.getName().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term name", ""));

		}
		if (this.newRegistryTerm.getId().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term id", ""));

		}

		if (this.newRegistryTerm.getDescription().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term description", ""));

		}
		if (this.newRegistryTerm.getValueSpace().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term value space", ""));

		}
		if (this.newRegistryTerm.getDefaultValue().trim().equals("")) {
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage("messages", new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Please provide the registry term default value", ""));

		}
		if (this.newRegistryTerm.getName().trim().equals("")
				|| this.newRegistryTerm.getDescription().trim().equals("")
				|| this.newRegistryTerm.getValueSpace().trim().equals("")
				|| this.newRegistryTerm.getDefaultValue().trim().equals("")
				|| this.newRegistryTerm.getId().trim().equals("")) {
			return;
		}
		// check if default value matches with type
		if (this.newRegistryTerm.getType().equals("integer")) {
			try {
				Integer.parseInt(this.newRegistryTerm.getDefaultValue().trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(
						"messages",
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"The default value \""
										+ this.newRegistryTerm
												.getDefaultValue().trim()
										+ "\" is not of type \""
										+ this.newRegistryTerm.getType() + "\"",
								""));
				return;
			}
		} else if (this.newRegistryTerm.getType().equals("boolean")) {
			try {
				if (!this.newRegistryTerm.getDefaultValue().trim()
						.toLowerCase().equals("true")
						&& !this.newRegistryTerm.getDefaultValue().trim()
								.toLowerCase().equals("false")) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage("messages",
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"The default value \""
											+ this.newRegistryTerm
													.getDefaultValue().trim()
											+ "\" is not of type \""
											+ this.newRegistryTerm.getType()
											+ "\"", ""));
					return;
				}

			} catch (Exception ex) {

			}
		} else if (this.newRegistryTerm.getType().equals("double")) {
			try {
				Double.parseDouble(this.newRegistryTerm.getDefaultValue()
						.trim());
			} catch (Exception ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(
						"messages",
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"The default value \""
										+ this.newRegistryTerm
												.getDefaultValue().trim()
										+ "\" is not of type \""
										+ this.newRegistryTerm.getType() + "\"",
								""));
				return;
			}
		}

		// get all registry terms
		List<RegistryTerm> list = ontologyInstance.getRegistryTerms();
		List<RegistryTerm> list2 = ontologyInstance.getProposedRegistryTerms();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId()
					.equalsIgnoreCase(newRegistryTerm.getId().trim())) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term id already exists", ""));

				return;
			}
		}

		for (int i = 0; i < list2.size(); i++) {
			if (list2.get(i).getId()
					.equalsIgnoreCase(newRegistryTerm.getId().trim())) {
				FacesContext context = FacesContext.getCurrentInstance();

				context.addMessage("messages", new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"This registry term id already exists", ""));

				return;
			}
		}

		ontologyInstance.getRegistryTerms().add(newRegistryTerm);

		// add registry term
//		String cookies = WebServicesForTerms.loginAndGetCookiesWithCurl();
//		WebServicesForTerms.postRegistryTermWithCurl(cookies,
//				newRegistryTerm);

		newRegistryTerm = new RegistryTerm();
		RequestContext rc = RequestContext.getCurrentInstance();
		rc.execute("addNewRegistryTermDialog.hide()");
	}

	public void openRegistryDialog() {
		selectedRegistryTermClone = selectedRegistryTerm.clone();
	}

	public void openProposedRegistryDialog() {

		selectedProposedRegistryTermClone = selectedProposedRegistryTerm
				.clone();

	}

	public void reCalculateRegistryTerms() {
		ontologyInstance.setRegistryTerms(ontologyInstance.getOntology()
				.getInstancesOfTerms("Registry"));
		ontologyInstance.setProposedRegistryTerms(ontologyInstance
				.getOntology().getInstancesOfTerms("ProposedRegistryTerms"));

	}

	public List<String> getRegistryTypes() {
		return registryTypes;
	}

	public void setRegistryTypes(List<String> registryTypes) {
		this.registryTypes = registryTypes;
	}

	public RegistryTerm getSelectedRegistryTermClone() {

		return selectedRegistryTermClone;
	}

	public void setSelectedRegistryTermClone(
			RegistryTerm selectedRegistryTermClone) {

		this.selectedRegistryTermClone = selectedRegistryTermClone;
	}

	/**
	 * @return the selectedProposedRegistryTermClone
	 */
	public RegistryTerm getSelectedProposedRegistryTermClone() {
		return selectedProposedRegistryTermClone;
	}

	/**
	 * @param selectedProposedRegistryTermClone
	 *            the selectedProposedRegistryTermClone to set
	 */
	public void setSelectedProposedRegistryTermClone(
			RegistryTerm selectedProposedRegistryTermClone) {
		this.selectedProposedRegistryTermClone = selectedProposedRegistryTermClone;
	}

	/**
	 * @return the allRegistryTerms
	 */
	public List<String> getAllRegistryTerms() {
		allRegistryTerms = new ArrayList<String>();
		// add proposed and accepted general terms
		for (RegistryTerm term : ontologyInstance.getRegistryTerms()) {
			String name = term.getName();
			if (term.getName().isEmpty())
				name = "undefined name";
			allRegistryTerms.add(name + "(ID = " + term.getId() + ")");
		}
		for (RegistryTerm term : ontologyInstance.getProposedRegistryTerms()) {
			String name = term.getName();
			if (term.getName().isEmpty())
				name = "undefined name";
			allRegistryTerms.add(name + "(ID = " + term.getId() + ")");
		}
		// add 'ALIAS' terms
		for (RegistryTerm term : ontologyInstance.getRegistryAliases()) {
			String name = term.getName();
			if (term.getName().isEmpty())
				name = "undefined name";
			allRegistryTerms.add(name + "(ID = " + term.getId() + ")");
		}

		for (RegistryTerm term : ontologyInstance.getProposedRegistryAliases()) {
			String name = term.getName();
			if (term.getName().isEmpty())
				name = "undefined name";
			allRegistryTerms.add(name + "(ID = " + term.getId() + ")");
		}

		String removeItem = "";
		RegistryTerm temp = new RegistryTerm();
		if (!selectedRegistryTermClone.getId().isEmpty()
				&& selectedRegistryTermClone.getId() != null)
			temp = selectedRegistryTermClone.clone();
		else if (!selectedProposedRegistryTermClone.getId().isEmpty()
				&& selectedProposedRegistryTermClone.getId() != null)
			temp = selectedProposedRegistryTermClone.clone();

		if (!temp.getName().isEmpty())
			removeItem = removeItem.concat(temp.getName() + "(ID = ");
		else
			removeItem = removeItem.concat("undefined name" + "(ID = ");

		removeItem = removeItem.concat(temp.getId() + ")");

		allRegistryTerms.remove(removeItem);

		Collections.sort(allRegistryTerms);
		return allRegistryTerms;
	}

	/**
	 * @param allRegistryTerms
	 *            the allRegistryTerms to set
	 */
	public void setAllRegistryTerms(List<String> allRegistryTerms) {
		this.allRegistryTerms = allRegistryTerms;
	}

}
