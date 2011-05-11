/*
 * Created on Sep 8, 2008
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
import gjb.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import gjb.util.tree.SExpressionParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import junit.framework.TestCase;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class LargeScaleTest extends TestCase {

    protected boolean isVerbose = false;

    public void test_regex_1() {
        File file = new File("test-data/non-repaired-regex-1.txt");
        compute(file);
    }

    public void test_regex_2_sore() {
        File file = new File("test-data/non-repaired-regex-2.txt");
        compute(file);
    }

    public void test_regex_3_sore() {
        File file = new File("test-data/non-repaired-regex-3.txt");
        compute(file);
    }

    public void test_regex_2() {
        File file = new File("test-data/all-regex-2.txt");
        final int alphabetSize = 2;
        RewriteEngine repairer = new FixedOrderRepairer();
        computeRepairs(file, alphabetSize, repairer);
    }

    public void test_regex_2_optimal() {
        File file = new File("test-data/all-regex-2.txt");
        final int alphabetSize = 2;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          Integer.MAX_VALUE);
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_3_sample() {
        File file = new File("test-data/repaired-regex-3-sample.txt");
        final int alphabetSize = 3;
        RewriteEngine repairer = new FixedOrderRepairer();
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_3_optimal() {
        File file = new File("test-data/repaired-regex-3-sample.txt");
        final int alphabetSize = 3;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          Integer.MAX_VALUE);
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_3_all_optimal() {
        File file = new File("test-data/all-expr-3.txt");
        final int alphabetSize = 3;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          Integer.MAX_VALUE);
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_5_sample() {
        File file = new File("test-data/regex-5-sample.txt");
        final int alphabetSize = 5;
        RewriteEngine repairer = new FixedOrderRepairer();
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_5_optimal() {
        File file = new File("test-data/regex-5-sample.txt");
        final int alphabetSize = 5;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          Integer.MAX_VALUE);
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_10_sample() {
        File file = new File("test-data/regex-10-sample.txt");
        final int alphabetSize = 10;
        RewriteEngine repairer = new FixedOrderRepairer();
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_10_best1() {
        File file = new File("test-data/regex-10-sample.txt");
        final int alphabetSize = 10;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          1);
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_10_best2() {
        File file = new File("test-data/regex-10-sample.txt");
        final int alphabetSize = 10;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          2);
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_10_best3() {
        File file = new File("test-data/regex-10-sample.txt");
        final int alphabetSize = 10;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          3);
        computeRepairs(file, alphabetSize, repairer);
    }
    
    public void test_regex_10_best4() {
        File file = new File("test-data/regex-10-sample.txt");
        final int alphabetSize = 10;
        RewriteEngine repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),
                                                          4);
        computeRepairs(file, alphabetSize, repairer);
    }

    public void test_regex_real_world_sores() {
        File file = new File("test-data/real-world-sores.txt");
        compute(file);
    }

    protected void compute(File file) {
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            RewriteEngine rewriter = new Rewriter();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String targetRegexStr = null;
                if (line.indexOf("\t") >= 0) {
                    String[] data = line.trim().split("\t");
                    targetRegexStr = data[1];
                } else {
                    targetRegexStr = line.trim();
                }
                // System.err.println("trying " + targetRegexStr);
                SparseNFA targetNfa = glushkov.create(targetRegexStr);
                Automaton automaton = factory.create(targetRegexStr);
                String regexStr = null;
                try {
                    regexStr = rewriter.rewriteToRegex(automaton);
                } catch (NoOpportunityFoundException e) {
                    e.printStackTrace();
                    fail("no opportunity for " + targetRegexStr);
                }
                ModifiableStateNFA nfa = glushkov.create(regexStr);
                assertTrue("equiv " + targetRegexStr + " vs " + regexStr,
                           EquivalenceTest.areEquivalent(targetNfa, nfa));
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
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    protected void computeRepairs(File file, int alphabetSize,
                                  RewriteEngine repairer) {
        int lineNumber = 0;
        BigInteger index = null;
        try {
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] data = line.trim().split("\t");
                index = new BigInteger(data[0]);
                if (isVerbose)
                    System.err.println("trying " + index.toString() + " at line " + lineNumber);
                Automaton automaton = factory.create(index, alphabetSize);
                if (GraphAutomatonFactory.isSound(automaton)) {
                    Automaton newAutomaton = repairer.rewrite(automaton);
                    assertTrue("reduced " + lineNumber, newAutomaton.isReduced());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (RuntimeException e) {
            System.err.println("failed for " + index.toString() + " at line " + lineNumber);
            e.printStackTrace();
        }
    }
    
}
