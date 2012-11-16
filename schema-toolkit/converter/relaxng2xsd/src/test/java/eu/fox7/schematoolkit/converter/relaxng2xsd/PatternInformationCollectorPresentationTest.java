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

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.converter.relaxng2xsd.PatternInformationCollector;
import eu.fox7.schematoolkit.relaxng.Element;
import eu.fox7.schematoolkit.relaxng.Pattern;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.tools.ExternalSchemaLoader;
import eu.fox7.schematoolkit.relaxng.writer.RNGWriter;

import java.util.Iterator;
import org.junit.Test;

/**
 * Test of class PatternInformationCollector
 * @author Lars Schmidt
 */
public class PatternInformationCollectorPresentationTest {

    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception 
     */
    @Test
    public void testCollectData() throws Exception {

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/A.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name8.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/PatternDataMixed.rng");

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/parser/rngs/relaxng.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/docbook_4_5/docbook.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/xhtml-strict/xhtml-strict_.rng");
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name9.rng");

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/optional.rng");

//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/acronym_problem.rng");
        String filePath = new String("/tests/eu/fox7/bonxai/relaxng/tools/rngs/problem_with_text.rng");
        filePath = this.getClass().getResource(filePath).getFile();
        
        RNGParser instance = new RNGParser(filePath, false);
        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        ExternalSchemaLoader relaxNGExternalSchemaLoader = new ExternalSchemaLoader(relaxNGSchema, true);
        relaxNGExternalSchemaLoader.handleExternalSchemas();

        // PatternInformationCollector
        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
        relaxNGPatternDataCollector.collectData();


        RNGWriter rngWriter = new RNGWriter(relaxNGSchema);
        System.out.println(rngWriter.getRNGString());

        // output
        for (Iterator<Pattern> it = relaxNGPatternDataCollector.getPatternIntel().keySet().iterator(); it.hasNext();) {
            Pattern currentKey = it.next();
            String name = "";
            if (currentKey instanceof Element) {
                name = ((Element) currentKey).getNameAttribute();
            }
            System.out.println(currentKey + "(" + name + ")" + ": " + relaxNGPatternDataCollector.getPatternIntel().get(currentKey));
        }
        System.out.println("\n\n");
    }
    /**
     * Test of collectData method, of class PatternInformationCollector.
     * @throws Exception
     */
//    @Test
//    public void testGetDataForPattern() throws Exception {
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name9.rng");
//
//        RNGParser instance = new RNGParser(filePath, false);
//        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();
//
//        ExternalSchemaLoader relaxNGExternalSchemaLoader = new ExternalSchemaLoader(relaxNGSchema, true);
//        relaxNGExternalSchemaLoader.handleExternalSchemas();
//
//        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(relaxNGSchema);
////        relaxNGPatternDataCollector.collectData();
//
//        RNGWriter rngWriter2 = new RNGWriter(relaxNGSchema);
//        System.out.println(rngWriter2.getRNGString());
//
//        Grammar grammar = (Grammar) relaxNGSchema.getRootPattern();
//        Element element = (Element) grammar.getStartPatterns().getFirst();
//        Element element2 = (Element) element.getPatterns().getFirst();
//        Choice choice = (Choice) element2.getPatterns().getFirst();
//        System.out.println(relaxNGPatternDataCollector.getDataForPattern(choice));
//    }
}
