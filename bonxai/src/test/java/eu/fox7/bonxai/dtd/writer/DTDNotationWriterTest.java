package eu.fox7.bonxai.dtd.writer;

import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.PublicNotation;
import eu.fox7.bonxai.dtd.SystemNotation;
import eu.fox7.bonxai.dtd.writer.DTDNotationWriter;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDNotationEmptyNameException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDNotationIdentifierNullException;

import org.junit.Test;

/**
 * Test of class DTDNotationWriter
 * @author Lars Schmidt
 */
public class DTDNotationWriterTest extends junit.framework.TestCase {

    /**
     * Test of getNotationsString method, of class DTDNotationWriter.
     * @throws Exception 
     */
    @Test
    public void testGetNotationsString() throws Exception {
        DocumentTypeDefinition dtd = new DocumentTypeDefinition();
        dtd.addNotation(new PublicNotation("name1", "publicOne"));
        dtd.addNotation(new SystemNotation("name2", "systemTwo"));

        DTDNotationWriter notationWriter = new DTDNotationWriter(dtd);

        String expResult = "<!NOTATION name1 PUBLIC \"publicOne\">\n<!NOTATION name2 SYSTEM \"systemTwo\">\n";
        String result = notationWriter.getNotationsString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNotationsString method with exception DTDNotationEmptyNameException (public), of class DTDNotationWriter.
     * @throws Exception
     */
    @Test
    public void testGetNotationsStringWithEmptyNamePublic() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtd.addNotation(new PublicNotation(null, "publicOne"));
            DTDNotationWriter notationWriter = new DTDNotationWriter(dtd);
            notationWriter.getNotationsString();

        } catch (DTDNotationEmptyNameException ex) {
            return;
        }
        fail("The PublicNotation has no name, but this wasn't detected");
    }

    /**
     * Test of getNotationsString method with exception DTDNotationEmptyNameException (system), of class DTDNotationWriter.
     * @throws Exception
     */
    @Test
    public void testGetNotationsStringWithEmptyNameSystem() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtd.addNotation(new SystemNotation(null, "systemOne"));
            DTDNotationWriter notationWriter = new DTDNotationWriter(dtd);
            notationWriter.getNotationsString();

        } catch (DTDNotationEmptyNameException ex) {
            return;
        }
        fail("The SystemNotation has no name, but this wasn't detected");
    }

    /**
     * Test of getNotationsString method with exception DTDNotationIdentifierNullException (public), of class DTDNotationWriter.
     * @throws Exception
     */
    @Test
    public void testGetNotationsStringWithIdentifierEmptyPublic() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtd.addNotation(new PublicNotation("name", null));
            DTDNotationWriter notationWriter = new DTDNotationWriter(dtd);
            notationWriter.getNotationsString();

        } catch (DTDNotationIdentifierNullException ex) {
            return;
        }
        fail("The PublicNotation has no publicID, but this wasn't detected");
    }

    /**
     * Test of getNotationsString method with exception DTDNotationIdentifierNullException (system), of class DTDNotationWriter.
     * @throws Exception
     */
    @Test
    public void testGetNotationsStringWithIdentifierEmptySystem() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtd.addNotation(new SystemNotation("name", null));
            DTDNotationWriter notationWriter = new DTDNotationWriter(dtd);
            notationWriter.getNotationsString();

        } catch (DTDNotationIdentifierNullException ex) {
            return;
        }
        fail("The SystemNotation has no publicID, but this wasn't detected");
    }

}
