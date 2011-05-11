package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ParticleContainer;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.*;
import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.xsd.AttributeParticle;
import de.tudortmund.cs.bonxai.xsd.ComplexContentExtension;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.ComplexTypeInheritanceModifier;
import de.tudortmund.cs.bonxai.xsd.ElementRef;
import de.tudortmund.cs.bonxai.xsd.ImportedSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentExtension;
import de.tudortmund.cs.bonxai.xsd.SimpleContentType;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class PatternElementConverter
 *
 * Conversion of RELAX NG elements into a XML XSDSchema particle structure
 *
 * @author Lars Schmidt
 */
public class PatternElementConverter extends ConverterBase {

    /**
     * Set of found recursive elements
     */
    private HashSet<Element> recursiveElements = new HashSet<Element>();
    /**
     * Map of already converted elements for avoidance of multiple conversion
     * of the same element
     */
    private HashMap<Element, LinkedList<de.tudortmund.cs.bonxai.common.Particle>> alreadyConvertedElements = new HashMap<Element, LinkedList<Particle>>();
    /**
     * Set of all already used toplevel group names. This helps to generate
     * unique names for new groups in the same schema.
     */
    private HashSet<String> usedTopLevelGroupNames = new HashSet<String>();
    /**
     * Set of all already used toplevel element names. This helps to generate
     * unique names for new elements in the same schema.
     */
    private HashSet<String> usedTopLevelElementNames = new HashSet<String>();
    /**
     * Used in case of element recursion
     */
    private HashMap<String, HashSet<Element>> fqNamesToRNGElements = new HashMap<String, HashSet<Element>>();
    /**
     * Used in case of complexType recursion
     *
     * Maps an RELAX NG element to its newly generated XML XSDSchema type.
     */
    private HashMap<Element, SymbolTableRef<Type>> elementToTypeMap = new HashMap<Element, SymbolTableRef<Type>>();
    /**
     * Maps an RELAX NG element to all newly generated XML XSDSchema elements.
     */
    private HashMap<Element, HashSet<de.tudortmund.cs.bonxai.xsd.Element>> rngElementToxsdElements = new HashMap<Element, HashSet<de.tudortmund.cs.bonxai.xsd.Element>>();
    /**
     * Stores references to all toplevel types in the current XML XSDSchema.
     */
    private HashSet<SymbolTableRef<Type>> topLevelTypes = new HashSet<SymbolTableRef<Type>>();
    /**
     * Local variable for the converter of RELAX NG patterns to XML XSDSchema attributes (attributeParticles)
     */
    private PatternAttributeConverter patternAttributeConverter;
    /**
     * Local variable for the converter of RELAX NG patterns to XML XSDSchema simpleType-definitions
     */
    private PatternSimpleTypeConverter patternSimpleTypeConverter;
    /**
     *
     */
    private HashMap<String, de.tudortmund.cs.bonxai.common.SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>> refToGroupMap = new HashMap<String, de.tudortmund.cs.bonxai.common.SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>>();
    private HashMap<String, de.tudortmund.cs.bonxai.common.SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup>> refToAttributeGroupMap = new HashMap<String, de.tudortmund.cs.bonxai.common.SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup>>();
    private HashMap<String, SymbolTableRef<Type>> refToTypeMap = new HashMap<String, SymbolTableRef<Type>>();
    private HashSet<String> usedLocalGroupNames = new HashSet<String>();

