package eu.fox7.bonxai.xsd.tools;

import java.net.URL;

import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.ForeignSchemaLoader;
import eu.fox7.bonxai.xsd.parser.XSDParser;
import eu.fox7.bonxai.xsd.tools.GroupReplacer;
import eu.fox7.bonxai.xsd.writer.XSDWriter;

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
        String uri = new String("tests/eu/fox7/bonxai/xsd/tools/groupReplacerTests/group.xsd");
    	URL url = this.getClass().getResource("/"+uri);

//        String uri = new String("tests/eu/fox7/bonxai/xsd/tools/groupReplacerTests/group1.xsd");
//        String uri = new String("tests/eu/fox7/bonxai/xsd/tools/groupReplacerTests/group2.xsd");

        XSDParser instance = new XSDParser(false, false);
        XSDSchema xsdSchema = instance.parse(url.getFile());

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