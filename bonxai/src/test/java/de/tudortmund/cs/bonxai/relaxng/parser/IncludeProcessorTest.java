package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidAnyUriException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class IncludeProcessor
 * @author Lars Schmidt
 */
public class IncludeProcessorTest extends junit.framework.TestCase {
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
     * Test valid case of class IncludeProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/includeTests/include_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        IncludeContent includeContent = grammar.getIncludeContents().getFirst();
        assertEquals(1, includeContent.getDefineLookUpTable().getAllReferencedObjects().size());
        assertEquals(1, includeContent.getDefineLookUpTable().getReferences().size());
        assertEquals(1, includeContent.getDefinedPatternNames().size());
        assertEquals(1, includeContent.getStartPatterns().size());
        assertEquals("namespaces.rng", includeContent.getHref());
    }

    /**
     * Test invalid case of class IncludeProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/includeTests/include_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under include-tag was not detected.");
    }

    /**
     * Test invalid case of class IncludeProcessor, with InvalidAnyUriException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidAnyUriException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/includeTests/include_invalid_anyURI.rng"));
        } catch (InvalidAnyUriException error) {
            return;
        }
        fail("Invalid anyURI in href-attribute of include-tag was not detected.");
    }
}
