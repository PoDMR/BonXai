package de.tudortmund.cs.bonxai.converter.xsd2dtd;

import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.dtd.writer.DTDWriter;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import org.junit.Test;

/**
 * Test of class XSD2DTDConverter
 * @author Lars Schmidt
 */
public class XSD2DTDConverterTest extends junit.framework.TestCase {

    /**
     * Test of convert method, of class XSD2DTDConverter.
     * @throws Exception 
     */
    @Test
    public void testConvert() throws Exception {
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/group.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/all.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/any.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/countingPattern.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/countingPattern_small.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/complexExample1.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/XMLSchema.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/wim/xhtml1-strict.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/docbook.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/constraints.xsd");
//        String uri = new String("tests/de/tudortmund/cs/bonxai/converter/xsd2dtd/xsds/constraintHandlerTests/constraints_global.xsd");


//        String uri = "tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/complexContentTests/complexcontent_valid.xsd";

                String uri = "http://www.w3.org/2001/XMLSchema.xsd";


        

        XSDParser instance = new XSDParser(false, false);
        XSDSchema xmlSchema = instance.parse(uri);

        XSDWriter xsd_Writer = new XSDWriter(xmlSchema);
        System.out.println(xsd_Writer.getXSDString());

        XSD2DTDConverter xsd2dtdConverter = new XSD2DTDConverter(xmlSchema, false);
        DocumentTypeDefinition dtd2 = xsd2dtdConverter.convert();

        DTDWriter dtdWriter = new DTDWriter(dtd2);
        dtdWriter.setWriteEntities(false);
        System.out.println(dtdWriter.getExternalSubsetString());
    }
}
