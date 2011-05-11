package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.Element;
import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ParticleAutomaton</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleAutomatonTest extends junit.framework.TestCase {

    private ParticleState particleStateA;
    private ParticleState particleStateB;
    private ParticleState particleStateC;
    private ParticleTransition particleTransition;

    public ParticleAutomatonTest() {
    }


    @Before
    public void setUp() {

        // Set up particle states
        particleStateA = new ParticleState(0);
        particleStateB = new ParticleState(1);
        particleStateC = new ParticleState(2);

        // Get new transition
        Element element = new Element("{}element");
        particleTransition = new ParticleTransition(particleStateA, particleStateA, false);
        particleTransition.addElement(element);
    }

    /**
     * Test of getNextStateNumber method, of class ParticleAutomaton.
     */
    @Test
    public void testGetNextStateNumber() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);

        Integer expResult = 1;
        Integer result = instance.getNextStateNumber();

        assertEquals(expResult, result);
        instance.addState(particleStateB);

        expResult = 2;
        result = instance.getNextStateNumber();

        assertEquals(expResult, result);
    }

    /**
     * Test of getStartState method, of class ParticleAutomaton.
     */
    @Test
    public void testGetStartState() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);

        ParticleState expResult = particleStateA;
        ParticleState result = instance.getStartState();

        assertEquals(expResult, result);
    }

    /**
     * Test of getStartState method, of class ParticleAutomaton, if current
     * start state was removed.
     */
    @Test
    public void testGetStartStateForNull() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.removeState(particleStateA);

        ParticleState result = instance.getStartState();

        assertTrue(result != null);
    }

    /**
     * Test of getFinalStates method, of class ParticleAutomaton for empty
     * final states field.
     */
    @Test
    public void testGetFinalStatesEmpty() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);

        LinkedHashSet<ParticleState> expResult = new LinkedHashSet<ParticleState>();
        LinkedHashSet<ParticleState> result = instance.getFinalStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of getFinalStates method, of class ParticleAutomaton for non empty
     * final states field.
     */
    @Test
    public void testGetFinalStatesNonEmpty() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addFinalState(particleStateA);

        LinkedHashSet<ParticleState> expResult = new LinkedHashSet<ParticleState>();
        expResult.add(particleStateA);
        LinkedHashSet<ParticleState> result = instance.getFinalStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of addFinalState method, of class ParticleAutomaton for a final
     * state, which does not exist in the automaton.
     */
    @Test
    public void testAddFinalStateNonExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);

        boolean expResult = true;
        boolean result = instance.addFinalState(particleStateA);

        assertEquals(expResult, result);
        assertTrue(instance.getFinalStates().contains(particleStateA));
    }

    /**
     * Test of addFinalState method, of class ParticleAutomaton for a final
     * state, which does exist in the automaton.
     */
    @Test
    public void testAddFinalStateExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addFinalState(particleStateA);

        boolean expResult = false;
        boolean result = instance.addFinalState(particleStateA);

        assertEquals(expResult, result);
    }

    /**
     * Test of clearFinalStates method, of class ParticleAutomaton.
     */
    @Test
    public void testClearFinalStates() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addFinalState(particleStateA);

        instance.clearFinalStates();

        assertTrue(instance.getFinalStates().isEmpty());
    }

    /**
     * Test of addState method, of class ParticleAutomaton for a state, which
     * does not exist in the automaton.
     */
    @Test
    public void testAddStateNonExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);

        boolean expResult = true;
        boolean result = instance.addState(particleStateB);

        assertEquals(expResult, result);
        assertTrue(instance.getStates().contains(particleStateB));
    }

    /**
     * Test of addState method, of class ParticleAutomaton for a state, which
     * does exist in the automaton.
     */
    @Test
    public void testAddStateExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addState(particleStateB);

        boolean expResult = false;
        boolean result = instance.addState(particleStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeState method, of class ParticleAutomaton for a state, which
     * does exist in the automaton.
     */
    @Test
    public void testRemoveStateExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addState(particleStateB);

        boolean expResult = true;
        boolean result = instance.removeState(particleStateB);

        assertEquals(expResult, result);
        assertTrue(!instance.getStates().contains(particleStateB));
        assertEquals(1, (int) instance.getNextStateNumber());
    }

    /**
     * Test of removeState method, of class ParticleAutomaton for a state, which
     * does not exist in the automaton.
     */
    @Test
    public void testRemoveStateNonExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);

        boolean expResult = false;
        boolean result = instance.removeState(particleStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class ParticleAutomaton for a
     * transition, which does not exist in the automaton.
     */
    @Test
    public void testAddTransitionNonExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addState(particleStateB);

        boolean expResult = true;
        boolean result = instance.addTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class ParticleAutomaton for a
     * transition, which does exist in the automaton.
     */
    @Test
    public void testAddTransitionExisting() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addState(particleStateB);
        instance.addTransition(particleTransition);

        boolean expResult = false;
        boolean result = instance.addTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class ParticleAutomaton for a
     * transition, which does exist in the automaton.
     */
    @Test
    public void testRemoveTransitionExisting()  {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addState(particleStateB);
        instance.addTransition(particleTransition);

        boolean expResult = true;
        boolean result = instance.removeTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class ParticleAutomaton for a
     * transition, which does not exist in the automaton.
     */
    @Test
    public void testRemoveTransitionNonExisting()  {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addState(particleStateB);

        boolean expResult = false;
        boolean result = instance.removeTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ParticleAutomaton.
     */
    @Test
    public void testToString() {
        ParticleAutomaton instance = new ParticleAutomaton(particleStateA);
        instance.addState(particleStateB);
        instance.addTransition(particleTransition);
        instance.addState(particleStateC);
        instance.addFinalState(particleStateC);
       
        String expResult = "digraph G { \n graph [rankdir=LR] \n\"(0)\"->\"(0)\"[label=\"({}element)\"]\n\"(2)\"" + "[peripheries=2] \n}";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}