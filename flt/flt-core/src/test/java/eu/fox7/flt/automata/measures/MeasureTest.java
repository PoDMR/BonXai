/**
 * Created on Jun 10, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.dense.GlushkovBigIntegerMatrixModifier;
import eu.fox7.flt.automata.factories.dense.GlushkovBigIntegerMatrixRepresentationFactory;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.automata.measures.ApproximateStringCount;
import eu.fox7.flt.automata.measures.GlushkovMatrixMeasure;
import eu.fox7.flt.automata.measures.LanguageMeasure;
import eu.fox7.flt.automata.measures.SampleSupport;
import eu.fox7.flt.automata.measures.StringCount;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class MeasureTest extends TestCase {

	protected GlushkovBigIntegerMatrixRepresentationFactory factory = new GlushkovBigIntegerMatrixRepresentationFactory();
	protected GlushkovFactory glushkovFactory = new GlushkovFactory();

    public static Test suite() {
        return new TestSuite(MeasureTest.class);
    }

    public void test_simpleExpression() {
    	final String regexStr = "(* (. (a) (| (b) (c))))";
        try {
			SparseNFA nfa = glushkovFactory.create(regexStr);
			LanguageMeasure<BigInteger> stringCount = new StringCount();
			assertEquals("count", BigInteger.valueOf(31), stringCount.compute(nfa));
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

    public void test_smallLanguage1() {
    	final String regexStr = "(* (. (a) (| (b) (c))))";
        try {
			SparseNFA nfa = glushkovFactory.create(regexStr);
			StringCount stringCount = new StringCount();
			assertEquals("count", BigInteger.valueOf(1), stringCount.compute(nfa, 0));
			assertEquals("count", BigInteger.valueOf(1), stringCount.compute(nfa, 1));
			assertEquals("count", BigInteger.valueOf(3), stringCount.compute(nfa, 2));
			assertEquals("count", BigInteger.valueOf(3), stringCount.compute(nfa, 3));
			assertEquals("count", BigInteger.valueOf(7), stringCount.compute(nfa, 4));
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

    public void test_smallLanguage2() {
    	final String regexStr = "(? (. (a) (? (. (a) (? (a))))))";
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		StringCount stringCount = new StringCount();
    		assertEquals("count", BigInteger.valueOf(1), stringCount.compute(nfa, 0));
    		assertEquals("count", BigInteger.valueOf(2), stringCount.compute(nfa, 1));
    		assertEquals("count", BigInteger.valueOf(3), stringCount.compute(nfa, 2));
    		assertEquals("count", BigInteger.valueOf(4), stringCount.compute(nfa, 3));
    		assertEquals("count", BigInteger.valueOf(4), stringCount.compute(nfa, 4));
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
    
    public void test_smallLanguage3() {
    	final String regexStr = "(? (. (a) (? (. (| (b) (c)) (? (| (d) (e) (f)))))))";
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		StringCount stringCount = new StringCount();
    		assertEquals("count", BigInteger.valueOf(1), stringCount.compute(nfa, 0));
    		assertEquals("count", BigInteger.valueOf(2), stringCount.compute(nfa, 1));
    		assertEquals("count", BigInteger.valueOf(4), stringCount.compute(nfa, 2));
    		assertEquals("count", BigInteger.valueOf(10), stringCount.compute(nfa, 3));
    		assertEquals("count", BigInteger.valueOf(10), stringCount.compute(nfa, 4));
    		assertEquals("count", BigInteger.valueOf(10), stringCount.compute(nfa, 5));
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

    public void test_approximateStringCount() {
    	final String regexStr = "(? (. (a) (? (. (| (b) (c)) (? (| (d) (e) (f)))))))";
    	final int maxLength = 10;
    	StringCount stringCount = new StringCount();
    	ApproximateStringCount approxStringCount = new ApproximateStringCount();
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		for (int length = 0; length < maxLength; length++)
    			assertEquals("count for " + length,
    			             stringCount.compute(nfa, length).doubleValue(),
    			             approxStringCount.compute(nfa, length), 0.001);
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
    
    public void test_emptyExpression() {
    	final String regexStr = "(EMPTY)";
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		StringCount stringCount = new StringCount();
    		for (int l = 0; l <= 5; l++)
    			assertEquals("count " + l,
    			             BigInteger.ZERO, stringCount.compute(nfa, l));
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

    public void test_epsilonExpression() {
    	final String regexStr = "(EPSILON)";
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		StringCount stringCount = new StringCount();
    		for (int l = 0; l <= 5; l++)
    			assertEquals("count " + l,
    			             BigInteger.ONE, stringCount.compute(nfa, l));
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
    
    public void test_symbolExpression() {
    	final String regexStr = "(a)";
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		StringCount stringCount = new StringCount();
    		assertEquals("count 0",
    		             BigInteger.ZERO,
    		             stringCount.compute(nfa, 0));
    		for (int l = 1; l <= 5; l++)
    			assertEquals("count " + l,
    			             BigInteger.ONE,
    			             stringCount.compute(nfa, l));
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
    
    public void test_alphabetRestriction01() {
    	final String regexStr = "(* (. (a) (| (b) (c))))";
    	GlushkovBigIntegerMatrixModifier restrictor  = new GlushkovBigIntegerMatrixModifier();
    	GlushkovMatrixMeasure<BigInteger> stringCount = new StringCount();
    	Set<String> subAlphabet = new HashSet<String>();
    	subAlphabet.add("a");
    	subAlphabet.add("b");
		try {
			SparseNFA nfa = glushkovFactory.create(regexStr);
			GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
			GlushkovMatrixRepresentation<BigInteger> mR = restrictor.restrictAlphabet(m, subAlphabet);
			assertEquals("count", BigInteger.valueOf(5), stringCount.compute(mR));
			subAlphabet.remove("b");
			mR = restrictor.restrictAlphabet(m, subAlphabet);
			assertEquals("count", BigInteger.valueOf(1), stringCount.compute(mR));
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

    public void test_alphabetRestriction02() {
    	final String regexStr = "(. (a) (* (| (a) (b))))";
    	GlushkovBigIntegerMatrixModifier restrictor  = new GlushkovBigIntegerMatrixModifier();
    	GlushkovMatrixMeasure<BigInteger> stringCount = new StringCount();
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
        	Set<String> subAlphabet = new HashSet<String>();
        	subAlphabet.add("a");
    		GlushkovMatrixRepresentation<BigInteger> mR = restrictor.restrictAlphabet(m, subAlphabet);
    		assertEquals("count", BigInteger.valueOf(8), stringCount.compute(mR));
        	subAlphabet.add("b");
    		subAlphabet.remove("a");
    		mR = restrictor.restrictAlphabet(m, subAlphabet);
    		assertEquals("count", BigInteger.valueOf(0), stringCount.compute(mR));
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
    
    public void test_weights01() {
    	final String regexStr = "(. (a) (| (a) (b)))";
    	GlushkovBigIntegerMatrixModifier restrictor  = new GlushkovBigIntegerMatrixModifier();
    	GlushkovMatrixMeasure<BigInteger> stringCount = new StringCount();
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
    		Map<String,BigInteger> weights = new HashMap<String,BigInteger>();
    		weights.put("a", BigInteger.valueOf(2));
    		weights.put("b", BigInteger.valueOf(3));
    		GlushkovMatrixRepresentation<BigInteger> mR = restrictor.addWeights(m, weights);
    		assertEquals("count", BigInteger.valueOf(10), stringCount.compute(mR));
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
    
    public void test_weights02() {
    	final String regexStr = "(. (| (a) (b)) (+ (c)))";
    	GlushkovBigIntegerMatrixModifier restrictor  = new GlushkovBigIntegerMatrixModifier();
    	StringCount stringCount = new StringCount();
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
    		Map<String,BigInteger> weights = new HashMap<String,BigInteger>();
    		weights.put("a", BigInteger.valueOf(2));
    		weights.put("b", BigInteger.valueOf(3));
    		weights.put("c", BigInteger.valueOf(5));
    		GlushkovMatrixRepresentation<BigInteger> mR = restrictor.addWeights(m, weights);
    		assertEquals("count", BigInteger.valueOf(150), stringCount.compute(mR, 3));
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
    
    public void test_weights03() {
    	final String regexStr = "(. (| (a) (b)) (* (c)))";
    	GlushkovBigIntegerMatrixModifier restrictor  = new GlushkovBigIntegerMatrixModifier();
    	StringCount stringCount = new StringCount();
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
    		Map<String,BigInteger> weights = new HashMap<String,BigInteger>();
    		weights.put("a", BigInteger.valueOf(2));
    		weights.put("b", BigInteger.valueOf(3));
    		weights.put("c", BigInteger.valueOf(5));
    		GlushkovMatrixRepresentation<BigInteger> mR = restrictor.addWeights(m, weights);
    		assertEquals("count", BigInteger.valueOf(155), stringCount.compute(mR, 3));
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
    
    public void test_weights04() {
    	final String regexStr = "(. (? (| (a) (b))) (* (c)))";
    	GlushkovBigIntegerMatrixModifier restrictor  = new GlushkovBigIntegerMatrixModifier();
    	StringCount stringCount = new StringCount();
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
    		Map<String,BigInteger> weights = new HashMap<String,BigInteger>();
    		weights.put("a", BigInteger.valueOf(2));
    		weights.put("b", BigInteger.valueOf(3));
    		weights.put("c", BigInteger.valueOf(5));
    		GlushkovMatrixRepresentation<BigInteger> mR = restrictor.addWeights(m, weights);
    		assertEquals("count", BigInteger.valueOf(311), stringCount.compute(mR, 3));
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
    
    public void test_weights05() {
    	final String regexStr = "(. (* (a)) (* (b)))";
    	GlushkovBigIntegerMatrixModifier restrictor  = new GlushkovBigIntegerMatrixModifier();
    	StringCount stringCount = new StringCount();
    	try {
    		SparseNFA nfa = glushkovFactory.create(regexStr);
    		GlushkovMatrixRepresentation<BigInteger> m = factory.create(nfa);
    		Map<String,BigInteger> weights = new HashMap<String,BigInteger>();
    		weights.put("a", BigInteger.valueOf(6));
    		weights.put("b", BigInteger.valueOf(1));
    		GlushkovMatrixRepresentation<BigInteger> mR = restrictor.addWeights(m, weights);
    		assertEquals("count", BigInteger.valueOf(8), stringCount.compute(mR, 1));
    		assertEquals("count", BigInteger.valueOf(51), stringCount.compute(mR, 2));
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
    
    public void test_support() {
        try {
            String[][] strs = {
                    {"a", "d"},
                    {"a", "d"},
                    {"b", "d"},
                    {"b", "b", "d"},
                    {"b", "b", "b", "b", "d"},
                    {"b", "b", "d"}
            };
            String regexStr = "(. (| (a) (. (+ (b)) (? (c)))) (d))";
            SparseNFA nfa = glushkovFactory.create(regexStr);
            SampleSupport supportMeasure = new SampleSupport(nfa);
            for (Transition transition : nfa.getTransitionMap().getTransitions()) {
                assertEquals("no support for " + transition.toString(),
                             0, supportMeasure.getSupport(transition));
            }
            Map<Transition,Integer> expected = new HashMap<Transition,Integer>();
            expected.put(new Transition(Symbol.create("a"),
                                        nfa.getInitialState(),
                                        nfa.getState("a_1")),
                                        2);
            expected.put(new Transition(Symbol.create("d"),
                                        nfa.getState("a_1"),
                                        nfa.getState("d_4")),
                                        2);
            expected.put(new Transition(Symbol.create("b"),
                                        nfa.getInitialState(),
                                        nfa.getState("b_2")),
                                        4);
            expected.put(new Transition(Symbol.create("b"),
                                        nfa.getState("b_2"),
                                        nfa.getState("b_2")),
                                        3);
            expected.put(new Transition(Symbol.create("d"),
                                        nfa.getState("b_2"),
                                        nfa.getState("d_4")),
                                        4);
            supportMeasure.compute(strs);
            for (Transition transition : nfa.getTransitionMap().getTransitions()) {
                if (expected.containsKey(transition))
                    assertEquals("support for " + transition.toString(),
                                 expected.get(transition).intValue(),
                                 supportMeasure.getSupport(transition));
                else
                    assertEquals("support for " + transition.toString(),
                                 0,
                                 supportMeasure.getSupport(transition));
            }
            assertEquals("total support", strs.length, supportMeasure.getTotalSupport());
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_visits() {
        try {
            String[][] strs = {
                    {"a", "d"},
                    {"a", "d"},
                    {"b", "d"},
                    {"b", "b", "d"},
                    {"b", "b", "b", "b", "d"},
                    {"b", "b", "d"}
            };
            String regexStr = "(. (| (a) (. (+ (b)) (? (c)))) (d))";
            SparseNFA nfa = glushkovFactory.create(regexStr);
            SampleSupport supportMeasure = new SampleSupport(nfa);
            for (Transition transition : nfa.getTransitionMap().getTransitions()) {
                assertEquals("no visits for " + transition.toString(),
                             0, supportMeasure.getVisits(transition));
            }
            Map<Transition,Integer> expected = new HashMap<Transition,Integer>();
            expected.put(new Transition(Symbol.create("a"),
                                        nfa.getInitialState(),
                                        nfa.getState("a_1")),
                                        2);
            expected.put(new Transition(Symbol.create("d"),
                                        nfa.getState("a_1"),
                                        nfa.getState("d_4")),
                                        2);
            expected.put(new Transition(Symbol.create("b"),
                                        nfa.getInitialState(),
                                        nfa.getState("b_2")),
                                        4);
            expected.put(new Transition(Symbol.create("b"),
                                        nfa.getState("b_2"),
                                        nfa.getState("b_2")),
                                        5);
            expected.put(new Transition(Symbol.create("d"),
                                        nfa.getState("b_2"),
                                        nfa.getState("d_4")),
                                        4);
            supportMeasure.compute(strs);
            for (Transition transition : nfa.getTransitionMap().getTransitions()) {
                assertEquals("no visits, no support for " + transition.toString(),
                             supportMeasure.getVisits(transition) == 0,
                             supportMeasure.getSupport(transition) == 0);
                if (expected.containsKey(transition))
                    assertEquals("support for " + transition.toString(),
                                 expected.get(transition).intValue(),
                                 supportMeasure.getVisits(transition));
                else
                    assertEquals("support for " + transition.toString(),
                                 0,
                                 supportMeasure.getVisits(transition));
            }
            assertEquals("total visits", 17, supportMeasure.getTotalVisits());
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

}
