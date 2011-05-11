package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.XSDSchema.BlockDefault;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.FinalDefault;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.Qualification;


import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.ElementRef;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import org.junit.Before;
import org.junit.Test;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.type.*;

import java.io.*;
import javax.xml.parsers.*;
import org.junit.*;
import org.w3c.dom.*;

import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class SchemaProcessorTest extends junit.framework.TestCase{

    // Schema for this testcase
    XSDSchema schema;
    // SchemaProcessor used by the testcase
    SchemaProcessor schemaProcessor;

    /**
     * Befor every test the schema and schemaProcessor are refreshed
     */
    @Before
    public void setUp() {
        schema = new XSDSchema();
        schemaProcessor = new SchemaProcessor(schema);
    }

    @Test
    public void testValidCase() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_valid.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(schema.getAttributeFormDefault() == Qualification.qualified);
        assertTrue(schema.getElementFormDefault() == Qualification.qualified);
        assertTrue(schema.getBlockDefaults().contains(BlockDefault.extension));
        assertTrue(schema.getBlockDefaults().contains(BlockDefault.restriction));
        assertTrue(schema.getBlockDefaults().contains(BlockDefault.substitution));
        assertTrue(schema.getFinalDefaults().contains(FinalDefault.extension));
        assertTrue(schema.getFinalDefaults().contains(FinalDefault.restriction));
        assertTrue(schema.getFinalDefaults().contains(FinalDefault.list));
        assertTrue(schema.getFinalDefaults().contains(FinalDefault.union));
        assertTrue(schema.getId().equals("ID"));
        assertTrue(schema.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema"));
        assertTrue(schema.getNamespaceList().getDefaultNamespace().getUri().equals("http://www.w3.org/2001/XMLSchema"));

        Element element = schema.getElementSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}element").getReference();
        assertTrue(element.getTypeAttr() == true);
        assertTrue(element.getType() instanceof SimpleType);

        Attribute attribute = schema.getAttributeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}attribute").getReference();
        assertTrue(attribute.getTypeAttr() == true);
        assertTrue(attribute.getSimpleType().getLocalName().equals("string"));

        Object object = schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}simpleType").getReference();
        assertTrue(object instanceof SimpleType);

        object = schema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}complexType").getReference();
        assertTrue(object instanceof ComplexType);

        object = schema.getGroupSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}group").getReference();
        assertTrue(object instanceof Group);

        object = schema.getAttributeGroupSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}attributeGroup").getReference();
        assertTrue(object instanceof AttributeGroup);

        assertTrue(schema.getForeignSchemas().get(0) instanceof IncludedSchema);
        assertTrue(schema.getForeignSchemas().get(1) instanceof ImportedSchema);
        assertTrue(schema.getForeignSchemas().get(2) instanceof RedefinedSchema);
    }

    /**
     * Test of processNode method, of class SchemaProcessor, for an invalid anyUri
     * which contains a key with two selectors
     * @throws java.lang.Exception
     */
    @Test
    public void testInvalidAnyUriException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_anyUri.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("The value of a namespace is an invalid anyUri, but it wasn't detected.");
    }

    @Test
    public void testInvalidAnyUriExceptionWithTargetNamespace() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_anyUri_targetNamespace.xsd"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("The value of the targetNamespace is an invalid anyUri, but it wasn't detected.");
    }

    @Test
    public void testEmptyIdException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_empty_id.xsd"));
        } catch (EmptyIdException error) {
            return;
        }
        fail("The id attribute of schema element is empty, but it wasn't detected.");
    }

    @Test
    public void testInvalidElementFormDefaultValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_elementFormDefault_value.xsd"));
        } catch (InvalidElementFormDefaultValueException error) {
            return;
        }
        fail("The value of a elementFormDefault is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidAttributeFormDefaultValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_attributeFormDefault_value.xsd"));
        } catch (InvalidAttributeFormDefaultValueException error) {
            return;
        }
        fail("The value of a attributeformdefault is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidFinalDefaultValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_finalDefault_value.xsd"));
        } catch (InvalidFinalDefaultValueException error) {
            return;
        }
        fail("The value of a finalDefault is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidBlockDefaultValueException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_blockDefault_value.xsd"));
        } catch (InvalidBlockDefaultValueException error) {
            return;
        }
        fail("The value of a blockDefault is invalid, but it wasn't detected.");
    }

    @Test
    public void testInvalidForeignSchemaLocationExceptionWithInclude() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_foreignSchema_location_include.xsd"));
        } catch (InvalidForeignSchemaLocationException error) {
            return;
        }
        fail("Include at wrong location in schema, but it wasn't detected.");
    }

    @Test
    public void testInvalidForeignSchemaLocationExceptionWithImport() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_foreignSchema_location_import.xsd"));
        } catch (InvalidForeignSchemaLocationException error) {
            return;
        }
        fail("Import at wrong location in schema, but it wasn't detected.");
    }

    @Test
    public void testInvalidForeignSchemaLocationExceptionWithRedefine() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_invalid_foreignSchema_location_redefine.xsd"));
        } catch (InvalidForeignSchemaLocationException error) {
            return;
        }
        fail("Redefine at wrong location in schema, but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithAttribute() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_illegal_object_returned_attribute.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("AttributeRef in schema element, but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithElement() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_illegal_object_returned_element.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("ElementRef in schema element, but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_illegal_object_returned_group.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("GroupRef in schema element, but it wasn't detected.");
    }

    @Test
    public void testIllegalObjectReturnedExceptionWithAttributeGroup() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_illegal_object_returned_attributeGroup.xsd"));
        } catch (IllegalObjectReturnedException error) {
            return;
        }
        fail("AttributeGroupRef in schema element, but it wasn't detected.");
    }

    @Test
    public void testUnsupportedContentException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_unsupported_content.xsd"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Schema contains unsupported content, but it wasn't detected.");
    }

        @Test
    public void testDuplicateElementException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_duplicate_element.xsd"));
        } catch (DuplicateElementException error) {
            return;
        }
        fail("A global element has a duplicate, but it wasn't detected.");
    }

    @Test
    public void testDuplicateAttributeException() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/schemaTests/schema_duplicate_attribute.xsd"));
        } catch (DuplicateAttributeException error) {
            return;
        }
        fail("A global attribute has a duplicate, but it wasn't detected.");
    }
}