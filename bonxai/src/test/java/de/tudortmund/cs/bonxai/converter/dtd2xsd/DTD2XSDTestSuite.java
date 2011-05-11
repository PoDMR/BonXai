package de.tudortmund.cs.bonxai.converter.dtd2xsd;

/**
 * @author Lars Schmidt
 */

public class DTD2XSDTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AttributeConverterTest.class);
        suite.addTestSuite(ElementConverterTest.class);
        suite.addTestSuite(ConverterBaseTest.class);
        suite.addTestSuite(NameCheckerTest.class);
//        suite.addTestSuite(DTD2XSDConverterTest.class);
        return suite;
    }
}