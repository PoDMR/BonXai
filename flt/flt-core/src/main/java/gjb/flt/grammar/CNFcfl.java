package gjb.flt.grammar;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.StringTokenizer;
import gjb.util.tree.Handle;

public class CNFcfl {

  protected static final String ARROW = "->";
  protected static final String OR = "|";

  protected Set<String> terminals, nonTerminals;
  protected Map<String,Set<String>> terminalRules;
  protected Map<String,Set<List<String>>> nonTerminalRules;
  protected Set<CNFNonTerminalRule> nonTerminalRuleSet;
  protected String startSymbol;
  protected Map<String,boolean[][]> D;
  protected Map<String,String[][]> L;
  protected Map<String,int[][]> M;
  protected Map<String,String[][]> R;

  public CNFcfl(Reader in)
	throws java.io.IOException, RuleNotInCNFException,
	       ProductionNotDefinedException {
	this.terminals = new HashSet<String>();
	this.nonTerminals = new HashSet<String>();
	this.terminalRules = new HashMap<String,Set<String>>();
	this.nonTerminalRules = new HashMap<String,Set<List<String>>>();
	this.nonTerminalRuleSet = new HashSet<CNFNonTerminalRule>();
	this.startSymbol = null;
	BufferedReader reader = new BufferedReader(in);
	String line;
	try {
        while ((line = reader.readLine()) != null) {
          Set<? extends CNFRule> rules = CNFcfl.parseCNFLine(line.trim());
          for (Iterator<? extends CNFRule> it = rules.iterator(); it.hasNext(); ) {
        	CNFRule rule = it.next();
        	nonTerminals.add(rule.lhs());
        	if (startSymbol == null)
        	  startSymbol = rule.lhs();
        	if (rule.isCNFTerminalRule()) {
        	  CNFTerminalRule tRule = (CNFTerminalRule) rule;
        	  if (!this.terminalRules.containsKey(tRule.lhs()))
        		this.terminalRules.put(tRule.lhs(), new HashSet<String>());
        	  this.terminalRules.get(tRule.lhs()).add(tRule.rhs());
        	  this.terminals.add(tRule.rhs());
        	} else {
        	  CNFNonTerminalRule ntRule = (CNFNonTerminalRule) rule;
        	  this.nonTerminalRuleSet.add(ntRule);
        	  if (!this.nonTerminalRules.containsKey(ntRule.lhs()))
        		this.nonTerminalRules.put(ntRule.lhs(), new HashSet<List<String>>());
        	  this.nonTerminalRules.get(ntRule.lhs()).add(ntRule.rhs());
        	  this.nonTerminals.add(ntRule.rhs(0));
        	  this.nonTerminals.add(ntRule.rhs(1));
        	}
          }
        }
    } catch (IOException e) {
        throw e;
    } catch (RuleNotInCNFException e) {
        throw e;
    } finally {
        reader.close();
    }
	this.completenessCheck();
  }

  public CYKTree cykTree(String input) throws LexicalErrorException,
	SyntaxErrorException {
	String str = CNFcfl.normalizeInputString(input);
	this.inputLexicalCheck(str);
	CYKTree tree = new CYKTree();
	this.calculateMatrices(str);
	int n = str.length();
	if (((boolean[][]) this.D.get(this.startSymbol()))[0][n-1]) {
	  Handle handle = new Handle(tree);
	  this.createTree(handle, this.startSymbol(), 0, n-1, str);
	  return tree;
	} else {
	  throw new SyntaxErrorException(input);
	}
  }

  protected void createTree(Handle handle, String symbol,
							int i, int j, String str) {
	handle.setKey(symbol);
	if (i == j) {
	  handle.addChild(str.substring(i, i+1));
	} else {
	  handle.addChild();
	  handle.down(0);
	  this.createTree(handle, ((String[][]) this.L.get(symbol))[i][j],
					  i, ((int [][]) this.M.get(symbol))[i][j], str);
	  handle.up();
	  handle.addChild();
	  handle.down(1);
	  this.createTree(handle, ((String[][]) this.R.get(symbol))[i][j],
					  ((int [][]) this.M.get(symbol))[i][j] + 1, j, str);
	  handle.up();
	}
  }

  protected static String normalizeInputString(String str) {
	return str.replaceAll("\\s", "");
  }

  protected void inputLexicalCheck(String str) throws LexicalErrorException {
	for (int i = 0; i < str.length(); i++) {
	  String c = str.substring(i, i+1);
	  if (!this.terminals.contains(c) && !c.equals(" ")) {
		throw new LexicalErrorException(c);
	  }
	}
  }

  public boolean isInputLexicallyCorrect(String input) {
	try {
	  this.inputLexicalCheck(input);
	  return true;
	} catch(LexicalErrorException e) {
	  return false;
	}
  }

