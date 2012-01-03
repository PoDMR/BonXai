package eu.fox7.bonxai.relaxng;

import org.junit.Test;

import eu.fox7.bonxai.relaxng.Element;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.ExternalRef;
import eu.fox7.bonxai.relaxng.Name;
import eu.fox7.bonxai.relaxng.NameClass;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.Text;
import static org.junit.Assert.*;

/**
 * Test of class Element
 * @author Lars Schmidt
 */
public class ElementTest extends junit.framework.TestCase {

    /**
     * Test of getNameClass method, of class Element.
     */
    @Test
    public void testGetNameClass() {
        Element instance = new Element();
        NameClass expResult = new Name("testName");
        instance.setNameClass(expResult);
        NameClass result = instance.getNameClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNameClass method, of class Element.
     */
    @Test
    public void testSetNameClass() {
        Element instance = new Element();
        NameClass expResult = new Name("testName");
        instance.setNameClass(expResult);
        NameClass result = instance.getNameClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPatterns method, of class Element.
     */
    @Test
    public void testGetPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Element instance = new Element();
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of addPattern method, of class Element.
     */
    @Test
    public void testAddPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Element instance = new Element();
        instance.addPattern(pattern2);
        instance.addPattern(pattern);
        instance.addPattern(pattern3);
        assertEquals(pattern2, instance.getPatterns().getFirst());
        assertEquals(pattern, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of getNameAttribute method, of class Element.
     */
    @Test
    public void testGetNameAttribute() {
        Element instance = new Element();
        String expResult = "attributeElementName";
        instance.setNameAttribute(expResult);
        String result = instance.getNameAttribute();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNameAttribute method, of class Element.
     */
    @Test
    public void testSetNameAttribute() {
        Element instance = new Element();
        String expResult = "attributeElementName";
        instance.setNameAttribute(expResult);
        String result = instance.getNameAttribute();
        assertEquals(expResult, result);
    }
}
