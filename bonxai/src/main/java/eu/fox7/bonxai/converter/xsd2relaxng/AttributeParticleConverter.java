package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.AttributeGroupRef;
import eu.fox7.bonxai.xsd.AttributeParticle;
import eu.fox7.bonxai.xsd.AttributeRef;
import eu.fox7.bonxai.xsd.AttributeUse;
import eu.fox7.bonxai.xsd.XSDSchema;

import java.util.Iterator;

/**
 * Class AttributeParticleConverter
 *
 * This class provides the conversion of any type of attributeParticle from
 * XML XSDSchema to its corresponding counterparts in RELAX NG
 *
 * @author Lars Schmidt
 */
public class AttributeParticleConverter extends ConverterBase {

    // Attribute holding the simpleTypeConverter for conversion of the type of
    // an attribute
    private SimpleTypeConverter simpleTypeConverter;

    /**
     * Constructor of class AttributeParticleConverter
     * @param xmlSchema     The source XML XSDSchema object for the conversion
     *                      process
     * @param relaxng       The target RELAX NG object for the conversion
     *                      process
     * @param simpleTypeConverter   The simpleTypeConverter, which is used to
     *                              generate RELAX NG pattern as conversion
     *                              results of XML XSDSchema simpleTypes
     *
     */
    public AttributeParticleConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, SimpleTypeConverter simpleTypeConverter) {
        super(xmlSchema, relaxng);
        this.simpleTypeConverter = simpleTypeConverter;
    }

    /**
     * Method: convert, this is the main method of this class and starts the
     *                  conversion progress for a given attributeParticle
     * @param attributeParticle     The source of the conversion process
     * @return Pattern              The resulting RELAX NG pattern after the
     *                              conversion
     */
    public Pattern convert(AttributeParticle attributeParticle) {

        // Prepare a return variable
        Pattern returnPattern = null;

        // Switch over all possible kinds of attributePattern from XML XSDSchema
        if (attributeParticle instanceof eu.fox7.bonxai.xsd.Attribute) {

            // Case: attribute

            eu.fox7.bonxai.xsd.Attribute xsdAttribute = (eu.fox7.bonxai.xsd.Attribute) attributeParticle;

            // Initialize a resulting RELAX NG attribute representation
            Attribute attribute = new Attribute();

            // Set the name and the namespace corresponding to the values in
            // XML XSDSchema
            attribute.setNameAttribute(xsdAttribute.getLocalName());

            // Set the namespace
            attribute.setAttributeNamespace(xsdAttribute.getNamespace());
                        
            // All conversion results (from simpleType, other constraints of the
            // XML XSDSchema attribute) will be collected within this group pattern
            Group contentPattern = new Group();

            // Handle the fixed-attribute of the XML XSDSchema attribute element
            if (xsdAttribute.getFixed() != null) {
                Value value = new Value(xsdAttribute.getFixed());
                // The type is set to string for all possible fixed-values
                value.setType("string");
                contentPattern.addPattern(value);
            } else {
                // Handle the given simpleType
                if (xsdAttribute.getSimpleType() != null) {

                    Pattern simpleTypePattern = this.simpleTypeConverter.convert(xsdAttribute.getSimpleType(), true, false);
                    if (!xsdAttribute.getSimpleType().isAnonymous() && !this.isXMLSchemaBuiltInType(xsdAttribute.getSimpleType().getNamespace(), xsdAttribute.getSimpleType().getLocalName())) {
                        simpleTypePattern = this.simpleTypeConverter.handleSubstitutions(xsdAttribute.getSimpleType(), simpleTypePattern, false);
                    }
                    contentPattern.addPattern(simpleTypePattern);
                }
                // If there is no simpleType given, the XML XSDSchema attribute has
                // the anySimpleType. This is equal to the semantics in
                // RELAX NG, so there is no conversion needed in this case.
            }

            // There is no corresponding feature in RELAX NG for the default
            // value of an attribute
            // (xsdAttribute.getDefault();)

            // There is no corresponding feature in RELAX NG for the form of an 
            // attribute.
            // In RELAX NG all possibilities of defining an element or attribute
            // in an instance are valid. There is no constraint on the
            // qualification of these tags.
            // (xsdAttribute.getForm();)

            // Handle the attribute use
            if (xsdAttribute.getUse() == null || xsdAttribute.getUse().equals(AttributeUse.Optional)) {

                // Case: optional --> frame the result with an "optional" pattern
                Optional optional = new Optional();
                if (!contentPattern.getPatterns().isEmpty()) {
                    attribute.setPattern(simplifyPatternStructure(contentPattern));
                }
                optional.addPattern(attribute);

                returnPattern = optional;

            } else if (xsdAttribute.getUse().equals(AttributeUse.Prohibited)) {

                // Case: prohibited --> notAllowed as child pattern
                NotAllowed notAllowed = new NotAllowed();
                contentPattern.addPattern(notAllowed);
                attribute.setPattern(simplifyPatternStructure(contentPattern));

                returnPattern = attribute;

            } else if (xsdAttribute.getUse().equals(AttributeUse.Required)) {

                // Case: required --> nothing has to be done here.
                // In RELAX NG all patterns are required as standard behaviour.
                if (!contentPattern.getPatterns().isEmpty()) {
                    attribute.setPattern(simplifyPatternStructure(contentPattern));
                }
                returnPattern = attribute;
            }

        } else if (attributeParticle instanceof eu.fox7.bonxai.xsd.AttributeGroup) {

            // Case: attributeGroup

            eu.fox7.bonxai.xsd.AttributeGroup attributeGroup = (AttributeGroup) attributeParticle;

            // Check if already converted
            if (this.xsdAttributeGroupDefineRefMap.containsKey(attributeGroup)) {
                Ref ref = new Ref(this.xsdAttributeGroupDefineRefMap.get(attributeGroup), (Grammar) this.relaxng.getRootPattern());
                ref.setRefName(this.xsdAttributeGroupDefineRefMap.get(attributeGroup).getKey());
                returnPattern = ref;
            } else {

                // Prepare a define in the root grammar object for the attributeGroup
                String attributeGroupDefineName = this.registerDummyInDefineRefMap(attributeGroup);
                this.xsdAttributeGroupDefineRefMap.put(attributeGroup, ((Grammar) this.relaxng.getRootPattern()).getDefineLookUpTable().getReference(attributeGroupDefineName));

                Group group = new Group();

                for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
                    AttributeParticle innerAttributeParticle = it.next();

                    Pattern attributePattern = this.convert(innerAttributeParticle);
                    group.addPattern(simplifyPatternStructure(attributePattern));
                }

                if (group.getPatterns().isEmpty()) {
                    group.addPattern(new Empty());
                }

                // Update the define for this newly generated pattern as conversion
                // result of the given XML XSDSchema group.
                Ref ref = this.setPatternToDefine(attributeGroupDefineName, simplifyPatternStructure(group));
                returnPattern = ref;
            }
        } else if (attributeParticle instanceof eu.fox7.bonxai.xsd.AttributeGroupRef) {

            // Case: attributeGroupRef

            eu.fox7.bonxai.xsd.AttributeGroupRef attributeGroupRef = (AttributeGroupRef) attributeParticle;
            returnPattern = this.convert(attributeGroupRef.getAttributeGroup());

        } else if (attributeParticle instanceof eu.fox7.bonxai.xsd.AttributeRef) {

            // Case: attributeRef

            eu.fox7.bonxai.xsd.AttributeRef attributeRef = (AttributeRef) attributeParticle;

            if (this.xsdAttributeDefineRefMap.containsKey(attributeRef.getAttribute())) {
                Ref ref = new Ref(this.xsdAttributeDefineRefMap.get(attributeRef.getAttribute()), (Grammar) this.relaxng.getRootPattern());
                ref.setRefName(this.xsdAttributeDefineRefMap.get(attributeRef.getAttribute()).getKey());
                returnPattern = ref;
            } else {
                String attributeDefineName = this.registerDummyInDefineRefMap(attributeRef.getAttribute());

                // Call the conversion method again for the nested attribute
                Pattern pattern = this.convert(attributeRef.getAttribute());

                Ref ref = this.setPatternToDefine(attributeDefineName, simplifyPatternStructure(pattern));
                returnPattern = ref;
            }
        } else if (attributeParticle instanceof eu.fox7.bonxai.common.AnyAttribute) {

            // Case: anyAttribute

            eu.fox7.bonxai.common.AnyAttribute anyAttribute = (AnyAttribute) attributeParticle;
            returnPattern = this.generateAnyAttributePattern(anyAttribute);
        }

        // Return the generated pattern structure
        return returnPattern;

    }
}
