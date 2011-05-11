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

import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashSet;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.converter.bonxai2xsd.ParticleConverter;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;

/**
 * Converts the element tree into the XML schema.
 *
 * The element tree defines the types of the elements. The passed list of root
 * elements needs to be converted into top level elements in the schema, to
 * remain possible root nodes.
 */
class TreeNodeTreeConverter extends PartConverter {

    /**
     * Hashmap for all yet used element names.
     *
     * Contains a mapping of String element names to their element tree nodes.
     * Used to generate unique names for elements.
     */
    protected HashMap<ElementTreeNode,String> typeNames = new HashMap<ElementTreeNode,String>();

    /**
     * Hashmap for all yet used constraint names.
     *
     * Contains a mapping of String constraint names to their constraint objects.
     * Used to generate unique names for constraints like KeyRef and Unique.
     */
    protected HashMap<de.tudortmund.cs.bonxai.bonxai.Constraint,String> constraintNames = new HashMap<de.tudortmund.cs.bonxai.bonxai.Constraint,String>();

    /**
     * Base constructor for part converters.
     *
     * @param schemas
     */
    public TreeNodeTreeConverter(HashMap<String, XSDSchema> schemas) {
        super(schemas);
    }

    /**
     * Get type name for element.
     *
     * @return typeName
     */
    protected String getElementTypeName(ElementTreeNode element) {
        if (this.typeNames.containsKey(element)) {
            return this.typeNames.get(element);
        } else {
            int i = 1;
            String name = "{" + element.getNamespace() + "}" + element.getName();
            while (this.typeNames.containsValue(name)) {
                name = "{" + element.getNamespace() + "}" + element.getName() + "_" + i;
            }

            this.typeNames.put(element, name);
            return name;
        }
    }

    /**
     * Get full qualified unique name for KeyRef- and Unique- Constraints.
     *
     * @return typeName
     */
    protected String getFullQualifiedUniqueConstraintName(String namespace, String type, de.tudortmund.cs.bonxai.bonxai.Constraint constraint) {
        if (this.constraintNames.containsKey(constraint)) {
            return this.constraintNames.get(constraint);
        } else {
            int i = 1;
            String name = "{" + namespace + "}" + type;
            while (this.constraintNames.containsValue(name)) {
                name = "{" + namespace + "}" + type + "_" + i;
            }

            this.constraintNames.put(constraint, name);
            return name;
        }
    }

    HashSet<String> attributeGroupNames;

    /**
     * Find all Attribute group references.
     *
     * Recursivly find all AttributeGroupReference onjects in the given
     * attribute pattern.
     */
    protected HashSet<AttributeGroupReference> findAttributeGroupRefs(AttributePattern pattern) {
        HashSet<AttributeGroupReference> set = new HashSet<AttributeGroupReference>();

        for (AttributeListElement attribute: pattern.getAttributeList()) {
            if (attribute instanceof AttributeGroupReference) {
                if (!attributeGroupNames.contains(((AttributeGroupReference) attribute).getName())) {
                    attributeGroupNames.add(((AttributeGroupReference) attribute).getName());
                    set.add((AttributeGroupReference) attribute);

                    // Recurse into attribute group
                    set.addAll(this.findAttributeGroupRefs(((AttributeGroupReference) attribute).getGroupElement().getAttributePattern()));
                }
            }
        }

        return set;
    }

    /**
     * Locate and convert attribute groups.
     *
     * Locate attribute groups in the current element and create the attribute
     * groups in the target schema of the current element. This is used to
     * determine the namespaces each attribute group is used in.
     */
    protected void convertContainedAttributeGroups(ElementTreeNode element, AttributePattern pattern) {
        XSDSchema xsd                  = this.getSchema(element.getNamespace());
        ParticleConverter converter = new ParticleConverter();
        attributeGroupNames = new HashSet<String>();
        for (AttributeGroupReference attr: this.findAttributeGroupRefs(pattern)) {
            AttributeGroupElement group   = attr.getGroupElement();
            AttributeGroup attributeGroup = new AttributeGroup("{" + element.getNamespace() + "}" + group.getName());
            for (AttributeParticle particle: converter.convertParticle(xsd, element.getNamespace(), group.getAttributePattern())) {
                attributeGroup.addAttributeParticle(particle);
            }
            xsd.getAttributeGroupSymbolTable().updateOrCreateReference(attributeGroup.getName(), attributeGroup);
        }
    }

