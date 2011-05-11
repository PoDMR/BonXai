package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.converter.relaxng2xsd.PatternInformationCollector;
import de.tudortmund.cs.bonxai.relaxng.Element;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
import de.tudortmund.cs.bonxai.relaxng.tools.ExternalSchemaLoader;
import de.tudortmund.cs.bonxai.relaxng.writer.RNGWriter;
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

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/A.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/name/name8.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/PatternDataMixed.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/relaxng.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/docbook_4_5/docbook.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/xhtml-strict/xhtml-strict_.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/name/name9.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/optional.rng");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/acronym_problem.rng");
        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/problem_with_text.rng");

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
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/name9.rng");
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
