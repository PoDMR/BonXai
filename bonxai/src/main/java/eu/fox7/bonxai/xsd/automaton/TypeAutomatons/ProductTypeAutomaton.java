package eu.fox7.bonxai.xsd.automaton.TypeAutomatons;

import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;

import java.util.*;

/**
 * This class represents a product automaton for type automatons. So this
 * automaton contains states which in turn represent lists of states. These
 * states originate in the type automatons used to build this automaton via
 * product construction.
 *
 * @author Dominik Wolff
 */
public class ProductTypeAutomaton extends TypeAutomaton {

    /**
     * Contructor for class <tt>ProductTypeAutomaton</tt> which initializes the
     * list of states and adds the newly generated start state to it.
     *
     * @param productTypeStartState <tt>ProductTypeState</tt> which is the root
     * of the new product automaton.
     * @throws InvalidTypeStateContentException Exception which is thrown if a
     * type state contains invlaid content.
     */
    public ProductTypeAutomaton(ProductTypeState productTypeStartState) throws InvalidTypeStateContentException {

        // Clear states
        states.clear();

        // Set new start state and add it to the state set
        this.startState = productTypeStartState;
        addState(productTypeStartState);
    }


    /**
     * Checks if the <tt>ProductTypeAutomaton</tt> already contains a state
     * with exactly the same contained states. If this is the case the existing
     * state is returned else the returned state is the not contained state.
     *
     * @param state State for which is checked if it is present in the
     * automaton.
     * @return Either the specified state if no equal state exists in the
     * automaton or the equivalent state which already exists in the automaton.
     */
    public ProductTypeState containsState(ProductTypeState state) {
        for (State compareState : getStates()) {
            LinkedList<TypeState> compareTypeStates = ((ProductTypeState) compareState).getTypeStates();

            // Check if each contained state exists in both containing states
            boolean isContained = true;

            // For each position in the type states
            for (int i = 0; i < compareTypeStates.size(); i++) {

                // Check if both contained states are the same
                if (state.getTypeStates().get(i) != compareTypeStates.get(i)) {
                    isContained = false;
                }
            }

            // If both states are equal return the already existing state.
            if (isContained) {
                return (ProductTypeState) compareState;
            }
        }
        return state;
    }
}