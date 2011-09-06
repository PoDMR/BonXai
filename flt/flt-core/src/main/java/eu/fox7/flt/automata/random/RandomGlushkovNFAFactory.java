/*
 * Created on Feb 22, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.random;

import eu.fox7.flt.automata.ModifiableNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.math.IllDefinedDistributionException;
import eu.fox7.math.ProbabilityDistribution;
import eu.fox7.math.ProbabilityDistributionFactory;
import eu.fox7.math.UniformDistribution;
import eu.fox7.util.RandomSelector;
import eu.fox7.util.SymbolIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class RandomGlushkovNFAFactory {

    public static final String ALPHABET_SIZE = "alphabetSize";
    public static final String TYPE_DISTRIBUTION = "typeDistribution";
    public static final String NUMBER_OF_FINAL_STATES = "numberOfFinalStates";
    public static final String NUMBER_OF_OUT_TRANSITIONS_INIT_STATE = "numberOfOutTransitionsInitState";
    public static final String PROBABILITY_DISTRIBUTION = "probabilityDistribution";
    public static final String DISTRIBUTION_SEPARATOR = ",";
    public static final String STATE_SEPARATOR = "_";
    protected Properties properties;
    protected String[] alphabet;
    protected Map<String,Set<String>> stateMap = new HashMap<String,Set<String>>();
    protected RandomSelector<String> selector = new RandomSelector<String>();

    public RandomGlushkovNFAFactory(Properties properties) {
        this.properties = properties;
    }

    public int getAlphabetSize() {
        return Integer.parseInt(properties.getProperty(ALPHABET_SIZE));
    }

    public int getNumberOfFinalStates() {
        return Integer.parseInt(properties.getProperty(NUMBER_OF_FINAL_STATES));
    }

    public int getNumberOfOutTransitionsInitState() {
        return Integer.parseInt(properties.getProperty(NUMBER_OF_OUT_TRANSITIONS_INIT_STATE));
    }

    public int[] getTypeDistribution() throws ConfigurationException {
        int[] distr = new int[getAlphabetSize()];
        String[] distrStr = properties.getProperty(TYPE_DISTRIBUTION).split(DISTRIBUTION_SEPARATOR);
        if (distrStr.length > distr.length)
            throw new ConfigurationException("alphabet size and type distribution size should match");
        for (int i = 0; i < distrStr.length; i++)
            distr[i] = Integer.parseInt(distrStr[i].trim());
        for (int i = distrStr.length; i < distr.length; i++)
            distr[i] = 1;
        return distr;
    }

    public ProbabilityDistribution getProbabilityDistribution()
            throws ConfigurationException {
        String defaultDistrStr = "eu.fox7.math.UniformDistribution(min = 0, max = " +
            getAlphabetSize() + ")";
        ProbabilityDistributionFactory factory = new ProbabilityDistributionFactory();
        try {
            return factory.create(properties.getProperty(PROBABILITY_DISTRIBUTION,
                                                         defaultDistrStr));
        } catch (IllDefinedDistributionException e) {
            throw new ConfigurationException(e);
        }
    }

    public SparseNFA create() throws ConfigurationException {
        SparseNFA nfa = new SparseNFA();
        initAlphabet();
        initStates(nfa);
        initFinalStates(nfa);
        initTransitions(nfa, new UniformDistribution(0, alphabet.length));
        completeNFA(nfa);
        return nfa;
    }

    protected void initAlphabet() {
        SymbolIterator symbolIt = new SymbolIterator();
        alphabet = new String[getAlphabetSize()];
        for (int i = 0; i < getAlphabetSize(); i++)
            alphabet[i] = symbolIt.next();
    }
    
    protected void initStates(ModifiableNFA nfa) throws ConfigurationException {
        nfa.setInitialState(Glushkov.INITIAL_STATE);
        int[] typeDistribution = getTypeDistribution();
        for (int iAlphabet = 0; iAlphabet < alphabet.length; iAlphabet++) {
            String symbol = alphabet[iAlphabet];
            stateMap.put(symbol, new HashSet<String>());
            for (int iType = 0; iType < typeDistribution[iAlphabet]; iType++) {
                String stateValue = symbol + STATE_SEPARATOR + iType;
                nfa.addState(stateValue);
                stateMap.get(symbol).add(stateValue);
            }
        }
    }

    protected void initFinalStates(ModifiableNFA nfa) {
        Set<String> states = new HashSet<String>();
        for (String symbol : stateMap.keySet())
            states.addAll(stateMap.get(symbol));
        for (String stateValue : selector.selectSubsetFrom(states, getNumberOfFinalStates()))
            nfa.addFinalState(stateValue);
    }

    protected void initTransitions(ModifiableNFA nfa, ProbabilityDistribution distr) {
        Set<String> outAlphabet = selector.selectSubsetFrom(stateMap.keySet(),
                                                            getNumberOfOutTransitionsInitState());
        String initStateValue = Glushkov.INITIAL_STATE;
        for (String symbolValue : outAlphabet) {
            String toStateValue = selectNextStateValue(symbolValue);   
            nfa.addTransition(symbolValue, initStateValue, toStateValue);
        }
        for (Set<String> stateSet : stateMap.values()) {
            for (String fromStateValue : stateSet) {
                if (fromStateValue.equals(Glushkov.INITIAL_STATE)) continue;
                outAlphabet = selector.selectSubsetFrom(stateMap.keySet(), distr.getNext());
                for (String symbolValue : outAlphabet) {
                    String toStateValue = selectNextStateValue(symbolValue);   
                    nfa.addTransition(symbolValue, fromStateValue, toStateValue);
                }
            }
        }
    }

    protected void completeNFA(SparseNFA nfa) {
        Set<String> reachables = statesToStateValues(nfa, nfa.reachableStates());
        Set<String> terminating = statesToStateValues(nfa, nfa.getTerminatingStates());
        for (String stateValue : nfa.getStateValues()) {
            if (stateValue.equals(Glushkov.INITIAL_STATE))
                continue;
            if (!reachables.contains(stateValue)) {
                String fromStateValue;
                while ((fromStateValue = selector.selectSubsetFrom(reachables, 1).iterator().next()).equals(Glushkov.INITIAL_STATE));
                String symbolValue = symbolFromState(stateValue);
                nfa.addTransition(symbolValue, fromStateValue, stateValue);
            }
            if (!terminating.contains(stateValue)) {
                String toStateValue;
                while ((toStateValue = selector.selectSubsetFrom(terminating, 1).iterator().next()).equals(Glushkov.INITIAL_STATE));
                String symbolValue = symbolFromState(toStateValue);
                nfa.addTransition(symbolValue, stateValue, toStateValue);
            }
        }
    }

    protected Set<String> statesToStateValues(SparseNFA nfa, Set<State> states) {
        Set<String> stateValues = new HashSet<String>();
        for (State state : states)
            stateValues.add(nfa.getStateValue(state));
        return stateValues;
    }

    protected String selectNextStateValue(String symbol) {
        Set<String> nextStateSingleton = selector.selectSubsetFrom(stateMap.get(symbol), 1);
        return nextStateSingleton.iterator().next();
    }

    protected String symbolFromState(String stateValue) {
        return stateValue.substring(0, stateValue.indexOf(STATE_SEPARATOR));
    }

}
