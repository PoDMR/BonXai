
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
public class DataTypeListTest extends junit.framework.TestCase {

    public DataTypeListTest() {
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
     * Test of addDataType method, of class DataTypeList.
     */
    @Test
    public void testAddDataType() {
        DataType expResult = new DataType("identifier", "uri");
        DataTypeList instance = new DataTypeList();
        instance.addDataType(expResult);
        DataType result = instance.getDataTypeByIdentifier("identifier");
        assertEquals(expResult.getIdentifier(), result.getIdentifier());
        assertEquals(expResult.getUri(), result.getUri());
    }

    /**
     * Test of getDataTypeByIdentifier method, of class DataTypeList.
     */
    @Test
    public void testGetDataTypeByIdentifier() {
        DataType expResult = new DataType("identifier", "uri");
        DataTypeList instance = new DataTypeList();
        instance.addDataType(expResult);
        DataType result = instance.getDataTypeByIdentifier("identifier");
        assertEquals(expResult.getIdentifier(), result.getIdentifier());
        assertEquals(expResult.getUri(), result.getUri());
    }

    /**
     * Test of getDataTypeByUri method, of class DataTypeList.
     */
    @Test
    public void testGetDataTypeByUri() {
        DataType expResult = new DataType("identifier", "uri");
        DataTypeList instance = new DataTypeList();
        instance.addDataType(expResult);
        DataType result = instance.getDataTypeByUri("uri");
        assertEquals(expResult.getIdentifier(), result.getIdentifier());
        assertEquals(expResult.getUri(), result.getUri());
    }
}
