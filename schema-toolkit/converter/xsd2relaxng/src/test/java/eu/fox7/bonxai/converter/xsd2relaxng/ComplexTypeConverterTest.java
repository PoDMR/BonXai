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

import java.io.FileNotFoundException;
import java.util.*;

import eu.fox7.bonxai.converter.xsd2relaxng.ComplexTypeConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.ElementConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.InheritanceInformationCollector;
import eu.fox7.bonxai.converter.xsd2relaxng.SimpleTypeConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.SubstitutionGroupInformationCollector;
import eu.fox7.bonxai.converter.xsd2relaxng.XSD2RelaxNGConverter;
import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.GroupRef;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.relaxng.AnyName;
import eu.fox7.schematoolkit.relaxng.Choice;
import eu.fox7.schematoolkit.relaxng.Data;
import eu.fox7.schematoolkit.relaxng.Define;
import eu.fox7.schematoolkit.relaxng.Empty;
import eu.fox7.schematoolkit.relaxng.Grammar;
import eu.fox7.schematoolkit.relaxng.Mixed;
import eu.fox7.schematoolkit.relaxng.Pattern;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.ZeroOrMore;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.AttributeUse;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of ComplexTypeConverter
 * @author Lars Schmidt
 */
public class ComplexTypeConverterTest extends junit.framework.TestCase {

