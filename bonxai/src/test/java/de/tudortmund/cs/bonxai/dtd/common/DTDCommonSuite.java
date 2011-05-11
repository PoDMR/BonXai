package de.tudortmund.cs.bonxai.dtd.common;

import de.tudortmund.cs.bonxai.dtd.parser.*;

/**
 * @author Lars Schmidt
 */

public class DTDCommonSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(ElementContentModelProcessorTest.class);
        suite.addTestSuite(AttributeTypeProcessorTest.class);
        suite.addTestSuite(DTDNameCheckerTest.class);

        return suite;
    }
}