package de.tudortmund.cs.bonxai.utils;

import org.junit.*;

import java.util.*;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

public class ConstraintVisitorTest extends junit.framework.TestCase {

    private XSDSchema getSchemaFixture() {
        return new XSDSchema();
    }

    private MockedConstraintVisitor getVisitorMock() {
        return new MockedConstraintVisitor();
    }

    private Key getKeyConstraintFixture() {
        return new Key( "{}foo", "bar" );
    }

    public final void testEmptySchema() {
        MockedConstraintVisitor visitor = this.getVisitorMock();
        XSDSchema schema = this.getSchemaFixture();
        visitor.visitSchema( schema );

        assertEquals( 0, visitor.visitedConstraints.size() );
    }

    public final void testGlobalElementConstraint()
    throws SymbolAlreadyRegisteredException {
        MockedConstraintVisitor visitor = this.getVisitorMock();
        XSDSchema schema = this.getSchemaFixture();
        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element( "{}element" );
        Constraint constraint = this.getKeyConstraintFixture();
        element.addConstraint( constraint );
        schema.addElement(
            schema.getElementSymbolTable().createReference( "{}element", element )
        );

        visitor.visitSchema( schema );

        assertSame( constraint, visitor.visitedConstraints.get( 0 ) );
    }

    public final void testTypeParticleContainerConstraint()
    throws SymbolAlreadyRegisteredException {
        MockedConstraintVisitor visitor = this.getVisitorMock();
        XSDSchema schema = this.getSchemaFixture();

        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element( "{}element" );
        Constraint constraint = this.getKeyConstraintFixture();
        element.addConstraint( constraint );

        ComplexContentType content = new ComplexContentType();
        SequencePattern sequence = new SequencePattern();
        sequence.addParticle( element );
        content.setParticle( sequence );

        schema.addType(
            schema.getTypeSymbolTable().createReference(
                "{}complex Type",
                new ComplexType(
                    "{}complex Type",
                    content
                )
            )
        );

        visitor.visitSchema( schema );

        assertSame( constraint, visitor.visitedConstraints.get( 0 ) );
    }

    public final void testTypeParticleContainerMultipleConstraints()
    throws SymbolAlreadyRegisteredException {
        MockedConstraintVisitor visitor = this.getVisitorMock();
        XSDSchema schema = this.getSchemaFixture();

        de.tudortmund.cs.bonxai.xsd.Element element1 = new de.tudortmund.cs.bonxai.xsd.Element( "{}element_1" );
        de.tudortmund.cs.bonxai.xsd.Element element2 = new de.tudortmund.cs.bonxai.xsd.Element( "{}element_2" );
        Constraint constraint1 = this.getKeyConstraintFixture();
        Constraint constraint2 = this.getKeyConstraintFixture();
        element1.addConstraint( constraint1 );
        element2.addConstraint( constraint2 );

        ComplexContentType content = new ComplexContentType();
        SequencePattern sequence = new SequencePattern();
        sequence.addParticle( element1 );
        sequence.addParticle( element2 );
        content.setParticle( sequence );

        schema.addType(
            schema.getTypeSymbolTable().createReference(
                "{}complex Type",
                new ComplexType(
                    "{}complex Type",
                    content
                )
            )
        );

        visitor.visitSchema( schema );

        assertSame( constraint1, visitor.visitedConstraints.get( 0 ) );
        assertSame( constraint2, visitor.visitedConstraints.get( 1 ) );
    }
}
