package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.relaxng.parser.RNGRootProcessor;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidNCNameException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ValueProcessor
 * @author Lars Schmidt
 */
public class ValueProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class ValueProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/valueTests/value_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Element);
        Element element = (Element) rngSchema.getRootPattern();
        List list = (List) (element.getPatterns().getFirst());
        Value value = (Value) list.getPatterns().getFirst();
        assertEquals("132", value.getContent());
        assertEquals("integer", value.getType());
    }

    /**
     * Test invalid case of class ValueProcessor, with InvalidNCNameException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidNCNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/valueTests/value_invalid_ncname.rng"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("Invalid NCName in value-tag was not detected.");
    }

}
