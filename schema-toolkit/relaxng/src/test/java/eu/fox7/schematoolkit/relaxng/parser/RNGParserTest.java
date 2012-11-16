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

import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

import org.junit.Test;

/**
 * Test of class RNGParser
 * @author Lars Schmidt
 */
public class RNGParserTest extends junit.framework.TestCase {

    public RNGParserTest() {
    }

    /**
     * Test of RNGParser constructor and parsing process
     * @throws Exception 
     */
    @Test
    public void testRNGParser() throws Exception {

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/relaxng.rng");
//
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/docbook_simplified.xml");
//
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/xhtml_simplified.xml");
//
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/test.xml");
//
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/namespaces.xml");
//
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/docbook5.rng.xml");
//
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/OpenDocument-schema-v1.0-os.rng");
//
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/xslt.rng");


//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/root_choice.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/root_element.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/root_externalRef.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/root_grammar.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/root_group.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/root_interleave.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/root_notAllowed.rng");

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/include.rng");

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/temp.xml");

        // RNGParser
        // Parameter 1: Path to file
        // Parameter 2: Debug
        RNGParser instance = new RNGParser(filePath, false);

        instance.getRNGSchema();

        RNGWriter rngWriter = new RNGWriter(instance.getRNGSchema());
        System.out.println(rngWriter.getRNGString());
    }
}
