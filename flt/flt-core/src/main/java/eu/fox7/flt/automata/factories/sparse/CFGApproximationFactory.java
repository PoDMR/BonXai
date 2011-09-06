package eu.fox7.flt.automata.factories.sparse;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.converters.AlphabetCleaner;
import eu.fox7.flt.automata.impl.sparse.InsertionPoint;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.matchers.NFAMatcher;
import eu.fox7.flt.grammar.CFG;
import eu.fox7.util.BoundedQueue;


public class CFGApproximationFactory {

	protected CFG cfg;
	protected SparseNFA nfa;
	protected int depth;

	public CFGApproximationFactory(CFG cfg) {
		this.cfg = cfg;
		this.depth = 2;
		this.nfa = constructNFA();
	}

	public CFGApproximationFactory(CFG cfg, int depth) {
		this.cfg = cfg;
		this.depth = depth;
		this.nfa = constructNFA(depth);
	}

	public CFG cfg() {
		return this.cfg;
	}

	public SparseNFA nfa() {
		return this.nfa;
	}

	public boolean run(String[] input) {
		NFAMatcher matcher = new NFAMatcher(this.nfa);
		return matcher.matches(input);
	}

	public int depth() {
		return this.depth;
	}

	protected SparseNFA constructNFA() {
		Set<InsertionPoint> insertionPoints = new HashSet<InsertionPoint>();
		Map<Symbol,CFGRuleNFAFactory> nonTerminalNFAs = CFGRuleNFAFactory.cfgRuleNFAs(cfg);
		SparseNFA[] nfas = new SparseNFA[cfg.nonTerminals().size()];
		int i = 0;
		for (CFGRuleNFAFactory ruleNFA : nonTerminalNFAs.values()) {
			nfas[i++] = ruleNFA.nfa();
			insertionPoints.addAll(ruleNFA.insertionPoints());
		}
		SparseNFA nfa = ThompsonBuilder.merge(nfas);
		for (InsertionPoint point : insertionPoints) {
			try {
				nfa.removeTransition(point.getSymbol(), point.getFromState(),
									 point.getToState());
			} catch(NoSuchTransitionException e) {
			    throw new Error("weird exception " + e.getMessage());
			}
			Symbol nonTerminal = point.getSymbol();
			SparseNFA nonTerminalNFA = nonTerminalNFAs.get(nonTerminal).nfa();
			nfa.addTransition(Symbol.getEpsilon(), point.getFromState(),
							  nonTerminalNFA.getInitialState());
			nfa.addTransition(Symbol.getEpsilon(),
							  eu.fox7.util.Collections.extractSingleton(nonTerminalNFA.getFinalStates()),
							  point.getToState());
		}
		SparseNFA startSymbolNFA = nonTerminalNFAs.get(Symbol.create(cfg.startSymbol())).nfa();
		nfa.setInitialState(startSymbolNFA.getInitialState());
		nfa.setFinalState(eu.fox7.util.Collections.extractSingleton(startSymbolNFA.getFinalStates()));
		AlphabetCleaner cleaner = new AlphabetCleaner();
		cleaner.clean(nfa);
		return nfa;
	}

	public SparseNFA constructNFA(int depth) {
		if (depth <= 0)	return ThompsonBuilder.sigmaStarNFA(cfg.terminals().toArray());
		if (depth == 1) return constructNFA();
		Map<Symbol,CFGRuleNFAFactory> nonTerminalNFAs = CFGRuleNFAFactory.cfgRuleNFAs(cfg);
		Symbol startSymbol = Symbol.create(cfg.startSymbol());
		CFGRuleNFAFactory startSymbolNFA = new CFGRuleNFAFactory(nonTerminalNFAs.get(startSymbol));
		SparseNFA nfa = startSymbolNFA.nfa();
		BoundedQueue<InsertionPoint> history = new BoundedQueue<InsertionPoint>(depth - 1);
		Map<String,CFGRuleNFAFactory> historyMap = new HashMap<String,CFGRuleNFAFactory>();
		construct(nfa, nonTerminalNFAs, history, historyMap, startSymbolNFA);
		AlphabetCleaner cleaner = new AlphabetCleaner();
		cleaner.clean(nfa);
		return nfa;
	}

	protected void construct(ModifiableStateNFA nfa, Map<Symbol,CFGRuleNFAFactory> nonTerminalNFAs,
							 BoundedQueue<InsertionPoint> history,
                             Map<String,CFGRuleNFAFactory> historyMap,
							 CFGRuleNFAFactory current) {
		historyMap.put(history.signature(), current);
		for (InsertionPoint insertionPoint : current.insertionPoints()) {
			BoundedQueue<InsertionPoint> newHistory = new BoundedQueue<InsertionPoint>(history);
			newHistory.push(insertionPoint);
			if (!historyMap.containsKey(newHistory.signature())) {
				Symbol nonTerminal = insertionPoint.getSymbol();
				CFGRuleNFAFactory newNFA = new CFGRuleNFAFactory(nonTerminalNFAs.get(nonTerminal));
				historyMap.put(newHistory.signature(), newNFA);
				nfa.add(newNFA.nfa());
				construct(nfa, nonTerminalNFAs, newHistory, historyMap, newNFA);
			}
			CFGRuleNFAFactory cfgRuleNFA = historyMap.get(newHistory.signature());
			try {
				nfa.removeTransition(insertionPoint.getSymbol(),
									 insertionPoint.getFromState(),
									 insertionPoint.getToState());
			} catch(NoSuchTransitionException e) {
				System.err.println("weird exception " + e.getMessage());
				System.err.println(insertionPoint.toString());
			}
			nfa.addTransition(Symbol.getEpsilon(),
							  insertionPoint.getFromState(),
							  cfgRuleNFA.nfa().getInitialState());
			nfa.addTransition(Symbol.getEpsilon(),
							  eu.fox7.util.Collections.extractSingleton(cfgRuleNFA.nfa().getFinalStates()),
							  insertionPoint.getToState());
		}
	}

}
