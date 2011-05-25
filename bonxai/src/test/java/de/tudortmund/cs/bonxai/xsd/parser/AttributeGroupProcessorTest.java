package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.AttributeGroupRef;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.ExclusiveAttributesException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.InvalidNCNameException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.InvalidQNameException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.AnyAttributeIsNotLastException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AttributeGroupProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeGroupProcessorTest extends junit.framework.TestCase {

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
    public void testValidCaseWithAttributeGroupAndAttributeGroupRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        if (schema.getAttributeGroups().isEmpty()) {
            fail("There are no AttributeGroups in the Schema");
        }

        AttributeGroup attributeGroup = schema.getAttributeGroups().getFirst();
        attributeGroup.getAttributeParticles();
        if (attributeGroup.getAttributeParticles().isEmpty()) {
            fail("There are no AttributeParticles in the AttributeGroup");
        }
        assertEquals(2, attributeGroup.getAttributeParticles().size());
        
        assertEquals("attributeGroupOne", attributeGroup.getId());
        assertEquals("{http://www.example.org}personattr", attributeGroup.getName());
        assertEquals("personattr", attributeGroup.getLocalName());
        assertEquals("http://www.example.org", attributeGroup.getNamespace());
        
        if (attributeGroup.getAnnotation() == null
                || attributeGroup.getAnnotation().getDocumentations() == null
                || attributeGroup.getAnnotation().getDocumentations().getFirst() == null
                || attributeGroup.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || attributeGroup.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(attributeGroup.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("a attributeGroupOne"));


        if (schema.getTypes().isEmpty()) {
            fail("There are no Types in the Schema");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().getFirst();
        if (complexType.getAttributes().isEmpty()) {
            fail("There are no Attributes in the ComplexType");
        }
        assertTrue(complexType.getAttributes().getFirst() instanceof AttributeGroupRef);
        AttributeGroupRef attributeGroupRef = (AttributeGroupRef) complexType.getAttributes().getFirst();

        // The attributeGroupRef of the AttributeGroup has to reference the same object as attributeGroup seen above.
        assertEquals(attributeGroupRef.getAttributeGroup(), attributeGroup);
    }

    @Test
    public void testInvalidQNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_invalid_qname.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("QName is invalid, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_exclusive_attributes.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("There are name and ref given in the attributeGroup, but this is not allowed and wasn't detected.");
    }

    @Test
    public void testEmptyIdExceptionWithAttributeGroupRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_empty_id_2.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testInvalidNCNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_invalid_ncname.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("NCName is invalid, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdExceptionWithAttributeGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_anyattribute.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is not last in AttributeGroup, but this was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/attributeGroupTests/attributegroup_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of AttributeGroup, but it was not detected.");
    }
}
