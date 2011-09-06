/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NFAException;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.RegexException;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.measures.LanguageTest;
import eu.fox7.flt.regex.measures.SingleOccurrenceTest;
import eu.fox7.flt.regex.random.Mutator;
import eu.fox7.flt.regex.random.NewSymbolMutator;
import eu.fox7.flt.regex.random.NoMutationFoundException;
import eu.fox7.flt.regex.random.SoreConservingSymbolMutator;
import eu.fox7.flt.regex.random.SymbolMutator;
import eu.fox7.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class MutationTest extends TestCase {

    public static Test suite() {
        return new TestSuite(MutationTest.class);
    }

    public void test_soreConservation() {
        final String[] regexStrs = {
                "(. (a) (b) (c))",
                "(. (| (a) (b)) (c))",
                "(? (+ (| (a) (. (b) (c) (? (d))))))"
        };
        Mutator mutator = new SoreConservingSymbolMutator();
        Glushkov glushkov = new Glushkov();
        LanguageTest soreTest = new SingleOccurrenceTest();
        for (String regexStr : regexStrs) {
            try {
                String newRegexStr = mutator.mutate(regexStr);
                assertFalse("ambiguous " + regexStr, glushkov.isAmbiguous(newRegexStr));
                assertTrue("sore " + regexStr, soreTest.test(newRegexStr));
            } catch (SExpressionParseException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (NoMutationFoundException e) {
                e.printStackTrace();
                fail("no mutation for " + regexStr);
            } catch (UnknownOperatorException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (FeatureNotSupportedException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (NFAException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (RegexException e) {
                e.printStackTrace();
                fail("no exception expected");
            }
        }
    }

    public void test_soreConservationFailure() {
        final String[] regexStrs = {
                "(| (a) (b) (c))",
                "(| (| (a) (b)) (c))",
                "(EPSILON)",
                "(a)"
        };
        Mutator mutator = new SoreConservingSymbolMutator();
        for (String regexStr : regexStrs) {
            try {
                mutator.mutate(regexStr);
                fail("should not be mutable " + regexStr);
            } catch (SExpressionParseException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (NoMutationFoundException e) {}
        }
    }
    

    public void test_symbolFailure() {
        final String[] regexStrs = {
                "(| (a) (b) (c))",
                "(| (| (a) (b)) (c))",
                "(EPSILON)",
                "(a)"
        };
        Mutator mutator = new SymbolMutator();
        for (String regexStr : regexStrs) {
            try {
                mutator.mutate(regexStr);
                fail("should not be mutable " + regexStr);
            } catch (SExpressionParseException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (NoMutationFoundException e) {}
        }
    }
    
    public void test_newSymbol() {
        final String[] regexStrs = {
                "(. (a) (b) (c))",
                "(. (| (a) (b)) (c))",
                "(? (+ (| (a) (. (b) (c) (? (d))))))",
                "(| (a) (b) (c))",
                "(| (| (a) (b)) (c))",
                "(EPSILON)",
                "(a)"
        };
        Mutator mutator = new NewSymbolMutator();
        Glushkov glushkov = new Glushkov();
        LanguageTest soreTest = new SingleOccurrenceTest();
        for (String regexStr : regexStrs) {
            try {
                String newRegexStr = mutator.mutate(regexStr);
                assertFalse("ambiguous " + regexStr, glushkov.isAmbiguous(newRegexStr));
                assertTrue("sore " + newRegexStr, soreTest.test(newRegexStr));
            } catch (SExpressionParseException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (NoMutationFoundException e) {
                e.printStackTrace();
                fail("no mutation for " + regexStr);
            } catch (UnknownOperatorException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (FeatureNotSupportedException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (NFAException e) {
                e.printStackTrace();
                fail("no exception expected");
            } catch (RegexException e) {
                e.printStackTrace();
                fail("no exception expected");
            }
        }
    }

}
