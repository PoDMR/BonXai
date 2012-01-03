package eu.fox7.bonxai.relaxng;

import org.junit.Test;

import eu.fox7.bonxai.relaxng.Name;

/**
 * Test of class Name
 * @author Lars Schmidt
 */
public class NameTest extends junit.framework.TestCase {


    /**
     * Test of getAttributeNamespace method, of class Name.
     */
    @Test
    public void testGetAttributeNamespace() {
        Name instance = new Name("myName");
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamespace method, of class Name.
     */
    @Test
    public void testSetAttributeNamespace() {
        Name instance = new Name("myName");
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContent method, of class Name.
     */
    @Test
    public void testGetContent() {
        Name instance = new Name("myName");
        assertEquals("myName", instance.getContent());
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setContent method, of class Name.
     */
    @Test
    public void testSetContent() {
        Name instance = new Name("myName");
        assertEquals("myName", instance.getContent());
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

}