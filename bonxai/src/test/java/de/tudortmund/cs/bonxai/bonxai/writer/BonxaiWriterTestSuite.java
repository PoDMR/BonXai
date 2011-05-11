package de.tudortmund.cs.bonxai.bonxai.writer;

public class BonxaiWriterTestSuite {
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(CompactSyntaxVisitorTest.class);
        return suite;
    }
}
