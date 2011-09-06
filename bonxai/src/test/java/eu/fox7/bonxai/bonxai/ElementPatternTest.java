
package eu.fox7.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.BonxaiType;
import eu.fox7.bonxai.bonxai.ElementPattern;
import eu.fox7.bonxai.common.Particle;
import static org.junit.Assert.*;

/**
 *
 */
public class ElementPatternTest extends junit.framework.TestCase {

    public ElementPatternTest() {
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
     * Test of getBonxaiType method, of class ElementPattern.
     */
    @Test
    public void testGetBonxaiType() {
        BonxaiType expResult = new BonxaiType("", "type");
        ElementPattern instance = new ElementPattern(expResult, true);
        BonxaiType result = instance.getBonxaiType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRegexp method, of class ElementPattern.
     */
    @Test
    public void testGetRegexp() {
        Particle expResult = new eu.fox7.bonxai.xsd.Element("{}name");
        ElementPattern instance = new ElementPattern(expResult);
        Particle result = instance.getRegexp();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetMixedDefault() {
        ElementPattern instance = new ElementPattern(new BonxaiType("", "type"));
        assertFalse(instance.isMixed());
    }

    @Test
    public void testsetMixed() {
        ElementPattern instance = new ElementPattern(new BonxaiType("", "type"));
        instance.setMixed(true);
        assertTrue(instance.isMixed());
    }
}
