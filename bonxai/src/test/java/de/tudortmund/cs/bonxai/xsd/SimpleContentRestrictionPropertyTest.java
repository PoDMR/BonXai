package de.tudortmund.cs.bonxai.xsd;


import org.junit.Test;

/**
 *
 */
public class SimpleContentRestrictionPropertyTest extends junit.framework.TestCase {


    public SimpleContentRestrictionPropertyTest() {
    }


    /**
     * Test of setValue method, of class SimpleContentRestrictionProperty.
     */
    @Test
    public void testSetValue() {
        SimpleContentRestrictionProperty<String> instance = new SimpleContentRestrictionProperty<String>("foo");
        assertEquals("foo", instance.getValue());
        instance.setValue("bar");
        assertEquals("bar", instance.getValue());
    }

    /**
     * Test of getValue method, of class SimpleContentRestrictionProperty.
     */
    @Test
    public void testGetValue() {
        Object expResult = new Object();
        SimpleContentRestrictionProperty<Object> instance = new SimpleContentRestrictionProperty<Object>(expResult);
        Object result = instance.getValue();
        assertEquals(expResult, result);

    }



}