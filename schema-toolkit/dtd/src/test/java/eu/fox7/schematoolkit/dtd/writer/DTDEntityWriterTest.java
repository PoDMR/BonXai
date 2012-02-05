package eu.fox7.schematoolkit.dtd.writer;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.InternalEntity;
import eu.fox7.schematoolkit.dtd.writer.DTDEntityWriter;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDInternalEntityEmptyNameException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDInternalEntityValueNullException;

import org.junit.Test;

/**
 * Test of class DTDEntityWriter
 * @author Lars Schmidt
 */
public class DTDEntityWriterTest extends junit.framework.TestCase {

    /**
     * Test of getInternalEntitiesString method, of class DTDEntityWriter.
     * @throws Exception 
     */
    @Test
    public void testGetInternalEntitiesString() throws Exception {

        DocumentTypeDefinition dtd = new DocumentTypeDefinition();
        dtd.addInternalEntity(new InternalEntity("entity1", "valueOne"));
        dtd.addInternalEntity(new InternalEntity("entity2", "valueTwo"));
        dtd.addInternalEntity(new InternalEntity("entity3", "valueThree"));
        DTDEntityWriter entityWriter = new DTDEntityWriter(dtd);

        String expResult = "<!ENTITY entity1 \"valueOne\">\n<!ENTITY entity2 \"valueTwo\">\n<!ENTITY entity3 \"valueThree\">\n";
        String result = entityWriter.getInternalEntitiesString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getInternalEntitiesString method with exception DTDInternalEntityEmptyNameException, of class DTDEntityWriter.
     * @throws Exception
     */
    @Test
    public void testGetInternalEntitiesStringWithEmptyName() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtd.addInternalEntity(new InternalEntity("", "valueOne"));
            dtd.addInternalEntity(new InternalEntity("entity2", "valueTwo"));
            dtd.addInternalEntity(new InternalEntity("entity3", "valueThree"));
            DTDEntityWriter entityWriter = new DTDEntityWriter(dtd);

            entityWriter.getInternalEntitiesString();

        } catch (DTDInternalEntityEmptyNameException ex) {
            return;
        }
        fail("The InternalEntity has no name, but this wasn't detected");
    }

    /**
     * Test of getInternalEntitiesString method with exception DTDInternalEntityEmptyNameException, of class DTDEntityWriter.
     * @throws Exception
     */
    @Test
    public void testGetInternalEntitiesStringWithEmptyNameNull() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtd.addInternalEntity(new InternalEntity(null, "valueOne"));
            dtd.addInternalEntity(new InternalEntity("entity2", "valueTwo"));
            dtd.addInternalEntity(new InternalEntity("entity3", "valueThree"));
            DTDEntityWriter entityWriter = new DTDEntityWriter(dtd);

            entityWriter.getInternalEntitiesString();

        } catch (DTDInternalEntityEmptyNameException ex) {
            return;
        }
        fail("The InternalEntity has no name, but this wasn't detected");
    }

    /**
     * Test of getInternalEntitiesString method with exception DTDInternalEntityEmptyNameException, of class DTDEntityWriter.
     * @throws Exception
     */
    @Test
    public void testGetInternalEntitiesStringWithNullValue() throws Exception {
        try {
            DocumentTypeDefinition dtd = new DocumentTypeDefinition();
            dtd.addInternalEntity(new InternalEntity("bla", null));
            dtd.addInternalEntity(new InternalEntity("entity2", "valueTwo"));
            dtd.addInternalEntity(new InternalEntity("entity3", "valueThree"));
            DTDEntityWriter entityWriter = new DTDEntityWriter(dtd);

            entityWriter.getInternalEntitiesString();

        } catch (DTDInternalEntityValueNullException ex) {
            return;
        }
        fail("The InternalEntity has no value (null), but this wasn't detected");
    }

    /**
     * Test of getExternalEntitiesString method, of class DTDEntityWriter.
     */
//    @Test
//    public void testGetExternalEntitiesString() {
//        // The method getExternalEntitiesString is not implemented yet, because
//        // all external DTD entities are resolved by the SAXParser.
//    }
}
