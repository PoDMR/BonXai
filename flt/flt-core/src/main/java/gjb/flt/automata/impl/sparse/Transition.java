package gjb.flt.automata.impl.sparse;


public class Transition {

	protected Symbol symbol;
	protected State fromState, toState;

	public Transition(Symbol symbol, State fromState, State toState) {
        assert symbol != null : "symbol is null";
        assert fromState != null : "fromState is null";
        assert toState != null : "toState is null";
		this.symbol = symbol;
		this.fromState = fromState;
		this.toState = toState;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public State getFromState() {
		return fromState;
	}

	public State getToState() {
		return toState;
	}

	@Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transition))
            return false;
        else {
            Transition t = (Transition) o;
            return this.getSymbol() == t.getSymbol() &&
                this.getFromState() == t.getFromState() &&
                this.getToState() == t.getToState();
        }
    }

	@Override
    public int hashCode() {
        return getSymbol().hashCode() + 37*getFromState().hashCode() +
               31*31*getToState().hashCode();
    }

	@Override
    public String toString() {
		return getFromState().toString() + ", " + getSymbol().toString() +
			" -> " + getToState().toString();
	}

    public String toString(SparseNFA nfa) {
        return nfa.getStateValue(getFromState()) + ", " + getSymbol().toString() +
            " -> " + nfa.getStateValue(getToState());
    }

}
