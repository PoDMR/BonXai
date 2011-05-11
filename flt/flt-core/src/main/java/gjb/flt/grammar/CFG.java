package gjb.flt.grammar;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

public class CFG {

	protected Set<String> terminals, nonTerminals;
	protected Map<String,Set<String[]>> productions;
	protected String startSymbol;
	protected Set<CFGRule> ruleSet;
	protected String ARROW = "->";
	protected String OR = "|";

	protected CFG() {
		terminals = new HashSet<String>();
		nonTerminals = new HashSet<String>();
		productions = new HashMap<String,Set<String[]>>();
		startSymbol = null;
		ruleSet = new HashSet<CFGRule>();
	}

	public CFG(Reader in) throws java.io.IOException, SyntaxErrorException {
		this();
		Set<String> allSymbols = new HashSet<String>();
		String line;
		BufferedReader reader = new BufferedReader(in);
		try {
            while ((line = reader.readLine()) != null) {
            	if (line.trim().length() == 0) {
            		continue;
            	}
            	Set<CFGRule> rules = parseLine(line);
            	ruleSet.addAll(rules);
            	for (CFGRule rule : rules) {
            		nonTerminals.add(rule.lhs());
            		for (int i = 0; i < rule.numberOfSymbols(); i++) {
            			allSymbols.add(rule.rhs(i));
            		}
            		if (!productions.containsKey(rule.lhs())) {
            			productions.put(rule.lhs(), new HashSet<String[]>());
            		}
            		productions.get(rule.lhs()).add(rule.rhs());
            		if (startSymbol == null) {
            			startSymbol = rule.lhs();
            		}
            	}
            }
        } catch (IOException e) {
            throw e;
        } catch (SyntaxErrorException e) {
            throw e;
        } finally {
            reader.close();
        }
		for (String symbol : allSymbols)
			if (!nonTerminals.contains(symbol))
				terminals.add(symbol);
	}

	protected Set<CFGRule> parseLine(String line) throws SyntaxErrorException {
		Set<CFGRule> rules = new HashSet<CFGRule>();
		String str = line.trim();
		int arrowPos = str.indexOf(ARROW);
		if (arrowPos < 0) {
			throw new SyntaxErrorException(line);
		}
		String lhs = str.substring(0, arrowPos-1).trim();
		String allRhs = str.substring(arrowPos + ARROW.length()).trim();
		if (allRhs.length() == 0) {
			throw new SyntaxErrorException(line);
		}
		StringTokenizer st = new StringTokenizer(allRhs, OR);
		while (st.hasMoreTokens()) {
			String rhs = st.nextToken();
			rules.add(new CFGRule(lhs, rhs.trim().split("\\s+")));
		}
		return rules;
	}

	public boolean hasTerminal(String symbol) {
		return terminals.contains(symbol);
	}

	public boolean hasNonTerminal(String symbol) {
		return nonTerminals.contains(symbol);
	}

	public Set<String> terminals() {
		return Collections.unmodifiableSet(terminals);
	}

	public Set<String> nonTerminals() {
		return Collections.unmodifiableSet(nonTerminals);
	}

	public Set<String[]> productions(String symbol)
		    throws NonExistingNonTerminalException {
		if (!productions.containsKey(symbol)) {
			throw new NonExistingNonTerminalException(symbol);
		}
		return Collections.unmodifiableSet(productions.get(symbol));
	}

	public String startSymbol() {
		return startSymbol;
	}

	public CFG cnf() {
		CFG cnf = new CFG();
		return cnf;
	}

}
