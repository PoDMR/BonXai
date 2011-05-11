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

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import de.tudortmund.cs.bonxai.bonxai.*;

import java.util.LinkedHashSet;

/**
 * Concrete implmentation of the root element finder.
 *
 * Searches for all elements referenced in any ancestor patterns and mathches
 * all ancestor patterns against pathes of those elements to check if the given
 * element and the given ancesor pattern is a root element.
 */
public class FullMatchingRootElementFinder extends RootElementFinder {

    /**
     * Return a list of all possible root elements.
     *
     * Find all possible root elements in the ancestor patterns of the given
     * Bonxai grammar.
     */
    public LinkedHashSet<ElementTreeNode> find(GrammarList grammar)
    {
        LinkedHashSet<ElementTreeNode> rootElements = new LinkedHashSet<ElementTreeNode>();
        HashMap<String,AncestorPatternElement> elements = findAllElements(grammar);

        for (String path: elements.keySet()) {
            for (Expression expression: grammar.getExpressions())
            {
                try {
                    AncestorPatternRegexpVisitor patternVisitor = new AncestorPatternRegexpVisitor(expression.getAncestorPattern());
                    Pattern ancestorPattern = patternVisitor.visit();
                    Matcher matcher         = ancestorPattern.matcher(path);

                    if (matcher.matches()) {
                        AncestorPatternElement element = elements.get(path);
                        rootElements.add(new ElementTreeNode(element.getNamespace(), element.getName(), expression));
                    }
                } catch (AncestorPatternUnknownParticleException e) {
                    // Just ignore this rule.
                    continue;
                }
            }
        }
        return rootElements;
    }

    /**
     * Find all elements.
     *
     * Find all elements referenced in any of the ancestor patterns in the
     * grammar.
     */
    protected HashMap<String,AncestorPatternElement> findAllElements(GrammarList grammar)
    {
        HashMap<String,AncestorPatternElement> elements = new HashMap<String,AncestorPatternElement>();
        if (grammar != null) {
            for (Expression expression: grammar.getExpressions())
            {
                elements.putAll(findElementsInAncestorPattern(expression.getAncestorPattern().getParticle()));
            }
        }
        return elements;
    }

    /**
     * Find all elements in an ancestor patttern.
     *
     * Find all elements referenced in any of the ancestor patterns in the
     * grammar.
     */
    protected HashMap<String,AncestorPatternElement> findElementsInAncestorPattern(AncestorPatternParticle particle)
    {
        HashMap<String,AncestorPatternElement> elements = new HashMap<String,AncestorPatternElement>();

        if (particle instanceof CardinalityParticle) {
            elements.putAll(findElementsInAncestorPattern(((CardinalityParticle) particle).getChild()));
        } else if (particle instanceof ContainerParticle) {
            for (AncestorPatternParticle childParticle: ((ContainerParticle) particle).getChildren()) {
                elements.putAll(findElementsInAncestorPattern(childParticle));
            }
        } else if (particle instanceof AncestorPatternElement) {
            String path = "/{" + ((AncestorPatternElement) particle).getNamespace() + "}" + ((AncestorPatternElement) particle).getName();
            elements.put(path, (AncestorPatternElement) particle);
        }

        return elements;
    }
}
