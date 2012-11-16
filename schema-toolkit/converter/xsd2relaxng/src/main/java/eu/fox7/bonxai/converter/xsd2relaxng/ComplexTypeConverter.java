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

package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.ComplexContentExtension;
import eu.fox7.schematoolkit.xsd.om.ComplexContentInheritance;
import eu.fox7.schematoolkit.xsd.om.ComplexContentRestriction;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Content;
import eu.fox7.schematoolkit.xsd.om.Inheritance;
import eu.fox7.schematoolkit.xsd.om.SimpleContentExtension;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class ComplexTypeConverter
 *
 * This class manages the conversion of a XML schema complexType with all
 * allowed and possible content particle structures.
 *
 * The handling of element-groups takes place within this class, too.
 *
 * @author Lars Schmidt
 */
public class ComplexTypeConverter extends ConverterBase {

    // The elementConverter is used for the conversion of one given XML XSDSchema element definition
    private ElementConverter elementConverter;
    // The elementConverter is used for the conversion of one given XML XSDSchema simpleType definition
    private SimpleTypeConverter simpleTypeConverter;
    // The elementConverter is used for the conversion of one given XML XSDSchema attributeParticle
    private AttributeParticleConverter attributeParticleConverter;
    /**
     * For the handling of the inheritance feature of XML XSDSchema some
     * preprocessing is needed.
     * The preprocessing takes place in class: XSD2RELAXNGConverter.
     * The result of this preprocessing is stores in the two variables defined
     * here:
     *
     * elementInheritanceInformation:   mapping of possible element substitutions
     *                                  for an element
     * typeInheritanceInformation:      mapping of possible element
     *                                  substitutions for a type
     */
    private LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>> elementInheritanceInformation;
    private LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation;

