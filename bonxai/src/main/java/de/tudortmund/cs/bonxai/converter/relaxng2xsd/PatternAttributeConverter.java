package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.MultipleAnyAttributeException;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.OnlyFullQualifiedNamesAllowedException;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.ParticleIsNullException;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.TypeIsNullException;
import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.AttributeGroupRef;
import de.tudortmund.cs.bonxai.xsd.AttributeParticle;
import de.tudortmund.cs.bonxai.xsd.AttributeRef;
import de.tudortmund.cs.bonxai.xsd.AttributeUse;
import de.tudortmund.cs.bonxai.xsd.ImportedSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentUnion;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class PatternConverter
 * @author Lars Schmidt
 */
public class PatternAttributeConverter extends ConverterBase {

    /**
     * Global set of already used toplevel attributeGroup names
     */
    protected HashSet<String> usedTopLevelAttributeGroupNames = new HashSet<String>();
    /**
     * Global set of already used toplevel attribute names
     */
    HashSet<String> usedTopLevelAttributeNames = new HashSet<String>();
    /**
     * Local variable for the PatternSimpleTypeConverter used within this PatternAttributeConverter
     */
    PatternSimpleTypeConverter patternSimpleTypeConverter;
    private HashMap<String, SymbolTableRef<AttributeGroup>> refToAttributeGroupMap;
    private HashMap<String, Integer> currentNameCountMap = new HashMap<String, Integer>();
    private HashSet<String> usedLocalAttributeGroupNames = new HashSet<String>();

