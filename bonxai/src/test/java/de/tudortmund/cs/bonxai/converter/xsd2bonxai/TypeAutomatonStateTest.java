package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.xsd.*;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

public class TypeAutomatonStateTest extends junit.framework.TestCase {

    private Content testContent;
    private ComplexType testComplexType;
    private TypeAutomatonState instance;
    private Content secondTestContent;
    private ComplexType secondTestComplexType;
    private TypeAutomatonState secondInstance;
    private Content thirdTestContent;
    private ComplexType thirdTestComplexType;
    private TypeAutomatonState thirdInstance;

    @Before
    @Override
    public void setUp() {
        testContent = new ComplexContentType();
        testComplexType = new ComplexType("{http://example.com/mynamespace}complexType", testContent);
        instance = new TypeAutomatonState(testComplexType);

        secondTestContent = new ComplexContentType();
        secondTestComplexType = new ComplexType("{http://example.com/mynamespace}second complexType", secondTestContent);
        secondInstance = new TypeAutomatonState(secondTestComplexType);

        thirdTestContent = new ComplexContentType();
        thirdTestComplexType = new ComplexType("{http://example.com/mynamespace}third complexType", thirdTestContent);
        thirdInstance = new TypeAutomatonState(thirdTestComplexType);
    }

    /**
     * Test of addInTransition method and getInTransitions method, of class TypeAutomatonState.
     */
    @Test
    public void testAddInTransitionAndGetInTransitions() {

        //Case: Null-pointer as parameter and no transition in list
        TypeAutomatonTransition result = instance.addInTransition(null);
        assertEquals(result, null);
        LinkedList<TypeAutomatonTransition> resultList = instance.getInTransitions();
        LinkedList<TypeAutomatonTransition> expResult = null;
        assertEquals(resultList, expResult);

        //Case: Transition as parameter and no transition in
        TypeAutomatonTransition testInTransition = new TypeAutomatonTransition(secondInstance, instance);
        Element testInElement = new Element("{http://example.com/mynamespace}element");
        testInTransition.addElement(testInElement);
        result = instance.addInTransition(testInTransition);
        assertEquals(result, testInTransition);
        resultList = instance.getInTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        expResult.add(testInTransition);
        assertEquals(resultList, expResult);

        //Case: Null-pointer as parameter and transition in list
        result = instance.addInTransition(null);
        assertEquals(result, null);
        resultList = instance.getInTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        expResult.add(testInTransition);
        assertEquals(resultList, expResult);

        //Case: Transition as parameter and same transition in list
        TypeAutomatonTransition secondTestInTransition = new TypeAutomatonTransition(secondInstance, instance);
        Element secondTestInElement = new Element("{http://example.com/mynamespace}second element");
        secondTestInTransition.addElement(secondTestInElement);
        result = instance.addInTransition(secondTestInTransition);
        assertEquals(result, testInTransition);
        resultList = instance.getInTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        TypeAutomatonTransition testTransitionCopy = new TypeAutomatonTransition(secondInstance, instance);
        testTransitionCopy.addElement(testInElement);
        testTransitionCopy.addElement(secondTestInElement);
        expResult.add(testTransitionCopy);
        assertTrue(resultList.size() == expResult.size());
        if (resultList.size() == expResult.size()) {
            for (int i = 0; i < resultList.size(); i++) {
                assertEquals(resultList.get(i).getDestination(), expResult.get(i).getDestination());
                assertEquals(resultList.get(i).getSource(), expResult.get(i).getSource());
                assertEquals(resultList.get(i).getElements(), expResult.get(i).getElements());
            }
        }

        //Case: Transition as parameter and transition in list
        TypeAutomatonTransition thirdTestInTransition = new TypeAutomatonTransition(thirdInstance, instance);
        Element thirdTestInElement = new Element("{http://example.com/mynamespace}third element");
        thirdTestInTransition.addElement(thirdTestInElement);
        result = instance.addInTransition(thirdTestInTransition);
        assertEquals(result, thirdTestInTransition);
        resultList = instance.getInTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        testTransitionCopy = new TypeAutomatonTransition(secondInstance, instance);
        testTransitionCopy.addElement(testInElement);
        testTransitionCopy.addElement(secondTestInElement);
        TypeAutomatonTransition secondTestTransitionCopy = new TypeAutomatonTransition(thirdInstance, instance);
        secondTestTransitionCopy.addElement(thirdTestInElement);
        expResult.add(testTransitionCopy);
        expResult.add(secondTestTransitionCopy);
        assertTrue(resultList.size() == expResult.size());
        if (resultList.size() == expResult.size()) {
            for (int i = 0; i < resultList.size(); i++) {
                assertEquals(resultList.get(i).getDestination(), expResult.get(i).getDestination());
                assertEquals(resultList.get(i).getSource(), expResult.get(i).getSource());
                assertEquals(resultList.get(i).getElements(), expResult.get(i).getElements());
            }
        }
    }

