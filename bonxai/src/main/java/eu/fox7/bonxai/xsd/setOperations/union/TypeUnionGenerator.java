package eu.fox7.bonxai.xsd.setOperations.union;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.GroupRef;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.XSDSchema.*;

import java.util.*;

/**
 * Type union generator class, which generates new types for the specified
 * output schema. These types represent the union of types used to construct
 * them. Types are simple and complex so different methods exist to build new
 * types.
 * 
 * @author Dominik Wolff
 */
public class TypeUnionGenerator {

    // XSDSchema which will contain the union of schemata contained in the schema group
    private XSDSchema outputSchema;

    // HashMap mapping target namespaces to output schemata, this is necessary to reference components in other schemata
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // HashMap mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> typeOldSchemaMap;

    // HashMap which contains for every local elemente, in the given context, its type reference
    private LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap;

    // Set containing all IDs used in all new output schemata
    private LinkedHashSet<String> usedIDs;

    // Attribute particle union generator of the type union generator class
    private AttributeParticleUnionGenerator attributeParticleUnionGenerator;

    // Particle union generator of the type union generator class
    private ParticleUnionGenerator particleUnionGenerator;

    /**
     * This is the constructor of the TypeUnionGenerator class, which
     * initializes most of the fields contained in the class.
     *
     * @param outputSchema  XSDSchema which will contain the new types.
     * @param namespaceOutputSchemaMap Map mapping namespaces to output
     * schemata.
     * @param typeOldSchemaMap Map mapping types to old schemata of the output
     * schema.
     * @param usedIDs Set containg all IDs used in each output schema.
     * @param attributeParticleUnionGenerator Attribute particle union generator
     * used to construct new attribute particles.
     * @param particleUnionGenerator Particle union generator used to generate
     * new particles for complexTypes.
     */
    public TypeUnionGenerator(XSDSchema outputSchema, LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap, LinkedHashMap<Type, XSDSchema> typeOldSchemaMap, LinkedHashSet<String> usedIDs, AttributeParticleUnionGenerator attributeParticleUnionGenerator, ParticleUnionGenerator particleUnionGenerator) {

        //Initialize class fields
        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.typeOldSchemaMap = typeOldSchemaMap;
        this.usedIDs = usedIDs;
        this.attributeParticleUnionGenerator = attributeParticleUnionGenerator;
        this.particleUnionGenerator = particleUnionGenerator;
    }

    /**
     * Creates a new annotation with new appInfos and new documentations by
     * copying the given old annotation.
     *
     * @param oldAnnotation Blueprint for the new annotation.
     * @return New Annotation matching the old annotation.
     */
    private Annotation generateNewAnnotation(Annotation oldAnnotation) {

        // Check if annotation is present
        if (oldAnnotation != null) {

            // Create new annotation
            Annotation newAnnotation = new Annotation();

            // Add id of the old annotation to the new annotation
            newAnnotation.setId(getID(oldAnnotation.getId()));

            // Get all old appInfos
            LinkedList<AppInfo> oldAppInfos = oldAnnotation.getAppInfos();

            // For each old appInfo create a new appInfo and add it to the annotation
            for (Iterator<AppInfo> it = oldAppInfos.iterator(); it.hasNext();) {
                AppInfo oldAppInfo = it.next();

                // Create new appInfo and add it to the appInfo list of the new annotation
                newAnnotation.addAppInfos(generateNewAppInfo(oldAppInfo));
            }

            // Get all old documentations
            LinkedList<Documentation> oldDocumentations = oldAnnotation.getDocumentations();

            // For each old documentation create a new documentation and add it to the annotation
            for (Iterator<Documentation> it = oldDocumentations.iterator(); it.hasNext();) {
                Documentation oldDocumentation = it.next();

                // Create new documentation and add it to the documentation list of the new annotation
                newAnnotation.addDocumentations(generateNewDocumentation(oldDocumentation));
            }

            // Retrun new created annotation
            return newAnnotation;
        }
        return null;
    }

    /**
     * Create new appInfo after the model of a specified old appInfo.
     *
     * @param oldAppInfo Old appInfo used to create the new appInfo.
     * @return New appInfo containing all information of the specified old
     * appInfo.
     */
    private AppInfo generateNewAppInfo(AppInfo oldAppInfo) {

        // Create new appInfo
        AppInfo newAppInfo = new AppInfo();

        // Set new node list for the new appInfo (node list is taken directly from the old appInfo because no operation changes this DOM structure)
        newAppInfo.setNodeList(oldAppInfo.getNodeList());

        // Set new source for the new appInfo
        newAppInfo.setSource(oldAppInfo.getSource());

        return newAppInfo;
    }

    /**
     * Create new ComplexContent extension by copying the specified old
     * ComplexContent extension. The new ComplexContent extension will contain a
     * new anonymous base type or a reference to a type contained in another
     * schema, this is achieved by copying the SymbolTableRef from the old
     * schema.
     *
     * @param oldComplexContentExtension Old ComplexContent extension used to
     * create the new extension.
     * @param oldSchema XSDSchema containing the old extension.
     * @return New ComplexContent extension which is a copy of the old
     * extension.
     */
    private ComplexContentInheritance generateNewComplexContentExtension(ComplexContentExtension oldComplexContentExtension) {

        // Prepare base type for the current component
        generateTypeReference(oldComplexContentExtension.getBase());

        // Create new ComplexContent extension with new base
        ComplexContentExtension newComplexContentExtension = new ComplexContentExtension(outputSchema.getTypeSymbolTable().getReference(oldComplexContentExtension.getBase().getName()));

        // Set new id and annotation in new extension
        newComplexContentExtension.setAnnotation(generateNewAnnotation(oldComplexContentExtension.getAnnotation()));
        newComplexContentExtension.setId(getID(oldComplexContentExtension.getId()));

        // Get list of all attribute particles contained in the old extension
        LinkedList<AttributeParticle> oldAttributeParticles = oldComplexContentExtension.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new extension
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new extension
            newComplexContentExtension.addAttribute(attributeParticleUnionGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // Return new ComplexContent extension
        return newComplexContentExtension;
    }

    /**
     * Create new complexContent inheritance (extension, restriction) from a
     * specified complexContent inheritance by copying the old complexContent
     * inheritance.
     *
     * @param oldComplexContentInheritance Old complexContent inheritance which
     * is copied to create the new complexContent inheritance.
     * @param oldSchema XSDSchema containing the old complexContent inheritance.
     * @return New complexContent inheritance created by copying the old
     * complexContent inheritance.
     */
    private ComplexContentInheritance generateNewComplexContentInheritance(ComplexContentInheritance oldComplexContentInheritance) {

        // Create new complexContent inheritance
        ComplexContentInheritance newComplexContentInheritance = null;

        // Check if the old inheritance is an extension
        if (oldComplexContentInheritance instanceof ComplexContentExtension) {

            // Create new complexContent extension
            ComplexContentExtension oldComplexContentExtension = (ComplexContentExtension) oldComplexContentInheritance;
            newComplexContentInheritance = generateNewComplexContentExtension(oldComplexContentExtension);

        // Check if the old inheritance is a restriction
        } else if (oldComplexContentInheritance instanceof ComplexContentRestriction) {

            // Create new complexContent restriction
            ComplexContentRestriction oldComplexContentRestriction = (ComplexContentRestriction) oldComplexContentInheritance;
            newComplexContentInheritance = generateNewComplexContentRestriction(oldComplexContentRestriction);
        }

        // Return new complexContent inheritance
        return newComplexContentInheritance;
    }

    /**
     * Create new ComplexContent restriction by copying the specified old
     * ComplexContent restriction. The new ComplexContent restriction will
     * contain a new anonymous base type or a reference to a type contained in
     * another schema, this is achieved by copying the SymbolTableRef from the
     * old schema.
     *
     * @param oldComplexContentRestriction Old ComplexContent restriction used
     * to create the new restriction.
     * @param oldSchema XSDSchema containing the old restriction.
     * @return New ComplexContent restriction which is a copy of the old
     * restriction.
     */
    private ComplexContentInheritance generateNewComplexContentRestriction(ComplexContentRestriction oldComplexContentRestriction) {

        // Prepare base type for the current component
        generateTypeReference(oldComplexContentRestriction.getBase());

        // Create new ComplexContent restriction with new base
        ComplexContentRestriction newComplexContentRestriction = new ComplexContentRestriction(outputSchema.getTypeSymbolTable().getReference(oldComplexContentRestriction.getBase().getName()));

        // Set new id and annotation in new restriction
        newComplexContentRestriction.setAnnotation(generateNewAnnotation(oldComplexContentRestriction.getAnnotation()));
        newComplexContentRestriction.setId(getID(oldComplexContentRestriction.getId()));

        // Get list of all attribute particles contained in the old restriction
        LinkedList<AttributeParticle> oldAttributeParticles = oldComplexContentRestriction.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new restriction
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new restriction
            newComplexContentRestriction.addAttribute(attributeParticleUnionGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // Return new ComplexContent restriction
        return newComplexContentRestriction;
    }

    /**
     * Create new ComplexContentType for a given old ComplexContentType. The new
     * ComplexContentType is an exact copy of the old ComplexContentType,
     * containing all information stored in the old ComplexContentType.
     *
     * @param oldComplexContentType ComplexContentType which is used to create
     * the new ComplexContentType.
     * @param oldSchema XSDSchema containing the old ComplexContentType.
     * @return New ComplexContentType which is a copy of the specified old
     * ComplexContentType.
     */
    private ComplexContentType generateNewComplexContentType(ComplexContentType oldComplexContentType) {

        // Create new ComplexContentType with new particle and new ComplexContentInheritance
        ComplexContentType newComplexContentType = new ComplexContentType(particleUnionGenerator.generateNewParticle(oldComplexContentType.getParticle(), false), generateNewComplexContentInheritance(oldComplexContentType.getInheritance()));

        // Set new fiels for the new ComplexContentType
        newComplexContentType.setAnnotation(generateNewAnnotation(oldComplexContentType.getAnnotation()));
        newComplexContentType.setId(getID(oldComplexContentType.getId()));
        newComplexContentType.setMixed(oldComplexContentType.getMixed());

        // Return new ComplexContentType
        return newComplexContentType;
    }

    /**
     * Create new complexType as a copy of the specified old complexType. All
     * informations contained in the old complexType are transfered to the new
     * complexType. The new complexType contains new attributes and so on.
     *
     * @param oldComplexType ComplexType used to create the new complexType as
     * an exact copy of the old complexType in the new schema.
     * @param oldSchema XSDSchema containing the old complexType and its type
     * reference.
     * @return New complexType in the new schema created from the specified old
     * complexType.
     */
    private ComplexType generateNewComplexType(ComplexType oldComplexType) {

        // Create new complexType with new content
        ComplexType newComplexType = new ComplexType(oldComplexType.getName(), generateNewContent(oldComplexType.getContent()));

        // Set id and annotation for the new type
        newComplexType.setAnnotation(generateNewAnnotation(oldComplexType.getAnnotation()));
        newComplexType.setId(getID(oldComplexType.getId()));

        // Set "abstract" and "mixed" values and check if complexType is anonymous
        newComplexType.setAbstract(oldComplexType.isAbstract());
        newComplexType.setIsAnonymous(oldComplexType.isAnonymous());
        newComplexType.setMixed(oldComplexType.getMixed());

        // Get list of all attribute particles contained in the old complexType
        LinkedList<AttributeParticle> oldAttributeParticles = oldComplexType.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new complexType
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new extension
            newComplexType.addAttribute(attributeParticleUnionGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // Check if "block" attribute is present in old type
        if (oldComplexType.getBlockModifiers() == null) {

            // Add "blockDefault" attribute values to new type (resolving "blockDefault" indirectly)
            if (typeOldSchemaMap.get(oldComplexType).getBlockDefaults().contains(BlockDefault.extension)) {
                newComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
            }
            if (typeOldSchemaMap.get(oldComplexType).getBlockDefaults().contains(BlockDefault.restriction)) {
                newComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
            }
        } else {

            // "block" attribute is present add "block" value to new type
            newComplexType.setBlockModifiers(oldComplexType.getBlockModifiers());
        }

        // Check if "final" attribute is present in old type
        if (oldComplexType.getFinalModifiers() == null) {

            // Add "finalDefault" attribute values to new type (resolving "finalDefault" indirectly)
            if (typeOldSchemaMap.get(oldComplexType).getFinalDefaults().contains(FinalDefault.extension)) {
                newComplexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
            }
            if (typeOldSchemaMap.get(oldComplexType).getFinalDefaults().contains(FinalDefault.restriction)) {
                newComplexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
            }
        } else {

            // "final" attribute is present add "final" value to new type
            newComplexType.setFinalModifiers(oldComplexType.getFinalModifiers());
        }

        // If new type is not anonymous and the schema does not already contain the type as top-level type add it to the list of top-level types
        if (!oldComplexType.isAnonymous() && !outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()).getReference())) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
        }

        // Return new complexType
        return newComplexType;
    }

