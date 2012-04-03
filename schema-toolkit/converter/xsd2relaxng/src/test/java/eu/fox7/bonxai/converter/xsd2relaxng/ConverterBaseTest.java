package eu.fox7.bonxai.converter.xsd2relaxng;

import java.io.FileNotFoundException;
import java.util.Iterator;

import eu.fox7.bonxai.converter.xsd2relaxng.ConverterBase;
import eu.fox7.bonxai.converter.xsd2relaxng.XSD2RelaxNGConverter;
import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.relaxng.AnyName;
import eu.fox7.schematoolkit.relaxng.Choice;
import eu.fox7.schematoolkit.relaxng.Define;
import eu.fox7.schematoolkit.relaxng.Empty;
import eu.fox7.schematoolkit.relaxng.Grammar;
import eu.fox7.schematoolkit.relaxng.Group;
import eu.fox7.schematoolkit.relaxng.Interleave;
import eu.fox7.schematoolkit.relaxng.NsName;
import eu.fox7.schematoolkit.relaxng.OneOrMore;
import eu.fox7.schematoolkit.relaxng.Pattern;
import eu.fox7.schematoolkit.relaxng.Ref;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.Text;
import eu.fox7.schematoolkit.relaxng.ZeroOrMore;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ConverterBase
 * @author Lars Schmidt
 */
public class ConverterBaseTest extends junit.framework.TestCase {

