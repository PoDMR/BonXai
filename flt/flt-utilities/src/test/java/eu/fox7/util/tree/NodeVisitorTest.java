package eu.fox7.util.tree;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.NodeTransformException;
import eu.fox7.util.tree.NodeVisitor;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;
import eu.fox7.util.tree.TreeVisitor;
import eu.fox7.util.tree.iterators.PostOrderIterator;

import java.util.*;
import junit.framework.*;

public class NodeVisitorTest extends TestCase {
  
	protected Tree tree1, tree2;
	protected String str1 = "(a (b (a)) (c (a) (c)))";
	protected String str2 = "(# (# (3)) (# (5) (7)))";

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public NodeVisitorTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(NodeVisitorTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		try {
			tree1 = Tree.parse(str1);
			tree2 = Tree.parse(str2);
		} catch (SExpressionParseException e) {
			throw new Error(e.getMessage());
		}
	}

	public void test_visitModifyOwnKeys() throws Exception {
		NodeVisitor visitor = new UpperCaseVisitor();
		TreeVisitor treeVisitor = new TreeVisitor(tree1);
		treeVisitor.visit(visitor, null);
		assertEquals(str1.toUpperCase(), tree1.toSExpression());
	}

	public void test_visitCountKeys() throws Exception {
		NodeVisitor visitor = new CountKeysVisitor();
		Map<String,Object> counter = new HashMap<String,Object>();
		TreeVisitor treeVisitor = new TreeVisitor(tree1);
		treeVisitor.visit(visitor, counter);
		assertEquals(3, ((Integer) counter.get("a")).intValue());
		assertEquals(1, ((Integer) counter.get("b")).intValue());
		assertEquals(2, ((Integer) counter.get("c")).intValue());
	}

	public void test_visitEmptyTree() {
		try {
			Tree tree = new Tree();
			NodeVisitor visitor = new CountKeysVisitor();
			Map<String,Object> counter = new HashMap<String,Object>();
			TreeVisitor treeVisitor = new TreeVisitor(tree);
			treeVisitor.visit(visitor, counter);
			assertEquals(0, counter.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void test_sumKeys() {
		NodeVisitor visitor = new SumKeysVisitor();
		TreeVisitor treeVisitor = new TreeVisitor(new PostOrderIterator(tree2));
		try {
			treeVisitor.visit(visitor, null);
			assertEquals("sum", "15", tree2.getRoot().getKey());
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public static class UpperCaseVisitor implements NodeVisitor {

		public void visit(Node node, Map<String,Object> parameters) {
			node.setKey(node.key().toUpperCase());
		}

	}

	public static class CountKeysVisitor implements NodeVisitor {

		public void visit(Node node, Map<String,Object> counter) {
			if (!counter.containsKey(node.key())) {
				counter.put(node.key(), new Integer(0));
			}
			counter.put(node.key(),
						new Integer(((Integer) counter.get(node.key())).intValue() + 1));
		}

	}

	public static class SumKeysVisitor implements NodeVisitor {

		public void visit(Node node, Map<String,Object> parameters) {
			if (node.hasChildren()) {
				int sum = 0;
				for (int i = 0; i < node.getNumberOfChildren(); i++)
					sum += Integer.valueOf(node.getChild(i).getKey());
				node.setKey(Integer.toString(sum));
			}
		}

	}

}
