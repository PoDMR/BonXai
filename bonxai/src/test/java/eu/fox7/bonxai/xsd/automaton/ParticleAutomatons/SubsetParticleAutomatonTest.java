package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.automaton.Automaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleTransition;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.SubsetParticleAutomaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.SubsetParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.EmptySubsetParticleStateFieldException;

import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>SubsetParticleAutomaton</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class SubsetParticleAutomatonTest extends junit.framework.TestCase {

    private SubsetParticleState subsetStateA;
    private SubsetParticleState subsetStateB;
    private SubsetParticleState subsetStateC;
    private ParticleTransition particleTransition;
    private LinkedHashSet<ParticleState> particleStatesB;

    public SubsetParticleAutomatonTest() {
    }

    @Before
    public void setUp() throws EmptySubsetParticleStateFieldException {

        // Set up particle state A
        LinkedHashSet<ParticleState> particleStatesA = new LinkedHashSet<ParticleState>();
        particleStatesA.add(new ParticleState(0));
        subsetStateA = new SubsetParticleState(particleStatesA, 0);

        // Set up particle state B
        particleStatesB = new LinkedHashSet<ParticleState>();
        particleStatesB.add(new ParticleState(1));
        particleStatesB.add(new ParticleState(2));
        subsetStateB = new SubsetParticleState(particleStatesB, 1);

        // Set up particle state C
        LinkedHashSet<ParticleState> particleStatesC = new LinkedHashSet<ParticleState>();
        particleStatesC.add(new ParticleState(3));
        subsetStateC = new SubsetParticleState(particleStatesC, 2);

        // Get new transition
        Element element = new Element("{}element");
        particleTransition = new ParticleTransition(subsetStateA, subsetStateB, false);
        particleTransition.addElement(element);
    }

    /**
     * Test of addState method, of class SubsetParticleAutomaton for a state,
     * which does not exist in the automaton.
     */
    @Test
    public void testAddStateNonExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);

        boolean expResult = true;
        boolean result = instance.addState(subsetStateB);

        assertEquals(expResult, result);
        assertTrue(instance.getStates().contains(subsetStateB));
    }

    /**
     * Test of addState method, of class SubsetParticleAutomaton for a state,
     * which does exist in the automaton.
     */
    @Test
    public void testAddStateExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);

        boolean expResult = false;
        boolean result = instance.addState(subsetStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeState method, of class SubsetParticleAutomaton for a state,
     * which does exist in the automaton.
     */
    @Test
    public void testRemoveStateExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);

        boolean expResult = true;
        boolean result = instance.removeState(subsetStateB);

        assertEquals(expResult, result);
        assertTrue(!instance.getStates().contains(subsetStateB));
        assertEquals(1, (int) instance.getNextStateNumber());
    }

    /**
     * Test of removeState method, of class SubsetParticleAutomaton for a state,
     * which does not exist in the automaton.
     */
    @Test
    public void testRemoveStateNonExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);

        boolean expResult = false;
        boolean result = instance.removeState(subsetStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class SubsetParticleAutomaton for a
     * transition, which does not exist in the automaton.
     */
    @Test
    public void testAddTransitionNonExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);

        boolean expResult = true;
        boolean result = instance.addTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class SubsetParticleAutomaton for a
     * transition, which does exist in the automaton.
     */
    @Test
    public void testAddTransitionExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);
        instance.addTransition(particleTransition);

        boolean expResult = false;
        boolean result = instance.addTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class SubsetParticleAutomaton for a
     * transition, which does exist in the automaton.
     */
    @Test
    public void testRemoveTransitionExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);
        instance.addTransition(particleTransition);

        boolean expResult = true;
        boolean result = instance.removeTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class SubsetParticleAutomaton for a
     * transition, which does not exist in the automaton.
     */
    @Test
    public void testRemoveTransitionNonExisting() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);

        boolean expResult = false;
        boolean result = instance.removeTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class SubsetParticleAutomaton for a
     * contained state.
     */
    @Test
    public void testContainsStateTrue() throws EmptySubsetParticleStateFieldException {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);

        SubsetParticleState expResult = (SubsetParticleState) subsetStateB;
        SubsetParticleState result = instance.containsState(new SubsetParticleState(particleStatesB, 4));

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class SubsetParticleAutomaton for a not
     * contained state.
     */
    @Test
    public void testContainsStateFalse() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);

        SubsetParticleState expResult = (SubsetParticleState) subsetStateB;
        SubsetParticleState result = instance.containsState(expResult);

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class SubsetParticleAutomaton.
     */
    @Test
    public void testToString() {
        SubsetParticleAutomaton instance = new SubsetParticleAutomaton(subsetStateA);
        instance.addState(subsetStateB);
        instance.addTransition(particleTransition);
        instance.addState(subsetStateC);
        instance.addFinalState(subsetStateC);
        Automaton.COMPLEX_REPRESENTATION = false;

        String expResult = "digraph G { \n graph [rankdir=LR] \n\"(0)\"->\"(1)\"[label=\"({}element)\"]\n\"(2)\"" + "[peripheries=2] \n}";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}