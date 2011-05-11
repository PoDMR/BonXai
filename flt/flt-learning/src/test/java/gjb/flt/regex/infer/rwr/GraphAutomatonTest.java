/*
 * Created on Sep 4, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.impl.InvalidLabelException;
import gjb.flt.regex.infer.rwr.impl.io.GraphAutomatonReader;
import gjb.flt.regex.infer.rwr.impl.io.GraphAutomatonWriter;
import gjb.util.tree.SExpressionParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class GraphAutomatonTest extends TestCase {

    public static Test suite() {
        return new TestSuite(GraphAutomatonTest.class);
    }

    public void testWriterReader1() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        GraphAutomatonWriter aWriter = new GraphAutomatonWriter();
        GraphAutomatonReader aReader = new GraphAutomatonReader();
        String[] regexes = {
                "(EMPTY)", "(EPSILON)", "(a)", "(abc)", "(. (a) (b))",
                "(. (b) (a))", "(. (+ (a)) (b))", "(| (a) (b))", "(| (a) (+ (b)))"
        };
        try {
            for (String regexStr : regexes) {
                Automaton automaton = factory.create(regexStr);
                StringWriter writer = new StringWriter();
                aWriter.write(writer, automaton);
                StringReader reader = new StringReader(writer.toString());
                Automaton newAutomaton = aReader.read(reader);
                assertTrue("equiv", areEquivalent(automaton, newAutomaton));
            }
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void testSampleConstruction() {
        final String[][] sample = {
                {"a", "a", "c"},
                {"a", "c"},
                {"b", "a", "c"},
                {"b", "a", "a", "c"},
                {"b"},
                {}
        };
        try {
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            Automaton automaton = factory.create(sample);
            assertEquals("number of states", 4, automaton.getNumberOfStates());
            assertTrue("epsilon accepted", automaton.acceptsEpsilon());
            assertTrue("a-a", automaton.hasTransition("(a)", "(a)"));
            assertTrue("a-c", automaton.hasTransition("(a)", "(a)"));
            assertTrue("b-a", automaton.hasTransition("(a)", "(a)"));
            assertFalse("a-b", automaton.hasTransition("(a)", "(b)"));
            assertFalse("b-b", automaton.hasTransition("(b)", "(b)"));
            assertFalse("b-c", automaton.hasTransition("(b)", "(c)"));
            assertFalse("c-a", automaton.hasTransition("(c)", "(a)"));
            assertFalse("c-b", automaton.hasTransition("(c)", "(b)"));
            assertFalse("c-c", automaton.hasTransition("(c)", "(c)"));
            assertTrue("a initial", automaton.isInitial("(a)"));
            assertTrue("b initial", automaton.isInitial("(b)"));
            assertFalse("c initial", automaton.isInitial("(c)"));
            assertFalse("a final", automaton.isFinal("(a)"));
            assertTrue("b final", automaton.isFinal("(b)"));
            assertTrue("c final", automaton.isFinal("(c)"));
            assertTrue("(a)", automaton.hasLabel("(a)"));
            assertTrue("(b)", automaton.hasLabel("(b)"));
            assertTrue("(c)", automaton.hasLabel("(c)"));
            assertFalse("d", automaton.hasLabel("d"));
        } catch (InvalidLabelException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void testRegexConstruction1() {
        final String regexStr = "(? (. (| (a) (+ (b))) (c)))";
        try {
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            Automaton automaton = factory.create(regexStr);
            assertTrue("initial a", automaton.isInitial("(a_1)"));
            assertTrue("initial b", automaton.isInitial("(b_2)"));
            assertFalse("initial c", automaton.isInitial("(c_3)"));
            assertFalse("final a", automaton.isFinal("(a_1)"));
            assertFalse("final b", automaton.isFinal("(b_2)"));
            assertTrue("final c", automaton.isFinal("(c_3)"));
            assertFalse("a-a", automaton.hasTransition("(a_1)", "(a_1)"));
            assertFalse("a-b", automaton.hasTransition("(a_1)", "(b_2)"));
            assertTrue("a-c", automaton.hasTransition("(a_1)", "(c_3)"));
            assertFalse("b-a", automaton.hasTransition("(b_2)", "(a_1)"));
            assertTrue("b-b", automaton.hasTransition("(b_2)", "(b_2)"));
            assertTrue("b-c", automaton.hasTransition("(b_2)", "(c_3)"));
            assertFalse("c-a", automaton.hasTransition("(c_3)", "(a_1)"));
            assertFalse("c-b", automaton.hasTransition("(c_3)", "(b_2)"));
            assertFalse("c-c", automaton.hasTransition("(c_3)", "(c_3)"));
            assertTrue("epsilong", automaton.acceptsEpsilon());
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (InvalidLabelException e) {
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

    public void testRegexConstruction2() {
        final String regexStr = "(? (. (| (a) (+ (b))) (? (c))))";
        try {
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            Automaton automaton = factory.create(regexStr);
            assertTrue("initial a", automaton.isInitial("(a_1)"));
            assertTrue("initial b", automaton.isInitial("(b_2)"));
            assertFalse("initial c", automaton.isInitial("(c_3)"));
            assertTrue("final a", automaton.isFinal("(a_1)"));
            assertTrue("final b", automaton.isFinal("(b_2)"));
            assertTrue("final c", automaton.isFinal("(c_3)"));
            assertFalse("a-a", automaton.hasTransition("(a_1)", "(a_1)"));
            assertFalse("a-b", automaton.hasTransition("(a_1)", "(b_2)"));
            assertTrue("a-c", automaton.hasTransition("(a_1)", "(c_3)"));
            assertFalse("b-a", automaton.hasTransition("(b_2)", "(a_1)"));
            assertTrue("b-b", automaton.hasTransition("(b_2)", "(b_2)"));
            assertTrue("b-c", automaton.hasTransition("(b_2)", "(c_3)"));
            assertFalse("c-a", automaton.hasTransition("(c_3)", "(a_1)"));
            assertFalse("c-b", automaton.hasTransition("(c_3)", "(b_2)"));
            assertFalse("c-c", automaton.hasTransition("(c_3)", "(c_3)"));
            assertTrue("epsilong", automaton.acceptsEpsilon());
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (InvalidLabelException e) {
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

    public void testReverseConstructor1() {
        final String regexStr = "(. (a) (b))";
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        try {
            Automaton automaton = factory.create(regexStr);
            RewriteEngine rewriter = new Rewriter();
            String derivedRegexStr = rewriter.rewriteToRegex(automaton);
            Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
            assertTrue("equiv", areEquivalent(automaton, derivedAutomaton));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
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
    
    public void testReverseConstructor2() {
        final String regexStr = "(. (a) (+ (. (b) (c))))";
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        try {
            Automaton automaton = factory.create(regexStr);
            RewriteEngine rewriter = new Rewriter();
            String derivedRegexStr = rewriter.rewriteToRegex(automaton);
            Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
            assertTrue("equiv", areEquivalent(automaton, derivedAutomaton));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
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
    
    public void testReverseConstructor3() {
        final String regexStr = "(+ (| (b) (c)))";
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        try {
            Automaton automaton = factory.create(regexStr);
            RewriteEngine rewriter = new Rewriter();
            String derivedRegexStr = rewriter.rewriteToRegex(automaton);
            Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
            assertTrue("equiv", areEquivalent(automaton, derivedAutomaton));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
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
    
    public void testReverseConstructor4() {
        final String regexStr = "(. (a) (+ (| (b) (c))))";
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        try {
            Automaton automaton = factory.create(regexStr);
            RewriteEngine rewriter = new Rewriter();
            String derivedRegexStr = rewriter.rewriteToRegex(automaton);
            Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
            assertTrue("equiv", areEquivalent(automaton, derivedAutomaton));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
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
    
    public void testReverseConstructor5() {
        final String regexStr = "(. (+ (| (b) (c))) (? (d)))";
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        try {
            Automaton automaton = factory.create(regexStr);
            RewriteEngine rewriter = new Rewriter();
            String derivedRegexStr = rewriter.rewriteToRegex(automaton);
            Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
            assertTrue("equiv", areEquivalent(automaton, derivedAutomaton));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
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
    
    public void testReverseConstructor6() {
        final String regexStr = "(. (a) (+ (| (b) (c))) (? (d)) (e))";
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        try {
            Automaton automaton = factory.create(regexStr);
            RewriteEngine rewriter = new Rewriter();
            String derivedRegexStr = rewriter.rewriteToRegex(automaton);
            Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
            assertTrue("equiv", areEquivalent(automaton, derivedAutomaton));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
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
    
    public void testReverseConstructor7() {
        final String regexStr = "(? (. (a) (+ (| (b) (c))) (? (d)) (e)))";
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        try {
            Automaton automaton = factory.create(regexStr);
            RewriteEngine rewriter = new Rewriter();
            String derivedRegexStr = rewriter.rewriteToRegex(automaton);
            Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
            assertTrue("equiv", areEquivalent(automaton, derivedAutomaton));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
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

    public void testExpand1() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        BigInteger index = new BigInteger("13317431172");
        Automaton automaton = factory.create(index, 5);
        RewriteEngine rewriter = new Rewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton);
        Automaton derivedAutomaton = factory.expand(newAutomaton);
        assertTrue("equiv " + index.toString(),
                   areEquivalent(automaton, derivedAutomaton));
    }

    public void testExpand2() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        BigInteger index = new BigInteger("23905643703");
        Automaton automaton = factory.create(index, 5);
        RewriteEngine rewriter = new Rewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton);
        Automaton derivedAutomaton = factory.expand(newAutomaton);
        assertTrue("equiv " + index.toString(),
                   areEquivalent(automaton, derivedAutomaton));
    }
    
    public void testExpand3() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        BigInteger index = new BigInteger("55432032433");
        Automaton automaton = factory.create(index, 5);
        if (GraphAutomatonFactory.isSound(automaton)) {
        RewriteEngine rewriter = new Rewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton);
        Automaton derivedAutomaton = factory.expand(newAutomaton);
        assertTrue("equiv " + index.toString(),
                   areEquivalent(automaton, derivedAutomaton));
        }
    }
    
    public void testExpand4() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        BigInteger index = new BigInteger("41574495158");
        Automaton automaton = factory.create(index, 5);
        if (GraphAutomatonFactory.isSound(automaton)) {
            RewriteEngine rewriter = new Rewriter();
            Automaton newAutomaton = rewriter.rewrite(automaton);
            Automaton derivedAutomaton = factory.expand(newAutomaton);
            assertTrue("equiv " + index.toString(),
                       areEquivalent(automaton, derivedAutomaton));
        }
    }
    
    public void testExpand() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        File file = new File("test-data/regex-5-sample.txt");
        BigInteger index = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] data = line.trim().split("\\t");
                index = new BigInteger(data[0]);
                Automaton automaton = factory.create(index, 5);
                if (!GraphAutomatonFactory.isSound(automaton))
                    continue;
                RewriteEngine rewriter = new Rewriter();
                Automaton newAutomaton = rewriter.rewrite(automaton);
                Automaton derivedAutomaton = factory.expand(newAutomaton);
                assertTrue("equiv " + index.toString(),
                           areEquivalent(automaton, derivedAutomaton));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (RuntimeException e) {
            e.printStackTrace();
            fail("unexpected exception in '" + index.toString() + "'");
        }
    }
    
    public void testReverse() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        File file = new File("test-data/real-world-sores.txt");
        String regexStr = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                regexStr = line.trim();
                Automaton automaton = factory.create(regexStr);
                RewriteEngine rewriter = new Rewriter();
                String derivedRegexStr = rewriter.rewriteToRegex(automaton);
                Automaton derivedAutomaton = factory.reverse(derivedRegexStr);
                assertTrue("equiv " + regexStr, areEquivalent(automaton, derivedAutomaton));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (RuntimeException e) {
            e.printStackTrace();
            fail("unexpected exception in '" + regexStr + "'");
        }
    }

    public void testIndexConstructor1() {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        BigInteger index = new BigInteger("100011011", 2);
        Automaton automaton = factory.create(index, 2);
        RewriteEngine rewriter = new Rewriter();
        try {
            assertEquals("equiv",
                         "(. (? (+ (a))) (b))",
                         rewriter.rewriteToRegex(automaton));
        } catch (NoOpportunityFoundException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    protected static boolean areEquivalent(Automaton a1, Automaton a2) {
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton n1 = factory.order(a1);
        Automaton n2 = factory.order(a2);
        if (n1.getNumberOfStates() != n2.getNumberOfStates())
            return false;
        for (int i = 0; i < n1.getNumberOfStates(); i++)
            for (int j = 0; j < n1.getNumberOfStates(); j++)
                if (n1.get(i, j) != n2.get(i, j))
                    return false;
        return true;
    }

}
