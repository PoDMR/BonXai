package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import java.util.*;

/**
 * This Class represents an automaton with states which contain types of a
 * specified XML XSDSchema schema. Each transition is annotated with elements who
 * are of the same type as the type entered by the transition they are labeled
 * to. The only state which has no type is the start state which kind represents
 * the root or schema element of the XSD. Furthermore the automaton contains a
 * <tt>HashMap</tt> which maps types to states for convenience.
 *
 * @author Dominik Wolff
 */
public class TypeAutomaton extends Automaton<TypeState,Transition> {

    /**
     * Starting point of the TypeAutomaton, which contains nothing
     */
    protected TypeState startState;

    /**
     * Contructor for class <tt>TypeAutomaton</tt> which initializes the list of 
     * states and adds the newly generated start state to it. Furthermore the
     * mapping of types to states via a <tt>HashMap</tt> is initialized.
     * @throws InvalidTypeStateContentException Exception which is thrown if a
     * type state contains invalid content.
     */
    public TypeAutomaton() throws InvalidTypeStateContentException {
        super();
        this.startState = new TypeStartState();
        this.states.add(startState);
    }

    /**
     * This method gets the start state of the automaton, which roughly 
     * represents the root or schema element of the XML XSDSchema XSDSchema used to
     * build this automaton.
     * 
     * @return The start state of this automaton.
     */
    public TypeState getStartState() {
        return startState;
    }


    /**
     * Checks if the <tt>TypeAutomaton</tt> already contains a state with
     * exactly the same contained type. If this is the case the existing state
     * is returned else the returned state is the not contained state.
     *
     * @param state State for which is checked if it is present in the
     * automaton.
     * @return Either the specified state if no equal state exists in the
     * automaton or the equivalent state which already exists in the automaton.
     */
    public TypeState containsState(TypeState state) {
        for (State compareState : getStates()) {
            TypeState compareTypeState = (TypeState) compareState;

            // If both states contain the same type they are equal
            if (compareTypeState.getTypes().iterator().next() == state.getTypes().iterator().next()) {
                return compareTypeState;
            }
        }
        return state;
    }

    /**
     * Returns a string representation of the <tt>TypeAutomaton</tt>. The result
     * should be a concise but informative representation that is easy for a
     * person to read. In order to achieve this the result is a string that can
     * be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>TypeAutomaton</tt>.
     */
    @Override
    public String toString() {

        // Set digraph representation from left to right
        String output = "digraph G { \n graph [rankdir=LR] \n";

        // Add each state and its transitions to the automaton
        for (Iterator<TypeState> it = states.iterator(); it.hasNext();) {
            State state = it.next();

            // Get outgoing transitions of the current state
            LinkedHashSet<Transition> transitions = state.getOutTransitions();

            // Add each outgoing transition of the current state to the out put
            for (Iterator<Transition> it2 = transitions.iterator(); it2.hasNext();) {
                Transition transition = it2.next();
                output += transition;
            }
        }

        // Return output with closing quotation mark
        return output + "}";
    }
}
