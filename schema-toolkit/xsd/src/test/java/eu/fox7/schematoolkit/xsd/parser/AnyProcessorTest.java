package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.ComplexContentExtension;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.InvalidNamespaceValueException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.InvalidProcessContentsValueException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.countingpattern.CountingPatternMaxOccursIllegalValueException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.countingpattern.CountingPatternMinOccursGreaterThanMaxOccursException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.UnsupportedContentException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;

/**
 * Test of class AnyProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class AnyProcessorTest extends junit.framework.TestCase {

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_valid_countingpattern.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType ProductType
        if (!(schema.getTypes().get(1) instanceof ComplexType)) {
            fail("Type ProductType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().get(1);
        if (!(complexType.getContent() instanceof ComplexContentType)) {
            fail("Content of ComplexType:ProductType is not ComplexContentType");
        }
        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

        if (!(complexContentType.getParticle() instanceof SequencePattern)) {
            fail("Particle of Content from ProductType is not SequencePattern");
        }

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        if (!(sequencePattern.getParticles().getFirst() instanceof CountingPattern)) {
            fail("Particle of SequencePattern is not CountingPattern");
        }

        CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();

        if (!(countingPattern.getParticles().getFirst() instanceof AnyPattern)) {
            fail("Particle of CountingPattern is not AnyPattern");
        }

        assertEquals("2", countingPattern.getMin().toString());
        assertEquals("4", countingPattern.getMax().toString());

        AnyPattern any = (AnyPattern) countingPattern.getParticles().getFirst();

        if (any.getAnnotation() == null
                || any.getAnnotation().getDocumentations() == null
                || any.getAnnotation().getDocumentations().getFirst() == null
                || any.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || any.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(any.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("This is an annotation."));

        assertEquals("anyOne", any.getId());
        assertEquals("##other", any.getNamespace());
        assertEquals(ProcessContentsInstruction.STRICT, any.getProcessContentsInstruction());
    }

    @Test
    public void testValidCaseWithAnyPattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType ProductType
        if (!(schema.getTypes().get(1) instanceof ComplexType)) {
            fail("Type ProductType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().get(1);
        if (!(complexType.getContent() instanceof ComplexContentType)) {
            fail("Content of ComplexType:ProductType is not ComplexContentType");
        }
        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

        if (!(complexContentType.getParticle() instanceof SequencePattern)) {
            fail("Particle of Content from ProductType is not SequencePattern");
        }

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        if (!(sequencePattern.getParticles().getFirst() instanceof AnyPattern)) {
            fail("Particle of SequencePattern is not CountingPattern");
        }

        AnyPattern any = (AnyPattern) sequencePattern.getParticles().getFirst();

        if (any.getAnnotation() == null
                || any.getAnnotation().getDocumentations() == null
                || any.getAnnotation().getDocumentations().getFirst() == null
                || any.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || any.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(any.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("This is an annotation."));

        assertEquals("anyOne", any.getId());
        assertEquals("##other", any.getNamespace());
        assertEquals(ProcessContentsInstruction.STRICT, any.getProcessContentsInstruction());
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testCountingPatternMaxOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_invalid_countingpattern_max.xsd"));
        } catch (CountingPatternMaxOccursIllegalValueException error) {
            return;
        }
        fail("Illegal Value of maxOccurs in any, but it was not detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueExceptionWithUnbound() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_invalid_countingpattern_min_unbounded.xsd"));
        } catch (CountingPatternMaxOccursIllegalValueException error) {
            return;
        }
        fail("Illegal Value of minOccurs (unbounded) in any, but it was not detected.");
    }

    @Test
    public void testCountingPatternMinOccursIllegalValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_invalid_countingpattern_min_unbounded.xsd"));
        } catch (CountingPatternMaxOccursIllegalValueException error) {
            return;
        }
        fail("Illegal Value of minOccurs (negative) in any, but it was not detected.");
    }

    @Test
    public void testCountingPatternMinOccursGreaterThanMaxOccursException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_invalid_countingpattern_min_max.xsd"));
        } catch (CountingPatternMinOccursGreaterThanMaxOccursException error) {
            return;
        }
        fail("Illegal Value of minOccurs greater than maxOccurs in any, but it was not detected.");
    }

    @Test
    public void testInvalidProcessContentsValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_invalid_pc.xsd"));
        } catch (InvalidProcessContentsValueException error) {
            return;
        }
        fail("Invalid value for ProcessContents-instruction, but it was not detected.");
    }

    @Test
    public void testInvalidNamespaceValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_invalid_ns.xsd"));
        } catch (InvalidNamespaceValueException error) {
            return;
        }
        fail("Invalid value for namespace in any, but it was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/anyTests/any_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of any, but it was not detected.");
    }
}
