package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.cloud4All.Solution;

import com.google.gson.JsonObject;

public class DiscountSchema {

	private int discount = 0;
	private Solution pairedSolution = new Solution();
	private String pairedSolutionString = "";
	private String discountReason = "";
	private List<Solution> solutionsListAsStrings = new ArrayList<Solution>();
	private List<SelectItem> selectItems = new ArrayList<SelectItem>();
	private Solution selectedItem;

	public DiscountSchema() {

	}

	public List<SelectItem> getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(List<SelectItem> selectItems) {
		this.selectItems = selectItems;
	}

	public Solution getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Solution selectedItem) {
		this.selectedItem = selectedItem;
	}

	/**
	 * @return the discount
	 */
	public int getDiscount() {
		return discount;
	}

	/**
	 * @param discount
	 *            the discount to set
	 */
	public void setDiscount(int discount) {
		this.discount = discount;
	}

	/**
	 * @return the pairedSolution
	 */
	public Solution getPairedSolution() {
		return pairedSolution;
	}

	/**
	 * @param pairedSolution
	 *            the pairedSolution to set
	 */
	public void setPairedSolution(Solution pairedSolution) {
		this.pairedSolution = pairedSolution;

	}

	/**
	 * @return the discountReason
	 */
	public String getDiscountReason() {
		return discountReason;
	}

	/**
	 * @param discountReason
	 *            the discountReason to set
	 */
	public void setDiscountReason(String discountReason) {
		this.discountReason = discountReason;
	}

	public String getPairedSolutionString() {
		return pairedSolutionString;
	}

	public void setPairedSolutionString(String pairedSolutionString) {
		this.pairedSolutionString = pairedSolutionString;
	}

	public DiscountSchema clone() {
		DiscountSchema discountSchema = new DiscountSchema();
		discountSchema.setDiscount(this.getDiscount());
		discountSchema.setDiscountReason(this.getDiscountReason());
		// we do not clone solution because it will cause an infinite loop
		// discountSchema.setPairedSolution(pairedSolution)
		discountSchema.setPairedSolutionString(this.getPairedSolutionString());
		return discountSchema;

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
	
	public JsonObject getJsonSchema(){
		JsonObject schema = new JsonObject();
		schema.addProperty("Discount", this.getDiscount());
		schema.addProperty("Paired_Solution", this.getPairedSolutionString());
		schema.addProperty("Discount_Reason", this.getDiscountReason());
		return schema;
	}

}
