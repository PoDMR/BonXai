package eu.fox7.bonxai.converter.relaxng2xsd;

import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.converter.relaxng2xsd.NameClassAnalyzer;
import eu.fox7.bonxai.converter.relaxng2xsd.RelaxNG2XSDConverter;
import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.relaxng.parser.RNGParser;
import eu.fox7.bonxai.relaxng.tools.XMLAttributeReplenisher;
import eu.fox7.bonxai.xsd.XSDSchema;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class NameClassAnalyzer
 * @author Lars Schmidt
 */
public class NameClassAnalyzerTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    RelaxNGSchema rng;

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
        filePath = this.getClass().getResource("/"+filePath).getFile();

        rng = new RelaxNGSchema();
        RNGParser instance = new RNGParser(filePath, false);
        rng = instance.getRNGSchema();

        XMLAttributeReplenisher xmlAttributeReplenisher = new XMLAttributeReplenisher(this.rng);
        xmlAttributeReplenisher.startReplenishment();
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_01.rng
     * @throws Exception 
     */
    @Test
    public void testAnalyze_01() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_01.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        instance.analyze();
        LinkedHashMap<String, Object> resultMap = instance.getNameInfos();

        String currentKey = resultMap.keySet().iterator().next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(1, resultMap.size());
        assertEquals("{}outer", currentKey);
        assertTrue(currentValue == null);
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_02.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_02() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_02.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        String currentKey = resultMap.keySet().iterator().next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(1, resultMap.size());
        assertEquals("{}test", currentKey);
        assertTrue(currentValue == null);
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_03.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_03() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_03.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        Iterator<String> keyIterator = resultMap.keySet().iterator();
        String currentKey = keyIterator.next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(2, resultMap.size());
        assertEquals("any", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern = (AnyPattern) currentValue;
        assertEquals("##other", anyPattern.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals(2, resultMap.size());
        assertEquals("-any_except_1", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern2 = (AnyPattern) currentValue;
        assertEquals("http://relaxng.org/ns", anyPattern2.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern2.getProcessContentsInstruction());
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_04.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_04() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_04.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        Iterator<String> keyIterator = resultMap.keySet().iterator();
        String currentKey = keyIterator.next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(1, resultMap.size());
        assertEquals("any", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern = (AnyPattern) currentValue;
        assertEquals("##any", anyPattern.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_05.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_05() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_05.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        Iterator<String> keyIterator = resultMap.keySet().iterator();
        String currentKey = keyIterator.next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(1, resultMap.size());
        assertEquals("any", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern = (AnyPattern) currentValue;
        assertEquals("http://www.example.org", anyPattern.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_06.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_06() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_06.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        Iterator<String> keyIterator = resultMap.keySet().iterator();
        String currentKey = keyIterator.next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(1, resultMap.size());
        assertEquals("any", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern = (AnyPattern) currentValue;
        assertEquals("##local", anyPattern.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_07.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_07() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_07.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        Iterator<String> keyIterator = resultMap.keySet().iterator();
        String currentKey = keyIterator.next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(2, resultMap.size());
        assertEquals("{}test", currentKey);
        assertTrue(currentValue == null);

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals("any_1", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern = (AnyPattern) currentValue;
        assertEquals("##any", anyPattern.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_08.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_08() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_08.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        Iterator<String> keyIterator = resultMap.keySet().iterator();
        String currentKey = keyIterator.next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(1, resultMap.size());
        assertEquals("-any_except", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern2 = (AnyPattern) currentValue;
        assertEquals("http://example.org", anyPattern2.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern2.getProcessContentsInstruction());
    }

    /**
     * Test of analyze method, of class NameClassAnalyzer.
     * Test: nameClassTest_09.rng
     * @throws Exception
     */
    @Test
    public void testAnalyze_09() throws Exception {
        parseAndPrepareRNG("tests/eu/fox7/bonxai/converter/relaxng2xsd/NameClassAnalyzerTest/nameClassTest_09.rng");

        Element element = (Element) this.rng.getRootPattern();
        NameClassAnalyzer instance = new NameClassAnalyzer(element);
        LinkedHashMap<String, Object> resultMap = instance.analyze();

        Iterator<String> keyIterator = resultMap.keySet().iterator();
        String currentKey = keyIterator.next();
        Object currentValue = resultMap.get(currentKey);

        assertEquals(7, resultMap.size());

        assertEquals("{http://www.example.org/ns2}test4", currentKey);
        assertTrue(currentValue == null);

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals("{http://www.example.org/ns2}test5", currentKey);
        assertTrue(currentValue == null);

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals("{http://www.example.org/ns2}test6", currentKey);
        assertTrue(currentValue == null);

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals("{}test", currentKey);
        assertTrue(currentValue == null);

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals("{}test2", currentKey);
        assertTrue(currentValue == null);

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals("any_5", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern = (AnyPattern) currentValue;
        assertEquals("##any", anyPattern.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern.getProcessContentsInstruction());

        currentKey = keyIterator.next();
        currentValue = resultMap.get(currentKey);

        assertEquals("-any_except_6", currentKey);
        assertTrue(currentValue instanceof AnyPattern);
        AnyPattern anyPattern2 = (AnyPattern) currentValue;
        assertEquals("http://www.example.org/ns2", anyPattern2.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, anyPattern2.getProcessContentsInstruction());
    }
}
