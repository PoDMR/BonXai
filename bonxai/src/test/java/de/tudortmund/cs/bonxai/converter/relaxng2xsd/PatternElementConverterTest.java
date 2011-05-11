package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.NoCombineMethodException;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.TooManyDataOrValuePatternsUnderAnElementException;
import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
import de.tudortmund.cs.bonxai.relaxng.tools.XMLAttributeReplenisher;
import de.tudortmund.cs.bonxai.xsd.ComplexContentExtension;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.ComplexTypeInheritanceModifier;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentExtension;
import de.tudortmund.cs.bonxai.xsd.SimpleContentType;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class PatternElementConverter
 * @author Lars Schmidt
 */
public class PatternElementConverterTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    RelaxNGSchema rng;
    PatternInformationCollector patternInformationCollector;

    /**
     * Before every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        this.schema = new XSDSchema();
        this.schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE));
        this.rng = new RelaxNGSchema();
    }

    @Override
    public void tearDown() {
        XSDWriter xsd_Writer = new XSDWriter(schema);
//        System.out.println(xsd_Writer.getXSDString());
    }

    private void parseAndPrepareRNG(String filePath) throws Exception {
        this.rng = new RelaxNGSchema();
        RNGParser instance = new RNGParser(filePath, false);
        this.rng = instance.getRNGSchema();

        XMLAttributeReplenisher xmlAttributeReplenisher = new XMLAttributeReplenisher(this.rng);
        xmlAttributeReplenisher.startReplenishment();

        this.patternInformationCollector = new PatternInformationCollector(this.rng);
        this.patternInformationCollector.collectData();
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception 
     */
    @Test
    public void testConvert_01_findRootElements_with_element() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_01.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}a", element.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception
     */
    @Test
    public void testConvert_02_findRootElements_with_choice() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_02.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(2, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}a", element.getName());
        de.tudortmund.cs.bonxai.xsd.Element element2 = this.schema.getElements().getLast();
        assertEquals("{}b", element2.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception
     */
    @Test
    public void testConvert_03_findRootElements_with_grammar_ref_parentRef() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_03.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}a", element.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception
     */
    @Test
    public void testConvert_04_convertElement_nameAttribute() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_04.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}a", element.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception
     */
    @Test
    public void testConvert_05_convertElement_nameClass() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_05.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}a", element.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception
     */
    @Test
    public void testConvert_06_convertElement_nameClassAny() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_06.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(0, this.schema.getElements().size());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception
     */
    @Test
    public void testConvert_07_convertElement_inner_nameClassAny() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_07.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());

        assertFalse(element.getAbstract());
        assertFalse(element.getNillable());
        assertFalse(element.getTypeAttr());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.getAttributes().isEmpty());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.common.AnyPattern);

        de.tudortmund.cs.bonxai.common.AnyPattern anyPattern = (de.tudortmund.cs.bonxai.common.AnyPattern) sequencePattern.getParticles().getFirst();
        assertEquals("##any", anyPattern.getNamespace());
        assertEquals(ProcessContentsInstruction.Strict, anyPattern.getProcessContentsInstruction());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     * @throws Exception
     */
    @Test
    public void testConvert_08_convertElement_externalNamespace() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_08.rng");
        this.schema.setTargetNamespace(this.rng.getRootPattern().getAttributeNamespace());
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{http://www.example.org}root", element.getName());

        assertFalse(element.getAbstract());
        assertFalse(element.getNillable());
        assertFalse(element.getTypeAttr());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.getAttributes().isEmpty());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.common.ElementRef);

        de.tudortmund.cs.bonxai.common.ElementRef elementRef = (de.tudortmund.cs.bonxai.common.ElementRef) sequencePattern.getParticles().getFirst();
        assertEquals("http://www.myNamespace.org", elementRef.getElement().getNamespace());

        assertEquals(1, this.schema.getForeignSchemas().size());
        assertEquals("http://www.myNamespace.org", this.schema.getForeignSchemas().getFirst().getSchema().getTargetNamespace());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element: not allowed
     *
     * @throws Exception
     */
    @Test
    public void testConvert_09_generateTypeForElement_notAllowed() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_09.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(0, this.schema.getElements().size());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element with only text content
     *
     * @throws Exception
     */
    @Test
    public void testConvert_10_generateTypeForElement_only_text() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_10.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());

        assertTrue(element.getType() instanceof SimpleType);

        SimpleType simpleType = (SimpleType) element.getType();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element with empty content
     *
     * @throws Exception
     */
    @Test
    public void testConvert_11_generateTypeForElement_only_empty() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_11.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.getAttributes().isEmpty());
        assertEquals(null, complexType.getContent());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element without element or attribute content, but with simpleType content
     *
     * @throws Exception
     */
    @Test
    public void testConvert_12_generateTypeForElement_only_simpleTypeContent() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_12.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof SimpleType);

        SimpleType simpleType = (SimpleType) element.getType();
        assertEquals("{http://www.w3.org/2001/XMLSchema}short", simpleType.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element with attribute content and simpleType content (ComplexType with SimpleContent, Attributes into extension, simpleType as base of extension)
     *
     * @throws Exception
     */
    @Test
    public void testConvert_13_generateTypeForElement_attributeAndSimpleTypeContent() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_13.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.getAttributes().isEmpty());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof SimpleContentType);

        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
        assertTrue(simpleContentType.getInheritance() instanceof SimpleContentExtension);

        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
        assertEquals(1, simpleContentExtension.getAttributes().size());
        assertTrue(simpleContentExtension.getBase() instanceof SimpleType);

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) simpleContentExtension.getAttributes().getFirst();
        assertEquals("{}bob", attribute.getName());

        SimpleType simpleType = (SimpleType) simpleContentExtension.getBase();
        assertEquals("{http://www.w3.org/2001/XMLSchema}short", simpleType.getName());
        assertFalse(simpleType.isAnonymous());
        assertTrue(simpleType.isDummy());
        assertEquals(null, simpleType.getInheritance());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: no elements, but attributes without simpleType content
     *
     * @throws Exception
     */
    @Test
    public void testConvert_14_generateTypeForElement_onlyAnAttribute() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_14.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertEquals(null, complexType.getContent());

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}bob", attribute.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element without attribute
     *
     * @throws Exception
     */
    @Test
    public void testConvert_15_generateTypeForElement_onlyElement() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_15.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with attribute
     *
     * @throws Exception
     */
    @Test
    public void testConvert_16_generateTypeForElement_ElementAndAttribute() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_16.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(1, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}blub", attribute.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with attribute and mixed
     *
     * @throws Exception
     */
    @Test
    public void testConvert_17_generateTypeForElement_ElementAndAttributeAndMixed() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_17.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertFalse(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.isAnonymous());
        assertEquals(1, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertTrue(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}blub", attribute.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element with element. There is a ref pattern that can be converted as a XML Schema complexType
     *
     * @throws Exception
     */
    @Test
    public void testConvert_18_generateTypeForElement_ElementInType() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_18.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertTrue(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertFalse(complexType.isAnonymous());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction));
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Element element2 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getLast();
        assertEquals("{}bab", element2.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element with element. There is a ref pattern that can be converted as a XML Schema complexType, plus attribute
     *
     * @throws Exception
     */
    @Test
    public void testConvert_19_generateTypeForElement_ElementInTypeAndAttribute() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_19.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertFalse(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.isAnonymous());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertTrue(complexContentType.getInheritance() instanceof ComplexContentExtension);
        assertEquals(null, complexContentType.getParticle());

        ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
        assertEquals(1, complexContentExtension.getAttributes().size());
        assertTrue(complexContentExtension.getBase() instanceof ComplexType);

        ComplexType complexType2 = (ComplexType) complexContentExtension.getBase();
        assertFalse(complexType2.isAnonymous());

        ComplexContentType complexContentType2 = (ComplexContentType) complexType2.getContent();
        assertFalse(complexContentType2.getMixed());
        assertEquals(null, complexContentType2.getInheritance());
        assertTrue(complexContentType2.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType2.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Element element2 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getLast();
        assertEquals("{}bab", element2.getName());

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) complexContentExtension.getAttributes().getFirst();
        assertEquals("{}blub", attribute.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element with element. There is a ref pattern that can be converted as a XML Schema complexType, plus attribute and mixed
     *
     * @throws Exception
     */
    @Test
    public void testConvert_20_generateTypeForElement_ElementInTypeAndAttribute() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_20.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertFalse(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.isAnonymous());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertTrue(complexContentType.getMixed());
        assertTrue(complexContentType.getInheritance() instanceof ComplexContentExtension);
        assertEquals(null, complexContentType.getParticle());

        ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
        assertEquals(1, complexContentExtension.getAttributes().size());
        assertTrue(complexContentExtension.getBase() instanceof ComplexType);

        ComplexType complexType2 = (ComplexType) complexContentExtension.getBase();
        assertFalse(complexType2.isAnonymous());

        ComplexContentType complexContentType2 = (ComplexContentType) complexType2.getContent();
        assertTrue(complexContentType2.getMixed());
        assertEquals(null, complexContentType2.getInheritance());
        assertTrue(complexContentType2.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType2.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Element element2 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getLast();
        assertEquals("{}bab", element2.getName());

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) complexContentExtension.getAttributes().getFirst();
        assertEquals("{}blub", attribute.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element with element. group with elements, plus attribute and mixed
     *
     * @throws Exception
     */
    @Test
    public void testConvert_21_generateTypeForElement_ElementInTypeAndAttribute() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_21.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertFalse(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertTrue(complexType.isAnonymous());
        assertEquals(1, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertTrue(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof de.tudortmund.cs.bonxai.common.GroupRef);

        de.tudortmund.cs.bonxai.common.GroupRef groupRef = (de.tudortmund.cs.bonxai.common.GroupRef) complexContentType.getParticle();
        assertTrue(groupRef.getGroup().getParticleContainer() instanceof SequencePattern);
        SequencePattern sequencePattern = (SequencePattern) groupRef.getGroup().getParticleContainer();
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Element element2 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getLast();
        assertEquals("{}bab", element2.getName());

        de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) complexType.getAttributes().getFirst();
        assertEquals("{}blub", attribute.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: no elements, no attributes and no simpleType content
     *
     * @throws Exception
     */
    @Test
    public void testConvert_22_generateTypeForElement_NoElementsNoAttributesNoSimpleType() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_22.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());

        Element elementModified = (Element) this.rng.getRootPattern();
        elementModified.setPatterns(new LinkedList<Pattern>());
        this.patternInformationCollector.getPatternIntel().put(elementModified, null);
        elementModified.getPatterns();

        instance.convert();
        assertEquals(1, this.schema.getElements().size());
        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());

        assertEquals(null, element.getType());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: element without element or attribute content, but with simpleType content
     * AND TooManyDataOrValuePatternsUnderAnElementException
     *
     * @throws Exception
     */
    @Test
    public void testConvert_23_generateTypeForElement_with_TooManyDataOrValuePatternsUnderAnElementException() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_23.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        try {
            instance.convert();
        } catch (TooManyDataOrValuePatternsUnderAnElementException error) {
            return;
        }
        fail("There is more than one simpleTypePattern (data/value) under the given element, but this was not detected.");
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with zeroOrMore
     *
     * @throws Exception
     */
    @Test
    public void testConvert_24_convertPatternToElementParticleStructure_ZeroOrMore() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_24.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.common.CountingPattern);
        de.tudortmund.cs.bonxai.common.CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();

        assertEquals("0", countingPattern.getMin().toString());
        assertEquals(null, countingPattern.getMax());

        assertTrue(countingPattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) countingPattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with Group
     *
     * @throws Exception
     */
    @Test
    public void testConvert_25_convertPatternToElementParticleStructure_Group() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_25.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();

        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with OneOrMore
     *
     * @throws Exception
     */
    @Test
    public void testConvert_26_convertPatternToElementParticleStructure_OneOrMore() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_26.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.common.CountingPattern);
        de.tudortmund.cs.bonxai.common.CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();

        assertEquals("1", countingPattern.getMin().toString());
        assertEquals(null, countingPattern.getMax());

        assertTrue(countingPattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) countingPattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with Optional
     *
     * @throws Exception
     */
    @Test
    public void testConvert_27_convertPatternToElementParticleStructure_Optional() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_27.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(1, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.common.CountingPattern);
        de.tudortmund.cs.bonxai.common.CountingPattern countingPattern = (CountingPattern) sequencePattern.getParticles().getFirst();

        assertEquals("0", countingPattern.getMin().toString());
        assertEquals("1", countingPattern.getMax().toString());

        assertTrue(countingPattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) countingPattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with Choice
     *
     * @throws Exception
     */
    @Test
    public void testConvert_28_convertPatternToElementParticleStructure_Choice() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_28.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof de.tudortmund.cs.bonxai.common.ChoicePattern);
        de.tudortmund.cs.bonxai.common.ChoicePattern choicePattern = (de.tudortmund.cs.bonxai.common.ChoicePattern) complexContentType.getParticle();

        assertTrue(choicePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) choicePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with Interleave
     *
     * @throws Exception
     */
    @Test
    public void testConvert_29_convertPatternToElementParticleStructure_Interleave() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_29.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof de.tudortmund.cs.bonxai.common.AllPattern);
        de.tudortmund.cs.bonxai.common.AllPattern allPattern = (de.tudortmund.cs.bonxai.common.AllPattern) complexContentType.getParticle();

        assertTrue(allPattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) allPattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * Case: Normal case, element with Grammar, parentRef and ref
     *
     * @throws Exception
     */
    @Test
    public void testConvert_30_convertPatternToElementParticleStructure_Grammar_parentRef_ref() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_30.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertEquals(null, complexType.getBlockModifiers());
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();

        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * convertPatternToElementParticleStructure with NoCombineMethodException
     *
     * @throws Exception
     */
    @Test
    public void testConvert_31_convertPatternToElementParticleStructure_with_NoCombineMethodException() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_31.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());

        Element element = (Element) this.rng.getRootPattern();
        Grammar grammar = (Grammar) element.getPatterns().getFirst();
        grammar.setStartCombineMethod(null);

        try {
            instance.convert();
        } catch (NoCombineMethodException error) {
            return;
        }
        fail("There is no combineMethod for startpatterns, but this was not detected.");
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with ZeroOrMore
     *
     * @throws Exception
     */
    @Test
    public void testConvert_32_findRecursiveElementsInGrammar_with_ZeroOrMore() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_32.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with Group
     *
     * @throws Exception
     */
    @Test
    public void testConvert_33_findRecursiveElementsInGrammar_with_Group() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_33.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with OneOrMore
     *
     * @throws Exception
     */
    @Test
    public void testConvert_34_findRecursiveElementsInGrammar_with_OneOrMore() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_34.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with Optional
     *
     * @throws Exception
     */
    @Test
    public void testConvert_35_findRecursiveElementsInGrammar_with_Optional() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_35.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with Choice
     *
     * @throws Exception
     */
    @Test
    public void testConvert_36_findRecursiveElementsInGrammar_with_Choice() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_36.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with Interleave
     *
     * @throws Exception
     */
    @Test
    public void testConvert_37_findRecursiveElementsInGrammar_with_Interleave() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_37.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with Mixed
     *
     * @throws Exception
     */
    @Test
    public void testConvert_38_findRecursiveElementsInGrammar_with_Mixed() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_38.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * findRecursiveElementsInGrammar with Grammar and ParentRef and Ref
     *
     * @throws Exception
     */
    @Test
    public void testConvert_39_findRecursiveElementsInGrammar_with_GrammarAndParentRefAndRef() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_39.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        HashSet<Element> recursiveElements = instance.getRecursiveElements();

        assertEquals(1, recursiveElements.size());
        assertEquals("bob", recursiveElements.iterator().next().getNameAttribute());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * checkPatternListForRefForComplexType with Choice
     *
     * @throws Exception
     */
    @Test
    public void testConvert_40_checkPatternListForRefForComplexType_Choice() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_40.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertTrue(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertFalse(complexType.isAnonymous());
        assertEquals("{}ref", complexType.getName());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction));
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Element element2 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getLast();
        assertEquals("{}bab", element2.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * checkPatternListForRefForComplexType with Group
     *
     * @throws Exception
     */
    @Test
    public void testConvert_41_checkPatternListForRefForComplexType_Group() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_41.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertTrue(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertFalse(complexType.isAnonymous());
        assertEquals("{}ref", complexType.getName());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction));
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Element element2 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getLast();
        assertEquals("{}bab", element2.getName());
    }

    /**
     * Test of convert method, of class PatternElementConverter.
     *
     * checkPatternListForRefForComplexType with Interleave
     *
     * @throws Exception
     */
    @Test
    public void testConvert_42_checkPatternListForRefForComplexType_Interleave() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternElementConverterTest/element_42.rng");
        PatternElementConverter instance = new PatternElementConverter(schema, rng, this.patternInformationCollector.getPatternIntel(), new HashSet<String>(), new HashSet<String>());
        instance.convert();
        assertEquals(1, this.schema.getElements().size());

        de.tudortmund.cs.bonxai.xsd.Element element = this.schema.getElements().getFirst();
        assertEquals("{}root", element.getName());
        assertTrue(element.getType() instanceof ComplexType);
        assertTrue(element.getTypeAttr());

        ComplexType complexType = (ComplexType) element.getType();
        assertFalse(complexType.getMixed());
        assertFalse(complexType.isAnonymous());
        assertEquals("{}ref", complexType.getName());
        assertEquals(0, complexType.getAttributes().size());
        assertEquals(null, complexType.getFinalModifiers());
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
        assertTrue(complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction));
        assertTrue(complexType.getContent() instanceof ComplexContentType);

        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
        assertFalse(complexContentType.getMixed());
        assertEquals(null, complexContentType.getInheritance());
        assertTrue(complexContentType.getParticle() instanceof SequencePattern);

        SequencePattern sequencePattern = (SequencePattern) complexContentType.getParticle();
        assertEquals(2, sequencePattern.getParticles().size());
        assertTrue(sequencePattern.getParticles().getFirst() instanceof de.tudortmund.cs.bonxai.xsd.Element);

        de.tudortmund.cs.bonxai.xsd.Element element1 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getFirst();
        assertEquals("{}bob", element1.getName());

        de.tudortmund.cs.bonxai.xsd.Element element2 = (de.tudortmund.cs.bonxai.xsd.Element) sequencePattern.getParticles().getLast();
        assertEquals("{}bab", element2.getName());
    }

}