package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

/**
 * RELAX NG to XSD testsuite
 * @author Lars Schmidt
 */

public class RELAXNG2XSDTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(ConverterBaseTest.class);
        suite.addTestSuite(PatternSimpleTypeConverterTest.class);
        suite.addTestSuite(PatternAttributeConverterTest.class);
        suite.addTestSuite(PatternElementConverterTest.class);
        suite.addTestSuite(NameClassAnalyzerTest.class);
        suite.addTestSuite(PatternInformationCollectorTest.class);
        return suite;
    }
}