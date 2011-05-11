package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;
import org.junit.Assert.*;

import de.tudortmund.cs.bonxai.common.*;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.SimpleContentUnion;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import java.util.LinkedList;

public class TypeLibraryExtractorTest extends junit.framework.TestCase {

    private XSDSchema schema;

    public void setUp() {
        this.schema = new XSDSchema();

        // Register some XSD primitive data types
        try {
            this.schema.addType(
                schema.getTypeSymbolTable().createReference(
                    "{http://www.w3.org/2001/XMLSchema}string",
                    new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null)
                )
            );
            this.schema.addType(
                schema.getTypeSymbolTable().createReference(
                    "{http://www.w3.org/2001/XMLSchema}double",
                    new SimpleType("{http://www.w3.org/2001/XMLSchema}double", null)
                )
            );
        } catch (SymbolAlreadyRegisteredException e) {
            throw new RuntimeException("Setup of types broken.");
        }

    }

    @Test
    public void testExtractEmptyTypeLibrary() {
        TypeLibraryExtractor extractor = new TypeLibraryExtractor();

        HashMap<String, XSDSchema> result = extractor.extractTypeLibraries(this.schema);

        assertNotNull(result);

        assertEquals(0, result.size());
    }

    @Test
    public void testExtractEmptyTypeLibraryComplexTypesOnly() {
        schema.getNamespaceList().addIdentifiedNamespace(
            new IdentifiedNamespace("default", "http://example.com")
        );
        schema.setTargetNamespace("http://example.com");

        this.createComplexType("{http://example.com}fooType");
        this.createComplexType("{http://example.com}barType");

        TypeLibraryExtractor extractor = new TypeLibraryExtractor();

        HashMap<String, XSDSchema> result = extractor.extractTypeLibraries(this.schema);

        assertNotNull(result);

        assertEquals(0, result.size());
    }

    @Test
    public void testExtractSingleTypeLibrary() {
        schema.getNamespaceList().addIdentifiedNamespace(
            new IdentifiedNamespace("default", "http://example.com")
        );
        schema.setTargetNamespace("http://example.com");

        this.createSimpleType("{http://example.com}fooType");
        this.createSimpleType("{http://example.com}barType");

        TypeLibraryExtractor extractor = new TypeLibraryExtractor();

        HashMap<String, XSDSchema> result = extractor.extractTypeLibraries(this.schema);

        assertNotNull(result);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("http://example.com"));

        XSDSchema resultSchema = result.get("http://example.com");

        assertNotNull(resultSchema);

        assertEquals("http://example.com", resultSchema.getTargetNamespace());

        assertNotNull(resultSchema.getTypes());
        assertEquals(2, resultSchema.getTypes().size());

        assertTrue(resultSchema.getTypes().get(0) instanceof SimpleType);
        assertEquals("fooType", ((SimpleType) resultSchema.getTypes().get(0)).getLocalName());

        assertTrue(resultSchema.getTypes().get(1) instanceof SimpleType);
        assertEquals("barType", ((SimpleType) resultSchema.getTypes().get(1)).getLocalName());
    }

    @Test
    public void testExtractMultipleTypeLibraries() {
        schema.getNamespaceList().addIdentifiedNamespace(
            new IdentifiedNamespace("foo", "http://example.com/foo")
        );
        schema.getNamespaceList().addIdentifiedNamespace(
            new IdentifiedNamespace("bar", "http://example.com/bar")
        );
        schema.setTargetNamespace("http://example.com/foo");

        this.createSimpleType("{http://example.com/foo}fooType");
        this.createSimpleType("{http://example.com/bar}barType");

        TypeLibraryExtractor extractor = new TypeLibraryExtractor();

        HashMap<String, XSDSchema> result = extractor.extractTypeLibraries(this.schema);

        assertNotNull(result);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("http://example.com/foo"));

        XSDSchema firstResultSchema = result.get("http://example.com/foo");

        assertNotNull(firstResultSchema);

        assertEquals("http://example.com/foo", firstResultSchema.getTargetNamespace());

        assertNotNull(firstResultSchema.getTypes());
        assertEquals(1, firstResultSchema.getTypes().size());

        assertTrue(firstResultSchema.getTypes().get(0) instanceof SimpleType);
        assertEquals("fooType", ((SimpleType) firstResultSchema.getTypes().get(0)).getLocalName());

        assertTrue(result.containsKey("http://example.com/bar"));

        XSDSchema secondResultSchema = result.get("http://example.com/bar");

        assertNotNull(secondResultSchema);

        assertEquals("http://example.com/bar", secondResultSchema.getTargetNamespace());

        assertNotNull(secondResultSchema.getTypes());
        assertEquals(1, secondResultSchema.getTypes().size());

        assertTrue(secondResultSchema.getTypes().get(0) instanceof SimpleType);
        assertEquals("barType", ((SimpleType) secondResultSchema.getTypes().get(0)).getLocalName());
    }

    @Test
    public void testExtractMultipleTypeLibrariesWithComplexTypes() {
        schema.getNamespaceList().addIdentifiedNamespace(
            new IdentifiedNamespace("foo", "http://example.com/foo")
        );
        schema.getNamespaceList().addIdentifiedNamespace(
            new IdentifiedNamespace("bar", "http://example.com/bar")
        );
        schema.getNamespaceList().addIdentifiedNamespace(
            new IdentifiedNamespace("baz", "http://example.com/baz")
        );
        schema.setTargetNamespace("http://example.com/foo");

        this.createComplexType("{http://example.com/foo}fooComplexType");
        this.createSimpleType("{http://example.com/foo}fooType");
        this.createSimpleType("{http://example.com/bar}barType");
        this.createComplexType("{http://example.com/bar}barComplexType");
        this.createComplexType("{http://example.com/baz}bazComplexType");

        TypeLibraryExtractor extractor = new TypeLibraryExtractor();

        HashMap<String, XSDSchema> result = extractor.extractTypeLibraries(this.schema);

        assertNotNull(result);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("http://example.com/foo"));

        XSDSchema firstResultSchema = result.get("http://example.com/foo");

        assertNotNull(firstResultSchema);

        assertEquals("http://example.com/foo", firstResultSchema.getTargetNamespace());

        assertNotNull(firstResultSchema.getTypes());
        assertEquals(1, firstResultSchema.getTypes().size());

        assertTrue(firstResultSchema.getTypes().get(0) instanceof SimpleType);
        assertEquals("fooType", ((SimpleType) firstResultSchema.getTypes().get(0)).getLocalName());

        assertTrue(result.containsKey("http://example.com/bar"));

        XSDSchema secondResultSchema = result.get("http://example.com/bar");

        assertNotNull(secondResultSchema);

        assertEquals("http://example.com/bar", secondResultSchema.getTargetNamespace());

        assertNotNull(secondResultSchema.getTypes());
        assertEquals(1, secondResultSchema.getTypes().size());

        assertTrue(secondResultSchema.getTypes().get(0) instanceof SimpleType);
        assertEquals("barType", ((SimpleType) secondResultSchema.getTypes().get(0)).getLocalName());
    }

    private void createComplexType(String name) {
        try {
            this.schema.addType(
                schema.getTypeSymbolTable().createReference(
                    name,
                    new ComplexType(
                        name,
                        new ComplexContentType(new SequencePattern())
                    )
                )
            );
        } catch (SymbolAlreadyRegisteredException e) {
            throw new RuntimeException("Broken complex type registration in test.");
        }
    }

    public void createSimpleType(String name) {
        LinkedList<SymbolTableRef<Type>> baseTypes = new LinkedList<SymbolTableRef<Type>>();

        baseTypes.add(
            this.schema.getTypeSymbolTable().getReference(
                "{http://www.w3.org/2001/XMLSchema}string"
            )
        );
        baseTypes.add(
            this.schema.getTypeSymbolTable().getReference(
                "{http://www.w3.org/2001/XMLSchema}double"
            )
        );

        try {
            this.schema.addType(
                schema.getTypeSymbolTable().createReference(
                    name,
                    new SimpleType(
                        name,
                        new SimpleContentUnion(baseTypes)
                    )
                )
            );
        } catch (SymbolAlreadyRegisteredException e) {
            throw new RuntimeException("Broken simple type registration in test.");
        }
    }
}
