package eu.fox7.bonxai.typeautomaton;

import java.util.Collection;

import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.schematoolkit.common.ElementProperties;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Type;

public interface TypeAutomaton extends ModifiableStateNFA, StateDFA {
	public QualifiedName getTypeName(State state);
	public Type getType(State state);
	
	public Collection<QualifiedName> getRootElements();
	
	public void setType(State state, Type type);
	public void setType(QualifiedName typeName, Type type);
	public void setTypeName(State state, QualifiedName typeName);

	public void setElementProperties(State state, ElementProperties elementProperties);
	public ElementProperties getElementProperties(State state);
	public Collection<Type> getTypes();
}