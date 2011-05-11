package eu.fox7.upafixer.impl;

import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import eu.fox7.treeautomata.converter.ContentAutomaton2TypeConverter;
import eu.fox7.treeautomata.converter.Type2ContentAutomatonConverter;
import eu.fox7.upafixer.UPAFixer;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.regex.Regex;
import gjb.flt.treeautomata.impl.ContentAutomaton;

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
	}
}
