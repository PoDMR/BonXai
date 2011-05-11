/**
 * Created on Apr 3, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.regex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.factories.Deriver;
import gjb.flt.regex.generators.LanguageGenerator;
import gjb.flt.regex.matchers.Matcher;
import gjb.util.AlphabetIterator;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;
import gjb.util.tree.selectors.PositionalNodeSelector;
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

	public void testLotsOfExprs() {
		try {
			final String fileName = "test-data/regexes-15.txt";
			final int nrExprs = 20;
			Set<String> alphabet = computeAlphabet(15);
			final int nrExamples = 100;
			Deriver deriver = new Deriver();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			int counter = 0;
			while ((line = reader.readLine()) != null) {
				String regexStr = line.trim();
				LanguageGenerator genRegex = new LanguageGenerator(regexStr);
				Matcher matcher = new Matcher(regexStr);
				for (String symbol : alphabet) {
					Regex derivative = deriver.derive(new Regex(regexStr), symbol);
					Matcher derivMatcher = new Matcher(derivative);
					for (int i = 0; i < nrExamples; i++) {
						List<String> example = genRegex.generateRandomExample();
						String exampleStr = StringUtils.join(example.iterator(), " ");
						if (example.size() > 0 && example.get(0).equals(symbol))
							assertTrue(derivative + " on " + exampleStr,
							           derivMatcher.matches(example.toArray(new String[0])));
						else
							assertFalse(derivative + " on " + exampleStr,
							            derivMatcher.matches(example.toArray(new String[0])));
					}
					if (!derivative.toString().equals("(" + genRegex.getRegex().emptySymbol() + ")")) {
						LanguageGenerator genDeriv = new LanguageGenerator(derivative.toString());
						for (int i = 0; i < nrExamples; i++) {
							List<String> example = genDeriv.generateRandomExample();
							String exampleStr = StringUtils.join(example.iterator(), " ");
							assertTrue(genRegex + " on " + exampleStr,
									   matcher.matches(example.toArray(new String[0])));
						}
					}
				}
				if (++counter > nrExprs)
					break;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (NoRegularExpressionDefinedException e) {
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
