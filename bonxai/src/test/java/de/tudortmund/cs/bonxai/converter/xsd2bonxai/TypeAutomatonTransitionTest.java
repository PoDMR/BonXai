package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.xsd.*;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

public class TypeAutomatonTransitionTest extends junit.framework.TestCase {

    private Content testContent;
    private ComplexType testComplexType;
    private TypeAutomatonState testTypeAutomatonState;
    private Content secondTestContent;
    private ComplexType secondTestComplexType;
    private TypeAutomatonState secondTestTypeAutomatonState;
    private TypeAutomatonTransition instance;

    @Before
    @Override
    public void setUp() {
        testContent = new ComplexContentType();
        testComplexType = new ComplexType("{http://example.com/mynamespace}complexType", testContent);
        testTypeAutomatonState = new TypeAutomatonState(testComplexType);

        secondTestContent = new ComplexContentType();
        secondTestComplexType = new ComplexType("{http://example.com/mynamespace}second complexType", secondTestContent);
        secondTestTypeAutomatonState = new TypeAutomatonState(secondTestComplexType);

        instance = new TypeAutomatonTransition(testTypeAutomatonState, secondTestTypeAutomatonState);
    }

    /**
     * Test of addElement method and getElements method, of class TypeAutomatonTransition.
     */
    @Test
    public void testAddElementAndGetElements() {

        //Case: Null-pointer as parameter and no elements in list
        instance.addElement(null);
        LinkedList<Element> result = instance.getElements();
        LinkedList<Element> expResult = null;
        assertEquals(result, expResult);

        //Case: Element parameter and no elements in list
        Element testElement = new Element("{http://example.com/mynamespace}element");
        instance.addElement(testElement);
        result = instance.getElements();
        expResult = new LinkedList<Element>();
        expResult.add(testElement);
        assertEquals(result, expResult);

        //Case: Null-pointer as parameter and elements in list
        instance.addElement(null);
        result = instance.getElements();
        expResult = new LinkedList<Element>();
        expResult.add(testElement);
        assertEquals(result, expResult);

        //Case: Element as parameter and same element in list
        instance.addElement(testElement);
        result = instance.getElements();
        expResult = new LinkedList<Element>();
        expResult.add(testElement);
        assertEquals(result, expResult);

        //Case: Element as parameter and element in list
        Element secondTestElement = new Element("{http://example.com/mynamespace}second element");
        instance.addElement(secondTestElement);
        result = instance.getElements();
        expResult = new LinkedList<Element>();
        expResult.add(testElement);
        expResult.add(secondTestElement);
        assertEquals(result, expResult);
    }

    /**
     * Test of getSource method, of class TypeAutomatonTransition.
     */
    @Test
    public void testGetSource() {
        TypeAutomatonState expResult = testTypeAutomatonState;
        TypeAutomatonState result = instance.getSource();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDestination method, of class TypeAutomatonTransition.
     */
    @Test
    public void testGetDestination() {
        TypeAutomatonState expResult = secondTestTypeAutomatonState;
        TypeAutomatonState result = instance.getDestination();
        assertEquals(expResult, result);
    }
}
