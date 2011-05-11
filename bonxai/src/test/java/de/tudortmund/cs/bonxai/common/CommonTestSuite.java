package de.tudortmund.cs.bonxai.common;

public class CommonTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AnyPatternTest.class);
        suite.addTestSuite(AnyAttributeTest.class);
        suite.addTestSuite(DualHashtableTest.class);
        suite.addTestSuite(DualHashtableTest.class);
        suite.addTestSuite(ElementRefTest.class);
        suite.addTestSuite(GroupRefTest.class);
        suite.addTestSuite(GroupTest.class);
        suite.addTestSuite(IdentifiedNamespaceTest.class);
        suite.addTestSuite(NamespaceListTest.class);
        suite.addTestSuite(ParticleContainerTest.class);
        suite.addTestSuite(SymbolTableRefTest.class);
        suite.addTestSuite(SymbolTableTest.class);
        suite.addTestSuite(MultiReferenceSymbolTableTest.class);
        return suite;
    }
}
