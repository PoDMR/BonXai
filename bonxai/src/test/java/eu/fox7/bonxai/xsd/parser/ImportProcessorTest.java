package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.xsd.ImportedSchema;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.InvalidAnyUriException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ImportProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class ImportProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/importTests/import_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        if (schema.getForeignSchemas().isEmpty()) {
            fail("Schema has no foreignSchemas attached");
        }
        if (!(schema.getForeignSchemas().getFirst() instanceof ImportedSchema)) {
            fail("ForeignSchema is not ImportedSchema");
        }

        ImportedSchema importedSchema = (ImportedSchema) schema.getForeignSchemas().getFirst();

        if (importedSchema.getAnnotation() == null
                || importedSchema.getAnnotation().getDocumentations() == null
                || importedSchema.getAnnotation().getDocumentations().getFirst() == null
                || importedSchema.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || importedSchema.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(importedSchema.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("an importedSchema"));

        assertEquals("importOne" , importedSchema.getId());
        assertEquals("http://www.w3.org/2001/XMLSchema.xsd" , importedSchema.getSchemaLocation());
        assertEquals("http://www.example.org" , importedSchema.getNamespace());
    }

    @Test
    public void testInvalideAnyUriExceptionWithNamespace() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/importTests/import_invalid_anyuri_ns.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("AnyUri is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalideAnyUriExceptionWithSchemaLocation() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/importTests/import_invalid_anyuri.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("AnyUri is invalid (schemaLocation), but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/importTests/import_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/importTests/import_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/importTests/import_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of import, but it was not detected.");
    }

}