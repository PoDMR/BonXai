package eu.fox7.schematoolkit.xsd.parser;


import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class AnyAttributeProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyAttributeTests/anyAttribute_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        ComplexType complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}first").getReference();
        Object object = complexType.getAttributes().getFirst();
        assertTrue(object instanceof AnyAttribute);
        AnyAttribute anyAttribute = (AnyAttribute) object;
        assertTrue(anyAttribute.getId().equals("first"));
        assertTrue(anyAttribute.getNamespace().equals("http://www.w3.org/1999/xhtml ##targetNamespace ##local"));
        assertTrue(anyAttribute.getProcessContentsInstruction() == ProcessContentsInstruction.LAX);


        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}second").getReference();
        object = complexType.getAttributes().getFirst();
        assertTrue(object instanceof AnyAttribute);
        anyAttribute = (AnyAttribute) object;
        assertTrue(anyAttribute.getId().equals("second"));
        assertTrue(anyAttribute.getNamespace().equals("##any"));
        assertTrue(anyAttribute.getProcessContentsInstruction() == ProcessContentsInstruction.SKIP);

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}third").getReference();
        object = complexType.getAttributes().getFirst();
        assertTrue(object instanceof AnyAttribute);
        anyAttribute = (AnyAttribute) object;
        assertTrue(anyAttribute.getId().equals("third"));
        assertTrue(anyAttribute.getNamespace().equals("##other"));
        assertTrue(anyAttribute.getProcessContentsInstruction() == ProcessContentsInstruction.STRICT);
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyAttributeTests/anyAttribute_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a anyAttribute element is empty, but it wasn't detected.");
    }

    @Test
    public void testInvalidProcessContentsValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyAttributeTests/anyAttribute_invalid_processContent_value.xsd"));
        } catch (InvalidProcessContentsValueException error) {
            return;
        }
        fail("The processcontent attribute of a anyAttribute element is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidNamespaceValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyAttributeTests/anyAttribute_invalid_namespace_value.xsd"));
        } catch (InvalidNamespaceValueException error) {
            return;
        }
        fail("The namespace attribute of a anyAttribute element is invalid, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyAttributeTests/anyAttribute_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("An anyAttribute contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyAttributeTests/anyAttribute_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("An anyAttribute contains unsupported content, but it wasn't detected.");
    }
}