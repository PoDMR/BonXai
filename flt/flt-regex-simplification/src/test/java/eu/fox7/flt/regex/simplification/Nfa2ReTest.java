package eu.fox7.flt.regex.simplification;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.io.SimpleWriter;
import eu.fox7.flt.regex.Glushkov;


/**
 * Test class for {@link Nfa2ReClass}.
 * @author Jonny Daenen
 *
 */
public class Nfa2ReTest  extends TestCase {

	    public static Test suite() {
	        return new TestSuite(Nfa2ReTest.class);
	    }
	    
	    public void testCorrect() {
	    	try {
				assertTrue(Nfa2Re_correctTest(1, 1000));
			} catch (Exception e) {
				fail();
			}
            
	    }
	    
	    public void testBottomUp() {
	    	try {
        	   assertTrue(Nfa2Re_withBottomUp_correctTest(5, 1000));
	         
			} catch (Exception e) {
				fail();
			}
           
	    }
	    
	    public void testRegex() {
	    	try {
	            assertTrue(Nfa2Re_regexTest(1000));
			} catch (Exception e) {
				fail();
			}
           
	    }
	    
	    public void testBottomUpRegex() {
	    	try {
        	   assertTrue(Nfa2Re_withBottomUp_regexTest(1000));
			} catch (Exception e) {
				fail();
			}
           
	    }
	    
	    public void testHeuristics() {
	    	 try {
	            assertTrue(heuristicTest(5, 1000));
			} catch (Exception e) {
				fail();
			}
           
	    }
	    

