package eu.fox7.upafixer.impl;

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.treeautomata.converter.ContentAutomaton2TypeConverter;
import eu.fox7.treeautomata.converter.Type2ContentAutomatonConverter;
import eu.fox7.upafixer.UPAFixer;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaToolkitException;

public abstract class AbstractUPAFixer implements UPAFixer {
	protected TypeAutomaton typeAutomaton;
	protected Type2ContentAutomatonConverter typeConverter;
	protected ContentAutomaton2TypeConverter caConverter;
	
	@Override
	public void fixUPA(TypeAutomaton typeAutomaton) throws SchemaToolkitException {
		this.typeAutomaton = typeAutomaton;
		this.typeConverter = new Type2ContentAutomatonConverter();
		this.caConverter = new ContentAutomaton2TypeConverter(typeAutomaton);
		
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				Type type = typeAutomaton.getType(state);
				if (type !=null) {
					QualifiedName typename = type.getName();
					StateNFA contentAutomaton = typeConverter.convertType(type);
					Regex regex = this.fixUPA(contentAutomaton);
					Type newType = this.caConverter.convertRegex(regex, typename, state);
					this.typeAutomaton.setType(typename, newType);
				}
			}
		}
	}

	public abstract Regex fixUPA(StateNFA contentAutomaton) throws SchemaToolkitException;

	@SuppressWarnings("deprecation")
	@Override
	public void fixUPA(Schema schema) throws SchemaToolkitException {
		XSDSchema xsdSchema;
		try {
			xsdSchema = (XSDSchema) schema;
		} catch (ClassCastException e) {
			throw new SchemaToolkitException("Can only fix UPA for XML Schema.", e);
		}
		XSDTypeAutomatonFactory taFactory = new XSDTypeAutomatonFactory(true);
		TypeAutomaton typeAutomaton = taFactory.createTypeAutomaton(xsdSchema);
		this.fixUPA(typeAutomaton);
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				Type type = typeAutomaton.getType(state);
				if (type != null)
					xsdSchema.addType(type);
			}
		}
	}
}
