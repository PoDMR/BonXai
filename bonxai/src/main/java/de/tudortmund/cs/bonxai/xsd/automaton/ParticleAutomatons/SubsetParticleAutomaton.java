package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.*;
import java.util.*;

/**
 * This class represents the subset automaton of a particle automaton. Each
 * subset particle automaton contains a single start state and set of final
 * states. Instead of an NFA for the given particle this automaton represents a
 * DFA for the given particle.
 *
 * @author Dominik Wolff
 */
public class SubsetParticleAutomaton extends ParticleAutomaton {

    /**
     * Constructor of the <tt>SubsetParticleAutomaton</tt> class. To construct a
     * subset particle automaton a start state has to be specified.
     *
     * @param startState New start state of the constructed automaton.
     */
    public SubsetParticleAutomaton(SubsetParticleState startState) {
        super(startState);
    }

    /**
     * Checks if the <tt>SubsetParticleAutomaton</tt> already contains a state
     * with exactly the same <tt>ParticleStates</tt>. If this is the case the
     * contained subset particle state is returned else the returned state is
     * the not contained state.
     *
     * @param state State for which is checked if it is present in the automaton.
     * @return Either the specified state if no equal state exists in the
     * automaton or the equivalent state which already exists in the automaton.
     */
    public SubsetParticleState containsState(SubsetParticleState state) {
        for (State compareState : getStates()) {
            LinkedHashSet<ParticleState> particleStates = ((SubsetParticleState) compareState).getParticleStates();

            // If the particle states contained in this subset state are equal to the particles contained in an already existing subset state return the existing state.
            if (particleStates.size() == state.getParticleStates().size() && particleStates.containsAll(state.getParticleStates()) && state.getParticleStates().containsAll(particleStates)) {
                return (SubsetParticleState) compareState;
            }
        }
        return state;
    }
}
