package eu.fox7.bonxai.xsd.tools;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.Attribute;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.AttributeGroupRef;
import eu.fox7.bonxai.xsd.AttributeParticle;
import eu.fox7.bonxai.xsd.AttributeRef;
import eu.fox7.bonxai.xsd.ComplexContentExtension;
import eu.fox7.bonxai.xsd.ComplexContentRestriction;
import eu.fox7.bonxai.xsd.ComplexContentType;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.ElementRef;
import eu.fox7.bonxai.xsd.SimpleContentExtension;
import eu.fox7.bonxai.xsd.SimpleContentRestriction;
import eu.fox7.bonxai.xsd.SimpleContentType;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Tool for replacing all "groupRef"s and "attributeGroupRef"s in a given
 * XML XSDSchema object structure with the content of the referenced "group" or
 * "attributeGroup".
 *
 * It is important, that the ForeignSchemaLoader connects the SymbolTableRefs
 * with the correct objects (from foreign schemas) before this GroupReplacer is
 * used.
 *
 * This class is used by the XSD2DTD converter.
 *
 * The "group" or "attributeGroup" declarations remain unchanged.
 *
 * @author Lars Schmidt
 */
public class GroupReplacer {

    /**
     * The XML XSDSchema object structure
     */
    private XSDSchema schema;

    /**
     * Constructor of class GroupReplacer
     * @param schema
     */
    public GroupReplacer(XSDSchema schema) {
        this.schema = schema;
    }

    /**
     * Method "replace"
     *
     * This is the base method of this class, which handles the replacement
     * progress for all "groupRef"s or "attributeGroupRef"s in the given schema.
     */
    public void replace() {
        for (Iterator<Type> it = this.schema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
            Type type = it.next();
            replaceGroupsInType(type);
        }
    }

