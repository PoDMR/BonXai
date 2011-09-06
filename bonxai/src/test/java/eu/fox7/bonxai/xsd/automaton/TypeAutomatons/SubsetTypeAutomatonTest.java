package eu.fox7.bonxai.xsd.automaton.TypeAutomatons;

import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.SubsetTypeAutomaton;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.SubsetTypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.TypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;

import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>SubsetTypeAutomaton</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class SubsetTypeAutomatonTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private SubsetTypeState subsetStateA;
    private SimpleType simpleTypeB;
    private SubsetTypeState subsetStateB;
    private Transition transition;
    private LinkedHashSet<TypeState> typeStatesA;
    private LinkedHashSet<TypeState> typeStatesB;

    public SubsetTypeAutomatonTest() {
    }

    @Before
    public void setUp() throws InvalidTypeStateContentException, EmptySubsetTypeStateFieldException {

        // Set up new product state A
        simpleTypeA = new SimpleType("{}simpleTypeA", null);
        TypeState typeStateA = new TypeState(simpleTypeA);
        simpleTypeB = new SimpleType("{}simpleTypeB", null);
        TypeState typeStateB = new TypeState(simpleTypeB);
        typeStatesA = new LinkedHashSet<TypeState>();
        typeStatesA.add(typeStateA);
        typeStatesA.add(typeStateB);
        subsetStateA = new SubsetTypeState(typeStatesA);

        // Set up new product state B
        typeStatesB = new LinkedHashSet<TypeState>();
        typeStatesB.add(typeStateB);
        subsetStateB = new SubsetTypeState(typeStatesB);

        // Get new transition
        Element element = new Element("{}element");
        transition = new Transition(subsetStateA, subsetStateB);
        transition.addElement(element);
    }

    /**
     * Test of addState method, of class SubsetTypeAutomaton for a state, which
     * does not exist in the automaton.
     */
    @Test
    public void testAddStateNonExisting() throws InvalidTypeStateContentException {
        SubsetTypeAutomaton instance = new SubsetTypeAutomaton(subsetStateA);

        boolean expResult = true;
        boolean result = instance.addState(subsetStateB);

        assertEquals(expResult, result);
        assertTrue(instance.getStates().contains(subsetStateB));
    }

    /**
     * Test of addState method, of class SubsetTypeAutomaton for a state, which
     * does exist in the automaton.
     */
    @Test
    public void testAddStateExisting() throws InvalidTypeStateContentException {
        SubsetTypeAutomaton instance = new SubsetTypeAutomaton(subsetStateA);

        boolean expResult = false;
        boolean result = instance.addState(subsetStateA);

        assertEquals(expResult, result);
    }

    /**
     * Test of removeState method, of class SubsetTypeAutomaton for a state,
     * which does exist in the automaton.
     */
    @Test
    public void testRemoveStateExisting() throws InvalidTypeStateContentException {
        SubsetTypeAutomaton instance = new SubsetTypeAutomaton(subsetStateA);

        boolean expResult = true;
        boolean result = instance.removeState(subsetStateA);

        assertEquals(expResult, result);
        assertTrue(!instance.getStates().contains(subsetStateA));
    }

    /**
     * Test of removeState method, of class SubsetTypeAutomaton for a state,
     * which does not exist in the automaton.
     */
    @Test
    public void testRemoveStateNonExisting() throws InvalidTypeStateContentException {
        SubsetTypeAutomaton instance = new SubsetTypeAutomaton(subsetStateA);

        boolean expResult = false;
        boolean result = instance.removeState(subsetStateB);

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class SubsetTypeAutomaton for a contained
     * state.
     */
    @Test
    public void testContainsStateTrue() throws InvalidTypeStateContentException, EmptySubsetTypeStateFieldException {
        SubsetTypeAutomaton instance = new SubsetTypeAutomaton(subsetStateA);
        instance.addState(subsetStateB);

        TypeState expResult = (TypeState) subsetStateB;
        TypeState result = instance.containsState(new SubsetTypeState(typeStatesB));

        assertEquals(expResult, result);
    }

    /**
     * Test of containsState method, of class SubsetTypeAutomaton for a not contained
     * state.
     */
    @Test
    public void testContainsStateFalse() throws InvalidTypeStateContentException {
        SubsetTypeAutomaton instance = new SubsetTypeAutomaton(subsetStateA);

        TypeState expResult = (TypeState) subsetStateB;
        TypeState result = instance.containsState(expResult);

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ProductTypeAutomaton.
     */
    @Test
    public void testToString() throws InvalidTypeStateContentException {
        SubsetTypeAutomaton instance = new SubsetTypeAutomaton(subsetStateA);
        instance.addState(subsetStateB);
        instance.addTransition(transition);

        String expResult = "digraph G { \n graph [rankdir=LR] \n\"(simpleTypeA,simpleTypeB)\"->\"(simpleTypeB)\"[label=\"({}element)\"]\n}";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}