    /**
     * Test of addOutTransition method and getOutTransitions method, of class TypeAutomatonState.
     */
    @Test
    public void testAddOutTransitionAndGetOutTransitions() {

        //Case: Null-pointer as parameter and no transition in list
        TypeAutomatonTransition result = instance.addOutTransition(null);
        assertEquals(result, null);
        LinkedList<TypeAutomatonTransition> resultList = instance.getOutTransitions();
        LinkedList<TypeAutomatonTransition> expResult = null;
        assertEquals(resultList, expResult);

        //Case: Transition as parameter and no transition in
        TypeAutomatonTransition testOutTransition = new TypeAutomatonTransition(instance, secondInstance);
        Element testOutElement = new Element("{http://example.com/mynamespace}element");
        testOutTransition.addElement(testOutElement);
        result = instance.addOutTransition(testOutTransition);
        assertEquals(result, testOutTransition);
        resultList = instance.getOutTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        expResult.add(testOutTransition);
        assertEquals(resultList, expResult);

        //Case: Null-pointer as parameter and transition in list
        result = instance.addOutTransition(null);
        assertEquals(result, null);
        resultList = instance.getOutTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        expResult.add(testOutTransition);
        assertEquals(resultList, expResult);

        //Case: Transition as parameter and same transition in list
        TypeAutomatonTransition secondTestOutTransition = new TypeAutomatonTransition(instance, secondInstance);
        Element secondTestOutElement = new Element("{http://example.com/mynamespace}second element");
        secondTestOutTransition.addElement(secondTestOutElement);
        result = instance.addOutTransition(secondTestOutTransition);
        assertEquals(result, testOutTransition);
        resultList = instance.getOutTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        TypeAutomatonTransition testTransitionCopy = new TypeAutomatonTransition(instance, secondInstance);
        testTransitionCopy.addElement(testOutElement);
        testTransitionCopy.addElement(secondTestOutElement);
        expResult.add(testTransitionCopy);
        assertTrue(resultList.size() == expResult.size());
        if (resultList.size() == expResult.size()) {
            for (int i = 0; i < resultList.size(); i++) {
                assertEquals(resultList.get(i).getDestination(), expResult.get(i).getDestination());
                assertEquals(resultList.get(i).getSource(), expResult.get(i).getSource());
                assertEquals(resultList.get(i).getElements(), expResult.get(i).getElements());
            }
        }

        //Case: Transition as parameter and transition in list
        TypeAutomatonTransition thirdTestOutTransition = new TypeAutomatonTransition(instance, thirdInstance);
        Element thirdTestOutElement = new Element("{http://example.com/mynamespace}third element");
        thirdTestOutTransition.addElement(thirdTestOutElement);
        result = instance.addOutTransition(thirdTestOutTransition);
        assertEquals(result, thirdTestOutTransition);
        resultList = instance.getOutTransitions();
        expResult = new LinkedList<TypeAutomatonTransition>();
        testTransitionCopy = new TypeAutomatonTransition(instance, secondInstance);
        testTransitionCopy.addElement(testOutElement);
        testTransitionCopy.addElement(secondTestOutElement);
        TypeAutomatonTransition secondTestTransitionCopy = new TypeAutomatonTransition(instance, thirdInstance);
        secondTestTransitionCopy.addElement(thirdTestOutElement);
        expResult.add(testTransitionCopy);
        expResult.add(secondTestTransitionCopy);
        assertTrue(resultList.size() == expResult.size());
        if (resultList.size() == expResult.size()) {
            for (int i = 0; i < resultList.size(); i++) {
                assertEquals(resultList.get(i).getDestination(), expResult.get(i).getDestination());
                assertEquals(resultList.get(i).getSource(), expResult.get(i).getSource());
                assertEquals(resultList.get(i).getElements(), expResult.get(i).getElements());
            }
        }
    }

    /**
     * Test of getType method, of class TypeAutomatonState.
     */
    @Test
    public void testGetType() {
        ComplexType expResult = testComplexType;
        ComplexType result = instance.getType();
        assertEquals(expResult, result);
    }
}
