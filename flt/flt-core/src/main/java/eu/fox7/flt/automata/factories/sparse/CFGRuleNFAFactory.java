package eu.fox7.flt.automata.factories.sparse;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import eu.fox7.flt.automata.impl.sparse.InsertionPoint;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.grammar.CFG;
import eu.fox7.flt.grammar.NonExistingNonTerminalException;


public class CFGRuleNFAFactory {

	protected SparseNFA nfa;
	protected CFG cfg;
	protected Symbol nonTerminal;
	protected Set<InsertionPoint> insertionPoints;

	public CFGRuleNFAFactory(CFGRuleNFAFactory cfgRuleNFA) {
		cfg = cfgRuleNFA.cfg();
		nonTerminal = cfgRuleNFA.nonTerminal();
		Map<State,State> stateRemap = cfgRuleNFA.nfa().stateRemapping();
		nfa = new SparseNFA(cfgRuleNFA.nfa(), stateRemap);
		insertionPoints = new HashSet<InsertionPoint>();
		for (InsertionPoint point : cfgRuleNFA.insertionPoints())
			insertionPoints.add(new InsertionPoint(point, stateRemap));
	}

	protected CFGRuleNFAFactory(CFG cfg, String nonTerminal) {
		this.cfg = cfg;
		this.nonTerminal = Symbol.create(nonTerminal);
		insertionPoints = new HashSet<InsertionPoint>();
		Set<String[]> productions = null;
		try {
			productions = cfg.productions(nonTerminal);
		} catch(NonExistingNonTerminalException e) {
			throw new Error("weird exception " + e.getMessage());
		}
		SparseNFA[] nfas = new SparseNFA[productions.size()];
		int j = 0;
		for (String[] prod : productions)
			nfas[j++] = production2NFA(nonTerminal, prod);
		nfa = ThompsonBuilder.union(nfas);
	}

	protected SparseNFA production2NFA(String nonTerminal, String[] production) {
		SparseNFA[] nfas = new SparseNFA[production.length];
		for (int i = 0; i < production.length; i++) {
			nfas[i] = ThompsonBuilder.symbolNFA(production[i]);
			if (cfg.hasNonTerminal(production[i])) {
				insertionPoints.add(new InsertionPoint(nonTerminal, production, i,
													   Symbol.create(production[i]),
													   nfas[i].getInitialState(),
													   eu.fox7.util.Collections.extractSingleton(nfas[i].getFinalStates())));
			}
		}
		return ThompsonBuilder.concatenate(nfas);
	}

	public static Map<Symbol,CFGRuleNFAFactory> cfgRuleNFAs(CFG cfg) {
		Map<Symbol,CFGRuleNFAFactory> nonTerminalNFAs = new HashMap<Symbol,CFGRuleNFAFactory>();
		for (String nonTerminal : cfg.nonTerminals()) {
			CFGRuleNFAFactory ruleNFA = new CFGRuleNFAFactory(cfg, nonTerminal);
			nonTerminalNFAs.put(Symbol.create(nonTerminal), ruleNFA);
		}
		return nonTerminalNFAs;
	}

	public SparseNFA nfa() {
		return nfa;
	}

	public CFG cfg() {
		return cfg;
	}

	public Symbol nonTerminal() {
		return nonTerminal;
	}

	public Set<InsertionPoint> insertionPoints() {
		return Collections.unmodifiableSet(insertionPoints);
	}

}
