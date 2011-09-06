package eu.fox7.util.tree;

import eu.fox7.util.tree.Handle;
import eu.fox7.util.tree.NoCurrentChildException;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;
import junit.framework.*;

public class HandleTest extends TestCase {
  
	protected Tree tree1, tree2;
	protected Node node1, node2, node3;
	protected Handle handle1, handle2, handle3;

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public HandleTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(HandleTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		tree1 = new Tree();
		handle1 = new Handle(tree1);
		tree2 = new Tree();
		handle2 = new Handle(tree2);
		handle2.setKey("test root");
		handle2.setValue(new Integer(11));
		handle2.addChild("test left", new Integer(15));
		handle2.addChild("test right", new Integer(17));
	}

	public void test_HandleTreeNotEmpty() {
		assertTrue(!handle1.tree().isEmpty());
		assertTrue(handle1.tree().getRoot() != null);
	}

	public void test_HandleHasTree() {
		assertEquals(handle1.tree(), tree1);
	}

	public void test_HandleSetKeyValue() {
		assertTrue(handle1.key() == null);
		handle1.setKey("Test");
		handle1.setValue(new Integer(5));
		assertEquals(tree1.getRoot().key(), "Test");
		assertEquals(tree1.getRoot().value(), new Integer(5));
	}

	public void test_HandleAddChild() {
		assertTrue(handle2.hasChildren());
		assertEquals(2, handle2.nrOfChildren());
		assertEquals(handle2.tree().getRoot().child(0).key(), "test left");
		assertEquals(handle2.tree().getRoot().child(0).value(), new Integer(15));
		assertEquals(handle2.tree().getRoot().child(1).key(), "test right");
		assertEquals(handle2.tree().getRoot().child(1).value(), new Integer(17));
	}

	public void test_HandleFirstChildDown() {
		handle2.first();
		handle2.down();
		assertEquals(handle2.key(), "test left");
	}

	public void test_HandleSecondChildDownAndBackUp() {
		handle2.first();
		handle2.next();
		handle2.down();
		assertEquals(handle2.key(), "test right");
		handle2.up();
		assertEquals(handle2.key(), "test root");
	}

	public void test_HandleFirstChildDownAndBackUp() {
		handle2.last();
		assertEquals(0, handle2.depth());
		handle2.previous();
		handle2.down();
		assertEquals(handle2.key(), "test left");
		assertEquals(1, handle2.depth());
		handle2.up();
		assertEquals(handle2.key(), "test root");
		assertEquals(0, handle2.depth());
	}

	public void test_HandleClone() {
		handle2.setPosition(1);
		Handle handle = new Handle(handle2);
		assertEquals(handle2.tree(), handle.tree());
		handle.down();
		assertEquals(handle.key(), "test right");
	}

	public void test_HandleTop() {
		handle2.down(1);
		assertEquals(1, handle2.depth());
		assertEquals(handle2.key(), "test right");
		handle2.top();
		assertEquals(handle2.key(), "test root");
		assertEquals(0, handle2.depth());
	}

	public void test_HandleChildKeyValue() {
		handle2.first();
		try {
			assertEquals(handle2.childKey(), "test left");
			assertEquals(handle2.childValue(), new Integer(15));
		} catch(Exception e) {
			fail("No exception should be generated " + e.toString());
		}
		handle2.next();
		try {
			assertEquals(handle2.childKey(), "test right");
			assertEquals(handle2.childValue(), new Integer(17));
		} catch(Exception e) {
			fail("No exception should be generated " + e.toString());
		}
	}

	public void test_HandleNoCurrentChildException() {
		try {
			handle2.child();
			fail("NoCurrentChildException should be thrown");
		} catch(NoCurrentChildException e) {
			assertTrue(true);
		}
	}

	public void test_HandleRemoveChild()
		throws NoCurrentChildException {
		handle2.first();
		Node oldNode = handle2.removeChild();
		assertEquals(1, handle2.nrOfChildren());
		assertEquals(oldNode.key(), "test left");
		handle2.first();
		assertEquals(handle2.childKey(), "test right");
		handle2.removeChild(0);
		assertEquals(0, handle2.nrOfChildren());
		assertTrue(!handle2.hasChildren());
		try {
			handle2.removeChild(0);
			fail("exception should be thrown");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}

	public void test_HandleNavigation() {
		assertEquals(handle2.key(), "test root");
		handle2.first();
		handle2.down();
		assertEquals(handle2.key(), "test left");
		handle2.up();
		handle2.next();
		handle2.down();
		assertEquals(handle2.key(), "test right");
		handle2.up();
		assertTrue(!handle2.next());
		handle2.previous();
		handle2.down();
		assertEquals(handle2.key(), "test left");
		handle2.addChild("test left-left", new Integer(3));
		handle2.addChild("test left-middle", new Integer(5));
		handle2.addChild("test left-right", new Integer(7));
		handle2.first();
		handle2.down();
		assertEquals(handle2.key(), "test left-left");
		handle2.up();
		handle2.next();
		handle2.down();
		assertEquals(handle2.key(), "test left-middle");
		handle2.up();
		handle2.next();
		handle2.down();
		assertEquals(handle2.key(), "test left-right");
		handle2.up();
		handle2.up();
		handle2.next();
		handle2.down();
		assertEquals(handle2.key(), "test right");
	}

}
