
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

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import de.tudortmund.cs.bonxai.bonxai.*;

class RegularExpressionAncestorPatternMatcher extends AncestorPatternMatcher {

    /**
     * Check if the current path matches the given ancestor pattern.
     *
     * Check if the ancestor path of a node in the tree does match a given
     * ancestor pattern of an Bonxai expression.
     */
    public boolean matches(TreeNode parentNode, AncestorPattern pattern)
    {
        String ancestorPath = this.visitAncestorPath(parentNode.getAncestorPath());
        AncestorPatternRegexpVisitor patternVisitor = new AncestorPatternRegexpVisitor(pattern);

        try {
            Pattern ancestorPattern = patternVisitor.visit();
            Matcher matcher         = ancestorPattern.matcher( ancestorPath );

            // System.out.println( "Pattern: '" + ancestorPattern.pattern() + "'" );
            // System.out.println( "Path   : '" + ancestorPath + "'" );

            return matcher.matches();
        } catch (AncestorPatternUnknownParticleException e) {
            return false;
        }
    }

    /**
     * Visit ancestor path into string representation.
     */
    protected String visitAncestorPath(Vector<TreeNode> ancestorPath)
    {
        String path = "";
        for (TreeNode node: ancestorPath)
        {
            if (node instanceof ElementTreeNode)
            {
                ElementTreeNode element = (ElementTreeNode) node;
                path += "/{" + element.getNamespace() + "}" + element.getName();
            }
        }

        return path;
    }
}
