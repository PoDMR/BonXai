package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.converter.relaxng2xsd.NameClassAnalyzer;
import eu.fox7.schematoolkit.relaxng.Element;
import eu.fox7.schematoolkit.relaxng.Grammar;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.RNGParser;
import eu.fox7.schematoolkit.relaxng.tools.XMLAttributeReplenisher;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.junit.Test;

/**
 * Test of class NameClassAnalyzer
 * @author Lars Schmidt
 */
public class NameClassAnalyzerPresentationTest {


    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * @throws Exception 
     */
    @Test
    public void testAnalyze() throws Exception {

        // name-attribute
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name1.rng");
        
        // nameClass: name
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name2.rng");

        // nameClass: anyName
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name3.rng");

//         nameClass: nsName
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name4.rng");

        // nameClass: nsName ns="http://www.example.org"
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name5.rng");

        // nameClass: nsName ns=""
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name6.rng");

        // nameClasses:
        //   <name>test</name>
        //   <anyName/>
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name7.rng");

        // nameClasses:
        //   <name>test</name>
        //   <anyName>
        //     <except>
        //       <nsName/>
        //     </except>
        //   </anyName>
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name10.rng");

        // nameClasses:
        //   <name>test</name>
        //   <anyName>
        //     <except>
        //       <nsName ns="http://example.org"/>
        //     </except>
        //   </anyName>
//        String filePath = new String("tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name8.rng");

        // complex Example: --> printed
        String filePath = new String("/tests/eu/fox7/bonxai/relaxng/tools/rngs/name/name9.rng");
        filePath = this.getClass().getResource(filePath).getFile();
        


        RNGParser parser = new RNGParser(filePath, false);

        RelaxNGSchema relaxNGSchema = parser.getRNGSchema();

        XMLAttributeReplenisher relaxNGSchemaReplenisher = new XMLAttributeReplenisher(relaxNGSchema);
        relaxNGSchemaReplenisher.startReplenishment();

        Grammar grammar = (Grammar) relaxNGSchema.getRootPattern();

        Element element = (Element) ((Element) grammar.getStartPatterns().getFirst()).getPatterns().getFirst();

        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> nameInfos = instance.analyze();


        
        // output
        for (Iterator<String> it = nameInfos.keySet().iterator(); it.hasNext();) {
            String currentKey = it.next();

            if (nameInfos.get(currentKey) != null) {
                Object currentAnyObject = nameInfos.get(currentKey);
                if (currentAnyObject instanceof AnyAttribute) {
                    AnyAttribute anyAttribute = (AnyAttribute) currentAnyObject;
                    if (currentKey.startsWith("-")) {
                        System.out.println("AnyAttribute in external schema (" + anyAttribute.getNamespace() + ") with content \"##other\", " + anyAttribute.getProcessContentsInstruction());
                    } else {
                        System.out.println("AnyAttribute: " + anyAttribute.getNamespace() + ", " + anyAttribute.getProcessContentsInstruction());
                    }
                } else if (currentAnyObject instanceof AnyPattern) {
                    AnyPattern anyPattern = (AnyPattern) currentAnyObject;
                    if (currentKey.startsWith("-")) {
                        System.out.println("AnyPattern in external schema (" + anyPattern.getNamespace() + ") with content \"##other\" " + anyPattern.getProcessContentsInstruction());
                    } else {
                        System.out.println("AnyPattern: " + anyPattern.getNamespace() + ", " + anyPattern.getProcessContentsInstruction());
                    }
                }
            } else {
                System.out.println(currentKey);
            }
        }
    }
}