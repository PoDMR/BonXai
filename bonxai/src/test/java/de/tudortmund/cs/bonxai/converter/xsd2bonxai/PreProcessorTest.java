package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.*;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

public class PreProcessorTest extends junit.framework.TestCase {
    private PreProcessorVisitorMock getPreProcessorVisitorMock() {
        return new PreProcessorVisitorMock();
    }

    private XSDSchema getSchemaFixture() {
        XSDSchema schema = new XSDSchema();
        return schema;
    }

    private ComplexType registerComplexTypeFixture( XSDSchema schema )
    throws SymbolAlreadyRegisteredException {
        ComplexType type = new ComplexType( "{}complex type", new ComplexContentType() );
        schema.addType(
            schema.getTypeSymbolTable().createReference(
                "{}complex type",
                type
            )
        );
        return type;
    }

    private PreProcessor getPreProcessorFixture() {
        return new PreProcessor();
    }

    public final void testSchemaVisited()
    throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();
        PreProcessor processor = this.getPreProcessorFixture();
        PreProcessorVisitorMock visitor = this.getPreProcessorVisitorMock();
        processor.addVisitor( visitor );
        processor.process( schema );

        assertEquals( 1, visitor.visitedSchemas.size() );
        assertEquals( 0, visitor.visitedComplexTypes.size() );
        assertSame( schema, visitor.visitedSchemas.get( 0 ) );
    }

    public final void testComplexTypesVisited()
    throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();
        ComplexType type = this.registerComplexTypeFixture( schema );
        PreProcessor processor = this.getPreProcessorFixture();
        PreProcessorVisitorMock visitor = this.getPreProcessorVisitorMock();
        processor.addVisitor( visitor );
        processor.process( schema );

        assertEquals( 1, visitor.visitedSchemas.size() );
        assertEquals( 1, visitor.visitedComplexTypes.size() );
        assertSame( schema, visitor.visitedSchemas.get( 0 ) );
        assertSame( type, visitor.visitedComplexTypes.get( 1 ) );
    }

    public final void testMultipleVisitors()
    throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();
        ComplexType type = this.registerComplexTypeFixture( schema );
        PreProcessor processor = this.getPreProcessorFixture();
        PreProcessorVisitorMock visitor1 = this.getPreProcessorVisitorMock();
        PreProcessorVisitorMock visitor2 = this.getPreProcessorVisitorMock();
        processor.addVisitor( visitor1 );
        processor.addVisitor( visitor2 );
        processor.process( schema );

        assertEquals( 1, visitor1.visitedSchemas.size() );
        assertEquals( 1, visitor1.visitedComplexTypes.size() );
        assertSame( schema, visitor1.visitedSchemas.get( 0 ) );
        assertSame( type, visitor1.visitedComplexTypes.get( 1 ) );

        assertEquals( 1, visitor2.visitedSchemas.size() );
        assertEquals( 1, visitor2.visitedComplexTypes.size() );
        assertSame( schema, visitor2.visitedSchemas.get( 0 ) );
        assertSame( type, visitor2.visitedComplexTypes.get( 1 ) );
    }

    public final void testVisitorsRunSequentially()
    throws SymbolAlreadyRegisteredException {
        XSDSchema schema = this.getSchemaFixture();
        ComplexType type = this.registerComplexTypeFixture( schema );
        PreProcessor processor = this.getPreProcessorFixture();
        PreProcessorVisitorMock visitor = this.getPreProcessorVisitorMock();
        processor.addVisitor( visitor );
        processor.addVisitor( visitor );
        processor.process( schema );

        assertEquals( 2, visitor.visitedSchemas.size() );
        assertEquals( 2, visitor.visitedComplexTypes.size() );
        assertSame( schema, visitor.visitedSchemas.get( 0 ) );
        assertSame( schema, visitor.visitedSchemas.get( 2 ) );
        assertSame( type, visitor.visitedComplexTypes.get( 1 ) );
        assertSame( type, visitor.visitedComplexTypes.get( 3 ) );
    }
}
