package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.AttributeParticle;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.SimpleContentExtension;
import eu.fox7.bonxai.xsd.SimpleContentList;
import eu.fox7.bonxai.xsd.SimpleContentRestriction;
import eu.fox7.bonxai.xsd.SimpleContentUnion;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class SimpleTypeConverter
 *
 * This class provides the conversion of a XML XSDSchema simpleType to a
 * corresponding RELAX NG pattern structure
 *
 * @author Lars Schmidt
 */
public class SimpleTypeConverter extends ConverterBase {

    private ComplexTypeConverter complexTypeConverter;
    private AttributeParticleConverter attributeParticleConverter;
    private LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation;

    /**
     * Constructor of class SimpleTypeConverter
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
     * @param typeInheritanceInformation        Inheritance mapping of possible element substitutions for an element
     */
    public SimpleTypeConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<eu.fox7.bonxai.xsd.Attribute, SymbolTableRef<LinkedList<Define>>> xsdAttributeDefineRefMap, HashMap<AttributeGroup, SymbolTableRef<LinkedList<Define>>> xsdAttributeGroupDefineRefMap, HashMap<eu.fox7.bonxai.xsd.Element, SymbolTableRef<LinkedList<Define>>> xsdElementDefineRefMap, HashMap<eu.fox7.bonxai.xsd.Group, SymbolTableRef<LinkedList<Define>>> xsdGroupDefineRefMap, HashMap<Type, SymbolTableRef<LinkedList<Define>>> xsdTypeDefineRefMap, LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation) {
        super(xmlSchema, relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap);
        this.typeInheritanceInformation = typeInheritanceInformation;
    }

    /**
     * Convert a given simpleType to its corresponding RELAX NG pattern
     * structure
     * @param simpleType        The source of the conversion in XML XSDSchema
     * @param refAllowed        Setting for the convecreation of defines/refs
     * @return Pattern          The result of the conversion in RELAX NG
     */
    public Pattern convert(SimpleType simpleType, boolean refAllowed) {
        return this.convert(simpleType, refAllowed, true, "");
    }

    public Pattern convert(SimpleType simpleType, boolean refAllowed, boolean convertAttributes) {
        return this.convert(simpleType, refAllowed, convertAttributes, "");
    }

    /**
     * Convert a given simpleType to its corresponding RELAX NG pattern
     * structure with some more settings
     * @param simpleType            The source of the conversion in XML XSDSchema
     * @param refAllowed            Setting for the convecreation of defines/refs
     * @param convertAttributes     Setting for the conversion of attributes
     * @return Pattern              The result of the conversion in RELAX NG
     */
    public Pattern convert(SimpleType simpleType, boolean refAllowed, boolean convertAttributes, String parentTypeName) {

        if (parentTypeName.equals(simpleType.getName())) {
            refAllowed = false;
        }

        Pattern returnPattern = null;
        Pattern inheritancePattern = null;
        String typeDefineName = null;

        if (this.xsdTypeDefineRefMap.containsKey(simpleType) && refAllowed) {
            Ref ref = new Ref(this.xsdTypeDefineRefMap.get(simpleType), (Grammar) this.relaxng.getRootPattern());
            ref.setRefName(this.xsdTypeDefineRefMap.get(simpleType).getKey());
            returnPattern = ref;
        } else {

            // Check XML XSDSchema datatype
            if (this.isXMLSchemaBuiltInType(simpleType.getNamespace(), simpleType.getLocalName())) {
                Data data = new Data(simpleType.getLocalName());
                returnPattern = data;
            } else {

                if (!simpleType.isAnonymous() && refAllowed) {
                    // ComplexType is not anonymous --> prepare a define in the root grammar object
                    typeDefineName = this.registerDummyInDefineRefMap(simpleType);
                    this.xsdTypeDefineRefMap.put(simpleType, ((Grammar) this.relaxng.getRootPattern()).getDefineLookUpTable().getReference(typeDefineName));
                }

                if (simpleType.getInheritance() != null) {
                    inheritancePattern = this.simplifyPatternStructure(this.convertSimpleContentInheritance(simpleType.getInheritance(), refAllowed, convertAttributes, simpleType.getName()));
                }

                if (inheritancePattern != null) {
                    returnPattern = inheritancePattern;
                }

                // The final attribute can not be translated to RELAX NG, because there is no inheritance
                // and thus it can not be prohibited to use the corresponding pattern as "base" for an inherited structure.
                // (simpleType.getFinalModifiers();)

                // Handling of a not anonymous type
                if (!simpleType.isAnonymous() && refAllowed) {
                    // ComplexType is not anonymous --> register the generated pattern in the define
                    Ref ref = this.setPatternToDefine(typeDefineName, returnPattern);
                    returnPattern = ref;
                }
            }
        }
        return returnPattern;
    }

    /**
     * Convert a given simpleType inheritance object to a RELAX NG pattern
     * structure
     * @param inheritance       Object (SimpleContentInheritance, SimpleTypeInheritance) holding the inheritance structure
     * @param refAllowed        Setting for the creation of defines/refs
     * @param convertAttributes Setting for the conversion of attributes
     * @return Pattern          The result of the conversion in RELAX NG
     */
    public Pattern convertSimpleContentInheritance(Object inheritance, boolean refAllowed, boolean convertAttributes, String parentTypeName) {

        Pattern returnPattern = null;

        // Check if the inheritance object is null
        if (inheritance != null) {
            if (inheritance instanceof SimpleContentExtension) {

                // Case: ComplexType -> SimpleContentType -> inheritance
                SimpleContentExtension simpleContentExtension = (SimpleContentExtension) inheritance;

                Pattern basePattern = null;

                // Switch over the type if the inheritance base
                if (simpleContentExtension.getBase() instanceof SimpleType) {
                    // Case "SimpleType":
                    basePattern = this.convert((SimpleType) simpleContentExtension.getBase(), true, false, parentTypeName);
                } else if (simpleContentExtension.getBase() instanceof ComplexType) {
                    // Case "ComplexType":

                    boolean localRefAllowed = true;
                    if (parentTypeName.equals(((ComplexType) simpleContentExtension.getBase()).getName())) {
                        localRefAllowed = false;
                    }

                    basePattern = this.complexTypeConverter.convert((ComplexType) simpleContentExtension.getBase(), localRefAllowed, convertAttributes, parentTypeName);
                }

                // Group for the result
                Group group = new Group();
                group.addPattern(simplifyPatternStructure(basePattern));

                // Conversion of attached attributes
                if (convertAttributes) {
                    for (Iterator<AttributeParticle> it = simpleContentExtension.getAttributes().iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        // Call convert-method from the attributeParticleConverter
                        Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);
                        group.addPattern(simplifyPatternStructure(attributePattern));
                    }
                }
                returnPattern = simplifyPatternStructure(group);

            } else if (inheritance instanceof SimpleContentRestriction) {

                // Case: ComplexType -> SimpleContentType or SimpleType -> inheritance
                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) inheritance;

                // Group for the base conversion result
                Group basePattern = new Group();

                // Conversion of attributes
                if (convertAttributes && simpleContentRestriction.getAttributes() != null && !simpleContentRestriction.getAttributes().isEmpty()) {
                    for (Iterator<AttributeParticle> it = simpleContentRestriction.getAttributes().iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        // Call convert-method from the attributeParticleConverter
                        Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);
                        basePattern.addPattern(simplifyPatternStructure(attributePattern));
                    }
                    convertAttributes = false;
                }

                // Switch over the type if the inheritance base
                if (simpleContentRestriction.getBase() instanceof SimpleType) {
                    // Case "SimpleType":
                    basePattern.addPattern(this.convert((SimpleType) simpleContentRestriction.getBase(), !this.hasFacetts(simpleContentRestriction), convertAttributes, parentTypeName));
                } else if (simpleContentRestriction.getBase() instanceof ComplexType) {
                    // Case "ComplexType":
                    basePattern.addPattern(this.complexTypeConverter.convert((ComplexType) simpleContentRestriction.getBase(), false, convertAttributes, parentTypeName));
                }

                // If the current restriction has an anonymous type within its content
                if (simpleContentRestriction.getAnonymousSimpleType() != null) {
                    // Conversion of the content anonymous type
                    boolean localRefAllowed = !this.hasFacetts(simpleContentRestriction) && refAllowed;
                    if (parentTypeName.equals(((SimpleType) simpleContentRestriction.getAnonymousSimpleType()).getName())) {
                        localRefAllowed = false;
                    }
                    basePattern.addPattern(this.convert((SimpleType) simpleContentRestriction.getAnonymousSimpleType(), localRefAllowed, convertAttributes, parentTypeName));
                }

                // Simplify the conversion result of the base type
                if (basePattern != null) {
                    returnPattern = simplifyPatternStructure(basePattern);
                }

                // If result is of type "Data" - handle the used XML XSDSchema facets
                if (returnPattern instanceof Data) {
                    Data data = (Data) returnPattern;

                    // Case "enumeration":
                    if (simpleContentRestriction.getEnumeration() != null && !simpleContentRestriction.getEnumeration().isEmpty()) {
                        Choice choice = new Choice();
                        for (Iterator<String> it = simpleContentRestriction.getEnumeration().iterator(); it.hasNext();) {
                            String valueString = it.next();
                            Value value = new Value(valueString);
                            value.setType("string");
                            choice.addPattern(value);
                        }
                        returnPattern = choice;
                    }

                    // Case "fractionDigits":
                    if (simpleContentRestriction.getFractionDigits() != null) {
                        Param param = new Param("fractionDigits");
                        param.setContent(simpleContentRestriction.getFractionDigits().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "length":
                    if (simpleContentRestriction.getLength() != null) {
                        Param param = new Param("length");
                        param.setContent(simpleContentRestriction.getLength().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxExclusive":
                    if (simpleContentRestriction.getMaxExclusive() != null) {
                        Param param = new Param("maxExclusive");
                        param.setContent(simpleContentRestriction.getMaxExclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxInclusive":
                    if (simpleContentRestriction.getMaxInclusive() != null) {
                        Param param = new Param("maxInclusive");
                        param.setContent(simpleContentRestriction.getMaxInclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxLength":
                    if (simpleContentRestriction.getMaxLength() != null) {
                        Param param = new Param("maxLength");
                        param.setContent(simpleContentRestriction.getMaxLength().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxInclusive":
                    if (simpleContentRestriction.getMinExclusive() != null) {
                        Param param = new Param("minExclusive");
                        param.setContent(simpleContentRestriction.getMinExclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "minInclusive":
                    if (simpleContentRestriction.getMinInclusive() != null) {
                        Param param = new Param("minInclusive");
                        param.setContent(simpleContentRestriction.getMinInclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "minLength":
                    if (simpleContentRestriction.getMinLength() != null) {
                        Param param = new Param("minLength");
                        param.setContent(simpleContentRestriction.getMinLength().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "pattern":
                    if (simpleContentRestriction.getPattern() != null) {
                        Param param = new Param("pattern");
                        String patternString = simpleContentRestriction.getPattern().getValue().toString();
                        // optional: escaping of [-+]? minus character in pattern [...] braces.
                        param.setContent(patternString);
                        data.addParam(param);
                    }

                    // Case "totalDigits":
                    if (simpleContentRestriction.getTotalDigits() != null) {
                        Param param = new Param("totalDigits");
                        param.setContent(simpleContentRestriction.getTotalDigits().getValue().toString());
                        data.addParam(param);
                    }

                    // There is no corresponding feature in RELAX NG for the XML XSDSchema "whitespace" facet!
                    // (simpleContentRestriction.getWhitespace();)
                }

            } else if (inheritance instanceof SimpleContentUnion) {

                // Case: SimpleType -> inheritance
                SimpleContentUnion simpleContentUnion = (SimpleContentUnion) inheritance;

                LinkedList<Pattern> memberTypePatterns = new LinkedList<Pattern>();

                // Handle all membertypes of the union
                for (Iterator<SymbolTableRef<Type>> it = simpleContentUnion.getAllMemberTypes().iterator(); it.hasNext();) {
                    SymbolTableRef<Type> strType = it.next();
                    if (strType.getReference() instanceof SimpleType) {

                        boolean localRefAllowed = true;
                        if (parentTypeName.equals(((SimpleType) strType.getReference()).getName())) {
                            localRefAllowed = false;
                        }

                        memberTypePatterns.add(this.convert((SimpleType) strType.getReference(), localRefAllowed, convertAttributes, parentTypeName));
                    }
                }

                if (!memberTypePatterns.isEmpty()) {

                    if (memberTypePatterns.size() > 1) {
                        Choice choice = new Choice();

                        for (Iterator<Pattern> it = memberTypePatterns.iterator(); it.hasNext();) {
                            Pattern pattern = it.next();
                            choice.addPattern(pattern);
                        }
                        returnPattern = simplifyPatternStructure(choice);
                    } else {
                        returnPattern = simplifyPatternStructure(memberTypePatterns.getFirst());
                    }
                }

            } else if (inheritance instanceof SimpleContentList) {

                // Case: SimpleType -> inheritance
                SimpleContentList simpleContentList = (SimpleContentList) inheritance;

                /**
                 * Prohibited paths in the resulting RELAX NG schema:
                 * list//list
                 * list//ref
                 * list//attribute
                 * list//text
                 * list//interleave
                 */
                List list = new List();

                ZeroOrMore zeroOrMore = new ZeroOrMore();

                // Handle the base type of the list
                boolean localRefAllowed = false;
                zeroOrMore.addPattern(this.convert((SimpleType) simpleContentList.getBaseSimpleType(), localRefAllowed, false, parentTypeName));

                list.addPattern(zeroOrMore);

                returnPattern = simplifyPatternStructure(list);
            }
        } else {
            // Exception for a null inheritance
            Data data = new Data("string");
            returnPattern = data;
        }

        return simplifyPatternStructure(returnPattern);
    }

    /**
     * Check if a restriction has facets attached
     * @param simpleContentRestriction      Source for the check
     * @return boolean                      True, if there is any facet
     */
    private boolean hasFacetts(SimpleContentRestriction simpleContentRestriction) {
        boolean returnValue = false;
        if ((simpleContentRestriction.getEnumeration() != null && !simpleContentRestriction.getEnumeration().isEmpty()) || simpleContentRestriction.getFractionDigits() != null || simpleContentRestriction.getLength() != null || simpleContentRestriction.getMaxExclusive() != null || simpleContentRestriction.getMaxInclusive() != null || simpleContentRestriction.getMaxLength() != null || simpleContentRestriction.getMinExclusive() != null || simpleContentRestriction.getMinInclusive() != null || simpleContentRestriction.getMinLength() != null || simpleContentRestriction.getPattern() != null || simpleContentRestriction.getTotalDigits() != null) {
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Getter for the complexTypeConverter
     * @return ComplexTypeConverter
     */
    public ComplexTypeConverter getComplexTypeConverter() {
        return complexTypeConverter;
    }

    /**
     * Setter for the ComplexTypeConverter
     * @param complexTypeConverter
     */
    public void setComplexTypeConverter(ComplexTypeConverter complexTypeConverter) {
        this.complexTypeConverter = complexTypeConverter;
    }

    /**
     * Getter for the attributeParticleConverter
     * @return AttributeParticleConverter
     */
    public AttributeParticleConverter getAttributeParticleConverter() {
        return attributeParticleConverter;
    }

    /**
     * Setter for the attributeParticleConverter
     * @param attributeParticleConverter
     */
    public void setAttributeParticleConverter(AttributeParticleConverter attributeParticleConverter) {
        this.attributeParticleConverter = attributeParticleConverter;
    }

    /**
     * Handling of simpleType substitutions from XML XSDSchema to offer a choice of
     * all allowed substitutions in the RELAX NG schema, too.
     * @param simpleType            simpleType as parent of the type to check for substitutions
     * @param pattern               Pattern structure containing a choice of all
     *                              converted elements defined in the
     *                              substitution
     * @param convertAttributes     Setting to allow/disallow the conversion of
     *                              attached attributes
     * @return Pattern              The result of the substitution handling
     */
    public Pattern handleSubstitutions(eu.fox7.bonxai.xsd.SimpleType simpleType, Pattern pattern, boolean convertAttributes) {
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
        if (XSD2RelaxNGConverter.HANDLE_SUBSTITUTIONS && this.typeInheritanceInformation.containsKey(simpleType)) {
            LinkedHashSet<eu.fox7.bonxai.xsd.Type> typeSubstitutions = this.typeInheritanceInformation.get(simpleType);

            if (typeSubstitutions != null && !typeSubstitutions.isEmpty()) {

                Choice choice = new Choice();
                choice.addPattern(pattern);
                for (Iterator<eu.fox7.bonxai.xsd.Type> it = typeSubstitutions.iterator(); it.hasNext();) {
                    eu.fox7.bonxai.xsd.Type xsdSubstitutionType = it.next();
                    Pattern substitutionTypePattern = this.convert((SimpleType) xsdSubstitutionType, true, convertAttributes, simpleType.getName());
                    choice.addPattern(substitutionTypePattern);
                }
                pattern = simplifyPatternStructure(choice);
            }
        }
        return pattern;
    }
}
