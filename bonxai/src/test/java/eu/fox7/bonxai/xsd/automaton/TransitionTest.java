package eu.fox7.bonxai.xsd.automaton;

import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.XSDSchema.Qualification;
import eu.fox7.bonxai.xsd.automaton.State;
import eu.fox7.bonxai.xsd.automaton.Transition;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.TypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>Transition</tt> class, checks that every method of this
 * class performs properly.
 * @author Dominik Wolff
 */
public class TransitionTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private State sourceState;
    private SimpleType simpleTypeB;
    private State destinationState;
    private Element elementA;
    private Element elementB;

    public TransitionTest() {
    }

    @Before
    public void setUp() throws InvalidTypeStateContentException {

        // Set up new source state
        simpleTypeA = new SimpleType("{}simpleTypeA", null);
        sourceState = new TypeState(simpleTypeA);

        // Set up new destination state
        simpleTypeB = new SimpleType("{}simpleTypeB", null);
        destinationState = new TypeState(simpleTypeB);

        // Set up element foo and bar
        elementA = new Element("{A}foo");
        elementB = new Element("{A}bar");
    }

    /**
     * Test of getSourceState method, of class Transition.
     */
    @Test
    public void testGetSourceState() {
        Transition instance = new Transition(sourceState, destinationState);

        State expResult = sourceState;
        State result = instance.getSourceState();

        assertEquals(expResult, result);
    }

    /**
     * Test of getDestinationState method, of class Transition.
     */
    @Test
    public void testGetDestinationState() {
        Transition instance = new Transition(sourceState, destinationState);

        State expResult = destinationState;
        State result = instance.getDestinationState();

        assertEquals(expResult, result);
    }

    /**
     * Test of getElements method, of class Transition for no existing elements.
     */
    @Test
    public void testGetElementsEmpty() {
        Transition instance = new Transition(sourceState, destinationState);

        LinkedHashSet<Element> expResult = new LinkedHashSet<Element>();
        LinkedHashSet<Element> result = instance.getElements();

        assertEquals(expResult, result);
    }

    /**
     * Test of getElements method, of class Transition for existing elements.
     */
    @Test
    public void testGetElementsNotEmpty() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);
        instance.addElement(elementB);

        LinkedHashSet<Element> expResult = new LinkedHashSet<Element>();
        expResult.add(elementA);
        expResult.add(elementB);
        LinkedHashSet<Element> result = instance.getElements();

        assertEquals(expResult, result);
    }

    /**
     * Test of addElement method, of class Transition for an element, which is
     * not present in the list.
     */
    @Test
    public void testAddElementNotInList() {
        Transition instance = new Transition(sourceState, destinationState);

        boolean expResult = true;
        boolean result = instance.addElement(elementA);

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
    }

    /**
     * Test of addElement method, of class Transition for an element, which is
     * not present in the list but another element with same name.
     */
    @Test
    public void testAddElementNotSameInList() {
        Transition instance = new Transition(sourceState, destinationState);
        Element tempElement = new Element("{}foo");
        instance.addElement(tempElement);

        boolean expResult = true;
        boolean result = instance.addElement(elementA);

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(tempElement));
    }

    /**
     * Test of addElement method, of class Transition for an element, which is
     * present in the list.
     */
    @Test
    public void testAddElementInList() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);

        boolean expResult = false;
        boolean result = instance.addElement(elementA);

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
    }

    /**
     * Test of addAllElements method, of class Transition.
     */
    @Test
    public void testAddAllElements() {
        Transition instance = new Transition(sourceState, destinationState);
        LinkedHashSet<Element> elementSet = new LinkedHashSet<Element>();
        elementSet.add(elementA);
        elementSet.add(elementB);
        instance.addAllElements(elementSet);

        LinkedHashSet<Element> expResult = elementSet;
        LinkedHashSet<Element> result = instance.getElements();

        assertEquals(expResult, result);
    }

    /**
     * Test of removeElement method, of class Transition, which is present in
     * the list.
     */
    @Test
    public void testRemoveElementInList() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);

        boolean expResult = true;
        boolean result = instance.removeElement(elementA);

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(!instance.getNameElementMap().get("{}foo").contains(elementA));
    }

    /**
     * Test of removeElement method, of class Transition, which is not present
     * in the list.
     */
    @Test
    public void testRemoveElementNotInList() {
        Transition instance = new Transition(sourceState, destinationState);

        boolean expResult = false;
        boolean result = instance.removeElement(elementA);

        assertEquals(expResult, result);
        assertTrue(!instance.getNameElementMap().containsKey("{}foo"));
    }

    /**
     * Test of clearElements method, of class Transition.
     */
    @Test
    public void testClearElements() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);
        instance.clearElements();

        boolean expResult = true;
        boolean result = instance.getElements().isEmpty();

        assertEquals(expResult, result);
    }

    /**
     * Test of getNameElementMap method, of class Transition for elements with
     * different names.
     */
    @Test
    public void testGetNameElementMapElementsWithDifferentNames() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);
        instance.addElement(elementB);

        HashMap<String, LinkedHashSet<Element>> expResult = new HashMap<String, LinkedHashSet<Element>>();

        LinkedHashSet<Element> linkedHashSet = new LinkedHashSet<Element>();
        linkedHashSet.add(elementA);
        expResult.put("{}foo", linkedHashSet);

        linkedHashSet = new LinkedHashSet<Element>();
        linkedHashSet.add(elementB);
        expResult.put("{}bar", linkedHashSet);

        HashMap<String, LinkedHashSet<Element>> result = instance.getNameElementMap();

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
        assertTrue(instance.getNameElementMap().containsKey("{}bar"));
        assertTrue(instance.getNameElementMap().get("{}bar").contains(elementB));
    }

    /**
     * Test of getNameElementMap method, of class Transition for elements with
     * same names.
     */
    @Test
    public void testGetNameElementMapElementsWithSameNames() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);
        Element tempElement = new Element("{}foo");
        instance.addElement(tempElement);

        HashMap<String, LinkedHashSet<Element>> expResult = new HashMap<String, LinkedHashSet<Element>>();

        LinkedHashSet<Element> linkedHashSet = new LinkedHashSet<Element>();
        linkedHashSet.add(elementA);
        linkedHashSet.add(tempElement);
        expResult.put("{}foo", linkedHashSet);

        HashMap<String, LinkedHashSet<Element>> result = instance.getNameElementMap();

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(tempElement));
    }

    /**
     * Test of getNameElementMap method, of class Transition for elements with
     * different qualifications.
     */
    @Test
    public void testGetNameElementMapElementsWithDifferentQualifications() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);
        elementB.setForm(Qualification.qualified);
        instance.addElement(elementB);

        HashMap<String, LinkedHashSet<Element>> expResult = new HashMap<String, LinkedHashSet<Element>>();

        LinkedHashSet<Element> linkedHashSet = new LinkedHashSet<Element>();
        linkedHashSet.add(elementA);
        expResult.put("{}foo", linkedHashSet);

        linkedHashSet = new LinkedHashSet<Element>();
        linkedHashSet.add(elementB);
        expResult.put("{A}bar", linkedHashSet);

        HashMap<String, LinkedHashSet<Element>> result = instance.getNameElementMap();

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
        assertTrue(instance.getNameElementMap().containsKey("{A}bar"));
        assertTrue(instance.getNameElementMap().get("{A}bar").contains(elementB));
    }

    /**
     * Test of toString method, of class Transition.
     */
    @Test
    public void testToString() {
        Transition instance = new Transition(sourceState, destinationState);
        instance.addElement(elementA);
        instance.addElement(elementB);

        String expResult = "\"simpleTypeA\"->\"simpleTypeB\"[label=\"({A}foo,{A}bar)\"]\n";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}