package de.tudortmund.cs.bonxai.dtd.parser;

/**
 * Testsuite for the DTDSAXParser
 * @author Lars Schmidt
 */

public class DTDParserSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(DTDSAXParserTest.class);
        suite.addTestSuite(DTDEventHandlerTest.class);

        return suite;
    }
}