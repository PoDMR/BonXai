package de.tudortmund.cs.bonxai.converter.xsd2dtd;

import java.util.LinkedHashMap;
import de.tudortmund.cs.bonxai.common.AllPattern;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.dtd.Attribute;
import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.dtd.Element;
import de.tudortmund.cs.bonxai.dtd.ElementRef;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Constraint;
import de.tudortmund.cs.bonxai.xsd.ForeignSchema;
import de.tudortmund.cs.bonxai.xsd.Key;
import de.tudortmund.cs.bonxai.xsd.KeyRef;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleConstraint;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Converter class for XSD elements and generation of DTD content models
 * @author Lars Schmidt
 */
public class ElementConverter {

    /**
     * HashMap for DTD elements and a list of possible content models
     * This list of content models
     */
    private LinkedHashMap<String, ElementWrapper> elementMap;
    private XSDSchema xmlSchema;
    private DocumentTypeDefinition dtd;
    private LinkedHashSet<SimpleConstraint> globalConstraints;
    private HashSet<XSDSchema> alreadyHandledSchemas;
    // HashSet used for marking already converted elements
    private HashSet<de.tudortmund.cs.bonxai.xsd.Element> alreadyConvertedElements;

    /**
     * Constructor of class ElementConverter
     *
     * This is a converter class for XSD elements and the generation of
     * corresponding DTD content models
     *
     * DTD attributes are converted in class AttributeConverter
     *
     * @param xmlSchema
     * @param dtd
     */
    public ElementConverter(XSDSchema xmlSchema, DocumentTypeDefinition dtd) {
        this.xmlSchema = xmlSchema;
        this.dtd = dtd;
        this.elementMap = new LinkedHashMap<String, ElementWrapper>();
        this.globalConstraints = new LinkedHashSet<SimpleConstraint>();
        this.alreadyHandledSchemas = new HashSet<XSDSchema>();
        this.alreadyConvertedElements = new HashSet<de.tudortmund.cs.bonxai.xsd.Element>();
    }

    /**
     * Method: convert
     *
     * This method is the base method of this class. It starts the conversion
     * with all globaly defined elements in the given XML Schema.
     *
     * All elements (inner elements, too) are noticed in the elementMap for
     * later progressing and conversion.
     *
     * @return LinkedHashMap<String, ElementWrapper>
     */
    public LinkedHashMap<String, ElementWrapper> convert() {

        LinkedList<de.tudortmund.cs.bonxai.xsd.Element> startElements = new LinkedList<de.tudortmund.cs.bonxai.xsd.Element>();

        // Find all top-level elements recursivly from within all external and local schemas
        findStartElements(xmlSchema, startElements);

        for (Iterator<de.tudortmund.cs.bonxai.xsd.Element> it = startElements.iterator(); it.hasNext();) {
            de.tudortmund.cs.bonxai.xsd.Element element = it.next();
            this.convertElement(element);
        }
        
        return this.elementMap;
    }

