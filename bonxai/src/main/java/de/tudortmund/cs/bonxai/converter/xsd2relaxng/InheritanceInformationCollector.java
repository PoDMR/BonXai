package de.tudortmund.cs.bonxai.converter.xsd2relaxng;

import de.tudortmund.cs.bonxai.xsd.ComplexContentExtension;
import de.tudortmund.cs.bonxai.xsd.ComplexContentRestriction;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.ComplexTypeInheritanceModifier;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentExtension;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import de.tudortmund.cs.bonxai.xsd.SimpleContentType;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Collect information about the inheritance structure of types in the current
 * XML XSDSchema document.
 *
 * Find all derived Types of a base type, regarding to the block/blockDefault
 * attributes. This is necessary for the correct handling of the XML XSDSchema
 * inheritance feature (restriction, extension) in case of the conversion to
 * RELAX NG.
 * In an instance it is possible to choose the type via the xsi:type attribute.
 * Types declared in external schemas have to be checked in this manner, too.
 * 
 * @author Lars Schmidt
 */
public class InheritanceInformationCollector {

    private LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation = new LinkedHashMap<Type, LinkedHashSet<Type>>();
    private HashSet<XSDSchema> alreadyHandledSchemas;

    /**
     * Constructor of class InheritanceInformationCollector
     */
    public InheritanceInformationCollector() {
        this.alreadyHandledSchemas = new HashSet<XSDSchema>();
    }

