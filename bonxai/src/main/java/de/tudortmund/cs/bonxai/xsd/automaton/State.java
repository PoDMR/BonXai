package de.tudortmund.cs.bonxai.xsd.automaton;

import java.util.*;

/**
 * This abstract class represents a state in an <tt>automaton</tt>. It consists
 * of two <tt>LinkedList</tt> which contain all incoming and outgoing 
 * transitions and a <tt>HashMap</tt> which maps reachable states to the
 * transitions needed to reach them.
 * 
 * @author Dominik Wolff
 */
public abstract class State {

    // List of incoming and outgoing transitions.
    private LinkedHashSet<Transition> inTransitions,  outTransitions;

    // HashMap which maps reachable states to transitions needed to reach them.
    private HashMap<State, Transition> nextStateTransitions;

    /**
     * Constructor of the <tt>State</tt> class, it initializes the contained
     * <tt>LinkedLists</tt> and <tt>HashMap</tt>.
     */
    public State() {
        inTransitions = new LinkedHashSet<Transition>();
        outTransitions = new LinkedHashSet<Transition>();
        nextStateTransitions = new HashMap<State, Transition>();
    }

    /**
     * This method returns a <tt>LinkedList</tt> of transitions, whose 
     * destination state is equival to this state.
     *
     * @return <tt>LinkedList</tt> of transitions entering this state.
     */
    public LinkedHashSet<Transition> getInTransitions() {
        return inTransitions;
    }

    /**
     * This method returns a <tt>LinkedList</tt> of transitions, whose source
     * state is equival to this state.
     *
     * @return <tt>LinkedList</tt> of transitions leaving this state.
     */
    public LinkedHashSet<Transition> getOutTransitions() {
        return outTransitions;
    }

    /**
     * This method returns a <tt>HashMap</tt> that helps to find transitions 
     * from this state to a specified state if present.
     *
     * @return <tt>HashMap</tt> HashMap which maps reachable states to
     * transitions needed to reach them.
     */
    public HashMap<State, Transition> getNextStateTransitions() {
        return nextStateTransitions;
    }

    /**
     * Adds the specified transtion to the list of outgoing transtions if it is
     * not already present. If the list of outgoing transtions already contains
     * the transition, the call leaves the list unchanged and returns
     * <tt>false</tt>.
     * (Also updates the nextStateTransitions <tt>HashMap</tt>, but only if the
     * transition can be added to the list.)
     *
     * @param outTransition Transition which is added to the list of outgoing
     * transtions.
     * @return <tt>true</tt> if the list of outgoing transtions did not already
     * contain the specified transition.
     */
    public boolean addOutTransition(Transition outTransition) {

        // Add transition to the list of outgoing transitions and store result
        boolean result = outTransitions.add(outTransition);

        // If transition was added to the list update next state to transition map
        if (result) {
            nextStateTransitions.put(outTransition.getDestinationState(), outTransition);
        }
        return result;
    }

    /**
     * Adds the specified transtion to the list of incoming transtions if it is
     * not already present. If the list of outgoing transtions already contains
     * the transition, the call leaves the list unchanged and returns
     * <tt>false</tt>.
     *
     * @param inTransition Transition which is added to the list of incoming
     * transtions.
     * @return <tt>true</tt> if the list of incoming transtions did not already
     * contain the specified transition.
     */
    public boolean addInTransition(Transition inTransition) {
        return inTransitions.add(inTransition);
    }

    /**
     * Removes the specified transition from the list of outgoing transtions if
     * it is present. Returns <tt>true</tt> if the list contained the
     * transition.
     * (The list of outgoing transtions will not contain the transition once the
     * call returns.)
     * (Also updates the nextStateTransitions <tt>HashMap</tt>, but only if the
     * transition can be removed from the list.)
     *
     * @param outTransition Transition is removed from the list of outgoing
     * transtions, if present.
     * @return <tt>true</tt> if the list of outgoing transtions contained the
     * specified transition.
     */
    public boolean removeOutTransition(Transition outTransition) {

        // Remove transition from the list of outgoing transitions and store result
        boolean result = outTransitions.remove(outTransition);

        // If transition was removed from the list update next state to transition map
        if (result) {
            nextStateTransitions.remove(outTransition.getDestinationState());
        }
        return result;
    }

    /**
     * Removes the specified transition from the list of incoming transtions if
     * it is present. Returns <tt>true</tt> if the list contained the 
     * transition.
     * (The list of incoming transtions will not contain the transition once the
     * call returns.)
     *
     * @param inTransition Transition is removed from the list of incoming
     * transtions, if present.
     * @return <tt>true</tt> if the list of incoming transtions contained the
     * specified transition.
     */
    public boolean removeInTransition(Transition inTransition) {
        return inTransitions.remove(inTransition);
    }
}
