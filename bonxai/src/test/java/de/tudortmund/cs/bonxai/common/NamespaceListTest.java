
package de.tudortmund.cs.bonxai.common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import static org.junit.Assert.*;

/**
 *
 */
public class NamespaceListTest extends junit.framework.TestCase {

    public NamespaceListTest() {
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
     * Test of getDefaultNamespace method, of class NamespaceList.
     */
    @Test
    public void testGetDefaultNamespace() {
        DefaultNamespace expResult = new DefaultNamespace("uri");
        NamespaceList instance = new NamespaceList(expResult);
        DefaultNamespace result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of addIdentifiedNamespace method, of class NamespaceList.
     */
    @Test
    public void testAddIdentifiedNamespace() {
        IdentifiedNamespace expResult = new IdentifiedNamespace("identifier", "uri");
        NamespaceList instance = new NamespaceList(new DefaultNamespace("uri"));
        instance.addIdentifiedNamespace(expResult);
        IdentifiedNamespace result = instance.getNamespaceByIdentifier("identifier");
        assertEquals(expResult.getIdentifier(), result.getIdentifier());
        assertEquals(expResult.getUri(), result.getUri());
    }

    /**
     * Test of getNamespaceByIdentifier method, of class NamespaceList.
     */
    @Test
    public void testGetNamespaceByIdentifier() {
        NamespaceList instance = new NamespaceList(new DefaultNamespace("uri"));
        IdentifiedNamespace expResult = new IdentifiedNamespace("identifier", "uri");
        instance.addIdentifiedNamespace(expResult);
        IdentifiedNamespace result = instance.getNamespaceByIdentifier("identifier");
        assertEquals(expResult.getIdentifier(), result.getIdentifier());
        assertEquals(expResult.getUri(), result.getUri());
    }

    /**
     * Test of getNamespaceByUri method, of class NamespaceList.
     */
    @Test
    public void testGetNamespaceByUri() {
        NamespaceList instance = new NamespaceList(new DefaultNamespace("uri"));
        IdentifiedNamespace expResult = new IdentifiedNamespace("identifier", "uri");
        instance.addIdentifiedNamespace(expResult);
        IdentifiedNamespace result = instance.getNamespaceByUri("uri");
        assertEquals(expResult.getIdentifier(), result.getIdentifier());
        assertEquals(expResult.getUri(), result.getUri());
    }

}
