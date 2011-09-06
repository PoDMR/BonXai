package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import eu.fox7.bonxai.xsd.automaton.*;

/**
 * This class represents a state in the <tt>ParticleAutomaton</tt>. Because
 * most information in such an automaton is annotated to the transitions states
 * have almost no content.
 *
 * @author Dominik Wolff
 */
public class ParticleState extends State {

    // Number of the state for easy readability
    private int stateNumber;

    /**
     * Constructor of the <tt>ParticleState</tt> class, it obtains the number of
     * the constructed state.
     *
     * @param stateNumber Number given to this specific state.
     */
    public ParticleState(int stateNumber) {
        this.stateNumber = stateNumber;
    }

    /**
     * This method returns the unique number given to this state.
     *
     * @return Specific number of this state.
     */
    public int getStateNumber() {
        return stateNumber;
    }

    /**
     * Set the number of this state. This number should be unique so that no
     * conflicts arise when using the "toString" method of the automaton.
     *
     * @param stateNumber Specific unique number for this state.
     */
    public void setStateNumber(int stateNumber) {
        this.stateNumber = stateNumber;
    }

    /**
     * Returns a string representation of the <tt>ParticleState</tt>. The result
     * should be a concise but informative representation that is easy for a
     * person to read. In order to achieve this the result is a string that can
     * be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>ParticleState</tt>.
     */
    @Override
    public String toString() {

        // The complex and non complex representation only returns the state number of this state
        String output = "(" + this.getStateNumber() + ")";

        // Add quotation marks to the output
        return output;
    }
}
