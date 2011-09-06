package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class SubstitutionGroupInformationCollector
 * 
 * Calculate all allowed substitution elements for the corresponding base element.
 * This is used by the XSD to RELAXNG Bonxai2XSDConverter.
 *
 * Element preprocessing: find all substitutionGroups and calculate valid
 * substitutions for an instance regarding to the block/blockDefault attributes.
 * Elements declared in external schemas have to be checked in this manner, too.
 * The default for the block-Attribute is set in the root of each schema
 * individually.
 *
 * This class requires a preprocessing, that sets the correct blockDefault-value
 * to all elements within the corresponding document. This is provided by class
 * "BlockFinalSpreadingHandler" in package "eu.fox7.bonxai.xsd.tools".
 *
 * @author Lars Schmidt
 */
public class SubstitutionGroupInformationCollector {

    private LinkedHashMap<Element, LinkedHashSet<Element>> substitutionGroupInformation = new LinkedHashMap<Element, LinkedHashSet<Element>>();
    private HashMap<SymbolTableRef<eu.fox7.bonxai.xsd.Element>, HashSet<SymbolTableRef<eu.fox7.bonxai.xsd.Element>>> substitutionElements = new HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>>();
    private HashSet<XSDSchema> alreadyHandledSchemas;

    /**
     * Constructor of class SubstitutionGroupInformationCollector
     *
     * Initialize the set of already handled schemas.
     */
    public SubstitutionGroupInformationCollector() {
        this.alreadyHandledSchemas = new HashSet<XSDSchema>();
    }

    /**
     * Collect information about the inheritance structure (substitutionGroup)
     * from a XML XSDSchema object and all of its external schemas and save
     * store all possible and allowed element substitutions in a map.
     * @param xmlSchema     Basis for the information collecting progress
     */
    public void collectInformation(XSDSchema xmlSchema) {

        // Calculate the data basis
        this.collectBaseInformation(xmlSchema);

        LinkedHashMap<Element, LinkedHashSet<Element>> substitutionGroupInformationResolved = new LinkedHashMap<Element, LinkedHashSet<Element>>();

        // Calculate all dependencies between multiple substitutionGroups
        for (Iterator<Element> it = substitutionGroupInformation.keySet().iterator(); it.hasNext();) {
            Element headElement = it.next();

            // Calculate all possible elements (even in nested substitutionGroups)
            LinkedHashSet<Element> foundElements = new LinkedHashSet<Element>();
            resolveSubstitutionsRecursivly(headElement, foundElements);


            // Sort the list of internal entites List
            LinkedList<Element> elements = new LinkedList<Element>(foundElements);
            Collections.sort(elements, new Comparator<Element>() {

                @Override
                public int compare(Element element1, Element element2) {
                    if (element1.getName() == null || element1.getName().equals("") || element2.getName() == null || element2.getName().equals("")) {
                        return 1;
                    }
                    return element1.getName().compareTo(element2.getName());
                }
            });

            LinkedHashSet<Element> sortedFoundElements = new LinkedHashSet<Element>(elements);

            // Add all found elements to the resolved map
            substitutionGroupInformationResolved.put(headElement, sortedFoundElements);
        }

        // Renew the old element map with the resolved version
        this.substitutionGroupInformation = substitutionGroupInformationResolved;
    }

