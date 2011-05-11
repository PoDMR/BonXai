package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions.EmptySubsetParticleStateFieldException;
import java.util.*;

/**
 * This class represents a state in the subset particle automaton. So this
 * <tt>SubsetParticleState</tt>S contains a set of particle states, which can
 * be found in  the particle automaton used to construct the subset automaton..
 *
 * @author Dominik Wolff
 */
public class SubsetParticleState extends ParticleState {

    // Set of contained ParticleStates
    private LinkedHashSet<ParticleState> particleStates;

    /**
     * This method constructs a new <tt>SubsetParticleState</tt> containing the
     * set of specified particle states. Because the subset particle automaton
     * is constructed via subset construction from a regular particle automaton,
     * each state of the subset automaton contains one or more states of the
     * regular particle automaton.
     *
     * @param particleStates Set of particle states contained in the current
     * state.
     * @param stateNumber Unique number given to the current state. Used to
     * differentiate different states.
     * @throws EmptySubsetParticleStateFieldException Exception that is thrown
     * if the current subset particle state contains no particle states.
     */
    public SubsetParticleState(LinkedHashSet<ParticleState> particleStates, int stateNumber) throws EmptySubsetParticleStateFieldException {
        super(stateNumber);

        // Check if set of contained states is empty
        if (particleStates != null && !particleStates.isEmpty()) {

            // Add contained states to subset state
            this.particleStates = particleStates;
        } else {

            // If the set of contained states is empty throw exception
            throw new EmptySubsetParticleStateFieldException(this);
        }
    }

    /**
     * This method returns a set of all particle states contained by the current
     * state.
     *
     * @return Set containing all <tt>ParticleStates</tt> present in the current
     * SubsetParticleState.
     */
    public LinkedHashSet<ParticleState> getParticleStates() {
        return new LinkedHashSet<ParticleState>(particleStates);
    }

    /**
     * Returns a string representation of the <tt>SubsetParticleState</tt>. The
     * result should be a concise but informative representation that is easy
     * for a person to read. In order to achieve this the result is a string
     * that can be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>SubsetParticleState</tt>.
     */
    @Override
    public String toString() {
        String output = "(";

        // Check if representation should be complex
        if (Automaton.COMPLEX_REPRESENTATION) {

            // Add each entry of the contained state list to the output
            for (Iterator<ParticleState> it = particleStates.iterator(); it.hasNext();) {
                ParticleState particleState = it.next();
                output += particleState;

                // Check if the end of the list is reached if not use a comma to separate contained states
                if (it.hasNext()) {
                    output += ",";
                } else {
                    output += ")";
                }
            }
        } else {

            // The non complex representation only returns the state number of this state
            output += this.getStateNumber() + ")";
        }

        // Add quotation marks to the output
        return output;
    }
}
