package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import de.tudortmund.cs.bonxai.relaxng.Ref;
import de.tudortmund.cs.bonxai.relaxng.Value;
import de.tudortmund.cs.bonxai.relaxng.Choice;
import de.tudortmund.cs.bonxai.relaxng.Param;
import de.tudortmund.cs.bonxai.relaxng.Data;
import de.tudortmund.cs.bonxai.relaxng.Define;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import java.util.*;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.relaxng.ZeroOrMore;
import de.tudortmund.cs.bonxai.xsd.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class SimpleTypeConverter
 * @author Lars Schmidt
 */
public class SimpleTypeConverterTest extends junit.framework.TestCase {

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_builtInType() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_refAllowed() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, false);

        boolean refAllowed = true;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Ref);

        Ref ref = (Ref) result;
        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof Data);

        Data data = (Data) define.getPatterns().getFirst();
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_List() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentList simpleContentList = new SimpleContentList(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentList, false);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.List);

        de.tudortmund.cs.bonxai.relaxng.List list = (de.tudortmund.cs.bonxai.relaxng.List) result;

        assertEquals(1, list.getPatterns().size());
        assertTrue(list.getPatterns().getFirst() instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) list.getPatterns().getFirst();
        assertEquals(1, zeroOrMore.getPatterns().size());
        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof Data);

        Data data = (Data) zeroOrMore.getPatterns().getFirst();
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_Union() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentUnion, false);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_Union2() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        SimpleType simpleTypeInteger = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}integer", null, true);

        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        memberTypes.add(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}integer", simpleTypeInteger));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentUnion, false);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Choice);
        de.tudortmund.cs.bonxai.relaxng.Choice choice = (Choice) result;

        assertEquals(2, choice.getPatterns().size());
        assertTrue(choice.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.Data);
        assertTrue(choice.getPatterns().getLast() instanceof de.tudortmund.cs.bonxai.relaxng.Data);

        Data data = (Data) choice.getPatterns().getFirst();
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());

        Data data2 = (Data) choice.getPatterns().getLast();
        assertEquals("integer", data2.getType());
        assertEquals(null, data2.getAttributeDatatypeLibrary());
        assertEquals(null, data2.getAttributeNamespace());
        assertEquals(null, data2.getDefaultNamespace());
        assertTrue(data2.getExceptPatterns().isEmpty());
        assertTrue(data2.getParams().isEmpty());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_length() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setLength(new SimpleContentFixableRestrictionProperty<Integer>(4, Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("4", param.getContent());
        assertEquals("length", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_fractionDigits() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setFractionDigits(new SimpleContentFixableRestrictionProperty<Integer>(4, Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("4", param.getContent());
        assertEquals("fractionDigits", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_maxExclusive() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setMaxExclusive(new SimpleContentFixableRestrictionProperty<String>("4", Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("4", param.getContent());
        assertEquals("maxExclusive", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_minExclusive() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setMinExclusive(new SimpleContentFixableRestrictionProperty<String>("4", Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("4", param.getContent());
        assertEquals("minExclusive", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_maxInsclusive() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>("4", Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("4", param.getContent());
        assertEquals("maxInclusive", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_minInclusive() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("4", Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("4", param.getContent());
        assertEquals("minInclusive", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_maxLength() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(3, Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("3", param.getContent());
        assertEquals("maxLength", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_minLength() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(3, Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("3", param.getContent());
        assertEquals("minLength", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_pattern() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setPattern(new SimpleContentFixableRestrictionProperty<String>("myPattern*", Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("myPattern*", param.getContent());
        assertEquals("pattern", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_totalDigits() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentRestriction.setTotalDigits(new SimpleContentFixableRestrictionProperty<Integer>(6, Boolean.TRUE));

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertEquals(1, data.getParams().size());

        Param param = data.getParams().getFirst();
        assertEquals("6", param.getContent());
        assertEquals("totalDigits", param.getName());
    }

    /**
     * Test of convert method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvert_SimpleType_restriction_facet_enumeration() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        LinkedList<String> tempStringList = new LinkedList<String>();
        tempStringList.add("a");
        tempStringList.add("b");
        tempStringList.add("c");
        simpleContentRestriction.setEnumeration(tempStringList);

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}test", simpleContentRestriction, true);

        boolean refAllowed = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof Choice);
        Choice choice = (Choice) result;

        assertEquals(3, choice.getPatterns().size());
        assertTrue(choice.getPatterns().get(0) instanceof Value);
        Value value1 = (Value) choice.getPatterns().get(0);
        assertEquals("a", value1.getContent());
        assertEquals("string", value1.getType());
        assertTrue(choice.getPatterns().get(1) instanceof Value);
        Value value2 = (Value) choice.getPatterns().get(1);
        assertEquals("b", value2.getContent());
        assertEquals("string", value2.getType());
        assertTrue(choice.getPatterns().get(2) instanceof Value);
        Value value3 = (Value) choice.getPatterns().get(2);
        assertEquals("c", value3.getContent());
        assertEquals("string", value3.getType());
    }

    /**
     * Test of convertSimpleTypeInheritance method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvertSimpleTypeInheritance() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        boolean refAllowed = false;
        boolean convertAttributes = false;

        Pattern result = instance.convertSimpleContentInheritance(simpleContentExtension, refAllowed, convertAttributes, "");

        assertTrue(result instanceof Data);

        Data data = (Data) result;
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());
    }

    /**
     * Test of convertSimpleTypeInheritance method, of class SimpleTypeConverter.
     */
    @Test
    public void testConvertSimpleTypeInheritance_attributes() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);

        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = new de.tudortmund.cs.bonxai.xsd.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().getReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string"), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        simpleContentExtension.addAttribute(attribute);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter attributeParticleConverter = new AttributeParticleConverter(xmlSchema, relaxNGSchema, instance);
        instance.setAttributeParticleConverter(attributeParticleConverter);


        boolean refAllowed = false;
        boolean convertAttributes = true;

        Pattern result = instance.convertSimpleContentInheritance(simpleContentExtension, refAllowed, convertAttributes, "");

        assertTrue(result instanceof de.tudortmund.cs.bonxai.relaxng.Group);
        de.tudortmund.cs.bonxai.relaxng.Group group = (de.tudortmund.cs.bonxai.relaxng.Group) result;

        assertEquals(2, group.getPatterns().size());
        assertTrue(group.getPatterns().get(0) instanceof Data);
        Data data = (Data) group.getPatterns().get(0);

        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());

        assertTrue(group.getPatterns().get(1) instanceof de.tudortmund.cs.bonxai.relaxng.Attribute);
        de.tudortmund.cs.bonxai.relaxng.Attribute attributePattern = (de.tudortmund.cs.bonxai.relaxng.Attribute) group.getPatterns().get(1);
        assertEquals("attributeName", attributePattern.getNameAttribute());


    }

    /**
     * Test of handleSubstitutions method, of class SimpleTypeConverter.
     */
    @Test
    public void testHandleSubstitutions() throws Exception {

        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        String uri = "/tests/de/tudortmund/cs/bonxai/converter/xsd2relaxng/simpleTypeConverterTests/inheritance.xsd";
        uri = this.getClass().getResource(uri).getFile();
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<de.tudortmund.cs.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter attributeParticleConverter = new AttributeParticleConverter(xmlSchema, relaxNGSchema, instance);
        instance.setAttributeParticleConverter(attributeParticleConverter);


        Type xsdType = xmlSchema.getTypeSymbolTable().getReference("{}AAA").getReference();

        Pattern simpleTypePattern = instance.convert((SimpleType) xsdType, false);

        Pattern result = instance.handleSubstitutions((SimpleType) xsdType, simpleTypePattern, true);

        assertTrue(result instanceof Choice);

        Choice choice = (Choice) result;
        assertEquals(2, choice.getPatterns().size());

        assertTrue(choice.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.List);

        de.tudortmund.cs.bonxai.relaxng.List list = (de.tudortmund.cs.bonxai.relaxng.List) choice.getPatterns().getFirst();

        assertEquals(1, list.getPatterns().size());
        assertTrue(list.getPatterns().getFirst() instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) list.getPatterns().getFirst();
        assertEquals(1, zeroOrMore.getPatterns().size());
        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof Data);

        Data data = (Data) zeroOrMore.getPatterns().getFirst();
        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());

        assertTrue(choice.getPatterns().getLast() instanceof de.tudortmund.cs.bonxai.relaxng.Ref);

        de.tudortmund.cs.bonxai.relaxng.Ref ref = (Ref) choice.getPatterns().getLast();

        assertEquals("BBB", ref.getRefName());

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof de.tudortmund.cs.bonxai.relaxng.Ref);

        de.tudortmund.cs.bonxai.relaxng.Ref ref2 = (Ref) define.getPatterns().getFirst();
        assertEquals("AAA", ref2.getRefName());

        // ----- no substitutions -----

        Type xsdType2 = xmlSchema.getTypeSymbolTable().getReference("{}BBB").getReference();
        Pattern simpleTypePattern2 = instance.convert((SimpleType) xsdType2, false);
        Pattern result2 = instance.handleSubstitutions((SimpleType) xsdType2, simpleTypePattern2, true);
        assertEquals(simpleTypePattern2, result2);
    }
}
