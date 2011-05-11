
package de.tudortmund.cs.bonxai.relaxng;

import org.junit.Test;

/**
 * Test of class Param
 * @author Lars Schmidt
 */
public class ParamTest extends junit.framework.TestCase {

    /**
     * Test of getName method, of class Param.
     */
    @Test
    public void testGetName() {
        Param instance = new Param("myName");
        String result = instance.getName();
        assertEquals("myName", result);
        instance.setName("example");
        String expResult2 = "example";
        String result2 = instance.getName();
        assertEquals(expResult2, result2);
    }

    /**
     * Test of setName method, of class Param.
     */
    @Test
    public void testSetName() {
        Param instance = new Param("myName");
        String result = instance.getName();
        assertEquals("myName", result);
        instance.setName("example");
        String expResult2 = "example";
        String result2 = instance.getName();
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getContent method, of class Param.
     */
    @Test
    public void testGetContent() {
        Param instance = new Param("myName");
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setContent method, of class Param.
     */
    @Test
    public void testSetContent() {
        Param instance = new Param("myName");
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

}