package eu.fox7.util.tree;
import java.io.StringWriter;
import java.io.Writer;

import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;
import eu.fox7.util.tree.io.SExpressionTreeWriter;
import eu.fox7.util.tree.io.TreeWriteException;
import eu.fox7.util.tree.io.TreeWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created on Jun 24, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SExpressionWriterTest extends TestCase {

	public static Test suite() {
		return new TestSuite(SExpressionWriterTest.class);
	}

	public void test_exprs() {
		final String[] treeStrs = {
				"()",
				"(a)",
				"(a (b))",
				"(a (b) (b))",
				"(a (a) (a))",
				"(a (b) (c (d)) (d))"
		};
		TreeWriter treeWriter = new SExpressionTreeWriter();
		for (String treeStr : treeStrs) {
			Writer writer = new StringWriter();
			try {
				Tree tree = Tree.parse(treeStr);
				treeWriter.write(tree, writer);
				assertEquals("tree", treeStr, writer.toString());
			} catch (SExpressionParseException e) {
				e.printStackTrace();
				fail("unexpected exception");
			} catch (TreeWriteException e) {
				e.printStackTrace();
				fail("unexpected exception");
			}
		}
	}

}
