package de.tudortmund.cs.bonxai.xsd.parser.exceptions.constraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyReferException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has an empty refer.
     * @param caller        Calling constraint.
     */
    public EmptyReferException(String caller) {
        super("Following constraint has an empty refer: " + caller);
    }
}