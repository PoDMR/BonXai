package eu.fox7.bonxai.xsd.automaton.TypeAutomatons;

import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.ProductTypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.TypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ProductTypeState</tt> class, checks that every method of
 * this class performs properly.
 * @author Dominik Wolff
 */
public class ProductTypeStateTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private TypeState stateA;
    private SimpleType simpleTypeB;
    private TypeState stateB;
    private LinkedList<TypeState> typeStates;

    public ProductTypeStateTest() {
    }

    @Before
    public void setUp() throws InvalidTypeStateContentException {

        // Set up new state A
        simpleTypeA = new SimpleType("{}simpleTypeA", null);
        stateA = new TypeState(simpleTypeA);

        // Set up new state B
        simpleTypeB = new SimpleType("{}simpleTypeB", null);
        stateB = new TypeState(simpleTypeB);

        // Set up state set
        typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);
        typeStates.add(null);
        typeStates.add(stateB);
    }

    /**
     * Test of getTypeStates method, of class ProductTypeState.
     */
    @Test
    public void testGetTypeStates() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        ProductTypeState instance = new ProductTypeState(typeStates);

        LinkedList<TypeState> expResult = new LinkedList<TypeState>();
        expResult.add(stateA);
        expResult.add(null);
        expResult.add(stateB);
        LinkedList<TypeState> result = instance.getTypeStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypes method, of class ProductTypeState.
     */
    @Test
    public void testGetTypes() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        ProductTypeState instance = new ProductTypeState(typeStates);

        LinkedHashSet<Type> expResult = new LinkedHashSet<Type>();
        expResult.add(simpleTypeA);
        expResult.add(simpleTypeB);
        LinkedHashSet<Type> result = instance.getTypes();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ProductTypeState.
     */
    @Test
    public void testToString() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        ProductTypeState instance = new ProductTypeState(typeStates);

        String expResult = "(simpleTypeA,sink,simpleTypeB)";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}