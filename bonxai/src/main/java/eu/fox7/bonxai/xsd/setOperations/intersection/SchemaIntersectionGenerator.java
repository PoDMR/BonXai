package eu.fox7.bonxai.xsd.setOperations.intersection;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.automaton.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;
import eu.fox7.bonxai.xsd.tools.ForeignSchemaResolver;

import java.util.*;

/**
 * This class is responsible for computing new Schemata representing the
 * intersection of sets of Schemata. In order to intersect different Schemata
 * all Schemata should have the same targetNamespace so that the new XSDSchema has
 * this targetNamespace. Furthermore the class supplies all needed methods to
 * intersect Schemata and there attributes, like methods to intersect default
 * values and so on.
 * 
 * @author Dominik Wolff
 */
public class SchemaIntersectionGenerator {

    // Set containing the minuend schemata used to build the new output schema
    private LinkedHashSet<XSDSchema> minuendSchemata;

    // HashMap mapping namespaces to output schemata (Can be used if an element with a foreign namespace is referenced).
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // Map mapping each any attribute to the old schema, that contains the any attribute
    private LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap;

    // Map mapping types to old schemata used to construct the new output schema
    private LinkedHashMap<Type, XSDSchema> typeOldSchemaMap;

    // Map mapping any patterns to their old schemata
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap;

    // Map mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata;

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    /**
     * Constuctor of the SchemaIntersectionGenerator class. Specified parameter
     * are used to initialize the class fields.
     *
     * @param minuendSchemata Set containing the minuend schemata used to build
     * the new output schema.
     * @param namespaceOutputSchemaMap HashMap mapping namespaces to output
     * schemata.
     * @param anyAttributeOldSchemaMap Map mapping each any attribute to the old
     * schema, that contains the any attribute.
     * @param typeOldSchemaMap Map mapping types to old schemata used to
     * construct the new output schema.
     * @param anyPatternOldSchemaMap Map mapping any patterns to their old
     * schemata.
     * @param elementOldSchemaMap Map mapping elements to old schemata used to
     * construct the new output schema.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param namespaceAbbreviationMap NamespaceAbbreviationMap maps namespaces
     * to abbreviations.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     */
    public SchemaIntersectionGenerator(LinkedHashSet<XSDSchema> minuendSchemata, LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap, LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap, LinkedHashMap<Type, XSDSchema> typeOldSchemaMap, LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap, LinkedHashMap<Element, XSDSchema> elementOldSchemaMap, LinkedHashSet<XSDSchema> otherSchemata, LinkedHashMap<String, String> namespaceAbbreviationMap, String workingDirectory) {

        // Initialize all class fields
        this.minuendSchemata = minuendSchemata;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.anyAttributeOldSchemaMap = anyAttributeOldSchemaMap;
        this.typeOldSchemaMap = typeOldSchemaMap;
        this.anyPatternOldSchemaMap = anyPatternOldSchemaMap;
        this.elementOldSchemaMap = elementOldSchemaMap;
        this.otherSchemata = otherSchemata;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.workingDirectory = workingDirectory;
    }

    /**
     * Generate a new schema interscetion. The result is stored in the specified
     * output schema.
     *
     * @param outputSchema The schema, in which the result of the interscetion is
     * stored.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product state contains no type states.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if a particle automaton is not supported by a specified method.
     */
    public void generateIntersection(XSDSchema outputSchema) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException, NonDeterministicFiniteAutomataException, EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NotSupportedParticleAutomatonException {

        // Create new attributeFormDefault attribute for the output schema, which has the value unqualified
        XSDSchema.Qualification attributeFormDefault = generateNewAttributeFormDefaultAttribute();
        outputSchema.setAttributeFormDefault(attributeFormDefault);

        // Create new elementFormDefault attribute for the output schema, which has the value unqualified
        XSDSchema.Qualification elementFormDefault = generateNewElementFormDefaultAttribute();
        outputSchema.setElementFormDefault(elementFormDefault);

        // Create new finalDefault attribute for the output schema, which contains only FinalDefaults contained in each schema of the schema set
        LinkedHashSet<XSDSchema.FinalDefault> finalDefault = generateNewFinalDefaultAttribute();
        outputSchema.setFinalDefaults(finalDefault);

        // Create new blockDefault attribute for the output schema, which contains only BlockDefault contained in each schema of the schema set
        LinkedHashSet<XSDSchema.BlockDefault> blockDefault = generateNewBlockDefaultAttribute();
        outputSchema.setBlockDefaults(blockDefault);

        // Generate new type automaton factory, which is used to construct a new product type automaton for the schema set
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();

        // Build for each schema a type automaton
        LinkedList<TypeAutomaton> typeAutomatons = new LinkedList<TypeAutomaton>();
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();
            typeAutomatons.add(typeAutomatonFactory.buildTypeAutomaton(schema, anyPatternOldSchemaMap));
        }

