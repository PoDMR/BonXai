
package de.tudortmund.cs.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.bonxai.DoubleSlashPrefixElement;
import de.tudortmund.cs.bonxai.bonxai.AncestorPatternElement;
import static org.junit.Assert.*;

/**
 *
 */
public class ElementTest extends junit.framework.TestCase {

    public ElementTest() {
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
     * Test of getName method, of class Element.
     */
    @Test
    public void testGetName() {
        AncestorPatternElement instance = new DoubleSlashPrefixElement("namespace", "name");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
