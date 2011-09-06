package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.bonxai.converter.xsd2relaxng.InheritanceInformationCollector;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.XSDParser;
import eu.fox7.bonxai.xsd.tools.BlockFinalSpreadingHandler;
import eu.fox7.bonxai.xsd.writer.XSDWriter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.junit.Test;

/**
 * Test of class InheritanceInformationCollector
 * @author Lars Schmidt
 */
public class InheritanceInformationCollectorTest extends junit.framework.TestCase {

    /**
     * Test of getAllTypeSubstitutionsForElement method, of class InheritanceInformationCollector.
     */
//    @Test
//    public void testGetAllTypeSubstitutionsForElement_presentation() throws Exception {
////        String uri = "tests/eu/fox7/bonxai/xsd/tools/groupReplacerTests/group.xsd";
////        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupBlockRestriction.xsd";
//
////        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/preparationTests/substitutionGroupBlockRestriction_temp.xsd";
////        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/preparationTests/type_inheritance/simpleType.xsd";
//        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/preparationTests/xhtml/redefineBase.xsd";
//
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
//        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
//        inheritanceInformationCollector.collectInformation(xmlSchema);
//
//        System.out.println("Type substitutions:" + "\n" + "-------------------");
//        for (Iterator<Element> it = xmlSchema.getElements().iterator(); it.hasNext();) {
//            Element element = it.next();
//            LinkedHashSet<Type> typeSubstitutions = inheritanceInformationCollector.getAllTypeSubstitutionsForElement(element);
//            System.out.println(element + " (Type: " + element.getType() + ")");
//            System.out.println("SubstitutionTypes: " + typeSubstitutions + "\n");
//        }
//    }


    @Test
    public void testCollectInformation() throws Exception {
        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/inheritanceInformationCollectorTests/inheritance.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();


        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Type, LinkedHashSet<Type>> result = inheritanceInformationCollector.getInheritanceInformation();

        assertEquals(1, result.size());

        Type type_aaa = result.keySet().iterator().next();
        assertEquals(1, result.get(type_aaa).size());

        LinkedHashSet<Type> substitutionTypes = result.get(type_aaa);
        Iterator<Type> itE = substitutionTypes.iterator();
        Type type_bbb = itE.next();

        assertEquals("{}BBB", type_bbb.getName());
    }

    @Test
    public void testCollectInformation_restriction() throws Exception {
        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/inheritanceInformationCollectorTests/inheritance_block_restriction.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();

        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Type, LinkedHashSet<Type>> result = inheritanceInformationCollector.getInheritanceInformation();

        assertEquals(0, result.size());
    }

    @Test
    public void testCollectInformation_extension() throws Exception {
        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/inheritanceInformationCollectorTests/inheritance_block_extension.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();

        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Type, LinkedHashSet<Type>> result = inheritanceInformationCollector.getInheritanceInformation();

        assertEquals(0, result.size());
    }

    @Test
    public void testCollectInformation_all() throws Exception {
        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/inheritanceInformationCollectorTests/inheritance_block_all.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();

        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Type, LinkedHashSet<Type>> result = inheritanceInformationCollector.getInheritanceInformation();

        assertEquals(0, result.size());
    }


    @Test
    public void testGetAllTypeSubstitutionsForElement() throws Exception {
        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/inheritanceInformationCollectorTests/inheritance_block_restriction_element.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();

        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        BlockFinalSpreadingHandler blockFinalSpreadingHandler = new BlockFinalSpreadingHandler();
        blockFinalSpreadingHandler.spread(xmlSchema);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);

        Element element = xmlSchema.getElementSymbolTable().getReference("{}C").getReference();

        LinkedHashSet<Type> typeSubstitutions = inheritanceInformationCollector.getAllTypeSubstitutionsForElement(element);

        assertEquals(0, typeSubstitutions.size());

        Element element2 = xmlSchema.getElementSymbolTable().getReference("{}E").getReference();

        LinkedHashSet<Type> typeSubstitutions2 = inheritanceInformationCollector.getAllTypeSubstitutionsForElement(element2);

        assertEquals(1, typeSubstitutions2.size());
        Iterator<Type> itE = typeSubstitutions2.iterator();
        Type type_bbb = itE.next();

        assertEquals("{}BBB", type_bbb.getName());
    }
}