    /**
     * Collect information as the basis of the check for allowed substitutions
     * from the head-element information provided in each schema-object:
     * substitutionElements
     * @param xmlSchema     Basis for the information collecting progress
     */
    private void collectBaseInformation(XSDSchema xmlSchema) {
        this.alreadyHandledSchemas.add(xmlSchema);

        // Each schema provides a map from a head-element to a set of all of its
        // possible substitutions. To calculate which substitutions is allowed
        // it is necessary to respect the block-attribute of each element or
        // type from the elements in the given set.
        HashMap<SymbolTableRef<eu.fox7.bonxai.xsd.Element>, HashSet<SymbolTableRef<eu.fox7.bonxai.xsd.Element>>> currentSubstitutionElementsMap = xmlSchema.getSubstitutionElements();
        for (Iterator<SymbolTableRef<eu.fox7.bonxai.xsd.Element>> it = currentSubstitutionElementsMap.keySet().iterator(); it.hasNext();) {
            SymbolTableRef<eu.fox7.bonxai.xsd.Element> stRefElementHead = it.next();

            if (substitutionElements.containsKey(stRefElementHead)) {
                HashSet<SymbolTableRef<eu.fox7.bonxai.xsd.Element>> targetSet = substitutionElements.get(stRefElementHead);
                targetSet.addAll(currentSubstitutionElementsMap.get(stRefElementHead));
                substitutionElements.put(stRefElementHead, targetSet);
            } else {
                substitutionElements.put(stRefElementHead, currentSubstitutionElementsMap.get(stRefElementHead));
            }

        }

        // Walk trough all foreignSchemas and collect all substitutionElements
        if (xmlSchema.getForeignSchemas() != null && !xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadyHandledSchemas.contains(foreignSchema.getSchema())) {
                    collectBaseInformation(foreignSchema.getSchema());
                }
            }
        }

        // Walk over all found substitutionElements to put only allowed substitutions
        // into the resulting map of substitutionGroupInformation
        for (Iterator<SymbolTableRef<eu.fox7.bonxai.xsd.Element>> it = substitutionElements.keySet().iterator(); it.hasNext();) {
            SymbolTableRef<eu.fox7.bonxai.xsd.Element> stRefElementHead = it.next();

            Element headElement = stRefElementHead.getReference();
            LinkedHashSet<Element> elementSubstitutions = new LinkedHashSet<Element>();

            for (Iterator<SymbolTableRef<Element>> it1 = substitutionElements.get(stRefElementHead).iterator(); it1.hasNext();) {
                SymbolTableRef<Element> stRefElementSubstitution = it1.next();
                Element substElement = stRefElementSubstitution.getReference();

                // Check block-attribute if the current substitution is allowed: method checkSubstitutionAllowedInInstance
                if (checkSubstitutionAllowedInInstance(headElement, substElement.getType())) {
                    elementSubstitutions.add(substElement);
                }
            }

            if (!elementSubstitutions.isEmpty()) {
                this.substitutionGroupInformation.put(headElement, elementSubstitutions);
            }
        }
    }

    /**
     * Getter for the generated substitutionGroupInformation
     * @return LinkedHashMap<Element, LinkedHashSet<Element>>   substitutionGroupInformation
     */
    public LinkedHashMap<Element, LinkedHashSet<Element>> getSubstitutionGroupInformation() {
        return substitutionGroupInformation;
    }

    /**
     * Check if a substitution is allowed for a head element and a given type of
     * a possible substitution element
     * @param head                  The head element of a substitutionGroup
     * @param substitutionType      The type that will be checked
     * @return boolean              True, if the type is a possible and allowed substitution
     */
    public boolean checkSubstitutionAllowedInInstance(Element head, Type substitutionType) {
        boolean returnBool = true;

        if (head.getBlockModifiers() != null && !head.getBlockModifiers().isEmpty()) {

            // No kind of substitution is allowed in an instance
            if (head.getBlockModifiers().contains(Element.Block.substitution)) {
                returnBool = false;
            }

            // There are only limitations in the case of different Types
            if (head.getType() != substitutionType) {

                // Substitution with a type that is a restriction of the base type is NOT allowed in an instance
                if (head.getBlockModifiers().contains(Element.Block.restriction)) {
                    LinkedList<String> inheritancePathString = new LinkedList<String>();
                    generateInheritancePathOfType(substitutionType, inheritancePathString);
                    inheritancePathString.size();
                    // Handle the anyType from XML XSDSchema.
                    if (head.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {
                        returnBool = false;
                    } else {
                        if (inheritancePathString.contains(head.getType().getName())) {
                            if (inheritancePathString.indexOf(head.getType().getName()) > inheritancePathString.indexOf("restriction")) {
                                returnBool = false;
                            }
                        }
                    }
                }

                // Substitution with a type that is an extension of the base type is NOT allowed in an instance
                if (head.getBlockModifiers().contains(Element.Block.extension)) {
                    LinkedList<String> inheritancePathString = new LinkedList<String>();
                    generateInheritancePathOfType(substitutionType, inheritancePathString);
                    inheritancePathString.size();
                    // Handle the anyType from XML XSDSchema.
                    if (head.getType().getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {
                        returnBool = false;
                    } else {
                        if (inheritancePathString.contains(head.getType().getName())) {
                            if (inheritancePathString.indexOf(head.getType().getName()) > inheritancePathString.indexOf("extension")) {
                                returnBool = false;
                            }
                        }
                    }
                }
            }
        }
        // Return the calculated value.
        return returnBool;
    }

    /**
     * Generate the path in the inheritance tree of a type. Add values for
     * "extension" or "restriction" or a typeName while the traversion over the
     * inheritance structure.
     * @param targetType        Type name
     * @param stringSet         The resulting inheritance path of the type until it primary base type
     */
    private void generateInheritancePathOfType(Type targetType, LinkedList<String> stringSet) {
        stringSet.add(targetType.getName());
        if (targetType instanceof ComplexType) {

            // Case "complexType":
            ComplexType complexType = (ComplexType) targetType;
            if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {

                // Case "complexContent":
                ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {

                    // Case "extension":
                    ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                    stringSet.add("extension");
                    generateInheritancePathOfType(complexContentExtension.getBase(), stringSet);
                } else if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentRestriction) {

                    // Case "restriction":
                    ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();
                    stringSet.add("restriction");
                    generateInheritancePathOfType(complexContentRestriction.getBase(), stringSet);
                }
            } else if (complexType.getContent() != null && complexType.getContent() instanceof SimpleContentType) {

                // Case "simpleContent":
                SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                    // Case "extension":
                    SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                    stringSet.add("extension");
                    generateInheritancePathOfType(simpleContentExtension.getBase(), stringSet);
                } else if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentRestriction) {

                    // Case "restriction":
                    SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                    stringSet.add("restriction");
                    generateInheritancePathOfType(simpleContentRestriction.getBase(), stringSet);
                }
            }
        } else if (targetType instanceof SimpleType) {

            // Case "simpleType":
            SimpleType simpleType = (SimpleType) targetType;
            if (simpleType.getInheritance() != null && simpleType.getInheritance() instanceof SimpleContentRestriction) {

                // Case "restriction":
                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                stringSet.add("restriction");
                generateInheritancePathOfType(simpleContentRestriction.getBase(), stringSet);
            }
        }
    }

    /**
     * Resolve a substitutionGroup recursivly and calculate only possible
     * substitutions
     * @param currentElement    Possible head of a substitutionGroup
     * @param foundElements     Found valid substitution elements
     */
    private void resolveSubstitutionsRecursivly(Element currentElement, LinkedHashSet<Element> foundElements) {
        // There is no circular substitutionGroup mechanism allowed in XML XSDSchema: --> only valid schemas will be converted
        if (substitutionGroupInformation.get(currentElement) != null && !substitutionGroupInformation.get(currentElement).isEmpty()) {
            for (Iterator<Element> it1 = substitutionGroupInformation.get(currentElement).iterator(); it1.hasNext();) {
                Element subElement = it1.next();
                resolveSubstitutionsRecursivly(subElement, foundElements);
                foundElements.add(subElement);
            }
        }
    }
}
