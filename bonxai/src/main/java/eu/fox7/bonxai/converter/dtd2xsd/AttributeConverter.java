package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.EnumerationOrNotationTokensEmtpyException;
import eu.fox7.bonxai.dtd.*;
import eu.fox7.bonxai.xsd.AttributeUse;
import eu.fox7.bonxai.xsd.SimpleContentRestriction;
import eu.fox7.bonxai.xsd.SimpleType;
import eu.fox7.bonxai.xsd.Type;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.XSDSchema.Qualification;

import java.util.LinkedList;

/**
 * Class for the conversion of a DTD attribute to its XML XSDSchema counterpart.
 * @author Lars Schmidt
 */
public class AttributeConverter extends ConverterBase {

    private DocumentTypeDefinition dtd;
    private Qualification qualification;
    private Element dtdElement;

    /**
     * Constructor of class AttributeConverter
     * This class handles the conversion process from a DTD attribute to the
     * corresponding XML XSDSchema attribute.
     * @param xmlSchema     The resulting XML XSDSchema object structure schema
     * @param targetNamespace       The targetNamespace of the resulting schema
     * @param namespaceAware        Handle the correct namespaces according to leading abbreviation of dtd names (i.e. "abc:testname") 
     */
    public AttributeConverter(XSDSchema xmlSchema, IdentifiedNamespace targetNamespace, boolean namespaceAware) {
        super(xmlSchema, targetNamespace, namespaceAware);
        this.qualification = Qualification.qualified;
        this.qualification = null;
    }

