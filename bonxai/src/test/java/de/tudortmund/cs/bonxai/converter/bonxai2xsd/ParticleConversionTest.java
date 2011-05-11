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
public class ParticleConversionTest extends junit.framework.TestCase {

    @Test
    public void testConvertGroupReference() {
        XSDSchema xsd = new XSDSchema();
        de.tudortmund.cs.bonxai.xsd.Group expected = new de.tudortmund.cs.bonxai.xsd.Group("{}foo", new SequencePattern());
        xsd.getGroupSymbolTable().updateOrCreateReference( "{}foo", expected);

        Bonxai bonxai                   = new Bonxai();
        bonxai.getGroupSymbolTable().updateOrCreateReference("foo", new ElementGroupElement("foo", new SequencePattern()));

        ParticleConverter converter = new ParticleConverter();

        GroupRef groupRef = new GroupRef(bonxai.getGroupSymbolTable().getReference("foo"));
        Particle result = converter.convertParticle(xsd, "", groupRef);

        assertTrue(result instanceof GroupRef);
        assertSame(expected, ((GroupRef) result).getGroup());
    }

    @Test
    public void testConvertGroupReferenceInSequence() {
        XSDSchema xsd = new XSDSchema();
        de.tudortmund.cs.bonxai.xsd.Group expected = new de.tudortmund.cs.bonxai.xsd.Group("{}foo", new SequencePattern());
        xsd.getGroupSymbolTable().updateOrCreateReference( "{}foo", expected);

        Bonxai bonxai                   = new Bonxai();
        bonxai.getGroupSymbolTable().updateOrCreateReference("foo", new ElementGroupElement("foo", new SequencePattern()));

        ParticleConverter converter = new ParticleConverter();
        SequencePattern pattern     = new SequencePattern();

        GroupRef groupRef = new GroupRef(bonxai.getGroupSymbolTable().getReference("foo"));
        pattern.addParticle(groupRef);

        Particle result = converter.convertParticle(xsd, "", pattern);

        assertTrue(result instanceof SequencePattern);
        LinkedList<Particle> children = ((SequencePattern) result).getParticles();
        assertTrue(children.get(0) instanceof GroupRef);
        assertSame(expected, ((GroupRef) children.get(0)).getGroup());
    }

