package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * AttributeType is not allowed
 * @author Lars Schmidt
 */
public class AttributeTypeIllegalValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttributeTypeIllegalValueException(String name, String typeString) {
        super("An attributeType value is not allowed in attribute:" + "\"" + name + "\"" + " Type: \"" + typeString + "\"");
    }

}
