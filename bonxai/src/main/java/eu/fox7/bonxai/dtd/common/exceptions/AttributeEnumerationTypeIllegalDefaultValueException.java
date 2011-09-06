package eu.fox7.bonxai.dtd.common.exceptions;

/**
 * Attribute Default value is not allowed in the given enumeration
 * @author Lars Schmidt
 */
public class AttributeEnumerationTypeIllegalDefaultValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttributeEnumerationTypeIllegalDefaultValueException(String name, String typeString, String value) {
        super("An attribute default value is not allowed in the given enumeration: Attribute:" + "\"" + name + "\"" + " Type: \"" + typeString + "\"" + " default: \"" + value + "\"");
    }

}
