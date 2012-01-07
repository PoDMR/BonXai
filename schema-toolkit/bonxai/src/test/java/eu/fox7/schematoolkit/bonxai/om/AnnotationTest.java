
package eu.fox7.schematoolkit.bonxai.om;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class AnnotationTest extends junit.framework.TestCase {

    public AnnotationTest() {
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
     * Test of getKey method, of class Annotation.
     */
    @Test
    public void testGetKey() {
        Annotation instance = new Annotation("key","value");
        String expResult = "key";
        String result = instance.getKey();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class Annotation.
     */
    @Test
    public void testGetValue() {
        Annotation instance = new Annotation("key","value");
        String expResult = "value";
        String result = instance.getValue();
        assertEquals(expResult, result);
    }

}
