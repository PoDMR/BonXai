package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.ElementRef;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.type.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class ElementProcessorTest extends junit.framework.TestCase{

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
    public void testValidCaseWithElement() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_valid_element.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        Element comment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}comment").getReference();
        assertTrue(comment.getAbstract());
        assertTrue(comment.getTypeAttr());
        assertTrue(comment.getType() instanceof SimpleType && comment.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));

        Element shipComment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipComment").getReference();
        assertTrue(shipComment.getFixed().equals("bod"));
        assertTrue(shipComment.getFinalModifiers().contains(Element.Final.extension));
        assertTrue(shipComment.getFinalModifiers().contains(Element.Final.restriction));
        assertTrue(shipComment.getTypeAttr());
        assertTrue(shipComment.getType() instanceof SimpleType && shipComment.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));

        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);


        Element order = (Element)((SequencePattern)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle()).getParticles().getFirst();
        assertTrue(order.getId().equals("order"));
        assertTrue(order.isNillable() == false);
        assertTrue(order.getForm() == XSDSchema.Qualification.unqualified);
        assertTrue(order.getBlockModifiers().contains(Element.Block.extension));
        assertTrue(order.getBlockModifiers().contains(Element.Block.restriction));
        assertTrue(order.getBlockModifiers().contains(Element.Block.substitution));
        assertTrue(order.getDefault().equals("bob"));
        assertTrue(order.getTypeAttr());
        assertTrue(order.getType() instanceof SimpleType && order.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));
    }

    @Test
    public void testValidCaseWithElementRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_valid_elementRef.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        Element comment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}comment").getReference();
        assertTrue(comment.getTypeAttr());
        assertTrue(comment.getType() instanceof SimpleType && comment.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));

        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);


        Object order = (Object)((SequencePattern)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle()).getParticles().getFirst();
        assertTrue(order instanceof ElementRef);
        assertTrue(((ElementRef)order).getId().equals("order"));
    }

    @Test
    public void testValidCaseWithElementAndCountingPattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_valid_element_countingPattern.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }


        Element comment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}comment").getReference();
        assertTrue(comment.getAbstract());
        assertTrue(comment.getTypeAttr());
        assertTrue(comment.getType() instanceof SimpleType && comment.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));

        Element shipComment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipComment").getReference();
        assertTrue(shipComment.getFixed().equals("bod"));
        assertTrue(shipComment.getFinalModifiers().contains(Element.Final.extension));
        assertTrue(shipComment.getFinalModifiers().contains(Element.Final.restriction));
        assertTrue(shipComment.getTypeAttr());
        assertTrue(shipComment.getType() instanceof SimpleType && shipComment.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));

        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);


        CountingPattern countingPattern = (CountingPattern)((SequencePattern)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle()).getParticles().getFirst();
        assertTrue(countingPattern.getMin() == 10);
        assertTrue(countingPattern.getMax() == 10);
        Element order = (Element) countingPattern.getParticles().getFirst();
        assertTrue(order.getId().equals("order"));
        assertTrue(order.isNillable() == false);
        assertTrue(order.getForm() == XSDSchema.Qualification.unqualified);
        assertTrue(order.getBlockModifiers().contains(Element.Block.extension));
        assertTrue(order.getBlockModifiers().contains(Element.Block.restriction));
        assertTrue(order.getBlockModifiers().contains(Element.Block.substitution));
        assertTrue(order.getDefault().equals("bob"));
        assertTrue(order.getTypeAttr());
        assertTrue(order.getType() instanceof SimpleType && order.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));
    }

    @Test
    public void testValidCaseWithElementRefAndCountingPattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_valid_elementRef_countingPattern.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        Element comment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}comment").getReference();
        assertTrue(comment.getTypeAttr());
        assertTrue(comment.getType() instanceof SimpleType && comment.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}string"));

        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);

        CountingPattern countingPattern = (CountingPattern)((SequencePattern)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle()).getParticles().getFirst();
        assertTrue(countingPattern.getMin() == 10);
        assertTrue(countingPattern.getMax() == 10);
        Object order = (Object) countingPattern.getParticles().getFirst();
        assertTrue(order instanceof ElementRef);
        assertTrue(((ElementRef)order).getId().equals("order"));
    }

    @Test
    public void testEmptyIdExceptionWithElement() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_empty_id_element.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of an element is empty, but it wasn't detected.");
    }

    @Test
    public void testInvalidNillableException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_nillable.xsd"));
        } catch (InvalidNillableException error) {
            return;
        }
        fail("The nillable attribute of an element is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidAbstractException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_abstract.xsd"));
        } catch (InvalidAbstractException error) {
            return;
        }
        fail("The abstract attribute of an element is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidFormValueLocationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_form_location.xsd"));
        } catch (InvalidFormValueLocationException error) {
            return;
        }
        fail("The form attribute location of an element is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidFormValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_form.xsd"));
        } catch (InvalidFormValueException error) {
            return;
        }
        fail("The form attribute of an element is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidFinalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_final.xsd"));
        } catch (InvalidFinalValueException error) {
            return;
        }
        fail("The final attribute of an element is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidBlockValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_block.xsd"));
        } catch (InvalidBlockValueException error) {
            return;
        }
        fail("The block attribute of an element is invalid, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithFixedAndDefault() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_fixed_default.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains fixed and default attributes, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMaxOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_maxOccurs_illegal_value.xsd"));
        } catch (CountingPatternMaxOccursIllegalValueException error) {
            return;
        }
        fail("The value of maxOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueExceptionWithUnbound() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_minOccurs_illegal_value_unbound.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("Value of minOccurs is unbound, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_minOccurs_illegal_value.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("The value of minOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursGreaterThanMaxOccursException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_minOccurs_greater_than_maxOccurs.xsd"));
        } catch (CountingPatternMinOccursGreaterThanMaxOccursException error) {
            return;
        }
        fail("MinOccurs value is greater than maxOccurs value, but it wasn't detected.");
    }

    @Test
    public void testNonGlobalSubstitutionGroupException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_non_global_substitutionGroup.xsd"));
        } catch (NonGlobalSubstitutionGroupException error) {
            return;
        }
        fail("An element contains a substitutionGroup an is not global, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithSubstitutionGroupAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_substitutionGroup_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a substitutionGroup and a ref, but it wasn't detected.");
    }

    @Test
    public void testInvalidQNameExceptionWithSubstitutionGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_qName_substitutionGroup.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("An element contains a substitutionGroup attribute which is no QName, but it wasn't detected.");
    }

    @Test
    public void testMissingNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_missing_name.xsd"));
        } catch (MissingNameException error) {
            return;
        }
        fail("An element has no name, but it wasn't detected.");
    }

    @Test
    public void testInvalidQNameExceptionWithType() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_qName_type.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("An element contains a type attribute which is no QName, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithNameAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_name_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a name and a ref, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithTypeAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_type_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a type and a ref, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWitNillableAndRefh() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_nillable_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a nillable and a ref, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithDefaultAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_default_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a default and a ref, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithFixedAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_fixed_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a fixed and a ref, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithFormAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_form_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a form and a ref, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesExceptionWithBlockAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_attributes_block_ref.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("An element contains a block and a ref, but it wasn't detected.");
    }


    @Test
    public void testInvalidQNameExceptionWithRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_qName_ref.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("An element contains a ref attribute which is no QName, but it wasn't detected.");
    }

    @Test
    public void testMultipleTypesExceptionWithComplexType() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_multiple_types_complexType.xsd"));
        } catch (MultipleTypesException error) {
            return;
        }
        fail("An element contains multiple complexTypes, but it wasn't detected.");
    }

    @Test
    public void testMultipleTypesExceptionWithSimpleType() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_multiple_types_simpleType.xsd"));
        } catch (MultipleTypesException error) {
            return;
        }
        fail("An element contains multiple types, but it wasn't detected.");
    }

    @Test
    public void testExclusiveContentExceptionWithUniqueAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_content_unique_ref.xsd"));
        } catch (ExclusiveContentException error) {
            return;
        }
        fail("An element contains unique element and ref attribute, but it wasn't detected.");
    }

    @Test
    public void testExclusiveContentExceptionWithKeyAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_content_key_ref.xsd"));
        } catch (ExclusiveContentException error) {
            return;
        }
        fail("An element contains key element and ref attribute, but it wasn't detected.");
    }

    @Test
    public void testExclusiveContentExceptionWithKeyRefAndRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_exclusive_content_keyRef_ref.xsd"));
        } catch (ExclusiveContentException error) {
            return;
        }
        fail("An element contains keyRef element and ref attribute, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("An element contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("An element contains unsupported content, but it wasn't detected.");
    }

    @Test
    public void testInvalidFinalLocationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/elementTests/element_invalid_final_location.xsd"));
        } catch (InvalidFinalLocationException error) {
            return;
        }
        fail("A non global element contains a final attribute, but it wasn't detected.");
    }
}