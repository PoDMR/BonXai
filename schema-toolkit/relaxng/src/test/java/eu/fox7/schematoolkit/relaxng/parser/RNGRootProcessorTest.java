package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Choice;
import eu.fox7.schematoolkit.relaxng.om.Element;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Group;
import eu.fox7.schematoolkit.relaxng.om.Interleave;
import eu.fox7.schematoolkit.relaxng.om.NotAllowed;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGRootProcessor;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class RNGRootProcessor
 * @author Lars Schmidt
 */
public class RNGRootProcessorTest extends junit.framework.TestCase {

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
     * Test of valid case with Grammar element, of class RNGRootProcessor.
     * @throws Exception
     */
    @Test
    public void testRootValidCaseGrammar() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_grammar.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        assertEquals("http://relaxng.org/ns/structure/1.0", rngSchema.getDefaultNamespace());

        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertEquals("http://relaxng.org/ns/structure/1.0", grammar.getDefaultNamespace());
        assertEquals(1, grammar.getStartPatterns().size());
    }

    /**
     * Test of valid case with Choice element, of class RNGRootProcessor.
     * @throws Exception
     */
    @Test
    public void testRootValidCaseChoice() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_choice.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertTrue(rngSchema.getRootPattern() instanceof Choice);
        assertEquals("http://relaxng.org/ns/structure/1.0", rngSchema.getDefaultNamespace());

        Choice choice = (Choice) rngSchema.getRootPattern();
        assertEquals("http://relaxng.org/ns/structure/1.0", choice.getDefaultNamespace());
        assertEquals(1, choice.getPatterns().size());
    }

    /**
     * Test of valid case with Element element, of class RNGRootProcessor.
     * @throws Exception
     */
    @Test
    public void testRootValidCaseElement() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_element.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertTrue(rngSchema.getRootPattern() instanceof Element);
        assertEquals("http://relaxng.org/ns/structure/1.0", rngSchema.getDefaultNamespace());

        Element element = (Element) rngSchema.getRootPattern();
        assertEquals("http://relaxng.org/ns/structure/1.0", element.getDefaultNamespace());
        assertEquals(1, element.getPatterns().size());
    }

    /**
     * Test of valid case with ExternalRef element, of class RNGRootProcessor.
     * @throws Exception
     */
    @Test
    public void testRootValidCaseExternalRef() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_externalRef.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertTrue(rngSchema.getRootPattern() instanceof ExternalRef);
        assertEquals("http://relaxng.org/ns/structure/1.0", rngSchema.getDefaultNamespace());

        ExternalRef externalRef = (ExternalRef) rngSchema.getRootPattern();
        assertEquals("http://relaxng.org/ns/structure/1.0", externalRef.getDefaultNamespace());
        assertEquals("relaxng.rng", externalRef.getHref());
    }

    /**
     * Test of valid case with Group element, of class RNGRootProcessor.
     * @throws Exception
     */
    @Test
    public void testRootValidCaseGroup() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_group.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertTrue(rngSchema.getRootPattern() instanceof Group);
        assertEquals("http://relaxng.org/ns/structure/1.0", rngSchema.getDefaultNamespace());

        Group group = (Group) rngSchema.getRootPattern();
        assertEquals("http://relaxng.org/ns/structure/1.0", group.getDefaultNamespace());
        assertEquals(2, group.getPatterns().size());
    }

    /**
     * Test of valid case with Interleave element, of class RNGRootProcessor.
     * @throws Exception
     */
    @Test
    public void testRootValidCaseInterleave() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_interleave.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertTrue(rngSchema.getRootPattern() instanceof Interleave);
        assertEquals("http://relaxng.org/ns/structure/1.0", rngSchema.getDefaultNamespace());

        Interleave interleave = (Interleave) rngSchema.getRootPattern();
        assertEquals("http://relaxng.org/ns/structure/1.0", interleave.getDefaultNamespace());
        assertEquals(2, interleave.getPatterns().size());
    }

    /**
     * Test of valid case with NotAllowed element, of class RNGRootProcessor.
     * @throws Exception
     */
    @Test
    public void testRootValidCaseNotAllowed() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_notAllowed.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertTrue(rngSchema.getRootPattern() instanceof NotAllowed);
        assertEquals("http://relaxng.org/ns/structure/1.0", rngSchema.getDefaultNamespace());

        NotAllowed notAllowed = (NotAllowed) rngSchema.getRootPattern();
        assertEquals("http://relaxng.org/ns/structure/1.0", notAllowed.getDefaultNamespace());
    }

    /**
     * Test of invalid case, of class RNGRootProcessor.
     * @throws Exception
     */
//    @Test
//    public void testRootInValidCase() throws Exception {
//        try {
//            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/rootTests/root_invalid.rng"));
//        } catch (UnsupportedContentException error) {
//            return;
//        }
//        fail("Exception was thrown: ");
//    }
}
