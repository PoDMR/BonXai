package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions.EmptyProductParticleStateFieldException;
import java.util.*;

/**
 * This class represents a state of a product automaton. This product automaton
 * is constructed for particle automatons. The product particle state contains a
 * list of particle states in difference to the subset particle automaton,
 * because the product is build for a specific number of automatons and each
 * automaton contains one particle state contained in this product particle
 * state.
 * 
 * @author Dominik Wolff
 */
public class ProductParticleState extends ParticleState {

    // List of <tt>ParticleStates</tt> contained by this state.
    LinkedList<ParticleState> particleStates;

    /**
     * Constructor of the <tt>ProductParticleState</tt> class. As parameter a
     * list of <tt>ParticleStates</tt> and a unique number for this state are
     * given.
     *
     * @param particleStates List of <tt>ParticleStates</tt> which should be
     * contained by this state.
     * @param stateNumber Unique number for this state. Which identifies this
     * state when using the "toString" method.
     * @throws EmptyProductParticleStateFieldException Exception that is thrown
     * if the current product particle state contains no particles.
     */
    public ProductParticleState(LinkedList<ParticleState> particleStates, int stateNumber) throws EmptyProductParticleStateFieldException {
        super(stateNumber);

        // Check if set of contained states is empty
        if(particleStates != null && !particleStates.isEmpty()){

            // Add contained states to product state
            this.particleStates = particleStates;
        }else{

            // If the set of contained states is empty throw exception
            throw new EmptyProductParticleStateFieldException(this);
        }
    }

    /**
     * This method returns the list of contained <tt>ParticleStates</tt>.
     *
     * @return List of <tt>ParticleStates</tt> contained by this state.
     */
    public LinkedList<ParticleState> getParticleStates() {
        return new LinkedList<ParticleState>(particleStates);
    }

    /**
     * Returns a string representation of the <tt>ProductParticleState</tt>. The
     * result should be a concise but informative representation that is easy
     * for a person to read. In order to achieve this the result is a string
     * that can be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>ProductParticleState</tt>.
     */
    @Override
    public String toString() {
        String output = "(";

        // Check if representation should be complex
        if (Automaton.COMPLEX_REPRESENTATION) {

            // Add each entry of the contained state list to the output
            for (Iterator<ParticleState> it = particleStates.iterator(); it.hasNext();) {
                ParticleState particleState = it.next();

                // If the list contains a null entry add "sink" to the output
                if (particleState == null) {
                    output += "sink";
                } else {
                    output += particleState;
                }

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
