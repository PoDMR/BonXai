package eu.fox7.bonxai.xsd.parser.exceptions.type;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexTypeAnyAttributeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplexTypeAnyAttributeException(String name) {
        super("There are additional Attributes to an AnyAttribute in a ComplexType:" + name);
    }
}