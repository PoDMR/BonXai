package eu.fox7.bonxai.relaxng;

import org.junit.Test;

import eu.fox7.bonxai.relaxng.Value;

/**
 * Test of class Value
 * @author Lars Schmidt
 */
public class ValueTest extends junit.framework.TestCase {

    /**
     * Test of getType method, of class Value.
     */
    @Test
    public void testGetType() {
        Value instance = new Value("content");
        instance.setType("myType");
        String expResult = "myType";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setType method, of class Value.
     */
    @Test
    public void testSetType() {
        Value instance = new Value("content");
        instance.setType("myType");
        String expResult = "myType";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContent method, of class Value.
     */
    @Test
    public void testGetContent() {
        Value instance = new Value("content");
        String expResult = "content";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setContent method, of class Value.
     */
    @Test
    public void testSetContent() {
        Value instance = new Value("content");
        instance.setContent("myNewContent");
        String expResult = "myNewContent";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

}