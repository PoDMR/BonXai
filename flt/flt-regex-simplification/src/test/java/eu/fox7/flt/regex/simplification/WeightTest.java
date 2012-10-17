package eu.fox7.flt.regex.simplification;

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;


/**
 * Test class for {@link Weight}.
 * @author Jonny Daenen
 *
 */
public class WeightTest  extends TestCase {

	    public static Test suite() {
	        return new TestSuite(WeightTest.class);
	    }
	    
	    public void testWeight() {

            try {
				assertTrue(stateWeight_correctTest(5, 1000));
			} catch (Exception e) {
				fail();
			}
	    }
	    
	    /**
	     * this function tests the stateWeight() function using random automata and stops if a computed regular expression is incorrect
	     * @param option        the kind of test: 0 -> load NFA from "input_nfa.txt", 1 -> trivial automaton, 2 -> random Glushkov, 3 -> minimized DFA, 4 -> DFA, 5 -> regular expression
	     * @param amount        the number of random automata for the test
	     * @return              true if succes, false if computed regular expression incorrect
	     * @throws Exception
	     */
	    public static boolean stateWeight_correctTest(int option, int amount) throws Exception {
	        // prepare FAdo for option 4
	        List<SparseNFA> AList;
	        Iterator<SparseNFA> it = null;
	        if (option == 4) {
	            AList = FAdoDatabase.getRandomDFAs(amount);
	            if (AList.isEmpty()) {
	                System.out.println("ERROR: FAdo didn't return any automata");
	                return false;
	            }
	            if (AList.size() < amount) {
	                System.out.println("ERROR: FAdo didn't return enough automata");
	                return false;
	            }
	            it = AList.iterator();
	        }

	        // start the test by creating new automaton
	        for (int i = 0; i < amount; i++) {
	            SparseNFA A;
	            switch (option) {
	                case 0: // load NFA from "input_nfa.txt"
	                    A = AutomatonTools.loadNFA("input_nfa.txt");
	                    break;
	                case 1: // trivial automaton
	                    A = AutomatonTools.getTrivialAutomaton();
	                    break;
	                case 2: // random Glushkov
	                    A = AutomatonTools.getRandomGlushkov();
	                    break;
	                case 3: // minimized DFA
	                    A = AutomatonTools.getRandomGlushkov();
	                    A = AutomatonTools.getMinimizedDFA(A);
	                    break;
	                case 4: // DFA
	                    A = it.next();
	                    break;
	                case 5: // regular expression
	                    A = AutomatonTools.getRandomRegexGlushkov();
	                    break;
	                default:
	                    return false;
	            }

	            // calculate regex from generated automaton
	            A = AutomatonTools.correctTransitionLabelsWithoutEpsilon(A);
	            boolean acceptEmpty = false;
	            String regex = Weight.stateWeight(A, acceptEmpty);

	            // convert calculated regex to automaton and compare
	            SparseNFA A2 = AutomatonTools.regexToGlushkov(regex);
	            boolean areEquivalent = AutomatonTools.compareNFAs(A, A2);

	            if (areEquivalent) {
	                System.out.println("Success!! Count: " + (i + 1));
	            } else {
	                System.out.println("Failed!! Count: " + (i + 1));
	                System.out.println("See 'output_nfa_error.txt' and 'nfa_error.pdf' for more info");
	                AutomatonTools.saveNFA(A, "output_nfa_error.txt");
	                AutomatonTools.writeNFAToPDF(A, "nfa_error.pdf");
	                return false;
	            }
	        }

	        return true;
	    }
}
