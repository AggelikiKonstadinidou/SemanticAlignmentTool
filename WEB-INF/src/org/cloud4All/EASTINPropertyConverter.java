package org.cloud4All;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.cloud4All.ontology.EASTINProperty;

import org.cloud4All.ontology.OntologyInstance;

public class EASTINPropertyConverter implements Converter {
	// Actions
	// ------------------------------------------------------------------------------------

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		// Convert the unique String representation of Foo to the actual Foo
		// object.
		if(value.equals("@@@@"))
			return ontologyInstance.findProperty(value);
		for (int i = 0; i < ontologyInstance.getProposedEASTINItems().size(); i++) {
			if (!value.equals("@@@@"))
				if ((ontologyInstance.getProposedEASTINItems().get(i).getName() + ontologyInstance
						.getProposedEASTINItems().get(i).getBelongsToCluster()+ontologyInstance.getProposedEASTINItems().get(i).getType())
						.equals(value.split("@@")[0] + value.split("@@")[1]+ value.split("@@")[2])) {
					return ontologyInstance.getProposedEASTINItems().get(i);
				}
		}
		for (int i = 0; i < ontologyInstance.getProposedMeasureEASTINItems()
				.size(); i++) {
			if (!value.equals("@@@@"))
				if ((ontologyInstance.getProposedMeasureEASTINItems().get(i)
						.getName() + ontologyInstance
						.getProposedMeasureEASTINItems().get(i)
						.getBelongsToCluster()+ontologyInstance
						.getProposedMeasureEASTINItems().get(i).getType()).equals(value.split("@@")[0]
						+ value.split("@@")[1]+ value.split("@@")[2])) {
					return ontologyInstance.getProposedMeasureEASTINItems()
							.get(i);
				}
		}
		return ontologyInstance.findProperty(value);

	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

		// Convert the Foo object to its unique String representation.
		return ((EASTINProperty) value).getName() + "@@"
				+ ((EASTINProperty) value).getBelongsToCluster()+"@@"+((EASTINProperty) value).getType();
		// return "aa";
	}
}
