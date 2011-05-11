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
import java.util.HashMap;

import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.converter.bonxai2xsd.ParticleConverter;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 *
 */
public class GrammarConverter extends PartConverter {

    /**
     * Attribute, that holds the document tree root, after the whole conversion
     */
    private SchemaTreeNode treeRoot = null;

    HashSet<String> nodeNameSet;
    HashSet<String> groupNames;
    HashSet<String> elementNames;

    /**
     * Base constructor for part converters.
     *
     * @param schemas
     */
    public GrammarConverter(HashMap<String, XSDSchema> schemas) {
        super(schemas);
    }

    /**
     * Convert the given Bonxai schema into XSD schemas.
     *
     * @param schema
     */
    public void convert(de.tudortmund.cs.bonxai.bonxai.Bonxai schema) {
        // @TODO: We need to respect the different possible target namespaces here.
        SchemaTreeNode tree = new SchemaTreeNode();

        FullMatchingRootElementFinder finder     = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> rootNodes = finder.find(schema.getGrammarList());
        for (ElementTreeNode rootNode : rootNodes) {
            tree.addChild(rootNode);
            nodeNameSet = new HashSet<String>();
            recurse(rootNode, schema.getGrammarList(), schema.getConstraintList());
        }

//        DebugUtil debug = new DebugUtil();
//        debug.printTreeNodeToSystemOut(tree);

        //reduceElementsInTree(tree);
//        debug.printTreeNodeToSystemOut(tree);

        //reduceGroupsInTree(tree, schema);
//        debug.printTreeNodeToSystemOut(tree);

        //convertGroupsFromTree(tree, schema);

        // Store for external test introspection
        this.treeRoot = tree;

        // Set the root of the tree into its variable for further handling
        TreeNodeTreeConverter converter = new TreeNodeTreeConverter(this.schemas);
        converter.convert(tree.getChilds(), tree);

        // Save all used groups from the symbolTables to the topLevel-GroupLists of the schemas.
        if (!this.schemas.isEmpty()) {
            for (String currentSchemaKey : this.schemas.keySet()) {
                XSDSchema currentSchema = this.schemas.get(currentSchemaKey);
                for (de.tudortmund.cs.bonxai.bonxai.ElementGroupElement currentGroup : schema.getGroupSymbolTable().getAllReferencedObjects()) {
                    if (currentGroup != null) {
                        ParticleConverter particleConverter = new ParticleConverter();
                        de.tudortmund.cs.bonxai.common.ParticleContainer particleContainer = (ParticleContainer) particleConverter.convertParticle(currentSchema, currentSchemaKey, currentGroup.getParticleContainer());
                        de.tudortmund.cs.bonxai.xsd.Group xsdGroup = new de.tudortmund.cs.bonxai.xsd.Group("{" + currentSchemaKey + "}" + currentGroup.getName(), particleContainer);
                        currentSchema.addGroup(currentSchema.getGroupSymbolTable().updateOrCreateReference("{" + currentSchemaKey + "}" + currentGroup.getName(), xsdGroup));
                    }
                }
            }
            for (String currentSchemaKey : this.schemas.keySet()) {
                XSDSchema currentSchema = this.schemas.get(currentSchemaKey);
                for (de.tudortmund.cs.bonxai.xsd.AttributeGroup currentAttributeGroup : currentSchema.getAttributeGroupSymbolTable().getAllReferencedObjects()) {
                    if (currentAttributeGroup != null)
                    currentSchema.addAttributeGroup(currentSchema.getAttributeGroupSymbolTable().getReference(currentAttributeGroup.getName()));
                }
            }
        }
    }

    private LinkedHashSet<String> findNamespacesForGroup(TreeNode node) {
        LinkedHashSet<String> resultNamespaceList  = new LinkedHashSet<String>();

        if (node instanceof ElementTreeNode) {
            resultNamespaceList.add(((ElementTreeNode) node).getNamespace());
        } else if (node instanceof SchemaTreeNode) {
//            ((SchemaTreeNode) node)
        } else if (node instanceof GroupTreeNode) {
            ((GroupTreeNode) node).getParents();
            for (TreeNode currentParent : ((GroupTreeNode) node).getParents()) {
                resultNamespaceList.addAll(findNamespacesForGroup(currentParent));
            }
        }



        return resultNamespaceList;
    }