    /**
     * Test of getRelaxNGSchema method, of class ConverterBase.
     */
    @Test
    public void testGetRelaxNGSchema() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();
        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        RelaxNGSchema result = instance.getRelaxNGSchema();
        assertEquals(rng, result);
    }

    /**
     * Test of getXmlSchema method, of class ConverterBase.
     */
    @Test
    public void testGetXmlSchema() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();
        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        XSDSchema result = instance.getXmlSchema();
        assertEquals(xmlSchema, result);
    }

    /**
     * Test of getUsedDefineNames method, of class ConverterBase.
     */
    @Test
    public void testGetUsedDefineNames() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        Grammar grammar = new Grammar();
        Define define = new Define("myDefine");
        define.addPattern(new Empty());
        grammar.addDefinePattern(define);
        rng.setRootPattern(grammar);

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        HashSet<String> result = instance.getUsedDefineNames();

        assertTrue(result.contains("myDefine"));
    }

    /**
     * Test of setPatternToDefine method, of class ConverterBase.
     */
    @Test
    public void testSetPatternToDefine_String_Pattern() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        Empty empty = new Empty();
        instance.setPatternToDefine("myDefine", empty);

        HashSet<String> result = instance.getUsedDefineNames();

        assertTrue(result.contains("myDefine"));

        LinkedList<Define> myDefinelist = ((Grammar) instance.getRelaxNGSchema().getRootPattern()).getDefinedPatternsFromLookUpTable("myDefine");

        assertEquals(1, myDefinelist.size());

        Define define1 = myDefinelist.getFirst();
        assertEquals("myDefine", define1.getName());
        assertEquals(empty, define1.getPatterns().getFirst());

    }

    /**
     * Test of setPatternToDefine method, of class ConverterBase.
     */
    @Test
    public void testSetPatternToDefine_String_LinkedList() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        LinkedList<Pattern> myPatternlist = new LinkedList<Pattern>();
        Empty empty = new Empty();
        Text text = new Text();
        myPatternlist.add(empty);
        myPatternlist.add(text);

        instance.setPatternToDefine("myDefine", myPatternlist);

        HashSet<String> result = instance.getUsedDefineNames();

        assertTrue(result.contains("myDefine"));

        LinkedList<Define> myDefinelist = ((Grammar) instance.getRelaxNGSchema().getRootPattern()).getDefinedPatternsFromLookUpTable("myDefine");

        assertEquals(1, myDefinelist.size());

        Define define1 = myDefinelist.getFirst();
        assertEquals("myDefine", define1.getName());
        assertEquals(empty, define1.getPatterns().getFirst());
        assertEquals(text, define1.getPatterns().getLast());
    }

    /**
     * Test of generateUniqueDefineName method, of class ConverterBase.
     */
    @Test
    public void testGenerateUniqueDefineName() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        String name = "myName";
        String expResult = "myName";
        String result = instance.generateUniqueDefineName(name);
        assertEquals(expResult, result);

        instance.setPatternToDefine(name, new Text());

        String name2 = "myName";
        String expResult2 = "myName_1";
        String result2 = instance.generateUniqueDefineName(name);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getXsdAttributeGroupDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testGetXsdAttributeGroupDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdAttributeGroupDefineRefMap(myMap);

        HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdAttributeGroupDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of setXsdAttributeGroupDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testSetXsdAttributeGroupDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdAttributeGroupDefineRefMap(myMap);

        HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdAttributeGroupDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of getXsdElementDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testGetXsdElementDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<Element, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<Element, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdElementDefineRefMap(myMap);

        HashMap<Element, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdElementDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of setXsdElementDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testSetXsdElementDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<Element, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<Element, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdElementDefineRefMap(myMap);

        HashMap<Element, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdElementDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of getXsdGroupDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testGetXsdGroupDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<eu.fox7.schematoolkit.xsd.om.Group, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<eu.fox7.schematoolkit.xsd.om.Group, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdGroupDefineRefMap(myMap);

        HashMap<eu.fox7.schematoolkit.xsd.om.Group, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdGroupDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of setXsdGroupDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testSetXsdGroupDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<eu.fox7.schematoolkit.xsd.om.Group, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<eu.fox7.schematoolkit.xsd.om.Group, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdGroupDefineRefMap(myMap);

        HashMap<eu.fox7.schematoolkit.xsd.om.Group, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdGroupDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of getXsdTypeDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testGetXsdTypeDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<Type, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<Type, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdTypeDefineRefMap(myMap);

        HashMap<Type, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdTypeDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of setXsdTypeDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testSetXsdTypeDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<Type, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<Type, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdTypeDefineRefMap(myMap);

        HashMap<Type, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdTypeDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of getXsdAttributeDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testGetXsdAttributeDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<Attribute, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdAttributeDefineRefMap(myMap);

        HashMap<Attribute, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdAttributeDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of setXsdAttributeDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testSetXsdAttributeDefineRefMap() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        HashMap<Attribute, SymbolTableRef<LinkedList<Define>>> myMap = new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>();

        instance.setXsdAttributeDefineRefMap(myMap);

        HashMap<Attribute, SymbolTableRef<LinkedList<Define>>> result = instance.getXsdAttributeDefineRefMap();
        assertEquals(myMap, result);
    }

    /**
     * Test of registerDummyInDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testRegisterDummyInDefineRefMap_Element() {

        XSD2RelaxNGConverter.PREFIX_ELEMENT_DEFINE = "";

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        Element xsdElement = new Element("{}test");
        String expResult = "test";
        String result = instance.registerDummyInDefineRefMap(xsdElement);
        assertEquals(expResult, result);
    }

    /**
     * Test of registerDummyInDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testRegisterDummyInDefineRefMap_Attribute() {

        XSD2RelaxNGConverter.PREFIX_ATTRIBUTE_DEFINE = "";

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        Attribute xsdAttribute = new Attribute("{}test");
        String expResult = "test";
        String result = instance.registerDummyInDefineRefMap(xsdAttribute);
        assertEquals(expResult, result);
    }

    /**
     * Test of registerDummyInDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testRegisterDummyInDefineRefMap_AttributeGroup() {

        XSD2RelaxNGConverter.PREFIX_ATTRIBUTEGROUP_DEFINE = "";

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        AttributeGroup attributeGroup = new AttributeGroup("{}test");
        String expResult = "test";
        String result = instance.registerDummyInDefineRefMap(attributeGroup);
        assertEquals(expResult, result);
    }

    /**
     * Test of registerDummyInDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testRegisterDummyInDefineRefMap_Group() {

        XSD2RelaxNGConverter.PREFIX_GROUP_DEFINE = "";

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        eu.fox7.schematoolkit.xsd.om.Group group = new eu.fox7.schematoolkit.xsd.om.Group("{}test", null);
        String expResult = "test";
        String result = instance.registerDummyInDefineRefMap(group);
        assertEquals(expResult, result);
    }

    /**
     * Test of registerDummyInDefineRefMap method, of class ConverterBase.
     */
    @Test
    public void testRegisterDummyInDefineRefMap_Type() {

        XSD2RelaxNGConverter.PREFIX_TYPE_DEFINE = "";

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        SimpleType type = new SimpleType("{}test", null);
        String expResult = "test";
        String result = instance.registerDummyInDefineRefMap(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of simplifyPatternStructure method, of class ConverterBase.
     */
    @Test
    public void testSimplifyPatternStructure() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        Pattern pattern = new Empty();
        Pattern result = instance.simplifyPatternStructure(pattern);
        assertEquals(pattern, result);

        Group group = new Group();
        Empty empty = new Empty();
        group.addPattern(empty);
        Pattern pattern2 = group;
        Pattern result2 = instance.simplifyPatternStructure(pattern2);
        assertEquals(empty, result2);

        Choice choice = new Choice();
        Empty empty2 = new Empty();
        choice.addPattern(empty2);
        Pattern pattern3 = choice;
        Pattern result3 = instance.simplifyPatternStructure(pattern3);
        assertEquals(empty2, result3);

        Interleave interleave = new Interleave();
        Empty empty3 = new Empty();
        interleave.addPattern(empty3);
        Pattern pattern4 = interleave;
        Pattern result4 = instance.simplifyPatternStructure(pattern4);
        assertEquals(empty3, result4);

        choice.addPattern(pattern4);
        Pattern result5 = instance.simplifyPatternStructure(choice);
        assertEquals(choice, result5);
    }

    /**
     * Test of generateAnyTypePattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyTypePattern() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        SimpleType anyType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}anyType", null);
        Pattern result = instance.generateAnyTypePattern(anyType);

        assertTrue(result instanceof Ref);
        Ref ref = (Ref) result;

        assertEquals("anyType", ref.getRefName());
        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();

        define.getPatterns();

        assertEquals(1, define.getPatterns().size());
        assertTrue(define.getPatterns().getFirst() instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) define.getPatterns().getFirst();

        assertEquals(1, zeroOrMore.getPatterns().size());
        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof Choice);
        Choice choice = (Choice) zeroOrMore.getPatterns().getFirst();

        assertEquals(3, choice.getPatterns().size());
        assertTrue(choice.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) choice.getPatterns().getFirst();

        assertTrue(element.getNameClass() instanceof AnyName);
        AnyName anyName = (AnyName) element.getNameClass();

        assertTrue(anyName.getExceptNames().isEmpty());
        assertTrue(anyName.getAttributeNamespace() == null);

        assertEquals(1, element.getPatterns().size());
        assertTrue(element.getPatterns().get(0) instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        Ref ref2 = (Ref) element.getPatterns().get(0);
        assertEquals("anyType", ref2.getRefName());
        assertEquals(1, ref2.getDefineList().size());

        Define define2 = ref2.getDefineList().getFirst();
        assertEquals(define, define2);

        assertTrue(choice.getPatterns().get(1) instanceof eu.fox7.schematoolkit.relaxng.om.Text);

        assertTrue(choice.getPatterns().get(2) instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attribute = (eu.fox7.schematoolkit.relaxng.om.Attribute) choice.getPatterns().get(2);

        assertTrue(attribute.getNameClass() instanceof AnyName);
        AnyName anyName2 = (AnyName) attribute.getNameClass();

        assertTrue(anyName2.getExceptNames().isEmpty());
        assertTrue(anyName2.getAttributeNamespace() == null);
    }

    /**
     * Test of generateAnyElementPattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyElementPattern_without_Namespace() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, null);

        Pattern result = instance.generateAnyElementPattern(anyPattern);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) result;

        assertTrue(element.getNameClass() instanceof AnyName);
        AnyName anyName = (AnyName) element.getNameClass();

        assertTrue(anyName.getExceptNames().isEmpty());
        assertTrue(anyName.getAttributeNamespace() == null);

        assertEquals(1, element.getPatterns().size());
        assertTrue(element.getPatterns().get(0) instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        Ref ref2 = (Ref) element.getPatterns().get(0);
        assertEquals("anyType", ref2.getRefName());
        assertEquals(1, ref2.getDefineList().size());

    }

    /**
     * Test of generateAnyElementPattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyElementPattern_with_Namespace() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "http://myNamespace.com/");

        Pattern result = instance.generateAnyElementPattern(anyPattern);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) result;

        assertTrue(element.getNameClass() instanceof NsName);
        NsName nsName = (NsName) element.getNameClass();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals("http://myNamespace.com/"));

        assertEquals(1, element.getPatterns().size());
        assertTrue(element.getPatterns().get(0) instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        Ref ref2 = (Ref) element.getPatterns().get(0);
        assertEquals("anyType", ref2.getRefName());
        assertEquals(1, ref2.getDefineList().size());

    }

    /**
     * Test of generateAnyElementPattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyElementPattern_with_local() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##local");

        Pattern result = instance.generateAnyElementPattern(anyPattern);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) result;

        assertTrue(element.getNameClass() instanceof NsName);
        NsName nsName = (NsName) element.getNameClass();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals(""));

        assertEquals(1, element.getPatterns().size());
        assertTrue(element.getPatterns().get(0) instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        Ref ref2 = (Ref) element.getPatterns().get(0);
        assertEquals("anyType", ref2.getRefName());
        assertEquals(1, ref2.getDefineList().size());
    }

    @Test
    public void testGenerateAnyElementPattern_with_other() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##other");

        Pattern result = instance.generateAnyElementPattern(anyPattern);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) result;

        assertTrue(element.getNameClass() instanceof AnyName);
        AnyName anyName = (AnyName) element.getNameClass();

        assertFalse(anyName.getExceptNames().isEmpty());
        assertTrue(anyName.getAttributeNamespace() == null);

        assertTrue(anyName.getExceptNames().iterator().next() instanceof NsName);
        NsName nsName = (NsName) anyName.getExceptNames().iterator().next();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals(""));

        assertEquals(1, element.getPatterns().size());
        assertTrue(element.getPatterns().get(0) instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        Ref ref2 = (Ref) element.getPatterns().get(0);
        assertEquals("anyType", ref2.getRefName());
        assertEquals(1, ref2.getDefineList().size());
    }

        /**
     * Test of generateAnyElementPattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyElementPattern_with_targetNamespace() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##targetNamespace");

        Pattern result = instance.generateAnyElementPattern(anyPattern);

        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);
        eu.fox7.schematoolkit.relaxng.om.Element element = (eu.fox7.schematoolkit.relaxng.om.Element) result;

        assertTrue(element.getNameClass() instanceof NsName);
        NsName nsName = (NsName) element.getNameClass();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals(""));

        assertEquals(1, element.getPatterns().size());
        assertTrue(element.getPatterns().get(0) instanceof eu.fox7.schematoolkit.relaxng.om.Ref);

        Ref ref2 = (Ref) element.getPatterns().get(0);
        assertEquals("anyType", ref2.getRefName());
        assertEquals(1, ref2.getDefineList().size());
    }

    /**
     * Test of generateAnyAttributePattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyAttributePattern_without_Namespace() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);


        AnyAttribute anyAttribute = new AnyAttribute();

        Pattern result = instance.generateAnyAttributePattern(anyAttribute);

        assertTrue(result instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) result;

        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attribute = (eu.fox7.schematoolkit.relaxng.om.Attribute) zeroOrMore.getPatterns().getFirst();

        assertTrue(attribute.getNameClass() instanceof AnyName);
        AnyName anyName2 = (AnyName) attribute.getNameClass();

        assertTrue(anyName2.getExceptNames().isEmpty());
        assertTrue(anyName2.getAttributeNamespace() == null);
    }

    /**
     * Test of generateAnyAttributePattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyAttributePattern_with_Namespace() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyAttribute anyAttribute = new AnyAttribute("http://myNamespace.com/");

        Pattern result = instance.generateAnyAttributePattern(anyAttribute);

        assertTrue(result instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) result;

        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attribute = (eu.fox7.schematoolkit.relaxng.om.Attribute) zeroOrMore.getPatterns().getFirst();

        assertTrue(attribute.getNameClass() instanceof NsName);
        NsName nsName = (NsName) attribute.getNameClass();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals("http://myNamespace.com/"));
    }

    /**
     * Test of generateAnyAttributePattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyAttributePattern_with_local() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyAttribute anyAttribute = new AnyAttribute("##local");

        Pattern result = instance.generateAnyAttributePattern(anyAttribute);

        assertTrue(result instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) result;

        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attribute = (eu.fox7.schematoolkit.relaxng.om.Attribute) zeroOrMore.getPatterns().getFirst();

        assertTrue(attribute.getNameClass() instanceof NsName);
        NsName nsName = (NsName) attribute.getNameClass();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals(""));
    }

    /**
     * Test of generateAnyAttributePattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyAttributePattern_with_other() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);


        AnyAttribute anyAttribute = new AnyAttribute("##other");

        Pattern result = instance.generateAnyAttributePattern(anyAttribute);

        assertTrue(result instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) result;

        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attribute = (eu.fox7.schematoolkit.relaxng.om.Attribute) zeroOrMore.getPatterns().getFirst();

        assertTrue(attribute.getNameClass() instanceof AnyName);
        AnyName anyName = (AnyName) attribute.getNameClass();

        assertFalse(anyName.getExceptNames().isEmpty());
        assertTrue(anyName.getAttributeNamespace() == null);

        assertTrue(anyName.getExceptNames().iterator().next() instanceof NsName);
        NsName nsName = (NsName) anyName.getExceptNames().iterator().next();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals(""));
    }

    /**
     * Test of generateAnyAttributePattern method, of class ConverterBase.
     */
    @Test
    public void testGenerateAnyAttributePattern_with_targetNamespace() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        AnyAttribute anyAttribute = new AnyAttribute("##targetNamespace");

        Pattern result = instance.generateAnyAttributePattern(anyAttribute);

        assertTrue(result instanceof ZeroOrMore);
        ZeroOrMore zeroOrMore = (ZeroOrMore) result;

        assertTrue(zeroOrMore.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Attribute);
        eu.fox7.schematoolkit.relaxng.om.Attribute attribute = (eu.fox7.schematoolkit.relaxng.om.Attribute) zeroOrMore.getPatterns().getFirst();

        assertTrue(attribute.getNameClass() instanceof NsName);
        NsName nsName = (NsName) attribute.getNameClass();

        assertTrue(nsName.getExceptNames().isEmpty());
        assertTrue(nsName.getAttributeNamespace().equals(""));
    }

    /**
     * Test of getLocalNamespaces method, of class ConverterBase.
     */
    @Test
    public void testGetLocalNamespaces() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        LinkedHashSet<String> localNamespaces = new LinkedHashSet<String>();
        
        instance.getLocalNamespaces(xmlSchema, localNamespaces, new LinkedHashSet<XSDSchema>());

        assertEquals(1, localNamespaces.size());
        assertEquals("", localNamespaces.iterator().next());


        ImportedSchema importedSchema = new ImportedSchema("http://myNamespace.com/", "test");
        importedSchema.setSchema(new XSDSchema("http://myNamespace.com/"));
        xmlSchema.addForeignSchema(importedSchema);

        LinkedHashSet<String> localNamespaces2 = new LinkedHashSet<String>();
        instance.getLocalNamespaces(xmlSchema, localNamespaces2, new LinkedHashSet<XSDSchema>());

        assertEquals(2, localNamespaces2.size());
        Iterator<String> itNamespaces = localNamespaces2.iterator();
        assertEquals("", itNamespaces.next());
        assertEquals("http://myNamespace.com/", itNamespaces.next());


        ImportedSchema importedSchema2 = new ImportedSchema("http://myNamespace2.com/", "test2");
        importedSchema2.setSchema(new XSDSchema("http://myNamespace2.com/"));

        importedSchema.getSchema().addForeignSchema(importedSchema2);
        
        LinkedHashSet<String> localNamespaces3 = new LinkedHashSet<String>();
        instance.getLocalNamespaces(xmlSchema, localNamespaces3, new LinkedHashSet<XSDSchema>());

        assertEquals(3, localNamespaces3.size());
        Iterator<String> itNamespaces2 = localNamespaces3.iterator();
        assertEquals("", itNamespaces2.next());
        assertEquals("http://myNamespace.com/", itNamespaces2.next());
        assertEquals("http://myNamespace2.com/", itNamespaces2.next());

    }

    /**
     * Test of isXMLSchemaBuiltInType method, of class ConverterBase.
     */
    @Test
    public void testIsXMLSchemaBuiltInType() {

        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);
        
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "string"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "boolean"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "float"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "decimal"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "datetime"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "duration"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "hexBinary"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "base64binary"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "anyURI"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "ID"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "IDREF"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "IDREFS"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "ENTITY"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "NOTATION"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "normalizedString"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "token"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "language"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "ENTITIES"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "NMTOKEN"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "NMTOKENS"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "Name"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "QName"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "NCName"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "integer"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "nonNegativeInteger"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "positiveInteger"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "NonPositiveInteger"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "NegativeInteger"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "byte"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "int"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "long"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "short"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "unsignedByte"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "unsignedInt"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "unsignedLong"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "unsignedShort"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "date"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "time"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "GYearMonth"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "GYear"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "GMonthDay"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "GDay"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "GMonth"));
        assertTrue(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "anySimpleType"));

        assertFalse(instance.isXMLSchemaBuiltInType("test", "short"));
        assertFalse(instance.isXMLSchemaBuiltInType(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, "wrong"));
    }

    /**
     * Test of collectAttributeNamesWithIDTypeRecursively method, of class ConverterBase.
     */
    @Test
    public void testCollectAttributeNamesWithIDTypeRecursively() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        LinkedHashSet<String> attributeNamesWithIDType = new LinkedHashSet<String>();
        LinkedHashSet<XSDSchema> alreadySeenSchemas = new LinkedHashSet<XSDSchema>();
        instance.collectAttributeNamesWithIDTypeRecursively(xmlSchema, attributeNamesWithIDType, alreadySeenSchemas);

        assertEquals(0, attributeNamesWithIDType.size());

        SimpleType simpleType = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}ID", null);
        simpleType.setIsAnonymous(false);

        eu.fox7.schematoolkit.xsd.om.Attribute attribute = new Attribute("{}myAttribute", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}ID", simpleType));
        attribute.setTypeAttr(Boolean.TRUE);
        xmlSchema.addAttribute(xmlSchema.getAttributeSymbolTable().updateOrCreateReference("{}myAttribute", attribute));

        ImportedSchema importedSchema = new ImportedSchema("http://myNamespace.com/", "test");
        importedSchema.setSchema(new XSDSchema("http://myNamespace.com/"));
        xmlSchema.addForeignSchema(importedSchema);

        ImportedSchema importedSchema2 = new ImportedSchema("http://myNamespace2.com/", "test2");
        importedSchema2.setSchema(new XSDSchema("http://myNamespace2.com/"));

        importedSchema.getSchema().addForeignSchema(importedSchema2);

        SimpleType simpleType2 = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}IDREF", null);
        simpleType2.setIsAnonymous(false);

        eu.fox7.schematoolkit.xsd.om.Attribute attribute2 = new Attribute("{}myAttribute2", importedSchema2.getSchema().getTypeSymbolTable().updateOrCreateReference("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}IDREF", simpleType2));
        attribute2.setTypeAttr(Boolean.TRUE);
        importedSchema2.getSchema().addAttribute(importedSchema2.getSchema().getAttributeSymbolTable().updateOrCreateReference("{}myAttribute2", attribute2));

        LinkedHashSet<String> attributeNamesWithIDType2 = new LinkedHashSet<String>();
        LinkedHashSet<XSDSchema> alreadySeenSchemas2 = new LinkedHashSet<XSDSchema>();
        instance.collectAttributeNamesWithIDTypeRecursively(xmlSchema, attributeNamesWithIDType2, alreadySeenSchemas2);

        assertEquals(2, attributeNamesWithIDType2.size());
        Iterator<String> itString = attributeNamesWithIDType2.iterator();
        assertEquals("{}myAttribute", itString.next());
        assertEquals("{}myAttribute2", itString.next());

    }

    /**
     * Test of collectAttributeNamesWithIDTypeRecursively method, of class ConverterBase.
     */
    @Test
    public void testCollectAttributeNamesWithIDTypeRecursively_particle() throws Exception {
        String uri = "tests/eu/fox7/bonxai/converter/xsd2relaxng/converterBaseTests/attributeIDtype.xsd";
        uri = this.getClass().getResource("/"+uri).getFile();
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);

        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        LinkedHashSet<String> attributeNamesWithIDType2 = new LinkedHashSet<String>();
        LinkedHashSet<XSDSchema> alreadySeenSchemas2 = new LinkedHashSet<XSDSchema>();
        instance.collectAttributeNamesWithIDTypeRecursively(xmlSchema, attributeNamesWithIDType2, alreadySeenSchemas2);

        assertEquals(2, attributeNamesWithIDType2.size());
        assertTrue(attributeNamesWithIDType2.contains("{http://myNamespace.com}myOne"));
        assertTrue(attributeNamesWithIDType2.contains("{http://myNamespace.com}myTwo"));
        
    }

    /**
     * Test of getNamespace method, of class ConverterBase.
     */
    @Test
    public void testGetNamespace() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        String result = instance.getNamespace("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}elementName");
        assertEquals(XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE, result);

        result = instance.getNamespace("{}elementName");
        assertEquals("", result);

        result = instance.getNamespace("elementName");
        assertEquals("", result);
    }

    /**
     * Test of getLocalName method, of class ConverterBase.
     */
    @Test
    public void testGetLocalName() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema rng = new RelaxNGSchema();

        ConverterBase instance = new ConverterBase(xmlSchema, rng) {
        };
        Grammar grammar = new Grammar();
        rng.setRootPattern(grammar);

        String result = instance.getLocalName("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}elementName");
        assertEquals("elementName", result);

        result = instance.getLocalName("{}elementName");
        assertEquals("elementName", result);

        result = instance.getLocalName("elementName");
        assertEquals("elementName", result);

        result = instance.getLocalName("{namespace}");
        assertEquals("", result);
    }
}
