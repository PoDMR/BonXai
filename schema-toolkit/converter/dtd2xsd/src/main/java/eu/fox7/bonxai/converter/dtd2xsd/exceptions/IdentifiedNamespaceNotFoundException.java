package eu.fox7.bonxai.converter.dtd2xsd.exceptions;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

/**
 * An expected namespace was not found in the namespacelist of the schema
 * @author Lars Schmidt
 */
public class IdentifiedNamespaceNotFoundException extends ConversionFailedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IdentifiedNamespaceNotFoundException(String namespace) {
        super("The following namespace was not found in the namespacelist of the schema: \"" + namespace + "\"");
    }

}
