/**
 * Created on Jul 9, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.dense.GlushkovBigIntegerMatrixRepresentationFactory;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.AmbiguityTest;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class LanguageTestTest extends TestCase {

	protected GlushkovBigIntegerMatrixRepresentationFactory factory = new GlushkovBigIntegerMatrixRepresentationFactory();

    public static Test suite() {
        return new TestSuite(LanguageTestTest.class);
    }

	public void test_ambiguity01() {
		String[] regexStrs = {
			"(. (a) (| (b) (c)))",
			"(. (a) (| (a) (b)))",
			"(. (? (a)) (| (a) (b)))",
			"(. (a) (| (a) (a)))",
			"(. (+ (a)) (a))",
			"(. (a) (+ (a)))",
			"(. (? (a)) (? (a)))",
			"(+ (. (a) (? (b)) (a)))",
			"(+ (. (a) (a)))",
			"(+ (. (a) (? (a))))"
		};
		boolean[] targetAmbiguity = {
				false, false, true, true, true, false, true, false, false, true
		};
		GlushkovFactory glushkov = new GlushkovFactory();
		AmbiguityTest<BigInteger> ambiguityTest = new AmbiguityTest<BigInteger>();
		try {
			for (int i = 0; i < regexStrs.length; i++) {
				SparseNFA nfa = glushkov.create(regexStrs[i]);
				assertEquals("NFA: " + regexStrs[i], targetAmbiguity[i], ambiguityTest.test(nfa));
				GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
				assertEquals("matrix: " + regexStrs[i], targetAmbiguity[i], ambiguityTest.test(m));
			}
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void test_ambiguity02() {
		final String fileName = "test-data/mixed-regexes.txt";
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
			GlushkovFactory glushkov = new GlushkovFactory();
			AmbiguityTest<BigInteger> ambiguityTest = new AmbiguityTest<BigInteger>();
			String line = null;
			while ((line = reader.readLine()) != null) {
				String regexStr = line.trim();
				if (regexStr.length() > 0) {
					SparseNFA nfa = glushkov.create(regexStr);
					GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
					assertEquals(regexStr + " at " + reader.getLineNumber(),
							     ambiguityTest.test(nfa), ambiguityTest.test(m));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SExpressionParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}
}
