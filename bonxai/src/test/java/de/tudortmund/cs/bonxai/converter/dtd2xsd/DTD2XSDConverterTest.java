package de.tudortmund.cs.bonxai.converter.dtd2xsd;

import de.tudortmund.cs.bonxai.dtd.*;
import de.tudortmund.cs.bonxai.dtd.parser.*;
import de.tudortmund.cs.bonxai.dtd.writer.*;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.util.Iterator;
import org.junit.Test;

/**
 * Test of class DTD2XSDConverter
 * @author Lars Schmidt
 */
public class DTD2XSDConverterTest extends junit.framework.TestCase {

    /**
     * Test of convert method, of class DTD2XSDConverter.
     * @throws Exception 
     */
    @Test
    public void testConvert() throws Exception {

        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/dtd2xsd/dtds/attributeConverterTests/attributes.xml");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/elements.xml");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/elementNamespaces.xml");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/XMLSchema.xsd");
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/dtd/parser/dtds/docbook_dtd.xml");

//        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/dtd2xsd/dtds/dtd1.xml");

        DTDSAXParser dtdParser = new DTDSAXParser(false);
        DocumentTypeDefinition dtd = dtdParser.parseXML(filePath);

//        DTDWriter dtdWriter = new DTDWriter(dtd);
//        dtdWriter.setWriteEntities(true);
//        System.out.println(dtdWriter.getExternalSubsetString());
//        System.out.println("\n--------------------------------------------------------------------------\n");

        DTD2XSDConverter instance = new DTD2XSDConverter(dtd);

        XSDSchema xmlSchema = instance.convert(true);
//        Schema xmlSchema = instance.convert(true);
//        Schema xmlSchema = instance.convert(true, false);
//        Schema xmlSchema = instance.convert(true, false);
//        Schema xmlSchema = instance.convert("http://www.mySafariNamespace.org", "safari", true);
//          Schema xmlSchema = instance.convert("http://www.example.org/bob", "bob", true);


        XSDWriter xsd_Writer = new XSDWriter(xmlSchema);
        System.out.println(xsd_Writer.getXSDString());

        if (!xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                System.out.println("\n--------------------------------------------------------------------------\n" + foreignSchema.getClass().getName() + " - " + foreignSchema.getSchemaLocation() + "\n--------------------------------------------------------------------------\n");
                xsd_Writer = new XSDWriter(foreignSchema.getSchema());
                System.out.println(xsd_Writer.getXSDString());
            }
        }
    }
}
