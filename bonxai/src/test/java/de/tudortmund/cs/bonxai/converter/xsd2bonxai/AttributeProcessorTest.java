package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.LinkedList;
import java.util.Vector;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.bonxai.*;

import org.junit.Test;
import org.junit.Assert.*;

public class AttributeProcessorTest extends ConverterTest {

    @Test
    public void testConvertAttribute()
    {
        de.tudortmund.cs.bonxai.xsd.Attribute fixture =
            getXsdAttributeFixture("{}fooAttribute", "{}fooType");

        SymbolTable<AttributeGroupElement> bonxaiSymbolTable =
            new SymbolTable<AttributeGroupElement>();
        AttributeProcessor processor = new AttributeProcessor(bonxaiSymbolTable);

        de.tudortmund.cs.bonxai.bonxai.Attribute actual =
            processor.convertAttribute(fixture);

        assertNotNull(actual);

        assertEquals("fooAttribute", actual.getName());
        assertEquals("", actual.getNamespace());

        assertTrue(actual.getType() instanceof BonxaiType);
        assertEquals("fooType", actual.getType().getType());
        assertEquals("", actual.getType().getNamespace());
        assertNull(actual.getDefault());
        assertNull(actual.getFixed());
        assertFalse(actual.isRequired());
    }

    @Test
    public void testConvertAttributeRef() {
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttr =
            getXsdAttributeFixture("{}fooAttribute", "{}fooType");

        de.tudortmund.cs.bonxai.common.SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Attribute> xsdAttrSymbRef =
            new SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Attribute>("fooAttribute");
        xsdAttrSymbRef.setReference(xsdAttr);

        de.tudortmund.cs.bonxai.xsd.AttributeRef fixture = new AttributeRef(xsdAttrSymbRef);
        fixture.setUse(AttributeUse.Required);

        SymbolTable<AttributeGroupElement> bonxaiSymbolTable =
            new SymbolTable<AttributeGroupElement>();
        AttributeProcessor processor = new AttributeProcessor(bonxaiSymbolTable);

        de.tudortmund.cs.bonxai.bonxai.Attribute actual =
            processor.convertAttributeRef(fixture);

        assertNotNull(actual);

        assertEquals("fooAttribute", actual.getName());
        assertEquals("", actual.getNamespace());

        assertTrue(actual.getType() instanceof BonxaiType);
        assertEquals("fooType", actual.getType().getType());
        assertEquals("", actual.getType().getNamespace());
        assertNull(actual.getDefault());
        assertNull(actual.getFixed());
        assertTrue(actual.isRequired());
    }

    @Test
    public void testConvertAttributes() {
        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> fixture =
            new LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>();
        fixture.add(
            getXsdAttributeFixture("{}fooAttribute", "{}fooType")
        );
        fixture.add(
            getXsdAttributeFixture("{}barAttribute", "{}barType")
        );

        SymbolTable<AttributeGroupElement> bonxaiSymbolTable =
            new SymbolTable<AttributeGroupElement>();
        AttributeProcessor processor = new AttributeProcessor(bonxaiSymbolTable);

        de.tudortmund.cs.bonxai.bonxai.AttributePattern actual =
            processor.convertAttributes(fixture);

        assertNotNull(actual);

        assertEquals(2, actual.getAttributeList().size());

        assertTrue(
            (actual.getAttributeList().elementAt(0) instanceof de.tudortmund.cs.bonxai.bonxai.Attribute)
        );

        de.tudortmund.cs.bonxai.bonxai.Attribute actFirstAttr =
            (de.tudortmund.cs.bonxai.bonxai.Attribute) actual.getAttributeList().elementAt(0);
        assertEquals("fooAttribute", actFirstAttr.getName());
        assertEquals("", actFirstAttr.getNamespace());
        assertTrue(actFirstAttr.getType() instanceof BonxaiType);
        assertEquals("fooType", actFirstAttr.getType().getType());
        assertEquals("", actFirstAttr.getType().getNamespace());
        assertNull(actFirstAttr.getDefault());
        assertNull(actFirstAttr.getFixed());

        assertTrue(
            (actual.getAttributeList().elementAt(1) instanceof de.tudortmund.cs.bonxai.bonxai.Attribute)
        );

        de.tudortmund.cs.bonxai.bonxai.Attribute actSectondAttr =
            (de.tudortmund.cs.bonxai.bonxai.Attribute) actual.getAttributeList().elementAt(1);
        assertEquals("barAttribute", actSectondAttr.getName());
        assertEquals("", actSectondAttr.getNamespace());
        assertTrue(actSectondAttr.getType() instanceof BonxaiType);
        assertEquals("barType", actSectondAttr.getType().getType());
        assertEquals("", actSectondAttr.getType().getNamespace());
        assertNull(actSectondAttr.getDefault());
        assertNull(actSectondAttr.getFixed());

        assertNull(actual.getAnyAttribute());
    }

