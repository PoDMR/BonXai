package de.tudortmund.cs.bonxai.xsd.parser;

/**
 * @author Lars Schmidt, Dominik Wolff
 */

public class New_parserSuite
{
    public static junit.framework.Test suite()
    {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AllProcessorTest.class);
        suite.addTestSuite(AnnotationProcessorTest.class);
        suite.addTestSuite(AnyAttributeProcessorTest.class);
        suite.addTestSuite(AttributeGroupProcessorTest.class);
        suite.addTestSuite(AttributeProcessorTest.class);
        suite.addTestSuite(ChoiceProcessorTest.class);
        suite.addTestSuite(ComplexContentProcessorTest.class);
        suite.addTestSuite(ComplexTypeProcessorTest.class);
        suite.addTestSuite(EDCProcessorTest.class);
        suite.addTestSuite(ElementProcessorTest.class);
        suite.addTestSuite(ExtensionProcessorTest.class);
        suite.addTestSuite(GroupProcessorTest.class);
        suite.addTestSuite(ImportProcessorTest.class);
        suite.addTestSuite(IncludeProcessorTest.class);
        suite.addTestSuite(KeyProcessorTest.class);
        suite.addTestSuite(KeyrefProcessorTest.class);
        suite.addTestSuite(ListProcessorTest.class);
        suite.addTestSuite(ProcessorTest.class);
        suite.addTestSuite(RedefineProcessorTest.class);
        suite.addTestSuite(RestrictionProcessorTest.class);
        suite.addTestSuite(SchemaProcessorTest.class);
        suite.addTestSuite(SequenceProcessorTest.class);
        suite.addTestSuite(SimpleContentProcessorTest.class);
        suite.addTestSuite(SimpleTypeProcessorTest.class);
        suite.addTestSuite(UnionProcessorTest.class);
        suite.addTestSuite(UniqueProcessorTest.class);
        //suite.addTestSuite(XSDParserTest.class);
        return suite;
    }
}