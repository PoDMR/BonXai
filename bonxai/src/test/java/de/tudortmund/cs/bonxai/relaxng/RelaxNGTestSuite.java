package de.tudortmund.cs.bonxai.relaxng;

/**
 * Testsuite for the Relax NG object model datastructure
 * @author Lars Schmidt
 */
public class RelaxNGTestSuite {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        suite.addTestSuite(AnyNameTest.class);
        suite.addTestSuite(AttributeTest.class);
        suite.addTestSuite(ChoiceTest.class);
        suite.addTestSuite(DataTest.class);
        suite.addTestSuite(DefineTest.class);
        suite.addTestSuite(ElementTest.class);
        suite.addTestSuite(ExternalRefTest.class);
        suite.addTestSuite(GrammarTest.class);
        suite.addTestSuite(GroupTest.class);
        suite.addTestSuite(IncludeContentTest.class);
        suite.addTestSuite(InterleaveTest.class);
        suite.addTestSuite(ListTest.class);
        suite.addTestSuite(MixedTest.class);
        suite.addTestSuite(NameClassChoiceTest.class);
        suite.addTestSuite(NameTest.class);
        suite.addTestSuite(NsNameTest.class);
        suite.addTestSuite(OneOrMoreTest.class);
        suite.addTestSuite(OptionalTest.class);
        suite.addTestSuite(ParamTest.class);
        suite.addTestSuite(ParentRefTest.class);
        suite.addTestSuite(PatternTest.class);
        suite.addTestSuite(RefTest.class);
        suite.addTestSuite(RelaxNGSchemaTest.class);
        suite.addTestSuite(ValueTest.class);
        suite.addTestSuite(ZeroOrMoreTest.class);
        return suite;
    }
}
