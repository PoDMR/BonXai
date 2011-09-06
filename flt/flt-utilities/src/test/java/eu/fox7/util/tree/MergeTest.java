package eu.fox7.util.tree;

import eu.fox7.util.tree.Handle;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;
import junit.framework.*;

public class MergeTest extends TestCase {
  
	protected Tree tree1, tree2;
	protected Node node1, node2, node3;
	protected Handle handle1, handle2, handle3;

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public MergeTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(MergeTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		tree1 = new Tree();
		handle1 = new Handle(tree1);
		handle1.setKey("root 1");
		handle1.setValue(new Integer(11));
		handle1.addChild("test 1 left", new Integer(15));
		handle1.addChild("test 1 right", new Integer(17));
		tree2 = new Tree();
		handle2 = new Handle(tree2);
		handle2.setKey("root 2");
		handle2.setValue(new Integer(21));
		handle2.addChild("test 2 left", new Integer(25));
		handle2.addChild("test 2 right", new Integer(27));
	}

	public void test_Insert() throws Exception {
		Handle handle = new Handle(tree1);
		handle.down(1);
		assertEquals(handle.key(), "test 1 right");
		assertEquals(handle.value(), new Integer(17));
		Handle h = new Handle(tree2);
		assertEquals(h.key(), "root 2");
		assertEquals(h.value(), new Integer(21));
		assertTrue(handle.insert(h));
		handle.first();
		assertEquals(handle.childKey(), "root 2");
		assertEquals(handle.childValue(), new Integer(21));
		handle.down(0);
		assertEquals(handle.nrOfChildren(), 2);
		handle.first();
		assertEquals(handle.childKey(), "test 2 left");
		assertEquals(handle.childValue(), new Integer(25));
		handle.down();
		assertEquals(handle.nrOfChildren(), 0);
		handle.up();
		handle.last();
		assertEquals(handle.childKey(), "test 2 right");
		assertEquals(handle.childValue(), new Integer(27));
		handle.down();
		assertEquals(handle.nrOfChildren(), 0);
		handle.up();
	}

	public void test_InsertAt() throws Exception {
		Handle handle = new Handle(tree1);
		handle.down(0);
		assertEquals(handle.key(), "test 1 left");
		assertEquals(handle.value(), new Integer(15));
		Handle h = new Handle(tree2);
		assertEquals(h.key(), "root 2");
		assertEquals(h.value(), new Integer(21));
		handle.up();
		Node node = handle.insert(h, 0);
		assertEquals(node.key(), "test 1 left");
		assertEquals(node.value(), new Integer(15));
		handle.first();
		assertEquals(handle.childKey(), "root 2");
		assertEquals(handle.childValue(), new Integer(21));
		handle.down(0);
		assertEquals(handle.nrOfChildren(), 2);
		handle.first();
		assertEquals(handle.childKey(), "test 2 left");
		assertEquals(handle.childValue(), new Integer(25));
		handle.down();
		assertEquals(handle.nrOfChildren(), 0);
		handle.up();
		handle.last();
		assertEquals(handle.childKey(), "test 2 right");
		assertEquals(handle.childValue(), new Integer(27));
		handle.down();
		assertEquals(handle.nrOfChildren(), 0);
		handle.up();
	}

}
