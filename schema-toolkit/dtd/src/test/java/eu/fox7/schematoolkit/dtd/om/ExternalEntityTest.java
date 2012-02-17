package eu.fox7.schematoolkit.dtd.om;

import org.junit.Before;
import org.junit.Test;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.ExternalEntity;

/**
 * Test of class ExternalEntity
 * @author Lars Schmidt
 */
public class ExternalEntityTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }
    
    /**
     * Test of getPublicID method, of class ExternalEntity.
     */
    @Test
    public void testGetPublicID() {
        ExternalEntity instance = new ExternalEntity("name", "publicID", "systemID");
        String expResult = "publicID";
        String result = instance.getPublicID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSystemID method, of class ExternalEntity.
     */
    @Test
    public void testGetSystemID() {
        ExternalEntity instance = new ExternalEntity("name", "publicID", "systemID");
        String expResult = "systemID";
        String result = instance.getSystemID();
        assertEquals(expResult, result);
    }


}