package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.converter.dtd2xsd.DTD2XSDConverter;
import eu.fox7.bonxai.converter.dtd2xsd.ElementConverter;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.DuplicateAttributeNameException;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.DuplicateElementNameException;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.UnsupportedDTDParticleException;
import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.parser.DTDSAXParser;
import eu.fox7.bonxai.xsd.Attribute;
import eu.fox7.bonxai.xsd.ComplexContentType;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.ElementRef;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.XSDSchema;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of class ElementConverter
 * @author Lars Schmidt
 */
public class ElementConverterTest extends junit.framework.TestCase {
    // Schema for this testcase

    XSDSchema schema;
    Element dtdElement;
    ElementConverter instance;
    DocumentTypeDefinition dtd;
    Vector<eu.fox7.bonxai.xsd.Element> elements;

    /**
     * Before every test the schema and schemaProcessor are refreshed
     * @throws Exception
     */
    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema();
        String filePath = this.getClass().getResource("/tests/eu/fox7/bonxai/converter/dtd2xsd/dtds/elementConverterTests/elements.xml").getFile();
        DTDSAXParser dtdParser = new DTDSAXParser(false);
        this.dtd = dtdParser.parseXML(filePath);
        this.dtdElement = dtd.getRootElement();
        instance = new ElementConverter(schema, null, false);

