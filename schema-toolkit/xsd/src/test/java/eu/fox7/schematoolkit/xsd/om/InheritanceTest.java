package eu.fox7.schematoolkit.xsd.om;


import org.junit.Test;

import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.Inheritance;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;

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