    /**
     * Convert an element.
     *
     * Converts a single ElementTreeNode node, representing an element in Bonxai
     * to an element in XSD.
     *
     * @return element
     */
    protected de.tudortmund.cs.bonxai.xsd.Element convertElement(LinkedHashSet<TreeNode> rootNodes, ElementTreeNode element) {

        XSDSchema xsd                  = this.getSchema(element.getNamespace());
        ParticleConverter converter = new ParticleConverter();

        de.tudortmund.cs.bonxai.xsd.Element xsdElement = new de.tudortmund.cs.bonxai.xsd.Element(element.getNamespace(), element.getName());

        if (element.getChildPattern() == null)
        {
            // The element is referenced, but not specified in the schema,
            // so no further attributes are set.
            return xsdElement;
        }
        ChildPattern pattern        = element.getChildPattern();

        // Check if there are attribute groups which should be converted
        if (pattern.getAttributePattern() != null) {
            this.convertContainedAttributeGroups(element, pattern.getAttributePattern());
        }

        // Convert simple types, if no attributes are set, otherwise it will
        // get a bit more complicated...
        if ((pattern.getElementPattern() != null) &&
            (pattern.getElementPattern().getBonxaiType() != null)) {
            String typeName = pattern.getElementPattern().getBonxaiType().getFullQualifiedName();

            if (pattern.getAttributePattern() == null ) {
                xsd.getTypeSymbolTable().updateOrCreateReference(typeName, new SimpleType(typeName, null));
                xsdElement.setType(xsd.getTypeSymbolTable().getReference(typeName));
            } else {
                xsd.getTypeSymbolTable().updateOrCreateReference(typeName, new SimpleType(typeName, null));
                SimpleContentExtension extension = new SimpleContentExtension(xsd.getTypeSymbolTable().getReference(typeName));

                SimpleContentType content = new SimpleContentType();
                content.setInheritance(extension);

                ComplexType type = new ComplexType(typeName, content);

                for (AttributeParticle attrParticle: converter.convertParticle(xsd, element.getNamespace(), pattern.getAttributePattern())) {
                    type.addAttribute(attrParticle);
                }

                xsd.getTypeSymbolTable().updateOrCreateReference(typeName, type);
                xsdElement.setType(xsd.getTypeSymbolTable().getReference(typeName));
            }

            if (pattern.getElementPattern().isMissing()) {
                xsdElement.setNillable();
            }

            if (pattern.getElementPattern().getDefault() != null) {
                xsdElement.setDefault(pattern.getElementPattern().getDefault());
            }

            if (pattern.getElementPattern().getFixed() != null) {
                xsdElement.setFixed(pattern.getElementPattern().getFixed());
            }
        }

        // Convert complex types
        if ((pattern.getElementPattern() != null) &&
            (pattern.getElementPattern().getRegexp() != null)) {
            String name = this.getElementTypeName(element);

            ComplexContentType content = new ComplexContentType();
            content.setParticle(
                this.deepConvert(
                    rootNodes,
                    element,
                    converter.convertParticle(xsd, element.getNamespace(), pattern.getElementPattern().getRegexp())
                )
            );
            content.setMixed(pattern.getElementPattern().isMixed());
            ComplexType type = new ComplexType(name, content);

            // Append attributes, if existing
            if (pattern.getAttributePattern() != null)
            {
                for (AttributeParticle attrParticle: converter.convertParticle(xsd, element.getNamespace(), pattern.getAttributePattern())) {
                    type.addAttribute(attrParticle);
                }
            }

            xsd.getTypeSymbolTable().updateOrCreateReference(name, type);
            xsdElement.setType(xsd.getTypeSymbolTable().getReference(name));
        }

        // Convert attributes, if no element pattern has been specified
        if ((pattern.getAttributePattern() != null) &&
            (pattern.getElementPattern() == null)) {
            String name = this.getElementTypeName(element);

            ComplexType type = new ComplexType(name, null);
            for (AttributeParticle attrParticle: converter.convertParticle(xsd, element.getNamespace(), pattern.getAttributePattern())) {
                type.addAttribute(attrParticle);
            }

            xsd.getTypeSymbolTable().updateOrCreateReference(name, type);
            xsdElement.setType(xsd.getTypeSymbolTable().getReference(name));
        }

        return xsdElement;
    }