    public ComplexTypeConverter initializeCompleTypeConverter(XSDSchema xmlSchema) {
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
        substitutionGroupInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Element, LinkedHashSet<Element>> substitutionGroupInformation = substitutionGroupInformationCollector.getSubstitutionGroupInformation();

        ElementConverter elementConverter = new ElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                substitutionGroupInformation,
                inheritanceInformation);

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                inheritanceInformation);

        ComplexTypeConverter instance = new ComplexTypeConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                elementConverter,
                simpleTypeConverter,
                substitutionGroupInformation,
                inheritanceInformation);

        simpleTypeConverter.setComplexTypeConverter(instance);
        return instance;
    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_empty() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        ComplexType complexType = new ComplexType("{}myComplexType", null);
        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof Empty);
    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_attribute() {
        XSDSchema xmlSchema = new XSDSchema();

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute = new eu.fox7.schematoolkit.xsd.om.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        ComplexType complexType = new ComplexType("{}myComplexType", null);
        complexType.addAttribute(attribute);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);

        eu.fox7.schematoolkit.relaxng.om.Attribute attributePattern = (eu.fox7.schematoolkit.relaxng.om.Attribute) result;

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_mixed() {
        XSDSchema xmlSchema = new XSDSchema();

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute = new eu.fox7.schematoolkit.xsd.om.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        ComplexType complexType = new ComplexType("{}myComplexType", null);
        complexType.addAttribute(attribute);
        complexType.setMixed(Boolean.TRUE);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Group);
        eu.fox7.schematoolkit.relaxng.om.Group group = (eu.fox7.schematoolkit.relaxng.om.Group) result;
        assertEquals(1, group.getPatterns().size());
        assertTrue(group.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Mixed);
        Mixed mixed = (Mixed) group.getPatterns().getFirst();
        assertEquals(1, mixed.getPatterns().size());
        assertTrue(mixed.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);

        eu.fox7.schematoolkit.relaxng.om.Attribute attributePattern = (eu.fox7.schematoolkit.relaxng.om.Attribute) mixed.getPatterns().getFirst();

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_abstract() {
        XSDSchema xmlSchema = new XSDSchema();


        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute = new eu.fox7.schematoolkit.xsd.om.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        ComplexType complexType = new ComplexType("{}myComplexType", null);
        complexType.addAttribute(attribute);
        complexType.setAbstract(Boolean.TRUE);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Group);
        eu.fox7.schematoolkit.relaxng.om.Group group = (eu.fox7.schematoolkit.relaxng.om.Group) result;
        assertEquals(2, group.getPatterns().size());
        assertTrue(group.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.NotAllowed);
        assertTrue(group.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);

        eu.fox7.schematoolkit.relaxng.om.Attribute attributePattern = (eu.fox7.schematoolkit.relaxng.om.Attribute) group.getPatterns().getLast();

        assertEquals("attributeName", attributePattern.getNameAttribute());
        assertEquals("", attributePattern.getAttributeNamespace());
        assertTrue(attributePattern.getPattern() instanceof Data);
    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_simpleContent() {
        XSDSchema xmlSchema = new XSDSchema();

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        eu.fox7.schematoolkit.xsd.om.Attribute attribute = new eu.fox7.schematoolkit.xsd.om.Attribute("{}attributeName", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type), null, null, AttributeUse.Required, Boolean.FALSE, XSDSchema.Qualification.qualified, null);

        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        SimpleContentType simpleContentType = new SimpleContentType();

        SimpleType simpleTypeString = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", simpleTypeString));
        simpleContentType.setInheritance(simpleContentRestriction);

        ComplexType complexType = new ComplexType("{}myComplexType", simpleContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

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
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_anyPattern() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        ChoicePattern choicePattern = new ChoicePattern();
        SequencePattern sequencePattern = new SequencePattern();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, null);
        sequencePattern.addParticle(anyPattern);
        choicePattern.addParticle(sequencePattern);

        ComplexContentType complexContentType = new ComplexContentType(choicePattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) result;

        assertTrue(element.getNameClass() instanceof AnyName);

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_element_choice_sequence() throws FileNotFoundException, FileNotFoundException {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        ChoicePattern choicePattern = new ChoicePattern();
        SequencePattern sequencePattern = new SequencePattern();
        Element element = new Element("{}myElement");
        sequencePattern.addParticle(element);
        choicePattern.addParticle(sequencePattern);

        ComplexContentType complexContentType = new ComplexContentType(choicePattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element2 = (eu.fox7.schematoolkit.relaxng.om.Element) result;

        assertEquals("myElement", element2.getNameAttribute());
        assertEquals(null, element2.getAttributeNamespace());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_countingPattern_star_element() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        CountingPattern countingPattern = new CountingPattern(0, null);
        Element element = new Element("{}myElement");
        countingPattern.addParticle(element);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(countingPattern);

        ComplexContentType complexContentType = new ComplexContentType(sequencePattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.ZeroOrMore);
        eu.fox7.schematoolkit.relaxng.om.ZeroOrMore zeroOrMore = (ZeroOrMore) result;
        assertEquals(1, zeroOrMore.getPatterns().size());
        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element2 = (eu.fox7.schematoolkit.relaxng.om.Element) zeroOrMore.getPatterns().getFirst();

        assertEquals("myElement", element2.getNameAttribute());
        assertEquals(null, element2.getAttributeNamespace());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_countingPattern_questionmark_element() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        CountingPattern countingPattern = new CountingPattern(0, 1);
        Element element = new Element("{}myElement");
        countingPattern.addParticle(element);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(countingPattern);

        ComplexContentType complexContentType = new ComplexContentType(sequencePattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Optional);
        eu.fox7.schematoolkit.relaxng.om.Optional optional = (eu.fox7.schematoolkit.relaxng.om.Optional) result;
        assertEquals(1, optional.getPatterns().size());
        assertTrue(optional.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element2 = (eu.fox7.schematoolkit.relaxng.om.Element) optional.getPatterns().getFirst();

        assertEquals("myElement", element2.getNameAttribute());
        assertEquals(null, element2.getAttributeNamespace());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_countingPattern_plus_element() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        CountingPattern countingPattern = new CountingPattern(1, null);
        Element element = new Element("{}myElement");
        countingPattern.addParticle(element);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(countingPattern);

        ComplexContentType complexContentType = new ComplexContentType(sequencePattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.OneOrMore);
        eu.fox7.schematoolkit.relaxng.om.OneOrMore oneOrMore = (eu.fox7.schematoolkit.relaxng.om.OneOrMore) result;
        assertEquals(1, oneOrMore.getPatterns().size());
        assertTrue(oneOrMore.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element2 = (eu.fox7.schematoolkit.relaxng.om.Element) oneOrMore.getPatterns().getFirst();

        assertEquals("myElement", element2.getNameAttribute());
        assertEquals(null, element2.getAttributeNamespace());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_countingPattern_other_element() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        CountingPattern countingPattern = new CountingPattern(1, 2);
        Element element = new Element("{}myElement");
        countingPattern.addParticle(element);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(countingPattern);

        ComplexContentType complexContentType = new ComplexContentType(sequencePattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Group);
        eu.fox7.schematoolkit.relaxng.om.Group group = (eu.fox7.schematoolkit.relaxng.om.Group) result;
        assertEquals(2, group.getPatterns().size());
        assertTrue(group.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element1 = (eu.fox7.schematoolkit.relaxng.om.Element) group.getPatterns().getFirst();

        assertEquals("myElement", element1.getNameAttribute());
        assertEquals(null, element1.getAttributeNamespace());

        assertTrue(group.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Optional);
        eu.fox7.schematoolkit.relaxng.om.Optional optional = (eu.fox7.schematoolkit.relaxng.om.Optional) group.getPatterns().getLast();
        assertEquals(1, optional.getPatterns().size());
        assertTrue(optional.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element2 = (eu.fox7.schematoolkit.relaxng.om.Element) optional.getPatterns().getFirst();

        assertEquals("myElement", element2.getNameAttribute());
        assertEquals(null, element2.getAttributeNamespace());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_all_element() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        AllPattern allPattern = new AllPattern();
        Element element = new Element("{}myElement");
        Element element2 = new Element("{}myElement2");
        allPattern.addParticle(element);
        allPattern.addParticle(element2);
        ComplexContentType complexContentType = new ComplexContentType(allPattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Interleave);
        eu.fox7.schematoolkit.relaxng.om.Interleave interleave = (eu.fox7.schematoolkit.relaxng.om.Interleave) result;
        assertEquals(2, interleave.getPatterns().size());
        assertTrue(interleave.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element3 = (eu.fox7.schematoolkit.relaxng.om.Element) interleave.getPatterns().getFirst();

        assertEquals("myElement", element3.getNameAttribute());
        assertEquals(null, element3.getAttributeNamespace());

        assertTrue(interleave.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element4 = (eu.fox7.schematoolkit.relaxng.om.Element) interleave.getPatterns().getLast();

        assertEquals("myElement2", element4.getNameAttribute());
        assertEquals(null, element4.getAttributeNamespace());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_elementRef() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        AllPattern allPattern = new AllPattern();
        Element element = new Element("{}myElement");

        ElementRef elementRef = new ElementRef(xmlSchema.getElementSymbolTable().updateOrCreateReference("{}myElement", element));

        Element element2 = new Element("{}myElement2");
        allPattern.addParticle(elementRef);
        allPattern.addParticle(element2);
        ComplexContentType complexContentType = new ComplexContentType(allPattern);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Interleave);
        eu.fox7.schematoolkit.relaxng.om.Interleave interleave = (eu.fox7.schematoolkit.relaxng.om.Interleave) result;
        assertEquals(2, interleave.getPatterns().size());

        assertTrue(interleave.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref = (eu.fox7.schematoolkit.relaxng.om.Ref) interleave.getPatterns().getFirst();
        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element3 = (eu.fox7.schematoolkit.relaxng.om.Element) define.getPatterns().getFirst();

        assertEquals("myElement", element3.getNameAttribute());
        assertEquals(null, element3.getAttributeNamespace());

        assertTrue(interleave.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element4 = (eu.fox7.schematoolkit.relaxng.om.Element) interleave.getPatterns().getLast();

        assertEquals("myElement2", element4.getNameAttribute());
        assertEquals(null, element4.getAttributeNamespace());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContent_no_inheritance_group_groupRef() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        AllPattern allPattern = new AllPattern();
        Element element = new Element("{}myElement");

        ElementRef elementRef = new ElementRef(xmlSchema.getElementSymbolTable().updateOrCreateReference("{}myElement", element));

        Element element2 = new Element("{}myElement2");
        allPattern.addParticle(elementRef);
        allPattern.addParticle(element2);

        eu.fox7.schematoolkit.xsd.om.Group xsdGroup = new Group("{}group", allPattern);

        GroupRef groupRef = new GroupRef(xmlSchema.getGroupSymbolTable().updateOrCreateReference("{}group", xsdGroup));

        ComplexContentType complexContentType = new ComplexContentType(groupRef);

        ComplexType complexType = new ComplexType("{}myComplexType", complexContentType);

        boolean refAllowed = false;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref = (eu.fox7.schematoolkit.relaxng.om.Ref) result;
        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Group);

        eu.fox7.schematoolkit.relaxng.om.Group groupPattern = (eu.fox7.schematoolkit.relaxng.om.Group) define.getPatterns().getFirst();
        assertEquals(1, groupPattern.getPatterns().size());

        assertTrue(groupPattern.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Interleave);

        eu.fox7.schematoolkit.relaxng.om.Interleave interleave = (eu.fox7.schematoolkit.relaxng.om.Interleave) groupPattern.getPatterns().getFirst();
        assertEquals(2, interleave.getPatterns().size());

        assertTrue(interleave.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref2 = (eu.fox7.schematoolkit.relaxng.om.Ref) interleave.getPatterns().getFirst();
        assertEquals(1, ref2.getDefineList().size());
        Define define2 = ref2.getDefineList().getFirst();
        assertEquals(1, define2.getPatterns().size());

        assertTrue(define2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element3 = (eu.fox7.schematoolkit.relaxng.om.Element) define2.getPatterns().getFirst();

        assertEquals("myElement", element3.getNameAttribute());
        assertEquals(null, element3.getAttributeNamespace());

        assertTrue(interleave.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element4 = (eu.fox7.schematoolkit.relaxng.om.Element) interleave.getPatterns().getLast();

        assertEquals("myElement2", element4.getNameAttribute());
        assertEquals(null, element4.getAttributeNamespace());

    }

    /**
     * Test of convertGroupToPattern method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvertGroupToPattern() {

        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        AllPattern allPattern = new AllPattern();
        Element element = new Element("{}myElement");

        ElementRef elementRef = new ElementRef(xmlSchema.getElementSymbolTable().updateOrCreateReference("{}myElement", element));

        Element element2 = new Element("{}myElement2");
        allPattern.addParticle(elementRef);
        allPattern.addParticle(element2);

        eu.fox7.schematoolkit.xsd.om.Group xsdGroup = new Group("{}group", allPattern);

        Pattern result = instance.convertGroupToPattern(xsdGroup);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref = (eu.fox7.schematoolkit.relaxng.om.Ref) result;
        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Group);

        eu.fox7.schematoolkit.relaxng.om.Group groupPattern = (eu.fox7.schematoolkit.relaxng.om.Group) define.getPatterns().getFirst();
        assertEquals(1, groupPattern.getPatterns().size());

        assertTrue(groupPattern.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Interleave);

        eu.fox7.schematoolkit.relaxng.om.Interleave interleave = (eu.fox7.schematoolkit.relaxng.om.Interleave) groupPattern.getPatterns().getFirst();
        assertEquals(2, interleave.getPatterns().size());

        assertTrue(interleave.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref2 = (eu.fox7.schematoolkit.relaxng.om.Ref) interleave.getPatterns().getFirst();
        assertEquals(1, ref2.getDefineList().size());
        Define define2 = ref2.getDefineList().getFirst();
        assertEquals(1, define2.getPatterns().size());

        assertTrue(define2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element3 = (eu.fox7.schematoolkit.relaxng.om.Element) define2.getPatterns().getFirst();

        assertEquals("myElement", element3.getNameAttribute());
        assertEquals(null, element3.getAttributeNamespace());

        assertTrue(interleave.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element4 = (eu.fox7.schematoolkit.relaxng.om.Element) interleave.getPatterns().getLast();

        assertEquals("myElement2", element4.getNameAttribute());
        assertEquals(null, element4.getAttributeNamespace());

    }

    /**
     * Test of handleSubstitutions method, of class ComplexTypeConverter.
     */
    @Test
    public void testHandleSubstitutions() throws Exception {

        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/complexTypeConverterTests/inheritance.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        Element xsdElement = xmlSchema.getElementSymbolTable().getReference("{}test").getReference();

        Pattern complexTypePattern = instance.convert((ComplexType) xsdElement.getType());

        Pattern result = instance.handleSubstitutions(xsdElement, complexTypePattern, true);

        assertTrue(result instanceof Choice);

        Choice choice = (Choice) result;
        assertEquals(2, choice.getPatterns().size());

        assertTrue(choice.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);
        eu.fox7.schematoolkit.relaxng.om.Ref ref1 = (eu.fox7.schematoolkit.relaxng.om.Ref) choice.getPatterns().getFirst();
        assertEquals("AAA", ref1.getRefName());

        assertTrue(choice.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);
        eu.fox7.schematoolkit.relaxng.om.Ref ref = (eu.fox7.schematoolkit.relaxng.om.Ref) choice.getPatterns().getLast();
        assertEquals("BBB", ref.getRefName());

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref2 = (eu.fox7.schematoolkit.relaxng.om.Ref) define.getPatterns().getFirst();
        assertEquals("AAA", ref2.getRefName());

        // ----- no substitutions -----
        Element xsdElement2 = xmlSchema.getElementSymbolTable().getReference("{}test2").getReference();
        Pattern complexTypePattern2 = instance.convert((ComplexType) xsdElement2.getType());
        Pattern result2 = instance.handleSubstitutions(xsdElement2, complexTypePattern2, true);
        assertEquals(complexTypePattern2, result2);
    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_refAllowed() {
        XSDSchema xmlSchema = new XSDSchema();
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        ComplexType complexType = new ComplexType("{}myComplexType", null);
        boolean refAllowed = true;

        Pattern result = instance.convert(complexType, refAllowed, true, "");

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Ref);
        eu.fox7.schematoolkit.relaxng.om.Ref ref = (eu.fox7.schematoolkit.relaxng.om.Ref) result;
        assertEquals("myComplexType", ref.getRefName());

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof Empty);
    }

    // TODO:
    // complexContentExtension
    // complexContentRestriction
    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_simpleContentExtension() throws Exception {
        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/complexTypeConverterTests/simpleContentExtension.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        Element xsdElement = xmlSchema.getElementSymbolTable().getReference("{}test").getReference();

        Pattern result = instance.convert((ComplexType) xsdElement.getType());

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Ref);
        eu.fox7.schematoolkit.relaxng.om.Ref ref = (eu.fox7.schematoolkit.relaxng.om.Ref) result;
        assertEquals("AAA", ref.getRefName());

        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref2 = (eu.fox7.schematoolkit.relaxng.om.Ref) define.getPatterns().getFirst();
        assertEquals("XXX", ref2.getRefName());

        assertEquals(1, ref2.getDefineList().size());
        Define define2 = ref2.getDefineList().getFirst();
        assertEquals(1, define2.getPatterns().size());

        assertTrue(define2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Data);

        eu.fox7.schematoolkit.relaxng.om.Data data = (eu.fox7.schematoolkit.relaxng.om.Data) define2.getPatterns().getFirst();

        assertEquals("Name", data.getType());

    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_simpleContentRestriction_content_and_baseComplexType_mixed() throws Exception {
        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/complexTypeConverterTests/simpleContentRestriction_content_and_baseComplexType_mixed.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        Element xsdElement = xmlSchema.getElementSymbolTable().getReference("{}e9").getReference();

        Pattern result = instance.convert((ComplexType) xsdElement.getType());

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Group);

        eu.fox7.schematoolkit.relaxng.om.Group groupPattern = (eu.fox7.schematoolkit.relaxng.om.Group) result;

        assertEquals(2, groupPattern.getPatterns().size());
        assertTrue(groupPattern.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Group);

        eu.fox7.schematoolkit.relaxng.om.Group groupPattern2 = (eu.fox7.schematoolkit.relaxng.om.Group) groupPattern.getPatterns().getFirst();

        assertEquals(1, groupPattern2.getPatterns().size());
        assertTrue(groupPattern2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Mixed);


        eu.fox7.schematoolkit.relaxng.om.Mixed mixed = (eu.fox7.schematoolkit.relaxng.om.Mixed) groupPattern2.getPatterns().getFirst();
        assertEquals(1, mixed.getPatterns().size());
        assertTrue(mixed.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Optional);
        eu.fox7.schematoolkit.relaxng.om.Optional optional = (eu.fox7.schematoolkit.relaxng.om.Optional) mixed.getPatterns().getFirst();
        assertEquals(1, optional.getPatterns().size());
        assertTrue(optional.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attribute1 = (eu.fox7.schematoolkit.relaxng.om.Attribute) optional.getPatterns().getFirst();

        assertEquals("attr1", attribute1.getNameAttribute());

        assertTrue(groupPattern.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Data);
        eu.fox7.schematoolkit.relaxng.om.Data data = (eu.fox7.schematoolkit.relaxng.om.Data) groupPattern.getPatterns().getLast();

        assertEquals("short", data.getType());
    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContentExtension() throws Exception {
        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/complexTypeConverterTests/complexContentExtension.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        Element xsdElement = xmlSchema.getElementSymbolTable().getReference("{http://www.w3.org/2001/ds}e8").getReference();

        Pattern result = instance.convert((ComplexType) xsdElement.getType());

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Group);

        eu.fox7.schematoolkit.relaxng.om.Group groupPattern = (eu.fox7.schematoolkit.relaxng.om.Group) result;

        assertEquals(2, groupPattern.getPatterns().size());
        assertTrue(groupPattern.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        eu.fox7.schematoolkit.relaxng.om.Ref ref = (eu.fox7.schematoolkit.relaxng.om.Ref) groupPattern.getPatterns().getFirst();
        assertEquals("e8_complexType", ref.getRefName());
        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Group);

        eu.fox7.schematoolkit.relaxng.om.Group groupPattern2 = (eu.fox7.schematoolkit.relaxng.om.Group) define.getPatterns().getFirst();

        assertEquals(2, groupPattern2.getPatterns().size());
        assertTrue(groupPattern2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) groupPattern2.getPatterns().getFirst();
        assertEquals("test", element.getNameAttribute());
        assertEquals("http://www.w3.org/2001/ds", element.getAttributeNamespace());

        assertTrue(groupPattern2.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Optional);

        eu.fox7.schematoolkit.relaxng.om.Optional optional = (eu.fox7.schematoolkit.relaxng.om.Optional) groupPattern2.getPatterns().getLast();
        assertEquals(1, optional.getPatterns().size());
        assertTrue(optional.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attr2 = (eu.fox7.schematoolkit.relaxng.om.Attribute) optional.getPatterns().getFirst();

        assertEquals("attr2", attr2.getNameAttribute());
        assertEquals("http://www.w3.org/2001/ds", attr2.getAttributeNamespace());


        assertTrue(groupPattern.getPatterns().getLast() instanceof eu.fox7.schematoolkit.relaxng.om.Optional);

        eu.fox7.schematoolkit.relaxng.om.Optional optional2 = (eu.fox7.schematoolkit.relaxng.om.Optional) groupPattern.getPatterns().getLast();
        assertEquals(1, optional2.getPatterns().size());
        assertTrue(optional2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attr3 = (eu.fox7.schematoolkit.relaxng.om.Attribute) optional2.getPatterns().getFirst();

        assertEquals("attr3", attr3.getNameAttribute());
        assertEquals("http://www.w3.org/2001/ds", attr3.getAttributeNamespace());


    }

    /**
     * Test of convert method, of class ComplexTypeConverter.
     */
    @Test
    public void testConvert_ComplexType_complexContentRestriction() throws Exception {
        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/complexTypeConverterTests/complexContentRestriction.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        ComplexTypeConverter instance = initializeCompleTypeConverter(xmlSchema);

        Element xsdElement = xmlSchema.getElementSymbolTable().getReference("{http://www.w3.org/2001/ds}e8").getReference();

        Pattern result = instance.convert((ComplexType) xsdElement.getType());

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Group);

        eu.fox7.schematoolkit.relaxng.om.Group groupPattern = (eu.fox7.schematoolkit.relaxng.om.Group) result;

        assertEquals(3, groupPattern.getPatterns().size());

        assertTrue(groupPattern.getPatterns().get(0) instanceof eu.fox7.schematoolkit.relaxng.om.Optional);

        eu.fox7.schematoolkit.relaxng.om.Optional optional = (eu.fox7.schematoolkit.relaxng.om.Optional) groupPattern.getPatterns().get(0);
        assertEquals(1, optional.getPatterns().size());
        assertTrue(optional.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attr1 = (eu.fox7.schematoolkit.relaxng.om.Attribute) optional.getPatterns().getFirst();

        assertEquals("attr1", attr1.getNameAttribute());
        assertEquals("http://www.w3.org/2001/ds", attr1.getAttributeNamespace());


        assertTrue(groupPattern.getPatterns().get(1) instanceof eu.fox7.schematoolkit.relaxng.om.Optional);

        eu.fox7.schematoolkit.relaxng.om.Optional optional2 = (eu.fox7.schematoolkit.relaxng.om.Optional) groupPattern.getPatterns().get(1);
        assertEquals(1, optional2.getPatterns().size());
        assertTrue(optional2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attr2 = (eu.fox7.schematoolkit.relaxng.om.Attribute) optional2.getPatterns().getFirst();

        assertEquals("attr2", attr2.getNameAttribute());
        assertEquals("http://www.w3.org/2001/ds", attr2.getAttributeNamespace());


        assertTrue(groupPattern.getPatterns().get(2) instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) groupPattern.getPatterns().get(2);
        assertEquals("ele1", element.getNameAttribute());
        assertEquals("http://www.w3.org/2001/ds", element.getAttributeNamespace());
    }
}
