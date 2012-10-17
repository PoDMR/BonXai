package eu.fox7.flt.regex.simplification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.converters.Simplifier;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.automata.io.DotWriter;
import eu.fox7.flt.automata.io.NFAReadException;
import eu.fox7.flt.automata.io.NFAWriteException;
import eu.fox7.flt.automata.io.SimpleReader;
import eu.fox7.flt.automata.io.SimpleWriter;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.automata.random.RandomGlushkovNFAFactory;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.random.RandomRegexFactory;
import eu.fox7.util.tree.SExpressionParseException;
//import eu.fox7.flt.automata.random.ConfigurationException;
//import eu.fox7.flt.regex.random.ConfigurationException;

/**
 * @author Jeroen Appermont 0726039
 */
public class AutomatonTools {

    private static Properties globalProperties;   // the configuration setting from config file 'glushkov.ini'

    /**
     * returns trivial automaton
     * @return      the trivial automaton
     */
    public static SparseNFA getTrivialAutomaton() {
        SparseNFA nfa = new SparseNFA();
        nfa.setInitialState(Glushkov.INITIAL_STATE); // points to all 'real' initial states
        nfa.addFinalState("q2");
        nfa.addTransition(Symbol.getEpsilon().toString(), Glushkov.INITIAL_STATE, "q2");

        return nfa;
    }

    /**
     * returns randomly generated Gluskov automaton using config file 'glushkov.ini'
     * @return      the randomly generated Gluskov automaton
     * @throws eu.fox7.flt.automata.random.ConfigurationException
     * @throws FileNotFoundException
     */
    public static SparseNFA getRandomGlushkov() throws eu.fox7.flt.automata.random.ConfigurationException, FileNotFoundException {
        // create RandomGlushkovNFAFactory
        Properties prop = createGlushkovProperties();
        RandomGlushkovNFAFactory randomGlushkovFactory = new RandomGlushkovNFAFactory(prop);

        // create random Glushkov
        SparseNFA nfa = randomGlushkovFactory.create();

        return nfa;
    }

    /**
     * creates Properties using config file 'glushkov.ini'
     * @return      the created Properties
     * @throws FileNotFoundException
     */
    private static Properties createGlushkovProperties() throws FileNotFoundException {
        // if globalProperties doesn't exists -> fill in using 'glushkov.ini'
        if (globalProperties == null) {
            globalProperties = new Properties();
            Scanner scFile = new Scanner(new File("glushkov.ini"));
            while (scFile.hasNextLine()) {
                String line = scFile.nextLine();
                if (line.charAt(0) == '#') {
                    continue;
                }
                Scanner scLine = new Scanner(line);
                scLine.useDelimiter("=");
                if (scLine.hasNext()) {
                    String name = scLine.next();
                    String value = scLine.next();
                    globalProperties.setProperty(name, value);
                }
                scLine.close();
            }
            scFile.close();
        }

        // return copy of globalProperties
        return new Properties(globalProperties);
    }

    /**
     * returns a new NFA whose transitions have the stateValue of their toState as label, but transitions to 'real' initial states get epsilon value
     * @param nfa       the input NFA
     * @return          the output NFA
     * @throws NoSuchTransitionException
     */
    public static SparseNFA correctTransitionLabelsWithEpsilon(StateNFA nfa) throws NoSuchTransitionException {
        SparseNFA nfa2 = new SparseNFA(nfa);

        // update all transition labels
        Set<Transition> transitionSet = new HashSet<Transition>(nfa2.getTransitionMap().getTransitions());
        for (Transition transition : transitionSet) {
            State fromState = transition.getFromState();
            State toState = transition.getToState();
            String symbol = nfa2.getStateValue(toState);
            if (nfa2.getInitialState().equals(fromState)) { // use epsilon value for transitions to 'real' initial states
                symbol = Symbol.getEpsilon().toString();
            }
            nfa2.addSymbol(symbol); // add toState's stateValue (or epsilon value) to nfa2's alphabet
            nfa2.removeTransition(transition.getSymbol(), fromState, toState);
            nfa2.addTransition(Symbol.create(symbol), fromState, toState);
        }

        return nfa2;
    }

