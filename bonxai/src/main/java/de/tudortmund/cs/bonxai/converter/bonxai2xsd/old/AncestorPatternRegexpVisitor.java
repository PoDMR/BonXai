
/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.converter.bonxai2xsd.old;

import de.tudortmund.cs.bonxai.bonxai.*;

import java.util.regex.Pattern;

class AncestorPatternRegexpVisitor {
    protected AncestorPattern ancestorPattern;

    /**
     * Create ancestor pattern viositor from AncestorPattern.
     */
    public AncestorPatternRegexpVisitor(AncestorPattern ancestorPattern) {
        this.ancestorPattern = ancestorPattern;
    }

    /**
     * Visit the ancestor pattern into a regular expression.
     *
     * Visit the given ancesor pattern into a regular expresision representing
     * the semantics of the ancesto pattern and matching certain string
     * representations of ancestor paths.
     */
    public Pattern visit() throws AncestorPatternUnknownParticleException
    {
        // @TODO: We might return an already compiled pattern for an ancestor
        // pattern from a caching HashMap here, to reduce the recompilation
        // overhead of the same ancestor patterns.
        String pattern = "\\A" + this.visitParticle(this.ancestorPattern.getParticle()) + "\\Z";
        return Pattern.compile(pattern);
    }

    /**
     * Visit a single particle.
     *
     * Visit any single particle and call the correct visitor method for the
     * respective particle.
     */
    protected String visitParticle(AncestorPatternParticle particle) throws AncestorPatternUnknownParticleException
    {
        if (particle instanceof OrExpression) {
            return this.visitOrExpression((OrExpression) particle);
        } else if (particle instanceof SequenceExpression) {
            return this.visitSequenceExpression((SequenceExpression) particle);
        } else if (particle instanceof CardinalityParticle) {
            return this.visitCardinalityParticle((CardinalityParticle) particle);
        } else if (particle instanceof de.tudortmund.cs.bonxai.bonxai.AncestorPatternElement) {
            return this.visitElement((de.tudortmund.cs.bonxai.bonxai.AncestorPatternElement) particle);
        } else {
            throw new AncestorPatternUnknownParticleException(particle);
        }
    }

    /**
     * Visit an element.
     *
     * Visit a single or double slash prefixed element and return a proper
     * string representation for the regular expresion.
     */
    protected String visitElement(de.tudortmund.cs.bonxai.bonxai.AncestorPatternElement element) {
        String regexp = "";
        if (element instanceof DoubleSlashPrefixElement) {
            regexp = ".*";
        }

        return regexp + "/\\{" + Pattern.quote(element.getNamespace()) + "\\}" + Pattern.quote(element.getName());
    }

    /**
     * Visit an element.
     *
     * Visit a single or double slash prefixed element and return a proper
     * string representation for the regular expresion.
     */
    protected String visitCardinalityParticle(de.tudortmund.cs.bonxai.bonxai.CardinalityParticle particle) throws AncestorPatternUnknownParticleException {
        if (particle.getMax() == null)
        {
            return "(?:" + this.visitParticle(particle.getChild()) + "){" + particle.getMin() + ",}";
        }
        else
        {
            return "(?:" + this.visitParticle(particle.getChild()) + "){" + particle.getMin() + "," + particle.getMax() + "}";
        }
    }

    /**
     * Visit an or expression.
     *
     * Visit an or expression and return a proper string representation of the
     * or expression for the regular expression. Visitor methods are called
     * recursivlely in this method.
     */
    protected String visitOrExpression(OrExpression expression) throws AncestorPatternUnknownParticleException {
        String regexp = "";
        for (AncestorPatternParticle particle: expression.getChildren())
        {
            regexp += this.visitParticle(particle) + "|";
        }

        if (regexp.length() == 0)
        {
            return "";
        }

        return "(?:" + regexp.substring(0, regexp.length() - 1) + ")";
    }

    /**
     * Visit an sequence expression.
     *
     * Visit an sequence expression and return a proper string representation
     * of the sequence expression fsequence the regular expression.
     * Visitsequence methods are called recursivlely in this method.
     */
    protected String visitSequenceExpression(SequenceExpression expression) throws AncestorPatternUnknownParticleException {
        String regexp = "";
        for (AncestorPatternParticle particle: expression.getChildren())
        {
            regexp += this.visitParticle(particle);
        }

        if (regexp.length() == 0)
        {
            return "";
        }

        return "(?:" + regexp + ")";
    }
}
