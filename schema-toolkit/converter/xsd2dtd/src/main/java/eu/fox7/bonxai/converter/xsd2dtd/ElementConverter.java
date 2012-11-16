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

package eu.fox7.bonxai.converter.xsd2dtd;

import java.util.LinkedHashMap;

import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Constraint;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.Key;
import eu.fox7.schematoolkit.xsd.om.KeyRef;
import eu.fox7.schematoolkit.xsd.om.SimpleConstraint;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Converter class for XSD elements and generation of DTD content models
 * @author Lars Schmidt
 */
public class ElementConverter {
    private XSDSchema xmlSchema;
    private DocumentTypeDefinition dtd;
    private LinkedHashSet<SimpleConstraint> globalConstraints;
    // HashSet used for marking already converted elements
    private HashSet<eu.fox7.schematoolkit.xsd.om.Element> alreadyConvertedElements;

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
        this.globalConstraints = new LinkedHashSet<SimpleConstraint>();
        this.alreadyConvertedElements = new HashSet<eu.fox7.schematoolkit.xsd.om.Element>();
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
     public void addConstraints(QualifiedName dtdName, LinkedList<Constraint> xsdConstraints) {
//        for (Iterator<Constraint> it = xsdConstraints.iterator(); it.hasNext();) {
//            Constraint constraint = it.next();
//
//            if (constraint instanceof Key) {
//                Key key = (Key) constraint;
//
//                if (key.getSelector().matches("\\.//[a-zA-Z0-9\\*]*")) {
//                    String selectorName = key.getSelector().substring(key.getSelector().lastIndexOf("/") + 1, key.getSelector().length());
//                    if (selectorName.equals("*")) {
//                        // put Constraint to ALL elements.
//                        this.globalConstraints.add(key);
//                    } else {
//                        // put Constraint to element with given selector name
//                        ElementWrapper elementWrapper = null;
//                        if (this.elementMap.get(selectorName) == null) {
//                            elementWrapper = new ElementWrapper(xmlSchema, selectorName);
//                            this.elementMap.put(dtdName, elementWrapper);
//                        } else {
//                            elementWrapper = this.elementMap.get(selectorName);
//                        }
//                        elementWrapper.addXSDConstraint(key);
//                    }
//                }
//            } else if (constraint instanceof KeyRef) {
//                KeyRef keyRef = (KeyRef) constraint;
//                if (keyRef.getSelector().matches("\\.//[a-zA-Z0-9\\*]*")) {
//                    String selectorName = keyRef.getSelector().substring(keyRef.getSelector().lastIndexOf("/") + 1, keyRef.getSelector().length());
//                    if (selectorName.equals("*")) {
//                        // put Constraint to ALL elements.
//                        this.globalConstraints.add(keyRef);
//                    } else {
//                        // put Constraint to element with given selector name
//                        ElementWrapper elementWrapper = null;
//                        if (this.elementMap.get(selectorName) == null) {
//                            elementWrapper = new ElementWrapper(xmlSchema, selectorName);
//                            this.elementMap.put(dtdName, elementWrapper);
//                        } else {
//                            elementWrapper = this.elementMap.get(selectorName);
//                        }
//                        elementWrapper.addXSDConstraint(keyRef);
//                    }
//                }
//            }
//        }
    }

