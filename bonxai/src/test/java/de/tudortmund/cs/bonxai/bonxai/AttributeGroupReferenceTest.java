package de.tudortmund.cs.bonxai.bonxai;

import org.junit.Test;
import org.junit.Assert.*;

import static org.junit.Assert.*;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;

/**
 *
 */
public class AttributeGroupReferenceTest extends junit.framework.TestCase {

    /**
     * Test of getAttributePattern method, of class AttributeGroupElement.
     */
    @Test
    public void testCreateAttributeReference() {
        AttributeGroupElement attrGroup = new AttributeGroupElement("fooBar", new AttributePattern());
        SymbolTableRef<AttributeGroupElement> symbRef =
            new SymbolTableRef<AttributeGroupElement>("fooBar", attrGroup);

        AttributeGroupReference attrGroupRef = new AttributeGroupReference(symbRef);

        assertSame(attrGroup, attrGroupRef.getGroupElement());
        assertEquals("fooBar", attrGroupRef.getName());
    }
}