    /**
     * Method "replaceGroupsInType"
     *
     * This method replaces all "groupRef"s or "attributeGroupRef"s in a given type.
     *
     * @param type      Source for the group replacement.
     */
    private void replaceGroupsInType(Type type) {
        if (type instanceof ComplexType) {
            // Case "complexType":
            ComplexType complexType = (ComplexType) type;
            LinkedList<AttributeParticle> newComplexTypeAttributeList = new LinkedList<AttributeParticle>();

            // Handle complexType - attributes
            if (complexType.getAttributes() != null) {
                for (Iterator<AttributeParticle> it1 = complexType.getAttributes().iterator(); it1.hasNext();) {
                    AttributeParticle attributeParticle = it1.next();
                    newComplexTypeAttributeList.addAll(this.replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
                }
                complexType.setAttributes(newComplexTypeAttributeList);
            }

            if (complexType.getContent() != null) {
                // Case "complexType" with "complexContent":
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                    if (complexContentType.getInheritance() instanceof ComplexContentExtension) {
                        // Case "complexType" with "complexContent" with Inheritance "extension":
                        ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                        // Recurse into base-type
                        replaceGroupsInType(complexContentExtension.getBase());
                        // Handle extension - attributes
                        LinkedList<AttributeParticle> newComplexContentExtensionAttributeList = new LinkedList<AttributeParticle>();
                        for (Iterator<AttributeParticle> it1 = complexContentExtension.getAttributes().iterator(); it1.hasNext();) {
                            AttributeParticle attributeParticle = it1.next();
                            newComplexContentExtensionAttributeList.addAll(this.replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
                        }
                        complexContentExtension.setAttributes(newComplexContentExtensionAttributeList);
                    } else if (complexContentType.getInheritance() instanceof ComplexContentRestriction) {
                        // Case "complexType" with "complexContent" with Inheritance "restriction":
                        ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();
                        // Recurse into base-type
                        replaceGroupsInType(complexContentRestriction.getBase());
                        // Handle restriction - attributes
                        LinkedList<AttributeParticle> newComplexContentRestrictionAttributeList = new LinkedList<AttributeParticle>();
                        for (Iterator<AttributeParticle> it1 = complexContentRestriction.getAttributes().iterator(); it1.hasNext();) {
                            AttributeParticle attributeParticle = it1.next();
                            newComplexContentRestrictionAttributeList.addAll(this.replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
                        }
                        complexContentRestriction.setAttributes(newComplexContentRestrictionAttributeList);
                    }
                    // Handle complexContent - particle
                    if (complexContentType.getParticle() != null) {
                        Particle newParticle = this.replaceGroupRefsInParticle(complexContentType.getParticle());
                        complexContentType.setParticle(newParticle);
                    }
                } else if (complexType.getContent() instanceof SimpleContentType) {
                    // Case "complexType" with "simpleContent":
                    SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                    if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                        // Case "complexType" with "simpleContent" with Inheritance "extension":
                        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                        // Recurse into base-type
                        replaceGroupsInType(simpleContentExtension.getBase());
                        // Handle extension - attributes
                        LinkedList<AttributeParticle> newSimpleContentExtensionAttributeList = new LinkedList<AttributeParticle>();
                        for (Iterator<AttributeParticle> it1 = simpleContentExtension.getAttributes().iterator(); it1.hasNext();) {
                            AttributeParticle attributeParticle = it1.next();
                            newSimpleContentExtensionAttributeList.addAll(this.replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
                        }
                        simpleContentExtension.setAttributes(newSimpleContentExtensionAttributeList);
                    } else if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                        // Case "complexType" with "simpleContent" with Inheritance "restriction":
                        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                        // Recurse into base-type
                        replaceGroupsInType(simpleContentRestriction.getBase());
                        // Handle restriction - attributes
                        LinkedList<AttributeParticle> newSimpleContentRestrictionAttributeList = new LinkedList<AttributeParticle>();
                        for (Iterator<AttributeParticle> it1 = simpleContentRestriction.getAttributes().iterator(); it1.hasNext();) {
                            AttributeParticle attributeParticle = it1.next();
                            newSimpleContentRestrictionAttributeList.addAll(this.replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
                        }
                        simpleContentRestriction.setAttributes(newSimpleContentRestrictionAttributeList);
                    }
                }
            }
        } else if (type instanceof SimpleType) {
            // Case "simpleType":
            SimpleType simpleType = (SimpleType) type;
            if (simpleType.getInheritance() != null) {
                if (simpleType.getInheritance() instanceof SimpleContentExtension) {
                    // Case "simpleType" with "extension":
                    SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleType.getInheritance();
                    // Recurse into base-type
                    replaceGroupsInType(simpleContentExtension.getBase());
                    // Handle extension - attributes
                    LinkedList<AttributeParticle> newSimpleTypeExtensionAttributeList = new LinkedList<AttributeParticle>();
                    for (Iterator<AttributeParticle> it1 = simpleContentExtension.getAttributes().iterator(); it1.hasNext();) {
                        AttributeParticle attributeParticle = it1.next();
                        newSimpleTypeExtensionAttributeList.addAll(this.replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
                    }
                    simpleContentExtension.setAttributes(newSimpleTypeExtensionAttributeList);
                } else if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
                    // Case "simpleType" with "restriction":
                    SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                    // Recurse into base-type
                    replaceGroupsInType(simpleContentRestriction.getBase());
                    // Handle restriction - attributes
                    LinkedList<AttributeParticle> newSimpleTypeRestrictionAttributeList = new LinkedList<AttributeParticle>();
                    for (Iterator<AttributeParticle> it1 = simpleContentRestriction.getAttributes().iterator(); it1.hasNext();) {
                        AttributeParticle attributeParticle = it1.next();
                        newSimpleTypeRestrictionAttributeList.addAll(this.replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
                    }
                    simpleContentRestriction.setAttributes(newSimpleTypeRestrictionAttributeList);
                }
            }
        }
    }

    /**
     * Method "replaceGroupRefsInParticle"
     *
     * This method replaces all "groupRef"s in a given particle.
     *
     * @param particle      Source for the group replacement.
     * @return Particle     The new particle as result of the group replacement.
     */
    private Particle replaceGroupRefsInParticle(Particle particle) {
        if (particle instanceof CountingPattern) {
            CountingPattern newCountingPattern = new CountingPattern(((CountingPattern) particle).getMin(), ((CountingPattern) particle).getMax());
            newCountingPattern.addParticle(replaceGroupRefsInParticle(((CountingPattern) particle).getParticles().getFirst()));
            return newCountingPattern;
        } else if (particle instanceof AllPattern) {
            AllPattern allPattern = (AllPattern) particle;
            AllPattern newAllPattern = new AllPattern();
            for (Iterator<Particle> it = allPattern.getParticles().iterator(); it.hasNext();) {
                Particle particle1 = it.next();
                newAllPattern.addParticle(replaceGroupRefsInParticle(particle1));
            }
            return newAllPattern;
        } else if (particle instanceof SequencePattern) {
            SequencePattern sequencePattern = (SequencePattern) particle;
            SequencePattern newSequencePattern = new SequencePattern();
            for (Iterator<Particle> it = sequencePattern.getParticles().iterator(); it.hasNext();) {
                Particle particle1 = it.next();
                newSequencePattern.addParticle(replaceGroupRefsInParticle(particle1));
            }
            return newSequencePattern;
        } else if (particle instanceof ChoicePattern) {
            ChoicePattern choicePattern = (ChoicePattern) particle;
            ChoicePattern newChoicePattern = new ChoicePattern();
            for (Iterator<Particle> it = choicePattern.getParticles().iterator(); it.hasNext();) {
                Particle particle1 = it.next();
                newChoicePattern.addParticle(replaceGroupRefsInParticle(particle1));
            }
            return newChoicePattern;
        } else if (particle instanceof Element) {
            return particle;
        } else if (particle instanceof ElementRef) {
            return particle;
        } else if (particle instanceof AnyPattern) {
            return particle;
        } else if (particle instanceof GroupRef) {

            Group group = ((GroupRef) particle).getGroup();
            if (group.getParticleContainer() instanceof SequencePattern) {
                return replaceGroupRefsInParticle(group.getParticleContainer());
            } else if (group.getParticleContainer() instanceof ChoicePattern) {
                return replaceGroupRefsInParticle(group.getParticleContainer());
            } else if (group.getParticleContainer() instanceof AllPattern) {
                return replaceGroupRefsInParticle(group.getParticleContainer());
            } else if (group.getParticleContainer() instanceof CountingPattern) {
                return replaceGroupRefsInParticle(group.getParticleContainer());
            }
        }
        return null;
    }

    /**
     * Method "replaceAttributeGroupRefsInAttributeParticle"
     *
     * This method replaces all "attributeGroupRef"s in a given attributeParticle.
     *
     * @param attributeParticle     Source for the attributeGroup replacement.
     * @return LinkedList<AttributeParticle>     The List of all new attributeParticles as result of the attributeGroup replacement.
     **/
    private LinkedList<AttributeParticle> replaceAttributeGroupRefsInAttributeParticle(AttributeParticle attributeParticle) {
        LinkedList<AttributeParticle> newAttributeList = new LinkedList<AttributeParticle>();
        if (attributeParticle instanceof AttributeRef) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof AnyAttribute) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof AttributeGroup) {
            AttributeGroup attributeGroup = (AttributeGroup) attributeParticle;
            for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
                AttributeParticle currentAttributeParticle = it.next();
                newAttributeList.addAll(replaceAttributeGroupRefsInAttributeParticle(currentAttributeParticle));
            }
        } else if (attributeParticle instanceof Attribute) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof AttributeGroupRef) {
            newAttributeList.addAll(replaceAttributeGroupRefsInAttributeParticle(((AttributeGroupRef) attributeParticle).getAttributeGroup()));
        }
        return newAttributeList;
    }
}
