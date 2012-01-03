package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.constraint.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class UniqueProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Element root = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}root").getReference();
        assertTrue(root.getConstraints().get(0)  instanceof Unique);
        Unique unique = (Unique) root.getConstraints().get(0);
        assertTrue(unique.getId().equals("id"));
        assertTrue(unique.getName().equals("{http://www.w3.org/2001/XMLSchema}myId"));
        assertTrue(unique.getSelector().equals("./AAA/a"));
        assertTrue(unique.getFields().contains("@id"));
        assertTrue(unique.getFields().contains("@other"));
    }

    @Test
    public void testInvalidNCNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_invalid_nCName.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("A unique contains a name attribute with invalid NCName, but it wasn't detected.");
    }

    @Test
    public void testMissingSelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_missing_selector.xsd"));
        } catch (MissingSelectorException error) {
            return;
        }
        fail("A unique contains no selector, but it wasn't detected.");
    }

    @Test
    public void testMissingFieldException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_missing_field.xsd"));
        } catch (MissingFieldException error) {
            return;
        }
        fail("A unique contains no field, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a unique element is empty, but it wasn't detected.");
    }

    @Test
    public void testDuplicatedSelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_duplicated_selector.xsd"));
        } catch (DuplicateSelectorException error) {
            return;
        }
        fail("A unique contains two selector, but it wasn't detected.");
    }

    @Test
    public void testEmptySelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_empty_selector.xsd"));
        } catch (EmptySelectorException error) {
            return;
        }
        fail("A unique contains an empty selector, but it wasn't detected.");
    }

    @Test
    public void testEmptyFieldException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_empty_field.xsd"));
        } catch (EmptyFieldException error) {
            return;
        }
        fail("A unique contains an empty field, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A unique contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A unique contains unsupported content, but it wasn't detected.");
    }
    
    @Test
    public void testDuplicateConstraintException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/uniqueTests/unique_duplicate_constraint.xsd"));
        } catch (DuplicateConstraintException error) {
            return;
        }
        fail("There is a duplicate constraint, but it wasn't detected.");
    }
}