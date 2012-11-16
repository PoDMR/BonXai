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
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;

import org.w3c.dom.Node;

/**
 * This processor handles div elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class DivProcessor extends RNGProcessorBase {

    private IncludeContent includeContent;

    public DivProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);

    }

    DivProcessor(RelaxNGSchema rngSchema, Grammar grammar, IncludeContent includeContent) {
        super(rngSchema, grammar);
        this.includeContent = includeContent;
    }

    @Override
    protected Object processNode(Node node) throws Exception {

        visitChildren(node);

        return null;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         *      (
         *          start|
         *          define|
         *          div|
         *          include
         *      )*
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
                            System.out.println("div: start");
                        }
                        StartProcessor startProcessor = new StartProcessor(rngSchema, this.grammar);
                        if (this.includeContent != null) {
                            this.includeContent.addStartPattern(startProcessor.processNode(childNode));
                        } else {
                            this.grammar.addStartPattern(startProcessor.processNode(childNode));
                        }

                        break;
                    case define:
                        if (getDebug()) {
                            System.out.println("div: define");
                        }
                        if (this.includeContent != null) {
                            DefineProcessor defineProcessor = new DefineProcessor(rngSchema, this.grammar, this.includeContent);
                            defineProcessor.processNode(childNode);
                        } else {
                            DefineProcessor defineProcessor = new DefineProcessor(rngSchema, this.grammar);
                            defineProcessor.processNode(childNode);
                        }
                        break;
                    case div:
                        if (getDebug()) {
                            System.out.println("div: div");
                        }
                        if (this.includeContent != null) {
                            DivProcessor divProcessor = new DivProcessor(rngSchema, this.grammar, this.includeContent);
                            divProcessor.processNode(childNode);
                        } else {
                            DivProcessor divProcessor = new DivProcessor(rngSchema, this.grammar);
                            divProcessor.processNode(childNode);
                        }
                        break;
                    case include:
                        if (getDebug()) {
                            System.out.println("div: include");
                        }
                        IncludeProcessor includeProcessor = new IncludeProcessor(rngSchema, this.grammar);
                        this.grammar.addIncludeContent(includeProcessor.processNode(childNode));
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "grammar");
                }
            }
        }
    }
}
