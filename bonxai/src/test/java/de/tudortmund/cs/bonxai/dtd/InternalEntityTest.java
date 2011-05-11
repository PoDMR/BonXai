package de.tudortmund.cs.bonxai.dtd;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of class InternalEntity
 * @author Lars Schmidt
 */
public class InternalEntityTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }


    /**
     * Test of getValue method, of class InternalEntity.
     */
    @Test
    public void testGetValue() {
        InternalEntity instance = new InternalEntity("name", "value");
        String expResult = "value";
        String result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class InternalEntity.
     */
    @Test
    public void testSetValue() {
        InternalEntity instance = new InternalEntity("name", "value");
        assertEquals("value", instance.getValue());
        instance.setValue("new value");
        assertEquals("new value", instance.getValue());
    }

}