    /**
     * Recurse.
     */
    private void recurse(TreeNode node, GrammarList grammar, de.tudortmund.cs.bonxai.bonxai.ConstraintList constraintList) {
        Vector<Expression> expressions = grammar.getExpressions();
        Vector<de.tudortmund.cs.bonxai.bonxai.Constraint> constraints = new Vector<de.tudortmund.cs.bonxai.bonxai.Constraint>();
        if (constraintList != null) {
            constraints = constraintList.getConstraints();
        }

        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        ParticleExtractor extractor = new ParticleExtractor();
        for (int i = expressions.indexOf(expressions.lastElement()); i >= 0; --i) {

            // Match constraint ancestor patterns, to integrate
            // constraints into the element tree nodes.
            if (node instanceof ElementTreeNode) {
                for (de.tudortmund.cs.bonxai.bonxai.Constraint currConstraint : constraints) {
                    if (matcher.matches(node, currConstraint.getAncestorPattern())) {
                        ((ElementTreeNode) node).addConstraint(currConstraint);
                    }
                }
            }

            if ((node instanceof ElementTreeNode) &&
                matcher.matches(node, expressions.get(i).getAncestorPattern())) {
                nodeNameSet.add((((ElementTreeNode) node).getFQName()));

                extractor.createSubtreeFromExpression(node, expressions.get(i));

                // Set correct child pattern for node
                ((ElementTreeNode) node).setChildPattern(expressions.get(i).getChildPattern());

                for (TreeNode child : node.getChilds()) {
                    if (child instanceof ElementTreeNode) {
                        if (!nodeNameSet.contains(((ElementTreeNode) child).getFQName())) {
                            nodeNameSet.add((((ElementTreeNode) child).getFQName()));
                            recurse(child, grammar, constraintList);
                        }
                    }

                    if (child instanceof GroupTreeNode) {
                        if (!nodeNameSet.contains(((GroupTreeNode) child).getName())) {
                            nodeNameSet.add((((GroupTreeNode) child).getName()));
                            recurse(child, grammar, constraintList);

                        }
                    }
                }

                break;
            } else if (node instanceof GroupTreeNode) {
                nodeNameSet.add((((GroupTreeNode) node).getName()));

                


                for (TreeNode child : node.getChilds()) {
                    if (child instanceof ElementTreeNode) {
                        if (!nodeNameSet.contains(((ElementTreeNode) child).getFQName())) {
                            
                            recurse(child, grammar, constraintList);
                        }
                    }

                    if (child instanceof GroupTreeNode) {
                        if (!nodeNameSet.contains(((GroupTreeNode) child).getName())) {

                            recurse(child, grammar, constraintList);

                        }
                    }
                }
            }

        // No ancestor pattern matched.
        }
    }

    /**
     * reduceElementsInTree.
     * Method that reduces the tree by finding objects with the same values and
     * referencing the appearance to the first found object of this kind.
     *
     * The handling of groups (destroying or renaming) does not take place in
     * this method!
     *
     * @param node - the root node of the document tree
     *
     * @TODO: public is for testing purposes, change to private
     */
    public void reduceElementsInTree(TreeNode node) {
        elementNames = new HashSet<String>();
        groupNames = new HashSet<String>();
        this.reduceElementsInTree(node, node, new LinkedHashSet<TreeNode>());
    }

    /**
     * reduceElementsInTree.
     * Recursive method that traverses the tree and calls the method
     * replaceNodeWithSameId(...) to reduce the given tree by eliminating
     * objects with the same values. All these will be replaced by the first
     * found object of this kind.
     *
     * @param node, visitorTreeNode, alreadyReplaced
     *
     */
    private void reduceElementsInTree(TreeNode node, TreeNode vistorTreeNode, LinkedHashSet<TreeNode> alreadyReplaced) {
        if (!(node instanceof SchemaTreeNode)) {
            if (node instanceof ElementTreeNode) {
                elementNames.add(((ElementTreeNode) node).getFQName());
            } else if (node instanceof GroupTreeNode) {
                groupNames.add(((GroupTreeNode) node).getName());
            }
            replaceNodeWithSameId(node, vistorTreeNode, alreadyReplaced);
        }
        for (TreeNode currentChild : node.getChilds()) {
            if (currentChild instanceof ElementTreeNode) {
                if (!elementNames.contains(((ElementTreeNode) currentChild).getFQName())) {
                    reduceElementsInTree(currentChild, vistorTreeNode, alreadyReplaced);
                }
            }
            if (currentChild instanceof GroupTreeNode) {
                if (!groupNames.contains(((GroupTreeNode) currentChild).getName())) {
                    reduceElementsInTree(currentChild, vistorTreeNode, alreadyReplaced);
                }
            }
        }
    }

