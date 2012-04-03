package eu.fox7.bonxai.converter.xsd2relaxng;

import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.HashMap;

import eu.fox7.bonxai.converter.xsd2relaxng.ElementConverter;
import eu.fox7.bonxai.converter.xsd2relaxng.InheritanceInformationCollector;
import eu.fox7.bonxai.converter.xsd2relaxng.SubstitutionGroupInformationCollector;
import eu.fox7.bonxai.converter.xsd2relaxng.XSD2RelaxNGConverter;
import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.relaxng.Choice;
import eu.fox7.schematoolkit.relaxng.Data;
import eu.fox7.schematoolkit.relaxng.Define;
import eu.fox7.schematoolkit.relaxng.Grammar;
import eu.fox7.schematoolkit.relaxng.NotAllowed;
import eu.fox7.schematoolkit.relaxng.Pattern;
import eu.fox7.schematoolkit.relaxng.Ref;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.Value;
import eu.fox7.schematoolkit.xsd.om.Attribute;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.XSDParser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ElementConverter
 * @author Lars Schmidt
 */
public class ElementConverterTest extends junit.framework.TestCase {

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        Element xsdElement = new Element("namespace", "name", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type));

        boolean setRef = false;

        ElementConverter instance = new ElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());

        Pattern result = instance.convertElement(xsdElement, setRef);
        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element elementPattern = (eu.fox7.schematoolkit.relaxng.om.Element) result;
        assertEquals("name", elementPattern.getNameAttribute());
        assertEquals("namespace", elementPattern.getAttributeNamespace());

        assertEquals(1, elementPattern.getPatterns().size());
        assertTrue(elementPattern.getPatterns().getFirst() instanceof Data);


    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_abstract() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        Element xsdElement = new Element("namespace", "name", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type));
        xsdElement.setAbstract(true);

        boolean setRef = false;

        ElementConverter instance = new ElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());

        Pattern result = instance.convertElement(xsdElement, setRef);
        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element elementPattern = (eu.fox7.schematoolkit.relaxng.om.Element) result;
        assertEquals("name", elementPattern.getNameAttribute());
        assertEquals("namespace", elementPattern.getAttributeNamespace());
        assertEquals(2, elementPattern.getPatterns().size());

        assertTrue(elementPattern.getPatterns().getFirst() instanceof NotAllowed);
        assertTrue(elementPattern.getPatterns().getLast() instanceof Data);
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_fixed() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        Element xsdElement = new Element("namespace", "name", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type));
        xsdElement.setFixed("fixedValue");

        boolean setRef = false;

        ElementConverter instance = new ElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());

        Pattern result = instance.convertElement(xsdElement, setRef);
        assertTrue(result instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element elementPattern = (eu.fox7.schematoolkit.relaxng.om.Element) result;
        assertEquals("name", elementPattern.getNameAttribute());
        assertEquals("namespace", elementPattern.getAttributeNamespace());
        assertEquals(1, elementPattern.getPatterns().size());

        assertTrue(elementPattern.getPatterns().getFirst() instanceof Value);
        Value value = (Value) elementPattern.getPatterns().getFirst();
        assertEquals("fixedValue", value.getContent());
        assertTrue(value.getType() == null);
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_ref() {
        XSDSchema xmlSchema = new XSDSchema();
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        SimpleType type = new SimpleType("{" + XSD2RelaxNGConverter.XMLSCHEMA_NAMESPACE + "}string", null, true);
        Element xsdElement = new Element("namespace", "name", xmlSchema.getTypeSymbolTable().updateOrCreateReference("{namespace}type", type));
        xsdElement.setFixed("fixedValue");

        boolean setRef = true;

        ElementConverter instance = new ElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                new LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>>(),
                new LinkedHashMap<Type, LinkedHashSet<Type>>());

        Pattern result = instance.convertElement(xsdElement, setRef);

        assertTrue(result instanceof Ref);

        Ref ref = (Ref) result;
        assertEquals(1, ref.getDefineList().size());
        Define define = ref.getDefineList().getFirst();
        assertEquals(1, define.getPatterns().size());

        assertTrue(define.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element elementPattern = (eu.fox7.schematoolkit.relaxng.om.Element) define.getPatterns().getFirst();
        assertEquals("name", elementPattern.getNameAttribute());
        assertEquals("namespace", elementPattern.getAttributeNamespace());
        assertEquals(1, elementPattern.getPatterns().size());

        assertTrue(elementPattern.getPatterns().getFirst() instanceof Value);
        Value value = (Value) elementPattern.getPatterns().getFirst();
        assertEquals("fixedValue", value.getContent());
        assertTrue(value.getType() == null);
    }

    /**
     * Test of handleSubstitutions method, of class ElementConverter.
     */
    @Test
    public void testHandleSubstitutions() throws Exception {
        String uri = "/tests/eu/fox7/bonxai/converter/xsd2relaxng/elementConverterTests/elementSubstitutions.xsd";
        uri = this.getClass().getResource(uri).getFile();
        
        XSDParser xsdParser = new XSDParser(false, false);
        XSDSchema xmlSchema = xsdParser.parse(uri);
        RelaxNGSchema relaxNGSchema = new RelaxNGSchema();

        // Define a grammar as root pattern of the new RELAX NG schema
        Grammar grammar = new Grammar(XSD2RelaxNGConverter.RELAXNG_XMLSCHEMA_DATATYPE_NAMESPACE, "", XSD2RelaxNGConverter.RELAXNG_NAMESPACE);
        relaxNGSchema.setRootPattern(grammar);

        boolean setRef = false;

        InheritanceInformationCollector inheritanceInformationCollector = new InheritanceInformationCollector();
        inheritanceInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = inheritanceInformationCollector.getInheritanceInformation();

        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();
        substitutionGroupInformationCollector.collectInformation(xmlSchema);

        LinkedHashMap<Element, LinkedHashSet<Element>> substitutionGroupInformation = substitutionGroupInformationCollector.getSubstitutionGroupInformation();


        Element xsdElement = xmlSchema.getElementSymbolTable().getReference("{}C").getReference();

        ElementConverter instance = new ElementConverter(
                xmlSchema,
                relaxNGSchema,
                new HashMap<Attribute, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<eu.fox7.schematoolkit.xsd.om.Element, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Group, SymbolTableRef<LinkedList<Define>>>(),
                new HashMap<Type, SymbolTableRef<LinkedList<Define>>>(),
                substitutionGroupInformation,
                inheritanceInformation);

        Pattern pattern = instance.convertElement(xsdElement, setRef);

        Pattern result = instance.handleSubstitutions(xsdElement, pattern);

        assertTrue(result instanceof Choice);

        Choice choice = (Choice) result;
        assertEquals(3, choice.getPatterns().size());

        assertEquals(pattern, choice.getPatterns().get(0));

        assertTrue(choice.getPatterns().get(1) instanceof Ref);

        Ref ref1 = (Ref) choice.getPatterns().get(1);
        assertEquals(1, ref1.getDefineList().size());
        Define define1 = ref1.getDefineList().getFirst();
        assertEquals(1, define1.getPatterns().size());

        assertTrue(define1.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element2 = (eu.fox7.schematoolkit.relaxng.om.Element) define1.getPatterns().getFirst();

        assertEquals("D", element2.getNameAttribute());

        assertTrue(choice.getPatterns().get(2) instanceof Ref);

        Ref ref2 = (Ref) choice.getPatterns().get(2);
        assertEquals(1, ref2.getDefineList().size());
        Define define2 = ref2.getDefineList().getFirst();
        assertEquals(1, define2.getPatterns().size());

        assertTrue(define2.getPatterns().getFirst() instanceof eu.fox7.schematoolkit.relaxng.om.Element);

        eu.fox7.schematoolkit.relaxng.om.Element element3 = (eu.fox7.schematoolkit.relaxng.om.Element) define2.getPatterns().getFirst();

        assertEquals("E", element3.getNameAttribute());

        // ----- no substitutions -----
        Element xsdElement2 = xmlSchema.getElementSymbolTable().getReference("{}root").getReference();
        Pattern pattern2 = instance.convertElement(xsdElement2, setRef);
        Pattern result2 = instance.handleSubstitutions(xsdElement2, pattern2);
        assertEquals(pattern2, result2);
    }
}
