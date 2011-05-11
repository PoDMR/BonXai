package de.tudortmund.cs.bonxai.dtd;

/**
 * Testsuite for the DTD object model datastructure
 * @author Lars Schmidt
 */
public class DTDTestSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AttributeTest.class);
        suite.addTestSuite(DocumentTypeDefinitionTest.class);
        suite.addTestSuite(ElementRefTest.class);
        suite.addTestSuite(ElementTest.class);
        suite.addTestSuite(ExternalEntityTest.class);
        suite.addTestSuite(InternalEntityTest.class);
        suite.addTestSuite(NotationTest.class);
        return suite;
    }
}
