package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class NonGlobalSubstitutionGroupException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when an element is not global and has a substitutionGroup-attribute
     * @param name
     */
    public NonGlobalSubstitutionGroupException(String name) {
        super("There is an empty substitutionGroup-property in the following element:" + name + " which is not declared globally.");
    }
}