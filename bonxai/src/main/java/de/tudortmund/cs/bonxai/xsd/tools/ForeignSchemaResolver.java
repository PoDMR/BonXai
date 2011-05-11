package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element.*;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.*;
import java.util.*;

/**
 * The ForeignSchemaResolver is mainly used to prepare a ForeignSchema for
 * resolution. There for it provides methods to remove schema attributes like
 * the "elementFormDefault" attribute and similar attributes. Classes like the
 * IncludeSchemaResolver class inherit from this class other classes use the 
 * ForeignSchemaResolver to remove schema attributes from schemata before they
 * begin working on the schemata.
 *
 * @author Dominik Wolff
 */
public class ForeignSchemaResolver {

    /**
     * This method removes the "attributeFormDefault" attribute from a given
     * schema and instead adds a "form" attribute with same value to every
     * local attribute, which previously had no "form" attribute.
     *
     * Only locally declared attributes may contain "form" values. AttributeRefs
     * or top-level attributes can not contain "form" values.
     *
     * @param schema XSDSchema from which the "attributeFormDefault" attribute is
     * removed.
     */
    public void removeAttributeFormDefault(XSDSchema schema) {

        // Global default value for all attributes declared in the target namespace.
        XSDSchema.Qualification attributeFormDefault = schema.getAttributeFormDefault();

        // For each locally declared attribute it is checked if it contains a local "form" attribute.
        for (Iterator<Attribute> it = getLocallyDeclaredAttributes(schema).iterator(); it.hasNext();) {
            Attribute locallyDeclaredAttribute = it.next();

            // If in the local attribute no "form" attribute is present a new "form" attribute is set with the default "form" value for locally declared attributes.
            if (locallyDeclaredAttribute.getForm() == null) {
                locallyDeclaredAttribute.setForm(attributeFormDefault);
            }
        }

        // After this the attribute "form" default value of the schema is set to the default (unqualified).
        schema.setAttributeFormDefault(XSDSchema.Qualification.unqualified);
    }

    /**
     * This method removes the "elementFormDefault" attribute from a  given
     * schema and instead adds a "form" attribute with same value to every local
     * element, which previously had no "form" attribute.
     *
     * Only locally declared elements may contain "form" values. ElementRefs
     * or top-level elements can not contain "form" values.
     *
     * @param schema XSDSchema from which the "elementFormDefault" attribute is
     * removed.
     */
    public void removeElementFormDefault(XSDSchema schema) {

        // Global default value for all elements declared in the target namespace.
        XSDSchema.Qualification elementFormDefault = schema.getElementFormDefault();

        // For each locally declared element it is checked if it contains a local "form" attribute.
        for (Iterator<Element> it = getLocallyDeclaredElements(schema).iterator(); it.hasNext();) {
            Element locallyDeclaredElement = it.next();

            // If in the local element no "form" attribute is present a new "form" attribute is set with the default "form" value for locally declared elements.
            if (locallyDeclaredElement.getForm() == null) {
                locallyDeclaredElement.setForm(elementFormDefault);
            }
        }

        // After this the element "form" default value of the schema is set to the default (unqualified).
        schema.setElementFormDefault(XSDSchema.Qualification.unqualified);
    }

    /**
     * This method removes the "blockDefault" attribute of a specified schema.
     * To do this it is checked for all elements and types of the given schema
     * wether they contain a "block" attribute or not. If an element/type
     * does not a new "block" is generated with the value of the "blockDefault"
     * attribute and added to the specific element/type.
     *
     * @param schema XSDSchema from which the "blockDefault" attribute is removed.
     */
    public void removeBlockDefault(XSDSchema schema) {

        // Store "blockDefault" attribute in variable.
        HashSet<BlockDefault> blockDefault = schema.getBlockDefaults();

        // "block" attributes are allowed in all elements wheter top-level or not.
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>(schema.getElements());
        elements.addAll(getLocallyDeclaredElements(schema));

        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // Check if element contains a "block" attribute if not add new "block" attribute.
            if (element.getBlockModifiers() == null) {

                // If "blockDefault" contains value extension add extension to new "block" attribute.
                if (blockDefault.contains(BlockDefault.extension)) {
                    element.addBlockModifier(Block.extension);
                }

                // If "blockDefault" contains value restriction add restriction to new "block" attribute.
                if (blockDefault.contains(BlockDefault.restriction)) {
                    element.addBlockModifier(Block.restriction);
                }

                // If "blockDefault" contains value substitution add substitution to new "block" attribute.
                if (blockDefault.contains(BlockDefault.substitution)) {
                    element.addBlockModifier(Block.substitution);
                }
            }
        }

