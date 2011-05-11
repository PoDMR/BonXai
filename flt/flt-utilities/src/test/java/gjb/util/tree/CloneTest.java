package gjb.util.tree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CloneTest extends TestCase {
  
	protected Tree tree1;
	protected String str1 = "(a (b (d)) (c (e) (f)))";
	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public CloneTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(CloneTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		try {
			tree1 = Tree.parse(str1);
		} catch (SExpressionParseException e) {
			throw new Error(e.getMessage());
		}
	}

	public void test_instantiation() {
		assertTrue(tree1 != null);
		Handle handle = new Handle(tree1);
		assertEquals(handle.key(), "a");
		handle.down(0);
		assertEquals(handle.key(), "b");
		handle.down(0);
		assertEquals(handle.key(), "d");
		handle.up();
		handle.up();
		handle.down(1);
		assertEquals(handle.key(), "c");
		handle.down(0);
		assertEquals(handle.key(), "e");
		handle.up();
		handle.down(1);
		assertEquals(handle.key(), "f");
		assertEquals(handle.tree(), tree1);
	}

	public void test_Clone() {
		Tree tree2 = new Tree(tree1);
		Handle handle = new Handle(tree2);
		assertEquals(handle.key(), "a");
		handle.down(0);
		assertEquals(handle.key(), "b");
		handle.down(0);
		assertEquals(handle.key(), "d");
		handle.up();
		handle.up();
		handle.down(1);
		assertEquals(handle.key(), "c");
		handle.down(0);
		assertEquals(handle.key(), "e");
		handle.up();
		handle.down(1);
		assertEquals(handle.key(), "f");
		assertEquals(handle.tree(), tree2);
	}

}
