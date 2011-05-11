package gjb.flt.grammar;

public class CNFRule {
  
  protected String lhs;

  protected CNFRule(String lhs) {
	this.lhs = lhs;
  }

  public boolean isCNFTerminalRule() {
	return false;
  }

  public boolean isCNFNonTerminalRule() {
	return false;
  }

  public String lhs() {
	return this.lhs;
  }

}
