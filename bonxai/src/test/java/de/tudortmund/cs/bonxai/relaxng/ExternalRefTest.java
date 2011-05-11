package de.tudortmund.cs.bonxai.relaxng;

import org.junit.Test;

/**
 * Test of class ExternalRef
 * @author Lars Schmidt
 */
public class ExternalRefTest extends junit.framework.TestCase {

    /**
     * Test of getHref method, of class ExternalRef.
     */
    @Test
    public void testGetHref() {
        ExternalRef instance = new ExternalRef("http://www.myDomain.org/rng.rng");
        String expResult = "http://www.myDomain.org/rng.rng";
        String result = instance.getHref();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHref method, of class ExternalRef.
     */
    @Test
    public void testSetHref() {
        ExternalRef instance = new ExternalRef("http://www.myDomain.org/rng.rng");
        instance.setHref("http://www.someOtherDomain.com/rng.rng");
        String expResult = "http://www.myDomain.org/rng.rng";
        String result = instance.getHref();
        assertEquals("http://www.someOtherDomain.com/rng.rng", result);
    }

}