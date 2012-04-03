package eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions;

/**
 * An expected namespace was not found in the namespacelist of the schema
 * @author Lars Schmidt
 */
public class IdentifiedNamespaceNotFoundException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IdentifiedNamespaceNotFoundException(String namespace) {
        super("The following namespace was not found in the namespacelist of the schema: \"" + namespace + "\"");
    }

}
