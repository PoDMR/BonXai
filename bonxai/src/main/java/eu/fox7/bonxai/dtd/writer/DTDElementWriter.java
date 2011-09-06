package eu.fox7.bonxai.dtd.writer;

import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.common.DTDNameChecker;
import eu.fox7.bonxai.dtd.common.ElementContentModelProcessor;
import eu.fox7.bonxai.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDElementContentModelEmptyException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDElementNameEmptyException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class DTDElementWriter
 * This class generates a DTD element declaration String for one given element.
 *
 * @author Lars Schmidt
 */
public class DTDElementWriter {

    private final Element element;

    /**
     * Constructor of class DTDElementWriter
     * @param element 
     */
    public DTDElementWriter(Element element) {
        this.element = element;
    }

    /**
     * Get a String containing the declaration of the current element
     * @return String
     * @throws Exception
     */
    public String getElementDeclarationString() throws Exception {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String elementString = "";

        if (this.element != null) {
            elementString += "<!ELEMENT ";

            if (this.element.getName() == null || this.element.getName().equals("")) {
                throw new DTDElementNameEmptyException("");
            }
            if (!nameChecker.checkForXMLName(this.element.getName())) {
                throw new IllegalNAMEStringException("ELEMENT: " + this.element.getName(), this.element.getName());
            }
            elementString += this.element.getName() + " ";

            ElementContentModelProcessor elementContentModelProcessor = new ElementContentModelProcessor();
            String elementContentModelRegExp = elementContentModelProcessor.convertParticleToRegExpString(this.element);

            if (elementContentModelRegExp != null && !elementContentModelRegExp.equals("")) {
                elementString += elementContentModelRegExp;
            } else {
                throw new DTDElementContentModelEmptyException(this.element.getName());
            }
            elementString += ">\n";
        }
        return elementString;
    }

    /**
     * Get a String containing the declaration of all attributes of the current element
     * @return String
     * @throws Exception
     */
    public String getElementAttachedAttributesString() throws Exception {
        String attributeString = "";
        if (!this.element.getAttributes().isEmpty()) {

            // Sort the list of attributes
            Vector<Attribute> attributes = new Vector<Attribute>(this.element.getAttributes());
            Collections.sort(attributes, new Comparator<Attribute>() {

                @Override
                public int compare(Attribute attribute1, Attribute attribute2) {
                    if (attribute1.getName() == null || attribute1.getName().equals("") || attribute2.getName() == null || attribute2.getName().equals("")) {
                        return 1;
                    }
                    return attribute1.getName().compareTo(attribute2.getName());
                }
            });
            for (Iterator<Attribute> it = attributes.iterator(); it.hasNext();) {
                Attribute currentAttribute = it.next();
                DTDAttributeWriter attributeWriter = new DTDAttributeWriter(currentAttribute);
                attributeString += attributeWriter.getAttributeDeclarationString(this.element.getName());
            }
        }
        return attributeString;
    }
}
