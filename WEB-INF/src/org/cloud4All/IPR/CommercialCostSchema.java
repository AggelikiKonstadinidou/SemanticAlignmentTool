package org.cloud4All.IPR;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CommercialCostSchema {
	private String commercialCostCurrency = "";
	private String costPaymentChargeType = "";
	private float commercialCost = 0;
	private SolutionTrialSchema trialSchema = new SolutionTrialSchema();
	private List<DiscountSchema> discountIfUsedWithOtherSolution = new ArrayList<DiscountSchema>();
	
	public JsonObject getJsonSchema(){
		JsonObject commercialCostSchema = new JsonObject();
		
		boolean flag = false;
		
		if(this.getCommercialCostCurrency()!=null && this.getCostPaymentChargeType()!=null){
		if (!this.getCommercialCostCurrency().isEmpty()
				|| !this.getCostPaymentChargeType().isEmpty()
				|| this.getCommercialCost() != 0)
			flag = true;
		}
		if (flag) {

			commercialCostSchema.addProperty("Commercial_Cost_Currency",
					this.getCommercialCostCurrency());
			commercialCostSchema.addProperty("Cost_Payment_Charge_Type",
					this.getCostPaymentChargeType());
			commercialCostSchema.addProperty("Commercial_Cost",
					this.getCommercialCost());
		}

		JsonObject solutionTrialSchema = this.getTrialSchema().getJsonSchema();

		if (solutionTrialSchema != null) {
			commercialCostSchema.add("Solution_Trial_Schema",
					solutionTrialSchema);
			flag = true;
		}

		JsonArray discountSchemas = new JsonArray();
		for (DiscountSchema temp : this.getDiscountIfUsedWithOtherSolution()) {
			JsonObject discountSchema = temp.getJsonSchema();
			discountSchemas.add(discountSchema);
		}

		if (this.getDiscountIfUsedWithOtherSolution().size() != 0) {
			commercialCostSchema.add("Discount schemas", discountSchemas);
			flag = true;
		}

		if (flag)
			return commercialCostSchema;
		else
			return null;
		
	}

	/**
	 * @return the commercialCostCurrency
	 */
	public String getCommercialCostCurrency() {
		return commercialCostCurrency;
	}

	/**
	 * @param commercialCostCurrency
	 *            the commercialCostCurrency to set
	 */
	public void setCommercialCostCurrency(String commercialCostCurrency) {
		this.commercialCostCurrency = commercialCostCurrency;
	}

	/**
	 * @return the costPaymentChargeType
	 */
	public String getCostPaymentChargeType() {
		return costPaymentChargeType;
	}

	/**
	 * @param costPaymentChargeType
	 *            the costPaymentChargeType to set
	 */
	public void setCostPaymentChargeType(String costPaymentChargeType) {
		this.costPaymentChargeType = costPaymentChargeType;
	}

	/**
	 * @return the commercialCost
	 */
	public float getCommercialCost() {
		return commercialCost;
	}

	/**
	 * @param commercialCost
	 *            the commercialCost to set
	 */
	public void setCommercialCost(float commercialCost) {
		this.commercialCost = commercialCost;
	}

	/**
	 * @return the trialSchema
	 */
	public SolutionTrialSchema getTrialSchema() {
		return trialSchema;
	}

	/**
	 * @param trialSchema
	 *            the trialSchema to set
	 */
	public void setTrialSchema(SolutionTrialSchema trialSchema) {
		this.trialSchema = trialSchema;
	}

	/**
	 * @return the discountIfUsedWithOtherSolution
	 */
	public List<DiscountSchema> getDiscountIfUsedWithOtherSolution() {
		return discountIfUsedWithOtherSolution;
	}

	/**
	 * @param discountIfUsedWithOtherSolution
	 *            the discountIfUsedWithOtherSolution to set
	 */
	public void setDiscountIfUsedWithOtherSolution(
			List<DiscountSchema> discountIfUsedWithOtherSolution) {
		this.discountIfUsedWithOtherSolution = discountIfUsedWithOtherSolution;
	}

	public CommercialCostSchema clone() {
		CommercialCostSchema commercialCostSchema = new CommercialCostSchema();
		commercialCostSchema.setCommercialCost(this.getCommercialCost());
		commercialCostSchema.setCommercialCostCurrency(this
				.getCommercialCostCurrency());
		commercialCostSchema.setCostPaymentChargeType(this
				.getCostPaymentChargeType());
		commercialCostSchema.setTrialSchema(this.getTrialSchema().clone());
		List<DiscountSchema> list = new ArrayList<DiscountSchema>();
		for (int i = 0; i < this.getDiscountIfUsedWithOtherSolution().size(); i++) {
			list.add(this.getDiscountIfUsedWithOtherSolution().get(i).clone());
		}
		commercialCostSchema.setDiscountIfUsedWithOtherSolution(list);
		return commercialCostSchema;
	}

}
