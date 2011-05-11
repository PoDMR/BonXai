package gjb.flt.grammar;

public class RuleNotInCNFException extends Exception {

    private static final long serialVersionUID = 1L;
    protected String line;

  public RuleNotInCNFException(String line) {
	super();
	this.line = line;
  }

  public String getMessage() {
	return "rule not in CNF: '" + this.line + "'";
  }

}
