/*
 * Created on Jul 5, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.regex;

import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.RegexException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.converters.Simplifier;
import gjb.flt.regex.converters.Sorter;
import gjb.flt.regex.converters.ZeroOrMoreEliminator;
import gjb.util.tree.SExpressionParseException;

import java.util.Properties;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TransformationTest extends TestCase {

    public static Test suite() {
        return new TestSuite(TransformationTest.class);
    }

    public void test_simplify_1() {
		final String regexStr = "(| ({2,4} (. (b) (a))) ({1,3} (. (b) (a))) ({2,4} (. (a) (b))) ({1,7} (. (b) (a))))";
		final String targetStr = regexStr;
		try {
			Simplifier simplifier = new Simplifier();
			String simplifiedStr = simplifier.simplify(regexStr);
			assertEquals(targetStr, simplifiedStr);
		} catch (TransformerConfigurationException e) {
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
		} catch (RegexException e) {
			e.printStackTrace();
			fail("unexpected exception");
        }
	}

    public void test_simplify_2() {
    	final String regexStr = "(. ({2,4} (a)))";
    	final String targetStr = "({2,4} (a))";
    	try {
    		Simplifier simplifier = new Simplifier();
    		String simplifiedStr = simplifier.simplify(regexStr);
    		assertEquals(targetStr, simplifiedStr);
    	} catch (TransformerConfigurationException e) {
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
    	} catch (RegexException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
        }
    }
 
    public void test_simplify_3() {
    	final String regexStr = "(| (a))";
    	final String targetStr = "(a)";
    	try {
    		Simplifier simplifier = new Simplifier();
    		String simplifiedStr = simplifier.simplify(regexStr);
    		assertEquals(targetStr, simplifiedStr);
    	} catch (TransformerConfigurationException e) {
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
    	} catch (RegexException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
        }
    }
    
    public void test_allOperator() {
    	final String regexStr = "(| ({2,4} (% (b) (a))) ({1,3} (. (b) (a))) ({2,4} (. (a) (b))) ({1,7} (. (b) (a))))";
    	final String targetStr = regexStr;
    	try {
    		Simplifier simplifier = new Simplifier();
    		Properties properties = new Properties();
    		properties.setProperty("interleave", "%");
    		Regex regex = new Regex(regexStr, properties);
    		String simplifiedStr = simplifier.simplify(regex);
			assertEquals(targetStr, simplifiedStr);
    	} catch (TransformerConfigurationException e) {
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
    	} catch (RegexException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
        }
    }
    
	public void test_zeroOrMoreElimination1() {
        String regexStr = "(. (* (a)) (| (b) (? (a)) (* (c))) (? (* (c))))";
        String targetStr = "(. (? (+ (a))) (| (b) (? (a)) (? (+ (c)))) (? (? (+ (c)))))";
        try {
            ZeroOrMoreEliminator eliminator = new ZeroOrMoreEliminator();
            String eliminatedStr = eliminator.eliminate(regexStr);
            assertEquals("* elimination", targetStr, eliminatedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_zeroOrMoreElimination2() {
        String regexStr = "(. (| (a) (? (b))))";
        String targetStr = "(. (| (a) (? (b))))";
        try {
            ZeroOrMoreEliminator eliminator = new ZeroOrMoreEliminator();
            String eliminatedStr = eliminator.eliminate(regexStr);
            assertEquals("* elimination", targetStr, eliminatedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_concatOptionalsRepeatSimplification() {
        String regexStr = "(+ (| (. (? (a)) (? (b))) (c)))";
        String targetStr = "(? (+ (| (a) (b) (c))))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_concatOptionalsRepeatSimplification2() {
        String regexStr = "(+ (| (. (+ (| (. (a) (? (b))) (c))) (+ (| (. (d) (e)) (. (? (f)) (? (g)))))) (h)))";
        String targetStr = "(+ (| (. (| (. (a) (? (b))) (c)) (? (+ (| (. (d) (e)) (f) (g))))) (h)))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_concatOptionalsRepeatSimplification3() {
        String regexStr = "(+ (. (? (a)) (? (+ (b)))))";
        String targetStr = "(? (+ (| (a) (b))))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_concatOptionalsRepeatSimplification4() {
        String regexStr = "(+ (. (a) (? (+ (b)))))";
        String targetStr = "(+ (. (a) (? (+ (b)))))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_lift_multiplicity_in_union() {
        String regexStr = "(+ (| (a) (+ (b))))";
        String targetStr = "(+ (| (a) (b)))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_lift_multiplicity_in_concat1() {
        String regexStr = "(+ (. (? (a)) (+ (b))))";
        String targetStr = "(+ (. (? (a)) (b)))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_lift_multiplicity_in_concat2() {
        String regexStr = "(+ (. (? (a)) (+ (b)) (? (c)) (? (d))))";
        String targetStr = "(+ (. (? (a)) (b) (? (c)) (? (d))))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_lift_multiplicity_in_concat3() {
        String regexStr = "(| (+ (. (? (a)) (+ (b)) (? (c)) (? (d)))) (e))";
        String targetStr = "(| (+ (. (? (a)) (b) (? (c)) (? (d)))) (e))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_lift_multiplicity_in_concat4() {
        String regexStr = "(+ (. (? (a))  (b) (? (c)) (? (d))))";
        String targetStr = "(+ (. (? (a)) (b) (? (c)) (? (d))))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_lift_multiplicity_in_concat_in_union3() {
        String regexStr = "(+ (| (. (? (a)) (+ (b)) (? (c)) (? (d))) (e)))";
        String targetStr = "(+ (| (. (? (a)) (b) (? (c)) (? (d))) (e)))";
        try {
            Simplifier simplifier = new Simplifier();
            String simplifiedStr = simplifier.simplify(regexStr);
            assertEquals("simplification", targetStr, simplifiedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_sort1() {
        String regexStr = "(| (b) (c) (a))";
        String targetStr = "(| (a) (b) (c))";
        try {
            Sorter sorter = new Sorter();
            String sortedStr = sorter.sort(regexStr);
            assertEquals("simplification", targetStr, sortedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_sort2() {
        String regexStr = "(| (b) (? (c)) (a))";
        String targetStr = "(| (a) (b) (? (c)))";
        try {
            Sorter sorter = new Sorter();
            String sortedStr = sorter.sort(regexStr);
            assertEquals("simplification", targetStr, sortedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_sort3() {
        String regexStr = "(| (. (b) (d)) (? (c)) (a))";
        String targetStr = "(| (a) (. (b) (d)) (? (c)))";
        try {
            Sorter sorter = new Sorter();
            String sortedStr = sorter.sort(regexStr);
            assertEquals("simplification", targetStr, sortedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_sort4() {
        String regexStr = "(| (. (d) (b)) (? (c)) (a))";
        String targetStr = "(| (a) (? (c)) (. (d) (b)))";
        try {
            Sorter sorter = new Sorter();
            String sortedStr = sorter.sort(regexStr);
            assertEquals("simplification", targetStr, sortedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }
    
    public void test_sort5() {
        String regexStr = "(? (. (e) (| (. (d) (b)) (? (c)) (a))))";
        String targetStr = "(? (. (e) (| (a) (? (c)) (. (d) (b)))))";
        try {
            Sorter sorter = new Sorter();
            String sortedStr = sorter.sort(regexStr);
            assertEquals("simplification", targetStr, sortedStr);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

}
