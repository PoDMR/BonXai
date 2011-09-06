
package eu.fox7.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.DataTypeList;
import eu.fox7.bonxai.bonxai.Declaration;
import eu.fox7.bonxai.bonxai.ImportList;
import eu.fox7.bonxai.common.DefaultNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import static org.junit.Assert.*;

/**
 *
 */
public class DeclarationTest extends junit.framework.TestCase {

    public DeclarationTest() {
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
     * Test of getImportList method, of class Declaration.
     */
    @Test
    public void testGetImportList() {
        ImportList expResult = new ImportList();
        DataTypeList dataTypeList = new DataTypeList();
        NamespaceList namespaceList = new NamespaceList(new DefaultNamespace("uri"));
        Declaration instance = new Declaration(expResult, dataTypeList, namespaceList);
        ImportList result = instance.getImportList();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDataTypeList method, of class Declaration.
     */
    @Test
    public void testGetDataTypeList() {
        ImportList importList = new ImportList();
        DataTypeList expResult = new DataTypeList();
        NamespaceList namespaceList = new NamespaceList(new DefaultNamespace("uri"));
        Declaration instance = new Declaration(importList, expResult, namespaceList);
        DataTypeList result = instance.getDataTypeList();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespaceList method, of class Declaration.
     */
    @Test
    public void testGetNamespaceList() {
        ImportList importList = new ImportList();
        DataTypeList dataTypeList = new DataTypeList();
        NamespaceList expResult = new NamespaceList(new DefaultNamespace("uri"));
        Declaration instance = new Declaration(importList, dataTypeList, expResult);
        NamespaceList result = instance.getNamespaceList();
        assertEquals(expResult, result);
    }
}
