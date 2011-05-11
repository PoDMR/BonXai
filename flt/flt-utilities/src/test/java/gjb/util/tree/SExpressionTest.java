package gjb.util.tree;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.*;

public class SExpressionTest extends TestCase {
  
	protected Tree tree1;
	protected Node node1, node2, node3, node4, node5, node6;
	protected String str1 = "(a (b (d)) (c (e) (f)))";
	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public SExpressionTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(SExpressionTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		tree1 = new Tree();
		node1 = new Node("a", new Integer(15));
		node2 = new Node("b", new Integer(16));
		node3 = new Node("c", new Integer(17));
		node4 = new Node("d", new Integer(18));
		node5 = new Node("e", new Integer(19));
		node6 = new Node("f", new Integer(20));
		tree1.setRoot(node1);
		node1.addChild(node2);
		node1.addChild(node3);
		node2.addChild(node4);
		node3.addChild(node5);
		node3.addChild(node6);
	}

	public void test_TreeNotNull() {
		assertTrue(tree1 != null);
	}

	public void test_toSExpression() {
		assertEquals(tree1.toSExpression(), str1);
	}

	public void test_fromSExpression()
		throws SExpressionParseException, NoCurrentChildException {
		Tree tree = Tree.parse("(a (b (c) (d)) (e (f)))");
		Handle handle = new Handle(tree);
		assertEquals(handle.key(), "a");
		handle.first();
		assertEquals(handle.childKey(), "b");
		handle.next();
		assertEquals(handle.childKey(), "e");
		handle.down();
		handle.first();
		assertEquals(handle.childKey(), "f");
		handle.up();
		handle.first();
		handle.down();
		handle.first();
		assertEquals(handle.childKey(), "c");
		handle.next();
		assertEquals(handle.childKey(), "d");
	}

	public void test_emptyTree() throws SExpressionParseException {
		Tree tree = Tree.parse("()");
		assertTrue(tree.isEmpty());
		assertEquals(tree.getRoot(), null);
	}

	public void test_readerConstructor()
		    throws SExpressionParseException,IOException, NoCurrentChildException {
		Reader reader = new StringReader("(a (b (c) (d)) (e (f)))");
		Tree tree = new Tree(reader);
		reader.close();
		Handle handle = new Handle(tree);
		assertEquals(handle.key(), "a");
		handle.first();
		assertEquals(handle.childKey(), "b");
		handle.next();
		assertEquals(handle.childKey(), "e");
		handle.down();
		handle.first();
		assertEquals(handle.childKey(), "f");
		handle.up();
		handle.first();
		handle.down();
		handle.first();
		assertEquals(handle.childKey(), "c");
		handle.next();
		assertEquals(handle.childKey(), "d");
	}

	public void test_SExpressionException() throws IOException {
	    Reader reader = new StringReader("(a (b (c) (d))");
	    try {
            new Tree(reader);
            fail();
        } catch (SExpressionParseException e) {
        } finally {
            reader.close();
        }
	}
	
}
