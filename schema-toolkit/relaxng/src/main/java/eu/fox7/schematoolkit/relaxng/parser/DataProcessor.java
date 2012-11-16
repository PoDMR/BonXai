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

import eu.fox7.schematoolkit.relaxng.om.Data;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyTypeException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the data elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class DataProcessor extends RNGProcessorBase {

    private Data data;

    DataProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Data processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * datatypeLibrary anyURI
         * type NCName
         */
        String type = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("type") != null) {
                type = ((Attr) attributes.getNamedItem("type")).getValue();
                if (!NameChecker.isNCName(type)) {
                    throw new InvalidNCNameException(type, "data: type attribute");
                }
            }
        }

        if (type != null) {
            this.data = new Data(type);
        } else {
            throw new EmptyTypeException("data");
        }

        this.setPatternAttributes(data, node);

        visitChildren(node);

        return this.data;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         *  (
         *      (
         *          param
         *      )*
         *      ,
         *      (
         *          except
         *      )?
         * )
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case param:
                        if (getDebug()) {
                            System.out.println("data: param");
                        }
                        ParamProcessor paramProcessor = new ParamProcessor(rngSchema, this.grammar);
                        this.data.addParam(paramProcessor.processNode(childNode));
                        break;
                    case except:
                        if (getDebug()) {
                            System.out.println("data: except");
                        }

                        ExceptProcessor exceptProcessor = new ExceptProcessor(rngSchema, this.grammar);

                        LinkedList<Pattern> patterns = exceptProcessor.processNode(childNode);
                        for (Iterator<Pattern> it = patterns.iterator(); it.hasNext();) {
                            Pattern currentPattern = it.next();
                            this.data.addExceptPattern(currentPattern);
                        }

                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "grammar");
                }
            }
        }
    }
}
