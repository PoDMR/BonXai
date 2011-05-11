package gjb.util.tree;


import gjb.util.tree.NodeTransformException;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;
import gjb.util.tree.converters.NodePropertyWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class NodePropertyWriterTest extends TestCase {

	public static Test suite() {
		return new TestSuite(NodePropertyWriterTest.class);
	}

	public void testTree() {
		final String regexStr = "(. (a) (| (b) (. (c) (d)) (? (e))) (+ (f)))";
		try {
			Reader reader = new StringReader(regexStr);
			final String target =
				".\tnull\tnull\t1\t0\t3\n" +
				"a\t.\tnull\t2\t0\t0\n" +
				"|\t.\ta\t2\t1\t3\n" +
				"b\t|\tnull\t3\t0\t0\n" +
				".\t|\tb\t3\t1\t2\n" +
				"c\t.\tnull\t4\t0\t0\n" +
				"d\t.\tc\t4\t1\t0\n" +
				"?\t|\t.\t3\t2\t1\n" +
				"e\t?\tnull\t4\t0\t0\n" +
				"+\t.\t|\t2\t2\t1\n" +
				"f\t+\tnull\t3\t0\t0\n";
			Tree tree = new Tree(reader);
			Writer writer = new StringWriter();
			NodePropertyWriter plsdWriter = new NodePropertyWriter(writer);
			plsdWriter.write(tree);
			assertEquals(target, writer.toString());
			
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void testTree_2_1() {
		final String regexStr = "(. (a) (| (b) (. (c) (d)) (? (e))) (+ (f)))";
		try {
			Reader reader = new StringReader(regexStr);
			final String target =
				".\tnull;null\tnull\t1\t0\t3\n" +
				"a\tnull;.\tnull\t2\t0\t0\n" +
				"|\tnull;.\ta\t2\t1\t3\n" +
				"b\t.;|\tnull\t3\t0\t0\n" +
				".\t.;|\tb\t3\t1\t2\n" +
				"c\t|;.\tnull\t4\t0\t0\n" +
				"d\t|;.\tc\t4\t1\t0\n" +
				"?\t.;|\t.\t3\t2\t1\n" +
				"e\t|;?\tnull\t4\t0\t0\n" +
				"+\tnull;.\t|\t2\t2\t1\n" +
				"f\t.;+\tnull\t3\t0\t0\n";
			Tree tree = new Tree(reader);
			Writer writer = new StringWriter();
			NodePropertyWriter plsdWriter = new NodePropertyWriter(writer, 2, 1);
			plsdWriter.write(tree);
			assertEquals(target, writer.toString());
			
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testTree_2_2() {
		final String regexStr = "(. (a) (| (b) (. (c) (d)) (? (e))) (+ (f)))";
		try {
			Reader reader = new StringReader(regexStr);
			final String target =
				".\tnull;null\tnull;null\t1\t0\t3\n" +
				"a\tnull;.\tnull;null\t2\t0\t0\n" +
				"|\tnull;.\tnull;a\t2\t1\t3\n" +
				"b\t.;|\tnull;null\t3\t0\t0\n" +
				".\t.;|\tnull;b\t3\t1\t2\n" +
				"c\t|;.\tnull;null\t4\t0\t0\n" +
				"d\t|;.\tnull;c\t4\t1\t0\n" +
				"?\t.;|\tb;.\t3\t2\t1\n" +
				"e\t|;?\tnull;null\t4\t0\t0\n" +
				"+\tnull;.\ta;|\t2\t2\t1\n" +
				"f\t.;+\tnull;null\t3\t0\t0\n";
			Tree tree = new Tree(reader);
			Writer writer = new StringWriter();
			NodePropertyWriter plsdWriter = new NodePropertyWriter(writer, 2, 2);
			plsdWriter.write(tree);
			assertEquals(target, writer.toString());
			
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testSingleNodeTree() {
		final String regexStr = "(a)";
		try {
			Reader reader = new StringReader(regexStr);
			final String target = "a\tnull\tnull\t1\t0\t0\n";
			Tree tree = new Tree(reader);
			Writer writer = new StringWriter();
			NodePropertyWriter plsdWriter = new NodePropertyWriter(writer);
			plsdWriter.write(tree);
			assertEquals(target, writer.toString());
			
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void testSingleNodeTree_1_1() {
		final String regexStr = "(a)";
		try {
			Reader reader = new StringReader(regexStr);
			final String target = "a\tnull\tnull\t1\t0\t0\n";
			Tree tree = new Tree(reader);
			Writer writer = new StringWriter();
			NodePropertyWriter plsdWriter = new NodePropertyWriter(writer, 1, 1);
			plsdWriter.write(tree);
			assertEquals(target, writer.toString());
			
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void testSingleNodeTree_2_2() {
		final String regexStr = "(a)";
		try {
			Reader reader = new StringReader(regexStr);
			final String target = "a\tnull;null\tnull;null\t1\t0\t0\n";
			Tree tree = new Tree(reader);
			Writer writer = new StringWriter();
			NodePropertyWriter plsdWriter = new NodePropertyWriter(writer, 2, 2);
			plsdWriter.write(tree);
			assertEquals(target, writer.toString());
			
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NodeTransformException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
}
