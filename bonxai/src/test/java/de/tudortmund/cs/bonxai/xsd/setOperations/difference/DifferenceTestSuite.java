package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

/**
 * @author Dominik Wolff
 */

public class DifferenceTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();

        suite.addTestSuite(AttributeParticleDifferenceGeneratorTest.class);
        suite.addTestSuite(ParticleDifferenceGeneratorTest.class);
        suite.addTestSuite(SchemaDifferenceGeneratorTest.class);
        suite.addTestSuite(TypeDifferenceGeneratorTest.class);
        suite.addTestSuite(DifferenceConstructorTest.class);

        return suite;
    }
}