package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedHashSet;
import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.NsName;
import static org.junit.Assert.*;

/**
 * Test of class NsName
 * @author Lars Schmidt
 */
public class NsNameTest extends junit.framework.TestCase {

    /**
     * Test of getExceptNames method, of class NsName.
     */
    @Test
    public void testGetExceptNames() {
        NsName instance = new NsName();
        Name name = new Name("myName");
        instance.addExceptName(name);
        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertTrue(result.contains(name));
    }

    /**
     * Test of addExceptName method, of class NsName.
     */
    @Test
    public void testAddExceptName() {
        NsName instance = new NsName();
        Name name = new Name("myName");
        instance.addExceptName(name);
        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertTrue(result.contains(name));
    }

    /**
     * Test of getAttributeNamespace method, of class NsName.
     */
    @Test
    public void testGetAttributeNamespace() {
        NsName instance = new NsName();
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamespace method, of class NsName.
     */
    @Test
    public void testSetAttributeNamespace() {
        NsName instance = new NsName();
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

}