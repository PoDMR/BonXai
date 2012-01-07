package eu.fox7.schematoolkit.bonxai.om;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.CountingPattern;
import static org.junit.Assert.*;

/**
 *
 */
public class CountingPatternTest extends junit.framework.TestCase {

    public CountingPatternTest() {
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
     * Test of getMax method, of class CountingPattern.
     */
    @Test
    public void testGetMax() {
        Integer expResult = 10;
        CountingPattern instance = new CountingPattern(5, expResult);
        Integer result = instance.getMax();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMin method, of class CountingPattern.
     */
    @Test
    public void testGetMin() {
        Integer expResult = 10;
        CountingPattern instance = new CountingPattern(expResult, 5);
        Integer result = instance.getMin();
        assertEquals(expResult, result);
    }

}
