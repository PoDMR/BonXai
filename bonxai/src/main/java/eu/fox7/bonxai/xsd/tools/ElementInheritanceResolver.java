package eu.fox7.bonxai.xsd.tools;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.Element.*;
import eu.fox7.bonxai.xsd.XSDSchema.*;

import java.util.*;

/**
 * Class used to resolve element inheritance in a given schema. Element
 * inheritance can be achieved in XML XSDSchema through the use of the
 * "substitutionGroup" attribute of an element component. So if all these
 * "substitutionGroup" attributes are removed the element inheritance is
 * removed. The schema after resolving the element inheritance will be
 * equivalent to the schema before. Element inheritance is also removed from
 * all contained schemata of the current schema, where the schema is the root
 * of the schema structure.
 *
 * @author Dominik Wolff
 */
public class ElementInheritanceResolver extends ResolverTool {

    // List containing all schemata contained under the current schema.
    private LinkedHashSet<XSDSchema> schemata = null;

    // Initialize substitutionMap mapping head elements to elements the can be substituted by.
    private HashMap<Element, LinkedHashSet<Element>> substitutionMap = new HashMap<Element, LinkedHashSet<Element>>();

    // Initialize elementsWithSubstitutionGroup set, containing only elements with "substitutionGroup" attribute
    private LinkedHashSet<Element> elementsWithSubstitutionGroup = new LinkedHashSet<Element>();

    // Initialize elementsWithSubstitutionGroupSchemaMap which maps elements with "substitutionGroup" attribute to schemata containing them.
    private HashMap<Element, XSDSchema> elementsWithSubstitutionGroupSchemaMap = new HashMap<Element, XSDSchema>();

    // Initialize HashMap mapping head element references to schemata containing them.
    private HashMap<ElementRef, XSDSchema> headElementRefsSchemaMap = new HashMap<ElementRef, XSDSchema>();

    // Initialize HashMap mapping head elements to schemata containing them.
    private HashMap<Element, XSDSchema> headElementSchemaMap = new HashMap<Element, XSDSchema>();

    // Initialize HashMap mapping types to schemata containing them.
    private HashMap<Type, XSDSchema> typeSchemaMap = new HashMap<Type, XSDSchema>();

    // Initialize HashMap mapping all pattern to components containing them.
    private HashMap<AllPattern, Object> allPatternContainingComponentMap = new HashMap<AllPattern, Object>();

    // Initialize set containing all schemata changed due to the element inheritance resolving process
    LinkedHashSet<XSDSchema> changedSchemata = new LinkedHashSet<XSDSchema>();

    /**
     * Constructor method of the ElementInheritanceResolver class. The specified
     * schema is used to remove element inheritance from this schema and all
     * schemata contained in it (through include/redefine/import components).
     *
     * @param schema XSDSchema for which all element inheritance is resolved.
     */
    public ElementInheritanceResolver(XSDSchema schema) {

        // Get all schemata contained under the current schema
        schemata = getSchemata(schema);
    }

    /**
     * This method removes every "substitutionGroup" attribute from the schema
     * and the set of contained schemata. In order to remove a
     * "substitutionGroup" attribute a set of insertable elements for each
     * substitution head element must be computed. These elements can be used
     * as substitutions for the head element if the head element is referenced
     * to. So each of these references has to be replaced by a choice pattern
     * containing references to these elements. Because it is possible to
     * reference elements which are not contained in the current schema
     * and haven no import/include component and a corresponding namespace
     * abbreviation in the current schema these things have to be added to the
     * schema if needed.
     *
     * @return Set of schemata.
     */
    public LinkedHashSet<XSDSchema> resolveSubstitutionGroups() {

        // Before starting the resolving process all fields with exception of the schemata field are cleared.
        substitutionMap.clear();
        elementsWithSubstitutionGroupSchemaMap.clear();
        elementsWithSubstitutionGroup.clear();
        headElementRefsSchemaMap.clear();
        typeSchemaMap.clear();
        allPatternContainingComponentMap.clear();
        changedSchemata.clear();

        // Update substitutionMap in order to have all substitution heads and their child elements
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema currentSchema = it.next();

            // Get all top-level elements of the current schema
            LinkedList<Element> topLevelElements = currentSchema.getElements();

            // Add for each top-level head element of the current schema an entry to the substitutionMap
            for (Iterator<Element> it2 = topLevelElements.iterator(); it2.hasNext();) {
                Element topLevelElement = it2.next();

                // Get elements which can be inserted for the top-level element
                LinkedHashSet<Element> substitutionElements = getSubstitutionElements(topLevelElement, currentSchema);

                // Only add head elements and their insertable elements to the substitutionMap when at least more elements than the element itself are present in the insertable element set
                if (substitutionElements.size() > 1) {
                    substitutionMap.put(topLevelElement, substitutionElements);

                    // For each head element the containing schema is stored
                    headElementSchemaMap.put(topLevelElement, currentSchema);
                }

                // If the top-level attribute contains a "substitutionGroup" attribute add it to the set of elements with "substitutionGroup" attribute
                if (topLevelElement.getSubstitutionGroup() != null) {
                    elementsWithSubstitutionGroup.add(topLevelElement);

                    // Update the entry of the elementsWithSubstitutionGroupSchemaMap for the current head element
                    elementsWithSubstitutionGroupSchemaMap.put(topLevelElement, currentSchema);
                }
            }
        }

