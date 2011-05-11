package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.xsd.*;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

public class TypeAutomatonTest extends junit.framework.TestCase {

    private Content testContent;
    private ComplexType testComplexType;
    private TypeAutomatonState testTypeAutomatonState;
    private Content secondTestContent;
    private ComplexType secondTestComplexType;
    private TypeAutomatonState secondTestTypeAutomatonState;
    private TypeAutomaton instance;

    @Before
    @Override
    public void setUp() {
        testContent = new ComplexContentType();
        testComplexType = new ComplexType("{http://example.com/mynamespace}complexType", testContent);
        testTypeAutomatonState = new TypeAutomatonState(testComplexType);

        secondTestContent = new ComplexContentType();
        secondTestComplexType = new ComplexType("{http://example.com/mynamespace}second complexType", secondTestContent);
        secondTestTypeAutomatonState = new TypeAutomatonState(secondTestComplexType);

        instance = new TypeAutomaton(testTypeAutomatonState);
    }

    /**
     * Test of registerTransition method and lookupTransitions method, of class TypeAutomaton.
     */
    @Test
    public void testRegisterTransitionAndLookupTransitions() {

        //Case: Element not in empty transitionLookup
        LinkedList<TypeAutomatonTransition> result = instance.lookupTransitions("{http://example.com/mynamespace}element");
        LinkedList<TypeAutomatonTransition> expResult = null;
        assertEquals(expResult, result);

        //Case: Element in transitionLookup
        TypeAutomatonTransition testTypeAutomatonTransition = new TypeAutomatonTransition(testTypeAutomatonState, secondTestTypeAutomatonState);
        Element testElement = new Element("{http://example.com/mynamespace}element");
        testTypeAutomatonTransition.addElement(testElement);
        instance.registerTransition("{http://example.com/mynamespace}element", testTypeAutomatonTransition);
        result = instance.lookupTransitions("{http://example.com/mynamespace}element");
        expResult = new LinkedList<TypeAutomatonTransition>();
        expResult.add(testTypeAutomatonTransition);
        assertEquals(expResult, result);

        //Case: Element not in transitionLookup
        result = instance.lookupTransitions("{http://example.com/mynamespace}second element");
        expResult = null;
        assertEquals(expResult, result);

        //Case: Parameter is null for lookupTransitions()
        result = instance.lookupTransitions(null);
        expResult = null;
        assertEquals(expResult, result);


        //Case: Both or one parameter is null for registerTransition()
        instance.registerTransition(null, testTypeAutomatonTransition);
        instance.registerTransition("{http://example.com/mynamespace}second element", null);
        instance.registerTransition(null, null);
        result = instance.lookupTransitions("{http://example.com/mynamespace}element");
        expResult = new LinkedList<TypeAutomatonTransition>();
        expResult.add(testTypeAutomatonTransition);
        assertEquals(expResult, result);
        result = instance.lookupTransitions("{http://example.com/mynamespace}second element");
        expResult = null;
        assertEquals(expResult, result);

        //Case: Transition is appended to same Element
        TypeAutomatonTransition secondTestTypeAutomatonTransition = new TypeAutomatonTransition(secondTestTypeAutomatonState, testTypeAutomatonState);
        Element secondTestElement = new Element("{http://example.com/mynamespace}element");
        secondTestTypeAutomatonTransition.addElement(secondTestElement);
        instance.registerTransition("{http://example.com/mynamespace}element", secondTestTypeAutomatonTransition);
        result = instance.lookupTransitions("{http://example.com/mynamespace}element");
        expResult = new LinkedList<TypeAutomatonTransition>();
        expResult.add(testTypeAutomatonTransition);
        expResult.add(secondTestTypeAutomatonTransition);
        assertEquals(expResult, result);
    }

    /**
     * Test of getStart method and lookupTransitions method, of class TypeAutomaton.
     */
    @Test
    public void testGetStart() {
        TypeAutomatonState result = instance.getStart();
        TypeAutomatonState expResult = testTypeAutomatonState;
        assertEquals(expResult, result);
    }

    /**
     * Test of getStates method, of class TypeAutomaton.
     */
    @Test
    public void testGetStates() {
        LinkedList<TypeAutomatonState> result = instance.getStates();
        assertNull(result);
        TypeAutomatonTransition testTypeAutomatonTransition = new TypeAutomatonTransition(testTypeAutomatonState, secondTestTypeAutomatonState);
        Element testElement = new Element("{http://example.com/mynamespace}element");
        testTypeAutomatonTransition.addElement(testElement);
        instance.registerTransition("{http://example.com/mynamespace}element", testTypeAutomatonTransition);
        result = instance.getStates();
        LinkedList<TypeAutomatonState> expResult = new LinkedList<TypeAutomatonState>();
        expResult.add(testTypeAutomatonState);
        expResult.add(secondTestTypeAutomatonState);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeTransition method, of class TypeAutomaton.
     */
    @Test
    public void testRemoveTransition() {
        TypeAutomatonTransition testTypeAutomatonTransition = new TypeAutomatonTransition(testTypeAutomatonState, secondTestTypeAutomatonState);
        Element testElement = new Element("{http://example.com/mynamespace}element");
        testTypeAutomatonTransition.addElement(testElement);
        instance.registerTransition("{http://example.com/mynamespace}element", testTypeAutomatonTransition);
        instance.removeTransition(testTypeAutomatonTransition);
        LinkedList<TypeAutomatonTransition> result = instance.lookupTransitions("{http://example.com/mynamespace}element");
        LinkedList<TypeAutomatonTransition> expResult = new LinkedList<TypeAutomatonTransition>();
        assertEquals(expResult, result);
    }
}
