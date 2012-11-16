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

import eu.fox7.schematoolkit.relaxng.om.AnyName;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultipleExceptException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the anyName elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class AnyNameProcessor extends RNGProcessorBase {

    private AnyName anyName;
    private boolean exceptAlreadyFound = false;

    public AnyNameProcessor(RelaxNGSchema rngSchema) {
        super(rngSchema);
    }

    @Override
    protected AnyName processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.anyName = new AnyName();

        visitChildren(node);

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("ns") != null) {
            String nsName = ((Attr) attributes.getNamedItem("ns")).getValue();
            anyName.setAttributeNamespace(nsName);
        }

        return this.anyName;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * (except)?
         * 
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case except:
                        if (getDebug()) {
                            System.out.println("data: except");
                        }
                        if (exceptAlreadyFound) {
                            throw new MultipleExceptException("anyName");
                        }
                        ExceptNameProcessor exceptNameProcessor = new ExceptNameProcessor(rngSchema, this.grammar);

                        LinkedList<NameClass> nameClasses = exceptNameProcessor.processNode(childNode);
                        for (Iterator<NameClass> it = nameClasses.iterator(); it.hasNext();) {
                            NameClass nameClass = it.next();
                            this.anyName.addExceptName(nameClass);
                        }
                        exceptAlreadyFound = true;
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "grammar");
                }
            }
        }
    }
}
