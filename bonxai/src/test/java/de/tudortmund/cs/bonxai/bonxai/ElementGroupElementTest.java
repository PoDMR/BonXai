
package de.tudortmund.cs.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
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
     * Test of getParticle method, of class ElementGroupElement.
     */
    @Test
    public void testGetParticle() {
        SequencePattern pattern = new SequencePattern();
        ElementGroupElement instance = new ElementGroupElement("name", pattern);
        Particle result = instance.getParticleContainer();
        assertSame(pattern, result);
    }
}
