package eu.fox7.schematoolkit.xsd.om;

import static org.junit.Assert.fail;

import org.junit.*;

import eu.fox7.schematoolkit.common.AttributeUse;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.UnexpectedTypeException;

public class AttributeTest extends junit.framework.TestCase {

    @Test
    public void testCreateAttributeNameOnly() {
    	QualifiedName attributeName= new QualifiedName(Namespace.EMPTY_NAMESPACE,"someName");
        Attribute attribute = new Attribute(attributeName);
        assertEquals(attribute.simpleTypeName, null);
        assertEquals(attribute.name, "{}someName");

    }

    @Test
    public void testCreateAttributeNameAndTypeSuccess() {
    	QualifiedName attributeName= new QualifiedName(Namespace.EMPTY_NAMESPACE,"someName");
        QualifiedName typeName = new QualifiedName(Namespace.EMPTY_NAMESPACE,"someType");
        Attribute attribute = new Attribute(attributeName, typeName);
        assertEquals(typeName, attribute.simpleTypeName);
        assertEquals(attributeName, attribute.name);

    }

    @Test
    public void testGetName() {
    	QualifiedName attributeName= new QualifiedName(Namespace.EMPTY_NAMESPACE,"someName");
        Attribute attribute = new Attribute(attributeName);
        assertEquals(attribute.getName(), attributeName);
    }

    @Test
    public void testSetGetUse()
    {
    	QualifiedName attributeName= new QualifiedName(Namespace.EMPTY_NAMESPACE,"someName");
        Attribute attribute = new Attribute(attributeName);

        assertEquals(AttributeUse.optional, attribute.getUse());

        attribute.setUse(AttributeUse.required);

        assertEquals(AttributeUse.required, attribute.getUse());

        attribute.setUse(AttributeUse.prohibited);

        assertEquals(AttributeUse.prohibited, attribute.getUse());

        attribute.setUse(AttributeUse.optional);

        assertEquals(AttributeUse.optional, attribute.getUse());
    }

    public void testSetGetDefault()
    {
    	QualifiedName attributeName= new QualifiedName(Namespace.EMPTY_NAMESPACE,"someName");
        Attribute attribute = new Attribute(attributeName);

        assertNull(attribute.getDefault());

        attribute.setDefault("default");

        assertEquals("default", attribute.getDefault());

        attribute.setDefault(null);

        assertNull(attribute.getDefault());
    }

    public void testSetGetFixed()
    {
    	QualifiedName attributeName= new QualifiedName(Namespace.EMPTY_NAMESPACE,"someName");
        Attribute attribute = new Attribute(attributeName);

        assertNull(attribute.getFixed());

        attribute.setFixed("default");

        assertEquals("default", attribute.getFixed());

        attribute.setFixed(null);

        assertNull(attribute.getFixed());
    }
}
