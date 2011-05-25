package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.tools.BlockFinalSpreadingHandler;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.junit.Test;

/**
 * Test of class SubstitutionGroupInformationCollector
 * @author Lars Schmidt
 */
public class SubstitutionGroupInformationCollectorTest extends junit.framework.TestCase {

    /**
     * Test of getSubstitutionGroupInformation method, of class SubstitutionGroupInformationCollector.
     */
//    @Test
//    public void testGetSubstitutionGroupInformation_presentation() throws Exception {
//
////        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupBlockRestriction.xsd";
//
////        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupBlockRestriction_temp.xsd";
//        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/substitutionGroupInformationCollectorTests/substitutionGroupNested.xsd";
//        XSDParser xsdParser = new XSDParser(false, false);
//        Schema xmlSchema = xsdParser.parse(uri);
//
//        XSD_Writer xsd_Writer = new XSD_Writer(xmlSchema);
//
//        System.out.println(xsd_Writer.getXSDString());
//
//        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
//        blockFinalSpreadingHandler.spread(xmlSchema);
//
//        xsd_Writer = new XSD_Writer(xmlSchema);
//        System.out.println(xsd_Writer.getXSDString());
//
//        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
//        substitutionGroupInformationCollector.collectInformation(xmlSchema);
//
//        LinkedHashMap<Element, LinkedHashSet<Element>> result = substitutionGroupInformationCollector.getSubstitutionGroupInformation();
//
//        System.out.println("Element substitutions:" + "\n" + "----------------------");
//        for (Iterator<Element> it = xmlSchema.getElements().iterator(); it.hasNext();) {
//            Element element = it.next();
//            System.out.println(element + " (Type: " + element.getType() + ")");
//            System.out.println("SubstitutionTypes: " + (result.get(element) != null ? result.get(element) : "[]") + "\n");
//        }
//    }

    @Test
    public void testGetSubstitutionGroupInformation_substitutionGroupBlockRestriction() throws Exception {
        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/substitutionGroupInformationCollectorTests/substitutionGroupBlockRestriction.xsd";

        uri = this.getClass().getResource("/"+uri).getFile();

        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
        substitutionGroupInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Element, LinkedHashSet<Element>> result = substitutionGroupInformationCollector.getSubstitutionGroupInformation();

        assertEquals(1, result.size());

        Element element_c = result.keySet().iterator().next();
        assertEquals(2, result.get(element_c).size());

        LinkedHashSet<Element> substitutionElements = result.get(element_c);
        Iterator<Element> itE = substitutionElements.iterator();
        Element element_d = itE.next();
        Element element_e = itE.next();

        assertEquals("{}D", element_d.getName());
        assertEquals("{}E", element_e.getName());
    }

    @Test
    public void testGetSubstitutionGroupInformation_substitutionGroupBlockExtension() throws Exception {
        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/substitutionGroupInformationCollectorTests/substitutionGroupBlockExtension.xsd";

        uri = this.getClass().getResource("/"+uri).getFile();

        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
        substitutionGroupInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Element, LinkedHashSet<Element>> result = substitutionGroupInformationCollector.getSubstitutionGroupInformation();

        assertEquals(2, result.size());
        Iterator<Element> itH = result.keySet().iterator();
        Element element_c = itH.next();
        assertEquals(2, result.get(element_c).size());

        LinkedHashSet<Element> substitutionElements = result.get(element_c);
        Iterator<Element> itE = substitutionElements.iterator();
        Element element_e = itE.next();
        Element element_f = itE.next();

        assertEquals("{}E", element_e.getName());
        assertEquals("{}F", element_f.getName());


        LinkedHashSet<Element> substitutionElements2 = result.get(element_e);
        Iterator<Element> itE2 = substitutionElements2.iterator();
        Element element_f2 = itE2.next();

        assertEquals("{}F", element_f2.getName());
    }

    @Test
    public void testGetSubstitutionGroupInformation_substitutionGroupBlockSubstitution() throws Exception {
        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/substitutionGroupInformationCollectorTests/substitutionGroupBlockSubstitution.xsd";

        uri = this.getClass().getResource("/"+uri).getFile();

        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
        substitutionGroupInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Element, LinkedHashSet<Element>> result = substitutionGroupInformationCollector.getSubstitutionGroupInformation();

        assertEquals(0, result.size());
    }

    @Test
    public void testGetSubstitutionGroupInformation_substitutionGroupNested() throws Exception {
        String uri = "tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/substitutionGroupInformationCollectorTests/substitutionGroupNested.xsd";

        uri = this.getClass().getResource("/"+uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
        substitutionGroupInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Element, LinkedHashSet<Element>> result = substitutionGroupInformationCollector.getSubstitutionGroupInformation();

        assertEquals(2, result.size());
        Iterator<Element> itH = result.keySet().iterator();
        Element element_c = itH.next();
        assertEquals(3, result.get(element_c).size());

        LinkedHashSet<Element> substitutionElements = result.get(element_c);
        Iterator<Element> itE = substitutionElements.iterator();
        Element element_d = itE.next();
        Element element_e = itE.next();
        Element element_f = itE.next();

        assertEquals("{}D", element_d.getName());
        assertEquals("{}E", element_e.getName());
        assertEquals("{}F", element_f.getName());


        LinkedHashSet<Element> substitutionElements2 = result.get(element_e);
        Iterator<Element> itE2 = substitutionElements2.iterator();
        Element element_f2 = itE2.next();

        assertEquals("{}F", element_f2.getName());
    }
}
