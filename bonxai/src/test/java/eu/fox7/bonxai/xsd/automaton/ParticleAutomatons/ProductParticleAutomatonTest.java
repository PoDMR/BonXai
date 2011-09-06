package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleTransition;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ProductParticleAutomaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ProductParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.EmptyProductParticleStateFieldException;

import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ProductParticleAutomaton</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ProductParticleAutomatonTest extends junit.framework.TestCase {

    private ProductParticleState productStateA;
    private ProductParticleState productStateB;
    private ProductParticleState productStateC;
    private ParticleTransition particleTransition;
    private LinkedList<ParticleState> particleStatesB;

    public ProductParticleAutomatonTest() {
    }

    @Before
    public void setUp() throws EmptyProductParticleStateFieldException {

        // Set up particle state A
        LinkedList<ParticleState> particleStatesA = new LinkedList<ParticleState>();
        particleStatesA.add(new ParticleState(0));
        particleStatesA.add(new ParticleState(2));
        productStateA = new ProductParticleState(particleStatesA, 0);

        // Set up particle state B
        particleStatesB = new LinkedList<ParticleState>();
        particleStatesB.add(new ParticleState(1));
        particleStatesB.add(null);
        productStateB = new ProductParticleState(particleStatesB, 1);

        // Set up particle state C
        LinkedList<ParticleState> particleStatesC = new LinkedList<ParticleState>();
        particleStatesC.add(new ParticleState(3));
        particleStatesC.add(null);
        productStateC = new ProductParticleState(particleStatesC, 2);

        // Get new transition
        Element element = new Element("{}element");
        particleTransition = new ParticleTransition(productStateA, productStateB, false);
        particleTransition.addElement(element);
    }

    /**
     * Test of addState method, of class ProductParticleAutomaton for a state,
     * which does not exist in the automaton.
     */
    @Test
    public void testAddStateNonExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);

        boolean expResult = true;
        boolean result = instance.addState(productStateB);

        assertEquals(expResult, result);
        assertTrue(instance.getStates().contains(productStateB));
    }

    /**
     * Test of addState method, of class ProductParticleAutomaton for a state,
     * which does exist in the automaton.
     */
    @Test
    public void testAddStateExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);

        boolean expResult = false;
        boolean result = instance.addState(productStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeState method, of class ProductParticleAutomaton for a
     * state, which does exist in the automaton.
     */
    @Test
    public void testRemoveStateExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);

        boolean expResult = true;
        boolean result = instance.removeState(productStateB);

        assertEquals(expResult, result);
        assertTrue(!instance.getStates().contains(productStateB));
        assertEquals(1, (int) instance.getNextStateNumber());
    }

    /**
     * Test of removeState method, of class ProductParticleAutomaton for a
     * state, which does not exist in the automaton.
     */
    @Test
    public void testRemoveStateNonExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);

        boolean expResult = false;
        boolean result = instance.removeState(productStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class ProductParticleAutomaton for a
     * transition, which does not exist in the automaton.
     */
    @Test
    public void testAddTransitionNonExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);

        boolean expResult = true;
        boolean result = instance.addTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of addTransition method, of class ProductParticleAutomaton for a
     * transition, which does exist in the automaton.
     */
    @Test
    public void testAddTransitionExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);
        instance.addTransition(particleTransition);

        boolean expResult = false;
        boolean result = instance.addTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class ProductParticleAutomaton for a
     * transition, which does exist in the automaton.
     */
    @Test
    public void testRemoveTransitionExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);
        instance.addTransition(particleTransition);

        boolean expResult = true;
        boolean result = instance.removeTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class ProductParticleAutomaton for a
     * transition, which does not exist in the automaton.
     */
    @Test
    public void testRemoveTransitionNonExisting() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);

        boolean expResult = false;
        boolean result = instance.removeTransition(particleTransition);

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class ProductParticleAutomaton for a
     * contained state.
     */
    @Test
    public void testContainsStateTrue() throws EmptyProductParticleStateFieldException {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);

        ProductParticleState expResult = (ProductParticleState) productStateB;
        ProductParticleState result = instance.containsState(new ProductParticleState(particleStatesB, 4));

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class ProductParticleAutomaton for a not
     * contained state.
     */
    @Test
    public void testContainsStateFalse() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);

        ProductParticleState expResult = (ProductParticleState) productStateB;
        ProductParticleState result = instance.containsState(expResult);

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ProductParticleAutomaton.
     */
    @Test
    public void testToString() {
        ProductParticleAutomaton instance = new ProductParticleAutomaton(productStateA);
        instance.addState(productStateB);
        instance.addTransition(particleTransition);
        instance.addState(productStateC);
        instance.addFinalState(productStateC);
        Automaton.COMPLEX_REPRESENTATION = false;

        String expResult = "digraph G { \n graph [rankdir=LR] \n\"(0)\"->\"(1)\"[label=\"({}element)\"]\n\"(2)\"" + "[peripheries=2] \n}";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}