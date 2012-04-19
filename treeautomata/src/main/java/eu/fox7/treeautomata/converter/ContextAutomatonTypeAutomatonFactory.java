package eu.fox7.treeautomata.converter;

import java.util.Map;
import java.util.Map.Entry;
import eu.fox7.bonxai.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.misc.StateRemapper;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Type;

public class ContextAutomatonTypeAutomatonFactory {
	private ContentAutomaton2TypeConverter caConverter;
	
	@Deprecated
	public ContextAutomatonTypeAutomatonFactory(NamespaceList namespaceList) {
	}
	
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
				ContentAutomaton contentAutomaton = contextAutomaton.getContentAutomaton(origState);
				
				QualifiedName typename = QualifiedName.getQualifiedNameFromFQN(contextAutomaton.getStateValue(origState));

				Type type = this.caConverter.convertContentAutomaton(contentAutomaton, typename, newState);
				typeAutomaton.setType(newState, type);
			}			
		}
		return typeAutomaton;
	}
}
