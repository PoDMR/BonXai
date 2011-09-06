package eu.fox7.flt.grammar;

public class LexicalErrorException extends Exception {

    private static final long serialVersionUID = 1L;
    protected String c;

  public LexicalErrorException(String c) {
	super();
	this.c = c;
  }

  public String getMessage() {
	return "lexical error: character '" + this.c + "' is not part of the CFL's alphabet";
  }

}
