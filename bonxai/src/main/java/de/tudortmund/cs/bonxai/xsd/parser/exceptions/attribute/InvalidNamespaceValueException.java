package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidNamespaceValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidNamespaceValueException(String caller) {
        super("The value of a namespace-property is invalid in:" + caller);
    }
}
