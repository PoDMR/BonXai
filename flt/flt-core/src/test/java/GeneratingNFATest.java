import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import gjb.flt.automata.factories.sparse.ThompsonBuilder;
import gjb.flt.automata.generators.LanguageGenerator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/*
 * Created on Mar 1, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */

/**
 * @author gjb
 * @version $Revision: 1.1 $
 */
public class GeneratingNFATest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(GeneratingNFATest.class);
    }

    public GeneratingNFATest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(GeneratingNFATest.class);
	}


    public void test_generateEmptyNFA() {
        int number = 0;
        LanguageGenerator g = new LanguageGenerator(ThompsonBuilder.emptyNFA());
        for (Iterator<List<String>> it = g.generatingRun(3); it.hasNext();) {
            it.next();
            number++;
        }
        assertEquals("no strings", 0, number);
    }

    public void test_generateEpsilonNFA() {
        int number = 0;
        LanguageGenerator g = new LanguageGenerator(ThompsonBuilder.epsilonNFA());
        for (Iterator<List<String>> it = g.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            assertEquals("empty string", 0, list.size());
            number++;
        }
        assertEquals("number of strings", 1, number);
    }

    public void test_generateSymbolNFA() {
        int number = 0;
        LanguageGenerator g = new LanguageGenerator(ThompsonBuilder.symbolNFA("abc"));
        for (Iterator<List<String>> it = g.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            assertEquals("single char string", 1, list.size());
            assertEquals("correct string", "abc", list.get(0));
            number++;
        }
        assertEquals("number of strings", 1, number);
    }

    public void test_generateRegex1() throws Exception {
	    LanguageGenerator g = new LanguageGenerator("(. (| (a) (b)) (c))");
	    Set<String> accepted = new HashSet<String>();
	    accepted.add("a-c");
	    accepted.add("b-c");
	    int number = 0;
        for (Iterator<List<String>> it = g.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            assertEquals("two char string", 2, list.size());
            assertTrue("correct string", accepted.contains(StringUtils.join(list.iterator(), "-")));
            number++;
        }
        assertEquals("number of strings", accepted.size(), number);
    }

    public void test_generateRegex2() throws Exception {
	    LanguageGenerator g = new LanguageGenerator("(. (a) (* (| (a) (b))))");
	    Set<String> accepted = new HashSet<String>();
	    accepted.add("a");
	    accepted.add("a-a");
	    accepted.add("a-b");
	    accepted.add("a-a-a");
	    accepted.add("a-a-b");
	    accepted.add("a-b-a");
	    accepted.add("a-b-b");
	    int number = 0;
        for (Iterator<List<String>> it = g.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            assertTrue("at least 1 char", list.size() >= 1);
            assertTrue("at most 3 chars", list.size() <= 3);
            assertTrue("correct string", accepted.contains(StringUtils.join(list.iterator(), "-")));
            number++;
        }
        assertEquals("number of strings", accepted.size(), number);
    }

    public void test_generateRegex3() throws Exception {
	    LanguageGenerator g = new LanguageGenerator("(. (* (a)) (* (b)))");
	    Set<String> accepted = new HashSet<String>();
	    accepted.add("");
	    accepted.add("a");
	    accepted.add("b");
	    accepted.add("a-a");
	    accepted.add("a-b");
	    accepted.add("b-b");
	    accepted.add("a-a-a");
	    accepted.add("a-a-b");
	    accepted.add("a-b-b");
	    accepted.add("b-b-b");
	    Set<String> produced = new HashSet<String>();
        for (Iterator<List<String>> it = g.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            produced.add(StringUtils.join(list.iterator(), "-"));
        }
        assertEquals("correct set", accepted, produced);
        assertEquals("correct size", 10, g.languageSize(3));
    }

    public void test_generateRegex4() throws Exception {
	    LanguageGenerator g = new LanguageGenerator("(* (| (a) (b) (c)))");
        assertEquals("all strings upto 3", 40, count(g.generatingRun(3)));
        assertEquals("all strings upto 5", 364, count(g.generatingRun(5)));
    }

    public void test_generateRegex5() throws Exception {
	    LanguageGenerator g = new LanguageGenerator("(. (* (a)) (* (b)))");
	    for (int i = 0; i <= 10; i++) {
	        assertEquals("strings up to " + i,
	                     (i + 1)*(i + 2)/2,
	                     count(g.generatingRun(i)));
	    }
    }

    public void test_generateRegex6() throws Exception {
        LanguageGenerator g = new LanguageGenerator("(. (a) (b) (c) (d) (+ (a)))");
	    for (int i = 0; i <= 4; i++) {
	        assertEquals("no strings of length " + i, 0, count(g.generatingRun(i)));
	    }
	    for (int i = 5; i < 20; i++) {
            assertEquals("1 string of length " + i, i - 4, count(g.generatingRun(i)));
            assertEquals("1 string of length " + i, i - 4, g.languageSize(i));
	    }
    }

    protected int count(Iterator<?> it) {
        int counter = 0;
        while (it.hasNext()) {
            it.next();
            counter++;
        }
        return counter;
    }

