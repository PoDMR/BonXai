package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;
import eu.fox7.bonxai.xsd.parser.exceptions.type.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexTypeProcessorTest  extends junit.framework.TestCase {

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        ComplexType complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}complex").getReference();
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(complexType.getId().equals("id"));
        assertTrue(complexType.getMixed());
        assertTrue(complexType.isAbstract());
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction));
        assertTrue(complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Restriction));

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}simple").getReference();
        assertTrue(complexType.getContent() instanceof SimpleContentType);

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}mixed").getReference();
        assertTrue(complexType.getAttributes().get(0) instanceof Attribute);
        assertTrue(complexType.getAttributes().get(1) instanceof AttributeGroupRef);
        assertTrue(complexType.getAttributes().get(2) instanceof AnyAttribute);
        assertTrue(complexType.isAbstract().equals(false));
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction));
        assertTrue(complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Restriction));

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}all").getReference();
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        Particle particle = ((ComplexContentType) complexType.getContent()).getParticle();
        assertTrue(particle instanceof AllPattern);

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}sequence").getReference();
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        particle = ((ComplexContentType) complexType.getContent()).getParticle();
        assertTrue(particle instanceof SequencePattern);

        complexType = (ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}choice").getReference();
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        particle = ((ComplexContentType) complexType.getContent()).getParticle();
        assertTrue(particle instanceof ChoicePattern);
    }

    @Test
    public void testInvalidNCNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/attribute_invalid_nCName.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("The name attribute of a attribute element is an invalid NCName, but it wasn't detected.");
    }

    @Test
    public void testInvalidMixedValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_invalid_mixed_value.xsd"));
        } catch (InvalidMixedValueException error) {
            return;
        }
        fail("The mixed attribute value of complexType is invalide, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of complexType is empty, but it wasn't detected.");
    }

    @Test
    public void testInvalidFinalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_invalid_final_value.xsd"));
        } catch (InvalidFinalValueException error) {
            return;
        }
        fail("The final attribute value of complexType is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidBlockValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_invalid_block_value.xsd"));
        } catch (InvalidBlockValueException error) {
            return;
        }
        fail("The block attribute value of complexType is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidAbstractException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_invalid_abstract_value.xsd"));
        } catch (InvalidAbstractException error) {
            return;
        }
        fail("The abstract attribute value of complexType is invalid, but it wasn't detected.");
    }

    @Test
    public void testComplexTypeMultipleContentExceptionWithSimpleContent() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_multiple_content_simpleContent.xsd"));
        } catch (ComplexTypeMultipleContentException error) {
            return;
        }
        fail("There are multiple simpleContent-childs in a ComplexType , but it wasn't detected.");
    }

    @Test
    public void testComplexTypeMultipleContentExceptionWithComplexContent() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_multiple_content_complexContent.xsd"));
        } catch (ComplexTypeMultipleContentException error) {
            return;
        }
        fail("There are multiple complexContent-childs in a ComplexType , but it wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_attribute_is_not_last_in_contentModel_group.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is an group in ComplexType with follows an attribute,attributeGroup or anyAttribute, but it wasn't detected.");
    }

    @Test
    public void testComplexTypeMultipleContentExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_multiple_content_group.xsd"));
        } catch (ComplexTypeMultipleContentException error) {
            return;
        }
        fail("There are multiple group-childs in a ComplexType , but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_illegalObject_returned_group.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("There is a group in ComplexType with no ref attribute, but it wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_attribute_is_not_last_in_contentModel_all.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is an all in ComplexType with follows an attribute,attributeGroup or anyAttribute, but it wasn't detected.");
    }

    @Test
    public void testComplexTypeMultipleContentExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_multiple_content_all.xsd"));
        } catch (ComplexTypeMultipleContentException error) {
            return;
        }
        fail("There are multiple all-childs in a ComplexType , but it wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_attribute_is_not_last_in_contentModel_choice.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is an choice in ComplexType with follows an attribute,attributeGroup or anyAttribute, but it wasn't detected.");
    }

    @Test
    public void testComplexTypeMultipleContentExceptionWithChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_multiple_content_choice.xsd"));
        } catch (ComplexTypeMultipleContentException error) {
            return;
        }
        fail("There are multiple choice-childs in a ComplexType , but it wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_attribute_is_not_last_in_contentModel_sequence.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is an sequence in ComplexType with follows an attribute,attributeGroup or anyAttribute, but it wasn't detected.");
    }

    @Test
    public void testComplexTypeMultipleContentExceptionWithSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_multiple_content_sequence.xsd"));
        } catch (ComplexTypeMultipleContentException error) {
            return;
        }
        fail("There are multiple sequence-childs in a ComplexType , but it wasn't detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttribute() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_anyAttribute_is_not_last_attribute.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("There is an attribute in ComplexType with follows an anyAttribute, but it wasn't detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_anyAttribute_is_not_last_attributeGroup.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("There is an attributeGroup in ComplexType with follows an anyAttribute, but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithAttributeGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_illegalObject_returned_attributeGroup.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("There is an attibuteGroup in ComplexType with no ref attribute, but it wasn't detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAnyAttribute() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_anyAttribute_is_not_last_anyAttribute.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("There is an anyAttribute in ComplexType with follows an anyAttribute, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A complexType contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexTypeTests/complexType_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A complexType contains unsupported content, but it wasn't detected.");
    }

}