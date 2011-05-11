package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.Vector;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;

import org.junit.Test;
import org.junit.Assert.*;

public class GroupsConverterTest extends ConverterTest {

    @Test
    public void testConvertNoGroups() {
        XSDSchema xsd = getSchemaFixture();
        TypeAutomaton typeAutomaton = getTypeAutomatonFixture();
        Bonxai bonxai = getBonxaiFixture();

        GroupsConverter converter = new GroupsConverter();

        Bonxai actual = converter.convert(xsd, typeAutomaton, bonxai);

        assertSame(bonxai, actual);

        assertNotNull(actual.getGroupList());

        assertEquals(0, actual.getGroupList().getGroupElements().size());
    }

    @Test
    public void testConvertElementGroup() {
        XSDSchema xsd = getSchemaFixture();
        TypeAutomaton typeAutomaton = getTypeAutomatonFixture();
        Bonxai bonxai = getBonxaiFixture();

        xsd.addGroup(
            getXsdElementGroupSymbolTableRefFixture("{}fooGroup")
        );

        GroupsConverter converter = new GroupsConverter();

        Bonxai actual = converter.convert(xsd, typeAutomaton, bonxai);

        assertSame(bonxai, actual);

        assertNotNull(actual.getGroupList());

        assertEquals(1, actual.getGroupList().getGroupElements().size());

        assertTrue(actual.getGroupList().getGroupElements().elementAt(0) instanceof ElementGroupElement);

        assertEquals(
            "fooGroup",
            ((ElementGroupElement) actual.getGroupList().getGroupElements().elementAt(0)).getName()
        );
    }

    @Test
    public void testConvertAttributeGroup() {
        XSDSchema xsd = getSchemaFixture();
        TypeAutomaton typeAutomaton = getTypeAutomatonFixture();
        Bonxai bonxai = getBonxaiFixture();

        xsd.addAttributeGroup(
            getXsdAttributeGroupSymbolTableRefFixture("{http://example.com/ns}fooGroup")
        );

        GroupsConverter converter = new GroupsConverter();

        Bonxai actual = converter.convert(xsd, typeAutomaton, bonxai);

        assertSame(bonxai, actual);

        assertNotNull(actual.getGroupList());

        assertEquals(1, actual.getGroupList().getGroupElements().size());

        assertTrue(actual.getGroupList().getGroupElements().elementAt(0) instanceof AttributeGroupElement);

        assertEquals(
            "fooGroup",
            ((AttributeGroupElement) actual.getGroupList().getGroupElements().elementAt(0)).getName()
        );
    }

    @Test
    public void testConvertMultipleGroups() {
        XSDSchema xsd = getSchemaFixture();
        TypeAutomaton typeAutomaton = getTypeAutomatonFixture();
        Bonxai bonxai = getBonxaiFixture();

        // XSD normal attribute group
        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup> normAttrGroupRef
            = getXsdAttributeGroupSymbolTableRefFixture("{}fooGroup");
        normAttrGroupRef.getReference().addAttributeParticle(
            AttributeProcessorTest.getXsdAttributeFixture("{}fooAttribute", "{}fooType")
        );
        xsd.addAttributeGroup(
            normAttrGroupRef
        );

        // XSD Element group
        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> elementGroup
            = getXsdElementGroupSymbolTableRefFixture("{}barGroup");
        ((de.tudortmund.cs.bonxai.xsd.Group)elementGroup.getReference()).getParticleContainer().addParticle(
            new de.tudortmund.cs.bonxai.xsd.Element(
                "http://example.com/barNamespace",
                "barElement"
            )
        );
        xsd.addGroup(
            elementGroup
        );

        // XSD any attribute group
        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup> anyAttrGroupRef
            = getXsdAttributeGroupSymbolTableRefFixture("{}bazGroup");
        anyAttrGroupRef.getReference().addAttributeParticle(
            AttributeProcessorTest.getXsdAttributeFixture("{}bazAttribute", "{}bazType")
        );
        anyAttrGroupRef.getReference().addAttributeParticle(
            new AnyAttribute(
                ProcessContentsInstruction.Lax,
                "http://example.com/bazNamespace"
            )
        );
        xsd.addAttributeGroup(
            anyAttrGroupRef
        );

        GroupsConverter converter = new GroupsConverter();

        // Actual conversion
        Bonxai actual = converter.convert(xsd, typeAutomaton, bonxai);

        // General assertions
        assertSame(bonxai, actual);

        assertNotNull(actual.getGroupList());

        assertEquals(3, actual.getGroupList().getGroupElements().size());

        // Attention: Element groups are converted first!

        // XSD Element group
        assertTrue(actual.getGroupList().getGroupElements().elementAt(0) instanceof ElementGroupElement);

        ElementGroupElement actNormElemGroup = (ElementGroupElement) actual.getGroupList().getGroupElements().elementAt(0);

        assertEquals(
            "barGroup",
            actNormElemGroup.getName()
        );

        assertTrue(
            actNormElemGroup.getParticleContainer() instanceof SequencePattern
        );

        // XSD normal attribute group
        assertTrue((actual.getGroupList().getGroupElements().elementAt(1) instanceof AttributeGroupElement));

        AttributeGroupElement actNormAttrGroup = (AttributeGroupElement) actual.getGroupList().getGroupElements().elementAt(1);

        assertEquals(
            "fooGroup",
            actNormAttrGroup.getName()
        );

        assertEquals(
            1,
            actNormAttrGroup.getAttributePattern().getAttributeList().size()
        );

        // XSD any attribute group
        assertTrue(actual.getGroupList().getGroupElements().elementAt(2) instanceof AttributeGroupElement);

        AttributeGroupElement actAnyAttrGroup = (AttributeGroupElement) actual.getGroupList().getGroupElements().elementAt(2);

        assertEquals(
            "bazGroup",
            actAnyAttrGroup.getName()
        );

        assertEquals(
            1,
            actAnyAttrGroup.getAttributePattern().getAttributeList().size()
        );
    }


    protected static SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup>
        getXsdAttributeGroupSymbolTableRefFixture(String name)
    {
        return new SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup>(
            name,
            new de.tudortmund.cs.bonxai.xsd.AttributeGroup(
                name
            )
        );
    }

    protected static SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>
        getXsdElementGroupSymbolTableRefFixture(String name)
    {
        return new SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>(
            name,
            new de.tudortmund.cs.bonxai.xsd.Group(
                name,
                new SequencePattern()
            )
        );
    }
}

