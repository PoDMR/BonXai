
package eu.fox7.bonxai.common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.DataType;
import eu.fox7.bonxai.common.DualHashtable;
import eu.fox7.bonxai.common.DualHashtableElement;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import static org.junit.Assert.*;

/**
 *
 */
public class DualHashtableTest extends junit.framework.TestCase {

    public DualHashtableTest() {
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
     * Test of getByIdentifier method, of class DualHashtable.
     */
    @Test
    public void testGetByIdentifier() {
        DualHashtableElement dualHashtableElement = new DataType("identifier", "uri");
        DualHashtable instance = new DualHashtable();
        instance.addElement(dualHashtableElement);
        String expResult = "identifier";
        String result = instance.getByUri("uri");
        assertEquals(expResult, result);
    }

    /**
     * Test of getByUri method, of class DualHashtable.
     */
    @Test
    public void testGetByUri() {
        DualHashtableElement dualHashtableElement = new DataType("identifier", "uri");
        DualHashtable instance = new DualHashtable();
        instance.addElement(dualHashtableElement);
        String expResult = "uri";
        String result = instance.getByIdentifier("identifier");
        assertEquals(expResult, result);
    }

    /**
     * Test of addElement method, of class DualHashtable.
     */
    @Test
    public void testAddElement() {
        DualHashtableElement dualHashtableElement = new DataType("identifier", "uri");
        DualHashtable instance = new DualHashtable();
        instance.addElement(dualHashtableElement);
        String expResult = "identifier";
        String result = instance.getByUri("uri");
        assertEquals(expResult, result);
        expResult = "uri";
        result = instance.getByIdentifier("identifier");
        assertEquals(expResult, result);
    }

    /**
     * Test of containsUri method, of class DualHashtable.
     */
    @Test
    public void testContainsUri() {
        DualHashtableElement dualHashtableElement = new DataType("identifier", "uri");
        DualHashtable instance = new DualHashtable();
        instance.addElement(dualHashtableElement);
        boolean result = instance.containsUri("uri");
        assertTrue(result);
    }

    /**
     * Test of containsIdentifier method, of class DualHashtable.
     */
    @Test
    public void testContainsIdentifier() {
        DualHashtableElement dualHashtableElement = new DataType("identifier", "uri");
        DualHashtable instance = new DualHashtable();
        instance.addElement(dualHashtableElement);
        boolean result = instance.containsIdentifier("identifier");
        assertTrue(result);
    }

    public void testGetUrisOrder() {
        for (int i = 0; i < 42; i++) {
            DualHashtable table = generateDualHashtable();

            int j = 0;
            for (String uri : table.getUris()) {
                switch (j) {
                    case 0:
                        assertEquals("Run " + j, "http://example.com/aaa", uri);
                        break;
                    case 1:
                        assertEquals("Run " + j, "http://example.com/bbb", uri);
                        break;
                    case 2:
                        assertEquals("Run " + j, "http://example.com/ggg", uri);
                        break;
                    case 3:
                        assertEquals("Run " + j, "http://example.com/qqq", uri);
                        break;
                    case 4:
                        assertEquals("Run " + j, "http://example.com/uuu", uri);
                        break;
                    case 5:
                        assertEquals("Run " + j, "http://example.com/zzz", uri);
                        break;
                }
                j++;
            }
        }
    }

    public DualHashtable generateDualHashtable() {
        DualHashtable table = new DualHashtable();

        table.addElement(new IdentifiedNamespace("aaa", "http://example.com/aaa"));
        table.addElement(new IdentifiedNamespace("zzz", "http://example.com/zzz"));
        table.addElement(new IdentifiedNamespace("ggg", "http://example.com/ggg"));
        table.addElement(new IdentifiedNamespace("uuu", "http://example.com/uuu"));
        table.addElement(new IdentifiedNamespace("qqq", "http://example.com/qqq"));
        table.addElement(new IdentifiedNamespace("bbb", "http://example.com/bbb"));

        return table;
    }
}
