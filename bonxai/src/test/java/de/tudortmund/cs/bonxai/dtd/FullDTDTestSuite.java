package de.tudortmund.cs.bonxai.dtd;

import de.tudortmund.cs.bonxai.dtd.common.DTDCommonSuite;
import de.tudortmund.cs.bonxai.dtd.parser.DTDParserSuite;
import de.tudortmund.cs.bonxai.dtd.writer.DTDWriterSuite;

public class FullDTDTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTest(DTDTestSuite.suite());
        suite.addTest(DTDCommonSuite.suite());
        suite.addTest(DTDParserSuite.suite());
        suite.addTest(DTDWriterSuite.suite());
        return suite;
    }
}
