package eu.fox7.bonxai.xsd.setOperations.intersection;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.Element.*;
import eu.fox7.bonxai.xsd.XSDSchema.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.*;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;

import java.util.*;

/**
 * This class is used to generate new paricles like elements, element references
 * and any patterns. It also provides usefull methods for the automaton classes
 * like the areIntersectableElements method which helps to compare a set of
 * elements. Other than that it contains methods to generate the intersection of
 * attributes like "fixed", "abstract" or "final", which are needed to construct
 * element intersections. For a XSDSchema object the generateNewTopLevelElements
 * method constructs all top-level Eeements and registers them in the schema.
 *
 * @author Dominik Wolff
 */
public class ParticleIntersectionGenerator {

    // XSDSchema which will contain the intersection of schemata contained in the schema group
    private XSDSchema outputSchema;

    // HashMap mapping namespaces to output schemata (Can be used if an element with a foreign namespace is referenced)
    private LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap;

    // HashMap mapping elements to old schemata used to construct the new output schema
    private LinkedHashMap<Element, XSDSchema> elementOldSchemaMap;

    // HashMap mapping any patterns to their old schemata.
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap;

    // HashMap mapping top-level elements to references of their types
    private LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap;

    // HashMap which contains for every local elemente in the given context its type reference
    private HashMap<String, SymbolTableRef<Type>> elementTypeMap;

    // HashMap which maps each element name to a list of elements is was generated from, if it was
    private LinkedHashMap<String, LinkedHashSet<Element>> nameTopLevelElementsMap;

    // Set of schemata not contained in a schema group
    private LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();

    // NamespaceAbbreviationMap maps namespaces to abbreviations
    private LinkedHashMap<String, String> namespaceAbbreviationMap;

    // Directory where the new output schemata will be stored (Default is ""c:\")
    private String workingDirectory = "c:/";

    // Type intersection generator of the attribute particle intersection generator class
    private TypeIntersectionGenerator typeIntersectionGenerator;

    /**
     * This is the constructor of the ParticleIntersectionGenerator class, which
     * initializes most of the fields contained in the class.
     *
     * @param outputSchema  XSDSchema which will contain the new types.
     * @param namespaceOutputSchemaMap Map mapping namespaces to output
     * schemata.
     * @param elementOldSchemaMap Map mapping elements to old schemata of the
     * schema group.
     * @param anyPatternOldSchemaMap Map mapping any pattern to old schemata of
     * the schema group.
     * @param topLevelElementTypeMap Map mapping top-level elements to
     * references of their types.
     * @param otherSchemata Set of schemata not contained in a schema group.
     * @param workingDirectory Directory where the new output schemata will be
     * stored.
     * @param namespaceAbbreviationMap Map maps namespaces to abbreviations.
     */
    public ParticleIntersectionGenerator(XSDSchema outputSchema, LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap, LinkedHashMap<Element, XSDSchema> elementOldSchemaMap, LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap, LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap, LinkedHashSet<XSDSchema> otherSchemata, LinkedHashMap<String, String> namespaceAbbreviationMap, String workingDirectory) {

        //Initialize class fields
        this.outputSchema = outputSchema;
        this.namespaceOutputSchemaMap = namespaceOutputSchemaMap;
        this.elementOldSchemaMap = elementOldSchemaMap;
        this.anyPatternOldSchemaMap = anyPatternOldSchemaMap;
        this.topLevelElementTypeMap = topLevelElementTypeMap;
        this.otherSchemata = otherSchemata;
        this.namespaceAbbreviationMap = namespaceAbbreviationMap;
        this.workingDirectory = workingDirectory;
    }

    /**
     * Method tests if a set of any patterns has a non empty intersection. This
     * is the case if the intersection of "namespace" attributes is not empty.
     * If the intersection is not empty a new any pattern can be generated via
     * the generateNewAnyPattern method.
     *
     * @param anyPatterns Set of any patterns for which the test is performed.
     * @return <tt>true</tt> is the intersection is possible and not empty, else
     * <tt>false</tt>.
     */
    public boolean areIntersectableAnyPatterns(LinkedHashSet<AnyPattern> anyPatterns) {

        // Get "namespace" attribute of the new any pattern
        String namespaceIntersection = getNamespace(anyPatterns);

        // If new "namespace" attribute is not empty return true els false
        if (namespaceIntersection != null && !namespaceIntersection.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * If a set of elements has a non empty intersection this method returns
     * <tt>true</tt> if the intersection is empty <tt>false</tt> is returned.
     * In order to check if the intersection is empty following attributes are
     * checked, it is checked if all element names and abstract values are the
     * same and if present all elements have the same fixed value. Elements with
     * different "fixed" values have no intersection because contents of these
     * elements are bound to their fixed values. The "abstract" attribute states
     * that an element is not allowed to appear in a schema instance.
     *
     * Types are not considered because type generation is handled in a
     * different class. So if the types of specified elements have no
     * intersection this method will not return false, if all other aspects
     * allow an intersection.
     *
     * @param elements Set of elements, which should be intersected and is
     * checked for an empty intersection.
     * @return <tt>true</tt> if the intersection is probably not empty and
     * <tt>false</tt> otherwise.
     */
    public boolean areIntersectableElements(LinkedHashSet<Element> elements) {

        // Initialize element name storage variable
        String elementName = null;

        // Compare the different elements with one another
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // Check if current element name equals the last element name if not return false
            if (elementName != null && !getInstanceName(element).equals(elementName)) {
                return false;
            } else {

                // Store last element name
                elementName = getInstanceName(element);
            }

            // If an element with "abstract" attribute is contained return false
            if (getAbstract(elements) == null && getAbstract(elements) == false) {
                return false;
            }

            // If an element of the element set contains a "fixed" attribute check if the getFixed method returns a valid new "fixed" attribute else set attribute to null
            if (hasFixed(elements) && getFixed(elements) == null) {
                return false;
            }
        }

        // If non of the above tests return false the intersection is possible
        return true;
    }

    /**
     * Method generates a new any pattern by intersecting a set of old any
     * patterns. The new any pattern contains a new "namespace" attribute, which
     * in turn was generated from the "namespace" attributes of the any patterns
     * in the set. This "namespace" attribute specifies, which elements can
     * match this new any pattern. In contrast to an any attribute an any
     * pattern can only represent one element where an any attribute can
     * represent many attributes.
     *
     * @param anyPatterns Set of any patterns used to construct the new any
     * pattern.
     * @return Resulting any pattern with new namespace and new
     * ProcessContentsInstruction.
     */
    public AnyPattern generateNewAnyPattern(LinkedHashSet<AnyPattern> anyPatterns) {

        // Generate new "namespace" attribute and check if the attribute is valid
        String newNamespace = getNamespace(anyPatterns);

        // Return null if the "namespace" attribute is invalid
        if (newNamespace.equals("")) {
            return null;
        }

        // Create new any pattern with new annotation and "ID" and "processContents" attributes
        AnyPattern newAnyPattern = new AnyPattern(ProcessContentsInstruction.Skip, newNamespace);
        newAnyPattern.setAnnotation(getAnnotation(new LinkedHashSet<Particle>(anyPatterns)));
        newAnyPattern.setId(getID(new LinkedHashSet<Particle>(anyPatterns)));

        // Return the new any pattern
        return newAnyPattern;
    }

    /**
     * This method gets the element with the specified element name from the
     * given any patterns, if such an element is contained in an any
     * pattern.
     *
     * @param elementName Name of the searched for element.
     * @param anyPatterns
     * @return
     */
    public Element getIncluded(String elementName, LinkedHashSet<AnyPattern> anyPatterns) {

        // Check for each any pattern if an element with the specified name is included
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();

            for (Iterator<Element> it2 = getContainedElements(anyPattern, anyPatternOldSchemaMap.get(anyPattern)).iterator(); it2.hasNext();) {
                Element containedElement = it2.next();

                // If an conatained element has the specified name return this element
                if (containedElement.getName().equals(elementName)) {
                    return containedElement;
                }
            }
        }
        return null;
    }

