package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

/**
 * XSD to RELAX NG testsuite
 * @author Lars Schmidt
 */

public class XSD2RELAXNGTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AttributeParticleConverterTest.class);
        suite.addTestSuite(ComplexTypeConverterTest.class);
        suite.addTestSuite(ConverterBaseTest.class);
        suite.addTestSuite(ElementConverterTest.class);
        suite.addTestSuite(InheritanceInformationCollectorTest.class);
        suite.addTestSuite(SimpleTypeConverterTest.class);
        suite.addTestSuite(StartElementConverterTest.class);
        suite.addTestSuite(SubstitutionGroupInformationCollectorTest.class);
        
        return suite;
    }
}