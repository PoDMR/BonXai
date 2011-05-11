package eu.fox7.treeautomata.converter;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.misc.StateRemapper;
import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import java.util.Map;
import java.util.Map.Entry;

import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

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