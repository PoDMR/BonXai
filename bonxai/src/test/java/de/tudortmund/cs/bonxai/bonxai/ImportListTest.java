
package de.tudortmund.cs.bonxai.bonxai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.common.DualHashtable;
import de.tudortmund.cs.bonxai.bonxai.Import;
import de.tudortmund.cs.bonxai.bonxai.ImportList;
import static org.junit.Assert.*;

/**
 *
 */
public class ImportListTest extends junit.framework.TestCase {

    public ImportListTest() {
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
     * Test of addImport method, of class ImportList.
     */
    @Test
    public void testAddImport() {
        Import expResult = new Import("url", "uri");
        ImportList instance = new ImportList();
        instance.addImport(expResult);
        Import result = instance.getImportByUri("uri");
        assertEquals(expResult.getUri(), result.getUri());
        assertEquals(expResult.getUrl(), result.getUrl());
    }

    /**
     * Test of getImportByUri method, of class ImportList.
     */
    @Test
    public void testGetImportByUri() {
        Import expResult = new Import("url", "uri");
        ImportList instance = new ImportList();
        instance.addImport(expResult);
        Import result = instance.getImportByUri("uri");
        assertEquals(expResult.getUri(), result.getUri());
        assertEquals(expResult.getUrl(), result.getUrl());
    }

    /**
     * Test of getImportByUrl method, of class ImportList.
     */
    @Test
    public void testGetImportByUrl() {
        Import expResult = new Import("url", "uri");
        ImportList instance = new ImportList();
        instance.addImport(expResult);
        Import result = instance.getImportByUrl("url");
        assertEquals(expResult.getUri(), result.getUri());
        assertEquals(expResult.getUrl(), result.getUrl());
    }

    /**
     * Test of getImportList method, of class ImportList.
     */
    @Test
    public void testGetImportList() {
        ImportList instance = new ImportList();
        DualHashtable expResult = new DualHashtable();
        expResult.addElement(new Import("uri"));
        expResult.addElement(new Import("hohoh"));
        instance.setImportList(expResult);
        DualHashtable result = instance.getImportList();
        assertEquals(expResult, result);
    }

    /**
     * Test of setImportList method, of class ImportList.
     */
    @Test
    public void testSetImportList() {
        ImportList instance = new ImportList();
        DualHashtable expResult = new DualHashtable();
        expResult.addElement(new Import("uri"));
        instance.setImportList(expResult);
        DualHashtable result = instance.getImportList();
        assertEquals(expResult, result);
    }
}
