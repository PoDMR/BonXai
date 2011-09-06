
package eu.fox7.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.AttributeGroupElement;
import eu.fox7.bonxai.bonxai.AttributePattern;
import static org.junit.Assert.*;

/**
 *
 */
public class AttributeGroupElementTest extends junit.framework.TestCase {

    public AttributeGroupElementTest() {
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
     * Test of getAttributePattern method, of class AttributeGroupElement.
     */
    @Test
    public void testGetAttributePattern() {
        AttributePattern expResult = new AttributePattern();
        AttributeGroupElement instance = new AttributeGroupElement("name", expResult);
        AttributePattern result = instance.getAttributePattern();
        assertEquals(expResult, result);
    }
}
