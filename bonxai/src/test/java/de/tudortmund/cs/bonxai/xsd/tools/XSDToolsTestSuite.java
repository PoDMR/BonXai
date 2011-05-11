package de.tudortmund.cs.bonxai.xsd.tools;

/**
 * Testsuite for all XML Schema tools
 * @author Lars Schmidt
 */
public class XSDToolsTestSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(BlockFinalSpreadingHandlerTest.class);
        suite.addTestSuite(GroupReplacerTest.class);
//        suite.addTestSuite(InterleaveHandlerTest.class);
        return suite;
    }
}
