package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.parser.ForeignSchemaLoader;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import org.junit.Test;

/**
 * Showcase example of class GroupReplacer
 * @author Lars Schmidt
 */
public class GroupReplacerExampleTest extends junit.framework.TestCase {

    /**
     * Test of replace method, of class GroupReplacer.
     * @throws Exception
     */
    @Test
    public void testReplace() throws Exception {
        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/tools/groupReplacerTests/group.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/tools/groupReplacerTests/group1.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/tools/groupReplacerTests/group2.xsd");

        XSDParser instance = new XSDParser(false, false);
        XSDSchema xsdSchema = instance.parse(uri);

        XSDWriter xsd_Writer = new XSDWriter(xsdSchema);

        System.out.println(xsd_Writer.getXSDString());

        ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(xsdSchema, false);
        foreignSchemaLoader.findForeignSchemas();

        GroupReplacer groupReplacer = new GroupReplacer(xsdSchema);
        groupReplacer.replace();

        xsd_Writer = new XSDWriter(xsdSchema);
        System.out.println("\n--------------------------------------------------------------------------\n" + " without groupRefs or attributeGroupRefs " + "\n--------------------------------------------------------------------------\n");
        System.out.println(xsd_Writer.getXSDString());
    }
}