    /**
     * Collect necessary information about the inheritance hierarchy of all
     * types
     * @param xmlSchema     Source of the conversion from XML XSDSchema to RELAX NG
     */
    public void collectInformation(XSDSchema xmlSchema) {

        // Add the current XML schema object to the set of already seen schemas to avoid a duplicate processing
        this.alreadyHandledSchemas.add(xmlSchema);

        // Walk over all types within the current typeSymbolTable
        for (Iterator<Type> it = xmlSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
            Type type = it.next();
            if (!type.isAnonymous() && !type.isDummy()) {
                if (type instanceof ComplexType) {

                    // Case "complexType":
                    ComplexType complexType = (ComplexType) type;
                    if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {

                        // Case "complexContent":
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                        if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {

                            // Case "extension":
                            ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(complexContentExtension.getBase(), ComplexTypeInheritanceModifier.Extension)) {
                                this.collectInformation(type, complexContentExtension.getBase());
                            }
                        } else if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentRestriction) {

                            // Case "restriction":
                            ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(complexContentRestriction.getBase(), ComplexTypeInheritanceModifier.Restriction)) {
                                this.collectInformation(type, complexContentRestriction.getBase());
                            }
                        }
                    } else if (complexType.getContent() != null && complexType.getContent() instanceof SimpleContentType) {

                        // Case "simpleContent":
                        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                        if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                            // Case "extension":
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(simpleContentExtension.getBase(), ComplexTypeInheritanceModifier.Extension)) {
                                this.collectInformation(type, simpleContentExtension.getBase());
                            }
                        } else if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentRestriction) {

                            // Case "restriction":
                            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(simpleContentRestriction.getBase(), ComplexTypeInheritanceModifier.Restriction)) {
                                this.collectInformation(type, simpleContentRestriction.getBase());
                            }
                        }
                    }
                } else if (type instanceof SimpleType) {

                    // Case "simpleType":
                    SimpleType simpleType = (SimpleType) type;
                    if (simpleType.getInheritance() != null && simpleType.getInheritance() instanceof SimpleContentRestriction) {

                        // Case "restriction":
                        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                        this.collectInformation(type, simpleContentRestriction.getBase());
                    }
                }
            }
        }

        // Walk trough all foreignSchemas
        if (xmlSchema.getForeignSchemas() != null && !xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadyHandledSchemas.contains(foreignSchema.getSchema())) {
                    collectInformation(foreignSchema.getSchema());
                }
            }
        }
    }

    /**
     * Collect information from two types by checking if the target type can be
     * used as a substitution for the sourceType. This method recurses trough
     * the given type inheritance structure. The remaining check if a type is a
     * allowed substitution is made in method
     * "checkInheritanceTypeAllowedInInstance"
     *
     * @param sourceType
     * @param targetType
     */
    private void collectInformation(Type sourceType, Type targetType) {
        if (!targetType.isAnonymous() && !targetType.isDummy()) {
            if (sourceType != null) {
                addToInheritanceInformation(targetType, sourceType);
                if (targetType instanceof ComplexType) {

                    // Case "complexType":
                    ComplexType complexType = (ComplexType) targetType;
                    if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {

                        // Case "complexContent":
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                        if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {

                            // Case "extension":
                            ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(complexContentExtension.getBase(), ComplexTypeInheritanceModifier.Extension)) {
                                this.collectInformation(sourceType, complexContentExtension.getBase());
                            }
                        } else if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentRestriction) {

                            // Case "restriction":
                            ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(complexContentRestriction.getBase(), ComplexTypeInheritanceModifier.Extension)) {
                                this.collectInformation(sourceType, complexContentRestriction.getBase());
                            }
                        }
                    } else if (complexType.getContent() != null && complexType.getContent() instanceof SimpleContentType) {

                        // Case "simpleContent":
                        SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                        if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                            // Case "extension":
                            SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(simpleContentExtension.getBase(), ComplexTypeInheritanceModifier.Extension)) {
                                this.collectInformation(sourceType, simpleContentExtension.getBase());
                            }
                        } else if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentRestriction) {

                            // Case "restriction":
                            SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                            if (checkInheritanceTypeAllowedInInstance(simpleContentRestriction.getBase(), ComplexTypeInheritanceModifier.Restriction)) {
                                this.collectInformation(sourceType, simpleContentRestriction.getBase());
                            }
                        }
                    }
                } else if (targetType instanceof SimpleType) {

                    // Case "simpleType":
                    SimpleType simpleType = (SimpleType) targetType;
                    if (simpleType.getInheritance() != null && simpleType.getInheritance() instanceof SimpleContentRestriction) {
                        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                        this.collectInformation(sourceType, simpleContentRestriction.getBase());
                    }
                }
            }
        }
    }

    /**
     * Mark a type as allowed substitution by putting it to a set with the
     * sourceType as the base
     * @param sourceType        Head of a type hierarchy
     * @param targetType        Derived Type, that can be used as substitution
     */
    private void addToInheritanceInformation(Type sourceType, Type targetType) {
        if (sourceType != null && targetType != null) {
            if (!sourceType.getName().equals(targetType.getName())) {
                LinkedHashSet<Type> typeSet = null;
                if (this.inheritanceInformation.get(sourceType) != null) {
                    typeSet = this.inheritanceInformation.get(sourceType);
                } else {
                    typeSet = new LinkedHashSet<Type>();
                }
                typeSet.add(targetType);
                this.inheritanceInformation.put(sourceType, typeSet);
            }
        }
    }

    /**
     * Getter for the already collected inheritanceInformation
     * @return LinkedHashMap<Type, LinkedHashSet<Type>>     inheritanceInformation
     */
    public LinkedHashMap<Type, LinkedHashSet<Type>> getInheritanceInformation() {
        return inheritanceInformation;
    }

    /**
     * Check if a type is an allowed substitution with a given ComplexTypeInheritanceModifier
     * @param type      Type that will be checked
     * @param ctim      ComplexTypeInheritanceModifier (block)
     * @return boolean  True, if the type can be used
     */
    private boolean checkInheritanceTypeAllowedInInstance(Type type, ComplexTypeInheritanceModifier ctim) {
        boolean returnBool = false;
        if (type instanceof ComplexType) {

            // Case "complexType":
            ComplexType complexType = (ComplexType) type;
            HashSet<ComplexTypeInheritanceModifier> blockModifier = complexType.getBlockModifiers();
            if (blockModifier == null || (blockModifier != null && !blockModifier.contains(ctim))) {
                returnBool = true;
            }
        } else if (type instanceof SimpleType) {

            // Case "simpleType":
            returnBool = true;
        }
        return returnBool;
    }

    /**
     * Getter for all type substitutions for a given XML XSDSchema element
     * @param element       XML XSDSchema element, with a type that could be a base of a type inheritance
     * @return LinkedHashSet<Type>  Set of all possible types, that can be used.
     */
    public LinkedHashSet<Type> getAllTypeSubstitutionsForElement(de.tudortmund.cs.bonxai.xsd.Element element) {
        if (this.inheritanceInformation != null) {
            LinkedHashSet<Type> typeSet = this.getAllTypeSubstitutionsForElement(element, this.inheritanceInformation);
            if (typeSet != null) {
                return typeSet;
            } else {
                return new LinkedHashSet<Type>();
            }
        } else {
            return new LinkedHashSet<Type>();
        }
    }

    /**
     * Calculate all allowed type substitutions for a given XML XSDSchema element
     * with the option to provide a inheritanceInformation base
     * @param element                   XML XSDSchema element, with a type that
     *                                  could be a base of a type inheritance
     * @param inheritanceInformation    The inheritance information base
     * @return LinkedHashSet<Type>      Set of all possible types, that can be
     *                                  used.
     */
    public LinkedHashSet<Type> getAllTypeSubstitutionsForElement(de.tudortmund.cs.bonxai.xsd.Element element, LinkedHashMap<Type, LinkedHashSet<Type>> inheritanceInformation) {
        LinkedHashSet<Type> typeSet = new LinkedHashSet<Type>();

        if (element != null) {
            if (element.getBlockModifiers() == null || element.getBlockModifiers().isEmpty()) {
                return inheritanceInformation.get(element.getType());
            } else {
                if (inheritanceInformation.containsKey(element.getType())) {
                    for (Type type: inheritanceInformation.get(element.getType())) {
                        SubstitutionGroupInformationCollector substitutionGroupInformationCollector = new SubstitutionGroupInformationCollector();

                        if (substitutionGroupInformationCollector.checkSubstitutionAllowedInInstance(element, type)) {
                            typeSet.add(type);
                        }
                    }
                }
            }
        } 

        return typeSet;
    }
}
