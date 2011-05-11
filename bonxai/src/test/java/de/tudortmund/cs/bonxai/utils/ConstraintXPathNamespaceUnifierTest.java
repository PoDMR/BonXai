package de.tudortmund.cs.bonxai.utils;

import java.util.*;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.TestSchemaGenerator;

public class ConstraintXPathNamespaceUnifierTest extends junit.framework.TestCase {
    private XSDSchema getConstraintSchemaFixture( String selector, String field )
    throws de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException {
        XSDSchema schema = new XSDSchema();
        de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element( "{}element" );
        Key key= new Key( "{}whatever", selector );
        key.addField( field );
        element.addConstraint( key );
        schema.addElement(
            schema.getElementSymbolTable().createReference( "{}element", element )
        );

        return schema;
    }

    private MockedRenameingNamespaceIdentifierUnifier getUnifierMock() {
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put( "foo", "foo" );
        mapping.put( "bar", "a-bar" );
        return new MockedRenameingNamespaceIdentifierUnifier( mapping );
    }

    private ConstraintXPathNamespaceUnifier getXPathUnifierFixture() {
        return new ConstraintXPathNamespaceUnifier(
            this.getUnifierMock()
        );
    }

    public final void testGetIdentifierUnifier() {
        MockedRenameingNamespaceIdentifierUnifier unifier = this.getUnifierMock();
        ConstraintXPathNamespaceUnifier nsUnifier = new ConstraintXPathNamespaceUnifier( unifier );
        this.assertSame( unifier, nsUnifier.getIdentifierUnifier() );
    }

    public final void testNoNamespace()
    throws de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException {
        ConstraintXPathNamespaceUnifier unifier = this.getXPathUnifierFixture();
        XSDSchema schema = this.getConstraintSchemaFixture(
            "/foo/bar",
            "//bar/baz//foo"
        );

        unifier.visitSchema( schema );

        assertEquals( "/foo/bar", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getSelector() );
        assertEquals( 1, ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().size() );
        assertEquals( "//bar/baz//foo", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().toArray()[0] );
    }

    public final void testNoReplacedNamespace()
    throws de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException {
        ConstraintXPathNamespaceUnifier unifier = this.getXPathUnifierFixture();
        XSDSchema schema = this.getConstraintSchemaFixture(
            "/foo:this/bar",
            "//bar/baz//foo:that:evil:element"
        );

        unifier.visitSchema( schema );

        assertEquals( "/foo:this/bar", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getSelector() );
        assertEquals( 1, ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().size() );
        assertEquals( "//bar/baz//foo:that:evil:element", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().toArray()[0] );
    }

    public final void testReplacedNamespace()
    throws de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException {
        ConstraintXPathNamespaceUnifier unifier = this.getXPathUnifierFixture();
        XSDSchema schema = this.getConstraintSchemaFixture(
            "/foo/bar:that",
            "//bar:this:evil:element/baz//foo"
        );

        unifier.visitSchema( schema );

        assertEquals( "/foo/a-bar:that", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getSelector() );
        assertEquals( 1, ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().size() );
        assertEquals( "//a-bar:this:evil:element/baz//foo", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().toArray()[0] );
    }

    public final void testReplacedMultipleNamespaces()
    throws de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException {
        ConstraintXPathNamespaceUnifier unifier = this.getXPathUnifierFixture();
        XSDSchema schema = this.getConstraintSchemaFixture(
            "/foo/bar:that",
            "//bar:this:evil:element/baz//bar:something"
        );

        unifier.visitSchema( schema );
        assertEquals( "/foo/a-bar:that", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getSelector() );
        assertEquals( 1, ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().size() );
        assertEquals( "//a-bar:this:evil:element/baz//a-bar:something", ((SimpleConstraint)schema.getElements().get( 0 ).getConstraints().get( 0 )).getFields().toArray()[0] );
    }

}
