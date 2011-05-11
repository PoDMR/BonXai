package de.tudortmund.cs.bonxai.relaxng.tools;

/**
 * Testsuite for all RELAX NG tools
 * @author Lars Schmidt
 */
public class RelaxNGToolsTestSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(ExternalSchemaLoaderTest.class);
        suite.addTestSuite(XMLAttributeReplenisherTest.class);
        return suite;
    }
}
