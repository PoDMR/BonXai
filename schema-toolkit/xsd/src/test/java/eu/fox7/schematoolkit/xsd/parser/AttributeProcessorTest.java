package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.AttributeRef;
import eu.fox7.schematoolkit.common.AttributeUse;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.type.MultipleTypesException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeProcessorTest extends junit.framework.TestCase{

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
    public void testValidCaseWithAttribute() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_valid_attribute.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        ComplexType complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}complex").getReference();
        Object object = complexType.getAttributes().getFirst();
        assertTrue(object instanceof Attribute);
        Attribute attribute = (Attribute) object;
        assertTrue(attribute.getName().equals("{http://www.w3.org/2001/XMLSchema}first"));
        assertTrue(attribute.getId().equals("id1"));
        assertTrue(attribute.getTypeAttr());
        assertTrue(attribute.getSimpleType() != null);
        assertTrue(attribute.getForm() == XSDSchema.Qualification.unqualified);
        assertTrue(attribute.getUse() == AttributeUse.required);
        assertTrue(attribute.getFixed().equals("boo"));

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}type").getReference();
        object = complexType.getAttributes().getFirst();
        assertTrue(object instanceof Attribute);
        attribute = (Attribute) object;
        assertTrue(attribute.getLocalName().equals("second"));
        assertTrue(attribute.getSimpleType() != null);
        assertTrue(attribute.getDefault().equals("6"));
    }

    @Test
    public void testValidCaseWithAttributeRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_valid_attributeRef.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        ComplexType complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}complex").getReference();
        Object object = complexType.getAttributes().getFirst();
        assertTrue(object instanceof AttributeRef);
        AttributeRef attributeRef = (AttributeRef) object;
        assertTrue(attributeRef.getId().equals("id"));
        assertTrue(attributeRef.getUse() == AttributeUse.required);
        assertTrue(attributeRef.getFixed().equals("boo"));

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}type").getReference();
        object = complexType.getAttributes().getFirst();
        assertTrue(object instanceof AttributeRef);
        attributeRef = (AttributeRef) object;
        assertTrue(attributeRef.getDefault().equals("boo"));
    }

    @Test
    public void testInvalidNCNameExceptionWithName() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_invalid_qName_name.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("The name attribute of a attribute element is an invalid NCName, but it wasn't detected.");
    }

    @Test
    public void testInvalidQNameExceptionWithType() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_invalid_qName_type.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("The type attribute of a attribute element is an invalid QName, but it wasn't detected.");
    }

    @Test
    public void testInvalidQNameExceptionWithRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_invalid_qName_ref.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("The ref attribute of a attribute element is an invalid QName, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdExceptionWithAttribute() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a attribute element is empty, but it wasn't detected.");
    }

    @Test
    public void testInvalidFormValueLocationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_invalid_form_value_location.xsd"));
        } catch (InvalidFormValueLocationException error) {
            return;
        }
        fail("The form attribute location of an attribute is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidFormValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_invalid_form_value.xsd"));
        } catch (InvalidFormValueException error) {
            return;
        }
        fail("The value of a form attribute is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidUseValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_invalid_use_value.xsd"));
        } catch (InvalidUseValueException error) {
            return;
        }
        fail("The value of a use attribute is invalid, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithFixedAndDefault() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_exclusive_attributes_fixed_default.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("The attribute contains both fixed and default attributes, but it wasn't detected.");
    }

    @Test
    public void testMissingNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_missing_name.xsd"));
        } catch (MissingNameException error) {
            return;
        }
        fail("The attribute contains no name attribute, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithNameAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_exclusive_attributes_name_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("The attribute contains both name and ref attributes, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithTypeAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_exclusive_attributes_type_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("The attribute contains both type and ref attributes, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithFormAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_exclusive_attributes_form_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("The attribute contains both form and ref attributes, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdExceptionWithAttributeRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_empty_id_attributeRef.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a attribute element with ref attribute is empty, but it wasn't detected.");
    }

    @Test
    public void testExclusiveContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_exclusive_content.xsd"));
        } catch (ExclusiveContentException error) {
            return;
        }
        fail("An attribute contains both simpleType and ref attribute, but it wasn't detected.");
    }

    @Test
    public void testMultipleTypesException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_multiple_types.xsd"));
        } catch (MultipleTypesException error) {
            return;
        }
        fail("An attribute contains two types, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("An attribute contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/attributeTests/attribute_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("An attribute contains unsupported content, but it wasn't detected.");
    }
}