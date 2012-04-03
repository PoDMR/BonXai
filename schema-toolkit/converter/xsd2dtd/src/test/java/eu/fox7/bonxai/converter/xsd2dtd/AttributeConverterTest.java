package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.bonxai.converter.xsd2dtd.AttributeConverter;
import eu.fox7.bonxai.converter.xsd2dtd.XSD2DTDConverter;
import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.AttributeType;
import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.AttributeRef;
import eu.fox7.schematoolkit.xsd.om.AttributeUse;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AttributeConverterTest
 * @author Lars Schmidt
 */
public class AttributeConverterTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    AttributeConverter attributeConverter;

    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema(XSD2DTDConverter.XMLSCHEMA_NAMESPACE);
        attributeConverter = new AttributeConverter(schema);
    }

    /**
     * Test of convertAttributes method, of class AttributeConverter.
     */
    @Test
    public void testConvertAttributes_without_Attributes() {
        Element element = new Element("{}elementName");

        LinkedList<Attribute> result = attributeConverter.convertAttributes(element);

        assertEquals(0, result.size());
    }

    /**
     * Test of convertAttributes method, of class AttributeConverter.
     */
    @Test
    public void testConvertAttributes() {
        Element element = new Element("{}elementName");
        ComplexType complexType = new ComplexType("{}type", null);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference("{}type", complexType));

        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                "default",
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        complexType.addAttribute(xsdAttribute);

        LinkedList<Attribute> result = attributeConverter.convertAttributes(element);

        assertEquals(1, result.size());

        Attribute dtdAttribute = result.getFirst();

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute() {
        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with AttributeRef, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_AttributeRef() {
        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        AttributeRef attributeRef = new AttributeRef(schema.getAttributeSymbolTable().updateOrCreateReference("{}myAttribute", xsdAttribute));

        Attribute dtdAttribute = attributeConverter.convertAttribute(attributeRef);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with AnyAttribute, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_AnyAttribute() {

        AnyAttribute anyAttribute = new AnyAttribute();

        Attribute dtdAttribute = attributeConverter.convertAttribute(anyAttribute);

        assertEquals(null, dtdAttribute);
    }

    /**
     * Test of convertAttribute method with AttributeGroup, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_AttributeGroup() {

        AttributeGroup attributeGroup = new AttributeGroup("{}myAGroup");

        Attribute dtdAttribute = attributeConverter.convertAttribute(attributeGroup);

        assertEquals(null, dtdAttribute);
    }

    /**
     * Test of convertAttribute method with use: optional, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_optional() {
        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with use: required, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_required() {
        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                null,
                null,
                AttributeUse.Required,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.REQUIRED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with use: prohibited, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_prohibited() {
        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                null,
                null,
                AttributeUse.Prohibited,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(null, dtdAttribute);
    }

    /**
     * Test of convertAttribute method with default-value, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_default() {
        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                "default",
                null,
                null,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(null, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals("default", dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with fixed-value, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_fixed() {
        SimpleType simpleType = new SimpleType("{}string", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}string", simpleType),
                null,
                "fixed",
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.FIXED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals("fixed", dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with no type, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_no_type() {
        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                null,
                null,
                null,
                null,
                false,
                null,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type: xs:boolean, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_boolean() {
        SimpleType simpleType = new SimpleType("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}boolean", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}boolean", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.NMTOKEN, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type: xs:IDREFS, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_IDREFS() {
        SimpleType simpleType = new SimpleType("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}IDREFS", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}IDREFS", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.IDREFS, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type: xs:NMTOKENS, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_NMTOKENS() {
        SimpleType simpleType = new SimpleType("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}NMTOKENS", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}NMTOKENS", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.NMTOKENS, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type: xs:ID, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_ID() {
        SimpleType simpleType = new SimpleType("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}ID", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}ID", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.ID, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type: xs:IDREF, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_IDREF() {
        SimpleType simpleType = new SimpleType("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}IDREF", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}IDREF", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.IDREF, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type: xs:ENTITY, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_ENTITY() {
        SimpleType simpleType = new SimpleType("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}ENTITY", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}ENTITY", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.ENTITY, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type: mySimpleType, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_mySimpleType() {

        SimpleType simpleType = new SimpleType("{}mySimpleType", null);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}mySimpleType", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type with inheritance -> enumeration, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_inheritance_enumeration() {

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction();
        LinkedList<String> enumerationList = new LinkedList<String>();
        enumerationList.add("value1");
        enumerationList.add("value2");
        enumerationList.add("value3");
        simpleContentRestriction.addEnumeration(enumerationList);

        SimpleType simpleType = new SimpleType("{}mySimpleType", simpleContentRestriction);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}mySimpleType", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(3, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("value1", dtdAttribute.getEnumerationOrNotationTokens().toArray()[0]);
        assertEquals("value2", dtdAttribute.getEnumerationOrNotationTokens().toArray()[1]);
        assertEquals("value3", dtdAttribute.getEnumerationOrNotationTokens().toArray()[2]);
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.ENUMERATION, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of convertAttribute method with type with inheritance -> enumeration without values, of class AttributeConverter.
     */
    @Test
    public void testConvertAttribute_inheritance_enumeration_without_values() {

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction();
        LinkedList<String> enumerationList = new LinkedList<String>();
        simpleContentRestriction.addEnumeration(enumerationList);

        SimpleType simpleType = new SimpleType("{}mySimpleType", simpleContentRestriction);

        eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(
                "{}myAttribute",
                schema.getTypeSymbolTable().updateOrCreateReference("{}mySimpleType", simpleType),
                null,
                null,
                AttributeUse.Optional,
                Boolean.TRUE,
                XSDSchema.Qualification.qualified,
                null);

        Attribute dtdAttribute = attributeConverter.convertAttribute(xsdAttribute);

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("myAttribute", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }
}
