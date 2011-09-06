package eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions;

import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.TypeState;

/**
 * It is not allowed to construct a <tt>TypeState</tt> with exception of a
 * <tt>TypeStartState</tt> with the type set to null. This exception is thrown
 * if such a <tt>TypeState</tt> is initialized.
 * @author Dominik Wolff
 */
public class InvalidTypeStateContentException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>InvalidTypeStateContentException</tt> class
     * with gets the invalid <tt>TypeState</tt> as parameter.
     * @param typeState State which contains null as value of the type field.
     */
    public InvalidTypeStateContentException(TypeState typeState) {
        super("Following TypeState " + typeState + " was initialized with null as parameter, which is not allowed and should be a valid type of an XML XSDSchema data structure");
    }
}
