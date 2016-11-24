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

package eu.fox7.schematoolkit.dtd.parser;

import eu.fox7.schematoolkit.common.Comment;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.dtd.common.AttributeTypeProcessor;
import eu.fox7.schematoolkit.dtd.common.ElementContentModelProcessor;
import eu.fox7.schematoolkit.dtd.common.exceptions.AttributeEnumerationTypeIllegalDefaultValueException;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.dtd.om.InternalEntity;
import eu.fox7.schematoolkit.dtd.om.PublicNotation;
import eu.fox7.schematoolkit.dtd.om.SystemNotation;
import eu.fox7.schematoolkit.dtd.parser.exceptions.DuplicateAttributeNameException;
import eu.fox7.schematoolkit.dtd.parser.exceptions.DuplicateElementNameException;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/*******************************************************************************
 * Event consumer class for all DTD parse events.
 * 
 * The DTD datastructure will be constructed by using the upcoming DTD events.
 * The private attribute "dtd" holds the resulting DTD object model when the
 * parsing process is complete.
 *
 *******************************************************************************
 * @author Lars Schmidt
 ******************************************************************************/
public class DTDEventHandler implements LexicalHandler, DeclHandler, DTDHandler {

    private DocumentTypeDefinition dtd;
    private boolean debug = false;
    private LinkedHashSet<String> elementDeclarations;
    private LinkedHashSet<String> attributeDeclarations;
    private List<Comment> comments = new LinkedList<Comment>();

    /**
     * Constructor of DTDEventHandler.
     */
    public DTDEventHandler() {
        this.dtd = new DocumentTypeDefinition();
        this.elementDeclarations = new LinkedHashSet<String>();
        this.attributeDeclarations = new LinkedHashSet<String>();
    }

    /**
     * Method startDTD
     * DOCTYPE event recognized:
     * <!DOCTYPE rootElementName PUBLIC "-//W3C//DTD XMLSCHEMA 200102//EN" "XMLSchema.dtd" [
     * @param rootElementName
     * @throws SAXException
     */
    @Override
    public void startDTD(String rootElementName, String publicId, String systemId) throws SAXException {
        // Set the root element of the DTD
//        this.dtd.setRootElement(new Element(new QualifiedName(Namespace.EMPTY_NAMESPACE, rootElementName)));
//        if (getDebug()) {
//            System.out.println("DTD for '" + rootElementName + "'");
//        }
        if (publicId != null) {
//            if (getDebug()) {
//                System.out.println("Public ID: '" + publicId + "'");
//            }
            this.dtd.setPublicID(publicId);
        }
        if (systemId != null) {
//            if (getDebug()) {
//                System.out.println("System ID: '" + systemId + "'\n");
//            }
            this.dtd.setSystemID(systemId);
        }
    }

    /**
     * Method elementDecl
     * ELEMENT event recognized:
     * <!ELEMENT elementName (elementContentModel) !>
     * @param elementName
     * @param elementContentModel
     * @throws SAXException
     */
    @Override
    public void elementDecl(String elementName, String elementContentModel) throws SAXException {
//        if (getDebug()) {
//            System.out.println("DTD elementDeclaration: " + elementName + " " + elementContentModel + "\n");
//        }

        if (this.elementDeclarations.contains(elementName)) {
            try {
                throw new DuplicateElementNameException(elementName);
            } catch (DuplicateElementNameException ex) {
                throw new SAXException(ex);
            }
        }
        this.elementDeclarations.add(elementName);

        // Initialize the ElementContentModelProcessor, which extracts particles from a ContentModel string
        ElementContentModelProcessor elementContentModelProcessor = new ElementContentModelProcessor(dtd);
        Particle particle = null;
        try {
            particle = elementContentModelProcessor.convertRegExpStringToParticle(elementContentModel);
        } catch (Exception ex) {
            throw new SAXException(ex);
        }
        // Create a new element object
        Element element = new Element(new QualifiedName(Namespace.EMPTY_NAMESPACE,elementName));
        // Update the element within the SymbolTable of all elements.
        this.dtd.addElement(element);

        // The ElementContentModelProcessor holds the information, ...
        // ... if the content is mixed.
        if (elementContentModelProcessor.isMixed()) {
            element.setMixed(true);
        }

        if (elementContentModelProcessor.isMixedStar()) {
            element.setMixedStar(true);
        }

        if (particle != null) {
            // In the case of an empty content we must not add a particle to the element
            element.setParticle(particle);
        }
    }

