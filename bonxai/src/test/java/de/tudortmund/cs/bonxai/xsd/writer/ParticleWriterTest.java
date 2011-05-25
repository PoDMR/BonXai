package de.tudortmund.cs.bonxai.xsd.writer;

import de.tudortmund.cs.bonxai.xsd.Group;
import org.junit.Test;
import org.junit.Before;

// import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.xsd.*;

public class ParticleWriterTest extends junit.framework.TestCase {

    XSDWriter writer;
    FoundElements foundElements;

    @Before
    public void setUp() {
        DefaultNamespace defaultNameSpace;

        XSDSchema s = new XSDSchema();
        foundElements = new FoundElements();

        defaultNameSpace = new DefaultNamespace("http://example.com/xyz");
        NamespaceList nslist = new NamespaceList(defaultNameSpace);
        nslist.addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
        nslist.addIdentifiedNamespace(new IdentifiedNamespace("bar", "http://example.com/bar"));
        foundElements.setNamespaceList(nslist);

        s.setNamespaceList(nslist);

        writer = new XSDWriter(s);
        try {
            writer.createXSD();
        } catch (Exception e) {
            fail("Exception thrown: \r\n" + e.getLocalizedMessage());
        }
    }

    @Test
    public void testWriteElementRef() {
        Element element = new Element("{http://example.com/xyz}someElement");
        SymbolTableRef<Element> ref = new SymbolTableRef<Element>("someKey", element);
        ElementRef elementRef = new ElementRef(ref);
        ParticleWriter.writeElementRef(writer.root, elementRef, foundElements, 2, null);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("someElement"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteElementRefWithNamespaceIdentifier() {
        Element element = new Element("{http://example.com/bar}someElement");
        SymbolTableRef<Element> ref = new SymbolTableRef<Element>("someKey", element);
        ElementRef elementRef = new ElementRef(ref);
        ParticleWriter.writeElementRef(writer.root, elementRef, foundElements, 2, null);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("bar:someElement"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteAnyPattern() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "http://example.com/bar");
        ParticleWriter.writeAnyPattern(writer.root, anyPattern, foundElements, 0, 99);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("any"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("namespace").getTextContent().equals("http://example.com/bar"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("processContents").getTextContent().equals("lax"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("0"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("99"));
    }

    @Test
    public void testWriteGroupRef() {
        Group group = new Group("{http://example.com/bar}someGroup", null);
        SymbolTableRef<de.tudortmund.cs.bonxai.common.Group> ref = new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("someKey", group);
        GroupRef groupRef = new GroupRef(ref);
        ParticleWriter.writeGroupRef(writer.root, groupRef, foundElements, 3, null);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("bar:someGroup"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteGroupRefWithNamespacePrefix() {
        Group group = new Group("{}someGroup", null);
        SymbolTableRef<de.tudortmund.cs.bonxai.common.Group> ref = new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("someKey", group);
        GroupRef groupRef = new GroupRef(ref);
        ParticleWriter.writeGroupRef(writer.root, groupRef, foundElements, 3, null);

        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("someGroup"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteParticle() {
        Group group = new Group("{}someGroup", null);
        SymbolTableRef<de.tudortmund.cs.bonxai.common.Group> ref = new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("someKey", group);
        GroupRef groupRef = new GroupRef(ref);
        ParticleWriter.writeParticle(writer.root, groupRef, foundElements, 3, null);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("someGroup"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteParticleWithStandardOccurrence() {
        Group group = new Group("{}someGroup", null);
        SymbolTableRef<de.tudortmund.cs.bonxai.common.Group> ref = new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("someKey", group);
        GroupRef groupRef = new GroupRef(ref);
        ParticleWriter.writeParticle(writer.root, groupRef, foundElements);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("someGroup"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs"));
    }

    @Test
    public void testWriteParticleContainer() {
        SequencePattern sequencePattern = new SequencePattern();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "http://example.com/bar");
        sequencePattern.addParticle(anyPattern);
        Group group = new Group("{}someGroup", null);
        SymbolTableRef<de.tudortmund.cs.bonxai.common.Group> ref = new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("someKey", group);
        GroupRef groupRef = new GroupRef(ref);
        sequencePattern.addParticle(groupRef);
        ParticleWriter.writeParticleContainer(writer.root, sequencePattern, foundElements, 3, null);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("sequence"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
        assertTrue(writer.root.getFirstChild().getFirstChild().getLocalName().equals("any"));
        assertTrue(writer.root.getFirstChild().getLastChild().getLocalName().equals("group"));
    }

    @Test
    public void testWriteParticleContainerWithStandardOccurrence() {
        SequencePattern sequencePattern = new SequencePattern();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "http://example.com/bar");
        sequencePattern.addParticle(anyPattern);
        Group group = new Group("{}someGroup", null);
        SymbolTableRef<de.tudortmund.cs.bonxai.common.Group> ref = new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("someKey", group);
        GroupRef groupRef = new GroupRef(ref);
        sequencePattern.addParticle(groupRef);
        ParticleWriter.writeParticleContainer(writer.root, sequencePattern, foundElements);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("sequence"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs"));
        assertTrue(writer.root.getFirstChild().getFirstChild().getLocalName().equals("any"));
        assertTrue(writer.root.getFirstChild().getLastChild().getLocalName().equals("group"));
    }

    @Test
    public void testWriteCountingPattern() {
        CountingPattern countingPattern = new CountingPattern(123,null);
        SequencePattern sequencePattern = new SequencePattern();
        countingPattern.addParticle(sequencePattern);
        ParticleWriter.writeCountingPattern(writer.root, countingPattern, foundElements);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("sequence"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("123"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteElement() {
        Element element = new Element("{http://example.com/bar}foo");
        String typeName = "{http://www.w3.org/2001/XMLSchema}integer";
        SimpleType type = new SimpleType(typeName, null);
        SymbolTableRef<Type> reftype = new SymbolTableRef<Type>("SomeTypeRef", type);
        element.setType(reftype);
        ParticleWriter.writeElement(writer.root, element, foundElements, 2, 3);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("foo"));
    }

    @Test
    public void testWriteElementWithConstraints() {
        Element element = new Element("{http://example.com/bar}foo");
        Unique unique = new Unique("someUnique", "foo/bar");
        unique.addField("@id");
        unique.addField("name");
        Key key = new Key("{}someKey", "foo/bar");
        key.addField("@id");
        key.addField("name");
        SymbolTableRef<SimpleConstraint> ref = new SymbolTableRef<SimpleConstraint>("someKey", key);
        KeyRef keyRef = new KeyRef("someKeyRef", "foo/bar", ref);
        keyRef.addField("@id");
        keyRef.addField("name");
        element.addConstraint(unique);
        element.addConstraint(key);
        element.addConstraint(keyRef);
        String typeName = "{http://www.w3.org/2001/XMLSchema}integer";
        SimpleType type = new SimpleType(typeName, null);
        SymbolTableRef<Type> reftype = new SymbolTableRef<Type>("SomeTypeRef", type);
        element.setType(reftype);
        ParticleWriter.writeElement(writer.root, element, foundElements, 2, 3);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("foo"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getLocalName().equals("unique"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getLocalName().equals("key"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getLocalName().equals("keyref"));

    }

    @Test
    public void testWriteElementWithFixedDefaultAndNillable() {
        Element element = new Element("{http://example.com/bar}foo");
        element.setDefault("0");
        element.setFixed("1");
        element.setNillable();
        String typeName = "{http://www.w3.org/2001/XMLSchema}integer";
        SimpleType type = new SimpleType(typeName, null);
        SymbolTableRef<Type> reftype = new SymbolTableRef<Type>("SomeTypeRef", type);
        element.setType(reftype);
        ParticleWriter.writeElement(writer.root, element, foundElements, 2, 3);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("nillable").getTextContent().equals("true"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("fixed").getTextContent().equals("1"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("foo"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("nillable").getTextContent().equals("true"));
    }

    @Test
    public void testWriteElementWithStandardOccurence() {
        Element element = new Element("{http://example.com/bar}foo");
        String typeName = "{http://www.w3.org/2001/XMLSchema}integer";
        SimpleType type = new SimpleType(typeName, null);
        SymbolTableRef<Type> reftype = new SymbolTableRef<Type>("SomeTypeRef", type);
        element.setType(reftype);
        ParticleWriter.writeElement(writer.root, element, foundElements);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("foo"));
    }

}