    /**
     * Method "convert"
     *
     * This method is the central method of this class.
     * By calling this method the conversion process will be started.
     *
     * @param dtd       The source DTD object structure for the conversion
     * @param dtdElement        The current DTD element, which is the parent of the current DTD attribute
     * @param dtdAttribute      The current DTD attribute as source of the conversion
     * @return eu.fox7.bonxai.xsd.Attribute        The resulting XSD attribute object
     * @throws Exception        Some different exceptions can be thrown in sub-methods of this method.
     */
    public eu.fox7.bonxai.xsd.Attribute convert(DocumentTypeDefinition dtd, Element dtdElement, Attribute dtdAttribute) throws Exception {
        // Initialize the variables of this class.
        this.dtd = dtd;
        this.dtdElement = dtdElement;

        // Declaration of necessary variables for the conversion progress.
        eu.fox7.bonxai.xsd.Attribute attribute = null;
        String fixedValue = null;
        AttributeUse attributeUse = null;
        String defaultValue = null;
        Annotation annotation = null;
        String attributeName = null;
        String typeName = null;
        SymbolTableRef<Type> attributeTypeRef = null;
        boolean attributeTypeAttribute = true;

        // Generate a full qualified name for the resulting XSD attribute
        attributeName = this.generateXSDFQName(dtdAttribute.getName());

        // If the targetNamespace object is null, the empty-String will be set as targetNamespaceUri.
        String targetUri = ((this.targetNamespace == null) ? "" : this.targetNamespace.getUri());


        // Handle the type of this DTD attribute
        switch (dtdAttribute.getType()) {
            // Unique ID or name. Must be a valid XML name.
            case ID:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE.toString() + "}" + "ID";
                
                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
                break;
            // Represents the value of an ID attribute of another element.
            case IDREF:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "IDREF";

                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
                break;
            // Represents multiple IDs of elements, separated by whitespace.
            case IDREFS:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "IDREFS";

                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
                break;
            // The name of an entity (which must be declared in the DTD)
            case ENTITY:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "ENTITY";

                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
                break;
            // A list of entity names, separated by whitespaces. (All entities must be declared in the DTD)
            case ENTITIES:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "ENTITIES";

                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
                break;
            // A valid XML name.
            case NMTOKEN:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "NMTOKEN";

                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
                break;
            // A list of valid XML names separated by whitespaces.
            case NMTOKENS:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "NMTOKENS";

                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
                break;
            // Character Data (text that doesn't contain markup)
            case CDATA:
                // Full qualified name of the type
                typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "string";

                // Generate a corresponding notationBaseSimpleType
                attributeTypeRef = generateNotationBaseSimpleType(typeName);
               break;
            // A list of notation names (which must be declared in the DTD) seperated by the pipe operator (x|y).
            case NOTATION:
                // Full qualified name of the anonymous notationBaseSimpleType
                typeName = "{" + targetUri + "}" + this.dtdElement.getName() + "-" + dtdAttribute.getName();

                // Variable holding the reference of the base type of the resulting simple content restriction
                SymbolTableRef<Type> notationRestrictionBaseTypeRef = null;
                // Full qualified name of the base type
                String notationBaseTypeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "token"; // xs:NOTATION How can notation elements in XSD be written?
                // Generate a base notationBaseSimpleType
                if (this.xmlSchema.getTypeSymbolTable().hasReference(notationBaseTypeName)) {
                    // Case "type exists already":
                    notationRestrictionBaseTypeRef = this.xmlSchema.getTypeSymbolTable().getReference(notationBaseTypeName);
                } else {
                    // Case "new type":
                    SimpleType notationBaseSimpleType = new SimpleType(notationBaseTypeName, null, true);
                    notationBaseSimpleType.setDummy(false);
                    notationBaseSimpleType.setIsAnonymous(false);
                    notationRestrictionBaseTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(notationBaseTypeName, notationBaseSimpleType);
                }

                // SimpleType inheritance
                SimpleContentRestriction notationSimpleContentRestriction = new SimpleContentRestriction(notationRestrictionBaseTypeRef);

                if (dtdAttribute.getEnumerationOrNotationTokens() == null || dtdAttribute.getEnumerationOrNotationTokens().isEmpty()) {
                    throw new EnumerationOrNotationTokensEmtpyException(attributeName, typeName);
                }

                if (dtdAttribute.getEnumerationOrNotationTokens() != null) {
                    notationSimpleContentRestriction.addEnumeration(new LinkedList<String>(dtdAttribute.getEnumerationOrNotationTokens()));
                } 

                SimpleType notationAttributeSimpleType = new SimpleType(typeName, notationSimpleContentRestriction, true);
                notationAttributeSimpleType.setDummy(false);

                attributeTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(typeName, notationAttributeSimpleType);
                attributeTypeAttribute = false;
                break;
            // A list of values seperated by the pipe operator (x|y). The value of the attribute must be one from this list.
            case ENUMERATION:

                // Full qualified name of the anonymous notationBaseSimpleType
                typeName = "{" + targetUri + "}" + this.dtdElement.getName() + "-" + dtdAttribute.getName();

                // Variable holding the reference of the base type of the resulting simple content restriction
                SymbolTableRef<Type> restrictionBaseTypeRef = null;
                // Full qualified name of the base type
                String baseTypeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "token";
                // Generate a base notationBaseSimpleType
                if (this.xmlSchema.getTypeSymbolTable().hasReference(baseTypeName)) {
                    // Case "type exists already":
                    restrictionBaseTypeRef = this.xmlSchema.getTypeSymbolTable().getReference(baseTypeName);
                } else {
                    // Case "new type":
                    SimpleType simpleType = new SimpleType(baseTypeName, null, true);
                    simpleType.setDummy(false);
                    simpleType.setIsAnonymous(false);
                    restrictionBaseTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(baseTypeName, simpleType);
                }

                // SimpleType inheritance
                SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(restrictionBaseTypeRef);

                if (dtdAttribute.getEnumerationOrNotationTokens() == null || dtdAttribute.getEnumerationOrNotationTokens().isEmpty()) {
                    throw new EnumerationOrNotationTokensEmtpyException(attributeName, typeName);
                }
                
                if (dtdAttribute.getEnumerationOrNotationTokens() != null) {
                    simpleContentRestriction.addEnumeration(new LinkedList<String>(dtdAttribute.getEnumerationOrNotationTokens()));
                } 

                SimpleType enumerationAttributeSimpleType = new SimpleType(typeName, simpleContentRestriction, true);
                enumerationAttributeSimpleType.setDummy(false);

                attributeTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(typeName, enumerationAttributeSimpleType);
                attributeTypeAttribute = false;
                break;
        }

        // Handle the AttributeDefaultPresence of this DTD attribute
        if (dtdAttribute.getAttributeDefaultPresence() != null) {
            if (dtdAttribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.FIXED)) {
                // fixed
                fixedValue = dtdAttribute.getValue();
            } else if (dtdAttribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.REQUIRED)) {
                // attributeUse = REQUIRED
                attributeUse = AttributeUse.Required;
            } else if (dtdAttribute.getAttributeDefaultPresence().equals(Attribute.AttributeDefaultPresence.IMPLIED)) {
                // attributeUse = IMPLIED
                attributeUse = AttributeUse.Optional;
            }
        }
        // Handle the default value of this DTD attribute.
        // This is only possible, if there is no fixed value set.
        if (fixedValue == null && dtdAttribute.getValue() != null) {
            // default
            defaultValue = dtdAttribute.getValue();
        }

        attribute = new eu.fox7.bonxai.xsd.Attribute(attributeName, attributeTypeRef, defaultValue, fixedValue, attributeUse, false, this.qualification, annotation);
        attribute.setTypeAttr(attributeTypeAttribute);

        return attribute;
    }
    
    private SymbolTableRef<Type> generateNotationBaseSimpleType(String typeName) {
    	SymbolTableRef<Type> attributeTypeRef = null;
    	if (this.xmlSchema.getTypeSymbolTable().hasReference(typeName)) {
            // Case "type exists already":
            attributeTypeRef = this.xmlSchema.getTypeSymbolTable().getReference(typeName);
        } else {
            // Case "new type":
            SimpleType simpleType = new SimpleType(typeName, null);
            simpleType.setDummy(false);
            attributeTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(typeName, simpleType);
        }
    	return attributeTypeRef;
    }
}