    /**
     * Get element reference for global element declaration.
     *
     * @return elementRef
     */
    protected ElementRef getElementReference(ElementTreeNode element) {
        XSDSchema xsd = this.getSchema(element.getNamespace());
        return new ElementRef(xsd.getElementSymbolTable().getReference(
            "{" + element.getNamespace() + "}" + element.getName()
        ));
    }

    /**
     * Converts a group.
     *
     * Converts a goup by adding the group to all schemas using this group. The
     * schemas using the group can be determined by the parents in the TreeNode
     * tree. All namespaces of the groups parents are relevant for the current
     * group.
     */
    protected void convertGroup(GroupTreeNode group) {
        ParticleConverter particleConverter = new ParticleConverter();
        ElementGroupElement element = (ElementGroupElement) group.getGroupRef().getGroup();

        for (String namespace: group.getNamespaces()) {
            XSDSchema xsd = this.getSchema(namespace);

            xsd.getGroupSymbolTable().updateOrCreateReference(
                "{" + namespace + "}" + element.getName(),
                new de.tudortmund.cs.bonxai.xsd.Group(
                    "{" + namespace + "}" + element.getName(),
                    (ParticleContainer) particleConverter.convertParticle(xsd, namespace, element.getParticleContainer())
                )
            );
        }
    }

    /**
     * Deep conversion of inlined particles.
     *
     * Receives a particleContainer representing the child pattern of the
     * current node and the ElementTreeNode object for the current node, which
     * contains all ElementTreeNode children of the current node, representing
     * the types for the elements in the child pattern. The elements in the
     * child pattern should be replaced by the types defined in the
     * ElementTreeNode childs.
     *
     * This methods recursively iterates over two trees, the particle tree and
     * the TreeNode tree, depending on the current nodes. Be careful touching
     * this. :)
     */
    protected Particle deepConvert(LinkedHashSet<TreeNode> rootNodes, TreeNode tree, Particle particle) {
        // Ensure inline elements as recursed in
        if (particle instanceof de.tudortmund.cs.bonxai.xsd.Element) {
            // Look for child in TreeNode matching the element name and convert
            // it
            for (TreeNode child : tree.getChilds()) {

                if ((child instanceof ElementTreeNode) &&
                    (((ElementTreeNode) child).getFQName().equals(((de.tudortmund.cs.bonxai.xsd.Element) particle).getName()))) {

                    return this.convertElement(rootNodes, (ElementTreeNode) child);
                }
            }
        }

        if (!(particle instanceof ParticleContainer)) {
            return particle;
        }

        if (((ParticleContainer) particle).getParticles().isEmpty()) {
            return particle;
        }

        // Recurse into particle trees and replace all element references
        // by the inlined elements or references to global elements.
        for (Particle currentParticle : ((ParticleContainer) particle).getParticles()) {
            if (currentParticle instanceof de.tudortmund.cs.bonxai.xsd.Element) {
                de.tudortmund.cs.bonxai.xsd.Element xsdElement = (de.tudortmund.cs.bonxai.xsd.Element) currentParticle;

                for (TreeNode child : tree.getChilds()) {

                    // Find the child in the element tree matching the
                    // currently active element node in the particle tree
                    // and handle this one.
                    if ((child instanceof ElementTreeNode) &&
                        (((ElementTreeNode) child).getName().equals(xsdElement.getLocalName())) &&
                        (((ElementTreeNode) child).getNamespace().equals(xsdElement.getNamespace()))) {

                        // If the element is a global type, we just
                        // reference that type. Otherwise we need to inline
                        // the whole type, by using the convertElement()
                        // method.
                        //
                        // We also need to declare the element as a global
                        // type, if it is from a different namespace.
                        if (rootNodes.contains(((ElementTreeNode) child))) {
                            ((ParticleContainer) particle).setParticle(
                                ((ParticleContainer) particle).getParticles().indexOf(currentParticle),
                                this.getElementReference((ElementTreeNode) child)
                            );
                        } else {
                            // @TODO: Instead of inlinining we could at
                            // least define and reference a globally
                            // defined named complex type for child
                            // pattern. Does not apply for the other
                            // element properties, though.
                            ((ParticleContainer) particle).setParticle(
                                ((ParticleContainer) particle).getParticles().indexOf(currentParticle),
                                this.convertElement(rootNodes, (ElementTreeNode) child)
                            );
                        }
                    }
                }
            } else if (currentParticle instanceof ParticleContainer) {
                // Recurse into particle containers
                ((ParticleContainer) particle).setParticle(
                    ((ParticleContainer) particle).getParticles().indexOf(currentParticle),
                    this.deepConvert(rootNodes, tree, currentParticle)
                );
            }
        }

        // Additionally iterate over all group childs in the tree and
        // convert them properly. Also recurse into all found groups.
        for (TreeNode child : tree.getChilds()) {
            if (child instanceof GroupTreeNode) {
                this.convertGroup((GroupTreeNode) child);
                this.deepConvert(rootNodes, child, particle);
            }
        }

        return particle;
    }

