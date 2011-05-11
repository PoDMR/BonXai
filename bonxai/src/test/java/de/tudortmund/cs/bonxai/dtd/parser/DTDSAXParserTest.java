package de.tudortmund.cs.bonxai.dtd.parser;

import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.dtd.writer.DTDWriter;
import org.junit.Test;

/**
 * Test of class DTDSAXParser
 * @author Lars Schmidt
 */
public class DTDSAXParserTest extends junit.framework.TestCase {

    /**
     * Test of parseXML method, of class DTDSAXParser.
     * @throws Exception 
     */
    @Test
    public void testParseXMLTest() throws Exception {
        // XMLSchema DTD
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/XMLSchema.xsd");

        // XML Specification DTD
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/xmlspec-v21.xml");

        // XHTML DTD
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/xhtml1-strict.xml");

        // DocBook DTD
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/docbook_dtd.xml");

        // Relax NG DTD
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/relaxng.xml");


        // aktuell erzeugte XML Datei
        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/elements.xml");

//             online Test
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/online.xml");


        DTDSAXParser instance = new DTDSAXParser(false);
        DocumentTypeDefinition dtd = instance.parseXML(filePath);

        DTDWriter dtdWriter = new DTDWriter(dtd);
        dtdWriter.setWriteEntities(true);

//        System.out.println(dtdWriter.getXMLWithFullDTDDeclarationString());
        System.out.println(dtdWriter.getExternalSubsetString());
//    }

    /**
     * Test of parseXML method, of class DTDSAXParser.
     * @throws Exception
     */
//    @Test
//    public void testParseDTDOnlyTest() throws Exception {
//        // XMLSchema DTD
//        String rootName = "xs:schema";
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/XMLSchema.dtd");
//
//        // XML Specification DTD
////        String rootName = "spec";
////        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/xmlspec-v21.dtd");
//
//        // XHTML DTD
////        String rootName = "html";
////        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/xhtml1-strict.dtd");
//
//        // DocBook DTD
////        String rootName = "title";
////        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/docbook.dtd");
//
//         Relax NG DTD
//        String rootName = "element";
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/relaxng.dtd");


////         online Test
//        String rootName = "element";
//        String filePath = new String("http://www.terminologyintheclouds.net/GeneterMemoTermStrict_V01.dtd");
//
//
//
//        DTDSAXParser instance = new DTDSAXParser(false);
//        DocumentTypeDefinition dtd = instance.parseDTDOnly(filePath, rootName);
//
//        DTDWriter dtdWriter = new DTDWriter(dtd);
//        dtdWriter.setWriteEntities(true);
//
//        System.out.println(dtdWriter.getExternalSubsetString());
//        System.out.println(dtdWriter.getXMLWithFullDTDDeclarationString());
    }
}
