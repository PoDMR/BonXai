package de.tudortmund.cs.bonxai.converter.xsd2dtd;

import de.tudortmund.cs.bonxai.xsd.tools.GroupReplacerTest;

/**
 * @author Lars Schmidt
 */

public class XSD2DTDTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(ConstraintHandlerTest.class);
        suite.addTestSuite(DTDNameGeneratorTest.class);
        suite.addTestSuite(ElementWrapperTest.class);
        suite.addTestSuite(PermutationToolTest.class);
        suite.addTestSuite(AttributeConverterTest.class);
        suite.addTestSuite(AttributeUnionBuilderTest.class);
        suite.addTestSuite(ElementConverterTest.class);
        suite.addTestSuite(ElementUnionBuilderTest.class);
//        suite.addTestSuite(XSD2DTDConverterTest.class);
        suite.addTestSuite(GroupReplacerTest.class);
        return suite;
    }
}