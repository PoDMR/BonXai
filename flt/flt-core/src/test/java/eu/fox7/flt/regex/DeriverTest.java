/**
 * Created on Apr 3, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.regex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import eu.fox7.flt.regex.NoRegularExpressionDefinedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.factories.Deriver;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.flt.regex.matchers.Matcher;
import eu.fox7.util.AlphabetIterator;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;
import eu.fox7.util.tree.selectors.PositionalNodeSelector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class DeriverTest extends TestCase {

	public static Test suite() {
		return new TestSuite(DeriverTest.class);
	}

	public void testExpr01() {
		final String regexStr = "(. (a) (| (b) (c)))";
		final String symbol1 = "a";
		final String symbol2 = "c";
		final String symbol3 = "d";
		final String targetStr1 = regexStr;
		final String targetStr2 = "(EMPTY)";
		final String targetStr3 = "(EMPTY)";
		Deriver deriver = new Deriver();
		try {
			Regex regex = deriver.derive(new Regex(regexStr), symbol1);
			assertEquals(symbol1, targetStr1, regex.toString());
			regex = deriver.derive(new Regex(regexStr), symbol2);
			assertEquals(symbol2, targetStr2, regex.toString());
			regex = deriver.derive(new Regex(regexStr), symbol3);
			assertEquals(symbol3, targetStr3, regex.toString());
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void testExpr02() {
		final String regexStr = "(. (| (a) (b) (c)) (d))";
		final String symbol1 = "a";
		final String symbol2 = "c";
		final String symbol3 = "d";
		final String targetStr1 = "(. (a) (d))";
		final String targetStr2 = "(. (c) (d))";
		final String targetStr3 = "(EMPTY)";
		Deriver deriver = new Deriver();
		try {
			Regex regex = deriver.derive(new Regex(regexStr), symbol1);
			assertEquals(symbol1, targetStr1, regex.toString());
			regex = deriver.derive(new Regex(regexStr), symbol2);
			assertEquals(symbol2, targetStr2, regex.toString());
			regex = deriver.derive(new Regex(regexStr), symbol3);
			assertEquals(symbol3, targetStr3, regex.toString());
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testExpr03() {
		final String regexStr = "(. (? (a)) (b))";
		final String symbol1 = "a";
		final String symbol2 = "b";
		final String symbol3 = "c";
		final String targetStr1 = "(. (a) (b))";
		final String targetStr2 = "(b)";
		final String targetStr3 = "(EMPTY)";
		Deriver deriver = new Deriver();
		try {
			Regex regex = deriver.derive(new Regex(regexStr), symbol1);
			assertEquals(symbol1, targetStr1, regex.toString());
			regex = deriver.derive(new Regex(regexStr), symbol2);
			assertEquals(symbol2, targetStr2, regex.toString());
			regex = deriver.derive(new Regex(regexStr), symbol3);
			assertEquals(symbol3, targetStr3, regex.toString());
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testSubexpressions() {
		final String regexStr = "(| (. (? (a)) (b)) (+ (| (c) (d))) (. (a) (? (b))) (. (* (a)) (b)))";
		try {
			Tree tree = Tree.parse(regexStr);
			Deriver deriver = new Deriver(new Regex());
			PositionalNodeSelector selector = new PositionalNodeSelector();
			Node node = selector.select(tree, 0);
			Node result = deriver.derive("a", node);
			assertEquals("first to a", "(. (a) (b))", result.toSExpression());
			node = selector.select(tree, 1);
			result = deriver.derive("a", node);
			assertEquals("second to a", "(EMPTY)", result.toSExpression());
			node = selector.select(tree, 2);
			result = deriver.derive("a", node);
			assertEquals("third to a", "(. (a) (? (b)))", result.toSExpression());
			node = selector.select(tree, 3);
			result = deriver.derive("a", node);
			assertEquals("fourth to a", "(. (a) (. (* (a)) (b)))", result.toSExpression());
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	protected Set<String> computeAlphabet(int n) {
		Set<String> alphabet = new HashSet<String>();
		AlphabetIterator it = new AlphabetIterator();
		for (int i = 0; i < n; i++)
			alphabet.add(it.next());
		return alphabet;
	}

}
