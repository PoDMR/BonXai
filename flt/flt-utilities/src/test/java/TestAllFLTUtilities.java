import gjb.math.BinnerTest;
import gjb.math.MatrixTest;
import gjb.math.ProbabilityDistributionFactoryTest;
import gjb.math.StatsTest;
import gjb.math.WilcoxonTest;
import gjb.util.CarthesianProductTest;
import gjb.util.CollectionsTest;
import gjb.util.EquivalenceRelationTest;
import gjb.util.PairIteratorTest;
import gjb.util.PairTest;
import gjb.util.PairwiseIteratorTest;
import gjb.util.PermutationSetTest;
import gjb.util.RandomSelectorTest;
import gjb.util.SymbolIteratorTest;
import gjb.util.TableFormatterTest;
import gjb.util.TimedRunnerTest;
import gjb.util.TimerTest;
import gjb.util.datafolder.DataFolderXmlTest;
import gjb.util.sampler.ExampleParserTest;
import gjb.util.tree.TestAllJavaTree;
import gjb.util.xml.AncestorChildrenDocumentIteratorTest;
import gjb.util.xml.ExampleIteratorTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created on Oct 26, 2009
 * Modified on $Date: 2009-11-09 13:14:49 $
 */

/**
 * @author lucg5005
 * @version $Revision: 1.4 $
 *
 */
public class TestAllFLTUtilities extends TestCase {

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public TestAllFLTUtilities(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
        suite.addTest(CarthesianProductTest.suite());
        suite.addTest(CollectionsTest.suite());
        suite.addTest(EquivalenceRelationTest.suite());
        suite.addTest(PairIteratorTest.suite());
        suite.addTest(PermutationSetTest.suite());
        suite.addTest(TableFormatterTest.suite());
        suite.addTest(SymbolIteratorTest.suite());
        suite.addTest(RandomSelectorTest.suite());
        suite.addTest(ProbabilityDistributionFactoryTest.suite());
        suite.addTest(PairTest.suite());
        suite.addTest(PairwiseIteratorTest.suite());
        suite.addTest(StatsTest.suite());
        suite.addTest(TimerTest.suite());
        suite.addTest(WilcoxonTest.suite());
        suite.addTest(BinnerTest.suite());
        suite.addTest(MatrixTest.suite());
        suite.addTest(TimedRunnerTest.suite());

        suite.addTest(TestAllJavaTree.suite());

		suite.addTest(ExampleParserTest.suite());

		suite.addTest(DataFolderXmlTest.suite());

		suite.addTest(AncestorChildrenDocumentIteratorTest.suite());
		suite.addTest(ExampleIteratorTest.suite());

		return suite;
	}

}
