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

import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.tools.ExternalSchemaLoader;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

import org.junit.Test;

/**
 * Test of class ExternalSchemaLoader
 * @author Lars Schmidt
 */
public class ExternalSchemaLoaderPresentationTest {

    /**
     * Test of handleExternalSchemas method, of class ExternalSchemaLoader.
     * @throws Exception 
     */
    @Test
    public void testHandleExternalSchemas() throws Exception {

        String filePath = this.getClass().getResource("/tests/eu/fox7/bonxai/relaxng/tools/rngs/A.rng").getFile();

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/ProblemA.rng");

        // redefine
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/redefine_addressbook.rng");

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/docbook_4_5/docbook.rng");

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/xhtml-strict/xhtml-strict.rng");


        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

//        RNGWriter rngWriter = new RNGWriter(relaxNGSchema);
//        System.out.println(rngWriter.getRNGString());

        ExternalSchemaLoader relaxNGExternalSchemaLoader = new ExternalSchemaLoader(relaxNGSchema, false);
        relaxNGExternalSchemaLoader.handleExternalSchemas();

        RNGWriter rngWriter2 = new RNGWriter(relaxNGSchema);
        System.out.println(rngWriter2.getRNGString());
    }
}
