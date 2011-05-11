package de.tudortmund.cs.bonxai.bonxai;

public class BonxaiTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AncestorPatternTest.class);
        suite.addTestSuite(AnnotationTest.class);
        suite.addTestSuite(AnyPatternTest.class);
        suite.addTestSuite(AttributeGroupElementTest.class);
        suite.addTestSuite(AttributeGroupReferenceTest.class);
        suite.addTestSuite(AttributePatternTest.class);
        suite.addTestSuite(AttributeTest.class);
        suite.addTestSuite(ChildPatternTest.class);
        suite.addTestSuite(ConstraintListTest.class);
        suite.addTestSuite(ContainerParticleTest.class);
        suite.addTestSuite(CardinalityParticleTest.class);
        suite.addTestSuite(CountingPatternTest.class);
        suite.addTestSuite(DataTypeListTest.class);
        suite.addTestSuite(DataTypeTest.class);
        suite.addTestSuite(DeclarationTest.class);
        suite.addTestSuite(ElementGroupElementTest.class);
        suite.addTestSuite(ElementPatternTest.class);
        suite.addTestSuite(ElementTest.class);
        suite.addTestSuite(ExpressionTest.class);
        suite.addTestSuite(GrammarListTest.class);
        suite.addTestSuite(GroupElementTest.class);
        suite.addTestSuite(GroupListTest.class);
        suite.addTestSuite(ImportListTest.class);
        suite.addTestSuite(ImportTest.class);
        suite.addTestSuite(KeyConstraintTest.class);
        suite.addTestSuite(KeyRefConstraintTest.class);
        suite.addTestSuite(ParticleContainerTest.class);
        suite.addTestSuite(UniqueConstraintTest.class);
        suite.addTestSuite(BonxaiTest.class);
        suite.addTestSuite(BonxaiTypeTest.class);
        return suite;
    }
}
