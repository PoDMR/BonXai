package eu.fox7.bonxai.xsd;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.Key;
import eu.fox7.bonxai.xsd.KeyRef;
import eu.fox7.bonxai.xsd.SimpleConstraint;

/**
 *
 */
public class KeyRefTest extends junit.framework.TestCase {

    public KeyRefTest() {
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
     * Test of getKeyOrUnique method, of class KeyRef.
     */
    @Test
    public void testGetKey() {
        Key expResult = new Key("{}someKey", "someKeySelector");
        SymbolTableRef<SimpleConstraint> ref = new SymbolTableRef<SimpleConstraint> ("SymbolTableKeyRef", expResult);
        KeyRef keyref = new KeyRef("someKey", "someSelector", ref);
        Key result = (Key) keyref.getKeyOrUnique();
        assertEquals(expResult, result);
    }

}