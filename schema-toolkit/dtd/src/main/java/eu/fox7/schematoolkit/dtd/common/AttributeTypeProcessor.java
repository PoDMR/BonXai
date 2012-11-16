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

package eu.fox7.schematoolkit.dtd.common;

import eu.fox7.schematoolkit.dtd.common.exceptions.AttributeTypeIllegalValueException;
import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNMTOKENStringException;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;

import java.util.LinkedHashSet;

/**
 * Processor for the type of the given attribute-object.
 * @author Lars Schmidt
 */
public class AttributeTypeProcessor {

    private Attribute attribute;
    private DocumentTypeDefinition dtd;

    public AttributeTypeProcessor(DocumentTypeDefinition dtd, Attribute attribute) {
        this.attribute = attribute;
        this.dtd = dtd;
    }

    /**
     * Setter for the type of the current attribute-object.
     *
     * This is NOT a normal "setter" for a String attribute, because this method
     * parses the given String and sets the
     *
     * type: (instanceof: AttributeType)
     *          of the attribute and depending on the given value possible
     *
     * enumerationOrNotationTokens: (instanceof: LinkedHashSet<String>),
     *          if the type is a NOTATION or an ENUMERATION.
     *
     * @param typeString - String
     * @throws Exception 
     */
    public void setTypeToAttribute(String typeString) throws DTDException {
        DTDNameChecker nameChecker = new DTDNameChecker();

        // Strip whitespace from typeString
        typeString = typeString.replace(" ", "");

        // Handling of different cases
        // Case: NOTATION
        if (typeString.startsWith("NOTATION")) {
            typeString = typeString.substring("NOTATION".length());

            if (typeString.matches("[(].+(\\|.+)*[)]")) {

                this.attribute.setType(AttributeType.NOTATION);
                LinkedHashSet<String> enumerationOrNotationTokens = new LinkedHashSet<String>();

                typeString = typeString.replaceAll("[()]", "");
                String[] notationValues = typeString.split("\\|");

                for (String currentString : notationValues) {
//                    if (!dtd.getNotationSymbolTable().hasReference(currentString)) {
//                        throw new NotationNotDeclaredException(attribute.getName(), currentString);
//                    }

                    if (!nameChecker.checkForXMLNmtoken(currentString)) {
                        throw new IllegalNMTOKENStringException(attribute.getName().getFullyQualifiedName(), currentString);
                    }

                    enumerationOrNotationTokens.add(currentString.trim());
                }
                this.attribute.setEnumerationOrNotationTokens(enumerationOrNotationTokens);

            }
        } else if (typeString.startsWith("(") && typeString.endsWith(")")) {
            // Case: ENUMERATION
            // http://www.w3.org/TR/xml/#NT-EnumeratedType
            this.attribute.setType(AttributeType.ENUMERATION);

            if (typeString.matches("[(].+(\\|.+)*[)]")) {

                typeString = typeString.replaceAll("[()]", "");
                String[] notationValues = typeString.split("\\|");

                LinkedHashSet<String> enumerationOrNotationTokens = new LinkedHashSet<String>();

                for (String currentString : notationValues) {
                    if (!nameChecker.checkForXMLNmtoken(currentString)) {
                        throw new IllegalNMTOKENStringException(attribute.getName().getFullyQualifiedName(), currentString);
                    }
                    enumerationOrNotationTokens.add(currentString.trim());
                }
                this.attribute.setEnumerationOrNotationTokens(enumerationOrNotationTokens);
            }
        } else {
            // Case: *all other* - cases defined in ENUM: AttributeType
            try {
                AttributeType attributeType = AttributeType.valueOf(typeString);

                if (attributeType.equals(AttributeType.ENUMERATION)) {
                    throw new AttributeTypeIllegalValueException(attribute.getName().getFullyQualifiedName(), typeString);
                }
                this.attribute.setType(attributeType);
            } catch (IllegalArgumentException ex) {
                throw new AttributeTypeIllegalValueException(attribute.getName().getFullyQualifiedName(), typeString);
            }
        }
    }
}
