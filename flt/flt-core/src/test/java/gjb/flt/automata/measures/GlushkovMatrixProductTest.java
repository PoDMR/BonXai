/**
 * Created on Aug 13, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.factories.dense.GlushkovBigIntegerMatrixProductFactory;
import gjb.flt.automata.factories.dense.GlushkovBigIntegerMatrixRepresentationFactory;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.StringCount;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

import java.math.BigInteger;

import junit.framework.TestCase;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class GlushkovMatrixProductTest extends TestCase {

	protected GlushkovBigIntegerMatrixRepresentationFactory matrixFactory = new GlushkovBigIntegerMatrixRepresentationFactory();

	public void testProduct01() {
		final String regexStr1 = "(. (a) (a) (* (b)))";
		final String regexStr2 = "(. (* (a)) (b) (b))";
		final double[] intersectionExpected = {0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0};
		final double[] unionExpected =        {0.0, 0.0, 2.0, 4.0, 5.0, 7.0, 9.0, 11.0};
		final double[] exclusive1Expected =   {0.0, 0.0, 1.0, 2.0, 2.0, 3.0, 4.0, 5.0};
		final double[] exclusive2Expected =   {0.0, 0.0, 1.0, 2.0, 2.0, 3.0, 4.0, 5.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
				 exclusive1Expected, exclusive2Expected);
	}

	public void testProduct02() {
		final String regexStr1 = "(. (a) (+ (| (b) (c))))";
		final String regexStr2 = "(. (a) (+ (| (b) (d))))";
		final double[] intersectionExpected = {0.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
		final double[] unionExpected =        {0.0, 0.0, 3.0, 10.0, 25.0, 56.0, 119.0, 246.0};
		final double[] exclusive1Expected =   {0.0, 0.0, 1.0, 4.0, 11.0, 26.0, 57.0, 120.0};
		final double[] exclusive2Expected =   {0.0, 0.0, 1.0, 4.0, 11.0, 26.0, 57.0, 120.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
				 exclusive1Expected, exclusive2Expected);
	}

	public void testProduct03() {
		final String regexStr1 = "(. (* (| (a) (b))) (? (c)))";
		final String regexStr2 = "(+ (b))";
		final double[] intersectionExpected = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		final double[] unionExpected =        {1.0, 4.0, 10.0, 22.0, 46.0, 94.0, 190.0, 382.0};
		final double[] exclusive1Expected =   {1.0, 3.0, 8.0, 19.0, 42.0, 89.0, 184.0, 375.0};
		final double[] exclusive2Expected =   {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
				 exclusive1Expected, exclusive2Expected);
	}

	public void testProduct04() {
		final String regexStr1 = "(. (+ (a)) (+ (b)))";
		final String regexStr2 = "(. (+ (b)) (+ (a)))";
		final double[] intersectionExpected = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		final double[] unionExpected =        {0.0, 0.0, 2.0, 6.0, 12.0, 20.0, 30.0, 42.0};
		final double[] exclusive1Expected =   {0.0, 0.0, 1.0, 3.0, 6.0, 10.0, 15.0, 21.0};
		final double[] exclusive2Expected =   {0.0, 0.0, 1.0, 3.0, 6.0, 10.0, 15.0, 21.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
				 exclusive1Expected, exclusive2Expected);
	}
	
	public void testProduct05() {
		final String regexStr1 = "(EPSILON)";
		final String regexStr2 = "(EPSILON)";
		final double[] intersectionExpected = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		final double[] unionExpected =        {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		final double[] exclusive1Expected =   {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		final double[] exclusive2Expected =   {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
				 exclusive1Expected, exclusive2Expected);
	}
	
	public void testProduct06() {
		final String regexStr1 = "(+ (a))";
		final String regexStr2 = "(+ (a))";
		final double[] intersectionExpected = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		final double[] unionExpected =        {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		final double[] exclusive1Expected =   {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		final double[] exclusive2Expected =   {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
				 exclusive1Expected, exclusive2Expected);
	}
	
	public void testProduct07() {
		final String regexStr1 = "(EMPTY)";
		final String regexStr2 = "(+ (a))";
		final double[] intersectionExpected = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		final double[] unionExpected =        {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		final double[] exclusive1Expected =   {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		final double[] exclusive2Expected =   {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
			     exclusive1Expected, exclusive2Expected);
	}
	
	public void testProduct08() {
		final String regexStr1 = "(EPSILON)";
		final String regexStr2 = "(+ (a))";
		final double[] intersectionExpected = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		final double[] unionExpected =        {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
		final double[] exclusive1Expected =   {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		final double[] exclusive2Expected =   {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
		testCase(regexStr1, regexStr2, intersectionExpected, unionExpected,
				exclusive1Expected, exclusive2Expected);
	}
	
	protected void testCase(final String regexStr1, final String regexStr2,
	                        final double[] intersectionExpected,
	                        final double[] unionExpected,
	                        final double[] exclusive1Expected,
	                        final double[] exclusive2Expected) {
		GlushkovFactory glushkov = new GlushkovFactory();
		StringCount count = new StringCount();
		try {
			SparseNFA nfa1 = glushkov.create(regexStr1);
			SparseNFA nfa2 = glushkov.create(regexStr2);
			GlushkovMatrixRepresentation<BigInteger> m1 = matrixFactory.create(nfa1);
			GlushkovMatrixRepresentation<BigInteger> m2 = matrixFactory.create(nfa2);
			GlushkovBigIntegerMatrixProductFactory factory = new GlushkovBigIntegerMatrixProductFactory(m1, m2);
			GlushkovMatrixRepresentation<BigInteger> intersection = factory.getIntersection();
			for (int length = 0; length < intersectionExpected.length; length++)
				assertEquals("length = " + length,
				             intersectionExpected[length],
				             count.compute(intersection, length));
			GlushkovMatrixRepresentation<BigInteger> union = factory.getUnion();
			for (int length = 0; length < unionExpected.length; length++)
				assertEquals("length = " + length,
				             unionExpected[length],
				             count.compute(union, length));
			GlushkovMatrixRepresentation<BigInteger> exclusive1 = factory.getExclusiveFirst();
			for (int length = 0; length < exclusive1Expected.length; length++)
				assertEquals("length = " + length,
				             exclusive1Expected[length],
				             count.compute(exclusive1, length));
			GlushkovMatrixRepresentation<BigInteger> exclusive2 = factory.getExclusiveSecond();
			for (int length = 0; length < exclusive2Expected.length; length++)
				assertEquals("length = " + length,
				             exclusive2Expected[length],
				             count.compute(exclusive2, length));
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
	
}
