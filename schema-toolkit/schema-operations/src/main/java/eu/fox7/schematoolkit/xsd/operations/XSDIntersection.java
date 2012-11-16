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

package eu.fox7.schematoolkit.xsd.operations;

import java.util.Collection;
import java.util.LinkedList;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaOperation;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.typeautomaton.factories.XSDTypeAutomatonFactory;
import eu.fox7.schematoolkit.typeautomaton.operations.TypeAutomatonIntersection;
import eu.fox7.schematoolkit.typeautomaton.operations.TypeAutomatonUnion;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class XSDIntersection implements SchemaOperation {
	private Collection<XSDSchema> schemas = new LinkedList<XSDSchema>();

	public XSDSchema computeIntersection(XSDSchema...schemas) throws SchemaToolkitException {
		NamespaceList namespaceList = new NamespaceList();
		
		XSDTypeAutomatonFactory factory = new XSDTypeAutomatonFactory(true);
		XSDSchema result = new XSDSchema();
		Collection<TypeAutomaton> typeAutomata = new LinkedList<TypeAutomaton>();

		Namespace targetNamespace = schemas[0].getTargetNamespace();
		namespaceList.setTargetNamespace(targetNamespace);

		DefaultNamespace defaultNamespace = schemas[0].getDefaultNamespace();
		namespaceList.setDefaultNamespace(defaultNamespace);

		for (XSDSchema schema: schemas) {
			if (! targetNamespace.equals(schema.getTargetNamespace()))
				throw new SchemaToolkitException("Schemas must have the same targetnamespace.");
			if (! defaultNamespace.equals(schema.getDefaultNamespace()))
				throw new SchemaToolkitException("Schemas must have the same defaultnamespace.");
			for (IdentifiedNamespace namespace: schema.getNamespaces()) {
				Namespace resultNamespace = namespaceList.getNamespaceByUri(namespace.getUri());
				if (resultNamespace == null)
					namespaceList.addNamespace(namespace);
			}
		}

		
		for (XSDSchema schema: schemas) {
			TypeAutomaton ta = factory.createTypeAutomaton(schema);
			typeAutomata.add(ta);
			System.err.println(ta);
		}
		
		TypeAutomatonIntersection taIntersection = new TypeAutomatonIntersection();
		TypeAutomaton resultTA = taIntersection.apply(typeAutomata);

		System.err.println(resultTA);

		result.setNamespaceList(namespaceList);
		
		for (QualifiedName rootElement: resultTA.getRootElements()) {
			Element element = new Element(rootElement);
			State state;
			try {
				state = resultTA.getNextState(Symbol.create(rootElement.getFullyQualifiedName()), resultTA.getInitialState());
			} catch (NotDFAException e) {
				throw new RuntimeException(e);
			}
			element.setTypeName(resultTA.getTypeName(state));
			result.addElement(element);
		}
		
		for (Type type: resultTA.getTypes()) {
			result.addType(type);
		}
		
		return result;
	}

	@Override
	public SchemaHandler apply() throws SchemaToolkitException {
		return this.computeIntersection(schemas.toArray(new XSDSchema[0])).getSchemaHandler();
	}

	@Override
	public SchemaHandler apply(Collection<Schema> schemas) throws SchemaToolkitException {
		return this.computeIntersection(schemas.toArray(new XSDSchema[0])).getSchemaHandler();
	}

	@Override
	public void addSchema(Schema schema) {
		this.schemas.add((XSDSchema) schema);
	}}
