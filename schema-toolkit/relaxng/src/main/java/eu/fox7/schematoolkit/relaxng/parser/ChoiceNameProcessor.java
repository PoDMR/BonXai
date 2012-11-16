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

package eu.fox7.schematoolkit.relaxng.parser;

import eu.fox7.schematoolkit.relaxng.om.NameClassChoice;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyNameClassException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the choice elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ChoiceNameProcessor extends RNGProcessorBase {

    private NameClassChoice nameClassChoice;

    public ChoiceNameProcessor(RelaxNGSchema rngSchema) {
        super(rngSchema, null);
    }

    @Override
    protected NameClassChoice processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.nameClassChoice = new NameClassChoice();

        visitChildren(node);

        if (this.nameClassChoice.getChoiceNames().size() < 1) {
            throw new EmptyNameClassException("choice has not enough nameClass parts");
        }

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("ns") != null) {
            String nsName = ((Attr) attributes.getNamedItem("ns")).getValue();
            nameClassChoice.setAttributeNamespace(nsName);
        }

        return this.nameClassChoice;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * as nameclass:
         * <choice> nameClass nameClass </choice>
         *
         *      (
         *         name|
         *         anyName|
         *         nsName|
         *         choice          
         *      ),
         *      (
         *         name|
         *         anyName|
         *         nsName|
         *         choice
         *      )               --> 2 nameclass
         * 
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case name:
                        if (getDebug()) {
                            System.out.println("choice (nameClass): name");
                        }
                        NameProcessor nameProcessor = new NameProcessor(rngSchema);
                        this.nameClassChoice.addChoiceName(nameProcessor.processNode(childNode));
                        break;
                    case anyName:
                        if (getDebug()) {
                            System.out.println("choice (nameClass): anyName");
                        }
                        AnyNameProcessor anyNameProcessor = new AnyNameProcessor(rngSchema);
                        this.nameClassChoice.addChoiceName(anyNameProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("choice (nameClass): choice");
                        }
                        ChoiceNameProcessor choiceNameProcessor = new ChoiceNameProcessor(rngSchema);
                        this.nameClassChoice.addChoiceName(choiceNameProcessor.processNode(childNode));
                        break;
                    case nsName:
                        if (getDebug()) {
                            System.out.println("choice (nameClass): nsName");
                        }
                        NsNameProcessor nsNameProcessor = new NsNameProcessor(rngSchema);
                        this.nameClassChoice.addChoiceName(nsNameProcessor.processNode(childNode));
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "choice (nameClass)");
                }
            }
        }
    }
}
