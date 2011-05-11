package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.PatternNotInitializedException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class StartProcessor
 * @author Lars Schmidt
 */
public class StartProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class StartProcessor with element.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithElement() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_element.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Element);
    }

    /**
     * Test valid case of class StartProcessor with empty.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithEmpty() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_empty.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Empty);
    }

    /**
     * Test valid case of class StartProcessor with mixed.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithMixed() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_mixed.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Mixed);
    }

    /**
     * Test valid case of class StartProcessor with attribute.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithAttribute() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_attribute.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Attribute);
    }

    /**
     * Test valid case of class StartProcessor with group.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithGroup() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_group.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Group);
    }

    /**
     * Test valid case of class StartProcessor with interleave.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithInterleave() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_interleave.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Interleave);
    }

    /**
     * Test valid case of class StartProcessor with choice.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithChoice() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_choice.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Choice);
    }

    /**
     * Test valid case of class StartProcessor with optional.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithOptional() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_optional.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Optional);
    }

    /**
     * Test valid case of class StartProcessor with zeroOrMore.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithZeroOrMore() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_zeroOrMore.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof ZeroOrMore);
    }

    /**
     * Test valid case of class StartProcessor with oneOrMore.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithOneOrMore() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_oneOrMore.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof OneOrMore);
    }

    /**
     * Test valid case of class StartProcessor with list.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithList() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_list.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof List);
    }

    /**
     * Test valid case of class StartProcessor with ref.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_ref.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Ref);
    }

    /**
     * Test valid case of class StartProcessor with parentRef.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithParentRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_parentRef.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar outerGrammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(outerGrammar.getStartPatterns().getFirst() instanceof Grammar);
        Grammar grammar = (Grammar) outerGrammar.getStartPatterns().getFirst();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof ParentRef);
    }

    /**
     * Test valid case of class StartProcessor with text.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithText() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_text.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Text);
    }

    /**
     * Test valid case of class StartProcessor with value.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithValue() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_value.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Value);
    }

    /**
     * Test valid case of class StartProcessor with data.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithData() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_data.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Data);
    }

    /**
     * Test valid case of class StartProcessor with notAllowed.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithNotAllowed() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_notAllowed.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof NotAllowed);
    }

    /**
     * Test valid case of class StartProcessor with externalRef.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithExternalRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_externalRef.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof ExternalRef);
    }

    /**
     * Test valid case of class StartProcessor with grammar.
     * This case could only be used within another grammar, not as standalone RelaxNG Schema.
     * @throws Exception
     */
    @Test
    public void testValidCaseWithgrammar() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_valid_grammar.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        Iterator<Pattern> it = grammar.getStartPatterns().iterator();

        assertTrue(it.next() instanceof Grammar);
    }

    /**
     * Test invalid case of class StartProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under start-tag was not detected.");
    }

    /**
     * Test invalid case of class StartProcessor, with PatternNotInitializedException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithEmptyPatternException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/startTests/start_empty_content.rng"));
        } catch (PatternNotInitializedException error) {
            return;
        }
        fail("There is no content under a start-tag, but this was not detected.");
    }
}
