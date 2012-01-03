package eu.fox7.schematoolkit.xsd.om;


import java.util.LinkedList;

import org.junit.Test;

import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.SimpleContentFixableRestrictionProperty;
import eu.fox7.schematoolkit.xsd.om.SimpleContentPropertyWhitespace;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestrictionProperty;
import eu.fox7.schematoolkit.xsd.om.Type;

/**
 *
 */
public class SimpleContentRestrictionTest extends junit.framework.TestCase {

    public SimpleContentRestrictionTest() {
    }

    /**
     * Test of getEnumeration method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetEnumeration() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        LinkedList<String> expResult = instance.enumeration;
        LinkedList<String> result = instance.getEnumeration();
        assertEquals(expResult, result);
    }

    /**
     * Test of addEnumeration method, of class SimpleContentRestriction.
     */
    @Test
    public void testAddEnumeration() {
        LinkedList<String> val = new LinkedList<String>();
        String scrp = new String();
        val.add(scrp);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref2");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.addEnumeration(val);
        assertEquals(instance.getEnumeration().getLast(),scrp);
        }

    /**
     * Test of getFractionDigits method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetFractionDigits() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref3");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<Integer> expResult = instance.fractionDigits;
        SimpleContentFixableRestrictionProperty<Integer> result = instance.getFractionDigits();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFractionDigits method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetFractionDigits() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref4");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<Integer> val = new SimpleContentFixableRestrictionProperty<Integer>(23, true);
        instance.setFractionDigits(val);
        assertEquals(val, instance.fractionDigits);
        }

    /**
     * Test of getMaxExclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetMaxExclusive() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref5");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<String> expResult = instance.maxExclusive;
        SimpleContentFixableRestrictionProperty<String> result = instance.getMaxExclusive();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMaxExclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetMaxExclusive() {
        SimpleContentFixableRestrictionProperty<String> val = new SimpleContentFixableRestrictionProperty<String>("Testval2", true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref6");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setMaxExclusive(val);
        assertEquals(val, instance.maxExclusive);
    }

    /**
     * Test of getMaxInclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetMaxInclusive() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref7");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<String> expResult = instance.maxInclusive;
        SimpleContentFixableRestrictionProperty<String> result = instance.getMaxInclusive();
        assertEquals(expResult, result);
       }

    /**
     * Test of setMaxInclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetMaxInclusive() {
        SimpleContentFixableRestrictionProperty<String> val = new SimpleContentFixableRestrictionProperty<String>("foo", true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref8");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setMaxInclusive(val);
        assertEquals(val, instance.maxInclusive);
    }

    /**
     * Test of getMaxLength method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetMaxLength() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref9");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<Integer> expResult = instance.maxLength;
        SimpleContentFixableRestrictionProperty<Integer> result = instance.getMaxLength();
        assertEquals(expResult, result);
        }

    /**
     * Test of setMaxLength method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetMaxLength() {
        SimpleContentFixableRestrictionProperty<Integer> val = new SimpleContentFixableRestrictionProperty<Integer>(23, true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref10");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setMaxLength(val);
        assertEquals(val, instance.maxLength);
    }

    /**
     * Test of getMinExclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetMinExclusive() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref11");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<String> expResult = instance.maxExclusive;
        SimpleContentFixableRestrictionProperty<String> result = instance.getMinExclusive();
        assertEquals(expResult, result);
        }

    /**
     * Test of setMinExclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetMinExclusive() {
        SimpleContentFixableRestrictionProperty<String> val = new SimpleContentFixableRestrictionProperty<String>("foo", true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref12");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setMinExclusive(val);
        assertEquals(val, instance.minExclusive);
        }

    /**
     * Test of getMinInclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetMinInclusive() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref13");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<String> expResult = instance.maxInclusive;
        SimpleContentFixableRestrictionProperty<String> result = instance.getMinInclusive();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMinInclusive method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetMinInclusive() {
        SimpleContentFixableRestrictionProperty<String> val = new SimpleContentFixableRestrictionProperty<String>("foo", true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref14");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setMinInclusive(val);
        assertEquals(val, instance.minInclusive);
    }

    /**
     * Test of getMinLength method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetMinLength() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref15");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<Integer> expResult = instance.minLength;
        SimpleContentFixableRestrictionProperty<Integer> result = instance.getMinLength();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMinLength method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetMinLength() {
        SimpleContentFixableRestrictionProperty<Integer> val = new SimpleContentFixableRestrictionProperty<Integer>(23, true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref16");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setMinLength(val);
        assertEquals(val, instance.minLength);
    }

    /**
     * Test of getPattern method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetPattern() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref17");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentRestrictionProperty<String> expResult = instance.pattern;
        SimpleContentRestrictionProperty<String> result = instance.getPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPattern method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetPattern() {
        SimpleContentFixableRestrictionProperty<String> val = new SimpleContentFixableRestrictionProperty<String>("Testval7", true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref16");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setPattern(val);
        assertEquals(val, instance.pattern);
    }

    /**
     * Test of getTotalDigits method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetTotalDigits() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref17");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<Integer> expResult = instance.totalDigits;
        SimpleContentFixableRestrictionProperty<Integer> result = instance.getTotalDigits();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTotalDigits method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetTotalDigits() {
        SimpleContentFixableRestrictionProperty<Integer> val = new SimpleContentFixableRestrictionProperty<Integer>(23, true);
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref16");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setTotalDigits(val);
        assertEquals(val, instance.totalDigits);
    }

    /**
     * Test of getWhitespace method, of class SimpleContentRestriction.
     */
    @Test
    public void testGetWhitespace() {
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref17");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> expResult = instance.whitespace;
        SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> result = instance.getWhitespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setWhitespace method, of class SimpleContentRestriction.
     */
    @Test
    public void testSetWhitespace() {
        SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> val =
            new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(
                SimpleContentPropertyWhitespace.Replace,
                true
            );
        SymbolTableRef<Type> base = new SymbolTableRef<Type>("Testsymboltableref16");
        SimpleContentRestriction instance = new SimpleContentRestriction(base);
        instance.setWhitespace(val);
        assertEquals(val, instance.whitespace);
    }
}
