package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.*;
import java.util.*;

/**
 * This class represents a product automaton for particle automatons. So this
 * automaton contains states which in turn represent sets of states. These states
 * originate in the particle automatons used to build this automaton via product
 * construction. To improve readability states also contain numbers.
 *
 * @author Dominik Wolff
 */
public class ProductParticleAutomaton extends ParticleAutomaton {

    /**
     * Constructor of the <tt>ProductParticleAutomaton</tt> class is given a
     * specified start state for the product automaton.
     *
     * @param startState <tt>ProductParticleState</tt> which is the root of the
     * new product automaton.
     */
    public ProductParticleAutomaton(ProductParticleState startState) {
        super(startState);
    }


    /**
     * Checks if the <tt>ProductParticleAutomaton</tt> already contains a state 
     * with exactly the same contained states. If this is the case the existing
     * state is returned else the returned state is the not contained state.
     *
     * @param state State for which is checked if it is present in the
     * automaton.
     * @return Either the specified state if no equal state exists in the
     * automaton or the equivalent state which already exists in the automaton.
     */
    public ProductParticleState containsState(ProductParticleState state) {
        for (State compareState : getStates()) {
            LinkedList<ParticleState> compareParticleStates = ((ProductParticleState) compareState).getParticleStates();

            // Check if each contained state exists in both containing states
            boolean isContained = true;

            // For each position in the particle states
            for (int i = 0; i < compareParticleStates.size(); i++) {

                // Check if both contained states are the same
                if (state.getParticleStates().get(i) != compareParticleStates.get(i)) {
                    isContained = false;
                }
            }

            // If both states are equal return the already existing state.
            if (isContained) {
                return (ProductParticleState) compareState;
            }
        }
        return state;
    }
}
