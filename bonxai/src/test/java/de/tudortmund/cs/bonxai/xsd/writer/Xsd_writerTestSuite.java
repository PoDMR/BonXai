package de.tudortmund.cs.bonxai.xsd.writer;

/**
 */
public class Xsd_writerTestSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(XSD_WriterTest.class);
        suite.addTestSuite(ParticleWriterTest.class);
        suite.addTestSuite(TypeWriterTest.class);
        suite.addTestSuite(AttributeWriterTest.class);
        suite.addTestSuite(ConstraintWriterTest.class);
        suite.addTestSuite(GroupWriterTest.class);
        return suite;
    }
}
