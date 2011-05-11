/*
 * Created on Jun 26, 2008
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata.matchers;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.matchers.DeterministicPartialMatcher;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.lang.StringUtils;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class DeterministicMatcherTest extends TestCase {

    public static Test suite() {
        return new TestSuite(DeterministicMatcherTest.class);
    }

    public void test_greedyExpr1() {
        final String regexStr = "(. (a) (+ (b)) (| (c) (d)) (e))";
        final String[] succStrs = {
                "abce",
                "abde",
                "abbde",
                "abbbbce",
                "abbceabce",
                "abdef"
        };
        final String[] succTails = {
                "",
                "",
                "",
                "",
                "abce",
                "f"
        };
        final String[] failedStrs = {
                "a",
                "abb",
                "abbd",
                "aa",
                "abcf",
                "abdde"
        };
        final String[] failedTails = {
                "",
                "",
                "",
                "a",
                "f",
                "de"
        };
        testExpression(regexStr, true,
                       succStrs, succTails, failedStrs, failedTails);
    }

    public void test_nonGreedyExpr1() {
        final String regexStr = "(. (a) (+ (b)) (| (c) (d)) (e))";
        final String[] succStrs = {
                "abce",
                "abde",
                "abbde",
                "abbbbce",
                "abbceabce",
                "abdef"
        };
        final String[] succTails = {
                "",
                "",
                "",
                "",
                "abce",
                "f"
        };
        final String[] failedStrs = {
                "a",
                "abb",
                "abbd",
                "aa",
                "abcf",
                "abdde"
        };
        final String[] failedTails = {
                "",
                "",
                "",
                "a",
                "f",
                "de"
        };
        testExpression(regexStr, false,
                       succStrs, succTails, failedStrs, failedTails);
    }

    public void test_greedyExpr2() {
        final String regexStr = "(. (a) (+ (b)))";
        final String[] succStrs = {
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "abdef"
        };
        final String[] succTails = {
                "",
                "",
                "de",
                "ce",
                "ceabce",
                "def"
        };
        final String[] failedStrs = {
                "a",
                "adbb",
                "fabbd",
                "aa"
        };
        final String[] failedTails = {
                "",
                "dbb",
                "fabbd",
                "a"
        };
        testExpression(regexStr, true,
                       succStrs, succTails, failedStrs, failedTails);
    }

    public void test_nonGreedyExpr2() {
        final String regexStr = "(. (a) (+ (b)))";
        final String[] succStrs = {
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "abdef"
        };
        final String[] succTails = {
                "",
                "bb",
                "bde",
                "bbbce",
                "bceabce",
                "def"
        };
        final String[] failedStrs = {
                "a",
                "adbb",
                "fabbd",
                "aa"
        };
        final String[] failedTails = {
                "",
                "dbb",
                "fabbd",
                "a"
        };
        testExpression(regexStr, false,
                       succStrs, succTails, failedStrs, failedTails);
    }
    
    public void test_greedyExpr3() {
        final String regexStr = "(? (. (a) (? (+ (b)))))";
        final String[] succStrs = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] succTails = {
                "",
                "",
                "",
                "",
                "de",
                "ce",
                "ceabce",
                "fabbd",
                "def",
                "def",
                "def"
        };
        final String[] failedStrs = {
        };
        final String[] failedTails = {
        };
        testExpression(regexStr, true,
                       succStrs, succTails, failedStrs, failedTails);
    }

    public void test_nonGreedyExpr3() {
        final String regexStr = "(? (. (a) (? (+ (b)))))";
        final String[] succStrs = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] succTails = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] failedStrs = {
        };
        final String[] failedTails = {
        };
        testExpression(regexStr, false,
                       succStrs, succTails, failedStrs, failedTails);
    }
    
    public void test_greedyEpsilon() {
        final String regexStr = "(EPSILON)";
        final String[] succStrs = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] succTails = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] failedStrs = {
        };
        final String[] failedTails = {
        };
        testExpression(regexStr, true,
                       succStrs, succTails, failedStrs, failedTails);
    }
    
    public void test_nonGreedyEpsilon() {
        final String regexStr = "(EPSILON)";
        final String[] succStrs = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] succTails = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] failedStrs = {
        };
        final String[] failedTails = {
        };
        testExpression(regexStr, false,
                       succStrs, succTails, failedStrs, failedTails);
    }
    
    public void test_greedyEmpty() {
        final String regexStr = "(EMPTY)";
        final String[] succStrs = {
        };
        final String[] succTails = {
        };
        final String[] failedStrs = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] failedTails = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        testExpression(regexStr, true,
                       succStrs, succTails, failedStrs, failedTails);
    }

    public void test_nonGreedyEmpty() {
        final String regexStr = "(EMPTY)";
        final String[] succStrs = {
        };
        final String[] succTails = {
        };
        final String[] failedStrs = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        final String[] failedTails = {
                "",
                "a",
                "ab",
                "abbb",
                "abbde",
                "abbbbce",
                "abbceabce",
                "fabbd",
                "abdef",
                "adef",
                "def"
        };
        testExpression(regexStr, false,
                       succStrs, succTails, failedStrs, failedTails);
    }
    
    public void test_nfa() {
        final String regexStr = "(| (. (? (a)) (b)) (a))";
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA nfa = glushkov.create(regexStr);
            new DeterministicPartialMatcher(nfa);
            fail("exception should be thrown");
        } catch (NotDFAException e) {
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

    protected void testExpression(final String regexStr, boolean isGreedy,
                                  final String[] succStrs,
                                  final String[] succTails,
                                  final String[] failedStrs,
                                  final String[] failedTails) {
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA nfa = glushkov.create(regexStr);
            DeterministicPartialMatcher matcher = new DeterministicPartialMatcher(nfa, isGreedy);
            for (int i = 0; i < succStrs.length; i++) {
                List<String> symbols = arrayToList(succStrs[i]);
                assertTrue("string " + i + " not matched, should",
                           matcher.match(symbols));
                assertEquals("succ tail for " + i,
                             succTails[i],
                             StringUtils.join(symbols.iterator(), ""));
            }
            for (int i = 0; i < failedStrs.length; i++) {
                List<String> symbols = arrayToList(failedStrs[i]);
                assertFalse("string " + i + " matched, should not",
                            matcher.match(symbols));
                assertEquals("fail tail for " + i,
                             failedTails[i],
                             StringUtils.join(symbols.iterator(), ""));
            }
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NotDFAException e) {
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

    protected List<String> arrayToList(String str) {
        List<String> list = new ArrayList<String>();
        for (String c : str.split(""))
            if (!c.isEmpty())
                list.add(c);
        return list;
    }
}
