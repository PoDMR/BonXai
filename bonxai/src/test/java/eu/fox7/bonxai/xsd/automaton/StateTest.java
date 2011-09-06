package eu.fox7.bonxai.xsd.automaton;

import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.automaton.State;
import eu.fox7.bonxai.xsd.automaton.Transition;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.TypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>State</tt> class, checks that every method of this class
 * performs properly.
 * @author Dominik Wolff
 */
public class StateTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private SimpleType simpleTypeB;
    private State stateB;

    public StateTest() {
    }

    @Before
    public void setUp() throws InvalidTypeStateContentException {

        // Set up type of instance
        simpleTypeA = new SimpleType("{}simpleTypeA", null);

        // Set up new state B
        simpleTypeB = new SimpleType("{}simpleTypeB", null);
        stateB = new TypeState(simpleTypeB);
    }

    /**
     * Test of getInTransitions method, of class State for no incoming
     * transitions.
     */
    @Test
    public void testGetInTransitionsEmpty() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);

        LinkedHashSet<Transition> expResult = new LinkedHashSet<Transition>();
        LinkedHashSet<Transition> result = instance.getInTransitions();

        assertEquals(expResult, result);
    }

    /**
     * Test of getInTransitions method, of class State for existing incoming
     * transitions.
     */
    @Test
    public void testGetInTransitionsNotEmpty() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition inTransition = new Transition(stateB, instance);
        instance.addInTransition(inTransition);

        LinkedHashSet<Transition> expResult = new LinkedHashSet<Transition>();
        expResult.add(inTransition);
        LinkedHashSet<Transition> result = instance.getInTransitions();

        assertEquals(expResult, result);
    }

    /**
     * Test of getOutTransitions method, of class State for no outgoing
     * transitions.
     */
    @Test
    public void testGetOutTransitionsEmpty() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);

        LinkedHashSet<Transition> expResult = new LinkedHashSet<Transition>();
        LinkedHashSet<Transition> result = instance.getOutTransitions();

        assertEquals(expResult, result);
    }

    /**
     * Test of getOutTransitions method, of class State for existing outgoing
     * transitions.
     */
    @Test
    public void testGetOutTransitionsNotEmpty() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition outTransition = new Transition(stateB, instance);
        instance.addOutTransition(outTransition);

        LinkedHashSet<Transition> expResult = new LinkedHashSet<Transition>();
        expResult.add(outTransition);
        LinkedHashSet<Transition> result = instance.getOutTransitions();

        assertEquals(expResult, result);
    }

    /**
     * Test of getNextStateTransitions method, of class State for no next
     * states.
     */
    @Test
    public void testGetNextStateTransitionsEmpty() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);

        HashMap<State, Transition> expResult = new HashMap<State, Transition>();
        HashMap<State, Transition> result = instance.getNextStateTransitions();

        assertEquals(expResult, result);
    }

    /**
     * Test of getNextStateTransitions method, of class State for existing next
     * states.
     */
    @Test
    public void testGetNextStateTransitionsNotEmpty() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition outTransition = new Transition(instance, stateB);
        instance.addOutTransition(outTransition);

        HashMap<State, Transition> expResult = new HashMap<State, Transition>();
        expResult.put(stateB, outTransition);
        HashMap<State, Transition> result = instance.getNextStateTransitions();

        assertEquals(expResult, result);
    }

    /**
     * Test of addOutTransition method, of class State for not existing outgoing
     * transition.
     */
    @Test
    public void testAddOutTransitionNonExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition outTransition = new Transition(instance, stateB);

        boolean expResult = true;
        boolean result = instance.addOutTransition(outTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of addOutTransition method, of class State for existing outgoing
     * transition.
     */
    @Test
    public void testAddOutTransitionExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition outTransition = new Transition(instance, stateB);
        instance.addOutTransition(outTransition);

        boolean expResult = false;
        boolean result = instance.addOutTransition(outTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of addInTransition method, of class State for not existing incoming
     * transition.
     */
    @Test
    public void testAddInTransitionNonExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition inTransition = new Transition(instance, stateB);

        boolean expResult = true;
        boolean result = instance.addInTransition(inTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of addInTransition method, of class State for existing incoming
     * transition.
     */
    @Test
    public void testAddInTransitionExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition inTransition = new Transition(instance, stateB);
        instance.addInTransition(inTransition);

        boolean expResult = false;
        boolean result = instance.addInTransition(inTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeOutTransition method, of class State for an existing
     * outgoing transition.
     */
    @Test
    public void testRemoveOutTransitionExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition outTransition = new Transition(instance, stateB);
        instance.addOutTransition(outTransition);

        boolean expResult = true;
        boolean result = instance.removeOutTransition(outTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeOutTransition method, of class State for an non existing
     * outgoing transition.
     */
    @Test
    public void testRemoveOutTransitionNonExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition outTransition = new Transition(instance, stateB);

        boolean expResult = false;
        boolean result = instance.removeOutTransition(outTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeInTransition method, of class State for an existing
     * outgoing transition.
     */
    @Test
    public void testRemoveInTransitionExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition inTransition = new Transition(stateB, instance);
        instance.addInTransition(inTransition);

        boolean expResult = true;
        boolean result = instance.removeInTransition(inTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeInTransition method, of class State for an non existing
     * outgoing transition.
     */
    @Test
    public void testRemoveInTransitionNonExisting() throws InvalidTypeStateContentException {
        State instance = new TypeState(simpleTypeA);
        Transition inTransition = new Transition(stateB, instance);

        boolean expResult = false;
        boolean result = instance.removeInTransition(inTransition);

        assertEquals(expResult, result);
    }
}