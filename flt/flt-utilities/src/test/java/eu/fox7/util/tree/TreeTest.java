package eu.fox7.util.tree;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;
import junit.framework.*;

public class TreeTest extends TestCase {
  
	protected Tree tree1, tree2;
	protected Node node1, node2, node3;

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public TreeTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(TreeTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		tree1 = new Tree();
		tree2 = new Tree();
		node1 = new Node("node-1", new Integer(15));
		node2 = new Node("node-2", new Integer(16));
		node3 = new Node("node-3", new Integer(17));
		tree2.setRoot(node1);
		node1.addChild(node2);
		node1.addChild(node3);
	}

	public void test_TreeNotNull() {
		assertTrue(tree1 != null && tree2 != null);
	}

	public void test_IsEmpty() {
		assertTrue(tree1.isEmpty());
	}

	public void test_CreateNode() {
		assertTrue(node1.key().equals("node-1") &&
				   node1.value().equals(new Integer(15)));
	}

	public void test_NotEmpty() {
		assertTrue(!tree2.isEmpty());
	}

	public void test_RootNotNull() {
		assertEquals(node1, tree2.getRoot());
	}

	public void test_CorrectRoot() {
		Node node = tree2.getRoot();
		assertEquals("node-1", node.key());
		assertEquals(new Integer(15), node.value());
	}

	public void test_NodeTreeNotNull() {
		assertTrue(node1.getTree() != null);
	}

	public void test_NodeTreeCorrect() {
		assertEquals(tree2, node1.getTree());
	}

	public void test_RootLeftChild() {
		Node left = tree2.getRoot().child(0);
		assertEquals("node-2", left.key());
		assertEquals(new Integer(16), left.value());
	}

	public void test_RootRightChild() {
		Node right = tree2.getRoot().child(0);
		assertEquals("node-2", right.key());
		assertEquals(new Integer(16), right.value());
	}

	public void test_RootNextSibling() {
		assertEquals(tree2.getRoot().child(1),
					 tree2.getRoot().child(0).getNextSibling());
	}

	public void test_RootPreviousSibling() {
		assertEquals(tree2.getRoot().child(0),
					 tree2.getRoot().child(1).getPreviousSibling());
	}

	public void test_RootNrOfChildren() {
		assertEquals(2, tree2.getRoot().getNumberOfChildren());
	}

	public void test_height() throws SExpressionParseException {
		assertEquals(2, tree2.getHeight());
		assertEquals(0, tree1.getHeight());
		tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)))) (l))))");
		assertEquals(7, tree1.getHeight());
	}

	public void test_maximumNrOfChildren() throws SExpressionParseException {
		assertEquals(2, tree2.getMaximumNrOfChildren());
		assertEquals(0, tree1.getMaximumNrOfChildren());
		tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)))) (l))))");
		assertEquals(3, tree1.getMaximumNrOfChildren());
	}

	public void test_width() throws SExpressionParseException {
		assertEquals(2, tree2.getWidth());
		assertEquals(0, tree1.getWidth());
		tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)))) (l))))");
		assertEquals(3, tree1.getWidth());
		tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)))) (l) (m))))");
		assertEquals(4, tree1.getWidth());
		tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)) (m) (n))) (l))))");
		assertEquals(5, tree1.getWidth());
	}

	public void test_numberOfNodes() throws SExpressionParseException {
	    assertEquals("empty tree", 0, (new Tree()).getNumberOfNodes());
	    Tree tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)))) (l))))");
	    assertEquals(12, tree1.getNumberOfNodes());
	    tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)))) (l) (m))))");
	    assertEquals(13, tree1.getNumberOfNodes());
	    tree1 = Tree.parse("(a (b (c (d)) (e (f (g (h) (i) (j (k)) (m) (n))) (l))))");
        assertEquals(14, tree1.getNumberOfNodes());
	    tree1 = Tree.parse("(a)");
        assertEquals(1, tree1.getNumberOfNodes());
	}
	
	public void test_positionAddChild() {
		Node n = new Node("a");
		for (int i = 4; i >= 0; i--) {
			n.setChild(i, new Node("a" + i));
		}
		assertEquals(5, n.getNumberOfChildren());
		for (int i = 0; i < n.getNumberOfChildren(); i++) {
			assertTrue(n.child(i) != null);
			assertEquals("a" + i, n.child(i).key());
		}
	}

}
