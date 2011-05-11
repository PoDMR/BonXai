package de.tudortmund.cs.bonxai.common;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;

/**
 *
 */
public class GroupRefTest extends junit.framework.TestCase {

    public GroupRefTest() {
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
     * Test of getGroup method, of class GroupRef.
     */
    @Test
    public void testGetGroup() {
        ParticleContainer container = new SequencePattern();
        Group expResult = new Group("{}someGroup", container);
        SymbolTableRef<de.tudortmund.cs.bonxai.common.Group> ref = new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("ref", expResult);
        GroupRef instance = new GroupRef(ref);
        de.tudortmund.cs.bonxai.xsd.Group result = (Group) instance.getGroup();
        assertEquals(expResult, result);
    }

}