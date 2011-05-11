/*
 * Created on Sep 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.EquivalenceTest;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.impl.io.GraphAutomatonReader;
import gjb.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import gjb.util.tree.SExpressionParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RepairerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RepairerTest.class);
    }

    public void testAutomaton1Backtracking() {
        final String signature = "(DOLbd)#(a0001)#(a0002)#(a0003)#(a0004)#(a0005)#(a0006)#(a0007)#(a0008)#(a0009)#(a0010)#(a0011)#(a0012)#(a0013)#(a0014)#(a0015)#(a0016)#(a0017)#(a0018)#(a0019)#(a0020)#(a0021)#(a0022)#(a0023)#(a0024)#(a0025)#(a0026)#(a0027)#(a0028)#(a0029)#1001000011100001100101100011001010111111111110100111111101101100010111010111111110001101110110111111011110111111010011011011111011111110010111111111101011101101110100011111111111011001110111111101111011111101101111111101101111111011100111111101111111011111111111111111111111101111111110011101111111011111011011111111101111111101101001111111111111010110111111011011111111011111010011111011101101110111011101011111111011101110111100111111111110111111110101111111010110111011111101011100111111111111111110111100111111011010110101101111111111110110111111100111010111110111111011111111011011101011011011111111111101110111111101111111111101101111111111111010111111101111011110011011110111011110111111011111111101111110110001101111011111110010101111111101001111111010101110111110111000011111101111010110111111010101111010010001111110111111011110111111111110111110111111111111111101111111111101101100011111100111111011010111111001111111111111111111111111111111111111111;";
        try {
            Reader reader = new StringReader(signature);
            GraphAutomatonReader aReader = new GraphAutomatonReader();
            Automaton automaton = aReader.read(reader);
            BacktrackingRepairer repairer = new BacktrackingRepairer(new LanguageSizeMeasure(), 1);
            Automaton newAutomaton = repairer.rewrite(automaton);
            assertTrue("reduced", newAutomaton.isReduced());
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception");;
        }
    }

    public void testAutomaton1() {
        final String signature = "(DOLbd)#(a0001)#(a0002)#(a0003)#(a0004)#(a0005)#(a0006)#(a0007)#(a0008)#(a0009)#(a0010)#(a0011)#(a0012)#(a0013)#(a0014)#(a0015)#(a0016)#(a0017)#(a0018)#(a0019)#(a0020)#(a0021)#(a0022)#(a0023)#(a0024)#(a0025)#(a0026)#(a0027)#(a0028)#(a0029)#1001000011100001100101100011001010111111111110100111111101101100010111010111111110001101110110111111011110111111010011011011111011111110010111111111101011101101110100011111111111011001110111111101111011111101101111111101101111111011100111111101111111011111111111111111111111101111111110011101111111011111011011111111101111111101101001111111111111010110111111011011111111011111010011111011101101110111011101011111111011101110111100111111111110111111110101111111010110111011111101011100111111111111111110111100111111011010110101101111111111110110111111100111010111110111111011111111011011101011011011111111111101110111111101111111111101101111111111111010111111101111011110011011110111011110111111011111111101111110110001101111011111110010101111111101001111111010101110111110111000011111101111010110111111010101111010010001111110111111011110111111111110111110111111111111111101111111111101101100011111100111111011010111111001111111111111111111111111111111111111111;";
        try {
            Reader reader = new StringReader(signature);
            GraphAutomatonReader aReader = new GraphAutomatonReader();
            Automaton automaton = aReader.read(reader);
            RewriteEngine repairer = new FixedOrderRepairer();
            Automaton newAutomaton = repairer.rewrite(automaton);
            assertTrue("reduced", newAutomaton.isReduced());
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception");;
        }
    }
    
    public void testNonSore1() {
        final String[][] sample = {
                {"a"},
                {"a", "b", "a"}
        };
        final String targetRegex = "(+ (. (a) (? (b))))";
        checkExpression(sample, targetRegex);
    }

    public void testNonSoreOptimal1() {
        final String[][] sample = {
                {"a"},
                {"a", "b", "a"}
        };
        final String targetRegex = "(+ (. (a) (? (b))))";
        checkExpressionOptimal(sample, targetRegex);
    }
    
    public void testMemoryProblem1() {
        BigInteger index = new BigInteger("64935");
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(index, 3);
        assertTrue("sound", GraphAutomatonFactory.isSound(automaton));
        RewriteEngine rewriter = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          Integer.MAX_VALUE);
       Automaton newAutomaton = rewriter.rewrite(automaton);
       assertTrue("reduced", newAutomaton.isReduced());
    }

    public void testMaxIndex() {
        BigInteger index = new BigInteger("23998");
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(index, 3);
        final int minIndex = 4;
        final int maxIndex = 5;
        for (int i = minIndex; i <= maxIndex; i++) {
            RewriteEngine rewriter = new BacktrackingRepairer(new LanguageSizeMeasure(), i);
            Automaton newAutomaton = rewriter.rewrite(automaton);
            try {
                rewriter.rewriteToRegex(newAutomaton);
            } catch (NoOpportunityFoundException e) {
                e.printStackTrace();
                fail("unexpected exception");
            }
        }
    }

    protected void checkExpression(String[][] sample,
                                   final String targetRegexStr) {
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA targetNfa = glushkov.create(targetRegexStr);
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            Automaton automaton = factory.create(sample);
            RewriteEngine rewriter = new FixedOrderRepairer();
            String regexStr = rewriter.rewriteToRegex(automaton);
            ModifiableStateNFA nfa = glushkov.create(regexStr);
            assertTrue("equiv", EquivalenceTest.areEquivalent(targetNfa, nfa));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
            e.printStackTrace();
            fail("unexpected exception" + e.getAutomaton().toString());
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    protected void checkExpressionOptimal(String[][] sample,
                                          final String targetRegexStr) {
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA targetNfa = glushkov.create(targetRegexStr);
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            Automaton automaton = factory.create(sample);
            RewriteEngine rewriter = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                              Integer.MAX_VALUE);
            String regexStr = rewriter.rewriteToRegex(automaton);
            ModifiableStateNFA nfa = glushkov.create(regexStr);
            assertTrue("equiv", EquivalenceTest.areEquivalent(targetNfa, nfa));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
            e.printStackTrace();
            fail("unexpected exception" + e.getAutomaton().toString());
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

}
