package eu.fox7.bonxai.common;


import org.junit.*;

import eu.fox7.bonxai.common.Element;
import eu.fox7.bonxai.common.SymbolAlreadyRegisteredException;
import eu.fox7.bonxai.common.SymbolTable;
import eu.fox7.bonxai.common.SymbolTableFoundation;
import eu.fox7.bonxai.common.SymbolTableRef;

import java.util.LinkedList;

/**
 * Tests for SymbolTable
 */
public class SymbolTableTest extends SymbolTableFoundationTest {

    protected SymbolTableFoundation<Element> getFoundationImplementationFixture() {
        return new SymbolTable<Element>();
    }

    public final void testGetAllReferences() throws SymbolAlreadyRegisteredException {
        SymbolTable<Element> table = new SymbolTable<Element>();
        Element        element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        Element anotherElement = new eu.fox7.bonxai.xsd.Element( "{}element_2" );

        table.createReference("reference 1", element);
        table.createReference("reference 2", anotherElement);

        LinkedList<SymbolTableRef<Element>> list = table.getReferences();
        assertSame( list.get(0).getReference(), element );
        assertSame( list.get(1).getReference(), anotherElement );
    }
}
