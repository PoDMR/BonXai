package de.tudortmund.cs.bonxai.common;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;

/**
 *
 */
public class AnyPatternTest extends junit.framework.TestCase {

    public AnyPatternTest() {
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
     * Test of getNamespace method, of class AnyPattern.
     */
    @Test
    public void testGetNamespace() {
        AnyPattern anypattern = new AnyPattern(null, "someNamespace");
        String expResult = "someNamespace";
        String result = anypattern.getNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getProcessContentsInstruction method, of class AnyPattern.
     */
    @Test
    public void testGetProcessContentsInstruction() {
        AnyPattern anypattern = new AnyPattern(ProcessContentsInstruction.Lax, "someNamespace");
        ProcessContentsInstruction expResult = ProcessContentsInstruction.Lax;
        ProcessContentsInstruction result = anypattern.getProcessContentsInstruction();
        assertEquals(expResult, result);
    }

}