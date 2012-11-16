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

import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.IncludeContent;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the include elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class IncludeProcessor extends RNGProcessorBase {

    private IncludeContent includeContent;

    public IncludeProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected IncludeContent processNode(Node node) throws Exception {
        /**
         * Attributes:
         * -----------
         * href anyURI
         */
        String hrefString = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("href") != null) {
                hrefString = ((Attr) attributes.getNamedItem("href")).getValue();
                if (!NameChecker.isAnyUri(hrefString)) {
                    throw new InvalidAnyUriException(hrefString, "include: href attribute");
                }
            }
        }
        if (hrefString != null) {
            this.includeContent = new IncludeContent(hrefString);
        } else {
            throw new InvalidAnyUriException(hrefString, "include: href attribute");
        }

        visitChildren(node);

        return this.includeContent;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         *
         * <include href="anyURI"> includeContent* </include>
         * includeContent  ::=  start | define | <div> includeContent* </div>
         * 
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case start:
                        if (getDebug()) {
                            System.out.println("include: start");
                        }
                        StartProcessor startProcessor = new StartProcessor(rngSchema, this.grammar);
                        this.includeContent.addStartPattern(startProcessor.processNode(childNode));
                        break;
                    case define:
                        if (getDebug()) {
                            System.out.println("include: define");
                        }
                        DefineProcessor defineProcessor = new DefineProcessor(rngSchema, this.grammar, this.includeContent);
                        defineProcessor.processNode(childNode);
                        break;
                    case div:
                        if (getDebug()) {
                            System.out.println("include: div");
                        }
                        DivProcessor divProcessor = new DivProcessor(rngSchema, this.grammar, this.includeContent);
                        divProcessor.processNode(childNode);
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "include");
                }
            }
        }
    }
}
