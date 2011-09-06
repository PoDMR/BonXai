package eu.fox7.flt.grammar;

public class SyntaxErrorException extends Exception {

    private static final long serialVersionUID = 1L;
    protected String expr;

  public SyntaxErrorException(String expr) {
	super();
	this.expr = expr;
  }

  public String getMessage() {
	return "syntax error in '" + this.expr + "'";
  }

}
