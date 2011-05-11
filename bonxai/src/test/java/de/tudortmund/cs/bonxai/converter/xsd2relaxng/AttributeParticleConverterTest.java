package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

import de.tudortmund.cs.bonxai.relaxng.NsName;
import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.relaxng.Optional;
import de.tudortmund.cs.bonxai.relaxng.Value;
import de.tudortmund.cs.bonxai.relaxng.Data;
import de.tudortmund.cs.bonxai.relaxng.Ref;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.relaxng.Define;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import java.util.LinkedList;
import java.util.HashMap;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.relaxng.NotAllowed;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.xsd.AttributeGroupRef;
import de.tudortmund.cs.bonxai.xsd.AttributeRef;
import de.tudortmund.cs.bonxai.xsd.AttributeUse;
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

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) result;

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

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        attribute.setFixed("myValue");

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) result;

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

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        AttributeRef attributeRef = new AttributeRef(xmlSchema.getAttributeSymbolTable().updateOrCreateReference("{}attributeName", attribute));

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attributeRef);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Ref);

        de.tudortmund.cs.bonxai.relaxng.Ref ref = (Ref) result;

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) define.getPatterns().getFirst();

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

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

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
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attributeGroup);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Ref);

        de.tudortmund.cs.bonxai.relaxng.Ref ref = (Ref) result;

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) define.getPatterns().getFirst();

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

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

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
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attributeGroupRef);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Ref);

        de.tudortmund.cs.bonxai.relaxng.Ref ref = (Ref) result;

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) define.getPatterns().getFirst();

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

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Optional, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Optional);
        de.tudortmund.cs.bonxai.relaxng.Optional optional = (Optional) result;
        assertEquals(1, optional.getPatterns().size());

        assertTrue(optional.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) optional.getPatterns().getFirst();

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

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Prohibited, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(attribute);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) result;

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof de.tudortmund.cs.bonxai.relaxng.Group);

        de.tudortmund.cs.bonxai.relaxng.Group group = (de.tudortmund.cs.bonxai.relaxng.Group) attributePattern.getPattern();
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

        de.tudortmund.cs.bonxai.common.AnyAttribute anyAttribute = new AnyAttribute("myNamespace");

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter instance = new AttributeParticleConverter(xmlSchema, relaxNGSchema, simpleTypeConverter);

        Pattern result = instance.convert(anyAttribute);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.ZeroOrMore);
        de.tudortmund.cs.bonxai.relaxng.ZeroOrMore zeroOrMore = (de.tudortmund.cs.bonxai.relaxng.ZeroOrMore) result;
        assertEquals(1, zeroOrMore.getPatterns().size());

        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);

        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) zeroOrMore.getPatterns().getFirst();

        assertTrue(attributePattern.getNameAttribute() == null);
        assertTrue(attributePattern.getAttributeNamespace() == null);

        assertTrue(attributePattern.getNameClass() instanceof NsName);
        NsName nsName = (NsName) attributePattern.getNameClass();
        assertEquals("myNamespace", nsName.getAttributeNamespace());

    }

}
