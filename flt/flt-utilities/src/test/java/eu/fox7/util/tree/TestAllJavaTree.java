package eu.fox7.util.tree;
import junit.framework.*;

public class TestAllJavaTree extends TestCase {
  
	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public TestAllJavaTree(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(TreeTest.suite());
		suite.addTest(HandleTest.suite());
		suite.addTest(MergeTest.suite());
		suite.addTest(SExpressionTest.suite());
		suite.addTest(CloneTest.suite());
		suite.addTest(IteratorTest.suite());
		suite.addTest(PrefixStringTest.suite());
		suite.addTest(ComparatorTest.suite());
		suite.addTest(FindTest.suite());
		suite.addTest(NodeVisitorTest.suite());
		suite.addTest(NodeModifierTest.suite());
		suite.addTest(JXPathTest.suite());
        suite.addTest(SerializerTest.suite());
        suite.addTest(NodeQueryTest.suite());
        suite.addTest(NodePropertyWriterTest.suite());
        suite.addTest(SExpressionWriterTest.suite());
        suite.addTest(SelectorTest.suite());
		return suite;
	}

}
