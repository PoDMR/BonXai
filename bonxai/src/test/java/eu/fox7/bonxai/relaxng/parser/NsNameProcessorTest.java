package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.relaxng.parser.RNGRootProcessor;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class NsNameProcessor
 * @author Lars Schmidt
 */
public class NsNameProcessorTest extends junit.framework.TestCase {

    // Relax NG Schema for this testcase
    RelaxNGSchema rngSchema;
    RNGRootProcessor rngRootProcessor;

    @Before
    @Override
    public void setUp() {
        rngSchema = new RelaxNGSchema();
        rngRootProcessor = new RNGRootProcessor(rngSchema);
    }

    /**
     * Test valid case of class NsNameProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/nsNameTests/nsName_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        NsName nsName = (NsName) ((Element) grammar.getStartPatterns().getFirst()).getNameClass();

        assertEquals(1, nsName.getExceptNames().size());
        assertEquals("http://www.example.org", nsName.getAttributeNamespace());
    }
}
