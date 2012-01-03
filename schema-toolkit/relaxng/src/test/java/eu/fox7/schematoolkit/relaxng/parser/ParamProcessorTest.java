package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.relaxng.parser.RNGRootProcessor;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MissingNameException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;

/**
 * Test of class ParamProcessor
 * @author Lars Schmidt
 */
public class ParamProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class ParamProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/paramTests/param_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Element);
        Element element = (Element) rngSchema.getRootPattern();
        Data data = (Data) (element.getPatterns().getFirst());
        Param param = data.getParams().getFirst();
        assertEquals("maxLength", param.getName());
        assertEquals("127", param.getContent());
    }

    /**
     * Test invalid case of class ParamProcessor, with InvalidNCNameException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidNCNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/paramTests/param_invalid_ncname.rng"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("Invalid NCName in param-tag was not detected.");
    }

    /**
     * Test invalid case of class ParamProcessor, with MissingNameException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithMissingNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/paramTests/param_missing_name.rng"));
        } catch (MissingNameException error) {
            return;
        }
        fail("Missing name in param-tag was not detected.");
    }
}
