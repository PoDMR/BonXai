/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGRootProcessor;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.PatternReferencedButNotDefinedException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class GrammarProcessor
 * @author Lars Schmidt
 */
public class GrammarProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class GrammarProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/grammarTests/grammar_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();

        assertEquals("http://relaxng.org/ns/structure/1.0", grammar.getDefaultNamespace());
        assertEquals("http://www.w3.org/2001/XMLSchema-datatypes", grammar.getAttributeDatatypeLibrary());
        assertEquals("http://docbook.org/ns/docbook", grammar.getAttributeNamespace());
        assertEquals(2, grammar.getStartPatterns().size());
        assertEquals(2, grammar.getDefinedPatternNames().size());
        assertEquals(2, grammar.getDefineLookUpTable().getReferences().size());
        assertEquals(2, grammar.getDefineLookUpTable().getAllReferencedObjects().size());

        // Include content
        assertEquals(1, grammar.getIncludeContents().size());
    }

    /**
     * Test invalid case of class GrammarProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/grammarTests/grammar_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under grammar-tag was not detected.");
    }

    /**
     * Test invalid case of class GrammarProcessor, with PatternReferencedButNotDefinedException.
     * @throws Exception
     */
//    @Test
//    public void testInValidCaseWithPatternReferencedButNotDefinedException() throws Exception {
//        try {
//            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/grammarTests/grammar_invalid_ref.rng"));
//        } catch (PatternReferencedButNotDefinedException error) {
//            return;
//        }
//        fail("A pattern is referenced, but not defined in the grammar and this was not detected.");
//    }
}
