package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

public class Xsd2BonxaiTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(ConstraintsConverterTest.class);
        suite.addTestSuite(DeclarationsConverterTest.class);
        suite.addTestSuite(AttributeProcessorTest.class);
        suite.addTestSuite(ParticleProcessorTest.class);
        suite.addTestSuite(GroupsConverterTest.class);
        suite.addTestSuite(BonxaiFactoryTest.class);
        suite.addTestSuite(PreProcessorTest.class);
        suite.addTestSuite(InheritancePreProcessorTest.class);
        suite.addTestSuite(SimpleContentPreProcessorTest.class);
        suite.addTestSuite(TypeLibraryExtractorTest.class);
        suite.addTestSuite(IntegrationTest.class);
        return suite;
    }
}
