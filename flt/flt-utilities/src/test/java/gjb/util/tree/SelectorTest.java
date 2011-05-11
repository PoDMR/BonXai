package gjb.util.tree;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;
import gjb.util.tree.selectors.PositionalNodeSelector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created on Sep 3, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SelectorTest extends TestCase {

	public static Test suite() {
		return new TestSuite(SelectorTest.class);
	}

	public void testEmptyTree() {
		Tree tree = new Tree();
		PositionalNodeSelector selector = new PositionalNodeSelector();
		try {
			selector.select(tree, 0);
			fail("expected exception");
		} catch (IndexOutOfBoundsException e) {}
	}

	public void testSelectionOnTree() {
		String treeStr = "(. (| (a) (? (b))) (c) (+ (. (d) (? (e)))))";
		PositionalNodeSelector selector = new PositionalNodeSelector();
		try {
			Tree tree = Tree.parse(treeStr);
			Node node = selector.select(tree, 0, 1, 0);
			assertEquals("b node at [0,1,0]", "b", node.getKey());
			node = selector.select(tree, 1);
			assertEquals("c node at [1]", "c", node.getKey());
			node = selector.select(tree, 2, 0, 1);
			assertEquals("c node at [2, 0, 1]", "?", node.getKey());
			node = selector.select(tree);
			assertEquals("root node", ".", node.getKey());
			try {
				selector.select(tree, 3);
				fail("expected exception for [3]");
			} catch (IndexOutOfBoundsException e) {}
			try {
				selector.select(tree, 2, 0, 0, 0);
				fail("expected exception for [2, 0, 0, 0]");
			} catch (IndexOutOfBoundsException e) {}
			try {
				selector.select(tree, -1);
				fail("expected exception for [-1]");
			} catch (IndexOutOfBoundsException e) {}
			node = selector.select(tree);
			assertEquals("root", ".", node.getKey());
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void testSelectionOnNode() {
		String treeStr = "(. (| (a) (? (b))) (c) (+ (. (d) (? (e)))))";
		PositionalNodeSelector selector = new PositionalNodeSelector();
		try {
			Tree tree = Tree.parse(treeStr);
			Node node = selector.select(tree, 0, 1);
			assertEquals("? node at [0,1]", "?", node.getKey());
			node = selector.select(node, 0);
			assertEquals("b node at [0,1,0]", "b", node.getKey());
			node = selector.select(node, -1, -1, -1);
			assertEquals(". node at []", ".", node.getKey());
			node = selector.select(node, new int[] {});
			assertEquals(". node at []", ".", node.getKey());
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

}
