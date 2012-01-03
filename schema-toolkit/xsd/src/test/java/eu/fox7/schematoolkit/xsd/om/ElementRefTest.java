package eu.fox7.schematoolkit.xsd.om;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.ElementRef;

/**
 *
 */
public class ElementRefTest extends junit.framework.TestCase {

    public ElementRefTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getElement method, of class ElementRef.
     */
    @Test
    public void testGetElement() {
        Element expResult = new Element("{}someElement");
        SymbolTableRef<Element> ref = new SymbolTableRef<Element>("someKey", expResult);
        ElementRef instance = new ElementRef(ref);
        Element result = instance.getElement();
        assertEquals(expResult, result);
    }

}
