
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
public class DataTypeTest extends junit.framework.TestCase {

    public DataTypeTest() {
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
     * Test of getUri method, of class DataType.
     */
    @Test
    public void testGetUri() {
        DataType instance = new DataType("identifier", "uri");
        String expResult = "uri";
        String result = instance.getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class DataType.
     */
    @Test
    public void testGetIdentifier() {
        DataType instance = new DataType("identifier", "uri");
        String expResult = "identifier";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }
}
