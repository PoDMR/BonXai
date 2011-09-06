package eu.fox7.bonxai.common;


import org.junit.*;

import eu.fox7.bonxai.common.Element;
import eu.fox7.bonxai.common.SymbolAlreadyRegisteredException;
import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;

import java.util.LinkedList;
import java.util.HashSet;

/**
 * Tests against the SymbolTableFoundation interface
 */
public abstract class SymbolTableFoundationTest extends junit.framework.TestCase {

    /**
     * All implementing tests need to return a new object instance of their
     * SymbolTableFoundation implementation in this method.
     */
    protected abstract SymbolTableFoundation<Element> getFoundationImplementationFixture();

    public final void testCreateSymbolTableFoundation() {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
    }

    public final void testGetNotExistingElement() {
        SymbolTableFoundation<Element> table  = this.getFoundationImplementationFixture();
        SymbolTableRef<Element> reference = table.getReference("not existing");
        assertTrue( reference instanceof SymbolTableRef<?> );
        assertEquals( reference.getReference(), null );
    }

    public final void testAppendReference() throws SymbolAlreadyRegisteredException {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        table.createReference("reference", element);

        SymbolTableRef<Element> reference = table.getReference("reference");
        assertSame(element, reference.getReference());
    }

    public final void testAppendMultipleReferences() throws SymbolAlreadyRegisteredException {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        table.createReference("reference 1", element);
        table.createReference("reference 2", new eu.fox7.bonxai.xsd.Element( "{}element" ));

        SymbolTableRef<Element> reference = table.getReference("reference 1");
        assertSame(element, reference.getReference());
    }

    public final void testReRegisterReference() throws SymbolAlreadyRegisteredException {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
        table.createReference( "reference", new eu.fox7.bonxai.xsd.Element( "{}element" ) );
        try {
            table.createReference( "reference", new eu.fox7.bonxai.xsd.Element( "{}element" ) );
            fail( "SymbolAlreadyRegisteredException expected." );
        }
        catch ( SymbolAlreadyRegisteredException e ) {
            // Expected
        }
    }

    public final void testCreateReferenceUsingUpdateOrCreate() {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        table.updateOrCreateReference("reference", element);

        SymbolTableRef<Element> reference = table.getReference("reference");
        assertSame(element, reference.getReference());
    }

    public final void testUpdateReference() throws SymbolAlreadyRegisteredException {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        Element updatedElement = new eu.fox7.bonxai.xsd.Element( "{}element_updated" );
        table.createReference("reference", element);

        SymbolTableRef<Element> reference = table.getReference("reference");
        assertSame(element, reference.getReference());

        table.updateOrCreateReference( "reference", updatedElement );
        reference = table.getReference( "reference" );
        assertSame(updatedElement, reference.getReference());
    }

    public final void testGetAllReferencedObjects() throws SymbolAlreadyRegisteredException {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
        Element        element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        Element anotherElement = new eu.fox7.bonxai.xsd.Element( "{}element_2" );

        table.createReference("reference 1", element);
        table.createReference("reference 2", anotherElement);

        HashSet<Element> set = table.getAllReferencedObjects();
        assertEquals( 2, set.size() );
        assertTrue( set.contains( element ) );
        assertTrue( set.contains( anotherElement) );
    }

    public void testHasReference() throws SymbolAlreadyRegisteredException {
        SymbolTableFoundation<Element> table = this.getFoundationImplementationFixture();
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        table.createReference("reference 1", element);

        assertTrue(table.hasReference("reference 1"));
        assertFalse(table.hasReference("reference 2"));
    }
}
