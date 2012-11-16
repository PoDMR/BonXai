/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.bonxai.converter.xsd2relaxng;

import java.util.*;

import eu.fox7.bonxai.converter.xsd2relaxng.AttributeParticleConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.InheritanceInformationCollector;
import eu.fox7.bonxai.converter.xsd2relaxng.SimpleTypeConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.XSD2RelaxNGConverter;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.relaxng.Choice;
import eu.fox7.schematoolkit.relaxng.Data;
import eu.fox7.schematoolkit.relaxng.Define;
import eu.fox7.schematoolkit.relaxng.Grammar;
import eu.fox7.schematoolkit.relaxng.Param;
import eu.fox7.schematoolkit.relaxng.Pattern;
import eu.fox7.schematoolkit.relaxng.Ref;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.Value;
import eu.fox7.schematoolkit.relaxng.ZeroOrMore;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;

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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.List);

        eu.fox7.schematoolkit.relaxng.om.List list = (eu.fox7.schematoolkit.relaxng.om.List) result;

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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Data);

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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        Pattern result = instance.convert(simpleType, refAllowed);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Choice);
        eu.fox7.schematoolkit.relaxng.om.Choice choice = (Choice) result;

        assertEquals(2, choice.getPatterns().size());
        assertTrue(choice.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Data);
        assertTrue(choice.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Data);

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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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

        eu.fox7.schematoolkit.xsd.om.Attribute attribute = new eu.fox7.schematoolkit.xsd.om.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().getReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string"), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        simpleContentExtension.addAttribute(attribute);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);
        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SimpleTypeConverter instance = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);
        AttributeParticleConverter attributeParticleConverter = new AttributeParticleConverter(xmlSchema, relaxNGSchema, instance);
        instance.setAttributeParticleConverter(attributeParticleConverter);


        boolean refAllowed = false;
        boolean convertAttributes = true;

        Pattern result = instance.convertSimpleContentInheritance(simpleContentExtension, refAllowed, convertAttributes, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Group);
        eu.fox7.schematoolkit.relaxng.om.Group group = (eu.fox7.schematoolkit.relaxng.om.Group) result;

        assertEquals(2, group.getPatterns().size());
        assertTrue(group.getPatterns().get(0) instanceof Data);
        Data data = (Data) group.getPatterns().get(0);

        assertEquals("string", data.getType());
        assertEquals(null, data.getAttributeDatatypeLibrary());
        assertEquals(null, data.getAttributeNamespace());
        assertEquals(null, data.getDefaultNamespace());
        assertTrue(data.getExceptPatterns().isEmpty());
        assertTrue(data.getParams().isEmpty());

        assertTrue(group.getPatterns().get(1) instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attributePattern = (eu.fox7.schematoolkit.relaxng.om.Attribute) group.getPatterns().get(1);
        assertEquals("attributeName", attributePattern.getNameAttribute());


    }

    /**
     * Test of handleSubstitutions method, of class SimpleTypeConverter.
     */
    @Test
    public void testHandleSubstitutions() throws Exception {

        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/simpleTypeConverterTests/inheritance.xsd";
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
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
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

        assertTrue(choice.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.List);

        eu.fox7.schematoolkit.relaxng.om.List list = (eu.fox7.schematoolkit.relaxng.om.List) choice.getPatterns().getFirst();

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

        assertTrue(choice.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref = (Ref) choice.getPatterns().getLast();

        assertEquals("BBB", ref.getRefName());

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref2 = (Ref) define.getPatterns().getFirst();
        assertEquals("AAA", ref2.getRefName());

        // ----- no substitutions -----

        Type xsdType2 = xmlSchema.getTypeSymbolTable().getReference("{}BBB").getReference();
        Pattern simpleTypePattern2 = instance.convert((SimpleType) xsdType2, false);
        Pattern result2 = instance.handleSubstitutions((SimpleType) xsdType2, simpleTypePattern2, true);
        assertEquals(simpleTypePattern2, result2);
    }
}
