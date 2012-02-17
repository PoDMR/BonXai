
package eu.fox7.schematoolkit.bonxai.om;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;

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
        BonxaiType expResult = new BonxaiType(new QualifiedName(Namespace.EMPTY_NAMESPACE,"type"));
        Attribute instance = new Attribute(new QualifiedName(Namespace.EMPTY_NAMESPACE,"name"), expResult, true);
        BonxaiType result = instance.getType();
        assertEquals(expResult, result);
    }

    @Test
    public void testCtorNameAndType() {
        BonxaiType type = new BonxaiType(new QualifiedName(Namespace.EMPTY_NAMESPACE,"type"));
        Attribute attribute = new Attribute(new QualifiedName(Namespace.EMPTY_NAMESPACE,"name"), type, true);

        assertEquals(type, attribute.getType());
        assertFalse(attribute.isRequired());
    }

    @Test
    public void testCtorNameTypeAndRequired() {
        BonxaiType type = new BonxaiType(new QualifiedName(Namespace.EMPTY_NAMESPACE,"type"));
        Attribute attribute = new Attribute(new QualifiedName(Namespace.EMPTY_NAMESPACE,"name"), type, true);

        assertEquals(type, attribute.getType());
        assertTrue(attribute.isRequired());
    }

    @Test
    public void testSetGetRequired() {
        BonxaiType type = new BonxaiType(new QualifiedName(Namespace.EMPTY_NAMESPACE,"type"));
        Attribute attribute = new Attribute(new QualifiedName(Namespace.EMPTY_NAMESPACE,"name"), type, true);

        assertFalse(attribute.isRequired());
        attribute.setRequired();
        assertTrue(attribute.isRequired());
        attribute.setOptional();
        assertFalse(attribute.isRequired());
    }

}
