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

package eu.fox7.schematoolkit.dtd.writer;

import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.dtd.common.DTDNameChecker;
import eu.fox7.schematoolkit.dtd.common.exceptions.AttributeEnumerationTypeIllegalDefaultValueException;
import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeNameEmptyException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeNullException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeTypeEnumOrNotationWithEmptyTokensException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeTypeNullException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeUnkownAttributeDefaultPresenceException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeValueEmptyException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDElementNameEmptyException;

import java.util.Iterator;

/**
 * Class DTDAttributeWriter
 * This class generates a DTD attribute declaration String for one given 
 * attribute.
 *
 * @author Lars Schmidt
 */
public class DTDAttributeWriter {

    private final Attribute attribute;

    /**
     * Constructor of class DTDAttributeWriter
     * @param attribute
     */
    public DTDAttributeWriter(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * Get a String containing the declaration of the current attribute for the given element
     * @param qualifiedName
     * @return String
     * @throws Exception
     */
    public String getAttributeDeclarationString(QualifiedName qualifiedName) throws DTDException {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String attributeString = "";

        if (this.attribute != null) {
            attributeString += "<!ATTLIST ";

            if (this.attribute.getName() == null || this.attribute.getName().equals("")) {
                throw new DTDAttributeNameEmptyException(qualifiedName.getName());
            }
            if (!nameChecker.checkForXMLName(this.attribute.getName())) {
                throw new IllegalNAMEStringException("ATTLIST: " + qualifiedName + " " + this.attribute.getName(), this.attribute.getName().getFullyQualifiedName());
            }
            if (qualifiedName == null || qualifiedName.equals("")) {
                throw new DTDElementNameEmptyException("for an attribute declaration: " + this.attribute.getName());
            }
            attributeString += qualifiedName.getName() + " ";
            attributeString += this.attribute.getName().getName() + " ";
            attributeString += getAttributeTypeString(qualifiedName);
            attributeString += ">\n";
        } else {
            throw new DTDAttributeNullException(qualifiedName.getName());
        }
        return attributeString;
    }

    private String getAttributeTypeString(QualifiedName qualifiedName) throws DTDException {
        String typeString = "";
        if (this.attribute.getType() == null) {
            throw new DTDAttributeTypeNullException(this.attribute.getName().getFullyQualifiedName());
        }
        if (this.attribute.getType().equals(AttributeType.CDATA)) {
            // StringType
            typeString += "CDATA" + " " + getAttributePresenceAndValueString(qualifiedName);

        } else if (this.attribute.getType().equals(AttributeType.ID)
                || this.attribute.getType().equals(AttributeType.IDREF)
                || this.attribute.getType().equals(AttributeType.IDREFS)
                || this.attribute.getType().equals(AttributeType.ENTITY)
                || this.attribute.getType().equals(AttributeType.ENTITIES)
                || this.attribute.getType().equals(AttributeType.NMTOKEN)
                || this.attribute.getType().equals(AttributeType.NMTOKENS)) {
            // Tokenized types
            typeString += this.attribute.getType().name() + " " + getAttributePresenceAndValueString(qualifiedName);

        } else if (this.attribute.getType().equals(AttributeType.NOTATION)
                || this.attribute.getType().equals(AttributeType.ENUMERATION)) {
            // Enumerated types
            if (this.attribute.getType().equals(AttributeType.NOTATION)) {
                typeString += "NOTATION" + " ";
            }
            if (this.attribute.getEnumerationOrNotationTokens().isEmpty()) {
                throw new DTDAttributeTypeEnumOrNotationWithEmptyTokensException(this.attribute.getName().getFullyQualifiedName(), this.attribute.getType().toString());
            } else {
                String enumerationString = "(";
                for (Iterator<String> it = attribute.getEnumerationOrNotationTokens().iterator(); it.hasNext();) {
                    String currentString = it.next();
                    enumerationString += currentString + "|";
                }
                enumerationString = enumerationString.substring(0, enumerationString.length() - 1);
                enumerationString += ")";

                if (attribute.getValue() != null && !attribute.getValue().equals("") && !enumerationString.contains(attribute.getValue())) {
                    throw new AttributeEnumerationTypeIllegalDefaultValueException(attribute.getName().getFullyQualifiedName(), enumerationString, attribute.getValue());
                }

                typeString += enumerationString + " " + getAttributePresenceAndValueString(qualifiedName);
            }
        }
        return typeString;
    }

    private String getAttributePresenceAndValueString(QualifiedName qualifiedName) throws DTDException {
        String attributeValueString = "";
        if (this.attribute.getAttributeDefaultPresence() != null) {
            // Case: AttributeDefaultPresence = FIXED
            if (this.attribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.FIXED)) {
                if (attribute.getValue() == null || attribute.getValue().equals("")) {
                    throw new DTDAttributeValueEmptyException(this.attribute.getName().getFullyQualifiedName());
                }
                attributeValueString += "#FIXED" + " \"" + attribute.getValue() + "\"";
            } else {
                // Case: AttributeDefaultPresence = IMPLIED
                if (this.attribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.IMPLIED)) {
                    attributeValueString += "#IMPLIED";
                } else {
                    // Case: AttributeDefaultPresence = REQUIRED
                    if (this.attribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.REQUIRED)) {
                        attributeValueString += "#REQUIRED";
                    } else {
                        throw new DTDAttributeUnkownAttributeDefaultPresenceException(this.attribute.getName().getFullyQualifiedName());
                    }
                }
            }
        } else {
            // Case: No AttributeDefaultPresence
            if (attribute.getValue() == null) {
                throw new DTDAttributeValueEmptyException(qualifiedName + ": " + this.attribute.getName());
            } else {
                attributeValueString += "\"" + attribute.getValue() + "\"";
            }
        }
        return attributeValueString;
    }
}
