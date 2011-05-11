package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ProductTypeAutomaton</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ProductTypeAutomatonTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private ProductTypeState productStateA;
    private SimpleType simpleTypeB;
    private ProductTypeState productStateB;
    private Transition transition;
    private LinkedList<TypeState> typeStatesA;
    private LinkedList<TypeState> typeStatesB;

    public ProductTypeAutomatonTest() {
    }

    @Before
    public void setUp() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {

        // Set up new product state A
        simpleTypeA = new SimpleType("{}simpleTypeA", null);
        TypeState typeStateA = new TypeState(simpleTypeA);
        simpleTypeB = new SimpleType("{}simpleTypeB", null);
        TypeState typeStateB = new TypeState(simpleTypeB);
        typeStatesA = new LinkedList<TypeState>();
        typeStatesA.add(typeStateA);
        typeStatesA.add(typeStateB);
        productStateA = new ProductTypeState(typeStatesA);

        // Set up new product state B
        typeStatesB = new LinkedList<TypeState>();
        typeStatesB.add(null);
        typeStatesB.add(typeStateB);
        productStateB = new ProductTypeState(typeStatesB);

        // Get new transition
        Element element = new Element("{}element");
        transition = new Transition(productStateA, productStateB);
        transition.addElement(element);
    }

    /**
     * Test of addState method, of class ProductTypeAutomaton for a state, which
     * does not exist in the automaton.
     */
    @Test
    public void testAddStateNonExisting() throws InvalidTypeStateContentException {
        ProductTypeAutomaton instance = new ProductTypeAutomaton(productStateA);

        boolean expResult = true;
        boolean result = instance.addState(productStateB);

        assertEquals(expResult, result);
        assertTrue(instance.getStates().contains(productStateB));
    }

    /**
     * Test of addState method, of class ProductTypeAutomaton for a state, which
     * does exist in the automaton.
     */
    @Test
    public void testAddStateExisting() throws InvalidTypeStateContentException {
        ProductTypeAutomaton instance = new ProductTypeAutomaton(productStateA);

        boolean expResult = false;
        boolean result = instance.addState(productStateA);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeState method, of class ProductTypeAutomaton for a state,
     * which does exist in the automaton.
     */
    @Test
    public void testRemoveStateExisting() throws InvalidTypeStateContentException {
        ProductTypeAutomaton instance = new ProductTypeAutomaton(productStateA);

        boolean expResult = true;
        boolean result = instance.removeState(productStateA);

        assertEquals(expResult, result);
        assertTrue(!instance.getStates().contains(productStateA));
    }

    /**
     * Test of removeState method, of class ProductTypeAutomaton for a state,
     * which does not exist in the automaton.
     */
    @Test
    public void testRemoveStateNonExisting() throws InvalidTypeStateContentException {
        ProductTypeAutomaton instance = new ProductTypeAutomaton(productStateA);

        boolean expResult = false;
        boolean result = instance.removeState(productStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class ProductTypeAutomaton for a contained
     * state.
     */
    @Test
    public void testContainsStateTrue() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        ProductTypeAutomaton instance = new ProductTypeAutomaton(productStateA);
        instance.addState(productStateB);

        TypeState expResult = (TypeState) productStateB;
        TypeState result = instance.containsState(new ProductTypeState(typeStatesB));

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class ProductTypeAutomaton for a not contained
     * state.
     */
    @Test
    public void testContainsStateFalse() throws InvalidTypeStateContentException {
        ProductTypeAutomaton instance = new ProductTypeAutomaton(productStateA);

        TypeState expResult = (TypeState) productStateB;
        TypeState result = instance.containsState(expResult);

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ProductTypeAutomaton.
     */
    @Test
    public void testToString() throws InvalidTypeStateContentException {
        ProductTypeAutomaton instance = new ProductTypeAutomaton(productStateA);
        instance.addState(productStateB);
        instance.addTransition(transition);

        String expResult = "digraph G { \n graph [rankdir=LR] \n\"(simpleTypeA,simpleTypeB)\"->\"(sink,simpleTypeB)\"[label=\"({}element)\"]\n}";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}