package de.tudortmund.cs.bonxai.relaxng;

import java.util.LinkedHashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AnyName
 * @author Lars Schmidt
 */
public class AnyNameTest extends junit.framework.TestCase {

    /**
     * Test of getExceptNames method, of class AnyName.
     */
    @Test
    public void testGetExceptNames() {
        AnyName instance = new AnyName();

        instance.addExceptName(new Name("myName"));
        instance.addExceptName(new Name("mySecondName"));

        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertEquals(2, result.size());
        assertTrue(((Name)result.iterator().next()).getContent().equals("myName"));
    }

    /**
     * Test of addExceptName method, of class AnyName.
     */
    @Test
    public void testAddExceptName() {
        AnyName instance = new AnyName();

        instance.addExceptName(new Name("myName"));
        instance.addExceptName(new Name("mySecondName"));

        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertEquals(2, result.size());
        assertTrue(((Name)result.iterator().next()).getContent().equals("myName"));
    }

}