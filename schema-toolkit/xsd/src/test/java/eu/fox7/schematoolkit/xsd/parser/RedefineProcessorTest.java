package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.RedefinedSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.InvalidAnyUriException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.MissingSchemaLocationException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.IllegalObjectReturnedException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class RedefineProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class RedefineProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        if (schema.getForeignSchemas().isEmpty()) {
            fail("Schema has no foreignSchemas attached");
        }
        if (!(schema.getForeignSchemas().getFirst() instanceof RedefinedSchema)) {
            fail("ForeignSchema is not RedefinedSchema");
        }

        RedefinedSchema redefinedSchema = (RedefinedSchema) schema.getForeignSchemas().getFirst();

        if (redefinedSchema.getAnnotation() == null
                || redefinedSchema.getAnnotation().getDocumentations() == null
                || redefinedSchema.getAnnotation().getDocumentations().getFirst() == null
                || redefinedSchema.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || redefinedSchema.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(redefinedSchema.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("a RedefinedSchema"));

        if (redefinedSchema.getAttributeGroups().isEmpty()) {
            fail("There is no AttributeGroup");
        }
        assertEquals("{http://www.example.org}personattr", redefinedSchema.getAttributeGroups().iterator().next().getName());
        
        if (redefinedSchema.getGroups().isEmpty()) {
            fail("There is no Group");
        }
        assertEquals("{http://www.example.org}groupRedefine", redefinedSchema.getGroups().iterator().next().getName());

        if (redefinedSchema.getTypes().isEmpty()) {
            fail("There is no Type");
        }
        assertEquals("{http://www.example.org}openAttrs", redefinedSchema.getTypes().iterator().next().getName());

        assertEquals("redefineOne" , redefinedSchema.getId());
        assertEquals("http://www.w3.org/2001/XMLSchema.xsd" , redefinedSchema.getSchemaLocation());
    }

    @Test
    public void testInvalideAnyUriException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_invalid_anyuri.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("AnyUri is invalid, but it wasn't detected.");
    }

    @Test
    public void testMissingSchemaLocationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_missing_location.xsd"));
        } catch (MissingSchemaLocationException error) {
            return;
        }
        fail("Attribute schemaLocation is missing, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_invalid_object_group.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("There is a GroupRef under Redefine, but this was not detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithAttributeGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_invalid_object_attributegroup.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("There is a AttributeGroupRef under Redefine, but this was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/redefineTests/redefine_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of redefine, but it was not detected.");
    }

}