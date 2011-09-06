package eu.fox7.treeautomata.converter;

import java.util.Map;
import java.util.Map.Entry;
import eu.fox7.bonxai.common.SymbolAlreadyRegisteredException;
import eu.fox7.bonxai.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.misc.StateRemapper;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;

public class ContextAutomatonTypeAutomatonFactory {
	private ContentAutomaton2TypeConverter caConverter;
	
	public ContextAutomatonTypeAutomatonFactory() {
	}
	
	public TypeAutomaton convertContextAutomaton(ContextAutomaton contextAutomaton) {
		Map<State,State> stateMap = StateRemapper.stateRemapping(contextAutomaton);
		TypeAutomaton typeAutomaton = new AnnotatedNFATypeAutomaton(contextAutomaton,stateMap);
		this.caConverter = new ContentAutomaton2TypeConverter(typeAutomaton);
		
		for (Entry<State,State> entry: stateMap.entrySet()) {
			State origState = entry.getKey();
			State newState = entry.getValue();
			if (! contextAutomaton.isInitialState(origState)) {
				String typename = "{}"+contextAutomaton.getStateValue(origState);

				try {
					typeAutomaton.createTypeRef(newState, typename);
				} catch (SymbolAlreadyRegisteredException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		for (Entry<State,State> entry: stateMap.entrySet()) {
			State origState = entry.getKey();
			State newState = entry.getValue();
			
			if (! contextAutomaton.isInitialState(origState)) {
				ContentAutomaton contentAutomaton = contextAutomaton.getContentAutomaton(origState);
				String typename = "{}"+contextAutomaton.getStateValue(origState);

				Type type = this.caConverter.convertContentAutomaton(contentAutomaton, typename, newState);
				typeAutomaton.updateType(typename, type);
			}			
		}
		return typeAutomaton;
	}
}
