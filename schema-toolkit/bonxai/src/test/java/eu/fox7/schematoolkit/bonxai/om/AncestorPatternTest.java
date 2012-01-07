
package eu.fox7.schematoolkit.bonxai.om;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class AncestorPatternTest extends junit.framework.TestCase {

    public AncestorPatternTest() {
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
     * Test of getParticle method, of class AncestorPattern.
     */
    @Test
    public void testGetParticle() {
        DoubleSlashPrefixElement doubleSlashPrefixElement = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern instance = new AncestorPattern(doubleSlashPrefixElement);
        AncestorPattern expResult = doubleSlashPrefixElement;
        AncestorPattern result = instance.getParticle();
        assertEquals(expResult, result);
    }
}
