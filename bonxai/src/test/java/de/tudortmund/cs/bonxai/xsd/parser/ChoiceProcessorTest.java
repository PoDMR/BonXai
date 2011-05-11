package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class ChoiceProcessorTest extends junit.framework.TestCase{

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
    public void testValidCaseWithCountingPattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_valid_countingPattern.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);
        Object object = (Object)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle();
        assertTrue(object instanceof CountingPattern);
        CountingPattern countingPattern = (CountingPattern) object;
        assertTrue(countingPattern.getMin() == 2);
        assertTrue(countingPattern.getMax() == 4);
        assertTrue(countingPattern.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
        assertTrue(choicePattern.getId().equals("choice"));

        assertTrue(choicePattern.getParticles().get(0) instanceof Element);
        assertTrue(choicePattern.getParticles().get(1) instanceof GroupRef);
        assertTrue(choicePattern.getParticles().get(2) instanceof ChoicePattern);
        assertTrue(choicePattern.getParticles().get(3) instanceof SequencePattern);
    }

    @Test
    public void testValidCaseWithChoicePattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_valid_choice.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);
        Object object = (Object)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle();
        assertTrue(object instanceof ChoicePattern);
        ChoicePattern choicePattern = (ChoicePattern) object;
        assertTrue(choicePattern.getId().equals("choice"));
        
        assertTrue(choicePattern.getParticles().get(0) instanceof Element);
        assertTrue(choicePattern.getParticles().get(1) instanceof GroupRef);
        assertTrue(choicePattern.getParticles().get(2) instanceof ChoicePattern);
        assertTrue(choicePattern.getParticles().get(3) instanceof SequencePattern);
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a choice element is empty, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMaxOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_maxOccurs_illegal_value.xsd"));
        } catch (CountingPatternMaxOccursIllegalValueException error) {
            return;
        }
        fail("The value of maxOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueExceptionWithUnbound() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_minOccurs_illegal_value_unbound.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("Value of minOccurs is unbound, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_minOccurs_illegal_value.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("The value of minOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursGreaterThanMaxOccursException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_minOccurs_greater_than_maxOccurs.xsd"));
        } catch (CountingPatternMinOccursGreaterThanMaxOccursException error) {
            return;
        }
        fail("MinOccurs value is greater than maxOccurs value, but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_illegal_object_returned.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("A group in a choice element has no ref attribute, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A choice contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/choiceTests/choice_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A choice contains unsupported content, but it wasn't detected.");
    }
}