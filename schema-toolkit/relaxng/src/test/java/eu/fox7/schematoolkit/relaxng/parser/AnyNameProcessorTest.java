package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.AnyName;
import eu.fox7.schematoolkit.relaxng.om.Element;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.NsName;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGRootProcessor;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultipleExceptException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AnyNameProcessor
 * @author Lars Schmidt
 */
public class AnyNameProcessorTest extends junit.framework.TestCase {

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
     * Test of processNode method, of class AnyNameProcessor.
     * @throws Exception
     */
    @Test
    public void testProcessNode() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/anyNameTests/anyName_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        // check for valid anyName...
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);

        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        assertTrue(((Element) grammar.getStartPatterns().getFirst()).getNameClass() instanceof AnyName);

        AnyName anyName = (AnyName) ((Element) grammar.getStartPatterns().getFirst()).getNameClass();
        assertEquals(2, anyName.getExceptNames().size());
        assertTrue(anyName.getExceptNames().iterator().next() instanceof NsName);
    }

    /**
     * Test invalid case of class AnyNameProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/anyNameTests/anyName_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("There is an unsupported content in this anyName, but it was not detected.");
    }

    /**
     * Test invalid case of class AnyNameProcessor, with PatternReferencedButNotDefinedException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithPatternReferencedButNotDefinedException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/anyNameTests/anyName_invalid_multiExcept.rng"));
        } catch (MultipleExceptException error) {
            return;
        }
        fail("There are more than one except tags under this anyName, but this was not detected.");
    }
}
