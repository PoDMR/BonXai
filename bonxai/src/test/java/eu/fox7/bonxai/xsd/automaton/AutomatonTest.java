package eu.fox7.bonxai.xsd.automaton;

import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.automaton.Transition;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;

import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>Automaton</tt> class, checks that every method of this
 * class performs properly.
 * @author Dominik Wolff
 */
public class AutomatonTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private TypeState stateA;
    private SimpleType simpleTypeB;
    private TypeState stateB;
    private Transition transition;

    public AutomatonTest() {
    }

    @Before
    public void setUp() throws InvalidTypeStateContentException {

        // Set up new state A
        simpleTypeA = new SimpleType("{}simpleTypeA", null);
        stateA = new TypeState(simpleTypeA);

        // Set up new state B
        simpleTypeB = new SimpleType("{}simpleTypeB", null);
        stateB = new TypeState(simpleTypeB);

        // Get new transition
        Element element = new Element("{}element");
        transition = new Transition(stateA, stateB);
        transition.addElement(element);
    }

    /**
     * Test of getStates method, of class Automaton for an empty states field.
     */
    @Test
    public void testGetStatesEmpty() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();

        LinkedHashSet<TypeState> expResult = new LinkedHashSet<TypeState>();
        expResult.add(instance.getStartState());
        LinkedHashSet<TypeState> result = instance.getStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of getStates method, of class Automaton for an non empty states
     * field.
     */
    @Test
    public void testGetStatesNotEmpty() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);

        LinkedHashSet<TypeState> expResult = new LinkedHashSet<TypeState>();
        expResult.add(instance.getStartState());
        expResult.add(stateA);
        LinkedHashSet<TypeState> result = instance.getStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of addState method, of class Automaton for a state, which does not
     * exist in the automaton.
     */
    @Test
    public void testAddStateNonExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();

        boolean expResult = true;
        boolean result = instance.addState(stateA);

        assertEquals(expResult, result);
        assertTrue(instance.getStates().contains(stateA));
    }

    /**
     * Test of addState method, of class Automaton for a state, which does exist
     * in the automaton.
     */
    @Test
    public void testAddStateExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);

        boolean expResult = false;
        boolean result = instance.addState(stateA);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeState method, of class Automaton for a state, which does
     * exist in the automaton.
     */
    @Test
    public void testRemoveStateExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);

        boolean expResult = true;
        boolean result = instance.removeState(stateA);

        assertEquals(expResult, result);
        assertTrue(!instance.getStates().contains(stateA));
    }

    /**
     * Test of removeState method, of class Automaton for a state, which does
     * not exist in the automaton.
     */
    @Test
    public void testRemoveStateNonExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();

        boolean expResult = false;
        boolean result = instance.removeState(stateA);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class Automaton for a transition, which
     * does not exist in the automaton.
     */
    @Test
    public void testAddTransitionNonExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);
        instance.addState(stateB);

        boolean expResult = true;
        boolean result = instance.addTransition(transition);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class Automaton for a transition, which
     * does exist in the automaton.
     */
    @Test
    public void testAddTransitionExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);
        instance.addState(stateB);
        instance.addTransition(transition);

        boolean expResult = false;
        boolean result = instance.addTransition(transition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class Automaton for a transition,
     * which does exist in the automaton.
     */
    @Test
    public void testRemoveTransitionExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);
        instance.addState(stateB);
        instance.addTransition(transition);

        boolean expResult = true;
        boolean result = instance.removeTransition(transition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class Automaton for a transition,
     * which does not exist in the automaton.
     */
    @Test
    public void testRemoveTransitionNonExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);
        instance.addState(stateB);

        boolean expResult = false;
        boolean result = instance.removeTransition(transition);

        assertEquals(expResult, result);
    }
}