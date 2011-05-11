package de.tudortmund.cs.bonxai.xsd.automaton.exceptions;

import de.tudortmund.cs.bonxai.xsd.automaton.Transition;

/**
 * It is not allowed to construct a <tt>Transition</tt> with no source state.
 * This exception is thrown if such a <tt>Transition</tt> is initialized.
 *
 * @author Dominik Wolff
 */
public class EmptySourceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>EmptySourceException</tt> class with gets the
     * invalid <tt>Transition</tt> as parameter.
     * @param transition Transition which contains no source state.
     */
    public EmptySourceException(Transition transition) {
        super("Following Transition " + transition + " was initialized with null as source state, which is not allowed and should be a valid state of the automaton");
    }
}
