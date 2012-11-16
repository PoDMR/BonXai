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
import eu.fox7.schematoolkit.relaxng.om.Optional;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.parser.exceptions.EmptyPatternException;

import org.w3c.dom.Node;

/**
 * This processor handles the optional elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class OptionalProcessor extends RNGPatternProcessorBase {

    private Optional optional;

    public OptionalProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Optional processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.optional = new Optional();
        this.setPatternAttributes(optional, node);
        visitChildren(node);

        if (this.optional.getPatterns().size() < 1) {
            throw new EmptyPatternException("\"optional\" has not enough pattern");
        }

        return this.optional;
    }

	@Override
	protected void addPattern(Pattern pattern) {
		this.optional.addPattern(pattern);
	}
    
}
