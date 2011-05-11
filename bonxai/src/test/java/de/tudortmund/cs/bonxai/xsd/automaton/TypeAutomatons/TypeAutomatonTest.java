package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>TypeAutomaton</tt> class, checks that every method of
 * this class performs properly.
 * @author Dominik Wolff
 */
public class TypeAutomatonTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private TypeState stateA;
    private SimpleType simpleTypeB;
    private TypeState stateB;
    private Transition transition;

    public TypeAutomatonTest() {
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
     * Test of getStartState method, of class TypeAutomaton.
     */
    @Test
    public void testGetStartState() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();

        TypeState result = instance.getStartState();

        assertTrue(result instanceof TypeStartState);
        assertTrue(instance.getStates().contains(result));
    }

    /**
     * Test of addState method, of class TypeAutomaton for a state, which does
     * not exist in the automaton.
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
     * Test of addState method, of class TypeAutomaton for a state, which does
     * exist in the automaton.
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
     * Test of removeState method, of class TypeAutomaton for a state, which
     * does exist in the automaton.
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
     * Test of removeState method, of class TypeAutomaton for a state, which
     * does not exist in the automaton.
     */
    @Test
    public void testRemoveStateNonExisting() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();

        boolean expResult = false;
        boolean result = instance.removeState(stateA);

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class TypeAutomaton for a contained
     * state.
     */
    @Test
    public void testContainsStateTrue() throws InvalidTypeStateContentException{
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);
        instance.addState(stateB);

        TypeState expResult = (TypeState) stateA;
        TypeState result = instance.containsState(new TypeState(simpleTypeA));

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class TypeAutomaton for a not contained
     * state.
     */
    @Test
    public void testContainsStateFalse() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateB);

        TypeState expResult = new TypeState(simpleTypeA);
        TypeState result = instance.containsState(expResult);

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class TypeAutomaton.
     */
    @Test
    public void testToString() throws InvalidTypeStateContentException {
        TypeAutomaton instance = new TypeAutomaton();
        instance.addState(stateA);
        instance.addState(stateB);
        instance.addTransition(transition);

        String expResult = "digraph G { \n graph [rankdir=LR] \n\"simpleTypeA\"->\"simpleTypeB\"[label=\"({}element)\"]\n}";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}