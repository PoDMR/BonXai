package eu.fox7.schematoolkit.dtd.writer;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.parser.DTDSAXParser;
import eu.fox7.schematoolkit.dtd.writer.DTDWriter;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDNoRootElementDefinedException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class DTDWriter
 * @author Lars Schmidt
 */
public class DTDWriterTest extends junit.framework.TestCase {

    private DTDSAXParser dtdParser;
    private DTDWriter dtdWriter;

    @Before
    @Override
    public void setUp() {
        this.dtdParser = new DTDSAXParser(false);
    }

    /**
     * Test of setWriteEntities method, of class DTDWriter.
     */
    @Test
    public void testSetWriteEntities() {
        try {
            dtdWriter = new DTDWriter(dtdParser.parseXML(this.getClass().getResource("/tests/eu/fox7/bonxai/dtd/writer/dtds/DTDWriterTests/writeEntites.xml").getFile()));
            assertFalse(dtdWriter.getExternalSubsetString().contains("ENTITY"));
            dtdWriter.setWriteEntities(true);
            assertTrue(dtdWriter.getExternalSubsetString().contains("ENTITY"));
        } catch (Exception ex) {
            fail("Parse error occured");
        }
    }

    /**
     * Test of getXMLContainingDTDString method, of class DTDWriter.
     * @throws Exception
     */
    @Test
    public void testGetXMLContainingDTDString() throws Exception {
        try {
            dtdWriter = new DTDWriter(dtdParser.parseXML(this.getClass().getResource("/tests/eu/fox7/bonxai/dtd/writer/dtds/DTDWriterTests/writeEntites.xml").getFile()));
            assertTrue(dtdWriter.getXMLWithFullDTDDeclarationString().contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
            assertTrue(dtdWriter.getXMLWithFullDTDDeclarationString().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE schema ["));
            assertTrue(dtdWriter.getXMLWithFullDTDDeclarationString().endsWith("]>\n<schema/>"));
        } catch (Exception ex) {
            fail("Parse error occured");
        }
    }

    /**
     * Test of getDTDString method, of class DTDWriter.
     * @throws Exception 
     */
    @Test
    public void testGetDTDString() throws Exception {
        try {
            dtdWriter = new DTDWriter(dtdParser.parseXML(this.getClass().getResource("/tests/eu/fox7/bonxai/dtd/writer/dtds/DTDWriterTests/getDTDString.xml").getFile()), true);
            assertFalse(dtdWriter.getExternalSubsetString().contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));

            String fileContent = "<!ENTITY % appinfo \"appinfo\">\n<!ENTITY % import \"import\">\n<!ENTITY test \"testValue\">\n\n<!NOTATION test PUBLIC \"test\">\n\n<!ELEMENT group EMPTY>\n<!ATTLIST group id ID #IMPLIED>\n<!ATTLIST group name NMTOKEN #IMPLIED>\n\n";
            assertEquals(fileContent, dtdWriter.getExternalSubsetString());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            fail("Parse error occured");
        }
    }

    /**
     * Test of getXMLContainingDTDString method, of class DTDWriter.
     * @throws Exception
     */
    @Test
    public void testgetXMLContainingDTDStringWithDTDNoRootElementDefinedException() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtdWriter = new DTDWriter(dtd);
            dtdWriter.getXMLWithFullDTDDeclarationString();
        } catch (DTDNoRootElementDefinedException ex) {
            return;
        }
        fail("The DTD has no root element, but this wasn't detected");
    }


}