    /**
     * returns a new NFA whose transitions have the stateValue of their toState as label, including transitions to 'real' initial states
     * @param nfa       the input NFA
     * @return          the output NFA
     * @throws NoSuchTransitionException
     */
    public static SparseNFA correctTransitionLabelsWithoutEpsilon(StateNFA nfa) throws NoSuchTransitionException {
        SparseNFA nfa2 = new SparseNFA(nfa);

        // update all transition labels
        Set<Transition> transitionSet = new HashSet<Transition>(nfa2.getTransitionMap().getTransitions());
        for (Transition transition : transitionSet) {
            State fromState = transition.getFromState();
            State toState = transition.getToState();
            String symbol = nfa2.getStateValue(toState);
            nfa2.addSymbol(symbol); // add toState's stateValue to nfa2's alphabet
            nfa2.removeTransition(transition.getSymbol(), fromState, toState);
            nfa2.addTransition(Symbol.create(symbol), fromState, toState);
        }

        return nfa2;
    }

    /**
     * creates new NFA by removing unnecessary states of input NFA
     * @param nfa       the input NFA
     * @return          a newly created simplified NFA
     */
    public static SparseNFA getSimplifiedNFA(StateNFA nfa) {
        SparseNFA nfa2 = new SparseNFA(nfa);
        Simplifier.simplify(nfa2);
        return nfa2;
    }

    /**
     * creates new DFA by rewriting input NFA as a minimal DFA
     * @param nfa       the input NFA
     * @return          a newly created minimized DFA
     */
    public static SparseNFA getMinimizedDFA(StateNFA nfa) {
        SparseNFA dfa = new SparseNFA(nfa);
        NFAMinimizer minimizer = new NFAMinimizer();
        minimizer.minimize(Determinizer.dfa(dfa));
        return dfa;
    }

    /**
     * returns random boolean
     * @return      the random boolean
     */
    public static boolean getRandomBoolean() {
        Random random = new Random();
        boolean bool = random.nextBoolean();
        return bool;
    }

    /**
     * loads a NFA from a file, using SimpleReader
     * @param filename              load from this file
     * @return                      the loaded NFA
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NFAReadException
     */
    public static SparseNFA loadNFA(String filename) throws FileNotFoundException, IOException, NFAReadException {
        FileReader fileReader = new FileReader(filename);
        SimpleReader simpleReader = new SimpleReader(fileReader);
        SparseNFA nfa = simpleReader.read();
        fileReader.close();
        return nfa;
    }

    /**
     * saves a NFA to a file, using SimpleWriter
     * @param nfa                   NFA to be saved
     * @param filename              save to this file
     * @throws IOException
     * @throws NFAWriteException
     */
    public static void saveNFA(StateNFA nfa, String filename) throws IOException, NFAWriteException {
        FileWriter fileWriter = new FileWriter(filename);
        SimpleWriter simpleWriter = new SimpleWriter(fileWriter);
        simpleWriter.write(nfa);
        fileWriter.close();
    }

    /**
     * writes a NFA to a PDF file using Graphviz
     * @param nfa                   NFA to be written
     * @param filename              write to this file
     * @throws InterruptedException
     * @throws IOException
     * @throws NFAWriteException
     */
    public static void writeNFAToPDF(StateNFA nfa, String filename) throws InterruptedException, IOException, NFAWriteException {
        FileWriter fileWriter = new FileWriter("nfa.dot");
        DotWriter dotWriter = new DotWriter(fileWriter);
        dotWriter.write(nfa);
        fileWriter.close();
        Process p = Runtime.getRuntime().exec("dot -Tpdf nfa.dot -o " + filename);
        p.waitFor();
        File file = new File("nfa.dot");
        file.delete();
    }

