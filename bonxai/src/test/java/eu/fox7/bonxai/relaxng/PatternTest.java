package eu.fox7.bonxai.relaxng;

import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.relaxng.Pattern;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class Pattern
 * @author Lars Schmidt
 */
public class PatternTest extends junit.framework.TestCase {

    /**
     * Test of getAttributeDatatypeLibrary method, of class Pattern.
     */
    @Test
    public void testGetAttributeDatatypeLibrary() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeDatatypeLibrary("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeDatatypeLibrary();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeDatatypeLibrary method, of class Pattern.
     */
    @Test
    public void testSetAttributeDatatypeLibrary() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeDatatypeLibrary("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeDatatypeLibrary();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAttributeNamespace method, of class Pattern.
     */
    @Test
    public void testGetAttributeNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamespace method, of class Pattern.
     */
    @Test
    public void testSetAttributeNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultNamespace method, of class Pattern.
     */
    @Test
    public void testGetDefaultNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setDefaultNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultNamespace method, of class Pattern.
     */
    @Test
    public void testSetDefaultNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setDefaultNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespaceList method, of class Pattern.
     */
    @Test
    public void testGetNamespaceList() {
        Pattern instance = new Pattern() {
        };
        NamespaceList result = instance.getNamespaceList();
        assertNotNull(result);
        assertEquals(null, result.getDefaultNamespace());
        assertEquals(0, result.getIdentifiedNamespaces().size());
    }
}
