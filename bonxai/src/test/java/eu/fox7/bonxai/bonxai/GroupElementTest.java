
package eu.fox7.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.GroupElement;
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
     * Test of getName method, of class GroupElement.
     */
    @Test
    public void testGetName() {
        GroupElement instance = new GroupElement("name");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