        // Build product automaton for the type automaton list
        ProductTypeAutomaton productTypeAutomaton = typeAutomatonFactory.buildProductTypeAutomaton(typeAutomatons);

        // Create map mapping product particle states to type names of the new output schema
        LinkedHashMap<ProductTypeState, String> stateTypeNameMap = new LinkedHashMap<ProductTypeState, String>();

        // Create for each state of the product automaton, with exception of the start state, a type stub
        for (Iterator<TypeState> it = productTypeAutomaton.getStates().iterator(); it.hasNext();) {
            State state = it.next();

            // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
            if (state != productTypeAutomaton.getStartState()) {
                ProductTypeState productTypeState = (ProductTypeState) state;

                // Get name of the new type (if anyType is contained it is anytype etc.)
                String typeName = getTypeName(productTypeState, outputSchema);

                // Update stateTypeNameMap
                stateTypeNameMap.put(productTypeState, typeName);

                // Create type stub in the output schema
                if (!outputSchema.getTypeSymbolTable().hasReference(typeName)) {
                    outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, null);
                }
            }
        }

        // Map mapping top-level elements to references of their types.
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

        // For each outgoing transition of the start state and for each element name annotated to these transitions update the topLevelElementTypeMap
        for (Transition outTransition : productTypeAutomaton.getStartState().getOutTransitions()) {
            for (String elementName : outTransition.getNameElementMap().keySet()) {

                // Get name of the reached type
                String typeName = stateTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());

                // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                SimpleType stubSimpleType = new SimpleType(typeName, null);
                stubSimpleType.setIsAnonymous(isAnonymous(((ProductTypeState) outTransition.getDestinationState()).getTypes(), outputSchema));

                if (!stubSimpleType.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                    stubSimpleType.setDummy(true);
                }

                topLevelElementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
            }
        }

        // Initialize new generator classes
        ParticleIntersectionGenerator particleIntersectionGenerator = null;
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = null;
        TypeIntersectionGenerator typeIntersectionGenerator = null;

        // New ParticleDifferenceGenerator has to know the namespaceConflictingElementsMap and elementGroupMap for ElementRef handling.
        particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, anyPatternOldSchemaMap, topLevelElementTypeMap, otherSchemata, namespaceAbbreviationMap, workingDirectory);

        // New AttributeParticleDifferenceGenerator
        attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, workingDirectory);

        // New TypeDifferenceGenerator has to know all four HashMaps for AttributeRef and ElementRef handling.
        typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        particleIntersectionGenerator.setTypeIntersectionGenerator(typeIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(typeIntersectionGenerator);

        // Get set containing the attribute or element lists of the minuend schemata
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();


        // Get top-level attributes and elements contained in the minuend schema into the given sets
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();
            topLevelAttributeListSet.add(schema.getAttributes());
            topLevelElementListSet.add(schema.getElements());
        }

        // Generate all new top-level attributes
        attributeParticleIntersectionGenerator.generateNewTopLevelAttributes(topLevelAttributeListSet);

        // Each state of the product automaton, with exception of the start state, corresponds to a type of the output schema, these types are now generated
        for (Iterator<TypeState> it = productTypeAutomaton.getStates().iterator(); it.hasNext();) {
            ProductTypeState productTypeState = (ProductTypeState) it.next();

            // Check if the current state is the start state of the automaton, which contains no types and does not correspond to a type
            if (productTypeState != productTypeAutomaton.getStartState()) {

                // Map mapping element names of the content model to type references, used for these elements
                LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();

                // For each outgoing transition of the current state and for each element name annotated to these transitions update the elementTypeMap
                for (Transition outTransition : productTypeState.getOutTransitions()) {
                    for (String elementName : outTransition.getNameElementMap().keySet()) {

                        // Get name of the reached type
                        String typeName = stateTypeNameMap.get((ProductTypeState) outTransition.getDestinationState());

                        // Put new entry into the elementTypeMap, for the current element namen and the corresponding type
                        SimpleType stubSimpleType = new SimpleType(typeName, null);
                        stubSimpleType.setIsAnonymous(isAnonymous(((ProductTypeState) outTransition.getDestinationState()).getTypes(), outputSchema));

                        if (!stubSimpleType.getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                            stubSimpleType.setDummy(true);
                        }

                        // Generate type stub for elements and "type" attribute computation
                        if (outputSchema.getTypeSymbolTable().getReference(typeName).getReference() != null) {
                            elementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().getReference(typeName));
                        } else {
                            elementTypeMap.put(elementName, outputSchema.getTypeSymbolTable().updateOrCreateReference(typeName, stubSimpleType));
                        }
                    }
                }

                // Get type name for the current type
                String newTypeName = stateTypeNameMap.get(productTypeState);

                // Check if output schema contains type stub and only a stub for the given type
                if (outputSchema.getTypeSymbolTable().hasReference(newTypeName) && outputSchema.getTypeSymbolTable().getReference(newTypeName) != null) {

                    // Generate new type for the given automaton state
                    typeIntersectionGenerator.generateNewTopLevelType(productTypeState.getTypes(), newTypeName, elementTypeMap);
                }
            }
        }

        // Remove empty type definitions from schema
        for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
            XSDSchema otherSchema = it.next();
            removeEmptyTypes(otherSchema, particleIntersectionGenerator);
        }
        removeEmptyTypes(outputSchema, particleIntersectionGenerator);

        // Add new top-level elements to output schema
        particleIntersectionGenerator.generateNewTopLevelElements(topLevelElementListSet);

        // Remove unused type definitions from schema
        for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
            XSDSchema otherSchema = it.next();
            removeUnusedTypes(otherSchema);
        }
        removeUnusedTypes(outputSchema);
    }

    /**
     * Generate a new elementFormDefault attribute for the output schema.
     * (Per default this attribute has the value unqualified, which is the
     * default, if no attribute is present.)
     *
     * @return New elementFormDefault attribute, which is a Qualification, for
     * the output schema.
     */
    private XSDSchema.Qualification generateNewElementFormDefaultAttribute() {
        boolean isQualified = false;

        // For all schemata check the contained elementFormDefault attribute values
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if attributes in schema are per default qualified
            if (schema.getElementFormDefault() != null && schema.getElementFormDefault() == XSDSchema.Qualification.qualified) {
                isQualified = true;
            }
        }

        // If a single schema contains an elementFormDefault attribute with value qualified resolve elementFormDefault in all schemata
        if (isQualified) {

            // Use foreign schema resolver to resolve elementFormDefault attributes in each schema
            ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
            for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
                XSDSchema schema = it.next();
                foreignSchemaResolver.removeElementFormDefault(schema);
            }
        }

        // New output schema has no elementFormDefault attribute per default
        return XSDSchema.Qualification.unqualified;
    }

    /**
     * Generate a new attributeFormDefault attribute for the output schema.
     * (Per default this attribute has the value unqualified, which is the
     * default, if no attribute is present.)
     *
     * @return New attributeFormDefault attribute, which is a Qualification, for
     * the output schema.
     */
    private XSDSchema.Qualification generateNewAttributeFormDefaultAttribute() {
        boolean isQualified = false;

        // For all schemata check the contained attributeFormDefault attribute values
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if attributes in schema are per default qualified
            if (schema.getAttributeFormDefault() != null && schema.getAttributeFormDefault() == XSDSchema.Qualification.qualified) {
                isQualified = true;
            }
        }

        // If a single schema contains an attributeFormDefault attribute with value qualified resolve attributeFormDefault in all schemata
        if (isQualified) {

            // Use foreign schema resolver to resolve attributeFormDefault attributes in each schema
            ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
            for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
                XSDSchema schema = it.next();
                foreignSchemaResolver.removeAttributeFormDefault(schema);
            }
        }

        // New output schema has no attributeFormDefault attribute per default
        return XSDSchema.Qualification.unqualified;
    }

    /**
     * This method checks whether the type, generate from the specified type
     * list, is anonymous or not. It is anonymous if all types contained in the
     * type list are anoymous.
     *
     * @param types List of types used to construct a new type.
     * @param outputSchema XSDSchema in which the new type will be present.
     * @return <tt>true</tt> if all specified types are anonymous, else
     * <tt>false</tt>.
     */
    private boolean isAnonymous(LinkedHashSet<Type> types, XSDSchema outputSchema) {

        // If type list contains more than one type the return false
        if (types.size() != 1 || containsStrictAnyPattern(types) || !types.iterator().next().getNamespace().equals(outputSchema.getTargetNamespace())) {
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

        } else if (particle instanceof AnyPattern && ((AnyPattern) particle).getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict)) {

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
    private boolean containsStrictAnyPattern(LinkedHashSet<Type> types) {

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
    private String getTypeName(ProductTypeState productTypeState, XSDSchema outputSchema) {

        // Get the set of types contained in the product type state
        LinkedHashSet<Type> containedTypes = productTypeState.getTypes();

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
            typeNames.add(type.getName());
        }

        // Build-in data types are not renamed
        if (typeNames.size() == 1 && containedTypes.iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return typeNames.iterator().next();
        }
        for (Iterator<String> it = typeNames.iterator(); it.hasNext();) {
            String currentTypeName = it.next();

            if (currentTypeName.equals("{http://www.w3.org/2001/XMLSchema}anyType") || currentTypeName.equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
                it.remove();
            }
        }

        // Build-in data types are not renamed
        if (typeNames.size() == 1 && containedTypes.iterator().next().getNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return typeNames.iterator().next();
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
     * Generate a new blockDefault attribute for the output schema. This
     * attribute contains only BlockDefault contained in each schema of the
     * schema set.
     *
     * @return New blockDefault attribute, which is a BlockDefault, for the
     * output schema.
     */
    private LinkedHashSet<XSDSchema.BlockDefault> generateNewBlockDefaultAttribute() {

        // Create blockDefault attribute, which contains every possible BlockDefault value
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();

        // Check for each schema the blockDefault attribute
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // If blockDefault attribute is present add contained BlockDefault value from the blockDefault attribute of the output schema
            if (schema.getBlockDefaults() != null) {

                // Check if "extension" is contained and if add "extension" from blockDefault attribute of the output schema
                if (schema.getBlockDefaults().contains(XSDSchema.BlockDefault.extension)) {
                    blockDefaults.add(XSDSchema.BlockDefault.extension);
                }
                // Check if "restriction" is contained and if add "restriction" from blockDefault attribute of the output schema
                if (schema.getBlockDefaults().contains(XSDSchema.BlockDefault.restriction)) {
                    blockDefaults.add(XSDSchema.BlockDefault.restriction);
                }
                // Check if "substitution" is contained and if add "substitution" from blockDefault attribute of the output schema
                if (schema.getBlockDefaults().contains(XSDSchema.BlockDefault.substitution)) {
                    blockDefaults.add(XSDSchema.BlockDefault.substitution);
                }
            }
        }

        // Use foreign schema resolver to resolve blockDefault attributes in each schema, that does not contain the same blockDefault attribute as the output schema
        ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if both blockDefault attributes do not contain the same values
            if (!(schema.getBlockDefaults().containsAll(blockDefaults) && blockDefaults.containsAll(schema.getBlockDefaults()))) {
                foreignSchemaResolver.removeBlockDefault(schema);
            }
        }
        return blockDefaults;
    }

    /**
     * Generate a new finalDefault attribute for the output schema. This
     * attribute contains only FinalDefaults contained in each schema of the
     * schema set.
     *
     * @return New finalDefault attribute, which is a FinalDefault, for the
     * output schema.
     */
    private LinkedHashSet<XSDSchema.FinalDefault> generateNewFinalDefaultAttribute() {

        // Create finalDefault attribute, which contains every possible FinalDefault value
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();

        // Check for each schema the finalDefault attribute
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // If finalDefault attribute is present remove contained FinalDefault value from the finalDefault attribute of the output schema
            if (schema.getFinalDefaults() != null) {

                // Check if "extension" is  contained and if add "extension" from finalDefault attribute of the output schema
                if (schema.getFinalDefaults().contains(XSDSchema.FinalDefault.extension)) {
                    finalDefaults.add(XSDSchema.FinalDefault.extension);
                }
                // Check if "restriction" is contained and if add "restriction" from finalDefault attribute of the output schema
                if (schema.getFinalDefaults().contains(XSDSchema.FinalDefault.restriction)) {
                    finalDefaults.add(XSDSchema.FinalDefault.restriction);
                }
                // Check if "list" is contained and if add "list" from finalDefault attribute of the output schema
                if (schema.getFinalDefaults().contains(XSDSchema.FinalDefault.list)) {
                    finalDefaults.add(XSDSchema.FinalDefault.list);
                }
                // Check if "union" is contained and if add "union" from finalDefault attribute of the output schema
                if (schema.getFinalDefaults().contains(XSDSchema.FinalDefault.union)) {
                    finalDefaults.add(XSDSchema.FinalDefault.union);
                }
            }
        }

        // Use foreign schema resolver to resolve finalDefault attributes in each schema, that does not contain the same finalDefault attribute as the output schema
        ForeignSchemaResolver foreignSchemaResolver = new ForeignSchemaResolver();
        for (Iterator<XSDSchema> it = minuendSchemata.iterator(); it.hasNext();) {
            XSDSchema schema = it.next();

            // Check if both finalDefault attributes do not contain the same values
            if (!(schema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(schema.getFinalDefaults()))) {
                foreignSchemaResolver.removeFinalDefault(schema);
            }
        }
        return finalDefaults;
    }

    /**
     * Method removes all unused types from the specified schema. A type is
     * unused if the type definition is not used in an element, an attribute or
     * another type.
     *
     * @param outputSchema XSDSchema for which the unused types are removed.
     */
    private void removeUnusedTypes(XSDSchema outputSchema) {
        LinkedHashSet<Type> usedTypes = new LinkedHashSet<Type>();

        // Get all referenced types in the current schema
        for (Iterator<Type> it3 = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
            Type type = it3.next();

            // If the referred type has the same namespace as the schema add type to set
            if (type.getNamespace().equals(outputSchema.getTargetNamespace())) {

                // Check if type is compleType
                if (type instanceof ComplexType) {
                    ComplexType complexType = (ComplexType) type;

                    // Get attribute particles contained in the compleType
                    for (Iterator<AttributeParticle> it4 = complexType.getAttributes().iterator(); it4.hasNext();) {
                        AttributeParticle attributeParticle = it4.next();

                        // Check that attribute particle is attribute and add the type to the set
                        if (attributeParticle instanceof Attribute) {
                            usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                        }
                    }

                    // If complexType has complex content check content for elements and attributes
                    if (complexType.getContent() instanceof ComplexContentType) {
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                        if (complexContentType.getInheritance() != null) {
                            if (complexContentType.getInheritance().getAttributes() != null) {

                                // Get attribute particles contained in the complex content inheritance
                                for (Iterator<AttributeParticle> it4 = complexContentType.getInheritance().getAttributes().iterator(); it4.hasNext();) {
                                    AttributeParticle attributeParticle = it4.next();

                                    // Check that attribute particle is attribute and add the type to the set
                                    if (attributeParticle instanceof Attribute) {
                                        usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                                    }
                                }
                            }
                        }
                        if (complexContentType.getParticle() != null) {

                            // Get elements contained in the complex content
                            for (Iterator<Element> it4 = getContainedElements(complexContentType.getParticle()).iterator(); it4.hasNext();) {
                                Element element = it4.next();
                                usedTypes.add(element.getType());
                            }
                        }
                    }

                    // If complexType has simple content check content for attributes
                    if (complexType.getContent() instanceof SimpleContentType) {
                        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                        // Check if content inheritance is an extension
                        if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                            usedTypes.add(simpleContentExtension.getBase());

                            // Get attribute particles contained in the extension
                            for (Iterator<AttributeParticle> it4 = simpleContentExtension.getAttributes().iterator(); it4.hasNext();) {
                                AttributeParticle attributeParticle = it4.next();

                                // Check that attribute particle is attribute and add the type to the set
                                if (attributeParticle instanceof Attribute) {
                                    usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                                }
                            }
                        }

                        // Check if content inheritance is a restriction
                        if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                            usedTypes.add(simpleContentRestriction.getBase());

                            // Get attribute particles contained in the restriction
                            for (Iterator<AttributeParticle> it4 = simpleContentRestriction.getAttributes().iterator(); it4.hasNext();) {
                                AttributeParticle attributeParticle = it4.next();

                                // Check that attribute particle is attribute and add the type to the set
                                if (attributeParticle instanceof Attribute) {
                                    usedTypes.add(((Attribute) attributeParticle).getSimpleType());
                                }
                            }
                        }
                    }
                }

                // Check types that are simpleType
                if (type instanceof SimpleType) {
                    SimpleType simpleType = (SimpleType) type;

                    // Check member types of a simple content union
                    if (simpleType.getInheritance() instanceof SimpleContentUnion) {
                        SimpleContentUnion simpleContentUnion = (SimpleContentUnion) simpleType.getInheritance();

                        for (Iterator<SymbolTableRef<Type>> it = simpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
                            SymbolTableRef<Type> symbolTableRef = it.next();
                            usedTypes.add(symbolTableRef.getReference());
                        }
                    }

                    // Check item type of a simple content list
                    if (simpleType.getInheritance() instanceof SimpleContentList) {
                        SimpleContentList simpleContentList = (SimpleContentList) simpleType.getInheritance();
                        usedTypes.add(simpleContentList.getBase());
                    }

                    // Check item types of a simple content restriction
                    if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
                        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                        usedTypes.add(simpleContentRestriction.getBase());
                    }
                }
            }
        }

        // Check top-level elements for types
        for (Iterator<Element> it3 = outputSchema.getElements().iterator(); it3.hasNext();) {
            Element topLevelElement = it3.next();
            usedTypes.add(topLevelElement.getType());
        }

        // Check top-level attributes for types
        for (Iterator<Attribute> it3 = outputSchema.getAttributes().iterator(); it3.hasNext();) {
            Attribute topLevelAttribute = it3.next();
            usedTypes.add(topLevelAttribute.getSimpleType());
        }

        // Remove unused types from list and symbolTable
        for (Iterator<Type> it = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
            Type type = it.next();

            if (type != null && !usedTypes.contains(type)) {
                outputSchema.removeType(outputSchema.getTypeSymbolTable().getReference(type.getName()));
                outputSchema.getTypeSymbolTable().removeReference(type.getName());
            }
        }
    }

    /**
     * Removes empty type definitions from specified schema.
     *
     * @param outputSchema XSDSchema for which empty types are removed.
     * @param particleIntersectionGenerator Particle intersection generator used
     * to check if the particle is optional.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     */
    private void removeEmptyTypes(XSDSchema outputSchema, ParticleIntersectionGenerator particleIntersectionGenerator) throws EmptySubsetParticleStateFieldException {

        // Use boolean variable to check if repair is needed
        boolean needsRepair = true;

        // As long as not all types are valid repair schema
        while (needsRepair) {
            needsRepair = false;

            // Get all referenced types in the current schema
            for (Iterator<Type> it3 = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                // If the referred type has the same namespace as the schema check type
                if (type.getNamespace().equals(outputSchema.getTargetNamespace())) {

                    // Check if type is compleType
                    if (type instanceof ComplexType) {
                        ComplexType complexType = (ComplexType) type;

                        // If complexType has complex content check content for elements
                        if (complexType.getContent() instanceof ComplexContentType) {
                            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                            // If complexType has a particle
                            if (complexContentType.getParticle() != null) {

                                // and if the particle is optional
                                if (particleIntersectionGenerator.isOptional(complexContentType.getParticle())) {

                                    // check if the particle has to be removed
                                    if (checkParticle(complexContentType.getParticle())) {
                                        complexContentType.setParticle(null);
                                    }
                                } else {

                                    // and if the particle is not optional and has to be removed the current type is set to empty
                                    if (checkParticle(complexContentType.getParticle())) {
                                        complexType.setDummy(true);
                                        needsRepair = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Check top-level groups
            for (Iterator<Group> it = outputSchema.getGroups().iterator(); it.hasNext();) {
                Group group = it.next();

                // Get element contained in group
                Element element = (Element) group.getParticleContainer().getParticles().getFirst();

                // Check if group has to be removed
                if (element.getType() == null || element.getType().isDummy()) {
                    outputSchema.removeGroup(outputSchema.getGroupSymbolTable().getReference(group.getName()));
                    outputSchema.getGroupSymbolTable().removeReference(group.getName());
                }
            }

            // Remove all empty types from symbolTable and list
            for (Iterator<Type> it3 = outputSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it3.hasNext();) {
                Type type = it3.next();

                if (type.isDummy()) {
                    outputSchema.removeType(outputSchema.getTypeSymbolTable().getReference(type.getName()));
                    outputSchema.getTypeSymbolTable().removeReference(type.getName());
                }
            }
        }
    }

    /**
     * Remove particles with empty types from the current particle and check if
     * specified particle has to be removed.
     *
     * @param particle Particle which may contain other particles. Particles can
     * contain empty types, which are removed along with their particles.
     * @return <tt>true<tt/> if the specified particle has to be removed.
     */
    private boolean checkParticle(Particle particle) {

        // If the current particle is an element and the type definition of this element is empty remove element
        if (particle instanceof Element && (((Element) particle).getType() == null || ((Element) particle).getType().isDummy())) {
            return true;
        } else if (particle instanceof SequencePattern) {
            SequencePattern sequencePattern = (SequencePattern) particle;

            // If a particle in the sequence has to be removed the whole sequence is removed
            LinkedList<Particle> particles = sequencePattern.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();

                // Check if the contained particle is empty
                if (checkParticle(currentParticle)) {

                    // Check if counting pattern allows optional use
                    if (particle instanceof CountingPattern) {
                        CountingPattern countingPattern = (CountingPattern) particle;

                        if (countingPattern.getMin() != 0) {
                            return true;
                        } else {
                            it.remove();
                        }
                    } else {
                        return true;
                    }
                }
            }
            // If sequence is empty afterwards remove choice
            if (particles.isEmpty()) {
                return true;
            } else {
                sequencePattern.setParticles(particles);
            }
        } else if (particle instanceof ChoicePattern) {
            ChoicePattern choicePattern = (ChoicePattern) particle;

            // If a particle in a choice has to be removed remove only this particle from the choice
            LinkedList<Particle> particles = choicePattern.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();

                // Check if the contained particle is empty
                if (checkParticle(currentParticle)) {
                    it.remove();
                }
            }

            // If choice is empty afterwards remove choice
            if (particles.isEmpty()) {
                return true;
            } else {
                choicePattern.setParticles(particles);
            }
        } else if (particle instanceof CountingPattern) {
            CountingPattern countingPattern = (CountingPattern) particle;
            Particle containedParticle = countingPattern.getParticles().getFirst();

            // Check if contained particle is removed
            if (checkParticle(containedParticle)) {
                return true;
            }
        } else if (particle instanceof GroupRef) {
            Group group = (Group) ((GroupRef) particle).getGroup();

            // Get element contained in group
            Element element = (Element) group.getParticleContainer().getParticles().getFirst();

            if (element.getType() == null || element.getType().isDummy()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all elements contained in the specified particle.
     *
     * @param particle Particle which is the root of the particle structure.
     * @return Set containing all elements contained in the specified particle.
     */
    private LinkedHashSet<Element> getContainedElements(Particle particle) {

        // Initialize set which will contain all elements contained in the specified particle
        LinkedHashSet<Element> containedElements = new LinkedHashSet<Element>();

        // Check if the particle is an element and add it to the set
        if (particle instanceof Element) {
            containedElements.add((Element) particle);
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check if the particle is a particle container and add elements contained in the container to the set
            LinkedList<Particle> particles = particleContainer.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();
                containedElements.addAll(getContainedElements(currentParticle));
            }
        }
        return containedElements;
    }

    /**
     * Get all any patterns contained in the specified particle.
     *
     * @param particle Particle which is the root of the particle structure.
     * @return Set containing all any patterns contained in the specified
     * particle.
     */
    private LinkedHashSet<AnyPattern> getContainedAnyPattern(Particle particle) {

        // Initialize set which will contain all any patterns contained in the specified particle
        LinkedHashSet<AnyPattern> containedAnyPatterns = new LinkedHashSet<AnyPattern>();

        // Check if the particle is an any pattern and add it to the set
        if (particle instanceof AnyPattern) {
            containedAnyPatterns.add((AnyPattern) particle);
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check if the particle is a particle container and add any patterns contained in the container to the set
            LinkedList<Particle> particles = particleContainer.getParticles();
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle currentParticle = it.next();
                containedAnyPatterns.addAll(getContainedAnyPattern(currentParticle));
            }
        }
        return containedAnyPatterns;
    }
}
