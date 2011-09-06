package eu.fox7.flt.grammar;

public class CFGRule {

	protected String lhs;
	protected String[] rhs;

	public CFGRule(String lhs, String[] rhs) {
		this.lhs = lhs;
		this.rhs = new String[rhs.length];
		for (int i = 0; i < rhs.length; i++) {
			this.rhs[i] = rhs[i];
		}
	}

	public String lhs() {
		return this.lhs;
	}

	public int numberOfSymbols() {
		return this.rhs.length;
	}

	public String[] rhs() {
	    String[] result = new String[rhs.length];
	    for (int i = 0; i < rhs.length; i++) {
            result[i] = rhs[i];
        }
		return result;
	}

	public String rhs(int i) {
		return this.rhs[i];
	}

}
