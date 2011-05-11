package de.tudortmund.cs.bonxai.relaxng.tools;

import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
import de.tudortmund.cs.bonxai.relaxng.writer.RNGWriter;
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

        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/relaxng.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/tools/rngs/PatternDataMixed.rng");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/relaxng/parser/rngs/docbook5.rng.xml");

        RNGParser instance = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = instance.getRNGSchema();

        RNGWriter rngWriter = new RNGWriter(relaxNGSchema);
        System.out.println(rngWriter.getRNGString());

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        RNGWriter rngWriter2 = new RNGWriter(relaxNGSchema);
        System.out.println(rngWriter2.getRNGString());

    }
}
