/**
 * Created on Nov 12, 2009
 * Modified on $Date: 2009-11-12 23:01:54 $
 */
package eu.fox7.flt.regex.factories;

import java.util.HashSet;
import java.util.Set;

import eu.fox7.flt.automata.factories.sparse.GNFAFactory;
import eu.fox7.flt.automata.impl.sparse.GNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.Regex;


/**
 * @author lucg5005
 * @version $Revision: 1.3 $
 *
 */
public class StateEliminationFactory {

	protected RegexFactory factory;
	protected GNFAFactory gnfaFactory;

	public StateEliminationFactory() {
		this(new Regex());
	}

	public StateEliminationFactory(Regex regex) {
		this.factory = new RegexFactory(regex);
		this.gnfaFactory = new GNFAFactory(regex);
	}

	public Regex create(StateNFA nfa, boolean isGlushkov) {
		GNFA gnfa = gnfaFactory.create(nfa, isGlushkov);
		return create(gnfa);
	}

	public Regex create(GNFA gnfa) {
		if (gnfa.getNumberOfStates() == 2) {
			return gnfa.getRegex(gnfa.getInitialState(), gnfa.getFinalState());
		} else {
			State ripState = eu.fox7.util.Collections.getOne(gnfa.getInternalStates());
			Regex r2 = gnfa.getRegex(ripState, ripState);
			Set<State> iSet = new HashSet<State>(gnfa.getInternalStates());
			iSet.remove(ripState);
			Set<State> jSet = new HashSet<State>(iSet);
			iSet.add(gnfa.getInitialState());
			jSet.add(gnfa.getFinalState());
			for (State iState : iSet) {
				for (State jState : jSet) {
					Regex r1 = gnfa.getRegex(iState, ripState);
					Regex r3 = gnfa.getRegex(ripState, jState);
					Regex r4 = gnfa.getRegex(iState, jState);
					Regex r = null;
					Regex r_a = null;
					if (r1 != null && r3 != null)
						if (r2 != null)
							r_a = factory.createConcatenation(r1, factory.createZeroOrMore(r2), r3);
						else
							r_a = factory.createConcatenation(r1, r3);
					if (r_a != null && r4 != null)
						r = factory.createUnion(r_a, r4);
					else if (r_a != null)
						r = r_a;
					else if (r4 != null)
						r = r4;
					if (r != null)
						gnfa.setRegex(iState, jState, r);
				}
			}
			gnfa.removeState(ripState);
			return create(gnfa);
		}
	}

}