  protected void calculateMatrices(String str) {
	this.D = new HashMap<String,boolean[][]>();
	this.L = new HashMap<String,String[][]>();
	this.M = new HashMap<String,int[][]>();
	this.R = new HashMap<String,String[][]>();
	int n = str.length();
	for (Iterator<String> it = this.nonTerminals.iterator(); it.hasNext(); ) {
	  String symbol = (String) it.next();
	  this.D.put(symbol, new boolean[n][n]);
	  this.L.put(symbol, new String[n][n]);
	  this.M.put(symbol, new int[n][n]);
	  this.R.put(symbol, new String[n][n]);
	  for (int i = 0; i < n; i++) {
		for (int j = i; j < n; j++) {
		  this.dMatrix(symbol)[i][j] = false;
		}
	  }
	  for (int i = 0; i < n; i++) {
		if (terminalRules.containsKey(symbol)) {
		  if (terminalRules.get(symbol).contains(str.substring(i, i+1))) {
			this.dMatrix(symbol)[i][i] = true;
		  }
		}
	  }
	}
	for (int j = 1; j < n; j++) {
	  for (int i = j-1; i >= 0; i--) {
		for (Iterator<CNFNonTerminalRule> ruleIt = this.nonTerminalRuleSet.iterator();
			 ruleIt.hasNext(); ) {
		  CNFNonTerminalRule rule = ruleIt.next();
		  String A = rule.lhs();
		  String B = rule.rhs(0);
		  String C = rule.rhs(1);
		  for (int k = i; k < j; k++) {
			if (this.dMatrix(B)[i][k] && this.dMatrix(C)[k+1][j]) {
			  this.dMatrix(A)[i][j] = true;
			  this.lMatrix(A)[i][j] = B;
			  this.mMatrix(A)[i][j] = k;
			  this.rMatrix(A)[i][j] = C;
			}
		  }
		}
	  }
	}
  }

  public static Set<CNFRule> parseCNFLine(String line)
          throws RuleNotInCNFException {
	  int lhsIndex = line.indexOf(CNFcfl.ARROW);
	  String lhs = line.substring(0, lhsIndex-1).trim();
	  String rhs = line.substring(lhsIndex+2).trim();
	  Set<CNFRule> rules = new HashSet<CNFRule>();
	  if (rhs.equals(CNFcfl.OR)) {
		  rules.add(new CNFTerminalRule(lhs, rhs));
		  return rules;
	  }
	  List<String> alternativeStrings = new ArrayList<String>();
	  StringTokenizer st = new StringTokenizer(rhs, CNFcfl.OR);
	  while (st.hasMoreTokens())
		  alternativeStrings.add(st.nextToken());
	  for (Iterator<String> it = alternativeStrings.iterator(); it.hasNext(); ) {
		  List<String> symbols = CNFcfl.parseProduction(it.next());
		  if (symbols.size() == 2)
			  rules.add(new CNFNonTerminalRule(lhs, symbols));
		  else if (symbols.size() == 1)
			  rules.add(new CNFTerminalRule(lhs, symbols.get(0)));
		  else
			  throw new RuleNotInCNFException(line);
	  }
	  return rules;
  }

  protected static List<String> parseProduction(String production) {
	List<String> symbols = new ArrayList<String>();
	StringTokenizer st = new StringTokenizer(production);
	while (st.hasMoreTokens()) {
	  String symbol = st.nextToken();
	  symbols.add(symbol);
	}
	return symbols;
  }

  public String startSymbol() {
	return this.startSymbol;
  }

  public Set<String> nonTerminals() {
	return this.nonTerminals;
  }

  public Set<String> terminals() {
	return this.terminals;
  }

  @SuppressWarnings("unchecked")
public Set productions(String symbol) {
	Set productions = new HashSet();
	if (this.terminalRules.containsKey(symbol))
	  productions.addAll(this.terminalRules.get(symbol));
	if (this.nonTerminalRules.containsKey(symbol))
	  productions.add(this.nonTerminalRules.get(symbol));
	return productions;
  }

  protected void completenessCheck() throws ProductionNotDefinedException {
	  for (Iterator<String> it = this.nonTerminals().iterator(); it.hasNext(); ) {
		  String symbol = it.next();
		  if (this.productions(symbol).size() == 0)
			  throw new ProductionNotDefinedException(symbol);
	  }
  }

  public boolean[][] dMatrix(String symbol) {
	  return (boolean[][]) this.D.get(symbol);
  }

  public String[][] lMatrix(String symbol) {
	  return (String[][]) this.L.get(symbol);
  }

  public int[][] mMatrix(String symbol) {
	  return (int[][]) this.M.get(symbol);
  }

  public String[][] rMatrix(String symbol) {
	  return (String[][]) this.R.get(symbol);
  }

}
