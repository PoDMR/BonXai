/**
 * Created on Jun 16, 2009
 * Modified on $Date: 2009-10-28 12:17:59 $
 */
package eu.fox7.flt.automata.factories.sparse;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.Glushkov;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class SOAFactory extends AbstractIncrementalNFAFactory<SparseNFA> {

	protected String currentState = Glushkov.INITIAL_STATE;

	public SOAFactory() {
		this.nfa = new SparseNFA();
		nfa.setInitialState(Glushkov.INITIAL_STATE);
	}

	public SOAFactory newInstance() {
		return new SOAFactory();
	}

	@Override
	public void add(String[] example) {
		for (String symbolValue : example)
			addSymbol(symbolValue);
		addEnd();
	}

	public void addSymbol(String symbolValue) {
		if (!nfa.hasTransition(symbolValue, currentState, symbolValue))
			nfa.addTransition(symbolValue, currentState, symbolValue);
		currentState = symbolValue;
	}

	public void addEnd() {
		nfa.addFinalState(currentState);
		currentState = Glushkov.INITIAL_STATE;
	}

}
