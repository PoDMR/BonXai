package eu.fox7.bonxai.typeautomaton.operations;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;

public interface UnaryOperation {
	public TypeAutomaton apply(TypeAutomaton source);
}
