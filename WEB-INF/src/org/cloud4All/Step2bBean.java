package org.cloud4All;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.cloud4All.IPR.DiscountSchema;
import org.cloud4All.IPR.SolutionAccessInfoForUsers;
import org.cloud4All.IPR.SolutionAccessInfoForVendors;
import org.cloud4All.ontology.Ontology;
import org.cloud4All.ontology.OntologyInstance;
import org.primefaces.component.api.UIData;
import org.primefaces.component.slider.Slider;
import org.primefaces.event.SlideEndEvent;

import com.sun.faces.component.visit.FullVisitContext;

@ManagedBean(name = "step2bBean")
@SessionScoped
public class Step2bBean {

	private SolutionAccessInfoForVendors accessInfoForVendors = new SolutionAccessInfoForVendors();
	private SolutionAccessInfoForUsers accessInfoForUsers = new SolutionAccessInfoForUsers();

	private double totalDiscount = 0;
	private double costAfterDiscount = 0;
	private List<String> selectedCountries = new ArrayList<String>();
	private List<String> countriesList = new ArrayList<String>();
	private UIData uiData;
	private OntologyInstance ontologyInstance;
	private List<Solution> solutionsListAsStrings = new ArrayList<Solution>();

	public Step2bBean() {
		super();
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ontologyInstance = (OntologyInstance) context.getApplication()
					.evaluateExpressionGet(context, "#{ontologyInstance}",
							OntologyInstance.class);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void calculateSolutions() {
		solutionsListAsStrings = new ArrayList<Solution>();
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean = (UserBean) context.getApplication()
				.evaluateExpressionGet(context, "#{userBean}", UserBean.class);
		for (int i = 0; i < ontologyInstance.getSolutions().size(); i++) {
			if (userBean.getVendorObj().getOntologyURI()
					.equals(ontologyInstance.getSolutions().get(i).getVendor()))
				solutionsListAsStrings.add(ontologyInstance.getSolutions()
						.get(i).cloneSolution());
		}
		Solution sol = new Solution();
		sol.setOntologyURI("");
		sol.setName("None");
		solutionsListAsStrings.add(0, sol);
	}

	public void addDiscountSchema() {
		try {
			DiscountSchema discountSchema = new DiscountSchema();
			calculateSolutions();

			for (int i = 0; i < solutionsListAsStrings.size(); i++) {
				if (solutionsListAsStrings.get(i) != null)
					discountSchema.getSolutionsListAsStrings().add(
							solutionsListAsStrings.get(i));
				discountSchema.getSelectItems().add(
						new SelectItem(solutionsListAsStrings.get(i),
								solutionsListAsStrings.get(i).getName()));
			}
			accessInfoForVendors.getCommercialCostSchema()
					.getDiscountIfUsedWithOtherSolution().add(discountSchema);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addDiscountSchemaForUsers() {
		try {
			DiscountSchema discountSchema = new DiscountSchema();
			calculateSolutions();

			for (int i = 0; i < solutionsListAsStrings.size(); i++) {
				if (solutionsListAsStrings.get(i) != null)
					discountSchema.getSolutionsListAsStrings().add(
							solutionsListAsStrings.get(i));
			}
			accessInfoForUsers.getCommercialCostSchema()
					.getDiscountIfUsedWithOtherSolution().add(discountSchema);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static ValueExpression createValueExpression(
			String valueExpression, Class<?> valueType) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(context.getELContext(), valueExpression,
						valueType);
	}

	public UIComponent findComponent(final String id) {
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot root = context.getViewRoot();
		final UIComponent[] found = new UIComponent[1];
		root.visitTree(new FullVisitContext(context), new VisitCallback() {
			public VisitResult visit(VisitContext context, UIComponent component) {
				if (component.getId().equals(id)) {
					found[0] = component;
					return VisitResult.COMPLETE;
				}
				return VisitResult.ACCEPT;
			}
		});
		return found[0];
	}

	public void nextPressed() {
		FacesContext context = FacesContext.getCurrentInstance();
		step3Bean step3Bean = (step3Bean) context
				.getApplication()
				.evaluateExpressionGet(context, "#{step3Bean}", step3Bean.class);

		step3Bean.setNewSetting(new Setting());

	}

	/**
	 * @return the accessInfoForVendors
	 */
	public SolutionAccessInfoForVendors getAccessInfoForVendors() {
		return accessInfoForVendors;
	}

	/**
	 * @param accessInfoForVendors
	 *            the accessInfoForVendors to set
	 */
	public void setAccessInfoForVendors(
			SolutionAccessInfoForVendors accessInfoForVendors) {
		this.accessInfoForVendors = accessInfoForVendors;
	}

	/**
	 * @return the solutionsListAsStrings
	 */
	public List<Solution> getSolutionsListAsStrings() {

		return solutionsListAsStrings;
	}

	/**
	 * @param solutionsListAsStrings
	 *            the solutionsListAsStrings to set
	 */
	public void setSolutionsListAsStrings(List<Solution> solutionsListAsStrings) {
		this.solutionsListAsStrings = solutionsListAsStrings;
	}

	/**
	 * @return the totalDiscount
	 */
	public double getTotalDiscount() {
		return totalDiscount;
	}

	/**
	 * @param totalDiscount
	 *            the totalDiscount to set
	 */
	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	/**
	 * @return the costAfterDiscount
	 */
	public double getCostAfterDiscount() {
		return costAfterDiscount;
	}

	/**
	 * @param costAfterDiscount
	 *            the costAfterDiscount to set
	 */
	public void setCostAfterDiscount(double costAfterDiscount) {
		this.costAfterDiscount = costAfterDiscount;
	}

	/**
	 * @return the selectedCountries
	 */
	public List<String> getSelectedCountries() {
		return selectedCountries;
	}

	/**
	 * @param selectedCountries
	 *            the selectedCountries to set
	 */
	public void setSelectedCountries(List<String> selectedCountries) {
		this.selectedCountries = selectedCountries;
	}

	/**
	 * @return the countriesList
	 */
	public List<String> getCountriesList() {
		return countriesList;
	}

	/**
	 * @param countriesList
	 *            the countriesList to set
	 */
	public void setCountriesList(List<String> countriesList) {
		this.countriesList = countriesList;
	}

	/**
	 * @return the accessInfoForUsers
	 */
	public SolutionAccessInfoForUsers getAccessInfoForUsers() {
		return accessInfoForUsers;
	}

	/**
	 * @param accessInfoForUsers
	 *            the accessInfoForUsers to set
	 */
	public void setAccessInfoForUsers(
			SolutionAccessInfoForUsers accessInfoForUsers) {
		this.accessInfoForUsers = accessInfoForUsers;
	}

	/**
	 * @return the uiData
	 */
	public UIData getUiData() {
		return uiData;
	}

	/**
	 * @param uiData
	 *            the uiData to set
	 */
	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

}
