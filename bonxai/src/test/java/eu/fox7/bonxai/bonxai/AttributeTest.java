
package eu.fox7.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.Attribute;
import eu.fox7.bonxai.bonxai.BonxaiType;
import static org.junit.Assert.*;

/**
 *
 */
public class AttributeTest extends junit.framework.TestCase {

    public AttributeTest() {
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
     * Test of getType method, of class Attribute.
     */
    @Test
    public void testGetType() {
        BonxaiType expResult = new BonxaiType("", "type");
        Attribute instance = new Attribute("", "name", expResult);
        BonxaiType result = instance.getType();
        assertEquals(expResult, result);
    }

    @Test
    public void testCtorNameAndType() {
        BonxaiType type = new BonxaiType("", "type");
        Attribute attribute = new Attribute("", "name", type);

        assertEquals(type, attribute.getType());
        assertFalse(attribute.isRequired());
    }

    @Test
    public void testCtorNameTypeAndRequired() {
        BonxaiType type = new BonxaiType("", "type");
        Attribute attribute = new Attribute("", "name", type, true);

        assertEquals(type, attribute.getType());
        assertTrue(attribute.isRequired());
    }

    @Test
    public void testSetGetRequired() {
        BonxaiType type = new BonxaiType("", "type");
        Attribute attribute = new Attribute("", "name", type);

        assertFalse(attribute.isRequired());
        attribute.setRequired();
        assertTrue(attribute.isRequired());
        attribute.setOptional();
        assertFalse(attribute.isRequired());
    }

    @Test
    public void testSetGetDefault() {
        BonxaiType type = new BonxaiType("", "type");
        Attribute attribute = new Attribute("", "name", type);

        assertNull(attribute.getDefault());

        attribute.setDefault("default");

        assertEquals("default", attribute.getDefault());

        attribute.setDefault(null);

        assertNull(attribute.getDefault());
    }

    @Test
    public void testSetGetFixed() {
        BonxaiType type = new BonxaiType("", "type");
        Attribute attribute = new Attribute("", "name", type);

        assertNull(attribute.getFixed());

        attribute.setFixed("fixed");

        assertEquals("fixed", attribute.getFixed());

        attribute.setFixed(null);

        assertNull(attribute.getFixed());
    }
}