	    /**
	     * this function tests the Nfa2Re() function using random automata and stops if a computed regular expression is incorrect
	     * @param option        the kind of test: 0 -> load NFA from "input_nfa.txt", 1 -> trivial automaton, 2 -> random Glushkov, 3 -> minimized DFA, 4 -> DFA, 5 -> regular expression
	     * @param amount        the number of random automata for the test
	     * @return              true if successful, false if computed regular expression incorrect
	     * @throws Exception
	     */
	    public static boolean Nfa2Re_correctTest(int option, int amount) throws Exception { //TODO random Glushkov -> states become very big (q125312 etc) after few cycles -> extra lag?
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
	            boolean acceptEmpty = false;
	            String regex = Nfa2ReClass.Nfa2Re(A, acceptEmpty);

	            // convert calculated regex to automaton and compare
	            A = AutomatonTools.correctTransitionLabelsWithoutEpsilon(A);
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

	    /**
	     * this function tests the Nfa2Re_withBottomUp() function using random automata and stops if a computed regular expression is incorrect
	     * @param option        the kind of test: 0 -> load NFA from "input_nfa.txt", 1 -> trivial automaton, 2 -> random Glushkov, 3 -> minimized DFA, 4 -> DFA, 5 -> regular expression
	     * @param amount        the number of random automata for the test
	     * @return              true if successful, false if computed regular expression incorrect
	     * @throws Exception
	     */
	    public static boolean Nfa2Re_withBottomUp_correctTest(int option, int amount) throws Exception { //TODO random Glushkov -> states become very big (q125312 etc) after few cycles -> extra lag?
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
	            boolean acceptEmpty = false;
	            String regex = Nfa2ReClass.Nfa2Re_withBottomUp(A, acceptEmpty);

	            // convert calculated regex to automaton and compare
	            A = AutomatonTools.correctTransitionLabelsWithoutEpsilon(A);
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

	    /**
	     * this function tests the Nfa2Re() function using a random regular expressions, which are converted to automata, and stops if the computed regular expression has more alphabet symbols (= states) than the original
	     * @param amount        the number of random automata for the test
	     * @return              true if successful, false if computed regular expression bigger than original
	     * @throws Exception
	     */
	    public static boolean Nfa2Re_regexTest(int amount) throws Exception {
	        for (int i = 0; i < amount; i++) {
	            // generate automaton from random regex
	            String regex1 = AutomatonTools.getRandomRegex();
	            SparseNFA A = AutomatonTools.regexToGlushkov(regex1);

	            // set acceptEmpty based on if Glushkov.INITIAL_STATE is a final state in automaton
	            boolean acceptEmpty;
	            if (A.isFinalState(Glushkov.INITIAL_STATE)) {
	                Set<String> finalStateValueSet = new HashSet<String>(A.getFinalStateValues());
	                finalStateValueSet.remove(Glushkov.INITIAL_STATE);
	                A.setFinalStateValues(finalStateValueSet); // Glushkov.INITIAL_STATE no longer final state
	                acceptEmpty = true;
	            } else {
	                acceptEmpty = false;
	            }

	            // calculate new regex from generated automaton
	            String regex2 = Nfa2ReClass.Nfa2Re(A, acceptEmpty);

	            // calcuate if new regex has equal or less alphabet symbols (= states) then original regex
	            int numStates1 = regex1.replaceAll("[^S]", "").length();
	            int numStates2 = regex2.replaceAll("[^S]", "").length();
	            if (numStates1 >= numStates2) {
	                System.out.println("Success!! Count: " + (i + 1));
	            } else {
	                System.out.println("Failed!! Count: " + (i + 1));
	                System.out.println("See 'output_regex_error.txt', 'output_nfa_error.txt' and 'nfa_error.pdf' for more info");
	                FileWriter fileWriter = new FileWriter("output_regex_error.txt");
	                fileWriter.write("\noriginal regular expression: ");
	                String regexHuman1 = AutomatonTools.convertRegexComputerToHuman(regex1);
	                fileWriter.write(regexHuman1);
	                fileWriter.write("\nregular expression calculated by Nfa2Re(): ");
	                String regexHuman2 = AutomatonTools.convertRegexComputerToHuman(regex2);
	                fileWriter.write(regexHuman2);
	                fileWriter.close();
	                AutomatonTools.saveNFA(A, "output_nfa_error.txt");
	                AutomatonTools.writeNFAToPDF(A, "nfa_error.pdf");
	                return false;
	            }
	        }

	        return true;
	    }

	    /**
	     * this function tests the Nfa2Re_withBottomUp() function using a random regular expressions, which are converted to automata, and stops if the computed regular expression has more alphabet symbols (= states) than the original
	     * @param amount        the number of random automata for the test
	     * @return              true if successful, false if computed regular expression bigger than original
	     * @throws Exception
	     */
	    public static boolean Nfa2Re_withBottomUp_regexTest(int amount) throws Exception {
	        for (int i = 0; i < amount; i++) {
	            // generate automaton from random regex
	            String regex1 = AutomatonTools.getRandomRegex();
	            SparseNFA A = AutomatonTools.regexToGlushkov(regex1);

	            // set acceptEmpty based on if Glushkov.INITIAL_STATE is a final state in automaton
	            boolean acceptEmpty;
	            if (A.isFinalState(Glushkov.INITIAL_STATE)) {
	                Set<String> finalStateValueSet = new HashSet<String>(A.getFinalStateValues());
	                finalStateValueSet.remove(Glushkov.INITIAL_STATE);
	                A.setFinalStateValues(finalStateValueSet); // Glushkov.INITIAL_STATE no longer final state
	                acceptEmpty = true;
	            } else {
	                acceptEmpty = false;
	            }

	            // calculate new regex from generated automaton
	            String regex2 = Nfa2ReClass.Nfa2Re_withBottomUp(A, acceptEmpty);

	            // calcuate if new regex has equal or less alphabet symbols (= states) then original regex
	            int numStates1 = regex1.replaceAll("[^S]", "").length();
	            int numStates2 = regex2.replaceAll("[^S]", "").length();
	            if (numStates1 >= numStates2) {
	                System.out.println("Success!! Count: " + (i + 1));
	            } else {
	                System.out.println("Failed!! Count: " + (i + 1));
	                System.out.println("See 'output_regex_error.txt', 'output_nfa_error.txt' and 'nfa_error.pdf' for more info");
	                FileWriter fileWriter = new FileWriter("output_regex_error.txt");
	                fileWriter.write("\noriginal regular expression: ");
	                String regexHuman1 = AutomatonTools.convertRegexComputerToHuman(regex1);
	                fileWriter.write(regexHuman1);
	                fileWriter.write("\nregular expression calculated by Nfa2Re_withBottomUp(): ");
	                String regexHuman2 = AutomatonTools.convertRegexComputerToHuman(regex2);
	                fileWriter.write(regexHuman2);
	                fileWriter.close();
	                AutomatonTools.saveNFA(A, "output_nfa_error.txt");
	                AutomatonTools.writeNFAToPDF(A, "nfa_error.pdf");
	                return false;
	            }
	        }

	        return true;
	    }

	    /**
	     * this function test the Nfa2Re(), Nfa2Re_withBottomUp() and the stateWeight() functions using random automata, logs the results and stops if a computed regular expression is incorrect. can be modified for different tests.
	     * @param option        the kind of test: 0 -> load NFA from "input_nfa.txt", 1 -> trivial automaton, 2 -> random Glushkov, 3 -> minimized DFA, 4 -> DFA, 5 -> regular expression
	     * @param amount        the number of random automata for the test
	     * @return              true if successful, false if computed regular expression incorrect
	     * @throws Exception
	     */
	    public static boolean heuristicTest(int option, int amount) throws Exception {
	        // prepare FAdo for option 4
	        List<SparseNFA> AList;
	        Iterator<SparseNFA> it = null;
	        if (option == 4) {
	            AList = FAdoDatabase.getRandomDFAs(amount);
	            if (AList.isEmpty()) {
	                System.out.println("ERROR: FAdo returned no automata");
	                return false;
	            }
	            it = AList.iterator();
	        }

	        // copy used files
	        if (option == 2) { // copy "glushkov.ini"
	            AutomatonTools.fileCopy("glushkov.ini", "LOG_heuristicTest_2_gluskov.ini");
	        } else if (option == 3) { // copy "glushkov.ini"
	            AutomatonTools.fileCopy("glushkov.ini", "LOG_heuristicTest_3_glushkov.ini");
	        } else if (option == 4) { // copy "fado.ini"
	            AutomatonTools.fileCopy("fado.ini", "LOG_heuristicTest_4_fado.ini");
	        }

	        // write data to LOG file
	        FileWriter log = new FileWriter("LOG_heuristicTest_" + option + ".log");
	        log.write("Test executed using option " + option + " and amount " + amount + "\n");
	        log.write("See function heuristicTest() for more info\n");
	        log.write("\n\n");
	        SimpleWriter logNFA = new SimpleWriter(log); // for writing NFA's

	        // write data to R file
	        FileWriter RFileAlphaCount = new FileWriter("R_heuristicTest_" + option + "_alphaSymbols_count.dat"); // alphabet symbols count
	        RFileAlphaCount.write("Nfa2Re Nfa2Re_with_BottomUp State_Weight\n");
	        FileWriter RFileAllCount = new FileWriter("R_heuristicTest_" + option + "_allSymbols_count.dat"); // all symbols count
	        RFileAllCount.write("Nfa2Re Nfa2Re_with_BottomUp State_Weight\n");
	        FileWriter RFileAlphaDiff = new FileWriter("R_heuristicTest_" + option + "_alphaSymbols_diff.dat"); // alphabet symbols difference count
	        RFileAlphaDiff.write("Nfa2Re_vs_Nfa2Re_with_BottomUp Nfa2Re_vs_State_Weight Nfa2Re_with_BottomUp_vs_State_Weight\n");
	        FileWriter RFileAllDiff = new FileWriter("R_heuristicTest_" + option + "_allSymbols_diff.dat"); // all symbols difference count
	        RFileAllDiff.write("Nfa2Re_vs_Nfa2Re_with_BottomUp Nfa2Re_vs_State_Weight Nfa2Re_with_BottomUp_vs_State_Weight\n");

	        // start the test by creating new automaton
	        boolean alphaWin1 = false, alphaWin2 = false, alphaWin3 = false, allWin1 = false, allWin2 = false, allWin3 = false; // 1 -> Nfa2Re(), 2 -> Nfa2Re_withBottomUp(), 3 -> StateWeight()
	        int alphaWins1 = 0, alphaWins2 = 0, alphaWins3 = 0, allWins1 = 0, allWins2 = 0, allWins3 = 0; // counted wins
	        float alphaWinAvg1 = 0, alphaWinAvg2 = 0, alphaWinAvg3 = 0, allWinAvg1 = 0, allWinAvg2 = 0, allWinAvg3 = 0; // regex alphabet/all symbols averages
	        float alphaDiffAvg12 = 0, alphaDiffAvg13 = 0, alphaDiffAvg23 = 0, allDiffAvg12 = 0, allDiffAvg13 = 0, allDiffAvg23 = 0; // average alphabet/all symbols difference between two regex
	        float nfaStateAvg = 0, nfaTransitionAvg = 0; // automaton averages
	        long startTime = System.currentTimeMillis();
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
	            nfaStateAvg += A.getNumberOfStates();
	            nfaTransitionAvg += A.getNumberOfTransitions();

	            // calculate regex from generated automaton
	            A = AutomatonTools.correctTransitionLabelsWithoutEpsilon(A);
	            boolean acceptEmpty = false;
	            String regex1 = Nfa2ReClass.Nfa2Re(A, acceptEmpty);
	            String regex2 = Nfa2ReClass.Nfa2Re_withBottomUp(A, acceptEmpty);
	            String regex3 = Weight.stateWeight(A, acceptEmpty);

	            // check if 3 calculated regex are equivalent
	            SparseNFA A1 = AutomatonTools.regexToGlushkov(regex1);
	            SparseNFA A2 = AutomatonTools.regexToGlushkov(regex2);
	            SparseNFA A3 = AutomatonTools.regexToGlushkov(regex3);
	            boolean areEquivalent1 = AutomatonTools.compareNFAs(A1, A2);
	            boolean areEquivalent2 = AutomatonTools.compareNFAs(A1, A3);
	            if (areEquivalent1 && areEquivalent2) {
	                System.out.println("Success!! Count: " + (i + 1));
	            } else {
	                System.out.println("Failed!! Count: " + (i + 1));
	                System.out.println("See 'output_regex_error.txt', 'output_nfa_error.txt' and 'nfa_error.pdf' for more info");
	                FileWriter fileWriter = new FileWriter("output_regex_error.txt");
	                fileWriter.write("\nregular expression calculated by Nfa2Re(): ");
	                String regexHuman1 = AutomatonTools.convertRegexComputerToHuman(regex1);
	                fileWriter.write(regexHuman1);
	                fileWriter.write("\nregular expression calculated by Nfa2Re_withBottomUp(): ");
	                String regexHuman2 = AutomatonTools.convertRegexComputerToHuman(regex2);
	                fileWriter.write(regexHuman2);
	                fileWriter.write("\nregular expression calculated by StateWeight(): ");
	                String regexHuman3 = AutomatonTools.convertRegexComputerToHuman(regex3);
	                fileWriter.write(regexHuman3);
	                fileWriter.close();
	                AutomatonTools.saveNFA(A, "output_nfa_error.txt");
	                AutomatonTools.writeNFAToPDF(A, "nfa_error.pdf");
	                return false;
	            }

	            // calculate winner(s) when counting alphabet symbols
	            String regex1AlphaSymbols = regex1.replaceAll("[|+.*?() 0-9_]", "");
	            String regex2AlphaSymbols = regex2.replaceAll("[|+.*?() 0-9_]", "");
	            String regex3AlphaSymbols = regex3.replaceAll("[|+.*?() 0-9_]", "");
	            int min = Math.min(regex1AlphaSymbols.length(), regex2AlphaSymbols.length());
	            min = Math.min(min, regex3AlphaSymbols.length());
	            if (regex1AlphaSymbols.length() == min) {
	                alphaWin1 = true;
	                alphaWins1++;
	            }
	            if (regex2AlphaSymbols.length() == min) {
	                alphaWin2 = true;
	                alphaWins2++;
	            }
	            if (regex3AlphaSymbols.length() == min) {
	                alphaWin3 = true;
	                alphaWins3++;
	            }

	            // calcuate winner(s) when counting all symbols
	            String regex1AllSymbols = regex1.replaceAll("[() 0-9_]", "");
	            String regex2AllSymbols = regex2.replaceAll("[() 0-9_]", "");
	            String regex3AllSymbols = regex3.replaceAll("[() 0-9_]", "");
	            min = Math.min(regex1AllSymbols.length(), regex2AllSymbols.length());
	            min = Math.min(min, regex3AllSymbols.length());
	            if (regex1AllSymbols.length() == min) {
	                allWin1 = true;
	                allWins1++;
	            }
	            if (regex2AllSymbols.length() == min) {
	                allWin2 = true;
	                allWins2++;
	            }
	            if (regex3AllSymbols.length() == min) {
	                allWin3 = true;
	                allWins3++;
	            }

	            // calculate averages
	            alphaWinAvg1 += regex1AlphaSymbols.length();
	            alphaWinAvg2 += regex2AlphaSymbols.length();
	            alphaWinAvg3 += regex3AlphaSymbols.length();
	            allWinAvg1 += regex1AllSymbols.length();
	            allWinAvg2 += regex2AllSymbols.length();
	            allWinAvg3 += regex3AllSymbols.length();
	            alphaDiffAvg12 += regex2AlphaSymbols.length() - regex1AlphaSymbols.length();
	            alphaDiffAvg13 += regex3AlphaSymbols.length() - regex1AlphaSymbols.length();
	            alphaDiffAvg23 += regex3AlphaSymbols.length() - regex2AlphaSymbols.length();
	            allDiffAvg12 += regex2AllSymbols.length() - regex1AllSymbols.length();
	            allDiffAvg13 += regex3AllSymbols.length() - regex1AllSymbols.length();
	            allDiffAvg23 += regex3AllSymbols.length() - regex2AllSymbols.length();

	            // write data to console
	            System.out.println("Nfa2Re alphabet symbols wins: " + alphaWins1);
	            System.out.println("Nfa2Re with BottomUp alphabet symbols wins: " + alphaWins2);
	            System.out.println("State Weight alphabet symbols winst: " + alphaWins3);
	            System.out.println("Nfa2Re all symbols wins: " + allWins1);
	            System.out.println("Nfa2Re with BottomUp all symbols wins: " + allWins2);
	            System.out.println("State Weight all symbols wins: " + allWins3);
	            System.out.println();

	            // write data to LOG file
	            log.write("automaton number " + (i + 1) + "\n");
	            log.write("\n");
	            logNFA.write(A);
	            log.write("\n");
	            String regexHuman1 = AutomatonTools.convertRegexComputerToHuman(regex1);
	            log.write("Nfa2Re regular expression:               " + regexHuman1 + "\n");
	            String regexHuman2 = AutomatonTools.convertRegexComputerToHuman(regex2);
	            log.write("Nfa2Re with BottomUp regular expression: " + regexHuman2 + "\n");
	            String regexHuman3 = AutomatonTools.convertRegexComputerToHuman(regex3);
	            log.write("State Weight regular expression:         " + regexHuman3 + "\n");
	            log.write("\n");
	            log.write("Nfa2Re regular expression has               " + regex1AlphaSymbols.length() + " alphabet symbol(s)\n");
	            log.write("Nfa2Re with BottomUp regular expression has " + regex2AlphaSymbols.length() + " alphabet symbol(s)\n");
	            log.write("State Weight regular expression has         " + regex3AlphaSymbols.length() + " alphabet symbol(s)\n");
	            if (alphaWin1) {
	                log.write("Nfa2Re, ");
	            }
	            if (alphaWin2) {
	                log.write("Nfa2Re with BottomUp, ");
	            }
	            if (alphaWin3) {
	                log.write("State Weight, ");
	            }
	            log.write("win(s) when counting alphabet symbols\n");
	            log.write("\n");
	            log.write("Nfa2Re regular expression has               " + regex1AllSymbols.length() + " all symbol(s)\n");
	            log.write("Nfa2Re with BottomUp regular expression has " + regex2AllSymbols.length() + " all symbol(s)\n");
	            log.write("State Weight regular expression has         " + regex3AllSymbols.length() + " all symbol(s)\n");
	            if (allWin1) {
	                log.write("Nfa2Re, ");
	            }
	            if (allWin2) {
	                log.write("Nfa2Re with BottomUp, ");
	            }
	            if (allWin3) {
	                log.write("State Weight, ");
	            }
	            log.write("win(s) when counting all symbols\n");
	            log.write("\n\n");

	            // write data to R file
	            RFileAlphaCount.write(regex1AlphaSymbols.length() + " " + regex2AlphaSymbols.length() + " " + regex3AlphaSymbols.length() + "\n");
	            RFileAllCount.write(regex1AllSymbols.length() + " " + regex2AllSymbols.length() + " " + regex3AllSymbols.length() + "\n");
	            RFileAlphaDiff.write((regex2AlphaSymbols.length() - regex1AlphaSymbols.length())
	                    + " " + (regex3AlphaSymbols.length() - regex1AlphaSymbols.length()) + " "
	                    + (regex3AlphaSymbols.length() - regex2AlphaSymbols.length()) + "\n");
	            RFileAllDiff.write((regex2AllSymbols.length() - regex1AllSymbols.length())
	                    + " " + (regex3AllSymbols.length() - regex1AllSymbols.length()) + " "
	                    + (regex3AllSymbols.length() - regex2AllSymbols.length()) + "\n");
	        }
	        long stopTime = System.currentTimeMillis();

	        // calculate averages
	        alphaWinAvg1 /= amount;
	        alphaWinAvg2 /= amount;
	        alphaWinAvg3 /= amount;
	        allWinAvg1 /= amount;
	        allWinAvg2 /= amount;
	        allWinAvg3 /= amount;
	        alphaDiffAvg12 /= amount;
	        alphaDiffAvg13 /= amount;
	        alphaDiffAvg23 /= amount;
	        allDiffAvg12 /= amount;
	        allDiffAvg13 /= amount;
	        allDiffAvg23 /= amount;
	        nfaStateAvg /= amount;
	        nfaTransitionAvg /= amount;

	        log.write("\n\n");
	        log.write("total number of automata: " + amount + "\n");
	        log.write("average number of automaton states: " + nfaStateAvg + "\n");
	        log.write("average number of automaton transitions: " + nfaTransitionAvg + "\n");
	        log.write("elapsed time in seconds: " + (stopTime - startTime) / 1000 + "\n");
	        log.write("\n");
	        log.write("total Nfa2Re wins when counting alphabet symbols:               " + alphaWins1 + "\n");
	        log.write("total Nfa2Re with BottomUp wins when counting alphabet symbols: " + alphaWins2 + "\n");
	        log.write("total State Weight wins when counting alphabet symbols:         " + alphaWins3 + "\n");
	        log.write("\n");
	        log.write("total Nfa2Re wins when counting all symbols:               " + allWins1 + "\n");
	        log.write("total Nfa2Re with BottomUp wins when counting all symbols: " + allWins2 + "\n");
	        log.write("total State Weight wins when counting all symbols:         " + allWins3 + "\n");
	        log.write("\n");
	        log.write("average Nfa2Re regular expression alphabet symbols size:               " + alphaWinAvg1 + "\n");
	        log.write("average Nfa2Re with BottomUp regular expression alphabet symbols size: " + alphaWinAvg2 + "\n");
	        log.write("average State Weight regular expression alphabet symbols size:         " + alphaWinAvg3 + "\n");
	        log.write("\n");
	        log.write("average Nfa2Re regular expression all symbols size:               " + allWinAvg1 + "\n");
	        log.write("average Nfa2Re with BottomUp regular expression all symbols size: " + allWinAvg2 + "\n");
	        log.write("average State Weight regular expression all symbols size:         " + allWinAvg3 + "\n");
	        log.write("\n");
	        log.write("average difference in # alphabet symbols of regex between Nfa2Re and Nfa2Re with BottomUp:       " + alphaDiffAvg12 + " (higher is better for Nfa2Re)\n");
	        log.write("average difference in # alphabet symbols of regex between Nfa2Re and State Weight:               " + alphaDiffAvg13 + " (higher is better for Nfa2Re)\n");
	        log.write("average difference in # alphabet symbols of regex between Nfa2Re with BottomUp and State Weight: " + alphaDiffAvg23 + " (higher is better for Nfa2Re with BottomUp)\n");
	        log.write("\n");
	        log.write("average difference in # all symbols of regex between Nfa2Re and Nfa2Re with BottomUp:       " + allDiffAvg12 + " (higher is better for Nfa2Re)\n");
	        log.write("average difference in # all symbols of regex between Nfa2Re and State Weight:               " + allDiffAvg13 + " (higher is better for Nfa2Re)\n");
	        log.write("average difference in # all symbols of regex between Nfa2Re with BottomUp and State Weight: " + allDiffAvg23 + " (higher is better for Nfa2Re with BottomUp)\n");

	        RFileAllDiff.close();
	        RFileAlphaDiff.close();
	        RFileAllCount.close();
	        RFileAlphaCount.close();
	        log.close();
	        return true;
	    }
}
