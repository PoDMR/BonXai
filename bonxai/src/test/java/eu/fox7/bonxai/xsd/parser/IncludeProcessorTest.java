package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.xsd.IncludedSchema;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.InvalidAnyUriException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.MissingSchemaLocationException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class IncludeProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class IncludeProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/includeTests/include_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        if (schema.getForeignSchemas().isEmpty()) {
            fail("Schema has no foreignSchemas attached");
        }
        if (!(schema.getForeignSchemas().getFirst() instanceof IncludedSchema)) {
            fail("ForeignSchema is not IncludedSchema");
        }

        IncludedSchema includedSchema = (IncludedSchema) schema.getForeignSchemas().getFirst();

        if (includedSchema.getAnnotation() == null
                || includedSchema.getAnnotation().getDocumentations() == null
                || includedSchema.getAnnotation().getDocumentations().getFirst() == null
                || includedSchema.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || includedSchema.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(includedSchema.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("an IncludedSchema"));

        assertEquals("includeOne" , includedSchema.getId());
        assertEquals("http://www.w3.org/2001/XMLSchema.xsd" , includedSchema.getSchemaLocation());
    }

    @Test
    public void testInvalideAnyUriException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/includeTests/include_invalid_anyuri.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("AnyUri is invalid, but it wasn't detected.");
    }

    @Test
    public void testMissingSchemaLocationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/includeTests/include_missing_location.xsd"));
        } catch (MissingSchemaLocationException error) {
            return;
        }
        fail("Attribute schemaLocation is missing, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/includeTests/include_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/includeTests/include_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/includeTests/include_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of include, but it was not detected.");
    }
}