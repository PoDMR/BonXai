import java.util.*;

import gjb.flt.automata.generators.ShortestStringGenerator;
import gjb.flt.automata.impl.sparse.SparseNFA;

public class TestTemp {

	public static void main(String[] args) {
		SparseNFA nfa = new SparseNFA();
		nfa.addTransition("a", "q1", "q2");
		nfa.addTransition("b", "q1", "q4");
		nfa.addTransition("b", "q2", "q2");
		nfa.addTransition("c", "q2", "q3");
		nfa.addTransition("f", "q3", "q3");
		nfa.addTransition("d", "q4", "q3");
		nfa.addTransition("a", "q3", "q7");
		nfa.addTransition("e", "q1", "q5");
		nfa.addTransition("f", "q5", "q4");
		nfa.addTransition("c", "q4", "q6");
		nfa.addTransition("d", "q6", "q5");
		nfa.setInitialState("q1");
		nfa.setFinalState("q7");
   		System.out.println(nfa.toString());
	}

}
