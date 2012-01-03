package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.xsd.om.Documentation;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.EmptyIdException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.EmptyLangException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.InvalidAnyUriException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AnnotationProcessor
 * @author Lars Schmidt, Dominik Wolff
 */
public class AnnotationProcessorTest extends junit.framework.TestCase{

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
    public void testProcessNodeForValidCase() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/annotationTests/annotation_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        if (schema.getTypes().isEmpty() || !(schema.getTypes().getFirst() instanceof SimpleType)) {
            fail("There is no SimpleType found");
        }
        SimpleType simpleType = (SimpleType) schema.getTypes().getFirst();

        if (simpleType.getAnnotation() == null
                || simpleType.getAnnotation().getDocumentations() == null
                || simpleType.getAnnotation().getDocumentations().getFirst() == null
                || simpleType.getAnnotation().getDocumentations().getFirst().getNodeList() == null
                || simpleType.getAnnotation().getDocumentations().getFirst().getNodeList().item(0) == null) {
            fail("There is no documentation content");
        }
        assertTrue(simpleType.getAnnotation().getDocumentations().getFirst().getNodeList().item(0).getTextContent().contains("This is an annotation."));

        assertEquals("annotationOne", simpleType.getAnnotation().getId());

        Documentation documentation = simpleType.getAnnotation().getDocumentations().getFirst();

        assertEquals("http://www.example.org/annot.html#documentation", documentation.getSource());
        assertEquals("en", documentation.getXmlLang());
        assertTrue(documentation.getNodeList() != null);

                if (simpleType.getAnnotation() == null
                || simpleType.getAnnotation().getAppInfos() == null
                || simpleType.getAnnotation().getAppInfos().getFirst() == null
                || simpleType.getAnnotation().getAppInfos().getFirst().getNodeList() == null
                || simpleType.getAnnotation().getAppInfos().getFirst().getNodeList().item(0) == null) {
            fail("There is no appinfo content");
        }
        assertTrue(simpleType.getAnnotation().getAppInfos().getFirst().getNodeList().item(0).getTextContent().contains("This is an appinfo."));

        assertEquals("http://www.example.org/annot.html#appinfo", simpleType.getAnnotation().getAppInfos().getFirst().getSource());
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/annotationTests/annotation_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The ID is empty, but it was not detected.");
    }

    @Test
    public void testInvalideAnyUriExceptionInAppinfo() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/annotationTests/annotation_invalid_anyuri_appinfo.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("The AnyUri of the Appinfo is invalid, but it was not detected.");
    }

    @Test
    public void testInvalideAnyUriExceptionInDocumentation() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/annotationTests/annotation_invalid_anyuri_documentation.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("The AnyUri of the Documentation is invalid, but it was not detected.");
    }

    @Test
    public void testEmptyLangException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/annotationTests/annotation_empty_lang.xsd"));
        } catch (EmptyLangException error) {
            return;
        }
        fail("The xml:lang attribute of a documentation is empty, but it was not detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/annotationTests/annotation_wrong_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content of Annotation, but it was not detected.");
    }
}