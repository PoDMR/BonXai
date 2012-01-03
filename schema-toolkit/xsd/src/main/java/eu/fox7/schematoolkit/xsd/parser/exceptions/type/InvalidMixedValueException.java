package eu.fox7.schematoolkit.xsd.parser.exceptions.type;

import eu.fox7.schematoolkit.xsd.om.ComplexType;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidMixedValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMixedValueException(ComplexType type) {
        super("Following ComplexType has an illegal mixed-property: " + type.getName());
    }
}