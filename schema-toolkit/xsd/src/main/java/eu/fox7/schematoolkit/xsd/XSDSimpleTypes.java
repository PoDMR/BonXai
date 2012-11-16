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

package eu.fox7.schematoolkit.xsd;

import java.util.HashSet;
import java.util.Set;

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public class XSDSimpleTypes {
	public static final QualifiedName STRING = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "string");
	public static final QualifiedName BOOLEAN = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "boolean");
	public static final QualifiedName FLOAT = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "float");
	public static final QualifiedName DOUBLE = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "double");
	public static final QualifiedName DECIMAL = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "decimal");
	public static final QualifiedName DATETIME = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "datetime");
	public static final QualifiedName DURATION = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "duration");
	public static final QualifiedName HEXBINARY = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "hexBinary");
	public static final QualifiedName BASE64BINARY = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "base64Binary");
	public static final QualifiedName ANYURI = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "anyURI");
	public static final QualifiedName ID = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "ID");
	public static final QualifiedName IDREF = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "IDREF");
	public static final QualifiedName ENTITY = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "ENTITY");
	public static final QualifiedName NOTATION = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "NOTATION");
	public static final QualifiedName NORMALIZEDSTRING = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "normalizedString");
	public static final QualifiedName TOKEN = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "token");
	public static final QualifiedName LANGUAGE = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "language");
	public static final QualifiedName IDREFS = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "IDREFS");
	public static final QualifiedName ENTITIES = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "ENTITIES");
	public static final QualifiedName NMTOKEN = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "NMTOKEN");
	public static final QualifiedName NMTOKENS = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "NMTOKENS");
	public static final QualifiedName NAME = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "Name");
	public static final QualifiedName QNAME = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "QName");
	public static final QualifiedName NCNAME = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "NCName");
	public static final QualifiedName INTEGER = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "integer");
	public static final QualifiedName NONNEGATIVEINTEGER = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "nonNegativeInteger");
	public static final QualifiedName POSITIVEINTEGER = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "positiveInteger");
	public static final QualifiedName NONPOSITIVEINTEGER = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "nonPositiveInteger");
	public static final QualifiedName NEGATIVEINTEGER = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "negativeInteger");
	public static final QualifiedName BYTE = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "byte");
	public static final QualifiedName INT = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "int");
	public static final QualifiedName LONG = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "long");
	public static final QualifiedName SHORT = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "short");
	public static final QualifiedName UNSIGNEDBYTE = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "unsignedByte");
	public static final QualifiedName UNSIGNEDINT = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "unsignedInt");
	public static final QualifiedName UNSIGNEDLONG = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "unsignedLong");
	public static final QualifiedName UNSIGNEDSHORT = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "unsignedShort");
	public static final QualifiedName DATE = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "date");
	public static final QualifiedName TIME = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "time");
	public static final QualifiedName GYEARMONTH = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "gYearMonth");
	public static final QualifiedName GYEAR = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "gYear");
	public static final QualifiedName GMONTHDAY = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "gMonthDay");
	public static final QualifiedName GDAY = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "gDay");
	public static final QualifiedName GMONTH = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "gMonth");
	public static final QualifiedName ANYSIMPLETYPE = new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, "anySimpleType");
	
	private static Set<QualifiedName> simpleTypes = new HashSet<QualifiedName>();
	
	static {
		simpleTypes.add(STRING);
		simpleTypes.add(BOOLEAN);
		simpleTypes.add(FLOAT);
		simpleTypes.add(DOUBLE);
		simpleTypes.add(DECIMAL);
		simpleTypes.add(DATETIME);
		simpleTypes.add(DURATION);
		simpleTypes.add(HEXBINARY);
		simpleTypes.add(BASE64BINARY);
		simpleTypes.add(ANYURI);
		simpleTypes.add(ID);
		simpleTypes.add(IDREF);
		simpleTypes.add(ENTITY);
		simpleTypes.add(NOTATION);
		simpleTypes.add(NORMALIZEDSTRING);
		simpleTypes.add(TOKEN);
		simpleTypes.add(LANGUAGE);
		simpleTypes.add(IDREFS);
		simpleTypes.add(ENTITIES);
		simpleTypes.add(NMTOKEN);
		simpleTypes.add(NMTOKENS);
		simpleTypes.add(NAME);
		simpleTypes.add(QNAME);
		simpleTypes.add(NCNAME);
		simpleTypes.add(INTEGER);
		simpleTypes.add(NONNEGATIVEINTEGER);
		simpleTypes.add(POSITIVEINTEGER);
		simpleTypes.add(NONPOSITIVEINTEGER);
		simpleTypes.add(NEGATIVEINTEGER);
		simpleTypes.add(BYTE);
		simpleTypes.add(INT);
		simpleTypes.add(LONG);
		simpleTypes.add(SHORT);
		simpleTypes.add(UNSIGNEDBYTE);
		simpleTypes.add(UNSIGNEDINT);
		simpleTypes.add(UNSIGNEDLONG);
		simpleTypes.add(UNSIGNEDSHORT);
		simpleTypes.add(DATE);
		simpleTypes.add(TIME);
		simpleTypes.add(GYEARMONTH);
		simpleTypes.add(GYEAR);
		simpleTypes.add(GMONTHDAY);
		simpleTypes.add(GDAY);
		simpleTypes.add(GMONTH);
		simpleTypes.add(ANYSIMPLETYPE);
	}
	
	public static boolean isSimpleType(QualifiedName typename) {
		return simpleTypes.contains(typename);
	}

	public static boolean isSimpleTypeLocalName(String typename) {
		return simpleTypes.contains(new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE, typename));
	}
	
	public static boolean isSimpleTypeFullyQualifiedName(String typename) {
		return simpleTypes.contains(QualifiedName.getQualifiedNameFromFQN(typename));
	}	
}