        instance.convert(this.dtd);
        // Sort the list of elements
        this.elements = new Vector<eu.fox7.bonxai.xsd.Element>(this.schema.getElements());
        Collections.sort(elements, new Comparator<eu.fox7.bonxai.xsd.Element>() {

            @Override
            public int compare(eu.fox7.bonxai.xsd.Element attribute1, eu.fox7.bonxai.xsd.Element attribute2) {
                if (attribute1.getName() == null || attribute1.getName().equals("") || attribute2.getName() == null || attribute2.getName().equals("")) {
                    return 1;
                }
                return attribute1.getName().compareTo(attribute2.getName());
            }
        });
    }

    /**
     * Test of convert method, of class ElementConverter with myElement01.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement01() throws Exception {
        assertEquals(18, this.elements.size());

        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(0);
        assertEquals("{}myElement01", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement01-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof ElementRef);

        ElementRef elementRef = (ElementRef) sequencePattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement02.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement02() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(1);
        assertEquals("{}myElement02", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement02-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        ElementRef elementRef = (ElementRef) countingPattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement03.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement03() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(2);
        assertEquals("{}myElement03", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement03-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();
        assertEquals(1, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        ElementRef elementRef = (ElementRef) countingPattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement04.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement04() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(3);
        assertEquals("{}myElement04", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement04-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(1, countingPattern.getMax().intValue());

        ElementRef elementRef = (ElementRef) countingPattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement05.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement05() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(4);
        assertEquals("{}myElement05", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement05-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof ChoicePattern);

        ChoicePattern choicePattern = (ChoicePattern) complexContentType.getParticle();
        assertEquals(2, choicePattern.getParticles().size());
        assertTrue(choicePattern.getParticles().getFirst() instanceof ElementRef);

        ElementRef elementRef = (ElementRef) choicePattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());

        assertTrue(choicePattern.getParticles().getLast() instanceof ElementRef);

        ElementRef elementRef2 = (ElementRef) choicePattern.getParticles().getLast();
        assertEquals("{}myElement2", elementRef2.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement06.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement06() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(5);
        assertEquals("{}myElement06", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement06-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof ElementRef);

        ElementRef elementRef = (ElementRef) sequencePattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());

        assertTrue(sequencePattern.getParticles().getLast() instanceof ElementRef);

        ElementRef elementRef2 = (ElementRef) sequencePattern.getParticles().getLast();
        assertEquals("{}myElement2", elementRef2.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement07.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement07() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(6);
        assertEquals("{}myElement07", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement07-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) complexContentType.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        SequencePattern sequencePattern = (SequencePattern) countingPattern.getParticles().getFirst();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof ElementRef);

        ElementRef elementRef = (ElementRef) sequencePattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());

        assertTrue(sequencePattern.getParticles().getLast() instanceof ElementRef);

        ElementRef elementRef2 = (ElementRef) sequencePattern.getParticles().getLast();
        assertEquals("{}myElement2", elementRef2.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement08.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement08() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(7);
        assertEquals("{}myElement08", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement08-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) complexContentType.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(1, countingPattern.getMax().intValue());

        ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
        assertEquals(2, choicePattern.getParticles().size());
        assertTrue(choicePattern.getParticles().getFirst() instanceof ElementRef);
        assertTrue(choicePattern.getParticles().getLast() instanceof SequencePattern);

        ElementRef elementRef = (ElementRef) choicePattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());

        SequencePattern sequencePattern = (SequencePattern) choicePattern.getParticles().getLast();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof ElementRef);
        assertTrue(sequencePattern.getParticles().getLast() instanceof ElementRef);

        ElementRef elementRef2 = (ElementRef) sequencePattern.getParticles().getFirst();
        assertEquals("{}myElement2", elementRef2.getElement().getName());

        assertTrue(sequencePattern.getParticles().getLast() instanceof ElementRef);

        ElementRef elementRef3 = (ElementRef) sequencePattern.getParticles().getLast();
        assertEquals("{}myElement3", elementRef3.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement09.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement09() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(8);
        assertEquals("{}myElement09", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertTrue(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement09-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() == null);
    }

    /**
     * Test of convert method, of class ElementConverter with myElement1.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement1() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(9);
        assertEquals("{}myElement1", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertTrue(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof SimpleType);
        SimpleType simpleType = (SimpleType) xsdElement.getType();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement10.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement10() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(10);
        assertEquals("{}myElement10", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertTrue(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement10-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() == null);
    }

    /**
     * Test of convert method, of class ElementConverter with myElement11.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement11() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(11);
        assertEquals("{}myElement11", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertTrue(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement11-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) complexContentType.getParticle();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
        assertEquals(2, choicePattern.getParticles().size());
        assertTrue(choicePattern.getParticles().getFirst() instanceof ElementRef);
        assertTrue(choicePattern.getParticles().getLast() instanceof ElementRef);

        ElementRef elementRef = (ElementRef) choicePattern.getParticles().getFirst();
        assertEquals("{}myElement1", elementRef.getElement().getName());

        ElementRef elementRef2 = (ElementRef) choicePattern.getParticles().getLast();
        assertEquals("{}myElement2", elementRef2.getElement().getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement12.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement12() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(12);
        assertEquals("{}myElement12", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertTrue(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement12-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        AnyPattern anyPattern = (AnyPattern) countingPattern.getParticles().getFirst();
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement13.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement13() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(13);
        assertEquals("{}myElement13", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertTrue(complexType.getAttributes().getFirst() instanceof Attribute);

        Attribute attribute = (Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}name", attribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", attribute.getSimpleType().getName());
        assertEquals("{}myElement13-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() == null);
    }

    /**
     * Test of convert method, of class ElementConverter with myElement14.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement14() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(14);
        assertEquals("{}myElement14", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getContent());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement15.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement15() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(15);
        assertEquals("{}myElement15", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertFalse(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) xsdElement.getType();
        assertTrue(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());

        assertEquals("{}myElement15-Type", complexType.getName());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertEquals(null, complexContentType.getInheritance());
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof CountingPattern);

        CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();
        assertEquals(0, countingPattern.getMin().intValue());
        assertEquals(null, countingPattern.getMax());

        AnyPattern anyPattern = (AnyPattern) countingPattern.getParticles().getFirst();
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement2.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement2() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(16);
        assertEquals("{}myElement2", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertTrue(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof SimpleType);
        SimpleType simpleType = (SimpleType) xsdElement.getType();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convert method, of class ElementConverter with myElement3.
     * @throws Exception
     */
    @Test
    public void testConvert_myElement3() throws Exception {
        eu.fox7.bonxai.xsd.Element xsdElement = this.elements.get(17);
        assertEquals("{}myElement3", xsdElement.getName());
        assertFalse(xsdElement.getAbstract());
        assertEquals(null, xsdElement.getDefault());
        assertEquals(null, xsdElement.getFixed());
        assertTrue(xsdElement.getTypeAttr());
        assertFalse(xsdElement.isDummy());
        assertTrue(xsdElement.getType() instanceof SimpleType);
        SimpleType simpleType = (SimpleType) xsdElement.getType();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convert method, of class ElementConverter with DuplicateAttributeNameException.
     * @throws Exception
     */
    @Test
    public void testConvert_WithDuplicateAttributeNameException() throws Exception {
        try {
            schema = new XSDSchema();
            schema.setTargetNamespace(DTD2XSDConverter.XMLSCHEMA_NAMESPACE);
            schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE));
            String filePath = new String("tests/eu/fox7/bonxai/converter/dtd2xsd/dtds/elementConverterTests/elements_duplicate_attributes.xml");
            filePath = this.getClass().getResource("/"+filePath).getFile();
            DTDSAXParser dtdParser = new DTDSAXParser(false);
            this.dtd = dtdParser.parseXML(filePath);
            this.dtdElement = dtd.getRootElement();
            instance = new ElementConverter(schema, new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE), true);

            instance.convert(this.dtd);
            instance.getClass();
        } catch (DuplicateAttributeNameException e) {
            return;
        }
        fail("There are duplicate attribute names after setting the targetNamespace, but this was not detected");
    }

    /**
     * Test of convert method, of class ElementConverter with DuplicateElementNameException.
     * @throws Exception
     */
    @Test
    public void testConvert_WithDuplicateElementNameException() throws Exception {
        try {
            schema = new XSDSchema();
            schema.setTargetNamespace(DTD2XSDConverter.XMLSCHEMA_NAMESPACE);
            schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE));
            String filePath = new String("tests/eu/fox7/bonxai/converter/dtd2xsd/dtds/elementConverterTests/elements_duplicate_elements.xml");
            filePath = this.getClass().getResource("/"+filePath).getFile();
            DTDSAXParser dtdParser = new DTDSAXParser(false);
            this.dtd = dtdParser.parseXML(filePath);
            this.dtdElement = dtd.getRootElement();
            instance = new ElementConverter(schema, new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE), true);

            instance.convert(this.dtd);
            instance.getClass();
        } catch (DuplicateElementNameException e) {
            return;
        }
        fail("There are duplicate element names after setting the targetNamespace, but this was not detected");
    }

    /**
     * Test of convert method, of class ElementConverter with UnsupportedDTDParticleException.
     * @throws Exception
     */
    @Test
    public void testConvert_WithUnsupportedDTDParticleException() throws Exception {
        try {
            schema = new XSDSchema();
            schema.setTargetNamespace(DTD2XSDConverter.XMLSCHEMA_NAMESPACE);
            schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE));
            String filePath = new String("tests/eu/fox7/bonxai/converter/dtd2xsd/dtds/elementConverterTests/elements_unsupported_particle.xml");
            filePath = this.getClass().getResource("/"+filePath).getFile();
            DTDSAXParser dtdParser = new DTDSAXParser(false);
            this.dtd = dtdParser.parseXML(filePath);
            this.dtdElement = dtd.getRootElement();

            instance = new ElementConverter(schema, new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE), true);

            this.dtd.getRootElement().setParticle(new AllPattern());
            instance.convert(this.dtd);
            instance.getClass();
        } catch (UnsupportedDTDParticleException e) {
            return;
        }
        fail("There is an unsupported particle within an element content model, but this was not detected");
    }
}
