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
import eu.fox7.schematoolkit.relaxng.om.Interleave;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;

import org.w3c.dom.Node;

/**
 * This processor handles the interleave elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class InterleaveProcessor extends RNGPatternProcessorBase {

    private Interleave interleave;

    public InterleaveProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Interleave processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.interleave = new Interleave();
        this.setPatternAttributes(interleave, node);
        visitChildren(node);

        if (this.interleave.getPatterns().size() < 1) {
            throw new EmptyPatternException("interleave has not enough pattern");
        }

        return this.interleave;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.interleave.addPattern(pattern);
	}
}
