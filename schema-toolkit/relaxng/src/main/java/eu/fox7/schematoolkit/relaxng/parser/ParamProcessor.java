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
import eu.fox7.schematoolkit.relaxng.om.Param;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MissingNameException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the param elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ParamProcessor extends RNGProcessorBase {

    public ParamProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Param processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * name NCName
         */
        String content = node.getTextContent();

        if (getDebug()) {
            System.out.println("Param: \"" + content + "\"");
        }
        String name = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("name") != null) {
            name = ((Attr) attributes.getNamedItem("name")).getValue();
            if (name == null || !NameChecker.isNCName(name)) {
                throw new InvalidNCNameException(name, "name of param");
            }
        }
        if (name == null) {
            throw new MissingNameException("Param");
        }
        Param param = new Param(name);
        param.setContent(content);

        return param;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * (#PCDATA) String content
         *  This is already handled in the method "processNode" above.
         */
    }
}
