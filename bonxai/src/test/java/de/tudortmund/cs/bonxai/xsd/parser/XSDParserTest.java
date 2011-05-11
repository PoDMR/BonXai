package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.InheritancePreProcessorVisitor;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.PreProcessor;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.io.File;
import java.util.Iterator;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class XSDParserTest extends junit.framework.TestCase {

    public XSDParserTest() {
    }

    /**
     * Test of parserXML method, of class XSDParser.
     * @throws Exception 
     */
    @Test
    public void testParserXML() throws Exception {

        // XML Schema XSD
        //String filePath = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/XMLSchema.xsd");
        //String filePath = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/listTests/list_valid.xsd");

        // XHTML strict xsd
//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/xhtml1-strict.xsd");
        
        // Substitution Group
//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/subGroup.xsd");

        // EDC Test 1
        //String filePath = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/edc_test_1.xsd");
        
        // EDC Test 2
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/edc_test_2.xsd");
        
        
//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/edc_test_2.xsd");
//        String uri = new String("http://www.w3.org/2001/XMLSchema.xsd");
//        String uri = new String("D:/Desktop/xsd_tests/base.xsd");
//        String uri = new String("D:/Desktop/xsd_tests/redefineBase.xsd");


//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/writer_bugs_01.xsd");

//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/union/xsds/redefinedSchemaResolverTest/removeRedefinedSchema_before.xsd");

//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/union/xsds/redefinedSchemaResolverTest/redefinedSchema.xsd");

//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/gemalt.xsd");

        String uri = "tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/rngs/Unbenannt1.xsd";

        // XSDParser
        // Parameter 1: Path to file
        // Parameter 2: Check EDC
        // Parameter 3: Debug
        XSDParser instance = new XSDParser(true, false);
        XSDSchema xmlSchema = instance.parse(uri);
        //ForeignSchemaLoader foreignSchemaLoader = new ForeignSchemaLoader(xsdSchema, false);
        //foreignSchemaLoader.findForeignSchema();
        XSDWriter xsd_Writer = new XSDWriter(xmlSchema);
       
        System.out.println(xsd_Writer.getXSDString());
if (!xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (foreignSchema.getSchema() != null) {
                    System.out.println("\n--------------------------------------------------------------------------\n" + foreignSchema.getClass().getName() + " - " + foreignSchema.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");
                    xsd_Writer = new XSDWriter(foreignSchema.getSchema());
                    System.out.println(xsd_Writer.getXSDString());

                    if (!foreignSchema.getSchema().getForeignSchemas().isEmpty()) {
                        for (Iterator<ForeignSchema> it2 = foreignSchema.getSchema().getForeignSchemas().iterator(); it2.hasNext();) {
                            ForeignSchema foreignSchema2 = it2.next();
                            if (foreignSchema2.getSchema() != null) {
                                System.out.println("\n--------------------------------------------------------------------------\n" + foreignSchema2.getClass().getName() + " - " + foreignSchema2.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");
                                xsd_Writer = new XSDWriter(foreignSchema2.getSchema());
                                System.out.println(xsd_Writer.getXSDString());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Test of getSchema method, of class XSDParser.
     */
    @Test
    public void testGetSchema() {
//        System.out.println("getSchema");
//        XSDParser instance = null;
//        Schema expResult = null;
//        Schema result = instance.getSchema();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of parseXSD method, of class XSDParser.
     */
    @Test
    public void testParseXSD() throws Exception {
//        System.out.println("parseXSD");
//        File file = null;
//        XSDParser instance = null;
//        Document expResult = null;
//        Document result = instance.parseXSD(file);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

}