package eu.fox7.bonxai.xsd.parser.exceptions.content;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ExclusiveContentException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when an object has two exlusive attributes.
     * @param exclusiveAttributes   Attributes which exclude each other.
     * @param caller                Name of calling object.
     */
    public ExclusiveContentException(String exclusiveAttributes, String caller) {
        super("Following attributes and/or content objects  exclude each other: " + exclusiveAttributes + " in object " + caller);
    }
}