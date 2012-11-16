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

package eu.fox7.schematoolkit.relaxng.tools;

import java.net.URL;

import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.tools.XMLAttributeReplenisher;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

import org.junit.Test;

/**
 * Test of class XMLAttributeReplenisher
 * @author Lars Schmidt
 */
public class XMLAttributeReplenisherPresentationTest {

    /**
     * Test of startReplenishment method, of class XMLAttributeReplenisher.
     * @throws Exception 
     */
    @Test
    public void testStartReplenishment() throws Exception {

        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/relaxng.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/PatternDataMixed.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/docbook5.rng.xml");
    	URL url = this.getClass().getResource("/"+filePath);

        RNGParser instance = new RNGParser(url.getFile(), false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        RNGWriter rngWriter = new RNGWriter(relaxNGSchema);
        System.out.println(rngWriter.getRNGString());

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        RNGWriter rngWriter2 = new RNGWriter(relaxNGSchema);
        System.out.println(rngWriter2.getRNGString());

    }
}
