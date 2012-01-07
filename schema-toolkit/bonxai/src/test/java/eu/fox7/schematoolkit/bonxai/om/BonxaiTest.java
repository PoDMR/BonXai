
package eu.fox7.schematoolkit.bonxai.om;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import static org.junit.Assert.*;

/**
 *
 */
public class BonxaiTest extends junit.framework.TestCase {

    public BonxaiTest() {
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
     * Test of getConstraintList method, of class Bonxai.
     */
    @Test
    public void testGetConstraintList() {
        Bonxai instance = new Bonxai();
        ConstraintList expResult = new ConstraintList();
        instance.setConstraintList(expResult);
        ConstraintList result = instance.getConstraintList();
        assertEquals(expResult, result);
    }

    /**
     * Test of setConstraintList method, of class Bonxai.
     */
    @Test
    public void testSetConstraintList() {
        Bonxai instance = new Bonxai();
        ConstraintList expResult = new ConstraintList();
        instance.setConstraintList(expResult);
        ConstraintList result = instance.getConstraintList();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDeclaration method, of class Bonxai.
     */
    @Test
    public void testGetDeclaration() {
        Bonxai instance = new Bonxai();
        ImportList importList = new ImportList();
        DataTypeList dataTypeList = new DataTypeList();
        NamespaceList namespaceList = new NamespaceList(new DefaultNamespace("uri"));
        Declaration expResult = new Declaration(importList, dataTypeList, namespaceList);
        instance.setDeclaration(expResult);
        Declaration result = instance.getDeclaration();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDeclaration method, of class Bonxai.
     */
    @Test
    public void testSetDeclaration() {
        Bonxai instance = new Bonxai();
        ImportList importList = new ImportList();
        DataTypeList dataTypeList = new DataTypeList();
        NamespaceList namespaceList = new NamespaceList(new DefaultNamespace("uri"));
        Declaration expResult = new Declaration(importList, dataTypeList, namespaceList);
        instance.setDeclaration(expResult);
        Declaration result = instance.getDeclaration();
        assertEquals(expResult, result);
    }

    /**
     * Test of getGrammarList method, of class Bonxai.
     */
    @Test
    public void testGetGrammarList() {
        Bonxai instance = new Bonxai();
        GrammarList expResult = new GrammarList();
        instance.setGrammarList(expResult);
        GrammarList result = instance.getGrammarList();
        assertEquals(expResult, result);
    }

    /**
     * Test of setGrammarList method, of class Bonxai.
     */
    @Test
    public void testSetGrammarList() {
        Bonxai instance = new Bonxai();
        GrammarList expResult = new GrammarList();
        instance.setGrammarList(expResult);
        GrammarList result = instance.getGrammarList();
        assertEquals(expResult, result);
    }

    /**
     * Test of getGroupList method, of class Bonxai.
     */
    @Test
    public void testGetGroupList() {
        Bonxai instance = new Bonxai();
        GroupList expResult = new GroupList();
        instance.setGroupList(expResult);
        GroupList result = instance.getGroupList();
        assertEquals(expResult, result);
    }

    /**
     * Test of setGroupList method, of class Bonxai.
     */
    @Test
    public void testSetGroupList() {
        Bonxai instance = new Bonxai();
        GroupList expResult = new GroupList();
        instance.setGroupList(expResult);
        GroupList result = instance.getGroupList();
        assertEquals(expResult, result);
    }
}
