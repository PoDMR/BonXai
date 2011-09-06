import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.matchers.NFAMatcher;
import eu.fox7.flt.regex.NoRegularExpressionDefinedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.converters.InfixConverter;
import eu.fox7.flt.regex.io.XMLReader;
import eu.fox7.flt.regex.io.XMLWriter;
import eu.fox7.flt.regex.measures.ShortestAcceptedStringMeasure;
import eu.fox7.util.tree.SExpressionParseException;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RegexTest extends TestCase {
  
	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public RegexTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(RegexTest.class);
	}

	public void test_regexParse() {
        ThompsonFactory regex = new ThompsonFactory();
		try {
			regex.create("(| (a) (. (b) (? (c))))");
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			regex.create("(| (a) (. b) (? (c))))");
			assertTrue(false);
		} catch (SExpressionParseException e) {
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			regex.create("(| (a) (@ (b) (? (c))))");
			assertTrue(false);
		} catch (UnknownOperatorException e) {
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void test_nonDefaultRegexParse() {
		Properties properties = new Properties();
		properties.setProperty("concat", ",");
        ThompsonFactory regex = new ThompsonFactory(properties);
		try {
			regex.create("(| (a) (, (b) (? (c))))");
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			regex.create("(| (a) (. (b) (? (c))))");
			assertTrue(false);
		} catch (UnknownOperatorException e) {
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void test_runRegex1() {
		Properties properties = new Properties();
		properties.setProperty("concat", ",");
        ThompsonFactory regex = new ThompsonFactory(properties);
		SparseNFA nfa = null;
		try {
			nfa = regex.create("(? (| (+ (b)) (, (a) (c))))");
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
        NFAMatcher matcher = new NFAMatcher(nfa);
		String[] input1 = {};
		assertTrue(matcher.matches(input1));
		String[] input2 = {"b", "b"};
		assertTrue(matcher.matches(input2));
		String[] input3 = {"a", "c"};
		assertTrue(matcher.matches(input3));
		String[] input4 = {"b", "b", "b", "c", "c"};
		assertTrue(!matcher.matches(input4));
		String[] input5 = {"a", "a", "a"};
		assertTrue(!matcher.matches(input5));
		String[] input6 = {"c", "a"};
		assertTrue(!matcher.matches(input6));
		String[] input7 = {"a"};
		assertTrue(!matcher.matches(input7));
		String[] input8 = {"c"};
		assertTrue(!matcher.matches(input8));
	}

	public void test_runRegex2() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("(| (epsilon) (a))");
    		NFAMatcher matcher = new NFAMatcher(nfa);
            String[] input1 = {};
            assertTrue(matcher.matches(input1));
            String[] input2 = {"a"};
            assertTrue(matcher.matches(input2));
            String[] input3 = {"a", "a"};
            assertTrue(!matcher.matches(input3));
        } catch (Exception e) {
            fail();
        }
	}
	
	public void test_matches1() throws Exception {
        eu.fox7.flt.regex.matchers.Matcher regex = new eu.fox7.flt.regex.matchers.Matcher("(* (. (a) (b)))");
        assertTrue("empty string match", regex.matches(new String[0]));
        String[] s1 = {"a", "b"};
        assertTrue("1 match", regex.matches(s1));
        String[] s2 = {"a", "b", "a", "b"};
        assertTrue("2 matches", regex.matches(s2));
        String[] s3 = {"a", "b", "a"};
        assertTrue("mismatch", !regex.matches(s3));
    }

    public void test_matches2() throws Exception {
	    eu.fox7.flt.regex.matchers.Matcher regex = new eu.fox7.flt.regex.matchers.Matcher("(* (. (a) (b)))");
	    assertTrue("empty string match", regex.matches(new String[0]));
	    String[] s1 = {"a", "b"};
	    assertTrue("1 match", regex.matches(s1));
	    String[] s2 = {"a", "b", "a", "b"};
	    assertTrue("2 matches", regex.matches(s2));
	    String[] s3 = {"a", "b", "a"};
	    assertTrue("mismatch", !regex.matches(s3));
	}

    public void test_shortestAcceptedStringLength1() throws Exception {
        final String regexStr = "(* (. (a) (b)))";
        ShortestAcceptedStringMeasure measure = new ShortestAcceptedStringMeasure(regexStr);
        assertEquals("shortest string length",
                     0, measure.shortestAcceptedStringLength());
    }

    public void test_shortestAcceptedStringLength2() throws Exception {
        final String regexStr = "(. (. (a) (b)) (* (c)))";
        ShortestAcceptedStringMeasure measure = new ShortestAcceptedStringMeasure(regexStr);
        assertEquals("shortest string length",
                     2, measure.shortestAcceptedStringLength());
    }

	public void test_interleave() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("(# (a) (b) (c))");
            String[][] ok = {
                    {"a", "b", "c"},
                    {"b", "c", "a"},
                    {"c", "a", "b"},
                    {"b", "a", "c"},
                    {"a", "c", "b"},
                    {"c", "b", "a"}};
    		NFAMatcher matcher = new NFAMatcher(nfa);
            for (int i = 0; i < ok.length; i++) {
                assertTrue(matcher.matches(ok[i]));
            }
            String[][] notOk = {
                    {"a", "b"},
                    {},
                    {"c"},
                    {"b", "c", "a", "a"}};
            for (int i = 0; i < notOk.length; i++) {
                assertTrue("accepted string: " + notOk[i], !matcher.matches(notOk[i]));
            }
        } catch (SExpressionParseException e) {
            fail();
        } catch (UnknownOperatorException e) {
            fail();
        }
	}
	
	public void test_2To5() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("({2,5} (a))");
            String[][] ok = {
                    {"a", "a"},
                    {"a", "a", "a"},
                    {"a", "a", "a", "a"},
                    {"a", "a", "a", "a", "a"}};
    		NFAMatcher matcher = new NFAMatcher(nfa);
            for (int i = 0; i < ok.length; i++) {
                assertTrue("rejected string: " + i, matcher.matches(ok[i]));
            }
            String[][] notOk = {
                    {},
                    {"a"},
                    {"a", "a", "a", "a", "a", "a"},
                    {"a", "a", "b"}};
            for (int i = 0; i < notOk.length; i++) {
                assertTrue(!matcher.matches(notOk[i]));
            }
        } catch (SExpressionParseException e) {
            fail();
        } catch (UnknownOperatorException e) {
            fail();
        }
	}
	
	public void test_2To() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("({2,} (a))");
            String[][] ok = {
                    {"a", "a"},
                    {"a", "a", "a"},
                    {"a", "a", "a", "a"},
                    {"a", "a", "a", "a", "a"},
                    {"a", "a", "a", "a", "a", "a"}};
            NFAMatcher matcher = new NFAMatcher(nfa);
            for (int i = 0; i < ok.length; i++) {
                assertTrue("rejected string: " + i, matcher.matches(ok[i]));
            }
            String[][] notOk = {
                    {},
                    {"a"},
                    {"a", "a", "b"}};
            for (int i = 0; i < notOk.length; i++) {
                assertTrue(!matcher.matches(notOk[i]));
            }
        } catch (SExpressionParseException e) {
            fail();
        } catch (UnknownOperatorException e) {
            fail();
        }
	}
	
	public void test_To5() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("({,5} (a))");
            String[][] ok = {
                    {},
                    {"a"},
                    {"a", "a"},
                    {"a", "a", "a"},
                    {"a", "a", "a", "a"},
                    {"a", "a", "a", "a", "a"}};
            NFAMatcher matcher = new NFAMatcher(nfa);
            for (int i = 0; i < ok.length; i++) {
                assertTrue("rejected string: " + i, matcher.matches(ok[i]));
            }
            String[][] notOk = {
                    {"a", "a", "b"},
                    {"a", "a", "a", "a", "a", "a"}};
            for (int i = 0; i < notOk.length; i++) {
                assertTrue(!matcher.matches(notOk[i]));
            }
        } catch (SExpressionParseException e) {
            fail();
        } catch (UnknownOperatorException e) {
            fail();
        }
	}
	
	public void test_2To2() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("({2,2} (a))");
            String[][] ok = {
                    {"a", "a"}};
            NFAMatcher matcher = new NFAMatcher(nfa);
            for (int i = 0; i < ok.length; i++) {
                assertTrue("rejected string: " + i, matcher.matches(ok[i]));
            }
            String[][] notOk = {
                    {},
                    {"a"},
                    {"a", "a", "a"},
                    {"a", "a", "a", "a"},
                    {"a", "a", "a", "a", "a"},
                    {"a", "a", "b"},
                    {"a", "a", "a", "a", "a", "a"}};
            for (int i = 0; i < notOk.length; i++) {
                assertTrue(!matcher.matches(notOk[i]));
            }
        } catch (SExpressionParseException e) {
            fail();
        } catch (UnknownOperatorException e) {
            fail();
        }
	}
	
	public void test_0To2() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("({0,2} (a))");
            String[][] ok = {
                    {},
                    {"a"},
                    {"a", "a"}};
            NFAMatcher matcher = new NFAMatcher(nfa);
            for (int i = 0; i < ok.length; i++) {
                assertTrue("rejected string: " + i, matcher.matches(ok[i]));
            }
            String[][] notOk = {
                    {"a", "a", "a"},
                    {"a", "a", "a", "a"},
                    {"a", "a", "a", "a", "a"},
                    {"a", "a", "b"},
                    {"a", "a", "a", "a", "a", "a"}};
            for (int i = 0; i < notOk.length; i++) {
                assertTrue(!matcher.matches(notOk[i]));
            }
        } catch (SExpressionParseException e) {
            fail();
        } catch (UnknownOperatorException e) {
            fail();
        }
	}
	
	public void test_3To2() {
        ThompsonFactory regex = new ThompsonFactory();
	    try {
            SparseNFA nfa = regex.create("({3,2} (a))");
            String[][] ok = {};
            NFAMatcher matcher = new NFAMatcher(nfa);
            for (int i = 0; i < ok.length; i++) {
                assertTrue("rejected string: " + i, matcher.matches(ok[i]));
            }
            String[][] notOk = {
                    {},
                    {"a"},
                    {"a", "a"},
                    {"a", "a", "a"},
                    {"a", "a", "a", "a"},
                    {"a", "a", "a", "a", "a"},
                    {"a", "a", "b"},
                    {"a", "a", "a", "a", "a", "a"}};
            for (int i = 0; i < notOk.length; i++) {
                assertTrue(!matcher.matches(notOk[i]));
            }
        } catch (SExpressionParseException e) {
            fail();
        } catch (UnknownOperatorException e) {
            fail();
        }
	}
	
    public void test_toString() throws Exception {
        Regex regex = new Regex("(| (. (a) (b)) (? (. (c) (d))))");
        InfixConverter converter = new InfixConverter();
        String regexStr = converter.convert(regex);
        assertEquals("regex toString", "(a . b) | (c . d)?", regexStr);
    }

    public void test_identifySymbol() throws Exception {
        Regex regex = new Regex();
        assertEquals("union operator",
                     "union",
                     regex.identifySymbol(Regex.UNION_OPERATOR));
        try {
            regex.identifySymbol("#####");
            fail("operator should be unknown");
        } catch (UnknownOperatorException e) {}   
    }

    public void test_writeReadExpression1() throws Exception {
        Regex regex = new Regex();
        StringWriter strWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(strWriter);
        try {
            xmlWriter.write(regex);
            fail("Can't write an undefined expression");
        } catch (NoRegularExpressionDefinedException e) {
        } catch (Exception e) {
            fail("No other exception should be thrown");
        }
        final String regexStr = "(* (. (| (a) (+ (b))) (* (c)) (? (d))))";
        regex = new Regex(regexStr);
        xmlWriter.write(regex);
        xmlWriter.close();
        StringReader strReader = new StringReader(strWriter.toString());
        XMLReader xmlReader = new XMLReader(strReader);
        regex = xmlReader.read();
        assertEquals("equivalent", regexStr, regex.toString());
    }

    public void test_writeReadExpression2() throws Exception {
        Regex regex = new Regex();
        StringWriter strWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(strWriter);
        final String regexStr = "({3,} (. (| (a) ({3,7} (b))) (* (c)) ({,2} (d))))";
        regex = new Regex(regexStr);
        xmlWriter.write(regex);
        xmlWriter.close();
        StringReader strReader = new StringReader(strWriter.toString());
        XMLReader xmlReader = new XMLReader(strReader);
        regex = xmlReader.read();
        assertEquals("equivalent", regexStr, regex.toString());
    }

}
