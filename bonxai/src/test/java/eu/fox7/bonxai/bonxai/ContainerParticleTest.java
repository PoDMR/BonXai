
package eu.fox7.bonxai.bonxai;

import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.AncestorPatternParticle;
import eu.fox7.bonxai.bonxai.ContainerParticle;
import eu.fox7.bonxai.bonxai.DoubleSlashPrefixElement;
import eu.fox7.bonxai.bonxai.OrExpression;
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
