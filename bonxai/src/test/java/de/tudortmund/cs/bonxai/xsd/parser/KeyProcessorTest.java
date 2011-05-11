package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.constraint.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Testcase of the key class
 * @author Lars Schmidt, Dominik Wolff
 */
public class KeyProcessorTest extends junit.framework.TestCase {

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
     * Test of processNode method, of class KeyProcessor, for a valid XSD with a
     * key containing two fields.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/TwoFields.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Key key = (Key) schema.getKeyAndUniqueSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}twoFields").getReference();
        String selector = key.getSelector();
        assertEquals(selector, "xs:group");
        assertEquals(true, key.getFields().contains("@one"));
        assertEquals(true, key.getFields().contains("@two"));
    }

    /**
     * Test of processNode method, of class KeyProcessor, for an invalid XSD
     * which contains two keys with same name
     * @throws java.lang.Exception
     */
    @Test
    public void testDuplicatedKeyException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/DupKey.xsd"));
        } catch (DuplicateConstraintException error) {
            return;
        }
        fail("Duplicted key was not found");
    }

    /**
     * Test of processNode method, of class KeyProcessor, for an invalid XSD
     * which contains a key with two selectors
     * @throws java.lang.Exception
     */
    @Test
    public void testDuplicatedSelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/DupSel.xsd"));
        } catch (DuplicateSelectorException error) {
            return;
        }
        fail("Duplicted selector was not found");
    }

    /**
     * Test of processNode method, of class KeyProcessor, for an invalid XSD
     * which contains a key with an empty field
     * @throws java.lang.Exception
     */
    @Test
    public void testEmptyFieldException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/EmpFie.xsd"));
        } catch (EmptyFieldException error) {
            return;
        }
        fail("Empoty field was not found");
    }

    /**
     * Test of processNode method, of class KeyProcessor, for an invalid XSD
     * which contains a key with an empty selector
     * @throws java.lang.Exception
     */
    @Test
    public void testEmptySelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/EmpSel.xsd"));
        } catch (EmptySelectorException error) {
            return;
        }
        fail("Empty selector was not found");
    }

    /**
     * Test of processNode method, of class KeyProcessor, for an invalid XSD
     * which contains a key with no fields
     * @throws java.lang.Exception
     */
    @Test
    public void testMissingFieldException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/MisFie.xsd"));
        } catch (MissingFieldException error) {
            return;
        }
        fail("Missing field was not found");
    }

    /**
     * Test of processNode method, of class KeyProcessor, for an invalid XSD
     * which contains a key with no selector
     * @throws java.lang.Exception
     */
    @Test
    public void testMissingSelectorException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/MisSel.xsd"));
        } catch (MissingSelectorException error) {
            return;
        }
        fail("Missing selector was not found");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/key_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a key element is empty, but it wasn't detected.");
    }

    @Test
    public void testInvalidNCNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/key_invalid_nCName.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("A key contains a name attribute with invalid NCName, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/key_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A key contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/key_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A key contains unsupported content, but it wasn't detected.");
    }

    @Test
    public void testDuplicateConstraintException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/keyTests/key_duplicate_constraint.xsd"));
        } catch (DuplicateConstraintException error) {
            return;
        }
        fail("There is a duplicate constraint, but it wasn't detected.");
    }
}