/**
 * Created on Mar 16, 2009
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.impl;

import java.io.StringWriter;
import java.io.Writer;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.io.DotWriter;
import eu.fox7.flt.automata.io.NFAWriteException;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class GraphAutomatonConverter {

	public SparseNFA convertToNFA(Automaton automaton) {
		SparseNFA nfa = new SparseNFA();
		nfa.setInitialState("q0");
		if (automaton.acceptsEpsilon())
			nfa.addFinalState("q0");
		for (int i = 0; i < automaton.getNumberOfStates() - 1; i++) {
			if (automaton.isInitial(i))
				nfa.addTransition(automaton.getLabel(i), "q0", automaton.getLabel(i));
			if (automaton.isFinal(i))
				nfa.addFinalState(automaton.getLabel(i));
		}
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++)
            for (int j = 0; j < automaton.getNumberOfStates() - 1; j++)
                if (automaton.get(i, j) != 0)
                    nfa.addTransition(automaton.getLabel(j),
                    		automaton.getLabel(i), 
                    		automaton.getLabel(j));
		return nfa;
	}

	public String convertToDot(Automaton automaton)
	        throws NFAWriteException {
		Writer str = new StringWriter();
		DotWriter dotWriter = new DotWriter(str);
		SparseNFA nfa = convertToNFA(automaton);
		dotWriter.write(nfa);
		return str.toString();
	}

}
