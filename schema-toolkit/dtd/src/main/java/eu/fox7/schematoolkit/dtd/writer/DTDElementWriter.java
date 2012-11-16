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
import eu.fox7.schematoolkit.dtd.common.DTDNameChecker;
import eu.fox7.schematoolkit.dtd.common.ElementContentModelProcessor;
import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDElementContentModelEmptyException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDElementNameEmptyException;

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
    public String getElementDeclarationString() throws DTDException {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String elementString = "";

        if (this.element != null) {
            elementString += "<!ELEMENT ";

            if (this.element.getName() == null || this.element.getName().equals("")) {
                throw new DTDElementNameEmptyException("");
            }
            if (!nameChecker.checkForXMLName(this.element.getName())) {
                throw new IllegalNAMEStringException("ELEMENT: ", this.element.getName().getName());
            }
            elementString += this.element.getName() + " ";

            ElementContentModelProcessor elementContentModelProcessor = new ElementContentModelProcessor();
            String elementContentModelRegExp = elementContentModelProcessor.convertParticleToRegExpString(this.element);

            if (elementContentModelRegExp != null && !elementContentModelRegExp.equals("")) {
                elementString += elementContentModelRegExp;
            } else {
                throw new DTDElementContentModelEmptyException(this.element.getName().getName());
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
    public String getElementAttachedAttributesString() throws DTDException {
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
                    return attribute1.getName().getFullyQualifiedName().compareTo(attribute2.getName().getFullyQualifiedName());
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
