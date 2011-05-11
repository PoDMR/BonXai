package de.tudortmund.cs.bonxai.xsd;


import org.junit.Test;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;

/**
 *
 */
public class InheritanceTest extends junit.framework.TestCase {


    public InheritanceTest() {
    }


    /**
     * Test of getBase method, of class Inheritance.
     */
    @Test
    public void testGetBase() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref");
        Type t = new SimpleType("{}test", null);
        base.setReference(t);
        Inheritance instance = new Inheritance(base);
        Type expResult = t;
        Type result = instance.getBase();
        assertSame(expResult, result);
    }
}
