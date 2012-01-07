
package eu.fox7.schematoolkit.bonxai.om;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.*;
import eu.fox7.schematoolkit.common.*;
import static org.junit.Assert.*;

/**
 *
 */
public class ElementGroupElementTest extends junit.framework.TestCase {

    public ElementGroupElementTest() {
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
     * Test of getParticle method, of class BonxaiGroup.
     */
    @Test
    public void testGetParticle() {
        SequencePattern pattern = new SequencePattern();
        BonxaiGroup instance = new BonxaiGroup("name", pattern);
        Particle result = instance.getParticleContainer();
        assertSame(pattern, result);
    }
}
