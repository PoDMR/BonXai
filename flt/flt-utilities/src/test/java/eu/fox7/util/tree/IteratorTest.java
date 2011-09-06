package eu.fox7.util.tree;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;
import eu.fox7.util.tree.iterators.ContextFilterIterator;
import eu.fox7.util.tree.iterators.FilterIterator;
import eu.fox7.util.tree.iterators.NodeIterator;
import eu.fox7.util.tree.iterators.PostOrderIterator;
import eu.fox7.util.tree.iterators.PreOrderIterator;
import eu.fox7.util.tree.selectors.Context;
import eu.fox7.util.tree.selectors.NodeContextSelector;
import eu.fox7.util.tree.selectors.NodeSelector;

import java.util.*;

import junit.framework.*;

public class IteratorTest extends TestCase {
  
	protected Tree tree1;
	protected String str1 = "(a (b (d)) (c (e) (f)))";
	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public IteratorTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(IteratorTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		try {
			tree1 = Tree.parse(str1);
		} catch (SExpressionParseException e) {
			throw new Error(e.getMessage());
		}
	}

	public void test_postOrderIteratorNode() {
		assertTrue(tree1 != null);
		String[] result = {"d", "b", "e", "f", "c", "a"};
		int i = 0;
		for (Iterator<Node> it = new PostOrderIterator(tree1); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_postOrderIteratorTree() {
		assertTrue(tree1 != null);
		String[] result = {"d", "b", "e", "f", "c", "a"};
		int i = 0;
		for (Iterator<Node> it = tree1.postOrderIterator(); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_preOrderIteratorNode() {
		assertTrue(tree1 != null);
		String[] result = {"a", "b", "d", "c", "e", "f"};
		int i = 0;
		for (Iterator<Node> it = new PreOrderIterator(tree1); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_preOrderIteratorTree() {
		assertTrue(tree1 != null);
		String[] result = {"a", "b", "d", "c", "e", "f"};
		int i = 0;
		for (Iterator<Node> it = tree1.preOrderIterator(); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_breadthFirstIteratorNode() {
		assertTrue(tree1 != null);
		String[] result = {"a", "b", "c", "d", "e", "f"};
		int i = 0;
		for (Iterator<Node> it = tree1.getRoot().breadthFirstIterator(); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_breadthFirstIteratorTree() {
		assertTrue(tree1 != null);
		String[] result = {"a", "b", "c", "d", "e", "f"};
		int i = 0;
		for (Iterator<Node> it = tree1.breadthFirstIterator(); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_leavesNode() {
		assertTrue(tree1 != null);
		String[] result = {"d", "e", "f"};
		int i = 0;
		for (Iterator<Node> it = tree1.getRoot().leaves(); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_leavesTree() {
		assertTrue(tree1 != null);
		String[] result = {"d", "e", "f"};
		int i = 0;
		for (Iterator<Node> it = tree1.leaves(); it.hasNext(); ) {
			Node node = it.next();
			assertEquals(result[i++], node.key());
		}
	}

	public void test_singleNodeIteratorNode() {
		Node n = new Node("a");
		Iterator<Node> it = new PostOrderIterator(n);
		assertEquals("a", it.next().key());
		assertTrue(!it.hasNext());
		it = new PreOrderIterator(n);
		assertEquals("a", it.next().key());
		assertTrue(!it.hasNext());
		it = n.breadthFirstIterator();
		assertEquals("a", it.next().key());
		assertTrue(!it.hasNext());
		it = n.leaves();
		assertTrue(it.hasNext());
		assertEquals("a", it.next().key());
		assertTrue(!it.hasNext());
	}

	public void test_emptyTreeTest() {
		Tree tree = new Tree();
		Iterator<Node> it = tree.postOrderIterator();
		assertTrue(!it.hasNext());
		it = tree.preOrderIterator();
		assertTrue(!it.hasNext());
		it = tree.breadthFirstIterator();
		assertTrue(!it.hasNext());
		it = tree.leaves();
		assertTrue(!it.hasNext());
	}

	public void test_filterIteratorNodeSelector() {
		try {
			Tree tree = Tree.parse("(+ (. (| (a) (b)) (| (c) (d)) (| (b) (a))))");
			NodeSelector selector = new UnionSelector();
			NodeIterator iterator = new PreOrderIterator(tree);
			int counter = 0;
			for (NodeIterator filter = new FilterIterator(iterator, selector);
			     filter.hasNext(); ) {
				Node node = filter.next();
				assertTrue("match", selector.isMatch(node));
				counter++;
			}
			assertEquals("matches", 2, counter);
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	@SuppressWarnings("unchecked")
    public void test_contextFilterIterator() {
		try {
	        Tree tree = Tree.parse("(+ (. (| (a) (b)) (| (c) (d)) (| (a) (a) (b) (a))))");
	        final int[] targetSizes = {1, 3};
	        ContextFilterIterator it = new ContextFilterIterator(new PreOrderIterator(tree),
	                                                             new UnionContextSelector());
	        int count = 0;
	        while (it.hasNext()) {
	        	Context set = it.next();
	        	assertTrue("count", count < targetSizes.length);
	        	assertEquals("node " + count,
	        	             targetSizes[count],
	        	             ((Set<Node>) set).size());
	        	count++;
	        }
	        assertEquals("total", 2, count);
        } catch (SExpressionParseException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
		
	}

	public static class UnionSelector implements NodeSelector {

		public boolean isMatch(Node node) {
			if (node.getKey().equals("|"))
				for (int i = 0; i < node.getNumberOfChildren(); i++)
					if (node.getChild(i).getKey().equals("a"))
						return true;
			return false;
		}
		
	}

	public static class UnionContextSelector implements NodeContextSelector<SetContext> {

		protected SetContext context = new SetContext();
	
		public boolean isMatch(Node node) {
			context.clear();
			if (node.getKey().equals("|")) {
				for (int i = 0; i < node.getNumberOfChildren(); i++) {
					Node child = node.getChild(i);
					if (child.getKey().equals("a"))
						context.add(child);
				}
				if (!context.isEmpty()) {
					context.setSelected(node);
					return true;
				} else
					context.clear();
			}
			return false;
		}

		public SetContext getContext() {
			return context;
		}
		
	}

	public static class SetContext extends HashSet<Node> implements Context {

		private static final long serialVersionUID = 5053983308437305614L;
		protected Node selected;

        public Node getSelected() {
        	return selected;
        }

		public void setSelected(Node selected) {
        	this.selected = selected;
        }

		@Override
		public SetContext clone() {
			SetContext clone = new SetContext();
			clone.addAll(this);
			return clone;
		}

		@Override
		public void clear() {
			super.clear();
			selected = null;
		}

	}

}
