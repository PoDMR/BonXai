package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.*;
import java.util.*;

/**
 * This class represents a particle automaton. Each particle automaton contains
 * a start state and a final state. Particles can be seen as a special form of
 * regular expressions. So this particle automaton is basically an NFA for this
 * expression.
 * 
 * @author Dominik Wolff
 */
public class ParticleAutomaton extends Automaton<ParticleState, ParticleTransition> {

    /**
     * Start state of the ParticleAutomaton
     */
    protected ParticleState startState;

    /**
     * Set of final states of the ParticleAutomaton
     */
    protected LinkedHashSet<ParticleState> finalStates;

    /**
     * Set of numbers currently used for states of the ParticleAutomaton
     */
    protected LinkedHashSet<Integer> stateNumbers = new LinkedHashSet<Integer>();

    /**
     * Constructor of the <tt>ParticleAutomaton</tt> class, it obtains a start
     * state as parameter, which is the root of the generated particle
     * automaton.
     *
     * @param startState State which is the root of the automaton. Each particle
     * represented by this automaton begins with an element leaving this state.
     */
    public ParticleAutomaton(ParticleState startState) {
        super();
        this.startState = startState;
        this.finalStates = new LinkedHashSet<ParticleState>();
        addState(startState);
    }

    /**
     * This method returns the next number which can be used for a state of this
     * automaton.
     *
     * @return Number for the next state.
     */
    public Integer getNextStateNumber() {
        for (int i = 1; i <= stateNumbers.size() + 1; i++) {
            if (!stateNumbers.contains(i)) {
                return i;
            }
        }
        return null;
    }

    /**
     * This method gets the start state of this <tt>ParticleAutomaton</tt> or
     * constructs a new one.
     *
     * @return The start-state of the <tt>ParticleAutomaton</tt>.
     */
    public ParticleState getStartState() {
        if (startState == null) {
            startState = new ParticleState(getNextStateNumber());
            addState(startState);
        }
        return startState;
    }

    /**
     * This method gets the set of final state of this
     * <tt>ParticleAutomaton</tt>. If no final state is present a new final
     * state is added to the set of final states.
     *
     * @return The set of final state contained in the product particle
     * automaton.
     */
    public LinkedHashSet<ParticleState> getFinalStates() {

        // Initialize final states field
        if (finalStates == null) {
            finalStates = new LinkedHashSet<ParticleState>();
        }
        return finalStates;
    }

    /**
     * Adds a particle state to the set of final states.
     *
     * @param finalState Particle state which should be added to the set of
     * final states.
     * @return <tt>true</tt> if the set of final states did not already
     * contain the specified state.
     */
    public boolean addFinalState(ParticleState finalState) {
        if (finalStates == null) {
            finalStates = new LinkedHashSet<ParticleState>();
        }
        return this.finalStates.add(finalState);
    }

    /**
     * Clears the set of final states of all states. After this the set is
     * empty and the automaton has no final states.
     */
    public void clearFinalStates() {
        this.finalStates.clear();
    }

    /**
     * Adds the specified state to this automaton if it is not already present.
     * If this automaton already contains the state, the call leaves the
     * automaton unchanged and returns <tt>false</tt>.
     *
     * @param state State which will be added to this automaton. Has to be a
     * <tt>ParticleState</tt>.
     * @return <tt>true</tt> if this automaton did not already contain the
     * specified state, which has to be a <tt>ParticleState</tt>.
     */
    @Override
    public boolean addState(ParticleState state) {
        // Add state number and state to the automaton
        stateNumbers.add((state).getStateNumber());
        return states.add(state);
    }

    /**
     * Removes the specified state and its transitions from this automaton if
     * it is present. Returns <tt>true</tt> if this automaton contained the
     * state. (This automaton will not contain the state once the call returns.)
     *
     * @param state State which will be removed from this automaton, if present.
     * Has to be a <tt>ParticleState</tt>.
     * @return <tt>true</tt> if the automaton contained the specified state,
     * which has to be a <tt>ParticleState</tt>.
     */
    @Override
    public boolean removeState(ParticleState state) {
        // Remove state number and state of the automaton
        stateNumbers.remove(((ParticleState) state).getStateNumber());
        return super.removeState(state);
    }

  
    /**
     * Returns a string representation of the <tt>ParticleAutomaton</tt>. The
     * result should be a concise but informative representation that is easy
     * for a person to read. In order to achieve this the result is a string
     * that can be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>ParticleAutomaton</tt>.
     */
    @Override
    public String toString() {

        // Set digraph representation from left to right
        String output = "digraph G { \n graph [rankdir=LR] \n";

        // Add each state and its transitions to the automaton
        for (Iterator<ParticleState> it = states.iterator(); it.hasNext();) {
            ParticleState state = it.next();

            // Get outgoing transitions of the current state
            LinkedHashSet<Transition> transitions = state.getOutTransitions();

            // Add each outgoing transition of the current state to the out put
            for (Iterator<Transition> it2 = transitions.iterator(); it2.hasNext();) {
                Transition transition = it2.next();
                output += transition;
            }
        }

        if (finalStates != null) {
            // Add final states to the output
            for (Iterator<ParticleState> it = finalStates.iterator(); it.hasNext();) {
                ParticleState particleState = it.next();
                output += "\"" + particleState + "\"" + "[peripheries=2] \n";
            }
        }

        // Return output with closing quotation mark
        return output + "}";
    }
}
