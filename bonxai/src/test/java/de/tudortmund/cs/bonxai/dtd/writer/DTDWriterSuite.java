package de.tudortmund.cs.bonxai.dtd.writer;

/**
 * Testsuite for the DTDWriter
 * @author Lars Schmidt
 */
public class DTDWriterSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(DTDAttributeWriterTest.class);
        suite.addTestSuite(DTDElementWriterTest.class);
        suite.addTestSuite(DTDEntityWriterTest.class);
        suite.addTestSuite(DTDNotationWriterTest.class);
        suite.addTestSuite(DTDWriterTest.class);
        return suite;
    }
}
