package eu.fox7.schematoolkit.xsd.writer;

import org.junit.Test;
import org.junit.Before;

// import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.writer.ParticleWriter;
import eu.fox7.schematoolkit.xsd.writer.XSDWriter;

public class ParticleWriterTest extends junit.framework.TestCase {

    XSDWriter writer;
    DefaultNamespace defaultNamespace;
    IdentifiedNamespace barNamespace;
    XSDSchema s;
	IdentifiedNamespace xsdNamespace;
    
    @Before
    public void setUp() {

        s = new XSDSchema();

        defaultNamespace = new DefaultNamespace("http://example.com/xyz");
        barNamespace = new IdentifiedNamespace("bar", "http://example.com/bar");
        NamespaceList nslist = new NamespaceList(defaultNamespace);
        xsdNamespace = new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        s.addIdentifiedNamespace(xsdNamespace);
        s.addIdentifiedNamespace(barNamespace);

        writer = new XSDWriter(s);
        try {
            writer.createXSD();
        } catch (Exception e) {
            fail("Exception thrown: \r\n" + e.getLocalizedMessage());
        }
    }

    @Test
    public void testWriteElementRef() {
        ElementRef elementRef = new ElementRef(new QualifiedName(defaultNamespace,"someElement"));
        ParticleWriter.writeElementRef(writer.root, elementRef, 2, null, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("someElement"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteElementRefWithNamespaceIdentifier() {
        ElementRef elementRef = new ElementRef(new QualifiedName(barNamespace, "someElement"));
        ParticleWriter.writeElementRef(writer.root, elementRef, 2, null, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("bar:someElement"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteAnyPattern() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "http://example.com/bar");
        ParticleWriter.writeAnyPattern(writer.root, anyPattern, 0, 99, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("any"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("namespace").getTextContent().equals("http://example.com/bar"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("processContents").getTextContent().equals("lax"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("0"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("99"));
    }

    @Test
    public void testWriteGroupRef() {
        GroupReference groupRef = new GroupReference(new QualifiedName(defaultNamespace,"someGroup"));
        ParticleWriter.writeGroupRef(writer.root, groupRef, 3, null, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("bar:someGroup"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteGroupRefWithNamespacePrefix() {
        GroupReference groupRef = new GroupReference(new QualifiedName(barNamespace,"someGroup"));
        ParticleWriter.writeGroupRef(writer.root, groupRef, 3, null, s);

        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("someGroup"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteParticle() {
        GroupReference groupRef = new GroupReference(new QualifiedName(barNamespace,"someGroup"));
        ParticleWriter.writeParticle(writer.root, groupRef, s, 3, null);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("group"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("ref").getTextContent().equals("someGroup"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteParticleWithStandardOccurrence() {
        GroupReference groupRef = new GroupReference(new QualifiedName(defaultNamespace,"someGroup"));
        ParticleWriter.writeParticle(writer.root, groupRef, s);
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
        GroupReference groupRef = new GroupReference(new QualifiedName(defaultNamespace,"someGroup"));
        sequencePattern.addParticle(groupRef);
        ParticleWriter.writeParticleContainer(writer.root, sequencePattern, s, 3, null);
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
        GroupReference groupRef = new GroupReference(new QualifiedName(defaultNamespace,"someGroup"));
        sequencePattern.addParticle(groupRef);
        ParticleWriter.writeParticleContainer(writer.root, sequencePattern, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("sequence"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs"));
        assertTrue(writer.root.getFirstChild().getFirstChild().getLocalName().equals("any"));
        assertTrue(writer.root.getFirstChild().getLastChild().getLocalName().equals("group"));
    }

    @Test
    public void testWriteCountingPattern() {
        SequencePattern sequencePattern = new SequencePattern();
        CountingPattern countingPattern = new CountingPattern(sequencePattern, 123, null);
        ParticleWriter.writeCountingPattern(writer.root, countingPattern, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("sequence"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("123"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("unbounded"));
    }

    @Test
    public void testWriteElement() {
        Element element = new Element(new QualifiedName(barNamespace,"foo"));
        QualifiedName typeName = new QualifiedName(xsdNamespace,"integer");
        SimpleType type = new SimpleType(typeName, null);
        element.setTypeName(type.getName());
        ParticleWriter.writeElement(writer.root, element, s, 2, 3);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs").getTextContent().equals("2"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs").getTextContent().equals("3"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("foo"));
    }

    @Test
    public void testWriteElementWithConstraints() {
        Element element = new Element(new QualifiedName(barNamespace,"foo"));
        Unique unique = new Unique(new QualifiedName(barNamespace,"someUnique"), "foo/bar");
        unique.addField("@id");
        unique.addField("name");
        Key key = new Key(new QualifiedName(barNamespace,"someKey"), "foo/bar");
        key.addField("@id");
        key.addField("name");
        KeyRef keyRef = new KeyRef(new QualifiedName(barNamespace,"someKeyRef"), "foo/bar", key);
        keyRef.addField("@id");
        keyRef.addField("name");
        element.addConstraint(unique);
        element.addConstraint(key);
        element.addConstraint(keyRef);
        QualifiedName typeName = new QualifiedName(xsdNamespace,"integer");
        SimpleType type = new SimpleType(typeName, null);
        element.setTypeName(type.getName());
        ParticleWriter.writeElement(writer.root, element, s, 2, 3);
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
        Element element = new Element(new QualifiedName(barNamespace,"foo"));
        element.setDefault("0");
        element.setFixed("1");
        element.setNillable();
        QualifiedName typeName = new QualifiedName(xsdNamespace,"integer");
        SimpleType type = new SimpleType(typeName, null);
        element.setTypeName(type.getName());
        ParticleWriter.writeElement(writer.root, element, s, 2, 3);
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
        Element element = new Element(new QualifiedName(barNamespace,"foo"));
        QualifiedName typeName = new QualifiedName(xsdNamespace,"integer");
        SimpleType type = new SimpleType(typeName, null);
        element.setTypeName(type.getName());
        ParticleWriter.writeElement(writer.root, element, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("element"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("minOccurs"));
        assertNull(writer.root.getFirstChild().getAttributes().getNamedItem("maxOccurs"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("foo"));
    }

}
