package eu.fox7.bonxai.xsd.parser.exceptions.type;

import eu.fox7.bonxai.xsd.ComplexType;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidMixedValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMixedValueException(ComplexType type) {
        super("Following ComplexType has an illegal mixed-property: " + type.getName());
    }
}