    @Test
    public void testConvertElement() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name");
        Particle result = converter.convertParticle(xsd, "ns", element);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.xsd.Element);
        assertEquals("{ns}name", ((de.tudortmund.cs.bonxai.xsd.Element) result).getName());
        assertEquals(false, (boolean) ((de.tudortmund.cs.bonxai.xsd.Element) result).isNillable());
        assertEquals(null, ((de.tudortmund.cs.bonxai.xsd.Element) result).getType());
    }

    @Test
    public void testConvertElementReference() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name");
        Particle result = converter.convertParticle(xsd, "some_other_ns", element);

        assertTrue(result instanceof ElementRef);
    }

    @Test
    public void testConvertMissingElement() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name", true);
        Particle result = converter.convertParticle(xsd, "ns", element);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.xsd.Element);
        assertEquals("{ns}name", ((de.tudortmund.cs.bonxai.xsd.Element) result).getName());
        assertEquals(true, (boolean) ((de.tudortmund.cs.bonxai.xsd.Element) result).isNillable());
        assertEquals(null, ((de.tudortmund.cs.bonxai.xsd.Element) result).getType());
    }

    @Test
    public void testConvertElementWithType() {
        XSDSchema xsd = new XSDSchema();
        SimpleType expected         = new SimpleType("{}foo", null);

        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name", new BonxaiType("", "foo"));

        Particle result = converter.convertParticle(xsd, "ns", element);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.xsd.Element);
        assertEquals("{ns}name", ((de.tudortmund.cs.bonxai.xsd.Element) result).getName());
        assertEquals(false, (boolean) ((de.tudortmund.cs.bonxai.xsd.Element) result).isNillable());
        assertEquals(expected, ((de.tudortmund.cs.bonxai.xsd.Element) result).getType());
    }

    @Test
    public void testConvertMissingElementWithType() {
        XSDSchema xsd = new XSDSchema();
        SimpleType expected         = new SimpleType("{}foo", null);

        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name", new BonxaiType("", "foo"), true);
        Particle result = converter.convertParticle(xsd, "ns", element);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.xsd.Element);
        assertEquals("{ns}name", ((de.tudortmund.cs.bonxai.xsd.Element) result).getName());
        assertEquals(true, (boolean) ((de.tudortmund.cs.bonxai.xsd.Element) result).isNillable());
        assertEquals(expected, ((de.tudortmund.cs.bonxai.xsd.Element) result).getType());
    }

    @Test
    public void testConvertElementWithTypeAndDefaultValue() {
        XSDSchema xsd = new XSDSchema();
        SimpleType expected         = new SimpleType("{}foo", null);

        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name", new BonxaiType("", "foo"), true);
        element.setDefault("42");
        Particle result = converter.convertParticle(xsd, "ns", element);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.xsd.Element);
        assertEquals("{ns}name", ((de.tudortmund.cs.bonxai.xsd.Element) result).getName());
        assertEquals(expected, ((de.tudortmund.cs.bonxai.xsd.Element) result).getType());
        assertEquals("42", ((de.tudortmund.cs.bonxai.xsd.Element) result).getDefault());
    }

    @Test
    public void testConvertElementWithTypeAndFixedValue() {
        XSDSchema xsd = new XSDSchema();
        SimpleType expected         = new SimpleType("{}foo", null);

        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name", new BonxaiType("", "foo"), true);
        element.setFixed("42");
        Particle result = converter.convertParticle(xsd, "ns", element);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.xsd.Element);
        assertEquals("{ns}name", ((de.tudortmund.cs.bonxai.xsd.Element) result).getName());
        assertEquals(expected, ((de.tudortmund.cs.bonxai.xsd.Element) result).getType());
        assertEquals("42", ((de.tudortmund.cs.bonxai.xsd.Element) result).getFixed());
    }

    @Test
    public void testConvertSequencePattern() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name");
        SequencePattern pattern     = new SequencePattern();
        pattern.addParticle( element );

        Particle result = converter.convertParticle(xsd, "ns", pattern);

        assertTrue(result instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) result).getParticles().size());
    }

    @Test
    public void testConvertChoicePattern() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name");
        ChoicePattern pattern     = new ChoicePattern();
        pattern.addParticle( element );

        Particle result = converter.convertParticle(xsd, "ns", pattern);

        assertTrue(result instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) result).getParticles().size());
    }

    @Test
    public void testConvertAllPattern() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name");
        AllPattern pattern     = new AllPattern();
        pattern.addParticle( element );

        Particle result = converter.convertParticle(xsd, "ns", pattern);

        assertTrue(result instanceof AllPattern);
        assertEquals(1, ((AllPattern) result).getParticles().size());
    }

    @Test
    public void testConvertCountingPattern() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.bonxai.Element element = new de.tudortmund.cs.bonxai.bonxai.Element("ns", "name");
        CountingPattern pattern     = new CountingPattern(1, null);
        pattern.addParticle( element );

        Particle result = converter.convertParticle(xsd, "ns", pattern);

        assertTrue(result instanceof CountingPattern);
        assertEquals(1, ((CountingPattern) result).getParticles().size());
        assertEquals(1, (int) ((CountingPattern) result).getMin());
        assertEquals(null, ((CountingPattern) result).getMax());
    }

    @Test
    public void testConvertAnyPattern() {
        XSDSchema xsd = new XSDSchema();
        Bonxai bonxai                   = new Bonxai();
        ParticleConverter converter = new ParticleConverter();

        AnyPattern element          = new AnyPattern(ProcessContentsInstruction.Lax, "##local");
        SequencePattern pattern     = new SequencePattern();
        pattern.addParticle( element );

        Particle result = converter.convertParticle(xsd, "ns", pattern);

        assertTrue(result instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) result).getParticles().size());
        assertTrue(((SequencePattern) result).getParticles().get(0) instanceof AnyPattern);

        AnyPattern anyResult        = (AnyPattern) ((SequencePattern) result).getParticles().get(0);
        assertEquals(ProcessContentsInstruction.Lax, anyResult.getProcessContentsInstruction());
        assertEquals("##local", anyResult.getNamespace());
    }
}
