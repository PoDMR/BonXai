package eu.fox7.schematoolkit.xsd.writer;

import org.junit.Test;
import org.junit.Before;

import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.writer.ConstraintWriter;
import eu.fox7.schematoolkit.xsd.writer.XSDWriter;

public class ConstraintWriterTest extends junit.framework.TestCase {

    XSDWriter writer;
    XSDSchema s;
    DefaultNamespace defaultNameSpace;
	private IdentifiedNamespace xsdNamespace;
	private IdentifiedNamespace barNamespace;

    @Before
    public void setUp() {
        s = new XSDSchema();

        defaultNameSpace = new DefaultNamespace("http://example.com/xyz");
        xsdNamespace = new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        barNamespace = new IdentifiedNamespace("bar", "http://example.com/bar");
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
    public void testWriteUnique() {
        Unique unique = new Unique(new QualifiedName(defaultNameSpace,"someUnique"), "foo/bar");
        unique.addField("@id");
        unique.addField("name");
        ConstraintWriter.writeConstraint(writer.root, unique, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("unique"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("someUnique"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getLocalName().equals("selector"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getAttributes().getNamedItem("xpath").getTextContent().equals("foo/bar"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getAttributes().getNamedItem("xpath").getTextContent().equals("name"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getAttributes().getNamedItem("xpath").getTextContent().equals("@id"));
    }

    @Test
    public void testWriteKey() {
        Key key = new Key(new QualifiedName(defaultNameSpace,"someKey"), "foo/bar");
        key.addField("@id");
        key.addField("name");
        ConstraintWriter.writeConstraint(writer.root, key, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("key"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("someKey"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getLocalName().equals("selector"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getAttributes().getNamedItem("xpath").getTextContent().equals("foo/bar"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getAttributes().getNamedItem("xpath").getTextContent().equals("name"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getAttributes().getNamedItem("xpath").getTextContent().equals("@id"));
    }

    @Test
    public void testWriteKeyRef() {
        Key key = new Key(new QualifiedName(defaultNameSpace,"someKey"), "foo/bar");
        KeyRef keyRef = new KeyRef(new QualifiedName(defaultNameSpace,"someKeyRef"), "foo/bar", key);
        keyRef.addField("@id");
        keyRef.addField("name");
        ConstraintWriter.writeConstraint(writer.root, keyRef, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("keyref"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("someKeyRef"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("refer").getTextContent().equals("someKey"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getLocalName().equals("selector"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getAttributes().getNamedItem("xpath").getTextContent().equals("foo/bar"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getAttributes().getNamedItem("xpath").getTextContent().equals("name"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getAttributes().getNamedItem("xpath").getTextContent().equals("@id"));
    }

    @Test
    public void testWriteKeyRefWithNamespacePrefix() {
        Key key = new Key(new QualifiedName(barNamespace,"someUnique"), "foo/bar");
        KeyRef keyRef = new KeyRef(new QualifiedName(defaultNameSpace,"someKeyRef"), "foo/bar", key);
        keyRef.addField("@id");
        keyRef.addField("name");
        ConstraintWriter.writeConstraint(writer.root, keyRef, s);
        assertTrue(writer.root.getFirstChild().getLocalName().equals("keyref"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("name").getTextContent().equals("someKeyRef"));
        assertTrue(writer.root.getFirstChild().getAttributes().getNamedItem("refer").getTextContent().equals("bar:someKey"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getLocalName().equals("selector"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(0).getAttributes().getNamedItem("xpath").getTextContent().equals("foo/bar"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(1).getAttributes().getNamedItem("xpath").getTextContent().equals("name"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getLocalName().equals("field"));
        assertTrue(writer.root.getFirstChild().getChildNodes().item(2).getAttributes().getNamedItem("xpath").getTextContent().equals("@id"));
    }

}
