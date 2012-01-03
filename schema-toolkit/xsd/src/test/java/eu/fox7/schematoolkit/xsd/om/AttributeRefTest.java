package eu.fox7.schematoolkit.xsd.om;

import org.junit.*;

import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeRef;
import eu.fox7.schematoolkit.xsd.om.AttributeUse;

public class AttributeRefTest extends junit.framework.TestCase {

    @Test
    public void testCreateAttributeRefRefOnly() {
        SymbolTableRef<Attribute> symbolTableRef = new SymbolTableRef<Attribute>("someKey");
        AttributeRef attributeRef = new AttributeRef(symbolTableRef);
        assertEquals(attributeRef.reference, symbolTableRef);
        assertFalse(attributeRef.getUse().equals(AttributeUse.Required));
    }

    @Test
    public void testCreateAttributeRefRefAndRequired() {
        SymbolTableRef<Attribute> symbolTableRef = new SymbolTableRef<Attribute>("someKey");
        AttributeRef attributeRef = new AttributeRef(symbolTableRef);
        attributeRef.setUse(AttributeUse.Required);
        assertEquals(attributeRef.reference, symbolTableRef);
        assertTrue(attributeRef.getUse().equals(AttributeUse.Required));
    }

    @Test
    public void testGetAttribute() {
        SymbolTableRef<Attribute> symbolTableRef = new SymbolTableRef<Attribute>("someKey");
        Attribute attribute = new Attribute("{}someName");
        symbolTableRef.setReference(attribute);
        AttributeRef attributeRef = new AttributeRef(symbolTableRef);
        assertEquals(attributeRef.getAttribute(), attribute);
    }

    @Test
    public void testSetRequired() {
        SymbolTableRef<Attribute> symbolTableRef = new SymbolTableRef<Attribute>("someKey");
        Attribute attribute = new Attribute("{}someName");
        symbolTableRef.setReference(attribute);
        AttributeRef attributeRef = new AttributeRef(symbolTableRef);

        assertFalse(attributeRef.getUse().equals(AttributeUse.Required));

        attributeRef.setUse(AttributeUse.Required);

        assertTrue(attributeRef.getUse().equals(AttributeUse.Required));

        attributeRef.setUse(AttributeUse.Optional);
        
        assertFalse(attributeRef.getUse().equals(AttributeUse.Required));
    }

}
