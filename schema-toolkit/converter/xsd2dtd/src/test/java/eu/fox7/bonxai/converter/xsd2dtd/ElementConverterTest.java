package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.bonxai.converter.xsd2dtd.ElementConverter;
import eu.fox7.bonxai.converter.xsd2dtd.ElementWrapper;
import eu.fox7.bonxai.converter.xsd2dtd.XSD2DTDConverter;
import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.ElementRef;
import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Constraint;
import eu.fox7.schematoolkit.xsd.om.Key;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ElementConverterTest
 * @author Lars Schmidt
 */
public class ElementConverterTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema(XSD2DTDConverter.XMLSCHEMA_NAMESPACE);
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of convert method, of class ElementConverter.
     */
    @Test
    public void testConvert() throws Exception {

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{}elementName");
        this.schema.addElement(this.schema.getElementSymbolTable().updateOrCreateReference("{}elementName", element));

        ElementConverter instance = new ElementConverter(schema, dtd);

        LinkedHashMap<String, ElementWrapper> hashMapElementWrapper = instance.convert();

        ElementWrapper elementWrapper = hashMapElementWrapper.get("elementName");

        assertEquals("elementName", elementWrapper.getDTDElementName());
        Element dtdElement = elementWrapper.getDTDElements().iterator().next();
        assertEquals("elementName", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);
    }

    /**
     * Test of addDTDElement method, of class ElementConverter.
     */
    @Test
    public void testAddDTDElement() {
        String dtdName = "elementName";
        Element element = new Element(dtdName);
        element.setMixed(Boolean.TRUE);
        ElementRef elementRef = new ElementRef(dtd.getElementSymbolTable().updateOrCreateReference(dtdName, element));
        ElementConverter instance = new ElementConverter(schema, dtd);

        assertEquals(0, instance.getElementMap().size());
        instance.addDTDElement(dtdName, element, elementRef);
        assertEquals(1, instance.getElementMap().size());
        instance.addDTDElement(dtdName, element, elementRef);
        assertEquals(1, instance.getElementMap().size());

        LinkedHashMap<String, ElementWrapper> hashMapElementWrapper = instance.getElementMap();
        ElementWrapper elementWrapper = hashMapElementWrapper.get("elementName");

        assertEquals("elementName", elementWrapper.getDTDElementName());
        Element dtdElement = elementWrapper.getDTDElements().iterator().next();
        assertEquals("elementName", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());

    }

    /**
     * Test of addDTDAttributes method, of class ElementConverter.
     */
    @Test
    public void testAddDTDAttributes() {
        String dtdName = "elementName";
        Element element = new Element(dtdName);
        element.setMixed(Boolean.TRUE);
        ElementRef elementRef = new ElementRef(dtd.getElementSymbolTable().updateOrCreateReference(dtdName, element));
        ElementConverter instance = new ElementConverter(schema, dtd);

        assertEquals(0, instance.getElementMap().size());
        instance.addDTDElement(dtdName, element, elementRef);
        assertEquals(1, instance.getElementMap().size());
        instance.addDTDElement(dtdName, element, elementRef);
        assertEquals(1, instance.getElementMap().size());

        Attribute myAttribute1 = new Attribute("attribute1", null);
        LinkedList<Attribute> dtdAttributeList = new LinkedList<Attribute>();
        dtdAttributeList.add(myAttribute1);

        instance.addDTDAttributes(dtdName, dtdAttributeList);

        Attribute myAttribute2 = new Attribute("attribute1", null);
        LinkedList<Attribute> dtdAttributeList2 = new LinkedList<Attribute>();
        dtdAttributeList2.add(myAttribute1);
        dtdAttributeList2.add(myAttribute2);

        instance.addDTDAttributes(dtdName, dtdAttributeList2);

        LinkedHashMap<String, ElementWrapper> hashMapElementWrapper = instance.getElementMap();

        ElementWrapper elementWrapper = hashMapElementWrapper.get("elementName");
        LinkedHashMap<String, LinkedHashSet<Attribute>> hashMapAttributes = elementWrapper.getDTDAttributeMap();

        LinkedHashSet<Attribute> attributeSet = hashMapAttributes.get("attribute1");

        assertEquals("elementName", elementWrapper.getDTDElementName());
        assertEquals(2, attributeSet.size());
        assertEquals("attribute1", attributeSet.iterator().next().getName());
    }

    /**
     * Test of addConstraints method, of class ElementConverter.
     */
    @Test
    public void testAddConstraints() {
        String dtdName = "elementName";

        ElementConverter instance = new ElementConverter(schema, dtd);

        LinkedList<Constraint> xsdConstraints = new LinkedList<Constraint>();
        Key key = new Key("{}myFQName", ".//elementName");
        xsdConstraints.add(key);


        assertEquals(0, instance.getElementMap().size());

        instance.addConstraints(dtdName, xsdConstraints);

        LinkedHashMap<String, ElementWrapper> hashMapElementWrapper = instance.getElementMap();
        ElementWrapper elementWrapper = hashMapElementWrapper.get("elementName");
        assertEquals("elementName", elementWrapper.getDTDElementName());

        assertEquals(1, elementWrapper.getXsdContraints().size());

    }

    /**
     * Test of addConstraints method, of class ElementConverter.
     */
    @Test
    public void testAddGlobalConstraints() {
        String dtdName = "elementName";

        ElementConverter instance = new ElementConverter(schema, dtd);

        LinkedList<Constraint> xsdConstraints = new LinkedList<Constraint>();
        Key key = new Key("{}myFQName", ".//*");
        xsdConstraints.add(key);
        assertEquals(0, instance.getGlobalConstraints().size());
        instance.addConstraints(dtdName, xsdConstraints);

        assertEquals(1, instance.getGlobalConstraints().size());
    }

    /**
     * Test of getElementMap method, of class ElementConverter.
     */
    @Test
    public void testGetElementMap() {
        String dtdName = "elementName";
        Element element = new Element(dtdName);
        element.setMixed(Boolean.TRUE);
        ElementRef elementRef = new ElementRef(dtd.getElementSymbolTable().updateOrCreateReference(dtdName, element));
        ElementConverter instance = new ElementConverter(schema, dtd);

        assertEquals(0, instance.getElementMap().size());
        instance.addDTDElement(dtdName, element, elementRef);
        assertEquals(1, instance.getElementMap().size());
        instance.addDTDElement(dtdName, element, elementRef);
        assertEquals(1, instance.getElementMap().size());

        LinkedHashMap<String, ElementWrapper> hashMapElementWrapper = instance.getElementMap();
        ElementWrapper elementWrapper = hashMapElementWrapper.get("elementName");

        assertEquals("elementName", elementWrapper.getDTDElementName());
        Element dtdElement = elementWrapper.getDTDElements().iterator().next();
        assertEquals("elementName", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
    }

    /**
     * Test of getGlobalConstraints method, of class ElementConverter.
     */
    @Test
    public void testGetGlobalConstraints() {
        String dtdName = "elementName";

        ElementConverter instance = new ElementConverter(schema, dtd);

        LinkedList<Constraint> xsdConstraints = new LinkedList<Constraint>();
        Key key = new Key("{}myFQName", ".//*");
        xsdConstraints.add(key);
        assertEquals(0, instance.getGlobalConstraints().size());
        instance.addConstraints(dtdName, xsdConstraints);

        assertEquals(1, instance.getGlobalConstraints().size());
    }

    public ElementConverter initElementConverter() {
        schema.setTargetNamespace(XSD2DTDConverter.XMLSCHEMA_NAMESPACE);
        schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xsd", XSD2DTDConverter.XMLSCHEMA_NAMESPACE));
        ElementConverter elementConverter = new ElementConverter(schema, dtd);
        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = true;
        return elementConverter;
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_Element() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
    }


    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_empty_complexType() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        ComplexType complexType = new ComplexType("{}type", null);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() == null);
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_simpleType() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        SimpleType simpleType = new SimpleType("{}type", null);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(simpleType.getName(), simpleType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertTrue(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() == null);
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_mixed_without_content() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        ComplexType complexType = new ComplexType("{}type", null);
        complexType.setMixed(Boolean.TRUE);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertTrue(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() == null);
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_mixed_with_content() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element3", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(sequencePattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        complexType.setMixed(Boolean.TRUE);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertTrue(dtdElement.getMixed());
        assertTrue(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        assertTrue(countingPattern.getParticles().getFirst() instanceof ChoicePattern);

        ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
        assertEquals(2, choicePattern.getParticles().size());
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_content() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        sequencePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element3", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(sequencePattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern1 = (SequencePattern) dtdElement.getParticle();
        assertEquals(3, sequencePattern1.getParticles().size());
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_choice() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element3", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(choicePattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof ChoicePattern);

        ChoicePattern choicePattern1 = (ChoicePattern) dtdElement.getParticle();
        assertEquals(3, choicePattern1.getParticles().size());
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_anyPattern() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, null);
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(anyPattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);

    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_deeper_anyPattern() {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        choicePattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element3", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        choicePattern.addParticle(new AnyPattern(ProcessContentsInstruction.Strict, null));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(choicePattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);

    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_allPattern_1() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.ALL_UPPER_BOUND_ANY = 7;
        XSD2DTDConverter.ALL_UPPER_BOUND_CHOICE = 6;
        XSD2DTDConverter.ALL_UPPER_BOUND_PERMUTATION = 5;

        AllPattern allPattern = new AllPattern();
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(allPattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof ChoicePattern);

        ChoicePattern choicePattern = (ChoicePattern) dtdElement.getParticle();
        assertEquals(2, choicePattern.getParticles().size());
        assertTrue(choicePattern.getParticles().get(0) instanceof SequencePattern);
        assertTrue(choicePattern.getParticles().get(1) instanceof SequencePattern);
        SequencePattern sequencePattern1 = (SequencePattern) choicePattern.getParticles().get(0);
        SequencePattern sequencePattern2 = (SequencePattern) choicePattern.getParticles().get(1);

        assertTrue(sequencePattern1.getParticles().get(0) instanceof ElementRef);
        assertTrue(sequencePattern1.getParticles().get(1) instanceof ElementRef);
        assertTrue(sequencePattern2.getParticles().get(0) instanceof ElementRef);
        assertTrue(sequencePattern2.getParticles().get(1) instanceof ElementRef);

        Element element1 = ((ElementRef) sequencePattern1.getParticles().get(0)).getElement();
        Element element2 = ((ElementRef) sequencePattern1.getParticles().get(1)).getElement();
        Element element3 = ((ElementRef) sequencePattern2.getParticles().get(0)).getElement();
        Element element4 = ((ElementRef) sequencePattern2.getParticles().get(1)).getElement();

        assertEquals("element1", element1.getName());
        assertEquals("element2", element2.getName());
        assertEquals("element2", element3.getName());
        assertEquals("element1", element4.getName());

    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_allPattern_2() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.ALL_UPPER_BOUND_ANY = 7;
        XSD2DTDConverter.ALL_UPPER_BOUND_CHOICE = 6;
        XSD2DTDConverter.ALL_UPPER_BOUND_PERMUTATION = 1;

        AllPattern allPattern = new AllPattern();
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(allPattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) dtdElement.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().get(0) instanceof ChoicePattern);
        assertTrue(sequencePattern.getParticles().get(1) instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) sequencePattern.getParticles().get(0);
        ChoicePattern choicePattern2 = (ChoicePattern) sequencePattern.getParticles().get(1);

        assertTrue(choicePattern1.getParticles().get(0) instanceof ElementRef);
        assertTrue(choicePattern1.getParticles().get(1) instanceof ElementRef);
        assertTrue(choicePattern2.getParticles().get(0) instanceof ElementRef);
        assertTrue(choicePattern2.getParticles().get(1) instanceof ElementRef);

        Element element1 = ((ElementRef) choicePattern1.getParticles().get(0)).getElement();
        Element element2 = ((ElementRef) choicePattern1.getParticles().get(1)).getElement();
        Element element3 = ((ElementRef) choicePattern2.getParticles().get(0)).getElement();
        Element element4 = ((ElementRef) choicePattern2.getParticles().get(1)).getElement();

        assertEquals("element1", element1.getName());
        assertEquals("element2", element2.getName());
        assertEquals("element1", element3.getName());
        assertEquals("element2", element4.getName());

    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_allPattern_3() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.ALL_UPPER_BOUND_ANY = 7;
        XSD2DTDConverter.ALL_UPPER_BOUND_CHOICE = 1;
        XSD2DTDConverter.ALL_UPPER_BOUND_PERMUTATION = 1;

        AllPattern allPattern = new AllPattern();
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(allPattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        assertEquals(1, countingPattern.getParticles().size());
        assertTrue(countingPattern.getParticles().get(0) instanceof ChoicePattern);
        ChoicePattern choicePattern1 = (ChoicePattern) countingPattern.getParticles().get(0);

        assertTrue(choicePattern1.getParticles().get(0) instanceof ElementRef);
        assertTrue(choicePattern1.getParticles().get(1) instanceof ElementRef);

        Element element1 = ((ElementRef) choicePattern1.getParticles().get(0)).getElement();
        Element element2 = ((ElementRef) choicePattern1.getParticles().get(1)).getElement();

        assertEquals("element1", element1.getName());
        assertEquals("element2", element2.getName());

    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_allPattern_4() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.ALL_UPPER_BOUND_ANY = 1;
        XSD2DTDConverter.ALL_UPPER_BOUND_CHOICE = 1;
        XSD2DTDConverter.ALL_UPPER_BOUND_PERMUTATION = 1;

        AllPattern allPattern = new AllPattern();
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        allPattern.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element2", new eu.fox7.schematoolkit.xsd.om.Element("{}element2"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(allPattern);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertTrue(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof AnyPattern);

    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_countingPattern_1() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = 40;

        CountingPattern countingPattern1 = new CountingPattern(0, 1);
        countingPattern1.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(countingPattern1);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(1, countingPattern.getMax().intValue());

        assertEquals(1, countingPattern.getParticles().size());
        assertTrue(countingPattern.getParticles().get(0) instanceof ElementRef);
        Element element1 = ((ElementRef) countingPattern.getParticles().get(0)).getElement();
        assertEquals("element1", element1.getName());
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_countingPattern_2() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = 40;

        CountingPattern countingPattern1 = new CountingPattern(0, null);
        countingPattern1.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(countingPattern1);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        assertEquals(1, countingPattern.getParticles().size());
        assertTrue(countingPattern.getParticles().get(0) instanceof ElementRef);
        Element element1 = ((ElementRef) countingPattern.getParticles().get(0)).getElement();
        assertEquals("element1", element1.getName());
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_countingPattern_3() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = 40;

        CountingPattern countingPattern1 = new CountingPattern(1, null);
        countingPattern1.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(countingPattern1);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) dtdElement.getParticle();
        assertEquals(1, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        assertEquals(1, countingPattern.getParticles().size());
        assertTrue(countingPattern.getParticles().get(0) instanceof ElementRef);
        Element element1 = ((ElementRef) countingPattern.getParticles().get(0)).getElement();
        assertEquals("element1", element1.getName());
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_countingPattern_4() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = 2;

        CountingPattern countingPattern1 = new CountingPattern(1, 3);
        countingPattern1.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(countingPattern1);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) dtdElement.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        assertEquals(1, countingPattern.getParticles().size());
        assertTrue(countingPattern.getParticles().get(0) instanceof ElementRef);
        Element element1 = ((ElementRef) countingPattern.getParticles().get(0)).getElement();
        assertEquals("element1", element1.getName());
    }

    /**
     * Test of convertElement method, of class ElementConverter.
     */
    @Test
    public void testConvertElement_complexType_with_countingPattern_5() throws Exception {
        ElementConverter instance = initElementConverter();

        eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}name");

        XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY = 2;

        CountingPattern countingPattern1 = new CountingPattern(1, 2);
        countingPattern1.addParticle(new eu.fox7.schematoolkit.common.ElementRef(schema.getElementSymbolTable().updateOrCreateReference("{}element1", new eu.fox7.schematoolkit.xsd.om.Element("{}element1"))));
        ComplexContentType complexContentType = new ComplexContentType();
        complexContentType.setParticle(countingPattern1);

        ComplexType complexType = new ComplexType("{}type", complexContentType);
        element.setType(schema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType));

        Element dtdElement = instance.convertElement(element);

        assertEquals("xsd:name", dtdElement.getName());
        assertFalse(dtdElement.getMixed());
        assertFalse(dtdElement.getMixedStar());
        assertEquals(0, dtdElement.getAttributes().size());
        assertFalse(dtdElement.isEmpty());
        assertFalse(dtdElement.hasAnyType());
        assertTrue(dtdElement.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) dtdElement.getParticle();

        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().get(0) instanceof ElementRef);
        Element element1 = ((ElementRef) sequencePattern.getParticles().get(0)).getElement();
        assertEquals("element1", element1.getName());

        assertTrue(sequencePattern.getParticles().get(1) instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().get(1);
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(1, countingPattern.getMax().intValue());

        Element element2 = ((ElementRef) countingPattern.getParticles().get(0)).getElement();
        assertEquals("element1", element2.getName());
    }
}
