package eu.fox7.upafixer.impl;

import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.treeautomata.converter.ContentAutomaton2TypeConverter;
import eu.fox7.treeautomata.converter.Type2ContentAutomatonConverter;
import eu.fox7.upafixer.UPAFixer;

public abstract class AbstractUPAFixer implements UPAFixer {
	protected TypeAutomaton typeAutomaton;
	protected Type2ContentAutomatonConverter typeConverter;
	protected ContentAutomaton2TypeConverter caConverter;
	
	@Override
	public void fixUPA(TypeAutomaton typeAutomaton) {
		this.typeAutomaton = typeAutomaton;
		this.typeConverter = new Type2ContentAutomatonConverter();
		this.caConverter = new ContentAutomaton2TypeConverter(typeAutomaton);
		
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				Type type = typeAutomaton.getType(state).getReference();
				String typename = type.getName();
				ContentAutomaton contentAutomaton = typeConverter.convertType(type);
				Regex regex = this.fixUPA(contentAutomaton);
				Type newType = this.caConverter.convertRegex(regex, typename, state);
				this.typeAutomaton.updateType(typename, newType);
			}
		}
	}

	@Override
	public void fixUPA(XSDSchema xsdSchema) {
		XSDTypeAutomatonFactory taFactory = new XSDTypeAutomatonFactory();
		TypeAutomaton typeAutomaton = taFactory.createTypeAutomaton(xsdSchema);
		this.fixUPA(typeAutomaton);
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				SymbolTableRef<Type> typeRef = typeAutomaton.getType(state);
				Type type = typeRef.getReference();
				String typename = typeRef.getKey();
				
				xsdSchema.getTypeSymbolTable().updateOrCreateReference(typename, type);
			}
		}
	}
}
