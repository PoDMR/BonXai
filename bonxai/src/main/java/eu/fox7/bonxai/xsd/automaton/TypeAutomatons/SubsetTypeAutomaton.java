package eu.fox7.bonxai.xsd.automaton.TypeAutomatons;

import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;

import java.util.*;

/**
 * This class represents a subset automaton which is basically a deterministic
 * finite automaton for a nondeterministic finite automaton, which is
 * represented by a non deterministic <tt>TypeAutomaton</tt>. Each state of this
 * automaton consists of a set of types.
 *
 * @author Dominik Wolff
 */
public class SubsetTypeAutomaton extends TypeAutomaton {

    /**
     * Contructor for class <tt>SubsetTypeAutomaton</tt> which initializes the
     * list of states and adds the newly generated start state to it.
     *
     * @param subsetTypeStartState Start state of the new subset type automaton.
     * @throws InvalidTypeStateContentException Exception which is thrown if a
     * type state contains invalid content.
     */
    public SubsetTypeAutomaton(SubsetTypeState subsetTypeStartState) throws InvalidTypeStateContentException {
        
        // Clear states
        states.clear();

        // Set new start state and add it to the state set
        this.startState = subsetTypeStartState;
        addState(subsetTypeStartState);
    }

 
    /**
     * Checks if the <tt>SubsetTypeAutomaton</tt> already contains a state
     * with exactly the same <tt>TypeStates</tt>. If this is the case the
     * contained subset type state is returned else the returned state is
     * the not contained state.
     *
     * @param state State for which is checked if it is present in the automaton.
     * @return Either the specified state if no equal state exists in the
     * automaton or the equivalent state which already exists in the automaton.
     */
    public SubsetTypeState containsState(SubsetTypeState state) {
        for (State compareState : getStates()) {
            LinkedHashSet<TypeState> typeStates = ((SubsetTypeState) compareState).getTypeStates();

            // If the type states contained in this subset state are equal to the type states contained in an already existing subset state return the existing state.
            if (typeStates.size() == state.getTypeStates().size() && typeStates.containsAll(state.getTypeStates()) && state.getTypeStates().containsAll(typeStates)) {
                return (SubsetTypeState) compareState;
            }
        }
        return state;
    }
}