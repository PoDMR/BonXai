package eu.fox7.schematoolkit.bonxai.om;

import org.junit.Test;
import org.junit.Assert.*;

import static org.junit.Assert.*;

import eu.fox7.schematoolkit.common.AttributeGroupReference;

/**
 *
 */
public class AttributeGroupReferenceTest extends junit.framework.TestCase {

    /**
     * Test of getAttributePattern method, of class AttributeGroupElement.
     */
    @Test
    public void testCreateAttributeReference() {
        BonxaiAttributeGroup attrGroup = new BonxaiAttributeGroup("fooBar", new AttributePattern());
        SymbolTableRef<BonxaiAttributeGroup> symbRef =
            new SymbolTableRef<BonxaiAttributeGroup>("fooBar", attrGroup);

        AttributeGroupReference attrGroupRef = new AttributeGroupReference(symbRef);

        assertSame(attrGroup, attrGroupRef.getGroupElement());
        assertEquals("fooBar", attrGroupRef.getName());
    }
}
