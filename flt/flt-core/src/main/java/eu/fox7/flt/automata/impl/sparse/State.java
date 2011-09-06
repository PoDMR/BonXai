package eu.fox7.flt.automata.impl.sparse;

public class State {

	static int counter = 0;
	protected int id;

	public State() {
		this.id = ++counter;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return "q" + id;
	}

}
