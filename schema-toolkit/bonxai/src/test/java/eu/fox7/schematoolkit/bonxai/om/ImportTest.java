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
public class ImportTest extends junit.framework.TestCase {

    public ImportTest() {
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
     * Test of getUri method, of class Import.
     */
    @Test
    public void testGetUri() {
        Import instance = new Import("uri");
        String expResult = "uri";
        String result = instance.getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUrl method, of class Import.
     */
    @Test
    public void testGetUrl() {
        Import instance = new Import("url", "uri");
        String expResult = "url";
        String result = instance.getUrl();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class Import.
     */
    @Test
    public void testGetIdentifier() {
        Import instance = new Import("url", "uri");
        String expResult = "url";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }
}
