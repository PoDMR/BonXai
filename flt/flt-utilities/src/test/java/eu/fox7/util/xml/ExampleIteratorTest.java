/*
 * Created on Jul 18, 2006
 * Modified on $Date: 2009-11-09 13:14:38 $
 */
package eu.fox7.util.xml;

import eu.fox7.util.xml.ExampleIterator;
import eu.fox7.util.xml.acstring.AncestorChildrenExampleParser;
import eu.fox7.util.xml.acstring.ExampleParsingException;
import eu.fox7.util.xml.acstring.ParseResult;

import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ExampleIteratorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ExampleIteratorTest.class);
    }

    public static Test suite() {
        return new TestSuite(ExampleIteratorTest.class);
    }

    public void test_stringReaderIterator() throws Exception {
        String exampleStr =
            "a b c\n" +
            "a b b b\n" + 
            "\n" +
            "a\n" +
            "c\n";
        try {
            List<String> examples = new LinkedList<String>();
            for (Iterator<ParseResult> exIt = new ExampleIterator(new StringReader(exampleStr));
                 exIt.hasNext(); ) {
                examples.add(StringUtils.join(exIt.next().getContent(),
                                              ExampleIterator.DefaultLineParser.DEFAULT_SEPARATOR));
            }
            assertEquals("number of examples", 5, examples.size());
            String str = StringUtils.join(examples.iterator(), "\n") + "\n";
            assertEquals("all examples", exampleStr, str);
        } catch (ExampleParsingException e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_stringReaderIteratorEmptyLast() throws Exception {
        String exampleStr =
            "a b c\n" +
            "a b b b\n" + 
            "\n" +
            "a\n" +
            "\n";
        try {
            List<String> examples = new LinkedList<String>();
            for (Iterator<ParseResult> exIt = new ExampleIterator(new StringReader(exampleStr));
                 exIt.hasNext(); ) {
                examples.add(StringUtils.join(exIt.next().getContent(),
                                              ExampleIterator.DefaultLineParser.DEFAULT_SEPARATOR));
            }
            assertEquals("number of examples", 5, examples.size());
            String str = StringUtils.join(examples.iterator(), "\n") + "\n";
            assertEquals("all examples", exampleStr, str);
        } catch (ExampleParsingException e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_stringReaderIteratorComment() throws Exception {
        String exampleStr =
            "a b c\n" +
            " a b b b  \n" +
            " # bla bla \n" +
            "\n" +
            "a\n" +
            "\n";
        String targetStr =
            "a b c\n" +
            "a b b b\n" +
            "\n" +
            "a\n" +
            "\n";
        try {
            List<String> examples = new LinkedList<String>();
            for (Iterator<ParseResult> exIt = new ExampleIterator(new StringReader(exampleStr));
                 exIt.hasNext(); ) {
                examples.add(StringUtils.join(exIt.next().getContent(),
                                              ExampleIterator.DefaultLineParser.DEFAULT_SEPARATOR));
            }
            assertEquals("number of examples", 5, examples.size());
            String str = StringUtils.join(examples.iterator(), "\n") + "\n";
            assertEquals("all examples", targetStr, str);
        } catch (ExampleParsingException e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_stringReaderIteratorLastComment() throws Exception {
        String exampleStr =
            "a b c\n" +
            " a b b b  \n" +
            "\n" +
            "a\n" +
            "\n" +
            " # bla bla \n";
        String targetStr =
            "a b c\n" +
            "a b b b\n" +
            "\n" +
            "a\n" +
            "\n";
        try {
            List<String> examples = new LinkedList<String>();
            for (Iterator<ParseResult> exIt = new ExampleIterator(new StringReader(exampleStr));
                 exIt.hasNext(); ) {
                examples.add(StringUtils.join(exIt.next().getContent(),
                                              ExampleIterator.DefaultLineParser.DEFAULT_SEPARATOR));
            }
            assertEquals("number of examples", 5, examples.size());
            String str = StringUtils.join(examples.iterator(), "\n") + "\n";
            assertEquals("all examples", targetStr, str);
        } catch (ExampleParsingException e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_ancChild() throws Exception {
        String exampleStr = 
            "a/b/c ::- d d e d\n" +
            "a ::- \n" +
            " ::- a\n" +
            " # this is comment\n" +
            "a/b ::- a";
        String targetStr = 
            "a/b/c ::- d d e d\n" +
            "a ::- \n" +
            " ::- a\n" +
            "a/b ::- a";
        List<String> examples = new LinkedList<String>();
        for (Iterator<ParseResult> exIt = new ExampleIterator(new StringReader(exampleStr), new AncestorChildrenExampleParser()); exIt.hasNext(); ) {
            ParseResult result = exIt.next();
            StringBuilder str = new StringBuilder();
            str.append(StringUtils.join(result.getContext(),
                                        AncestorChildrenExampleParser.DEFAULT_ANCESTOR_SEP));
            str.append(" ");
            str.append(AncestorChildrenExampleParser.DEFAULT_ANCESTOR_CHILDREN_SEP);
            str.append(" ");
            str.append(StringUtils.join(result.getContent(),
                                        AncestorChildrenExampleParser.DEFAULT_CHILDREN_SEP));
            examples.add(str.toString());
        }
        String resultStr = StringUtils.join(examples.iterator(), "\n");
        assertEquals("all examples", targetStr, resultStr);
    }

}