    /**
     * Constructor of class PatternAttributeConverter with PatternSimpleTypeConverter given
     * @param xmlSchema                     target XML XSDSchema
     * @param relaxng                       source RELAX NG XSDSchema
     * @param patternInformation            global map of pattern information
     * @param usedLocalNames                global set of already used local names
     * @param usedLocalTypeNames            global set of already used local type names
     * @param patternSimpleTypeConverter    PatternSimpleTypeConverter
     */
    public PatternAttributeConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<Pattern, HashSet<String>> patternInformation, HashSet<String> usedLocalNames, HashSet<String> usedLocalTypeNames, PatternSimpleTypeConverter patternSimpleTypeConverter) {
        super(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
        this.patternSimpleTypeConverter = patternSimpleTypeConverter;
        this.refToAttributeGroupMap = new HashMap<String, SymbolTableRef<AttributeGroup>>();
    }

    /**
     * Constructor of class PatternAttributeConverter
     * @param xmlSchema                     target XML XSDSchema
     * @param relaxng                       source RELAX NG XSDSchema
     * @param patternInformation            global map of pattern information
     * @param usedLocalNames                global set of already used local names
     * @param usedLocalTypeNames            global set of already used local type names
     */
    public PatternAttributeConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<Pattern, HashSet<String>> patternInformation, HashSet<String> usedLocalNames, HashSet<String> usedLocalTypeNames) {
        super(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
        this.patternSimpleTypeConverter = new PatternSimpleTypeConverter(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
        this.refToAttributeGroupMap = new HashMap<String, SymbolTableRef<AttributeGroup>>();
    }

    /**
     * Method for the conversion of a given pattern list to the corresponding XML XSDSchema attributeParticle list
     * @param patternsContainingAttribute                                       basis for the conversion
     * @return LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>        result of the conversion
     * @throws Exception
     */
    public LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> convertPatternListToAttributeParticleList(HashSet<Pattern> patternsContainingAttribute) throws Exception {
        // handle attribute content conversion
        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> resultAttributeList = new LinkedList<AttributeParticle>();
        this.currentNameCountMap = new HashMap<String, Integer>();
        for (Iterator<Pattern> it = patternsContainingAttribute.iterator(); it.hasNext();) {
            Pattern currentAttributeContentPattern = it.next();
            this.mergeAttributeLists(resultAttributeList, this.convertPatternToAttributeParticleList(currentAttributeContentPattern));
        }
        resultAttributeList = cleanUpAttributeList(resultAttributeList);
        resultAttributeList = attributeGroupPostProcessing(resultAttributeList);
        return resultAttributeList;
    }

    /**
     * Method for generating a type for a given attribute
     * @param attribute                 basis for the conversion
     * @param localTypeName             local name for the resulting type
     * @return de.tudortmund.cs.bonxai.xsd.Type     type, result of the type creation
     * @throws TypeIsNullException
     * @throws Exception
     */
    private de.tudortmund.cs.bonxai.xsd.Type generateTypeForAttribute(Attribute attribute, String localTypeName) throws TypeIsNullException, Exception {
        // Handle SimpleType/String-Content: value, data, param, ...
        SimpleType simpleType = null;
        if (attribute.getPattern() != null) {
            simpleType = this.patternSimpleTypeConverter.generateSimpleTypeForPattern(attribute.getPattern(), new LinkedList<Pattern>());
        }
        return simpleType;
    }

    /**
     * Method for converting a given RELAX NG pattern into a XML XSDSchema attributeParticle (list in case of multiple names)
     * @param relaxNGPattern        source of the conversion
     * @return LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>        result of the conversion, a list of XML XSDSchema attribute particles
     * @throws Exception
     */
    private LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> convertPatternToAttributeParticleList(Pattern relaxNGPattern) throws Exception {

        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> resultAttributeList = new LinkedList<AttributeParticle>();

        if (relaxNGPattern != null) {
            if (this.patternInformation.get(relaxNGPattern) != null && this.patternInformation.get(relaxNGPattern).contains("attribute") || relaxNGPattern instanceof Attribute) {
                if (relaxNGPattern instanceof ZeroOrMore) {

                    // Case: ZeroOrMore

                    ZeroOrMore zeroOrMore = (ZeroOrMore) relaxNGPattern;

                    for (Iterator<Pattern> it1 = zeroOrMore.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof Element) {
                    // Case: Element
//                    Element innerElement = (Element) relaxNGPattern;
                } else if (relaxNGPattern instanceof Ref) {

                    // Case: Ref

                    Ref ref = (Ref) relaxNGPattern;

                    if (RelaxNG2XSDConverter.REF_TO_ATTRIBUTEGROUP_CONVERSION && this.refToAttributeGroupMap.get(ref.getUniqueRefID()) != null) {
                        AttributeGroupRef attributeGroupRef = new AttributeGroupRef(this.refToAttributeGroupMap.get(ref.getUniqueRefID()));
                        resultAttributeList.add(attributeGroupRef);
                    } else {

                        LinkedList<AttributeParticle> generatedAttributeParticles = new LinkedList<AttributeParticle>();
                        for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                            Define define = it.next();
                            for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                                Pattern pattern = it1.next();
                                if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                                    this.mergeAttributeLists(generatedAttributeParticles, convertPatternToAttributeParticleList(pattern));
                                }
                            }
                        }

                        String namespace = ref.getAttributeNamespace();
                        if (namespace == null) {
                            namespace = "";
                        }

                        if (RelaxNG2XSDConverter.REF_TO_ATTRIBUTEGROUP_CONVERSION && generatedAttributeParticles.size() > 1 && this.checkForAttributeRefOrAnyAttribute(generatedAttributeParticles)) {

                            SymbolTableRef<AttributeGroup> strAgr = putAttributeParticleIntoAttributeGroup(ref.getRefName(), namespace, generatedAttributeParticles);

                            this.refToAttributeGroupMap.put(ref.getUniqueRefID(), strAgr);

                            AttributeGroupRef attributeGroupRef = new AttributeGroupRef(strAgr);

                            resultAttributeList.add(attributeGroupRef);
                        } else {
                            this.mergeAttributeLists(resultAttributeList, generatedAttributeParticles);
                        }
                    }
                } else if (relaxNGPattern instanceof Group) {

                    // Case: Group

                    Group group = (Group) relaxNGPattern;

                    for (Iterator<Pattern> it1 = group.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof OneOrMore) {

                    // Case: OneOrMore

                    OneOrMore oneOrMore = (OneOrMore) relaxNGPattern;

                    for (Iterator<Pattern> it1 = oneOrMore.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof Optional) {

                    // Case: Optional

                    Optional optional = (Optional) relaxNGPattern;

                    for (Iterator<Pattern> it1 = optional.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof Choice) {

                    // Case: Choice

                    Choice choice = (Choice) relaxNGPattern;

                    for (Iterator<Pattern> it1 = choice.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof Interleave) {

                    // Case: Interleave

                    Interleave interleave = (Interleave) relaxNGPattern;

                    for (Iterator<Pattern> it1 = interleave.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof ParentRef) {

                    // Case: ParentRef

                    ParentRef parentRef = (ParentRef) relaxNGPattern;

                    if (this.refToAttributeGroupMap.get(parentRef.getUniqueRefID()) != null) {
                        AttributeGroupRef attributeGroupRef = new AttributeGroupRef(this.refToAttributeGroupMap.get(parentRef.getUniqueRefID()));
                        resultAttributeList.add(attributeGroupRef);
                    } else {

                        LinkedList<AttributeParticle> generatedAttributeParticles = new LinkedList<AttributeParticle>();
                        for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                            Define define = it.next();
                            for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                                Pattern pattern = it1.next();
                                if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                                    this.mergeAttributeLists(generatedAttributeParticles, convertPatternToAttributeParticleList(pattern));
                                }
                            }
                        }

                        String namespace = parentRef.getAttributeNamespace();
                        if (namespace == null) {
                            namespace = "";
                        }

                        if (generatedAttributeParticles.size() > 1 && this.checkForAttributeRefOrAnyAttribute(generatedAttributeParticles)) {

                            SymbolTableRef<AttributeGroup> strAgr = putAttributeParticleIntoAttributeGroup(parentRef.getRefName(), namespace, generatedAttributeParticles);

                            this.refToAttributeGroupMap.put(parentRef.getUniqueRefID(), strAgr);

                            AttributeGroupRef attributeGroupRef = new AttributeGroupRef(strAgr);

                            resultAttributeList.add(attributeGroupRef);
                        } else {
                            this.mergeAttributeLists(resultAttributeList, generatedAttributeParticles);
                        }
                    }

                } else if (relaxNGPattern instanceof Mixed) {

                    // Case: Mixed

                    Mixed mixed = (Mixed) relaxNGPattern;

                    for (Iterator<Pattern> it1 = mixed.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof Grammar) {

                    // Case: Grammar

                    Grammar grammar = (Grammar) relaxNGPattern;

                    for (Iterator<Pattern> it1 = grammar.getStartPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute") || pattern instanceof Attribute) {
                            this.mergeAttributeLists(resultAttributeList, convertPatternToAttributeParticleList(pattern));
                        }
                    }
                } else if (relaxNGPattern instanceof Attribute) {

                    // Case: Attribute

                    Attribute attribute = (Attribute) relaxNGPattern;
                    this.mergeAttributeLists(resultAttributeList, this.convertAttribute(attribute));
                }
            }
        }
        return resultAttributeList;
    }

    /**
     * Method for merging two given attributeLists into one resulting list
     * @param attributeList1        list 1
     * @param attributeList2        list 2
     * @return LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>
     */
    private LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> mergeAttributeLists(LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeList1, LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeList2) throws MultipleAnyAttributeException, OnlyFullQualifiedNamesAllowedException {
        for (Iterator<AttributeParticle> it = attributeList2.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();
            this.addAttributeParticleToAttributeList(attributeList1, attributeParticle);
        }
        return attributeList1;
    }

    /**
     * Method for adding a given AttributeParticle to a attributeParticleList
     *
     * This method handles the merging of anyAttributes, because there is only one allowed in a context in XML XSDSchema
     *
     * @param attributeList
     * @param attributeParticle
     */
    private void addAttributeParticleToAttributeList(LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeList, de.tudortmund.cs.bonxai.xsd.AttributeParticle attributeParticle) throws MultipleAnyAttributeException, OnlyFullQualifiedNamesAllowedException {
        AnyAttribute anyAttributeAlreadyInList = null;

        for (Iterator<de.tudortmund.cs.bonxai.xsd.AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
            de.tudortmund.cs.bonxai.xsd.AttributeParticle innerAttributeParticle = it.next();
            if (innerAttributeParticle instanceof AnyAttribute) {
                anyAttributeAlreadyInList = (AnyAttribute) innerAttributeParticle;
                break;
            }
        }

        if (anyAttributeAlreadyInList != null && attributeParticle instanceof AnyAttribute) {
            attributeList.removeLast();
            attributeList.add(this.mergeAnyAttributes(anyAttributeAlreadyInList, (AnyAttribute) attributeParticle));
        } else if (anyAttributeAlreadyInList != null) {
            // Exception: There is already an AnyAttribute in the current resultAttributeList!
//            throw new MultipleAnyAttributeException();
        } else {
            attributeList.add(attributeParticle);
        }
    }

    /**
     * Method for cleaning up attributeLists.
     * Attribute with the same name are merged into one.
     * @param attributeList                             source for the cleaning progress
     * @return LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>    cleaned attributeList as result
     */
    private LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> cleanUpAttributeList(LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeList) {
        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> newAttributeList = new LinkedList<AttributeParticle>();
        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> otherAttributeParticles = new LinkedList<AttributeParticle>();
        HashMap<String, LinkedList<de.tudortmund.cs.bonxai.xsd.Attribute>> attributeNameInfo = new HashMap<String, LinkedList<de.tudortmund.cs.bonxai.xsd.Attribute>>();

        for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            if (attributeParticle instanceof de.tudortmund.cs.bonxai.xsd.Attribute) {
                de.tudortmund.cs.bonxai.xsd.Attribute currentAttribute = (de.tudortmund.cs.bonxai.xsd.Attribute) attributeParticle;
                if (attributeNameInfo.get(currentAttribute.getName()) != null) {
                    attributeNameInfo.get(currentAttribute.getName()).add(currentAttribute);
                } else {
                    LinkedList<de.tudortmund.cs.bonxai.xsd.Attribute> newCurrentAttributeList = new LinkedList<de.tudortmund.cs.bonxai.xsd.Attribute>();
                    newCurrentAttributeList.add(currentAttribute);
                    attributeNameInfo.put(currentAttribute.getName(), newCurrentAttributeList);
                }
            } else {
                otherAttributeParticles.add(attributeParticle);
            }
        }
        for (Iterator<String> it = attributeNameInfo.keySet().iterator(); it.hasNext();) {
            String currentAttributeName = it.next();


            String namespace = null;
            if (attributeNameInfo.get(currentAttributeName) != null && attributeNameInfo.get(currentAttributeName).size() > 1) {
                namespace = attributeNameInfo.get(currentAttributeName).getFirst().getNamespace();
                de.tudortmund.cs.bonxai.xsd.Attribute newAttribute = new de.tudortmund.cs.bonxai.xsd.Attribute(currentAttributeName);
                newAttribute.setForm(null);

                AttributeUse attributeUse = AttributeUse.Required;

                LinkedList<SymbolTableRef<Type>> typeList = new LinkedList<SymbolTableRef<Type>>();
                for (Iterator<de.tudortmund.cs.bonxai.xsd.Attribute> it1 = attributeNameInfo.get(currentAttributeName).iterator(); it1.hasNext();) {
                    de.tudortmund.cs.bonxai.xsd.Attribute currentAttribute = it1.next();
                    if (currentAttribute.getUse().equals(AttributeUse.Optional) && !attributeUse.equals(AttributeUse.Prohibited)) {
                        attributeUse = AttributeUse.Optional;
                    }

                    if (currentAttribute.getUse().equals(AttributeUse.Prohibited)) {
                        attributeUse = AttributeUse.Prohibited;
                    }

                    if (currentAttribute.getSimpleType() != null) {
                        SymbolTableRef<Type> strType = this.xmlSchema.getTypeSymbolTable().getReference(currentAttribute.getSimpleType().getName());
                        if (!typeList.contains(strType)) {
                            typeList.add(strType);
                        }
                    }
                }

                newAttribute.setUse(attributeUse);

                if (!typeList.isEmpty()) {
                    if (typeList.size() > 1) {

                        typeList = this.patternSimpleTypeConverter.optimizeSimpleTypeUnion(typeList);

                        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(typeList);

                        String localTypeName = "simpleType";
                        if (this.usedLocalTypeNames.contains(localTypeName)) {
                            localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
                        }
                        this.usedLocalTypeNames.add(localTypeName);

                        SimpleType newSimpleType = new SimpleType("{" + namespace + "}" + localTypeName, simpleContentUnion, true);
                        newSimpleType.setDummy(false);
                        newAttribute.setSimpleType(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(localTypeName, newSimpleType));
                        newAttribute.setTypeAttr(false);
                    } else {
                        newAttribute.setSimpleType(typeList.getFirst());
                        if (typeList.getFirst().getReference().isAnonymous()) {
                            newAttribute.setTypeAttr(false);
                        } else {
                            newAttribute.setTypeAttr(true);
                        }
                    }
                }
                newAttributeList.add(newAttribute);

            } else if (attributeNameInfo.get(currentAttributeName) != null) {
                newAttributeList.add(attributeNameInfo.get(currentAttributeName).getFirst());
            }
        }
        newAttributeList.addAll(otherAttributeParticles);
        return newAttributeList;
    }

    /**
     * Merging of two AnyAttributes
     * @param anyAttributeAlreadyInList     first anyAttribute
     * @param newAnyAttribute               second anyAttribute
     * @return AnyAttribute     - resulting anyAttribute
     */
    private AnyAttribute mergeAnyAttributes(AnyAttribute anyAttributeAlreadyInList, AnyAttribute newAnyAttribute) throws OnlyFullQualifiedNamesAllowedException {
        ProcessContentsInstruction pci = ProcessContentsInstruction.Skip;
        if (anyAttributeAlreadyInList.getProcessContentsInstruction().equals(newAnyAttribute.getProcessContentsInstruction()) && anyAttributeAlreadyInList.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict)) {
            pci = ProcessContentsInstruction.Strict;
        }

        String resultingNamespaceList = anyAttributeAlreadyInList.getNamespace();

        if ((resultingNamespaceList.contains("{##other}") && !newAnyAttribute.getNamespace().contains("{##other}")) || (!resultingNamespaceList.contains("{##other}") && newAnyAttribute.getNamespace().contains("{##other}"))) {
            resultingNamespaceList = "{##any}";
        }

        if (!resultingNamespaceList.contains("{##any}") && !newAnyAttribute.getNamespace().contains("{##any}")) {

            String[] namespacesSecondAttribute = newAnyAttribute.getNamespace().split(" ");

            for (int i = 0; i < namespacesSecondAttribute.length; i++) {
                String string = namespacesSecondAttribute[i];
                if (!anyAttributeAlreadyInList.getNamespace().contains(string)) {

                    if (!string.equals("{##other}")) {
                        resultingNamespaceList = resultingNamespaceList + " " + string;
                    }
                }
            }
        }
        resultingNamespaceList = resultingNamespaceList.trim();

//        if (!resultingNamespaceList.contains("{")) {
//            // Exception: There is an unqualified string
//            throw new OnlyFullQualifiedNamesAllowedException("anyAttribute namespaceList");
//        }

        AnyAttribute resultAnyAttribute = new AnyAttribute(pci, resultingNamespaceList);

        return resultAnyAttribute;
    }

    /**
     * Convert a giveen RELAX NG attribute pattern into a list of XML XSDSchema attributeParticles
     * @param attribute         basis of the converison
     * @return LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle>        result of the conversion
     * @throws Exception
     */
    private LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> convertAttribute(Attribute attribute) throws Exception {

        LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> resultAttributeList = new LinkedList<AttributeParticle>();

        // Generate all possible names for the resulting structure.
        // For each name there has to be generated a new attribute.

        NameClassAnalyzer nameClassAnalyzer = new NameClassAnalyzer(attribute);
        HashMap<String, Object> attributeNameInfos = nameClassAnalyzer.analyze();

        String localTypeName = "type";
        int countRealAttributeNames = 0;
        for (Iterator<String> it = attributeNameInfos.keySet().iterator(); it.hasNext();) {
            String string = it.next();
            string = string.substring(string.lastIndexOf("}") + 1, string.length());
            if (attributeNameInfos.get(string) == null) {
                localTypeName = localTypeName + "_" + string;
                countRealAttributeNames++;
            }
        }

        localTypeName = localTypeName.substring(0, localTypeName.length());

        // Generate a XML XSDSchema type depending on the content patterns of the
        // current RELAX NG attribute

        if (this.usedLocalNames.contains(localTypeName)) {
            localTypeName = localTypeName + "_" + this.usedLocalNames.size();
        }
        this.usedLocalNames.add(localTypeName);

        Type type = this.generateTypeForAttribute(attribute, localTypeName);


        SymbolTableRef<Type> typeRef = null;
        String fullQualifiedTypeName = null;
        if (type != null) {
            SimpleType simpleType = (SimpleType) type;
            fullQualifiedTypeName = simpleType.getName();
            typeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(fullQualifiedTypeName, simpleType);

            if (typeRef != null && countRealAttributeNames > 1) {
                type.setIsAnonymous(false);
                this.xmlSchema.addType(typeRef);
            }
        }

        // If there is an anyAttribute as result of the nameGeneration. This pattern
        // can be used as result attributeParticle

        if (!attributeNameInfos.isEmpty()) {

            for (Iterator<String> it = attributeNameInfos.keySet().iterator(); it.hasNext();) {
                String currentNameKey = it.next();

                if (attributeNameInfos.get(currentNameKey) != null) {
                    Object currentAnyObject = attributeNameInfos.get(currentNameKey);
                    if (currentAnyObject instanceof AnyAttribute) {
                        AnyAttribute anyAttribute = (AnyAttribute) currentAnyObject;
                        this.registerNameInMap("{*}");
                        if (currentNameKey.startsWith("-")) {
                            // Generate Imported XSDSchema and attach it to the basis schema
                            // Put the anyAttribute to the correct XSDSchema
                            ImportedSchema importedSchema = updateOrCreateImportedSchema(anyAttribute.getNamespace());

                            AnyAttribute newAnyAttribute = new AnyAttribute(ProcessContentsInstruction.Strict, "##other");

                            String attributeGroupName = "externalAnyAttribute";

                            if (this.usedLocalAttributeGroupNames.contains(attributeGroupName)) {
                                attributeGroupName = attributeGroupName + "_" + this.usedLocalAttributeGroupNames.size();
                            }
                            this.usedLocalAttributeGroupNames.add(attributeGroupName);

                            attributeGroupName = "{" + anyAttribute.getNamespace() + "}" + attributeGroupName;

                            de.tudortmund.cs.bonxai.xsd.AttributeGroup attributeGroup = new de.tudortmund.cs.bonxai.xsd.AttributeGroup(attributeGroupName);
                            attributeGroup.addAttributeParticle(newAnyAttribute);

                            SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup> strAttributeGroup = this.xmlSchema.getAttributeGroupSymbolTable().updateOrCreateReference(attributeGroupName, attributeGroup);

                            if (!usedTopLevelAttributeGroupNames.contains(attributeGroupName)) {
                                importedSchema.getSchema().addAttributeGroup(strAttributeGroup);
                                usedTopLevelAttributeGroupNames.add(attributeGroupName);
                            }

                            importedSchema.getSchema().getAttributeGroupSymbolTable().setReference(attributeGroup.getName(), strAttributeGroup);
                            de.tudortmund.cs.bonxai.xsd.AttributeGroupRef attributeGroupRef = new de.tudortmund.cs.bonxai.xsd.AttributeGroupRef(strAttributeGroup);
                            this.addAttributeParticleToAttributeList(resultAttributeList, attributeGroupRef);

                        } else {
                            this.addAttributeParticleToAttributeList(resultAttributeList, anyAttribute);
                        }
                    }
                } else {
                    de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = null;
                    if (typeRef != null) {
                        xsdAttribute = new de.tudortmund.cs.bonxai.xsd.Attribute(currentNameKey, typeRef);
                        if (typeRef.getReference().isAnonymous()) {
                            xsdAttribute.setTypeAttr(false);
                        } else {
                            xsdAttribute.setTypeAttr(true);
                        }
                    } else {
                        xsdAttribute = new de.tudortmund.cs.bonxai.xsd.Attribute(currentNameKey);
                        xsdAttribute.setTypeAttr(false);
                    }
                    this.registerNameInMap(xsdAttribute.getName());
                    SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Attribute> strAttribute = this.xmlSchema.getAttributeSymbolTable().updateOrCreateReference(xsdAttribute.getName(), xsdAttribute);

                    // There must not be a default value for the form of an attribute.
                    xsdAttribute.setForm(null);

                    /**
                     * Set XML XSDSchema attribute "use" property:
                     **/
                    if (this.patternInformation.get(attribute) != null && this.patternInformation.get(attribute).contains("optional")) {
                        xsdAttribute.setUse(AttributeUse.Optional);
                    } else {
                        xsdAttribute.setUse(AttributeUse.Required);
                    }

                    if (this.patternInformation.get(attribute) != null && this.patternInformation.get(attribute).contains("notAllowed")) {
                        xsdAttribute.setUse(AttributeUse.Prohibited);
                    }

                    if (xsdAttribute.getNamespace().equals(this.xmlSchema.getTargetNamespace())) {
                        this.addAttributeParticleToAttributeList(resultAttributeList, xsdAttribute);
                    } else {
                        // Generate Imported XSDSchema and attach it to the basis schema
                        // Put the xsdAttribute to the correct XSDSchema

                        ImportedSchema importedSchema = updateOrCreateImportedSchema(xsdAttribute.getNamespace());
                        if (!usedTopLevelAttributeNames.contains(xsdAttribute.getName())) {
                            importedSchema.getSchema().addAttribute(strAttribute);
                            usedTopLevelAttributeNames.add(xsdAttribute.getName());
                            importedSchema.getSchema().getAttributeSymbolTable().setReference(xsdAttribute.getName(), strAttribute);
                            if (type != null) {
                                importedSchema.getSchema().getTypeSymbolTable().setReference(fullQualifiedTypeName, typeRef);
                                if (!type.isAnonymous() && !type.isDummy()) {
                                    importedSchema.getSchema().addType(typeRef);
                                }
                            }
                            AttributeRef attributeRef = new AttributeRef(strAttribute);

                            if (strAttribute.getReference().getUse() != null) {
                                attributeRef.setUse(strAttribute.getReference().getUse());
                                strAttribute.getReference().setUse(null);
                            }

                            this.addAttributeParticleToAttributeList(resultAttributeList, attributeRef);
                        } else {
                            SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Attribute> strAttributeTwo = importedSchema.getSchema().getAttributeSymbolTable().getReference(xsdAttribute.getName());
                            AttributeRef attributeRef = new AttributeRef(strAttributeTwo);
                            if (strAttributeTwo.getReference().getUse() != null) {
                                attributeRef.setUse(strAttributeTwo.getReference().getUse());
                                strAttributeTwo.getReference().setUse(null);
                            }
                            this.addAttributeParticleToAttributeList(resultAttributeList, attributeRef);
                        }
                    }
                }
            }
        }

        if (!resultAttributeList.isEmpty()) {
            return resultAttributeList;
        } else {
            // Exception on not initialized resultParticle
            throw new ParticleIsNullException(attribute.getClass().getName(), "{" + attribute.getAttributeNamespace() + "}" + attribute.getNameAttribute());
        }
    }

    /**
     * Method for putting a given attributeParticle into a new attributeGroup
     * @param localAttributeGroupName       String - favored attributeGroup name, this will be used, if it is not already used.
     * @param namespace                     String - namespace of the schema
     * @param attributeParticles            List of attributeParticles which will be set into the attributeGroup
     * @return SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup>       resulting SymbolTableReference to the generated attributeGroup
     * @throws Exception
     */
    private SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup> putAttributeParticleIntoAttributeGroup(String localAttributeGroupName, String namespace, LinkedList<de.tudortmund.cs.bonxai.xsd.AttributeParticle> attributeParticles) throws ParticleIsNullException, Exception {

        if (this.usedLocalAttributeGroupNames.contains(localAttributeGroupName)) {
            localAttributeGroupName = localAttributeGroupName + "_" + this.usedLocalAttributeGroupNames.size();
        }
        this.usedLocalAttributeGroupNames.add(localAttributeGroupName);
        String fqAttributeGroupName = "{" + namespace + "}" + localAttributeGroupName;


        String targetNamespace = this.xmlSchema.getTargetNamespace();
        if (targetNamespace == null) {
            targetNamespace = "";
        }

        de.tudortmund.cs.bonxai.xsd.AttributeGroup attributeGroup = new de.tudortmund.cs.bonxai.xsd.AttributeGroup(fqAttributeGroupName);

        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();
            attributeGroup.addAttributeParticle(attributeParticle);
        }

        SymbolTableRef<de.tudortmund.cs.bonxai.xsd.AttributeGroup> strAttributeGroup = this.xmlSchema.getAttributeGroupSymbolTable().updateOrCreateReference(attributeGroup.getName(), attributeGroup);

        return strAttributeGroup;
    }

    /**
     * Setter for the RELAX NG ref to XML XSDSchema attribute group map
     * @param refToAttributeGroupMap
     */
    public void setRefToAttributeGroupMap(HashMap<String, SymbolTableRef<AttributeGroup>> refToAttributeGroupMap) {
        this.refToAttributeGroupMap = refToAttributeGroupMap;
    }

    /**
     * Register all used attributeGroups in the global list of attributeGroups of the XML XSDSchema to write them out with the XSD Writer.
     * @param attributeParticleList      source list
     */
    private void registerUnregisteredAttributeGroupsInSchema(LinkedList<AttributeParticle> attributeParticleList) {
        for (Iterator<AttributeParticle> it = attributeParticleList.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            if (attributeParticle instanceof AttributeGroup) {
                AttributeGroup attributeGroup = (AttributeGroup) attributeParticle;

                if (!this.usedTopLevelAttributeGroupNames.contains(attributeGroup.getName())) {
                    this.usedTopLevelAttributeGroupNames.add(attributeGroup.getName());
                    this.xmlSchema.addAttributeGroup(this.xmlSchema.getAttributeGroupSymbolTable().getReference(attributeGroup.getName()));
                }
                LinkedList<AttributeParticle> tempList = new LinkedList<AttributeParticle>();
                for (Iterator<AttributeParticle> it1 = attributeGroup.getAttributeParticles().iterator(); it1.hasNext();) {
                    AttributeParticle attributeParticle1 = it1.next();
                    tempList.add(attributeParticle1);
                }
                if (!tempList.isEmpty()) {
                    registerUnregisteredAttributeGroupsInSchema(tempList);
                }

            } else if (attributeParticle instanceof AttributeGroupRef) {
                AttributeGroupRef attributeGroupRef = (AttributeGroupRef) attributeParticle;
                AttributeGroup attributeGroup = attributeGroupRef.getAttributeGroup();

                if (this.usedTopLevelAttributeGroupNames != null && !this.usedTopLevelAttributeGroupNames.contains(attributeGroup.getName())) {
                    this.usedTopLevelAttributeGroupNames.add(attributeGroup.getName());
                    this.xmlSchema.addAttributeGroup(this.xmlSchema.getAttributeGroupSymbolTable().getReference(attributeGroup.getName()));
                }
                LinkedList<AttributeParticle> tempList = new LinkedList<AttributeParticle>();
                for (Iterator<AttributeParticle> it1 = attributeGroup.getAttributeParticles().iterator(); it1.hasNext();) {
                    AttributeParticle attributeParticle1 = it1.next();
                    tempList.add(attributeParticle1);
                }
                if (!tempList.isEmpty()) {
                    registerUnregisteredAttributeGroupsInSchema(tempList);
                }
            }
        }
    }

    /**
     * Postprocessing for the generation of attributeGroups
     * Some generated groups are not allowed because of intersecting attribute names. These are resolved and cleaned up.
     * @param attributeParticleList     source for the postprocessing
     * @return LinkedList<AttributeParticle>    resulting attributeParticleList - cleaned
     */
    private LinkedList<AttributeParticle> attributeGroupPostProcessing(LinkedList<AttributeParticle> attributeParticleList) {

        LinkedList<AttributeParticle> newResultAttributeParticleList = new LinkedList<AttributeParticle>();
        for (Iterator<AttributeParticle> it = attributeParticleList.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();
            newResultAttributeParticleList.addAll(replaceAttributeGroupRefsInAttributeParticle(attributeParticle));
        }

        newResultAttributeParticleList = cleanUpAttributeList(newResultAttributeParticleList);
        registerUnregisteredAttributeGroupsInSchema(newResultAttributeParticleList);

        return newResultAttributeParticleList;
    }

    /**
     * Register the current full qualified attribute name in the global map
     * This is for counting occurrences of an attribute name.
     * @param name      count this name
     */
    private void registerNameInMap(String name) {
        if (this.currentNameCountMap.get(name) != null) {
            Integer integer = this.currentNameCountMap.get(name);
            integer++;
            this.currentNameCountMap.put(name, integer);
        } else {
            this.currentNameCountMap.put(name, new Integer(1));
        }
    }

    /**
     * Get a set of full qualified attribute names, that are used more than once in the current context
     * @return
     */
    private HashSet<String> getMultipleCountNames() {
        HashSet<String> resultSet = new HashSet<String>();
        for (Iterator<String> it = this.currentNameCountMap.keySet().iterator(); it.hasNext();) {
            String string = it.next();
            if (this.currentNameCountMap.get(string) > 1) {
                resultSet.add(string);
            }
        }
        return resultSet;
    }

    /**
     * Check if an attributeGroup is valid in the current position in the schema.
     * This is the case, if there is not an attribute with the same name as a part of the group in the same context.
     * @param attributeGroup        source for the check
     * @return boolean              true - if the attributeGroup can be used at the current position of the schema
     */
    private boolean isAttributeGroupValid(AttributeGroup attributeGroup) {
        boolean returnBoolean = true;

        for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            if (attributeParticle instanceof de.tudortmund.cs.bonxai.xsd.Attribute) {
                de.tudortmund.cs.bonxai.xsd.Attribute attribute = (de.tudortmund.cs.bonxai.xsd.Attribute) attributeParticle;
                if (this.getMultipleCountNames().contains(attribute.getName())) {
                    return false;
                }
            } else if (attributeParticle instanceof AttributeRef) {
                de.tudortmund.cs.bonxai.xsd.Attribute attribute = ((AttributeRef) attributeParticle).getAttribute();
                if (this.getMultipleCountNames().contains(attribute.getName())) {
                    return false;
                }
            } else if (attributeParticle instanceof AnyAttribute) {
                if (this.getMultipleCountNames().contains("{*}")) {
                    return false;
                }
            }
        }
        return returnBoolean;
    }

    /**
     * Resolve attributeGroups in case of duplicate attributeNames.
     * Duplicate attributes will be merged in a second process.
     * @param attributeParticle     source for the check
     * @return LinkedList<AttributeParticle>    list of particles with invalid attributeGroups replaced
     */
    private LinkedList<AttributeParticle> replaceAttributeGroupRefsInAttributeParticle(AttributeParticle attributeParticle) {
        LinkedList<AttributeParticle> newAttributeList = new LinkedList<AttributeParticle>();
        if (attributeParticle instanceof de.tudortmund.cs.bonxai.xsd.AttributeRef) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof de.tudortmund.cs.bonxai.common.AnyAttribute) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof de.tudortmund.cs.bonxai.xsd.AttributeGroup) {
            AttributeGroup attributeGroup = (AttributeGroup) attributeParticle;
            if (!this.isAttributeGroupValid(attributeGroup)) {
                for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
                    AttributeParticle currentAttributeParticle = it.next();
                    newAttributeList.addAll(replaceAttributeGroupRefsInAttributeParticle(currentAttributeParticle));
                }
            } else {
                AttributeGroupRef attributeGroupRef = new AttributeGroupRef(this.xmlSchema.getAttributeGroupSymbolTable().getReference(attributeGroup.getName()));
                newAttributeList.add(attributeGroupRef);
            }
        } else if (attributeParticle instanceof de.tudortmund.cs.bonxai.xsd.Attribute) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof de.tudortmund.cs.bonxai.xsd.AttributeGroupRef) {
            newAttributeList.addAll(replaceAttributeGroupRefsInAttributeParticle(((AttributeGroupRef) attributeParticle).getAttributeGroup()));
        }
        return newAttributeList;
    }

    /**
     * Check if there is an AnyAttribute or an AttributeRef within a list of AttributeParticles
     * If this results to true, no attributeGroup will be generated based on this list.
     * @param attributeParticleList     source for the check
     * @return boolean  true - if there is an AnyAttribute or an AttributeRef
     */
    private boolean checkForAttributeRefOrAnyAttribute(LinkedList<AttributeParticle> attributeParticleList) {
        boolean returnBool = true;
        for (Iterator<AttributeParticle> it = attributeParticleList.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();
            if ((attributeParticle instanceof AnyAttribute) || (attributeParticle instanceof AttributeRef)) {
                return false;
            }
        }
        return returnBool;
    }
}