    /**
     * Find all start elements from the current XML Schema object structure
     * representing a local schema and all of its foreign or external schemas
     * recursivly.
     * @param currentXSDSchema      The current XML schema document object
     * @param startElements         List of all found elements, that can be used as start elements for valid XML instances
     */
    private void findStartElements(XSDSchema currentXSDSchema, LinkedList<de.tudortmund.cs.bonxai.xsd.Element> startElements) {
        this.alreadyHandledSchemas.add(currentXSDSchema);

        startElements.addAll(currentXSDSchema.getElements());

        // Walk trough all foreignSchemas.
        if (currentXSDSchema.getForeignSchemas() != null && !currentXSDSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = currentXSDSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!this.alreadyHandledSchemas.contains(foreignSchema.getSchema())) {
                    findStartElements(foreignSchema.getSchema(), startElements);
                }
            }
        }
    }

    /**
     * Method addDTDElement
     *
     * Add a given element or elementRef or both to the elementMap and the
     * correct elementWrapper object according to the given full qualified XSD
     * name.
     *
     * @param dtdName
     * @param element
     * @param elementRef
     */
    public void addDTDElement(String dtdName, Element element, ElementRef elementRef) {
        if (this.elementMap.get(dtdName) == null) {
            // Case: No Entry in the HashMap
            ElementWrapper elementWrapper = new ElementWrapper(xmlSchema, dtdName);
            if (element != null) {
                elementWrapper.addDTDElement(element);
            }
            if (elementRef != null) {
                elementWrapper.addDTDElementRef(elementRef);
            }
            this.elementMap.put(dtdName, elementWrapper);
        } else {
            // Case: There is already an entry in the HashMap
            ElementWrapper elementWrapper = this.elementMap.get(dtdName);
            if (element != null) {
                elementWrapper.addDTDElement(element);
            }
            if (elementRef != null) {
                elementWrapper.addDTDElementRef(elementRef);
            }
        }
    }

    /**
     * Method addDTDAttributes
     *
     * Add a given list of DTD attributes to the elementWrapper with respect to
     * the dtdName of the element
     *
     * @param dtdName   the DTD name of the element
     * @param dtdAttributeList     list of DTD attributes for addition
     */
    public void addDTDAttributes(String dtdName, LinkedList<Attribute> dtdAttributeList) {
        if (this.elementMap.get(dtdName) == null) {
            // Case: No Entry in the HashMap
            ElementWrapper elementWrapper = new ElementWrapper(xmlSchema, dtdName);
            if (dtdAttributeList != null && !dtdAttributeList.isEmpty()) {
                for (Iterator<Attribute> it = dtdAttributeList.iterator(); it.hasNext();) {
                    Attribute attribute = it.next();
                    elementWrapper.addDTDAttribute(attribute);
                }
            }
            this.elementMap.put(dtdName, elementWrapper);
        } else {
            // Case: There is already an entry in the HashMap
            ElementWrapper elementWrapper = this.elementMap.get(dtdName);
            if (dtdAttributeList != null && !dtdAttributeList.isEmpty()) {
                for (Iterator<Attribute> it = dtdAttributeList.iterator(); it.hasNext();) {
                    Attribute attribute = it.next();
                    elementWrapper.addDTDAttribute(attribute);
                }
            }
        }
    }

    /**
     * Method addConstraints
     *
     * Add a given list of XML Schema constraints to the elementWrapper with respect to
     * the dtdName of the corresponding element
     *
     * @param dtdName   the DTD name of the element
     * @param xsdConstraints    list of XML Schema constraints for addition
     */
    public void addConstraints(String dtdName, LinkedList<Constraint> xsdConstraints) {
        for (Iterator<Constraint> it = xsdConstraints.iterator(); it.hasNext();) {
            Constraint constraint = it.next();

            if (constraint instanceof Key) {
                Key key = (Key) constraint;

                if (key.getSelector().matches("\\.//[a-zA-Z0-9\\*]*")) {
                    String selectorName = key.getSelector().substring(key.getSelector().lastIndexOf("/") + 1, key.getSelector().length());
                    if (selectorName.equals("*")) {
                        // put Constraint to ALL elements.
                        this.globalConstraints.add(key);
                    } else {
                        // put Constraint to element with given selector name
                        ElementWrapper elementWrapper = null;
                        if (this.elementMap.get(selectorName) == null) {
                            elementWrapper = new ElementWrapper(xmlSchema, selectorName);
                            this.elementMap.put(dtdName, elementWrapper);
                        } else {
                            elementWrapper = this.elementMap.get(selectorName);
                        }
                        elementWrapper.addXSDConstraint(key);
                    }
                }
            } else if (constraint instanceof KeyRef) {
                KeyRef keyRef = (KeyRef) constraint;
                if (keyRef.getSelector().matches("\\.//[a-zA-Z0-9\\*]*")) {
                    String selectorName = keyRef.getSelector().substring(keyRef.getSelector().lastIndexOf("/") + 1, keyRef.getSelector().length());
                    if (selectorName.equals("*")) {
                        // put Constraint to ALL elements.
                        this.globalConstraints.add(keyRef);
                    } else {
                        // put Constraint to element with given selector name
                        ElementWrapper elementWrapper = null;
                        if (this.elementMap.get(selectorName) == null) {
                            elementWrapper = new ElementWrapper(xmlSchema, selectorName);
                            this.elementMap.put(dtdName, elementWrapper);
                        } else {
                            elementWrapper = this.elementMap.get(selectorName);
                        }
                        elementWrapper.addXSDConstraint(keyRef);
                    }
                }
            }
        }
    }

    /**
     * Getter for the generated elementMap
     * @return LinkedHashMap<String, ElementWrapper>
     */
    public LinkedHashMap<String, ElementWrapper> getElementMap() {
        return elementMap;
    }

    /**
     * Method: convertElement
     *
     * Conversion of a XML Schema element and its particle structure within a
     * type to the corresponding DTD element counterpart
     *
     * @param element       de.tudortmund.cs.bonxai.xsd.Element
     * @return  Element     de.tudortmund.cs.bonxai.dtd.Element
     */
    public Element convertElement(de.tudortmund.cs.bonxai.xsd.Element element) {
        // Initialize the DTDNameGenerator
        DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
        String dtdElementName = dtdNameGenerator.getDTDElementName(element.getName(), element.getForm());

        // Prepare the result
        Element dtdElement = new Element(dtdElementName);

		// Mark the element as already seen/converted
        alreadyConvertedElements.add(element);

        Particle dtdParticle = null;

        if (element.getType() != null) {
            if (element.getType() instanceof ComplexType) {
                ComplexType complexType = (ComplexType) element.getType();
                if (complexType.getContent() != null) {
                    if (complexType.getContent() instanceof ComplexContentType) {
                        // Case: Complex content model
                        ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                        if (complexContentType.getParticle() != null) {
                            if (!complexType.getMixed()) {
                                // Normal Content model
                                dtdParticle = convertContentModel(complexContentType.getParticle());
                            } else {
                                // Mixed Content model
                                LinkedList<Particle> particleList = convertMixedContentModel(complexContentType.getParticle(), new HashSet<String>());
                                ChoicePattern dtdChoicePattern = new ChoicePattern();
                                boolean isAnyPattern = false;
                                // Loop over the particleList and add the content to the choice
                                for (Iterator<Particle> it = particleList.iterator(); it.hasNext();) {
                                    Particle particle = it.next();
                                    if (particle instanceof AnyPattern) {
                                        isAnyPattern = true;
                                    }
                                    dtdChoicePattern.addParticle(particle);
                                }

                                if (isAnyPattern) {
                                    // ANY type found
                                    dtdParticle = new AnyPattern(ProcessContentsInstruction.Strict, "");
                                } else {
                                    // Mixed content has to be framed with a countingPattern (...)*
                                    CountingPattern dtdCountingPattern = new CountingPattern(0, null);
                                    dtdCountingPattern.addParticle(dtdChoicePattern);
                                    dtdParticle = dtdCountingPattern;
                                }
                                dtdElement.setMixedStar(true);
                            }
                        } else {
                            // There are no particles in this complexContent
                            if (complexType.getMixed()) {
                                // CASE (#PCDATA)*:
                                dtdElement.setMixedStar(true);
                            }
                        }
                    } else {
                        // CASE: (#PCDATA)*    (SimpleContentType)
                        dtdElement.setMixedStar(true);
                    }
                } else {
                    // Case: EMPTY (no content model at all)
                    dtdElement.setMixedStar(false);
                    dtdParticle = null;
                }
            } else if (element.getType() instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) element.getType();
                if (simpleType.getLocalName().equals("anyType")) {
                    // Case ANY:
                    dtdParticle = new AnyPattern(ProcessContentsInstruction.Strict, "");
                } else {
                    // CASE (#PCDATA)*:
                    dtdElement.setMixedStar(true);
                }
            }
        } else {
            // Case EMPTY:
            dtdElement.setMixedStar(false);
            // ANY type
            dtdParticle = new AnyPattern(ProcessContentsInstruction.Strict, "");
        }

        if (dtdParticle != null) {
            dtdElement.setParticle(dtdParticle);
        }

        // Convert attributes
        AttributeConverter attributeConverter = new AttributeConverter(this.xmlSchema);
        LinkedList<Attribute> dtdAttributes = attributeConverter.convertAttributes(element);

        if (dtdAttributes != null && !dtdAttributes.isEmpty()) {
            this.addDTDAttributes(dtdElement.getName(), dtdAttributes);
        }

        // Handle Constraints for DTD ID/IDREF
        if (element.getConstraints() != null && !element.getConstraints().isEmpty()) {
            this.addConstraints(dtdElement.getName(), element.getConstraints());
        }

        // Add the DTD element to the correct elementWrapper
        this.addDTDElement(dtdElement.getName(), dtdElement, null);

        // return the result
        return dtdElement;
    }

    /**
     * Convert a given XML Schema Particle to its DTD counterpart
     * @param particle      XML Schema particle as source of the conversion
     * @return Particle     DTD particle as result
     */
    private Particle convertContentModel(Particle particle) {
        Integer level = new Integer(0);
        LinkedHashMap<Integer, Integer> tempHash = new LinkedHashMap<Integer, Integer>();
        return this.convertContentModel(particle, level, tempHash);
    }

    /**
     * Convert a XML Schema content model to its DTD counterpart 
     * (There is no handling of mixed content in this method.)
     * 
     * @param particle      XSD particle structure
     * @return Particle     DTD particle structure
     */
    private Particle convertContentModel(Particle particle, Integer level, LinkedHashMap<Integer, Integer> countingPatternMap) {
        Particle dtdParticle = null;
        level++;
        if (particle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) {

            // Case "ElementRef":

            de.tudortmund.cs.bonxai.xsd.ElementRef elementRef = (de.tudortmund.cs.bonxai.xsd.ElementRef) particle;
            if (elementRef.getElement() != null) {

                de.tudortmund.cs.bonxai.xsd.Element xsdElement = elementRef.getElement();
                DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
                String dtdElementName = dtdNameGenerator.getDTDElementName(xsdElement.getName(), xsdElement.getForm());

                SymbolTableRef<Element> symbolTableRef = new SymbolTableRef<Element>(dtdElementName, new Element(dtdElementName));
                ElementRef dtdElementRef = new ElementRef(symbolTableRef);

                this.addDTDElement(dtdElementName, null, dtdElementRef);
                dtdParticle = dtdElementRef;

            } else {
                // Failure with elementRef --> ANY
                dtdParticle = new AnyPattern(ProcessContentsInstruction.Strict, "");
            }
        } else if (particle instanceof de.tudortmund.cs.bonxai.xsd.Element) {

            // Case "Element":

            de.tudortmund.cs.bonxai.xsd.Element xsdElement = (de.tudortmund.cs.bonxai.xsd.Element) particle;

            String dtdElementName = null;
            SymbolTableRef<Element> symbolTableRef = null;

            if (!this.alreadyConvertedElements.contains(xsdElement)) {
                Element dtdContentElement = this.convertElement(xsdElement);
                dtdElementName = dtdContentElement.getName();
                symbolTableRef = this.dtd.getElementSymbolTable().updateOrCreateReference(dtdElementName, dtdContentElement);
            } else {
                DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
                dtdElementName = dtdNameGenerator.getDTDElementName(xsdElement.getName(), xsdElement.getForm());
                symbolTableRef = this.dtd.getElementSymbolTable().getReference(dtdElementName);
            }

            ElementRef dtdElementRef = new ElementRef(symbolTableRef);
            this.addDTDElement(dtdElementName, null, dtdElementRef);
            dtdParticle = dtdElementRef;

        } else if (particle instanceof SequencePattern) {

            // Case "SequencePattern":

            SequencePattern dtdSequencePattern = new SequencePattern();
            SequencePattern xsdSequencePattern = (SequencePattern) particle;
            for (Iterator<Particle> it = xsdSequencePattern.getParticles().iterator(); it.hasNext();) {
                Particle curentXSDParticle = it.next();
                Particle newDTDParticle = convertContentModel(curentXSDParticle, level, countingPatternMap);
                if (newDTDParticle != null) {
                    dtdSequencePattern.addParticle(newDTDParticle);
                }
                if (newDTDParticle instanceof AnyPattern) {
                    return new AnyPattern(ProcessContentsInstruction.Strict, "");
                }
            }
            dtdParticle = dtdSequencePattern;
        } else if (particle instanceof ChoicePattern) {

            // Case "ChoicePattern":

            ChoicePattern dtdChoicePattern = new ChoicePattern();
            ChoicePattern xsdChoicePattern = (ChoicePattern) particle;
            for (Iterator<Particle> it = xsdChoicePattern.getParticles().iterator(); it.hasNext();) {
                Particle curentXSDParticle = it.next();
                Particle newDTDParticle = convertContentModel(curentXSDParticle, level, countingPatternMap);
                if (newDTDParticle != null) {
                    dtdChoicePattern.addParticle(newDTDParticle);
                }
                if (newDTDParticle instanceof AnyPattern) {
                    return new AnyPattern(ProcessContentsInstruction.Strict, "");
                }
            }
            dtdParticle = dtdChoicePattern;
        } else if (particle instanceof AnyPattern) {

            // Case "AnyPattern":

            // ANY has to be set as root particle --> backtracking
            dtdParticle = new AnyPattern(ProcessContentsInstruction.Strict, "");
        } else if (particle instanceof AllPattern) {

            // Case "AllPattern":

            AllPattern xsdAllPattern = (AllPattern) particle;
            int countParticles = xsdAllPattern.getParticles().size();

            // Depending on global settings the conversion of an AllPattern can
            // result in different particle structures
            if (countParticles <= XSD2DTDConverter.ALL_UPPER_BOUND_PERMUTATION) {

                // Case "Permutations":

                ChoicePattern dtdChoicePattern = new ChoicePattern();

                LinkedList<Particle> particleList = new LinkedList<Particle>();

                for (Iterator<Particle> it = xsdAllPattern.getParticles().iterator(); it.hasNext();) {
                    Particle curentXSDParticle = it.next();
                    Particle newDTDParticle = convertContentModel(curentXSDParticle, level, countingPatternMap);
                    if (newDTDParticle != null) {
                        particleList.add(newDTDParticle);
                    }
                    if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.Strict, "");
                    }
                }
                countParticles = particleList.size();
                PermutationTool instance = new PermutationTool(countParticles);
                int[] currentPermutation = null;
                while (instance.morePermutationsLeft()) {
                    SequencePattern currentDTDSequencePattern = new SequencePattern();
                    currentPermutation = instance.calculateNextPermutation();
                    for (int i = 0; i < countParticles; i++) {
                        currentDTDSequencePattern.addParticle(particleList.get(currentPermutation[i]));
                    }
                    dtdChoicePattern.addParticle(currentDTDSequencePattern);
                }
                dtdParticle = dtdChoicePattern;
            } else if (countParticles <= XSD2DTDConverter.ALL_UPPER_BOUND_CHOICE) {

                // Case "Sequence of choices":

                ChoicePattern dtdChoicePattern = new ChoicePattern();
                for (Iterator<Particle> it = xsdAllPattern.getParticles().iterator(); it.hasNext();) {
                    Particle curentXSDParticle = it.next();
                    Particle newDTDParticle = convertContentModel(curentXSDParticle, level, countingPatternMap);
                    if (newDTDParticle != null) {
                        dtdChoicePattern.addParticle(newDTDParticle);
                    }
                    if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.Strict, "");
                    }
                }
                SequencePattern dtdSequencePattern = new SequencePattern();
                for (int i = 0; i < countParticles; i++) {
                    dtdSequencePattern.addParticle(dtdChoicePattern);
                }
                dtdParticle = dtdSequencePattern;
            } else if (countParticles <= XSD2DTDConverter.ALL_UPPER_BOUND_ANY) {

                // Case "One choice with countingpattern":

                ChoicePattern dtdChoicePattern = new ChoicePattern();
                for (Iterator<Particle> it = xsdAllPattern.getParticles().iterator(); it.hasNext();) {
                    Particle curentXSDParticle = it.next();
                    Particle newDTDParticle = convertContentModel(curentXSDParticle, level, countingPatternMap);
                    if (newDTDParticle != null) {
                        dtdChoicePattern.addParticle(newDTDParticle);
                    }
                    if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.Strict, "");
                    }
                }
                CountingPattern dtdCountingPattern = new CountingPattern(0, null);

                dtdCountingPattern.addParticle(dtdChoicePattern);
                dtdParticle = dtdCountingPattern;
            } else {
                for (Iterator<Particle> it = xsdAllPattern.getParticles().iterator(); it.hasNext();) {
                    Particle curentXSDParticle = it.next();
                    // The individual xsdParticles have to be converted for writing out declarations of new elements in the DTD,
                    // which are not used in other positions
                    convertContentModel(curentXSDParticle, level, countingPatternMap);
                }
                dtdParticle = new AnyPattern(ProcessContentsInstruction.Strict, "");
            }

        } else if (particle instanceof CountingPattern) {

            // Case "CountingPattern":

            CountingPattern xsdCountingPattern = (CountingPattern) particle;

            if (xsdCountingPattern.getMin() != null && xsdCountingPattern.getMin() == 0
                    && xsdCountingPattern.getMax() == null) {

                // Case "CountingPattern" --> "(...)*":

                CountingPattern dtdCountingPattern = new CountingPattern(0, null);
                countingPatternMap.put(level, 1);
                Integer countingPatternMultiplicity = 1;
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }
                Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticles().getFirst(), level, countingPatternMap);
                if (newDTDParticle == null) {
                    return null;
                } else if (newDTDParticle instanceof AnyPattern) {
                    return new AnyPattern(ProcessContentsInstruction.Strict, "");
                } else {
                    dtdCountingPattern.addParticle(newDTDParticle);
                }
                dtdParticle = dtdCountingPattern;
            } else if (xsdCountingPattern.getMin() != null && xsdCountingPattern.getMin() == 1
                    && xsdCountingPattern.getMax() == null) {

                // Case "CountingPattern" --> "(...)+":

                CountingPattern dtdCountingPattern = new CountingPattern(1, null);
                countingPatternMap.put(level, 1);
                Integer countingPatternMultiplicity = 1;
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }
                Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticles().getFirst(), level, countingPatternMap);
                if (newDTDParticle == null) {
                    return null;
                } else if (newDTDParticle instanceof AnyPattern) {
                    return new AnyPattern(ProcessContentsInstruction.Strict, "");
                } else {
                    dtdCountingPattern.addParticle(newDTDParticle);
                }
                dtdParticle = dtdCountingPattern;
            } else if (xsdCountingPattern.getMin() != null && xsdCountingPattern.getMin() == 0
                    && xsdCountingPattern.getMax() != null && xsdCountingPattern.getMax() == 1) {

                // Case "CountingPattern" --> "(...)?":

                CountingPattern dtdCountingPattern = new CountingPattern(0, 1);
                countingPatternMap.put(level, 1);
                Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticles().getFirst(), level, countingPatternMap);
                if (newDTDParticle == null) {
                    return null;
                } else if (newDTDParticle instanceof AnyPattern) {
                    return new AnyPattern(ProcessContentsInstruction.Strict, "");
                } else {
                    dtdCountingPattern.addParticle(newDTDParticle);
                }
                dtdParticle = dtdCountingPattern;
            } else if (xsdCountingPattern.getMin() != null
                    && xsdCountingPattern.getMax() == null) {

                // Case "CountingPattern" --> (a)[2,unbounded] --> "(a,a,a*)":

                countingPatternMap.put(level, xsdCountingPattern.getMin());
                Integer countingPatternMultiplicity = 1;
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }
                if (XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY > 0 && countingPatternMultiplicity > XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY) {
                    CountingPattern dtdCountingPattern = new CountingPattern(0, null);
                    countingPatternMap.put(level, 1);
                    Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticles().getFirst(), level, countingPatternMap);
                    if (newDTDParticle == null) {
                        return null;
                    } else if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.Strict, "");
                    } else {
                        dtdCountingPattern.addParticle(newDTDParticle);
                    }
                    dtdParticle = dtdCountingPattern;
                } else {
                    Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticles().getFirst(), level, countingPatternMap);
                    if (newDTDParticle == null) {
                        return null;
                    } else if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.Strict, "");
                    }
                    SequencePattern dtdSequencePattern = new SequencePattern();
                    for (int i = 0; i < xsdCountingPattern.getMin(); i++) {
                        dtdSequencePattern.addParticle(newDTDParticle);
                    }
                    CountingPattern newOptionalCountingpattern = new CountingPattern(0, null);
                    newOptionalCountingpattern.addParticle(newDTDParticle);
                    dtdSequencePattern.addParticle(newOptionalCountingpattern);
                    dtdParticle = dtdSequencePattern;
                }
            } else if (xsdCountingPattern.getMin() != null
                    && xsdCountingPattern.getMax() != null) {

                // Case "CountingPattern" --> (a)[2,3] --> "(a,a,a?)":

                if (xsdCountingPattern.getMin() == 0 && xsdCountingPattern.getMax() == 0) {
                    return null;
                }

                countingPatternMap.put(level, xsdCountingPattern.getMax());
                Integer countingPatternMultiplicity = 1;
                for (Iterator<Integer> it = countingPatternMap.keySet().iterator(); it.hasNext();) {
                    Integer currentKey = it.next();
                    countingPatternMultiplicity = countingPatternMultiplicity * countingPatternMap.get(currentKey);
                }
                if (XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY > 0 && countingPatternMultiplicity > XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND_MULTIPLICITY) {
                    CountingPattern dtdCountingPattern = new CountingPattern(0, null);
                    countingPatternMap.put(level, 1);
                    Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticles().getFirst(), level, countingPatternMap);
                    if (newDTDParticle == null) {
                        return null;
                    } else if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.Strict, "");
                    } else {
                        dtdCountingPattern.addParticle(newDTDParticle);
                    }
                    dtdParticle = dtdCountingPattern;
                } else {
                    Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticles().getFirst(), level, countingPatternMap);
                    if (newDTDParticle == null) {
                        return null;
                    } else if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.Strict, "");
                    }
                    SequencePattern dtdSequencePattern = new SequencePattern();
                    for (int i = 0; i < xsdCountingPattern.getMin(); i++) {
                        dtdSequencePattern.addParticle(newDTDParticle);
                    }
                    CountingPattern newOptionalCountingpattern = new CountingPattern(0, 1);
                    newOptionalCountingpattern.addParticle(newDTDParticle);
                    for (int i = 0; i < xsdCountingPattern.getMax() - xsdCountingPattern.getMin(); i++) {
                        dtdSequencePattern.addParticle(newOptionalCountingpattern);
                    }
                    dtdParticle = dtdSequencePattern;
                }
            }
        }
        level--;
        return dtdParticle;
    }

    /**
     * Convert a XML Schema MIXED content model to its DTD counterpart
     * (Mixed content models are handled in this method.)
     *
     * @param particle      XSD particle structure
     * @param alreadySeen   reminder for already seen element names (recursion)
     * @return Particle     DTD particle structure
     */
    private LinkedList<Particle> convertMixedContentModel(Particle particle, HashSet<String> alreadySeen) {
        LinkedList<Particle> particleList = new LinkedList<Particle>();

        if (particle instanceof de.tudortmund.cs.bonxai.xsd.ElementRef) {

            // Case "ElementRef":

            de.tudortmund.cs.bonxai.xsd.ElementRef elementRef = (de.tudortmund.cs.bonxai.xsd.ElementRef) particle;
            if (elementRef.getElement() != null) {
                de.tudortmund.cs.bonxai.xsd.Element xsdElement = elementRef.getElement();
                DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
                String dtdElementName = dtdNameGenerator.getDTDElementName(xsdElement.getName(), xsdElement.getForm());

                SymbolTableRef<Element> symbolTableRef = new SymbolTableRef<Element>(dtdElementName, new Element(dtdElementName));
                ElementRef dtdElementRef = new ElementRef(symbolTableRef);

                this.addDTDElement(dtdElementName, null, dtdElementRef);

                if (!alreadySeen.contains(dtdElementName)) {
                    alreadySeen.add(dtdElementName);
                    particleList.add(dtdElementRef);
                }
            } else {
                // Failure with elementRef --> ANY
                particleList.add(new AnyPattern(ProcessContentsInstruction.Strict, ""));
            }
        } else if (particle instanceof de.tudortmund.cs.bonxai.xsd.Element) {

            // Case "Element":

            de.tudortmund.cs.bonxai.xsd.Element xsdElement = (de.tudortmund.cs.bonxai.xsd.Element) particle;

            String dtdElementName = null;
            SymbolTableRef<Element> symbolTableRef = null;
            
            if (!this.alreadyConvertedElements.contains(xsdElement)) {
                Element dtdContentElement = this.convertElement(xsdElement);
                dtdElementName = dtdContentElement.getName();
                symbolTableRef = this.dtd.getElementSymbolTable().updateOrCreateReference(dtdElementName, dtdContentElement);
            } else {
                DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
                dtdElementName = dtdNameGenerator.getDTDElementName(xsdElement.getName(), xsdElement.getForm());
                symbolTableRef = this.dtd.getElementSymbolTable().getReference(dtdElementName);
            }

            ElementRef dtdElementRef = new ElementRef(symbolTableRef);
            this.addDTDElement(dtdElementName, null, dtdElementRef);
            
            if (!alreadySeen.contains(dtdElementName)) {
                particleList.add(dtdElementRef);
                alreadySeen.add(dtdElementName);
            }
        } else if (particle instanceof SequencePattern) {

            // Case "SequencePattern":

            SequencePattern xsdSequencePattern = (SequencePattern) particle;
            for (Iterator<Particle> it = xsdSequencePattern.getParticles().iterator(); it.hasNext();) {
                Particle curentXSDParticle = it.next();
                LinkedList<Particle> innerParticleList = convertMixedContentModel(curentXSDParticle, alreadySeen);
                if (innerParticleList != null) {
                    particleList.addAll(innerParticleList);
                }
            }
        } else if (particle instanceof ChoicePattern) {

            // Case "ChoicePattern":

            ChoicePattern xsdChoicePattern = (ChoicePattern) particle;
            for (Iterator<Particle> it = xsdChoicePattern.getParticles().iterator(); it.hasNext();) {
                Particle curentXSDParticle = it.next();
                LinkedList<Particle> innerParticleList = convertMixedContentModel(curentXSDParticle, alreadySeen);
                if (innerParticleList != null) {
                    particleList.addAll(innerParticleList);
                }
            }
        } else if (particle instanceof AnyPattern) {

            // Case "AnyPattern":

            // ANY has to be set as root particle --> backtracking
            particleList.add(new AnyPattern(ProcessContentsInstruction.Strict, ""));
        } else if (particle instanceof AllPattern) {

            // Case "AllPattern":

            AllPattern xsdAllPattern = (AllPattern) particle;
            for (Iterator<Particle> it = xsdAllPattern.getParticles().iterator(); it.hasNext();) {
                Particle curentXSDParticle = it.next();
                LinkedList<Particle> innerParticleList = convertMixedContentModel(curentXSDParticle, alreadySeen);
                if (innerParticleList != null) {
                    particleList.addAll(innerParticleList);
                }
            }
        } else if (particle instanceof CountingPattern) {

            // Case "CountingPattern":

            CountingPattern xsdCountingPattern = (CountingPattern) particle;
            if (xsdCountingPattern.getMin() != null && xsdCountingPattern.getMin() == 0 && xsdCountingPattern.getMax() != null && xsdCountingPattern.getMax() == 0) {
                return null;
            } else {
                LinkedList<Particle> innerParticleList = convertMixedContentModel(xsdCountingPattern.getParticles().getFirst(), alreadySeen);
                if (innerParticleList != null) {
                    particleList.addAll(innerParticleList);
                }
            }
        }
        return particleList;
    }

    /**
     * Getter for the global xsd Constraints
     * @return LinkedHashSet<SimpleConstraint>      global xsd constraints
     */
    public LinkedHashSet<SimpleConstraint> getGlobalConstraints() {
        return globalConstraints;
    }
}

