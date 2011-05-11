package de.tudortmund.cs.bonxai.xsd;

import org.junit.*;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.AttributeGroupRef;

public class AttributeGroupRefTest extends junit.framework.TestCase {

    @Test
    public void testCreateAttributeGroupRef() {
        SymbolTableRef<AttributeGroup> symbolTableRef = new SymbolTableRef<AttributeGroup>("someKey");
        AttributeGroupRef attributeGroupRef = new AttributeGroupRef(symbolTableRef);
        assertEquals(attributeGroupRef.attributeGroupRef, symbolTableRef);
    }

    @Test
    public void testGetAttributeGroup() {
        SymbolTableRef<AttributeGroup> symbolTableRef = new SymbolTableRef<AttributeGroup>("someKey");
        AttributeGroup attributeGroup = new AttributeGroup("{}someName");
        symbolTableRef.setReference(attributeGroup);
        AttributeGroupRef attributeGroupRef = new AttributeGroupRef(symbolTableRef);
        assertEquals(attributeGroupRef.getAttributeGroup(), attributeGroup);
    }

}