    /**
     * Constructor of class PatternElementConverter
     * @param xmlSchema                 target of the conversion
     * @param relaxng                   source of the conversion
     * @param patternInformation        global map of pattern information
     * @param usedLocalNames            globale set of all used local names
     * @param usedLocalTypeNames        global set of already used local type names
     */
    public PatternElementConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<Pattern, HashSet<String>> patternInformation, HashSet<String> usedLocalNames, HashSet<String> usedLocalTypeNames) {
        super(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
        this.patternSimpleTypeConverter = new PatternSimpleTypeConverter(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
        this.patternAttributeConverter = new PatternAttributeConverter(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames, this.patternSimpleTypeConverter);
        this.patternAttributeConverter.setRefToAttributeGroupMap(this.refToAttributeGroupMap);
    }

    /**
     * Method: convert
     * This is the main method of this class for starting the conversion process
     * @throws Exception
     */
    public void convert() throws Exception {

        // Find all possible root elements in the given RELAX NG root pattern
        LinkedList<Pattern> rootElements = findRootElementPatterns();

        for (Iterator<Pattern> it = rootElements.iterator(); it.hasNext();) {
            Element element = (Element) it.next();

            if (!(this.patternInformation.get(element) != null && this.patternInformation.get(element).contains("notAllowed"))) {
                // Convert the current RELAX NG element into a list of XML schema particles holding the converted elements
                LinkedList<de.tudortmund.cs.bonxai.common.Particle> currentParticleList = this.convertElement(element);

                for (Iterator<Particle> it1 = currentParticleList.iterator(); it1.hasNext();) {
                    Particle particle = it1.next();

                    if (particle instanceof de.tudortmund.cs.bonxai.xsd.Element) {

                        de.tudortmund.cs.bonxai.xsd.Element xsdElement = (de.tudortmund.cs.bonxai.xsd.Element) particle;

                        // Namespace check
                        if (xsdElement.getNamespace().equals(this.xmlSchema.getTargetNamespace()) || xsdElement.getNamespace().equals("")) {
                            // Add the element to the list of global elements
                            SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> strElement = new SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element>(xsdElement.getName(), xsdElement);
//                          SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> strElement = this.xmlSchema.getElementSymbolTable().updateOrCreateReference(xsdElement.getName(), xsdElement);
                            this.xmlSchema.addElement(strElement);
                            this.usedTopLevelElementNames.add(xsdElement.getName());
                        }
                    } else if (particle instanceof de.tudortmund.cs.bonxai.common.AnyPattern) {
                        // AnyPattern is not allowed as schema root!
                        // ==> ignore the returned AnyPattern
                    }
                }
            }
        }

        this.setGeneratedTypesToInnerElements();

    }

    /**
     * Method for finding the root element patterns in the given RELAX NG XSDSchema
     * @return LinkedList<Pattern>
     */
    private LinkedList<Pattern> findRootElementPatterns() throws RecursionWithoutElementException {
        LinkedList<Pattern> resultRootPatternList = new LinkedList<Pattern>();
        HashSet<Pattern> alreadySeenRootElements = new HashSet<Pattern>();
        resultRootPatternList.addAll(this.findRootElements(this.relaxng.getRootPattern(), alreadySeenRootElements));
        return resultRootPatternList;
    }

    /**
     * Recursive method for finding the root element patterns
     * @param pattern                   Current start pattern for check of root elements
     * @param alreadySeenRootElements   List of already seen root elements to avoid infinit loops
     * @return LinkedList<Pattern>      List of all possible root elements
     */
    private LinkedList<Pattern> findRootElements(Pattern pattern, HashSet<Pattern> alreadySeenRootElements) throws RecursionWithoutElementException {
        LinkedList<Pattern> resultRootPatternList = new LinkedList<Pattern>();
        if (!alreadySeenRootElements.contains(pattern)) {
            alreadySeenRootElements.add(pattern);

            if (pattern instanceof Element) {

                // Case: Element

                resultRootPatternList.add(pattern);

            } else if (pattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) pattern;

                for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    resultRootPatternList.addAll(findRootElements(innerPattern, alreadySeenRootElements));
                }

            } else if (pattern instanceof Ref) {

                // Case: Ref

                Ref ref = (Ref) pattern;

                for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                    Define innerDefine = it.next();
                    for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                        Pattern innerPattern = it2.next();
                        resultRootPatternList.addAll(findRootElements(innerPattern, alreadySeenRootElements));
                    }
                }

            } else if (pattern instanceof ParentRef) {

                // Case: ParentRef

                ParentRef parentRef = (ParentRef) pattern;

                for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                    Define innerDefine = it.next();
                    for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                        Pattern innerPattern = it2.next();
                        resultRootPatternList.addAll(findRootElements(innerPattern, alreadySeenRootElements));
                    }
                }
            } else if (pattern instanceof Grammar) {

                // Case: Grammar

                Grammar grammar = (Grammar) pattern;

                if (RelaxNG2XSDConverter.RECURSION_MODE_COMPLEXTYPE || !RelaxNG2XSDConverter.RECURSION_MODE_ELEMENT_SET_ALL_ELEMENTS_TOPLEVEL) {
                    this.findRecursiveElementsInGrammar(grammar);
                }

                for (Iterator<Pattern> it = grammar.getStartPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    resultRootPatternList.addAll(findRootElements(innerPattern, alreadySeenRootElements));
                }
            }
        }
        return resultRootPatternList;
    }

    /**
     * Convert a given element pattern to the corresponding XML XSDSchema particles
     * @param element               basis of the conversion
     * @return LinkedList<de.tudortmund.cs.bonxai.common.Particle>      result of the conversion
     * @throws Exception
     */
    private LinkedList<de.tudortmund.cs.bonxai.common.Particle> convertElement(Element element) throws Exception {

        if (RelaxNG2XSDConverter.RECURSION_MODE_ELEMENT_SET_ALL_ELEMENTS_TOPLEVEL && !RelaxNG2XSDConverter.RECURSION_MODE_COMPLEXTYPE) {
            recursiveElements.add(element);
        }

        LinkedList<de.tudortmund.cs.bonxai.common.Particle> resultParticle = new LinkedList<Particle>();

        // Generate all possible names for the resulting structure.
        // For each name there has to be generated a new element.

        NameClassAnalyzer nameClassAnalyzer = new NameClassAnalyzer(element);
        HashMap<String, Object> elementNameInfos = nameClassAnalyzer.analyze();

        String localTypeName = "";
        int countRealElementNames = 0;
        for (Iterator<String> it = elementNameInfos.keySet().iterator(); it.hasNext();) {
            String string = it.next();
            if (localTypeName.equals("")) {
                string = string.substring(string.lastIndexOf("}") + 1, string.length());
                if (elementNameInfos.get(string) == null) {
                    localTypeName = string;
                    countRealElementNames++;
                }
            } else {
                string = string.substring(string.lastIndexOf("}") + 1, string.length());
                if (elementNameInfos.get(string) == null) {
                    localTypeName = localTypeName + "_" + string;
                    countRealElementNames++;
                }
            }
        }
        if (countRealElementNames > 0) {
            localTypeName = localTypeName.substring(0, localTypeName.length());
        } else {
            localTypeName = "type";
        }
        SymbolTableRef<Type> typeRef = null;

        // If there is an anyAttribute as result of the nameGeneration. This pattern
        // can be used as result attributeParticle

        boolean normalElement = false;

        if (!elementNameInfos.isEmpty()) {

            for (Iterator<String> it = elementNameInfos.keySet().iterator(); it.hasNext();) {
                String currentNameKey = it.next();

                if (elementNameInfos.get(currentNameKey) != null) {
                    Object currentAnyObject = elementNameInfos.get(currentNameKey);
                    if (currentAnyObject instanceof AnyPattern) {
                        AnyPattern anyPattern = (AnyPattern) currentAnyObject;

                        if (currentNameKey.startsWith("-")) {
                            // Set the anyPattern into the correct XSDSchema
                            AnyPattern newAnyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##other");

                            String groupName = "externalAny";
                            SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> strGroup = putParticleIntoGroup(groupName, anyPattern.getNamespace(), newAnyPattern, false);
                            de.tudortmund.cs.bonxai.common.GroupRef groupRef = new GroupRef(strGroup);
                            resultParticle.add(groupRef);

                        } else {
                            resultParticle.add(anyPattern);
                        }
                    }
                } else {

                    registerElementFQName(currentNameKey, element);

                    if (normalElement == false && ((this.recursiveElements.contains(element) && this.elementToTypeMap.get(element) == null) || !this.recursiveElements.contains(element))) {
                        normalElement = true;

                        // Generate a unique name for the type
                        if (this.usedLocalTypeNames.contains(localTypeName)) {
                            localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
                        }
                        this.usedLocalTypeNames.add(localTypeName);

                        String namespace = null;
                        if (element.getAttributeNamespace() == null) {
                            namespace = "";
                        } else {
                            namespace = element.getAttributeNamespace();
                        }

                        if (this.recursiveElements.contains(element)) {
                            ComplexType dummy = new ComplexType("{" + namespace + "}" + localTypeName, null, false);
                            dummy.setDummy(true);
                            dummy.setIsAnonymous(true);
                            typeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + namespace + "}" + localTypeName, dummy);
                            if (this.elementToTypeMap.get(element) == null) {
                                this.elementToTypeMap.put(element, typeRef);
                            }
                        }

                        typeRef = generateTypeForElement(element, element.getPatterns(), localTypeName, countRealElementNames);
                        if (this.elementToTypeMap.get(element) == null) {
                            this.elementToTypeMap.put(element, typeRef);
                        }
                    } else {
                        if (this.elementToTypeMap.get(element) != null) {
                            typeRef = this.elementToTypeMap.get(element);
                        }
                    }

                    de.tudortmund.cs.bonxai.xsd.Element xsdElement = new de.tudortmund.cs.bonxai.xsd.Element(currentNameKey, typeRef);

                    if (typeRef != null) {
                        if (!typeRef.getReference().isAnonymous()) {
                            xsdElement.setTypeAttr(true);
                        }
                    }

                    if (this.recursiveElements.contains(element)) {
                        this.addXSDElementToRNGElementMap(element, xsdElement);
                    }

                    if (xsdElement.getNamespace().equals(this.xmlSchema.getTargetNamespace()) || xsdElement.getNamespace().equals("")) {

                        if (!RelaxNG2XSDConverter.RECURSION_MODE_COMPLEXTYPE) {
                            if (this.recursiveElements.contains(element)) {

                                if (!this.usedTopLevelElementNames.contains(xsdElement.getName())) {
                                    SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> strElement = this.xmlSchema.getElementSymbolTable().updateOrCreateReference(xsdElement.getName(), xsdElement);

                                    this.xmlSchema.addElement(strElement);
                                    this.usedTopLevelElementNames.add(xsdElement.getName());

                                    ElementRef elementRef = new ElementRef(strElement);
                                    resultParticle.add(elementRef);

                                } else {
                                    if (!checkForDuplicateElement(currentNameKey, element)) {
                                        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> strElement = this.xmlSchema.getElementSymbolTable().updateOrCreateReference(xsdElement.getName(), xsdElement);

                                        ElementRef elementRef = new ElementRef(strElement);
                                        resultParticle.add(elementRef);
                                    } else {
                                        if (this.alreadyConvertedElements.get(element) != null) {
                                            resultParticle = this.alreadyConvertedElements.get(element);
                                        } else {
                                            // There is an other element with the same name as the current. Frame the current element into a attributeGroup.
                                            String groupName = "group_" + xsdElement.getLocalName();
                                            SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> strGroup = putParticleIntoGroup(groupName, xsdElement.getNamespace(), xsdElement, false);
                                            de.tudortmund.cs.bonxai.common.GroupRef groupRef = new GroupRef(strGroup);
                                            resultParticle.add(groupRef);
                                        }
                                    }
                                }
                            } else {
                                resultParticle.add(xsdElement);
                            }
                        } else {
                            if (this.recursiveElements.contains(element)) {
                                if (typeRef != null && typeRef.getReference() != null) {
                                    if (!topLevelTypes.contains(typeRef) && !this.refToTypeMap.containsValue(typeRef)) {

                                        if (!typeRef.getReference().isDummy()) {
                                            if (checkTopLevelTypes(typeRef.getReference().getName())) {
                                                topLevelTypes.add(typeRef);
                                                this.xmlSchema.addType(typeRef);
                                            }
                                            xsdElement.setTypeAttr(true);
                                            typeRef.getReference().setIsAnonymous(false);
                                        }
                                    }
                                }
                            }
                            resultParticle.add(xsdElement);
                        }
                    } else {
                        ImportedSchema importedSchema = updateOrCreateImportedSchema(xsdElement.getNamespace());
                        if (!usedTopLevelElementNames.contains(xsdElement.getName())) {
                            // Generate Imported XSDSchema and attach it to the basis schema
                            // Put the xsdElement to the correct XSDSchema
                            SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> strElement = this.xmlSchema.getElementSymbolTable().updateOrCreateReference(xsdElement.getName(), xsdElement);


                            importedSchema.getSchema().addElement(strElement);
                            usedTopLevelElementNames.add(xsdElement.getName());

                            importedSchema.getSchema().getElementSymbolTable().setReference(xsdElement.getName(), strElement);

                            if (typeRef != null) {
                                importedSchema.getSchema().getTypeSymbolTable().setReference(typeRef.getReference().getName(), typeRef);
                                if (!typeRef.getReference().isAnonymous() && !typeRef.getReference().isDummy()) {
                                    importedSchema.getSchema().addType(typeRef);
                                }
                            }

                            ElementRef elementRef = new ElementRef(strElement);
                            resultParticle.add(elementRef);

                        } else {
                            ElementRef elementRef = new ElementRef(importedSchema.getSchema().getElementSymbolTable().getReference(xsdElement.getName()));
                            resultParticle.add(elementRef);
                        }
                    }
                }
            }
        }

        if (!resultParticle.isEmpty()) {
            this.alreadyConvertedElements.put(element, resultParticle);
            return resultParticle;
        } else {
            // Exception on not initialized resultParticle
            throw new ParticleIsNullException(element.getClass().getName(), "{" + element.getAttributeNamespace() + "}" + element.getNameAttribute());
        }
    }

    /**
     * Method for generating a XML XSDSchema type for a given RELAX NG element and its child-pattern
     * @param element
     * @param patternList
     * @param localTypeName
     * @param countRealElementNames
     * @return SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Type>
     * @throws TypeIsNullException
     * @throws Exception
     */
    private SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Type> generateTypeForElement(Element element, LinkedList<Pattern> patternList, String localTypeName, Integer countRealElementNames) throws TypeIsNullException, Exception {

        HashSet<String> currentPatternInformation = new HashSet<String>();
        LinkedList<Pattern> patternsContainingElement = new LinkedList<Pattern>();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>();
        HashSet<Pattern> patternsContainingData = new HashSet<Pattern>();
        boolean refNameType = false;

        // Generate a XML XSDSchema type depending on the content patterns
        de.tudortmund.cs.bonxai.xsd.Type resultType = null;

        // Collect all information-tags about the element, which is the parent pattern
        if (this.patternInformation.get(element) != null) {
            currentPatternInformation.addAll(this.patternInformation.get(element));
        }

        for (Iterator<Pattern> it = patternList.iterator(); it.hasNext();) {
            Pattern pattern = it.next();
            // Collect all information-tags about the current pattern
            if (this.patternInformation.get(pattern) != null && !(pattern instanceof Element) && !(pattern instanceof Attribute)) {
                currentPatternInformation.addAll(this.patternInformation.get(pattern));
            }
            // Collect patterns with element-tags
            if ((this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element")) || pattern instanceof Element) {
                patternsContainingElement.add(pattern);
            }
            // Collect patterns with attribute-tags
            if ((this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute")) || pattern instanceof Attribute) {
                patternsContainingAttribute.add(pattern);
            }
            // Collect patterns with data- or value-tags
            if (((this.patternInformation.get(pattern) != null && (this.patternInformation.get(pattern).contains("data") || this.patternInformation.get(pattern).contains("value"))) || pattern instanceof Data || pattern instanceof Value) && (!(pattern instanceof Element) && !(pattern instanceof Attribute))) {
                patternsContainingData.add(pattern);
            }
        }

        if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternsContainingData.isEmpty() && currentPatternInformation.contains("notAllowed")) {

            /*******************************************************************
             * Case: element: not allowed
             *       --> null (no type)
             ******************************************************************/
            resultType = null;

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternsContainingData.isEmpty() && currentPatternInformation.contains("text")) {

            /*******************************************************************
             * Case: element with only text content
             *       --> SimpleType: String
             ******************************************************************/
            String namespace = RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE;

            SimpleType simpleType = new SimpleType("{" + namespace + "}" + "string", null, false);
            simpleType.setDummy(true);
            resultType = simpleType;

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternsContainingData.isEmpty() && currentPatternInformation.contains("empty")) {

            /*******************************************************************
             * Case: element with empty content
             *       --> ComplexType: empty
             ******************************************************************/
            String namespace = null;
            if (element.getAttributeNamespace() == null) {
                namespace = "";
            } else {
                namespace = element.getAttributeNamespace();
            }

            resultType = new ComplexType("{" + namespace + "}" + localTypeName, null, true);

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && !patternsContainingData.isEmpty()) {

            /*******************************************************************
             * Case: element without element or attribute content,
             *       but with simpleType content
             *       --> SimpleType
             ******************************************************************/
            SimpleType simpleType = null;
            if (patternsContainingData.size() == 1) {
                simpleType = this.patternSimpleTypeConverter.generateSimpleTypeForPattern(patternsContainingData.iterator().next(), new LinkedList<Pattern>());
            } else {
                // Exception --> There is more than one simpleTypePattern under the given element
                throw new TooManyDataOrValuePatternsUnderAnElementException(element, "Type: " + localTypeName);
            }

            resultType = simpleType;

        } else if (patternsContainingElement.isEmpty() && !patternsContainingAttribute.isEmpty() && !patternsContainingData.isEmpty()) {

            /*******************************************************************
             * Case: element with attribute content and simpleType content
             *       --> ComplexType with SimpleContent
             *           --> Attributes into extension,
             *           --> simpleType as base of extension
             ******************************************************************/
            SimpleType simpleType = null;
            SimpleContentExtension simpleContentExtension = null;

            if (patternsContainingData.size() == 1) {
                simpleType = this.patternSimpleTypeConverter.generateSimpleTypeForPattern(patternsContainingData.iterator().next(), new LinkedList<Pattern>());
            } else {
                // Exception --> There is more than one simpleTypePattern under the given element
                throw new TooManyDataOrValuePatternsUnderAnElementException(element, "Type: " + localTypeName);
            }

            SimpleContentType simpleContentType = null;
            if (simpleType != null) {
                SymbolTableRef<Type> strBaseType = null;
                if (simpleType != null) {
                    strBaseType = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(simpleType.getName(), simpleType);
                    if (!simpleType.getNamespace().equals(RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE)) {
                        simpleType.setIsAnonymous(false);
                        this.xmlSchema.addType(strBaseType);
                    }
                } else {
                    // Exception: There is an unexpected null simpleType under the given element
                    throw new SimpleTypeIsNullException(element, "Type: " + localTypeName);
                }
                simpleContentExtension = new SimpleContentExtension(strBaseType);
                simpleContentType = new SimpleContentType();
                simpleContentType.setInheritance(simpleContentExtension);
            }

            String namespace = null;
            if (element.getAttributeNamespace() == null) {
                namespace = "";
            } else {
                namespace = element.getAttributeNamespace();
            }
            ComplexType complexType = new ComplexType("{" + namespace + "}" + localTypeName, simpleContentType, true);

            // handle attribute content conversion
            LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(patternsContainingAttribute);

            for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
                AttributeParticle attributeParticle = it.next();
                if (simpleContentExtension != null) {
                    simpleContentExtension.addAttribute(attributeParticle);
                } else {
                    complexType.addAttribute(attributeParticle);
                }
            }

            resultType = complexType;

        } else if (patternsContainingElement.isEmpty() && !patternsContainingAttribute.isEmpty() && patternsContainingData.isEmpty()) {

            /*******************************************************************
             * Case: no elements, but attributes without simpleType content
             *       --> ComplexType with attributes
             ******************************************************************/
            String namespace = null;
            if (element.getAttributeNamespace() == null) {
                namespace = "";
            } else {
                namespace = element.getAttributeNamespace();
            }
            ComplexType complexType = new ComplexType("{" + namespace + "}" + localTypeName, null, true);

            // handle mixed/text content conversion
            if (currentPatternInformation.contains("mixed") || currentPatternInformation.contains("text") || !patternsContainingData.isEmpty()) {
                complexType.setMixed(true);
            }

            // handle attribute content conversion
            LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(patternsContainingAttribute);

            for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
                AttributeParticle attributeParticle = it.next();
                if (attributeParticle != null) {
                    complexType.addAttribute(attributeParticle);
                }
            }

            if (!complexType.getAttributes().isEmpty()) {
                resultType = complexType;
            } else {
                resultType = null;
            }

        } else if (!patternsContainingElement.isEmpty()) {

            /*******************************************************************
             * Case: element with element (and attribute) content
             *       --> ComplexType
             ******************************************************************/
            LinkedList<Pattern> typePattern = new LinkedList<Pattern>(patternsContainingElement);

            // Check if there is a ref that can be used as standalone type
            HashMap<Ref, LinkedList<Pattern>> typeMap = checkPatternListForRefForComplexType(typePattern);

            ComplexType refComplexType = null;
            SymbolTableRef<Type> strRefType = null;

            if (typeMap != null && !typeMap.isEmpty() && typeMap.keySet() != null) {
                // Case: There is a ref pattern that can be converted as a XML XSDSchema complexType
                Ref ref = typeMap.keySet().iterator().next();
                LinkedList<Pattern> remainingPatterns = new LinkedList<Pattern>(typeMap.get(ref));

                // Generate the type for the given ref recursivly
                strRefType = generateComplexTypeForRefRecursive(ref);

                // Set the generated complexType to the refComplexType variable for later usage
                refComplexType = (ComplexType) strRefType.getReference();
                refNameType = true;

                // Determine the pattern containing the found ref pattern
                Pattern patternWithRef = null;
                for (Iterator<Pattern> it = typePattern.iterator(); it.hasNext();) {
                    Pattern pattern = it.next();
                    if (!remainingPatterns.contains(pattern)) {
                        patternWithRef = pattern;
                        break;
                    }
                }

                // Remove the found "ref-parent-pattern" from all remaining pattern sets
                if (patternWithRef != null) {
                    LinkedList<Pattern> temp = new LinkedList<Pattern>();
                    temp.add(patternWithRef);
                    patternsContainingAttribute.removeAll(temp);
                    patternsContainingData.removeAll(temp);
                }
                patternsContainingElement = remainingPatterns;
            }

            // Case: Normal case, but with advertence to the possibly generated baseType
            ComplexType complexType = null;
            boolean mixed = false;
            de.tudortmund.cs.bonxai.common.Particle xsdParticle = null;
            LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeList = null;

            if ((refNameType && (!patternsContainingElement.isEmpty() || !patternsContainingAttribute.isEmpty() || !patternsContainingData.isEmpty())) || !refNameType) {

                String namespace = null;
                if (element.getAttributeNamespace() == null) {
                    namespace = "";
                } else {
                    namespace = element.getAttributeNamespace();
                }

                complexType = new ComplexType("{" + namespace + "}" + localTypeName, null, true);

                // handle attribute content conversion
                attributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(patternsContainingAttribute);

                if (!refNameType) {
                    for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        if (attributeParticle != null) {
                            complexType.addAttribute(attributeParticle);
                        }
                    }
                }

                // handle element content conversion

                if (patternsContainingElement.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();
                    for (Iterator<Pattern> it = patternsContainingElement.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        addParticleToSequencePattern(sequencePattern, convertPatternToElementParticleStructure(currentElementContentPattern));
                    }
                    if (!sequencePattern.getParticles().isEmpty()) {
                        xsdParticle = sequencePattern;
                    }

                } else if (patternsContainingElement.size() == 1) {
                    de.tudortmund.cs.bonxai.common.Particle resultParticle = convertPatternToElementParticleStructure(patternsContainingElement.iterator().next());
                    if (resultParticle != null) {
                        if (resultParticle instanceof de.tudortmund.cs.bonxai.xsd.Element || resultParticle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) {
                            SequencePattern sequencePattern = new SequencePattern();
                            sequencePattern.addParticle(resultParticle);
                            if (!sequencePattern.getParticles().isEmpty()) {
                                xsdParticle = sequencePattern;
                            }
                        } else {
                            xsdParticle = resultParticle;
                        }
                    }
                }

                // Determine if the element is within a (XOR) choice with Attributes or simpleType content
                if (currentPatternInformation.contains("optional") && xsdParticle != null) {
                    if (xsdParticle instanceof CountingPattern) {
                        CountingPattern countingPattern = (CountingPattern) xsdParticle;
                        if (countingPattern.getMin() != 0) {
                            CountingPattern newCountingPattern = new CountingPattern(0, countingPattern.getMax());
                            newCountingPattern.addParticle(countingPattern.getParticles().getFirst());
                            xsdParticle = newCountingPattern;
                        }
                    } else {
                        CountingPattern newCountingPattern = new CountingPattern(0, 1);
                        newCountingPattern.addParticle(xsdParticle);
                        xsdParticle = newCountingPattern;
                    }
                }

                // handle mixed/text content conversion
                if (currentPatternInformation.contains("mixed") || currentPatternInformation.contains("text") || !patternsContainingData.isEmpty()) {
                    mixed = true;
                }

                // Adding the element particles to the content of the refComplexType
                if (!refNameType) {
                    if (xsdParticle != null) {
                        ComplexContentType complexContentType = new ComplexContentType(xsdParticle, mixed);
                        de.tudortmund.cs.bonxai.common.Particle myParticle = null;
                        if (xsdParticle instanceof CountingPattern) {
                            myParticle = ((CountingPattern) xsdParticle).getParticles().getFirst();
                        } else {
                            myParticle = xsdParticle;
                        }
                        if (!(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupRef)) {
                            SequencePattern newSequencePattern = new SequencePattern();
                            newSequencePattern.addParticle(xsdParticle);
                            complexContentType.setParticle(newSequencePattern);
                        } else {
                            complexContentType.setParticle(xsdParticle);
                        }
                        complexType.setContent(complexContentType);
                    }
                } else {
                    // Extend ComplexType with attributes
                    strRefType.getReference().setDummy(false);
                    strRefType.getReference().setIsAnonymous(false);
                    ComplexContentExtension complexContentExtension = new ComplexContentExtension(strRefType);

                    for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        if (attributeParticle != null) {
                            complexContentExtension.addAttribute(attributeParticle);
                        }
                    }

                    ComplexContentType complexContentType = new ComplexContentType(xsdParticle, complexContentExtension, mixed);
                    de.tudortmund.cs.bonxai.common.Particle myParticle = null;

                    if (xsdParticle instanceof CountingPattern) {
                        myParticle = ((CountingPattern) xsdParticle).getParticles().getFirst();
                    } else {
                        myParticle = xsdParticle;
                    }
                    if (xsdParticle != null && !(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupRef)) {
                        SequencePattern newSequencePattern = new SequencePattern();
                        newSequencePattern.addParticle(xsdParticle);
                        complexContentType.setParticle(newSequencePattern);
                    } else {
                        complexContentType.setParticle(xsdParticle);
                    }
                    complexType.setContent(complexContentType);
                }
            } else {
                if (refComplexType != null) {
                    // Ref-type case: ref-type is the used type
                    complexType = refComplexType;
                }
            }

            if (mixed == true || xsdParticle != null || (attributeList != null && !attributeList.isEmpty()) || (complexType != null)) {
                resultType = complexType;
            } else {
                resultType = null;
            }

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternsContainingData.isEmpty()) {

            /*******************************************************************
             * Case: no elements, no attributes and no simpleType content
             *       --> null (no type)
             ******************************************************************/
            resultType = null;
        }

        String fullQualifiedTypeName = null;

        SymbolTableRef<Type> typeRef = null;
        if (resultType != null) {

            if (resultType instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) resultType;
                fullQualifiedTypeName = simpleType.getName();
                typeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(fullQualifiedTypeName, simpleType);

            } else if (resultType instanceof ComplexType) {

                ComplexType complexType = (ComplexType) resultType;
                if (!refNameType) {
                    complexType.setIsAnonymous(true);
                }
                fullQualifiedTypeName = complexType.getName();
                if (this.elementToTypeMap.containsKey(element)) {
                    typeRef = this.elementToTypeMap.get(element);
                    typeRef.setReference(resultType);
                } else {
                    typeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(fullQualifiedTypeName, complexType);
                }
            }
        }

        return typeRef;
    }

    /**
     * Convert a RELAX NG pattern to the corresponding XML XSDSchema particle structure
     * @param relaxNGPattern        basis of the conversion
     * @return de.tudortmund.cs.bonxai.common.Particle      result of the conversion
     * @throws Exception
     */
    private de.tudortmund.cs.bonxai.common.Particle convertPatternToElementParticleStructure(Pattern relaxNGPattern) throws Exception {
        de.tudortmund.cs.bonxai.common.Particle resultParticle = null;
        if (relaxNGPattern != null && this.patternInformation.get(relaxNGPattern) != null && this.patternInformation.get(relaxNGPattern).contains("element") || relaxNGPattern instanceof Element) {
            if (relaxNGPattern instanceof ZeroOrMore) {

                // Case: ZeroOrMore

                ZeroOrMore zeroOrMore = (ZeroOrMore) relaxNGPattern;
                CountingPattern countingPattern = new CountingPattern(0, null);

                LinkedList<Pattern> zeroOrMoreElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = zeroOrMore.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        zeroOrMoreElementPatterns.add(pattern);
                    }
                }

                if (zeroOrMoreElementPatterns.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();
                    for (Iterator<Pattern> it = zeroOrMoreElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToSequencePattern(sequencePattern, tempParticle);
                        }
                    }

                    if (!sequencePattern.getParticles().isEmpty()) {
                        countingPattern.addParticle(sequencePattern);
                    }
                } else if (zeroOrMoreElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(zeroOrMoreElementPatterns.getFirst());
                    if (tempParticle != null) {
                        countingPattern.addParticle(tempParticle);
                    }
                }

                if (!countingPattern.getParticles().isEmpty()) {
                    resultParticle = countingPattern;
                }

            } else if (relaxNGPattern instanceof Element) {

                // Case: Element

                Element innerElement = (Element) relaxNGPattern;

                if (this.recursiveElements.contains(innerElement)) {
                    // Handle recursive elements

                    LinkedList<de.tudortmund.cs.bonxai.common.Particle> elementParticles = null;
                    if (this.alreadyConvertedElements.get(innerElement) != null) {
                        elementParticles = this.alreadyConvertedElements.get(innerElement);
                    } else {
                        if (!(this.patternInformation.get(innerElement) != null && this.patternInformation.get(innerElement).contains("notAllowed"))) {
                            elementParticles = convertElement(innerElement);
                        }
                    }

                    if (elementParticles != null && elementParticles.size() > 1) {

                        ChoicePattern choicePattern = new ChoicePattern();
                        for (Iterator<de.tudortmund.cs.bonxai.common.Particle> it = elementParticles.iterator(); it.hasNext();) {
                            de.tudortmund.cs.bonxai.common.Particle innerParticle = it.next();
                            addParticleToChoicePattern(choicePattern, innerParticle);
                        }

                        resultParticle = choicePattern;

                    } else if (elementParticles != null && elementParticles.size() == 1) {
                        de.tudortmund.cs.bonxai.common.Particle elementParticle = elementParticles.getFirst();
                        resultParticle = elementParticle;
                    }

                } else {
                    if (!(this.patternInformation.get(innerElement) != null && this.patternInformation.get(innerElement).contains("notAllowed"))) {
                        LinkedList<de.tudortmund.cs.bonxai.common.Particle> elementParticles = convertElement(innerElement);

                        if (elementParticles.size() > 1) {
                            ChoicePattern choicePattern = new ChoicePattern();
                            for (Iterator<de.tudortmund.cs.bonxai.common.Particle> it = elementParticles.iterator(); it.hasNext();) {
                                de.tudortmund.cs.bonxai.common.Particle innerParticle = it.next();
                                addParticleToChoicePattern(choicePattern, innerParticle);
                            }
                            resultParticle = choicePattern;
                        } else if (elementParticles.size() == 1) {
                            resultParticle = elementParticles.getFirst();
                        }
                    }
                }

//                if (this.patternInformation.get(innerElement) != null && this.patternInformation.get(innerElement).contains("optional")) {
//                    CountingPattern countingPattern = new CountingPattern(0, 1);
//                    countingPattern.addParticle(resultParticle);
//                    resultParticle = countingPattern;
//                }


            } else if (relaxNGPattern instanceof Ref) {

                // Case: Ref

                Ref ref = (Ref) relaxNGPattern;

                if (RelaxNG2XSDConverter.REF_TO_GROUP_CONVERSION && this.refToGroupMap.get(ref.getUniqueRefID()) != null) {
                    GroupRef groupRef = new GroupRef(this.refToGroupMap.get(ref.getUniqueRefID()));
                    resultParticle = groupRef;
                } else {

                    LinkedList<Define> defineList = ref.getDefineList();
                    CombineMethod combineMethod = null;

                    LinkedList<Pattern> definePatterns = new LinkedList<Pattern>();

                    for (Iterator<Define> it = defineList.iterator(); it.hasNext();) {
                        Define define = it.next();

                        if (define.getCombineMethod() != null) {
                            combineMethod = define.getCombineMethod();
                        }

                        LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();

                        for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                            Pattern pattern = it1.next();
                            if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                                defineElementPatterns.add(pattern);
                            }
                        }

                        if (defineElementPatterns.size() > 1) {

                            // Add all patterns of one define to a group (this is a sequence in XSD)
                            // for the correct handling of the combine-method of the define-elements
                            Group group = new Group();
                            for (Iterator<Pattern> it2 = defineElementPatterns.iterator(); it2.hasNext();) {
                                Pattern innerPattern = it2.next();
                                group.addPattern(innerPattern);
                            }
                            definePatterns.add(group);

                            PatternInformationCollector pdc = new PatternInformationCollector(this.relaxng);
                            HashSet<String> dataForNewPattern = pdc.getDataForPattern(group);
                            this.patternInformation.put(group, dataForNewPattern);

                        } else if (defineElementPatterns.size() == 1) {
                            definePatterns.add(defineElementPatterns.getFirst());
                        }
                    }

                    String namespace = null;
                    String groupName = null;
                    SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> strGroupDummy = null;
                    // Check if it is useful to build a XML XSDSchema group from a given Define.
                    if (this.isDefinePatternListUsefulAsXSDGroup(definePatterns)) {
                        groupName = ref.getRefName();
                        if (this.usedLocalGroupNames.contains(groupName)) {
                            groupName = groupName + this.usedLocalGroupNames.size();
                        }
                        this.usedLocalGroupNames.add(groupName);
                        namespace = this.xmlSchema.getTargetNamespace();
                        if (namespace == null) {
                            namespace = "";
                        }
                        strGroupDummy = this.xmlSchema.getGroupSymbolTable().updateOrCreateReference("{" + namespace + "}" + groupName, null);
                        this.refToGroupMap.put(ref.getUniqueRefID(), strGroupDummy);
                    }


                    if (definePatterns.size() > 1) {
                        de.tudortmund.cs.bonxai.common.Particle combineParticle = null;

                        LinkedList<Particle> conversionResults = new LinkedList<Particle>();
                        boolean allPatternAllowed = true;


                        for (Iterator<Pattern> it = definePatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                            if (tempParticle != null) {
                                if (!RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
                                    if (!(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.Element) && !(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) && !(tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern)) {
                                        allPatternAllowed = false;
                                    }

                                    if (tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern) {
                                        CountingPattern countingPattern = (CountingPattern) tempParticle;

                                        if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                                            allPatternAllowed = false;
                                        }

                                        if (countingPattern.getMax() == null || (countingPattern.getMax() != null && countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                                            allPatternAllowed = false;
                                        }
                                    }
                                }
                                conversionResults.add(tempParticle);
                            }
                        }
                        if (!conversionResults.isEmpty()) {
                            if (combineMethod != null && combineMethod.equals(CombineMethod.choice)) {
                                combineParticle = new ChoicePattern();
                            } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && allPatternAllowed) {
                                combineParticle = new AllPattern();
                            } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                                combineParticle = new ChoicePattern();
                            } else {
                                // Exception: There is no combineMethod defined for the current ref.
                                throw new NoCombineMethodException("ref: " + ref.getRefName());
                            }

                            for (Iterator<Particle> it = conversionResults.iterator(); it.hasNext();) {
                                Particle currentParticle = it.next();

                                if (combineParticle instanceof ChoicePattern) {
                                    ChoicePattern tempChoicePattern = (ChoicePattern) combineParticle;
                                    addParticleToChoicePattern(tempChoicePattern, currentParticle);
                                } else if (combineParticle instanceof AllPattern) {
                                    AllPattern tempAllPattern = (AllPattern) combineParticle;
                                    tempAllPattern.addParticle(currentParticle);
                                }
                            }

                            if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                                CountingPattern countingPattern = new CountingPattern(0, null);
                                countingPattern.addParticle(combineParticle);
                                resultParticle = countingPattern;
                            } else {
                                resultParticle = combineParticle;
                            }
                        }
                    } else if (definePatterns.size() == 1) {
                        Particle tempParticle = convertPatternToElementParticleStructure(definePatterns.getFirst());
                        if (tempParticle != null) {
                            resultParticle = tempParticle;
                        }
                    }

                    if (RelaxNG2XSDConverter.REF_TO_GROUP_CONVERSION && resultParticle != null && strGroupDummy != null) {
                        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> strGroup = this.putParticleIntoGroup(groupName, namespace, resultParticle, true);
                        de.tudortmund.cs.bonxai.common.GroupRef groupRef = new GroupRef(strGroup);
                        this.refToGroupMap.put(ref.getUniqueRefID(), strGroup);
                        if (strGroup != null) {
                            resultParticle = groupRef;
                        }
                    } else {
                        this.refToGroupMap.remove(ref.getUniqueRefID());
                    }
                }
            } else if (relaxNGPattern instanceof Group) {

                // Case: Group

                Group group = (Group) relaxNGPattern;

                SequencePattern sequencePattern = new SequencePattern();

                LinkedList<Pattern> groupElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = group.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        groupElementPatterns.add(pattern);
                    }
                }

                if (groupElementPatterns.size() > 1) {
                    for (Iterator<Pattern> it = groupElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToSequencePattern(sequencePattern, tempParticle);
                        }
                    }
                    if (!sequencePattern.getParticles().isEmpty()) {
                        resultParticle = sequencePattern;
                    }
                } else if (groupElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(groupElementPatterns.getFirst());
                    if (tempParticle != null) {
                        resultParticle = tempParticle;
                    }
                }

            } else if (relaxNGPattern instanceof OneOrMore) {

                // Case: OneOrMore

                OneOrMore oneOrMore = (OneOrMore) relaxNGPattern;
                CountingPattern countingPattern = new CountingPattern(1, null);
                LinkedList<Pattern> oneOrMoreElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = oneOrMore.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        oneOrMoreElementPatterns.add(pattern);
                    }
                }

                if (oneOrMoreElementPatterns.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();
                    for (Iterator<Pattern> it = oneOrMoreElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToSequencePattern(sequencePattern, tempParticle);
                        }
                    }
                    if (!sequencePattern.getParticles().isEmpty()) {
                        countingPattern.addParticle(sequencePattern);
                    }
                } else if (oneOrMoreElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(oneOrMoreElementPatterns.getFirst());
                    if (tempParticle != null) {
                        countingPattern.addParticle(tempParticle);
                    }
                }

                if (!countingPattern.getParticles().isEmpty()) {
                    resultParticle = countingPattern;
                }
            } else if (relaxNGPattern instanceof Optional) {

                // Case: Optional

                Optional optional = (Optional) relaxNGPattern;
                CountingPattern countingPattern = new CountingPattern(0, 1);
                LinkedList<Pattern> optionalElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = optional.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        optionalElementPatterns.add(pattern);
                    }
                }

                if (optionalElementPatterns.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();
                    for (Iterator<Pattern> it = optionalElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToSequencePattern(sequencePattern, tempParticle);
                        }
                    }
                    if (!sequencePattern.getParticles().isEmpty()) {
                        countingPattern.addParticle(sequencePattern);
                    }
                } else if (optionalElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(optionalElementPatterns.getFirst());
                    if (tempParticle != null) {
                        countingPattern.addParticle(tempParticle);
                    }
                }

                if (!countingPattern.getParticles().isEmpty()) {
                    resultParticle = countingPattern;
                }
            } else if (relaxNGPattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) relaxNGPattern;

                LinkedList<Pattern> choiceElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = choice.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        choiceElementPatterns.add(pattern);
                    }
                }

                if (choiceElementPatterns.size() > 1) {
                    ChoicePattern choicePattern = new ChoicePattern();

                    for (Iterator<Pattern> it = choiceElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToChoicePattern(choicePattern, tempParticle);
                        }
                    }
                    if (!choicePattern.getParticles().isEmpty()) {
                        resultParticle = choicePattern;
                    }
                } else if (choiceElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(choiceElementPatterns.getFirst());
                    if (tempParticle != null) {
                        resultParticle = tempParticle;
                    }
                }

            } else if (relaxNGPattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) relaxNGPattern;

                LinkedList<Pattern> interleaveElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = interleave.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        interleaveElementPatterns.add(pattern);
                    }
                }

                if (interleaveElementPatterns.size() > 1) {

                    de.tudortmund.cs.bonxai.common.Particle combineParticle = null;

                    LinkedList<Particle> conversionResults = new LinkedList<Particle>();
                    boolean allPatternAllowed = true;

                    for (Iterator<Pattern> it = interleaveElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (!RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
                            if (!(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.Element) && !(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) && !(tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern)) {
                                allPatternAllowed = false;
                            }

                            if (tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern) {
                                CountingPattern countingPattern = (CountingPattern) tempParticle;

                                if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                                    allPatternAllowed = false;
                                }

                                if (countingPattern.getMax() == null || (countingPattern.getMax() != null && countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                                    allPatternAllowed = false;
                                }
                            }
                        }
                        if (tempParticle != null) {
                            conversionResults.add(tempParticle);
                        }
                    }
                    if (!conversionResults.isEmpty()) {
                        if (allPatternAllowed) {
                            combineParticle = new AllPattern();
                        } else {
                            combineParticle = new ChoicePattern();
                        }

                        for (Iterator<Particle> it = conversionResults.iterator(); it.hasNext();) {
                            Particle currentParticle = it.next();

                            if (combineParticle instanceof ChoicePattern) {
                                ChoicePattern tempChoicePattern = (ChoicePattern) combineParticle;
                                addParticleToChoicePattern(tempChoicePattern, currentParticle);
                            } else if (combineParticle instanceof AllPattern) {
                                AllPattern tempAllPattern = (AllPattern) combineParticle;
                                tempAllPattern.addParticle(currentParticle);
                            }
                        }

                        if (!allPatternAllowed) {
                            CountingPattern countingPattern = new CountingPattern(0, null);
                            countingPattern.addParticle(combineParticle);
                            resultParticle = countingPattern;
                        } else {
                            resultParticle = combineParticle;
                        }
                    }
                }
            } else if (relaxNGPattern instanceof ParentRef) {

                // Case: ParentRef

                ParentRef parentRef = (ParentRef) relaxNGPattern;

                LinkedList<Define> defineList = parentRef.getDefineList();
                CombineMethod combineMethod = null;

                LinkedList<Pattern> definePatterns = new LinkedList<Pattern>();

                for (Iterator<Define> it = defineList.iterator(); it.hasNext();) {
                    Define define = it.next();

                    if (define.getCombineMethod() != null) {
                        combineMethod = define.getCombineMethod();
                    }

                    LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            defineElementPatterns.add(pattern);
                        }
                    }

                    if (defineElementPatterns.size() > 1) {

                        // Add all patterns of one define to a group (this is a sequence in XSD)
                        // for the correct handling of the combine-method of the define-elements
                        Group group = new Group();
                        for (Iterator<Pattern> it2 = defineElementPatterns.iterator(); it2.hasNext();) {
                            Pattern innerPattern = it2.next();
                            group.addPattern(innerPattern);
                        }
                        definePatterns.add(group);

                        PatternInformationCollector pdc = new PatternInformationCollector(this.relaxng);
                        HashSet<String> dataForNewPattern = pdc.getDataForPattern(group);
                        this.patternInformation.put(group, dataForNewPattern);

                    } else if (defineElementPatterns.size() == 1) {
                        definePatterns.add(defineElementPatterns.getFirst());
                    }
                }

                if (definePatterns.size() > 1) {
                    de.tudortmund.cs.bonxai.common.Particle combineParticle = null;

                    LinkedList<Particle> conversionResults = new LinkedList<Particle>();
                    boolean allPatternAllowed = true;


                    for (Iterator<Pattern> it = definePatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            if (!RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
                                if (!(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.Element) && !(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) && !(tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern)) {
                                    allPatternAllowed = false;
                                }

                                if (tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern) {
                                    CountingPattern countingPattern = (CountingPattern) tempParticle;

                                    if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                                        allPatternAllowed = false;
                                    }

                                    if (countingPattern.getMax() == null || (countingPattern.getMax() != null && countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                                        allPatternAllowed = false;
                                    }
                                }
                            }
                            conversionResults.add(tempParticle);
                        }
                    }
                    if (!conversionResults.isEmpty()) {
                        if (combineMethod != null && combineMethod.equals(CombineMethod.choice)) {
                            combineParticle = new ChoicePattern();
                        } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && allPatternAllowed) {
                            combineParticle = new AllPattern();
                        } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                            combineParticle = new ChoicePattern();
                        } else {
                            // Exception: There is no combineMethod defined for the current parentRef.
                            throw new NoCombineMethodException("parentRef: " + parentRef.getRefName());
                        }

                        for (Iterator<Particle> it = conversionResults.iterator(); it.hasNext();) {
                            Particle currentParticle = it.next();

                            if (combineParticle instanceof ChoicePattern) {
                                ChoicePattern tempChoicePattern = (ChoicePattern) combineParticle;
                                addParticleToChoicePattern(tempChoicePattern, currentParticle);
                            } else if (combineParticle instanceof AllPattern) {
                                AllPattern tempAllPattern = (AllPattern) combineParticle;
                                tempAllPattern.addParticle(currentParticle);
                            }
                        }

                        if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                            CountingPattern countingPattern = new CountingPattern(0, null);
                            countingPattern.addParticle(combineParticle);
                            resultParticle = countingPattern;
                        } else {
                            resultParticle = combineParticle;
                        }
                    }
                } else if (definePatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(definePatterns.getFirst());
                    if (tempParticle != null) {
                        resultParticle = tempParticle;
                    }
                }
            } else if (relaxNGPattern instanceof Mixed) {

                // Case: Mixed

                Mixed mixed = (Mixed) relaxNGPattern;

                LinkedList<Pattern> mixedElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = mixed.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        mixedElementPatterns.add(pattern);
                    }
                }

                if (mixedElementPatterns.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();

                    for (Iterator<Pattern> it = mixedElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToSequencePattern(sequencePattern, tempParticle);
                        }
                    }
                    if (!sequencePattern.getParticles().isEmpty()) {
                        resultParticle = sequencePattern;
                    }
                } else if (mixedElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(mixedElementPatterns.getFirst());
                    if (tempParticle != null) {
                        resultParticle = tempParticle;
                    }
                }

            } else if (relaxNGPattern instanceof Grammar) {

                // Case: Grammar

                Grammar grammar = (Grammar) relaxNGPattern;

                if (RelaxNG2XSDConverter.RECURSION_MODE_COMPLEXTYPE || !RelaxNG2XSDConverter.RECURSION_MODE_ELEMENT_SET_ALL_ELEMENTS_TOPLEVEL) {
                    this.findRecursiveElementsInGrammar(grammar);
                }

                LinkedList<Pattern> grammarElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = grammar.getStartPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        grammarElementPatterns.add(pattern);
                    }
                }
                CombineMethod combineMethod = grammar.getStartCombineMethod();

                LinkedList<Particle> conversionResults = new LinkedList<Particle>();
                boolean allPatternAllowed = true;

                if (grammarElementPatterns.size() > 1) {
                    de.tudortmund.cs.bonxai.common.Particle combineParticle = null;
                    for (Iterator<Pattern> it = grammarElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            if (!RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
                                if (!(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.Element) && !(tempParticle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) && !(tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern)) {
                                    allPatternAllowed = false;
                                }

                                if (tempParticle instanceof de.tudortmund.cs.bonxai.common.CountingPattern) {
                                    CountingPattern countingPattern = (CountingPattern) tempParticle;

                                    if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                                        allPatternAllowed = false;
                                    }

                                    if (countingPattern.getMax() == null || (countingPattern.getMax() != null && countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                                        allPatternAllowed = false;
                                    }
                                }
                            }
                            conversionResults.add(tempParticle);
                        }
                    }
                    if (!conversionResults.isEmpty()) {
                        if (combineMethod != null && combineMethod.equals(CombineMethod.choice)) {
                            combineParticle = new ChoicePattern();
                        } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && allPatternAllowed) {
                            combineParticle = new AllPattern();
                        } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                            combineParticle = new ChoicePattern();
                        } else {
                            // Exception: There is no combineMethod defined for the current pef.
                            throw new NoCombineMethodException("grammar: " + "start pattern");
                        }

                        for (Iterator<Particle> it = conversionResults.iterator(); it.hasNext();) {
                            Particle currentParticle = it.next();

                            if (combineParticle instanceof ChoicePattern) {
                                ChoicePattern tempChoicePattern = (ChoicePattern) combineParticle;
                                addParticleToChoicePattern(tempChoicePattern, currentParticle);
                            } else if (combineParticle instanceof AllPattern) {
                                AllPattern tempAllPattern = (AllPattern) combineParticle;
                                tempAllPattern.addParticle(currentParticle);
                            }
                        }

                        if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                            CountingPattern countingPattern = new CountingPattern(0, null);
                            countingPattern.addParticle(combineParticle);
                            resultParticle = countingPattern;
                        } else {
                            resultParticle = combineParticle;
                        }
                    }
                } else if (grammarElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(grammarElementPatterns.getFirst());
                    if (tempParticle != null) {
                        resultParticle = tempParticle;
                    }
                }
            }
        }
        return resultParticle;
    }

    /**
     * Find recursive elements in a given grammar
     *
     * Follow the pattern structure via a dfs like algorithm.
     * If a circle is found, all elements on this circle are recursive elements.
     *
     * @param grammar
     */
    private void findRecursiveElementsInGrammar(Grammar grammar) throws RecursionWithoutElementException {
        if (grammar != null && !grammar.getDefineLookUpTable().getAllReferencedObjects().isEmpty()) {

            for (Iterator<LinkedList<Define>> it = grammar.getDefineLookUpTable().getAllReferencedObjects().iterator(); it.hasNext();) {
                LinkedList<Define> defineList = it.next();
                int countDefines = 1;
                for (Iterator<Define> it1 = defineList.iterator(); it1.hasNext();) {
                    Define define = it1.next();

                    countDefines++;
                    int countPatterns = 1;
                    LinkedList<Object> currentParents = new LinkedList<Object>();
                    currentParents.add(define.getName());
                    for (Iterator<Pattern> it2 = define.getPatterns().iterator(); it2.hasNext();) {
                        Pattern currentPattern = it2.next();

                        countPatterns++;
                        this.findRecursiveElementsInPattern(currentPattern, currentParents);
                    }
                }
            }
        }
    }

    /**
     * Recursive method for finding all recursive elements in a given RELAX NG pattern
     * @param relaxNGPattern        source for the search
     * @param currentParents        perceive information about the path in the pattern structure to avoid infinit loops
     */
    private void findRecursiveElementsInPattern(Pattern relaxNGPattern, LinkedList<Object> currentParents) throws RecursionWithoutElementException {

        if (relaxNGPattern != null) {
            if (this.patternInformation.get(relaxNGPattern) != null && this.patternInformation.get(relaxNGPattern).contains("element") || relaxNGPattern instanceof Element) {
                if (relaxNGPattern instanceof ZeroOrMore) {

                    // Case: ZeroOrMore

                    ZeroOrMore zeroOrMore = (ZeroOrMore) relaxNGPattern;
                    // System.out.println("ZeroOrMore");
                    LinkedList<Pattern> zeroOrMoreElementPatterns = new LinkedList<Pattern>();
                    for (Iterator<Pattern> it1 = zeroOrMore.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            zeroOrMoreElementPatterns.add(pattern);
                        }
                    }
                    if (zeroOrMoreElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = zeroOrMoreElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Element) {

                    // Case: Element

                    Element innerElement = (Element) relaxNGPattern;
                    // System.out.println("Element: " + (innerElement).getNameAttribute());

                    currentParents.add(innerElement);

                    LinkedList<Pattern> elementPatterns = new LinkedList<Pattern>();
                    for (Iterator<Pattern> it1 = innerElement.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            elementPatterns.add(pattern);
                        }
                    }
                    if (elementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = elementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Ref) {

                    // Case: Ref

                    Ref ref = (Ref) relaxNGPattern;
                    // System.out.println("Ref: " + ref.getRefName());

                    if (!currentParents.contains(ref.getUniqueRefID())) {
                        currentParents.add(ref.getUniqueRefID());
                        LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();

                        for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                            Define define = it.next();
                            for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                                Pattern pattern = it1.next();
                                if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                                    defineElementPatterns.add(pattern);
                                }
                            }
                        }
                        if (defineElementPatterns.size() > 0) {
                            for (Iterator<Pattern> it = defineElementPatterns.iterator(); it.hasNext();) {
                                Pattern currentElementContentPattern = it.next();
                                findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                            }
                        }
                    } else {
                        // System.out.println("Define already seen: " + ref.getRefName());

                        int firstPositionOfDefineNameInParentList = currentParents.indexOf(ref.getRefName());
                        int countElementsInLoop = 0;
                        for (int i = currentParents.size() - 1; i > firstPositionOfDefineNameInParentList; i--) {
                            Object currentParentObject = currentParents.get(i);
                            if (currentParentObject instanceof Element) {
                                recursiveElements.add((Element) currentParentObject);
                                countElementsInLoop++;
                            }
                        }

                        this.refToGroupMap.put(ref.getUniqueRefID(), null);

                        if (countElementsInLoop < 1) {
                            throw new RecursionWithoutElementException();
                        }

                    }
                } else if (relaxNGPattern instanceof Group) {

                    // Case: Group

                    Group group = (Group) relaxNGPattern;
                    // System.out.println("Group");
                    LinkedList<Pattern> groupElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = group.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            groupElementPatterns.add(pattern);
                        }
                    }
                    if (groupElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = groupElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof OneOrMore) {

                    // Case: OneOrMore

                    OneOrMore oneOrMore = (OneOrMore) relaxNGPattern;
                    // System.out.println("OneOrMore");
                    LinkedList<Pattern> oneOrMoreElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = oneOrMore.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            oneOrMoreElementPatterns.add(pattern);
                        }
                    }
                    if (oneOrMoreElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = oneOrMoreElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Optional) {

                    // Case: Optional

                    Optional optional = (Optional) relaxNGPattern;
                    // System.out.println("Optional");
                    LinkedList<Pattern> optionalElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = optional.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            optionalElementPatterns.add(pattern);
                        }
                    }
                    if (optionalElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = optionalElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Choice) {

                    // Case: Choice

                    Choice choice = (Choice) relaxNGPattern;
                    // System.out.println("Choice");
                    LinkedList<Pattern> choiceElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = choice.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            choiceElementPatterns.add(pattern);
                        }
                    }
                    if (choiceElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = choiceElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Interleave) {

                    // Case: Interleave

                    Interleave interleave = (Interleave) relaxNGPattern;
                    // System.out.println("Interleave");
                    LinkedList<Pattern> interleaveElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = interleave.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            interleaveElementPatterns.add(pattern);
                        }
                    }
                    if (interleaveElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = interleaveElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof ParentRef) {

                    // Case: ParentRef

                    ParentRef parentRef = (ParentRef) relaxNGPattern;
                    // System.out.println("ParentRef: " + parentRef.getRefName());

                    if (!currentParents.contains(parentRef.getUniqueRefID())) {
                        currentParents.add(parentRef.getUniqueRefID());
                        LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();

                        for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                            Define define = it.next();
                            for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                                Pattern pattern = it1.next();
                                if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                                    defineElementPatterns.add(pattern);
                                }
                            }
                        }
                        if (defineElementPatterns.size() > 0) {
                            for (Iterator<Pattern> it = defineElementPatterns.iterator(); it.hasNext();) {
                                Pattern currentElementContentPattern = it.next();
                                findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                            }
                        }
                    } else {

                        int firstPositionOfDefineNameInParentList = currentParents.indexOf(parentRef.getRefName());
                        int countElementsInLoop = 0;
                        for (int i = currentParents.size() - 1; i > firstPositionOfDefineNameInParentList; i--) {
                            Object currentParentObject = currentParents.get(i);
                            if (currentParentObject instanceof Element) {
                                recursiveElements.add((Element) currentParentObject);
                                countElementsInLoop++;
                            }
                        }

                        this.refToGroupMap.put(parentRef.getUniqueRefID(), null);

                        if (countElementsInLoop < 1) {
                            throw new RecursionWithoutElementException();
                        }
                    }
                } else if (relaxNGPattern instanceof Mixed) {

                    // Case: Mixed

                    Mixed mixed = (Mixed) relaxNGPattern;
                    // System.out.println("Mixed");
                    LinkedList<Pattern> mixedElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = mixed.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            mixedElementPatterns.add(pattern);
                        }
                    }
                    if (mixedElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = mixedElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Grammar) {

                    // Case: Grammar

                    Grammar grammar = (Grammar) relaxNGPattern;
                    // System.out.println("Mixed");
                    LinkedList<Pattern> grammarElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = grammar.getStartPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            grammarElementPatterns.add(pattern);
                        }
                    }
                    if (grammarElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = grammarElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add a XML XSDSchema particle to a sequencePattern - avoid sequences in sequences
     * @param sequencePattern
     * @param particle
     */
    private void addParticleToSequencePattern(SequencePattern sequencePattern, Particle particle) {
        if (particle != null) {
            if (particle instanceof SequencePattern) {
                SequencePattern innerSequencePattern = (SequencePattern) particle;
                for (Iterator<Particle> it = innerSequencePattern.getParticles().iterator(); it.hasNext();) {
                    Particle innerParticle = it.next();
                    sequencePattern.addParticle(innerParticle);
                }
            } else {
                sequencePattern.addParticle(particle);
            }
        }
    }

    /**
     * Add a XML XSDSchema particle to a choicePattern - avoid choices in choices
     * @param choicePattern
     * @param particle
     */
    private void addParticleToChoicePattern(ChoicePattern choicePattern, Particle particle) {
        if (particle != null) {
            if (particle instanceof ChoicePattern) {
                ChoicePattern innerChoicePattern = (ChoicePattern) particle;
                for (Iterator<Particle> it = innerChoicePattern.getParticles().iterator(); it.hasNext();) {
                    Particle innerParticle = it.next();
                    choicePattern.addParticle(innerParticle);
                }
            } else {
                choicePattern.addParticle(particle);
            }
        }
    }

    /**
     * Register a generated full qualified name for a RELAX NG element
     * @param fqName    String
     * @param element   RELAX NG element pattern
     */
    private void registerElementFQName(String fqName, Element element) {
        if (this.fqNamesToRNGElements.get(fqName) == null) {
            HashSet<Element> newElementSet = new HashSet<Element>();
            newElementSet.add(element);
            this.fqNamesToRNGElements.put(fqName, newElementSet);
        } else {
            HashSet<Element> newElementSet = this.fqNamesToRNGElements.get(fqName);
            newElementSet.add(element);
        }
    }

    /**
     * Check for duplicate full qualified element names
     * @param fqName
     * @param element
     * @return boolean      true, is there is another element with this name
     */
    private boolean checkForDuplicateElement(String fqName, Element element) {
        boolean returnBool = false;
        if (this.fqNamesToRNGElements.get(fqName) != null) {
            HashSet<Element> newElementSet = this.fqNamesToRNGElements.get(fqName);

            for (Iterator<Element> it = newElementSet.iterator(); it.hasNext();) {
                Element element1 = it.next();
                if (element1 != element) {
                    returnBool = true;
                }
            }
        }
        return returnBool;
    }

    /**
     * Method for putting a XML XSDSchema particle into a XML XSDSchema group
     *
     * @param localGroupName
     * @param namespace
     * @param particle
     * @return de.tudortmund.cs.bonxai.common.GroupRef
     * @throws ParticleIsNullException
     * @throws Exception
     */
    private SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> putParticleIntoGroup(String localGroupName, String namespace, de.tudortmund.cs.bonxai.common.Particle particle, boolean forceGroupName) throws ParticleIsNullException, Exception {

        if (!forceGroupName) {
            if (this.usedLocalGroupNames.contains(localGroupName)) {
                localGroupName = localGroupName + "_" + this.usedLocalGroupNames.size();
            }
        }
        this.usedLocalGroupNames.add(localGroupName);

        String fqGroupName = "{" + namespace + "}" + localGroupName;

        ImportedSchema importedSchema = null;

        String targetNamespace = this.xmlSchema.getTargetNamespace();
        if (targetNamespace == null) {
            targetNamespace = "";
        }

        if (!namespace.equals(targetNamespace)) {
            // Generate Imported XSDSchema and attach it to the basis schema
            importedSchema = updateOrCreateImportedSchema(namespace);
        }

        ParticleContainer groupParticleContainer = null;

        Particle particleForCheck = null;

        if (particle instanceof CountingPattern) {
            particleForCheck = ((CountingPattern) particle).getParticles().getFirst();
        } else {
            particleForCheck = particle;
        }

        Type type = null;
        if (particleForCheck instanceof de.tudortmund.cs.bonxai.common.AllPattern || particleForCheck instanceof de.tudortmund.cs.bonxai.common.ChoicePattern || particleForCheck instanceof de.tudortmund.cs.bonxai.common.SequencePattern) {
            groupParticleContainer = (ParticleContainer) particle;
        } else { //if (particleForCheck instanceof de.tudortmund.cs.bonxai.xsd.Element || particleForCheck instanceof de.tudortmund.cs.bonxai.common.AnyPattern || particleForCheck instanceof de.tudortmund.cs.bonxai.common.ElementRef) {
            SequencePattern sequencePattern = new SequencePattern();
            sequencePattern.addParticle(particle);
            groupParticleContainer = sequencePattern;

            if (particleForCheck instanceof de.tudortmund.cs.bonxai.xsd.Element) {
                de.tudortmund.cs.bonxai.xsd.Element currentElement = (de.tudortmund.cs.bonxai.xsd.Element) particleForCheck;
                type = currentElement.getType();
            }
        }

        if (groupParticleContainer != null) {
            de.tudortmund.cs.bonxai.xsd.Group group = new de.tudortmund.cs.bonxai.xsd.Group(fqGroupName, groupParticleContainer);
            SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> strGroup = this.xmlSchema.getGroupSymbolTable().updateOrCreateReference(group.getName(), group);

            if (!usedTopLevelGroupNames.contains(group.getName())) {
                if (importedSchema != null) {
                    importedSchema.getSchema().getGroupSymbolTable().setReference(group.getName(), strGroup);
                    importedSchema.getSchema().addGroup(strGroup);

                    if (type != null) {
                        importedSchema.getSchema().getTypeSymbolTable().setReference(type.getName(), this.xmlSchema.getTypeSymbolTable().getReference(type.getName()));
                    }

                } else {
                    this.xmlSchema.addGroup(strGroup);
                }
                usedTopLevelGroupNames.add(group.getName());
            }

            return strGroup;
        } else {
            throw new ParticleIsNullException(fqGroupName, "see inner particle");
        }
    }

    /**
     * Method for adding a XML XSDSchema element to the RELAX NG to XSD Element map
     * @param rngElement    - key of the map
     * @param xsdElement    - XML XSDSchema element to store
     */
    private void addXSDElementToRNGElementMap(Element rngElement, de.tudortmund.cs.bonxai.xsd.Element xsdElement) {
        if (this.rngElementToxsdElements.get(rngElement) != null) {
            this.rngElementToxsdElements.get(rngElement).add(xsdElement);
        } else {
            HashSet<de.tudortmund.cs.bonxai.xsd.Element> tempHashSet = new HashSet<de.tudortmund.cs.bonxai.xsd.Element>();
            tempHashSet.add(xsdElement);
            this.rngElementToxsdElements.put(rngElement, tempHashSet);
        }
    }

    /**
     * Method for refreshing the types of recursivly generated inner elements
     *
     * The new type for an element is stored in the elementToTypeMap.
     * It stores XML XSDSchema types for RELAX NG elements.
     *
     * The resulting XML XSDSchema elements for a RELAX NG element are stored in
     * the rngElementToxsdElements map.
     *
     */
    private void setGeneratedTypesToInnerElements() {
        for (Iterator<Element> it = this.rngElementToxsdElements.keySet().iterator(); it.hasNext();) {
            Element element = it.next();
            SymbolTableRef<Type> strType = this.elementToTypeMap.get(element);
            if (strType != null) {
                for (Iterator<de.tudortmund.cs.bonxai.xsd.Element> it1 = this.rngElementToxsdElements.get(element).iterator(); it1.hasNext();) {
                    de.tudortmund.cs.bonxai.xsd.Element xsdElement = it1.next();
                    if (RelaxNG2XSDConverter.RECURSION_MODE_COMPLEXTYPE && !strType.getReference().isDummy()) {
                        strType.getReference().setIsAnonymous(false);
                    }
                    xsdElement.setType(strType);
                }
            }
        }
    }

    /**
     * Check a given patternlist for a ref that can be converted to a XML XSDSchema
     * complexType.
     * @param patternList       Source for the check
     * @return HashMap<Ref, LinkedList<Pattern>>        returns a HashMap with only one entry.
     *
     * The key is the found ref pattern that can be converted to a XML XSDSchema
     * complexType and as value there is a list of all other patterns not
     * containing this found ref. If there is no such ref, null is returned.
     */
    private HashMap<Ref, LinkedList<Pattern>> checkPatternListForRefForComplexType(LinkedList<Pattern> patternList) {
        // Initialize the return variable
        Ref ref = null;
        // The parentPattern is only used to determine which pattern is the direct parent of the possibly found ref
        Pattern parentPattern = null;
        boolean first = true;
        for (Iterator<Pattern> it = patternList.iterator(); it.hasNext();) {
            Pattern pattern = it.next();
            if ((pattern instanceof Ref)) {

                // Case: Ref

                ref = (Ref) pattern;
            } else if (pattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) pattern;
                if (choice.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(choice.getPatterns());
                }
            } else if (pattern instanceof Group) {

                // Case: Group

                Group group = (Group) pattern;
                if (group.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(group.getPatterns());
                }
            } else if (pattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) pattern;
                if (interleave.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(interleave.getPatterns());
                }
            }
            if (ref != null && first) {
                parentPattern = pattern;
                break;
            }

            if (pattern != null && this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                first = false;
            }
        }

        // Build the returnvalue
        if (ref != null && (parentPattern != null)) {
            HashMap<Ref, LinkedList<Pattern>> returnMap = new HashMap<Ref, LinkedList<Pattern>>();
            patternList = this.removePatternFromPatternList(patternList, parentPattern);
            returnMap.put(ref, patternList);
            return returnMap;
        }
        return null;
    }

    /**
     * Recursive check of a given patternlist for a ref that can be converted to a XML XSDSchema
     * complexType.
     * @param patternList       Source for the check
     * @returnRef        returns the possibly found ref
     */
    private Ref checkPatternListForRefForComplexTypeRecursive(LinkedList<Pattern> patternList) {
        Ref ref = null;
        for (Iterator<Pattern> it = patternList.iterator(); it.hasNext();) {
            Pattern pattern = it.next();
            if ((pattern instanceof Ref)) {

                // Case: Ref

                ref = (Ref) pattern;
            } else if (pattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) pattern;
                if (choice.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(choice.getPatterns());
                }
            } else if (pattern instanceof Group) {

                // Case: Group

                Group group = (Group) pattern;
                if (group.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(group.getPatterns());
                }
            } else if (pattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) pattern;
                if (interleave.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(interleave.getPatterns());
                }
            }
        }
        return ref;
    }

    /**
     * Method for removing a given pattern from a patternlist, if it is in there.
     * @param patternList       basis of the search and replace process
     * @param pattern           source for the search progress
     * @return LinkedList<Pattern>  a nes patternlist without the given pattern
     */
    private LinkedList<Pattern> removePatternFromPatternList(LinkedList<Pattern> patternList, Pattern pattern) {
        LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();
        for (Iterator<Pattern> it = patternList.iterator(); it.hasNext();) {
            Pattern checkPattern = it.next();
            if (checkPattern != pattern) {
                newPatternList.add(checkPattern);
            }
        }
        return newPatternList;
    }

    /**
     * Generate recursivly a complexType and extension/base structure for a given ref
     * @param ref       source for the conversion
     * @return SymbolTableRef<Type>     the generated type
     */
    private SymbolTableRef<Type> generateComplexTypeForRefRecursive(Ref ref) throws Exception {
        SymbolTableRef<Type> resultTypeRef = null;

        if (refToTypeMap.get(ref.getUniqueRefID()) != null) {
            resultTypeRef = refToTypeMap.get(ref.getUniqueRefID());
        } else {
            HashSet<String> refInformation = this.patternInformation.get(ref);
            HashSet<Pattern> refSet = new HashSet<Pattern>();
            refSet.add(ref);

            String namespace = null;
            if (ref.getAttributeNamespace() == null) {
                namespace = "";
            } else {
                namespace = ref.getAttributeNamespace();
            }
            // Avoid creating types with the same name
            String refTypeName = ref.getRefName();
            if (this.usedLocalTypeNames.contains(refTypeName)) {
                refTypeName = refTypeName + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(refTypeName);

            // Set a dummyComplexType complexType to the symbolTable for further recursive iterations
            ComplexType refComplexType = new ComplexType("{" + namespace + "}" + refTypeName, null, false);
            refComplexType.setDummy(true);
            refComplexType.setIsAnonymous(false);
            SymbolTableRef<Type> typeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(refComplexType.getName(), refComplexType);
            this.refToTypeMap.put(ref.getUniqueRefID(), typeRef);
            this.topLevelTypes.add(typeRef);

            // Handle mixed/text content conversion
            if (refInformation.contains("mixed") || refInformation.contains("text") || refInformation.contains("data") || refInformation.contains("value")) {
                refComplexType.setMixed(true);
            }

            // Prohibit the use of the xsi:type-attribute in the instance for choosing the elements type
            refComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
            refComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);

            // Fetch all child patterns of a given ref from the subordinate define pattern
            LinkedList<Pattern> definePatterns = new LinkedList<Pattern>();
            SymbolTableRef<Type> strRefBaseType = null;
            if (ref.getDefineList().size() == 1) {
                definePatterns = ref.getDefineList().getFirst().getPatterns();

                // Check if there is a new ref within the found child patterns
                HashMap<Ref, LinkedList<Pattern>> innerTypeMap = null;
                if (!definePatterns.isEmpty()) {
                    innerTypeMap = checkPatternListForRefForComplexType(definePatterns);
                }

                // Recurse to build a new type for the inner ref pattern
                if (innerTypeMap != null && !innerTypeMap.isEmpty() && innerTypeMap.keySet() != null) {
                    Ref innerRef = innerTypeMap.keySet().iterator().next();
                    strRefBaseType = this.generateComplexTypeForRefRecursive(innerRef);
                    definePatterns = innerTypeMap.get(innerRef);
                }
            } else {

                // There is no valid inner ref, so handle all patterns of the define correctly regarding the defined combine method
                PatternInformationCollector pic = new PatternInformationCollector(this.relaxng);
                LinkedList<Define> defineList = ref.getDefineList();
                CombineMethod combineMethod = null;

                LinkedList<Pattern> newDefinePatterns = new LinkedList<Pattern>();
                LinkedList<Pattern> resultDefinePatterns = new LinkedList<Pattern>();

                for (Iterator<Define> it = defineList.iterator(); it.hasNext();) {
                    Define define = it.next();
                    if (define.getCombineMethod() != null) {
                        combineMethod = define.getCombineMethod();
                    }
                    LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();
                    for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            defineElementPatterns.add(pattern);
                        }
                    }
                    if (defineElementPatterns.size() > 1) {

                        // Add all patterns of one define to a group (this is a sequence in XSD)
                        // for the correct handling of the combine-method of the define-elements
                        Group group = new Group();
                        for (Iterator<Pattern> it2 = defineElementPatterns.iterator(); it2.hasNext();) {
                            Pattern innerPattern = it2.next();
                            group.addPattern(innerPattern);
                        }
                        newDefinePatterns.add(group);

                        HashSet<String> dataForNewPattern = pic.getDataForPattern(group);
                        this.patternInformation.put(group, dataForNewPattern);

                    } else if (defineElementPatterns.size() == 1) {
                        newDefinePatterns.add(defineElementPatterns.getFirst());
                    }
                }

                if (combineMethod.equals(CombineMethod.choice)) {
                    Choice choice = new Choice();
                    for (Iterator<Pattern> it = newDefinePatterns.iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        choice.addPattern(pattern);
                    }
                    HashSet<String> dataForNewChoicePattern = pic.getDataForPattern(choice);
                    this.patternInformation.put(choice, dataForNewChoicePattern);
                    resultDefinePatterns.add(choice);
                } else {
                    Interleave interleave = new Interleave();
                    for (Iterator<Pattern> it = newDefinePatterns.iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        interleave.addPattern(pattern);
                    }
                    HashSet<String> dataForNewInterleavePattern = pic.getDataForPattern(interleave);
                    this.patternInformation.put(interleave, dataForNewInterleavePattern);
                    resultDefinePatterns.add(interleave);
                }
                definePatterns = resultDefinePatterns;
            }

            // Handle the attribute content conversion for the ref
            LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> refAttributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(new HashSet<Pattern>(definePatterns));

            if (strRefBaseType == null) {
                for (Iterator<AttributeParticle> it = refAttributeList.iterator(); it.hasNext();) {
                    AttributeParticle attributeParticle = it.next();
                    if (attributeParticle != null) {
                        refComplexType.addAttribute(attributeParticle);
                    }
                }
            }

            // Handle the element content conversion for the ref
            Group group = new Group();
            for (Iterator<Pattern> it = definePatterns.iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                group.addPattern(pattern);
            }

            PatternInformationCollector patternInformationCollector = new PatternInformationCollector(relaxng);
            this.patternInformation.put(group, patternInformationCollector.getDataForPattern(group));

            de.tudortmund.cs.bonxai.common.Particle refXSDParticle = null;
            de.tudortmund.cs.bonxai.common.Particle resultParticle = convertPatternToElementParticleStructure(group);
            if (resultParticle != null) {
                if (resultParticle instanceof de.tudortmund.cs.bonxai.xsd.Element || resultParticle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) {
                    SequencePattern sequencePattern = new SequencePattern();
                    sequencePattern.addParticle(resultParticle);
                    if (!sequencePattern.getParticles().isEmpty()) {
                        refXSDParticle = sequencePattern;
                    }
                } else {
                    refXSDParticle = resultParticle;
                }
            }

            // Adding the element particles to the content of the refComplexType
            if (strRefBaseType == null) {
                if (refXSDParticle != null) {
                    ComplexContentType complexContentType = new ComplexContentType(refXSDParticle, refComplexType.getMixed());
                    de.tudortmund.cs.bonxai.common.Particle myParticle = null;
                    if (refXSDParticle instanceof CountingPattern) {
                        myParticle = ((CountingPattern) refXSDParticle).getParticles().getFirst();
                    } else {
                        myParticle = refXSDParticle;
                    }
                    if (!(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupRef)) {
                        SequencePattern newSequencePattern = new SequencePattern();
                        newSequencePattern.addParticle(refXSDParticle);
                        complexContentType.setParticle(newSequencePattern);
                    } else {
                        complexContentType.setParticle(refXSDParticle);
                    }
                    refComplexType.setContent(complexContentType);
                }
            } else {
                // Extend ComplexType with attributes
                strRefBaseType.getReference().setIsAnonymous(false);
                strRefBaseType.getReference().setDummy(false);
                ComplexContentExtension complexContentExtension = new ComplexContentExtension(strRefBaseType);

                for (Iterator<AttributeParticle> it = refAttributeList.iterator(); it.hasNext();) {
                    AttributeParticle attributeParticle = it.next();
                    if (attributeParticle != null) {
                        complexContentExtension.addAttribute(attributeParticle);
                    }
                }

                ComplexContentType complexContentType = new ComplexContentType(refXSDParticle, complexContentExtension, refComplexType.getMixed());
                de.tudortmund.cs.bonxai.common.Particle myParticle = null;

                if (refXSDParticle instanceof CountingPattern) {
                    myParticle = ((CountingPattern) refXSDParticle).getParticles().getFirst();
                } else {
                    myParticle = refXSDParticle;
                }
                if (refXSDParticle != null && !(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupRef)) {
                    SequencePattern newSequencePattern = new SequencePattern();
                    newSequencePattern.addParticle(myParticle);
                    complexContentType.setParticle(newSequencePattern);
                } else {
                    complexContentType.setParticle(myParticle);
                }
                refComplexType.setContent(complexContentType);
            }

            // Save generated ref type to global map!
            resultTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(refComplexType.getName(), refComplexType);
            refToTypeMap.put(ref.getUniqueRefID(), resultTypeRef);
            this.topLevelTypes.add(resultTypeRef);
            resultTypeRef.getReference().setIsAnonymous(false);
            resultTypeRef.getReference().setDummy(false);
            this.xmlSchema.addType(resultTypeRef);
        }
        return resultTypeRef;
    }

    /**
     * Check if it is useful to build a XML XSDSchema group from a given Define.
     * This allows only groups with more than one element content pattern.
     *
     * @param definePatterns    source for the check
     * @return boolean      returns true, if there is more than one element content pattern in the given patternlist
     */
    private boolean isDefinePatternListUsefulAsXSDGroup(LinkedList<Pattern> definePatterns) {
        boolean returnValue = true;
        if (definePatterns != null) {
            if (definePatterns.size() == 1) {

                if (((definePatterns.getFirst() instanceof OneOrMore) || (definePatterns.getFirst() instanceof ZeroOrMore) || (definePatterns.getFirst() instanceof Optional) || (definePatterns.getFirst() instanceof Ref) || (definePatterns.getFirst() instanceof Element))) {
                    returnValue = false;
                }
                if (definePatterns.getFirst() instanceof Group) {
                    Group group = (Group) definePatterns.getFirst();
                    int countElementPattern = 0;
                    for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        if (pattern instanceof Element || (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element"))) {
                            countElementPattern++;
                        }
                    }
                    if (countElementPattern < 2) {
                        returnValue = false;
                    }
                }
                if (definePatterns.getFirst() instanceof Choice) {
                    Choice choice = (Choice) definePatterns.getFirst();
                    int countElementPattern = 0;
                    for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        if (pattern instanceof Element || (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element"))) {
                            countElementPattern++;
                        }
                    }
                    if (countElementPattern < 2) {
                        returnValue = false;
                    }
                }
                if (definePatterns.getFirst() instanceof Interleave) {
                    Interleave interleave = (Interleave) definePatterns.getFirst();
                    int countElementPattern = 0;
                    for (Iterator<Pattern> it = interleave.getPatterns().iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        if (pattern instanceof Element || (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element"))) {
                            countElementPattern++;
                        }
                    }
                    if (countElementPattern < 2) {
                        returnValue = false;
                    }
                }
            }
        } else {
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Getter for the set of found recursive elements within the RELAX NG schema.
     *
     * This set contains ALL recursive elements, even defined in nested grammars.
     * This is possible, because elements with the same name defined in different
     * grammars are different java objects and can both be fetched and put into
     * this set.
     *
     * @return HashSet<Element> - set of all recursivly defined elements under grammars
     */
    public HashSet<Element> getRecursiveElements() {
        return recursiveElements;
    }

    /**
     * Check if there is a type with the given name in the topLevelType Sets.
     * @param typeName - name for check
     * @return boolean - returns false, if a type with the name is already in the set
     */
    private boolean checkTopLevelTypes(String typeName) {
        boolean resultboolean = true;

        for (Iterator<SymbolTableRef<Type>> it = topLevelTypes.iterator(); it.hasNext();) {
            SymbolTableRef<Type> strType = it.next();
            if (strType.getReference().getName().equals(typeName)) {
                return false;
            }
        }
        return resultboolean;
    }
}
