
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
public class GroupElementTest extends junit.framework.TestCase {

    public GroupElementTest() {
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
     * Test of getName method, of class BonxaiAbstractGroup.
     */
    @Test
    public void testGetName() {
        BonxaiAbstractGroup instance = new BonxaiAbstractGroup("name");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
