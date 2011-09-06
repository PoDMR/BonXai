package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.xsd.Attribute;
import eu.fox7.bonxai.xsd.ComplexContentExtension;
import eu.fox7.bonxai.xsd.ComplexContentType;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.SimpleContentExtension;
import eu.fox7.bonxai.xsd.SimpleContentType;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.InvalidQNameException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.AnyAttributeIsNotLastException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.AttributeIsNotLastInContentModelException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.IllegalObjectReturnedException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.MultipleAnnotationException;
import eu.fox7.bonxai.xsd.parser.exceptions.content.UnsupportedContentException;
import eu.fox7.bonxai.xsd.parser.exceptions.inheritance.EmptyInheritanceBaseException;
import eu.fox7.bonxai.xsd.parser.exceptions.inheritance.ExtensionMultipleParticleException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ExtensionProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class ExtensionProcessorTest extends junit.framework.TestCase {

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
     * Test of processNode method for a valid case with ComplexContent
     * @throws Exception
     */
    @Test
    public void testValidCaseWithComplexContent() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType ProductType
        if (!(schema.getTypes().get(1) instanceof ComplexType)) {
            fail("Type SizeType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().get(1);
        if (!(complexType.getContent() instanceof ComplexContentType)) {
            fail("Content of ComplexType:ProductType is not SimpleContentType");
        }
        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        if (!(complexContentType.getInheritance() instanceof ComplexContentExtension)) {
            fail("Inheritance of ComplexContent of ComplexType:ProductType is not ComplexContentExtension");
        }
        ComplexContentExtension extension = (ComplexContentExtension) complexContentType.getInheritance();

        if (extension.getAttributes().isEmpty()) {
            fail("There is no attribute defined under extension");
        }

        Attribute attribute = (Attribute) extension.getAttributes().getFirst();
        assertEquals("{http://www.example.org}effDate", attribute.getName());
        Attribute secondAttribute = (Attribute) extension.getAttributes().getLast();
        assertEquals("{http://www.example.org}lang", secondAttribute.getName());

        if (extension.getAnnotation() == null
                || extension.getAnnotation().getDocumentations() == null
                || extension.getAnnotation().getDocumentations().getFirst() == null
                || extension.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || extension.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(extension.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("This is an annotation of extensionOne."));

        assertEquals("{http://www.example.org}ItemType", extension.getBase().getName());
        assertEquals("extensionOne", extension.getId());

        if (complexContentType.getParticle() == null) {
            fail("Particle of ComplexContent is null");
        }

        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(3, sequencePattern.getParticles().size());
    }

    /**
     * Test of processNode method for a valid case with SimpleContent
     * @throws Exception
     */
    @Test
    public void testValidCaseWithSimpleContent() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_simplecontent_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType SizeType
        if (!(schema.getTypes().getFirst() instanceof ComplexType)) {
            fail("Type SizeType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().getFirst();
        if (!(complexType.getContent() instanceof SimpleContentType)) {
            fail("Content of ComplexType:SizeType is not SimpleContentType");
        }
        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
        if (!(simpleContentType.getInheritance() instanceof SimpleContentExtension)) {
            fail("Inheritance of SimpleContent of ComplexType:SizeType is not SimpleContentExtension");
        }
        SimpleContentExtension extension = (SimpleContentExtension) simpleContentType.getInheritance();

        if (extension.getAttributes().isEmpty()) {
            fail("There is no attribute defined under extension");
        }

        Attribute attribute = (Attribute) extension.getAttributes().getFirst();
        assertEquals("{http://www.example.org}system", attribute.getName());
        AnyAttribute anyAttribute = (AnyAttribute) extension.getAttributes().getLast();
        assertEquals("anyAttributeID", anyAttribute.getId());

        if (extension.getAnnotation() == null
                || extension.getAnnotation().getDocumentations() == null
                || extension.getAnnotation().getDocumentations().getFirst() == null
                || extension.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || extension.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(extension.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("This is an annotation of extensionOne."));

        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", extension.getBase().getName());
        assertEquals("extensionOne", extension.getId());
    }

    /**
     * Test of getParticle method, of class ExtensionProcessor.
     */
    @Test
    public void testGetParticle() {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        // Check ComplexType ProductType
        if (!(schema.getTypes().get(1) instanceof ComplexType)) {
            fail("Type SizeType is not ComplexType");
        }
        ComplexType complexType = (ComplexType) schema.getTypes().get(1);
        if (!(complexType.getContent() instanceof ComplexContentType)) {
            fail("Content of ComplexType:ProductType is not SimpleContentType");
        }
        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        if (!(complexContentType.getInheritance() instanceof ComplexContentExtension)) {
            fail("Inheritance of ComplexContent of ComplexType:ProductType is not ComplexContentExtension");
        }
        ComplexContentExtension extension = (ComplexContentExtension) complexContentType.getInheritance();

        if (extension.getAttributes().isEmpty()) {
            fail("There is no attribute defined under extension");
        }

        Attribute attribute = (Attribute) extension.getAttributes().getFirst();
        assertEquals("{http://www.example.org}effDate", attribute.getName());
        Attribute secondAttribute = (Attribute) extension.getAttributes().getLast();
        assertEquals("{http://www.example.org}lang", secondAttribute.getName());

        if (extension.getAnnotation() == null
                || extension.getAnnotation().getDocumentations() == null
                || extension.getAnnotation().getDocumentations().getFirst() == null
                || extension.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || extension.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no annotation content");
        }
        assertTrue(extension.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("This is an annotation of extensionOne."));

        assertEquals("{http://www.example.org}ItemType", extension.getBase().getName());
        assertEquals("extensionOne", extension.getId());

        if (complexContentType.getParticle() == null) {
            fail("Particle of ComplexContent is null");
        }

        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(3, sequencePattern.getParticles().size());
        // Particles are testet in the corresponding ProcessorTests (SequenceProcessorTest, ChoiceProcessorTest, ...).

        // If method "getParticle" would fail, the complexContentType would have no Particle. This is tested above.
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_attr_group.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading a group under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_illegal_object.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("There is the declaration of a Group within a ComplexContentExtension, but this wasn't detected. Only GroupRefs are allowed here.");
    }

    @Test
    public void testExtensionMultipleParticleExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_multi_group.xsd"));
        } catch (ExtensionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (\"Group\") under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_attr_all.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading an all under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testExtensionMultipleParticleExceptionWithAll() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_multi_all.xsd"));
        } catch (ExtensionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (\"All\") under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_attr_choice.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading a choice under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testExtensionMultipleParticleExceptionWithChoice() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_multi_choice.xsd"));
        } catch (ExtensionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (\"Choice\") under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testAttributeIsNotLastInContentModelExceptionWithSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_attr_sequence.xsd"));
        } catch (AttributeIsNotLastInContentModelException error) {
            return;
        }
        fail("There is already an attribute in the current ContentModel while reading a sequence under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testExtensionMultipleParticleExceptionWithSequence() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_multi_sequence.xsd"));
        } catch (ExtensionMultipleParticleException error) {
            return;
        }
        fail("There are multiple particles (\"Sequence\") under a ComplexContentExtension, but this wasn't detected.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeAndComplexContentExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_anyattribute_attr_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a ComplexContentextension.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeAndSimpleContentExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_simplecontent_anyattribute_attr_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a SimpleContentextension.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeGroupAndComplexContentExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_anyattribute_attrgroup_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a ComplexContentextension.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAttributeGroupAndSimpleContentExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_simplecontent_anyattribute_attrgroup_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a SimpleContentextension.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAnyAttributeAndComplexContentExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_complexcontent_anyattribute_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a ComplexContentextension.");
    }

    @Test
    public void testAnyAttributeIsNotLastExceptionWithAnyAttributeAndSimpleContentExtension() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_simplecontent_anyattribute_pos.xsd"));
        } catch (AnyAttributeIsNotLastException error) {
            return;
        }
        fail("AnyAttribute is in the wrong position under a SimpleContentextension.");
    }

    @Test
    public void testMultipleAnnotationException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_multi_annot.xsd"));
        } catch (MultipleAnnotationException error) {
            return;
        }
        fail("Multiple annotations, but this was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of extension, but it was not detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("There is an empty ID, but it wasn't detected.");
    }

    @Test
    public void testEmptyInheritanceBaseException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_empty_inheritance_base.xsd"));
        } catch (EmptyInheritanceBaseException error) {
            return;
        }
        fail("There is an empty base in the extension, but it wasn't detected.");
    }

    @Test
    public void testInvalidQNameException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/extensionTests/extension_invalid_qname.xsd"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("There is an invalid QName as base of the extension, but it wasn't detected.");
    }
}
