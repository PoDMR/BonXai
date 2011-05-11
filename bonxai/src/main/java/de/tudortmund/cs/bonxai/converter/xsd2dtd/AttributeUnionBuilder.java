package de.tudortmund.cs.bonxai.converter.xsd2dtd;

import de.tudortmund.cs.bonxai.dtd.Attribute;
import de.tudortmund.cs.bonxai.dtd.AttributeType;
import de.tudortmund.cs.bonxai.tools.StatusLogger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Stack;

/**
 * Class AttributeUnionBuilder
 * @author Lars Schmidt
 */
public class AttributeUnionBuilder {

    private final ElementWrapper elementWrapper;

    /**
     * Constructor of class AttributeUnionBuilder
     * @param elementWrapper
     */
    public AttributeUnionBuilder(ElementWrapper elementWrapper) {
        this.elementWrapper = elementWrapper;
    }

    /**
     * Unify all attributes contained in the given elementWrapper to one per name
     * @throws Exception
     */
    public void unifyAttributes() throws Exception {
        // Prepare the result
        LinkedHashSet<Attribute> resultingHashSet = new LinkedHashSet<Attribute>();
        // Loop over all keys of the DTD attribute map
        for (Iterator<String> it = this.elementWrapper.getDTDAttributeMap().keySet().iterator(); it.hasNext();) {
            String mapKey = it.next();
            LinkedHashSet<Attribute> attributeList = this.elementWrapper.getDTDAttributeMap().get(mapKey);

            // Convert the attributeList to a stack
            Stack<Attribute> attributeStack = new Stack<Attribute>();
            attributeStack.addAll(attributeList);
            // Pop the first attribute from the stack
            Attribute currentResultAttribute = attributeStack.pop();

            if (!attributeStack.isEmpty()) {
                StatusLogger.logWarning("XSD2DTD", "Attribute \"" + mapKey + "\" has " + attributeList.size() + " occurrences under element \"" + elementWrapper.getDTDElementName() + "\". They will be unified to an upper approximation!");
            }

            // If there are more attributes with the current name,
            // unify all of them to one resulting attribute.
            while (!attributeStack.isEmpty()) {
                currentResultAttribute = this.unifyTwoAttributes(currentResultAttribute, attributeStack.pop());
            }

            // There is only one attribute left
            resultingHashSet.add(currentResultAttribute);
        }

        LinkedHashMap<String, LinkedHashSet<Attribute>> attributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        // Rebuild the the attributeMap and put the new one to the elementWrapper
        for (Iterator<Attribute> it = resultingHashSet.iterator(); it.hasNext();) {
            Attribute attribute = it.next();
            LinkedHashSet<Attribute> resultHashSet = new LinkedHashSet<Attribute>();
            resultHashSet.add(attribute);
            attributeMap.put(attribute.getName(), resultHashSet);
        }
        this.elementWrapper.setDtdAttributeMap(attributeMap);
    }

