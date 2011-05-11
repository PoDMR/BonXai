package de.tudortmund.cs.bonxai.utils;

public class UtilsTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(ConstraintVisitorTest.class);
        suite.addTestSuite(Base26ConverterTest.class);
        suite.addTestSuite(NamespaceIdentifierUnifierTest.class);
        suite.addTestSuite(ConstraintXPathNamespaceUnifierTest.class);
        return suite;
    }
}
