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

import eu.fox7.schematoolkit.relaxng.om.CombineMethod;
import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.IncludeContent;
import eu.fox7.schematoolkit.relaxng.om.NotAllowed;
import eu.fox7.schematoolkit.relaxng.om.ParentRef;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Ref;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.om.Text;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.CombineMethodNotMatchingException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidCombineMethodException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MissingHrefException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.ParentGrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.ReturnObjectIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.UnsupportedContentException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the currentDefine elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class DefineProcessor extends RNGPatternProcessorBase {

    private Define define;
    private IncludeContent includeContent;

    DefineProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    DefineProcessor(RelaxNGSchema rngSchema, Grammar grammar, IncludeContent includeContent) {
        super(rngSchema, grammar);
        this.includeContent = includeContent;
    }

    @Override
    protected Object processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * name NCName
         * combine: method (choice|interleave)
         */
        String defineName = null;
        CombineMethod combineMethod = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("name") != null) {
                defineName = ((Attr) attributes.getNamedItem("name")).getValue();
                if (!NameChecker.isNCName(defineName)) {
                    throw new InvalidNCNameException(defineName, "define");
                }
            }
        }

        if (defineName != null) {
            this.define = new Define(defineName);
            visitChildren(node);
            if (this.includeContent != null) {
                this.includeContent.addDefinePattern(define);
            } else {
                this.grammar.addDefinePattern(define);
            }
        } else {
            throw new InvalidNCNameException(defineName, "define");
        }

        if (attributes != null) {
            if (attributes.getNamedItem("combine") != null) {
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("choice")) {
                    combineMethod = CombineMethod.choice;
                }
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("interleave")) {
                    combineMethod = CombineMethod.interleave;
                }
                if (combineMethod == null) {
                    throw new InvalidCombineMethodException("define");
                } else {
                	List<Define> defineList = this.grammar.getDefinedPattern(defineName);
                	for (Iterator<Define> it = defineList.iterator(); it.hasNext();) {
                		Define currentDefine = it.next();
                		if (currentDefine.getCombineMethod() != null && !currentDefine.getCombineMethod().equals(combineMethod)) {
                			throw new CombineMethodNotMatchingException(defineName);
                		}
                	}

                    this.define.setCombineMethod(combineMethod);
                }
            }
        }

        if (this.define != null) {
            return this.define;
        } else {
            throw new ReturnObjectIsNullException("define");
        }

    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.define.addPattern(pattern);
	}
}
