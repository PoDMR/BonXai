package eu.fox7.treeautomata.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.BidiMap;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.misc.StateRemapper;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.treeautomata.om.ExtendedContextAutomaton;

public class XSD2ContextAutomatonConverter {
	private boolean addSimpleTypes; 
	private Type2ContentAutomatonConverter typeConverter;
	private Map<Element,State> elementStateMap;

    /** 
     * Maps types to states of the context automaton
     */
    private Map<QualifiedName, State> typeMap;

	public XSD2ContextAutomatonConverter() {
		this(false);
	}

	public XSD2ContextAutomatonConverter(boolean addSimpleTypes) {
		this.addSimpleTypes = addSimpleTypes;
	}
	
	public void setAddSimpleTypes(boolean addSimpleTypes) {
		this.addSimpleTypes = addSimpleTypes;
	}

	public ExtendedContextAutomaton convertXSD(XSDSchema xsd) {
		this.elementStateMap = new HashMap<Element,State>();
		this.typeConverter = new Type2ContentAutomatonConverter();
		XSDTypeAutomatonFactory factory = new XSDTypeAutomatonFactory(this.addSimpleTypes);
		TypeAutomaton typeAutomaton = factory.createTypeAutomaton(xsd);

		Map<State,State> stateMap = StateRemapper.stateRemapping(typeAutomaton);
		this.typeMap = new HashMap<QualifiedName,State>();
		for (Entry<QualifiedName,State> entry: factory.getTypeMap().entrySet())
			typeMap.put(entry.getKey(), stateMap.get(entry.getValue()));
		
		ExtendedContextAutomaton contextAutomaton = new ExtendedContextAutomaton(typeAutomaton,stateMap);
		
		for (Entry<State,State> entry: stateMap.entrySet()) {
			State origState = entry.getKey();
			State newState = entry.getValue();
			if (! typeAutomaton.isInitialState(origState)) {
				Type type = typeAutomaton.getType(origState);
				StateNFA contentAutomaton = this.typeConverter.convertType(type);
				contextAutomaton.annotate(newState, contentAutomaton);
				
				BidiMap<Particle, State> localElementStateMap = typeConverter.getElementStateMap();
				for (Entry<Particle, State> entry2: localElementStateMap.entrySet()) 
					if (entry2.getKey() instanceof Element) {
						Element element = (Element) entry2.getKey();
						this.elementStateMap.put(element, entry2.getValue());
					}
			}			
		}
		return contextAutomaton;
	}
	
	public Map<QualifiedName, State> getTypeMap() {
		return typeMap;
	}
	
	public Map<Element, State> getElementStateMap() {
		return this.elementStateMap;
	}
	

} 