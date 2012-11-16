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

import eu.fox7.bonxai.converter.dtd2xsd.exceptions.DTDNameIsEmptyException;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.DTDNameStartsWithUnsupportedSymbolException;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.IdentifiedNamespaceNotFoundException;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import java.util.Iterator;

/**
 * Base class for all DTD2XSD converter classes
 * @author Lars Schmidt
 */
public abstract class ConverterBase extends NameChecker {

    protected XSDSchema xmlSchema;
    protected Namespace targetNamespace;

    public ConverterBase(XSDSchema xmlSchema, Namespace targetNamespace) {
        this.xmlSchema = xmlSchema;
        this.targetNamespace = targetNamespace;
    }

    /**
     * Method generateXSDFQName is used for Element- and Attributenames
     * 
     * In DTD there are names like "xs:minLength" allowed for an element or an 
     * attibute. These are not a valid XML XSDSchema NCNames! 
     * It is also a problem, that there are no namespaces in DTD.
     * 
     * A possible solution for this problem is, to define IdentifiedNamespace 
     * Dummies and use their abbreviation for the full-qualified name in the XSD
     * object model.
     *
     * @param qualifiedName
     * @return QualifiedName
     * @throws Exception 
     */
    protected QualifiedName generateXSDFQName(QualifiedName qualifiedName) throws ConversionFailedException {
        if (qualifiedName == null || qualifiedName.equals("")) {
            throw new DTDNameIsEmptyException();
        }

        String newName = qualifiedName.getName().replaceAll("[^0-9a-zA-Z\\.\\-\\_]", "-");
        if (newName.startsWith("-")) {
        	throw new DTDNameStartsWithUnsupportedSymbolException(qualifiedName.getFullyQualifiedName());
        }
        QualifiedName returnXSDFQName = new QualifiedName(this.targetNamespace,newName);

        return returnXSDFQName;
    }

}
