package de.tudortmund.cs.bonxai.relaxng;

import de.tudortmund.cs.bonxai.relaxng.parser.RNGParserTestSuite;
import de.tudortmund.cs.bonxai.relaxng.writer.RNGWriterTestSuite;

public class FullRelaxNGTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTest(RelaxNGTestSuite.suite());
        suite.addTest(RNGParserTestSuite.suite());
        suite.addTest(RNGWriterTestSuite.suite());
        return suite;
    }
}