    /**
     * Method: convertElement
     *
     * Conversion of a XML Schema element and its particle structure within a
     * type to the corresponding DTD element counterpart
     *
     * @param element       eu.fox7.schematoolkit.xsd.om.Element
     * @return  Element     eu.fox7.bonxai.dtd.Element
     */
    public Element convertElement(QualifiedName elementName, Type type) {
        // Initialize the DTDNameGenerator
        DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
        String dtdElementName = dtdNameGenerator.getDTDElementName(elementName);

        // Prepare the result
        Element dtdElement = new Element(new QualifiedName(Namespace.EMPTY_NAMESPACE,dtdElementName));

        Particle dtdParticle = null;

        if (type != null) {
            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;
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
                                    dtdParticle = new AnyPattern(ProcessContentsInstruction.STRICT, "");
                                } else {
                                    // Mixed content has to be framed with a countingPattern (...)*
                                    dtdParticle = new CountingPattern(dtdChoicePattern, 0, null);
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
            } else if (type instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) type;
                if (simpleType.getLocalName().equals("anyType")) {
                    // Case ANY:
                    dtdParticle = new AnyPattern(ProcessContentsInstruction.STRICT, "");
                } else {
                    // CASE (#PCDATA)*:
                    dtdElement.setMixedStar(true);
                }
            }
        } else {
            // Case EMPTY:
            dtdElement.setMixedStar(false);
            // ANY type
            dtdParticle = new AnyPattern(ProcessContentsInstruction.STRICT, "");
        }

        if (dtdParticle != null) {
            dtdElement.setParticle(dtdParticle);
        }

        // Convert attributes
        AttributeConverter attributeConverter = new AttributeConverter(this.xmlSchema);
        LinkedList<Attribute> dtdAttributes = attributeConverter.convertAttributes(element);
        
        for (Attribute attribute: dtdAttributes)
        	dtdElement.addAttribute(attribute);

        // Handle Constraints for DTD ID/IDREF
        if (element.getConstraints() != null && !element.getConstraints().isEmpty()) {
            this.addConstraints(dtdElement.getName(), element.getConstraints());
        }

        // return the result
        return dtdElement;
    }

    /**
     * Convert a given XML Schema Particle to its DTD counterpart
     * @param particle      XML Schema particle as source of the conversion
     * @return Particle     DTD particle as result
     */
    private Particle convertContentModel(Particle particle) {
        Particle dtdParticle = null;
        if (particle instanceof eu.fox7.schematoolkit.common.ElementRef) {
            // Case "ElementRef":
            eu.fox7.schematoolkit.common.ElementRef elementRef = (eu.fox7.schematoolkit.common.ElementRef) particle;
            eu.fox7.schematoolkit.xsd.om.Element xsdElement = this.xmlSchema.getElement(elementRef);
            if (xsdElement != null) {
                DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
                QualifiedName dtdElementName = dtdNameGenerator.getDTDElementName(xsdElement.getName(), xsdElement.getForm());

                ElementRef dtdElementRef = new ElementRef(dtdElementName);

                dtdParticle = dtdElementRef;
            } else {
                // Failure with elementRef --> ANY
                dtdParticle = new AnyPattern(ProcessContentsInstruction.STRICT, "");
            }
        } else if (particle instanceof eu.fox7.schematoolkit.xsd.om.Element) {
            // Case "Element":
            eu.fox7.schematoolkit.xsd.om.Element xsdElement = (eu.fox7.schematoolkit.xsd.om.Element) particle;

            dtdParticle = new ElementRef(xsdElement.getName());
        } else if (particle instanceof SequencePattern) {
            // Case "SequencePattern":
            SequencePattern dtdSequencePattern = new SequencePattern();
            SequencePattern xsdSequencePattern = (SequencePattern) particle;
            for (Particle curentXSDParticle: xsdSequencePattern.getParticles()) {
                Particle newDTDParticle = convertContentModel(curentXSDParticle);
                if (newDTDParticle != null) {
                    dtdSequencePattern.addParticle(newDTDParticle);
                }
                if (newDTDParticle instanceof AnyPattern) {
                    return new AnyPattern(ProcessContentsInstruction.STRICT, "");
                }
            }
            dtdParticle = dtdSequencePattern;
        } else if (particle instanceof ChoicePattern) {
            // Case "ChoicePattern":
            ChoicePattern dtdChoicePattern = new ChoicePattern();
            ChoicePattern xsdChoicePattern = (ChoicePattern) particle;
            for (Particle curentXSDParticle: xsdChoicePattern.getParticles()) {
                Particle newDTDParticle = convertContentModel(curentXSDParticle);
                if (newDTDParticle != null) {
                    dtdChoicePattern.addParticle(newDTDParticle);
                }
                if (newDTDParticle instanceof AnyPattern) {
                    return new AnyPattern(ProcessContentsInstruction.STRICT, "");
                }
            }
            dtdParticle = dtdChoicePattern;
        } else if (particle instanceof AnyPattern) {
            // Case "AnyPattern":
            // ANY has to be set as root particle --> backtracking
            dtdParticle = new AnyPattern(ProcessContentsInstruction.STRICT, "");
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
                    Particle newDTDParticle = convertContentModel(curentXSDParticle);
                    if (newDTDParticle != null) {
                        particleList.add(newDTDParticle);
                    }
                    if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.STRICT, "");
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
                    Particle newDTDParticle = convertContentModel(curentXSDParticle);
                    if (newDTDParticle != null) {
                        dtdChoicePattern.addParticle(newDTDParticle);
                    }
                    if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.STRICT, "");
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
                    Particle newDTDParticle = convertContentModel(curentXSDParticle);
                    if (newDTDParticle != null) {
                        dtdChoicePattern.addParticle(newDTDParticle);
                    }
                    if (newDTDParticle instanceof AnyPattern) {
                        return new AnyPattern(ProcessContentsInstruction.STRICT, "");
                    }
                }
                dtdParticle = new CountingPattern(dtdChoicePattern, 0, null);
            } else {
                for (Iterator<Particle> it = xsdAllPattern.getParticles().iterator(); it.hasNext();) {
                    Particle curentXSDParticle = it.next();
                    // The individual xsdParticles have to be converted for writing out declarations of new elements in the DTD,
                    // which are not used in other positions
                    convertContentModel(curentXSDParticle);
                }
                dtdParticle = new AnyPattern(ProcessContentsInstruction.STRICT, "");
            }
        } else if (particle instanceof CountingPattern) {
            // Case "CountingPattern":
            CountingPattern xsdCountingPattern = (CountingPattern) particle;
            int min = xsdCountingPattern.getMin();
            Integer max = xsdCountingPattern.getMax();

            Particle newDTDParticle = convertContentModel(xsdCountingPattern.getParticle());
            if (newDTDParticle == null) {
                return null;
            } else if (newDTDParticle instanceof AnyPattern) {
                return new AnyPattern(ProcessContentsInstruction.STRICT, "");
            }

            if ((max != null) && (XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND > 0 && max > XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND))
            	max = null;
            
            if ((max == null) && (XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND > 0 && min > XSD2DTDConverter.COUNTINGPATTERN_UPPER_BOUND))
            	min = 0;
            
            if ((min==0 && max==null) ||
                (min==1 && max==null) ||
                (min==0 && max.equals(1))) {
               	dtdParticle = new CountingPattern(newDTDParticle, min, max);
            } else if (max == null) {
                // Case "CountingPattern" --> (a)[2,unbounded] --> "(a,a,a*)":
            	SequencePattern dtdSequencePattern = new SequencePattern();
            	for (int i = 0; i < min; i++) {
            		dtdSequencePattern.addParticle(newDTDParticle);
            	}
            	CountingPattern newOptionalCountingpattern = new CountingPattern(newDTDParticle, 0, null);
            	dtdSequencePattern.addParticle(newOptionalCountingpattern);
            	dtdParticle = dtdSequencePattern;
            } else if (max != null) {
                // Case "CountingPattern" --> (a)[2,3] --> "(a,a,a?)":
                if (min == 0 && max == 0) {
                    return null;
                }

                SequencePattern dtdSequencePattern = new SequencePattern();
                for (int i = 0; i < xsdCountingPattern.getMin(); i++) {
                	dtdSequencePattern.addParticle(newDTDParticle);
                }
                
                if (max > min) { // (a(a(a)?)?)?
                	CountingPattern newOptionalCountingpattern = new CountingPattern(newDTDParticle, 0, 1);
                	for (int i = 0; i < max - min - 1; i++) {
                		SequencePattern sp = new SequencePattern();
                		sp.addParticle(newDTDParticle);
                		sp.addParticle(newOptionalCountingpattern);
                		newOptionalCountingpattern = new CountingPattern(sp, 0, 1);
                	}
                	dtdSequencePattern.addParticle(newOptionalCountingpattern);
                }
                dtdParticle = dtdSequencePattern;
            }
        }
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
    private LinkedList<Particle> convertMixedContentModel(Particle particle, HashSet<QualifiedName> alreadySeen) {
        LinkedList<Particle> particleList = new LinkedList<Particle>();

        if (particle instanceof eu.fox7.schematoolkit.common.ElementRef) {
            // Case "ElementRef":
            eu.fox7.schematoolkit.common.ElementRef elementRef = (eu.fox7.schematoolkit.common.ElementRef) particle;
            eu.fox7.schematoolkit.xsd.om.Element xsdElement = this.xmlSchema.getElement(elementRef);
            if (xsdElement != null) {
                DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(xmlSchema);
                QualifiedName dtdElementName = dtdNameGenerator.getDTDElementName(xsdElement.getName(), xsdElement.getForm());

                ElementRef dtdElementRef = new ElementRef(dtdElementName);

                if (!alreadySeen.contains(dtdElementName)) {
                    alreadySeen.add(dtdElementName);
                    particleList.add(dtdElementRef);
                }
            } else {
                // Failure with elementRef --> ANY
                particleList.add(new AnyPattern(ProcessContentsInstruction.STRICT, ""));
            }
        } else if (particle instanceof eu.fox7.schematoolkit.xsd.om.Element) {
            // Case "Element":
            eu.fox7.schematoolkit.xsd.om.Element xsdElement = (eu.fox7.schematoolkit.xsd.om.Element) particle;

            if (!alreadySeen.contains(dtdElementName)) {
                particleList.add(dtdElementRef);
                alreadySeen.add(dtdElementName);
            }
            dtdParticle = new ElementRef(xsdElement.getName());
        } else if (particle instanceof eu.fox7.schematoolkit.xsd.om.Element) {
            // Case "Element":
            eu.fox7.schematoolkit.xsd.om.Element xsdElement = (eu.fox7.schematoolkit.xsd.om.Element) particle;

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
        } else if (particle instanceof ParticleContainer) {
            // Case "SequencePattern", ChoicePattern or AllPattern:
            ParticleContainer xsdParticleContainer = (ParticleContainer) particle;
            for (Particle curentXSDParticle: xsdParticleContainer.getParticles()) {
                LinkedList<Particle> innerParticleList = convertMixedContentModel(curentXSDParticle, alreadySeen);
                if (innerParticleList != null) {
                    particleList.addAll(innerParticleList);
                }
            }
        } else if (particle instanceof AnyPattern) {
            // Case "AnyPattern":
            // ANY has to be set as root particle --> backtracking
            particleList.add(new AnyPattern(ProcessContentsInstruction.STRICT, ""));
        } else if (particle instanceof CountingPattern) {
            // Case "CountingPattern":
            CountingPattern xsdCountingPattern = (CountingPattern) particle;
            if (xsdCountingPattern.getMin() == 0 && xsdCountingPattern.getMax() != null && xsdCountingPattern.getMax() == 0) {
                return null;
            } else {
                LinkedList<Particle> innerParticleList = convertMixedContentModel(xsdCountingPattern.getParticle(), alreadySeen);
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

