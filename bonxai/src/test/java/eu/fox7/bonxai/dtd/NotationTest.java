package eu.fox7.bonxai.dtd;

import org.junit.Before;
import org.junit.Test;

import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.PublicNotation;
import eu.fox7.bonxai.dtd.SystemNotation;

/**
 * Test of classes PublicNotation and SystemNotation
 * @author Lars Schmidt
 */
public class NotationTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of getName method, of class Notation.
     */
    @Test
    public void testGetName() {
        SystemNotation instance = new SystemNotation("name", "systemID");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class PublicNotation.
     */
    @Test
    public void testGetIdentifierPublic() {
        PublicNotation instance = new PublicNotation("name", "publicID");
        String expResult = "publicID";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class SystemNotation.
     */
    @Test
    public void testGetIdentifierSystem() {
        SystemNotation instance = new SystemNotation("name", "systemID");
        String expResult = "systemID";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }
}