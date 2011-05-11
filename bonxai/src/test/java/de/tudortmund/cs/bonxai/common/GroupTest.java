package de.tudortmund.cs.bonxai.common;

import java.util.Hashtable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.common.SequencePattern;

/**
 *
 */
public class GroupTest extends junit.framework.TestCase {

    public GroupTest() {
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
     * Test of getContainer method, of class Group.
     */
    @Test
    public void testGetContainer() {
        ParticleContainer expResult = new SequencePattern();
        Group instance = new Group("{}someGroup", expResult);
        ParticleContainer result = instance.getParticleContainer();
        //Test: is an identical clone returned?
        //assertNotSame(expResult, result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class Group.
     */
    @Test
    public void testGetName() {
        ParticleContainer container = null;
        String expResult = "{}someGroup";
        Group instance = new Group(expResult, container);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testEquals() {
        Group group1 = new Group("{}fooGroup", new SequencePattern());
        Group group2 = new Group("{}fooGroup", new SequencePattern());
        Group group3 = new Group("{}barGroup", new SequencePattern());

        assertTrue(group1.equals(group2));
        assertTrue(group2.equals(group1));

        assertFalse(group1.equals(group3));
        assertFalse(group2.equals(group3));

        assertFalse(group1.equals(null));
        assertFalse(group2.equals(null));
        assertFalse(group3.equals(null));
    }

    @Test
    public void testHashCode() {
        Hashtable<String, Group> table = new Hashtable<String, Group>();

        Group group1 = new Group("{}fooGroup", new SequencePattern());
        Group group2 = new Group("{}fooGroup", new SequencePattern());
        Group group3 = new Group("{}barGroup", new SequencePattern());

        table.put("{}fooGroup", group1);

        assertTrue(table.containsValue(group1));
        assertTrue(table.containsValue(group2));
        assertFalse(table.containsValue(group3));
    }
}
