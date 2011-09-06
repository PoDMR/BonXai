
package eu.fox7.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.AncestorPattern;
import eu.fox7.bonxai.bonxai.AncestorPatternParticle;
import eu.fox7.bonxai.bonxai.DoubleSlashPrefixElement;
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
        AncestorPatternParticle expResult = doubleSlashPrefixElement;
        AncestorPatternParticle result = instance.getParticle();
        assertEquals(expResult, result);
    }
}
