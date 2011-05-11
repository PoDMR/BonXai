package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.relaxng.parser.RNGParser;
import de.tudortmund.cs.bonxai.relaxng.tools.XMLAttributeReplenisher;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentFixableRestrictionProperty;
import de.tudortmund.cs.bonxai.xsd.SimpleContentList;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import de.tudortmund.cs.bonxai.xsd.SimpleContentUnion;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.writer.XSDWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class PatternSimpleTypeConverter
 * @author Lars Schmidt
 */
public class PatternSimpleTypeConverterTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    RelaxNGSchema rng;
    private HashSet<String> usedLocalNames;
    private HashMap<Pattern, HashSet<String>> patternInformation;

    /**
     * Before every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        schema = new XSDSchema();
        schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE));
        rng = new RelaxNGSchema();
    }

    private void parseAndPrepareRNG(String filePath) throws Exception {
        rng = new RelaxNGSchema();
        RNGParser instance = new RNGParser(filePath, false);
        rng = instance.getRNGSchema();

        XMLAttributeReplenisher xmlAttributeReplenisher = new XMLAttributeReplenisher(this.rng);
        xmlAttributeReplenisher.startReplenishment();
        this.usedLocalNames = xmlAttributeReplenisher.getUsedNames();

        PatternInformationCollector relaxNGPatternDataCollector = new PatternInformationCollector(this.rng);
        relaxNGPatternDataCollector.collectData();
        this.patternInformation = relaxNGPatternDataCollector.getPatternIntel();

    }

    private void writeToSystemOut(SimpleType simpleType) {
        de.tudortmund.cs.bonxai.xsd.Element dummyElement = new de.tudortmund.cs.bonxai.xsd.Element("{}dummy", this.schema.getTypeSymbolTable().updateOrCreateReference(simpleType.getName(), simpleType));
        this.schema.addElement(this.schema.getElementSymbolTable().updateOrCreateReference(dummyElement.getName(), dummyElement));
        XSDWriter xsd_Writer = new XSDWriter(schema);
        System.out.println(xsd_Writer.getXSDString());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_01.rng
     * @throws Exception 
     */
    @Test
    public void testGenerateSimpleTypeForPattern_01() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_01.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", result.getName());
        assertTrue(result.getInheritance() == null);
        assertTrue(result.getFinalModifiers() == null);
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_02.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_02() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_02.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) result.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);
        assertTrue(simpleContentRestriction.getLength() != null);
        SimpleContentFixableRestrictionProperty<Integer> scfrp = simpleContentRestriction.getLength();
        assertEquals(2, scfrp.getValue().intValue());
        SimpleType anonymousSimpleType = (SimpleType) simpleContentRestriction.getBase();

        assertTrue(anonymousSimpleType.getInheritance() != null);
        assertTrue(anonymousSimpleType.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType.getInheritance() instanceof SimpleContentList);

        SimpleContentList simpleContentList = (SimpleContentList) anonymousSimpleType.getInheritance();

        assertTrue(simpleContentList.getBaseSimpleType() != null);
        assertTrue(simpleContentList.getBaseSimpleType() instanceof SimpleType);

        SimpleType anonymousSimpleType2 = simpleContentList.getBaseSimpleType();

        assertTrue(anonymousSimpleType2.getInheritance() != null);
        assertTrue(anonymousSimpleType2.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType2.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) anonymousSimpleType2.getInheritance();

        assertTrue(simpleContentUnion.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());

        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", simpleContentUnion.getAllMemberTypes().getFirst().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleContentUnion.getAllMemberTypes().getLast().getReference().getName());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_03.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_03() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_03.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) result.getInheritance();

        assertTrue(simpleContentUnion.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());

        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", simpleContentUnion.getAllMemberTypes().getFirst().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleContentUnion.getAllMemberTypes().getLast().getReference().getName());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_04.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_04() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_04.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) result.getInheritance();

        assertTrue(simpleContentUnion.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());

        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", simpleContentUnion.getMemberTypes().getFirst().getReference().getName());

        assertTrue(simpleContentUnion.getAllMemberTypes().getLast().getReference() != null);

        SimpleType anonymousSimpleType2 = (SimpleType) simpleContentUnion.getAnonymousSimpleTypes().getFirst().getReference();

        assertTrue(anonymousSimpleType2.getInheritance() != null);
        assertTrue(anonymousSimpleType2.getFinalModifiers() == null);


        assertTrue(anonymousSimpleType2.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) anonymousSimpleType2.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType.getName());


        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(2, simpleContentRestriction.getEnumeration().size());

        assertEquals("3", simpleContentRestriction.getEnumeration().getFirst());
        assertEquals("4", simpleContentRestriction.getEnumeration().getLast());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_05.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_05() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_05.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) result.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);
        assertTrue(simpleContentRestriction.getLength() != null);
        SimpleContentFixableRestrictionProperty<Integer> scfrp = simpleContentRestriction.getLength();
        assertEquals(2, scfrp.getValue().intValue());
        SimpleType anonymousSimpleType = (SimpleType) simpleContentRestriction.getBase();

        assertTrue(anonymousSimpleType.getInheritance() != null);
        assertTrue(anonymousSimpleType.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType.getInheritance() instanceof SimpleContentList);

        SimpleContentList simpleContentList = (SimpleContentList) anonymousSimpleType.getInheritance();

        assertTrue(simpleContentList.getBaseSimpleType() != null);
        assertTrue(simpleContentList.getBaseSimpleType() instanceof SimpleType);

        SimpleType anonymousSimpleType2 = simpleContentList.getBaseSimpleType();

        assertTrue(anonymousSimpleType2.getInheritance() != null);
        assertTrue(anonymousSimpleType2.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType2.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction2 = (SimpleContentRestriction) anonymousSimpleType2.getInheritance();

        assertTrue(simpleContentRestriction2.getBase() instanceof SimpleType);

        SimpleType baseType = (SimpleType) simpleContentRestriction2.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType.getName());

        assertTrue(simpleContentRestriction2.getEnumeration() != null);
        assertEquals(2, simpleContentRestriction2.getEnumeration().size());

        assertEquals("3", simpleContentRestriction2.getEnumeration().getFirst());
        assertEquals("4", simpleContentRestriction2.getEnumeration().getLast());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_06.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_06() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_06.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) result.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType.getName());

        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(1, simpleContentRestriction.getEnumeration().size());

        assertEquals("3", simpleContentRestriction.getEnumeration().getFirst());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_07.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_07() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_07.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) result.getInheritance();

        assertTrue(simpleContentUnion.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());

        SimpleType anonymousSimpleType1 = (SimpleType) simpleContentUnion.getAnonymousSimpleTypes().getFirst().getReference();

        assertTrue(anonymousSimpleType1.getInheritance() != null);
        assertTrue(anonymousSimpleType1.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType1.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) anonymousSimpleType1.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType.getName());

        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(1, simpleContentRestriction.getEnumeration().size());

        assertEquals("3", simpleContentRestriction.getEnumeration().getFirst());

        SimpleType anonymousSimpleType2 = (SimpleType) simpleContentUnion.getAnonymousSimpleTypes().getLast().getReference();

        assertTrue(anonymousSimpleType2.getInheritance() != null);
        assertTrue(anonymousSimpleType2.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType2.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction2 = (SimpleContentRestriction) anonymousSimpleType2.getInheritance();

        assertTrue(simpleContentRestriction2.getBase() instanceof SimpleType);

        SimpleType baseType2 = (SimpleType) simpleContentRestriction2.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType2.getName());

        assertTrue(simpleContentRestriction2.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction2.getEnumeration().size());

        SimpleContentFixableRestrictionProperty<Integer> scfrp = simpleContentRestriction2.getLength();
        assertEquals(0, scfrp.getValue().intValue());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_08.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_08() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_08.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) result.getInheritance();

        assertTrue(simpleContentUnion.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());

        SimpleType anonymousSimpleType1 = (SimpleType) simpleContentUnion.getAnonymousSimpleTypes().getFirst().getReference();

        assertTrue(anonymousSimpleType1.getInheritance() != null);
        assertTrue(anonymousSimpleType1.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType1.getInheritance() instanceof SimpleContentUnion);
        SimpleContentUnion simpleContentUnion2 = (SimpleContentUnion) anonymousSimpleType1.getInheritance();

        assertTrue(simpleContentUnion2.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion2.getAllMemberTypes().size());

        assertEquals("{http://www.w3.org/2001/XMLSchema}short", simpleContentUnion2.getAllMemberTypes().getFirst().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}NCName", simpleContentUnion2.getAllMemberTypes().getLast().getReference().getName());

        SimpleType anonymousSimpleType2 = (SimpleType) simpleContentUnion.getAnonymousSimpleTypes().getLast().getReference();

        assertTrue(anonymousSimpleType2.getInheritance() != null);
        assertTrue(anonymousSimpleType2.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType2.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction2 = (SimpleContentRestriction) anonymousSimpleType2.getInheritance();

        assertTrue(simpleContentRestriction2.getBase() instanceof SimpleType);

        SimpleType baseType2 = (SimpleType) simpleContentRestriction2.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType2.getName());

        assertTrue(simpleContentRestriction2.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction2.getEnumeration().size());

        SimpleContentFixableRestrictionProperty<Integer> scfrp = simpleContentRestriction2.getLength();
        assertEquals(0, scfrp.getValue().intValue());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_09.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_09() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_09.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion2 = (SimpleContentUnion) result.getInheritance();

        assertTrue(simpleContentUnion2.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion2.getAllMemberTypes().size());

        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleContentUnion2.getAllMemberTypes().getFirst().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}short", simpleContentUnion2.getAllMemberTypes().getLast().getReference().getName());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_10.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_10() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_10.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", result.getName());
        assertTrue(result.getInheritance() == null);
        assertTrue(result.getFinalModifiers() == null);
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_11.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_11() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_11.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) result.getInheritance();

        assertTrue(simpleContentUnion.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());

        assertEquals("{http://www.w3.org/2001/XMLSchema}byte", simpleContentUnion.getMemberTypes().getFirst().getReference().getName());

        SimpleType anonymousSimpleType1 = (SimpleType) simpleContentUnion.getAnonymousSimpleTypes().getFirst().getReference();

        assertTrue(anonymousSimpleType1.getInheritance() != null);
        assertTrue(anonymousSimpleType1.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType1.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) anonymousSimpleType1.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType2 = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType2.getName());

        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(1, simpleContentRestriction.getEnumeration().size());

        assertEquals("5", simpleContentRestriction.getEnumeration().getFirst());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_12.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_12() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_12.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) result.getInheritance();

        assertTrue(simpleContentUnion.getAllMemberTypes() != null);
        assertEquals(2, simpleContentUnion.getAllMemberTypes().size());

        assertEquals("{http://www.w3.org/2001/XMLSchema}byte", simpleContentUnion.getMemberTypes().getFirst().getReference().getName());

        SimpleType anonymousSimpleType1 = (SimpleType) simpleContentUnion.getAnonymousSimpleTypes().getFirst().getReference();

        assertTrue(anonymousSimpleType1.getInheritance() != null);
        assertTrue(anonymousSimpleType1.getFinalModifiers() == null);

        assertTrue(anonymousSimpleType1.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) anonymousSimpleType1.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType2 = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}token", baseType2.getName());

        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(1, simpleContentRestriction.getEnumeration().size());

        assertEquals("5", simpleContentRestriction.getEnumeration().getFirst());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_13.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_13() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_13.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) result.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType2 = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}decimal", baseType2.getName());

        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction.getEnumeration().size());

        assertEquals("127", simpleContentRestriction.getMaxExclusive().getValue());
    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_14.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_14() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_14.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType_2", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) result.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType2 = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{}simpleType", baseType2.getName());

        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction.getEnumeration().size());

        assertEquals("pattern2", simpleContentRestriction.getPattern().getValue());

        assertTrue(baseType2.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction2 = (SimpleContentRestriction) baseType2.getInheritance();

        assertTrue(simpleContentRestriction2.getBase() instanceof SimpleType);

        SimpleType baseType = (SimpleType) simpleContentRestriction2.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}decimal", baseType.getName());

        assertTrue(simpleContentRestriction2.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction2.getEnumeration().size());

        assertEquals("pattern1", simpleContentRestriction2.getPattern().getValue());

    }

    /**
     * Test of generateSimpleTypeForPattern method, of class PatternSimpleTypeConverter.
     * Test: datatypes_15.rng
     * @throws Exception
     */
    @Test
    public void testGenerateSimpleTypeForPattern_15() throws Exception {
        parseAndPrepareRNG("tests/de/tudortmund/cs/bonxai/converter/relaxng2xsd/PatternSimpleTypeConverterTest/simpleType_15.rng");
        PatternSimpleTypeConverter instance = new PatternSimpleTypeConverter(schema, rng, patternInformation, usedLocalNames, usedLocalNames);

        Element element = (Element) this.rng.getRootPattern();

        SimpleType result = instance.generateSimpleTypeForPattern(element.getPatterns().getFirst(), new LinkedList<Pattern>());

//        writeToSystemOut(result);

        assertEquals("{}simpleType_3", result.getName());
        assertTrue(result.getInheritance() != null);
        assertTrue(result.getFinalModifiers() == null);

        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) result.getInheritance();

        assertTrue(simpleContentRestriction.getBase() instanceof SimpleType);

        SimpleType baseType2 = (SimpleType) simpleContentRestriction.getBase();

        assertEquals("{}simpleType_2", baseType2.getName());

        assertTrue(simpleContentRestriction.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction.getEnumeration().size());

        assertEquals("[5-6]{1}", simpleContentRestriction.getPattern().getValue());

        assertTrue(baseType2.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction2 = (SimpleContentRestriction) baseType2.getInheritance();

        assertTrue(simpleContentRestriction2.getBase() instanceof SimpleType);

        SimpleType baseType = (SimpleType) simpleContentRestriction2.getBase();

        assertEquals("{}simpleType", baseType.getName());

        assertTrue(simpleContentRestriction2.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction2.getEnumeration().size());

        assertEquals("[0-9]{1}", simpleContentRestriction2.getPattern().getValue());

        assertTrue(baseType.getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction3 = (SimpleContentRestriction) baseType.getInheritance();

        assertTrue(simpleContentRestriction3.getBase() instanceof SimpleType);

        SimpleType baseType3 = (SimpleType) simpleContentRestriction3.getBase();

        assertEquals("{http://www.w3.org/2001/XMLSchema}decimal", baseType3.getName());

        assertTrue(simpleContentRestriction3.getEnumeration() != null);
        assertEquals(0, simpleContentRestriction3.getEnumeration().size());

        assertEquals("[0-5]{1}", simpleContentRestriction3.getPattern().getValue());
        assertEquals("5", simpleContentRestriction3.getMinInclusive().getValue());
        assertEquals("5", simpleContentRestriction3.getMaxInclusive().getValue());
        assertEquals(1, simpleContentRestriction3.getTotalDigits().getValue().intValue());
    }
}
