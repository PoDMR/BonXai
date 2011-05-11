package de.tudortmund.cs.bonxai.relaxng.writer;

/**
 * Testsuite for the Relax NG Writer
 * @author Lars Schmidt
 */
public class RNGWriterTestSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(NameClassWriterTest.class);
        suite.addTestSuite(PatternWriterTest.class);
        suite.addTestSuite(RNGWriterBaseTest.class);
        
        // This is only used for writing out a parsed Relax NG Schema.
//        suite.addTestSuite(RNGWriterTest.class);
        return suite;
    }
}
