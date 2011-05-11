package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.*;
import org.junit.*;

import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

public class SimpleContentPreProcessorTest extends junit.framework.TestCase {
    private int uniqueId = 0;

    private PreProcessor getPreProcessorFixture() {
        PreProcessor processor = new PreProcessor();
        processor.addVisitor( new SimpleContentPreProcessorVisitor() );
        return processor;
    }

    private XSDSchema getSchemaFixture() {
        XSDSchema schema = new XSDSchema();
        return schema;
    }

    private ComplexType registerComplexTypeFixture( XSDSchema schema, Content content )
    throws SymbolAlreadyRegisteredException {
        this.uniqueId++;
        ComplexType type = new ComplexType( "{}complex type " + this.uniqueId, content );
        schema.addType(
            schema.getTypeSymbolTable().createReference(
                "{}complex type " + this.uniqueId,
                type
            )
        );
        return type;
    }

    private SymbolTableRef<Type> getLastTypeReference( XSDSchema schema ) {
        return schema.getTypeSymbolTable().getReference(
            "{}complex type " + this.uniqueId
        );
    }

    private Attribute getAttributeFixture( String name ) {
        return new Attribute( name, new SymbolTableRef<Type>( "foo", null ) );
    }

    public final void setUp() {
        // Initialize id counter
        this.uniqueId = 0;
    }

    public final void testSimpleTypeExtraction()
    throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();

        SimpleContentType baseContent = new SimpleContentType();
        SimpleContentRestriction baseRestriction = new SimpleContentRestriction(
            new SymbolTableRef<Type>(
                "{http://www.w3.org/2001/XMLSchema}string",
                new SimpleType( "{http://www.w3.org/2001/XMLSchema}string", null )
            )
        );
        baseContent.setInheritance( baseRestriction );
        ComplexType baseType = this.registerComplexTypeFixture(
            schema,
            baseContent
        );
        SymbolTableRef<Type> baseTypeRef = this.getLastTypeReference( schema );

        this.getPreProcessorFixture().process( schema );

        assertTrue( baseContent.getInheritance() instanceof SimpleContentExtension );
        assertSame(
            schema.getTypeSymbolTable().getReference(
                baseTypeRef.getKey() + "-SimpleType"
            ).getReference(),
            ((SimpleContentExtension)baseContent.getInheritance()).getBase()
        );
        assertSame(
            baseRestriction,
            ((SimpleType)schema.getTypeSymbolTable().getReference(
                baseTypeRef.getKey() + "-SimpleType"
            ).getReference()).getInheritance()
        );
    }

    public final void testNoAttributeChanges()
    throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();

        SimpleContentType baseContent = new SimpleContentType();
        SimpleContentRestriction baseRestriction = new SimpleContentRestriction(
            new SymbolTableRef<Type>(
                "{http://www.w3.org/2001/XMLSchema}string",
                new SimpleType( "{http://www.w3.org/2001/XMLSchema}string", null )
            )
        );
        baseContent.setInheritance( baseRestriction );
        ComplexType baseType = this.registerComplexTypeFixture(
            schema,
            baseContent
        );
        Attribute attribute = this.getAttributeFixture( "{}attribute" );
        baseType.addAttribute( attribute );

        this.getPreProcessorFixture().process( schema );

        assertEquals( 1, baseType.getAttributes().size() );
        assertSame( attribute, baseType.getAttributes().get( 0 ) );
    }

    public final void testUniqueNameGeneration()
    throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();

        SimpleContentType baseContent = new SimpleContentType();
        SimpleContentRestriction baseRestriction = new SimpleContentRestriction(
            new SymbolTableRef<Type>(
                "{http://www.w3.org/2001/XMLSchema}string",
                new SimpleType( "{http://www.w3.org/2001/XMLSchema}string", null )
            )
        );
        baseContent.setInheritance( baseRestriction );
        ComplexType baseType = this.registerComplexTypeFixture(
            schema,
            baseContent
        );
        SymbolTableRef<Type> baseTypeRef = this.getLastTypeReference( schema );

        schema.getTypeSymbolTable().createReference(
            baseTypeRef.getKey() + "-SimpleType",
            null
        );
        schema.getTypeSymbolTable().createReference(
            baseTypeRef.getKey() + "-SimpleType-1",
            null
        );

        this.getPreProcessorFixture().process( schema );

        assertTrue( baseContent.getInheritance() instanceof SimpleContentExtension );
        assertSame(
            schema.getTypeSymbolTable().getReference(
                baseTypeRef.getKey() + "-SimpleType-2"
            ).getReference(),
            ((SimpleContentExtension)baseContent.getInheritance()).getBase()
        );
        assertSame(
            baseRestriction,
            ((SimpleType)schema.getTypeSymbolTable().getReference(
                baseTypeRef.getKey() + "-SimpleType-2"
            ).getReference()).getInheritance()
        );
    }
}
