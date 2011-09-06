/*
 * Created on Sep 4, 2008
 * Modified on $Date: 2009-11-12 22:16:18 $
 */
package eu.fox7.flt.regex.infer.rwr.impl;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.5 $
 * 
 */
public class GraphAutomatonFactory {

    protected static Regex regex = new Regex();

    public Automaton create(String[][] sample) {
        Set<String> alphabet = computeAlphabet(sample);
        Automaton automaton = new GraphAutomaton(alphabet.size());
        for (String[] example : sample) {
            try {
                if (example.length == 0) {
                    automaton.addEpsilon();
                } else {
                    automaton.addInitial(pack(example[0]));
                    for (int i = 0; i < example.length - 1; i++)
                        automaton.addTransition(pack(example[i]),
                                                pack(example[i+1]));
                    automaton.addFinal(pack(example[example.length-1]));
                }
            } catch (AutomatonConstructionException e) {
                e.printStackTrace();
                throw new RuntimeException("unexpected exception", e);
            }
        }
        return order(automaton);
    }

    public Automaton create(StateNFA nfa) {
        Automaton automaton = new GraphAutomaton(nfa.getNumberOfStates() - 1);
        List<State> toDo = new LinkedList<State>();
        Set<State> done = new HashSet<State>();
        State state = nfa.getInitialState();
        try {
            for (Transition transition : nfa.getOutgoingTransitions(state)) {
                State toState = transition.getToState();
                String toStateValue = pack(nfa.getStateValue(toState));
                automaton.addInitial(toStateValue);
                if (nfa.isFinalState(toState))
                    automaton.addFinal(toStateValue);
                toDo.add(toState);
            }
            if (nfa.isFinalState(state))
                automaton.addEpsilon();
            while (!toDo.isEmpty()) {
                State fromState = toDo.remove(0);
                String fromStateValue = pack(nfa.getStateValue(fromState));
                done.add(fromState);
                for (Transition transition : nfa.getOutgoingTransitions(fromState)) {
                    State toState = transition.getToState();
                    String toStateValue = pack(nfa.getStateValue(toState));
                    automaton.addTransition(fromStateValue, toStateValue);
                    if (nfa.isFinalState(toState))
                        automaton.addFinal(toStateValue);
                    if (!done.contains(toState))
                        toDo.add(toState);
                }
            }
        } catch (AutomatonConstructionException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
        return order(automaton);
    }

    public Automaton create(String regexStr)
            throws SExpressionParseException, UnknownOperatorException,
                   FeatureNotSupportedException {
        GlushkovFactory glushkov = new GlushkovFactory();
        StateNFA nfa = glushkov.create(regexStr);
        return create(nfa);
    }

    public Automaton reverse(String regexStr)
            throws SExpressionParseException, FeatureNotSupportedException {
        Regex regex = new Regex();
        Tree tree = regex.getTree(regexStr);
        return reverse(tree.getRoot());
    }

    protected Automaton reverse(Node node) throws FeatureNotSupportedException {
        String symbol = node.getKey();
        if (regex.isEmptySymbol(symbol))
            return emptyAutomaton();
        else if (regex.isEpsilonSymbol(symbol))
            return epsilonAutomaton();
        else if (regex.isOperatorSymbol(symbol)) {
            if (symbol.equals(regex.zeroOrOneOperator()))
                return optionalAutomaton(node.getChild(0));
            else if (symbol.equals(regex.oneOrMoreOperator()))
                return repetitionAutomaton(node.getChild(0));
            else if (symbol.equals(regex.concatOperator()))
                return concatAutomaton(node.getChild(0), node.getChild(1));
            else if (symbol.equals(regex.unionOperator()))
                return disjunctionAutomaton(node.getChild(0), node.getChild(1));
            else
                throw new FeatureNotSupportedException("symbol '" + symbol + "'");
        } else {
            return symbolAutomaton(symbol);
        }
    }

    protected Automaton disjunctionAutomaton(Node node1, Node node2)
            throws FeatureNotSupportedException {
        Automaton subautomaton1 = reverse(node1);
        int n1 = subautomaton1.getNumberOfStates() - 1;
        Automaton subautomaton2 = reverse(node2);
        int n2 = subautomaton2.getNumberOfStates() - 1;
        int n = n1 + n2;
        Automaton automaton = new GraphAutomaton(n);
        /* copy subautomaton 1 */
        for (int i = 0; i < n1; i++)
            for (int j = 0; j < n1; j++)
                automaton.set(i, j, subautomaton1.get(i, j));
        for (int i = 0; i < n1; i++)
            automaton.set(i, n, subautomaton1.get(i, n1));
        for (int j = 0; j < n1; j++)
            automaton.set(n, j, subautomaton1.get(n1, j));
        /* copy subautomaton 2 */
        for (int i = 0; i < n2; i++)
            for (int j = 0; j < n2; j++)
                automaton.set(n1 + i, n1 + j, subautomaton2.get(i, j));
        for (int i = 0; i < n2; i++)
            automaton.set(n1 + i, n, subautomaton2.get(i, n2));
        for (int j = 0; j < n2; j++)
            automaton.set(n, n1 + j, subautomaton2.get(n2, j));
        if (subautomaton1.get(n1, n1) != 0 || subautomaton2.get(n2, n2) != 0)
            automaton.set(n, n, 1);
        /* copy labels */
        for (int i = 0; i < n1; i++)
            automaton.setLabel(i, subautomaton1.getLabel(i));
        for (int i = 0; i < n2; i++)
            automaton.setLabel(n1 + i, subautomaton2.getLabel(i));
        return automaton;
    }

    protected Automaton concatAutomaton(Node node1, Node node2)
            throws FeatureNotSupportedException {
        String symbol1 = node1.getKey();
        String symbol2 = node2.getKey();
        boolean isOptional1 = symbol1.equals(regex.zeroOrOneOperator());
        boolean isOptional2 = symbol2.equals(regex.zeroOrOneOperator());
        Automaton subautomaton1 = isOptional1 ? reverse(node1.getChild(0)) : reverse(node1);
        int n1 = subautomaton1.getNumberOfStates() - 1;
        Automaton subautomaton2 = isOptional2 ? reverse(node2.getChild(0)) : reverse(node2);
        int n2 = subautomaton2.getNumberOfStates() - 1;
        int n = n1 + n2;
        Automaton automaton = new GraphAutomaton(n);
        /* copy subautomaton 1 */
        for (int i = 0; i < n1; i++)
            for (int j = 0; j < n1; j++)
                automaton.set(i, j, subautomaton1.get(i, j));
        /* connect finals of subautomaton 1 with initials of subautomaton 2 */
        for (int i = 0; i < n1; i++)
            for (int j = 0; j < n2; j++)
                if (subautomaton1.isFinal(i) && subautomaton2.isInitial(j))
                    automaton.set(i, n1 + j, 1);
        /* copy subautomaton 2 */
        for (int i = 0; i < n2; i++)
            for (int j = 0; j < n2; j++)
                automaton.set(n1 + i, n1 + j, subautomaton2.get(i, j));
        /* all initials of subautomaton 1 are initials */
        for (int j = 0; j < n1; j++)
            automaton.set(n, j, subautomaton1.get(n1, j));
        /* all finals of automaton 2 are finals */
        for (int i = 0; i < n2; i++)
            automaton.set(n1 + i, n, subautomaton2.get(i, n2));
        /* if subautomaton 1 is optional, initials of subautomaton 2 are initials */
        if (isOptional1)
            for (int j = 0; j < n2; j++)
                automaton.set(n, n1 + j, subautomaton2.get(n2, j));
        /* if subautomaton 2 is optional, finals of subautomaton 1 are finals */
        if (isOptional2)
            for (int i = 0; i < n1; i++)
                automaton.set(i, n, subautomaton1.get(i, n1));
        /* if subautomaton 1 and 2 are optional, so is automaton */
        if (isOptional1 && isOptional2)
            automaton.set(n, n, 1);
        for (int i = 0; i < n1; i++)
            automaton.setLabel(i, subautomaton1.getLabel(i));
        for (int i = 0; i < n2; i++)
            automaton.setLabel(n1 + i, subautomaton2.getLabel(i));
        return automaton;
    }

    protected Automaton emptyAutomaton() {
        Automaton automaton = new GraphAutomaton(0);
        return automaton;
    }

    protected Automaton epsilonAutomaton() {
        Automaton automaton = emptyAutomaton();
        automaton.addEpsilon();
        return automaton;
    }

    protected Automaton symbolAutomaton(String symbol) {
        Automaton automaton = new GraphAutomaton(1);
        try {
            automaton.addInitial(pack(symbol));
            automaton.addFinal(pack(symbol));
        } catch (AutomatonConstructionException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
        return automaton;
    }

    protected Automaton optionalAutomaton(Node node)
            throws FeatureNotSupportedException {
        Automaton subautomaton = reverse(node);
        Automaton automaton = new GraphAutomaton(subautomaton.getNumberOfStates() - 1);
        for (int i = 0; i < subautomaton.getNumberOfStates(); i++)
            for (int j = 0; j < subautomaton.getNumberOfStates(); j++) {
                automaton.set(i, j, subautomaton.get(i, j));
            }
        for (int i = 0; i < subautomaton.getNumberOfStates() - 1; i++)
            automaton.setLabel(i, subautomaton.getLabel(i));
        automaton.addEpsilon();
        return automaton;
    }

    protected Automaton repetitionAutomaton(Node node)
            throws FeatureNotSupportedException {
        Automaton subautomaton = reverse(node);
        int n = subautomaton.getNumberOfStates() - 1;
        Automaton automaton = new GraphAutomaton(n);
        /* copy subautomaton */
        for (int i = 0; i < subautomaton.getNumberOfStates(); i++)
            for (int j = 0; j < subautomaton.getNumberOfStates(); j++)
                automaton.set(i, j, subautomaton.get(i, j));
        /* every final linked to all initials */
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (subautomaton.isFinal(i) && subautomaton.isInitial(j))
                    automaton.set(i, j, 1);
        for (int i = 0; i < n; i++)
            automaton.setLabel(i, subautomaton.getLabel(i));
        return automaton;
    }

    public Automaton expand(Automaton automaton) {
        try {
            boolean done = false;
            while (!done) {
                done = true;
                for (int i = 0; i < automaton.getNumberOfStates() - 1; i++) {
                    String label = automaton.getLabel(i);
                    if (regex.isOperatorSymbol(label.substring(1, 2))) {
                        Automaton subautomaton = reverse(label);
                        automaton = substitute(automaton, i, subautomaton);
                        done = false;
                        break;
                    }
                }
            }
            return automaton;
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
    }

    protected Automaton substitute(Automaton automaton, int index,
                                   Automaton subautomaton) {
        int nOrig = automaton.getNumberOfStates() - 1;
        int nSub = subautomaton.getNumberOfStates() - 1;
        int n = nOrig - 1 + nSub;
        Automaton newAutomaton = new GraphAutomaton(n);
        /* copy automaton */
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++)
                newAutomaton.set(i, j, automaton.get(i, j));
            for (int j = index + 1; j < automaton.getNumberOfStates(); j++)
                newAutomaton.set(i, nSub + j - 1, automaton.get(i, j));
        }
        for (int i = index + 1; i < automaton.getNumberOfStates(); i++) {
            for (int j = 0; j < index; j++)
                newAutomaton.set(nSub + i - 1, j, automaton.get(i, j));
            for (int j = index + 1; j < automaton.getNumberOfStates(); j++)
                newAutomaton.set(nSub + i - 1, nSub + j - 1,
                                 automaton.get(i, j));
        }
        /* fix for subautomaton accepting empty string */
        if (subautomaton.acceptsEpsilon()) {
        	for (int i = 0; i < index; i++) {
        		if (automaton.get(i, index) != 0) {
        			for (int j = 0; j < index; j++)
        				if (newAutomaton.get(i, j) == 0)
        					newAutomaton.set(i, j, automaton.get(i, j));
        			for (int j = index + 1; j < automaton.getNumberOfStates(); j++)
        				if (newAutomaton.get(i, nSub + j - 1) == 0)
        					newAutomaton.set(i, nSub + j - 1, automaton.get(i, j));
        		}
        	}
        	for (int i = index + 1; i < automaton.getNumberOfStates(); i++) {
        		if (automaton.get(i, index) != 0) {
        			for (int j = 0; j < index; j++)
        				if (newAutomaton.get(nSub + i - 1, j) == 0)
        					newAutomaton.set(nSub + i - 1, j, automaton.get(i, j));
        			for (int j = index + 1; j < automaton.getNumberOfStates(); j++)
        				if (newAutomaton.get(nSub + i - 1, nSub + j - 1) == 0)
        					newAutomaton.set(nSub + i - 1, nSub + j - 1, automaton.get(i, j));
        		}
        	}
        }
        /* copy subautomaton */
        for (int i = 0; i < nSub; i++)
            for (int j = 0; j < nSub; j++)
                newAutomaton.set(index + i, index + j, subautomaton.get(i, j));
        /* fix self-loop */
        if (automaton.get(index, index) != 0)
        	for (int i = 0; i < nSub; i++)
        		if (subautomaton.isFinal(i))
        			for (int j = 0; j < nSub; j++)
        				if (subautomaton.isInitial(j))
        					newAutomaton.set(index + i, index + j, 1);
        /* fix predecessors of subautomaton */
        for (int i = 0; i < index; i++)
            if (automaton.get(i, index) != 0)
                for (int j = 0; j < nSub; j++)
                    newAutomaton.set(i, index + j, subautomaton.get(nSub, j));
        for (int i = index + 1; i < automaton.getNumberOfStates(); i++)
            if (automaton.get(i, index) != 0)
                for (int j = 0; j < nSub; j++)
                    newAutomaton.set(nSub + i - 1, index + j,
                                     subautomaton.get(nSub, j));
        /* fix successsors of subautomaton */
        for (int j = 0; j < index; j++)
            if (automaton.get(index, j) != 0)
                for (int i = 0; i < nSub; i++)
                    newAutomaton.set(index + i, j, subautomaton.get(i, nSub));
        for (int j = index + 1; j < automaton.getNumberOfStates(); j++)
            if (automaton.get(index, j) != 0)
                for (int i = 0; i < nSub; i++)
                    newAutomaton.set(index + i, nSub + j - 1,
                                     subautomaton.get(i, nSub));
        if (n == nSub && subautomaton.acceptsEpsilon())
            newAutomaton.set(n, n, 1);
        for (int i = 0; i < index; i++)
            newAutomaton.setLabel(i, automaton.getLabel(i));
        for (int i = index + 1; i < nOrig; i++)
            newAutomaton.setLabel(nSub + i - 1, automaton.getLabel(i));
        for (int i = 0; i < nSub; i++)
            newAutomaton.setLabel(index + i, subautomaton.getLabel(i));
        return newAutomaton;
    }

    public Automaton create(BigInteger index, int alphabetSize) {
        Automaton automaton = new GraphAutomaton(alphabetSize);
        String[] alphabet = computeAlphabet(alphabetSize);
        int n = alphabetSize + 1;
        try {
            for (int j = 0; j < alphabetSize; j++)
                if (index.testBit(j))
                    automaton.addInitial(pack(alphabet[j]));
            if (index.testBit(alphabetSize))
                automaton.addEpsilon();
            for (int i = 1; i < alphabetSize + 1; i++) {
                for (int j = 0; j < alphabetSize; j++)
                    if (index.testBit(i*n + j))
                        automaton.addTransition(pack(alphabet[i-1]),
                                                pack(alphabet[j]));
                if (index.testBit(i*n + alphabetSize))
                    automaton.addFinal(pack(alphabet[i-1]));
            }
        } catch (AutomatonConstructionException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
        return automaton;
    }

    public Automaton order(Automaton automaton) {
        List<String> labels = new ArrayList<String>();
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++)
            labels.add(automaton.getLabel(i));
        Collections.sort(labels);
        Automaton newAutomaton = new GraphAutomaton(labels);
        try {
            for (int i = 0; i < labels.size(); i++) {
                if (automaton.isInitial(labels.get(i)))
                    newAutomaton.addInitial(labels.get(i));
                if (automaton.isFinal(labels.get(i)))
                    newAutomaton.addFinal(labels.get(i));
                for (int j = 0; j < labels.size(); j++)
                    if (automaton.hasTransition(labels.get(i), labels.get(j)))
                        newAutomaton.addTransition(labels.get(i), labels.get(j));
            }
            if (automaton.acceptsEpsilon())
                newAutomaton.addEpsilon();
        } catch (InvalidLabelException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        } catch (AutomatonConstructionException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
        return newAutomaton;
    }

    protected static Set<String> computeAlphabet(String[][] sample) {
        Set<String> alphabet = new HashSet<String>();
        for (String[] example : sample)
            for (String symbol : example)
                alphabet.add(symbol);
        return alphabet;
    }

    protected static String[] computeAlphabet(int n) {
        String[] alphabet = new String[n];
        char c = 'a';
        for (int i = 0; i < n; i++) {
            c = (char) ('a' + i % 26);
            if (i < 26)
                alphabet[i] = Character.toString(c);
            else
                alphabet[i] = Character.toString(c) + (i/26);
        }
        return alphabet;
    }

    protected String pack(String label) {
        return Regex.LEFT_BRACKET + label + Regex.RIGHT_BRACKET;
    }

    public static boolean isSound(Automaton automaton) {
        int alphabetSize = automaton.getNumberOfStates() - 1;
        boolean[] visited = new boolean[alphabetSize];
        for (int j = 0; j < alphabetSize; j++)
            visited[j] = false;
        int[] stringCount = new int[alphabetSize];
        for (int j = 0; j < alphabetSize; j++)
            if (automaton.get(alphabetSize, j) != 0) {
                stringCount[j] = 1;
                visited[j] = true;
            }
        for (int length = 1; length <= alphabetSize + 1; length++) {
            stringCount = updateStringCountForward(automaton, stringCount);
            for (int j = 0; j < alphabetSize; j++)
                if (stringCount[j] != 0)
                    visited[j] = true;
        }
        for (int j = 0; j < alphabetSize; j++) {
            if (!visited[j])
                return false;
            if (automaton.get(j, alphabetSize) != 0) {
                stringCount[j] = 1;
                visited[j] = true;
            } else {
                stringCount[j] = 0;
                visited[j] = false;
            }
        }
        for (int length = 1; length <= alphabetSize + 1; length++) {
            stringCount = updateStringCountBackward(automaton, stringCount);
            for (int j = 0; j < alphabetSize; j++)
                if (stringCount[j] != 0)
                    visited[j] = true;
        }
        for (int j = 0; j < alphabetSize; j++)
            if (!visited[j])
                return false;
        return true;
    }

    protected static int[] updateStringCountForward(Automaton newAutomaton,
                                                    int[] stringCount) {
        int[] newCount = new int[stringCount.length];
        for (int j = 0; j < stringCount.length; j++) {
            newCount[j] = 0;
            for (int i = 0; i < stringCount.length; i++)
                newCount[j] += stringCount[i]*newAutomaton.get(i, j);
        }
        return newCount;
    }

    protected static int[] updateStringCountBackward(Automaton newAutomaton,
                                                     int[] stringCount) {
        int[] newCount = new int[stringCount.length];
        for (int i = 0; i < stringCount.length; i++) {
            newCount[i] = 0;
            for (int j = 0; j < stringCount.length; j++)
                newCount[i] += newAutomaton.get(i, j)*stringCount[j];
        }
        return newCount;
    }

}
