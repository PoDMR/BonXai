package eu.fox7.bonxai.dtd.writer;

import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.AttributeType;
import eu.fox7.bonxai.dtd.common.DTDNameChecker;
import eu.fox7.bonxai.dtd.common.exceptions.AttributeEnumerationTypeIllegalDefaultValueException;
import eu.fox7.bonxai.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeNameEmptyException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeNullException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeTypeEnumOrNotationWithEmptyTokensException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeTypeNullException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeUnkownAttributeDefaultPresenceException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeValueEmptyException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDElementNameEmptyException;

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
     * @param elementName
     * @return String
     * @throws Exception
     */
    public String getAttributeDeclarationString(String elementName) throws Exception {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String attributeString = "";

        if (this.attribute != null) {
            attributeString += "<!ATTLIST ";

            if (this.attribute.getName() == null || this.attribute.getName().equals("")) {
                throw new DTDAttributeNameEmptyException(elementName);
            }
            if (!nameChecker.checkForXMLName(this.attribute.getName())) {
                throw new IllegalNAMEStringException("ATTLIST: " + elementName + " " + this.attribute.getName(), this.attribute.getName());
            }
            if (elementName == null || elementName.equals("")) {
                throw new DTDElementNameEmptyException("for an attribute declaration: " + this.attribute.getName());
            }
            attributeString += elementName + " ";
            attributeString += this.attribute.getName() + " ";
            attributeString += getAttributeTypeString(elementName);
            attributeString += ">\n";
        } else {
            throw new DTDAttributeNullException(elementName);
        }
        return attributeString;
    }

    private String getAttributeTypeString(String elementName) throws Exception {
        String typeString = "";
        if (this.attribute.getType() == null) {
            throw new DTDAttributeTypeNullException(this.attribute.getName());
        }
        if (this.attribute.getType().equals(AttributeType.CDATA)) {
            // StringType
            typeString += "CDATA" + " " + getAttributePresenceAndValueString(elementName);

        } else if (this.attribute.getType().equals(AttributeType.ID)
                || this.attribute.getType().equals(AttributeType.IDREF)
                || this.attribute.getType().equals(AttributeType.IDREFS)
                || this.attribute.getType().equals(AttributeType.ENTITY)
                || this.attribute.getType().equals(AttributeType.ENTITIES)
                || this.attribute.getType().equals(AttributeType.NMTOKEN)
                || this.attribute.getType().equals(AttributeType.NMTOKENS)) {
            // Tokenized types
            typeString += this.attribute.getType().name() + " " + getAttributePresenceAndValueString(elementName);

        } else if (this.attribute.getType().equals(AttributeType.NOTATION)
                || this.attribute.getType().equals(AttributeType.ENUMERATION)) {
            // Enumerated types
            if (this.attribute.getType().equals(AttributeType.NOTATION)) {
                typeString += "NOTATION" + " ";
            }
            if (this.attribute.getEnumerationOrNotationTokens().isEmpty()) {
                throw new DTDAttributeTypeEnumOrNotationWithEmptyTokensException(this.attribute.getName(), this.attribute.getType().toString());
            } else {
                String enumerationString = "(";
                for (Iterator<String> it = attribute.getEnumerationOrNotationTokens().iterator(); it.hasNext();) {
                    String currentString = it.next();
                    enumerationString += currentString + "|";
                }
                enumerationString = enumerationString.substring(0, enumerationString.length() - 1);
                enumerationString += ")";

                if (attribute.getValue() != null && !attribute.getValue().equals("") && !enumerationString.contains(attribute.getValue())) {
                    throw new AttributeEnumerationTypeIllegalDefaultValueException(attribute.getName(), enumerationString, attribute.getValue());
                }

                typeString += enumerationString + " " + getAttributePresenceAndValueString(elementName);
            }
        }
        return typeString;
    }

    private String getAttributePresenceAndValueString(String elementName) throws Exception {
        String attributeValueString = "";
        if (this.attribute.getAttributeDefaultPresence() != null) {
            // Case: AttributeDefaultPresence = FIXED
            if (this.attribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.FIXED)) {
                if (attribute.getValue() == null || attribute.getValue().equals("")) {
                    throw new DTDAttributeValueEmptyException(this.attribute.getName());
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
                        throw new DTDAttributeUnkownAttributeDefaultPresenceException(this.attribute.getName());
                    }
                }
            }
        } else {
            // Case: No AttributeDefaultPresence
            if (attribute.getValue() == null) {
                throw new DTDAttributeValueEmptyException(elementName + ": " + this.attribute.getName());
            } else {
                attributeValueString += "\"" + attribute.getValue() + "\"";
            }
        }
        return attributeValueString;
    }
}
