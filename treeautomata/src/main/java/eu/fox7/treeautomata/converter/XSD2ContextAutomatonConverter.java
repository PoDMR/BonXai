package eu.fox7.treeautomata.converter;

import java.util.Map;
import java.util.Map.Entry;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.misc.StateRemapper;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;

public class XSD2ContextAutomatonConverter {
	private boolean addSimpleTypes; 
	private Type2ContentAutomatonConverter typeConverter;
	
	public XSD2ContextAutomatonConverter() {
		this(false);
	}

	public XSD2ContextAutomatonConverter(boolean addSimpleTypes) {
		this.addSimpleTypes = addSimpleTypes;
	}
	
	public void setAddSimpleTypes(boolean addSimpleTypes) {
		this.addSimpleTypes = addSimpleTypes;
	}

	public ContextAutomaton convertXSD(XSDSchema xsd) {
		this.typeConverter = new Type2ContentAutomatonConverter();
		XSDTypeAutomatonFactory factory = new XSDTypeAutomatonFactory(this.addSimpleTypes);
		TypeAutomaton typeAutomaton = factory.createTypeAutomaton(xsd);
		return this.convertTypeAutomaton(typeAutomaton);
	}

	public ContextAutomaton convertTypeAutomaton(TypeAutomaton typeAutomaton) {
		Map<State,State> stateMap = StateRemapper.stateRemapping(typeAutomaton);
		ContextAutomaton contextAutomaton = new ContextAutomaton(typeAutomaton,stateMap);
		
		for (Entry<State,State> entry: stateMap.entrySet()) {
			State origState = entry.getKey();
			State newState = entry.getValue();
			if (! typeAutomaton.isInitialState(origState)) {
				Type type = typeAutomaton.getType(origState).getReference();
				ContentAutomaton contentAutomaton = this.typeConverter.convertType(type);
				contextAutomaton.annotate(newState, contentAutomaton);
			}			
		}
		return contextAutomaton;
	}
	

} 