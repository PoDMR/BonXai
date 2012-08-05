package eu.fox7.schematoolkit.typeautomaton.operations;

import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;

public interface UnaryOperation {
	public TypeAutomaton apply(TypeAutomaton source);
}
