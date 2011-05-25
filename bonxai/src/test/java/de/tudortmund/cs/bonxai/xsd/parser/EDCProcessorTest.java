package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class EDCProcessorTest extends junit.framework.TestCase {

    //Schema for this testcase
    XSDSchema schema;

    /**
     * Befor every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        schema = new XSDSchema();
    }

//    /**
//     * Test of isValid method with result false, of class EDCChecker.
//     * @throws Exception
//     */
//    @Test
//    public void testInvalidCaseWithInheritance() throws Exception {
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/EDCTests/invalid_case_inheritance.xsd");
//        XSDParser instance = new XSDParser(false, false);
//        EDCChecker edcProcessor = new EDCChecker(instance.parse(filePath));
//        assertFalse(edcProcessor.isValid());
//    }
//
//    /**
//     * Test of isValid method with result false, of class EDCChecker.
//     * @throws Exception
//     */
//    @Test
//    public void testInvalidCaseWithSubstitutionGroup() throws Exception {
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/EDCTests/invalid_case_substitutionGroup.xsd");
//        XSDParser instance = new XSDParser(false, false);
//        EDCChecker edcProcessor = new EDCChecker(instance.parse(filePath));
//        assertFalse(edcProcessor.isValid());
//    }
//
//    /**
//     * Test of isValid method with result false, of class EDCChecker.
//     * @throws Exception
//     */
//    @Test
//    public void testInvalidCaseWithComplexSchema() throws Exception {
//        String filePath = new String("tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/EDCTests/invalid_case_complexSchema.xsd");
//        XSDParser instance = new XSDParser(false, false);
//        EDCChecker edcProcessor = new EDCChecker(instance.parse(filePath));
//        assertFalse(edcProcessor.isValid());
//    }

    /**
     * Test of isValid method with result true, of class EDCChecker.
     * @throws Exception
     */
    @Test
    public void testValidCase() throws Exception {
        String filePath = this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/parser/xsds/EDCTests/valid_case.xsd").getFile();
        XSDParser instance = new XSDParser(false, false);
        EDCChecker edcProcessor = new EDCChecker(instance.parse(filePath));
        assertTrue(edcProcessor.isValid());
    }
}
