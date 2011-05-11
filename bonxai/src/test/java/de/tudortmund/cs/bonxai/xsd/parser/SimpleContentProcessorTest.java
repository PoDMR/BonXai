package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import de.tudortmund.cs.bonxai.xsd.SimpleContentType;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance.MissingSimpleContentInheritanceException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance.SimpleContentMultipleInheritanceException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class SimpleContentProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class SimpleContentProcessorTest extends junit.framework.TestCase{

    // Schema for this testcase
    XSDSchema schema;

    // SchemaProcessor used by the testcase
    SchemaProcessor schemaProcessor;

    /**
     * Befor every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        schema = new XSDSchema();
        schemaProcessor = new SchemaProcessor(schema);
    }

    /**
     * Test of processNode method for a valid case
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/simpleContentTests/simplecontent_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType SizeType
        if (!(schema.getTypes().get(1) instanceof ComplexType)) {
            fail("Type SizeType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().get(1);
        if (!(complexType.getContent() instanceof SimpleContentType)) {
            fail("Content of ComplexType:SizeType is not SimpleContentType");
        }
        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
        if (!(simpleContentType.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleContent of ComplexType:SizeType is not SimpleContentRestriction");
        }

        assertEquals("simpleContentTypeOne", simpleContentType.getId());

        if (simpleContentType.getAnnotation() == null
                || simpleContentType.getAnnotation().getDocumentations() == null
                || simpleContentType.getAnnotation().getDocumentations().getFirst() == null
                || simpleContentType.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || simpleContentType.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(simpleContentType.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("a simpleContentType"));

        SimpleContentRestriction restriction = (SimpleContentRestriction) simpleContentType.getInheritance();

        assertEquals("6", restriction.getMaxInclusive().getValue());
        assertEquals("2", restriction.getMinInclusive().getValue());
        if (restriction.getAttributes().isEmpty()) {
            fail("There is no attribute defined under restriction");
        }

        Attribute attribute = (Attribute) restriction.getAttributes().getFirst();
        assertEquals("{http://www.example.org}system", attribute.getName());
        AnyAttribute anyAttribute = (AnyAttribute) restriction.getAttributes().getLast();
        assertEquals("anyAttributeID", anyAttribute.getId());
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/simpleContentTests/simplecontent_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testMissingSimpleContentInheritanceException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/simpleContentTests/simplecontent_no_inherit.xsd"));
        } catch (MissingSimpleContentInheritanceException error) {
            return;
        }
        fail("There is no Inheritance given, but this was not detected.");
    }

    @Test
    public void testSimpleContentMultipleInheritanceExceptionWithExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/simpleContentTests/simplecontent_multi_inherit_ext.xsd"));
        } catch (SimpleContentMultipleInheritanceException error) {
            return;
        }
        fail("There is more than one Inheritance given, but this was not detected.");
    }

    @Test
    public void testSimpleContentMultipleInheritanceExceptionWithRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/simpleContentTests/simplecontent_multi_inherit_restr.xsd"));
        } catch (SimpleContentMultipleInheritanceException error) {
            return;
        }
        fail("There is more than one Inheritance given, but this was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/simpleContentTests/simplecontent_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/simpleContentTests/simplecontent_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of SimpleContent, but it was not detected.");
    }
}