    /**
     * Set new type intersection generator for the particle generator.
     *
     * @param typeIntersectionGenerator Type intersection generator, which is
     * used in the particle generator.
     */
    public void setTypeIntersectionGenerator(TypeIntersectionGenerator typeIntersectionGenerator) {
        this.typeIntersectionGenerator = typeIntersectionGenerator;
    }

    /**
     * Method that gets the instance name of an element.
     *
     * @param element Element, which instance name is requested.
     * @return Instance name of the element.
     */
    private String getInstanceName(Element element) {

        // Use emtpy namespace if element is not qualified
        if (element.getForm() == Qualification.qualified) {
            return element.getName();
        } else {
            return "{}" + element.getLocalName();
        }
    }

    /**
     * Create new appInfo after the model of a specified old appInfo.
     *
     * @param oldAppInfo Old appInfo used to create the new appInfo.
     * @return New appInfo containing all information of the specified old
     * appInfo.
     */
    private AppInfo generateNewAppInfo(AppInfo oldAppInfo) {

        // Create new appInfo
        AppInfo newAppInfo = new AppInfo();

        // Set new node list for the new appInfo (node list is taken directly from the old appInfo because no operation changes this DOM structure)
        newAppInfo.setNodeList(oldAppInfo.getNodeList());

        // Set new source for the new appInfo
        newAppInfo.setSource(oldAppInfo.getSource());
        return newAppInfo;
    }

    /**
     * Create new documentation after the model of a specified old documentation.
     *
     * @param oldAppInfo Old documentation used to create the new documentation.
     * @return New documentation containing all information of the specified old
     * documentation.
     */
    private Documentation generateNewDocumentation(Documentation oldDocumentation) {

        // Create new documentation
        Documentation newDocumentation = new Documentation();

        // Set new node list for the new documentation (node list is taken directly from the old appInfo because no operation changes this DOM structure)
        newDocumentation.setNodeList(oldDocumentation.getNodeList());

        // Set new source and XmlLang attributes for the new documentation
        newDocumentation.setSource(oldDocumentation.getSource());
        newDocumentation.setXmlLang(oldDocumentation.getXmlLang());
        return newDocumentation;
    }

    /**
     * This method is called to create a new element of element reference from a
     * set of particles. The new particle represents an intersection of the old
     * particles and is local, meaning some attributes which are allowed for
     * top-level elements are not allowed in this context and vice versa.
     *
     * Although the method exepts all particles only elements and element
     * references are processed .
     *
     * @param particles Set of particles, ideally a set of elements and element
     * references.
     * @return A new particle either an element or element reference if the
     * intersection is not empty. If it is a null pointer is returned.
     */
    public Particle generateNewLocalParticle(LinkedHashSet<Particle> particles) {

        // Use sets to partition the particle set into two different sets
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        LinkedHashSet<ElementRef> elementRefs = new LinkedHashSet<ElementRef>();

        // Partition particle set into an element set and an element reference set
        for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
            Particle particle = it.next();

            // If particle is an element add it to the element set else if the particle is an element reference add it to the element reference set
            if (particle instanceof Element) {
                elements.add((Element) particle);
            } else if (particle instanceof ElementRef) {
                elementRefs.add((ElementRef) particle);
            }
        }

