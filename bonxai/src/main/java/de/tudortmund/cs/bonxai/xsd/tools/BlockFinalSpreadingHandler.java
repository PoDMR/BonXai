package de.tudortmund.cs.bonxai.xsd.tools;

import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.ComplexTypeInheritanceModifier;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.BlockDefault;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.FinalDefault;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.SimpleTypeInheritanceModifier;
import de.tudortmund.cs.bonxai.xsd.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Class for spreading the blockDefault and finalDefault attributes of a parent
 * schema to all elements and types defined in this schema.
 * This class can also walk trough attached external schemas and spread their
 * default values for block and final.
 *
 * @author Lars Schmidt
 */
public class BlockFinalSpreadingHandler {

    // Store already handled schema objects
    private HashSet<XSDSchema> alreadyHandledSchemas;

    /**
     * Constructor of class BlockFinalSpreadingHandler
     */
    public BlockFinalSpreadingHandler() {
        this.alreadyHandledSchemas = new HashSet<XSDSchema>();
    }

    /**
     * Spread the default values for block and final from the schema root
     * element to all elements and types (not anonymous) within a schema
     * @param xmlSchema
     */
    public void spread(XSDSchema xmlSchema) {

        this.alreadyHandledSchemas.add(xmlSchema);

        HashSet<BlockDefault> schemaBlockDefaults = xmlSchema.getBlockDefaults();
        HashSet<FinalDefault> schemaFinalDefaults = xmlSchema.getFinalDefaults();

        // Block
        if (schemaBlockDefaults != null && !schemaBlockDefaults.isEmpty()) {

            /**
             * Element handling
             * 
             * The attribute block is not only useful in case of
             * toplevel-elements:
             * SubstitutionGroups can only be formed on the base of a
             * toplevel-element as its base, but the allowed substitution types
             * of the type of an element can be set individually on each element!
             */
            LinkedHashSet<Element> elements = getAllElementsFromTheCurrentSchema(xmlSchema);

            for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element element = it.next();
                // Check each type of block modifier value for elements
                if (element.getBlockModifiers() == null) {
                    if (schemaBlockDefaults.contains(BlockDefault.extension)) {
                        element.addBlockModifier(Element.Block.extension);
                    }
                    if (schemaBlockDefaults.contains(BlockDefault.restriction)) {
                        element.addBlockModifier(Element.Block.restriction);
                    }
                    if (schemaBlockDefaults.contains(BlockDefault.substitution)) {
                        element.addBlockModifier(Element.Block.substitution);
                    }
                }
            }

            // Type handling
            for (Iterator<Type> it = xmlSchema.getTypes().iterator(); it.hasNext();) {
                Type type = it.next();
                if (type instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) type;
                    // Check each type of block modifier value for types
                    if (complexType.getBlockModifiers() == null) {
                        if (schemaBlockDefaults.contains(BlockDefault.extension)) {
                            complexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
                        }
                        if (schemaBlockDefaults.contains(BlockDefault.restriction)) {
                            complexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
                        }
                    }
                }
            }
        }

        // Final
        if (schemaFinalDefaults != null && !schemaFinalDefaults.isEmpty()) {
            // Element handling
            for (Iterator<Element> it = xmlSchema.getElements().iterator(); it.hasNext();) {
                Element element = it.next();
                if (element.getFinalModifiers() == null) {
                    // Check each type of final modifier value for elements
                    if (schemaFinalDefaults.contains(FinalDefault.extension)) {
                        element.addFinalModifier(Element.Final.extension);
                    }
                    if (schemaFinalDefaults.contains(FinalDefault.restriction)) {
                        element.addFinalModifier(Element.Final.restriction);
                    }
                }
            }

            // Type handling
            for (Iterator<Type> it = xmlSchema.getTypes().iterator(); it.hasNext();) {
                Type type = it.next();
                if (type instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) type;
                    // Check each type of final modifier value for complexTypes
                    if (complexType.getFinalModifiers() == null) {
                        if (schemaFinalDefaults.contains(FinalDefault.extension)) {
                            complexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
                        }
                        if (schemaFinalDefaults.contains(FinalDefault.restriction)) {
                            complexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
                        }
                    }
                } else if (type instanceof SimpleType) {
                    SimpleType simpleType = (SimpleType) type;
                    // Check each type of final modifier value for simpleTypes
                    if (simpleType.getFinalModifiers() == null) {
                        if (schemaFinalDefaults.contains(FinalDefault.list)) {
                            simpleType.addFinalModifier(SimpleTypeInheritanceModifier.List);
                        }
                        if (schemaFinalDefaults.contains(FinalDefault.restriction)) {
                            simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
                        }
                        if (schemaFinalDefaults.contains(FinalDefault.union)) {
                            simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Union);
                        }
                    }
                }
            }
        }

        // Walk trough all foreignSchemas.
        if (xmlSchema.getForeignSchemas() != null && !xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadyHandledSchemas.contains(foreignSchema.getSchema())) {
                    spread(foreignSchema.getSchema());
                }
            }
        }
    }

    /**
     * Collect all elements from a given schema to a set and return it
     * @param xmlSchema     Source for the collection
     * @return LinkedHashSet<Element>   Set of all elements
     */
    private LinkedHashSet<Element> getAllElementsFromTheCurrentSchema(XSDSchema xmlSchema) {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();

        // Add all top-level elements to the set
        elements.addAll(xmlSchema.getElements());

        // Walk into all types and handle local elements
        for (Iterator<Type> it = xmlSchema.getTypes().iterator(); it.hasNext();) {
            Type type = it.next();
            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                    if (complexContentType.getParticle() != null) {
                        // Walk into the particle and handle local elements
                        LinkedHashSet<Element> currentElementSet = this.getAllElementsFromTheCurrentParticle(complexContentType.getParticle());
                        elements.addAll(currentElementSet);
                    }
                }
            }
        }

        // Walk into all groups and handle local elements
        for (Iterator<de.tudortmund.cs.bonxai.xsd.Group> it = xmlSchema.getGroups().iterator(); it.hasNext();) {
            de.tudortmund.cs.bonxai.xsd.Group group = it.next();
            if (group.getParticleContainer() != null) {
                // Walk into the particle and handle local elements
                LinkedHashSet<Element> currentElementSet = this.getAllElementsFromTheCurrentParticle(group.getParticleContainer());
                elements.addAll(currentElementSet);
            }
        }
        return elements;
    }

    /**
     * Collect all elements from a given particle
     * @param particle      Source for the element collection
     * @return LinkedHashSet<Element>   List of all found elements from the given particle
     */
    private LinkedHashSet<Element> getAllElementsFromTheCurrentParticle(Particle particle) {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle innerParticle = it.next();
                elements.addAll(getAllElementsFromTheCurrentParticle(innerParticle));
            }
        } else if (particle instanceof Element) {
            elements.add((Element) particle);
        }
        return elements;
    }
}
