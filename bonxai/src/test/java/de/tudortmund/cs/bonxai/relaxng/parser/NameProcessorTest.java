package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidQNameException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class NameProcessor
 * @author Lars Schmidt
 */
public class NameProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class NameProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/nameTests/name_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        Name name = (Name) ((Element) grammar.getStartPatterns().getFirst()).getNameClass();

        assertEquals("name", name.getContent());
        assertEquals("http://www.example.org", name.getAttributeNamespace());
    }

    /**
     * Test invalid case of class NameProcessor, with InvalidQNameException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidQNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/nameTests/name_invalid_QName.rng"));
        } catch (InvalidQNameException error) {
            return;
        }
        fail("There is an invalid QName in a name-tag, but this was not detected.");
    }
}
