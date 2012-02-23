package eu.fox7.schematoolkit.xsd.om;

import org.junit.*;

import eu.fox7.schematoolkit.common.AttributeGroupReference;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;

public class AttributeGroupRefTest extends junit.framework.TestCase {

    @Test
    public void testCreateAttributeGroupRef() {
    	QualifiedName name = new QualifiedName(Namespace.EMPTY_NAMESPACE, "localname");
        AttributeGroupReference attributeGroupRef = new AttributeGroupReference(name);
        assertEquals(attributeGroupRef.getName(), name);
    }
}
