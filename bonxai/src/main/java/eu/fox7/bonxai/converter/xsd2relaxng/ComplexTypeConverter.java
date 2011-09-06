package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.AnyPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.xsd.AttributeParticle;
import eu.fox7.bonxai.xsd.ComplexContentExtension;
import eu.fox7.bonxai.xsd.ComplexContentRestriction;
import eu.fox7.bonxai.xsd.ComplexContentType;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.ElementRef;
import eu.fox7.bonxai.xsd.SimpleContentExtension;
import eu.fox7.bonxai.xsd.SimpleContentRestriction;
import eu.fox7.bonxai.xsd.SimpleContentType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

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
    private LinkedHashMap<eu.fox7.bonxai.xsd.Element, LinkedHashSet<eu.fox7.bonxai.xsd.Element>> elementInheritanceInformation;
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
    public ComplexTypeConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<eu.fox7.bonxai.xsd.Attribute, SymbolTableRef<LinkedList<Define>>> xsdAttributeDefineRefMap, HashMap<eu.fox7.bonxai.xsd.AttributeGroup, SymbolTableRef<LinkedList<Define>>> xsdAttributeGroupDefineRefMap, HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>> xsdElementDefineRefMap, HashMap<eu.fox7.bonxai.xsd.Group, SymbolTableRef<LinkedList<Define>>> xsdGroupDefineRefMap, HashMap<Type, SymbolTableRef<LinkedList<Define>>> xsdTypeDefineRefMap, ElementConverter elementConverter, SimpleTypeConverter simpleTypeConverter, LinkedHashMap<eu.fox7.bonxai.xsd.Element, LinkedHashSet<eu.fox7.bonxai.xsd.Element>> elementInheritanceInformation, LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation) {
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
        return this.convert(complexType, true, true, "");
    }

    /**
     * Convert a given complexType to its corresponding RELAX NG pattern
     * structure with some settings
     * @param complexType           The source of the conversion in XML XSDSchema
     * @param refAllowed            Setting for the convecreation of defines/refs
     * @param convertAttributes     Setting for the conversion of attributes
     * @return Pattern              The result of the conversion in RELAX NG
     */
    public Pattern convert(ComplexType complexType, boolean refAllowed, boolean convertAttributes, String parentTypeName) {

        if (parentTypeName.equals(complexType.getName())) {
            refAllowed = false;
        }

        Pattern returnPattern = null;
        String typeDefineName = null;

        // Handling of ONE complexType
        if (complexType != null) {

            if (this.xsdTypeDefineRefMap.containsKey(complexType)) {
                Ref ref = new Ref(this.xsdTypeDefineRefMap.get(complexType), (Grammar) this.relaxng.getRootPattern());
                ref.setRefName(this.xsdTypeDefineRefMap.get(complexType).getKey());
                returnPattern = ref;
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
                                        LinkedHashMap<String, AttributeParticle> attributeBaseMap = this.collectAttributesFromType(complexContentRestriction.getBase());
                                        attributeBaseMap.size();


                                        LinkedHashMap<String, AttributeParticle> currentAttributeMap = this.getAttributeMapFromAttributeParticleList(complexContentRestriction.getAttributes());

                                        // Attributes overwrite the attributes from the base-type
                                        LinkedHashMap<String, AttributeParticle> attributeMap = this.mergeAttributeMaps(attributeBaseMap, currentAttributeMap);
                                        attributeBaseMap.size();

                                        for (Iterator<String> it = attributeMap.keySet().iterator(); it.hasNext();) {
                                            String key = it.next();
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

                                    // Recursive handling of the base-type
                                    if (complexContentExtension.getBase() != null) {
                                        Pattern basePattern = this.convert((ComplexType) complexContentExtension.getBase());
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
                        for (Iterator<eu.fox7.bonxai.xsd.AttributeParticle> it = complexType.getAttributes().iterator(); it.hasNext();) {
                            eu.fox7.bonxai.xsd.AttributeParticle attributeParticle = it.next();

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
        Integer level = new Integer(0);
        LinkedHashMap<Integer, Integer> tempHash = new LinkedHashMap<Integer, Integer>();
        return this.convertParticleToPattern(particle, level, tempHash);
    }

    /**
     * Convert a particle structure from XML XSDSchema to the corresponding pattern
     * structure in RELAX NG recursivly for one particle
     * @param particle              The source of the current conversion in
     *                              XML XSDSchema
     * @param level                 This is an integer value for depth level of
     *                              the recursive structure used for checking
     *                              the countingPattern upper bound mark
     * @param countingPatternMap    A mapping for level to the countingPattern
     *                              upper bound mark
     * @return Pattern              The result of the current conversion in
     *                              RELAX NG
     */
    private Pattern convertParticleToPattern(Particle particle, Integer level, LinkedHashMap<Integer, Integer> countingPatternMap) {
        Pattern returnPattern = null;
        level++;

        // Switch between all possible particle types
        if (particle instanceof ChoicePattern) {

            // Case "ChoicePattern":
            ChoicePattern choicePattern = (ChoicePattern) particle;

            Choice choice = new Choice();

            for (Iterator<Particle> it = choicePattern.getParticles().iterator(); it.hasNext();) {
                Particle innerParticle = it.next();

                Pattern innerPattern = this.convertParticleToPattern(innerParticle, level, countingPatternMap);

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

                Pattern innerPattern = this.convertParticleToPattern(innerParticle, level, countingPatternMap);

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

                Pattern innerPattern = this.convertParticleToPattern(innerParticle, level, countingPatternMap);

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

            if (countingPattern.getMin() != null && countingPattern.getMin() == 0
                    && countingPattern.getMax() == null) {

                // Case "CountingPattern" --> "(...)*" --> zeroOrMore:
                ZeroOrMore zeroOrMore = new ZeroOrMore();

                // Add one to the countingPatternMap for this level
                countingPatternMap.put(level, 1);
                Integer countingPatternMultiplicity = 1;
                // Calculate the current multiplicity value
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }

                Pattern innerPattern = this.convertParticleToPattern(countingPattern.getParticles().getFirst(), level, countingPatternMap);

                if (innerPattern != null) {
                    zeroOrMore.addPattern(this.simplifyPatternStructure(innerPattern));
                }

                if (zeroOrMore.getPatterns().isEmpty()) {
                    zeroOrMore.addPattern(new Empty());
                }

                returnPattern = zeroOrMore;

            } else if (countingPattern.getMin() != null && countingPattern.getMin() == 1
                    && countingPattern.getMax() == null) {

                // Case "CountingPattern" --> "(...)+" --> oneOrMore:
                OneOrMore oneOrMore = new OneOrMore();

                // Add one to the countingPatternMap for this level
                countingPatternMap.put(level, 1);
                Integer countingPatternMultiplicity = 1;
                // Calculate the current multiplicity value
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }

                for (Iterator<Particle> it = countingPattern.getParticles().iterator(); it.hasNext();) {
                    Particle innerParticle = it.next();

                    Pattern innerPattern = this.convertParticleToPattern(innerParticle, level, countingPatternMap);

                    if (innerPattern != null) {
                        oneOrMore.addPattern(this.simplifyPatternStructure(innerPattern));
                    }
                }
                if (oneOrMore.getPatterns().isEmpty()) {
                    oneOrMore.addPattern(new Empty());
                }
                returnPattern = oneOrMore;

            } else if (countingPattern.getMin() != null && countingPattern.getMin() == 0
                    && countingPattern.getMax() != null && countingPattern.getMax() == 1) {

                // Case "CountingPattern" --> "(...)?" --> optional:
                Optional optional = new Optional();

                // Add one to the countingPatternMap for this level
                countingPatternMap.put(level, 1);

                for (Iterator<Particle> it = countingPattern.getParticles().iterator(); it.hasNext();) {
                    Particle innerParticle = it.next();

                    Pattern innerPattern = this.convertParticleToPattern(innerParticle, level, countingPatternMap);

                    if (innerPattern != null) {
                        optional.addPattern(this.simplifyPatternStructure(innerPattern));
                    }
                }
                if (optional.getPatterns().isEmpty()) {
                    optional.addPattern(new Empty());
                }
                returnPattern = optional;

            } else if (countingPattern.getMin() != null
                    && countingPattern.getMax() == null) {

                // Case "CountingPattern" --> (a)[2,unbounded] --> "(a,a,a*)" --> group{a,a,zeroOrMore{a}}:
                countingPatternMap.put(level, countingPattern.getMin());
                Integer countingPatternMultiplicity = 1;
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }

                Pattern innerPattern = this.convertParticleToPattern(countingPattern.getParticles().getFirst(), level, countingPatternMap);

                if (XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY > 0 && countingPatternMultiplicity > XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY) {
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
            } else if (countingPattern.getMin() != null
                    && countingPattern.getMax() != null) {

                // Case "CountingPattern" --> (a)[2,4] --> "(a,a,a?,a?)"--> group{a,a,optional{a},optional{a}}:
                countingPatternMap.put(level, countingPattern.getMax());
                Integer countingPatternMultiplicity = 1;
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }

                Pattern innerPattern = this.convertParticleToPattern(countingPattern.getParticles().getFirst(), level, countingPatternMap);

                Group group = new Group();

                if (countingPattern.getMin() == 0 && countingPattern.getMax() == 0) {
                    // Empty as child
                    group.addPattern(new Empty());
                } else {
                    if (XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY > 0 && countingPatternMultiplicity > XSD2RelaxNGConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY) {
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

                if (group.getPatterns().isEmpty()) {
                    group.addPattern(new Empty());
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

            Pattern elementPattern = this.elementConverter.convertElement(elementRef.getElement(), true);
            elementPattern = this.elementConverter.handleSubstitutions(elementRef.getElement(), elementPattern);

            returnPattern = elementPattern;

        } else if (particle instanceof eu.fox7.bonxai.xsd.Element) {

            // Case "Element":
            eu.fox7.bonxai.xsd.Element xsdElement = (eu.fox7.bonxai.xsd.Element) particle;

            Pattern elementPattern = this.elementConverter.convertElement(xsdElement, false);
            elementPattern = this.elementConverter.handleSubstitutions(xsdElement, elementPattern);

            returnPattern = elementPattern;

        } else if (particle instanceof eu.fox7.bonxai.common.GroupRef) {

            // Case "GroupRef":
            eu.fox7.bonxai.common.GroupRef groupRef = (eu.fox7.bonxai.common.GroupRef) particle;

            Pattern groupPattern = this.convertGroupToPattern((eu.fox7.bonxai.xsd.Group) groupRef.getGroup());

            returnPattern = groupPattern;
        }
        level--;
        return simplifyPatternStructure(returnPattern);
    }

    /**
     * Convert a XML XSDSchema group to the pattern structure in RELAX NG
     * @param xsdGroup      The source of the conversion
     * @return Pattern      The result of the conversion
     */
    public Pattern convertGroupToPattern(eu.fox7.bonxai.xsd.Group xsdGroup) {
        Pattern returnPattern = null;

        if (this.xsdGroupDefineRefMap.containsKey(xsdGroup)) {
            Ref ref = new Ref(this.xsdGroupDefineRefMap.get(xsdGroup), (Grammar) this.relaxng.getRootPattern());
            ref.setRefName(this.xsdGroupDefineRefMap.get(xsdGroup).getKey());
            returnPattern = ref;
        } else {

            // Prepare a define in the root grammar object for the group
            String groupDefineName = this.registerDummyInDefineRefMap(xsdGroup);

            this.xsdGroupDefineRefMap.put(xsdGroup, ((Grammar) this.relaxng.getRootPattern()).getDefineLookUpTable().getReference(groupDefineName));

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

            Pattern contentPattern = this.convertParticleToPattern(xsdGroup.getParticleContainer());
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
    public Pattern handleSubstitutions(eu.fox7.bonxai.xsd.Element xsdElement, Pattern pattern, boolean convertAttributes) {
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
        if (XSD2RelaxNGConverter.HANDLE_SUBSTITUTIONS && this.typeInheritanceInformation.containsKey(xsdElement.getType())) {

            InheritanceInformationCollector informationCollector = new InheritanceInformationCollector();
            LinkedHashSet<eu.fox7.bonxai.xsd.Type> typeSubstitutions = informationCollector.getAllTypeSubstitutionsForElement(xsdElement, this.typeInheritanceInformation);

            if (!typeSubstitutions.isEmpty()) {

                Choice choice = new Choice();
                choice.addPattern(pattern);
                for (Iterator<eu.fox7.bonxai.xsd.Type> it = typeSubstitutions.iterator(); it.hasNext();) {
                    eu.fox7.bonxai.xsd.Type xsdSubstitutionType = it.next();
                    Pattern substitutionTypePattern = this.convert((ComplexType) xsdSubstitutionType, true, convertAttributes, xsdElement.getType().getName());
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
    private LinkedHashMap<String, AttributeParticle> collectAttributesFromType(Type currentType) {

        LinkedHashMap<String, AttributeParticle> attributeMap = new LinkedHashMap<String, AttributeParticle>();

        if (currentType instanceof ComplexType) {
            // Case "ComplexType":
            ComplexType complexType = (ComplexType) currentType;

            if (complexType.getContent() != null) {
                if (complexType.getContent() instanceof ComplexContentType) {
                    // Case "ComplexContentType":
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();

                    if (complexContentType.getInheritance() instanceof ComplexContentRestriction) {
                        // Case "ComplexContentRestriction":
                        ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();

                        LinkedHashMap<String, AttributeParticle> baseMap = collectAttributesFromType(complexContentRestriction.getBase());
                        LinkedHashMap<String, AttributeParticle> currentMap = this.getAttributeMapFromAttributeParticleList(complexContentRestriction.getAttributes());

                        attributeMap = mergeAttributeMaps(baseMap, currentMap);

                    } else if (complexContentType.getInheritance() instanceof ComplexContentExtension) {
                        // Case "ComplexContentExtension":
                        ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();

                        LinkedHashMap<String, AttributeParticle> baseMap = collectAttributesFromType(complexContentExtension.getBase());
                        LinkedHashMap<String, AttributeParticle> currentMap = this.getAttributeMapFromAttributeParticleList(complexContentExtension.getAttributes());

                        attributeMap = mergeAttributeMaps(baseMap, currentMap);
                    }

                } else if (complexType.getContent() instanceof SimpleContentType) {
                    // Case "SimpleContentType":
                    SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                    if (simpleContentType.getInheritance() instanceof SimpleContentRestriction) {
                        // Case "SimpleContentRestriction":
                        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();

                        LinkedHashMap<String, AttributeParticle> baseMap = collectAttributesFromType(simpleContentRestriction.getBase());
                        LinkedHashMap<String, AttributeParticle> currentMap = this.getAttributeMapFromAttributeParticleList(simpleContentRestriction.getAttributes());

                        attributeMap = mergeAttributeMaps(baseMap, currentMap);

                    } else if (simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                        // Case "SimpleContentExtension":
                        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();

                        LinkedHashMap<String, AttributeParticle> baseMap = collectAttributesFromType(simpleContentExtension.getBase());
                        LinkedHashMap<String, AttributeParticle> currentMap = this.getAttributeMapFromAttributeParticleList(simpleContentExtension.getAttributes());

                        attributeMap = mergeAttributeMaps(baseMap, currentMap);
                    }
                }
            }

            LinkedHashMap<String, AttributeParticle> currentMap = this.getAttributeMapFromAttributeParticleList(complexType.getAttributes());

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
    private LinkedHashMap<String, AttributeParticle> getAttributeMapFromAttributeParticleList(LinkedList<AttributeParticle> attributes) {
        LinkedHashMap<String, AttributeParticle> resultMap = new LinkedHashMap<String, AttributeParticle>();

        for (Iterator<AttributeParticle> it = attributes.iterator(); it.hasNext();) {
            AttributeParticle attributeParticle = it.next();

            LinkedHashSet<AttributeParticle> attributeSet = this.collectAttributesFromAttributeParticle(attributeParticle);

            for (Iterator<AttributeParticle> it1 = attributeSet.iterator(); it1.hasNext();) {
                AttributeParticle attributeParticle1 = it1.next();
                if (attributeParticle1 instanceof eu.fox7.bonxai.xsd.Attribute) {
                    eu.fox7.bonxai.xsd.Attribute attribute = (eu.fox7.bonxai.xsd.Attribute) attributeParticle1;
                    resultMap.put(attribute.getName(), attribute);
                } else if (attributeParticle1 instanceof eu.fox7.bonxai.common.AnyAttribute) {
                    eu.fox7.bonxai.common.AnyAttribute anyAttribute = (AnyAttribute) attributeParticle1;
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
    private LinkedHashMap<String, AttributeParticle> mergeAttributeMaps(LinkedHashMap<String, AttributeParticle> baseMap, LinkedHashMap<String, AttributeParticle> overwritingMap) {

        LinkedHashMap<String, AttributeParticle> resultMap = new LinkedHashMap<String, AttributeParticle>();

        for (Iterator<String> it = overwritingMap.keySet().iterator(); it.hasNext();) {
            String key = it.next();

            if (overwritingMap.get(key) instanceof eu.fox7.bonxai.xsd.Attribute) {
                eu.fox7.bonxai.xsd.Attribute attribute = (eu.fox7.bonxai.xsd.Attribute) overwritingMap.get(key);
                resultMap.put(attribute.getName(), attribute);
            } else if (overwritingMap.get(key) instanceof eu.fox7.bonxai.common.AnyAttribute) {
                eu.fox7.bonxai.common.AnyAttribute anyAttribute = (AnyAttribute) overwritingMap.get(key);
                resultMap.put("anyAttribute", anyAttribute);
            }
        }

        for (Iterator<String> it = baseMap.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            if (!resultMap.containsKey(key)) {
                if (baseMap.get(key) instanceof eu.fox7.bonxai.xsd.Attribute) {
                    eu.fox7.bonxai.xsd.Attribute attribute = (eu.fox7.bonxai.xsd.Attribute) baseMap.get(key);
                    resultMap.put(attribute.getName(), attribute);
                } else if (baseMap.get(key) instanceof eu.fox7.bonxai.common.AnyAttribute) {
                    eu.fox7.bonxai.common.AnyAttribute anyAttribute = (AnyAttribute) baseMap.get(key);
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

        if (attributeParticle instanceof eu.fox7.bonxai.xsd.Attribute || attributeParticle instanceof eu.fox7.bonxai.common.AnyAttribute) {
            // Case "attribute", "anyAttribute":
            attributeSet.add(attributeParticle);
        } else if (attributeParticle instanceof eu.fox7.bonxai.xsd.AttributeGroup) {
            // Case "attributeGroup":
            eu.fox7.bonxai.xsd.AttributeGroup attributeGroup = (eu.fox7.bonxai.xsd.AttributeGroup) attributeParticle;
            for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
                AttributeParticle attributeParticle1 = it.next();
                attributeSet.addAll(collectAttributesFromAttributeParticle(attributeParticle1));
            }
        } else if (attributeParticle instanceof eu.fox7.bonxai.xsd.AttributeGroupRef) {
            // Case "attributeGroup ref":
            eu.fox7.bonxai.xsd.AttributeGroupRef attributeGroupRef = (eu.fox7.bonxai.xsd.AttributeGroupRef) attributeParticle;
            attributeSet.addAll(collectAttributesFromAttributeParticle(attributeGroupRef.getAttributeGroup()));
        } else if (attributeParticle instanceof eu.fox7.bonxai.xsd.AttributeRef) {
            // Case "attribute ref":
            eu.fox7.bonxai.xsd.AttributeRef attributeRef = (eu.fox7.bonxai.xsd.AttributeRef) attributeParticle;
            attributeSet.addAll(collectAttributesFromAttributeParticle(attributeRef.getAttribute()));
        }
        return attributeSet;
    }
}
