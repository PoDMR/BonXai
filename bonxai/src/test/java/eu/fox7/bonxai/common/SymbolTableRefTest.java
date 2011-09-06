package eu.fox7.bonxai.common;


import org.junit.*;

import eu.fox7.bonxai.common.Element;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.Type;

/**
 * Tests for type connector
 */
public class SymbolTableRefTest extends junit.framework.TestCase {

    @Test
    public final void testCreateSymbolTableRefFromKey() {
        SymbolTableRef reference  = new SymbolTableRef("reference");
    }

    @Test
    public final void testCreateSymbolTableRefFromKeyAndElement() {
        SymbolTableRef<Element> reference  = new SymbolTableRef<Element>("reference", new eu.fox7.bonxai.xsd.Element( "{}element" ));
    }

    @Test
    public final void testGetKeyFromSymbolTableRef1() {
        SymbolTableRef reference  = new SymbolTableRef("reference");
        assertSame("reference", reference.getKey());
    }

    @Test
    public final void testGetKeyFromSymbolTableRef2() {
        SymbolTableRef<Element> reference  = new SymbolTableRef<Element>("reference", new eu.fox7.bonxai.xsd.Element( "{}element" ));
        assertSame("reference", reference.getKey());
    }

    @Test
    public final void testGetElementFromSymbolTableRef1() {
        SymbolTableRef<Element> reference  = new SymbolTableRef<Element>("reference");
        assertSame(null, reference.getReference());
    }

    @Test
    public final void testGetElementFromSymbolTableRef2() {
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        SymbolTableRef<Element> reference  = new SymbolTableRef<Element>("reference", element);
        assertSame(element, reference.getReference());
    }

    @Test
    public final void testSetReferenceInSymbolTable1() {
        Element element = new eu.fox7.bonxai.xsd.Element( "{}element" );
        SymbolTableRef<Element> reference  = new SymbolTableRef<Element>("reference");
        reference.setReference(element);
        assertSame(element, reference.getReference());
    }

    @Test
    public final void testSetReferenceInSymbolTable2() {
        Element element1 = new eu.fox7.bonxai.xsd.Element( "{}element" );
        Element element2 = new eu.fox7.bonxai.xsd.Element( "{}element" );
        SymbolTableRef<Element> reference  = new SymbolTableRef<Element>("reference", element1);
        reference.setReference(element2);
        assertNotSame(element1, reference.getReference());
        assertSame(element2, reference.getReference());
    }

    public void testEquals() {
        SymbolTableRef<Element> ref1 = new SymbolTableRef<Element>("{http://example.com}foo");
        SymbolTableRef<Element> ref2 = new SymbolTableRef<Element>("{http://example.com}foo");
        SymbolTableRef<Element> ref3 = new SymbolTableRef<Element>("{http://example.com}bar");

        assertTrue(ref1.equals(ref1));
        assertTrue(ref2.equals(ref2));
        assertTrue(ref3.equals(ref3));

        assertTrue(ref1.equals(ref2));
        assertTrue(ref2.equals(ref1));

        assertFalse(ref1.equals(ref3));
        assertFalse(ref2.equals(ref3));
        assertFalse(ref3.equals(ref1));
        assertFalse(ref3.equals(ref2));
    }
}
