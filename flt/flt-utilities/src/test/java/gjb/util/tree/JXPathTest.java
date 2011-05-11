package gjb.util.tree;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.jxpath.JXPathContext;

public class JXPathTest extends TestCase {
  
	protected Tree tree1;
	protected String str1 = "(a (b (a)) (c (a) (c)))";

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public JXPathTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(JXPathTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		try {
			tree1 = Tree.parse(str1);
		} catch (SExpressionParseException e) {
			throw new Error(e.getMessage());
		}
	}

	public void test_simpleQuery1() {
		JXPathContext jXPath = JXPathContext.newContext(tree1);
		String key = (String) jXPath.getValue("root/@key");
		assertEquals("a", key);
	}

	@SuppressWarnings("unchecked")
    public void test_simpleQuery2() {
		JXPathContext jXPath = JXPathContext.newContext(tree1.getRoot());
		jXPath.setLenient(true);
		Iterator children = jXPath.iterate("children");
		assertTrue(children.hasNext());
		assertEquals("b", ((Node) children.next()).key());
		assertTrue(children.hasNext());
		assertEquals("c", ((Node) children.next()).key());
		assertTrue(!children.hasNext());
	}

	public void test_simpleQuery3() {
		JXPathContext jXPath = JXPathContext.newContext(tree1);
		jXPath.setLenient(true);
		String key = (String) jXPath.getValue("root/children[1]/@key");
		assertEquals("b", key);
		key = (String) jXPath.getValue("root/children[2]/@key");
		assertEquals("c", key);
	}

	public void test_query1() {
		JXPathContext jXPath = JXPathContext.newContext(tree1);
		jXPath.setLenient(true);
		Node node = (Node) jXPath.getValue("root/children[@key = 'b']");
		assertEquals("b", node.key());
	}

	public void test_query2() {
		JXPathContext jXPath = JXPathContext.newContext(tree1);
		jXPath.setLenient(true);
		Node node = (Node) jXPath.getValue("root/children[@key = 'b']/children[1]");
		assertEquals("a", node.key());
	}

	@SuppressWarnings("unchecked")
    public void test_query3() {
		JXPathContext jXPath = JXPathContext.newContext(tree1);
		jXPath.setLenient(true);
		Iterator it = jXPath.iterate("//node[@key = 'a']");
		assertTrue(it != null);
		assertTrue(it.hasNext());
		Node node = (Node) it.next();
		assertEquals("a", node.key());
		assertEquals(1, node.getHeight());
	}

}
