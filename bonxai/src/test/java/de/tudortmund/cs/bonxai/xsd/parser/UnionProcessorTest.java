package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import de.tudortmund.cs.bonxai.xsd.SimpleContentUnion;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.InvalidQNameException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.type.EmptySimpleContentUnionMemberTypesException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for class UnionProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class UnionProcessorTest extends junit.framework.TestCase {

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/unionTests/union_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        if (!(schema.getElements().getFirst().getType() instanceof SimpleType)) {
            fail("Type of Element is not SimpleType");
        }
        SimpleType simpleType = (SimpleType) schema.getElements().getFirst().getType();
        if (!(simpleType.getInheritance() instanceof SimpleContentUnion)) {
            fail("Inheritance of SimpleType is not SimpleContentUnion");
        }
        SimpleContentUnion union = (SimpleContentUnion) simpleType.getInheritance();

        assertEquals("unionOne", union.getId());

        assertEquals(2, union.getMemberTypes().size());
        assertEquals(1, union.getAnonymousSimpleTypes().size());
        assertTrue(union.getMemberTypes().getFirst().getKey().equals("{http://www.example.org}sizebyno"));
        assertTrue(union.getMemberTypes().getLast().getKey().equals("{http://www.example.org}sizebystring"));

        SimpleType unionSimpleType = (SimpleType) union.getAnonymousSimpleTypes().getFirst().getReference();

        if (!(unionSimpleType.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of Union-SimpleType is not SimpleContentRestriction");
        }
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) unionSimpleType.getInheritance();
        assertEquals(2, simpleContentRestriction.getEnumeration().size());

        assertEquals("XS", simpleContentRestriction.getEnumeration().getFirst());
        assertEquals("XXL", simpleContentRestriction.getEnumeration().getLast());

        if (union.getAnnotation() == null
                || union.getAnnotation().getDocumentations() == null
                || union.getAnnotation().getDocumentations().getFirst() == null
                || union.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || union.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(union.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("a union of three types"));
    }

    /**
     * Test of processNode method, of class UnionProcessor, for an invalid XSD
     * which contains an invalid QName
     * @throws java.lang.Exception
     */
    @Test
    public void testInvalidQNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/unionTests/union_invalid_qname.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("QName is invalid, but it wasn't detected.");
    }

    /**
     * Test of processNode method, of class UnionProcessor, for an invalid XSD
     * which contains an empty ID
     * @throws java.lang.Exception
     */
    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/unionTests/union_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    /**
     * Test of processNode method, of class UnionProcessor, for an invalid XSD
     * which contains no MemberTypes or SimpleTypes
     * @throws java.lang.Exception
     */
    @Test
    public void testEmptySimpleContentUnionMemberTypesException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/unionTests/union_empty_memberTypes.xsd"));
        } catch (EmptySimpleContentUnionMemberTypesException error) {
            return;
        }
        fail("MemberTypes are empty, but it was not detected.");
    }

    /**
     * Test of processChild method, of class UnionProcessor, for an invalid XSD
     * which contains multiple annotations
     * @throws java.lang.Exception
     */
    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/unionTests/union_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    /**
     * Test of processChild method, of class UnionProcessor, for an invalid XSD
     * which an unsupported and wrong content child
     * @throws java.lang.Exception
     */
    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/unionTests/union_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of union, but it was not detected.");
    }
}
