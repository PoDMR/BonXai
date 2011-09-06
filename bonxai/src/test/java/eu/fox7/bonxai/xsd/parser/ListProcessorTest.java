package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.xsd.SimpleContentList;
import eu.fox7.bonxai.xsd.SimpleContentRestriction;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.InvalidQNameException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;
import eu.fox7.bonxai.xsd.parser.exceptions.type.EmptySimpleContentListTypeException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for class ListProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class ListProcessorTest extends junit.framework.TestCase {

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/listTests/list_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        if (!(schema.getTypeSymbolTable().hasReference("{http://www.example.org}listWithContent"))) {
            fail("Type listWithContent is not present");
        }

        if (!(schema.getTypeSymbolTable().hasReference("{http://www.example.org}listOfString"))) {
            fail("Type listOfString is not present");
        }

        assertEquals("{http://www.example.org}listWithContent", schema.getTypes().getFirst().getName());
        assertEquals("{http://www.example.org}listOfString", schema.getTypes().getLast().getName());



        // Check first simpleType (Case with RefSimpleType):
        SimpleType simpleTypeOne = (SimpleType) schema.getTypes().getFirst();
        if (!(simpleTypeOne.getInheritance() instanceof SimpleContentList)) {
            fail("Inheritance of SimpleType:listWithContent is not SimpleContentList");
        }
        SimpleContentList list = (SimpleContentList) simpleTypeOne.getInheritance();

        assertEquals("listOne", list.getId());
        assertTrue(list.getBaseSimpleType() != null);

        SimpleType listContentSimpleType = list.getBaseSimpleType();

        if (!(listContentSimpleType.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of List-SimpleType is not SimpleContentRestriction");
        }

        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) listContentSimpleType.getInheritance();
        assertEquals(3, simpleContentRestriction.getEnumeration().size());



        // Check second simpleType (Case with getBaseSimpleType):
        SimpleType simpleTypeTwo = (SimpleType) schema.getTypes().getLast();
        if (!(simpleTypeTwo.getInheritance() instanceof SimpleContentList)) {
            fail("Inheritance of SimpleType:listWithContent is not SimpleContentList");
        }
        SimpleContentList listTwo = (SimpleContentList) simpleTypeTwo.getInheritance();

        assertEquals("listTwo", listTwo.getId());
        assertTrue(listTwo.getBaseSimpleType() != null);

        SimpleType listBaseSimpleType = listTwo.getBaseSimpleType();

        assertEquals("{http://www.w3.org/2001/XMLSchema}string", listBaseSimpleType.getName());

        assertTrue(listBaseSimpleType.getInheritance() == null);

        if (list.getAnnotation() == null
                || list.getAnnotation().getDocumentations() == null
                || list.getAnnotation().getDocumentations().getFirst() == null
                || list.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || list.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(list.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("a list"));
    }

    /**
     * Test of processNode method, of class ListProcessor, for an invalid XSD
     * which contains an invalid QName
     * @throws java.lang.Exception
     */
    @Test
    public void testInvalidQNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/listTests/list_invalid_qname.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("QName is invalid, but it wasn't detected.");
    }

    /**
     * Test of processNode method, of class ListProcessor, for an invalid XSD
     * which contains no BaseSimpleType
     * @throws java.lang.Exception
     */
    @Test
    public void testEmptySimpleContentListTypeException_1() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/listTests/list_no_base_1.xsd"));
        } catch (EmptySimpleContentListTypeException error) {
            return;
        }
        fail("There is no BaseSimpleType, but this wasn't detected.");
    }

    /**
     * Test of processNode method, of class ListProcessor, for an invalid XSD
     * which contains no RefSimpleType
     * @throws java.lang.Exception
     */
    @Test
    public void testEmptySimpleContentListTypeException_2() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/listTests/list_no_base_2.xsd"));
        } catch (EmptySimpleContentListTypeException error) {
            return;
        }
        fail("There is no RefSimpleType, but this wasn't detected.");
    }

        /**
     * Test of processNode method, of class ListProcessor, for an invalid XSD
     * which contains an empty ID
     * @throws java.lang.Exception
     */
    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/listTests/list_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    /**
     * Test of processChild method, of class ListProcessor, for an invalid XSD
     * which contains multiple annotations
     * @throws java.lang.Exception
     */
    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/listTests/list_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    /**
     * Test of processChild method, of class ListProcessor, for an invalid XSD
     * which an unsupported and wrong content child
     * @throws java.lang.Exception
     */
    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/listTests/list_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of list, but it was not detected.");
    }
}
