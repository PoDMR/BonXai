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

package eu.fox7.schematoolkit.xsd.om;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlSchema;

import eu.fox7.schematoolkit.common.QualifiedName;

/*
 * This class is intended to wrap a typename 
 * into a type, if the type itself is not 
 * available (e.g. imported from another schema).
 * 
 * It is not meant to be used inside an XML Schema.
 */
public class TypeReference extends Type {
	private static Collection<QualifiedName> XMLSCHEMA_SIMPLETYPES;
	
	static {
		XMLSCHEMA_SIMPLETYPES = new HashSet<QualifiedName>();
		XMLSCHEMA_SIMPLETYPES.add(new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE,"string"));
	}
	
	
	public TypeReference(QualifiedName name) {
		super(name);
	}
	
	public boolean isXSDSimpleType() {
		return XMLSCHEMA_SIMPLETYPES.contains(this.getName());
		
	}
	
	public boolean isXSDType() {
		return XSDSchema.XMLSCHEMA_NAMESPACE.equals(this.getName().getNamespaceURI());
	}
	
	

}
