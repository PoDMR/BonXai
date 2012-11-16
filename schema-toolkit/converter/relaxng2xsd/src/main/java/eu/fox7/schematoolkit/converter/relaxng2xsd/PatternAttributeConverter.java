/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AttributeGroupReference;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions.MultipleAnyAttributeException;
import eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions.OnlyFullQualifiedNamesAllowedException;
import eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions.ParticleIsNullException;
import eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions.TypeIsNullException;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.AttributeRef;
import eu.fox7.schematoolkit.xsd.om.AttributeUse;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.SimpleContentUnion;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

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
    protected HashSet<QualifiedName> usedTopLevelAttributeGroupNames = new HashSet<QualifiedName>();
    /**
     * Global set of already used toplevel attribute names
     */
    HashSet<String> usedTopLevelAttributeNames = new HashSet<String>();
    /**
     * Local variable for the PatternSimpleTypeConverter used within this PatternAttributeConverter
     */
    PatternSimpleTypeConverter patternSimpleTypeConverter;
    private HashMap<String, Integer> currentNameCountMap = new HashMap<String, Integer>();
    private HashSet<String> usedLocalAttributeGroupNames = new HashSet<String>();
	private HashMap<String, QualifiedName> refToAttributeGroupMap = new HashMap<String, QualifiedName>();

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
    }

    /**
     * Method for the conversion of a given pattern list to the corresponding XML XSDSchema attributeParticle list
     * @param patternsContainingAttribute                                       basis for the conversion
     * @return LinkedList<eu.fox7.schematoolkit.xsd.om.AttributeParticle>        result of the conversion
     * @throws Exception
     */
    public LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> convertPatternListToAttributeParticleList(HashSet<Pattern> patternsContainingAttribute) throws Exception {
        // handle attribute content conversion
        LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> resultAttributeList = new LinkedList<AttributeParticle>();
        this.currentNameCountMap = new HashMap<String, Integer>();
        for (Pattern currentAttributeContentPattern: patternsContainingAttribute) {
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
     * @return eu.fox7.schematoolkit.xsd.om.Type     type, result of the type creation
     * @throws TypeIsNullException
     * @throws Exception
     */
    private eu.fox7.schematoolkit.xsd.om.Type generateTypeForAttribute(Attribute attribute, String localTypeName) throws TypeIsNullException, Exception {
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
     * @return LinkedList<eu.fox7.schematoolkit.xsd.om.AttributeParticle>        result of the conversion, a list of XML XSDSchema attribute particles
     * @throws Exception
     */
    private LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> convertPatternToAttributeParticleList(Pattern relaxNGPattern) throws Exception {

        LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> resultAttributeList = new LinkedList<AttributeParticle>();

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
                        AttributeGroupReference attributeGroupRef = new AttributeGroupReference(this.refToAttributeGroupMap.get(ref.getUniqueRefID()));
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

                        Namespace namespace = ref.getAttributeNamespace();

                        if (RelaxNG2XSDConverter.REF_TO_ATTRIBUTEGROUP_CONVERSION && generatedAttributeParticles.size() > 1 && this.checkForAttributeRefOrAnyAttribute(generatedAttributeParticles)) {

                            AttributeGroup agr = putAttributeParticleIntoAttributeGroup(ref.getRefName(), namespace, generatedAttributeParticles);

                            this.refToAttributeGroupMap.put(ref.getUniqueRefID(), agr.getName());

                            AttributeGroupReference attributeGroupRef = new AttributeGroupReference(agr.getName());

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
                        AttributeGroupReference attributeGroupRef = new AttributeGroupReference(this.refToAttributeGroupMap.get(parentRef.getUniqueRefID()));
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

                        Namespace namespace = parentRef.getAttributeNamespace();

                        if (generatedAttributeParticles.size() > 1 && this.checkForAttributeRefOrAnyAttribute(generatedAttributeParticles)) {

                            AttributeGroup agr = putAttributeParticleIntoAttributeGroup(parentRef.getRefName(), namespace, generatedAttributeParticles);

                            this.refToAttributeGroupMap.put(parentRef.getUniqueRefID(), agr.getName());

                            AttributeGroupReference attributeGroupRef = new AttributeGroupReference(agr.getName());

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
     * @return LinkedList<eu.fox7.schematoolkit.xsd.om.AttributeParticle>
     */
    private LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> mergeAttributeLists(LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeList1, LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeList2) throws MultipleAnyAttributeException, OnlyFullQualifiedNamesAllowedException {
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
    private void addAttributeParticleToAttributeList(LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeList, eu.fox7.schematoolkit.common.AttributeParticle attributeParticle) throws MultipleAnyAttributeException, OnlyFullQualifiedNamesAllowedException {
        AnyAttribute anyAttributeAlreadyInList = null;

        for (Iterator<eu.fox7.schematoolkit.common.AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
            eu.fox7.schematoolkit.common.AttributeParticle innerAttributeParticle = it.next();
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
     * @return LinkedList<eu.fox7.schematoolkit.xsd.om.AttributeParticle>    cleaned attributeList as result
     */
    private LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> cleanUpAttributeList(LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeList) {
        LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> newAttributeList = new LinkedList<AttributeParticle>();
        LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> otherAttributeParticles = new LinkedList<AttributeParticle>();
        HashMap<QualifiedName, LinkedList<eu.fox7.schematoolkit.xsd.om.Attribute>> attributeNameInfo = new HashMap<QualifiedName, LinkedList<eu.fox7.schematoolkit.xsd.om.Attribute>>();

        for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
                eu.fox7.schematoolkit.xsd.om.Attribute currentAttribute = (eu.fox7.schematoolkit.xsd.om.Attribute) attributeParticle;
                if (attributeNameInfo.get(currentAttribute.getName()) != null) {
                    attributeNameInfo.get(currentAttribute.getName()).add(currentAttribute);
                } else {
                    LinkedList<eu.fox7.schematoolkit.xsd.om.Attribute> newCurrentAttributeList = new LinkedList<eu.fox7.schematoolkit.xsd.om.Attribute>();
                    newCurrentAttributeList.add(currentAttribute);
                    attributeNameInfo.put(currentAttribute.getName(), newCurrentAttributeList);
                }
            } else {
                otherAttributeParticles.add(attributeParticle);
            }
        }
        for (QualifiedName currentAttributeName: attributeNameInfo.keySet()) {
            Namespace namespace = null;
            if (attributeNameInfo.get(currentAttributeName) != null && attributeNameInfo.get(currentAttributeName).size() > 1) {
                namespace = attributeNameInfo.get(currentAttributeName).getFirst().getName().getNamespace();
                eu.fox7.schematoolkit.xsd.om.Attribute newAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(currentAttributeName);
                newAttribute.setForm(null);

                AttributeUse attributeUse = AttributeUse.Required;

                LinkedList<Type> typeList = new LinkedList<Type>();
                for (eu.fox7.schematoolkit.xsd.om.Attribute currentAttribute: attributeNameInfo.get(currentAttributeName)) {
                    if (currentAttribute.getUse().equals(AttributeUse.Optional) && !attributeUse.equals(AttributeUse.Prohibited)) {
                        attributeUse = AttributeUse.Optional;
                    }

                    if (currentAttribute.getUse().equals(AttributeUse.Prohibited)) {
                        attributeUse = AttributeUse.Prohibited;
                    }

                    if (currentAttribute.getSimpleTypeName() != null) {
                        Type strType = this.xmlSchema.getType(currentAttribute.getSimpleTypeName());
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
                        newAttribute.setSimpleType(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(localTypeName, newSimpleType));
                    } else {
                        newAttribute.setSimpleType(typeList.getFirst());
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
     * @return LinkedList<eu.fox7.schematoolkit.xsd.om.AttributeParticle>        result of the conversion
     * @throws Exception
     */
    private LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> convertAttribute(Attribute attribute) throws Exception {

        LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> resultAttributeList = new LinkedList<AttributeParticle>();

        // Generate all possible names for the resulting structure.
        // For each name there has to be generated a new attribute.

        NameClassAnalyzer nameClassAnalyzer = new NameClassAnalyzer(attribute);
        nameClassAnalyzer.analyze();

        String localTypeName = "type";

        // Generate a XML XSDSchema type depending on the content patterns of the
        // current RELAX NG attribute

        if (this.usedLocalNames.contains(localTypeName)) {
            localTypeName = localTypeName + "_" + this.usedLocalNames.size();
        }
        this.usedLocalNames.add(localTypeName);

        Type type = this.generateTypeForAttribute(attribute, localTypeName);

        QualifiedName fullQualifiedTypeName = null;
        if (type != null) {
            SimpleType simpleType = (SimpleType) type;
            fullQualifiedTypeName = simpleType.getName();

//            if (countRealAttributeNames > 1) {
                type.setIsAnonymous(false);
//            }
            this.xmlSchema.addType(type);
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

                            eu.fox7.schematoolkit.xsd.om.AttributeGroup attributeGroup = new eu.fox7.schematoolkit.xsd.om.AttributeGroup(attributeGroupName);
                            attributeGroup.addAttributeParticle(newAnyAttribute);

                            SymbolTableRef<eu.fox7.schematoolkit.xsd.om.AttributeGroup> strAttributeGroup = this.xmlSchema.getAttributeGroupSymbolTable().updateOrCreateReference(attributeGroupName, attributeGroup);

                            if (!usedTopLevelAttributeGroupNames.contains(attributeGroupName)) {
                                importedSchema.getSchema().addAttributeGroup(strAttributeGroup);
                                usedTopLevelAttributeGroupNames.add(attributeGroupName);
                            }

                            importedSchema.getSchema().getAttributeGroupSymbolTable().setReference(attributeGroup.getName(), strAttributeGroup);
                            eu.fox7.bonxai.xsd.AttributeGroupRef attributeGroupRef = new eu.fox7.bonxai.xsd.AttributeGroupRef(strAttributeGroup);
                            this.addAttributeParticleToAttributeList(resultAttributeList, attributeGroupRef);

                        } else {
                            this.addAttributeParticleToAttributeList(resultAttributeList, anyAttribute);
                        }
                    }
                } else {
                    eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = null;
                    if (typeRef != null) {
                        xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(currentNameKey, typeRef);
                        if (typeRef.getReference().isAnonymous()) {
                            xsdAttribute.setTypeAttr(false);
                        } else {
                            xsdAttribute.setTypeAttr(true);
                        }
                    } else {
                        xsdAttribute = new eu.fox7.schematoolkit.xsd.om.Attribute(currentNameKey);
                        xsdAttribute.setTypeAttr(false);
                    }
                    this.registerNameInMap(xsdAttribute.getName());
                    SymbolTableRef<eu.fox7.schematoolkit.xsd.om.Attribute> strAttribute = this.xmlSchema.getAttributeSymbolTable().updateOrCreateReference(xsdAttribute.getName(), xsdAttribute);

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
                            SymbolTableRef<eu.fox7.schematoolkit.xsd.om.Attribute> strAttributeTwo = importedSchema.getSchema().getAttributeSymbolTable().getReference(xsdAttribute.getName());
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
     * @return SymbolTableRef<eu.fox7.schematoolkit.xsd.om.AttributeGroup>       resulting SymbolTableReference to the generated attributeGroup
     * @throws Exception
     */
    private AttributeGroup putAttributeParticleIntoAttributeGroup(String localAttributeGroupName, Namespace namespace, LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeParticles) throws ParticleIsNullException, Exception {

        if (this.usedLocalAttributeGroupNames.contains(localAttributeGroupName)) {
            localAttributeGroupName = localAttributeGroupName + "_" + this.usedLocalAttributeGroupNames.size();
        }
        this.usedLocalAttributeGroupNames.add(localAttributeGroupName);
        QualifiedName fqAttributeGroupName = new QualifiedName(namespace,localAttributeGroupName);


        AttributeGroup attributeGroup = new AttributeGroup(fqAttributeGroupName);

        for (Iterator<AttributeParticle> it = attributeParticles.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();
            attributeGroup.addAttributeParticle(attributeParticle);
        }

        return attributeGroup;
    }

    /**
     * Setter for the RELAX NG ref to XML XSDSchema attribute group map
     * @param refToAttributeGroupMap
     */
    public void setRefToAttributeGroupMap(HashMap<String, QualifiedName> refToAttributeGroupMap) {
        this.refToAttributeGroupMap = refToAttributeGroupMap;
    }

    /**
     * Register all used attributeGroups in the global list of attributeGroups of the XML XSDSchema to write them out with the XSD Writer.
     * @param attributeParticleList      source list
     */
    private void registerUnregisteredAttributeGroupsInSchema(LinkedList<AttributeParticle> attributeParticleList) {
        for (AttributeParticle attributeParticle: attributeParticleList) {
            AttributeParticle attributeParticle = it.next();

            if (attributeParticle instanceof AttributeGroup) {
                AttributeGroup attributeGroup = (AttributeGroup) attributeParticle;

                if (!this.usedTopLevelAttributeGroupNames.contains(attributeGroup.getName())) {
                    this.usedTopLevelAttributeGroupNames.add(attributeGroup.getName());
                    this.xmlSchema.addAttributeGroup(attributeGroup);
                }
                LinkedList<AttributeParticle> tempList = new LinkedList<AttributeParticle>();
                for (Iterator<AttributeParticle> it1 = attributeGroup.getAttributeParticles().iterator(); it1.hasNext();) {
                    AttributeParticle attributeParticle1 = it1.next();
                    tempList.add(attributeParticle1);
                }
                if (!tempList.isEmpty()) {
                    registerUnregisteredAttributeGroupsInSchema(tempList);
                }

            } else if (attributeParticle instanceof AttributeGroupReference) {
                AttributeGroupReference attributeGroupRef = (AttributeGroupReference) attributeParticle;
                AttributeGroup attributeGroup = this.xmlSchema.getAttributeGroup(attributeGroupRef);

                if (this.usedTopLevelAttributeGroupNames != null && !this.usedTopLevelAttributeGroupNames.contains(attributeGroup.getName())) {
                    this.usedTopLevelAttributeGroupNames.add(attributeGroup.getName());
                    this.xmlSchema.addAttributeGroup(attributeGroup);
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
        for (AttributeParticle attributeParticle: attributeParticleList) {
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

            if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
                eu.fox7.schematoolkit.xsd.om.Attribute attribute = (eu.fox7.schematoolkit.xsd.om.Attribute) attributeParticle;
                if (this.getMultipleCountNames().contains(attribute.getName())) {
                    return false;
                }
            } else if (attributeParticle instanceof AttributeRef) {
                eu.fox7.schematoolkit.xsd.om.Attribute attribute = this.xmlSchema.getAttribute(((AttributeRef) attributeParticle));
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
        if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.AttributeRef) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof eu.fox7.schematoolkit.common.AnyAttribute) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
            newAttributeList.add(attributeParticle);
        } else if (attributeParticle instanceof eu.fox7.schematoolkit.common.AttributeGroupReference) {
            newAttributeList.addAll(replaceAttributeGroupRefsInAttributeGroup(this.xmlSchema.getAttributeGroup(((AttributeGroupReference) attributeParticle))));
        }
        return newAttributeList;
    }
    
    private LinkedList<AttributeParticle> replaceAttributeGroupRefsInAttributeGroup(AttributeGroup attributeParticle) {
        LinkedList<AttributeParticle> newAttributeList = new LinkedList<AttributeParticle>();
    	AttributeGroup attributeGroup = (AttributeGroup) attributeParticle;
    	if (!this.isAttributeGroupValid(attributeGroup)) {
    		for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
    			AttributeParticle currentAttributeParticle = it.next();
    			newAttributeList.addAll(replaceAttributeGroupRefsInAttributeParticle(currentAttributeParticle));
    		}
    	} else {
    		AttributeGroupReference attributeGroupRef = new AttributeGroupReference(attributeGroup.getName());
    		newAttributeList.add(attributeGroupRef);
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


