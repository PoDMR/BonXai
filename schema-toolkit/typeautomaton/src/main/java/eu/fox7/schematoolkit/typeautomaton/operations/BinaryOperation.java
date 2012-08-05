package eu.fox7.schematoolkit.typeautomaton.operations;

import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;

public interface BinaryOperation {
	public TypeAutomaton apply(TypeAutomaton left, TypeAutomaton right);
}
