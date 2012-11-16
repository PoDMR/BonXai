/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.typeautomaton;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.common.ElementProperties;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.TypeReference;

public class AnnotatedNFATypeAutomaton extends
		SparseNFA implements TypeAutomaton {
	
	private HashMap<QualifiedName,Type> types = new HashMap<QualifiedName,Type>();
	private HashMap<State,QualifiedName> typeNames = new HashMap<State,QualifiedName>();
	private HashMap<State,ElementProperties> elementProperties = new HashMap<State,ElementProperties>();
	
	public AnnotatedNFATypeAutomaton() {
		super();
	}
	
	public AnnotatedNFATypeAutomaton(StateNFA nfa, Map<State,State> stateMap) {
		super(nfa, stateMap);
	}

	@Override
	public Type getType(State state) {
		QualifiedName typename = typeNames.get(state);
		Type type = types.get(typename);
		if ((type == null) && (typename != null))
			type = new TypeReference(typename);
		return type;
	}

	@Override
	public QualifiedName getTypeName(State state) {
		return typeNames.get(state);
	}

	@Override
	public void setType(State state, Type type) {
		types.put(type.getName(), type);
		typeNames.put(state, type.getName());
	}

	@Override
	public void setType(QualifiedName typeName, Type type) {
		types.put(typeName, type);
	}

	@Override
	public void setTypeName(State state, QualifiedName typeName) {
		typeNames.put(state, typeName);
	}

	@Override
	public void setElementProperties(State state,
			ElementProperties elementProperties) {
		this.elementProperties.put(state, elementProperties);
	}

	@Override
	public ElementProperties getElementProperties(State state) {
		return this.elementProperties.get(state);
	}

	@Override
	public Collection<Type> getTypes() {
		return this.types.values();
	}

	@Override
	public Collection<QualifiedName> getRootElements() {
		Collection<QualifiedName> elements = new HashSet<QualifiedName>();
		for (Transition transition: this.getOutgoingTransitions(initialState))
			elements.add(QualifiedName.getQualifiedNameFromFQN(transition.getSymbol().toString()));
		return elements;
	}
	
	@Override
	public Collection<Symbol> getRootSymbols() {
		Collection<Symbol> symbols = new HashSet<Symbol>();
		for (Transition transition: this.getOutgoingTransitions(initialState))
			symbols.add(transition.getSymbol());
		return symbols;
	}
	
	@Override
	public TypeAutomatonSchemaHandler getSchemaHandler() {
		return new TypeAutomatonSchemaHandler(this);
	}
	
	@Override
	public SchemaLanguage getSchemaLanguage() {
		return SchemaLanguage.TYPEAUTOMATON;
	}
}
