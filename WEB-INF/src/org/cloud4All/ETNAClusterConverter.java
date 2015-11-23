package org.cloud4All;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.cloud4All.ontology.ETNACluster;
import org.cloud4All.ontology.OntologyInstance;

public class ETNAClusterConverter implements Converter {
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		OntologyInstance ontologyInstance = (OntologyInstance) context
				.getApplication().evaluateExpressionGet(context,
						"#{ontologyInstance}", OntologyInstance.class);
		// Convert the unique String representation of Foo to the actual Foo
		// object.
		return ontologyInstance.findCluster(value);
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

		// Convert the Foo object to its unique String representation.
		return ((ETNACluster) value).getName();
		// return "aa";
	}
}
