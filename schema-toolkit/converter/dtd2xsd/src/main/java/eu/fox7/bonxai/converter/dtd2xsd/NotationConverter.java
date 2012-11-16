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

package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

/**
 * Class for the conversion of a DTD NOTATION to XML XSDSchema.
 *
 * This is not used at the moment. The DTD NOTATION type is currently handled
 * the same way like the enumeration type.
 *
 * It is planned to support this type in the correct way, if there is time left
 * in the period of this diploma-thesis.
 *
 * (Precondition: A representation for the XML XSDSchema "notation"s has to be
 * build in the XML XSDSchema object structure of this project)
 *
 * @author Lars Schmidt
 */
public class NotationConverter extends ConverterBase {

    NotationConverter(XSDSchema xmlSchema, IdentifiedNamespace targetNamespace, boolean namespaceAware) {
        super(xmlSchema, targetNamespace);
    }

    void convert(DocumentTypeDefinition dtd) {

    }

}
