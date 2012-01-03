package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.ComplexContentRestriction;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.UnsupportedContentException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.WrongComplexContentMixedValueException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance.ComplexContentMultipleInheritanceException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance.MissingComplexContentInheritanceException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ComplexContentProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexContentProcessorTest extends junit.framework.TestCase{

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
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType RestrictedProductType
        if (!(schema.getTypes().getFirst() instanceof ComplexType)) {
            fail("Type RestrictedProductType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().get(2);
        if (!(complexType.getContent() instanceof ComplexContentType)) {
            fail("Content of ComplexType:RestrictedProductType is not ComplexContentType");
        }
        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        if (!(complexContentType.getInheritance() instanceof ComplexContentRestriction)) {
            fail("Inheritance of ComplexContent of ComplexType:RestrictedProductType is not ComplexContentRestriction");
        }

        assertTrue(complexContentType.getMixed());
        
        ComplexContentRestriction restriction = (ComplexContentRestriction) complexContentType.getInheritance();

        if (restriction.getAttributes().isEmpty()) {
            fail("There is no attribute defined under restriction");
        }

        Attribute attribute = (Attribute) restriction.getAttributes().getFirst();
        assertEquals("{http://www.example.org}routingNum", attribute.getName());

        assertEquals("RestrictedProductTypeRestriction", restriction.getId());

        if (complexContentType.getParticle() == null) {
            fail("There is no Particle in the ComplexContentType");
        }

        assertTrue(complexContentType.getParticle() instanceof SequencePattern);
        // Particles are testet in the corresponding ProcessorTests (SequenceProcessorTest, ChoiceProcessorTest, ...).

    }

    @Test
    public void testWrongComplexContentMixedValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_invalid_mixed.xsd"));
        } catch (WrongComplexContentMixedValueException error) {
            return;
        }
        fail("The value of the attribute \"mixed\" is invalid, but it was not detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("ID is empty, but it was not detected.");
    }

    @Test
    public void testMissingComplexContentInheritanceException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_no_inherit.xsd"));
        } catch (MissingComplexContentInheritanceException error) {
            return;
        }
        fail("There is no Inheritance given, but this was not detected.");
    }

    @Test
    public void testComplexContentMultipleInheritanceExceptionWithRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_multi_inherit_restr.xsd"));
        } catch (ComplexContentMultipleInheritanceException error) {
            return;
        }
        fail("There is more than one Inheritance given, but this was not detected.");
    }

    @Test
    public void testComplexContentMultipleInheritanceExceptionWithExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_multi_inherit_ext.xsd"));
        } catch (ComplexContentMultipleInheritanceException error) {
            return;
        }
        fail("There is more than one Inheritance given, but this was not detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of ComplexContent, but it was not detected.");
    }

}