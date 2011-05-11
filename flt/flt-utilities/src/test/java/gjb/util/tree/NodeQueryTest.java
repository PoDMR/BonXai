package gjb.util.tree;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/*
 * Created on Jun 1, 2006
 * Modified on $Date: 2009-11-12 22:13:31 $
 */

public class NodeQueryTest extends TestCase {

    protected Tree tree1, tree2;
    protected String str1 = "(a (b (a)) (c (a) (c)))";
    protected String str2 = "(a (b (c (d) (e)) (f (g (h (i))))))";
    

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NodeQueryTest.class);
    }

    public static Test suite() {
        return new TestSuite(NodeQueryTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            tree1 = Tree.parse(str1);
            tree2 = Tree.parse(str2);
        } catch (SExpressionParseException e) {
            throw new Error(e.getMessage());
        }
    }

    public void test_descendants() {
        Node node1 = tree1.getRoot();
        Node node2 = node1.child(0).child(0);
        assertTrue("grandchild 1", node2.isDecendantOf(node1));
        assertTrue("grandparent 1", !node1.isDecendantOf(node2));
        Node node3 = node1.child(1);
        assertTrue("child 1", node3.isDecendantOf(node1));
        assertTrue("parent 1", !node1.isDecendantOf(node3));
        assertTrue("incomparable 1",
                   !node2.isDecendantOf(node3) &&
                   !node3.isDecendantOf(node2));
    }

    public void test_leastCommonAncestor() {
        Node root = tree1.getRoot();
        Node lca = root;
        Node node1 = root.child(0).child(0);
        Node node2 = root.child(1).child(0);
        assertEquals("lca 1", lca, Node.getLeastCommonAncestor(node1, node2));
        assertEquals("lca self 1", lca, Node.getLeastCommonAncestor(lca, lca));
        assertEquals("lca self 2", node2, Node.getLeastCommonAncestor(node2, node2));
        Node node3 = root.child(1).child(1);
        lca = root.child(1);
        assertEquals("lca 2", lca, Node.getLeastCommonAncestor(node2, node3));
        Node node4 = root.child(1);
        assertEquals("lca parent 1", node4, Node.getLeastCommonAncestor(node4, node3));
        assertEquals("lca parent 2", node4, Node.getLeastCommonAncestor(node2, node4));
    }

    public void test_leastCommonAncestorDiffPathLength() {
        Node root = tree2.getRoot();
        Node lca = root.child(0);
        Node node1 = root.child(0).child(0);
        Node node2 = root.child(0).child(1).child(0).child(0);
        assertEquals("lca", lca, Node.getLeastCommonAncestor(node1, node2));
        assertEquals("lca", lca, Node.getLeastCommonAncestor(node2, node1));
    }

}
