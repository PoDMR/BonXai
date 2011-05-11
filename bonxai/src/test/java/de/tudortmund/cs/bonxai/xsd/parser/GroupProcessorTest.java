package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
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
public class GroupProcessorTest extends junit.framework.TestCase{

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
    public void testValidCaseWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_valid_group.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        Group group = schema.getGroupSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}first").getReference();
        assertTrue(group.getId().equals("first"));
        assertTrue(group.getName().equals("{http://www.w3.org/2001/XMLSchema}first"));
        assertTrue(group.getParticleContainer() instanceof AllPattern);

        group = schema.getGroupSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}second").getReference();
        assertTrue(group.getId().equals("second"));
        assertTrue(group.getName().equals("{http://www.w3.org/2001/XMLSchema}second"));
        assertTrue(group.getParticleContainer() instanceof ChoicePattern);

        group = schema.getGroupSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}third").getReference();
        assertTrue(group.getId().equals("third"));
        assertTrue(group.getName().equals("{http://www.w3.org/2001/XMLSchema}third"));
        assertTrue(group.getParticleContainer() instanceof SequencePattern);
    }

    @Test
    public void testValidCaseWithGroupRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_valid_groupRef.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        ComplexContentType complexContentType = (ComplexContentType)((ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}root").getReference()).getContent();
        assertTrue(complexContentType.getParticle() instanceof GroupRef);
        assertTrue(((GroupRef)complexContentType.getParticle()).getId().equals("firstRef"));
        assertTrue(((GroupRef)complexContentType.getParticle()).getGroup().getName().equals("{http://www.w3.org/2001/XMLSchema}first"));
        assertTrue(((GroupRef)complexContentType.getParticle()).getGroup().getParticleContainer() instanceof AllPattern);
    }

    @Test
    public void testValidCaseWithCountingPattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_valid_countingPattern.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        ComplexContentType complexContentType = (ComplexContentType)((ComplexType) schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}root").getReference()).getContent();
        assertTrue(complexContentType.getParticle() instanceof CountingPattern);
        CountingPattern countingPattern = (CountingPattern) complexContentType.getParticle();
        assertTrue(countingPattern.getMin() == 10);
        assertTrue(countingPattern.getMax() == 20);
        assertTrue(countingPattern.getParticles().getFirst() instanceof GroupRef);
        GroupRef groupRef = (GroupRef) countingPattern.getParticles().getFirst();
        assertTrue(groupRef.getId().equals("firstRef"));
        assertTrue(groupRef.getGroup().getName().equals("{http://www.w3.org/2001/XMLSchema}first"));
        assertTrue(groupRef.getGroup().getParticleContainer() instanceof AllPattern);
    }

    @Test
    public void testInvalidQNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_invalid_qName.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("A group contains a ref attribute with invalid QName, but it wasn't detected.");
    }

    @Test
    public void testExclusiveAttributesException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_exclusive_attributes.xsd"));
        } catch (ExclusiveAttributesException error) {
            return;
        }
        fail("Group contains both name and ref attributes, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdExceptionWithGroupRef() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_empty_id_groupRef.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a groupRef element is empty, but it wasn't detected.");
    }


    @Test
    public void testCountingPatternMaxOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_maxOccurs_illegal_value.xsd"));
        } catch (CountingPatternMaxOccursIllegalValueException error) {
            return;
        }
        fail("The value of maxOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueExceptionWithUnbound() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_minOccurs_illegal_value_unbound.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("Value of minOccurs is unbound, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_minOccurs_illegal_value.xsd"));
        } catch (CountingPatternMinOccursIllegalValueException error) {
            return;
        }
        fail("The value of minOccurs is illegal, but it wasn't detected.");
    }

    @Test
    public void testCountingPatternMinOccursGreaterThanMaxOccursException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_minOccurs_greater_than_maxOccurs.xsd"));
        } catch (CountingPatternMinOccursGreaterThanMaxOccursException error) {
            return;
        }
        fail("MinOccurs value is greater than maxOccurs value, but it wasn't detected.");
    }

    @Test
    public void testInvalidNCNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_invalid_nCName.xsd"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("A group contains a name attribute with invalid NCName, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_empty_id_group.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of a group element is empty, but it wasn't detected.");
    }

    @Test
    public void testGroupMultipleParticleContainerExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_multiple_particleContainer_all.xsd"));
        } catch (GroupMultipleParticleContainerException error) {
            return;
        }
        fail("A group contains multiple all elements, but it wasn't detected.");
    }

    @Test
    public void testGroupMultipleParticleContainerExceptionChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_multiple_particleContainer_choice.xsd"));
        } catch (GroupMultipleParticleContainerException error) {
            return;
        }
        fail("A group contains multiple choice elements, but it wasn't detected.");
    }

    @Test
    public void testGroupMultipleParticleContainerExceptionSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_multiple_particleContainer_sequence.xsd"));
        } catch (GroupMultipleParticleContainerException error) {
            return;
        }
        fail("A group contains multiple sequence elements, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_multiple_annotation.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("A group contains multiple annotations, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/groupTests/group_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("A group contains unsupported content, but it wasn't detected.");
    }


}