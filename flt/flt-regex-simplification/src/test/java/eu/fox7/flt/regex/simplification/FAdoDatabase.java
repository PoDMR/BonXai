package eu.fox7.flt.regex.simplification;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.regex.Glushkov;

/**
 * @author Jeroen Appermont 0726039
 */
public class FAdoDatabase {

    private static Properties globalProperties;   // the configuration setting from config file 'fado.ini'

    /**
     * returns one random DFA using config file 'fado.ini'
     * @return                          the computed automaton
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     */
    public static SparseNFA getRandomDFA() throws ClassNotFoundException, FileNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        List<SparseNFA> sparseNFAList = getRandomDFAs(1);
        Iterator<SparseNFA> it = sparseNFAList.iterator();
        return it.next();
    }

    /**
     * returns a number of random DFA's using config file 'fado.ini'
     * @param amount                    the number of returned automata
     * @return                          a list of computed automata
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     */
    public static List<SparseNFA> getRandomDFAs(int amount) throws ClassNotFoundException, FileNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        // get Properties
        Properties prop = createDFAProperties();
        String numStates = prop.getProperty("stateSize");
        String numOutTransitions = prop.getProperty("symbolSize");

        // get DFA's from FAdo
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://khixote.ncc.up.pt/icdfa_dataset", "guest", "guest");
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT string, final_states FROM random_icdfa WHERE n_states=" + numStates + " AND n_symbols=" + numOutTransitions + " AND final_states NOT LIKE '' LIMIT " + amount);
        conn.close();

        List<SparseNFA> sparseNFAList = new LinkedList<SparseNFA>();
        while (result.next()) {
            String icdfa = result.getString("string");
            String finalStates = result.getString("final_states");
            if (finalStates.isEmpty()) {
                System.exit(1);
            }
            SparseNFA nfa = ICDFAToSparseNFA(icdfa, finalStates, Integer.valueOf(numStates), Integer.valueOf(numOutTransitions));
            sparseNFAList.add(nfa);
        }

        return sparseNFAList;
    }

    /**
     * creates Properties using config file 'fado.ini'
     * @return      the created Properties
     */
    private static Properties createDFAProperties() throws FileNotFoundException {
        // if globalProperties doesn't exists -> fill in using 'fado.ini'
        if (globalProperties == null) {
            globalProperties = new Properties();
            Scanner scFile = new Scanner(new File("fado.ini"));
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
     * converts an ICDFA string and a string with final states to a SparseNFA
     * @param icdfa                     the input ICDFA string
     * @param finalStates               the input final states string
     * @param numStates                 the number of states
     * @param numOutTransitions         the number of outgoing transitions for each state
     * @return                          the calculated SparseNFA
     */
    private static SparseNFA ICDFAToSparseNFA(String icdfa, String finalStates, int numStates, int numOutTransitions) {
        SparseNFA sparseNFA = new SparseNFA();
        String[] icdfaArray = icdfa.split(",");
        String[] finalStateArray = finalStates.split(",");

        // create necessary states for sparseNFA
        sparseNFA.setInitialState(Glushkov.INITIAL_STATE); // this state points to initial state
        Arrays.sort(finalStateArray); // needed for binarySearch()
        for (int state = 0; state < numStates; state++) {
            if (Arrays.binarySearch(finalStateArray, "" + state) >= 0) { // if state is a final state
                sparseNFA.addFinalState("q" + (state + 2));
            } else { // state is no final state
                sparseNFA.addState("q" + (state + 2));
            }
        }

        // convert icdfa to transitions for sparseNFA
        sparseNFA.addTransition(Symbol.getEpsilon(), sparseNFA.getState(Glushkov.INITIAL_STATE), sparseNFA.getState("q2")); // q2 is the real initial state
        for (int state = 0; state < numStates; state++) { // icdfaArray.length == states * symbols -> every cycle sets outgoing transitions for a state i
            for (int outTransition = 0; outTransition < numOutTransitions; outTransition++) {
                int toState = Integer.valueOf(icdfaArray[state * numOutTransitions + outTransition]);
                sparseNFA.addTransition(Symbol.getEpsilon(), sparseNFA.getState("q" + (state + 2)), sparseNFA.getState("q" + (toState + 2)));
            }
        }

        return sparseNFA;
    }
}
