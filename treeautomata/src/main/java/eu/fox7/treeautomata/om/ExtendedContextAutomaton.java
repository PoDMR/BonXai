package eu.fox7.treeautomata.om;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.schematoolkit.common.AbstractAttribute;

public class ExtendedContextAutomaton extends AnnotatedSparseNFA<StateNFA,Object> {
	private Map<State,Set<AbstractAttribute>> attributes = new HashMap<State,Set<AbstractAttribute>>(); 
	
	public ExtendedContextAutomaton() {
		super();
	}

	public ExtendedContextAutomaton(StateNFA nfa, Map<State, State> stateRemap) {
		super(nfa, stateRemap);
	}

	public ExtendedContextAutomaton(StateNFA nfa) {
		super(nfa);
	}

	public enum StateType {
		CONTENT, ELEMENTREF, TYPEREF
	}
	
	public StateType getStateType(State state) {
		return null;
	}

	public void setContentAutomaton(State state, StateNFA contentAutomaton) {
		this.annotate(state, contentAutomaton);
	}
	
	public Set<AbstractAttribute> getAttributes(State state) {
		return this.attributes.get(state);
	}
	
	public void addAttribute(State state, AbstractAttribute attribute) {
		Set<AbstractAttribute> stateAttributes = this.attributes.get(state);
		if (stateAttributes==null) {
			stateAttributes = new HashSet<AbstractAttribute>();
			this.attributes.put(state, stateAttributes);
		}
		
		stateAttributes.add(attribute);
	}
}
