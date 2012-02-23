package eu.fox7.schematoolkit.xsd.om;

import java.util.LinkedList;
import java.util.Hashtable;

import org.junit.*;

import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;


public class AttributeGroupTest extends junit.framework.TestCase {

    @Test
    public void testCreateAttributeGroup() {
    	QualifiedName groupName = new QualifiedName(Namespace.EMPTY_NAMESPACE, "someGroup");
        AttributeGroup attributeGroup = new AttributeGroup(groupName);
        assertEquals(attributeGroup.name, groupName);
        assertTrue(attributeGroup.attributeParticles.isEmpty());
    }

    @Test
    public void testGetAttributeParticles() {
    	QualifiedName groupName = new QualifiedName(Namespace.EMPTY_NAMESPACE, "someGroup");
        AttributeGroup attributeGroup = new AttributeGroup(groupName);
        attributeGroup.attributeParticles = new LinkedList<AttributeParticle>();
    	QualifiedName attributeName = new QualifiedName(Namespace.EMPTY_NAMESPACE, "someName");
        attributeGroup.addAttributeParticle(new Attribute(attributeName));
        attributeGroup.getAttributeParticles().clear();
        assertEquals(attributeGroup.getAttributeParticles().size(), 1);
    }

    @Test
    public void testGetName() {
    	QualifiedName groupName = new QualifiedName(Namespace.EMPTY_NAMESPACE, "someGroup");
        AttributeGroup attributeGroup = new AttributeGroup(groupName);
        assertEquals(attributeGroup.name, "{}someGroup");
    }

    @Test
    public void testAddAttributeParticle() {
    	QualifiedName groupName = new QualifiedName(Namespace.EMPTY_NAMESPACE, "someGroup");
        AttributeGroup attributeGroup = new AttributeGroup(groupName);
    	QualifiedName attributeName = new QualifiedName(Namespace.EMPTY_NAMESPACE, "someName");
        attributeGroup.addAttributeParticle(new Attribute(attributeName));
        assertEquals(attributeGroup.name, groupName);
        assertEquals(attributeGroup.getAttributeParticles().size(), 1);
    }

    @Test
    public void testEquals() {
    	QualifiedName groupName1 = new QualifiedName(Namespace.EMPTY_NAMESPACE, "fooGroup");
    	QualifiedName groupName2 = new QualifiedName(Namespace.EMPTY_NAMESPACE, "fooGroup");
    	QualifiedName groupName3 = new QualifiedName(Namespace.EMPTY_NAMESPACE, "barGroup");
        AttributeGroup attributeGroup1 = new AttributeGroup(groupName1);
        AttributeGroup attributeGroup2 = new AttributeGroup(groupName2);
        AttributeGroup attributeGroup3 = new AttributeGroup(groupName3);

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

    	QualifiedName groupName1 = new QualifiedName(Namespace.EMPTY_NAMESPACE, "fooGroup");
    	QualifiedName groupName2 = new QualifiedName(Namespace.EMPTY_NAMESPACE, "fooGroup");
    	QualifiedName groupName3 = new QualifiedName(Namespace.EMPTY_NAMESPACE, "barGroup");
        AttributeGroup attributeGroup1 = new AttributeGroup(groupName1);
        AttributeGroup attributeGroup2 = new AttributeGroup(groupName2);
        AttributeGroup attributeGroup3 = new AttributeGroup(groupName3);

        table.put("fooGroup", attributeGroup1);

        assertTrue(table.containsValue(attributeGroup1));
        assertTrue(table.containsValue(attributeGroup2));
        assertFalse(table.containsValue(attributeGroup3));
    }
}
