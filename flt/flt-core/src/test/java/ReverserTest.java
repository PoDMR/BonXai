import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.factories.sparse.GlushkovReverser;
import gjb.flt.automata.generators.LanguageGenerator;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.matchers.NFAMatcher;

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.lang.StringUtils;

public class ReverserTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ReverserTest.class);
    }

    public static Test suite() {
        return new TestSuite(ReverserTest.class);
    }

    public void test_simpleConcat() {
        String regexStr = "(. (a) (a) (b) (c))";
        String[][] accepted = {
                {"c", "b", "a", "a"}
        };
        String[][] notAccepted = {
                {"a", "a", "b", "c"},
                {"a"},
                {"c"},
                {}
        };
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA nfa = glushkov.create(regexStr);
            SparseNFA reversedNFA = GlushkovReverser.reverse(nfa);
            NFAMatcher matcher = new NFAMatcher(reversedNFA);
            for (int i = 0; i < accepted.length; i++)
                assertTrue("accepted " + StringUtils.join(accepted[i], " "),
                           matcher.matches(accepted[i]));
            for (int i = 0; i < notAccepted.length; i++)
                assertTrue("not accepted " + StringUtils.join(notAccepted[i], " "),
                           !matcher.matches(notAccepted[i]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    @SuppressWarnings("unchecked")
    public void test_finiteLanguage() {
        String regexStr = "(. (? (| (a) (b))) (? (. (a) (b))) (? (a)))";
        int maxStringLength = 4;
        String[][] accepted = {};
        String[][] notAccepted = {
                {"b", "a", "b", "a"},
                {"b", "b"},
                {"a", "a", "a"},
                {"a", "a", "b"}
        };
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA nfa = glushkov.create(regexStr);
            SparseNFA reversedNFA = GlushkovReverser.reverse(nfa);
            NFAMatcher matcher = new NFAMatcher(reversedNFA);
            LanguageGenerator g = new LanguageGenerator(nfa);
            int counter = 0;
            for (Iterator exIt = g.generatingRun(maxStringLength); exIt.hasNext(); ) {
                counter++;
                List tokens = (List) exIt.next();
                String[] example = (String[]) tokens.toArray(new String[0]);
                String[] reverseExample = reverse(example);
                assertTrue("accepted " + StringUtils.join(reverseExample, " "),
                           matcher.matches(reverseExample));
            }
            assertEquals("number of strings", 12, counter);
            for (int i = 0; i < accepted.length; i++)
                assertTrue("accepted " + StringUtils.join(accepted[i], " "),
                           matcher.matches(accepted[i]));
            for (int i = 0; i < notAccepted.length; i++)
                assertTrue("not accepted " + StringUtils.join(notAccepted[i], " "),
                           !matcher.matches(notAccepted[i]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    @SuppressWarnings("unchecked")
    public void test_infiniteLanguage() {
        String regexStr = "(. (+ (| (a) (b))) (? (. (a) (b))) (+ (c)))";
        int maxStringLength = 6;
        String[][] accepted = {};
        String[][] notAccepted = {
                {"b", "a", "b", "a"},
                {"b", "b"},
                {"a", "a", "a"},
                {"a", "a", "b"}
        };
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA nfa = glushkov.create(regexStr);
            SparseNFA reversedNFA = GlushkovReverser.reverse(nfa);
            NFAMatcher matcher = new NFAMatcher(reversedNFA);
            LanguageGenerator g = new LanguageGenerator(nfa);
            for (Iterator exIt = g.generatingRun(maxStringLength); exIt.hasNext(); ) {
                List tokens = (List) exIt.next();
                String[] example = (String[]) tokens.toArray(new String[0]);
                String[] reverseExample = reverse(example);
                assertTrue("accepted " + StringUtils.join(reverseExample, " "),
                           matcher.matches(reverseExample));
            }
            for (int i = 0; i < accepted.length; i++)
                assertTrue("accepted " + StringUtils.join(accepted[i], " "),
                           matcher.matches(accepted[i]));
            for (int i = 0; i < notAccepted.length; i++)
                assertTrue("not accepted " + StringUtils.join(notAccepted[i], " "),
                           !matcher.matches(notAccepted[i]));
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    protected static String[] reverse(String[] a) {
        String[] reversedA = new String[a.length];
        for (int i = 0; i < a.length; i++)
            reversedA[a.length - i - 1] = a[i];
        return reversedA;
    }

}
