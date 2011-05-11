package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.*;
import org.junit.*;

import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;

public class InheritancePreProcessorTest extends junit.framework.TestCase {

    private int uniqueId = 0;

    private PreProcessor getPreProcessorFixture() {
        PreProcessor processor = new PreProcessor();
        processor.addVisitor(new InheritancePreProcessorVisitor());
        return processor;
    }

    private XSDSchema getSchemaFixture() {
        XSDSchema schema = new XSDSchema();
        return schema;
    }

    private ComplexType registerComplexTypeFixture(XSDSchema schema, Content content)
            throws SymbolAlreadyRegisteredException {
        this.uniqueId++;
        ComplexType type = new ComplexType("{}complextype_" + this.uniqueId, content);
        schema.addType(
                schema.getTypeSymbolTable().createReference(
                "{}complextype_" + this.uniqueId,
                type));
        return type;
    }

    private SymbolTableRef<Type> getLastTypeReference(XSDSchema schema) {
        return schema.getTypeSymbolTable().getReference(
                "{}complextype_" + this.uniqueId);
    }

    private Attribute getAttributeFixture(String name) {
        return new Attribute(name, new SymbolTableRef<Type>("foo", null));
    }

    public final void setUp() {
        // Initialize id counter
        this.uniqueId = 0;
    }

    public final void testComplexContentRestriction()
            throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();

        SequencePattern baseSequence = new SequencePattern();
        baseSequence.addParticle(new Element("{}element1"));
        baseSequence.addParticle(new Element("{}element2"));
        ComplexContentType baseContent = new ComplexContentType();
        baseContent.setParticle(baseSequence);
        ComplexType baseType = this.registerComplexTypeFixture(
                schema,
                baseContent);
        SymbolTableRef<Type> baseTypeRef = this.getLastTypeReference(schema);

        baseType.addAttribute(getAttributeFixture("{}attribute1"));
        baseType.addAttribute(getAttributeFixture("{}attribute2"));

        SequencePattern restrictionSequence = new SequencePattern();
        Element restrictionElement = new Element("{}element1");
        restrictionSequence.addParticle(restrictionElement);
        ComplexContentType restrictionContent = new ComplexContentType();
        restrictionContent.setParticle(restrictionSequence);
        ComplexType restrictionType = this.registerComplexTypeFixture(
                schema,
                restrictionContent);
        Attribute restrictionAttribute = getAttributeFixture("{}attribute");
        restrictionType.addAttribute(restrictionAttribute);
        restrictionContent.setInheritance(new ComplexContentRestriction(baseTypeRef));

        this.getPreProcessorFixture().process(schema);

        assertNull(restrictionContent.getInheritance());
        assertEquals(1, restrictionSequence.getParticles().size());
        assertSame(restrictionElement, restrictionSequence.getParticles().get(0));
        assertEquals(1, restrictionType.getAttributes().size());
        assertSame(restrictionAttribute, restrictionType.getAttributes().get(0));
    }

    public final void testComplexContentExtension()
            throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();

        SequencePattern baseSequence = new SequencePattern();
        Element baseElement = new Element("{}element1");
        baseSequence.addParticle(baseElement);
        ComplexContentType baseContent = new ComplexContentType();
        baseContent.setParticle(baseSequence);
        ComplexType baseType = this.registerComplexTypeFixture(
                schema,
                baseContent);
        SymbolTableRef<Type> baseTypeRef = this.getLastTypeReference(schema);

        Attribute baseAttribute = getAttributeFixture("{}attribute1");
        baseType.addAttribute(baseAttribute);

        SequencePattern extensionSequence = new SequencePattern();
        Element extensionElement = new Element("{}element2");
        extensionSequence.addParticle(extensionElement);
        ComplexContentType extensionContent = new ComplexContentType();
        extensionContent.setParticle(extensionSequence);
        ComplexType extensionType = this.registerComplexTypeFixture(
                schema,
                extensionContent);
        SymbolTableRef<Type> baseTypeRef2 = this.getLastTypeReference(schema);
        Attribute extensionAttribute = getAttributeFixture("{}attribute2");
        extensionType.addAttribute(extensionAttribute);
        ComplexContentExtension complexContentExtension = new ComplexContentExtension(baseTypeRef);
        complexContentExtension.addAttribute(extensionAttribute);
        extensionContent.setInheritance(complexContentExtension);

        SequencePattern extensionSequence2 = new SequencePattern();
        Element extensionElement2 = new Element("{}element3");
        extensionSequence2.addParticle(extensionElement2);
        ComplexContentType extensionContent2 = new ComplexContentType();
        extensionContent2.setParticle(extensionSequence2);
        ComplexType extensionType2 = this.registerComplexTypeFixture(
                schema,
                extensionContent2);
        Attribute extensionAttribute2 = getAttributeFixture("{}attribute3");
        ComplexContentExtension complexContentExtension2 = new ComplexContentExtension(baseTypeRef2);
        complexContentExtension2.addAttribute(extensionAttribute2);
        extensionContent2.setInheritance(complexContentExtension2);

        this.getPreProcessorFixture().process(schema);

        ComplexContentType newExtensionContent = (ComplexContentType) extensionType.getContent();
        SequencePattern newExtensionSequence = (SequencePattern) newExtensionContent.getParticle();

        assertNull(newExtensionContent.getInheritance());
        assertEquals(2, newExtensionSequence.getParticles().size());

//        SequencePattern innerSequence1 = ((SequencePattern) newExtensionSequence.getParticles().get(0));
//        SequencePattern innerSequence2 = ((SequencePattern) newExtensionSequence.getParticles().get(1));

        //assertSame(baseElement, innerSequence1.getParticles().get(0));
        //assertSame(extensionElement, innerSequence2.getParticles().get(0));

        assertEquals(2, extensionType.getAttributes().size());
        assertSame(extensionAttribute, extensionType.getAttributes().get(0));
        assertSame(baseAttribute, extensionType.getAttributes().get(1));

        assertEquals(3, extensionType2.getAttributes().size());
    }
}
