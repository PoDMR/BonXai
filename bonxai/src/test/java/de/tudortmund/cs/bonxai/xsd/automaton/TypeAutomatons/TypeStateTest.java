package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;
import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>TypeState</tt> class, checks that every method of this
 * class performs properly.
 * @author Dominik Wolff
 */
public class TypeStateTest extends junit.framework.TestCase{

    private SimpleType simpleType;

    public TypeStateTest() {
    }

    @Before
    public void setUp() {

        // New simpleType
        simpleType = new SimpleType("{}simpleType", null);
    }

    /**
     * Test of getTypes method, of class TypeState.
     */
    @Test
    public void testGetTypes() throws InvalidTypeStateContentException {
        TypeState instance = new TypeState(simpleType);

        LinkedHashSet<Type> expResult = new LinkedHashSet<Type>();
        expResult.add(simpleType);
        LinkedHashSet<Type> result = instance.getTypes();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class TypeState for a type, which is not
     * empty.
     */
    @Test
    public void testToStringNotEmptyType() throws InvalidTypeStateContentException {
        TypeState instance = new TypeState(simpleType);

        String expResult = simpleType.getLocalName();
        String result = instance.toString();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class TypeState for a type, which is empty.
     */
    @Test
    public void testToStringEmptyType() throws InvalidTypeStateContentException {
        TypeState instance = new TypeStartState();

        String expResult = "sink";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}