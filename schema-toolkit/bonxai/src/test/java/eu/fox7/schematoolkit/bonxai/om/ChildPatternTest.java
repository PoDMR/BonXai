
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
public class ChildPatternTest extends junit.framework.TestCase {

    public ChildPatternTest() {
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
     * Test of getAttributePattern method, of class ChildPattern.
     */
    @Test
    public void testGetAttributePattern() {
        AttributePattern expResult = new AttributePattern();
        ChildPattern instance = new ChildPattern(expResult, new ElementPattern(new BonxaiType("", "type"), true));
        AttributePattern result = instance.getAttributePattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of getElementPattern method, of class ChildPattern.
     */
    @Test
    public void testGetElementPattern() {
        ElementPattern expResult = new ElementPattern(new BonxaiType("", "type"), true);
        ChildPattern instance = new ChildPattern(new AttributePattern(), expResult);
        ElementPattern result = instance.getElementPattern();
        assertEquals(expResult, result);
    }
}