//    /*
//     * The probability distribution for the alternative strings 'ab' and 'cde' is
//     * binomial, hence one can calculate the probability for the number of 'ab'
//     * outcomes in a number of independent trials N.  In order to ensure succesful
//     * testing, we want a 99% probability interval that the average will not deviate
//     * more than n from N p. Hence
//     * \sum_{i = N p - n}^{N p + n} C(N, i) p^i (1-p)^{N-i} > 0.99 
//     * Using Maple, this condition yields n = 13 for N = 100 and p = 0.5.
//     */
//    public void test_randomExample1() throws Exception {
//        Glushkov glushkov = new Glushkov();
//        GeneratingNFA nfa = glushkov.generatingNFA("(| (. (a) (b)) (. (c) (d) (e)))");
//        int nrB = 0;
//        int nrE = 0;
//        int totalNumber = 100;
//        int prob99Number = 13;
//        for (int i = 0; i < totalNumber; i++) {
//            List<String> example = nfa.generateRandomExample(1.0);
//            assertTrue("always non null", example != null);
//            String exampleStr = StringUtils.join(example.iterator(), "");
//            if (exampleStr.endsWith("b"))
//                nrB++;
//            else if (exampleStr.endsWith("e"))
//                nrE++;
//            else
//                fail("should end with 'b' or 'e'");
//        }
//        assertTrue("equal chances", Math.abs(nrE - totalNumber/2) <= prob99Number);
//    }
//
//    /*
//     * The probability distribution for the alternative strings 'ab' and 'cde' is
//     * binomial, hence one can calculate the probability for the number of 'ab'
//     * outcomes in a number of independent trials N.  In order to ensure succesful
//     * testing, we want a 99% probability interval that the average will not deviate
//     * more than n from N p. Hence
//     * \sum_{i = N p - n}^{N p + n} C(N, i) p^i (1-p)^{N-i} > 0.99 
//     * Using Maple, this condition yields n = 41 for N = 1000 and p = 0.5; n = 29 for
//     * N = 500 and p = 0.5.
//     */
//    public void test_randomExample2() throws Exception {
//        Glushkov glushkov = new Glushkov();
//        GeneratingNFA nfa = glushkov.generatingNFA("(| (. (a) (b)) (. (c) (d) (e)))");
//        int nrB = 0;
//        int nrE = 0;
//        int nrNull = 0;
//        int totalNumber = 1000;
//        int prob99NumberTotal = 41;
//        int prob99NumberEach = 29;
//        for (int i = 0; i < totalNumber; i++) {
//            List<String> example = nfa.generateRandomExample(0.5);
//            if (example == null) {
//                nrNull++;
//            } else {
//                String exampleStr = StringUtils.join(example.iterator(), "");
//                if (exampleStr.endsWith("b"))
//                    nrB++;
//                else if (exampleStr.endsWith("e"))
//                    nrE++;
//                else
//                    fail("should end with 'b' or 'e'");
//            }
//        }
//        assertTrue("equal chances 'b' and 'e'", Math.abs(nrE - totalNumber/4) <= prob99NumberEach);
//        assertTrue("equal chances end", Math.abs(nrNull - totalNumber/2) <= prob99NumberTotal);
//    }

    public void test_randomExample3() throws Exception {
	    LanguageGenerator g = new LanguageGenerator("(. (| (a) (b)) (c))");
	    Set<String> accepted = new HashSet<String>();
	    accepted.add("a-c");
	    accepted.add("b-c");
	    for (int i = 0; i < 30; i++) {
	        String str = StringUtils.join(g.generateRandomExample(1.0).iterator(), "-");
	        accepted.remove(str);
	    }
	    assertTrue("all generated", accepted.isEmpty());
    }

    public void test_randomExample4() throws Exception {
	    LanguageGenerator g = new LanguageGenerator("(* (. (a) (a)))");
	    Set<String> accepted = new HashSet<String>();
	    accepted.add("");
	    accepted.add("aa");
	    accepted.add("aaaa");
	    accepted.add("aaaaaa");
	    accepted.add("aaaaaaaa");
	    accepted.add("aaaaaaaaaa");
	    for (int i = 0; i < 200; i++) {
	        String str = StringUtils.join(g.generateRandomExample(0.1).iterator(), "");
	        accepted.remove(str);
	    }
	    assertTrue("all generated", accepted.isEmpty());
    }

}
