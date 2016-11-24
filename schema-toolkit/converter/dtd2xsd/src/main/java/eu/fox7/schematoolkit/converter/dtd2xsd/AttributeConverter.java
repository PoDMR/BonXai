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

package eu.fox7.schematoolkit.converter.dtd2xsd;

import eu.fox7.schematoolkit.dtd.om.*;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.common.Annotation;
import eu.fox7.schematoolkit.common.AttributeUse;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.converter.dtd2xsd.exceptions.EnumerationOrNotationTokensEmtpyException;
import eu.fox7.schematoolkit.xsd.XSDSimpleTypes;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema.Qualification;

import java.util.LinkedList;

/**
 * Class for the conversion of a DTD attribute to its XML XSDSchema counterpart.
 * @author Lars Schmidt
 */
public class AttributeConverter extends ConverterBase {

    private DocumentTypeDefinition dtd;
    private Qualification qualification;
    private Element dtdElement;

    /**
     * Constructor of class AttributeConverter/
     * This class handles the conversion process from a DTD attribute to the
     * corresponding XML XSDSchema attribute.
     * @param xmlSchema     The resulting XML XSDSchema object structure schema
     * @param targetNamespace       The targetNamespace of the resulting schema
     * @param namespaceAware        Handle the correct namespaces according to leading abbreviation of dtd names (i.e. "abc:testname") 
     */
    public AttributeConverter(XSDSchema xmlSchema, Namespace targetNamespace) {
        super(xmlSchema, targetNamespace);
        this.qualification = Qualification.qualified;
        this.qualification = null;
    }

    /**
     * Method "convert"
     *
     * This method is the central method of this class.
     * By calling this method the conversion process will be started.
     *
     * @param dtd       The source DTD object structure for the conversion
     * @param dtdElement        The current DTD element, which is the parent of the current DTD attribute
     * @param dtdAttribute      The current DTD attribute as source of the conversion
     * @return eu.fox7.schematoolkit.xsd.om.Attribute        The resulting XSD attribute object
     * @throws Exception        Some different exceptions can be thrown in sub-methods of this method.
     */
    public eu.fox7.schematoolkit.xsd.om.Attribute convert(DocumentTypeDefinition dtd, Element dtdElement, Attribute dtdAttribute) throws ConversionFailedException {
        // Initialize the variables of this class.
        this.dtd = dtd;
        this.dtdElement = dtdElement;

        // Declaration of necessary variables for the conversion progress.
        eu.fox7.schematoolkit.xsd.om.Attribute attribute = null;
        String fixedValue = null;
        AttributeUse attributeUse = null;
        String defaultValue = null;
        Annotation annotation = null;
        QualifiedName attributeName = null;
        QualifiedName typeName = null;
        boolean attributeTypeAttribute = true;

        // Generate a full qualified name for the resulting XSD attribute
        attributeName = this.generateXSDFQName(dtdAttribute.getName());

        // If the targetNamespace object is null, the empty-String will be set as targetNamespaceUri.
        String targetUri = ((this.targetNamespace == null) ? "" : this.targetNamespace.getUri());


        // Handle the type of this DTD attribute
        switch (dtdAttribute.getType()) {
            // Unique ID or name. Must be a valid XML name.
            case ID:
                typeName = XSDSimpleTypes.ID;
                break;
            // Represents the value of an ID attribute of another element.
            case IDREF:
                typeName = XSDSimpleTypes.IDREF;
                break;
            // Represents multiple IDs of elements, separated by whitespace.
            case IDREFS:
                typeName = XSDSimpleTypes.IDREFS;
                break;
            // The name of an entity (which must be declared in the DTD)
            case ENTITY:
                typeName = XSDSimpleTypes.ENTITY;
                break;
            // A list of entity names, separated by whitespaces. (All entities must be declared in the DTD)
            case ENTITIES:
                typeName = XSDSimpleTypes.ENTITIES;
                break;
            // A valid XML name.
            case NMTOKEN:
                typeName = XSDSimpleTypes.NMTOKEN;
                break;
            // A list of valid XML names separated by whitespaces.
            case NMTOKENS:
                typeName = XSDSimpleTypes.NMTOKENS;
                break;
            // Character Data (text that doesn't contain markup)
            case CDATA:
                typeName = XSDSimpleTypes.STRING;
               break;
            // A list of notation names (which must be declared in the DTD) seperated by the pipe operator (x|y).
            case NOTATION:
            case ENUMERATION:
                // A list of values seperated by the pipe operator (x|y). The value of the attribute must be one from this list.
                // Full qualified name of the anonymous notationBaseSimpleType
                typeName = new QualifiedName(targetUri,this.dtdElement.getName().getName() + "-" + dtdAttribute.getName());

                // Full qualified name of the base type
                QualifiedName notationBaseTypeName = XSDSimpleTypes.TOKEN; // xs:NOTATION How can notation elements in XSD be written?

                // SimpleType inheritance
                SimpleContentRestriction notationSimpleContentRestriction = new SimpleContentRestriction(notationBaseTypeName);

                if (dtdAttribute.getEnumerationOrNotationTokens() == null || dtdAttribute.getEnumerationOrNotationTokens().isEmpty()) {
                    throw new EnumerationOrNotationTokensEmtpyException(attributeName.getFullyQualifiedName(), typeName.getFullyQualifiedName());
                }

                if (dtdAttribute.getEnumerationOrNotationTokens() != null) {
                    notationSimpleContentRestriction.addEnumeration(new LinkedList<String>(dtdAttribute.getEnumerationOrNotationTokens()));
                } 

                SimpleType notationAttributeSimpleType = new SimpleType(typeName, notationSimpleContentRestriction, true);

                this.xmlSchema.addType(notationAttributeSimpleType);
                break;
        }

        // Handle the AttributeDefaultPresence of this DTD attribute
        if (dtdAttribute.getAttributeDefaultPresence() != null) {
            if (dtdAttribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.FIXED)) {
                // fixed
                fixedValue = dtdAttribute.getValue();
            } else if (dtdAttribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.REQUIRED)) {
                // attributeUse = REQUIRED
                attributeUse = AttributeUse.required;
            } else if (dtdAttribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.IMPLIED)) {
                // attributeUse = IMPLIED
                attributeUse = AttributeUse.optional;
            }
        }
        // Handle the default value of this DTD attribute.
        // This is only possible, if there is no fixed value set.
        if (fixedValue == null && dtdAttribute.getValue() != null) {
            // default
            defaultValue = dtdAttribute.getValue();
        }

        attribute = new eu.fox7.schematoolkit.xsd.om.Attribute(attributeName, typeName, defaultValue, fixedValue, attributeUse, this.qualification, annotation);

        return attribute;
    }

}
