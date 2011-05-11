package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;

/**
 * This class represents the start state of a <tt>TypeAutomaton</tt>. Start 
 * states does not contain anything so the constructor initializes this state
 * while setting the fields of the super class to null.
 *
 * @author Dominik Wolff
 */
public class TypeStartState extends TypeState {

    /**
     * Constructor for the <tt>TypeStartState</tt> class, which sets the
     * fields of the super class to null. So no type is contained in it.
     *
     * @throws InvalidTypeStateContentException Exception that is normally
     * thrown if the specified typ of a type state is an invalid content, such
     * as a null pointer. Which is allowed in this case.
     */
    public TypeStartState() throws InvalidTypeStateContentException {
        super(null);
    }
}
