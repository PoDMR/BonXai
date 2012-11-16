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
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.om.Value;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the value elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ValueProcessor extends RNGProcessorBase {

    private Value value;

    public ValueProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Value processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * type NCName
         * datatypeLibrary anyURI
         * ns String
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

        String content = node.getTextContent();

        this.value = new Value(content);

        // type is optional in Full XML Syntax
        if (type != null) {
            this.value.setType(type);
        }
        this.setPatternAttributes(value, node);
        return this.value;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {
        /**
         * Children:
         * ---------
         * (#PCDATA) String content
         *
         */
    }
}