    /**
     * Finds all elements chaning namespaces and adds the as additional root nodes.
     *
     * The list of root nodes and the root nodes assigned to the schema need
     * to be extended by all elements where the parent node namespace differs
     * from the current namespace.
     */
    protected void findNamespaceChangers(LinkedHashSet<TreeNode> rootNodes, SchemaTreeNode tree, TreeNode element) {
        if (element instanceof ElementTreeNode) {
            ElementTreeNode current = (ElementTreeNode) element;

            // Check if element switches the namespace
            String parentNamespace = null;
            int parentNodeIndex    = current.getAncestorPath().size() - 2;
            if (current.getAncestorPath().get(parentNodeIndex) instanceof ElementTreeNode) {
                parentNamespace = ((ElementTreeNode) current.getAncestorPath().get(parentNodeIndex)).getNamespace();
            }

            // If namespaces differ, this is an additional root node
            if (!current.getNamespace().equals(parentNamespace)) {
                rootNodes.add(current);
                tree.addChild(current);
            }
        }

        // Recurse
        for (TreeNode child : element.getChilds()) {
            findNamespaceChangers(rootNodes, tree, child);
        }
    }

    /**
     * Convert the given tree node tree into XSD schemas.
     */
    public void convert(LinkedHashSet<TreeNode> rootNodes, TreeNode tree) {

        // The list of root nodes and the root nodes assigned to the schema
        // need to be extended by all elements where the parent node namespace
        // differs from the current namespace.
        if (tree instanceof SchemaTreeNode) {
            this.findNamespaceChangers(rootNodes, (SchemaTreeNode) tree, tree);
        }

        for (TreeNode node: tree.getChilds()) {
            if (node instanceof ElementTreeNode) {
                // Create node in xsd
                ElementTreeNode element = (ElementTreeNode) node;
                XSDSchema xsd              = this.getSchema(element.getNamespace());

                de.tudortmund.cs.bonxai.xsd.Element xsdElement = this.convertElement(rootNodes, element);

                // Add to schema root
                xsd.getElementSymbolTable().updateOrCreateReference(
                    xsdElement.getName(),
                    xsdElement
                );
                xsd.addElement(xsd.getElementSymbolTable().getReference(xsdElement.getName()));

                //Convert constraints
                LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Constraint> xsdConstraints = this.convertConstraints(element);

                // Append the converted constraints to the XSD element
                for (de.tudortmund.cs.bonxai.xsd.Constraint currentConstraint : xsdConstraints) {
                    xsdElement.addConstraint(currentConstraint);
                }
            } else {
                // Recurse
                convert(rootNodes, node);
            }
        }
    }

