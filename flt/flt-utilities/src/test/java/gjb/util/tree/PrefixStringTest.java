package gjb.util.tree;

import gjb.util.tree.io.PrefixStringTreeReader;
import gjb.util.tree.io.PrefixStringTreeWriter;
import junit.framework.*;
import java.io.*;

public class PrefixStringTest extends TestCase {
  
	protected Tree tree1, tree2, tree3, tree4;
	protected String str1 = "(a (b (d)) (c (e) (f)))";
	protected String str2 = "(a)";
	protected String str3 = "()";
	protected String str4 = "(a (b (c)))";
	protected String treeStr1 = "a b d -1 -1 c e -1 f -1 -1 -1";
	protected String treeStr2 = "a -1";
	protected String treeStr3 = "";
	protected String treeStr4 = "a b c -1 -1 -1";

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public PrefixStringTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(PrefixStringTest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		try {
			tree1 = Tree.parse(str1);
			tree2 = Tree.parse(str2);
			tree3 = Tree.parse(str3);
			tree4 = Tree.parse(str4);
		} catch (SExpressionParseException e) {
			System.err.println(e.getMessage());
		}
	}

	public void test_prefixStringTreeWriter() throws TreeException {
		assertTrue(tree1 != null);
		StringWriter writer = new StringWriter();
		PrefixStringTreeWriter treeWriter = new PrefixStringTreeWriter();
		treeWriter.write(tree1, writer);
		assertEquals(treeStr1, writer.toString());
		writer = new StringWriter();
		treeWriter.write(tree2, writer);
		assertEquals(treeStr2, writer.toString());
		writer = new StringWriter();
		treeWriter.write(tree3, writer);
		assertEquals(treeStr3, writer.toString());
		writer = new StringWriter();
		treeWriter.write(tree4, writer);
		assertEquals(treeStr4, writer.toString());
	}

	public void test_prefixStringTreeReader() throws TreeException {
		PrefixStringTreeReader treeReader = new PrefixStringTreeReader();
		StringReader reader = new StringReader(treeStr1);
		Tree tree = treeReader.read(reader);
		assertEquals(tree.toSExpression(), str1);
		reader = new StringReader(treeStr2);
		tree = treeReader.read(reader);
		assertEquals(tree.toSExpression(), str2);
		reader = new StringReader(treeStr3);
		tree = treeReader.read(reader);
		assertEquals(tree.toSExpression(), str3);
		reader = new StringReader(treeStr4);
		tree = treeReader.read(reader);
		assertEquals(tree.toSExpression(), str4);
	}

}
