
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
public class BonxaiTypeTest extends junit.framework.TestCase {

    public BonxaiTypeTest() {
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
     * Test of getType method, of class BonxaiType.
     */
    @Test
    public void testGetType() {
        BonxaiType instance = new BonxaiType("http://www.w3.org/2001/XMLSchema", "type");
        String expResult = "type";
        String result = instance.getType();
        assertEquals(expResult, result);
    }
}
