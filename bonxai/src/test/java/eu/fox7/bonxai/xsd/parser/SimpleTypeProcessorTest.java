package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.xsd.SimpleContentRestriction;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.SimpleTypeInheritanceModifier;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.InvalidFinalValueException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.InvalidNCNameException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;
import eu.fox7.bonxai.xsd.parser.exceptions.inheritance.MissingSimpleTypeInheritanceException;
import eu.fox7.bonxai.xsd.parser.exceptions.inheritance.SimpleTypeMultipleInheritanceException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class SimpleTypeProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class SimpleTypeProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        if (schema.getTypes().isEmpty() || !(schema.getTypes().getFirst() instanceof SimpleType)) {
            fail("There is no SimpleType found");
        }
        SimpleType simpleType = (SimpleType) schema.getTypes().getFirst();

        assertEquals("simpleTypeOne", simpleType.getId());
        assertEquals("http://www.w3.org/2001/XMLSchema", simpleType.getNamespace());
        assertEquals("http://www.w3.org/2001/XMLSchema", simpleType.getNamespace());
        assertEquals("stringtype", simpleType.getLocalName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}stringtype", simpleType.getName());
        assertEquals("http://www.w3.org/2001/XMLSchema", simpleType.getNamespace());

        assertFalse(simpleType.getFinalModifiers().isEmpty());
        assertFalse(simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.List));
        assertFalse(simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Union));
        assertTrue(simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Restriction));

        
        if (simpleType.getAnnotation() == null
                || simpleType.getAnnotation().getDocumentations() == null
                || simpleType.getAnnotation().getDocumentations().getFirst() == null
                || simpleType.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || simpleType.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(simpleType.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("a simpleType"));

        if (!(simpleType.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleType is not SimpleContentRestriction");
        }
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleContentRestriction.getBase().getName());
    }

    /**
     * Test of processNode method, of class SimpleTypeProcessor, for an invalid XSD
     * which contains no Inheritance
     * @throws java.lang.Exception
     */
    @Test
    public void testMissingSimpleTypeInheritanceException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_no_inherit.xsd"));
        } catch (MissingSimpleTypeInheritanceException error) {
            return;
        }
        fail("There is no Inheritance in the tested SimpleType, but this wasn't detected.");
    }

    @Test
    public void testInvalidNCNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_invalid_ncname.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("There is an invalid NCName used for the tested SimpleType, but this wasn't detected.");
    }

    @Test
    public void testInvalidFinalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_invalid_final.xsd"));
        } catch (InvalidFinalValueException error) {
            return;
        }
        fail("There is an invalid final value used in the tested SimpleType, but this wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testSimpleTypeMultipleInheritanceExceptionWithRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_multi_inherit.xsd"));
        } catch (SimpleTypeMultipleInheritanceException error) {
            return;
        }
        fail("There is more than one Inheritance (restriction) declared, but this was not detected.");
    }

    @Test
    public void testSimpleTypeMultipleInheritanceExceptionWithList() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_multi_inherit_list.xsd"));
        } catch (SimpleTypeMultipleInheritanceException error) {
            return;
        }
        fail("There is more than one Inheritance (list) declared, but this was not detected.");
    }

    @Test
    public void testSimpleTypeMultipleInheritanceExceptionWithUnion() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_multi_inherit_union.xsd"));
        } catch (SimpleTypeMultipleInheritanceException error) {
            return;
        }
        fail("There is more than one Inheritance (union) declared, but this was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/simpleTypeTests/simpletype_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of simpleType, but it was not detected.");
    }

}