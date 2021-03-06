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

import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.relaxng.parser.RNGRootProcessor;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyTypeException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class DataProcessor
 * @author Lars Schmidt
 */
public class DataProcessorTest extends junit.framework.TestCase {

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
     * Test valid case of class DataProcessor.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/dataTests/data_valid.rng"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertTrue(rngSchema.getRootPattern() instanceof Grammar);
        Grammar grammar = (Grammar) rngSchema.getRootPattern();
        assertTrue(grammar.getDefineLookUpTable().getReference("define").getReference().getFirst().getPatterns().getFirst() instanceof Data);

        Data data = (Data) grammar.getDefineLookUpTable().getReference("define").getReference().getFirst().getPatterns().getFirst();

        assertEquals(1, data.getParams().size());
        Param param = data.getParams().getFirst();
        assertEquals("maxInclusive", param.getName());
        assertEquals("120", param.getContent());

        assertEquals(1, data.getExceptPatterns().size());
        Value value = (Value) data.getExceptPatterns().getFirst();
        assertEquals("0", value.getContent());
    }

    /**
     * Test invalid case of class DataProcessor, with UnsupportedContentException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithUnsupportedContentException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/dataTests/data_invalid_content.rng"));
        } catch (UnsupportedContentException error) {
            return;
        }
        fail("Unsupported content under data-tag was not detected.");
    }

    /**
     * Test invalid case of class DataProcessor, with InvalidNCNameException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithInvalidNCNameException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/dataTests/data_invalid_ncname.rng"));
        } catch (InvalidNCNameException error) {
            return;
        }
        fail("There is an invalid NCName for type of data-tag, but it was not detected.");
    }

    /**
     * Test invalid case of class DataProcessor, with EmptyTypeException.
     * @throws Exception
     */
    @Test
    public void testInValidCaseWithEmptyTypeException() throws Exception {
        try {
            rngRootProcessor.processNode(RNGUtilities.getSchemaNode("tests/eu/fox7/bonxai/relaxng/parser/rngs/dataTests/data_invalid_type.rng"));
        } catch (EmptyTypeException error) {
            return;
        }
        fail("There is no type attribute in data-tag, but it was not detected.");
    }
}
