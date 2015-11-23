package org.cloud4All;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.cloud4All.IPR.EULA;
import org.cloud4All.IPR.SLA;
import org.cloud4All.IPR.SolutionUserSchema;
import org.cloud4All.IPR.Vendor;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyInstance;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.model.User;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean {

	private boolean user = false;
	private boolean vendor = false;
	private boolean admin = false;
	private Vendor vendorObj = null;
	private OntologyInstance ontologyInstance;
	private String userURI = "";

	public UserBean() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);
			User currentUser = LiferayFacesContext.getInstance().getUser();
			for (int i = 0; i < currentUser.getRoles().size(); i++) {
				if (currentUser.getRoles().get(i).getName()
						.equals("ApplicationUser")) {
					user = true;

				} else if (currentUser.getRoles().get(i).getName()
						.equals("ApplicationVendor")) {
					vendor = true;

					// load vendor object

					vendorObj = ontologyInstance.getOntology().loadVendor(
							currentUser.getEmailAddress());

					if (vendorObj == null) {
						// add vendor instance to ontology
						ontologyInstance.getOntology().addVendor(currentUser);
						vendorObj = ontologyInstance.getOntology().loadVendor(
								currentUser.getEmailAddress());
					}
					userURI = vendorObj.getOntologyURI();
					// load all vendor's solutions
					for (int j = 0; j < ontologyInstance.getSolutions().size(); j++) {
						if (vendorObj.getOntologyURI().equals(
								ontologyInstance.getSolutions().get(j)
										.getVendor())) {
							vendorObj.getVendorOf().add(
									ontologyInstance.getSolutions().get(j));
						}
					}
					// load all slas
					for (int j = 0; j < ontologyInstance.getSolutions().size(); j++) {
						for (int k = 0; k < ontologyInstance.getSolutions()
								.get(j).getSLAs().size(); k++) {
							SLA sla = ontologyInstance.getSolutions().get(j)
									.getSLAs().get(k);
							if (vendorObj.getOntologyURI().equals(
									sla.getVendorOfAgreement())) {
								vendorObj.getSlasAsVendor().add(sla);
							}
							if (vendorObj.getOntologyURI().equals(
									sla.getOwnerOfAgreement())) {
								vendorObj.getSlasAsOwner().add(sla);
							}
						}
					}
					// load eulas' applications and users
					for (int j = 0; j < vendorObj.getEulas().size(); j++) {
						EULA eula = vendorObj.getEulas().get(j);
						for (int k = 0; k < ontologyInstance
								.getSolutionUserSchemas().size(); k++) {
							SolutionUserSchema schema = ontologyInstance
									.getSolutionUserSchemas().get(k);
							if (eula.getOntologyURI().equals(
									schema.getEula().getOntologyURI())) {
								eula.setRefersToSolution(ontologyInstance
										.getOntology().getSolutionNameByURI(
												schema.getRefersToSolution()));
								eula.setUser(schema.getUserId());
							}
						}

					}

				} else if (currentUser.getRoles().get(i).getName()
						.equals("Administrator")) {
					admin = true;

				}
			}
//			if (!user && !vendor) {
//				user = true;
//			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public OntologyInstance getOntologyInstance() {
		return ontologyInstance;
	}

	public void setOntologyInstance(OntologyInstance ontologyInstance) {
		this.ontologyInstance = ontologyInstance;
	}

	public void saveVendor() {
		ontologyInstance.getOntology().saveVendor(vendorObj);
	}

	/**
	 * @return the user
	 */
	public boolean isUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(boolean user) {
		this.user = user;
	}

	/**
	 * @return the vendor
	 */
	public boolean isVendor() {
		return vendor;
	}

	/**
	 * @param vendor
	 *            the vendor to set
	 */
	public void setVendor(boolean vendor) {
		this.vendor = vendor;
	}

	/**
	 * @return the vendorObj
	 */
	public Vendor getVendorObj() {
		return vendorObj;
	}

	/**
	 * @param vendorObj
	 *            the vendorObj to set
	 */
	public void setVendorObj(Vendor vendorObj) {
		this.vendorObj = vendorObj;
	}

}
