package de.tudortmund.cs.bonxai.dtd.parser;

import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.dtd.AttributeType;
import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.dtd.Element;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class DTDEventHandler
 * @author Lars Schmidt
 */
public class DTDEventHandlerTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of startDTD method, of class DTDEventHandler.
     * @throws Exception 
     */
    @Test
    public void testStartDTD() throws Exception {
        String rootElementName = "rootElement";
        String publicId = "test1";
        String systemId = "test2";
        DTDEventHandler instance = new DTDEventHandler();
        instance.startDTD(rootElementName, publicId, systemId);
        assertEquals("test1", instance.getDTD().getPublicID());
        assertEquals("test2", instance.getDTD().getSystemID());
        assertEquals("rootElement", instance.getDTD().getRootElement().getName());
        assertEquals(1, instance.getDTD().getElementSymbolTable().getAllReferencedObjects().size());
    }

    /**
     * Test of elementDecl method, of class DTDEventHandler.
     * @throws Exception 
     */
    @Test
    public void testElementDecl() throws Exception {
        String elementName = "elementName";
        String elementContentModel = "ANY";
        DTDEventHandler instance = new DTDEventHandler();
        instance.elementDecl(elementName, elementContentModel);
        assertEquals(1, instance.getDTD().getElementSymbolTable().getAllReferencedObjects().size());
        assertEquals("elementName", instance.getDTD().getElementSymbolTable().getReference("elementName").getKey());
        assertEquals("elementName", instance.getDTD().getElementSymbolTable().getReference("elementName").getReference().getName());
        assertTrue(instance.getDTD().getElementSymbolTable().getReference("elementName").getReference().getParticle() instanceof AnyPattern);
    }

    /**
     * Test of attributeDecl method, of class DTDEventHandler.
     * @throws Exception
     */
    @Test
    public void testAttributeDecl() throws Exception {
        String elementName = "elementName";
        String elementContentModel = "ANY";
        DTDEventHandler instance = new DTDEventHandler();
        instance.elementDecl(elementName, elementContentModel);
        String attributeName = "attributeName";
        String type = "NMTOKEN";
        String mode = "#IMPLIED";
        String value = null;
        instance.attributeDecl(elementName, attributeName, type, mode, value);
        assertEquals(1, instance.getDTD().getElementSymbolTable().getAllReferencedObjects().size());
        assertEquals("elementName", instance.getDTD().getElementSymbolTable().getReference("elementName").getKey());
        assertEquals("elementName", instance.getDTD().getElementSymbolTable().getReference("elementName").getReference().getName());
        assertTrue(instance.getDTD().getElementSymbolTable().getReference("elementName").getReference().getParticle() instanceof AnyPattern);
        Element elementResult = instance.getDTD().getElementSymbolTable().getReference("elementName").getReference();
        assertEquals(1, elementResult.getAttributes().size());
        assertEquals("attributeName", elementResult.getAttributes().getFirst().getName());
        assertEquals(AttributeType.NMTOKEN, elementResult.getAttributes().getFirst().getType());
    }

    /**
     * Test of internalEntityDecl method, of class DTDEventHandler.
     * @throws Exception 
     */
    @Test
    public void testInternalEntityDecl() throws Exception {
        String name = "entityName";
        String value = "entityValue";
        DTDEventHandler instance = new DTDEventHandler();
        instance.internalEntityDecl(name, value);
        assertEquals(1, instance.getDTD().getInternalEntitySymbolTable().getAllReferencedObjects().size());
        assertEquals("entityName", instance.getDTD().getInternalEntitySymbolTable().getReference("entityName").getKey());
        assertEquals("entityName", instance.getDTD().getInternalEntitySymbolTable().getReference("entityName").getReference().getName());
        assertEquals("entityValue", instance.getDTD().getInternalEntitySymbolTable().getReference("entityName").getReference().getValue());
    }

    /**
     * Test of getDTD method, of class DTDEventHandler.
     */
    @Test
    public void testGetDTD() {
        DTDEventHandler instance = new DTDEventHandler();
        DocumentTypeDefinition result = instance.getDTD();
        assertTrue(result != null);
        assertTrue(result instanceof DocumentTypeDefinition);
    }

    /**
     * Test of setDebug method, of class DTDEventHandler.
     */
    @Test
    public void testSetDebug() {
        DTDEventHandler instance = new DTDEventHandler();
        assertFalse(instance.getDebug());
        instance.setDebug(true);
        assertTrue(instance.getDebug());
        instance.setDebug(false);
        assertFalse(instance.getDebug());
    }

    /**
     * Test of getDebug method, of class DTDEventHandler.
     */
    @Test
    public void testGetDebug() {
        DTDEventHandler instance = new DTDEventHandler();
        assertFalse(instance.getDebug());
        instance.setDebug(true);
        assertTrue(instance.getDebug());
        instance.setDebug(false);
        assertFalse(instance.getDebug());
    }

    /**
     * Test of notationDecl method, of class DTDEventHandler.
     */
    @Test
    public void testNotationDecl() {
        String name = "notationName";
        String publicID = "pub";
        DTDEventHandler instance = new DTDEventHandler();
        instance.notationDecl(name, publicID, null);
        assertEquals(1, instance.getDTD().getNotationSymbolTable().getAllReferencedObjects().size());
        assertEquals("notationName", instance.getDTD().getNotationSymbolTable().getReference("notationName").getKey());
        assertEquals("notationName", instance.getDTD().getNotationSymbolTable().getReference("notationName").getReference().getName());
        assertEquals("pub", instance.getDTD().getNotationSymbolTable().getReference("notationName").getReference().getIdentifier());
        instance.notationDecl("second", null, "sys");
        assertEquals(2, instance.getDTD().getNotationSymbolTable().getAllReferencedObjects().size());
        assertEquals("sys", instance.getDTD().getNotationSymbolTable().getReference("second").getReference().getIdentifier());

    }
}