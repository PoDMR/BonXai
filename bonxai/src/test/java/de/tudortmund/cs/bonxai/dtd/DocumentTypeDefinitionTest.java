package de.tudortmund.cs.bonxai.dtd;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class DocumentTypeDefinition
 * @author Lars Schmidt
 */
public class DocumentTypeDefinitionTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of getRootElement method, of class DocumentTypeDefinition.
     */
    @Test
    public void testGetRootElement() {
        Element expResult = new Element("root");
        dtd.setRootElementRef(new ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("root", expResult)));
        Element result = dtd.getRootElement();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRootElementRef method, of class DocumentTypeDefinition.
     */
    @Test
    public void testSetRootElementRef() {
        Element expResult = new Element("root");
        Element result = dtd.getRootElement();
        assertTrue(result == null);
        dtd.setRootElementRef(new ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("root", expResult)));
        Element result2 = dtd.getRootElement();
        assertEquals(expResult, result2);
    }

    /**
     * Test of getElementSymbolTable method, of class DocumentTypeDefinition.
     */
    @Test
    public void testGetElementSymbolTable() {
        assertTrue(dtd.getElementSymbolTable().getAllReferencedObjects().isEmpty());
        Element element = new Element("root");
        dtd.setRootElementRef(new ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("root", element)));
        assertFalse(dtd.getElementSymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(element, dtd.getElementSymbolTable().getReference("root").getReference());
    }

    /**
     * Test of registerElementFromContentModel method, of class DocumentTypeDefinition.
     */
    @Test
    public void testRegisterElementFromContentModel() {
        assertTrue(dtd.getElementSymbolTable().getAllReferencedObjects().isEmpty());
        Element element = new Element("root");
        dtd.registerElementFromContentModel(element);
        assertFalse(dtd.getElementSymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(element, dtd.getElementSymbolTable().getReference("root").getReference());
    }

    /**
     * Test of addInternalEntity method, of class DocumentTypeDefinition.
     */
    @Test
    public void testAddInternalEntity() {
        assertTrue(dtd.getInternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        InternalEntity entity = new InternalEntity("name", "value");
        dtd.addInternalEntity(entity);
        assertFalse(dtd.getInternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(entity, dtd.getInternalEntitySymbolTable().getReference("name").getReference());
    }

    /**
     * Test of addExternalEntity method, of class DocumentTypeDefinition.
     */
    @Test
    public void testAddExternalEntity() {
        assertTrue(dtd.getExternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        ExternalEntity entity = new ExternalEntity("name", "publicID", "systemID");
        dtd.addExternalEntity(entity);
        assertFalse(dtd.getExternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(entity, dtd.getExternalEntitySymbolTable().getReference("name").getReference());
    }

    /**
     * Test of getInternalEntitySymbolTable method, of class DocumentTypeDefinition.
     */
    @Test
    public void testGetInternalEntitySymbolTable() {
        assertTrue(dtd.getInternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        InternalEntity entity = new InternalEntity("name", "value");
        dtd.addInternalEntity(entity);
        assertFalse(dtd.getInternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(entity, dtd.getInternalEntitySymbolTable().getReference("name").getReference());
    }

    /**
     * Test of getExternalEntitySymbolTable method, of class DocumentTypeDefinition.
     */
    @Test
    public void testGetExternalEntitySymbolTable() {
        assertTrue(dtd.getExternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        ExternalEntity entity = new ExternalEntity("name", "publicID", "systemID");
        dtd.addExternalEntity(entity);
        assertFalse(dtd.getExternalEntitySymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(entity, dtd.getExternalEntitySymbolTable().getReference("name").getReference());
    }

    /**
     * Test of addNotation method, of class DocumentTypeDefinition.
     */
    @Test
    public void testAddNotation() {
        assertTrue(dtd.getNotationSymbolTable().getAllReferencedObjects().isEmpty());
        PublicNotation notation = new PublicNotation("name", "publicID");
        dtd.addNotation(notation);
        assertFalse(dtd.getNotationSymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(notation, dtd.getNotationSymbolTable().getReference("name").getReference());
    }

    /**
     * Test of getNotationSymbolTable method, of class DocumentTypeDefinition.
     */
    @Test
    public void testGetNotationSymbolTable() {
        assertTrue(dtd.getNotationSymbolTable().getAllReferencedObjects().isEmpty());
        SystemNotation notation = new SystemNotation("name", "systemID");
        dtd.addNotation(notation);
        assertFalse(dtd.getNotationSymbolTable().getAllReferencedObjects().isEmpty());
        assertEquals(notation, dtd.getNotationSymbolTable().getReference("name").getReference());
    }
}
