package eu.fox7.bonxai.common;


import org.junit.*;

import eu.fox7.bonxai.common.Element;
import eu.fox7.bonxai.common.MultiReferenceSymbolTable;
import eu.fox7.bonxai.common.SymbolAlreadyRegisteredException;
import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;

import java.util.LinkedList;

/**
 * Tests for MultiReferenceSymbolTable
 */
public class MultiReferenceSymbolTableTest extends SymbolTableFoundationTest {

    @Override
    protected SymbolTableFoundation<Element> getFoundationImplementationFixture() {
        return new MultiReferenceSymbolTable<Element>();
    }

    public final void testGetAllReferences() throws SymbolAlreadyRegisteredException {
        MultiReferenceSymbolTable<Element> table = new MultiReferenceSymbolTable<Element>();
        Element        element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        Element anotherElement = new eu.fox7.bonxai.xsd.Element( "{}element_2" );

        table.createReference("reference 1", element);
        table.createReference("reference 2", anotherElement);

        LinkedList<LinkedList<SymbolTableRef<Element>>> list = table.getAllReferences();
        assertSame( list.get(0).peek().getReference(), element );
        assertSame( list.get(1).peek().getReference(), anotherElement );
    }

    public final void testAddExistingReference() throws SymbolAlreadyRegisteredException {
        MultiReferenceSymbolTable<Element> table = new MultiReferenceSymbolTable<Element>();
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        SymbolTableRef<Element> reference = new SymbolTableRef<Element>( "reference", element );

        table.addReference( reference );

        assertSame( reference, table.getReference( "reference" ) );
    }

    public final void testReAddedReference() throws SymbolAlreadyRegisteredException {
        MultiReferenceSymbolTable<Element> table = new MultiReferenceSymbolTable<Element>();
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        SymbolTableRef<Element> reference = new SymbolTableRef<Element>( "reference", element );

        table.addReference( reference );
        try {
            table.addReference( reference );
            table.createReference( "reference", new eu.fox7.bonxai.xsd.Element( "{}element" ) );
            fail( "SymbolAlreadyRegisteredException expected." );
        }
        catch ( SymbolAlreadyRegisteredException e ) {
            // Expected
        }
    }

    public final void testAddSecondReference() throws SymbolAlreadyRegisteredException {
        MultiReferenceSymbolTable<Element> table = new MultiReferenceSymbolTable<Element>();
        Element initialElement = new eu.fox7.bonxai.xsd.Element( "{}element" );
        SymbolTableRef<Element> firstReference = new SymbolTableRef<Element>( "reference", initialElement );

        Element newElement = new eu.fox7.bonxai.xsd.Element( "{}new_element" );
        SymbolTableRef<Element> secondReference = new SymbolTableRef<Element>( "reference", newElement );

        table.addReference( firstReference );
        table.addReference( secondReference );

        assertSame( table.getReference( "reference" ).getReference(), newElement );

        LinkedList<LinkedList<SymbolTableRef<Element>>> list = table.getAllReferences();
        assertSame( list.get( 0 ).get( 0 ), firstReference );
        assertSame( list.get( 0 ).get( 1 ), secondReference );
        assertSame( list.get( 0 ).get( 0 ).getReference(), newElement );
        assertSame( list.get( 0 ).get( 1 ).getReference(), newElement );
    }
}