    /**
     * replaceNodeWithSameId.
     * Recursive method that helps to traverse the tree and find nodes with
     * equal IDs. All these will be replaced by the first found object with
     * this ID. This helps to reduce the tree by disposing unnecessary objects.
     *
     * The handling of groups (destroying or renaming) does not take place in
     * this method!
     *
     * @param node, visitorTreeNode, alreadyReplaced
     *
     */
    private void replaceNodeWithSameId(TreeNode node, TreeNode vistorTreeNode, LinkedHashSet<TreeNode> alreadyReplaced) {
        if (node.generateId().equals(vistorTreeNode.generateId()) && !node.equals(vistorTreeNode)) {

            if (!alreadyReplaced.contains(node)) {
                for (TreeNode currentParentNode : vistorTreeNode.getParents()) {
                    currentParentNode.replaceChild(vistorTreeNode, node);
                }
                alreadyReplaced.add(vistorTreeNode);
            }
        }
        for (TreeNode currentChild : vistorTreeNode.getChilds()) {
            replaceNodeWithSameId(node, currentChild, alreadyReplaced);
        }
    }

    /**
     * reduceGroupsInTree.
     * Method that reduces the tree by finding groups with the same name but a
     * different childpattern. These groups have to be handled.
     *
     * Rename the found group, write this new group into the
     * grouplist and convert it to XSD after that.
     *
     * @param node - the root node of the document tree
     *
     * @TODO: public is for testing purposes, change to private
     */
    public void reduceGroupsInTree(TreeNode node, Bonxai schema) {
        groupNames = new HashSet<String>();
        elementNames = new HashSet<String>();
        this.reduceGroupsInTree(node, node, new LinkedHashSet<TreeNode>(), schema);
    }

    public void convertGroupsFromTree(TreeNode node, Bonxai schema) {
        groupNames = new HashSet<String>();
        this.convertGroupsFromTree(node, node, new LinkedHashSet<TreeNode>(), schema);
    }


    /**
     * reduceGroupsInTree.
     * Recursive method that traverses the tree and calls the method
     * handleGroupsWithSameNameButDifferentId(...) to reduce the given tree
     * by handling the groups with the same name but different childpatterns.
     *
     * @param node, visitorTreeNode, alreadyHandled
     *
     */
    private void reduceGroupsInTree(TreeNode node, TreeNode vistorTreeNode, LinkedHashSet<TreeNode> alreadyHandled, Bonxai schema) {
        if (node instanceof GroupTreeNode) {
            groupNames.add(((GroupTreeNode) node).getName());
            System.out.println(((GroupTreeNode) node).getName());
            ((GroupTreeNode) node).addNamespaces(findNamespacesForGroup(node));
            handleGroupsWithSameNameButDifferentId((GroupTreeNode) node, vistorTreeNode, alreadyHandled, schema);
        } else if (node instanceof ElementTreeNode) {
            elementNames.add(((ElementTreeNode) node).getFQName());
        }

        for (TreeNode currentChild : node.getChilds()) {
            if (currentChild instanceof ElementTreeNode) {
                if (!elementNames.contains(((ElementTreeNode) currentChild).getFQName())) {
                    reduceGroupsInTree(currentChild, vistorTreeNode, alreadyHandled, schema);
                }
            }

            if (currentChild instanceof GroupTreeNode) {
                if (!groupNames.contains(((GroupTreeNode) currentChild).getName())) {
                    reduceGroupsInTree(currentChild, vistorTreeNode, alreadyHandled, schema);
                }
            }
        }
    }

