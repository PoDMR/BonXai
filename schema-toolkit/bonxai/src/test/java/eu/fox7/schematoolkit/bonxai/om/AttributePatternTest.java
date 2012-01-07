package eu.fox7.schematoolkit.bonxai.om;

import java.util.Vector;

import org.junit.Test;


import eu.fox7.schematoolkit.common.AnyAttribute;

import static org.junit.Assert.*;

/**
 *
 */
public class AttributePatternTest extends junit.framework.TestCase {

    public AttributePatternTest() {
    }

    /**
     * Test of getAnyAttribute method, of class AttributePattern.
     */
    @Test
    public void testGetAnyAttrbibute() {
        AttributePattern instance = new AttributePattern();
        AnyAttribute expResult = null;
        AnyAttribute result = instance.getAnyAttribute();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAnyAttribute method, of class AttributePattern.
     */
    @Test
    public void testSetAnyAttrbibute() {
        AttributePattern instance = new AttributePattern();
        AnyAttribute expResult = new AnyAttribute();
        instance.setAnyAttribute(expResult);
        AnyAttribute result = instance.getAnyAttribute();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAttributeList method, of class AttributePattern.
     */
    @Test
    public void testGetAttributeList() {
        AttributePattern instance = new AttributePattern();
        Vector<AttributeListElement> expResult = new Vector<AttributeListElement>();
        expResult.add(new Attribute("", "name", new BonxaiType("", "string")));
        instance.setAttributeList(expResult);
        Vector<AttributeListElement> result = instance.getAttributeList();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeList method, of class AttributePattern.
     */
    @Test
    public void testSetAttributeList() {
        AttributePattern instance = new AttributePattern();
        Vector<AttributeListElement> expResult = new Vector<AttributeListElement>();
        expResult.add(new Attribute("", "name", new BonxaiType("", "string")));
        instance.setAttributeList(expResult);
        Vector<AttributeListElement> result = instance.getAttributeList();
        assertEquals(expResult, result);
    }
}