    /**
     * Create new complexType for a list of given complexTypes. These
     * complexTypes are used to create a union, which results in a new
     * complexType.
     *
     * @param complexTypes Types used to create the new complexType.
     * @param typeName Name of the new type, which is computed beforehand.
     * @return ComplexType which may contain complex or simple content.
     */
    private ComplexType generateNewComplexType(LinkedList<ComplexType> complexTypes, String typeName) {

        // Initialize lists to store contents and attribute particles
        LinkedList<Content> contents = new LinkedList<Content>();
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        // Check if mixed type with no content exists
        boolean emptyMixedType = false;

        // For each complexType get content an contained attribute list
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // Add content and attribute list to the corresponding lists
            contents.add(complexType.getContent());
            attributeParticleLists.add(complexType.getAttributes());

            // If the current content is a complex content, which contains no elements and either the content or the type is mixed set emptyMixedType to true
            if (complexType.getContent() instanceof ComplexContentType) {
                ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                if (!hasNoEmptyContent(complexContentType.getParticle()) && (complexType.getMixed() || complexContentType.getMixed())) {
                    emptyMixedType = true;
                }
            }
        }

        // Generate new contetn for the given content list
        Content newContent = generateNewContent(contents, emptyMixedType, attributeParticleLists);

        // If no content could be created
        if (newContent == null && attributeParticleLists.isEmpty()) {

            // Name of the anyType
            String name = "{http://www.w3.org/2001/XMLSchema}anyType";

            // Check if any type has to be registered in the output schema
            if (!outputSchema.getTypeSymbolTable().hasReference(name)) {
                outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
            }

            // Create new complexType with complex content, which restricts the anyType
            ComplexContentType complexContentType = new ComplexContentType();
            complexContentType.setInheritance(new ComplexContentRestriction(outputSchema.getTypeSymbolTable().getReference(name)));
            ComplexType newComplexType = new ComplexType(typeName, complexContentType);

            //Register new complexType in the output schema
            outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, newComplexType);

