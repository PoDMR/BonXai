package eu.fox7.schematoolkit.xsd.om;


import org.junit.Test;

import eu.fox7.schematoolkit.xsd.om.SimpleContentFixableRestrictionProperty;


public class SimpleContentFixableRestrictionPropertyTest extends junit.framework.TestCase {


    public SimpleContentFixableRestrictionPropertyTest() {
    }

    /**
     * Test of getFixed method, of class SimpleContentFixableRestrictionProperty.
     */
    @Test
    public void testGetFixed() {
        SimpleContentFixableRestrictionProperty<String> instance = new SimpleContentFixableRestrictionProperty<String>("Test", true);
        Boolean expResult = true;
        Boolean result = instance.getFixed();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFixed method, of class SimpleContentFixableRestrictionProperty.
     */
    @Test
    public void testSetFixed() {
        SimpleContentFixableRestrictionProperty<String> instance = new SimpleContentFixableRestrictionProperty<String>("Test", true);
        Boolean expResult = true;
        instance.setFixed(true);
        Boolean result = instance.getFixed();
        assertEquals(expResult, result);
    }

}
