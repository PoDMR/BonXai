package de.tudortmund.cs.bonxai.xsd.automaton;

import java.util.LinkedHashSet;

/**
 * This abstract Class represents an automaton with simplistic methods like 
 * adding states and transitions or removing them. It also contains a list of
 * all states which can be used for other operations on this automaton.
 * 
 * @author Dominik Wolff
 */
public abstract class Automaton <StateClass extends State, TransitionClass extends Transition> {

    /**
     * Field which enables the user to change between a complex variant and a simple variant of the "toString" output
     */
    public static boolean COMPLEX_REPRESENTATION = false;

    /**
     * List of all states which are contained in this automaton.
     */
    protected LinkedHashSet<StateClass> states;

    /**
     * Constructor for class <tt>Automaton</tt> which initializes the list of
     * states.
     */
    public Automaton() {
        states = new LinkedHashSet<StateClass>();
    }

    /**
     * This method returns a <tt>LinkedHashSet</tt> which contains all states
     * present in this automaton.
     *
     * @return <tt>LinkedHashSet</tt> of all states contained in this automaton.
     */
    public LinkedHashSet<StateClass> getStates() {
        return states;
    }

    /**
     * Adds the specified state to this automaton if it is not already present.
     * If this automaton already contains the state, the call leaves the
     * automaton unchanged and returns <tt>false</tt>.
     *
     * @param state State which is added to this automaton.
     * @return <tt>true</tt> if this automaton did not already contain the
     * specified state.
     */
    public boolean addState(StateClass state) {
        return states.add(state);
    }

    /**
     * Removes the specified state and its transitions from this automaton if
     * it is present. Returns <tt>true</tt> if this automaton contained the
     * state.
     * (This automaton will not contain the state or transition to/or from it
     * once the call returns.)
     *
     * @param state State which is removed from this automaton, if present.
     * @return <tt>true</tt> if the automaton contained the specified state.
     */
    public boolean removeState(StateClass state) {

        // Check if the automaton contains the specified state
        if (states.contains(state)) {

            // Remove all incoming transitions of the removed state
            LinkedHashSet<Transition> inTransitions = new LinkedHashSet<Transition>(state.getInTransitions());
            for (Transition transition : inTransitions) {
                removeTransition(transition);
            }

            // Remove all outgoing transitions of the removed state
            LinkedHashSet<Transition> outTransitions = new LinkedHashSet<Transition>(state.getOutTransitions());
            for (Transition transition : outTransitions) {
                removeTransition(transition);
            }

            // Remove state from the automaton
            return states.remove(state);
        }
        return false;
    }

    /**
     * Adds the specified transition to this automaton if it is not already
     * present. If this automaton already contains the transition, the call
     * leaves the automaton unchanged and returns <tt>false</tt>.
     * 
     * (Both source state and destination state of the transition must be
     * present in the list of all states.)
     *
     * @param transition Transition which is added to this automaton.
     * @return <tt>true</tt> if this automaton did not already contain the
     * specified transition (This means neither the list of out transitions of
     * the source state nor the list of in transitions of the destination state
     * contains the specified transition).
     */
    public boolean addTransition(TransitionClass transition) {
        State source = transition.getSourceState();
        State destination = transition.getDestinationState();
        
        // Check if the process is successful
        boolean result = false;

        // Check if source and destination state are not null and contained in the automaton and check that no transition between source and destination state already exists
        if (source != null && destination != null && transition != null && !source.getNextStateTransitions().containsKey(destination) && states.contains(source) && states.contains(destination)) {

            // Add transition to the outgoing transitions of the source state and the incoming transitions of the destination state
            result = source.addOutTransition(transition);
            result &= destination.addInTransition(transition);
            return result;
        }
        return result;
    }

    /**
     * Removes the specified transition from this automaton if it is present.
     * Returns <tt>true</tt> if this automaton contained the transition.
     *
     * (This automaton will not contain the transition once the call returns.)
     * (This means both the list of out transitions of the source state
     * and the list of in transitions of the destination state are modified.)
     *
     * @param transition Transition which is removed from this automaton, if
     * present.
     * @return <tt>true</tt> if the automaton contained the specified 
     * transition.
     */
    public boolean removeTransition(Transition transition) {
        State source = transition.getSourceState();
        State destination = transition.getDestinationState();

        // Check if the process is successful
        boolean result = false;

        // Check if source and destination state are not null and contained in the automaton and check that a transition between source and destination state exists
        if (source != null && destination != null && transition != null && source.getNextStateTransitions().containsKey(destination) && states.contains(source) && states.contains(destination)) {

            // Remove transition from the outgoing transitions of the source state and the incoming transitions of the destination state
            result = source.removeOutTransition(transition);
            result &= destination.removeInTransition(transition);
            return result;
        }
        return result;
    }
}