            // If new type is not anonymous and the schema does not already contain the type as top-level type add it to the list of top-level types
            if (!isAnonymous(new LinkedList<Type>(complexTypes)) && !outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()).getReference())) {
                outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
            }
            return newComplexType;
        } else {

            // Create new complexType with the new content and "mixed" attribute
            ComplexType newComplexType = new ComplexType(typeName, newContent);
            newComplexType.setMixed(getComplexTypeMixed(complexTypes));

            // Set new annotation and "ID" attribute for new complexType
            newComplexType.setId(getID(new LinkedList<Type>(complexTypes)));
            newComplexType.setAnnotation(getTypeAnnotation(new LinkedList<Type>(complexTypes)));

            // Set "abstract" attribute for the new complexType
            newComplexType.setAbstract(getAbstract(complexTypes));
            newComplexType.setIsAnonymous(isAnonymous(new LinkedList<Type>(complexTypes)));

            // Set "block" and "final" attributes for the new complexType
            newComplexType.setBlockModifiers(getBlock(complexTypes));
            newComplexType.setFinalModifiers(getComplexTypeFinal(complexTypes));

            // Check if content is complex content, because complex content contains no attributes, which have to be contained by the complexType
            if (newContent instanceof ComplexContentType || newContent == null) {

                // Use AttributeParticleUnionGenerator to generate new attribute list for the new complexType
                newComplexType.setAttributes(attributeParticleUnionGenerator.generateAttributeParticleUnion(attributeParticleLists));

                // If content has "mixed" attribute true set "mixed" attribute in complexType to true
                if (newComplexType.getMixed() != true && newContent != null && ((ComplexContentType) newContent).getMixed() == true) {
                    newComplexType.setMixed(true);
                }

            } else if (newContent instanceof SimpleContentType) {

                // Simple content can not contain "mixed" attribute
                newComplexType.setMixed(false);
            }

            // Update type SymbolTable with new complexType
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newComplexType.getName(), newComplexType);

            // If new type is not anonymous and the schema does not already contain the type as top-level type add it to the list of top-level types
            if (!isAnonymous(new LinkedList<Type>(complexTypes)) && !outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()).getReference())) {
                outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
            }

            // Return the new complexType
            return newComplexType;
        }
    }

    /**
     * Create new content (ComplexContentType, SimpleContentType) by copying the
     * old content. All information stored in the old content is present in the
     * new content.
     *
     * @param oldContent Content which is used to created the new content, so
     * that the new content is a copy of the old content.
     * @param oldSchema XSDSchema containing the specified old content.
     * @return New content, copy the old content.
     */
    private Content generateNewContent(Content oldContent) {

        // Create new content
        Content newContent = null;

        // If old content is an ComplexContentType create new ComplexContentType.
        if (oldContent instanceof ComplexContentType) {

            // Create new ComplexContentType with the createNewComplexContentType method
            ComplexContentType oldComplexContentType = (ComplexContentType) oldContent;
            newContent = generateNewComplexContentType(oldComplexContentType);

        // If old content is an SimpleContentType create new SimpleContentType.
        } else if (oldContent instanceof SimpleContentType) {

            // Create new SimpleContentType with the createNewSimpleContentType method
            SimpleContentType oldSimpleContentType = (SimpleContentType) oldContent;
            newContent = generateNewSimpleContentType(oldSimpleContentType);
        }

        // Return new content.
        return newContent;
    }

    /**
     * This method creates a new content for a given list of content types.
     * These content types belong to types, which are used to create a new type
     * which contains the created new content.
     *
     * @param contents List of content types used to construct the new type
     * content.
     * @param emptyContentMixed Boolean variable, which is <tt>true</tt> if a
     * type is mixed and contains particles. The content of this type has to be
     * in the content list. The "mixed" attribute exists in the type and the
     * content.
     * @param attributeParticleLists List of attribute particle lists.
     * Attributes are stored in the type and the inheritance.
     * @return New content, which can be used for a new type.
     */
    private Content generateNewContent(LinkedList<Content> contents, boolean emptyContentMixed, LinkedList<LinkedList<AttributeParticle>> attributeParticleLists) {

        // Check if content is simple or complex and whether elements are contained
        boolean simpleContent = false;
        boolean complexContent = false;
        boolean complexContentWithElements = false;

        // Perform check for each content
        for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
            Content content = it.next();

            // If current content is simple content set variable simpleContent to true
            if (content instanceof SimpleContentType) {
                simpleContent = true;
            } else if (content instanceof ComplexContentType) {

                // If current content is complex content set variable complexContent to true
                complexContent = true;

                // If content contains elements set complexContentWithElements to true
                if (content != null && hasNoEmptyContent(((ComplexContentType) content).getParticle())) {
                    complexContentWithElements = true;
                }
            }
        }

        // If given contents are only simple contents
        if (simpleContent && !complexContent) {

            // Initialize lists to store member types, simple contennt types and simple content extensions
            LinkedList<SimpleType> memberTypes = new LinkedList<SimpleType>();
            LinkedList<SimpleContentType> simpleContentTypes = new LinkedList<SimpleContentType>();
            LinkedList<SimpleContentExtension> simpleContentExtensions = new LinkedList<SimpleContentExtension>();

            // The list of attribute lists should be empty, because only simple content is present. But to make sure list is initialized anew.
            // attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

            // Check each content
            for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
                Content content = it.next();

                // Check if content is simple contennt
                if (content instanceof SimpleContentType) {
                    SimpleContentType simpleContentType = (SimpleContentType) content;

                    // Add simple content to list
                    simpleContentTypes.add(simpleContentType);

                    // Check if content contains an extension
                    if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                        // Add extension, attribute list and member type to corresponding lists
                        simpleContentExtensions.add(simpleContentExtension);
                        attributeParticleLists.add(simpleContentExtension.getAttributes());
                        memberTypes.add((SimpleType) simpleContentExtension.getBase());
                    }
                }
            }

            // Generate new base type for the new extension, which contains all found member types
            SimpleType newBase = null;

            // Check if base type already exists
            if (outputSchema.getTypeSymbolTable().hasReference(getBaseTypeName(memberTypes))) {
                newBase = (SimpleType) outputSchema.getTypeSymbolTable().getReference(getBaseTypeName(memberTypes)).getReference();
            } else {
                newBase = generateNewSimpleType(memberTypes, getBaseTypeName(memberTypes));
            }
            SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newBase.getName()));

            // Set ID and annotation for new extension
            newSimpleContentExtension.setAnnotation(getSimpleContentExtensionAnnotation(simpleContentExtensions));
            newSimpleContentExtension.setId(getSimpleContentExtensionID(simpleContentExtensions));

            // Set attributes for new extension
            newSimpleContentExtension.setAttributes(attributeParticleUnionGenerator.generateAttributeParticleUnion(attributeParticleLists));

            // Generate new simple content type with new extension as inheritance
            SimpleContentType simpleContentType = new SimpleContentType();
            simpleContentType.setInheritance(newSimpleContentExtension);

            // Set ID and annotation for new simple content type and return it
            simpleContentType.setAnnotation(getContentAnnotation(new LinkedList<Content>(simpleContentTypes)));
            simpleContentType.setId(getContentID(new LinkedList<Content>(simpleContentTypes)));
            return simpleContentType;

        } else if (complexContent && !simpleContent) {

            // If given contents are only complex contents initialize new set and list for particles and complex content types
            LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
            LinkedList<ComplexContentType> complexContentTypes = new LinkedList<ComplexContentType>();

            // Check each content
            for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
                Content content = it.next();

                // Check if content is complex content
                if (content instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) content;

                    // Add complex content to list and add particle to set if it is not null
                    complexContentTypes.add(complexContentType);
                    if (complexContentType.getParticle() != null) {
                        particles.add(complexContentType.getParticle());
                    }
                }
            }

            // Check if empty content is present
            if (contents.contains(null)) {

                // Add empty sequence for simple content
                particles.add(new SequencePattern());
            }

            // Use particle union generator to compute new particle
            Particle particle = particleUnionGenerator.generateParticleUnion(particles, elementTypeMap);

            // Create new complex content with computed particle and set "mixed", "ID" attributes and annotation
            ComplexContentType newComplexContentType = new ComplexContentType(particle, null, getComplexContentTypeMixed(complexContentTypes));
            newComplexContentType.setAnnotation(getContentAnnotation(new LinkedList<Content>(complexContentTypes)));
            newComplexContentType.setId(getContentID(new LinkedList<Content>(complexContentTypes)));

            // Return new complex content
            return newComplexContentType;

        } else if (simpleContent && complexContent) {

            // If both complex content and simple content are present check if complex content contains elements
            if (!complexContentWithElements) {

                // Initialize lists to store member types, simple contennt types and simple content extensions
                LinkedList<SimpleType> memberTypes = new LinkedList<SimpleType>();
                LinkedList<SimpleContentType> simpleContentTypes = new LinkedList<SimpleContentType>();
                LinkedList<SimpleContentExtension> simpleContentExtensions = new LinkedList<SimpleContentExtension>();

                // Check each content
                for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
                    Content content = it.next();

                    // Check if content is simple contennt
                    if (content instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) content;

                        // Add simple content to list
                        simpleContentTypes.add(simpleContentType);

                        // Check if content contains an extension
                        if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                            // Add extension, attribute list and member type to corresponding lists
                            simpleContentExtensions.add(simpleContentExtension);
                            attributeParticleLists.add(simpleContentExtension.getAttributes());
                            memberTypes.add((SimpleType) simpleContentExtension.getBase());
                        }
                    }
                }

                // Check if a type exist without content and which is mixed
                if (emptyContentMixed) {

                    // Generate new complex content type which is mixed and contains no content
                    ComplexContentType newComplexContentType = new ComplexContentType(null, true);
                    return newComplexContentType;
                } else {

                    // Generate new base type for the new extension, which contains all found member types
                    SimpleType newBase = null;

                    // Check if base type already exists
                    if (outputSchema.getTypeSymbolTable().hasReference(getBaseTypeName(memberTypes))) {
                        newBase = (SimpleType) outputSchema.getTypeSymbolTable().getReference(getBaseTypeName(memberTypes)).getReference();
                    } else {
                        newBase = generateNewSimpleType(memberTypes, getBaseTypeName(memberTypes));
                    }
                    SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newBase.getName()));

                    // Set ID and annotation for new extension
                    newSimpleContentExtension.setAnnotation(getSimpleContentExtensionAnnotation(simpleContentExtensions));
                    newSimpleContentExtension.setId(getSimpleContentExtensionID(simpleContentExtensions));

                    // Set attributes for new extension
                    newSimpleContentExtension.setAttributes(attributeParticleUnionGenerator.generateAttributeParticleUnion(attributeParticleLists));

                    // Generate new simple content type with new extension as inheritance
                    SimpleContentType simpleContentType = new SimpleContentType();
                    simpleContentType.setInheritance(newSimpleContentExtension);

                    // Set ID and annotation for new simple content type and return it
                    simpleContentType.setAnnotation(getContentAnnotation(new LinkedList<Content>(simpleContentTypes)));
                    simpleContentType.setId(getContentID(new LinkedList<Content>(simpleContentTypes)));
                    return simpleContentType;
                }
            } else {

                // Complex content is present and contains elements get attributes contained in simple content types
                for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
                    Content content = it.next();

                    // Check if content is simple content
                    if (content instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) content;

                        // Check if content contains an extension
                        if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                            // Add extension, attribute list and member type to corresponding lists
                            attributeParticleLists.add(simpleContentExtension.getAttributes());
                        }
                    }
                }

                // For given complex contents initialize new set and list for particles and complex content types
                LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
                LinkedList<ComplexContentType> complexContentTypes = new LinkedList<ComplexContentType>();

                // Check each content
                for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
                    Content content = it.next();

                    // Check if content is complex content
                    if (content instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) content;

                        // Add complex content to list and add particle to set if it is not null
                        complexContentTypes.add(complexContentType);
                        if (complexContentType.getParticle() != null) {
                            particles.add(complexContentType.getParticle());
                        }
                    }
                }

                // Add empty sequence for simple content
                particles.add(new SequencePattern());

                // Use particle union generator to compute new particle
                Particle particle = particleUnionGenerator.generateParticleUnion(particles, elementTypeMap);

                // Create new complex content with computed particle and set "mixed", "ID" attributes and annotation
                ComplexContentType newComplexContentType = new ComplexContentType(particle, null, true);
                newComplexContentType.setAnnotation(getContentAnnotation(new LinkedList<Content>(complexContentTypes)));
                newComplexContentType.setId(getContentID(new LinkedList<Content>(complexContentTypes)));

                // Return new complex content
                return newComplexContentType;
            }
        }
        return null;
    }

    /**
     * Create new documentation after the model of a specified old documentation.
     *
     * @param oldAppInfo Old documentation used to create the new documentation.
     * @return New documentation containing all information of the specified old
     * documentation.
     */
    private Documentation generateNewDocumentation(Documentation oldDocumentation) {

        // Create new documentation
        Documentation newDocumentation = new Documentation();

        // Set new node list for the new documentation (node list is taken directly from the old appInfo because no operation changes this DOM structure)
        newDocumentation.setNodeList(oldDocumentation.getNodeList());

        // Set new source and XmlLang attributes for the new documentation
        newDocumentation.setSource(oldDocumentation.getSource());
        newDocumentation.setXmlLang(oldDocumentation.getXmlLang());
        return newDocumentation;
    }

    /**
     * Create new SimpleContent extension by copying the specified old
     * SimpleContent extension. The new SimpleContent extension will contain a
     * new anonymous base type or a reference to a type contained in another
     * schema, this is achieved by copying the SymbolTableRef from the old
     * schema.
     *
     * @param oldSimpleContentExtension Old SimpleContent extension used to create the new
     * extension.
     * @param oldSchema XSDSchema containing the old extension.
     * @return New SimpleContent extension which is a copy of the old extension.
     */
    private SimpleContentInheritance generateNewSimpleContentExtension(SimpleContentExtension oldSimpleContentExtension) {

        // Prepare base type for the current component
        generateTypeReference(oldSimpleContentExtension.getBase());

        // Create new SimpleContent extension with new base
        SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(oldSimpleContentExtension.getBase().getName()));

        // Set new id and annotation in new extension
        newSimpleContentExtension.setAnnotation(generateNewAnnotation(oldSimpleContentExtension.getAnnotation()));
        newSimpleContentExtension.setId(getID(oldSimpleContentExtension.getId()));

        // Get list of all attribute particles contained in the old extension
        LinkedList<AttributeParticle> oldAttributeParticles = oldSimpleContentExtension.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new extension
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new extension
            newSimpleContentExtension.addAttribute(attributeParticleUnionGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // Return new SimpleContent extension
        return newSimpleContentExtension;
    }

    /**
     * Create new simpleContent inheritance (extension, restriction) from a
     * specified simpleContent inheritance by copying the old simpleContent
     * inheritance.
     *
     * @param oldSimpleContentInheritance Old simpleContent inheritance which is
     * copied to create the new simpleContent inheritance.
     * @param oldSchema XSDSchema containing the old simpleContent inheritance.
     * @return New simpleContent inheritance created by copying the old
     * simpleContent inheritance.
     */
    private SimpleContentInheritance generateNewSimpleContentInheritance(SimpleContentInheritance oldSimpleContentInheritance) {

        // Create new simpleContent inheritance
        SimpleContentInheritance newSimpleContentInheritance = null;

        // Check if the old inheritance is an extension
        if (oldSimpleContentInheritance instanceof SimpleContentExtension) {

            // Create new simpleContent extension
            SimpleContentExtension oldSimpleContentExtension = (SimpleContentExtension) oldSimpleContentInheritance;
            newSimpleContentInheritance = generateNewSimpleContentExtension(oldSimpleContentExtension);

        // Check if the old inheritance is a restriction
        } else if (oldSimpleContentInheritance instanceof SimpleContentRestriction) {

            // Create new simpleContent restriction
            SimpleContentRestriction oldSimpleContentRestriction = (SimpleContentRestriction) oldSimpleContentInheritance;
            newSimpleContentInheritance = generateNewSimpleContentRestriction(oldSimpleContentRestriction);
        }

        // Return new simpleContent inheritance
        return newSimpleContentInheritance;
    }

    /**
     * Create new list by copying the specified old list. The new list will
     * contain a new anonymous base type or a reference to a type contained in
     * another schema, this is achieved by copying the SymbolTableRef from the
     * old schema.
     *
     * @param oldSimpleContentList Old list used to create the new list.
     * @param oldSchema XSDSchema containing the old list.
     * @return New list which is a copy of the old list.
     */
    private SimpleContentList generateNewSimpleContentList(SimpleContentList oldSimpleContentList) {

        // Prepare base type for the current component
        generateTypeReference(oldSimpleContentList.getBase());
        SimpleContentList newSimpleContentList = null;

        // Create new list with new base type
        if (isBuiltInDatatype(oldSimpleContentList.getBase().getName())) {
            newSimpleContentList = new SimpleContentList(outputSchema.getTypeSymbolTable().getReference(oldSimpleContentList.getBase().getName()));
        } else {
            newSimpleContentList = new SimpleContentList(outputSchema.getTypeSymbolTable().getReference("{" + outputSchema.getTargetNamespace() + "}" + oldSimpleContentList.getBase().getLocalName()));
        }

        // Set new id and annotation in new list
        newSimpleContentList.setAnnotation(generateNewAnnotation(oldSimpleContentList.getAnnotation()));
        newSimpleContentList.setId(getID(oldSimpleContentList.getId()));

        // Return new list
        return newSimpleContentList;
    }

    /**
     * Create new restriction by copying the specified old restriction. The new
     * restriction will contain a new anonymous type, a new base type and new
     * "facets" used to restrict the new type.
     *
     * @param oldSimpleContentRestriction Old restriction used to create the
     * new restriction.
     * @param oldSchema XSDSchema containing the old restriction.
     * @return New restriction which is a copy of the old restriction.
     */
    private SimpleContentRestriction generateNewSimpleContentRestriction(SimpleContentRestriction oldSimpleContentRestriction) {

        // Prepare base type for the current component
        generateTypeReference(oldSimpleContentRestriction.getBase());

        // Create new anonymous simpleType if old anonymous simpleType is present
        SimpleType newAnonymousSimpleType = null;
        if (oldSimpleContentRestriction.getAnonymousSimpleType() != null) {

            // Store new anonymous simpleType for later usage
            newAnonymousSimpleType = (SimpleType) generateNewType(oldSimpleContentRestriction.getAnonymousSimpleType());
        }
        SimpleContentRestriction newSimpleContentRestriction = null;

        // Create new restriction with new base and new anonymous type
        if (isBuiltInDatatype(oldSimpleContentRestriction.getBase().getName())) {
            newSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(oldSimpleContentRestriction.getBase().getName()), newAnonymousSimpleType);
        } else {
            newSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference("{" + outputSchema.getTargetNamespace() + "}" + oldSimpleContentRestriction.getBase().getLocalName()), newAnonymousSimpleType);
        }

        // Set new id and annotation in new restiction
        newSimpleContentRestriction.setAnnotation(generateNewAnnotation(oldSimpleContentRestriction.getAnnotation()));
        newSimpleContentRestriction.setId(getID(oldSimpleContentRestriction.getId()));

        // Get list of all attribute particles contained in the old restriction
        LinkedList<AttributeParticle> oldAttributeParticles = oldSimpleContentRestriction.getAttributes();

        // Create for each attribute particle a new attribute particle and add it to the new restriction
        for (Iterator<AttributeParticle> it = oldAttributeParticles.iterator(); it.hasNext();) {
            AttributeParticle oldAttributeParticle = it.next();

            // Add new create attribute particle to the new restriction
            newSimpleContentRestriction.addAttribute(attributeParticleUnionGenerator.generateNewAttributeParticle(oldAttributeParticle));
        }

        // Set "facets" of the new restriction
        newSimpleContentRestriction.setEnumeration(new LinkedList<String>(oldSimpleContentRestriction.getEnumeration()));

        if (oldSimpleContentRestriction.getFractionDigits() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newFractionDigits = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getFractionDigits().getValue(), oldSimpleContentRestriction.getFractionDigits().getFixed());
            newSimpleContentRestriction.setFractionDigits(newFractionDigits);
        }
        if (oldSimpleContentRestriction.getLength() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getLength().getValue(), oldSimpleContentRestriction.getLength().getFixed());
            newSimpleContentRestriction.setLength(newLength);
        }
        if (oldSimpleContentRestriction.getMaxLength() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newMaxLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getMaxLength().getValue(), oldSimpleContentRestriction.getMaxLength().getFixed());
            newSimpleContentRestriction.setMaxLength(newMaxLength);
        }
        if (oldSimpleContentRestriction.getMinLength() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newMinLength = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getMinLength().getValue(), oldSimpleContentRestriction.getMinLength().getFixed());
            newSimpleContentRestriction.setMinLength(newMinLength);
        }
        if (oldSimpleContentRestriction.getTotalDigits() != null) {
            SimpleContentFixableRestrictionProperty<Integer> newTotalDigits = new SimpleContentFixableRestrictionProperty<Integer>(oldSimpleContentRestriction.getTotalDigits().getValue(), oldSimpleContentRestriction.getTotalDigits().getFixed());
            newSimpleContentRestriction.setTotalDigits(newTotalDigits);
        }
        if (oldSimpleContentRestriction.getMaxExclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMaxExclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMaxExclusive().getValue(), oldSimpleContentRestriction.getMaxExclusive().getFixed());
            newSimpleContentRestriction.setMaxExclusive(newMaxExclusive);
        }
        if (oldSimpleContentRestriction.getMaxInclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMaxInclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMaxInclusive().getValue(), oldSimpleContentRestriction.getMaxInclusive().getFixed());
            newSimpleContentRestriction.setMaxInclusive(newMaxInclusive);
        }
        if (oldSimpleContentRestriction.getMinExclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMinExclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMinExclusive().getValue(), oldSimpleContentRestriction.getMinExclusive().getFixed());
            newSimpleContentRestriction.setMinExclusive(newMinExclusive);
        }
        if (oldSimpleContentRestriction.getMinInclusive() != null) {
            SimpleContentFixableRestrictionProperty<String> newMinInclusive = new SimpleContentFixableRestrictionProperty<String>(oldSimpleContentRestriction.getMinInclusive().getValue(), oldSimpleContentRestriction.getMinInclusive().getFixed());
            newSimpleContentRestriction.setMinInclusive(newMinInclusive);
        }
        if (oldSimpleContentRestriction.getPattern() != null) {
            SimpleContentRestrictionProperty<String> newPattern = new SimpleContentRestrictionProperty<String>(oldSimpleContentRestriction.getPattern().getValue());
            newSimpleContentRestriction.setPattern(newPattern);
        }
        if (oldSimpleContentRestriction.getWhitespace() != null) {
            SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> newWhitespace = new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(oldSimpleContentRestriction.getWhitespace().getValue(), oldSimpleContentRestriction.getWhitespace().getFixed());
            newSimpleContentRestriction.setWhitespace(newWhitespace);
        }

        // Return new restriction
        return newSimpleContentRestriction;
    }

    /**
     * Create new SimpleContentType for a given old SimpleContentType. The new
     * SimpleContentType is an exact copy of the old SimpleContentType,
     * containing all information stored in the old SimpleContentType.
     *
     * @param oldSimpleContentType SimpleContentType which is used to create the
     * new SimpleContentType.
     * @param oldSchema XSDSchema containing the old SimpleContentType.
     * @return New SimpleContentType which is a copy of the specified old
     * SimpleContentType.
     */
    private SimpleContentType generateNewSimpleContentType(SimpleContentType oldSimpleContentType) {

        // Create new SimpleContentType
        SimpleContentType newSimpleContentType = new SimpleContentType();

        // Set id and annotation for the new SimpleContentType
        newSimpleContentType.setAnnotation(generateNewAnnotation(oldSimpleContentType.getAnnotation()));
        newSimpleContentType.setId(getID(oldSimpleContentType.getId()));

        // Set new created inheritance
        newSimpleContentType.setInheritance(generateNewSimpleContentInheritance(oldSimpleContentType.getInheritance()));

        // Retrun new SimpleContentType with new inheritance
        return newSimpleContentType;
    }

    /**
     * Create new union by copying the specified old union. The new union will
     * contain new member types or referes to member types contained in other
     * schemata.
     *
     * @param createNewSimpleContentUnion Old union used to create the new
     * union.
     * @param oldSchema XSDSchema containing the old union.
     * @return New union which is a copy of the old union.
     */
    private SimpleTypeInheritance generateNewSimpleContentUnion(SimpleContentUnion oldSimpleContentUnion) {

        // Create new list for all new member types
        LinkedList<SymbolTableRef<Type>> newMemberTypes = new LinkedList<SymbolTableRef<Type>>();

        // Check for each old member type the new type SymbolTable
        for (Iterator<SymbolTableRef<Type>> it = oldSimpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
            SymbolTableRef<Type> symbolTableRef = it.next();

            // Prepare member types for the new union component
            generateTypeReference(symbolTableRef.getReference());

            // Get new member type from the type SymbolTable of the new schema
            if (isBuiltInDatatype(symbolTableRef.getKey())) {
                newMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(symbolTableRef.getKey()));
            } else {
                newMemberTypes.add(outputSchema.getTypeSymbolTable().getReference("{" + outputSchema.getTargetNamespace() + "}" + symbolTableRef.getKey().substring(symbolTableRef.getKey().lastIndexOf("}") + 1)));
            }
        }

        // Create new union with new member types
        SimpleContentUnion newSimpleContentUnion = new SimpleContentUnion(newMemberTypes);

        // Set new id and annotation in new union
        newSimpleContentUnion.setAnnotation(generateNewAnnotation(oldSimpleContentUnion.getAnnotation()));
        newSimpleContentUnion.setId(getID(oldSimpleContentUnion.getId()));

        // Return new union
        return newSimpleContentUnion;
    }

    /**
     * Method creates a new simpleType copying a specified old simpleType. Old
     * and new simpleType are mostly identical, but the new simpleType is
     * registered in the new schema where the old simpleType has to be
     * registered in the old schema.
     *
     * @param oldSimpleType SimpleType for which a copy is generated.
     * @param oldSchema  XSDSchema containing the old simpleType either directly
     * or indirectly.
     * @return New simpleType which is a copy of the specified old simpleType.
     */
    private SimpleType generateNewSimpleType(SimpleType oldSimpleType) {

        // If the simpleType is a build-in type it its reigistered with the output schema
        if (isBuiltInDatatype(oldSimpleType.getName())) {
            SimpleType newSimpleType = new SimpleType(oldSimpleType.getName(), null);

            // Update type SymbolTable and return new simpleType
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);
            return newSimpleType;
        }

        // Create new simpleType with new inheritance
        SimpleType newSimpleType = new SimpleType("{" + outputSchema.getTargetNamespace() + "}" + oldSimpleType.getLocalName(), generateNewSimpleTypeInheritance(oldSimpleType.getInheritance()));

        // Check if "final" attribute is present in old simpleType
        if (oldSimpleType.getFinalModifiers() == null) {

            // Add "finalDefault" attribute values to new simpleType (resolving "finalDefault" indirectly)
            if (typeOldSchemaMap.get(oldSimpleType).getFinalDefaults().contains(FinalDefault.restriction)) {
                newSimpleType.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
            }
            if (typeOldSchemaMap.get(oldSimpleType).getFinalDefaults().contains(FinalDefault.list)) {
                newSimpleType.addFinalModifier(SimpleTypeInheritanceModifier.List);
            }
            if (typeOldSchemaMap.get(oldSimpleType).getFinalDefaults().contains(FinalDefault.union)) {
                newSimpleType.addFinalModifier(SimpleTypeInheritanceModifier.Union);
            }
        } else {

            // "final" attribute is present add "final" value to new simpleType
            newSimpleType.setFinalModifiers(oldSimpleType.getFinalModifiers());
        }

        // Check wether the new simpleType has to be anonymous
        newSimpleType.setIsAnonymous(oldSimpleType.isAnonymous());

        // Set id and annotation for the new simple type
        newSimpleType.setAnnotation(generateNewAnnotation(oldSimpleType.getAnnotation()));
        newSimpleType.setId(getID(oldSimpleType.getId()));

        // Update type SymbolTable and return new simpleType
        outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

        // Check if type has to be added to the top-level type list of the schema
        if (!oldSimpleType.isAnonymous() && !outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()).getReference()) && !isBuiltInDatatype(newSimpleType.getName())) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
        }
        return newSimpleType;
    }

    /**
     * Create new simpleType by uniting the given simpleTypes. The union of
     * simpleTypes is a new simpleType, which can be anonymous if all given
     * simpleTypes were anonymous and contains the union of other attributes as
     * well.
     *
     * @param simpleTypes List of simpleTypes used to construct this new type.
     * @param typeName Name of the new simpleType, was generate beforhand so
     * that elements could reference the new type.
     * @return A new simpleType, can be a anySimpleType.
     */
    public SimpleType generateNewSimpleType(LinkedList<SimpleType> simpleTypes, String typeName) {

        // If only one type is contained in the list use the copy method
        if (new LinkedHashSet<SimpleType>(simpleTypes).size() == 1 && isBuiltInDatatype(typeName)) {

            // Check if type has to be regstered in the output schema
            if (!outputSchema.getTypeSymbolTable().hasReference(typeName)) {
                outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, new SimpleType(typeName, null));
            }

            // Return any simple type
            return (SimpleType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference();
        }

        // Check if type is only type
        if (new LinkedHashSet<SimpleType>(simpleTypes).size() == 1 && typeName.substring(typeName.lastIndexOf("}") + 1).equals(simpleTypes.getFirst().getLocalName())) {
            return generateNewSimpleType(simpleTypes.getFirst());
        }

        // Check simpleType list for anySimpleType
        for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
            SimpleType simpleType = it.next();

            // Check if any simpleType is an anySimpleType
            if (simpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {

                // Name of the any simple type
                String name = "{http://www.w3.org/2001/XMLSchema}anySimpleType";

                // Check if any type has to be regstered in the output schema
                if (!outputSchema.getTypeSymbolTable().hasReference(name)) {
                    outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
                }

                // Return any simple type
                return (SimpleType) outputSchema.getTypeSymbolTable().getReference(name).getReference();
            }
        }

        // Create new simpleType with new inheritance
        SimpleType newSimpleType = new SimpleType(typeName, getNewSimpleTypeInheritance(simpleTypes));

        // Get new "final" attribute for the new simpleType
        newSimpleType.setFinalModifiers(getSimpleTypeFinal(simpleTypes));

        // Check whether the new simpleType has to be anonymous
        newSimpleType.setIsAnonymous(isAnonymous(new LinkedList<Type>(simpleTypes)));

        // Set id and annotation for the new simple type
        newSimpleType.setAnnotation(getTypeAnnotation(new LinkedList<Type>(simpleTypes)));
        newSimpleType.setId(getID(new LinkedList<Type>(simpleTypes)));

        // Update type SymbolTable and return new simpleType
        outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

        if (!isAnonymous(new LinkedList<Type>(simpleTypes)) && !outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()).getReference()) && !isBuiltInDatatype(newSimpleType.getName())) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
        }

        // Return new simpleType
        return newSimpleType;
    }

    /**
     * Create new simpleType inheritance (union, list, restriction) from a
     * specified simpleType inheritance by copying the old simpleType
     * inheritance.
     *
     * @param oldSimpleTypeInheritance Old simpleType inheritance which is
     * copied to create the new simpleType inheritance.
     * @param oldSchema XSDSchema containing the old simpleType inheritance.
     * @return New simpleType inheritance created by copying the old simpleType
     * inheritance.
     */
    private SimpleTypeInheritance generateNewSimpleTypeInheritance(SimpleTypeInheritance oldSimpleTypeInheritance) {

        // Create new simpleType inheritance
        SimpleTypeInheritance newSimpleTypeInheritance = null;

        // Check if the old inheritance is a list
        if (oldSimpleTypeInheritance instanceof SimpleContentList) {

            // Create new simpleType list
            SimpleContentList oldSimpleContentList = (SimpleContentList) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = generateNewSimpleContentList(oldSimpleContentList);

        // Check if the old inheritance is a restriction
        } else if (oldSimpleTypeInheritance instanceof SimpleContentRestriction) {

            // Create new simpleType restriction
            SimpleContentRestriction oldSimpleContentRestriction = (SimpleContentRestriction) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = generateNewSimpleContentRestriction(oldSimpleContentRestriction);

        // Check if the old inheritance is a union
        } else if (oldSimpleTypeInheritance instanceof SimpleContentUnion) {

            // Create new simpleType union
            SimpleContentUnion oldSimpleContentUnion = (SimpleContentUnion) oldSimpleTypeInheritance;
            newSimpleTypeInheritance = generateNewSimpleContentUnion(oldSimpleContentUnion);
        }

        // Return new simpleType inheritance
        return newSimpleTypeInheritance;
    }

    /**
     * This method creates a new top-level type. This type will be a copie of
     * the specified old type contained in the given old schema. Furthermore the
     * new type will be registered in the SymbolTable of the new schema and in
     * the list of top-level types.
     *
     * @param topLevelType Type which is the blueprint for the new type.
     */
    public void generateNewTopLevelType(Type topLevelType) {

        // Create new type
        generateNewType(topLevelType);

        // Add new type to the list of top-level types
        if (!isBuiltInDatatype(topLevelType.getName()) && outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(topLevelType.getName()))) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(topLevelType.getName()));
        }
    }

    /**
     * Create new type copying the old type. New type has not to be a top-level
     * type it can be anonymous and locally defined. Further more this type can
     * be a simpleType or complexType.
     *
     * @param oldType Type which is the model for the new type.
     * @return New type matching the old type.
     */
    public Type generateNewType(Type oldType) {

        // Create new type for the old type
        Type newType = null;

        // Check if old type is a simpleType
        if (oldType instanceof SimpleType) {

            // Create new simpleType
            SimpleType oldSimpleType = (SimpleType) oldType;
            newType = generateNewSimpleType(oldSimpleType);

        // Check if old type is a complexType
        } else if (oldType instanceof ComplexType) {

            // Create new complexType
            ComplexType oldComplexType = (ComplexType) oldType;
            newType = generateNewComplexType(oldComplexType);
        }

        // Return new type
        return newType;
    }

    /**
     * Create new type, which results from the union of given types. The new
     * type can be either a simpleType or a complexType depending on the types
     * used to build it. If the type list contains both simple and complex types
     * the new type can contain an anyType if no other union is possible.
     *
     * @param types Set of types used to contstruct the new type.
     * @param elementTypeMap Map mapping local elements to types, the type
     * automaton is used to generate this map.
     * @param typeName Name of the new type, which is constructed beforehand
     * so that elements can referre to the type before its construction.
     */
    public void generateNewType(LinkedHashSet<Type> types, LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap, String typeName) {

        // Set new value for elementTypeMap field
        this.elementTypeMap = elementTypeMap;

        // Initialize lists to contain simpleTypes and complexTypes contained in the type list
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        LinkedList<ComplexType> complexTypes = new LinkedList<ComplexType>();

        // Use variable to check if compleTypes use elements
        boolean complexContentWithElements = false;

        // Check if mixed type with no content exists
        boolean emptyMixedType = false;

        // Check for each type if it is a complexType or a simpleType
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // Check if type is a simpleType
            if (type instanceof SimpleType) {

                // If type is no anyType add type to the list of simpleTypes
                if (!type.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {
                    simpleTypes.add((SimpleType) type);
                } else {

                    // Name of the anyType
                    String name = "{http://www.w3.org/2001/XMLSchema}anyType";

                    // Register anyType in the output schema
                    outputSchema.getTypeSymbolTable().updateOrCreateReference(name, new SimpleType(name, null));
                    return;
                }
            } else if (type instanceof ComplexType) {

                // If type is a complexType check if the content is a complex content
                if (((ComplexType) type).getContent() instanceof ComplexContentType) {
                    Content content = ((ComplexType) type).getContent();

                    // Check if content contains elements, and if set complexContentWithElements to true
                    if ((content != null && hasNoEmptyContent(((ComplexContentType) content).getParticle())) || content == null) {
                        complexContentWithElements = true;
                    }
                    if (!hasNoEmptyContent(((ComplexContentType) content).getParticle()) && (((ComplexType) type).getMixed() || ((ComplexContentType) content).getMixed())) {
                        emptyMixedType = true;
                    }
                }

                // Add type to the list of simpleTypes
                complexTypes.add((ComplexType) type);
            }
        }

        // The given type list only contains simpleTypes create new simpleType
        if (!simpleTypes.isEmpty() && complexTypes.isEmpty()) {

            // Generate new simpleType
            generateNewSimpleType(simpleTypes, typeName);

        } else if (simpleTypes.isEmpty() && !complexTypes.isEmpty()) {

            // Generate new complexType because the given type list only contains complexTypes
            generateNewComplexType(complexTypes, typeName);

        } else if (!simpleTypes.isEmpty() && !complexTypes.isEmpty()) {

            // If the given type list contains complexTypes and simpleTypes and the complexTypes contain elements
            if (complexContentWithElements) {

                // Generate for the new list a new type
                ComplexType complexType = generateNewComplexType(complexTypes, typeName);

                // Only complex content can be mixed
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                    ChoicePattern newChoicePattern = new ChoicePattern();
                    newChoicePattern.addParticle(complexContentType.getParticle());
                    newChoicePattern.addParticle(new SequencePattern());
                    complexContentType.setParticle(newChoicePattern);
                    complexType.setMixed(true);
                }
            } else {

                // Initialize list of member types
                LinkedList<SimpleType> memberTypes = new LinkedList<SimpleType>();

                // Add each simpleType to the list of member types
                for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
                    SimpleType memberType = it.next();
                    memberTypes.add(memberType);
                }

                // Initialize lists to store contents, simple content extensions and attribute particle lists
                LinkedList<Content> contents = new LinkedList<Content>();
                LinkedList<SimpleContentExtension> simpleContentExtensions = new LinkedList<SimpleContentExtension>();
                LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

                // Check all complexTypes to update the lists
                for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
                    ComplexType memberType = it.next();

                    // Add attribute particles and content of the current complexType
                    attributeParticleLists.add(memberType.getAttributes());
                    contents.add(memberType.getContent());

                    // Check if complexType has simple content
                    if (memberType.getContent() instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) memberType.getContent();

                        // Check if simple content contains an extension (Inheritance is resolved, so only extension remains)
                        if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                            // Add extension, attributes and simpleType to the corresponding lists
                            simpleContentExtensions.add(simpleContentExtension);
                            attributeParticleLists.add(simpleContentExtension.getAttributes());
                            memberTypes.add((SimpleType) simpleContentExtension.getBase());
                        }
                    }
                }

                // Check if empty mixed complexType exists
                if (emptyMixedType) {

                    // Create new complexType which can be anonymous
                    ComplexType newComplexType = new ComplexType(typeName, null, isAnonymous(new LinkedList<Type>(complexTypes)));
                    newComplexType.setAnnotation(getTypeAnnotation(new LinkedList<Type>(complexTypes)));
                    newComplexType.setId(getID(new LinkedList<Type>(complexTypes)));
                    newComplexType.setAttributes(attributeParticleUnionGenerator.generateAttributeParticleUnion(attributeParticleLists));
                    newComplexType.setMixed(true);

                    //Register new complexType in the output schema
                    outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, newComplexType);

                    // If new type is not anonymous and the schema does not already contain the type as top-level type add it to the list of top-level types
                    if (!isAnonymous(new LinkedList<Type>(complexTypes)) && !outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()).getReference())) {
                        outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
                    }
                } else {

                    // Generate new base type for the new complexType, which contains all found member types
                    SimpleType newBase = null;

                    // Check if base type already exists
                    if (outputSchema.getTypeSymbolTable().hasReference(getBaseTypeName(memberTypes))) {
                        newBase = (SimpleType) outputSchema.getTypeSymbolTable().getReference(getBaseTypeName(memberTypes)).getReference();
                    } else {
                        newBase = generateNewSimpleType(memberTypes, getBaseTypeName(memberTypes));
                    }

                    // Create new extension with base type and set annotation and id
                    SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newBase.getName()));
                    newSimpleContentExtension.setAnnotation(getSimpleContentExtensionAnnotation(simpleContentExtensions));
                    newSimpleContentExtension.setId(getSimpleContentExtensionID(simpleContentExtensions));

                    // Generate new attribute particles for the new complexType
                    newSimpleContentExtension.setAttributes(attributeParticleUnionGenerator.generateAttributeParticleUnion(attributeParticleLists));

                    // Create new content with extension and set annotation and id
                    SimpleContentType newSimpleContentType = new SimpleContentType();
                    newSimpleContentType.setInheritance(newSimpleContentExtension);
                    newSimpleContentType.setAnnotation(getContentAnnotation(contents));
                    newSimpleContentType.setId(getContentID(contents));

                    // Create new complexType which can be anonymous
                    ComplexType complexType = new ComplexType(typeName, newSimpleContentType, isAnonymous(new LinkedList<Type>(complexTypes)));
                    complexType.setAnnotation(getTypeAnnotation(new LinkedList<Type>(complexTypes)));
                    complexType.setId(getID(new LinkedList<Type>(complexTypes)));

                    // Add complexType to type SymbolTable and to the list of  top-level types if it is not anonymous
                    outputSchema.getTypeSymbolTable().updateOrCreateReference(complexType.getName(), complexType);
                    if (!isAnonymous(new LinkedList<Type>(complexTypes))) {
                        outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(complexType.getName()));
                    }
                }
            }
        }
    }

    /**
     * This method checks whether the type, generate from the specified
     * complexTypes list, is abstract or not. It is abstract if all complexTypes
     * contained in the complexTypes list are abstract.
     *
     * @param complexTypes List of complexTypes used to construct a new type.
     * @return <tt>true</tt> if all specified complexTypes are abstract, else
     * <tt>false</tt>.
     */
    private boolean getAbstract(LinkedList<ComplexType> complexTypes) {

        // Check for each complexType, if the type is abstract
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // If a single complexType is not abstract return false
            if (!complexType.isAbstract()) {
                return false;
            }
        }

        // If all given complexTypes are abstract, true is returned
        return true;
    }

    /**
     * Create new name for a new base type. The name is constructed by using the
     * specified list of member types.
     *
     * @param memberTypes List of member types. The names of these member types
     * are contained in the new base type name.
     * @return String representing the name of the new base type.
     */
    private String getBaseTypeName(LinkedList<SimpleType> memberTypes) {

        // If only one simpleType is the new base type no new name is created
        if (memberTypes.size() == 1) {

            // Build-in data types are not renamed
            if (memberTypes.iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                return memberTypes.iterator().next().getName();
            }
            return "{" + outputSchema.getTargetNamespace() + "}" + memberTypes.getFirst().getLocalName();
        } else {

            // Generate new name for the base type
            String typeName = "{" + outputSchema.getTargetNamespace() + "}union-type.";

            for (Iterator<SimpleType> it = memberTypes.iterator(); it.hasNext();) {
                String name = it.next().getLocalName();

                // If any simple type is contained return the any simple type name
                if (name.equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                    return "{http://www.w3.org/2001/XMLSchema}anySimpleType";
                }

                // Add each type name to the new name
                typeName += name;

                // If current restriction type is not the last add a "." to the name
                if (it.hasNext()) {
                    typeName += ".";
                }
            }

            // Use type name base and number to find valid type name
            String typeNameBase = typeName;
            int number = 1;

            // As long as the type name already exists check next type name
            while (outputSchema.getTypeSymbolTable().hasReference(typeName)) {

                // Type name consists of a type name base and a number
                typeName = typeNameBase + "." + number;
                number++;
            }
            return typeName;
        }
    }

    /**
     * This method creates a new "block" attribute for a given set of
     * complexTypes. The new "block" attribute is different than the default
     * attribute of the output schema and contains only
     * ComplexTypeInheritanceModifier values contained in all specified "block"
     * attribute.
     *
     * @param complexTypes Set of complexTypes, each may contain a "block"
     * attribute.
     * @return Set of ComplexTypeInheritanceModifier values or null if the set
     * is equivalent to the set contained by the output schema.
     */
    private HashSet<ComplexTypeInheritanceModifier> getBlock(LinkedList<ComplexType> complexTypes) {

        // Generate new "block" attribute, which contains every ComplexTypeInheritanceModifier value
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Extension);
        block.add(ComplexTypeInheritanceModifier.Restriction);

        // Check for each complexType the "block" attribute
        for (ComplexType complexType : complexTypes) {

            // If "block" attribute is present remove not contained ComplexTypeInheritanceModifier value from the "block" attribute of the new complexType
            if (complexType.getBlockModifiers() != null) {

                // Check if "extension" is not contained and if remove "extension" from "block" attribute of the new complexType
                if (!complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension)) {
                    block.remove(ComplexTypeInheritanceModifier.Extension);
                }
                // Check if "restriction" is not contained and if remove "restriction" from "block" attribute of the new complexType
                if (!complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction)) {
                    block.remove(ComplexTypeInheritanceModifier.Restriction);
                }
            } else if (typeOldSchemaMap.get(complexType).getBlockDefaults() != null) {

                // Remove Block values not contained in the default value
                if (!typeOldSchemaMap.get(complexType).getBlockDefaults().contains(BlockDefault.extension)) {
                    block.remove(ComplexTypeInheritanceModifier.Extension);
                }
                if (!typeOldSchemaMap.get(complexType).getBlockDefaults().contains(BlockDefault.restriction)) {
                    block.remove(ComplexTypeInheritanceModifier.Restriction);
                }
            } else {

                // Remove every Block value from the "block" attribute of the new complexType
                block.clear();
            }
        }

        // Generate a set containing BlockDefault values for the Block values contained in the set of the new complexType
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();

        // Check if "extension" is contained and if add "extension"
        if (block.contains(ComplexTypeInheritanceModifier.Extension)) {
            blockDefaults.add(XSDSchema.BlockDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (block.contains(ComplexTypeInheritanceModifier.Restriction)) {
            blockDefaults.add(XSDSchema.BlockDefault.restriction);
        }

        // Check if new "block" values is different than the default value of the output schema
        if (!(outputSchema.getBlockDefaults().containsAll(blockDefaults) && blockDefaults.containsAll(outputSchema.getBlockDefaults()))) {
            return block;
        } else {

            // If output schema has the same "blockDefault" value as the new complexTypes "block" attribute return null
            return null;
        }
    }

    /**
     * Check if the new complex content type generated from the list of
     * specified complex content types is mixed. This is the case if one of
     * these complex content types contains a "mixed" attribute.
     *
     * @param complexContentTypes List of complex content types used to contruct
     * a new complex content type.
     * @return <tt>true</tt> if a single given complex content type is mixed,
     * else <tt>false</tt>.
     */
    private boolean getComplexContentTypeMixed(LinkedList<ComplexContentType> complexContentTypes) {

        // Check for each complex content type, if the complex content type contains a "mixed" attribute
        for (Iterator<ComplexContentType> it = complexContentTypes.iterator(); it.hasNext();) {
            ComplexContentType complexContentType = it.next();

            // If a single complex content type is mixed return true
            if (complexContentType.getMixed()) {
                return true;
            }
        }

        // If no complex content type of the list is mixed the new complex content type is not mixed either
        return false;
    }

    /**
     * This method creates a new "final" attribute for a given set of
     * complexTypes. The new "final" attribute is different than the default
     * attribute of the output schema and contains only
     * ComplexTypeInheritanceModifier values contained in all specified "final"
     * attribute.
     *
     * @param complexTypes Set of complexTypes, each may contain a "final"
     * attribute.
     * @return Set of ComplexTypeInheritanceModifier values or null if the set
     * is equivalent to the set contained by the output schema.
     */
    private HashSet<ComplexTypeInheritanceModifier> getComplexTypeFinal(LinkedList<ComplexType> complexTypes) {

        // Generate new "final" attribute, which contains every ComplexTypeInheritanceModifier value
        HashSet<ComplexTypeInheritanceModifier> finalValue = new HashSet<ComplexTypeInheritanceModifier>();
        finalValue.add(ComplexTypeInheritanceModifier.Extension);
        finalValue.add(ComplexTypeInheritanceModifier.Restriction);

        // Check for each complexType the "final" attribute
        for (ComplexType complexType : complexTypes) {

            // If "final" attribute is present remove not contained ComplexTypeInheritanceModifier value from the "final" attribute of the new complexType
            if (complexType.getFinalModifiers() != null) {

                // Check if "extension" is not contained and if remove "restriction" from "extension" attribute of the new complexType
                if (!complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Extension)) {
                    finalValue.remove(ComplexTypeInheritanceModifier.Extension);
                }
                // Check if "restriction" is not contained and if remove "restriction" from "final" attribute of the new complexType
                if (!complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Restriction)) {
                    finalValue.remove(ComplexTypeInheritanceModifier.Restriction);
                }
            } else {

                // Check if the schema of the complexType has a default value
                if (typeOldSchemaMap.get(complexType).getFinalDefaults() != null) {

                    // Remove ComplexTypeInheritanceModifier values not contained in the default value
                    if (!outputSchema.getFinalDefaults().contains(FinalDefault.extension)) {
                        finalValue.remove(ComplexTypeInheritanceModifier.Extension);
                    }
                    if (!outputSchema.getFinalDefaults().contains(FinalDefault.restriction)) {
                        finalValue.remove(ComplexTypeInheritanceModifier.Restriction);
                    }
                } else {

                    // Remove every ComplexTypeInheritanceModifier value from the "final" attribute of the new element
                    finalValue.clear();
                }
            }
        }

        // Generate a set containing FinalDefault values for the ComplexTypeInheritanceModifier values contained in the set of the new element
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();

        // Check if "extension" is contained and if add "extension"
        if (finalValue.contains(ComplexTypeInheritanceModifier.Extension)) {
            finalDefaults.add(XSDSchema.FinalDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (finalValue.contains(ComplexTypeInheritanceModifier.Restriction)) {
            finalDefaults.add(XSDSchema.FinalDefault.restriction);
        }

        // Check if new "final" values is different than the default value of the output schema
        if (!(outputSchema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(outputSchema.getFinalDefaults()))) {
            return finalValue;
        } else {

            // If output schema has the same "finalDefault" value as the new elements "final" attribute return null
            return null;
        }
    }

    /**
     * Check if the new complexType generated from the list of specified
     * complexTypes is mixed. This is the case if one of these complexTypes
     * contains a "mixed" attribute.
     *
     * @param complexTypes List of complexTypes used to contruct a new
     * complexType.
     * @return <tt>true</tt> if a single given complexTypes is mixed, else
     * <tt>false</tt>.
     */
    private boolean getComplexTypeMixed(LinkedList<ComplexType> complexTypes) {

        // Check for each complexType, if the type contains a "mixed" attribute
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // If a single complexType is mixed return true
            if (complexType.getMixed()) {
                return true;
            }
        }

        // If no complexType of the list is mixed the new complexType is not mixed either
        return false;
    }

    /**
     * This method creates a new annotation for a given list of contents. Each
     * contetn may contain an annotation, these annotations are used to
     * contstruct the new annotation, which contains the app infos and
     * documentations of the old annotations.
     *
     * @param contents Set of contents, which is used to construct a new
     * content. This content will contain the new annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotations contained in the specified contents.
     */
    private Annotation getContentAnnotation(LinkedList<Content> contents) {

        // Create new annotation
        Annotation newAnnotation = null;

        // Check each content for a contained annotation
        for (Content content : contents) {

            // Check if content is empty
            if (content != null) {

                // Check if content contains an annotation
                if (content.getAnnotation() != null) {
                    Annotation oldAnnotation = content.getAnnotation();

                    // If new annotation is still null initialize new annotation
                    if (newAnnotation == null) {
                        newAnnotation = new Annotation();
                    }

                    // Get all old appInfos
                    LinkedList<AppInfo> oldAppInfos = oldAnnotation.getAppInfos();

                    // For each old appInfo create a new appInfo and add it to the annotation
                    for (Iterator<AppInfo> it = oldAppInfos.iterator(); it.hasNext();) {
                        AppInfo oldAppInfo = it.next();

                        // Create new appInfo and add it to the appInfo list of the new annotation
                        newAnnotation.addAppInfos(generateNewAppInfo(oldAppInfo));
                    }

                    // Get all old documentations
                    LinkedList<Documentation> oldDocumentations = oldAnnotation.getDocumentations();

                    // For each old documentation create a new documentation and add it to the annotation
                    for (Iterator<Documentation> it = oldDocumentations.iterator(); it.hasNext();) {
                        Documentation oldDocumentation = it.next();

                        // Create new documentation and add it to the documentation list of the new annotation
                        newAnnotation.addDocumentations(generateNewDocumentation(oldDocumentation));
                    }
                }
            }
        }
        return newAnnotation;
    }

    /**
     * Generates a new ID for a given set of contents. New ID is valid for
     * all output schema.
     *
     * @param contents Set of contents, each particle may contain an ID.
     * @return New ID for the content build for the content set.
     */
    private String getContentID(LinkedList<Content> contents) {

        // Initialize new ID
        String newID = "";

        // Check for each content if an ID is contained
        for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
            Content content = it.next();

            // Check if content is empty
            if (content != null) {

                // If an ID is contained append ID to new ID
                if (content.getId() != null) {
                    newID += content.getId();
                }
                if (!newID.equals("") && it.hasNext()) {
                    newID += ".";
                }
            }
        }

        // Check if a new ID was generated
        if (!newID.equals("")) {

            //  Use ID base and number to find a valid ID
            String newIDBase = newID;
            int number = 1;

            // As long as the new ID is invalid use another new ID
            while (usedIDs.contains(newID)) {

                // Add a number to the base of the new ID
                newID = newIDBase + "." + number;
                number++;
            }

            // Add new ID to the set of used IDs
            usedIDs.add(newID);
        } else {
            return null;
        }
        return newID;
    }

    /**
     * Generates a new ID for a given set of types. New ID is valid for
     * all output schema.
     *
     * @param types List of types, each type may contain an ID.
     * @return New ID for the type build for the type set.
     */
    private String getID(LinkedList<Type> types) {

        // Initialize new ID
        String newID = "";

        // Check for each types if an ID is contained
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // If an ID is contained append ID to new ID
            if (type.getId() != null) {
                newID += type.getId();
            }
            if (!newID.equals("") && it.hasNext()) {
                newID += ".";
            }
        }

        // Check if a new ID was generated
        if (!newID.equals("")) {

            //  Use ID base and number to find a valid ID
            String newIDBase = newID;
            int number = 1;

            // As long as the new ID is invalid use another new ID
            while (usedIDs.contains(newID)) {

                // Add a number to the base of the new ID
                newID = newIDBase + "." + number;
                number++;
            }

            // Add new ID to the set of used IDs
            usedIDs.add(newID);
        } else {
            return null;
        }
        return newID;
    }

    /**
     * Generates a new ID for a given ID. New ID is valid for all output schema.
     *
     * @param id String, which contains an ID.
     * @return New ID for the given ID.
     */
    private String getID(String id) {

        // Check if a ID was generated
        if (id != null) {

            //  Use ID base and number to find a valid ID
            String newIDBase = id;
            int number = 1;

            // As long as the new ID is invalid use another new ID
            while (usedIDs.contains(id)) {

                // Add a number to the base of the new ID
                id = newIDBase + "." + number;
                number++;
            }

            // Add new ID to the set of used IDs
            usedIDs.add(id);
        }
        return id;
    }

    /**
     * Create new simpleType inheritance for a given set of simple types. Given
     * simpleTypes are placed in a union, which then returned as new simpleType
     * inheritance.
     *
     * @param simpleTypes Set of simpleTypes used to construct the new
     * simpleType inheritance.
     * @return New simpleType inheritance, which contains all given simpleTypes.
     */
    private SimpleTypeInheritance getNewSimpleTypeInheritance(LinkedList<SimpleType> simpleTypes) {

        // Initialize sets to store member types and build-in datatype names
        LinkedHashSet<SymbolTableRef<Type>> memberTypes = new LinkedHashSet<SymbolTableRef<Type>>();
        LinkedHashSet<String> buildInTypes = new LinkedHashSet<String>();

        // Split simpleTypes into to two sets
        for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
            SimpleType simpleType = it.next();

            // Check if current simpleType is build-in datatype and if its is add its name to the set
            if (isBuiltInDatatype(simpleType.getName())) {
                buildInTypes.add(simpleType.getName());
            } else {

                // If current simpleType is no build-in datatype generate new member type and add it to the set
                SimpleType newMemberType = generateNewSimpleType(simpleType);
                memberTypes.add(outputSchema.getTypeSymbolTable().getReference(newMemberType.getName()));
            }
        }

        // For each build-in datatype name create a new type
        for (Iterator<String> it = buildInTypes.iterator(); it.hasNext();) {
            String string = it.next();

            // Create new type in output schema for the current build-in datatype name
            SimpleType simpleType = new SimpleType(string, null);
            if (!outputSchema.getTypeSymbolTable().hasReference(string)) {
                outputSchema.getTypeSymbolTable().updateOrCreateReference(string, simpleType);
            }

            // Add new type to member types list
            memberTypes.add(outputSchema.getTypeSymbolTable().getReference(string));
        }

        // For each member type fix "final" attributes
        for (Iterator<SymbolTableRef<Type>> it = memberTypes.iterator(); it.hasNext();) {
            SymbolTableRef<Type> symbolTableRef = it.next();

            // Check if member type is simpleType
            if (symbolTableRef.getReference() instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) symbolTableRef.getReference();

                // If "final" attribute is present and contains "union" set new "final" attribute
                if (simpleType.getFinalModifiers() != null && simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Union)) {

                    // Remove "union" from "final" attribute value
                    HashSet<SimpleTypeInheritanceModifier> simpleTypeInheritanceModifiers = new HashSet<SimpleTypeInheritanceModifier>();
                    for (Iterator<SimpleTypeInheritanceModifier> it2 = simpleType.getFinalModifiers().iterator(); it2.hasNext();) {
                        SimpleTypeInheritanceModifier simpleTypeInheritanceModifier = it2.next();

                        // If current simpleType inheritance modifier is not "union" add it to new set
                        if (simpleTypeInheritanceModifier != SimpleTypeInheritanceModifier.Union) {
                            simpleTypeInheritanceModifiers.add(simpleTypeInheritanceModifier);
                        }
                    }

                    // Set new set of simpleType inheritance modifiers without "union" as "final" attribute
                    simpleType.setFinalModifiers(simpleTypeInheritanceModifiers);
                }
            }
        }
        return new SimpleContentUnion(new LinkedList<SymbolTableRef<Type>>(memberTypes));
    }

    /**
     * This method creates a new annotation for a given list of simple content
     * extenstions. Each simple content extenstion may contain an annotation,
     * these annotations are used to contstruct the new annotation, which
     * contains the app infos and documentations of the old annotations.
     *
     * @param simpleContentExtensions Set of simple content extenstions, which
     * is used to construct a new simple content extenstion. This simple content
     * extenstion will contain the new annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotations contained in the specified simple content extenstions.
     */
    private Annotation getSimpleContentExtensionAnnotation(LinkedList<SimpleContentExtension> simpleContentExtensions) {

        // Create new annotation
        Annotation newAnnotation = null;

        // Check each simple content extenstions for a contained annotation
        for (SimpleContentExtension simpleContentExtension : simpleContentExtensions) {

            // Check if simple content extension contains an annotation
            if (simpleContentExtension.getAnnotation() != null) {
                Annotation oldAnnotation = simpleContentExtension.getAnnotation();

                // If new annotation is still null initialize new annotation
                if (newAnnotation == null) {
                    newAnnotation = new Annotation();
                }

                // Get all old appInfos
                LinkedList<AppInfo> oldAppInfos = oldAnnotation.getAppInfos();

                // For each old appInfo create a new appInfo and add it to the annotation
                for (Iterator<AppInfo> it = oldAppInfos.iterator(); it.hasNext();) {
                    AppInfo oldAppInfo = it.next();

                    // Create new appInfo and add it to the appInfo list of the new annotation
                    newAnnotation.addAppInfos(generateNewAppInfo(oldAppInfo));
                }

                // Get all old documentations
                LinkedList<Documentation> oldDocumentations = oldAnnotation.getDocumentations();

                // For each old documentation create a new documentation and add it to the annotation
                for (Iterator<Documentation> it = oldDocumentations.iterator(); it.hasNext();) {
                    Documentation oldDocumentation = it.next();

                    // Create new documentation and add it to the documentation list of the new annotation
                    newAnnotation.addDocumentations(generateNewDocumentation(oldDocumentation));
                }
            }
        }
        return newAnnotation;
    }

    /**
     * Generates a new ID for a given set of simple content extensions. New ID
     * is valid for all output schema.
     *
     * @param simpleContentExtensions Set of simple content extensions, each
     * simple content extension may contain an ID.
     * @return New ID for the simple content extension build for the simple
     * content extension set.
     */
    private String getSimpleContentExtensionID(LinkedList<SimpleContentExtension> simpleContentExtensions) {

        // Initialize new ID
        String newID = null;

        // Check for each simple content extension if an ID is contained
        for (SimpleContentExtension simpleContentExtension: simpleContentExtensions) {
            // If an ID is contained append ID to new ID
            if (simpleContentExtension.getId() != null) {
            	if (newID==null) newID="";
                newID += simpleContentExtension.getId() + ".";
            }
        }

        // Check if a new ID was generated
        if (newID != null) {

            //  Use ID base and number to find a valid ID
            String newIDBase = newID;
            int number = 1;

            // As long as the new ID is invalid use another new ID
            while (usedIDs.contains(newID)) {

                // Add a number to the base of the new ID
                newID = newIDBase + number;
                number++;
            }

            // Add new ID to the set of used IDs
            usedIDs.add(newID);
        }
        return newID;
    }

    /**
     * This method creates a new "final" attribute for a given set of
     * simpleTypes. The new "final" attribute is different than the default
     * attribute of the output schema and contains only
     * SimpleTypeInheritanceModifier values contained in all specified "final"
     * attribute.
     *
     * @param simpleTypes Set of simpleTypes, each may contain a "final"
     * attribute.
     * @return Set of SimpleTypeInheritanceModifier values or null if the set is
     * equivalent to the set contained by the output schema.
     */
    private HashSet<SimpleTypeInheritanceModifier> getSimpleTypeFinal(LinkedList<SimpleType> simpleTypes) {

        // Generate new "final" attribute, which contains every SimpleTypeInheritanceModifier value
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        finalValue.add(SimpleTypeInheritanceModifier.Union);

        // Check for each simpleType the "final" attribute
        for (SimpleType simpleType : simpleTypes) {

            // If "final" attribute is present remove not contained SimpleTypeInheritanceModifier value from the "final" attribute of the new simpleType
            if (simpleType.getFinalModifiers() != null) {

                // Check if "list" is not contained and if remove "list" from "list" attribute of the new simpleType
                if (!simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.List)) {
                    finalValue.remove(SimpleTypeInheritanceModifier.List);
                }
                // Check if "restriction" is not contained and if remove "restriction" from "final" attribute of the new simpleType
                if (!simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Restriction)) {
                    finalValue.remove(SimpleTypeInheritanceModifier.Restriction);
                }
                // Check if "union" is not contained and if remove "union" from "final" attribute of the new simpleType
                if (!simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Union)) {
                    finalValue.remove(SimpleTypeInheritanceModifier.Union);
                }
            } else {

                // If the type is no build-in datatype
                if (!isBuiltInDatatype(simpleType.getName())) {

                    // Check if the schema of the simpleType has a default value
                    if (typeOldSchemaMap.get(simpleType).getFinalDefaults() != null) {

                        // Remove SimpleTypeInheritanceModifier values not contained in the default value
                        if (!outputSchema.getFinalDefaults().contains(FinalDefault.list)) {
                            finalValue.remove(SimpleTypeInheritanceModifier.List);
                        }
                        if (!outputSchema.getFinalDefaults().contains(FinalDefault.restriction)) {
                            finalValue.remove(SimpleTypeInheritanceModifier.Restriction);
                        }
                        if (!outputSchema.getFinalDefaults().contains(FinalDefault.union)) {
                            finalValue.remove(SimpleTypeInheritanceModifier.Union);
                        }
                    } else {

                        // Remove every SimpleTypeInheritanceModifier value from the "final" attribute of the new element
                        finalValue.clear();
                    }
                } else {

                    // Remove every SimpleTypeInheritanceModifier value from the "final" attribute of the new element
                    finalValue.clear();
                }
            }
        }

        // Generate a set containing FinalDefault values for the SimpleTypeInheritanceModifier values contained in the set of the new element
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();

        // Check if "list" is contained and if add "list"
        if (finalValue.contains(SimpleTypeInheritanceModifier.List)) {
            finalDefaults.add(XSDSchema.FinalDefault.list);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (finalValue.contains(SimpleTypeInheritanceModifier.Restriction)) {
            finalDefaults.add(XSDSchema.FinalDefault.restriction);
        }
        // Check if "union" is contained and if add "union"
        if (finalValue.contains(SimpleTypeInheritanceModifier.Union)) {
            finalDefaults.add(XSDSchema.FinalDefault.union);
        }

        // Check if new "final" values is different than the default value of the output schema
        if (!(outputSchema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(outputSchema.getFinalDefaults()))) {
            return finalValue;
        } else {

            // If output schema has the same "finalDefault" value as the new elements "final" attribute return null
            return null;
        }
    }

    /**
     * This method creates a new annotation for a given list of types. Each
     * type may contain an annotation, these annotations are used to contstruct
     * the new annotation, which contains the app infos and documentations of
     * the old annotations.
     *
     * @param types Set of types, which is used to construct a new type. This
     * type will contain the new annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotations contained in the specified types.
     */
    private Annotation getTypeAnnotation(LinkedList<Type> types) {

        // Create new annotation
        Annotation newAnnotation = null;

        // Check each type for a contained annotation
        for (Type type : types) {

            // Check if type contains an annotation
            if (type.getAnnotation() != null) {
                Annotation oldAnnotation = type.getAnnotation();

                // If new annotation is still null initialize new annotation
                if (newAnnotation == null) {
                    newAnnotation = new Annotation();
                }

                // Get all old appInfos
                LinkedList<AppInfo> oldAppInfos = oldAnnotation.getAppInfos();

                // For each old appInfo create a new appInfo and add it to the annotation
                for (Iterator<AppInfo> it = oldAppInfos.iterator(); it.hasNext();) {
                    AppInfo oldAppInfo = it.next();

                    // Create new appInfo and add it to the appInfo list of the new annotation
                    newAnnotation.addAppInfos(generateNewAppInfo(oldAppInfo));
                }

                // Get all old documentations
                LinkedList<Documentation> oldDocumentations = oldAnnotation.getDocumentations();

                // For each old documentation create a new documentation and add it to the annotation
                for (Iterator<Documentation> it = oldDocumentations.iterator(); it.hasNext();) {
                    Documentation oldDocumentation = it.next();

                    // Create new documentation and add it to the documentation list of the new annotation
                    newAnnotation.addDocumentations(generateNewDocumentation(oldDocumentation));
                }
            }
        }
        return newAnnotation;
    }

    /**
     * Checks if the specified particle has no empty content. The contetn is
     * empty if no elements are contained or if elements never appear in a
     * valid XML XSDSchema instance under an element with this type definition.
     *
     * @param particle Particle for which the check is performed
     * @return <tt>true</tt> if the particle has no empty content.
     */
    private boolean hasNoEmptyContent(Particle particle) {

        // Check if the current particle is a particle
        if (particle instanceof ParticleContainer) {

            // Check if particle container is counting pattern
            if (particle instanceof CountingPattern) {
                CountingPattern countingPattern = (CountingPattern) particle;

                // If counting pattern has zero as maximal occurrence return false
                if (countingPattern.getMax() != null && countingPattern.getMax() == 0) {
                    return false;
                }
            }

            // For each particle contained in the particle container check if elements are contained
            ParticleContainer particleContainer = (ParticleContainer) particle;
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();

                // If the contained particle contains elements return true
                if (hasNoEmptyContent(containedParticle)) {
                    return true;
                }
            }
        } else if (particle instanceof GroupRef) {

            // If particle is a group reference check referenced group
            Group group = (Group) ((GroupRef) particle).getGroup();
            return hasNoEmptyContent(group.getParticleContainer());

        } else if (particle instanceof ElementRef && !((ElementRef) particle).getElement().getAbstract()) {

            // If the particle is an element reference and the referenced element is not abstract return true
            return true;
        } else if (particle instanceof Element || particle instanceof AnyPattern && particleUnionGenerator.getContainedElements((AnyPattern) particle) != null) {

            // If particle is element or any pattern return true
            return true;
        }

        // In all other cases return false
        return false;
    }

    /**
     * Checks if a particle contains a strict validated any pattern
     * @param particle Particle, which may contain a strict validated any
     * pattern.
     * @return <tt>true</tt> if the particle contains strict validated any
     * pattern.
     */
    private boolean containsStrictAnyPattern(Particle particle) {

        // Check if the current particle is a particle
        if (particle instanceof ParticleContainer) {

            // For each particle contained in the particle container check if any patterns are contained
            ParticleContainer particleContainer = (ParticleContainer) particle;
            for (Iterator<Particle> it = particleContainer.getParticles().iterator(); it.hasNext();) {
                Particle containedParticle = it.next();
                return containsStrictAnyPattern(containedParticle);

            }
        } else if (particle instanceof GroupRef) {

            // If particle is a group reference check referenced group
            Group group = (Group) ((GroupRef) particle).getGroup();
            return containsStrictAnyPattern(group.getParticleContainer());

        } else if (particle instanceof AnyPattern && particleUnionGenerator.getContainedElements((AnyPattern) particle) != null) {

            // If particle or any pattern return true
            return true;
        }

        // In all other cases return false
        return false;
    }

    /**
     * Checks if a list of types contains a strict validated any pattern
     * @param types List of types, which may contain a strict validated any
     * pattern.
     * @return <tt>true</tt> if the particle contains strict validated any
     * pattern.
     */
    private boolean containsStrictAnyPattern(LinkedList<Type> types) {

        // Check for each type if a strict validates any pattern is contained
        for (Type type : types) {

            // Only complexTypes can contain any patterns
            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;

                // Check if compleType contains strict any pattern
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                    return containsStrictAnyPattern(complexContentType.getParticle());
                }
            }
        }
        return false;
    }
    
    /**
     * This method checks whether the type, generate from the specified type
     * list, is anonymous or not. It is anonymous if all types contained in the
     * type list are anoymous.
     *
     * @param types List of types used to construct a new type.
     * @return <tt>true</tt> if all specified types are anonymous, else
     * <tt>false</tt>.
     */
    private boolean isAnonymous(LinkedList<Type> types) {

        // If type list contains more than one type the return false
        if (types.size() != 1 || containsStrictAnyPattern(types) || !types.getFirst().getNamespace().equals(outputSchema.getTargetNamespace())) {
            return false;
        }

        // Check for each type, if the type is anonymous
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // If a single type is not anonymous return false
            if (!type.isAnonymous()) {
                return false;
            }
        }

        // If all given types are anonymous, true is returned
        return true;
    }

    /**
     * Check if the specified name belongs to a build-in datatype.
     *
     * @param name Name of the type, which may be a build-in datatype.
     * @return <tt>true</tt> if the name belongs to a build-in datatype, else
     * <tt>false</tt>.
     */
    public boolean isBuiltInDatatype(String name) {

        // Compare each build-in datatype with the specified name
        for (BuiltInDatatypes builtInDatatypes : BuiltInDatatypes.values()) {

            // If the build-in datatype has the same name return true
            if (builtInDatatypes.uri().equals(name)) {
                return true;
            }
        }

        // If no build-in datatype with this name exist return false
        return false;
    }

    /**
     * Generates a new type for a type reference.
     *
     * @param type Type which is referenced and for which a new type is
     * generated.
     */
    private void generateTypeReference(Type type) {

        // If the base type is anonymous (the type is locally defined and is not referenced elsewhere) a new type is created
        if (type.isAnonymous()) {
            generateNewType(type);
        } else {
            generateNewTopLevelType(type);
        }
    }

    // Enumeration containing all build-in datatypes
    private enum BuiltInDatatypes {

        STRING("{http://www.w3.org/2001/XMLSchema}string"),
        BOOLEAN("{http://www.w3.org/2001/XMLSchema}boolean"),
        DECIMAL("{http://www.w3.org/2001/XMLSchema}decimal"),
        FLOAT("{http://www.w3.org/2001/XMLSchema}float"),
        DOUBLE("{http://www.w3.org/2001/XMLSchema}double"),
        DURATION("{http://www.w3.org/2001/XMLSchema}duration"),
        DATETIME("{http://www.w3.org/2001/XMLSchema}dateTime"),
        TIME("{http://www.w3.org/2001/XMLSchema}time"),
        DATE("{http://www.w3.org/2001/XMLSchema}date"),
        GYEARMONTH("{http://www.w3.org/2001/XMLSchema}gYearMonth"),
        GYEAR("{http://www.w3.org/2001/XMLSchema}gYear"),
        GMONTHDAY("{http://www.w3.org/2001/XMLSchema}gMonthDay"),
        GDAY("{http://www.w3.org/2001/XMLSchema}gDay"),
        GMONTH("{http://www.w3.org/2001/XMLSchema}gMonth"),
        HEXBINARY("{http://www.w3.org/2001/XMLSchema}hexBinary"),
        BASE64BINARY("{http://www.w3.org/2001/XMLSchema}base64Binary"),
        ANYURI("{http://www.w3.org/2001/XMLSchema}anyURI"),
        QNAME("{http://www.w3.org/2001/XMLSchema}QName"),
        NOTATION("{http://www.w3.org/2001/XMLSchema}NOTATION"),
        NORMALIZEDSTRING("{http://www.w3.org/2001/XMLSchema}normalizedString"),
        TOKEN("{http://www.w3.org/2001/XMLSchema}token"),
        LANGUAGE("{http://www.w3.org/2001/XMLSchema}language"),
        NMTOKEN("{http://www.w3.org/2001/XMLSchema}NMTOKEN"),
        NMTOKENS("{http://www.w3.org/2001/XMLSchema}NMTOKENS"),
        NAME("{http://www.w3.org/2001/XMLSchema}Name"),
        NCNAME("{http://www.w3.org/2001/XMLSchema}NCName"),
        ID("{http://www.w3.org/2001/XMLSchema}ID"),
        IDREF("{http://www.w3.org/2001/XMLSchema}IDREF"),
        IDREFS("{http://www.w3.org/2001/XMLSchema}IDREFS"),
        ENTITY("{http://www.w3.org/2001/XMLSchema}ENTITY"),
        ENTITIES("{http://www.w3.org/2001/XMLSchema}ENTITIES"),
        INTEGER("{http://www.w3.org/2001/XMLSchema}integer"),
        NONPOSITIVEINTEGER("{http://www.w3.org/2001/XMLSchema}nonPositiveInteger"),
        NEGATIVEINTEGER("{http://www.w3.org/2001/XMLSchema}negativeInteger"),
        LONG("{http://www.w3.org/2001/XMLSchema}long"),
        INT("{http://www.w3.org/2001/XMLSchema}int"),
        SHORT("{http://www.w3.org/2001/XMLSchema}short"),
        BYTE("{http://www.w3.org/2001/XMLSchema}byte"),
        NONNEGATIVEINTEGER("{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"),
        UNSIGNEDLONG("{http://www.w3.org/2001/XMLSchema}unsignedLong"),
        UNSIGNEDINT("{http://www.w3.org/2001/XMLSchema}unsignedInt"),
        UNSIGNEDSHORT("{http://www.w3.org/2001/XMLSchema}unsignedShort"),
        UNSIGNEDBYTE("{http://www.w3.org/2001/XMLSchema}unsignedByte"),
        POSITIVEINTEGER("{http://www.w3.org/2001/XMLSchema}positiveInteger"),
        ANYTYPE("{http://www.w3.org/2001/XMLSchema}anyType"),
        ANYSIMPLETYPE("{http://www.w3.org/2001/XMLSchema}anySimpleType");
        private final String uri;

        BuiltInDatatypes(String uri) {
            this.uri = uri;
        }

        public String uri() {
            return uri;
        }
    }
}