    /**
     * Convert Constraints.
     *
     * Key, KeyRef and Unique constraints are converted from Bonxai to its XSD
     * counterpart.
     *
     * KeyRefs and Uniques become new full qualified unique names. Keys already
     * have a full qualified name (discussion pending).
     *
     * Keys will be registered in the global keySymbolTable of the xsd, so that
     * KeyRefs find their References in there and append them to their content.
     *
     * Selectors and fields will be converted directly.
     *
     * @param elementTreeNode
     * @return resultXsdConstraints - converted constraints in a
     *                       LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Constraint>
     *
     */
    protected LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Constraint> convertConstraints(ElementTreeNode elementTreeNode) {
        LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Constraint> resultXsdConstraints = new LinkedHashSet<de.tudortmund.cs.bonxai.xsd.Constraint>();
        for (de.tudortmund.cs.bonxai.bonxai.Constraint currentBonxaiConstraint : elementTreeNode.getConstraints()) {

            if (currentBonxaiConstraint instanceof KeyConstraint) {
                /**
                 * @TODO: Discussion pending. See mailinglist.
                 *
                 * !key.name has to be unique!
                 * It will be used for xsd.Key.Name, too.
                 *
                 * -- I hope this will be checked by the bonxai-parser. --
                 *
                 * Otherwise, we have to generate new names, but in this case
                 * all KeyRef.reference- attributes has to be changed.
                 *
                 * Walk across the global constraint list and change the names
                 * from the old value to the unique-new-one.
                 *
                 */

                /**
                 * @TODO; Second Task: name is "full qualified" ?
                 */

                // Case not "full qualified" --> add the right Namespace to the Key Name
//                String keyName = "{" + elementTreeNode.getNamespace() + "}" + ((KeyConstraint) currentBonxaiConstraint).getName();

                // Case "full qualified" --> the name can be used directly
                String keyName = ((KeyConstraint) currentBonxaiConstraint).getName();

                de.tudortmund.cs.bonxai.xsd.Key xsdKeySimpleConstraint = new de.tudortmund.cs.bonxai.xsd.Key(
                        keyName,
                        ((KeyConstraint) currentBonxaiConstraint).getConstraintSelector());

                XSDSchema xsd  = this.getSchema(elementTreeNode.getNamespace());
                xsd.getKeyAndUniqueSymbolTable().updateOrCreateReference(keyName,xsdKeySimpleConstraint);

                // The fields will directly be used in xsd like in bonxai.
                for (String currentField : ((KeyConstraint) currentBonxaiConstraint).getConstraintFields()) {
                    xsdKeySimpleConstraint.addField(currentField);
                }
                resultXsdConstraints.add(xsdKeySimpleConstraint);

            } else if (currentBonxaiConstraint instanceof KeyRefConstraint) {

                XSDSchema xsd  = this.getSchema(elementTreeNode.getNamespace());

                // @TODO: What KeyRef has to be set into the parameter list here?
                // The following might be right.

                // The reference has to comply with the right name of the key!
                //(full or not full qualified!)
                SymbolTableRef<SimpleConstraint> keyRef = xsd.getKeyAndUniqueSymbolTable().getReference(((KeyRefConstraint) currentBonxaiConstraint).getReference());

                de.tudortmund.cs.bonxai.xsd.KeyRef xsdKeyRefSimpleConstraint = new de.tudortmund.cs.bonxai.xsd.KeyRef(
                        getFullQualifiedUniqueConstraintName(elementTreeNode.getNamespace(), "keyref_" + elementTreeNode.getName(), currentBonxaiConstraint),
                        ((KeyRefConstraint) currentBonxaiConstraint).getConstraintSelector(),
                        keyRef);

                // The fields will directly be used in xsd like in bonxai.
                for (String currentField : ((KeyRefConstraint) currentBonxaiConstraint).getConstraintFields()) {
                    xsdKeyRefSimpleConstraint.addField(currentField);
                }
                resultXsdConstraints.add(xsdKeyRefSimpleConstraint);

            } else if (currentBonxaiConstraint instanceof UniqueConstraint) {

                de.tudortmund.cs.bonxai.xsd.Unique xsdUniqueSimpleConstraint = new de.tudortmund.cs.bonxai.xsd.Unique(
                        getFullQualifiedUniqueConstraintName(elementTreeNode.getNamespace(), "unique_" + elementTreeNode.getName(), currentBonxaiConstraint),
                        ((UniqueConstraint) currentBonxaiConstraint).getConstraintSelector());

                // The fields will directly be used in xsd like in bonxai.
                for (String currentField : ((UniqueConstraint) currentBonxaiConstraint).getConstraintFields()) {
                    xsdUniqueSimpleConstraint.addField(currentField);
                }
                resultXsdConstraints.add(xsdUniqueSimpleConstraint);
            }
        }
        return resultXsdConstraints;
    }
}
