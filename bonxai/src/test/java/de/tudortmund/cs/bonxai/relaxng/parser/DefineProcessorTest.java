package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.CombineMethodNotMatchingException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidCombineMethodException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidNCNameException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of class DefineProcessor
 * @author Lars Schmidt
 */
public class DefineProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class DefineProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/defineTests/define_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar outerGrammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(outerGrammar.getStartPatterns().getFirst() instanceof Grammar);
        Grammar grammar = (Grammar) outerGrammar.getStartPatterns().getFirst();
        assertTrue(grammar.getDefineLookUpTable().getReference("define").getReference().getFirst() instanceof Define);

        Define define = grammar.getDefineLookUpTable().getReference("define").getReference().getFirst();
        LinkedList<Pattern> temp = define.getPatterns();

        Iterator<Pattern> it = define.getPatterns().iterator();

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

        assertEquals("define", define.getName());
        assertEquals(CombineMethod.interleave, define.getCombineMethod());

    }

    /**
     * Test invalid case of class DefineProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/defineTests/define_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under define-tag was not detected.");
    }

    /**
     * Test invalid case of class DefineProcessor, with InvalidNCNameException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidNCNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/defineTests/define_invalid_ncname.rng"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("There is an invalid NCName in a define-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class DefineProcessor, with InvalidCombineMethodException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidCombineMethodException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/defineTests/define_invalid_combine.rng"));
        } catch (InvalidCombineMethodException error) {
            return;
        }
        fail("There is an invalid combine method in a define-tag, but this was not detected.");
    }

    /**
     * Test invalid case of class DefineProcessor, with CombineMethodNotMatchingException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithCombineMethodNotMatchingException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/defineTests/define_invalid_combine_match.rng"));
        } catch (CombineMethodNotMatchingException error) {
            return;
        }
        fail("There are two not matching combine methods for a define-name, but this was not detected.");
    }
}
