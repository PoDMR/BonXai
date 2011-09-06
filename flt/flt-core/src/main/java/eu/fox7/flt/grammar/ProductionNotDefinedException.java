package eu.fox7.flt.grammar;

public class ProductionNotDefinedException extends Exception {

    private static final long serialVersionUID = 1L;
    protected String symbol;

  public ProductionNotDefinedException(String symbol) {
	super();
	this.symbol = symbol;
  }

  public String getMessage() {
	return "no production defined for non-terminal '" + this.symbol + "'";
  }

}
