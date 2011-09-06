package eu.fox7.bonxai.common;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.Group;

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
        SymbolTableRef<eu.fox7.bonxai.common.Group> ref = new SymbolTableRef<eu.fox7.bonxai.common.Group>("ref", expResult);
        GroupRef instance = new GroupRef(ref);
        eu.fox7.bonxai.xsd.Group result = (Group) instance.getGroup();
        assertEquals(expResult, result);
    }

}