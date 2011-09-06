package eu.fox7.bonxai.xsd.setOperations.intersection;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.XSDSchema.BlockDefault;
import eu.fox7.bonxai.xsd.XSDSchema.FinalDefault;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;
import eu.fox7.bonxai.xsd.tools.SimpleTypeInheritanceContainer;

import java.util.*;

/**
 * This class is responsible for creating new Types in the resulting
 * schema of an intersection operation. Created Types can be SimpleTypes or
 * ComplexTypes either locally defined or globally defined. If the intersection
 * of types is empty no type can be returned and containing Elements or
 * Attributes are typeless which in turn means that the Element or Attribute
 * itself is not part of the intersection.
 * @author Dominik Wolff
 */
public class TypeIntersectionGenerator {

    // XSDSchema which will contain the intersection of schemata contained in the schema group
    private XSDSchema outputSchema;

    // Map mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> typeOldSchemaMap;

    // HashMap mapping namespaces to output schemata (Can be used if an element with a foreign namespace is referenced)
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // Attribute particle intersection generator of the type intersection generator class
    private AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator;

    // Particle intersection generator of the type intersection generator class
    private ParticleIntersectionGenerator particleIntersectionGenerator;

    /**
     * Constructor of the TypeIntersectionGenerator class.
     *
     * @param outputSchema XSDSchema which will contain the intersection of schemata
     * contained in the schema group.
     * @param namespaceOutputSchemaMap HashMap mapping namespaces to output
     * schemata.
     * @param typeOldSchemaMap Map mapping types to old schemata used to
     * construct the new output schema.
     * @param attributeParticleIntersectionGenerator Attribute particle
     * intersection generator of the type intersection generator class.
     * @param particleIntersectionGenerator Particle intersection generator of
     * the type intersection generator class.
     */
    public TypeIntersectionGenerator(XSDSchema outputSchema,
            LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap,
            LinkedHashMap<Type, XSDSchema> typeOldSchemaMap,
            AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator,
            ParticleIntersectionGenerator particleIntersectionGenerator) {

        //Initialize class fields
        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.typeOldSchemaMap = typeOldSchemaMap;
        this.attributeParticleIntersectionGenerator = attributeParticleIntersectionGenerator;
        this.particleIntersectionGenerator = particleIntersectionGenerator;
    }

    /**
     * Generates a new final value from a set of SimpleTypes. The final attribute
     * of XML XSDSchema restricts type inheritance, so when computing the intersection
     * of types the most restrictive final value is computed.
     *
     * @param simpleTypes Set of SimpleTypes, each SimpleType contains a final
     * value which is used to compute the new final value.
     * @return Set of SimpleTypeInheritanceModifier, which represents the new
     * final value.
     */
    private HashSet<SimpleTypeInheritanceModifier> getSimpleTypeFinal(HashSet<SimpleType> simpleTypes) {

        // Generate new "final" attribute
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();

        // Check for each simpleType the "final" attribute
        for (SimpleType simpleType : simpleTypes) {

            // If "final" attribute is present add contained SimpleTypeInheritanceModifier value from the "final" attribute of the new simpleType
            if (simpleType.getFinalModifiers() != null) {

                // Check if "list" is contained and if add "list" from "final" attribute of the new simpleType
                if (simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.List)) {
                    finalValue.add(SimpleTypeInheritanceModifier.List);
                }
                // Check if "restriction" is contained and if add "restriction" from "final" attribute of the new simpleType
                if (simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Restriction)) {
                    finalValue.add(SimpleTypeInheritanceModifier.Restriction);
                }
                // Check if "union" is contained and if add "union" from "final" attribute of the new simpleType
                if (simpleType.getFinalModifiers().contains(SimpleTypeInheritanceModifier.Union)) {
                    finalValue.add(SimpleTypeInheritanceModifier.Union);
                }
            } else if (typeOldSchemaMap.get(simpleType).getFinalDefaults() != null) {

                // Add SimpleTypeInheritanceModifier values contained in the default value
                if (typeOldSchemaMap.get(simpleType).getFinalDefaults().contains(FinalDefault.list)) {
                    finalValue.add(SimpleTypeInheritanceModifier.List);
                }
                if (typeOldSchemaMap.get(simpleType).getFinalDefaults().contains(FinalDefault.restriction)) {
                    finalValue.add(SimpleTypeInheritanceModifier.Restriction);
                }
                if (typeOldSchemaMap.get(simpleType).getFinalDefaults().contains(FinalDefault.union)) {
                    finalValue.add(SimpleTypeInheritanceModifier.Union);
                }
            }

