package eu.fox7.treeautomata.converter;

import java.util.Map;
import java.util.Map.Entry;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.misc.StateRemapper;
import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException;
import de.tudortmund.cs.bonxai.typeautomaton.AnnotatedNFATypeAutomaton;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.xsd.Type;

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
