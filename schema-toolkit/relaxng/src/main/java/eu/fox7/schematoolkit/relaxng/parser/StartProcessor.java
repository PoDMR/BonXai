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
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.CombineMethodNotMatchingException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.GrammarIsNullException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.InvalidCombineMethodException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.MultiplePatternException;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.PatternNotInitializedException;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the start elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class StartProcessor extends RNGPatternProcessorBase {

    Pattern pattern;

    StartProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Pattern processNode(Node node) throws Exception {
        /**
         * Attributes:
         * -----------
         */
        if (this.grammar == null) {
            throw new GrammarIsNullException("start");
        }

        CombineMethod combineMethod = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("combine") != null) {
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("choice")) {
                    combineMethod = CombineMethod.choice;
                }
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("interleave")) {
                    combineMethod = CombineMethod.interleave;
                }
                if (combineMethod == null) {
                    throw new InvalidCombineMethodException("start");
                } else {
                    if (this.grammar.getStartCombineMethod() != null) {
                        if (!this.grammar.getStartCombineMethod().equals(combineMethod)) {
                            throw new CombineMethodNotMatchingException("start: " + this.grammar.getStartCombineMethod().name() + " <-> " + combineMethod.name());
                        }
                    }
                    this.grammar.setStartCombineMethod(combineMethod);
                }
            }
        }

        visitChildren(node);
        if (this.pattern != null) {
            return this.pattern;
        } else {
            throw new PatternNotInitializedException("pattern", "start");
        }
    }

	@Override
	protected void addPattern(Pattern pattern) throws MultiplePatternException {
		if (this.pattern == null)
			this.pattern = pattern;
		else
			throw new MultiplePatternException("StartProcessor");
	}


}
