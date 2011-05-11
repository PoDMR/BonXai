package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.ComplexContentRestriction;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentList;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import de.tudortmund.cs.bonxai.xsd.SimpleContentType;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.InvalidQNameException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.AnyAttributeIsNotLastException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.AttributeIsNotLastInContentModelException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.IllegalObjectReturnedException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance.EmptyInheritanceBaseException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance.RestrictionMultipleParticleException;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance.RestrictionWrongChildException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class RestrictionProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class RestrictionProcessorTest extends junit.framework.TestCase {

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
     * Test of processNode method for a valid case within a SimpleType
     * @throws Exception
     */
    @Test
    public void testValidCaseSimpleType() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check SimpleType DressSizeTypeOne
        if (!(schema.getTypes().getFirst() instanceof SimpleType)) {
            fail("Type DressSizeTypeOne is not SimpleType");
        }
        SimpleType simpleType = (SimpleType) schema.getTypes().getFirst();
        if (!(simpleType.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleType:DressSizeTypeOne is not SimpleContentRestriction");
        }
        SimpleContentRestriction restriction = (SimpleContentRestriction) simpleType.getInheritance();

        if (restriction.getBase() == null) {
            fail("Base of SimpleContentRestriction is null");
        }
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", restriction.getBase().getName());

        assertEquals("restrictionOne", restriction.getId());

        assertTrue(restriction.getEnumeration().contains("12"));
        assertTrue(restriction.getEnumeration().contains("13"));
        assertTrue(restriction.getEnumeration().contains("14"));

        if (restriction.getPattern() == null) {
            fail("Pattern of SimpleContentRestriction is null");
        }
        assertEquals("\\d{2}", restriction.getPattern().getValue());

        if (restriction.getFractionDigits() == null) {
            fail("FractionDigits of SimpleContentRestriction is null");
        }
        assertEquals("0", restriction.getFractionDigits().getValue().toString());

        assertEquals("14", restriction.getMaxInclusive().getValue());
        assertEquals("12", restriction.getMinInclusive().getValue());

        assertEquals("2", restriction.getTotalDigits().getValue().toString());

        assertEquals("collapse", restriction.getWhitespace().getValue().toString().toLowerCase());

        if (restriction.getAnnotation() == null
                || restriction.getAnnotation().getDocumentations() == null
                || restriction.getAnnotation().getDocumentations().getFirst() == null
                || restriction.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || restriction.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(restriction.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("a restriction"));

        // Check SimpleType DressSizeTypeTwo
        if (!(schema.getTypes().get(1) instanceof SimpleType)) {
            fail("Type DressSizeTypeTwo is not SimpleType");
        }
        SimpleType simpleType2 = (SimpleType) schema.getTypes().get(1);
        if (!(simpleType2.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleType:DressSizeTypeTwo is not SimpleContentRestriction");
        }
        SimpleContentRestriction restriction2 = (SimpleContentRestriction) simpleType2.getInheritance();

        assertEquals("15", restriction2.getMaxExclusive().getValue());
        assertEquals("11", restriction2.getMinExclusive().getValue());

        // Check SimpleType DressSizeTypeThree
        if (!(schema.getTypes().get(2) instanceof SimpleType)) {
            fail("Type DressSizeTypeThree is not SimpleType");
        }
        SimpleType simpleType3 = (SimpleType) schema.getTypes().get(2);
        if (!(simpleType3.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleType:DressSizeTypeThree is not SimpleContentRestriction");
        }
        SimpleContentRestriction restriction3 = (SimpleContentRestriction) simpleType3.getInheritance();

        assertEquals("2", restriction3.getLength().getValue().toString());

        // Check SimpleType DressSizeTypeFour
        if (!(schema.getTypes().get(3) instanceof SimpleType)) {
            fail("Type DressSizeTypeFour of Element is not SimpleType");
        }
        SimpleType simpleType4 = (SimpleType) schema.getTypes().get(3);
        if (!(simpleType4.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleType:DressSizeTypeFour is not SimpleContentRestriction");
        }
        SimpleContentRestriction restriction4 = (SimpleContentRestriction) simpleType4.getInheritance();

        assertEquals("4", restriction4.getMaxLength().getValue().toString());
        assertEquals("2", restriction4.getMinLength().getValue().toString());

        // Check SimpleType restrictedString
        if (!(schema.getTypes().get(4) instanceof SimpleType)) {
            fail("Type restrictedString is not SimpleType");
        }
        SimpleType simpleType5 = (SimpleType) schema.getTypes().get(4);
        if (!(simpleType5.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleType:restrictedString is not SimpleContentRestriction");
        }
        SimpleContentRestriction restriction5 = (SimpleContentRestriction) simpleType5.getInheritance();

        assertTrue(restriction5.getBase() != null);
    }

    /**
     * Test of processNode method for a valid case within a SimpleContent of a ComplexType
     * @throws Exception
     */
    @Test
    public void testValidCaseSimpleContent() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simplecontent_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType SizeType
        if (!(schema.getTypes().get(1) instanceof ComplexType)) {
            fail("Type SizeType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().get(1);
        if (!(complexType.getContent() instanceof SimpleContentType)) {
            fail("Content of ComplexType:SizeType is not SimpleContentType");
        }
        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
        if (!(simpleContentType.getInheritance() instanceof SimpleContentRestriction)) {
            fail("Inheritance of SimpleContent of ComplexType:SizeType is not SimpleContentRestriction");
        }
        SimpleContentRestriction restriction = (SimpleContentRestriction) simpleContentType.getInheritance();

        assertEquals("6", restriction.getMaxInclusive().getValue());
        assertEquals("2", restriction.getMinInclusive().getValue());
        if (restriction.getAttributes().isEmpty()) {
            fail("There is no attribute defined under restriction");
        }

        Attribute attribute = (Attribute) restriction.getAttributes().getFirst();
        assertEquals("{http://www.example.org}system", attribute.getName());
        AnyAttribute anyAttribute = (AnyAttribute) restriction.getAttributes().getLast();
        assertEquals("anyAttributeID", anyAttribute.getId());

        //Notice: The type (as base or within the content) of this restriction is already testet in case: testValidCaseSimpleType
        // This is handled by the same JAVA code.
    }

    /**
     * Test of processNode method for a valid case within a ComplexContent of a ComplexType
     * @throws Exception
     */
    @Test
    public void testValidCaseComplexContent() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_valid.xsd"));
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

        //Notice: The type (as base or within the content) of this restriction is already testet in case: testValidCaseSimpleType
        // This is handled by the same JAVA code.
    }

    /**
     * Test of getParticle method, of class RestrictionProcessor.
     *
     * Method of the Processor itself!
     */
    @Test
    public void testGetParticle() {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_valid.xsd"));
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
        ComplexContentRestriction restriction = (ComplexContentRestriction) complexContentType.getInheritance();

        if (complexContentType.getParticle() == null) {
            fail("There is no Particle in the ComplexContentType");
        }

        assertTrue(complexContentType.getParticle() instanceof SequencePattern);
        // Particles are testet in the corresponding ProcessorTests (SequenceProcessorTest, ChoiceProcessorTest, ...).

        // If method "getParticle" would fail, the complexContentType would have no Particle. This is tested above.

    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_attr_group.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading a group under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_illegal_object.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("There is the declaration of a Group within a ComplexContentRestriction, but this wasn't detected. Only GroupRefs are allowed here.");
    }

    @Test
    public void testRestrictionMultipleParticleExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_multi_group.xsd"));
        } catch (RestrictionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (\"Group\") under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_wrong_child_group.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"Group\" under a SimpleType-SimpleContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_attr_all.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading an All under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testRestrictionMultipleParticleExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_multi_all.xsd"));
        } catch (RestrictionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (\"All\") under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_wrong_child_all.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"All\" under a SimpleType-SimpleContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_attr_choice.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading a choice under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testRestrictionMultipleParticleExceptionWithChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_multi_choice.xsd"));
        } catch (RestrictionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (choices) under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_wrong_child_choice.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"Choice\" under a SimpleType-SimpleContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_attr_sequence.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading a sequence under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testRestrictionMultipleParticleExceptionWithSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_multi_sequence.xsd"));
        } catch (RestrictionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (sequence) under a ComplexContentRestriction, but this wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_wrong_child_sequence.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"Sequence\" under a SimpleType-SimpleContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeAndComplexContentRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_anyattribute_attr_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a ComplexContentRestriction.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeAndSimpleContentRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simplecontent_anyattribute_attr_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a SimpleContentRestriction.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithAttribute() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_wrong_child_attribute.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"Attribute\" under a SimpleType-SimpleContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeGroupAndComplexContentRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_anyattribute_attrgroup_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a ComplexContentRestriction.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeGroupAndSimpleContentRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simplecontent_anyattribute_attrgroup_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a SimpleContentRestriction.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithAttributeGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_wrong_child_attributegroup.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"AttributeGroupRef\" under a SimpleType-SimpleContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAnyAttributeAndComplexContentRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_anyattribute_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a ComplexContentRestriction.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAnyAttributeAndSimpleContentRestriction() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simplecontent_anyattribute_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a SimpleContentRestriction.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithAnyAttribute() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_simpletype_wrong_child.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("AnyAttribute under a SimpleType is invalid, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithSimpleType() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_simpletype.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"SimpleType\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithMinExclusive() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_minexclusive.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"MinExclusive\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithMaxExclusive() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_maxexclusive.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"MaxExclusive\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithMinInclusive() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_mininclusive.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"MinInclusive\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithMaxInclusive() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_maxinclusive.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"MaxInclusive\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithTotalDigits() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_totaldigits.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"TotalDigits\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithFractionDigits() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_fractiondigits.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"FractionDigits\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithLength() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_length.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"Length\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithMinLength() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_minlength.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"MinLength\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithMaxLength() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_maxlength.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"MaxLength\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithEnumeration() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_enumeration.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"enumeration\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithWhiteSpace() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_whitespace.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"whiteSpace\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testRestrictionWrongChildExceptionWithPattern() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_complexcontent_wrong_child_pattern.xsd"));
        } catch (RestrictionWrongChildException error) {
            return;
        }
        fail("There is an invalid child \"pattern\" under a ComplexContentRestriction, but it wasn't detected.");
    }

    @Test
    public void testEmptyInheritanceBaseException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_empty_inheritance_base.xsd"));
        } catch (EmptyInheritanceBaseException error) {
            return;
        }
        fail("There is an empty base in the restriction, but it wasn't detected.");
    }

    @Test
    public void testInvalidQNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_invalid_qname.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("There is an invalid QName as base of the restriction, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("There is an empty ID, but it wasn't detected.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/restrictionTests/restriction_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of restriction, but it was not detected.");
    }
}
