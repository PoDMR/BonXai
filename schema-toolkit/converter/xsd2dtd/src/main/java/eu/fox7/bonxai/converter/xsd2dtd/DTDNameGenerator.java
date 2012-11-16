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

package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.Iterator;

/**
 * Class DTDNameGenerator
 * @author Lars Schmidt
 */
public class DTDNameGenerator {

    private final XSDSchema xmlSchema;
    private boolean topLevel;

    /**
     * Constructor of class DTDNameGenerator
     * @param xmlSchema     The XML XSDSchema source of the conversion
     */
    public DTDNameGenerator(XSDSchema xmlSchema) {
        this.xmlSchema = xmlSchema;
        this.topLevel = false;
    }

    /**
     * Generate a DTD element name with respect to the global settings for
     * namespace abbreviations and topLevel elements and its qualification
     * property
     *
     * @param fullQualifiedXSDName      the full qualified name of the xsd element
     * @param instanceQualification     the current qualification property of the xsd element
     * @return String   the generated DTD element name
     */
    public QualifiedName getDTDElementName(QualifiedName xsdName, XSDSchema.Qualification instanceQualification) {
    	String fullQualifiedXSDName = xsdName.getFullyQualifiedName();
    	if (this.isTopLevelElement(fullQualifiedXSDName)) {
            this.topLevel = true;
        }

        if (instanceQualification != null) {
            return this.getDTDName(fullQualifiedXSDName, instanceQualification);
        } else if (this.xmlSchema.getElementFormDefault() != null) {
            return this.getDTDName(fullQualifiedXSDName, this.xmlSchema.getElementFormDefault());
        } else {
            return this.getDTDName(fullQualifiedXSDName, null);
        }
    }

    /**
     * Generate a DTD attribute name with respect to the global settings for
     * namespace abbreviations
     *
     * @param fullQualifiedXSDName            the full qualified name of the xsd attribute
     * @param instanceQualification     the current qualification property of the xsd attribute
     * @return String   the generated DTD attribute name
     */
    public QualifiedName getDTDAttributeName(QualifiedName xsdAttributeName, XSDSchema.Qualification instanceQualification) {
        String fullQualifiedXSDName = xsdAttributeName.getFullyQualifiedName();
    	if (this.isTopLevelAttribute(fullQualifiedXSDName)) {
            this.topLevel = true;
        }

        if (instanceQualification != null) {
            return this.getDTDName(fullQualifiedXSDName, instanceQualification);
        } else if (this.xmlSchema.getAttributeFormDefault() != null) {
            return this.getDTDName(fullQualifiedXSDName, this.xmlSchema.getAttributeFormDefault());
        } else {
            return this.getDTDName(fullQualifiedXSDName, null);
        }
    }

    /**
     * Generate a DTD element or attribute name with respect to the global settings for
     * namespace abbreviations and topLevel elements and its qualification
     * property
     *
     * @param fullQualifiedXSDName      the full qualified name of the xsd element or attribute
     * @param qualification     the current qualification property of the xsd element or attribute
     * @return String      the generated DTD element or attribute name
     */
    public String getDTDName(String fullQualifiedXSDName, XSDSchema.Qualification qualification) {
        boolean usePrefix = false;

        if (qualification != null) {
            if (qualification.equals(XSDSchema.Qualification.qualified)) {
                usePrefix = true;
            }
        }

        String dtdName = "";
        if (XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE || XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE) {
            if (!this.getNamespace(fullQualifiedXSDName).equals("") && (this.topLevel || usePrefix) || XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE) {
                if (XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE && usePrefix || !XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE || XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE) {
                    if (this.xmlSchema.getNamespaceList().getNamespaceByUri(this.getNamespace(fullQualifiedXSDName)).getIdentifier() != null) {
                        String namespaceIdentifier = this.xmlSchema.getNamespaceList().getNamespaceByUri(this.getNamespace(fullQualifiedXSDName)).getIdentifier();
                        dtdName = namespaceIdentifier + ":";
                    }
                }
            }
        }
        return dtdName + this.getLocalName(fullQualifiedXSDName);
    }

    /**
     * Get namespace URI from full qualified name.
     *
     * @param fullQualifiedXSDName
     * @return string
     */
    public String getNamespace(String fullQualifiedXSDName) {
        if (fullQualifiedXSDName.lastIndexOf("}") != -1) {
            return fullQualifiedXSDName.substring(1, fullQualifiedXSDName.lastIndexOf("}"));
        } else {
            return "";
        }
    }

    /**
     * Get local name from full qualified name.
     *
     * @param fullQualifiedXSDName
     * @return string
     */
    public String getLocalName(String fullQualifiedXSDName) {
        return fullQualifiedXSDName.substring(fullQualifiedXSDName.lastIndexOf("}") + 1);
    }

    private boolean isTopLevelElement(String fullQualifiedXSDName) {
        for (Iterator<eu.fox7.schematoolkit.xsd.om.Element> it = xmlSchema.getElements().iterator(); it.hasNext();) {
            eu.fox7.schematoolkit.xsd.om.Element element = it.next();
            if (element.getName().equals(fullQualifiedXSDName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTopLevelAttribute(String fullQualifiedXSDName) {
        for (Iterator<eu.fox7.schematoolkit.xsd.om.Attribute> it = xmlSchema.getAttributes().iterator(); it.hasNext();) {
            eu.fox7.schematoolkit.xsd.om.Attribute attribute = it.next();
            if (attribute.getName().equals(fullQualifiedXSDName)) {
                return true;
            }
        }
        return false;
    }
}
