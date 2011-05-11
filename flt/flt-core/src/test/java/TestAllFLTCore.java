import gjb.flt.automata.AnnotatedNFATest;
import gjb.flt.automata.BreadthFirstStateIteratorTest;
import gjb.flt.automata.KLAFactoryTest;
import gjb.flt.automata.MinimizerTest;
import gjb.flt.automata.PrefixStringRelationTest;
import gjb.flt.automata.PrefixTreeFactoryTest;
import gjb.flt.automata.SOAFactoryTest;
import gjb.flt.automata.SimpleReadWriteTest;
import gjb.flt.automata.SimulatorTest;
import gjb.flt.automata.StateMergerTest;
import gjb.flt.automata.SubexpressionAmbiguityTest;
import gjb.flt.automata.factories.SOABuilderTest;
import gjb.flt.automata.factories.SupportSOAFactoryTest;
import gjb.flt.automata.matchers.DeterministicMatcherTest;
import gjb.flt.automata.measures.MatrixRepresentationTest;
import gjb.flt.automata.measures.RedundancyTest;
import gjb.flt.automata.measures.SOAEditDistanceTest;
import gjb.flt.automata.measures.SOATestTest;
import gjb.flt.automata.random.RandomGlushkovNFAFactoryTest;
import gjb.flt.regex.DeriverTest;
import gjb.flt.regex.GlushkovTest;
import gjb.flt.regex.TransformationTest;
import gjb.flt.regex.converters.AlphabetNormalizerTest;
import gjb.flt.regex.converters.DTDConverterTest;
import gjb.flt.regex.converters.EpsilonEmptyEliminatorTest;
import gjb.flt.regex.converters.LexicographicNormalizerTest;
import gjb.flt.regex.factories.StateEliminationFactoryTest;
import gjb.flt.regex.measures.LanguageTestTest;
import gjb.flt.regex.measures.MeasureTest;
import gjb.flt.regex.random.InvalidSampleGeneratorTest;
import gjb.flt.regex.random.MutationTest;
import gjb.flt.regex.random.RandomSOREFactoryTest;
import gjb.flt.regex.random.SampleTest;
import gjb.flt.treegrammar.XMLDocumentTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.7 $
 * 
 * Class to bundle all tests for the FLT project
 */
public class TestAllFLTCore extends TestCase {
  
	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public TestAllFLTCore(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(NFASetupTest.suite());
		suite.addTest(NFACloneTest.suite());
		suite.addTest(NFAOperatorsTest.suite());
		// suite.addTest(CNFcflTest.suite());
		suite.addTest(CFGTest.suite());
		suite.addTest(CFGApproximationTest.suite());
		suite.addTest(DFATest.suite());
		suite.addTest(GNFATest.suite());
		suite.addTest(MinimizeTest.suite());
		suite.addTest(EmptyLanguageTest.suite());
		suite.addTest(RegexTest.suite());
		suite.addTest(GlushkovTest.suite());
		suite.addTest(StringCostTest.suite());
		suite.addTest(CostDistributionTest.suite());
		suite.addTest(GeneratingNFATest.suite());
		suite.addTest(GeneratingRegexTest.suite());
        suite.addTest(AnnotatedNFATest.suite());
        suite.addTest(SOAFactoryTest.suite());
        suite.addTest(StateMergerTest.suite());
        suite.addTest(KLAFactoryTest.suite());
        suite.addTest(PrefixStringRelationTest.suite());
        suite.addTest(ReverserTest.suite());
        suite.addTest(PrefixTreeFactoryTest.suite());
        suite.addTest(SimpleReadWriteTest.suite());
        suite.addTest(RandomGlushkovNFAFactoryTest.suite());
        suite.addTest(RandomSOREFactoryTest.suite());
        suite.addTest(TransformationTest.suite());
        suite.addTest(SimulatorTest.suite());
        suite.addTest(SampleTest.suite());
        suite.addTest(MatrixRepresentationTest.suite());
        suite.addTest(RedundancyTest.suite());
        suite.addTest(MinimizerTest.suite());
        suite.addTest(BreadthFirstStateIteratorTest.suite());
        suite.addTest(DeterministicMatcherTest.suite());
        suite.addTest(MeasureTest.suite());
        suite.addTest(MutationTest.suite());
        suite.addTest(InvalidSampleGeneratorTest.suite());
        suite.addTest(AlphabetNormalizerTest.suite());
        suite.addTest(LexicographicNormalizerTest.suite());
        suite.addTest(LanguageTestTest.suite());
        suite.addTest(DeriverTest.suite());
        suite.addTest(EpsilonEmptyEliminatorTest.suite());
        suite.addTest(SOABuilderTest.suite());
        suite.addTest(DTDConverterTest.suite());
        suite.addTest(gjb.flt.automata.measures.MeasureTest.suite());
        suite.addTest(gjb.flt.automata.measures.LanguageTestTest.suite());
        suite.addTest(SubexpressionAmbiguityTest.suite());
        suite.addTest(SupportSOAFactoryTest.suite());
        suite.addTest(XMLDocumentTest.suite());
        suite.addTest(SOATestTest.suite());
        suite.addTest(SOAEditDistanceTest.suite());
        suite.addTest(StateEliminationFactoryTest.suite());
		return suite;
	}

}
