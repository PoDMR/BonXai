package de.tudortmund.cs.bonxai.xsd.setOperations.union;

/**
 * @author Dominik Wolff
 */

public class UnionTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();

        suite.addTestSuite(AttributeParticleUnionGeneratorTest.class);
        suite.addTestSuite(ParticleUnionGeneratorTest.class);
        suite.addTestSuite(SchemaUnionGeneratorTest.class);
        suite.addTestSuite(TypeUnionGeneratorTest.class);
        suite.addTestSuite(UnionConstructorTest.class);

        return suite;
    }
}