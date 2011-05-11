package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class AllProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_valid_countingPattern.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);
        Object object = (Object)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle();
        assertTrue(object instanceof CountingPattern);
        CountingPattern countingPattern = (CountingPattern) object;
        assertTrue(countingPattern.getMin() == 0);
        assertTrue(countingPattern.getMax() == 1);
        assertTrue(countingPattern.getParticles().getFirst() instanceof AllPattern);
        AllPattern allPattern = (AllPattern) countingPattern.getParticles().getFirst();
        assertTrue(allPattern.getId().equals("all"));
        assertTrue(allPattern.getParticles().get(0) instanceof Element);
    }

    @Test
    public void testValidCaseWithAllPattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_valid_all.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Element shipment = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}shipment").getReference();
        assertTrue(shipment.getType() instanceof ComplexType);
        Object object = (Object)((ComplexContentType)((ComplexType)shipment.getType()).getContent()).getParticle();
        assertTrue(object instanceof AllPattern);
        AllPattern allPattern = (AllPattern) object;
        assertTrue(allPattern.getId().equals("all"));
        assertTrue(allPattern.getParticles().get(0) instanceof Element);
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a all element is empty, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMaxOccursNotAllowedValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_maxOccurs_not_allowed_value.xsd"));
        } catch (CountingPatternMaxOccursNotAllowedValueException error) {
            return;
        }
        fail("The value of maxOccurs is not allowed, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_minOccurs_illegal_value.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("The value of minOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testSameElementUnderAllException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_same_element_under all.xsd"));
        } catch (SameElementUnderAllException error) {
            return;
        }
        fail("The all element contains elements which same name and type, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternNotAllowedValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_countingPattern_not_allowed_value.xsd"));
        } catch (CountingPatternNotAllowedValueException error) {
            return;
        }
        fail("The all element contains an elements which has not allowed minOccurs and/or maxOccurs, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A all contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/allTests/all_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A all contains unsupported content, but it wasn't detected.");
    }

    @Test
    public void testGetElementFromParticle() throws Exception {
        AllProcessor allProcessor = new AllProcessor(schema);
        Element element = new Element("namespace", "name");
        schema.getElementSymbolTable().updateOrCreateReference(element.getName(), element);
        Object object = allProcessor.getElementFromParticle(element);
        assertTrue(object instanceof Element);
        assertEquals(object, element);


        ElementRef elementRef = new ElementRef(schema.getElementSymbolTable().getReference(element.getName()));
        object = allProcessor.getElementFromParticle(elementRef);
        assertTrue(object instanceof Element);
        assertEquals(object, element);

        CountingPattern countingPattern = new CountingPattern(1, 1);
        countingPattern.addParticle(element);
        object = allProcessor.getElementFromParticle(countingPattern);
        assertTrue(object instanceof Element);
        assertEquals(object, element);

        countingPattern = new CountingPattern(1, 1);
        countingPattern.addParticle(elementRef);
        object = allProcessor.getElementFromParticle(countingPattern);
        assertTrue(object instanceof Element);
        assertEquals(object, element);
    }

    @Test
    public void testCheckParticlesForSameElement() throws Exception {
        AllProcessor allProcessor = new AllProcessor(schema);
        Element element = new Element("namespace", "name");
        Element secondElement = new Element("namespace", "name");
        String result = allProcessor.checkParticlesForSameElement(element, element);
        assertTrue(result.equals(element.getName()));

        result = allProcessor.checkParticlesForSameElement(null, element);
        assertTrue(result.equals(""));
    }

}