            // If the type is a build-in datatype
            if (isBuiltInDatatype(simpleType.getName())) {
                return null;
            }
        }

        // Generate a set containing FinalDefault values for the Final values contained in the set of the new simpleType
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

            // If output schema has the same "finalDefault" value as the new simpleTypes "final" attribute return null
            return null;
        }
    }

    /**
     * Checks if a simpleType can contain no content.
     *
     * @param simpleType SimpleType, which may be able to be empty.
     * @return <tt>true<tt/> iff the simpleType can be empty.
     */
    private boolean canBeEmpty(SimpleType simpleType) {

        // Check simpleType inheritance
        if (simpleType.getInheritance() instanceof SimpleContentList) {
            SimpleContentList simpleContentList = (SimpleContentList) simpleType.getInheritance();

            // Check if the item type of the list can contain empty content
            canBeEmpty((SimpleType) simpleContentList.getBase());

        } else if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();

            // Check if restriction allowes empty content
            if ((!simpleContentRestriction.getEnumeration().isEmpty() && !simpleContentRestriction.getEnumeration().contains("")) ||
                    (simpleContentRestriction.getLength() != null && new Double(simpleContentRestriction.getLength().getValue()) > 0) ||
                    (simpleContentRestriction.getMinLength() != null && new Double(simpleContentRestriction.getMinLength().getValue()) > 0) ||
                    (simpleContentRestriction.getTotalDigits() != null && new Double(simpleContentRestriction.getTotalDigits().getValue()) > 0)) {
                return false;
            }
            return canBeEmpty((SimpleType) simpleContentRestriction.getBase());

        } else if (simpleType.getInheritance() instanceof SimpleContentUnion) {
            SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();

            // Check each member type of the union if one can be empty the union can be empty
            for (Iterator<SymbolTableRef<Type>> it = simpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
                SymbolTableRef<Type> symbolTableRef = it.next();

                if (!canBeEmpty((SimpleType) symbolTableRef.getReference())) {
                    return false;
                }
            }
        } else if (simpleType.getInheritance() == null) {

            // List of build-in datatypes which can be empty
            LinkedHashSet<String> typesAllowingEmpty = new LinkedHashSet<String>();
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}string");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}anySimpleType");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}anyURI");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}base64Binary");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}hexBinary");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}normalizedString");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}token");
            typesAllowingEmpty.add("{http://www.w3.org/2001/XMLSchema}anyType");

            // Return true if the current build-in datatype is contained in the list
            if (typesAllowingEmpty.contains(simpleType.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a new final value from a set of ComplexTypes. The final attribute
     * of XML XSDSchema restricts type inheritance, so when computing the intersection
     * of types the most restrictive final value is computed.
     *
     * @param complexTypes Set of ComplexTypes, each ComplexType contains a final
     * value which is used to compute the new final value.
     * @return Set of ComplexTypeInheritanceModifier, which represents the new
     * final value.
     */
    private HashSet<ComplexTypeInheritanceModifier> getComplexTypeFinal(HashSet<ComplexType> complexTypes) {

        // Generate new "final" attribute
        HashSet<ComplexTypeInheritanceModifier> finalValue = new HashSet<ComplexTypeInheritanceModifier>();

        // Check for each complexType the "final" attribute
        for (ComplexType complexType : complexTypes) {

            // If "final" attribute is present add contained Final value from the "final" attribute of the new complexType
            if (complexType.getFinalModifiers() != null) {

                // Check if "extension" is contained and if add "extension" from "final" attribute of the new complexType
                if (complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Extension)) {
                    finalValue.add(ComplexTypeInheritanceModifier.Extension);
                }
                // Check if "restriction" is contained and if add "restriction" from "final" attribute of the new complexType
                if (!complexType.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Restriction)) {
                    finalValue.add(ComplexTypeInheritanceModifier.Restriction);
                }
            } else if (typeOldSchemaMap.get(complexType).getFinalDefaults() != null) {

                // Add Final values contained in the default value
                if (typeOldSchemaMap.get(complexType).getFinalDefaults().contains(FinalDefault.extension)) {
                    finalValue.add(ComplexTypeInheritanceModifier.Extension);
                }
                if (typeOldSchemaMap.get(complexType).getFinalDefaults().contains(FinalDefault.restriction)) {
                    finalValue.add(ComplexTypeInheritanceModifier.Restriction);
                }
            }
        }

        // Generate a set containing FinalDefault values for the Final values contained in the set of the new complexType
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

            // If output schema has the same "finalDefault" value as the new complexTypes "final" attribute return null
            return null;
        }
    }

    /**
     * Generate new simpleType for a given set of simpleTypes. New simpleType
     * can be anonymous or not, depending on the given simpleTypes.
     *
     * @param simpleTypes Set of simpleTypes used to construct the new
     * simpleType.
     * @return New simpleType, which is the result of the intersection of given
     * simpleTypes.
     */
    public SimpleType generateNewSimpleType(LinkedHashSet<SimpleType> simpleTypes) {

        // If a single type is not anonymous the new type is not too.
        boolean anonymous = true;
        for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
            SimpleType simpleType = it.next();

            if (simpleType == null || !simpleType.isAnonymous() || !simpleType.getNamespace().equals(outputSchema.getTargetNamespace())) {
                anonymous = false;
            }
        }

        // Get name of the new simpleType
        String newTypeName = getTypeName(new LinkedHashSet<Type>(simpleTypes));
        SimpleType simpleType = null;

        // Create new simpleType depending on if the type is anonymous or not
        if (anonymous) {
            simpleType = generateNewAnonymousSimpleType(simpleTypes, newTypeName);
        } else {
            simpleType = generateNewTopLevelSimpleType(simpleTypes, newTypeName);
        }
        return simpleType;
    }

    /**
     * For a given SimpleType a set of SimpleTypeInheritanceContainers is returned,
     * these contain among other things information about the base types. Each base
     * type is a ur base of the specified SimpleType which is derived for this
     * types by a chain of restrictions, lists and unions. Furthermore the
     * SimpleTypeInheritanceContainers contain information about how the base
     * types were restricted. This information is important to calculate the
     * intersection of SimpleTypes.
     *
     * @param simpleType Type for which the informations are gathered.
     * @return Set of SimpleTypeInheritanceContainers, these are simple containers
     * to store informations which are found on the way to the base type.
     */
    private LinkedHashSet<SimpleTypeInheritanceContainer> getSimpleTypeInheritanceInformation(SimpleType simpleType) {

        // Generate new set containing inheritance informations for the given simpleType
        LinkedHashSet<SimpleTypeInheritanceContainer> simpleTypeInheritanceInformation = new LinkedHashSet<SimpleTypeInheritanceContainer>();

        // Check if simpleType has a list inheritance
        if (simpleType.getInheritance() instanceof SimpleContentList) {
            SimpleContentList simpleContentList = (SimpleContentList) simpleType.getInheritance();
            simpleTypeInheritanceInformation.addAll(getSimpleTypeInheritanceInformation((SimpleType) simpleContentList.getBase()));

            // For each found inheritance information in the list add list header
            for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceInformation.iterator(); it.hasNext();) {
                SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();
                simpleTypeInheritanceContainer.setHasList(true);
                simpleTypeInheritanceContainer.addSimpleContentList(simpleContentList);
            }
        } else if (simpleType.getInheritance() instanceof SimpleContentUnion) {
            SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();
            LinkedList<SymbolTableRef<Type>> memberTypes = simpleContentUnion.getAllMemberTypes();

            // Get inheritance information for each member type of the union
            for (Iterator<SymbolTableRef<Type>> it = memberTypes.iterator(); it.hasNext();) {
                SymbolTableRef<Type> symbolTableRef = it.next();
                simpleTypeInheritanceInformation.addAll(getSimpleTypeInheritanceInformation((SimpleType) symbolTableRef.getReference()));
            }
        } else if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
            simpleTypeInheritanceInformation.addAll(getSimpleTypeInheritanceInformation((SimpleType) simpleContentRestriction.getBase()));

            // Get inheritance information for the contained base type and add facets to it
            for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceInformation.iterator(); it.hasNext();) {
                SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

                // Check if base type is a list or another type variant
                if (((SimpleType) simpleContentRestriction.getBase()).getInheritance() instanceof SimpleContentList) {
                    simpleTypeInheritanceContainer.setHasSimpleTypeRestriction(true);
                    LinkedHashSet<SimpleContentList> simpleContentLists = new LinkedHashSet<SimpleContentList>();
                    simpleContentLists.add((SimpleContentList) ((SimpleType) simpleContentRestriction.getBase()).getInheritance());
                    simpleTypeInheritanceContainer.setSimpleContentLists(simpleContentLists);
                    simpleTypeInheritanceContainer.setListEnumeration(simpleContentRestriction.getEnumeration());
                    simpleTypeInheritanceContainer.setListLength(simpleContentRestriction.getLength());
                    simpleTypeInheritanceContainer.setListMaxLength(simpleContentRestriction.getMaxLength());
                    simpleTypeInheritanceContainer.setListMinLength(simpleContentRestriction.getMinLength());
                    simpleTypeInheritanceContainer.addListPattern(simpleContentRestriction.getPattern());
                    simpleTypeInheritanceContainer.setListWhitespace(simpleContentRestriction.getWhitespace());

                } else {
                    simpleTypeInheritanceContainer.setHasSimpleTypeRestriction(true);
                    simpleTypeInheritanceContainer.setSimpleTypeEnumeration(simpleContentRestriction.getEnumeration());
                    simpleTypeInheritanceContainer.setSimpleTypeFractionDigits(simpleContentRestriction.getFractionDigits());
                    simpleTypeInheritanceContainer.setSimpleTypeLength(simpleContentRestriction.getLength());
                    simpleTypeInheritanceContainer.setSimpleTypeMaxExclusive(simpleContentRestriction.getMaxExclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMaxInclusive(simpleContentRestriction.getMaxInclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMaxLength(simpleContentRestriction.getMaxLength());
                    simpleTypeInheritanceContainer.setSimpleTypeMinExclusive(simpleContentRestriction.getMinExclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMinInclusive(simpleContentRestriction.getMinInclusive());
                    simpleTypeInheritanceContainer.setSimpleTypeMinLength(simpleContentRestriction.getMinLength());
                    simpleTypeInheritanceContainer.addSimpleTypePattern(simpleContentRestriction.getPattern());
                    simpleTypeInheritanceContainer.setSimpleTypeTotalDigits(simpleContentRestriction.getTotalDigits());
                    simpleTypeInheritanceContainer.setSimpleTypeWhitespace(simpleContentRestriction.getWhitespace());
                }

            }
        } else if (simpleType.getInheritance() == null) {

            // Build-in types are the base of the inheritance information
            SimpleTypeInheritanceContainer inheritanceContainer = new SimpleTypeInheritanceContainer();
            inheritanceContainer.setBase(simpleType);
            simpleTypeInheritanceInformation.add(inheritanceContainer);
        }
        return simpleTypeInheritanceInformation;
    }

    /**
     * Method generates from a given set of SimpleTypes a new SimpleType
     * inheritance structure. This is necessary because the intersection of two
     * SimpleTypes has to consider type derivation. I.e. if to SimpleTypes
     * have the same base Type for example xs:string and both SimpleTypes are
     * derived from xs:string via restriction an intersection of these
     * restrictions has to be constructed. This only gets more more complicated
     * considering that XML XSDSchema allows type derivation via restiction, list
     * and union. In order to construct a correct intersection derivation path
     * informations are stored in SimpleTypeInheritanceContainer which are then
     * intersected and translated back into a new type structure.
     *
     * @param simpleTypes Set of SimpleTypes each contains its own
     * SimpleContentInheritance.
     * @return A new SimpleTypeInheritance if the intersection is not empty
     * otherwise a null pointer.
     */
    private SimpleTypeInheritance generateNewSimpleTypeInheritance(LinkedHashSet<SimpleType> simpleTypes) {

        // For each SimpleType its base types are found and stored in the simpleTypeInheritanceInformationMap, base types are the types from which the SimpleType is derived
        HashMap<SimpleType, HashSet<SimpleTypeInheritanceContainer>> simpleTypeInheritanceInformationMap = new HashMap<SimpleType, HashSet<SimpleTypeInheritanceContainer>>();
        for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
            SimpleType simpleType = it.next();
            simpleTypeInheritanceInformationMap.put(simpleType, getSimpleTypeInheritanceInformation(simpleType));
        }

        // Compare each set of base types with all other sets to find intersecting base types
        for (Iterator<SimpleType> it = simpleTypeInheritanceInformationMap.keySet().iterator(); it.hasNext();) {
            SimpleType simpleType = it.next();
            HashSet<SimpleTypeInheritanceContainer> newSimpleTypeInheritanceInformations = new HashSet<SimpleTypeInheritanceContainer>();

            for (Iterator<SimpleTypeInheritanceContainer> it2 = simpleTypeInheritanceInformationMap.get(simpleType).iterator(); it2.hasNext();) {
                SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it2.next();
                SimpleType buildInType = simpleTypeInheritanceContainer.getBase();

                // Tests if the type buildInType is present in each set of base types
                boolean inIntersection = false;
                for (Iterator<SimpleType> it3 = simpleTypeInheritanceInformationMap.keySet().iterator(); it3.hasNext();) {
                    SimpleType otherSimpleType = it3.next();

                    for (Iterator<SimpleTypeInheritanceContainer> it4 = simpleTypeInheritanceInformationMap.get(otherSimpleType).iterator(); it4.hasNext();) {
                        SimpleType otherBuildInType = it4.next().getBase();

                        if (otherBuildInType != buildInType && otherBuildInType.getName().equals(buildInType.getName())) {
                            inIntersection = true;
                        }
                    }
                }

                // If buildInType is present in each set of base types it will be present in the intersection
                if (inIntersection || simpleTypeInheritanceInformationMap.keySet().size() == 1) {
                    newSimpleTypeInheritanceInformations.add(simpleTypeInheritanceContainer);
                }
            }
            simpleTypeInheritanceInformationMap.put(simpleType, newSimpleTypeInheritanceInformations);
        }

        // Get list of base types
        LinkedHashSet<String> baseTypeNames = new LinkedHashSet<String>();

        for (Iterator<SimpleType> it = simpleTypeInheritanceInformationMap.keySet().iterator(); it.hasNext();) {
            SimpleType simpleType = it.next();

            if (simpleTypeInheritanceInformationMap.get(simpleType).isEmpty()) {
                return null;
            }
            for (Iterator<SimpleTypeInheritanceContainer> it2 = simpleTypeInheritanceInformationMap.get(simpleType).iterator(); it2.hasNext();) {
                SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it2.next();
                baseTypeNames.add(simpleTypeInheritanceContainer.getBase().getName());
            }
        }

        // List of new inheritanceContainers
        LinkedList<SimpleTypeInheritanceContainer> simpleTypeInheritanceContainers = new LinkedList<SimpleTypeInheritanceContainer>();

        for (Iterator<String> it = baseTypeNames.iterator(); it.hasNext();) {
            String baseTypeName = it.next();

            SimpleType[] simpleTypeArray = new SimpleType[simpleTypes.size()];
            int position = 0;

            for (SimpleType simpleType : simpleTypes) {
                simpleTypeArray[position] = simpleType;
                position++;
            }
            position = 0;

            simpleTypeInheritanceContainers.addAll(generateNewSimpleTypeInheritanceContainers(simpleTypeArray, position, new HashSet<SimpleTypeInheritanceContainer>(), simpleTypeInheritanceInformationMap, baseTypeName));
        }

        // If the list of inheritanceContainers is empty null is returned
        System.out.print(simpleTypeInheritanceContainers.size());
        if (simpleTypeInheritanceContainers.isEmpty()) {
            return null;
        }

        HashMap<SimpleTypeInheritanceContainer, SimpleType> simpleTypeMap = new HashMap<SimpleTypeInheritanceContainer, SimpleType>();

        // For each SimpleTypeInheritanceContainer build a new SimpleType with or without restriction
        for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

            SimpleType newSimpleType = new SimpleType(simpleTypeInheritanceContainer.getBase().getName(), null, false);
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

            if (simpleTypeInheritanceContainer.isHasSimpleTypeRestriction()) {
                newSimpleType = generateNewGlobalSimpleTypeWithRestriction(simpleTypeInheritanceContainer);
            }
            simpleTypeMap.put(simpleTypeInheritanceContainer, newSimpleType);
        }

        // List of memberTypes of the SimpleContentUnion which is returned by this method
        LinkedList<SymbolTableRef<Type>> globalMemberTypes = new LinkedList<SymbolTableRef<Type>>();
        HashMap<LinkedList<SimpleTypeInheritanceContainer>, SimpleType> listMap = new HashMap<LinkedList<SimpleTypeInheritanceContainer>, SimpleType>();

        for (Iterator<SimpleTypeInheritanceContainer> it = simpleTypeInheritanceContainers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer simpleTypeInheritanceContainer = it.next();

            // If inheritance path contains no list add type to memeberTypes list
            if (!simpleTypeInheritanceContainer.isHasList()) {
                globalMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(simpleTypeMap.get(simpleTypeInheritanceContainer).getName()));

            } else {

                // Find containers with same list
                LinkedList<SimpleTypeInheritanceContainer> containersWithSameList = new LinkedList<SimpleTypeInheritanceContainer>();
                for (Iterator<SimpleTypeInheritanceContainer> it2 = simpleTypeInheritanceContainers.iterator(); it2.hasNext();) {
                    SimpleTypeInheritanceContainer otherSimpleTypeInheritanceContainer = it2.next();

                    if (simpleTypeInheritanceContainer.getSimpleContentLists().containsAll(otherSimpleTypeInheritanceContainer.getSimpleContentLists()) &&
                            otherSimpleTypeInheritanceContainer.getSimpleContentLists().containsAll(simpleTypeInheritanceContainer.getSimpleContentLists())) {
                        containersWithSameList.add(otherSimpleTypeInheritanceContainer);
                    }
                }
                SimpleType newListSimpleType = null;

                // If the new list should be unique the list of containers with same list has size one
                if (containersWithSameList.size() == 1) {
                    SimpleContentList newSimpleContentList = new SimpleContentList(outputSchema.getTypeSymbolTable().getReference(simpleTypeMap.get(simpleTypeInheritanceContainer).getName()));
                    String newListTypeName = getListTypeName();
                    newListSimpleType = new SimpleType(newListTypeName, newSimpleContentList, false);

                    outputSchema.getTypeSymbolTable().updateOrCreateReference(newListSimpleType.getName(), newListSimpleType);

                    if (!outputSchema.getTypes().contains(newListSimpleType)) {
                        outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newListSimpleType.getName()));
                    }
                } else {

                    // If the no list exists
                    if (listMap.get(containersWithSameList) == null) {

                        // Find memberTypes for new SimpleContentUnion
                        LinkedList<SymbolTableRef<Type>> newMemberTypes = new LinkedList<SymbolTableRef<Type>>();
                        for (Iterator<SimpleTypeInheritanceContainer> it2 = containersWithSameList.iterator(); it2.hasNext();) {
                            SimpleTypeInheritanceContainer inheritanceContainer = it2.next();
                            newMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(simpleTypeMap.get(inheritanceContainer).getName()));
                        }

                        // Build new SimpleType with SimpleContentUnion
                        SimpleContentUnion newSimpleContentUnion = new SimpleContentUnion(newMemberTypes);
                        String newUnionTypeName = getUnionTypeName();
                        SimpleType newUnionSimpleType = new SimpleType(newUnionTypeName, newSimpleContentUnion, false);

                        outputSchema.getTypeSymbolTable().updateOrCreateReference(newUnionSimpleType.getName(), newUnionSimpleType);

                        if (!outputSchema.getTypes().contains(newUnionSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newUnionSimpleType.getName()));
                        }

                        // Build new SimpleType with SimpleContentList which derives from the SimpleType with SimpleContentUnion
                        SimpleContentList newSimpleContentList = new SimpleContentList(outputSchema.getTypeSymbolTable().getReference(newUnionSimpleType.getName()));
                        String newListTypeName = getListTypeName();
                        newListSimpleType = new SimpleType(newListTypeName, newSimpleContentList, false);

                        outputSchema.getTypeSymbolTable().updateOrCreateReference(newListSimpleType.getName(), newListSimpleType);

                        if (!outputSchema.getTypes().contains(newListSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newListSimpleType.getName()));
                        }
                        listMap.put(containersWithSameList, newListSimpleType);
                    }
                }

                // Check if list already exists
                if (!simpleTypeInheritanceContainer.getListEnumeration().isEmpty() ||
                        simpleTypeInheritanceContainer.getListLength() != null ||
                        simpleTypeInheritanceContainer.getListMaxLength() != null ||
                        simpleTypeInheritanceContainer.getListMinLength() != null ||
                        !simpleTypeInheritanceContainer.getListPatternList().isEmpty()) {

                    // If list type is not null build new restriction
                    if (newListSimpleType != null) {
                        String restictionTypeName = generateNewGlobalSimpleTypeWithListRestriction(simpleTypeInheritanceContainer, newListSimpleType).getName();
                        globalMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(restictionTypeName));
                    }
                } else {

                    // If list type is not add it to member types
                    if (newListSimpleType != null) {
                        globalMemberTypes.add(outputSchema.getTypeSymbolTable().getReference(newListSimpleType.getName()));
                    }
                }
            }
        }

        // The method retuns a SimpleContentUnion if its memberTypes are not empty
        if (!globalMemberTypes.isEmpty()) {
            SimpleContentUnion simpleContentUnion = new SimpleContentUnion(globalMemberTypes);
            return simpleContentUnion;
        }
        return null;
    }

    /**
     * Generate new container for a given array of containers.
     * @param simpleTypeArray Array of simpleTypes.
     * @param position Postition in the subtrahend array.
     * @param simpleTypeInheritanceContainers Set of inheritance containers.
     * @param simpleTypeInheritanceInformationMap Map mapping simpleTypes to
     * inheritance containers.
     * @param baseTypeName Name of the base type.
     * @return New list of inheritance containers.
     */
    private LinkedList<SimpleTypeInheritanceContainer> generateNewSimpleTypeInheritanceContainers(SimpleType[] simpleTypeArray,
            int position,
            HashSet<SimpleTypeInheritanceContainer> simpleTypeInheritanceContainers,
            HashMap<SimpleType, HashSet<SimpleTypeInheritanceContainer>> simpleTypeInheritanceInformationMap,
            String baseTypeName) {

        // Get list of new containers
        LinkedList<SimpleTypeInheritanceContainer> newSimpleTypeInheritanceContainers = new LinkedList<SimpleTypeInheritanceContainer>();

        // Traverse simpleType array
        if (position < simpleTypeArray.length) {
            SimpleType simpleType = simpleTypeArray[position];
            position++;

            // Get all possible container combinations
            for (SimpleTypeInheritanceContainer simpleTypeInheritanceContainer : simpleTypeInheritanceInformationMap.get(simpleType)) {

                // Use recusive call to get combinations
                if (simpleTypeInheritanceContainer.getBase().getName().equals(baseTypeName)) {
                    simpleTypeInheritanceContainers.add(simpleTypeInheritanceContainer);
                    newSimpleTypeInheritanceContainers.addAll(generateNewSimpleTypeInheritanceContainers(simpleTypeArray, position, simpleTypeInheritanceContainers, simpleTypeInheritanceInformationMap, baseTypeName));
                    simpleTypeInheritanceContainers.remove(simpleTypeInheritanceContainer);
                }
            }
            position--;
            return newSimpleTypeInheritanceContainers;
        } else {

            // Get new container
            SimpleTypeInheritanceContainer newContainer = generateNewSimpleTypeInheritanceContainer(simpleTypeInheritanceContainers);

            // Check if container is empty
            if (newContainer != null) {
                newSimpleTypeInheritanceContainers.add(newContainer);
            }

            // Return new container in list
            return newSimpleTypeInheritanceContainers;
        }
    }

    /**
     * This method generates an intersection of SimpleTypeInheritanceContainers.
     * The new resulting SimpleTypeInheritanceContainer specifies an inheritance
     * path which is later translated into a chain of SimpleTypes. All 
     * SimpleTypeInheritanceContainers should have the same base type else the 
     * intersection is empty.
     * 
     * @param containers Set of  SimpleTypeInheritanceContainers  needed to
     * generate the new resulting SimpleTypeInheritanceContainer.
     * @return Null if no intersection could be generated else a 
     * SimpleTypeInheritanceContainer generated by intersecting the set of 
     * specified SimpleTypeInheritanceContainers.
     */
    private SimpleTypeInheritanceContainer generateNewSimpleTypeInheritanceContainer(HashSet<SimpleTypeInheritanceContainer> containers) {

        // Generate new container
        SimpleTypeInheritanceContainer newContainer = new SimpleTypeInheritanceContainer();
        newContainer.setHasList(true);

        // Add Information from each container to the new container
        for (Iterator<SimpleTypeInheritanceContainer> it = containers.iterator(); it.hasNext();) {
            SimpleTypeInheritanceContainer currentContainer = it.next();

            // If one inheritanceContainers contains a restriction with facets the intersection does as well
            if (currentContainer.isHasSimpleTypeRestriction()) {
                newContainer.setHasSimpleTypeRestriction(true);
            }

            // All base types should be the same
            if (newContainer.getBase() == null) {
                newContainer.setBase(currentContainer.getBase());
            } else if (!newContainer.getBase().getName().equals(currentContainer.getBase().getName())) {
                return null;
            }

            // If a length value is present in the new container and the new and old value are different no intersection is possible
            if (currentContainer.getSimpleTypeLength() != null) {

                if (newContainer.getSimpleTypeLength() != null && newContainer.getSimpleTypeLength().getValue() != currentContainer.getSimpleTypeLength().getValue()) {
                    return null;
                } else {
                    newContainer.setSimpleTypeLength(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getSimpleTypeLength().getValue(), currentContainer.getSimpleTypeLength().getFixed()));
                }
            }

            // If a new minLength is bigger than the old minLength the new minLength replaces the old
            if (currentContainer.getSimpleTypeMinLength() != null) {

                if (newContainer.getSimpleTypeMinLength() == null || (newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMinLength().getValue() < currentContainer.getSimpleTypeMinLength().getValue())) {
                    newContainer.setSimpleTypeMinLength(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getSimpleTypeMinLength().getValue(), currentContainer.getSimpleTypeMinLength().getFixed()));
                }
            }

            // If a new maxLength is smaller than the old maxLength the new maxLength replaces the old
            if (currentContainer.getSimpleTypeMaxLength() != null) {

                if (newContainer.getSimpleTypeMaxLength() == null || (newContainer.getSimpleTypeMaxLength() != null && newContainer.getSimpleTypeMaxLength().getValue() > currentContainer.getSimpleTypeMaxLength().getValue())) {
                    newContainer.setSimpleTypeMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getSimpleTypeMaxLength().getValue(), currentContainer.getSimpleTypeMaxLength().getFixed()));
                }
            }

            // If maxLength is smaller than minLength no intersection is possible
            if (newContainer.getSimpleTypeMaxLength() != null && newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMaxLength().getValue() < newContainer.getSimpleTypeMinLength().getValue()) {
                return null;
            }

            // If minLength or maxLength differ from length the intersection is empty
            if (newContainer.getSimpleTypeLength() != null && (newContainer.getSimpleTypeMaxLength() != null && newContainer.getSimpleTypeMaxLength().getValue() != newContainer.getSimpleTypeLength().getValue() || newContainer.getSimpleTypeMinLength() != null && newContainer.getSimpleTypeMinLength().getValue() != newContainer.getSimpleTypeLength().getValue())) {
                return null;
            }

            // Patterns of different restricitions are added to the pattern list (the pattern intersection may be empty but regardless of this the resulting schema is valid)
            if (!currentContainer.getSimpleTypePatternList().isEmpty()) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it3 = currentContainer.getSimpleTypePatternList().iterator(); it3.hasNext();) {
                    SimpleContentRestrictionProperty<String> pattern = it3.next();

                    if (pattern != null) {
                        newContainer.addSimpleTypePattern(pattern);
                    }
                }
            }

            // If enum list is empty and the current container contains enums add these to the list, else if the list is not empty check all enums contained in both list and remove all other.
            // In case this intersection is empty the whole intersection is empty.
            if (!currentContainer.getSimpleTypeEnumeration().isEmpty()) {

                if (newContainer.getSimpleTypeEnumeration().isEmpty()) {
                    newContainer.setSimpleTypeEnumeration(currentContainer.getSimpleTypeEnumeration());
                } else {
                    LinkedList<String> newEnumeration = new LinkedList<String>();
                    for (Iterator<String> it3 = currentContainer.getSimpleTypeEnumeration().iterator(); it3.hasNext();) {
                        String enumeration = it3.next();

                        if (newContainer.getSimpleTypeEnumeration().contains(enumeration)) {
                            newEnumeration.add(enumeration);
                        }
                    }
                    if (newEnumeration.isEmpty()) {
                        return null;
                    }
                    newContainer.setSimpleTypeEnumeration(newEnumeration);
                }
            }

            // Set whitespace to the most restrictive value
            if (currentContainer.getSimpleTypeWhitespace() != null) {

                if (currentContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Collapse) {
                    newContainer.setSimpleTypeWhitespace(currentContainer.getSimpleTypeWhitespace());
                }
                if (currentContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Replace && (newContainer.getSimpleTypeWhitespace() == null || newContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve)) {
                    newContainer.setSimpleTypeWhitespace(currentContainer.getSimpleTypeWhitespace());
                }
                if (currentContainer.getSimpleTypeWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve && newContainer.getSimpleTypeWhitespace() == null) {
                    newContainer.setSimpleTypeWhitespace(currentContainer.getSimpleTypeWhitespace());
                }
            }

            // If a new maxInclusive is smaller than the old maxInclusive the new maxInclusive replaces the old
            if (currentContainer.getSimpleTypeMaxInclusive() != null) {

                if (newContainer.getSimpleTypeMaxInclusive() == null || (newContainer.getSimpleTypeMaxInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) > new Double(currentContainer.getSimpleTypeMaxInclusive().getValue()))) {
                    newContainer.setSimpleTypeMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(currentContainer.getSimpleTypeMaxInclusive().getValue(), false));
                }
            }

            // If a new maxExculsive is smaller than the old maxExculsive the new maxExculsive replaces the old
            if (currentContainer.getSimpleTypeMaxExclusive() != null) {

                if (newContainer.getSimpleTypeMaxExclusive() == null || (newContainer.getSimpleTypeMaxExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) > new Double(currentContainer.getSimpleTypeMaxExclusive().getValue()))) {
                    newContainer.setSimpleTypeMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(currentContainer.getSimpleTypeMaxExclusive().getValue(), false));
                }
            }
            // If a new minInclusive is bigger than the old minInclusive the new minInclusive replaces the old
            if (currentContainer.getSimpleTypeMinInclusive() != null) {

                if (newContainer.getSimpleTypeMinInclusive() == null || (newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMinInclusive().getValue()) < new Double(currentContainer.getSimpleTypeMinInclusive().getValue()))) {
                    newContainer.setSimpleTypeMinInclusive(new SimpleContentFixableRestrictionProperty<String>(currentContainer.getSimpleTypeMinInclusive().getValue(), false));
                }
            }
            // If a new minExculsive is bigger than the old minExculsive the new minExculsive replaces the old
            if (currentContainer.getSimpleTypeMinExclusive() != null) {

                if (newContainer.getSimpleTypeMinExclusive() == null || (newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMinExclusive().getValue()) < new Double(currentContainer.getSimpleTypeMinExclusive().getValue()))) {
                    newContainer.setSimpleTypeMinExclusive(new SimpleContentFixableRestrictionProperty<String>(currentContainer.getSimpleTypeMinExclusive().getValue(), false));
                }
            }

            // If maxInclusive is smaller than minInclusive no intersection is possible
            if (newContainer.getSimpleTypeMaxInclusive() != null && newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) < new Double(newContainer.getSimpleTypeMinInclusive().getValue())) {
                return null;
            }

            // If maxInclusive is smaller equal than minExclusive no intersection is possible
            if (newContainer.getSimpleTypeMaxInclusive() != null && newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMaxInclusive().getValue()) <= new Double(newContainer.getSimpleTypeMinExclusive().getValue())) {
                return null;
            }

            // If maxExclusive is smaller equal than minInclusive no intersection is possible
            if (newContainer.getSimpleTypeMaxExclusive() != null && newContainer.getSimpleTypeMinInclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(newContainer.getSimpleTypeMinInclusive().getValue())) {
                return null;
            }

            // If maxExclusive is smaller equal than minExclusive no intersection is possible
            if (newContainer.getSimpleTypeMaxExclusive() != null && newContainer.getSimpleTypeMinExclusive() != null && new Double(newContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(newContainer.getSimpleTypeMinExclusive().getValue())) {
                return null;
            }

            // If the old totalDigits value is null the new value replaces the old. Otherwise if the old totalDigits value does not equal the new value the intersection is empty.
            if (currentContainer.getSimpleTypeTotalDigits() != null) {

                if (newContainer.getSimpleTypeTotalDigits() != null && newContainer.getSimpleTypeTotalDigits().getValue() != currentContainer.getSimpleTypeTotalDigits().getValue()) {
                    return null;
                } else {
                    newContainer.setSimpleTypeTotalDigits(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getSimpleTypeTotalDigits().getValue(), currentContainer.getSimpleTypeTotalDigits().getFixed()));
                }
            }

            // The new fractionDigits value is compute analog to the totalDigits value.
            if (currentContainer.getSimpleTypeFractionDigits() != null) {

                if (newContainer.getSimpleTypeFractionDigits() == null || newContainer.getSimpleTypeFractionDigits().getValue() > currentContainer.getSimpleTypeFractionDigits().getValue()) {
                    newContainer.setSimpleTypeFractionDigits(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getSimpleTypeFractionDigits().getValue(), currentContainer.getSimpleTypeFractionDigits().getFixed()));
                }
            }

            // If both a fractionDigits value and a totalDigits value are present the intersection is empty if the fractionDigits value is greater equal than the totalDigits value.
            if (newContainer.getSimpleTypeFractionDigits() != null && newContainer.getSimpleTypeTotalDigits() != null && newContainer.getSimpleTypeFractionDigits().getValue() >= newContainer.getSimpleTypeTotalDigits().getValue()) {
                return null;
            }

            // After building the restriction facets the list facets are computed.
            // Per default a new SimpleTypeInheritanceContainer is set to contain a list but if a SimpleTypeInheritanceContainer of the specified list does not contain a list the field is set to false
            if (currentContainer.isHasList() == false) {
                newContainer.setHasList(false);
            }

            // Add SimpleContentList to list of SimpleContentLists in order to find a new list
            if (!currentContainer.getSimpleContentLists().isEmpty()) {
                for (Iterator<SimpleContentList> it3 = currentContainer.getSimpleContentLists().iterator(); it3.hasNext();) {
                    SimpleContentList simpleContentList = it3.next();

                    if (simpleContentList != null) {
                        newContainer.getSimpleContentLists().add(simpleContentList);
                    }
                }
            }

            // If a length value is present in the new inheritanceContainer and the new and old value are different no intersection is possible
            if (currentContainer.getListLength() != null) {

                if (newContainer.getListLength() != null && newContainer.getListLength().getValue() != currentContainer.getListLength().getValue()) {
                    return null;
                } else {
                    newContainer.setListLength(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getListLength().getValue(), false));
                }
            }

            // If a new minLength is bigger than the old minLength the new minLength replaces the old
            if (currentContainer.getListMinLength() != null) {

                if (newContainer.getListMinLength() != null && newContainer.getListMinLength().getValue() < currentContainer.getListMinLength().getValue()) {
                    newContainer.setListMinLength(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getListMinLength().getValue(), false));
                }
            }

            // If a new maxLength is smaller than the old maxLength the new maxLength replaces the old
            if (currentContainer.getListMaxLength() != null) {

                if (newContainer.getListMaxLength() != null && newContainer.getListMaxLength().getValue() > currentContainer.getListMaxLength().getValue()) {
                    newContainer.setListMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(currentContainer.getListMaxLength().getValue(), false));
                }
            }

            // If maxLength is smaller than minLength no intersection is possible
            if (newContainer.getListMaxLength() != null && newContainer.getListMinLength() != null && newContainer.getListMaxLength().getValue() < newContainer.getListMinLength().getValue()) {
                return null;
            }

            // If minLength or maxLength differ from length the intersection is empty
            if (newContainer.getListLength() != null && (newContainer.getListMaxLength() != null && newContainer.getListMaxLength().getValue() != newContainer.getListLength().getValue() || newContainer.getListMinLength() != null && newContainer.getListMinLength().getValue() != newContainer.getListLength().getValue())) {
                return null;
            }

            // Patterns of different restricitions are added to the pattern list (the pattern intersection may be empty but regardless of this the resulting schema is valid)
            if (!currentContainer.getListPatternList().isEmpty()) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it3 = currentContainer.getListPatternList().iterator(); it3.hasNext();) {
                    SimpleContentRestrictionProperty<String> pattern = it3.next();

                    if (pattern != null) {
                        newContainer.addListPattern(pattern);
                    }
                }
            }

            // If enum list is empty and the current container contains enums add these to the list, else if the list is not empty check all enums contained in both list and remove all other.
            // In case this intersection is empty the whole intersection is empty.
            if (!currentContainer.getListEnumeration().isEmpty()) {

                if (newContainer.getListEnumeration().isEmpty()) {
                    newContainer.setListEnumeration(currentContainer.getListEnumeration());
                } else {
                    LinkedList<String> newListEnumeration = new LinkedList<String>();
                    for (Iterator<String> it3 = currentContainer.getListEnumeration().iterator(); it3.hasNext();) {
                        String enumeration = it3.next();

                        if (newContainer.getListEnumeration().contains(enumeration)) {
                            newListEnumeration.add(enumeration);
                        }
                    }
                    if (newListEnumeration.isEmpty()) {
                        return null;
                    }
                    newContainer.setListEnumeration(newListEnumeration);
                }
            }

            // Set whitespace to the most restrictive value
            if (currentContainer.getListWhitespace() != null) {

                if (currentContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Collapse) {
                    newContainer.setListWhitespace(currentContainer.getListWhitespace());
                }
                if (currentContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Replace && (newContainer.getListWhitespace() == null || newContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve)) {
                    newContainer.setListWhitespace(currentContainer.getListWhitespace());
                }
                if (currentContainer.getListWhitespace().getValue() == SimpleContentPropertyWhitespace.Preserve && newContainer.getListWhitespace() == null) {
                    newContainer.setListWhitespace(currentContainer.getListWhitespace());
                }
            }
        }
        return newContainer;
    }

    /**
     * This method generates for a specified SimpleTypeInheritanceContainer a new
     * SimpleType structure consisting of SimpleTypes with SimpleContentRestrictions.
     * In order to construct the new SimpleTypes the field contained in the
     * container are used to build new restrictions. If the container contains
     * more than one pattern for each pattern a new SimpleType is constructed
     * which contains the last SimpleType as base type.
     *
     * The specified SimpleTypeInheritanceContainer must contain a restiction
     * field else the result is null.
     *
     * @param simpleTypeInheritanceContainer Container which contains information
     * about an inheritance path.
     * @return Null if no restriction fields are present in the
     * SimpleTypeInheritanceContainer else the last SimpleType which was constructed.
     * This SimpleType is a child of all other generated SimpleTypes.
     */
    private SimpleType generateNewGlobalSimpleTypeWithRestriction(SimpleTypeInheritanceContainer simpleTypeInheritanceContainer) {

        // Check if restriction is contained in inheritance information
        if (simpleTypeInheritanceContainer.isHasSimpleTypeRestriction()) {

            // Create new restriction with base type as base
            String baseName = simpleTypeInheritanceContainer.getBase().getName();
            SimpleContentRestriction newSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(baseName));

            // Set enumeration and length
            newSimpleContentRestriction.setEnumeration(simpleTypeInheritanceContainer.getSimpleTypeEnumeration());
            newSimpleContentRestriction.setLength(simpleTypeInheritanceContainer.getSimpleTypeLength());

            // If both maxExclusive and maxInclusive are present
            if (simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive() != null && simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive() != null) {

                // If maxExclusive is smaller or equal maxInclusive use maxExclusive else maxInclusive
                if (new Double(simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive().getValue()) <= new Double(simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive().getValue())) {
                    newSimpleContentRestriction.setMaxExclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive());
                } else {
                    newSimpleContentRestriction.setMaxInclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive());
                }
            } else {

                // Set maxExclusive or maxInclusive if both are not present at the same time
                newSimpleContentRestriction.setMaxExclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxExclusive());
                newSimpleContentRestriction.setMaxInclusive(simpleTypeInheritanceContainer.getSimpleTypeMaxInclusive());
            }

            // If both minExclusive and minInclusive are present
            if (simpleTypeInheritanceContainer.getSimpleTypeMinExclusive() != null && simpleTypeInheritanceContainer.getSimpleTypeMinInclusive() != null) {

                // If minExclusive is greater or equal minInclusive use minExclusive else minInclusive
                if (new Double(simpleTypeInheritanceContainer.getSimpleTypeMinExclusive().getValue()) >= new Double(simpleTypeInheritanceContainer.getSimpleTypeMinInclusive().getValue())) {
                    newSimpleContentRestriction.setMinExclusive(simpleTypeInheritanceContainer.getSimpleTypeMinExclusive());
                } else {
                    newSimpleContentRestriction.setMinInclusive(simpleTypeInheritanceContainer.getSimpleTypeMinInclusive());
                }
            } else {

                // Set minExclusive or minInclusive if both are not present at the same time
                newSimpleContentRestriction.setMinExclusive(simpleTypeInheritanceContainer.getSimpleTypeMinExclusive());
                newSimpleContentRestriction.setMinInclusive(simpleTypeInheritanceContainer.getSimpleTypeMinInclusive());
            }

            // Set maxLength, minLength, totalDigits, fractionDigits and Whitespace
            newSimpleContentRestriction.setMaxLength(simpleTypeInheritanceContainer.getSimpleTypeMaxLength());
            newSimpleContentRestriction.setMinLength(simpleTypeInheritanceContainer.getSimpleTypeMinLength());
            newSimpleContentRestriction.setTotalDigits(simpleTypeInheritanceContainer.getSimpleTypeTotalDigits());
            newSimpleContentRestriction.setFractionDigits(simpleTypeInheritanceContainer.getSimpleTypeFractionDigits());
            newSimpleContentRestriction.setWhitespace(simpleTypeInheritanceContainer.getSimpleTypeWhitespace());

            // Create new type which contains the restriction
            String newTypeName = getRestrictionTypeName();
            SimpleType newSimpleType = new SimpleType(newTypeName, newSimpleContentRestriction, false);

            // Add type to schema
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

            if (!outputSchema.getTypes().contains(newSimpleType)) {
                outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
            }

            // Create new types for each pattern contained
            if (simpleTypeInheritanceContainer.getSimpleTypePatternList().size() >= 1) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it = simpleTypeInheritanceContainer.getSimpleTypePatternList().iterator(); it.hasNext();) {
                    SimpleContentRestrictionProperty<String> pattern = it.next();
                    newSimpleContentRestriction.setPattern(pattern);

                    // If another pattern exists
                    if (it.hasNext()) {

                        // Create new restriction with base type as base
                        SimpleContentRestriction nextSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
                        String nextTypeName = getRestrictionTypeName();

                        // Create new type which contains the restriction
                        SimpleType nextSimpleType = new SimpleType(nextTypeName, nextSimpleContentRestriction, false);

                        // Add type to schema
                        outputSchema.getTypeSymbolTable().updateOrCreateReference(nextSimpleType.getName(), nextSimpleType);

                        if (!outputSchema.getTypes().contains(nextSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(nextSimpleType.getName()));
                        }

                        // Update next restriction
                        newSimpleContentRestriction = nextSimpleContentRestriction;
                    }
                }
            }
            return newSimpleType;
        }
        return null;
    }

    /**
     * Constructs for a given SimpleType, which should be a SimpleType with
     * SimpleContentList inheritance, and a specified SimpleTypeInheritanceContainer
     * a new SimpleType, which restricts the given SimpleType. If the
     * SimpleTypeInheritanceContainer does not contain any restrictions for list
     * a null pointer is returned.
     *
     * SimpleType should contain a SimpleContentList inheritance if not null is
     * returned.
     *
     * @param simpleTypeInheritanceContainer Container which contains infomations
     * about list restriction.
     * @param simpleType SimpleType with SimpleContentList content, which is the
     * type from which the new type is derived.
     * @return Either a new SimpleType which restricts the specified Type or null.
     */
    private SimpleType generateNewGlobalSimpleTypeWithListRestriction(SimpleTypeInheritanceContainer simpleTypeInheritanceContainer, SimpleType simpleType) {

        // Check if List has facets
        if (!simpleTypeInheritanceContainer.getListEnumeration().isEmpty() ||
                simpleTypeInheritanceContainer.getListLength() != null ||
                simpleTypeInheritanceContainer.getListMaxLength() != null ||
                simpleTypeInheritanceContainer.getListMinLength() != null ||
                !simpleTypeInheritanceContainer.getListPatternList().isEmpty() ||
                simpleType.getInheritance() instanceof SimpleContentList) {

            // Generate new restriction and set new facets
            SimpleContentRestriction newSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(simpleType.getName()));
            newSimpleContentRestriction.setEnumeration(simpleTypeInheritanceContainer.getListEnumeration());
            newSimpleContentRestriction.setLength(simpleTypeInheritanceContainer.getListLength());
            newSimpleContentRestriction.setMaxLength(simpleTypeInheritanceContainer.getListMaxLength());
            newSimpleContentRestriction.setMinLength(simpleTypeInheritanceContainer.getListMinLength());
            newSimpleContentRestriction.setWhitespace(simpleTypeInheritanceContainer.getListWhitespace());

            // Create new type to contain the restriction
            String newTypeName = getRestrictionTypeName();
            SimpleType newSimpleType = new SimpleType(newTypeName, newSimpleContentRestriction, false);
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);

            // Add new type as top-level type to the schema
            if (!outputSchema.getTypes().contains(newSimpleType)) {
                outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
            }

            // Add new restrictions for each pattern contained in the container
            if (simpleTypeInheritanceContainer.getListPatternList().size() >= 1) {

                for (Iterator<SimpleContentRestrictionProperty<String>> it = simpleTypeInheritanceContainer.getListPatternList().iterator(); it.hasNext();) {
                    SimpleContentRestrictionProperty<String> pattern = it.next();
                    newSimpleContentRestriction.setPattern(pattern);

                    if (it.hasNext()) {

                        // Generate new restriction
                        SimpleContentRestriction nextSimpleContentRestriction = new SimpleContentRestriction(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));

                        // Create new type to contain the restriction
                        String nextTypeName = getRestrictionTypeName();
                        SimpleType nextSimpleType = new SimpleType(nextTypeName, nextSimpleContentRestriction, false);
                        outputSchema.getTypeSymbolTable().updateOrCreateReference(nextSimpleType.getName(), nextSimpleType);

                        // Add new type as top-level type to the schema
                        if (!outputSchema.getTypes().contains(nextSimpleType)) {
                            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(nextSimpleType.getName()));
                        }

                        // Set next restriction as new restriction
                        newSimpleContentRestriction = nextSimpleContentRestriction;
                    }
                }
            }
            return newSimpleType;
        }
        return null;
    }

    /**
     * This method generates a new simple content extension by intersecting old
     * extensions. These extensions are contained in the specified set of
     * complexTypes. The intersection will construct a new base type and new
     * attribute particles through intersectiong the old ones.
     *
     * Only complexTypes with simple content and extension are processed all
     * other complexTypes are ignored.
     *
     * @param complexTypes Set of complexTypes, these complexTypes should
     * contain extensions, which are used to create the new simple content
     * extension.
     * @return New SimpleContentExtension if the intersection is not empty, if
     * it is a null pointer will be returned.
     */
    private SimpleContentExtension generateNewSimpleContentExtension(LinkedHashSet<ComplexType> complexTypes) {

        // Get set of contained simple content extensions
        LinkedHashSet<SimpleContentExtension> simpleContentExtensions = new LinkedHashSet<SimpleContentExtension>();

        // For each complex type add contained extension to the extension set
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // Check if complexType contains simple content and extension
            if (complexType.getContent() instanceof SimpleContentType) {
                SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                    SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                    simpleContentExtensions.add(simpleContentExtension);
                }
            }
        }
        // Build list of base types to generate a new base type via intersection
        LinkedHashSet<SimpleType> simpleTypes = new LinkedHashSet<SimpleType>();
        LinkedList<LinkedList<AttributeParticle>> attributeParticleList = new LinkedList<LinkedList<AttributeParticle>>();

        boolean optionalAttributeParticles = true;
        for (Iterator<SimpleContentExtension> it = simpleContentExtensions.iterator(); it.hasNext();) {
            SimpleContentExtension simpleContentExtension = it.next();
            simpleTypes.add((SimpleType) simpleContentExtension.getBase());
            attributeParticleList.add(simpleContentExtension.getAttributes());

            if (!attributeParticleIntersectionGenerator.isOptional(simpleContentExtension.getAttributes())) {
                optionalAttributeParticles = false;
            }
        }
        String newBaseTypeName = getTypeName(new LinkedHashSet<Type>(simpleTypes));
        SimpleType newBase = generateNewTopLevelSimpleType(simpleTypes, newBaseTypeName);


        // Build new AttributeParticles for the new SimpleContentExtension
        LinkedList<AttributeParticle> newAttributeParticles = attributeParticleIntersectionGenerator.generateAttributeParticleIntersection(attributeParticleList);

        if (newAttributeParticles == null && !optionalAttributeParticles) {
            return null;
        }
        SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newBase.getName()));
        newSimpleContentExtension.setAnnotation(getAnnotation());
        newSimpleContentExtension.setId(getSimpleContentExtensionID(simpleContentExtensions));
        newSimpleContentExtension.setAttributes(newAttributeParticles);
        return newSimpleContentExtension;
    }

    /**
     * Create new type name for the specified product type state, which contains
     * types. The new name is constructed by using the names of these contained
     * types.
     *
     * @param productTypeState State, which contains types.
     * @param outputSchema XSDSchema in which the new type is registered. Used to
     * check which type names already exist.
     * @return New type name for the specified type set not used in the output
     * schema yet.
     */
    private String getTypeName(LinkedHashSet<Type> containedTypes) {

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}";

        if (containedTypes.size() != 1) {
            typeName += "intersection-type.";
        }

        // Get set of all contained type names
        LinkedHashSet<String> typeNames = new LinkedHashSet<String>();

        // Add type name to set of conatined type names
        for (Iterator<Type> it = containedTypes.iterator(); it.hasNext();) {
            Type type = it.next();

            if (type != null) {
                typeNames.add(type.getName());
            } else {
                typeNames.add("{http://www.w3.org/2001/XMLSchema}anySimpleType");
            }
        }

        // Build-in data types are not renamed
        if (typeNames.size() == 1 && containedTypes.iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return containedTypes.iterator().next().getName();
        }

        for (Iterator<String> it = typeNames.iterator(); it.hasNext();) {
            String currentTypeName = it.next();

            if (currentTypeName.equals("{http://www.w3.org/2001/XMLSchema}anyType") || currentTypeName.equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                it.remove();
            }
        }

        // Build-in data types are not renamed
        if (typeNames.size() == 1 && containedTypes.iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return containedTypes.iterator().next().getName();
        }

        // Create new name
        for (Iterator<Type> it = containedTypes.iterator(); it.hasNext();) {
            Type type = it.next();

            // Add each type name to the new name
            typeName += type.getLocalName();

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

    /**
     * This method generates an intersection of simple content types, the
     * resulting simple content type contains among others a new inheritance,
     * which in turnis the result of an intersection. The set of
     * SimpleContentTypes is specfied via the set of complexTypes.
     *
     * Each complexType should contain a simple contetn otherwise the
     * complexType is ignored.
     *
     * @param complexTypes Set of complexTypes, each complexType containing a
     * simple content, which is used to compute the intersection.
     * @return Null if the intersection was empty, this is the case i.e. if the
     * new inheritance is null, or a new SimpleContentType if the intersection
     * is not empty.
     */
    private SimpleContentType generateNewSimpleContentType(LinkedHashSet<ComplexType> complexTypes) {

        // Get a set of contained simple content types in oder to construct a new "ID" attribute
        LinkedHashSet<SimpleContentType> simpleContentTypes = new LinkedHashSet<SimpleContentType>();

        // For each complexType check if simple content is contained
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // Check if current content is simple content and add the content to the set
            if (complexType.getContent() instanceof SimpleContentType) {
                SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                simpleContentTypes.add(simpleContentType);
            }
        }

        // Generate new simple content extension (Inheritanc was removed before, so only extension are left)
        SimpleContentInheritance newInheritance = generateNewSimpleContentExtension(complexTypes);

        // If inheritance was constructed generate new contetn or if no return null
        if (newInheritance == null) {
            return null;
        } else {

            // Generate new simple content with new inheritance
            SimpleContentType simpleContentType = new SimpleContentType();
            simpleContentType.setInheritance(newInheritance);

            // Set "ID" attribute and annotation for new content
            simpleContentType.setAnnotation(getAnnotation());
            simpleContentType.setId(getContentID(new LinkedHashSet<Content>(simpleContentTypes)));

            // Return new simple content
            return simpleContentType;
        }
    }

    /**
     * This method generates a new anonymous simpleType by intersecting the set
     * of specified simpleTypes. The new simpleType is given the specified name
     * and is registered in the output schema. In order to intersect simpleTypes
     * other new simpleTypes for the intersected inheritance structures may be
     * constructed.
     *
     * @param simpleTypes Set of simpleTypes which are used to construct the new
     * simpleType.
     * @param newTypeName Name of the new simpleType, containing both namespace
     * and local name of the type.
     * @return Either a simpleType representing the intersection of given
     * simpleTypes or null if the intersection is empty.
     */
    private SimpleType generateNewAnonymousSimpleType(LinkedHashSet<SimpleType> simpleTypes, String newTypeName) {

        // Remove anyType and anySimpleType from type set
        for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
            SimpleType simpleType = it.next();

            if (simpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType") || simpleType.getName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                it.remove();
            }
        }

        // Create new simpleType inheritance for the new anonymous simpleType
        SimpleTypeInheritance newSimpleTypeInheritance = generateNewSimpleTypeInheritance(simpleTypes);

        if (newSimpleTypeInheritance == null) {
            return null;
        }

        // Create new anonymous simpleType with new "ID" attribute and annotation
        SimpleType newSimpleType = new SimpleType(newTypeName, newSimpleTypeInheritance, true);
        newSimpleType.setAnnotation(getAnnotation());
        newSimpleType.setId(getTypeID(new LinkedHashSet<Type>(simpleTypes)));

        // Add new simpleType to the type SymbolTable of the output schema
        outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);
        return newSimpleType;
    }

    /**
     * This method creates a new anonymous complexType. The new complexType
     * results from an intersection of specified complexTypes, that is if the
     * intersection is not empty otherwise null is returned, which means there
     * was no intersection between given complexTypes. If an intersection of
     * complexTypes is empty depends on contained particles, attribute
     * particles,"mixed" attributes and other factors.
     *
     * @param complexTypes Set of complexTypes, the intersection of these types
     * is the result of this method.
     * @param newTypeName New name of the constructed complexType.
     * @param localElementTypes Map containing for each local defined element
     * a corresponding type definition.
     * @return New complexType resulting from the intersection of specified
     * complexTypes or null if the intersection is empty.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if particle automaton is not supported for a given operation.
     */
    private ComplexType generateNewAnonymousComplexType(LinkedHashSet<ComplexType> complexTypes, String newTypeName, LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {

        // Use complexType sets to partition complex types into to different sets
        LinkedHashSet<ComplexType> complexContentComplexTypes = new LinkedHashSet<ComplexType>();
        LinkedHashSet<ComplexType> simpleContentComplexTypes = new LinkedHashSet<ComplexType>();

        // Partition complexTypes into complexTypes with complex content and complexTypes with simple content
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // If complexType has complex content add type to complex content complexType set else if the complexType has simple content add the type to the simple content complexType set
            if (complexType.getContent() instanceof ComplexContentType || complexType.getContent() == null) {
                complexContentComplexTypes.add(complexType);

            } else if (complexType.getContent() instanceof SimpleContentType) {
                simpleContentComplexTypes.add(complexType);
            }
        }

        // Use variables to store the content and attributes of the new complexType
        Content content = null;
        LinkedList<AttributeParticle> newAttributeParticles = null;


        if (!complexContentComplexTypes.isEmpty() && simpleContentComplexTypes.isEmpty()) {

            // If only complexTypes with complexContent exist generate new complex content generate new complex content for the new complexType

            content = generateNewComplexContentType(complexContentComplexTypes, localElementTypes);


            // If content is ComplexContentType then all attribute particles are contained in the complexType, so new attribute particles have to be constructed here
            // Get new list of attribute particle lists (used to construct the new attribute particle list of the new complexType)
            LinkedList<LinkedList<AttributeParticle>> attributeParticleList = new LinkedList<LinkedList<AttributeParticle>>();

            // Use variable to check if all attribute particles are optional
            boolean optionalAttributeParticles = true;

            // Check for all complexTypes with complex content if they contain attributes (Inheritance was resolved before)
            for (Iterator<ComplexType> it = complexContentComplexTypes.iterator(); it.hasNext();) {
                ComplexType complexType = it.next();

                // If for the current attribute list not all contained particles are optional set variable to false
                if (!attributeParticleIntersectionGenerator.isOptional(complexType.getAttributes())) {
                    optionalAttributeParticles = false;
                }

                // Add attribute particle list to list
                attributeParticleList.add(complexType.getAttributes());
            }

            // Generate new attribute particle with the attribute particle intersection generator
            newAttributeParticles = attributeParticleIntersectionGenerator.generateAttributeParticleIntersection(attributeParticleList);

            // If no attribute particles were generated and all attribute particles were not optional return no type
            if (newAttributeParticles == null && !optionalAttributeParticles) {
                return null;
            }
        } else if (!simpleContentComplexTypes.isEmpty() && complexContentComplexTypes.isEmpty()) {

            // If only complex Types with simple content exist generate new simple content (Attributes are present in this content and not the complexType)
            content = generateNewSimpleContentType(simpleContentComplexTypes);
        } else if (!simpleContentComplexTypes.isEmpty() && !complexContentComplexTypes.isEmpty()) {

            // If both complexTypes with simple and complex content exist check if intersection is still possible
            // Get new list of attribute particle lists (used to construct the new attribute particle list of the new complexType)
            LinkedList<LinkedList<AttributeParticle>> attributeParticleList = new LinkedList<LinkedList<AttributeParticle>>();

            // Use variable to check if all attribute particles are optional
            boolean optionalAttributeParticles = true;

            // Store found content in an extra set to compute a new "ID" for the new content
            LinkedHashSet<Content> contents = new LinkedHashSet<Content>();
            boolean allMixed = true;

            // Check for each complexType with complex content if intersection is possible
            for (Iterator<ComplexType> it = complexContentComplexTypes.iterator(); it.hasNext();) {
                ComplexType complexType = it.next();

                // Check if content is really complex content
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                    // Add complex content to content list
                    contents.add(complexContentType);

                    // An intersection of complexTypes with simple and complex content is not empty if all Types with complex content are mixed and have no particles
                    if (!complexType.getMixed() && !complexContentType.getMixed()) {
                        allMixed = false;
                    }
                    if (complexContentType.getParticle() != null && !hasNoEmptyContent(complexContentType.getParticle()) && !particleIntersectionGenerator.isOptional(complexContentType.getParticle())) {
                        return null;
                    }

                    // If for the current attribute list not all contained particles are optional set variable to false
                    if (!attributeParticleIntersectionGenerator.isOptional(complexType.getAttributes())) {
                        optionalAttributeParticles = false;
                    }

                    // Add attribute particle list to list
                    attributeParticleList.add(complexType.getAttributes());
                }
            }

            // Build a set containing all simpleTypes used by extensions and a set containing these extensions
            LinkedHashSet<SimpleType> simpleTypes = new LinkedHashSet<SimpleType>();
            LinkedHashSet<SimpleContentExtension> simpleContentExtensions = new LinkedHashSet<SimpleContentExtension>();

            // For each complexType with simple content check if extension is contained
            for (Iterator<ComplexType> it = simpleContentComplexTypes.iterator(); it.hasNext();) {
                ComplexType complexType = it.next();

                // Check if complexType has simple content
                if (complexType.getContent() instanceof SimpleContentType) {
                    SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                    // Add simple content to content list
                    contents.add(simpleContentType);

                    // Check if simple content contains extension
                    if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                        // Add the base type of the extension to the simpleType set (Inheritance was resolved) and the extension to the extension set
                        simpleTypes.add((SimpleType) simpleContentExtension.getBase());
                        simpleContentExtensions.add(simpleContentExtension);

                        // Check for the extension if the contained attribute particles are optional (later these attribute particles are intersected with the attribute particles of the complex content complexTypes )
                        if (!attributeParticleIntersectionGenerator.isOptional(simpleContentExtension.getAttributes())) {
                            optionalAttributeParticles = false;
                        }

                        // Add attribute particle list to the list of attribute particle list
                        attributeParticleList.add(simpleContentExtension.getAttributes());
                    }
                }
            }

            // Create new attribute particles from the found attribute particle lists
            newAttributeParticles = attributeParticleIntersectionGenerator.generateAttributeParticleIntersection(attributeParticleList);

            // If no attribute particles were generated and all attribute particles were not optional return no type
            if (newAttributeParticles == null && !optionalAttributeParticles) {
                return null;
            }
            if (allMixed) {

                // Generate new base type name and base type
                String newSimpleTypeBaseName = getTypeName(new LinkedHashSet<Type>(simpleTypes));
                SimpleType newSimpleTypeBase = generateNewTopLevelSimpleType(simpleTypes, newSimpleTypeBaseName);

                // Create new extension with the base type and created attribute particles
                SimpleContentExtension newSimpleContentExtension = new SimpleContentExtension(outputSchema.getTypeSymbolTable().getReference(newSimpleTypeBaseName));
                newSimpleContentExtension.setAttributes(newAttributeParticles);

                // Set new annotation and "ID" attribute for the new extension
                newSimpleContentExtension.setAnnotation(getAnnotation());
                newSimpleContentExtension.setId(getSimpleContentExtensionID(simpleContentExtensions));

                // Create new simple content with new annotation, "ID" attribute and new extension
                SimpleContentType newSimpleContentType = new SimpleContentType();
                newSimpleContentType.setAnnotation(getAnnotation());
                newSimpleContentType.setId(getContentID(contents));
                newSimpleContentType.setInheritance(newSimpleContentExtension);

                // Set new simple content as content of the new complexType
                content = newSimpleContentType;
            } else {

                for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
                    SimpleType simpleType = it.next();

                    if (!canBeEmpty(simpleType)) {
                        return null;
                    }
                }
                content = new ComplexContentType();
            }
        }

        // If content is empty no complex type was constructed and null is returned
        if (content == null) {
            return null;
        }

        // Create new complexType with new type name and content
        // TODO look
        ComplexType newComplexType = new ComplexType(newTypeName, content, true);

        // Set new annotation, new "ID" and new "mixed" attributes for the new complexType
        newComplexType.setAnnotation(getAnnotation());
        newComplexType.setId(getTypeID(new LinkedHashSet<Type>(complexTypes)));
        newComplexType.setMixed(getMixed(complexTypes));

        if (content instanceof ComplexContentType) {
            newComplexType.setAttributes(newAttributeParticles);
        }

        // Register ComplexType in output schema
        outputSchema.getTypeSymbolTable().updateOrCreateReference(newComplexType.getName(), newComplexType);
        return newComplexType;
    }

    /**
     * Creates an empty annotation.
     *
     * @return null.
     */
    private Annotation getAnnotation() {
        return null;
    }

    /**
     * Method generates a new top-level defined type as the result of a type
     * intersection. The specified set of types is used for this purpose. Types
     * can be simpleTypes or complexTypes if all specified types are only from
     * one these two sorts the respective method is used to generate a new type
     * of this sort. If the contained types are simpleTypes and complexTypes it
     * is checked if attributes and elements are optional in complexTypes and if
     * the simpleTypes and complexTypes with simpleContent have an intersection
     * which is then returned by this method.
     *
     * @param types Set of types, both sorts of types are possible.
     * @param newTypeName New name of the generated type.
     * @param localElementTypes Map containing for each local defined element
     * a corresponding type definition.
     * @return A type either a simpleType or a complexType depending on the
     * types contained in the specified set. Otherwise null if the intersection
     * of types is empty.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if particle automaton is not supported for a given operation.
     */
    public Type generateNewTopLevelType(LinkedHashSet<Type> types, String newTypeName, LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {

        // If the simpleType is a build-in type it its reigistered with the output schema
        if (isBuiltInDatatype(newTypeName)) {
            SimpleType newSimpleType = new SimpleType(newTypeName, null);

            // Update type SymbolTable and return new simpleType
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);
            return newSimpleType;
        }

        // Use type sets to partition types into to different sets
        LinkedHashSet<SimpleType> simpleTypes = new LinkedHashSet<SimpleType>();
        LinkedHashSet<ComplexType> complexTypes = new LinkedHashSet<ComplexType>();

        // Partition types into complexTypes and simpLeTypes
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // If type is a complexType add type to complexType set else if the type is a simpleType add the type to the simpleType set
            if (type instanceof SimpleType) {

                if (!type.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {
                    simpleTypes.add((SimpleType) type);
                }
            } else if (type instanceof ComplexType) {
                complexTypes.add((ComplexType) type);
            }
        }

        // If only simpleTypes are contained in the type set generate new top-level simpleType
        if (!simpleTypes.isEmpty() && complexTypes.isEmpty()) {
            return generateNewTopLevelSimpleType(simpleTypes, newTypeName);

        } else if (simpleTypes.isEmpty() && !complexTypes.isEmpty()) {

            // If only complexTypes are contained in the type set generate new top-level complexType
            return generateNewTopLevelComplexType(complexTypes, newTypeName, localElementTypes);

        } else if (!simpleTypes.isEmpty() && !complexTypes.isEmpty()) {

            boolean allMixed = true;

            // If both complexTypes and simpleTypes are contained in type set check if intersection is still possible
            // For each contained compleType check if the complexType allows an intersection with a simpleType
            for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
                ComplexType complexType = it.next();

                // Check if the current complexType has simple or complex content
                if (complexType.getContent() instanceof ComplexContentType) {
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                    if (!complexType.getMixed() && !complexContentType.getMixed()) {
                        allMixed = false;
                    }

                    // In order for a complexType simpleType intersection to be not null the complexType has to be mixed and the containes particle has to be optional or empty
                    if (complexContentType.getParticle() != null && hasNoEmptyContent(complexContentType.getParticle()) && !particleIntersectionGenerator.isOptional(complexContentType.getParticle())) {
                        return null;
                    }

                    // If a single complexType requieres attributes no intersection can be build
                    if (!attributeParticleIntersectionGenerator.isOptional(complexType.getAttributes())) {
                        return null;
                    }
                } else if (complexType.getContent() instanceof SimpleContentType) {
                    SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                    // Check if the simple content contains an extension (Inheritance was resolved)
                    if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                        // If a single complexType requieres Attributes no intersection can be build
                        if (!attributeParticleIntersectionGenerator.isOptional(simpleContentExtension.getAttributes())) {
                            return null;
                        }

                        // Add base type to the list of simpleTypes which should be intersected. Because the result of this intersection is at best a simpleType
                        simpleTypes.add((SimpleType) simpleContentExtension.getBase());
                    }
                }
            }
            if (allMixed) {

                // If complexTypes make the intersection possible generate new simpleType
                return generateNewTopLevelSimpleType(simpleTypes, newTypeName);
            } else {

                // Check if an empty type can be the result of the intersection
                for (Iterator<SimpleType> it = simpleTypes.iterator(); it.hasNext();) {
                    SimpleType simpleType = it.next();

                    if (!canBeEmpty(simpleType)) {
                        return null;
                    }
                }

                // Add empty complexType to schema
                outputSchema.getTypeSymbolTable().updateOrCreateReference(newTypeName, new ComplexType(newTypeName, null));
                if (!outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference())) {
                    outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newTypeName));
                }
                return outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference();
            }
        } else {

            // Per default return null (If no types were specified)
            return null;
        }
    }

    /**
     * Method generates a new top-level defined complexType in the specified
     * schema. The Type itself is the result of an intersection of given
     * complexTypes. So it is possible that instead of a type a null pointer
     * is returned if the intersection is empty. A top-level complexType is
     * different from an anonymous Type and contains "abstract", "final" and
     * "block" attributes, which are not contained by anonymous types. If one
     * type contained in the set of complexTypes has abstract value true the
     * intersection is empty.
     *
     * @param complexTypes Set of complexTypes used to compute the intersection.
     * @param newTypeName New name of the constructed complexType.
     * @param localElementTypes Map containing for each local defined element
     * a corresponding type definition.
     * @return New complexType if the intersection is not empty else a null
     * pointer is returned.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if particle automaton is not supported for a given operation.
     */
    private ComplexType generateNewTopLevelComplexType(LinkedHashSet<ComplexType> complexTypes, String newTypeName, LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        ComplexType newComplexType = generateNewAnonymousComplexType(complexTypes, newTypeName, localElementTypes);

        // Return null if the new complexType is empty
        if (newComplexType == null) {
            return null;
        }

        // Check if any complexType is abstract in which case no type is build
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // Check if current complexType has an "abstract" attribute
            if (complexType.isAbstract()) {
                return null;
            }
        }

        // Set "final" and "block" attributes which are not present for anonymous Types
        newComplexType.setBlockModifiers(getComplexTypeBlock(complexTypes));
        newComplexType.setFinalModifiers(getComplexTypeFinal(complexTypes));

        // Add compleType to list of top-level types contained in the output schema
        if (!outputSchema.getTypes().contains(newComplexType)) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newComplexType.getName()));
        }
        return newComplexType;
    }

    /**
     * Each complexType which contains an element content needs a complex
     * content, which contains particles and a "mixed" attribute. So a
     * complexType with complex content, which results from a complexType
     * intersection needs a new ComplexContentType, which in turn results from a
     * ComplexContentType intersection. To compute this intersection the
     * generateNewComplexContentType methode is used. It constructs a particle
     * intersection and a new "mixed" attribute.
     *
     * @param complexTypes Set of complexTypes which are containing complex
     * content. These are used to compute the intersection and the resulting new
     * ComplexContentType.
     * @param localElementTypes Map containing for each local defined element
     * a corresponding type definition.
     * @return A new ComplexContentType if the intersection is not empty
     * otherwise a null pointer is returned by the call.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a type automaton is a no deterministic finite automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if particle automaton is not supported for a given operation.
     */
    private ComplexContentType generateNewComplexContentType(LinkedHashSet<ComplexType> complexTypes, LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        // Get new particle list, which is used to construct the new particle and content set to generate new "ID" attrubute and annotation
        LinkedList<Particle> particles = new LinkedList<Particle>();
        LinkedHashSet<ComplexContentType> complexContentTypes = new LinkedHashSet<ComplexContentType>();

        // Use variable to check if the all contained particles are optional, meaning it its possible to contain no elements
        boolean optionalParticle = true;

        // For all specified complexTypes check if content is complex content
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();
            if (complexType.getContent() instanceof ComplexContentType) {
                ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                // Add compex content to set and particle to list
                complexContentTypes.add(complexContentType);
                particles.add(complexContentType.getParticle());

                // If the current particle is not optional the variable is set to false
                if (!particleIntersectionGenerator.isOptional(complexContentType.getParticle())) {
                    optionalParticle = false;
                }
            } else if (complexType.getContent() == null) {
                particles.add(null);
            }
        }

        // Generate new Particle via the ParticleIntersectionGenerator
        Particle newParticle = particleIntersectionGenerator.generateParticleIntersection(particles, localElementTypes);

        // Elements and any patterns are not allowed to appear directly in an complexType
        if (newParticle instanceof Element || newParticle instanceof AnyPattern) {
            SequencePattern sequencePattern = new SequencePattern();
            sequencePattern.addParticle(newParticle);
            newParticle = sequencePattern;
        }

        // Return null if the new particle is null and not every particle used to construct the new particle was optional
        if (newParticle == null && !optionalParticle) {
            return null;
        }

        // Build new complex content with new "mixed" and "ID" attributes and a new annotation
        ComplexContentType complexContentType = new ComplexContentType(newParticle, getComplexContentMixed(complexContentTypes));
        complexContentType.setAnnotation(getAnnotation());
        complexContentType.setId(getContentID(new LinkedHashSet<Content>(complexContentTypes)));

        // Return the complete new content
        return complexContentType;
    }

    /**
     * This method generates a new top-level defined simpleType as the result of
     * an intersection of specified simpleTypes.  In difference to anonymous
     * simpleTypes top-level simpleTypes can be refernced by elements and
     * contain "final" values, which restrict type inheritance. If the
     * intersection is not successful null is returned instead of a simpleType.
     *
     * @param simpleTypes Set of SimpleTypes not necessarily all top-level
     * simpleTypes.
     * @param newTypeName Name of the new top-level SimpepleType.
     * @return A new simpleType if the intersection is successful or null
     * otherwise.
     */
    private SimpleType generateNewTopLevelSimpleType(LinkedHashSet<SimpleType> simpleTypes, String newTypeName) {

        // If the simpleType is a build-in type it its reigistered with the output schema
        if (isBuiltInDatatype(newTypeName)) {
            SimpleType newSimpleType = new SimpleType(newTypeName, null);

            // Update type SymbolTable and return new simpleType
            outputSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType);
            return newSimpleType;
        }
        SimpleType newSimpleType = generateNewAnonymousSimpleType(simpleTypes, newTypeName);

        // Return null if the new simpleType is empty
        if (newSimpleType == null) {
            return null;
        }
        newSimpleType.setIsAnonymous(false);

        // Set "final" attribute, which is not present for anonymous Types
        newSimpleType.setFinalModifiers(getSimpleTypeFinal(simpleTypes));

        if (!outputSchema.getTypes().contains(newSimpleType)) {
            outputSchema.addType(outputSchema.getTypeSymbolTable().getReference(newSimpleType.getName()));
        }
        return newSimpleType;
    }

    /**
     * Generates a new "block" value from a set of complexTypes. The "block"
     * attribute of XML XSDSchema restricts the use of derived types, so when
     * computing the intersection the most restrictive "block" value is
     * computed.
     *
     * @param complexTypes Set of complexTypes, each complexType contains a
     * "block" value, which is used to compute the new "block" attribute.
     * @return Set of ComplexTypeInheritanceModifier, which represents the new
     * "block" attribute.
     */
    private HashSet<ComplexTypeInheritanceModifier> getComplexTypeBlock(LinkedHashSet<ComplexType> complexTypes) {

        // Generate new "block" attribute
        HashSet<ComplexTypeInheritanceModifier> newBlock = new HashSet<ComplexTypeInheritanceModifier>();

        // Check for each complexType the "block" attribute
        for (ComplexType complexType : complexTypes) {

            // If "block" attribute is present add contained ComplexTypeInheritanceModifier value from the "block" attribute of the new complexType
            if (complexType.getBlockModifiers() != null) {

                // Check if "extension" is contained and if add "extension" from "block" attribute of the new complexType
                if (complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension)) {
                    newBlock.add(ComplexTypeInheritanceModifier.Extension);
                }
                // Check if "restriction" is contained and if add "restriction" from "block" attribute of the new complexType
                if (complexType.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Restriction)) {
                    newBlock.add(ComplexTypeInheritanceModifier.Restriction);
                }
            } else if (typeOldSchemaMap.get(complexType).getBlockDefaults() != null) {

                // Add Block values contained in the default value
                if (typeOldSchemaMap.get(complexType).getBlockDefaults().contains(BlockDefault.extension)) {
                    newBlock.add(ComplexTypeInheritanceModifier.Extension);
                }
                if (typeOldSchemaMap.get(complexType).getBlockDefaults().contains(BlockDefault.restriction)) {
                    newBlock.add(ComplexTypeInheritanceModifier.Restriction);
                }
            }
        }

        // Generate a set containing BlockDefault values for the Block values contained in the set of the new complexType
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();

        // Check if "extension" is contained and if add "extension"
        if (newBlock.contains(ComplexTypeInheritanceModifier.Extension)) {
            blockDefaults.add(XSDSchema.BlockDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (newBlock.contains(ComplexTypeInheritanceModifier.Restriction)) {
            blockDefaults.add(XSDSchema.BlockDefault.restriction);
        }

        // Check if new "block" values is different than the default value of the output schema
        if (!(outputSchema.getBlockDefaults().containsAll(blockDefaults) && blockDefaults.containsAll(outputSchema.getBlockDefaults()))) {
            return newBlock;
        } else {

            // If output schema has the same "blockDefault" value as the new complexTypes "block" attribute return null
            return null;
        }
    }

    /**
     * Checks if the specified particle has no empty content. The contetn is
     * empty if no elements are contained or if elements never appear in a
     * valid XML XSDSchema instance under an element with this type definition.
     * This method can be used to determine if a complexType with complex
     * content and a Particle can be intersected with another complexType with
     * simple content.
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
        } else if (particle instanceof Element || particle instanceof AnyPattern) {

            // If particle is element or any pattern return true
            return true;
        }

        // In all other cases return false
        return false;
    }

    /**
     * The getTypeID method generates an intersection of specified "ID"s. The
     * result is a new "ID" which is used by the type resulting from the
     * intersection of given types. If two types have different "ID" values the
     * intersection is empty and a null pointer is returned.
     *
     * @param types Set of type from which the new "ID" is calculated.
     * @return Null if the intersection is empty or a String representing the
     * "ID" if all types carry the same "ID" value.
     */
    private String getTypeID(LinkedHashSet<Type> types) {

        // Initialize new "ID" attribute
        String newID = "";

        // Check for each given type if an "ID" attribute is contained
        for (Iterator<Type> it = types.iterator(); it.hasNext();) {
            Type type = it.next();

            // If type contains no "ID" attribte or if contained "ID" attribute is different from the new "ID" attribute return null else store the contained "ID" attribute in the new "ID" attribute
            if (type.getId() == null || type.getId().equals("") || !newID.equals(type.getId()) && !newID.equals("")) {
                return null;
            } else {
                newID = type.getId();
            }
        }
        return newID;
    }

    /**
     * The getContentID method generates an intersection of specified "ID"s. The
     * result is a new "ID" which is used by the content resulting from the
     * intersection of given contents. If two contents have different "ID"
     * values the intersection is empty and a null pointer is returned.
     *
     * @param contents Set of Content objects from which the new "ID" is
     * calculated.
     * @return Null if the intersection is empty or a String representing the
     * "ID" if all Content objects carry the same "ID" value.
     */
    private String getContentID(LinkedHashSet<Content> contents) {

        // Initialize new "ID" attribute
        String newID = "";

        // Check for each given content if an "ID" attribute is contained
        for (Iterator<Content> it = contents.iterator(); it.hasNext();) {
            Content content = it.next();

            // If content contains no "ID" attribte or if contained "ID" attribute is different from the new "ID" attribute return null else store the contained "ID" attribute in the new "ID" attribute
            if (content.getId() == null || content.getId().equals("") || !newID.equals(content.getId()) && !newID.equals("")) {
                return null;
            } else {
                newID = content.getId();
            }
        }
        return newID;
    }

    /**
     * The getSimpleContentExtensionID method generates an intersection of
     * specified "ID"s. The result is a new "ID" which is used by the
     * SimpleContentExtension resulting from the intersection of given
     * SimpleContentExtensions. If two SimpleContentExtensions have different
     * "ID" values the intersection is empty and a null pointer is returned.
     *
     * @param simpleContentExtensions Set of SimpleContentExtensions from which
     * the new "ID" is calculated.
     * @return Null if the intersection is empty or a String representing the
     * "ID" if all SimpleContentExtensions carry the same "ID" value.
     */
    private String getSimpleContentExtensionID(LinkedHashSet<SimpleContentExtension> simpleContentExtensions) {

        // Initialize new "ID" attribute, which is null per default
        String newID = "";

        // Check for each given extension if an "ID" attribute is contained
        for (Iterator<SimpleContentExtension> it = simpleContentExtensions.iterator(); it.hasNext();) {
            SimpleContentExtension simpleContentExtension = it.next();

            // If extension contains no "ID" attribte or if contained "ID" attribute is different from the new "ID" attribute return null else store the contained "ID" attribute in the new "ID" attribute
            if (simpleContentExtension.getId() == null || simpleContentExtension.getId().equals("") || !newID.equals(simpleContentExtension.getId()) && !newID.equals("")) {
                return null;
            } else {
                newID = simpleContentExtension.getId();
            }
        }
        return newID;
    }

    /**
     * Get new name for an restriction type.
     *
     * @return New name of an restriction type.
     */
    private String getRestrictionTypeName() {

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}intersection-type.restriction";

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

    /**
     * Get new name for a list type.
     *
     * @return New name of a list type.
     */
    private String getListTypeName() {

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}intersection-type.list";

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

    /**
     * Get new name for a union type.
     *
     * @return New name of an union type.
     */
    private String getUnionTypeName() {

        // Generate new name for the new type
        String typeName = "{" + outputSchema.getTargetNamespace() + "}intersection-type.union";

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

    /**
     * This method generates a new "mixed" attribute for a compex content types.
     * The new "mixed" attribute is the result of an intersection of "mixed"
     * attributes. In order for the new "mixed" attribute to be true all
     * specified CompexContentTypes have to be mixed if only one
     * CompexContentType is not mixed the resulting CompexContentType will not
     * be mixed either.
     *
     * @param complexContentTypes Set of CompexContentTypes, each of these has
     * its own "mixed" attribute, which is used to compute the new "mixed"
     * attribute.
     * @return <tt>true</tt> if the "mixed" attributes of all CompexContentTypes
     * are <tt>true</tt>, otherwise <tt>false</tt>.
     */
    private boolean getComplexContentMixed(LinkedHashSet<ComplexContentType> complexContentTypes) {

        if (complexContentTypes.isEmpty()) {
            return false;
        }

        // Check for each complex content the contained "mixed" attribute
        for (Iterator<ComplexContentType> it = complexContentTypes.iterator(); it.hasNext();) {
            ComplexContentType complexContentType = it.next();

            // If contained "mixed" attribute has value "false" return false
            if (!complexContentType.getMixed()) {
                return false;
            }
        }

        // If all complex content types contain "mixed" attributes with value "true" return true
        return true;
    }

    /**
     * This method generates a new "mixed" attribute for a complexType, analog
     * to the getComplexContentMixed method the new "mixed" attribute is the
     * result of an intersection of mixed attributes. If one "mixed" attribute
     * has value "false" the resulting "mixed" attribute has value "false" as
     * well.
     *
     * @param complexTypes Set of complexTypes, each of these types has its own
     * "mixed" attribute, which is needed for the computation of the
     * intersection.
     * @return <tt>false</tt> if the "mixed" attributes of one complexType
     * is <tt>false</tt>, else <tt>true</tt>.
     */
    private boolean getMixed(LinkedHashSet<ComplexType> complexTypes) {

        // Check for each complexType the contained "mixed" attribute
        for (Iterator<ComplexType> it = complexTypes.iterator(); it.hasNext();) {
            ComplexType complexType = it.next();

            // If "mixed" attribute has value "false" return false
            if (!complexType.getMixed()) {
                return false;
            }
        }

        // If all complexTypes contain "mixed" attributes with value "true" return true
        return true;
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

    /**
     * Check if the specified name belongs to a build-in datatype.
     *
     * @param name Name of the type, which may be a build-in datatype.
     * @return <tt>true</tt> if the name belongs to a build-in datatype, else
     * <tt>false</tt>.
     */
    private boolean isBuiltInDatatype(String name) {

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
}
