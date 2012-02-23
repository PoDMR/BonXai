package eu.fox7.bonxai.typeautomaton.operations;

import java.util.Collection;
import java.util.LinkedList;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;

public abstract class Operation implements UnaryOperation, BinaryOperation {

	@Override
	public TypeAutomaton apply(TypeAutomaton left, TypeAutomaton right) {
		LinkedList<TypeAutomaton> list = new LinkedList<TypeAutomaton>();
		list.add(left);
		list.add(right);
		return this.apply(list);
	}

	@Override
	public TypeAutomaton apply(TypeAutomaton source) {
		LinkedList<TypeAutomaton> list = new LinkedList<TypeAutomaton>();
		list.add(source);
		return this.apply(list);
	}
	
	public abstract TypeAutomaton apply(Collection<TypeAutomaton> input);

}
