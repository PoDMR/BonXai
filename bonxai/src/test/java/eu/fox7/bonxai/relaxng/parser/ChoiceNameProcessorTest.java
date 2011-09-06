package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.relaxng.parser.RNGRootProcessor;
import eu.fox7.bonxai.relaxng.parser.exceptions.EmptyNameClassException;
import eu.fox7.bonxai.relaxng.parser.exceptions.UnsupportedContentException;

import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ChoiceNameProcessor
 * @author Lars Schmidt
 */
public class ChoiceNameProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class ChoiceNameProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/choiceNameTests/choiceName_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Element);
        Element element = (Element) grammar.getStartPatterns().getFirst();
        assertTrue(element.getPatterns().getFirst() instanceof OneOrMore);
        OneOrMore oneOrMore = (OneOrMore) element.getPatterns().getFirst();
        assertTrue(oneOrMore.getPatterns().getFirst() instanceof Attribute);
        Attribute attribute = (Attribute) oneOrMore.getPatterns().getFirst();
        assertTrue(attribute.getNameClass() instanceof NameClassChoice);

        NameClassChoice nameClassChoice = (NameClassChoice) attribute.getNameClass();
        assertEquals(4, nameClassChoice.getChoiceNames().size());

        Iterator<NameClass> it = nameClassChoice.getChoiceNames().iterator();
        assertTrue(it.next() instanceof AnyName);
        assertTrue(it.next() instanceof Name);
        assertTrue(it.next() instanceof NsName);
        assertTrue(it.next() instanceof NameClassChoice);
    }

    /**
     * Test invalid case of class ChoiceNameProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/choiceNameTests/choiceName_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under NameClass choice-tag was not detected.");
    }

    /**
     * Test invalid case of class ChoiceNameProcessor, with EmptyNameClassException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithEmptyNameClassException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/choiceNameTests/choiceName_empty_nameClass.rng"));
        } catch (EmptyNameClassException error) {
            return;
        }
        fail("The NameClass is empty, but this was not detected.");
    }
}