        // Get headElementRefParticleContainerMap mapping element references of head elements to particle containers containing these references
        LinkedHashMap<ElementRef, ParticleContainer> headElementRefParticleContainerMap = getHeadElementRefs();

        // Remove "substitutionGroup" attribute from each element which contained it
        removeSubstitutionAttributes();

        // Each head element reference has to be replaced with a choice of all insertable elements for the given head element.
        for (Iterator<ElementRef> it2 = headElementRefParticleContainerMap.keySet().iterator(); it2.hasNext();) {
            ElementRef headElementRef = it2.next();

            // Replace current head element reference with an equivalent choice pattern
            replaceHeadElementRef(headElementRef, headElementRefParticleContainerMap.get(headElementRef));
        }

        // Return set of changed schemata
        return changedSchemata;
    }

    /**
     * This method computes a set of elements which can be used as substitutes
     * for the specified head element. In other words these elements are
     * children of the specified element. To find these child elements all
     * schemata contained in the given schema are traversed and top-level
     * elements with "substitutionGroup" attribute added to the set if they
     * refernce the head element itself or a child element of the head element.
     * If the head element contains a "block" attribute or if not the schema
     * contains a "blockDefault" attribute only elements whose inheritance is
     * not blocked are added to the set.
     *
     * The set will contain abstract defined elements, these elements can not
     * directly be inserted for the head element but are important for the
     * inheritance tree.
     *
     * @param headElement Element for which all applicable insertable are
     * computed.
     * @param currentSchema XSDSchema containing the current head element and
     * hence contains the "blockDefault" value needed if the element has no
     * "block" attribute.
     * @return Set of elements which can be used as substitutes for the
     * specified head element.
     */
    private LinkedHashSet<Element> getSubstitutionElements(Element headElement, XSDSchema currentSchema) {

        // Get the "block" value of the head element
        HashSet<Block> blockValue = headElement.getBlockModifiers();

        // If "block" attribute is not present in head element check schema defaults
        if (blockValue == null) {

            blockValue = new HashSet<Block>();

            // Transform schema BlockDefault objects into Block objects
            if (currentSchema.getBlockDefaults().contains(BlockDefault.extension)) {
                blockValue.add(Block.extension);
            }
            if (currentSchema.getBlockDefaults().contains(BlockDefault.restriction)) {
                blockValue.add(Block.restriction);
            }
            if (currentSchema.getBlockDefaults().contains(BlockDefault.substitution)) {
                blockValue.add(Block.substitution);
            }
        }

        // Set of elements which can replace the specified head element (head element itself is in the set)
        LinkedHashSet<Element> substitutionElements = new LinkedHashSet<Element>();
        substitutionElements.add(headElement);

        // Check if block blocks substitution
        if (blockValue != null && !blockValue.contains(Block.substitution)) {

            // Get element stack and add head element to stack
            Stack<Element> stack = new Stack<Element>();
            stack.add(headElement);

            // As long as the stack contains an element check if this element is substitution head
            while (!stack.isEmpty()) {
                Element currentHeadElement = stack.pop();

                // Check all schemata for possible child elements of the current head element and add them if possible (check block) to the set and stack
                for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
                    XSDSchema schema = it.next();

                    // Check if current schema has a SymbolTableRef for the current head element and if this SymbolTableRef is contained in the substitutionElements HashMap of the schema
                    if (schema.getElementSymbolTable().hasReference(currentHeadElement.getName()) && schema.isSubstitutionHead(schema.getElementSymbolTable().getReference(currentHeadElement.getName()))) {

                        // Get a set of top-level elements each element specifies the current head element as direct parent.
                        HashSet<Element> currentSubstitutionElements = schema.getSubstitutionElements(schema.getElementSymbolTable().getReference(currentHeadElement.getName()));

                        // Check for each possible substitution element if it is not blocked
                        for (Iterator<Element> it2 = currentSubstitutionElements.iterator(); it2.hasNext();) {
                            Element currentSubstitutionElement = it2.next();

                            // Check if current substitution element has an allowed inheritance variant
                            if (isValidSubstitutionElement(currentSubstitutionElement, blockValue)) {
                                substitutionElements.add(currentSubstitutionElement);
                                stack.add(currentSubstitutionElement);
                            }
                        }
                    }
                }
            }
        }

        // Return set of substitution elements for the head element
        return substitutionElements;
    }

    /**
     * This method checks the type of a specified element. Using element
     * inheritance it is allowed to omit type definitions for elements. These
     * elements inherit the types of their parents. In order to find these types
     * the parents of the current element are traversed until one element has a
     * type definition.
     *
     * @param element Element for which the type is checked.
     * @return Type of the specified element.
     */
    private Type getSubstitutionType(Element element) {

        // Check whether the type of the element is the "xs:anyType" and if the element has a substitution head
        if (element.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}anyType") && element.getSubstitutionGroup() != null) {

            // Check head element for substitution type
            return getSubstitutionType(element.getSubstitutionGroup().getReference());
        }

        // Return the type of the element if it has no head element
        return element.getType();
    }

    /**
     * This getHeadElementRefs method gets all element references referring to
     * head elements found in the set of schemata. This is done by checking all
     * groups and types in each schema. In order to check the particles 
     * contained in types and groups another getHeadElementRefs method for 
     * particles is used. 
     * 
     * @return This mehtod does not return a set of ElementRefs but a HashMap
     * mapping ElementRefs to particle containers. This is useful to add new
     * choice with element references to the particle container.
     */
    private LinkedHashMap<ElementRef, ParticleContainer> getHeadElementRefs() {

        // Initialize map mapping element references of head elements to parctilce containers containing these references
        LinkedHashMap<ElementRef, ParticleContainer> headElementRefParticleContainerMap = new LinkedHashMap<ElementRef, ParticleContainer>();

        // Check each schema for element references
        for (Iterator<XSDSchema> it = schemata.iterator(); it.hasNext();) {
            XSDSchema currentSchema = it.next();

            // Check each group for element references
            for (Iterator<Group> it2 = currentSchema.getGroups().iterator(); it2.hasNext();) {
                Group currentGroup = it2.next();

                // Check if group contains an all pattern
                if (currentGroup.getParticleContainer() instanceof AllPattern) {
                    allPatternContainingComponentMap.put((AllPattern) currentGroup.getParticleContainer(), currentGroup);
                }

                // Update the headElementRefParticleContainerMap with new head element refs and containers
                headElementRefParticleContainerMap.putAll(getHeadElementRefs(currentGroup.getParticleContainer(), null, currentSchema));
            }

            // Check for each type (ask SymbolTable for all types) if the type is a complexType with complexContent containing element references
            for (Iterator<SymbolTableRef<Type>> it2 = currentSchema.getTypeSymbolTable().getReferences().iterator(); it2.hasNext();) {
                Type type = it2.next().getReference();

                // For each type the schema containing the type is stored.
                typeSchemaMap.put(type, currentSchema);

                // Check if type is a complexType
                if (type instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) type;

                    // Check if complexType contains complexContent
                    if (complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                        // Check if group contains an all pattern
                        if (complexContentType.getParticle() instanceof AllPattern) {
                            allPatternContainingComponentMap.put((AllPattern) complexContentType.getParticle(), complexContentType);
                        }

                        // Update the headElementRefParticleContainerMap with new head element refs and containers
                        headElementRefParticleContainerMap.putAll(getHeadElementRefs(complexContentType.getParticle(), null, currentSchema));
                    }
                }
            }
        }

        // Return map with all found head element references
        return headElementRefParticleContainerMap;
    }

    /**
     * This getHeadElementRefs method gets all element references referring to
     * head elements found in the specified particle. In order to find all
     * element references the particle is traversed recursively. If an element
     * reference was found it is checked that the refered element is contained
     * in the substitutionMap hence a substitution head. For particle container
     * contained in the particle the parentParticleContainer parameter is used
     * to add valid parents for element references.
     *
     * @param particle Particle may be an ElementRef or a ParticleContainer all
     * other forms of particles are ignored.
     * @param parentParticleContainer Container containing the current particle.
     * @param currentSchema XSDSchema containing the current particle and
     * particle container.
     * @return HashMap mapping ElementRefs referring to substitution head
     * elements to ParticleContainers containing these references.
     */
    private LinkedHashMap<ElementRef, ParticleContainer> getHeadElementRefs(Particle particle, ParticleContainer parentParticleContainer, XSDSchema currentSchema) {

        // Initialize map mapping element references of head elements to parctilce containers containing these references
        LinkedHashMap<ElementRef, ParticleContainer> headElementRefParticleContainerMap = new LinkedHashMap<ElementRef, ParticleContainer>();

        // Check if particle is an ElementRef or ...
        if (particle instanceof ElementRef) {
            ElementRef elementRef = (ElementRef) particle;

            // If substitutionMap contains an entry for the reference element an new entry in the headElementRefParticleContainerMap is made for this element reference and it is added to the headElementRefsSchemaMap
            if (substitutionMap.containsKey(elementRef.getElement())) {
                headElementRefParticleContainerMap.put(elementRef, parentParticleContainer);
                headElementRefsSchemaMap.put(elementRef, currentSchema);
            }

        // Check whether the particle is a particle container.
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // For each particle of the particle container check if it contains element references
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle currentParticle = it.next();

                // Search new particle for valid element references (the parent particle container is now the current particle container)
                headElementRefParticleContainerMap.putAll(getHeadElementRefs(currentParticle, particleContainer, currentSchema));
            }
        }

        // After traversing the particle return complete map
        return headElementRefParticleContainerMap;
    }

    /**
     * Checks whether an element is a valid substitution element for a head
     * element with given "block" value. In order to check this the type of the
     * current element has to be checked. If the type contains a blocked
     * inheritance variant the element is not a valid substitution element.
     *
     * @param currentElement Element for which the validity is checked.
     * @param blockValue "block" value of a head element. This head element is
     * either the parent of the current element or a parent of the current
     * element in the inheritance structure.
     * @return <tt>true</tt> if the element can be used as substitute for the
     * head element else <tt>false</tt>.
     */
    private boolean isValidSubstitutionElement(Element currentElement, HashSet<Block> blockValue) {

        // Get the type of the specified current element
        Type currentType = getSubstitutionType(currentElement);

        // Check if current type is a complexType or ...
        if (currentType instanceof ComplexType) {
            ComplexType currentComplexType = (ComplexType) currentType;

            // Check if the current complexType contains complexContent or ...
            if (currentComplexType.getContent() instanceof ComplexContentType) {
                ComplexContentType complexContentType = (ComplexContentType) currentComplexType.getContent();

                // Check if inheritance is an extension or ...
                if (complexContentType.getInheritance() instanceof ComplexContentExtension) {

                    // If extension is blocked false is returned else true
                    if (blockValue.contains(Block.extension)) {
                        return false;
                    } else {
                        return true;
                    }

                // Check if inheritance is a restriction.
                } else if (complexContentType.getInheritance() instanceof ComplexContentRestriction) {

                    // If restriction is blocked false is returned else true
                    if (blockValue.contains(Block.restriction)) {
                        return false;
                    } else {
                        return true;
                    }
                }

            // Check if the current complexType contains simpleContent.
            } else if (currentComplexType.getContent() instanceof SimpleContentType) {
                SimpleContentType simpleContentType = (SimpleContentType) currentComplexType.getContent();

                // Check if inheritance is an extension or ...
                if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                    // If extension is blocked false is returned else true
                    if (blockValue.contains(Block.extension)) {
                        return false;
                    } else {
                        return true;
                    }

                // Check if inheritance is a restriction.
                } else if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {

                    // If restriction is blocked false is returned else true
                    if (blockValue.contains(Block.restriction)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }

        // Check if the current type is a simpleType.
        } else if (currentType instanceof SimpleType) {
            SimpleType currentSimpleType = (SimpleType) currentType;

            // Check if inheritance is an restriction (their is no simpleType extension).
            if (currentSimpleType.getInheritance() instanceof SimpleContentRestriction) {

                // If restriction is blocked false is returned else true
                if (blockValue.contains(Block.restriction)) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        // If the type has no inheritance true is returned (for a valid schema this is only possible if the type of the head element is inherited)
        return true;
    }

    /**
     * This method removes "substitutionGroup" attributes from all elements
     * contained in the set of schemata contained in the schema given to the
     * constructor. Each top-level element which contain a "substitutionGroup"
     * attribute is processed by this method. The "substitutionGroup" attribute
     * is removed and if necessary a new type for the element is provided, in
     * case the element inherited the type of the head element.
     */
    private void removeSubstitutionAttributes() {

        // Remove "substitutionGroup" attribute from each element contained in the elementsWithSubstitutionGroup set and update types for elements which inherit their types from head elements.
        for (Iterator<Element> it = elementsWithSubstitutionGroup.iterator(); it.hasNext();) {
            Element element = it.next();

            // Get type of the current element
            Type elementType = getSubstitutionType(element);

            // Get schema that contains the current elemet as top-level element
            XSDSchema currentSchema = elementsWithSubstitutionGroupSchemaMap.get(element);

            // The element in the current schema 
            changedSchemata.add(currentSchema);

            // If the type of the element is another type than the registered type the type is inherited from a parent.
            if (elementType != element.getType()) {

                // If current schema does not contain the type reference add it to the type SymbolTable of the current schema
                if (!currentSchema.getTypeSymbolTable().hasReference(elementType.getName())) {
                    currentSchema.getTypeSymbolTable().updateOrCreateReference(elementType.getName(), elementType);

                    // If the type is anonymous change type to non anonymous type
                    if (elementType.isAnonymous()) {
                        elementType.setIsAnonymous(false);

                        // Check if current element contains the type definition
                        for (Iterator<Element> it2 = elementsWithSubstitutionGroupSchemaMap.keySet().iterator(); it2.hasNext();) {
                            Element currentElement = it2.next();

                            // Make type top-level
                            if (currentElement.getType() == elementType) {
                                elementsWithSubstitutionGroupSchemaMap.get(element).addType(elementsWithSubstitutionGroupSchemaMap.get(element).getTypeSymbolTable().getReference(elementType.getName()));
                            }
                        }

                        // Set block to value ##all
                        if (elementType instanceof ComplexType) {
                            ComplexType complexType = (ComplexType) elementType;
                            complexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
                            complexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
                        }
                    }

                    // Add import/include component and namespace abbreviation to current schema because type reference did not exist and type is not anonymous
                    supportReference(currentSchema, typeSchemaMap.get(elementType));
                }

                // Add a "type" attribute to the current element
                element.setTypeAttr(true);
            }

            // Remove "substitutionGroup" attribute from current element
            element.setSubstitutionGroup(null);
        }
    }

    /**
     * Replaces the specified head element reference in the given particle
     * container with a choice pattern containing element references to all
     * elements which can substitute for the head element. Abstract defined
     * elements are ignored because they can not appear elsewhere in the schema.
     * In order to reference elements contained in foreign schemata new
     * include/import components and namespace abbreviations are needed if
     * not already present in the current schema.
     *
     * @param headElementRef Reference which is replaced by a new choice
     * pattern.
     * @param particleContainer Container containing the specified element
     * reference.
     */
    private void replaceHeadElementRef(ElementRef headElementRef, ParticleContainer particleContainer) {

        // Check if the particle container is an all pattern, which has to be resolved in order for a choice pattern to be contained in it
        if (particleContainer instanceof AllPattern) {

            // Create new choice pattern
            ChoicePattern choicePattern = new ChoicePattern();

            // Get particles contained in the current all pattern
            Particle[] containedParticle = (Particle[]) particleContainer.getParticles().toArray();

            // Add all possible particle sequences to the choice pattern
            choicePattern.setParticles(permutateAllPattern(particleContainer.getParticles().size() - 1, containedParticle));

            // Get content or group containing the all pattern
            Object object = allPatternContainingComponentMap.get(particleContainer);

            // Check if the oject is a complex content or a group
            if (object instanceof ComplexContentType) {
                ComplexContentType complexContentType = (ComplexContentType) object;

                // Set resolved all pattern as new particle for the content
                complexContentType.setParticle(choicePattern);

            } else if (object instanceof Group) {
                Group group = (Group) object;

                // Set resolved all pattern as new particle container for the group
                group.setContainer(choicePattern);
            }
        }

        // Get a set of all elements which can replace the specified head element (head element itself is in the set)
        LinkedHashSet<Element> substitutionElements = substitutionMap.get(headElementRef.getElement());

        // Create new ChoicePattern, which is used to replace the ElementRef
        ChoicePattern newChoicePattern = new ChoicePattern();

        // Add for each element contained in the substitutionElements set a reference to this element to the choice pattern
        for (Iterator<Element> it = substitutionElements.iterator(); it.hasNext();) {
            Element substitutionElement = it.next();

            // Check whether the substitution element is defined abstract if not a new ElementRef to this element is generated and added to the choice pattern
            if (!substitutionElement.getAbstract()) {

                // If the head element is the current substitution element add the head element reference to the choice
                if (substitutionElement == headElementRef.getElement()) {
                    newChoicePattern.addParticle(headElementRef);

                // Else generate a new ElementRef to the current substitution element
                } else {

                    // Get the schema in which the head element reference is contained
                    XSDSchema currentSchema = headElementRefsSchemaMap.get(headElementRef);

                    // If current schema does not contain the element reference add it to the element SymbolTable of the current schema
                    if (!currentSchema.getElementSymbolTable().hasReference(substitutionElement.getName())) {
                        currentSchema.getElementSymbolTable().updateOrCreateReference(substitutionElement.getName(), substitutionElement);

                        // Add import/include component and namespace abbreviation to current schema because element reference did not exist
                        supportReference(currentSchema, headElementSchemaMap.get(headElementRef.getElement()));
                    }

                    // Create new element reference with new element SymbolTabelRef and add it to the choice pattern
                    ElementRef newElementRef = new ElementRef(currentSchema.getElementSymbolTable().getReference(substitutionElement.getName()));
                    newChoicePattern.addParticle(newElementRef);
                }
            }
        }

        // Get particles contained in the particle container and add new choice pattern at the position of the head element reference
        LinkedList<Particle> particleContainerParticles = particleContainer.getParticles();
        particleContainerParticles.add(particleContainerParticles.indexOf(headElementRef), newChoicePattern);

        // Remove head element reference (this reference may be contained in the choice pattern) and set new particle list in the particle container
        particleContainerParticles.remove(headElementRef);
        particleContainer.setParticles(particleContainerParticles);

        // XSDSchema was changed so add schema to changed schema list
        changedSchemata.add(headElementRefsSchemaMap.get(headElementRef));
    }

    /**
     * Type and element refernces can only be used in a current schema if the
     * schema containing these types and elements definitions is refernced in
     * the current schema. If this is not the case new schema references in the
     * form of import/include components are needed for the current schema. If
     * a foreign schema is included in the schema a new namespace abbreviation
     * is not necessary because the foreign schema shares its target namespace
     * with the current schema, but if it is imported a new namespace
     * abbreviation is necessary to refernce compontent contained in this
     * namespace. This method adds import/include components and namespace
     * abbreviations to the current schema if necessary in order to support the
     * reference.
     * @param currentSchema XSDSchema to which the import/include component and
     * namespace abbreviation is added.
     * @param referencedSchema XSDSchema containing the target of the reference.
     * Its target namespace and location is needed in order to construct the
     * new components.
     */
    private void supportReference(XSDSchema currentSchema, XSDSchema referencedSchema) {

        // Check all foreign schemata of the current schema whether the referenced schema is already included/imported
        boolean isAlreadyReferenced = false;
        for (Iterator<ForeignSchema> it = currentSchema.getForeignSchemas().iterator(); it.hasNext();) {
            ForeignSchema foreignSchema = it.next();

            // If the foreign schema references to the referenced schema nothing has to be done
            if (foreignSchema.getSchema() != null && foreignSchema.getSchema() == referencedSchema) {
                isAlreadyReferenced = true;
            }
        }

        // If the referenced schema is not imported or included in the current schema a new foreign schema and namespace abbreviation have to be created.
        if (!isAlreadyReferenced) {

            // If both the current schema and the refernced schema have the same target namespace only an include component is necessary.
            if (currentSchema.getTargetNamespace().equals(referencedSchema.getTargetNamespace())) {

                // Create new included schema with schema location, schema and parent schema
                IncludedSchema includedSchema = new IncludedSchema(referencedSchema.getSchemaLocation());
                includedSchema.setSchema(referencedSchema);
                includedSchema.setParentSchema(currentSchema);

                // Add new included schema to current schema
                currentSchema.addForeignSchema(includedSchema);

            // Referenced schema is not imported or included in the current schema and has another target namespace so a new import component is generated
            } else {

                // Create new imported schema with schema location, schema and parent schema
                ImportedSchema importedSchema = new ImportedSchema(referencedSchema.getTargetNamespace(), referencedSchema.getSchemaLocation());
                importedSchema.setSchema(referencedSchema);
                importedSchema.setParentSchema(currentSchema);

                // Add new imported schema to current schema
                currentSchema.addForeignSchema(importedSchema);

                // Create set of used namespace abbreviations
                LinkedHashSet<String> abbreviations = new LinkedHashSet<String>();

                // Add for each IdentifiedNamespace an abbreviation to the set
                for (Iterator<IdentifiedNamespace> it = currentSchema.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
                    IdentifiedNamespace identifiedNamespace = it.next();
                    abbreviations.add(identifiedNamespace.getIdentifier());
                }

                // Create new namespaces
                IdentifiedNamespace identifiedNamespace = null;

                // Finde new abbreviation for namespace
                int number = 1;
                while (identifiedNamespace == null) {
                    String abbreviation = "ns" + number;

                    // If abbreviation is not used in current schema construct new IdentifiedNamespace
                    if (currentSchema.getNamespaceList().getNamespaceByIdentifier(abbreviation).getUri() == null) {
                        identifiedNamespace = new IdentifiedNamespace(abbreviation, referencedSchema.getTargetNamespace());
                    }
                    number++;
                }

                // Add new namespace with abbreviation to current schema
                currentSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
            }
        }
    }

    /**
     * Used to create a list of sequence patterns representing all sequence
     * patterns which match the all pattern for which the list is generated. In
     * order to create the list the particles contained in the all pattern are
     * permutated using a divide and conquere approach. The resulting list
     * can be used by a choice pattern which is then equivalent to the all
     * pattern.
     *
     * @param particleNumber Current number of the permutated particle.
     * @param particleArray Array containing all particles of an all pattern.
     * @return List of sequence patterns which can be used to by a choice
     * pattern.
     */
    private LinkedList<Particle> permutateAllPattern(int particleNumber, Particle[] particleArray) {

        // Linked list of particles which will be contained by a choice pattern
        LinkedList<Particle> choiceParticles = new LinkedList<Particle>();

        // If the particle number is zero
        if (particleNumber == 0) {

            // Create new sequence pattern and add all particles of the current particle array to the sequence pattern
            SequencePattern sequencePattern = new SequencePattern();
            for (Particle particle : particleArray) {
                sequencePattern.addParticle(particle);
            }

            // Add sequence pattern to particle set for the choice pattern
            choiceParticles.add(sequencePattern);
        } else {

            // Calculate permutations for fraction problems
            for (int i = particleNumber; 0 <= i; i--) {

                // Switch particle at position i with particle at position particleNumber
                Particle particle = particleArray[i];
                particleArray[i] = particleArray[particleNumber];
                particleArray[particleNumber] = particle;

                // Generate permutation for a smaller particle number and add results to the particle list
                choiceParticles.addAll(permutateAllPattern(particleNumber - 1, particleArray));

                // Switch particle at position i with particle at position particleNumber
                particle = particleArray[i];
                particleArray[i] = particleArray[particleNumber];
                particleArray[particleNumber] = particle;
            }
        }

        // Return list of sequence patterns, which can be used by a choice particle
        return choiceParticles;
    }
}