    private void convertGroupsFromTree(TreeNode node, TreeNode vistorTreeNode, LinkedHashSet<TreeNode> alreadyHandled, Bonxai schema) {
        if (node instanceof GroupTreeNode) {
            GroupTreeNode groupNode = (GroupTreeNode) node;
            groupNames.add(((GroupTreeNode) node).getName());
            System.out.println(((GroupTreeNode) node).getName());
            if (groupNode.getGroupRef().getGroup() != null) {
                de.tudortmund.cs.bonxai.bonxai.ElementGroupElement group = ((de.tudortmund.cs.bonxai.bonxai.ElementGroupElement) (groupNode.getGroupRef().getGroup()));
                schema.getGroupSymbolTable().updateOrCreateReference(group.getName(), group);
            }
        }
        for (TreeNode currentChild : node.getChilds()) {
            if (currentChild instanceof ElementTreeNode) {
                    convertGroupsFromTree(currentChild, vistorTreeNode, alreadyHandled, schema);
            }

            if (currentChild instanceof GroupTreeNode) {
                if (!groupNames.contains(((GroupTreeNode) currentChild).getName())) {
                    convertGroupsFromTree(currentChild, vistorTreeNode, alreadyHandled, schema);
                }
            }
        }
    }


    /**
     * handleGroupsWithSameNameButDifferentId.
     * Recursive method that helps to traverse the tree and find groups with the
     * same name but different childpatterns. This helps to reduce the tree by
     * disposing invalid groups.
     *
     * @param node, visitorTreeNode, alreadyHandled
     *
     */
    private void handleGroupsWithSameNameButDifferentId(GroupTreeNode node, TreeNode vistorTreeNode, LinkedHashSet<TreeNode> alreadyHandled, Bonxai schema) {

        if (vistorTreeNode instanceof GroupTreeNode) {
            GroupTreeNode visitorGroupNode = (GroupTreeNode) vistorTreeNode;

            // Groups have to be handled when:
            // - the name of at least two groups is the same and
            // - their ID is different. This means, that they have a different childpattern

            /* @TODO:
             * We have to write good example Bonxai-Schemas for the bonxai parser to
             * check that this cool feature will be used often enough ;-)
             */
            if (node.getName().equals(visitorGroupNode.getName()) && !(node.generateId().equals(vistorTreeNode.generateId())) && !node.equals(visitorGroupNode)) {
                if (!alreadyHandled.contains(node)) {

                    /**
                     * - rename the new Copy this group from and to Bonxai,
                     * - rename it there and
                     * - write the right child
                     * pattern
                     */

                    // create a name for the new group
                    String newGroupName = "name-" + visitorGroupNode.generateId() + "-name";
                    visitorGroupNode.setName(newGroupName);

                    // create a new groupElement for the groupList
                    ElementGroupElement newGroupElement = new ElementGroupElement(newGroupName, (ParticleContainer)visitorGroupNode.getParticle());
                    // Add this new groupElement to the groupList
                    schema.getGroupList().getGroupElements().add(newGroupElement);
                    schema.getGroupSymbolTable().updateOrCreateReference(newGroupName, newGroupElement);

                    /* modify the groupRef in the groupTreeNode. This has to be
                     * used instead of the one in the original expression of the
                     * node to be converted into XSD
                     */
                    de.tudortmund.cs.bonxai.common.GroupRef groupRefToGroup = new GroupRef(schema.getGroupSymbolTable().updateOrCreateReference(newGroupName, newGroupElement));


                    // Clone the particle with a replaced group reference
                    ElementTreeNode parent = (ElementTreeNode) visitorGroupNode.getAncestorPath().lastElement();
                    if (parent.getExpression() != null) {
                        ElementPattern pattern = parent.getExpression().getChildPattern().getElementPattern();
                        ParticleGroupRefReplacer replacer = new ParticleGroupRefReplacer();
                        pattern.setRegexp(replacer.replaceGroupRefInParticle(pattern.getRegexp(), visitorGroupNode.getGroupRef(), groupRefToGroup));
                    }
                    visitorGroupNode.setGroupRef(groupRefToGroup);
                    alreadyHandled.add(vistorTreeNode);
                }
            }
        }
        for (TreeNode currentChild : vistorTreeNode.getChilds()) {
            handleGroupsWithSameNameButDifferentId(node, currentChild, alreadyHandled, schema);
        }
    }

    /**
     * Return the tree generated within the conversion progress
     *
     * @return treeRoot
     */
    public SchemaTreeNode getTreeRoot() {
        return this.treeRoot;
    }


}

