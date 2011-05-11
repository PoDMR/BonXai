package de.tudortmund.cs.bonxai.xsd;

import static org.junit.Assert.fail;

import org.junit.*;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.SimpleType;

public class AttributeTest extends junit.framework.TestCase {

    @Test
    public void testCreateAttributeNameOnly() {
        Attribute attribute = new Attribute("{}someName");
        assertEquals(attribute.simpleTypeRef, null);
        assertEquals(attribute.name, "{}someName");

    }

    @Test
    public void testCreateAttributeNameAndTypeSuccess() {
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(
            "someType",
            new SimpleType("{}someType", null)
        );
        Attribute attribute = new Attribute("{}someName", typeRef);
        assertEquals(typeRef, attribute.simpleTypeRef);
        assertEquals("{}someName", attribute.name);

    }

    /*
     * Using (expected=...) annotation parameter does not work.
     */
    @Test
    public void testCreateAttributeNameAndTypeFailure() {
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(
            "someType",
            new ComplexType("{}someType", null)
        );

        try
        {
            Attribute attribute = new Attribute("{}someName", typeRef);
            fail("Exception not thrown on ComplexType for Attribute.");
        }
        catch (UnexpectedTypeException e) {}
    }

    @Test
    public void testGetName() {
        Attribute attribute = new Attribute("{}someName");
        assertEquals(attribute.getName(), "{}someName");
    }

    @Test
    public void testGetSimpleTypeWithoutRef() {
        Attribute attribute = new Attribute("{}someName");
        assertEquals(attribute.getSimpleType(), null);
    }

    @Test
    public void testGetSimpleTypeWithRef() {
        Attribute attribute = new Attribute("{}someName");
        SimpleType simpleType = new SimpleType("{}someType", null);
        SymbolTableRef<Type> simpleTypeRef= new SymbolTableRef<Type>("someKey", simpleType);
        attribute.simpleTypeRef = simpleTypeRef;
        assertEquals(attribute.getSimpleType(), simpleType);
    }

    @Test
    public void testSetSimpleTypeSuccess() {
        Attribute attribute = new Attribute("{}someName");
        SimpleType simpleType = new SimpleType("{}someType", null);
        SymbolTableRef<Type> simpleTypeRef= new SymbolTableRef<Type>("someKey", simpleType);
        attribute.setSimpleType(simpleTypeRef);
        assertEquals(attribute.simpleTypeRef.getReference(), simpleType);
    }

    @Test
    public void testSetComplexTypeFailure() {
        Attribute attribute = new Attribute("{}someName");
        ComplexType simpleType = new ComplexType("{}someType", null);
        SymbolTableRef<Type> simpleTypeRef= new SymbolTableRef<Type>("someKey", simpleType);
        try
        {
            attribute.setSimpleType(simpleTypeRef);
            fail( "Exception not thrown on ComplexType for Attribute.");
        }
        catch (UnexpectedTypeException e) {}
    }

    @Test
    public void testSetGetUse()
    {
        Attribute attribute = new Attribute("{}someName");

        assertEquals(AttributeUse.Optional, attribute.getUse());

        attribute.setUse(AttributeUse.Required);

        assertEquals(AttributeUse.Required, attribute.getUse());

        attribute.setUse(AttributeUse.Prohibited);

        assertEquals(AttributeUse.Prohibited, attribute.getUse());

        attribute.setUse(AttributeUse.Optional);

        assertEquals(AttributeUse.Optional, attribute.getUse());
    }

    public void testSetGetDefault()
    {
        Attribute attribute = new Attribute("{}someName");

        assertNull(attribute.getDefault());

        attribute.setDefault("default");

        assertEquals("default", attribute.getDefault());

        attribute.setDefault(null);

        assertNull(attribute.getDefault());
    }

    public void testSetGetFixed()
    {
        Attribute attribute = new Attribute("{}someName");

        assertNull(attribute.getFixed());

        attribute.setFixed("default");

        assertEquals("default", attribute.getFixed());

        attribute.setFixed(null);

        assertNull(attribute.getFixed());
    }
}
