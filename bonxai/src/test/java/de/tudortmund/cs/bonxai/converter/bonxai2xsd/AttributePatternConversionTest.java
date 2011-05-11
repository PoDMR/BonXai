package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import org.junit.Test;

import java.util.Vector;
import java.util.LinkedList;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;

/**
 *
 */
public class AttributePatternConversionTest extends junit.framework.TestCase {

    @Test
    public void testConvertAnyAttribute() {
        XSDSchema xsd                  = new XSDSchema();
        ParticleConverter converter = new ParticleConverter();
        AttributePattern pattern    = new AttributePattern();
        AnyAttribute attribute      = new AnyAttribute();
        pattern.setAnyAttribute(attribute);

        LinkedList<AttributeParticle> list = converter.convertParticle(xsd, "", pattern);

        assertSame(attribute, list.get(0));
    }

    @Test
    public void testConvertAttributeList() {
        XSDSchema xsd                  = new XSDSchema();
        ParticleConverter converter = new ParticleConverter();
        AttributePattern pattern    = new AttributePattern();

        Vector<AttributeListElement> attributes = new Vector<AttributeListElement>();
        pattern.setAttributeList(attributes);

        de.tudortmund.cs.bonxai.bonxai.Attribute attribute = new de.tudortmund.cs.bonxai.bonxai.Attribute("", "attribute", new BonxaiType("http://www.w3.org/2001/XMLSchema", "string"));
        attributes.add(attribute);

        LinkedList<AttributeParticle> list           = converter.convertParticle(xsd, "", pattern);

        assertTrue(list.get(0) instanceof de.tudortmund.cs.bonxai.xsd.Attribute);
        de.tudortmund.cs.bonxai.xsd.Attribute expected = (de.tudortmund.cs.bonxai.xsd.Attribute) list.get(0);

        assertEquals("{}attribute", expected.getName());
    }

    @Test
    public void testConvertAttributeListMultipleElements() {
        XSDSchema xsd                  = new XSDSchema();
        ParticleConverter converter = new ParticleConverter();
        AttributePattern pattern    = new AttributePattern();

        Vector<AttributeListElement> attributes = new Vector<AttributeListElement>();
        pattern.setAttributeList(attributes);

        de.tudortmund.cs.bonxai.bonxai.Attribute attribute1 = new de.tudortmund.cs.bonxai.bonxai.Attribute("", "attribute1", new BonxaiType("http://www.w3.org/2001/XMLSchema", "string"));
        attributes.add(attribute1);
        de.tudortmund.cs.bonxai.bonxai.Attribute attribute2 = new de.tudortmund.cs.bonxai.bonxai.Attribute("", "attribute2", new BonxaiType("http://www.w3.org/2001/XMLSchema", "string"));
        attributes.add(attribute2);

        LinkedList<AttributeParticle> list           = converter.convertParticle(xsd, "", pattern);

        assertTrue(list.get(0) instanceof de.tudortmund.cs.bonxai.xsd.Attribute);
        de.tudortmund.cs.bonxai.xsd.Attribute expected1 = (de.tudortmund.cs.bonxai.xsd.Attribute) list.get(0);

        assertTrue(list.get(1) instanceof de.tudortmund.cs.bonxai.xsd.Attribute);
        de.tudortmund.cs.bonxai.xsd.Attribute expected2 = (de.tudortmund.cs.bonxai.xsd.Attribute) list.get(1);

        assertEquals("{}attribute2", expected2.getName());
    }

    @Test
    public void testConvertAttributeGroupReference() {
        XSDSchema xsd                  = new XSDSchema();
        AttributeGroup expected     = new AttributeGroup("{}foo");
        xsd.getAttributeGroupSymbolTable().updateOrCreateReference( "{}foo", expected);

        Bonxai bonxai                   = new Bonxai();
        bonxai.getAttributeGroupElementSymbolTable().updateOrCreateReference("foo", new AttributeGroupElement("foo", new AttributePattern()));

        ParticleConverter converter = new ParticleConverter();
        AttributePattern pattern    = new AttributePattern();

        Vector<AttributeListElement> attributes = new Vector<AttributeListElement>();
        pattern.setAttributeList(attributes);

        AttributeGroupReference attribute = new AttributeGroupReference(bonxai.getAttributeGroupElementSymbolTable().getReference("foo"));
        attributes.add(attribute);

        LinkedList<AttributeParticle> list           = converter.convertParticle(xsd, "", pattern);

        assertTrue(list.get(0) instanceof de.tudortmund.cs.bonxai.xsd.AttributeGroupRef);
        assertSame(expected, ((de.tudortmund.cs.bonxai.xsd.AttributeGroupRef) list.get(0)).getAttributeGroup() );
    }

    @Test
    public void testConvertAttributeWithType() {
        XSDSchema xsd                  = new XSDSchema();
        SimpleType expected         = new SimpleType("{http://www.w3.org/2001/XMLSchema}foo", null);

        ParticleConverter converter = new ParticleConverter();
        AttributePattern pattern    = new AttributePattern();

        Vector<AttributeListElement> attributes = new Vector<AttributeListElement>();
        pattern.setAttributeList(attributes);

        de.tudortmund.cs.bonxai.bonxai.Attribute attribute = new de.tudortmund.cs.bonxai.bonxai.Attribute("", "attribute", new BonxaiType("http://www.w3.org/2001/XMLSchema", "foo"));
        attributes.add(attribute);

        LinkedList<AttributeParticle> list = converter.convertParticle(xsd, "", pattern);

        assertTrue(list.get(0) instanceof de.tudortmund.cs.bonxai.xsd.Attribute);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttr = (de.tudortmund.cs.bonxai.xsd.Attribute) list.get(0);

        assertEquals(expected, xsdAttr.getSimpleType());
    }
}
