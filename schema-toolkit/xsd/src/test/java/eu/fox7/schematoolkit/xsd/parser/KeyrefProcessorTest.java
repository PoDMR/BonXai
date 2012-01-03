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
public class KeyrefProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Element root = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}root").getReference();
        assertTrue(root.getConstraints().get(1)  instanceof KeyRef);
        KeyRef keyRef = (KeyRef) root.getConstraints().get(1);
        assertTrue(keyRef.getId().equals("myIdref"));
        assertTrue(keyRef.getName().equals("{http://www.w3.org/2001/XMLSchema}myIdref"));
        assertTrue(keyRef.getSelector().equals("./BBB/b"));
        assertTrue(keyRef.getFields().contains("@idref"));
        assertTrue(keyRef.getFields().contains("@other"));
    }

    @Test
    public void testDuplicatedSelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_duplicated_selector.xsd"));
        } catch (DuplicateSelectorException error) {
            return;
        }
        fail("A keyRef contains two selector, but it wasn't detected.");
    }

    @Test
    public void testEmptySelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_empty_selector.xsd"));
        } catch (EmptySelectorException error) {
            return;
        }
        fail("A keyRef contains an empty selector, but it wasn't detected.");
    }

    @Test
    public void testEmptyFieldException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_empty_field.xsd"));
        } catch (EmptyFieldException error) {
            return;
        }
        fail("A keyRef contains an empty field, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A keyRef contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A keyRef contains unsupported content, but it wasn't detected.");
    }

    @Test
    public void testInvalidNCNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_invalid_nCName.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("A keyRef contains a name attribute with invalid NCName, but it wasn't detected.");
    }

    @Test
    public void testInvalidQNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_invalid_qName.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("A keyRef contains a refer attribute with invalid QName, but it wasn't detected.");
    }

    @Test
    public void testEmptyReferException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_empty_refer.xsd"));
        } catch (EmptyReferException error) {
            return;
        }
        fail("A keyRef contains an empty refer, but it wasn't detected.");
    }

    @Test
    public void testMissingReferException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_missing_refer.xsd"));
        } catch (MissingReferException error) {
            return;
        }
        fail("A keyRef contains no refer, but it wasn't detected.");
    }

    @Test
    public void testMissingSelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_missing_selector.xsd"));
        } catch (MissingSelectorException error) {
            return;
        }
        fail("A keyRef contains no selector, but it wasn't detected.");
    }

    @Test
    public void testMissingFieldException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_missing_field.xsd"));
        } catch (MissingFieldException error) {
            return;
        }
        fail("A keyRef contains no field, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a keyRef element is empty, but it wasn't detected.");
    }
    @Test
    public void testDuplicateConstraintException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/keyRefTests/keyRef_duplicate_constraint.xsd"));
        } catch (DuplicateConstraintException error) {
            return;
        }
        fail("There is a duplicate constraint, but it wasn't detected.");
    }
}