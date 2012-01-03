package eu.fox7.schematoolkit.relaxng.tools;

import eu.fox7.schematoolkit.relaxng.*;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.tools.ExternalSchemaLoader;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

import org.junit.Test;

/**
 * Test of class ExternalSchemaLoader
 * @author Lars Schmidt
 */
public class ExternalSchemaLoaderTest extends junit.framework.TestCase {

    /**
     * Test of handleExternalSchemas method, of class ExternalSchemaLoader without external reference replacement..
     * @throws Exception
     */
    @Test
    public void testHandleExternalSchemas_replacing_off() throws Exception {

        String filePath = new String("/tests/eu/fox7/bonxai/relaxng/tools/ExternalSchemaLoaderTest/A.rng");
        filePath = this.getClass().getResource(filePath).getFile();
        
        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        ExternalSchemaLoader relaxNGExternalSchemaLoader = new ExternalSchemaLoader(relaxNGSchema, false);
        relaxNGExternalSchemaLoader.handleExternalSchemas();

//        RNGWriter rngWriter2 = new RNGWriter(relaxNGSchema);
//        System.out.println(rngWriter2.getRNGString());

        assertTrue(relaxNGSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) relaxNGSchema.getRootPattern();

        // Test for parsed included schema
        assertEquals(1, grammar.getIncludeContents().size());
        assertTrue(grammar.getIncludeContents().getFirst() != null);
        assertTrue(grammar.getIncludeContents().getFirst().getRngSchema() != null);
        RelaxNGSchema includedRNG_C = grammar.getIncludeContents().getFirst().getRngSchema();

        assertTrue(includedRNG_C.getRootPattern() instanceof Grammar);
        Grammar includedRNG_C_grammar = (Grammar) includedRNG_C.getRootPattern();
        assertTrue(includedRNG_C_grammar.getDefinedPatternsFromLookUpTable("refC") != null);

        assertEquals(1, grammar.getStartPatterns().size());
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Choice);
        Choice choice = (Choice) grammar.getStartPatterns().getFirst();

        assertTrue(choice.getPatterns().getFirst() instanceof Element);
        Element elementA = (Element) choice.getPatterns().getFirst();

        assertTrue(elementA.getPatterns().getFirst() instanceof Grammar);
        Grammar nestedGrammar = (Grammar) elementA.getPatterns().getFirst();

        // Test for parsed nested grammar included schema
        assertEquals(1, nestedGrammar.getIncludeContents().size());
        assertTrue(nestedGrammar.getIncludeContents().getFirst() != null);
        assertTrue(nestedGrammar.getIncludeContents().getFirst().getRngSchema() != null);
        RelaxNGSchema includedRNG_B = nestedGrammar.getIncludeContents().getFirst().getRngSchema();

        assertTrue(includedRNG_B.getRootPattern() instanceof Grammar);
        Grammar includedRNG_B_grammar = (Grammar) includedRNG_B.getRootPattern();
        assertTrue(includedRNG_B_grammar.getDefinedPatternsFromLookUpTable("refB") != null);

        // Test for parsed externalRef schema
        assertTrue(elementA.getPatterns().getLast() instanceof ExternalRef);
        ExternalRef externalRefA = (ExternalRef) elementA.getPatterns().getLast();

        assertTrue(externalRefA.getRngSchema() != null);
        RelaxNGSchema externalRefARNGSchema = externalRefA.getRngSchema();

        assertTrue(externalRefARNGSchema.getRootPattern() != null);
        assertTrue(externalRefARNGSchema.getRootPattern() instanceof ExternalRef);
        ExternalRef externalRefARNGSchemaExternalRef = (ExternalRef) elementA.getPatterns().getLast();
        RelaxNGSchema externalRefARNGSchema2 = externalRefARNGSchemaExternalRef.getRngSchema();

        assertTrue(externalRefARNGSchema2.getRootPattern() != null);
        assertTrue(externalRefARNGSchema2.getRootPattern() instanceof ExternalRef);
        ExternalRef externalRefARNGSchema2ExternalRef = (ExternalRef) externalRefARNGSchema2.getRootPattern();
        RelaxNGSchema externalRefARNGSchema3 = externalRefARNGSchema2ExternalRef.getRngSchema();
        assertTrue(externalRefARNGSchema3.getRootPattern() instanceof Attribute);
        Attribute attribute = (Attribute) externalRefARNGSchema3.getRootPattern();
        assertTrue(attribute.getNameClass() != null);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("bob", name.getContent());
    }

    /**
     * Test of handleExternalSchemas method, of class ExternalSchemaLoader with external reference replacement.
     * @throws Exception
     */
    @Test
    public void testHandleExternalSchemas_replacing_on() throws Exception {

        String filePath = new String("/tests/eu/fox7/bonxai/relaxng/tools/ExternalSchemaLoaderTest/A.rng");
        filePath = this.getClass().getResource(filePath).getFile();
        
        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        ExternalSchemaLoader relaxNGExternalSchemaLoader = new ExternalSchemaLoader(relaxNGSchema, true);
        relaxNGExternalSchemaLoader.handleExternalSchemas();

//        RNGWriter rngWriter2 = new RNGWriter(relaxNGSchema);
//        System.out.println(rngWriter2.getRNGString());

        assertTrue(relaxNGSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) relaxNGSchema.getRootPattern();

        // Test for replaced included schema
        assertEquals(0, grammar.getIncludeContents().size());
        assertTrue(grammar.getDefinedPatternsFromLookUpTable("refC") != null);

        assertEquals(2, grammar.getStartPatterns().size());
        assertTrue(grammar.getStartPatterns().getFirst() instanceof Choice);
        Choice choice = (Choice) grammar.getStartPatterns().getFirst();

        assertTrue(choice.getPatterns().getFirst() instanceof Element);
        Element elementA = (Element) choice.getPatterns().getFirst();

        assertTrue(elementA.getPatterns().getFirst() instanceof Grammar);
        Grammar nestedGrammar = (Grammar) elementA.getPatterns().getFirst();

        // Test for replaced nested grammar included schema
        assertEquals(0, nestedGrammar.getIncludeContents().size());
        
        assertTrue(nestedGrammar.getDefinedPatternsFromLookUpTable("refB") != null);

        // Test for replaced externalRef
        assertTrue(elementA.getPatterns().getLast() instanceof Attribute);
        Attribute attribute = (Attribute) elementA.getPatterns().getLast();
        assertTrue(attribute.getNameClass() != null);
        assertTrue(attribute.getNameClass() instanceof Name);
        Name name = (Name) attribute.getNameClass();
        assertEquals("bob", name.getContent());
    }
}