    @Test
    public void testConvertAttributesWithGroupRef() {
        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> fixture =
            new LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>();
        fixture.add(
            getXsdAttributeFixture("{}fooAttribute", "{}fooType")
        );

        de.tudortmund.cs.bonxai.xsd.AttributeGroup fixtureAttrGroup = new
            de.tudortmund.cs.bonxai.xsd.AttributeGroup("{}barAttributeGroup");

        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup> fixtureAttrGroupSymbRef =
            new SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup>(
                "barAttributeGroup",
                fixtureAttrGroup
            );

        fixture.add(
            new de.tudortmund.cs.bonxai.xsd.AttributeGroupRef(fixtureAttrGroupSymbRef)
        );

        SymbolTable<AttributeGroupElement> bonxaiSymbolTable =
            new SymbolTable<AttributeGroupElement>();
        AttributeProcessor processor = new AttributeProcessor(bonxaiSymbolTable);

        de.tudortmund.cs.bonxai.bonxai.AttributePattern actual =
            processor.convertAttributes(fixture);

        assertNotNull(actual);

        assertEquals(2, actual.getAttributeList().size());

        assertTrue(
            (actual.getAttributeList().elementAt(0) instanceof de.tudortmund.cs.bonxai.bonxai.Attribute)
        );

        de.tudortmund.cs.bonxai.bonxai.Attribute actFirstAttr =
            (de.tudortmund.cs.bonxai.bonxai.Attribute) actual.getAttributeList().elementAt(0);
        assertEquals("fooAttribute", actFirstAttr.getName());
        assertTrue(actFirstAttr.getType() instanceof BonxaiType);
        assertEquals("fooType", actFirstAttr.getType().getType());
        assertEquals("", actFirstAttr.getType().getNamespace());
        assertNull(actFirstAttr.getDefault());
        assertNull(actFirstAttr.getFixed());

        assertTrue(
            (actual.getAttributeList().elementAt(1) instanceof de.tudortmund.cs.bonxai.bonxai.AttributeGroupReference)
        );

        // Ugly, but works. Should add a hasReference() method to SymbolTable.
        try {
            SymbolTableRef<AttributeGroupElement> notPossible =
                bonxaiSymbolTable.createReference(
                    "barAttributeGroup",
                    ((de.tudortmund.cs.bonxai.bonxai.AttributeGroupReference) actual.getAttributeList().elementAt(1)).getGroupElement()
                );
            fail("SymbolTableRef for referenced attribute group does not exist.");
        } catch (SymbolAlreadyRegisteredException e) {}
    }

    @Test
    public void testConvertAttributesAnyAttribute() {
        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> fixture =
            new LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>();
        fixture.add(
            getXsdAttributeFixture("{}fooAttribute", "{}fooType")
        );
        fixture.add(
            new AnyAttribute(
                ProcessContentsInstruction.Lax,
                "http://example.com/fooNamespace"
            )
        );

        SymbolTable<AttributeGroupElement> bonxaiSymbolTable = new SymbolTable<AttributeGroupElement>();
        AttributeProcessor processor = new AttributeProcessor(bonxaiSymbolTable);

        de.tudortmund.cs.bonxai.bonxai.AttributePattern actual =
            processor.convertAttributes(fixture);

        assertNotNull(actual);

        assertEquals(1, actual.getAttributeList().size());

        assertTrue(
            (actual.getAttributeList().elementAt(0) instanceof de.tudortmund.cs.bonxai.bonxai.Attribute)
        );

        de.tudortmund.cs.bonxai.bonxai.Attribute actFirstAttr =
            (de.tudortmund.cs.bonxai.bonxai.Attribute) actual.getAttributeList().elementAt(0);
        assertEquals("fooAttribute", actFirstAttr.getName());
        assertTrue(actFirstAttr.getType() instanceof BonxaiType);
        assertEquals("fooType", actFirstAttr.getType().getType());
        assertEquals("", actFirstAttr.getType().getNamespace());
        assertNull(actFirstAttr.getDefault());
        assertNull(actFirstAttr.getFixed());

        assertNotNull(actual.getAnyAttribute());

        assertSame(fixture.get(1), actual.getAnyAttribute());
    }

    protected static de.tudortmund.cs.bonxai.xsd.Attribute getXsdAttributeFixture(String name, String type) {
        return new de.tudortmund.cs.bonxai.xsd.Attribute(
                name,
                new SymbolTableRef<Type>(
                    type,
                    new SimpleType(type, null)
                )
        );
    }
}
