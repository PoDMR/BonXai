package eu.fox7.schematoolkit.xsd.om;

import java.util.LinkedList;
import java.util.Hashtable;

import org.junit.*;

import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;


public class AttributeGroupTest extends junit.framework.TestCase {

    @Test
    public void testCreateAttributeGroup() {
        AttributeGroup attributeGroup = new AttributeGroup("{}someGroup");
        assertEquals(attributeGroup.name, "{}someGroup");
        assertTrue(attributeGroup.attributeParticles.isEmpty());
    }

    @Test
    public void testGetAttributeParticles() {
        AttributeGroup attributeGroup = new AttributeGroup("{}someGroup");
        attributeGroup.attributeParticles = new LinkedList<AttributeParticle>();
        attributeGroup.attributeParticles.add(new Attribute("{}someName"));
        attributeGroup.getAttributeParticles().clear();
        assertEquals(attributeGroup.getAttributeParticles().size(), 1);
    }

    @Test
    public void testGetName() {
        AttributeGroup attributeGroup = new AttributeGroup("{}someGroup");
        assertEquals(attributeGroup.name, "{}someGroup");
    }

    @Test
    public void testAddAttributeParticle() {
        AttributeGroup attributeGroup = new AttributeGroup("{}someGroup");
        attributeGroup.addAttributeParticle(new Attribute("{}someName"));
        assertEquals(attributeGroup.name, "{}someGroup");
        assertEquals(attributeGroup.getAttributeParticles().size(), 1);
    }

    @Test
    public void testEquals() {
        AttributeGroup attributeGroup1 = new AttributeGroup("{}fooGroup");
        AttributeGroup attributeGroup2 = new AttributeGroup("{}fooGroup");
        AttributeGroup attributeGroup3 = new AttributeGroup("{}barGroup");

        assertTrue(attributeGroup1.equals(attributeGroup2));
        assertTrue(attributeGroup2.equals(attributeGroup1));

        assertFalse(attributeGroup1.equals(attributeGroup3));
        assertFalse(attributeGroup2.equals(attributeGroup3));

        assertFalse(attributeGroup1.equals(null));
        assertFalse(attributeGroup2.equals(null));
        assertFalse(attributeGroup3.equals(null));
    }

    @Test
    public void testHashCode() {
        Hashtable<String, AttributeGroup> table = new Hashtable<String, AttributeGroup>();

        AttributeGroup attributeGroup1 = new AttributeGroup("{}fooGroup");
        AttributeGroup attributeGroup2 = new AttributeGroup("{}fooGroup");
        AttributeGroup attributeGroup3 = new AttributeGroup("{}barGroup");

        table.put("fooGroup", attributeGroup1);

        assertTrue(table.containsValue(attributeGroup1));
        assertTrue(table.containsValue(attributeGroup2));
        assertFalse(table.containsValue(attributeGroup3));
    }
}
