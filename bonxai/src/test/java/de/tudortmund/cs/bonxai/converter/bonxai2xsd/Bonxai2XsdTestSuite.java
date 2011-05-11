package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

public class Bonxai2XsdTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AncestorPatternRegexpVisitorTest.class);
        suite.addTestSuite(RegularExpressionAncestorPatternMatcherTest.class);
        suite.addTestSuite(SchemaTreeNodeTest.class);
        suite.addTestSuite(GroupTreeNodeTest.class);
        suite.addTestSuite(ElementTreeNodeTest.class);
        suite.addTestSuite(AttributePatternConversionTest.class);
        suite.addTestSuite(ParticleConversionTest.class);
        suite.addTestSuite(ParticleExtractorTest.class);
        suite.addTestSuite(FullMatchingRootElementFinderTest.class);
        suite.addTestSuite(GrammarConverterTest.class);
        suite.addTestSuite(TreeNodeTreeConverterTest.class);
        suite.addTestSuite(TreeNodeTreeAttributeConverterTest.class);
        suite.addTestSuite(TreeNodeTreeGroupConverterTest.class);
        suite.addTestSuite(TreeNodeTreeAttributeGroupConverterTest.class);
        suite.addTestSuite(ParticleGroupRefReplacerTest.class);
        suite.addTestSuite(ConverterTest.class);
        return suite;
    }
}
