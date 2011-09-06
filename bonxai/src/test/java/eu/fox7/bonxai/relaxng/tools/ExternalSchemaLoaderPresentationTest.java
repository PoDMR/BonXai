package eu.fox7.bonxai.relaxng.tools;

import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.RNGParser;
import eu.fox7.bonxai.relaxng.tools.ExternalSchemaLoader;
import eu.fox7.bonxai.relaxng.writer.RNGWriter;

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