    /**
     * Constructor of class ComplexTypeConverter
     * @param xmlSchema     The source XML XSDSchema object for the conversion
     *                      process
     * @param relaxng       The target RELAX NG object for the conversion
     *                      process
     *
     * @param xsdAttributeDefineRefMap          Look-up table for holding the mapping between an XML XSDSchema attribute and the corresponding conversion result RELAX NG pattern
     * @param xsdAttributeGroupDefineRefMap     Look-up table for holding the mapping between an XML XSDSchema attributeGroup and the corresponding conversion result RELAX NG pattern
     * @param xsdElementDefineRefMap            Look-up table for holding the mapping between an XML XSDSchema element and the corresponding conversion result RELAX NG pattern
     * @param xsdGroupDefineRefMap              Look-up table for holding the mapping between an XML XSDSchema group and the corresponding conversion result RELAX NG pattern
     * @param xsdTypeDefineRefMap               Look-up table for holding the mapping between an XML XSDSchema type and the corresponding conversion result RELAX NG pattern
     *
     * @param elementConverter      The elementConverter, which is used to
     *                              generate RELAX NG pattern as conversion
     *                              results of a XML XSDSchema element
     * @param simpleTypeConverter   The simpleTypeConverter, which is used to
     *                              generate RELAX NG pattern as conversion
     *                              results of XML XSDSchema simpleTypes
     * @param elementInheritanceInformation     Inheritance mapping of possible element substitutions for an element
     * @param typeInheritanceInformation        Inheritance mapping of possible element substitutions for an element
     */
    public ComplexTypeConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<eu.fox7.schematoolkit.xsd.om.Attribute, String> xsdAttributeDefineRefMap, HashMap<eu.fox7.schematoolkit.xsd.om.AttributeGroup, String> xsdAttributeGroupDefineRefMap, HashMap<eu.fox7.schematoolkit.xsd.om.Element, String> xsdElementDefineRefMap, HashMap<eu.fox7.schematoolkit.xsd.om.Group, String> xsdGroupDefineRefMap, HashMap<Type, String> xsdTypeDefineRefMap, ElementConverter elementConverter, SimpleTypeConverter simpleTypeConverter, LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>> elementInheritanceInformation, LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation) {
        super(xmlSchema, relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap);
        this.elementConverter = elementConverter;
        this.simpleTypeConverter = simpleTypeConverter;
        this.attributeParticleConverter = new AttributeParticleConverter(xmlSchema, relaxng, simpleTypeConverter);
        this.elementInheritanceInformation = elementInheritanceInformation;
        this.typeInheritanceInformation = typeInheritanceInformation;
    }

    /**
     * Convert a given complexType to its corresponding RELAX NG pattern
     * structure
     * @param complexType       The source of the conversion in XML XSDSchema
     * @return Pattern          The result of the conversion in RELAX NG
     */
    public Pattern convert(ComplexType complexType) {
        return this.convert(complexType, true, true, null);
    }

    /**
     * Convert a given complexType to its corresponding RELAX NG pattern
     * structure with some settings
     * @param complexType           The source of the conversion in XML XSDSchema
     * @param refAllowed            Setting for the convecreation of defines/refs
     * @param convertAttributes     Setting for the conversion of attributes
     * @return Pattern              The result of the conversion in RELAX NG
     */
    public Pattern convert(ComplexType complexType, boolean refAllowed, boolean convertAttributes, QualifiedName parentTypeName) {

        if (complexType.getName().equals(parentTypeName)) {
            refAllowed = false;
        }

        Pattern returnPattern = null;
        String typeDefineName = null;

        // Handling of ONE complexType
        if (complexType != null) {

            if (this.xsdTypeDefineRefMap.containsKey(complexType)) {
            	returnPattern = new Ref(this.xsdTypeDefineRefMap.get(complexType), grammar);
            } else {
                Group baseGroup = new Group();
                boolean isAlreadyMixed = false;

                if (!complexType.isAnonymous() && refAllowed) {
                    // ComplexType is not anonymous --> prepare a define in the root grammar object
                    typeDefineName = this.registerDummyInDefineRefMap(complexType);
                    this.xsdTypeDefineRefMap.put(complexType, ((Grammar) this.relaxng.getRootPattern()).getDefineLookUpTable().getReference(typeDefineName));
                }

                if (complexType.getContent() == null && complexType.getAttributes().isEmpty()) {
                    // ComplexType without content --> Empty
                    // ComplexType without content, but is mixed --> Text
                    if (complexType.getMixed()) {
                        isAlreadyMixed = true;
                        returnPattern = new Text();
                    } else {
                        returnPattern = new Empty();
                    }
                } else {

                    Group typeConversionGroup = new Group();

                    // Content/Inheritance conversion
                    if (complexType.getContent() != null) {

                        if (complexType.getContent() instanceof ComplexContentType) {
                            ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                            if (complexContentType.getInheritance() != null) {
                                if (complexContentType.getInheritance() instanceof ComplexContentRestriction) {
                                    ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();

                                    // Collect all attributes from the base type
                                    // in a map <attributeName, attribute>.
                                    // The attributes in inherited types have to
                                    // overwrite attributes with the same name
                                    // from the base
                                    // The particles are defined in the
                                    // complexContentType of the complexType.
                                    // For these there is no handling necessary!

                                    if (convertAttributes) {

                                        // Collect Attributes and convert them here!
                                    	Type baseType = this.xmlSchema.getType(complexContentRestriction.getBaseType());
                                        LinkedHashMap<QualifiedName, AttributeParticle> attributeBaseMap = this.collectAttributesFromType(baseType);
                                        attributeBaseMap.size();


                                        LinkedHashMap<QualifiedName, AttributeParticle> currentAttributeMap = this.getAttributeMapFromAttributeParticleList(complexContentRestriction.getAttributes());

                                        // Attributes overwrite the attributes from the base-type
                                        LinkedHashMap<QualifiedName, AttributeParticle> attributeMap = this.mergeAttributeMaps(attributeBaseMap, currentAttributeMap);
                                        attributeBaseMap.size();

                                        for (QualifiedName key: attributeMap.keySet()) {
                                            AttributeParticle attributeParticle = attributeMap.get(key);
                                            Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);
                                            if (attributePattern != null) {
                                                typeConversionGroup.addPattern(simplifyPatternStructure(attributePattern));
                                            }
                                        }
//                                        for (Iterator<AttributeParticle> it = complexContentRestriction.getAttributes().iterator(); it.hasNext();) {
//                                            AttributeParticle attributeParticle = it.next();
//                                            Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);
//                                            if (attributePattern != null) {
//                                                typeConversionGroup.addPattern(simplifyPatternStructure(attributePattern));
//                                            }
//                                        }
                                        convertAttributes = false;
                                    }

                                } else if (complexContentType.getInheritance() instanceof ComplexContentExtension) {
                                    ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                                    Type baseType = this.xmlSchema.getType(complexContentExtension.getBaseType());
                                    // Recursive handling of the base-type
                                    if (baseType != null) {
                                        Pattern basePattern = this.convert((ComplexType) baseType);
                                        if (basePattern != null) {
                                            baseGroup.addPattern(simplifyPatternStructure(basePattern));
                                        }
                                    }

                                    if (convertAttributes) {
                                        // Attributes extend the list of all attributes from the base-type
                                        for (Iterator<AttributeParticle> it = complexContentExtension.getAttributes().iterator(); it.hasNext();) {
                                            AttributeParticle attributeParticle = it.next();
                                            Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);
                                            if (attributePattern != null) {
                                                typeConversionGroup.addPattern(simplifyPatternStructure(attributePattern));
                                            }
                                        }
                                    }
                                }
                            }

                            // Particle conversion
                            Pattern complexContentPattern = this.convertParticleToPattern(complexContentType.getParticle());

                            // Check if the complexContent of the complexType is mixed
                            if (complexContentType.getMixed() && complexContentPattern != null) {
                                // Handling of mixed --> frame with mixed-pattern
                                Mixed mixed = new Mixed();
                                mixed.addPattern(complexContentPattern);
                                typeConversionGroup.addPattern(mixed);
                                isAlreadyMixed = true;
                            } else {
                                if (complexContentPattern != null) {
                                    typeConversionGroup.addPattern(complexContentPattern);
                                }
                            }
                        } else if (complexType.getContent() instanceof SimpleContentType) {
                            SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();

                            if (simpleContentType.getInheritance() != null) {
                                Pattern simpleContentPattern = this.simpleTypeConverter.convertSimpleContentInheritance(simpleContentType.getInheritance(), true, convertAttributes, complexType.getName());
                                if (simpleContentPattern != null) {
                                    typeConversionGroup.addPattern(simpleContentPattern);
                                }
                            }
                        }
                    }

                    // Handle the conversion of attributes
                    if (complexType.getAttributes() != null && !complexType.getAttributes().isEmpty() && convertAttributes) {
                        for (Iterator<eu.fox7.schematoolkit.common.AttributeParticle> it = complexType.getAttributes().iterator(); it.hasNext();) {
                            eu.fox7.schematoolkit.common.AttributeParticle attributeParticle = it.next();

                            Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);

                            if (attributePattern != null) {
                                typeConversionGroup.addPattern(simplifyPatternStructure(attributePattern));
                            }
                        }
                    }

                    // The block attribute from the complexType or the default value from the schema element
                    // is already handled within the preprocessing and influences the handling of the substitutions
                    // processed below. (complexType.getBlockModifiers();)

                    // The final attribute can not be translated to RELAX NG, because there is no inheritance
                    // and thus it can not be prohibited to use the corresponding pattern as "base" for an inherited structure.
                    // (complexType.getFinalModifiers();)


                    // Check if the complexType is mixed
                    if (!isAlreadyMixed && complexType.getMixed()) {

                        // Handling of mixed --> frame with mixed-pattern
                        Mixed mixed = new Mixed();
                        mixed.addPattern(simplifyPatternStructure(typeConversionGroup));

                        Group returnGroup = new Group();
                        if (!baseGroup.getPatterns().isEmpty()) {
                            returnGroup.addPattern(simplifyPatternStructure(baseGroup));
                        }
                        returnGroup.addPattern(mixed);
                        returnPattern = returnGroup;
                    } else {
                        if (!baseGroup.getPatterns().isEmpty()) {
                            LinkedList<Pattern> typeConversionGroupPatterns = typeConversionGroup.getPatterns();
                            typeConversionGroupPatterns.addFirst(simplifyPatternStructure(baseGroup));
                            typeConversionGroup.setPatterns(typeConversionGroupPatterns);
                        }
                        returnPattern = simplifyPatternStructure(typeConversionGroup);
                    }

                    // Handle the abstract attribute of the complexType.
                    // This attribute prohibits the usage of the current type within an instance.
                    if (complexType.isAbstract()) {
                        Group group = new Group();
                        // To prohibit the usage of a pattern, there
                        group.addPattern(new NotAllowed());
                        group.addPattern(returnPattern);
                        returnPattern = simplifyPatternStructure(group);
                    }
                }

                // Handling of a not anonymous type
                if (!complexType.isAnonymous() && refAllowed) {
                    // ComplexType is not anonymous --> register the generated pattern in the define
                    Ref ref = this.setPatternToDefine(typeDefineName, simplifyPatternStructure(returnPattern));
                    returnPattern = ref;
                }
            }
        }
        return returnPattern;
    }

    /**
     * Convert a particle structure from XML XSDSchema to the corresponding pattern
     * structure in RELAX NG
     * @param particle      The source of the current conversion in XML XSDSchema
     * @return Pattern      The result of the current conversion in RELAX NG
     */
    private Pattern convertParticleToPattern(Particle particle) {
        Pattern returnPattern = null;

        // Switch between all possible particle types
        if (particle instanceof ChoicePattern) {

            // Case "ChoicePattern":
            ChoicePattern choicePattern = (ChoicePattern) particle;

            Choice choice = new Choice();

            for (Iterator<Particle> it = choicePattern.getParticles().iterator(); it.hasNext();) {
                Particle innerParticle = it.next();

                Pattern innerPattern = this.convertParticleToPattern(innerParticle);

                if (innerPattern != null) {
                    choice.addPattern(this.simplifyPatternStructure(innerPattern));
                }
            }

            if (choice.getPatterns().isEmpty()) {
                choice.addPattern(new Empty());
            }

            returnPattern = choice;

        } else if (particle instanceof SequencePattern) {

            // Case "SequencePattern":
            SequencePattern sequencePattern = (SequencePattern) particle;

            Group group = new Group();

            for (Iterator<Particle> it = sequencePattern.getParticles().iterator(); it.hasNext();) {
                Particle innerParticle = it.next();

                Pattern innerPattern = this.convertParticleToPattern(innerParticle);

                if (innerPattern != null) {
                    group.addPattern(this.simplifyPatternStructure(innerPattern));
                }
            }

            if (group.getPatterns().isEmpty()) {
                group.addPattern(new Empty());
            }

            returnPattern = group;

        } else if (particle instanceof AllPattern) {

            // Case "AllPattern":
            AllPattern allPattern = (AllPattern) particle;

            Interleave interleave = new Interleave();

            for (Iterator<Particle> it = allPattern.getParticles().iterator(); it.hasNext();) {
                Particle innerParticle = it.next();

                Pattern innerPattern = this.convertParticleToPattern(innerParticle);

                if (innerPattern != null) {
                    interleave.addPattern(this.simplifyPatternStructure(innerPattern));
                }
            }

            if (interleave.getPatterns().isEmpty()) {
                interleave.addPattern(new Empty());
            }

            returnPattern = interleave;

        } else if (particle instanceof CountingPattern) {

            // Case "CountingPattern":
            CountingPattern countingPattern = (CountingPattern) particle;
            Pattern innerPattern = this.convertParticleToPattern(countingPattern.getParticle());
            RelaxNGCountingPattern relaxNGCountingPattern = null;

            if (countingPattern.getMin() == 0 && countingPattern.getMax() == null) {
                // Case "CountingPattern" --> "(...)*" --> zeroOrMore:
            	relaxNGCountingPattern = new ZeroOrMore();
            } else if (countingPattern.getMin() == 1 && countingPattern.getMax() == null) {
                // Case "CountingPattern" --> "(...)+" --> oneOrMore:
            	relaxNGCountingPattern = new OneOrMore();
            } else if (countingPattern.getMin() == 0 && countingPattern.getMax() == 1) {
                // Case "CountingPattern" --> "(...)?" --> optional:
            	relaxNGCountingPattern = new Optional();

                if (innerPattern != null) {
                	relaxNGCountingPattern.addPattern(this.simplifyPatternStructure(innerPattern));
                }

                if (relaxNGCountingPattern.getPatterns().isEmpty()) {
                	relaxNGCountingPattern.addPattern(new Empty());
                }

                returnPattern = relaxNGCountingPattern;
            } else if (countingPattern.getMax() == null) {
                // Case "CountingPattern" --> (a)[2,unbounded] --> "(a,a,a*)" --> group{a,a,zeroOrMore{a}}:
                if (XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY > 0 && countingPattern.getMin() > XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY) {
                    // Approximation to regular expression (a*)
                    ZeroOrMore zeroOrMore = new ZeroOrMore();
                    zeroOrMore.addPattern(innerPattern);
                    returnPattern = zeroOrMore;
                } else {
                    // Generate regular expression for the given countingPattern like: "(a,a,a*)"
                    Group group = new Group();
                    for (int i = 0; i < countingPattern.getMin(); i++) {
                        group.addPattern(innerPattern);
                    }
                    ZeroOrMore zeroOrMore = new ZeroOrMore();
                    zeroOrMore.addPattern(innerPattern);
                    group.addPattern(zeroOrMore);

                    if (group.getPatterns().isEmpty()) {
                        group.addPattern(new Empty());
                    }

                    returnPattern = group;
                }
            } else if (countingPattern.getMax() != null) {
                // Case "CountingPattern" --> (a)[2,4] --> "(a,a,a?,a?)"--> group{a,a,optional{a},optional{a}}:
                Group group = new Group();

                if (countingPattern.getMin() == 0 && countingPattern.getMax() == 0) {
                    // Empty as child
                    group.addPattern(new Empty());
                } else {
                    if (XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY > 0 && countingPattern.getMax() > XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY) {
                        // Approximation to regular expression (a*)
                        ZeroOrMore zeroOrMore = new ZeroOrMore();
                        zeroOrMore.addPattern(innerPattern);
                        group.addPattern(zeroOrMore);
                    } else {
                        if (countingPattern.getMin() == 0 && countingPattern.getMax() == 0) {
                            // Empty as child
                            group.addPattern(new Empty());
                        } else {
                            // Generate regular expression for the given countingPattern like: "(a,a,a?,a?)"
                            for (int i = 0; i < countingPattern.getMin(); i++) {
                                group.addPattern(innerPattern);
                            }

                            Optional optional = new Optional();
                            optional.addPattern(innerPattern);
                            for (int i = 0; i < countingPattern.getMax() - countingPattern.getMin(); i++) {
                                group.addPattern(optional);
                            }
                        }
                    }
                }

                returnPattern = group;
            }
        } else if (particle instanceof AnyPattern) {

            // Case "AnyPattern":
            AnyPattern anyPattern = (AnyPattern) particle;

            returnPattern = this.generateAnyElementPattern(anyPattern);

        } else if (particle instanceof ElementRef) {

            // Case "ElementRef":
            ElementRef elementRef = (ElementRef) particle;

            eu.fox7.schematoolkit.xsd.om.Element element = xmlSchema.getElement(elementRef);
            Pattern elementPattern = this.elementConverter.convertElement(element, true);
            elementPattern = this.elementConverter.handleSubstitutions(element, elementPattern);

            returnPattern = elementPattern;

        } else if (particle instanceof eu.fox7.schematoolkit.xsd.om.Element) {

            // Case "Element":
            eu.fox7.schematoolkit.xsd.om.Element xsdElement = (eu.fox7.schematoolkit.xsd.om.Element) particle;

            Pattern elementPattern = this.elementConverter.convertElement(xsdElement, false);
            elementPattern = this.elementConverter.handleSubstitutions(xsdElement, elementPattern);

            returnPattern = elementPattern;

        } else if (particle instanceof eu.fox7.schematoolkit.common.GroupReference) {

            // Case "GroupRef":
            eu.fox7.schematoolkit.common.GroupReference groupRef = (eu.fox7.schematoolkit.common.GroupReference) particle;

            eu.fox7.schematoolkit.xsd.om.Group group = this.xmlSchema.getGroup(groupRef);
            Pattern groupPattern = this.convertGroupToPattern(group);

            returnPattern = groupPattern;
        }
        return simplifyPatternStructure(returnPattern);
    }

    /**
     * Convert a XML XSDSchema group to the pattern structure in RELAX NG
     * @param xsdGroup      The source of the conversion
     * @return Pattern      The result of the conversion
     */
    public Pattern convertGroupToPattern(eu.fox7.schematoolkit.xsd.om.Group xsdGroup) {
        Pattern returnPattern = null;

        if (this.xsdGroupDefineRefMap.containsKey(xsdGroup)) {
            Ref ref = new Ref(this.xsdGroupDefineRefMap.get(xsdGroup), (Grammar) this.relaxng.getRootPattern());
            returnPattern = ref;
        } else {

            // Prepare a define in the root grammar object for the group
            String groupDefineName = this.registerDummyInDefineRefMap(xsdGroup);

            this.xsdGroupDefineRefMap.put(xsdGroup, groupDefineName);

            /**
             * The following group has nothing to do with the group given from XML XSDSchema.
             * In RELAX NG a "group" is only a container with the semantics of a
             * sequence in XML XSDSchema.
             *
             * To convert the content of the XML XSDSchema group, a container is
             * bulit for holding all converted parts. These will be stored in the following
             * RELAX NG group.
             */
            Group returnGroup = new Group();

            Pattern contentPattern = this.convertParticleToPattern(xsdGroup.getParticle());
            returnGroup.addPattern(contentPattern);

            // Update the define for this newly generated pattern as conversion
            // result of the given XML XSDSchema group.
            Ref ref = this.setPatternToDefine(groupDefineName, returnGroup);
            returnPattern = ref;
        }

        return returnPattern;
    }

    /**
     * Handling of complexType substitutions from XML XSDSchema to offer a choice of
     * all allowed substitutions in the RELAX NG schema, too.
     * @param xsdElement            Element as parent of the type to check for substitutions
     * @param pattern               Pattern structure containing a choice of all
     *                              converted elements defined in the
     *                              substitution
     * @param convertAttributes     Setting to allow/disallow the conversion of
     *                              attached attribues
     * @return Pattern              The result of the substitution handling
     */
    public Pattern handleSubstitutions(eu.fox7.schematoolkit.xsd.om.Element xsdElement, Pattern pattern, boolean convertAttributes) {
        /**
         * Inheritance Handling in type conversion:
         *
         * Use case: type is used deeper within the document.
         * This is not for topLevel types.
         *
         * If the complexType is the head of an inheritance structure, convert all
         * substitutions of this type and combine them in a choice and return
         * the generated choice.
         *
         * The choice must not be put into a define for a type.
         * It is better to generate the substitutions on-the-fly.
         */
    	if (XSD2RelaxNGConverter.HANDLE_SUBSTITUTIONS && this.typeInheritanceInformation.containsKey(xsdElement.getTypeName())) {

            InheritanceInformationCollector informationCollector = new InheritanceInformationCollector();
            LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Type> typeSubstitutions = informationCollector.getAllTypeSubstitutionsForElement(xsdElement, this.typeInheritanceInformation);

            if (!typeSubstitutions.isEmpty()) {

                Choice choice = new Choice();
                choice.addPattern(pattern);
                for (Iterator<eu.fox7.schematoolkit.xsd.om.Type> it = typeSubstitutions.iterator(); it.hasNext();) {
                    eu.fox7.schematoolkit.xsd.om.Type xsdSubstitutionType = it.next();
                    Pattern substitutionTypePattern = this.convert((ComplexType) xsdSubstitutionType, true, convertAttributes, xsdElement.getTypeName());
                    choice.addPattern(substitutionTypePattern);
                }
                pattern = simplifyPatternStructure(choice);
            }
        }
        return pattern;
    }

    /**
     * Getter for the attributeParticleConverter
     * @return AttributeParticleConverter
     */
    public AttributeParticleConverter getAttributeParticleConverter() {
        return attributeParticleConverter;
    }

    /**
     * Getter for the simpleTypeConverter
     * @return SimpleTypeConverter
     */
    public SimpleTypeConverter getSimpleTypeConverter() {
        return simpleTypeConverter;
    }

    /**
     * Collect all attributes from a type and all of its base-types in the
     * inheritance hierarchy. This is used in case of a conversion of a
     * complexContent restriction, where all attributes from the base type can
     * be used, even if they are not defined in the restriction itself.
     * @param currentType   Type as source for the collection of attributes
     * @return LinkedHashMap<String, AttributeParticle>  Map containing all found attributes/anyAttributes
     */
    private LinkedHashMap<QualifiedName, AttributeParticle> collectAttributesFromType(Type currentType) {

        LinkedHashMap<QualifiedName, AttributeParticle> attributeMap = new LinkedHashMap<QualifiedName, AttributeParticle>();

        if (currentType instanceof ComplexType) {
            // Case "ComplexType":
            ComplexType complexType = (ComplexType) currentType;

            Content content = complexType.getContent();
            if (content != null) {
                Inheritance inheritance = content.getInheritance();
                Type baseType = this.xmlSchema.getType(inheritance.getBaseType()); 
                LinkedHashMap<QualifiedName, AttributeParticle> baseMap = collectAttributesFromType(baseType);
                LinkedHashMap<QualifiedName, AttributeParticle> currentMap = null;
                if (inheritance instanceof SimpleContentRestriction) 
                	currentMap = this.getAttributeMapFromAttributeParticleList(((SimpleContentRestriction) inheritance).getAttributes());
                else if (inheritance instanceof SimpleContentExtension)
                	currentMap = this.getAttributeMapFromAttributeParticleList(((SimpleContentExtension) inheritance).getAttributes());
                attributeMap = mergeAttributeMaps(baseMap, currentMap);
            }

            LinkedHashMap<QualifiedName, AttributeParticle> currentMap = this.getAttributeMapFromAttributeParticleList(complexType.getAttributes());

            attributeMap = mergeAttributeMaps(attributeMap, currentMap);

        }

        return attributeMap;
    }

    /**
     * Generate a map of all attributes referenced by their qualified names from
     * a given list of attributeParticles.
     * @param attributes    LinkedList<AttributeParticle> source for the collection of attributes
     * @return LinkedHashMap<String, AttributeParticle>     Map containing all found attributes/anyAttributes
     */
    private LinkedHashMap<QualifiedName, AttributeParticle> getAttributeMapFromAttributeParticleList(LinkedList<AttributeParticle> attributes) {
        LinkedHashMap<QualifiedName, AttributeParticle> resultMap = new LinkedHashMap<QualifiedName, AttributeParticle>();

        for (AttributeParticle attributeParticle: attributes) {
            LinkedHashSet<AttributeParticle> attributeSet = this.collectAttributesFromAttributeParticle(attributeParticle);

            for (AttributeParticle attributeParticle1: attributeSet) {
                if (attributeParticle1 instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
                    eu.fox7.schematoolkit.xsd.om.Attribute attribute = (eu.fox7.schematoolkit.xsd.om.Attribute) attributeParticle1;
                    resultMap.put(attribute.getName(), attribute);
                } else if (attributeParticle1 instanceof eu.fox7.schematoolkit.common.AnyAttribute) {
                    eu.fox7.schematoolkit.common.AnyAttribute anyAttribute = (AnyAttribute) attributeParticle1;
                    resultMap.put("anyAttribute", anyAttribute);
                }
            }
        }
        return resultMap;
    }

    /**
     * Merge two given attributeMaps. The attributes in the second map overwrite
     * attributes with the same nam frim the first map. This is used in case of
     * a complexContent restriction.
     * @param baseMap   LinkedHashMap<String, AttributeParticle>    the source attributeMap
     * @param overwritingMap    LinkedHashMap<String, AttributeParticle>    the overwriting attributeMap
     * @return LinkedHashMap<String, AttributeParticle> attributeMap with resulting attributes from both maps
     */
    private LinkedHashMap<QualifiedName, AttributeParticle> mergeAttributeMaps(LinkedHashMap<QualifiedName, AttributeParticle> baseMap, LinkedHashMap<QualifiedName, AttributeParticle> overwritingMap) {

        LinkedHashMap<QualifiedName, AttributeParticle> resultMap = new LinkedHashMap<QualifiedName, AttributeParticle>();

        for (QualifiedName key: overwritingMap.keySet()) {
            if (overwritingMap.get(key) instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
                eu.fox7.schematoolkit.xsd.om.Attribute attribute = (eu.fox7.schematoolkit.xsd.om.Attribute) overwritingMap.get(key);
                resultMap.put(attribute.getName(), attribute);
            } else if (overwritingMap.get(key) instanceof eu.fox7.schematoolkit.common.AnyAttribute) {
                eu.fox7.schematoolkit.common.AnyAttribute anyAttribute = (AnyAttribute) overwritingMap.get(key);
                resultMap.put("anyAttribute", anyAttribute);
            }
        }

        for (QualifiedName key: baseMap.keySet()) {
            if (!resultMap.containsKey(key)) {
                if (baseMap.get(key) instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
                    eu.fox7.schematoolkit.xsd.om.Attribute attribute = (eu.fox7.schematoolkit.xsd.om.Attribute) baseMap.get(key);
                    resultMap.put(attribute.getName(), attribute);
                } else if (baseMap.get(key) instanceof eu.fox7.schematoolkit.common.AnyAttribute) {
                    eu.fox7.schematoolkit.common.AnyAttribute anyAttribute = (AnyAttribute) baseMap.get(key);
                    resultMap.put("anyAttribute", anyAttribute);
                }
            }
        }
        return resultMap;
    }

    /**
     * Collect all attributes/anyAttributes recursivly from a given
     * attributeParticle structure
     * @param attributeParticle     source for the collection of attributes
     * @return LinkedHashSet<AttributeParticle>     set containing all found attributes/anyAttributes
     */
    private LinkedHashSet<AttributeParticle> collectAttributesFromAttributeParticle(AttributeParticle attributeParticle) {
        LinkedHashSet<AttributeParticle> attributeSet = new LinkedHashSet<AttributeParticle>();

        if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.Attribute || attributeParticle instanceof eu.fox7.schematoolkit.common.AnyAttribute) {
            // Case "attribute", "anyAttribute":
            attributeSet.add(attributeParticle);
        } else if (attributeParticle instanceof eu.fox7.schematoolkit.common.AttributeGroupReference) {
            // Case "attributeGroup ref":
            eu.fox7.schematoolkit.common.AttributeGroupReference attributeGroupRef = (eu.fox7.schematoolkit.common.AttributeGroupReference) attributeParticle;
            eu.fox7.schematoolkit.xsd.om.AttributeGroup attributeGroup = this.xmlSchema.getAttributeGroup(attributeGroupRef);
            for (AttributeParticle attributeParticle1: attributeGroup.getAttributeParticles()) {
                attributeSet.addAll(collectAttributesFromAttributeParticle(attributeParticle1));
            }
        } else if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.AttributeRef) {
            // Case "attribute ref":
            eu.fox7.schematoolkit.xsd.om.AttributeRef attributeRef = (eu.fox7.schematoolkit.xsd.om.AttributeRef) attributeParticle;
            attributeSet.addAll(collectAttributesFromAttributeParticle(this.xmlSchema.getAttribute(attributeRef)));
        }
        return attributeSet;
    }
}
