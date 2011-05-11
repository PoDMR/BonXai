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

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.ElementGroupElement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 */
public class ParticleExtractor {

    private HashSet<String> groupNodeNames;

    /**
     *
     */
    public ParticleExtractor() {
        this.groupNodeNames = new HashSet<String>();
    }

    /**
     * Create the subtree of Nodes extracted from the given particle and attach
     * them to the given parent node
     *
     * @param parentTreeNode
     * @param expression
     * @return parentTreeNode
     */
    public TreeNode createSubtreeFromExpression(TreeNode parentTreeNode, de.tudortmund.cs.bonxai.bonxai.Expression expression) {
        this.groupNodeNames = new HashSet<String>();
        if (expression.getChildPattern().getElementPattern() == null) {
            return parentTreeNode;
        }
        return createSubtreeFromExpressionAndParticle(parentTreeNode, expression, expression.getChildPattern().getElementPattern().getRegexp());
    }

    /**
     * Create the subtree of Nodes extracted from the given particle and attach
     * them to the given parent node.
     *
     * This is used for recusring into group definitions, which contain a
     * special particle container, which should not be fetched from the
     * expression.
     *
     * @param parentTreeNode
     * @param expression
     * @param particle
     * @return parentTreeNode
     */
    private TreeNode createSubtreeFromExpressionAndParticle(TreeNode parentTreeNode, de.tudortmund.cs.bonxai.bonxai.Expression expression, de.tudortmund.cs.bonxai.common.Particle particle) {
        Vector<de.tudortmund.cs.bonxai.converter.bonxai2xsd.old.TreeNode> childNodes = this.getChildTree(particle, expression);



        Iterator<TreeNode> itr = childNodes.iterator();
        while (itr.hasNext()) {
            TreeNode currTreeNode = (TreeNode) itr.next();
//                    if (currTreeNode instanceof ElementTreeNode) {
//                        if(((ElementTreeNode)currTreeNode).getName() != null) {
//                            parentTreeNode.addChild(currTreeNode);
//                        }
//                    }
//                    if (currTreeNode instanceof GroupTreeNode) {
//                        if(((GroupTreeNode)currTreeNode).getName() != null) {
                            parentTreeNode.addChild(currTreeNode);
//                        }
//                    }

        }
        return parentTreeNode;
    }

    /**
     * Create a vector of all child-TreeNodes extracted from the given particle
     * Recursive method to build the child trees
     *
     * @param particle
     * @return returnVector - vector of treenodes
     */
    private Vector<de.tudortmund.cs.bonxai.converter.bonxai2xsd.old.TreeNode> getChildTree(de.tudortmund.cs.bonxai.common.Particle particle, de.tudortmund.cs.bonxai.bonxai.Expression expression) {

        // Initialize an empty vector as result object
        Vector<de.tudortmund.cs.bonxai.converter.bonxai2xsd.old.TreeNode> returnVector = new Vector<de.tudortmund.cs.bonxai.converter.bonxai2xsd.old.TreeNode>();

        if (particle instanceof de.tudortmund.cs.bonxai.common.AnyPattern) {
            // Cast the particle to the right object instance for this case
//            AnyPattern anyPattern = (AnyPattern) particle;
        } else if (particle instanceof de.tudortmund.cs.bonxai.bonxai.Element) {

            // Cast the particle to the right object instance for this case
            de.tudortmund.cs.bonxai.bonxai.Element element = (de.tudortmund.cs.bonxai.bonxai.Element) particle;

            ElementTreeNode elementTreeNode = new ElementTreeNode(element.getNamespace(), element.getName(), null);

            returnVector.add(elementTreeNode);

        } else if (particle instanceof de.tudortmund.cs.bonxai.common.ParticleContainer) {

            // Cast the particle to the right object instance for this case
            ParticleContainer particleContainer = (ParticleContainer) particle;

            /* traverse through the List of particles in this sequence to add
             * the elements to the returnvector
             */
            if (!(particleContainer.getParticles().isEmpty())) {
                for (Particle currentParticle : particleContainer.getParticles()) {
                    returnVector.addAll(getChildTree(currentParticle, expression));
                }
            }

        } else if (particle instanceof de.tudortmund.cs.bonxai.common.GroupRef) {

            // Cast the particle to the right object instance for this case
            GroupRef groupRef = (GroupRef) particle;

            if (groupRef.getGroup() != null) {
                de.tudortmund.cs.bonxai.bonxai.ElementGroupElement currGroup = (ElementGroupElement) groupRef.getGroup();

                if (!groupNodeNames.contains(currGroup.getName())) {
                    GroupTreeNode groupTreeNode = new GroupTreeNode(currGroup.getName(), expression, groupRef);
                    groupNodeNames.add(currGroup.getName());
                    createSubtreeFromExpressionAndParticle(groupTreeNode, expression, currGroup.getParticleContainer());
                    returnVector.add(groupTreeNode);
                }                
            } else {
                // TODO: Exception
            }
        }

        return returnVector;
    }
}

