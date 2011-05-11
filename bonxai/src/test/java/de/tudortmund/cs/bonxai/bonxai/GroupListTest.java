
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.bonxai.GroupElement;
import de.tudortmund.cs.bonxai.bonxai.GroupList;
import static org.junit.Assert.*;

/**
 *
 */
public class GroupListTest extends junit.framework.TestCase {

    public GroupListTest() {
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
     * Test of getGroupElements method, of class GroupList.
     */
    @Test
    public void testGetGroupElements() {
        GroupList instance = new GroupList();
        GroupElement groupElement = new GroupElement("name");
        instance.addGroupElement(groupElement);
        Vector<GroupElement> expResult = new Vector<GroupElement>();
        expResult.add(groupElement);
        Vector<GroupElement> result = instance.getGroupElements();
        assertEquals(expResult, result);
    }

    /**
     * Test of addGroupElement method, of class GroupList.
     */
    @Test
    public void testAddGroupElement() {
        GroupList instance = new GroupList();
        GroupElement groupElement = new GroupElement("name");
        instance.addGroupElement(groupElement);
        Vector<GroupElement> expResult = new Vector<GroupElement>();
        expResult.add(groupElement);
        Vector<GroupElement> result = instance.getGroupElements();
        assertEquals(expResult, result);
    }
}