        // If an element is present in the particle set create new element else check if an new element reference is possible
        if (!elementRefs.isEmpty() && elements.isEmpty()) {

            // Use new element set to store referred elements
            LinkedHashSet<Element> referredElements = new LinkedHashSet<Element>();

            // Add all referred elements to the referred element set
            for (Iterator<ElementRef> it = elementRefs.iterator(); it.hasNext();) {
                ElementRef elementRef = it.next();
                referredElements.add(elementRef.getElement());
            }

            // Get entry of the nameTopLevelElementsMap
            HashSet<Element> intersectingElements = nameTopLevelElementsMap.get(getInstanceName(referredElements.iterator().next()));

            // If top-level element exists in the new output schema and if it was build from the referred elements create new element reference
            if (namespaceOutputSchemaMap.get(referredElements.iterator().next().getNamespace()) != null && namespaceOutputSchemaMap.get(referredElements.iterator().next().getNamespace()).getElementSymbolTable().hasReference(referredElements.iterator().next().getName()) &&
                    intersectingElements.containsAll(referredElements) && referredElements.containsAll(intersectingElements) && areIntersectableElements(referredElements)) {

                // Create new element reference referring to the top-level element and set annotation and "ID" attribute
                ElementRef newElementRef = new ElementRef(namespaceOutputSchemaMap.get(referredElements.iterator().next().getNamespace()).getElementSymbolTable().getReference(referredElements.iterator().next().getName()));
                newElementRef.setAnnotation(getAnnotation(particles));
                newElementRef.setId(getID(particles));

                return newElementRef;
            } else {

                // Add all referred elements to the element set
                for (Iterator<ElementRef> it = elementRefs.iterator(); it.hasNext();) {
                    ElementRef elementRef = it.next();
                    elements.add(elementRef.getElement());
                }
            }
        } else if (!elements.isEmpty()) {

            // Add all referred elements to the element set
            for (Iterator<ElementRef> it = elementRefs.iterator(); it.hasNext();) {
                ElementRef elementRef = it.next();
                elements.add(elementRef.getElement());
            }

            // If elements are intersectable create new element
            if (areIntersectableElements(elements)) {

                // Create new type with new element name and new type
                Element newElement = null;

                // If element is qualified the namespace is important
                if ((getForm(particles) == null || getForm(particles) != XSDSchema.Qualification.qualified)) {

                    // Create new elment with same local name but different namespace as the old element but new type
                    newElement = new Element("{" + outputSchema.getTargetNamespace() + "}" + elements.iterator().next().getLocalName(), elementTypeMap.get(getInstanceName(elements.iterator().next())));
                } else {

                    // Create new elment with same name as the old element but new type
                    newElement = new Element(elements.iterator().next().getName(), elementTypeMap.get(getInstanceName(elements.iterator().next())));
                }

                // Create new "form" attribute, which can be null if empty
                XSDSchema.Qualification newForm = getForm(particles);

                // If an invalid "form" attribute was build return null else set the new attribute
                if (newForm == null) {
                    return null;
                } else if (newForm != outputSchema.getElementFormDefault()) {
                    newElement.setForm(newForm);
                }

                // Set new annotation and "ID" attribute
                newElement.setAnnotation(getAnnotation(particles));
                newElement.setId(getID(new LinkedHashSet<Particle>(particles)));

                // Set new "block" attribute
                newElement.setBlockModifiers(getBlock(elements));

                // Set new "nillable" attribute
                if (getNillable(elements)) {
                    newElement.setNillable();
                } else {
                    newElement.setNotNillable();
                }

                // Set new "default", "fixed", "type" and "abstract" attributes
                newElement.setDefault(getDefault(elements));
                newElement.setFixed(getFixed(elements));
                newElement.setTypeAttr(elementTypeMap.get(getInstanceName(elements.iterator().next())).getReference().isAnonymous());

                // Return the new top-level element
                return newElement;
            } else {

                // If intersection is empty return null
                return null;
            }
        }
        return null;
    }

    /**
     * Generates a new import element in the specified importing XSDSchema, which
     * references the imported schema.
     *
     * @param importingSchema XSDSchema, in which the new import will be present.
     * @param importedSchema XSDSchema, which is imported.
     */
    private void generateNewImport(XSDSchema importingSchema, XSDSchema importedSchema) {

        // Construct new import-component, which refers to the referencedSchema
        ImportedSchema newImport = new ImportedSchema(importedSchema.getTargetNamespace(), importedSchema.getSchemaLocation().substring(importedSchema.getSchemaLocation().lastIndexOf("/") + 1));
        newImport.setParentSchema(importingSchema);
        newImport.setSchema(importedSchema);

        // Check if output schema already contains an import-component with same namespace and schema location
        for (Iterator<ForeignSchema> it3 = importingSchema.getForeignSchemas().iterator(); it3.hasNext();) {
            ForeignSchema foreignSchema = it3.next();

            if (foreignSchema instanceof ImportedSchema) {
                ImportedSchema currentImportedSchema = (ImportedSchema) foreignSchema;

                // Check namespace and schema location of currentImportedSchema and importedSchema
                if (currentImportedSchema.getNamespace().equals(newImport.getNamespace()) && currentImportedSchema.getSchemaLocation().equals(newImport.getSchemaLocation())) {

                    // If import-component exists in output schema set importedSchema to null
                    importedSchema = null;
                }
            }
        }
        if (importedSchema != null) {

            // Add new import-component to the output schema
            importingSchema.addForeignSchema(newImport);

            // Construct new IdentifiedNamespace for import-component
            IdentifiedNamespace identifiedNamespace = constructNewIdentifiedNamespace(importingSchema, importedSchema.getTargetNamespace());

            // Add new IdentifiedNamespace to output schema
            if (identifiedNamespace != null) {
                importingSchema.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
            }
        }
    }

    /**
     * This method constructs a new IdentifiedNamespace for a given schema and
     * namespace. IdentifiedNamespace are used to map namespaces to
     * identifier/abbreviations. If the namespace is already attributed to an
     * abbreviation no IdentifiedNamespace is constructed and this mehtod returns
     * a null-pointer.
     *
     * @param schema XSDSchema in which the new IdentifiedNamespace can be placed.
     * @param namespace Namespace of the new IdentifiedNamespace, if a new
     * IdentifiedNamespace is generated.
     * @return Null if the given schema already contains an IdentifiedNamespace
     * for the specified namespace.
     */
    private IdentifiedNamespace constructNewIdentifiedNamespace(XSDSchema schema, String namespace) {

        // Construct new IdentifiedNamespace for import-component
        IdentifiedNamespace identifiedNamespace = null;

        // Check if namespace abbreviation is present in schema
        if (schema.getNamespaceList().getNamespaceByUri(namespace).getIdentifier() == null) {

            // Check if abbrevation exists in namespaceAbbreviationMap, if not generate new abbreviation (Form i.e. ns2)
            if (namespaceAbbreviationMap.containsKey(namespace)) {
                identifiedNamespace = new IdentifiedNamespace(namespaceAbbreviationMap.get(namespace), namespace);
            } else {

                // Finde new abbreviation for namespace
                int number = 1;
                while (identifiedNamespace == null) {
                    String abbreviation = "ns" + number;

                    // If abbreviation is not used in schema construct new IdentifiedNamespace
                    if (schema.getNamespaceList().getNamespaceByIdentifier(abbreviation).getUri() == null) {
                        identifiedNamespace = new IdentifiedNamespace(abbreviation, namespace);
                    }
                    number++;
                }
            }
        }
        return identifiedNamespace;
    }

    /**
     * Get elements contained in the specified any pattern, if the any pattern
     * is processed strictly.
     *
     * @param anyPattern Any pattern which specifies the set of elements.
     * @param schema XSDSchema which contains the any element.
     * @return A set containing all elements contained in the specified any
     * pattern.
     */
    private LinkedHashSet<Element> getContainedElements(AnyPattern anyPattern, XSDSchema schema) {

        // Check if the any pattern is processed strictly
        if (anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Strict) || anyPattern.getProcessContentsInstruction().equals(ProcessContentsInstruction.Lax)) {

            // Initalize set to store all top-level elements
            LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();

            // If any pattern namespace attribute contains "##any"
            if (anyPattern.getNamespace() == null || anyPattern.getNamespace().contains("##any")) {

                // Add all elements contained in the schema to the set
                topLevelElements.addAll(schema.getElements());

                // Add all elements contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                }
                return topLevelElements;

            } else if (anyPattern.getNamespace().contains("##other")) {

                // If any pattern namespace attribute contains "##other" only add elements contained in foreign schemata to the set
                for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                    ForeignSchema foreignSchema = it.next();
                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                }
                return topLevelElements;
            } else {

                // Check list of namespaces
                for (String currentNamespace : anyPattern.getNamespace().split(" ")) {

                    // If any pattern namespace attribute contains "##local"
                    if (currentNamespace.contains("##local")) {

                        // Check if target namespace is empty
                        if (schema.getTargetNamespace().equals("")) {

                            // Add all elements contained in the schema to the set
                            topLevelElements.addAll(schema.getElements());
                        } else {

                            // If any pattern namespace attribute contains "##local" only add elements contained in foreign schemata to the set
                            for (Iterator<ForeignSchema> it = anyPatternOldSchemaMap.get(anyPattern).getForeignSchemas().iterator(); it.hasNext();) {
                                ForeignSchema foreignSchema = it.next();

                                // Check if the current namespace is the namespace of the foreign schema
                                if (foreignSchema.getSchema().getTargetNamespace().equals("")) {
                                    topLevelElements.addAll(foreignSchema.getSchema().getElements());
                                }
                            }
                            return topLevelElements;
                        }
                    } else if (currentNamespace.contains("##targetNamespace")) {

                        // Add all elements contained in the schema to the set
                        topLevelElements.addAll(schema.getElements());
                    } else {

                        // Find foreign schema with the specified namespace
                        for (Iterator<ForeignSchema> it = schema.getForeignSchemas().iterator(); it.hasNext();) {
                            ForeignSchema foreignSchema = it.next();

                            // Check if target namespace is empty
                            if (foreignSchema.getSchema().getTargetNamespace().equals("")) {

                                // Add all elements contained in the schema to the set
                                topLevelElements.addAll(foreignSchema.getSchema().getElements());
                            }
                            // Check if the current namespace is the namespace of the foreign schema
                            if (foreignSchema.getSchema().getTargetNamespace().equals(currentNamespace)) {
                                topLevelElements.addAll(foreignSchema.getSchema().getElements());
                            }
                        }
                    }
                }
                return topLevelElements;
            }
        } else {

            // If the any pattern is processed "lax" or "skip" no elements are returned
            return new LinkedHashSet<Element>();
        }
    }

    /**
     * This method computes a new top-level element intersection for the
     * specified elements. If the elements have a non empty intersection the
     * constructed element is registered in the output schema. This is only
     * possible for elements with same names, same abstract values and so on. If
     * the intersection is empty no element will be build and a null pointer is
     * returned. Because only top-level elements are build attributes like
     * "form" are ignored and every element is registered as top-level in the
     * output schema.
     *
     * @param topLevelElements Set of top-level elements. If possible an
     * intersection for these elements will be build.
     * @param typeRef Reference to a type for the resulting element.
     * @return Null pointer if the element intersection is empty and an new
     * element otherwise.
     */
    private Element generateNewTopLevelElement(LinkedHashSet<Element> topLevelElements, SymbolTableRef<Type> typeRef) {

        // If top-level elements are intersectable and the type of the element is not null create new top-level element
        if (areIntersectableElements(topLevelElements) && typeRef.getReference() != null && !typeRef.getReference().isDummy()) {

            // Create new type with new element name and new type
            Element newElement = new Element(topLevelElements.iterator().next().getName(), typeRef);

            // Set new annotation and "ID" attribute
            newElement.setAnnotation(getAnnotation(new LinkedHashSet<Particle>(topLevelElements)));
            newElement.setId(getID(new LinkedHashSet<Particle>(topLevelElements)));

            // Set new "final" and "block" attributes
            newElement.setFinalModifiers(getFinal(topLevelElements));
            newElement.setBlockModifiers(getBlock(topLevelElements));

            // Set new "nillable" attribute
            if (getNillable(topLevelElements)) {
                newElement.setNillable();
            } else {
                newElement.setNotNillable();
            }

            // Set new "default", "fixed", "type" and "abstract" attributes
            newElement.setDefault(getDefault(topLevelElements));
            newElement.setFixed(getFixed(topLevelElements));
            newElement.setTypeAttr(typeRef.getReference().isAnonymous());
            newElement.setAbstract(getAbstract(topLevelElements));

            // Update element SymbolTable and top-level elements list with new top-level element
            outputSchema.getElementSymbolTable().updateOrCreateReference(newElement.getName(), newElement);

            if (!outputSchema.getElements().contains(newElement)) {
                outputSchema.addElement(outputSchema.getElementSymbolTable().getReference(newElement.getName()));
            }

            // Return the new top-level element
            return newElement;
        } else {

            // If intersection is empty return null
            return null;
        }
    }

    /**
     * This method generates all top-level elements of the output schema. To
     * do so it is given a set of element lists, these list are checked for
     * intersecting elements for which new elements are build and registered in
     * the output schema. In difference to the same method for attributes
     * top-level elements, which do not appear in every schema are not present
     * in the output schema. This is because top-level elements are not only
     * used to be referenced by element references but are also the root
     * elements of instances of XML XSDSchema schemata. So if an element is
     * top-level element in one schema and in another schema it is not then in
     * the intersection of these schemata the element can not be top-level.
     *
     * @param topLevelElementListSet Set of lists of top-level elements, each
     * schema contains a list of top-level elements these are combined in this
     * list. Because elements are top-level certain attributes like "form" are
     * not used.
     * @return Map which maps top-level element names to lists of elements which
     * were used to build them.
     */
    public LinkedHashMap<String, LinkedHashSet<Element>> generateNewTopLevelElements(LinkedHashSet<LinkedList<Element>> topLevelElementListSet) {

        // Get map mapping top-level element names to sets of top-level elements
        nameTopLevelElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();

        // Generate nameTopLevelElementsMap which maps each element name to a list of elements is was generated from, if it was.
        for (Iterator<LinkedList<Element>> it = topLevelElementListSet.iterator(); it.hasNext();) {
            LinkedList<Element> topLevelElements = it.next();

            // Add each contained top-level element to an entry of the nameTopLevelElementsMap
            for (Iterator<Element> it2 = topLevelElements.iterator(); it2.hasNext();) {
                Element topLevelElement = it2.next();

                // Check if element has the same namespace as the output schema
                if (topLevelElement.getNamespace().equals(outputSchema.getTargetNamespace())) {

                    // Update nameTopLevelElementsMap with new top-level element
                    if (!nameTopLevelElementsMap.containsKey(getInstanceName(topLevelElement))) {
                        nameTopLevelElementsMap.put(getInstanceName(topLevelElement), new LinkedHashSet<Element>());
                    }
                    LinkedHashSet<Element> intersectionElements = nameTopLevelElementsMap.get(getInstanceName(topLevelElement));
                    intersectionElements.add(topLevelElement);
                    nameTopLevelElementsMap.put(getInstanceName(topLevelElement), intersectionElements);
                }
            }
        }

        // Get set of removable map entries
        LinkedHashSet<String> removableElementNames = new LinkedHashSet<String>();

        // An element name in the nameTopLevelElementsMap responds to a set of elements if this set is of the same size as the number of
        // intersecting schemata, meaning each schema contains an element with same name, this entry is left in the HashMap all other are removed.
        for (Iterator<String> it = nameTopLevelElementsMap.keySet().iterator(); it.hasNext();) {
            String elementName = it.next();

            // Check if abstract element is contained
            for (Iterator<Element> it2 = nameTopLevelElementsMap.get(elementName).iterator(); it2.hasNext();) {
                Element element = it2.next();

                // If abstract element exist in entry remove entry
                if (element.getAbstract() == true) {
                    removableElementNames.add(elementName);
                }
            }

            // Compare size of nameTopLevelElementsMap entry and the specified set of element lists
            if (nameTopLevelElementsMap.get(elementName).size() != topLevelElementListSet.size()) {
                removableElementNames.add(elementName);
            }
        }

        // Remove map entries
        for (Iterator<String> it = removableElementNames.iterator(); it.hasNext();) {
            String elementName = it.next();
            nameTopLevelElementsMap.remove(elementName);
        }

        // For each entry in the nameTopLevelElementsMap a new top-level element is constructed if possible else no element is build
        for (Iterator<String> it = nameTopLevelElementsMap.keySet().iterator(); it.hasNext();) {
            String elementName = it.next();
            generateNewTopLevelElement(nameTopLevelElementsMap.get(elementName), topLevelElementTypeMap.get(elementName));
        }

        // Return the constructed map
        return nameTopLevelElementsMap;
    }

    /**
     * When intersecting complexTypes a new particle representing the
     * intersection of old particles is needed. This method computes the
     * intersection of specified particles. In order to do so a number of
     * automatons are used. First for each particle a NFA is constructed, which
     * is the transformed into a DFA via subset construction, after this all
     * automatons are used to generate a product automaton. The final particle
     * is then constructed via state elimination from the product automaton. To
     * optimise particle length a number of optimizations can be done in post
     * processing.
     *
     * @param particles List of particles, most particles are usually contained
     * by complexTypes and in order to intersect them they are combined to a
     * list.
     * @param elementTypeMap MAp which contains for every local elemente in the
     * given context its type reference.
     * @return Particle which represents the intersection of the specified
     * particles.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     * @throws EmptyProductParticleStateFieldException Exception which is thrown
     * if a product state contains no particle states.
     * @throws NotSupportedParticleAutomatonException Exception which is thrown
     * if a particle automaton is not supported by the current method.
     * @throws NonDeterministicFiniteAutomataException Exception which is thrown
     * if a particle automaton is no deterministic finite automaton.
     */
    public Particle generateParticleIntersection(LinkedList<Particle> particles, LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap) throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {

        // Set new element name to type map, which is later used by the particle automaton factory
        this.elementTypeMap = elementTypeMap;

        // Create new particle automaton factory
        ParticleAutomatonFactory particleAutomatonFactory = new ParticleAutomatonFactory();

        // For each particle an non deterministic automaton is build similar to the Thompson construction of an NFA
        LinkedList<ParticleAutomaton> particleAutomatons = new LinkedList<ParticleAutomaton>();

        for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
            Particle particle = it.next();

            // Build new particle automaton for the current particle, determinise it and add it to the particle automaton set
            ParticleAutomaton particleAutomaton = particleAutomatonFactory.buildParticleAutomaton(particle, anyPatternOldSchemaMap);
            particleAutomatons.add(particleAutomatonFactory.buildSubsetParticleAutomaton(particleAutomaton));
        }

        // All determinstic automatons are used to construct a product automaton which represents the intersection of all particles
        ProductParticleAutomaton productParticleAutomaton = particleAutomatonFactory.buildProductAutomatonIntersection(particleAutomatons, this);

        // Via state elimination a new Particle is generate from the product automaton.
        ParticleBuilder particleBuilder = new ParticleBuilder();
        Particle particle = particleBuilder.buildParticle(productParticleAutomaton);

        // Replace elements with group references if necessary
        replaceElementsWithGroupReferences(particle, 0, null);
        return particle;
    }

    /**
     * Replaces elements with foreign namespaces in the specified particle.
     * Instead of elements new group references are placed in the particle
     * linking to the same elments in foreign schemata.
     *
     * @param particle Particle, which is the context of this operation.
     * @param particlePosition Position of the current particle in a parent
     * particle.
     * @param parentParticle Parent of the current particle. Only Particle
     * containers can be parents.
     */
    private void replaceElementsWithGroupReferences(Particle particle, int particlePosition, ParticleContainer parentParticle) {

        // For an element check if element has to be replaced
        if (particle instanceof Element) {
            Element element = (Element) particle;

            // Check again if the element was qualified and if add new group to an other schema
            if (element.getForm() != null && element.getForm() == XSDSchema.Qualification.qualified && !element.getNamespace().equals(outputSchema.getTargetNamespace())) {

                // Get other schema to store element group in
                XSDSchema otherSchema = new XSDSchema(element.getNamespace());
                otherSchema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xs", "http://www.w3.org/2001/XMLSchema"));
                otherSchema.setSchemaLocation(workingDirectory + "OtherSchema(" + element.getNamespace() + ").xsd");

                // Check if schema exist in schema groups
                if (namespaceOutputSchemaMap.get(element.getNamespace()) != null) {
                    otherSchema = namespaceOutputSchemaMap.get(element.getNamespace());
                } else {

                    // Check if other schema is present in other schema list
                    for (Iterator<XSDSchema> it = otherSchemata.iterator(); it.hasNext();) {
                        XSDSchema currentOtherSchema = it.next();

                        // If other schema has same namespace as the element use this schema
                        if (otherSchema.getTargetNamespace().equals(element.getNamespace())) {
                            otherSchema = currentOtherSchema;
                        }
                    }

                    // Add other schema to schema set
                    otherSchemata.add(otherSchema);
                }

                // Get new particle container, which contains the element
                ChoicePattern choicePattern = new ChoicePattern();
                choicePattern.addParticle(element);

                // Build new group name and avoid conflicts
                String newGroupNameBase = "{" + otherSchema.getTargetNamespace() + "}" + "element.group." + element.getLocalName();
                String newGroupName = newGroupNameBase;
                int number = 1;

                // Check if group name is already taken in the other schema and add new number to the group name base until the name is available
                while (otherSchema.getGroupSymbolTable().hasReference(newGroupName)) {
                    newGroupName = newGroupNameBase + "." + number;
                    number++;
                }

                // Build new group and add it to the other schema
                Group newGroup = new Group(newGroupName, choicePattern);
                otherSchema.getGroupSymbolTable().updateOrCreateReference(newGroupName, newGroup);
                otherSchema.addGroup(otherSchema.getGroupSymbolTable().getReference(newGroupName));

                // Generate new import components in both schemata if necessary
                generateNewImport(outputSchema, otherSchema);
                generateNewImport(otherSchema, outputSchema);

                // Register group in output schema and return group reference
                outputSchema.getGroupSymbolTable().updateOrCreateReference(newGroupName, newGroup);

                // Replace old element with group reference
                parentParticle.setParticle(particlePosition, new GroupRef(outputSchema.getGroupSymbolTable().getReference(newGroupName)));
            }
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer particleContainer = (ParticleContainer) particle;

            // Check for a particle container all contained elements
            for (int i = 0; i < particleContainer.getParticles().size(); i++) {
                Particle containedParticle = particleContainer.getParticles().get(i);

                // Replace elements in particle container
                replaceElementsWithGroupReferences(containedParticle, i, particleContainer);
            }
        }
    }

    /**
     * Builds intersection of abstract values for a given set of elements. Each
     * element is either absract or not. If all elements have the same abstract
     * value this value is returned. Different abstract values will result in an
     * empty intersection and a null pointer is returned.
     *
     * @param elements Set of elements, each element may conatin an abstract
     * value.
     * @return <tt>true</tt> or <tt>false</tt> if abstract value is true in all
     * elements or false in all elements respectively. Otherwise null if
     * different abstract values are present in the element set.
     */
    private Boolean getAbstract(HashSet<Element> elements) {

        // Initialize new "abstract" attribute
        Boolean newAbstract = null;

        // Check for each given element if an "abstract" attribute is contained
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // If element contains no "abstract" attribute and if stored "abstract" attribute is not false or if stored "abstract" attribute is not null and the current element contains no "abstract"
            // attribute or both "abstract" attributes are different return null
            if (newAbstract != null && ((element.getAbstract() != null && element.getAbstract() != newAbstract) || (element.getAbstract() == null && newAbstract != false))) {
                return null;
            } else {

                // If no "abstract" value was stored and the current element contains an attribute store the value in the new "abstract" attribute
                if (element.getAbstract() == null || element.getAbstract() == false) {
                    newAbstract = false;
                } else {
                    newAbstract = true;
                }
            }
        }
        return newAbstract;
    }

    /**
     * This method creates a new annotation for a given set of particles. Each
     * particle may contain an annotation, these annotations are used to
     * contstruct the new annotation, which contains the app infos and
     * documentations of the old annotations.
     *
     * @param particles Set of particles, which is used to construct a new
     * particle. This particle will contain the new annotation.
     * @return New Annotation, which contains the information stored in the
     * old annotations contained in the specified particles.
     */
    private Annotation getAnnotation(LinkedHashSet<Particle> particles) {

        // Create new annotation
        Annotation newAnnotation = null;

        // Check each particle for a contained annotation
        for (Particle particle : particles) {

            // Check if particle contains an annotation
            if (particle.getAnnotation() != null) {
                Annotation oldAnnotation = particle.getAnnotation();

                // If new annotation is still null initialize new annotation
                if (newAnnotation == null) {
                    newAnnotation = new Annotation();
                }

                // Get all old appInfos
                LinkedList<AppInfo> oldAppInfos = oldAnnotation.getAppInfos();

                // For each old appInfo create a new appInfo and add it to the annotation
                for (Iterator<AppInfo> it = oldAppInfos.iterator(); it.hasNext();) {
                    AppInfo oldAppInfo = it.next();

                    // Create new appInfo and add it to the appInfo list of the new annotation
                    newAnnotation.addAppInfos(generateNewAppInfo(oldAppInfo));
                }

                // Get all old documentations
                LinkedList<Documentation> oldDocumentations = oldAnnotation.getDocumentations();

                // For each old documentation create a new documentation and add it to the annotation
                for (Iterator<Documentation> it = oldDocumentations.iterator(); it.hasNext();) {
                    Documentation oldDocumentation = it.next();

                    // Create new documentation and add it to the documentation list of the new annotation
                    newAnnotation.addDocumentations(generateNewDocumentation(oldDocumentation));
                }
            }
        }
        return newAnnotation;
    }

    /**
     * Generates a new "block" value from a set of elements. The "block"
     * attribute of XML XSDSchema restricts the use of derived types, so when
     * computing the intersection the most restrictive "block" value is
     * computed.
     *
     * @param elements Set of elements, each element contains a "block" value,
     * which is used to compute the new "block" attribute.
     * @return Set of ComplexTypeInheritanceModifier, which represents the new
     * "block" attribute.
     */
    private HashSet<Block> getBlock(LinkedHashSet<Element> elements) {

        // Generate new "block" attribute
        HashSet<Block> block = new HashSet<Block>();

        // Check for each element the "block" attribute
        for (Element element : elements) {

            // If "block" attribute is present add contained Block value from the "block" attribute of the new element
            if (element.getBlockModifiers() != null) {

                // Check if "extension" is contained and if add "extension" from "block" attribute of the new element
                if (element.getBlockModifiers().contains(Block.extension)) {
                    block.add(Block.extension);
                }
                // Check if "restriction" is contained and if add "restriction" from "block" attribute of the new element
                if (element.getBlockModifiers().contains(Block.restriction)) {
                    block.add(Block.restriction);
                }
                // Check if "substitution" is  contained and if add "substitution" from "block" attribute of the new element
                if (element.getBlockModifiers().contains(Block.substitution)) {
                    block.add(Block.substitution);
                }
            } else if (elementOldSchemaMap.get(element).getBlockDefaults() != null) {

                // Add Block values contained in the default value
                if (elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.extension)) {
                    block.add(Block.extension);
                }
                if (elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.restriction)) {
                    block.add(Block.restriction);
                }
                if (elementOldSchemaMap.get(element).getBlockDefaults().contains(BlockDefault.substitution)) {
                    block.add(Block.substitution);
                }
            }
        }

        // Generate a set containing BlockDefault values for the Block values contained in the set of the new element
        LinkedHashSet<XSDSchema.BlockDefault> blockDefaults = new LinkedHashSet<XSDSchema.BlockDefault>();

        // Check if "extension" is contained and if add "extension"
        if (block.contains(Block.extension)) {
            blockDefaults.add(XSDSchema.BlockDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (block.contains(Block.restriction)) {
            blockDefaults.add(XSDSchema.BlockDefault.restriction);
        }
        // Check if "substitution" is contained and if add "substitution"
        if (block.contains(Block.substitution)) {
            blockDefaults.add(XSDSchema.BlockDefault.substitution);
        }

        // Check if new "block" values is different than the default value of the output schema
        if (!(outputSchema.getBlockDefaults().containsAll(blockDefaults) && blockDefaults.containsAll(outputSchema.getBlockDefaults()))) {
            return block;
        } else {

            // If output schema has the same "blockDefault" value as the new elements "block" attribute return null
            return null;
        }
    }

    /**
     * Constructs an intersection of default values. If all elements contain the
     * same "default" value a String with this value is returned. Otherwise if
     * different "default" values are present a null pointer is returned.
     * "Default" values do not affect the intersection of elements.
     *
     * @param elements Set of elements, which may contain "defaul"t values.
     * @return String representing the new "default" value or null pointer if
     * intersection is empty.
     */
    private String getDefault(LinkedHashSet<Element> elements) {

        // New "default" value, per default null
        String newDefault = null;

        // For each element check the contained "default" value
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // If "default" value is not present or if different "default" values are present return null else update the new "default" value
            if (element.getDefault() == null || newDefault != null && !newDefault.equals(element.getDefault())) {
                return null;
            } else {
                newDefault = element.getDefault();
            }
        }

        // Return the new "default" value
        return newDefault;
    }

    /**
     * Generates a new "final" attribute from a set of given elements. Because
     * the "final" attribute of an element restricts the type inheritance, the
     * most restrictive "final" value is computed as result of the intersection.
     * Only top-level elements may contain "final" values.
     *
     * @param elements Set of elements, each Element may contains a "final"
     * value, which then can be used to compute the new "final" value.
     * @return Set of Final enums, which represents the new "final" value.
     */
    private HashSet<Final> getFinal(LinkedHashSet<Element> elements) {

        // Generate new "final" attribute
        HashSet<Final> finalValue = new HashSet<Final>();

        // Check for each element the "final" attribute
        for (Element element : elements) {

            // If "final" attribute is present add contained Final value from the "final" attribute of the new element
            if (element.getFinalModifiers() != null) {

                // Check if "extension" is contained and if add "extension" from "final" attribute of the new element
                if (element.getFinalModifiers().contains(Final.extension)) {
                    finalValue.add(Final.extension);
                }
                // Check if "restriction" is contained and if add "restriction" from "final" attribute of the new element
                if (element.getFinalModifiers().contains(Final.restriction)) {
                    finalValue.add(Final.restriction);
                }
            } else if (elementOldSchemaMap.get(element).getFinalDefaults() != null) {

                // Add Final values contained in the default value
                if (elementOldSchemaMap.get(element).getFinalDefaults().contains(FinalDefault.extension)) {
                    finalValue.add(Final.extension);
                }
                if (elementOldSchemaMap.get(element).getFinalDefaults().contains(FinalDefault.restriction)) {
                    finalValue.add(Final.restriction);
                }
            }
        }

        // Generate a set containing FinalDefault values for the Final values contained in the set of the new element
        LinkedHashSet<XSDSchema.FinalDefault> finalDefaults = new LinkedHashSet<XSDSchema.FinalDefault>();

        // Check if "extension" is contained and if add "extension"
        if (finalValue.contains(Final.extension)) {
            finalDefaults.add(XSDSchema.FinalDefault.extension);
        }
        // Check if "restriction" is contained and if add "restriction"
        if (finalValue.contains(Final.restriction)) {
            finalDefaults.add(XSDSchema.FinalDefault.restriction);
        }

        // Check if new "final" values is different than the default value of the output schema
        if (!(outputSchema.getFinalDefaults().containsAll(finalDefaults) && finalDefaults.containsAll(outputSchema.getFinalDefaults()))) {
            return finalValue;
        } else {

            // If output schema has the same "finalDefault" value as the new elements "final" attribute return null
            return null;
        }
    }

    /**
     * Computes the intersection of "fixed" attributes for a set of elements.
     * The intersection is empty if different elements have differet "fixed"
     * values. If an Element has no "fixed" value it is ignored. Only if all
     * elements have either no or the same "fixed" values a non empty
     * intersection is build.
     *
     * @param elements Set of elements, each element may contains a "fixed"
     * attribute.
     * These "fixed" values are used to generate the new "fixed" value.
     * @return String if all elements contain the same "fixed" value or null.
     * And null pointer if different "fixed" values are present.
     */
    private String getFixed(LinkedHashSet<Element> elements) {

        // New "fixed" value, per default null
        String newFixed = null;

        // For each element check the contained "fixed" value
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // Check if element contains "fixed" value
            if (element.getFixed() != null) {

                // If no "fixed" value was seen before update new "fixed" value else check if both "fixed" values are equal and if not return null
                if (newFixed != null && !newFixed.equals(element.getFixed())) {
                    return null;
                } else {
                    newFixed = element.getFixed();
                }
            }
        }

        // Return new "fixed" value which should not be null
        return newFixed;
    }

    /**
     * Computes the a new "form" attribute from different particles. Each
     * element or element reference contains directly or indirectly a "form"
     * attribute which is used to generate the new "form" attribute.
     *
     * This method ignores most particles only elements and element references
     * are used to compute the new "form" value.
     *
     * @param particles Set of particles. Elements directly contain "form"
     * attributes and element references refer to top-level defined elements
     * which are generally qualified.
     * @return Null if no intersection was possible i.e. intersection of
     * qualified and unqualified. Qualification if all particles contain the
     * same "form" value.
     */
    private Qualification getForm(LinkedHashSet<Particle> particles) {

        // Initialize new "form" attribute, which is null per default
        Qualification newForm = null;

        // Check for each given particle if a "form" attribute is contained
        for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
            Particle particle = it.next();

            // Check if particle is element or element reference
            if (particle instanceof Element) {
                Element element = (Element) particle;

                // If element has no "form" attribute or if current "form" attribute is not equivalent to the new "form" attribute return null else store current "form" attribute in new "form" attribute
                if (newForm != null && (element.getForm() != null && newForm != element.getForm() || element.getForm() == null && newForm != outputSchema.getElementFormDefault())) {
                    return null;
                } else {

                    // Set new element form
                    if (element.getForm() != null) {
                        newForm = element.getForm();
                    } else {
                        newForm = outputSchema.getElementFormDefault();
                    }
                }
            } else if (particle instanceof ElementRef) {

                // If particle is element reference and if current "form" attribute is not equivalent to the new "form" attribute return null else store current "form" attribute in new "form" attribute
                if (newForm != null && newForm != Qualification.qualified) {
                    return null;
                } else {
                    newForm = Qualification.qualified;
                }
            }
        }
        return newForm;

    }

    /**
     * The getID method generates an intersection of specified "IDs". The result
     * is a new "ID" which is used by the particle resulting from the
     * intersection of given particles. If two particle have different "ID"
     * values the intersection is empty and a null pointer is returned.
     *
     * @param particles Set of particle from which the new "ID" is calculated.
     * @return Null if the intersection is empty or a String representing the
     * "ID" if all particles carry the same "ID" value;
     */
    private String getID(LinkedHashSet<Particle> particles) {

        // Initialize new "ID" attribute
        String newID = "";

        // Check for each given particle if an "ID" attribute is contained
        for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
            Particle particle = it.next();

            // If particle contains no "ID" attribute or if contained "ID" attribute is different from the new "ID" attribute return null else store the contained "ID" attribute in the new "ID" attribute
            if (particle.getId() == null || particle.getId().equals("") || !newID.equals(particle.getId()) && !newID.equals("")) {
                return null;
            } else {
                newID = particle.getId();
            }
        }
        return newID;
    }

    /**
     * Generates a new "namespace" attribute for a new any pattern. The
     * attribute is constructed as intersection of the given any pattern set. To
     * generate an accurate intersection the target namespaces of the any
     * patterns are considered too. This is important for values like "##other",
     * which selects all namespaces different from the target namespace of the
     * current any pattern.
     *
     * @param anyPatterns Set of any patterns each may contain a "namespace"
     * attribute.
     * @return New list of namespaces. The list is represented as String and
     * represents an intersection of the "namespace" values of specified any
     * patterns.
     */
    public String getNamespace(LinkedHashSet<AnyPattern> anyPatterns) {

        // Use boolean variabel to check if each any pattern has "namespace" value "##any"
        boolean any = true;

        // Get a set containing all namespaces contained in the any patterns
        HashSet<String> namespaces = new HashSet<String>();

        // For each any pattern check the contained "namespace" attribute
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();

            if (anyPattern != null) {

                // If any pattern contains "namespace" attribute check contained values else add "##any" to the namespace
                if (anyPattern.getNamespace() != null) {

                    // Get current "namespace" attribute
                    String namespace = anyPattern.getNamespace();

                    // If "namespace" attribute equals "##any" or "##other" or "##local" add namespace to namespace set
                    if (namespace.equals("##any") || namespace.equals("##other") || namespace.equals("##local")) {
                        namespaces.add(namespace);
                    } else if (namespace.equals("##targetNamespace")) {

                        // If "namespace" attribute equals "##targetNamespace" get the target namespace of the containing schema and add it to the namespace set
                        namespaces.add(anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace());
                    } else {

                        // If the "namespace" attribute contains a list of namespaces split the string and add namespaces to the namespace set
                        for (String currentNamespace : namespace.split(" ")) {

                            // If current namespace equals "##local" or an URI add the namespace to the set
                            if (currentNamespace.equals("##local") || !currentNamespace.equals("##targetNamespace")) {
                                namespaces.add(currentNamespace);

                            } else if (currentNamespace.equals("##targetNamespace")) {

                                // For a current namespace with value "##targetNamespace" get the target namespace of the containing schema and add it to the namespace set
                                namespaces.add(anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace());
                            }
                        }
                    }
                } else {

                    // If any pattern has no "namespace" attribute the default values is "##any"
                    namespaces.add("##any");
                }

                // If the current any pattern contains a "namespace" attribute which contains no "##any" the any pattern has no "##any" value
                if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any")) {
                    any = false;
                }
            }
        }

        // If each any pattern contained an "##any" value return "##any" as value of the any pattern
        if (any) {
            return "##any";
        }

        // Check for each "namespace" if each any pattern contains it
        for (Iterator<String> it = namespaces.iterator(); it.hasNext();) {
            String namespace = it.next();
            for (Iterator<AnyPattern> it2 = anyPatterns.iterator(); it2.hasNext();) {
                AnyPattern anyPattern = it2.next();

                if (anyPattern != null && namespaces.contains(namespace)) {

                    // Set namespace of the any attribute to the default value
                    if (anyPattern.getNamespace() == null) {
                        anyPattern.setNamespace("##any");
                    }

                    // If current "namespace" is "##any" it can be removed (as it was checked beforehand)
                    if (namespace.equals("##any")) {
                        it.remove();

                    } else if (namespace.equals("##other")) {

                        // If current "namespace" is "##other" and if current any pattern contains no "namespace" with "##any" or "##other" remove it
                        if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any") && !anyPattern.getNamespace().contains("##other")) {
                            it.remove();
                        }
                    } else if (namespace.equals("##local")) {

                        // If current "namespace" is "##local" and if current any pattern contains no "namespace" with "##any" or "##local", remove it
                        if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any") && !anyPattern.getNamespace().contains("##local")) {
                            it.remove();
                        }
                    } else {

                        // If current "namespace" has any other value and if current any pattern contains no "namespace" with "##any"or the "namespace" value itself or "other", when the target namespace is not the specified namespace, remove it
                        if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().contains("##any") && !anyPattern.getNamespace().contains(namespace) && !(anyPattern.getNamespace().contains("##targetNamespace") &&  anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespace)) && !((anyPattern.getNamespace().contains("##other") && !anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespace)))) {
                            it.remove();
                        }
                    }
                }
            }
        }

        // After removing all "namespace" values not contained in each any pattern check if namespace set contains "##other"
        if (namespaces.contains("##other")) {

            // For each any pattern check if the new "namespace" attribute will have value "##other"
            for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
                AnyPattern anyPattern = it.next();

                // If an "##other" valued "namepace" with same target namespace exist return "##other"
                if (anyPattern.getNamespace() != null && anyPattern.getNamespace().contains("##other") && anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(outputSchema.getTargetNamespace())) {
                    return "##other";
                }
            }

            // If no "##other" valued "namespace" was constructed return "##any"
            return "##any";
        } else {

            // Build "namespace" value string list
            String newNamespace = "";

            // For each namespace contained in the namespace list add a value to the string
            for (Iterator<String> it = namespaces.iterator(); it.hasNext();) {
                String namespace = it.next();

                // If current namespace is the target namespace add value "##targetNamespace" to the string else the namespace value
                if (outputSchema.getTargetNamespace().equals(namespace)) {
                    newNamespace += "##targetNamespace";
                } else {
                    newNamespace += namespace;
                }

                // Check if namespace follows and add whitespace
                if (it.hasNext()) {
                    newNamespace += " ";
                }
            }

            // Return the generated "namespace" attribute
            return newNamespace;
        }
    }

    /**
     * This method generates a new ProcessContentsInstruction from a specified
     * set of any patterns. If an any pattern has "strict" as value of its
     * ProcessContentsInstruction a validator must validate this any pattern.
     * So if a single any pattern of the specified any patterns has
     * ProcessContentsInstructionvalue "strict" the new
     * ProcessContentsInstruction is sticted as well. Analog for "lax" and
     * "skip", which is returned if no any pattern has
     * ProcessContentsInstruction "strict" or "lax".
     *
     * @param anyPatterns Set of any pattern, each any pattern has a
     * ProcessContentsInstruction.
     * @return New ProcessContentsInstruction if any any pattern has
     * ProcessContentsInstruction "strict", "strict" is returned, then "lax" and
     * then "skip".
     */
    private ProcessContentsInstruction getProcessContentsInstruction(LinkedHashSet<AnyPattern> anyPatterns) {

        // Use variable to determine if new "processContents" attribute is "lax"
        boolean lax = false;

        // For each any pattern check the "processContents" attribute
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();

            // If any pattern has no "processContents" attribute or an attribute with value "strict" return strict
            if (anyPattern.getProcessContentsInstruction() == ProcessContentsInstruction.Strict || anyPattern.getProcessContentsInstruction() == null) {
                return ProcessContentsInstruction.Strict;
            }
            // If "processContents" attribute has value "lax" set variable to true
            if (anyPattern.getProcessContentsInstruction() == ProcessContentsInstruction.Lax) {
                lax = true;
            }
        }

        // If variable is true ("processContents" attribute with value "lax" was seen) return "lax" else return "skip"
        if (lax) {
            return ProcessContentsInstruction.Lax;
        } else {
            return ProcessContentsInstruction.Skip;
        }
    }

    /**
     * Generates an intersection of "nillable" attributes for a specified set of
     * elements. Each element is either nillable or not, in difference to other
     * elements like "fixed". If all elements have the same "nillable" value
     * this value is returned.
     *
     * @param elements Set of elements, each element with a "nillable" value.
     * @return <tt>true</tt> or <tt>false</tt> if "nillable" value is "true" in
     * all elements or "false" otherwise.
     */
    private boolean getNillable(LinkedHashSet<Element> elements) {

        // Check if an contained element has "nillable" value "false"
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // If an element has "nillable" value "false" for this element the "xsi:nil" attribute can not be used so the generated element can not use it either return false
            if (element.getNillable() == false) {
                return false;
            }
        }

        // If all elements contain "nillable" attributes with value "true" return true
        return true;
    }

    /**
     * Checks if one of the elements contained in the given element set contains
     * a "fixed" value. If this is the case a new fixed value can be computed.
     *
     * @param elements Set of elements, each element may contain a "fixed"
     * attribute.
     * @return <tt>true</tt> if an element contains a "fixed" value, else
     * <tt>false</tt>.
     */
    private boolean hasFixed(LinkedHashSet<Element> elements) {

        // For each element check if "fixed" value is contained
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();

            // If element contains "fixed" value return true
            if (element.getFixed() != null) {
                return true;
            }
        }

        // Return false if no element contains a "fixed" attribute
        return false;
    }

    /**
     * This method checks if a specified element name is included in one of the
     * given any patterns. This is the case if one any pattern contains the
     * same namespace as the element.
     *
     * @param elementName Qualified name of an element, to check wether an
     * element is include in an any pattern only the name is needed.
     * @param anyPatterns Set of any patterns, the element name may be included
     * in one of them.
     * @return <tt>true</tt> if the element name is included in any of the any
     * patterns, otherwise <tt>false</tt>.
     */
    public boolean isIncluded(String elementName, LinkedHashSet<AnyPattern> anyPatterns) {

        // Get namespace of the element
        String namespaceElement = elementName.substring(1, elementName.lastIndexOf("}"));

        // For each any patter is tested if one includes the specfied namespace
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();

            // Compare namespace with different "namespace" attribute values
            if (anyPattern.getNamespace() == null || anyPattern.getNamespace().contains("##any")) {
                return true;
            } else if (anyPattern.getNamespace().contains("##other") && !anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespaceElement)) {
                return true;
            } else if (anyPattern.getNamespace().contains("##local") && namespaceElement.equals("")) {
                return true;
            } else if (anyPattern.getNamespace().contains("##targetNamespace") && anyPatternOldSchemaMap.get(anyPattern).getTargetNamespace().equals(namespaceElement)) {
                return true;
            } else if (anyPattern.getNamespace().contains(namespaceElement) && !namespaceElement.equals("")) {
                return true;
            }
        }

        // If namespace is not included return false
        return false;
    }

    /**
     * The isOptional method checks if a particle accepts an empty content. An
     * intersection with optional particles can be empty without effecting the
     * the containing type. But if only one partice is not optional an empty
     * intersection is not valid and the containing type will result in an
     * empty intersection as well.
     *
     * @param particle Particle for which is checked if it is optional.
     * @return <tt>true</tt> if the Particle is optional else <tt>false</tt>.
     * @throws EmptySubsetParticleStateFieldException Exception which is thrown
     * if a subset state contains no particle states.
     */
    public boolean isOptional(Particle particle) throws EmptySubsetParticleStateFieldException {

        // Use particle automaton factory for check
        ParticleAutomatonFactory particleAutomatonFactory = new ParticleAutomatonFactory();

        // Generate new particle automaton for the given particle and determinise it
        ParticleAutomaton particleAutomaton = particleAutomatonFactory.buildParticleAutomaton(particle, anyPatternOldSchemaMap);
        particleAutomaton = particleAutomatonFactory.buildSubsetParticleAutomaton(particleAutomaton);

        // If the set of final states contains the start state the particle is optional
        if (particleAutomaton.getFinalStates().contains(particleAutomaton.getStartState())) {
            return true;
        } else {
            return false;
        }
    }
}
