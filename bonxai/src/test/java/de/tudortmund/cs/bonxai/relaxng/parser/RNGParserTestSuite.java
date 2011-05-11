package de.tudortmund.cs.bonxai.relaxng.parser;

/**
 * Testsuite for the Relax NG Parser
 * @author Lars Schmidt
 */
public class RNGParserTestSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(RNGProcessorBaseTest.class);
        suite.addTestSuite(RNGRootProcessorTest.class);
        suite.addTestSuite(AnyNameProcessorTest.class);
        suite.addTestSuite(AttributeProcessorTest.class);
        suite.addTestSuite(ChoiceNameProcessorTest.class);
        suite.addTestSuite(ChoiceProcessorTest.class);
        suite.addTestSuite(DataProcessorTest.class);
        suite.addTestSuite(DefineProcessorTest.class);
        suite.addTestSuite(DivProcessorTest.class);
        suite.addTestSuite(ElementProcessorTest.class);
        suite.addTestSuite(ExceptNameProcessorTest.class);
        suite.addTestSuite(ExceptProcessorTest.class);
        suite.addTestSuite(GrammarProcessorTest.class);
        suite.addTestSuite(GroupProcessorTest.class);
        suite.addTestSuite(IncludeProcessorTest.class);
        suite.addTestSuite(InterleaveProcessorTest.class);
        suite.addTestSuite(ListProcessorTest.class);
        suite.addTestSuite(MixedProcessorTest.class);
        suite.addTestSuite(NameProcessorTest.class);
        suite.addTestSuite(NsNameProcessorTest.class);
        suite.addTestSuite(OneOrMoreProcessorTest.class);
        suite.addTestSuite(OptionalProcessorTest.class);
        suite.addTestSuite(ParamProcessorTest.class);
        suite.addTestSuite(StartProcessorTest.class);
        suite.addTestSuite(ValueProcessorTest.class);
        suite.addTestSuite(ZeroOrMoreProcessorTest.class);
        return suite;
    }
}
