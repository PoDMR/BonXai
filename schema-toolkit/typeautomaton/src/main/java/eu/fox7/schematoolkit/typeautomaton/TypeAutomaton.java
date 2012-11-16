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

import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.common.ElementProperties;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.Type;

public interface TypeAutomaton extends ModifiableStateNFA, StateDFA, Schema {
	public QualifiedName getTypeName(State state);
	public Type getType(State state);
	
	public Collection<QualifiedName> getRootElements();
	
	public void setType(State state, Type type);
	public void setType(QualifiedName typeName, Type type);
	public void setTypeName(State state, QualifiedName typeName);
	
	public void setElementProperties(State state, ElementProperties elementProperties);
	public ElementProperties getElementProperties(State state);
	public Collection<Type> getTypes();
	public Collection<Symbol> getRootSymbols();
}