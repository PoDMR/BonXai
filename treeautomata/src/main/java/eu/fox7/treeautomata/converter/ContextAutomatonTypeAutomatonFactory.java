package eu.fox7.treeautomata.converter;

import java.util.Map;
import java.util.Map.Entry;

import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.automata.misc.StateRemapper;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.xsd.om.Type;

public class ContextAutomatonTypeAutomatonFactory {
	private ContentAutomaton2TypeConverter caConverter;
	
	@Deprecated
	public ContextAutomatonTypeAutomatonFactory(NamespaceList namespaceList) {
	}
	
	public ContextAutomatonTypeAutomatonFactory() {
	}
	
	
	public TypeAutomaton convertContextAutomaton(AnnotatedSparseNFA<? extends SparseNFA, ?> contextAutomaton) {
		Map<State,State> stateMap = StateRemapper.stateRemapping(contextAutomaton);
		TypeAutomaton typeAutomaton = new AnnotatedNFATypeAutomaton(contextAutomaton,stateMap);
		this.caConverter = new ContentAutomaton2TypeConverter(typeAutomaton);
		Namespace namespace = this.getTargetNamespace(contextAutomaton);		

		for (Entry<State,State> entry: stateMap.entrySet()) {
			State origState = entry.getKey();
			State newState = entry.getValue();
			if (! contextAutomaton.isInitialState(origState)) {
				QualifiedName typename = new QualifiedName(namespace, contextAutomaton.getStateValue(origState));
				typeAutomaton.setTypeName(newState, typename);
			}
		}

		for (Entry<State,State> entry: stateMap.entrySet()) {
			State origState = entry.getKey();
			State newState = entry.getValue();
			
			if (! contextAutomaton.isInitialState(origState)) {
				StateNFA contentAutomaton = contextAutomaton.getAnnotation(origState);
				
				// QualifiedName typename = QualifiedName.getQualifiedNameFromFQN(contextAutomaton.getStateValue(origState));
				if (contentAutomaton != null) { 
					QualifiedName typename = typeAutomaton.getTypeName(newState);
					Type type = this.caConverter.convertContentAutomaton(contentAutomaton, typename, newState);
					typeAutomaton.setType(newState, type);
				} else
					System.err.println("No content automaton for state " + origState);
			}			
		}
		return typeAutomaton;
	}

	private Namespace getTargetNamespace(
			AnnotatedSparseNFA<? extends SparseNFA, ?> contextAutomaton) {
		try {
			State initial = contextAutomaton.getInitialState();
			Transition transition = contextAutomaton.getOutgoingTransitions(initial).iterator().next();
			String namespaceUri = QualifiedName.getNamespaceFromFQN(transition.getSymbol().toString());
			return new DefaultNamespace(namespaceUri);
		} catch (NullPointerException e) {
			return Namespace.EMPTY_NAMESPACE;
		}
	}
}