        // Anonymous types are not allowed to contain "block" attribute, so the list of top-level types is used.
        LinkedHashSet<Type> topLevelTypes = new LinkedHashSet<Type>(schema.getTypes());

        // Check for each type if a "block" attribute is present and add new attribute if necessary.
        for (Iterator<Type> it = topLevelTypes.iterator(); it.hasNext();) {
            Type topLevelType = it.next();

            // Only complexTypes have "block" attributes.
            if (topLevelType instanceof ComplexType) {
                ComplexType complexType = (ComplexType) topLevelType;

                // Check wether a "block" attribute is present in the current complexType.
                if (complexType.getBlockModifiers() == null) {

                    // If "blockDefault" contains value extension add extension to new "block" attribute.
                    if (blockDefault.contains(BlockDefault.extension)) {
                        complexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
                    }

                    // If "blockDefault" contains value restriction add restriction to new "block" attribute.
                    if (blockDefault.contains(BlockDefault.restriction)) {
                        complexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
                    }
                }
            }
        }

        // Set the "blockDefault" attribute of the schema to empty
        schema.setBlockDefaults(new HashSet<BlockDefault>());
    }

    /**
     * This method removes the "finalDefault" attribute of a specified schema.
     * In order to do so it is checked for all elements and types of the given
     * schema wether they contain a "final" attribute or not. If an
     * element/type does not a new "final" is generated with the value
     * of the "finalDefault" attribute and added to the specific
     * element/type.
     *
     * @param schema XSDSchema from which the "finalDefault" attribute is removed.
     */
    public void removeFinalDefault(XSDSchema schema) {

        // Store "finalDefault" attribute in variable.
        HashSet<FinalDefault> finalDefault = schema.getFinalDefaults();

        // Only top-level elements are allowed to contain "final" values so a set of these elements is generated.
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>(schema.getElements());

        // Check "final" attribute for each top-level attribute.
        for (Iterator<Element> it = topLevelElements.iterator(); it.hasNext();) {
            Element topLevelElement = it.next();

            // If no "final" attribute is present in the current top-level element set new "final" attribute.
            if (topLevelElement.getFinalModifiers() == null) {

                // If "finalDefault" contains value extension add extesion to new "final" attribute.
                if (finalDefault.contains(FinalDefault.extension)) {
                    topLevelElement.addFinalModifier(Final.extension);
                }

                // If "finalDefault" contains value restriction add restriction to new "final" attribute.
                if (finalDefault.contains(FinalDefault.restriction)) {
                    topLevelElement.addFinalModifier(Final.restriction);
                }
            }
        }

        // Anonymous types are not allowed to contain "final" attributes, so instead of the SymbolTable the list of top-level types is used.
        LinkedHashSet<Type> topLevelTypes = new LinkedHashSet<Type>(schema.getTypes());

        // Check "final" attributes for all top-level types.
        for (Iterator<Type> it = topLevelTypes.iterator(); it.hasNext();) {
            Type topLevelType = it.next();

            // In case the top-level type is a complexType only restriction and/or extension are allowed as values.
            if (topLevelType instanceof ComplexType) {
                ComplexType complexType = (ComplexType) topLevelType;

                // Check if a "final" attribute is present.
                if (complexType.getFinalModifiers() == null) {

                    // If "finalDefault" contains value extension add extesion to new "final" attribute.
                    if (finalDefault.contains(FinalDefault.extension)) {
                        complexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
                    }
                    // If "finalDefault" contains value restriction add restriction to new "final" attribute.
                    if (finalDefault.contains(XSDSchema.FinalDefault.restriction)) {
                        complexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
                    }
                }

            // In case the top-level type is a SimpleType restriction and/or list and/or union are allowed as values.
            } else if (topLevelType instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) topLevelType;

                // Check wether a "final" attribute is present.
                if (simpleType.getFinalModifiers() == null) {

                    // If "finalDefault" contains value restriction add restriction to new "final" attribute.
                    if (finalDefault.contains(FinalDefault.restriction)) {
                        simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
                    }

                    // If "finalDefault" contains value list add list to new "final" attribute.
                    if (finalDefault.contains(FinalDefault.list)) {
                        simpleType.addFinalModifier(SimpleTypeInheritanceModifier.List);
                    }

                    // If "finalDefault" contains value union add union to new "final" attribute.
                    if (finalDefault.contains(FinalDefault.union)) {
                        simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Union);
                    }
                }
            }
        }

        // Set the "finalDefault" attribute of the schema to empty
        schema.setFinalDefaults(new HashSet<FinalDefault>());
    }

    /**
     * This method finds all locally declared attributes contained in the
     * given schema. Only these attributes are allowed to contain "form"
     * attributes, hence methods like the removeAttributeFormDefault method
     * need this method to find all of those so that new "form" values may be
     * set.
     *
     * @param schema XSDSchema containing local attribute declarations which are
     * found by this method.
     * @return Set of locally declared attributes contained in the specified
     * schema.
     */
    private LinkedHashSet<Attribute> getLocallyDeclaredAttributes(XSDSchema schema) {

        // Set of all locally declared attributes
        LinkedHashSet<Attribute> locallyDeclaredAttributes = new LinkedHashSet<Attribute>();

        // First all groups are checked for local attribute declarations.
        for (Iterator<AttributeGroup> it = schema.getAttributeGroups().iterator(); it.hasNext();) {
            AttributeGroup attributeGroup = it.next();

            // Each attribute group contains an attribute particle which in turn contains local attributes and attribute references.
            for (Iterator<AttributeParticle> it2 = attributeGroup.getAttributeParticles().iterator(); it2.hasNext();) {
                AttributeParticle attributeParticle = it2.next();

                // If current attribute particle is an attribute add it to the set of locally declared attributes.
                if (attributeParticle instanceof Attribute) {
                    locallyDeclaredAttributes.add((Attribute) attributeParticle);
                }
            }
        }

        // Beside attribute groups only types contain local attributes and in order to get all types the TypeSymbolTable is used.
        LinkedHashSet<Type> types = schema.getTypeSymbolTable().getAllReferencedObjects();

        // Check for each type if a local attribute is contained
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // Only ComplexTypes are allowed to contain local attribute declarations.
            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;

                // Store attribute particles in a set.
                LinkedHashSet<AttributeParticle> attributeParticles = new LinkedHashSet<AttributeParticle>();

                //For complexTypes both content types store attributes differently.
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                    // In case the type contains a ComplexContentType either an inheritance contains the attributes or the complexType itself.
                    if (complexContentType.getInheritance() != null && complexType.getAttributes().isEmpty()) {

                        // Inheritance contains attribute particles
                        attributeParticles.addAll(complexContentType.getInheritance().getAttributes());
                    } else {

                        // ComplexType itself contains attribute particles
                        attributeParticles.addAll(complexType.getAttributes());
                    }
                } else if (complexType.getContent() instanceof SimpleContentType) {

                    // In case the complexType contains a SimpleContentType the SimpleContentInheritance contains the attributes
                    SimpleContentInheritance simpleContentInheritance = ((SimpleContentType) complexType.getContent()).getInheritance();

                    // Check if the simpleContentInheritance is a restriction or extension.
                    if (simpleContentInheritance instanceof SimpleContentExtension) {

                        // Attribute particles of an extension are added to the set.
                        attributeParticles.addAll(((SimpleContentExtension) simpleContentInheritance).getAttributes());
                    } else if (simpleContentInheritance instanceof SimpleContentRestriction) {

                        // Attribute particles of a restriction are added to the set.
                        attributeParticles.addAll(((SimpleContentRestriction) simpleContentInheritance).getAttributes());
                    }
                }

                // Check if the type contains local attribute declarations.
                for (Iterator<AttributeParticle> it2 = attributeParticles.iterator(); it2.hasNext();) {
                    AttributeParticle attributeParticle = it2.next();

                    // If current attribute particle is an attribute add it to the set of locally declared attributes.
                    if (attributeParticle instanceof Attribute) {
                        locallyDeclaredAttributes.add((Attribute) attributeParticle);
                    }
                }
            }
        }

        // Return the set of locally declared attributes to the caller.
        return locallyDeclaredAttributes;
    }

    /**
     * This method finds all elements declared local in the specified schema.
     * Only these elements are allowed to contain "form" attributes. Hence the
     * removeElementFormDefault method uses this method to find local elements
     * without "form" attributes.
     *
     * @param schema XSDSchema which contains local elements found by this method.
     * @return Set of locally declared elements found in the specified schema.
     */
    private LinkedHashSet<Element> getLocallyDeclaredElements(XSDSchema schema) {

        // Set of all locally declared elements
        LinkedHashSet<Element> locallyDeclaredElements = new LinkedHashSet<Element>();

        // For each group it is checked if this group contains local element declarations.
        for (Iterator<Group> it = schema.getGroups().iterator(); it.hasNext();) {
            Group group = it.next();

            // Search the container of the group for locally declared elements and add them to the set.
            locallyDeclaredElements.addAll(getElements(group.getParticleContainer()));
        }

        // Get all type contained in the schema via the TypeSymbolTable.
        LinkedHashSet<Type> types = schema.getTypeSymbolTable().getAllReferencedObjects();

        // For each type check if it contains local element declarations
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // Only complexTypes are allowed to contain elements.
            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;

                // And only complexTypes with complexContent contain particles.
                if (complexType.getContent() instanceof ComplexContentType) {

                    // Get particle from complexContent and check wether it is a ParticleContainer (Normally a complexContent has to contain a ParticleContainer if not it is invalid).
                    Particle particle = ((ComplexContentType) complexType.getContent()).getParticle();
                    if (particle instanceof ParticleContainer) {

                        // Search the container of the complexContent for locally declared elements and add them to the set.
                        locallyDeclaredElements.addAll(getElements((ParticleContainer) particle));
                    }
                }
            }
        }

        // Return the set of locally declared elements to the caller.
        return locallyDeclaredElements;
    }

    /**
     * This method finds all elements directly contained in a ParticleContainer.
     * Such elements are not referenced via an ElementRef nor are they contained
     * in Groups and referenced via a GroupRef. Directly contained elements are
     * local elements found in the particle structure of the ParticleContainer.
     *
     * @param particleContainer This container is the root of the searched
     * particle structure.
     * @return Set of local elements contained in the specified container.
     */
    private LinkedHashSet<Element> getElements(ParticleContainer particleContainer) {

        // Set of elements contained in the ParticleContainer
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();

        // For each particle is check wether it is a ParticleContainer or an Element
        for (Particle particle : particleContainer.getParticles()) {

            if (particle instanceof ParticleContainer) {

                // In case the particle is another ParticleContainer the getElements method is called for this container.
                elements.addAll(getElements((ParticleContainer) particle));
            } else if (particle instanceof Element) {

                // In case the particle is an element the element is added to the set.
                elements.add((Element) particle);
            }
        }

        // Return set containg all elements contained in the ParticleContainer
        return elements;
    }
}