    /**
     * Method attributeDecl
     * ATTLIST event recognized:
     * <!ATTLIST elementName attributeName type mode value !>
     * @param elementName
     * @param attributeName
     * @throws SAXException
     */
    @Override
    public void attributeDecl(String elementName, String attributeName, String type, String mode, String value) throws SAXException {
//        if (getDebug()) {
//            System.out.println("DTD attributeDeclaration: " + elementName + " " + attributeName + " " + type + " " + mode + " " + value + "\n");
//        }

    	QualifiedName attributeQualifiedName = new QualifiedName(Namespace.EMPTY_NAMESPACE,attributeName);
    	
        if (this.attributeDeclarations.contains(elementName + "-" + attributeName)) {
            try {
                throw new DuplicateAttributeNameException(attributeName, elementName);
            } catch (DuplicateAttributeNameException ex) {
                throw new SAXException(ex);
            }
        }
        this.attributeDeclarations.add(elementName + "-" + attributeName);

        // Find the right element for which the current attribute has to be generated.
        // If this Element is not already registered, generate a new one with the given name.
        Element element = dtd.getElement(elementName);

        // Now we have the correct element.
        Attribute attribute = new Attribute(attributeQualifiedName, mode, value);

        AttributeTypeProcessor attributeTypeProcessor = new AttributeTypeProcessor(this.dtd, attribute);
        try {
            attributeTypeProcessor.setTypeToAttribute(type);
        } catch (Exception ex) {
            throw new SAXException(ex);
        }

        if (value != null && (attribute.getType().equals(AttributeType.NOTATION) || attribute.getType().equals(AttributeType.ENUMERATION))) {
            if (!attribute.getEnumerationOrNotationTokens().contains(value)) {
                try {
                    throw new AttributeEnumerationTypeIllegalDefaultValueException(attributeName, type, value);
                } catch (AttributeEnumerationTypeIllegalDefaultValueException ex) {
                    throw new SAXException(ex);
                }
            }
        }

        element.addAttribute(attribute);

    }

    /**
     * Method internalEntityDecl
     * ENTITY event recognized:
     * <!ENTITY entityName entityValue!>
     * @throws SAXException
     */
    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
//        if (getDebug()) {
//            System.out.println("DTD internalEntityDecl: \"" + name + ";\" --> \"" + value + "\"\n");
//        }

        this.dtd.addInternalEntity(new InternalEntity(name, value));
    }

    @Override
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        /**
         * Important:
         * This is not necessary and therefor not supported in the current
         * implementation. All external entities are fetched by the SAXParser itself
         * and their content will be placed within the generated dtd structure.
         */
//        if (getDebug()) {
//            System.out.println("DTD externalEntityDecl: \"" + name + ";\" --> \"" + publicId + "\" (\"" + systemId + "\")\n");
//        }

//        this.dtd.addExternalEntity(new ExternalEntity(name, publicId, systemId));
    }

    /**
     * Getter for the generated DTD definition
     * @return DocumentTypeDefinition
     */
    public DocumentTypeDefinition getDTD() {
        return this.dtd;
    }

    /**
     * Setter for the debug variable for more output during parseprogress
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Getter for the debug variable
     * @return
     */
    public boolean getDebug() {
        return debug;
    }

    /**
     * Handle a DTD notation declaration
     * @param name
     * @param publicID
     * @param systemID
     */
    @Override
    public void notationDecl(String name, String publicID, String systemID) {
        if (this.debug) {
            System.out.println("DTD notationDeclaration: " + name + " " + publicID + " " + systemID + "\n");
        }

        if (publicID != null) {
            PublicNotation notation = new PublicNotation(name, publicID);
            this.dtd.addNotation(notation);
        } else if (systemID != null) {
            SystemNotation notation = new SystemNotation(name, systemID);
            this.dtd.addNotation(notation);
        }
    }

    /**
     * Handle an DTD unparsed entity declaration
     * @param name
     * @param publicID
     * @param systemID
     * @param notationName
     */
    @Override
    public void unparsedEntityDecl(String name, String publicID, String systemID, String notationName) {
        if (this.debug) {
            System.out.println("DTD unparsedEntityDeclaration: " + name + " " + publicID + " " + systemID + " " + notationName + "\n");
        }
    }

    /**
     * Method comment
     * Comment event recognized:
     * <!-- commentContent -->
     * @throws SAXException
     */
    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        /** Comments in the form of:
         *
         * <!-- Lorem ipsum dolor sit amet, consetetur sadipscing elitr, ... -->
         *
         * are recognized here.
         */
//        System.out.println(ch);
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void endCDATA() throws SAXException {
    }
}
