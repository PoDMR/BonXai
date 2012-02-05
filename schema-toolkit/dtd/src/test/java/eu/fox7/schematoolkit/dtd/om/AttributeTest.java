package eu.fox7.schematoolkit.dtd.om;

import java.util.LinkedHashSet;
import org.junit.Test;

import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import static org.junit.Assert.*;

/**
 * Test of class Attribute in package DTD
 * @author Lars Schmidt
 */
public class AttributeTest extends junit.framework.TestCase {

    public AttributeTest() {
    }

    /**
     * Test of setType method, of class Attribute.
     */
    @Test
    public void testSetType() {
        Attribute attribute = new Attribute("name", "value");

        assertEquals(null, attribute.getType());

        attribute.setType(AttributeType.ID);
        assertEquals(AttributeType.ID, attribute.getType());
        attribute.setType(AttributeType.IDREF);
        assertEquals(AttributeType.IDREF, attribute.getType());
        attribute.setType(AttributeType.IDREFS);
        assertEquals(AttributeType.IDREFS, attribute.getType());
        attribute.setType(AttributeType.CDATA);
        assertEquals(AttributeType.CDATA, attribute.getType());
        attribute.setType(AttributeType.ENTITIES);
        assertEquals(AttributeType.ENTITIES, attribute.getType());
        attribute.setType(AttributeType.ENTITY);
        assertEquals(AttributeType.ENTITY, attribute.getType());
        attribute.setType(AttributeType.ENUMERATION);
        assertEquals(AttributeType.ENUMERATION, attribute.getType());
        attribute.setType(AttributeType.NMTOKEN);
        assertEquals(AttributeType.NMTOKEN, attribute.getType());
        attribute.setType(AttributeType.NMTOKENS);
        assertEquals(AttributeType.NMTOKENS, attribute.getType());
        attribute.setType(AttributeType.NOTATION);
        assertEquals(AttributeType.NOTATION, attribute.getType());
    }

    /**
     * Test of getEnumerationOrNotationTokens method, of class Attribute.
     */
    @Test
    public void testGetEnumerationOrNotationTokens() {
        Attribute attribute = new Attribute("name", "value");
        attribute.setType(AttributeType.ENUMERATION);
        LinkedHashSet<String> expResult = new LinkedHashSet<String>();
        expResult.add("test");
        expResult.add("temp");
        attribute.setEnumerationOrNotationTokens(expResult);
        LinkedHashSet<String> result = attribute.getEnumerationOrNotationTokens();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAttributeDefaultPresence method, of class Attribute.
     */
    @Test
    public void testGetAttributeDefaultPresence() {
        Attribute attribute = new Attribute("name", "value");
        assertEquals(null, attribute.getAttributeDefaultPresence());
        Attribute attribute1 = new Attribute("name", "mode", "value");
        assertEquals(null, attribute1.getAttributeDefaultPresence());
        Attribute attribute2 = new Attribute("name", "#REQUIRED", "value");
        assertEquals(Attribute.AttributeDefaultPresence.REQUIRED, attribute2.getAttributeDefaultPresence());
        Attribute attribute3 = new Attribute("name", "#required", "value");
        assertEquals(null, attribute3.getAttributeDefaultPresence());
        Attribute attribute4 = new Attribute("name", "#FIXED", "value");
        assertEquals(Attribute.AttributeDefaultPresence.FIXED, attribute4.getAttributeDefaultPresence());
        Attribute attribute5 = new Attribute("name", "#IMPLIED", "value");
        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, attribute5.getAttributeDefaultPresence());
    }

    /**
     * Test of getName method, of class Attribute.
     */
    @Test
    public void testGetName() {
        Attribute attribute = new Attribute("name", "value");
        assertFalse(attribute.getName() == null);
        assertEquals("name", attribute.getName());
    }

    /**
     * Test of getType method, of class Attribute.
     */
    @Test
    public void testGetType() {
        Attribute attribute = new Attribute("name", "value");

        assertEquals(null, attribute.getType());

        attribute.setType(AttributeType.ID);
        assertEquals(AttributeType.ID, attribute.getType());
        attribute.setType(AttributeType.IDREF);
        assertEquals(AttributeType.IDREF, attribute.getType());
        attribute.setType(AttributeType.IDREFS);
        assertEquals(AttributeType.IDREFS, attribute.getType());
        attribute.setType(AttributeType.CDATA);
        assertEquals(AttributeType.CDATA, attribute.getType());
        attribute.setType(AttributeType.ENTITIES);
        assertEquals(AttributeType.ENTITIES, attribute.getType());
        attribute.setType(AttributeType.ENTITY);
        assertEquals(AttributeType.ENTITY, attribute.getType());
        attribute.setType(AttributeType.ENUMERATION);
        assertEquals(AttributeType.ENUMERATION, attribute.getType());
        attribute.setType(AttributeType.NMTOKEN);
        assertEquals(AttributeType.NMTOKEN, attribute.getType());
        attribute.setType(AttributeType.NMTOKENS);
        assertEquals(AttributeType.NMTOKENS, attribute.getType());
        attribute.setType(AttributeType.NOTATION);
        assertEquals(AttributeType.NOTATION, attribute.getType());
    }

    /**
     * Test of getValue method, of class Attribute.
     */
    @Test
    public void testGetValue() {
        Attribute attribute = new Attribute("name", "value");
        assertFalse(attribute.getValue() == null);
        assertEquals("value", attribute.getValue());
        Attribute attribute2 = new Attribute("name", null);
        assertTrue(attribute2.getValue() == null);
    }

    /**
     * Test of setEnumerationOrNotationTokens method, of class Attribute.
     */
    @Test
    public void testSetEnumerationOrNotationTokens() {
        Attribute attribute = new Attribute("name", "value");
        attribute.setType(AttributeType.ENUMERATION);
        LinkedHashSet<String> expResult = new LinkedHashSet<String>();
        expResult.add("test");
        expResult.add("temp");
        attribute.setEnumerationOrNotationTokens(expResult);
        LinkedHashSet<String> result = attribute.getEnumerationOrNotationTokens();
        assertEquals(expResult, result);
    }
}
