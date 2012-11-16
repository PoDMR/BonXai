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

package eu.fox7.schematoolkit.relaxng.writer;

import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.relaxng.*;
import eu.fox7.schematoolkit.relaxng.om.AnyName;
import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.NameClassChoice;
import eu.fox7.schematoolkit.relaxng.om.NsName;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.InvalidQNameException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.UnknownNameClassException;

import java.util.Iterator;

/**
 * Writer for the nameClass structure of a Relax NG schema.
 * Writing means returning the correct DOM elements for the given data structure
 * object.
 * In this case: NameClass
 *
 * @author Lars Schmidt
 */
public class NameClassWriter extends RNGWriterBase {
    /**
     * Constructor of class NameClassWriter
     * @param rngDocument
     * @param namespaces
     */
    public NameClassWriter(org.w3c.dom.Document rngDocument, NamespaceList namespaces) {
        super(rngDocument, namespaces);
    }

    /**
     * Public method createNodeForNameClass.
     * This class creates a specific DOM element node for the given NameClass
     * datastructure object. This is handled recursivly in case of a choice.
     * @param nameClass     NameClass to create a DOM Node for
     * @return org.w3c.dom.Element      resulting DOM element
     * @throws Exception    UnknownNameClassException or InvalidQNameException
     */
    public org.w3c.dom.Element createNodeForNameClass(NameClass nameClass) throws Exception {
        return this.switchNodeCreationForNameClass(nameClass);
    }

    /**
     * Method to switch over all possible NameClasses
     * @param nameClass     NameClass to create a DOM node for
     * @return org.w3c.dom.Element      resulting DOM element
     * @throws Exception    UnknownNameClassException or InvalidQNameException
     */
    private org.w3c.dom.Element switchNodeCreationForNameClass(NameClass nameClass) throws Exception {
        org.w3c.dom.Element returnElement;

        if (nameClass instanceof Name) {
            Name name = (Name) nameClass;
            returnElement = createNodeForName(name);
        } else if (nameClass instanceof NsName) {
            NsName nsName = (NsName) nameClass;
            returnElement = createNodeForNsName(nsName);
        } else if (nameClass instanceof AnyName) {
            AnyName anyName = (AnyName) nameClass;
            returnElement = createNodeForAnyName(anyName);
        } else if (nameClass instanceof NameClassChoice) {
            NameClassChoice nameClassChoice = (NameClassChoice) nameClass;
            returnElement = createNodeForNameClassChoice(nameClassChoice);
        } else {
            throw new UnknownNameClassException(nameClass.getClass().getName());
        }
        return returnElement;
    }

    /**
     * Create a DOM node for a "Name" object
     * @param name      the Name object to create a node for
     * @return org.w3c.dom.Element      resulting DOM element
     * @throws InvalidQNameException    will be thrown if there is an invalid QName as content of the Name-object
     */
    private org.w3c.dom.Element createNodeForName(Name name) throws InvalidQNameException {
        org.w3c.dom.Element nameNode = createElementNode("name");
        if (name.getAttributeNamespace() != null) {
            nameNode.setAttribute("ns", name.getAttributeNamespace().getUri());
        }
        if (!isQName(name.getContent())) {
            throw new InvalidQNameException(name.getContent(), "content of name");
        }
        nameNode.setTextContent(name.getContent());
        return nameNode;
    }

    /**
     * Create a DOM node for a "NsName" object
     * @param nsName      the NsName object to create a node for
     * @return org.w3c.dom.Element      resulting DOM element
     * @throws Exception    UnknownNameClassException or InvalidQNameException in case of the except childs
     */
    private org.w3c.dom.Element createNodeForNsName(NsName nsName) throws Exception {
        org.w3c.dom.Element nsNameNode = createElementNode("nsName");
        if (nsName.getAttributeNamespace() != null) {
            nsNameNode.setAttribute("ns", nsName.getAttributeNamespace().getUri());
        }
        if (nsName.getExceptNames() != null && !nsName.getExceptNames().isEmpty()) {
            org.w3c.dom.Element exceptNode = createElementNode("except");
            for (Iterator<NameClass> it = nsName.getExceptNames().iterator(); it.hasNext();) {
                NameClass currentNameClass = it.next();
                if (currentNameClass != null) {
                    exceptNode.appendChild(createNodeForNameClass(currentNameClass));
                }
            }
            nsNameNode.appendChild(exceptNode);
        }

        return nsNameNode;
    }

    /**
     * Create a DOM node for a "AnyName" object
     * @param anyName      the AnyName object to create a node for
     * @return org.w3c.dom.Element      resulting DOM element
     * @throws Exception    UnknownNameClassException or InvalidQNameException in case of the except childs
     */
    private org.w3c.dom.Element createNodeForAnyName(AnyName anyName) throws Exception {
        org.w3c.dom.Element anyNameNode = createElementNode("anyName");
        if (anyName.getAttributeNamespace() != null) {
            anyNameNode.setAttribute("ns", anyName.getAttributeNamespace().getUri());
        }
        if (anyName.getExceptNames() != null && !anyName.getExceptNames().isEmpty()) {
            org.w3c.dom.Element exceptNode = createElementNode("except");
            for (Iterator<NameClass> it = anyName.getExceptNames().iterator(); it.hasNext();) {
                NameClass currentNameClass = it.next();

                //Important: It is not allowed to write an "anyName" as ExceptName under an "anyName". (deep check?)
                if (currentNameClass != null) {
                    exceptNode.appendChild(createNodeForNameClass(currentNameClass));
                }
            }
            anyNameNode.appendChild(exceptNode);
        }
        return anyNameNode;
    }

    /**
     * Create a DOM node for a "NameClassChoice" object --> "choice"-Tag
     * @param nameClassChoice      the NameClassChoice object to create a node for
     * @return org.w3c.dom.Element      resulting DOM element
     * @throws Exception    UnknownNameClassException or InvalidQNameException in case of illegal children
     */
    private org.w3c.dom.Element createNodeForNameClassChoice(NameClassChoice nameClassChoice) throws Exception {
        org.w3c.dom.Element choiceNode = createElementNode("choice");

        if (nameClassChoice.getChoiceNames() != null && !nameClassChoice.getChoiceNames().isEmpty()) {
            for (Iterator<NameClass> it = nameClassChoice.getChoiceNames().iterator(); it.hasNext();) {
                NameClass currentNameClass = it.next();
                if (currentNameClass != null) {
                    choiceNode.appendChild(createNodeForNameClass(currentNameClass));
                }
            }
        }
        return choiceNode;
    }
}
