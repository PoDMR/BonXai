package gjb.flt.grammar;

import java.util.ArrayList;
import java.util.List;

import gjb.flt.grammar.CNFRule;

public class CNFNonTerminalRule extends CNFRule {

    protected List<String> rhs;

    public CNFNonTerminalRule(String lhs, List<String> rhs) {
        super(lhs);
        this.rhs = new ArrayList<String>();
        this.rhs.addAll(rhs);
    }

    public String rhs(int index) {
        return rhs.get(index);
    }

    public List<String> rhs() {
        return this.rhs;
    }

    public boolean isCNFTerminalRule() {
        return false;
    }

    public boolean isCNFNonTerminalRule() {
        return true;
    }

    public String toString() {
        return this.lhs() + " -> " + this.rhs().toString();
    }

}
