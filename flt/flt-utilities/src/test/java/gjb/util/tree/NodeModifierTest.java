package gjb.util.tree;

import java.util.Map;

import junit.framework.*;

public class NodeModifierTest extends TestCase {
  
	protected Tree tree1, tree2;
	protected String str1 = "(a (b (a)) (c (a) (c)))";
	protected String str2 = "(# (# (3)) (# (5) (7)))";

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public NodeModifierTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(NodeModifierTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
	    tree1 = Tree.parse(str1);
	    tree2 = Tree.parse(str2);
	}

	public void test_modifyKeys() {
		NodeModifier modifier = new UpperCaseModifier();
		TreeModifier treeModifier = new TreeModifier(tree1);
		try {
			Tree tree = treeModifier.modify(modifier, null);
			assertEquals("modification", str1.toUpperCase(), tree.toSExpression());
			assertEquals("invariant", str1, tree1.toSExpression());
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void test_modifyEmptyTree() {
		try {
			Tree tree = new Tree();
			NodeModifier modifier = new UpperCaseModifier();
			TreeModifier treeModifier = new TreeModifier(tree);
			assertTrue(treeModifier.modify(modifier, null).isEmpty());
		} catch (Exception e) {
			assertTrue(false);
		}
	}
		
	public static class UpperCaseModifier implements NodeModifier {

		public Node modify(Node node, Map<String,Object> parameters) {
			return new Node(node.key().toUpperCase());
		}

	}

}
