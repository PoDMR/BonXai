package eu.fox7.bonxai.typeautomaton.operations;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;

public interface BinaryOperation {
	public TypeAutomaton apply(TypeAutomaton left, TypeAutomaton right);
}
