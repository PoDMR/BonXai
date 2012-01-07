package eu.fox7.schematoolkit.bonxai.om;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import static org.junit.Assert.*;

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
        String result = anypattern.getNamespaces().iterator().next().getUri();
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
