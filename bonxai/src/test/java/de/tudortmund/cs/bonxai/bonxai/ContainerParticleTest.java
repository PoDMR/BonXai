
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.bonxai.AncestorPatternParticle;
import de.tudortmund.cs.bonxai.bonxai.ContainerParticle;
import de.tudortmund.cs.bonxai.bonxai.DoubleSlashPrefixElement;
import de.tudortmund.cs.bonxai.bonxai.OrExpression;
import static org.junit.Assert.*;

/**
 *
 */
public class ContainerParticleTest extends junit.framework.TestCase {

    public ContainerParticleTest() {
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
     * Test of getChildren method, of class ContainerParticle.
     */
    @Test
    public void testGetChildren() {
        Vector<AncestorPatternParticle> expResult = new Vector<AncestorPatternParticle>();
        expResult.add(new DoubleSlashPrefixElement("namespace", "name"));
        ContainerParticle instance = new OrExpression(expResult);
        Vector<AncestorPatternParticle> result = instance.getChildren();
        assertEquals(expResult, result);
    }
}
