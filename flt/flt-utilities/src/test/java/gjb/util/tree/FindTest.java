package gjb.util.tree;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FindTest extends TestCase {
  
	protected Tree tree1;
	protected String str1 = "(a (b (d)) (c (e) (f)))";
	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public FindTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(FindTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		try {
			tree1 = Tree.parse(str1);
		} catch (SExpressionParseException e) {
			throw new Error(e.getMessage());
		}
	}

	public void test_find1() {
		Node node = tree1.findFirstNodeWithKey("a");
		assertEquals("a", node.key());
		node = tree1.findFirstNodeWithKey("e");
		assertEquals("e", node.key());
		assertTrue(tree1.findFirstNodeWithKey("g") == null);
	}

	public void test_find2() {
		Tree tree = new Tree();
		Node root = new Node("a");
		tree.setRoot(root);
		Node b1 = new Node("b");
		root.addChild(b1);
		Node b2 = new Node("b");
		root.addChild(b2);
		Node c1 = new Node("c");
		b1.addChild(c1);
		Node c2 = new Node("c");
		b2.addChild(c2);
		Node d1 = new Node("d");
		c2.addChild(d1);
		Node d2 = new Node("d");
		c2.addChild(d2);
		assertEquals(root, tree.findFirstNodeWithKey("a"));
		assertEquals(b1, tree.findFirstNodeWithKey("b"));
		assertEquals(c1, tree.findFirstNodeWithKey("c"));
		assertEquals(d1, tree.findFirstNodeWithKey("d"));
		assertTrue(tree.findFirstNodeWithKey("alpha") == null);
		assertTrue(tree.findFirstNodeWithKey("") == null);
		assertTrue(tree.findFirstNodeWithKey(null) == null);
	}

	public void test_findEmptyTree() {
		Tree tree = new Tree();
		assertTrue(tree.findFirstNodeWithKey("a") == null);
	}

}
