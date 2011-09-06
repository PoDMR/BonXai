package eu.fox7.bonxai.xsd.automaton.TypeAutomatons;

import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.SubsetTypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.TypeState;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;

import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>SubsetTypeState</tt> class, checks that every method of
 * this class performs properly.
 * @author Dominik Wolff
 */
public class SubsetTypeStateTest extends junit.framework.TestCase {

    private SimpleType simpleTypeA;
    private TypeState stateA;
    private SimpleType simpleTypeB;
    private TypeState stateB;
    private LinkedHashSet<TypeState> typeStates;

    public SubsetTypeStateTest() {
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
        typeStates = new LinkedHashSet<TypeState>();
        typeStates.add(stateA);
        typeStates.add(stateB);
    }

    /**
     * Test of getTypeStates method, of class SubsetTypeState.
     */
    @Test
    public void testGetTypeStates() throws EmptySubsetTypeStateFieldException, InvalidTypeStateContentException {
        SubsetTypeState instance = new SubsetTypeState(typeStates);

        LinkedHashSet<TypeState> expResult = new LinkedHashSet<TypeState>();
        expResult.add(stateA);
        expResult.add(stateB);
        LinkedHashSet<TypeState> result = instance.getTypeStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypes method, of class SubsetTypeState.
     */
    @Test
    public void testGetTypes() throws EmptySubsetTypeStateFieldException, InvalidTypeStateContentException {
        SubsetTypeState instance = new SubsetTypeState(typeStates);

        LinkedHashSet<Type> expResult = new LinkedHashSet<Type>();
        expResult.add(simpleTypeA);
        expResult.add(simpleTypeB);
        LinkedHashSet<Type> result = instance.getTypes();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class SubsetTypeState.
     */
    @Test
    public void testToString() throws EmptySubsetTypeStateFieldException, InvalidTypeStateContentException {
        SubsetTypeState instance = new SubsetTypeState(typeStates);

        String expResult = "(simpleTypeA,simpleTypeB)";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}