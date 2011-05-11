
package de.tudortmund.cs.bonxai.common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import static org.junit.Assert.*;

/**
 *
 */
public class IdentifiedNamespaceTest extends junit.framework.TestCase {

    public IdentifiedNamespaceTest() {
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
     * Test of getUri method, of class IdentifiedNamespace.
     */
    @Test
    public void testGetUri() {
        IdentifiedNamespace instance = new IdentifiedNamespace("identifier", "uri");
        String expResult = "uri";
        String result = instance.getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class IdentifiedNamespace.
     */
    @Test
    public void testGetIdentifier() {
        IdentifiedNamespace instance = new IdentifiedNamespace("identifier", "uri");
        String expResult = "identifier";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }
}
