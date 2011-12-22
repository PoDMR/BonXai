package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Element.Block;
import eu.fox7.bonxai.xsd.Group;

import java.util.*;

/*******************************************************************************
 * EDCChecker for checking the EDC constraint of XSD.
 *
 * http://www.w3.org/TR/xmlschema-1/#cos-element-consistent
 *
 * XSDSchema Component Constraint: Element Declarations Consistent
 * If the {particles} contains, either directly, indirectly (that is, within the
 * {particles} of a contained model group, recursively) or implicitly two or
 * more element declaration particles with the same {name} and
 * {target namespace}, then all their type definitions must be the same
 * top-level definition, that is, all of the following must be true:
 * 
 * 1 all their {type definition}s must have a non-absent {name}.
 * 2 all their {type definition}s must have the same {name}.
 * 3 all their {type definition}s must have the same {target namespace}.
 *
 * [Definition:]  A list of particles implicitly contains an element declaration
 * if a member of the list contains that element declaration in its substitution
 * group.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class EDCChecker {

    // XSDSchema for which the EDC constraint is checked
    private XSDSchema schema;

    /**
     * Constructor for the EDCChecker getting the schema which should be
     * checked
     * @param schema
     */
    public EDCChecker(XSDSchema schema) {
        this.schema = schema;
    }

    /**
     * Checks if the EDC constraint of XML XSDSchema is valid for the specified
     * schema. In order to do this, all content models are checked for elements
     * with same name and different types. Content models can only appear in
     * groups and complexTypes.
     *
     * @return  True if the schema is valid with respect to the EDC constraint
     */
    public boolean isValid() {
        LinkedList<Element> list = new LinkedList<Element>();

        // Check for all groups of the schema the EDC constraint
        for (Group group : schema.getGroups()) {

            // Build list containing all elements of the current group content model
            list = processParticleContainer(group.getParticleContainer());

//            System.out.println("Group: " + group.getLocalName() + list);

            // Check if list contains element with same name and different types
            if (checkListsForDuplicateElements(list)) {
                return false;
            }
        }

        // Check for all types of the schema the EDC constraint
        for (Type type : schema.getTypes()) {

            // Only complexTypes have content models
            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;

                if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                    // To test complexType extension all base content models have to be in one sequence
                    if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {
                        SequencePattern sequencePattern = new SequencePattern();
                        ComplexContentExtension currentComplexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                       
                        // Boolean variable breakBool signals the end of the inheritance structure
                        boolean breakBool = false;
                        do {
                            // Add own content model to sequence
                            sequencePattern.addParticle(complexContentType.getParticle());

                            // Check base: If base has no content model set break
                            complexType = ((ComplexType) currentComplexContentExtension.getBase());
                            if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {
                                complexContentType = (ComplexContentType) complexType.getContent();

                                // If base is an extension itself currentComplexContentExtension is set to new extension else the content model of the base is added to sequence
                                if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {
                                    currentComplexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                                } else {
                                    sequencePattern.addParticle(complexContentType.getParticle());
                                    breakBool = true;
                                }
                            } else {
                                breakBool = true;
                            }
                        } while (currentComplexContentExtension.getBase() != null && currentComplexContentExtension.getBase() instanceof ComplexType && !breakBool);
                        
                        // List contains complexType extension content model
                        list = processParticleContainer(sequencePattern);
                    } else {

                        // In the case of a complexType with no inheritance traverse particles
                        if (complexContentType.getParticle() != null) {
                            if (complexContentType.getParticle() instanceof ParticleContainer) {
                                list = processParticleContainer((ParticleContainer) complexContentType.getParticle());
                            } else if (complexContentType.getParticle() instanceof GroupRef) {
                                Group group = (Group) ((GroupRef) complexContentType.getParticle()).getGroup();
                                list = processParticleContainer(group.getParticleContainer());
                            }
                        }
                    }
                    
                    // Check type content model for duplicate elements
                    if (checkListsForDuplicateElements(list)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check if a list of elements contains two elements with same name and
     * different types.
     *
     * @param currentList       List of elements representing a content model.
     * @return                  True if an invalid element pair exists.
     */
    private boolean checkListsForDuplicateElements(LinkedList<Element> currentList) {
        // Traverse currentList with two pointers to check each element against all other elements
        for (int i = 0; i < currentList.size(); i++) {
            for (int j = i + 1; j < currentList.size(); j++) {
                Element element = currentList.get(i);
                Element otherElement = currentList.get(j);

                // Compare two elements with same names to check if types are different
                if (element.getName().equals(otherElement.getName())) {
                    if (element.getType() != otherElement.getType()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Traverse a particle structure to generate a list of contained elements.
     *
     * @param particleContainer         Particle which is the root of the
     *                                  traversed particle structure.
     * @return                          List of elements contained in the
     *                                  particle structure.
     */
    private LinkedList<Element> processParticleContainer(ParticleContainer particleContainer) {
        LinkedList<Element> list = new LinkedList<Element>();
        
        for (Particle particle : particleContainer.getParticles()) {
            LinkedList<Element> result = new LinkedList<Element>();

            if (particle instanceof ParticleContainer) {
                // Case: ParticleContainer
                result = processParticleContainer((ParticleContainer) particle);

            } else if (particle instanceof GroupRef) {
                // Case: GroupRef
                Group group = (Group) ((GroupRef) particle).getGroup();
                result = processParticleContainer(group.getParticleContainer());

            } else if (particle instanceof Element) {
                // Case: Element
                result = processElement((Element) particle);

            } else if (particle instanceof ElementRef) {
                // Case: ElementRef
                Element element = ((ElementRef) particle).getElement();
                result = processElement(element);
            }
            list.addAll(result);
        }
        return list;
    }

    /**
     *
     * @param element
     * @return
     */
    private LinkedList<Element> processElement(Element element) {


        // TODO: Beware the block-attribute for substitutions (elements, types) for the
        // check for valid elements - preprocessing?


        LinkedList<Element> tempList = new LinkedList<Element>();
        if ((element.getBlockModifiers() != null && !element.getBlockModifiers().contains(Block.substitution))) {
            tempList.addAll(processSubstitutionGroup(element));
        } else {
            if (!this.schema.getBlockDefaults().contains(XSDSchema.BlockDefault.substitution)) {
                tempList.addAll(processSubstitutionGroup(element));
            }
        }
        LinkedList<Element> resultList = new LinkedList<Element>(tempList);
        if ((element.getBlockModifiers() != null && element.getBlockModifiers().contains(Block.extension)) || this.schema.getBlockDefaults().contains(XSDSchema.BlockDefault.extension)) {
            for (Iterator<Element> it = tempList.iterator(); it.hasNext();) {
                Element element1 = it.next();
                if (element1.getType() != null && element1.getType() instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) element1.getType();
                    if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                        if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {
                            resultList.remove(element1);
                        }
                    }
                    if (complexType.getContent() != null && complexType.getContent() instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                        if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                            resultList.remove(element1);
                        }
                    }
                }
            }
        }

        if ((element.getBlockModifiers() != null && element.getBlockModifiers().contains(Block.restriction)) || this.schema.getBlockDefaults().contains(XSDSchema.BlockDefault.restriction)) {
            for (Iterator<Element> it = tempList.iterator(); it.hasNext();) {
                Element element1 = it.next();
                if (element1.getType() != null && element1.getType() instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) element1.getType();
                    if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                        if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentRestriction) {
                            resultList.remove(element1);
                        }
                    }
                    if (complexType.getContent() != null && complexType.getContent() instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                        if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                            resultList.remove(element1);
                        }
                    }
                }
                if (element1.getType() != null && element1.getType() instanceof SimpleType) {
                    SimpleType simpleType = (SimpleType) element1.getType();
                    if (simpleType.getInheritance() != null && simpleType.getInheritance() instanceof SimpleContentRestriction) {
                        resultList.remove(element1);
                    }
                }
            }
        }
        if (!element.getAbstract()) {
            resultList.add(element);
        }
        return resultList;
    }

    private LinkedList<Element> processSubstitutionGroup(Element element) {
        LinkedList<Element> tempList = new LinkedList<Element>();
        if (schema.getElementSymbolTable().hasReference(element.getName()) && schema.isSubstitutionHead(schema.getElementSymbolTable().getReference(element.getName()))) {
            for (Element subElement : schema.getSubstitutionElements(schema.getElementSymbolTable().getReference(element.getName()))) {
                if (!subElement.getAbstract()) {
                    tempList.add(subElement);
                }
                tempList.addAll(processSubstitutionGroup(subElement));
            }
        }
        return tempList;
    }
}
