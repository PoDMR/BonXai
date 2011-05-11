package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class OneOrMoreProcessor
 * @author Lars Schmidt
 */
public class OneOrMoreProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class OneOrMoreProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/oneOrMoreTests/oneOrMore_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar outerGrammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(outerGrammar.getStartPatterns().getFirst() instanceof Grammar);
        Grammar grammar = (Grammar) outerGrammar.getStartPatterns().getFirst();
        assertTrue(grammar.getDefineLookUpTable().getReference("define").getReference().getFirst().getPatterns().getFirst() instanceof OneOrMore);

        OneOrMore oneOrMore = (OneOrMore) grammar.getDefineLookUpTable().getReference("define").getReference().getFirst().getPatterns().getFirst();
        LinkedList<Pattern> temp = oneOrMore.getPatterns();

        Iterator<Pattern> it = oneOrMore.getPatterns().iterator();

        assertTrue(it.next() instanceof Element);
        assertTrue(it.next() instanceof Empty);
        assertTrue(it.next() instanceof Text);
        assertTrue(it.next() instanceof Data);
        assertTrue(it.next() instanceof Value);
        assertTrue(it.next() instanceof List);
        assertTrue(it.next() instanceof Attribute);
        assertTrue(it.next() instanceof Optional);
        assertTrue(it.next() instanceof Mixed);
        assertTrue(it.next() instanceof ZeroOrMore);
        assertTrue(it.next() instanceof ExternalRef);
        assertTrue(it.next() instanceof NotAllowed);
        assertTrue(it.next() instanceof Grammar);
        assertTrue(it.next() instanceof Ref);
        assertTrue(it.next() instanceof ParentRef);
        assertTrue(it.next() instanceof OneOrMore);
        assertTrue(it.next() instanceof Choice);
        assertTrue(it.next() instanceof Group);
        assertTrue(it.next() instanceof Interleave);

    }

    /**
     * Test invalid case of class OneOrMoreProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/oneOrMoreTests/oneOrMore_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under oneOrMore-tag was not detected.");
    }

    /**
     * Test invalid case of class OneOrMoreProcessor, with EmptyPatternException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithEmptyPatternException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/oneOrMoreTests/oneOrMore_empty_content.rng"));
        } catch (EmptyPatternException error) {
            return;
        }
        fail("There is no content under a oneOrMore-tag, but this was not detected.");
    }
}
