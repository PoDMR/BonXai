package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ExclusiveAttributesException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when an object has two exlusive attributes.
     * @param exclusiveAttributes   Attributes which exclude each other.
     * @param caller                Name of calling object.
     */
    public ExclusiveAttributesException(String exclusiveAttributes, String caller) {
        super("Following attributes exclude each other: " + exclusiveAttributes + " in object " + caller);
    }
}