    /**
     * Unify two DTD attributes to one resulting attribute via approximation.
     * 
     * @param attribute1    The first attribute for the union.
     * @param attribute2    The second attribute for the union.
     * @return Attribute    The resulting attribute.
     * @throws Exception
     */
    private Attribute unifyTwoAttributes(Attribute attribute1, Attribute attribute2) throws Exception {

        Attribute.AttributeDefaultPresence resultAttributeDefaultPresence = null;

        // AttributeDefaultPresence
        if (attribute1.getAttributeDefaultPresence() == null || attribute2.getAttributeDefaultPresence() == null) {
            resultAttributeDefaultPresence = null;
        } else if (attribute1.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.IMPLIED) || attribute2.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.IMPLIED)) {
            resultAttributeDefaultPresence = Attribute.AttributeDefaultPresence.IMPLIED;
        } else if (attribute1.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.REQUIRED) || attribute2.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.REQUIRED)) {
            resultAttributeDefaultPresence = Attribute.AttributeDefaultPresence.REQUIRED;
        } else {
            resultAttributeDefaultPresence = Attribute.AttributeDefaultPresence.FIXED;
        }

        // Value
        String resultValue = null;
        if (attribute1.getValue() != null && attribute2.getValue() != null && attribute1.getValue().equals(attribute2.getValue())) {
            resultValue = attribute1.getValue();
        }

        if (resultAttributeDefaultPresence == null && resultValue == null || resultAttributeDefaultPresence != null && resultAttributeDefaultPresence.equals(Attribute.AttributeDefaultPresence.FIXED) && resultValue == null) {
            resultAttributeDefaultPresence = Attribute.AttributeDefaultPresence.IMPLIED;
        }


        // Type
        AttributeType resultAttributeType = null;
        if (attribute1.getType() != null && attribute2.getType() != null) {
            if (attribute1.getType().equals(AttributeType.CDATA) || attribute2.getType().equals(AttributeType.CDATA)) {
                resultAttributeType = AttributeType.CDATA;
            } else if (attribute1.getType().equals(AttributeType.NMTOKENS) || attribute2.getType().equals(AttributeType.NMTOKENS)) {
                resultAttributeType = AttributeType.NMTOKENS;
            } else if (attribute1.getType().equals(AttributeType.NMTOKEN) || attribute2.getType().equals(AttributeType.NMTOKEN)) {
                resultAttributeType = AttributeType.NMTOKEN;
            } else if (attribute1.getType().equals(attribute2.getType())) {
                resultAttributeType = attribute1.getType();
            } else {
                resultAttributeType = AttributeType.CDATA;
            }
        } else {
            resultAttributeType = AttributeType.CDATA;
        }

        // EnumerationOrNotationTokens
        LinkedHashSet<String> resultEnumerationOrNotationTokens = new LinkedHashSet<String>();
        if (resultAttributeType.equals(AttributeType.ENUMERATION) || resultAttributeType.equals(AttributeType.NOTATION)) {
            resultEnumerationOrNotationTokens = attribute1.getEnumerationOrNotationTokens();
            if (resultEnumerationOrNotationTokens == null) {
                resultEnumerationOrNotationTokens = new LinkedHashSet<String>();
            }
            if (attribute2.getEnumerationOrNotationTokens() != null) {
                for (Iterator<String> it = attribute2.getEnumerationOrNotationTokens().iterator(); it.hasNext();) {
                    String currentToken = it.next();
                    if (!resultEnumerationOrNotationTokens.contains(currentToken)) {
                        resultEnumerationOrNotationTokens.add(currentToken);
                    }
                }
            }
        }

        // Prepare and initialize the resulting attribute
        Attribute resultAttribute = new Attribute(attribute1.getName(), ((resultAttributeDefaultPresence != null) ? "#" + resultAttributeDefaultPresence.name() : null), resultValue);
        resultAttribute.setType(resultAttributeType);
        resultAttribute.setEnumerationOrNotationTokens(resultEnumerationOrNotationTokens);


        // Debug
        if (XSD2DTDConverter.getDebug()) {
            System.out.println("Attribute:");
            System.out.println("(A) " + attribute1.getName() + ": " + attribute1.getType() + " " + attribute1.getValue() + " " + attribute1.getAttributeDefaultPresence() + " " + attribute1.getEnumerationOrNotationTokens());
            System.out.println("(B) " + attribute2.getName() + ": " + attribute2.getType() + " " + attribute2.getValue() + " " + attribute2.getAttributeDefaultPresence() + " " + attribute2.getEnumerationOrNotationTokens() + "\n");
            System.out.println("(A U B) result: " + resultAttribute.getType() + " " + resultAttribute.getValue() + " " + resultAttribute.getAttributeDefaultPresence() + " " + resultAttribute.getEnumerationOrNotationTokens() + "\n\n");
        }

        // return the result
        return resultAttribute;
    }
}
