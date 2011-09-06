package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class SequenceProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_valid_countingPattern.xsd"));
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
        assertTrue(countingPattern.getParticles().getFirst() instanceof SequencePattern);
        SequencePattern sequencePattern = (SequencePattern) countingPattern.getParticles().getFirst();
        assertTrue(sequencePattern.getId().equals("sequence"));

        assertTrue(sequencePattern.getParticles().get(0) instanceof Element);
        assertTrue(sequencePattern.getParticles().get(1) instanceof GroupRef);
        assertTrue(sequencePattern.getParticles().get(2) instanceof ChoicePattern);
        assertTrue(sequencePattern.getParticles().get(3) instanceof SequencePattern);
    }

    @Test
    public void testValidCaseWithSequencePattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_valid_sequence.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);
        Object object = (Object)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle();
        assertTrue(object instanceof SequencePattern);
        SequencePattern sequencePattern = (SequencePattern) object;
        assertTrue(sequencePattern.getId().equals("sequence"));

        assertTrue(sequencePattern.getParticles().get(0) instanceof Element);
        assertTrue(sequencePattern.getParticles().get(1) instanceof GroupRef);
        assertTrue(sequencePattern.getParticles().get(2) instanceof ChoicePattern);
        assertTrue(sequencePattern.getParticles().get(3) instanceof SequencePattern);
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a sequence element is empty, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMaxOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_maxOccurs_illegal_value.xsd"));
        } catch (CountingPatternMaxOccursIllegalValueException error) {
            return;
        }
        fail("The value of maxOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueExceptionWithUnbound() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_minOccurs_illegal_value_unbound.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("Value of minOccurs is unbound, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_minOccurs_illegal_value.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("The value of minOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursGreaterThanMaxOccursException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_minOccurs_greater_than_maxOccurs.xsd"));
        } catch (CountingPatternMinOccursGreaterThanMaxOccursException error) {
            return;
        }
        fail("MinOccurs value is greater than maxOccurs value, but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_illegal_object_returned.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("A group in a sequence element has no ref attribute, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A sequence contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/sequenceTests/sequence_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A sequence contains unsupported content, but it wasn't detected.");
    }
}