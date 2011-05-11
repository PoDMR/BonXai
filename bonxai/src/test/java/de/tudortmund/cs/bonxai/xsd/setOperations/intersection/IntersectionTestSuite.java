package de.tudortmund.cs.bonxai.xsd.setOperations.intersection;

/**
 * @author Dominik Wolff
 */

public class IntersectionTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();

        suite.addTestSuite(AttributeParticleIntersectionGeneratorTest.class);
        suite.addTestSuite(ParticleIntersectionGeneratorTest.class);
        suite.addTestSuite(SchemaIntersectionGeneratorTest.class);
        suite.addTestSuite(TypeIntersectionGeneratorTest.class);
        suite.addTestSuite(IntersectionConstructorTest.class);

        return suite;
    }
}