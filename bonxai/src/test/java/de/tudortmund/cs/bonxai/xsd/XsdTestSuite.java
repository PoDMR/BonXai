package de.tudortmund.cs.bonxai.xsd;

public class XsdTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AttributeGroupRefTest.class);
        suite.addTestSuite(AttributeGroupTest.class);
        suite.addTestSuite(AttributeRefTest.class);
        suite.addTestSuite(AttributeTest.class);
        suite.addTestSuite(ComplexContentTypeTest.class);
        suite.addTestSuite(ComplexTypeTest.class);
        suite.addTestSuite(CountingPatternTest.class);
        suite.addTestSuite(ElementTest.class);
        suite.addTestSuite(ImportedSchemaTest.class);
        suite.addTestSuite(IncludedSchemaTest.class);
        suite.addTestSuite(InheritanceTest.class);
        suite.addTestSuite(KeyRefTest.class);
        suite.addTestSuite(RedefinedSchemaTest.class);
        suite.addTestSuite(SchemaTest.class);
        suite.addTestSuite(SimpleConstraintTest.class);
        suite.addTestSuite(SimpleContentFixableRestrictionPropertyTest.class);
        suite.addTestSuite(SimpleContentRestrictionPropertyTest.class);
        suite.addTestSuite(SimpleContentRestrictionTest.class);
        suite.addTestSuite(SimpleContentTypeTest.class);
        suite.addTestSuite(SimpleContentUnionTest.class);
        suite.addTestSuite(SimpleTypeTest.class);
        return suite;
    }
}