    /**
     * quickly logs a NFA to a file, using saveNFA() and writeNFAToPDF(), and catches Exception
     * @param nfa                   NFA to be logged
     */
    public static void quickLog(StateNFA nfa) {
        try {
            saveNFA(nfa, "quickLog.txt");
            writeNFAToPDF(nfa, "quickLog.pdf");
        } catch (Exception ex) {
            System.out.println("EXCEPTION: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * checks if the two NFA's are equivalent
     * @param nfa1              NFA to be compared
     * @param nfa2              NFA to be compared
     * @return                  the boolean
     * @throws NoSuchStateException
     * @throws NoSuchTransitionException
     * @throws NotDFAException
     */
    public static boolean compareNFAs(StateNFA nfa1, StateNFA nfa2) throws NoSuchStateException, NoSuchTransitionException, NotDFAException {
        boolean areEquivalent = EquivalenceTest.areEquivalent(Determinizer.dfa(nfa1), Determinizer.dfa(nfa2));
        return areEquivalent;
    }

    /**
     * returns a new NFA which is the input NFA with only wanted states (Glushkov.INITIAL_STATE is always included)
     * @param nfa                   the input NFA
     * @param wantedStateValueSet   set of wanted states
     * @return                      the new NFA
     * @throws NoSuchStateException
     */
    public static SparseNFA getSubNFA(StateNFA nfa, Set<String> wantedStateValueSet) throws NoSuchStateException {
        SparseNFA nfa2 = new SparseNFA(nfa);
        Set<String> wantedStateValueSet2 = new HashSet<String>(wantedStateValueSet);
        wantedStateValueSet2.add(Glushkov.INITIAL_STATE); // keep Glushkov.INITIAL_STATE in nfa2

        Set<String> stateValueSet = new HashSet<String>(nfa2.getStateValues());
        for (String stateValue : stateValueSet) { // search and remove unwanted states
            if (!wantedStateValueSet2.contains(stateValue)) { // if unwanted state
                nfa2.removeState(stateValue);
            }
        }

        return nfa2;
    }

    /**
     * write WCC to console
     * @param WCC       a WCC
     */
    public static void writeWCCToConsole(Set<String> WCC) {
        System.out.println("\nWCC");
        for (String state : WCC) {
            System.out.print(state + " ");
        }
        System.out.println();
    }

    /**
     * write SCC set to console
     * @param SCCSet    a set of SCC's
     */
    public static void writeSCCSetToConsole(Set<Set<String>> SCCSet) {
        int i = 0;
        for (Set<String> SCC : SCCSet) {
            System.out.println("\nSCC " + i);
            for (String string : SCC) {
                System.out.print(string + " ");
            }
            System.out.println();
            i++;
        }
    }

    /**
     * returns randomly generated regular expression
     * @return      the randomly generated regular expression
     * @throws eu.fox7.flt.regex.random.ConfigurationException
     */
    public static String getRandomRegex() throws eu.fox7.flt.regex.random.ConfigurationException {
        Properties prop = new Properties();
        setRegexProperties(prop);
        RandomRegexFactory randomRegexCreator = new RandomRegexFactory(prop);
        String regex = randomRegexCreator.create().toString();
        return regex;
    }

    /**
     * set random properties for RandomRegexFactory.create()
     * @param prop      used to store the generated properties
     */
    private static void setRegexProperties(Properties prop) {
        int alphabetSize = (int) (Math.random() * 5 + 1);
        prop.setProperty("alphabetSize", "" + alphabetSize);
        prop.setProperty("noSimplification", "true"); // simplify randomly generated regex or not?
    }

    /**
     * creates a Glushkov NFA from a regular expression
     * WARNING: if Glushkov.INITIAL_STATE final state -> acceptEmpty = true and Glushkov.INITIAL_STATE no longer final
     * WARNING: Glushkov.INITIAL_STATE can never have incoming transitions or a self-loop.
     * @param regex     create from this regular expression
     * @return          the created Glushkov NFA
     * @throws FeatureNotSupportedException
     * @throws SExpressionParseException
     * @throws UnknownOperatorException
     */
    public static SparseNFA regexToGlushkov(String regex) throws FeatureNotSupportedException, SExpressionParseException, UnknownOperatorException {
        GlushkovFactory glushkovFactory = new GlushkovFactory();
        SparseNFA nfa = glushkovFactory.create(regex);
        return nfa;
    }

    /**
     * returns Glushkov automaton based on randomly generated regular expression
     * @return          the created Glushkov NFA
     * @throws eu.fox7.flt.regex.random.ConfigurationException
     * @throws FeatureNotSupportedException
     * @throws SExpressionParseException
     * @throws UnknownOperatorException
     */
    public static SparseNFA getRandomRegexGlushkov() throws eu.fox7.flt.regex.random.ConfigurationException, FeatureNotSupportedException, SExpressionParseException, UnknownOperatorException {
        // generate automaton from random regex
        String regex1 = getRandomRegex();
        SparseNFA nfa = regexToGlushkov(regex1);

        // make sure that Glushkov.INITIAL_STATE is not a final state
        if (nfa.isFinalState(Glushkov.INITIAL_STATE)) {
            Set<String> finalStateValueSet = new HashSet<String>(nfa.getFinalStateValues());
            finalStateValueSet.remove(Glushkov.INITIAL_STATE);
            nfa.setFinalStateValues(finalStateValueSet); // Glushkov.INITIAL_STATE no longer final state
        }

        return nfa;
    }

    /**
     * converts a regex in computer form to a regex in human form ( example (. (? a) b) -> (a? . b) )
     * @param regex         the regex in computer form
     * @return              the regex in human form
     */
    public static String convertRegexComputerToHuman(String regex) {
        // all brackets become square brackets
        regex = regex.replace('(', '[');
        regex = regex.replace(')', ']');

        // square brackets -> regular expression parts that still need to be converted
        Pattern pattern = Pattern.compile("\\[[^\\[\\]]*\\]"); // find regex part "[.*]" with . all chars except '[' or ']'
        Matcher matcher = pattern.matcher(regex);
        while (matcher.find()) {
            String regexPart = matcher.group();
            String regexPartHuman = convertRegexPartComputerToHuman(regexPart);
            regex = regex.replace(regexPart, regexPartHuman);
            matcher = pattern.matcher(regex);
        }

        return regex;
    }

    /**
     * converts a single regex part in computer form to a regex in human form ( example (. a b) -> (a . b) )
     * @param regexPart     the regex part in computer form
     * @return              the regex part in human form
     */
    private static String convertRegexPartComputerToHuman(String regexPart) {
        // if regexPart.charAt(1) is not one of the following chars: "|+.*?"
        if ("|+.*?".indexOf(regexPart.charAt(1)) == -1) {
            String regexPartNew = regexPart.replace('[', '(');
            regexPartNew = regexPartNew.replace(']', ')');
            return regexPartNew; // human order: same as computer order
        }

        // remove '[' at beginning and ']' at end of string
        String regexPartNew = regexPart.replaceAll("[\\[\\]]", "");

        // if first character (operator) is one of the following chars: "+*?"
        if ("+*?".indexOf(regexPartNew.charAt(0)) != -1) {
            char operator = regexPartNew.charAt(0);
            regexPartNew = regexPartNew.substring(2);
            regexPartNew += operator;
            regexPartNew = "(" + regexPartNew + ")";
            return regexPartNew; // human order: regex followed by operator
        }

        // if first character is one of the following chars: "|."
        char operator = regexPartNew.charAt(0);
        Pattern pattern = Pattern.compile("[()]"); // find all round brackets in regexPart
        Matcher matcher = pattern.matcher(regexPartNew);
        String regexAid = "("; // add new bracket parts of regexPartNew, one by one

        // regexPartNew should look like this: operator_(.*)_(.*) etc. ( example (| (a) (b) (c)) ) -> find positions round brackets
        while (matcher.find()) { // find bracket parts, one by one
            int count = 0;
            int posL = matcher.start(); // opening bracket '('
            do {
                String bracket = matcher.group();
                if (bracket.equals("(")) {
                    count++;
                } else { // if (bracket.equals(")"))
                    count--;
                }
                if (count != 0) {
                    matcher.find();
                }
            } while (count != 0); // if (count == 0) -> matching closing bracket found
            int posR = matcher.start(); // matching closing bracket ')' of opening bracket '('

            // add found bracket part
            if (posL != 2) { // if not first bracket part
                regexAid += " " + operator + " ";
            }
            regexAid += regexPartNew.substring(posL, posR + 1);
        }

        regexAid += ")";
        regexPartNew = regexAid;
        return regexPartNew; // human order: first brackets part, operator, second brackets part
    }

    /**
     * simply copies a file from one location to the other
     * @param fromFilename      filename of source file
     * @param toFilename        filename of destination file
     * @return                  true if success, false is failed
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean fileCopy(String fromFilename, String toFilename) throws FileNotFoundException, IOException {
        File fromFile = new File(fromFilename);
        File toFile = new File(toFilename);

        if (!fromFile.exists()) {
            return false;
        }

        FileInputStream fromStream = new FileInputStream(fromFile);
        FileOutputStream toStream = new FileOutputStream(toFile);
        FileChannel fromChannel = fromStream.getChannel();
        FileChannel toChannel = toStream.getChannel();
        toChannel.transferFrom(fromChannel, 0, fromChannel.size());

        toChannel.close();
        fromChannel.close();
        toStream.close();
        fromStream.close();

        return true;
    }
}
