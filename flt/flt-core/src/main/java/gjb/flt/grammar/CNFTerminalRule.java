package gjb.flt.grammar;

import gjb.flt.grammar.CNFRule;

public class CNFTerminalRule extends CNFRule {
  
  protected String rhs;
  
  public CNFTerminalRule(String lhs, String rhs) {
	super(lhs);
	this.rhs = rhs;
  }

  public String rhs() {
	return this.rhs;
  }

  public boolean isCNFTerminalRule() {
	return true;
  }

  public boolean isCNFNonTerminalRule() {
	return false;
  }

  public String toString() {
	return this.lhs() + " -> " + this.rhs();
  }

}
