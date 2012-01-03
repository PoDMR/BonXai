package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.relaxng.parser.RNGRootProcessor;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyNameClassException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ExceptNameProcessor
 * @author Lars Schmidt
 */
public class ExceptNameProcessorTest extends junit.framework.TestCase {

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
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/exceptNameTests/exceptName_valid_any.rng"));
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
        assertTrue(attribute.getNameClass() instanceof AnyName);

        AnyName anyName = (AnyName) attribute.getNameClass();
        assertEquals(3, anyName.getExceptNames().size());

        Iterator<NameClass> it = anyName.getExceptNames().iterator();
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
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/exceptNameTests/exceptName_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under NameClass choice-tag was not detected.");
    }
}
