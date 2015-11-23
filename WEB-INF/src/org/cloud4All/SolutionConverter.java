package org.cloud4All;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.cloud4All.ontology.OntologyInstance;

public class SolutionConverter implements Converter {

	// Actions
	// ------------------------------------------------------------------------------------

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		// Convert the unique String representation of Foo to the actual Foo
		// object.
		return ontologyInstance.find(value);
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		// Convert the Foo object to its unique String representation.
		return ((Solution) value).getOntologyURI();
		// return "aa";
	}

}