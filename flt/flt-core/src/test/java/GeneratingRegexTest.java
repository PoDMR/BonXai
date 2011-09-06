import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.lang.StringUtils;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class GeneratingRegexTest extends TestCase {

	public static Test suite() {
		return new TestSuite(GeneratingRegexTest.class);
	}

    public void test_generateRegex1() throws Exception {
	    LanguageGenerator regex = new LanguageGenerator("(. (| (a) (b)) (c))");
	    Set<String> accepted = new HashSet<String>();
	    accepted.add("a-c");
	    accepted.add("b-c");
	    Set<String> notFound = new HashSet<String>(accepted);
	    for (int i = 0; i < 100; i++) {
	        List<String> example = regex.generateRandomExample(0.5, 0.5);
	        String str = StringUtils.join(example.iterator(), "-");
	        assertTrue("accepted", accepted.contains(str));
	        notFound.remove(str);
	    }
        assertEquals("all found", 0, notFound.size());
    }

    public void test_generateRegex2() throws Exception {
    	LanguageGenerator regex = new LanguageGenerator("(. (a) (* (| (a) (b))))");
	    regex.setAverageRepetitions(1.0);
	    Set<String> notFound = new HashSet<String>();
	    notFound.add("a");
	    notFound.add("a-a");
	    notFound.add("a-b");
	    notFound.add("a-a-a");
	    notFound.add("a-a-b");
	    notFound.add("a-b-a");
	    notFound.add("a-b-b");
	    Pattern acceptingPattern = Pattern.compile("a(-(a|b))*");
	    for (int i = 0; i < 500; i++) {
	        List<String> example = regex.generateRandomExample();
	        String str = StringUtils.join(example.iterator(), "-");
	        Matcher matcher = acceptingPattern.matcher(str);
	        assertTrue("accepted", matcher.matches());
	        notFound.remove(str);
	    }
        assertEquals("all found", 0, notFound.size());
    }

    public void test_generateRegex3() throws Exception {
	    LanguageGenerator regex = new LanguageGenerator("(. (a) (+ (| (a) (b))))");
	    regex.setAverageRepetitions(1.0);
	    Set<String> notFound = new HashSet<String>();
	    notFound.add("a-a");
	    notFound.add("a-b");
	    notFound.add("a-a-a");
	    notFound.add("a-a-b");
	    notFound.add("a-b-a");
	    notFound.add("a-b-b");
	    Pattern acceptingPattern = Pattern.compile("a(-(a|b))+");
	    for (int i = 0; i < 500; i++) {
	        List<String> example = regex.generateRandomExample();
	        String str = StringUtils.join(example.iterator(), "-");
	        Matcher matcher = acceptingPattern.matcher(str);
	        assertTrue("accepted", matcher.matches());
	        notFound.remove(str);
	    }
        assertEquals("all found", 0, notFound.size());
    }

    public void test_defaultDistributionTree() throws Exception {
        String regexStr = "(. (| (a) (b) (? (c))) (* (d)) (e) (+ (f)))";
        LanguageGenerator regex = new LanguageGenerator(regexStr);
        Tree tree = regex.getTree();
        Node node = tree.getRoot();
        assertEquals("no distr for root", null, node.value());
        assertEquals("union distr",
                     "default-union-3",
                     node.child(0).value());
        assertEquals("zero or one distr",
                     "default-zeroOrOne",
                     node.child(0).child(2).value());
        assertEquals("zore or more distr",
                     "default-zeroOrMore",
                     node.child(1).value());
        assertEquals("one or more distr",
                     "default-oneOrMore",
                     node.child(3).value());
        assertEquals("no distr for leaf", null, node.child(2).value());
    }

    public void test_mixedDistributionTree() throws Exception {
        String regexStr = "(. (|[foo] (a) (b) (?[bar] (c))) (*[hey] (d)) (e) (+ (f)))";
        LanguageGenerator regex = new LanguageGenerator(regexStr);
        Tree tree = regex.getTree();
        Node node = tree.getRoot();
        assertEquals("no distr for root", null, node.value());
        assertEquals("union distr",
                     "foo",
                     node.child(0).value());
        assertEquals("zero or one distr",
                     "bar",
                     node.child(0).child(2).value());
        assertEquals("zore or more distr",
                     "hey",
                     node.child(1).value());
        assertEquals("one or more distr",
                     "default-oneOrMore",
                     node.child(3).value());
        assertEquals("no distr for leaf", null, node.child(2).value());
    }

    public void test_generateRegexAll1() throws Exception {
        LanguageGenerator regex = new LanguageGenerator("(. (| (a) (b)) (c))");
        Set<String> accepted = new HashSet<String>();
        accepted.add("a-c");
        accepted.add("b-c");
        int number = 0;
        for (Iterator<List<String>> it = regex.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            assertEquals("two char string", 2, list.size());
            assertTrue("correct string", accepted.contains(StringUtils.join(list.iterator(), "-")));
            number++;
        }
        assertEquals("number of strings", accepted.size(), number);
    }

    public void test_generateRegexAll2() throws Exception {
        LanguageGenerator regex = new LanguageGenerator("(. (a) (* (| (a) (b))))");
        Set<String> accepted = new HashSet<String>();
        accepted.add("a");
        accepted.add("a-a");
        accepted.add("a-b");
        accepted.add("a-a-a");
        accepted.add("a-a-b");
        accepted.add("a-b-a");
        accepted.add("a-b-b");
        int number = 0;
        for (Iterator<List<String>> it = regex.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            assertTrue("at least 1 char", list.size() >= 1);
            assertTrue("at most 3 chars", list.size() <= 3);
            assertTrue("correct string", accepted.contains(StringUtils.join(list.iterator(), "-")));
            number++;
        }
        assertEquals("number of strings", accepted.size(), number);
    }

    public void test_generateRegexAll3() throws Exception {
    	LanguageGenerator regex = new LanguageGenerator("(. (* (a)) (* (b)))");
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
        for (Iterator<List<String>> it = regex.generatingRun(3); it.hasNext();) {
            List<String> list = it.next();
            produced.add(StringUtils.join(list.iterator(), "-"));
        }
        assertEquals("correct set", accepted, produced);
    }

    public void test_generateRegexRandom() throws Exception {
        final String regexStr = "(* (. (a) (? (| (b) (c)))))";
        final int numberOfExpressions = 500;
        LanguageGenerator regex = new LanguageGenerator(regexStr);
        Set<String> accepted = new HashSet<String>();
        accepted.add("");
        accepted.add("a");
        accepted.add("a-b");
        accepted.add("a-c");
        accepted.add("a-a");
        accepted.add("a-a-b");
        accepted.add("a-a-c");
        accepted.add("a-a-a");
        Set<String> produced = new HashSet<String>();
        for (int i = 0; i < numberOfExpressions; i++)
            produced.add(StringUtils.join(regex.generateRandomExample().iterator(), "-"));
        for (String exampleStr : accepted)
            assertTrue("accepted '" + exampleStr + "'",
                       produced.contains(exampleStr));
    }

}
