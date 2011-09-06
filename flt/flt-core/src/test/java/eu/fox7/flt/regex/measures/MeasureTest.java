/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.regex.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.measures.ChainTest;
import eu.fox7.flt.regex.measures.LanguageMeasure;
import eu.fox7.flt.regex.measures.SingleOccurrenceTest;
import eu.fox7.flt.regex.measures.SymbolsPerState;
import eu.fox7.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class MeasureTest extends TestCase {

    protected Regex regex = new Regex();
    
    public static Test suite() {
        return new TestSuite(MeasureTest.class);
    }

    public void test_nonSingleOccurrence() {
        final String regexStr = "(. (a) (b) (| (a) (c)))";
        try {
            SingleOccurrenceTest test = new SingleOccurrenceTest();
            assertFalse(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void test_singleOccurrence() {
        final String regexStr = "(. (a) (b) (| (c) (d)))";
        try {
            SingleOccurrenceTest test = new SingleOccurrenceTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_singleOccurrenceEpsilon() {
        final String regexStr = "(EPSILON)";
        try {
            SingleOccurrenceTest test = new SingleOccurrenceTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void test_chain_01() {
        final String regexStr = "(. (? (a)) (? (| (b) (c))) (d) (| (e) (f)))";
        try {
        ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void test_chain_02() {
        final String regexStr = "(. (? (a)) (? (b)))";
        try {
            ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_chain_03() {
        final String regexStr = "(? (a))";
        try {
            ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_chain_04() {
        final String regexStr = "(EPSILON)";
        try {
            ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_chain_05() {
        final String regexStr = "(| (a) (b) (c))";
        try {
            ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_chain_06() {
        final String regexStr = "(* (| (a) (b) (c)))";
        try {
            ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_chain_07() {
        final String regexStr = "(a)";
        try {
            ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_chain_08() {
        final String regexStr = "(. (a) (b))";
        try {
            ChainTest test = new ChainTest();
            assertTrue(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_non_chain_01() {
        final String regexStr = "(. (| (? (a)) (b)) (c))";
        try {
            ChainTest test = new ChainTest();
            assertFalse(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_non_chain_02() {
        final String regexStr = "(. (| (. (a) (b)) (c)) (d))";
        try {
            ChainTest test = new ChainTest();
            assertFalse(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_non_chain_03() {
        final String regexStr = "(| (. (a) (b)) (c))";
        try {
            ChainTest test = new ChainTest();
            assertFalse(regexStr, test.test(regexStr));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void test_symbols_per_state_01() {
        final String regexStr = "(| (. (a) (b)) (c))";
        final double expected = 1.0;
        try {
            LanguageMeasure measure = new SymbolsPerState();
            double result = measure.compute(regexStr);
            assertTrue(regexStr, Math.abs(result - expected) < 1.0e-6);
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

    public void test_symbols_per_state_02() {
        final String regexStr = "(| (. (a) (b)) (b))";
        final double expected = 1.5;
        try {
            LanguageMeasure measure = new SymbolsPerState();
            double result = measure.compute(regexStr);
            assertTrue(regexStr, Math.abs(result - expected) < 1.0e-6);
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
    
    public void test_symbols_per_state_03() {
        final String regexStr = "(" + regex.epsilonSymbol() + ")";
        final double expected = 0.0;
        try {
            LanguageMeasure measure = new SymbolsPerState();
            double result = measure.compute(regexStr);
            assertTrue(regexStr, Math.abs(result - expected) < 1.0e-6);
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
    
    public void test_symbols_per_state_04() {
        final String regexStr = "(" + regex.emptySymbol() + ")";
        final double expected = 0.0;
        try {
            LanguageMeasure measure = new SymbolsPerState();
            double result = measure.compute(regexStr);
            assertTrue(regexStr, Math.abs(result - expected) < 1.0e-6);
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
    
    public void test_symbols_per_state_05() {
        final String regexStr = "(. (| (a) (b)) (b) (c) (* (a)) (+ (| (? (a)) (c))))";
        final double expected = 7.0/3.0;
        try {
            LanguageMeasure measure = new SymbolsPerState();
            double result = measure.compute(regexStr);
            assertTrue(regexStr, Math.abs(result - expected) < 1.0e-6);
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
