
package eu.fox7.schematoolkit.bonxai.om;

import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        Vector<AncestorPattern> expResult = new Vector<AncestorPattern>();
        expResult.add(new DoubleSlashPrefixElement("namespace", "name"));
        ContainerParticle instance = new OrExpression(expResult);
        Vector<AncestorPattern> result = instance.getChildren();
        assertEquals(expResult, result);
    }
}
