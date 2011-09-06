package eu.fox7.bonxai.converter.xsd2relaxng;

import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.HashMap;

import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.converter.xsd2relaxng.AttributeParticleConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.InheritanceInformationCollector;
import eu.fox7.bonxai.converter.xsd2relaxng.SimpleTypeConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.XSD2RelaxNGConverter;
import eu.fox7.bonxai.relaxng.Data;
import eu.fox7.bonxai.relaxng.Define;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.NotAllowed;
import eu.fox7.bonxai.relaxng.NsName;
import eu.fox7.bonxai.relaxng.Optional;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.Ref;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.Value;
import eu.fox7.bonxai.xsd.Attribute;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.AttributeGroupRef;
import eu.fox7.bonxai.xsd.AttributeRef;
import eu.fox7.bonxai.xsd.AttributeUse;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AttributeParticleConverter
 * @author Lars Schmidt
 */
public class AttributeParticleConverterTest extends junit.framework.TestCase {

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_attribute() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        eu.fox7.bonxai.xsd.Attribute attribute = new eu.fox7.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) result;

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_attribute_fixed() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        eu.fox7.bonxai.xsd.Attribute attribute = new eu.fox7.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        attribute.setFixed("myValue");

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) result;

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Value);
        Value value = (Value) attributePattern.getPattern();
        assertEquals("myValue", value.getContent());
        assertEquals("string", value.getType());
    }

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_attributeRef() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        eu.fox7.bonxai.xsd.Attribute attribute = new eu.fox7.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        AttributeRef attributeRef = new AttributeRef(xmlSchema.getAttributeSymbolTable().updateOrCreateReference("{}attributeName", attribute));

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attributeRef);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.Ref);

        eu.fox7.bonxai.relaxng.Ref ref = (Ref) result;

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) define.getPatterns().getFirst();

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_attributeGroup() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        eu.fox7.bonxai.xsd.Attribute attribute = new eu.fox7.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        AttributeGroup attributeGroup = new AttributeGroup("{}myAGroup");
        attributeGroup.addAttributeParticle(attribute);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attributeGroup);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.Ref);

        eu.fox7.bonxai.relaxng.Ref ref = (Ref) result;

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) define.getPatterns().getFirst();

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_attributeGroupRef() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        eu.fox7.bonxai.xsd.Attribute attribute = new eu.fox7.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        AttributeGroup attributeGroup = new AttributeGroup("{}myAGroup");
        attributeGroup.addAttributeParticle(attribute);

        AttributeGroupRef attributeGroupRef = new AttributeGroupRef(xmlSchema.getAttributeGroupSymbolTable().updateOrCreateReference("{}myAGroup", attributeGroup));

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attributeGroupRef);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.Ref);

        eu.fox7.bonxai.relaxng.Ref ref = (Ref) result;

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) define.getPatterns().getFirst();

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_attribute_use_optional() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        eu.fox7.bonxai.xsd.Attribute attribute = new eu.fox7.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Optional, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.Optional);
        eu.fox7.bonxai.relaxng.Optional optional = (Optional) result;
        assertEquals(1, optional.getPatterns().size());

        assertTrue(optional.getPatterns().getFirst() instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) optional.getPatterns().getFirst();

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_attribute_use_prohibited() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        eu.fox7.bonxai.xsd.Attribute attribute = new eu.fox7.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Prohibited, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) result;

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof eu.fox7.bonxai.relaxng.Group);

        eu.fox7.bonxai.relaxng.Group group = (eu.fox7.bonxai.relaxng.Group) attributePattern.getPattern();
        assertEquals(2, group.getPatterns().size());
        assertTrue(group.getPatterns().get(0) instanceof Data);
        assertTrue(group.getPatterns().get(1) instanceof NotAllowed);
    }

    /**
     * Test of convert method, of class AttributeParticleConverter.
     */
    @Test
    public void testConvert_anyAttribute() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        eu.fox7.bonxai.common.AnyAttribute anyAttribute = new AnyAttribute("myNamespace");

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(anyAttribute);

        assertTrue(result instanceof eu.fox7.bonxai.relaxng.ZeroOrMore);
        eu.fox7.bonxai.relaxng.ZeroOrMore zeroOrMore = (eu.fox7.bonxai.relaxng.ZeroOrMore) result;
        assertEquals(1, zeroOrMore.getPatterns().size());

        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof eu.fox7.bonxai.relaxng.Attribute);

        eu.fox7.bonxai.relaxng.Attribute attributePattern = (eu.fox7.bonxai.relaxng.Attribute) zeroOrMore.getPatterns().getFirst();

        assertTrue(attributePattern.getNameAttribute() == null);
        assertTrue(attributePattern.getAttributeNamespace() == null);

        assertTrue(attributePattern.getNameClass() instanceof NsName);
        NsName nsName = (NsName) attributePattern.getNameClass();
        assertEquals("myNamespace", nsName.getAttributeNamespace());

    }

}
