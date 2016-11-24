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

package eu.fox7.schematoolkit.dtd.common;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelCountingPatternNotAllowedDTDValueException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelEmptyChildParticleListException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalMixedDuplicateElementException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalMixedParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelNullParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelStringEmptyException;
import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.schematoolkit.dtd.om.Element;

import java.util.HashSet;

/**
 * Helper class for extracting a particle from a given ContentModel-String or
 * in direction of generating a regular expression string from a given
 * element and its particle.
 * 
 * @author Lars Schmidt
 */
public class ElementContentModelProcessor {
    /**
     * Constructor of class ElementContentModelProcessor without any parameter
     * This is used in the case: writing the content particle of a given element
     * to a regular expression string in a DTD element content model
     */
    public ElementContentModelProcessor() {
    }



    /**
     * Public method: convertParticleToRegExpString generates a regular
     * expression string for the use in a resulting DTD file from a given
     * element (particle + mixed/empty).
     * @param element
     * @return String
     * @throws Exception
     */
    public String convertParticleToRegExpString(Element element) throws DTDException {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String currentRegExpString = "";

        // Differentiate between the possible types of the given element
        if (element.hasAnyType()) {
            // Element has the "any"-Type
            currentRegExpString = "ANY";
        } else if (!element.getMixed() && !element.getMixedStar() && element.isEmpty()) {
            // Element is "empty"
            currentRegExpString = "EMPTY";
        } else if (element.getMixed() && !element.getMixedStar() && element.isEmpty()) {
            // Element has the "mixed"-Type and no particle (is empty)
            currentRegExpString = "(#PCDATA)";
        } else if (element.getMixed() && element.getMixedStar() && element.isEmpty()) {
            // Element has the "mixed"-Type and no particle (is empty)
            currentRegExpString = "(#PCDATA)*";
        } else if (element.getMixed() && !element.isEmpty()) {
            // Element has the "mixed"-Type and a particle (not empty)
            String mixedParticleString = "";

            // There is only a choice in a countingPattern (...|...|...)* allowed here!
            // Every particle in the choicePattern has to be an ElementRef!
            if (!(element.getParticle() instanceof CountingPattern)) {
                throw new ContentModelIllegalMixedParticleException("Element (" + element.getName() + ") content model child instance: " + element.getParticle().getClass().getName() + ". Only CountingPattern is allowed here.");
            }

            // The particle of the element has to be of type CountingPattern
            CountingPattern countingPattern = (CountingPattern) element.getParticle();

            // The min value of the countingpattern has to be at last 1
            if (!(countingPattern.getMin() == 0 && countingPattern.getMax() == null)) {
                throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": CountingPattern values (minOccurs: " + countingPattern.getMin() + ", maxOccurs: " + countingPattern.getMax() + ")");
            }

            // The two only allowed particle of the countingPattern are choicPattern or elementRef
            if (!(countingPattern.getParticle() instanceof ChoicePattern) && !(countingPattern.getParticle() instanceof ElementRef)) {
                throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": CountingPattern child instance: " + countingPattern.getParticle().getClass().getName());
            }

            if (countingPattern.getParticle() instanceof ChoicePattern) {
                // Choicepattern
                ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticle();

                // The choicePattern is not allowed to be empty or null
                if (choicePattern.getParticles() == null || choicePattern.getParticles().isEmpty()) {
                    throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": ChoicePattern is empty");
                }

                HashSet<String> elementNames = new HashSet<String>();

                // The only allowed type of the content of the choicePattern is ElementRef
                for (Particle currentParticle : choicePattern.getParticles()) {
                    if (currentParticle instanceof ElementRef) {
                        ElementRef elementRef = (ElementRef) currentParticle;
                        if (elementRef.getElementName() == null) {
                            // elementRef element is null
                            throw new ContentModelNullParticleException("ElementRef", "Element is null");
                        }
                        QualifiedName currentElementName = elementRef.getElementName();

                        if (!nameChecker.checkForXMLName(currentElementName)) {
                            throw new IllegalNAMEStringException("Element: ", currentElementName.getName());
                        }

                        if (!elementNames.contains(currentElementName)) {
                            elementNames.add(currentElementName.getName());
                            mixedParticleString += currentElementName.getName() + "|";
                        } else {
                            throw new ContentModelIllegalMixedDuplicateElementException("Element \"" + element.getName() + "\": ChoicePattern contains more than one of: \"" + currentElementName + "\"");
                        }
                    } else {
                        throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": ChoicePattern child instance: " + currentParticle.getClass().getName());
                    }
                }

            } else if (countingPattern.getParticle() instanceof ElementRef) {
            	ElementRef elementRef = (ElementRef) countingPattern.getParticle();
            	if (elementRef.getElementName() == null) {
            		// elementRef element is null
            		throw new ContentModelNullParticleException("ElementRef", "Element is null");
            	}

            	if (!nameChecker.checkForXMLName(elementRef.getElementName())) {
            		throw new IllegalNAMEStringException("Element: ", elementRef.getElementName().getName());
            	}
            	mixedParticleString += elementRef.getElementName().getName() + "|";
            }
            

            mixedParticleString = mixedParticleString.substring(0, mixedParticleString.length() - 1);

            // Combine the generated string with the standard frame for mixed content model elements
            currentRegExpString = "(#PCDATA|" + mixedParticleString + ")*";

        } else if (!element.getMixed() && !element.isEmpty()) {
            // Case: normal content model
            currentRegExpString = convertParticleToRegExpString(element.getParticle(), element.getName());
            if (!currentRegExpString.startsWith("(")) {
                currentRegExpString = "(" + currentRegExpString + ")";
            }

        }
        return currentRegExpString;
    }

    /**
     * Private method: convertParticleToRegExpString is used by the
     * convertParticleToRegExpString method and recursivly handles the particle
     * of the given Element.
     * @param particle, elementName
     * @return String
     * @throws Exception
     */
    private String convertParticleToRegExpString(Particle particle, QualifiedName elementName) throws DTDException {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String currentRegExpString = "";

        // Handle the normal particle content of an element content model
        if (particle instanceof AnyPattern) {
            // Case: ANY
            currentRegExpString = "ANY";
        } else if (particle instanceof ChoicePattern) {
            // Case: CHOICE
            ChoicePattern choicePattern = (ChoicePattern) particle;

            if (choicePattern == null || choicePattern.getParticles().isEmpty()) {
                throw new ContentModelEmptyChildParticleListException("choicePattern");
            }

            currentRegExpString += "(";
            for (Particle currentChildParticle : choicePattern.getParticles()) {
                currentRegExpString += convertParticleToRegExpString(currentChildParticle, elementName) + "|";
            }
            currentRegExpString = currentRegExpString.substring(0, currentRegExpString.length() - 1) + ")";
        } else if (particle instanceof SequencePattern) {
            // Case: SEQUENCE
            SequencePattern sequencePattern = (SequencePattern) particle;

            if (sequencePattern == null || sequencePattern.getParticles().isEmpty()) {
                throw new ContentModelEmptyChildParticleListException("sequencePattern");
            }

            currentRegExpString += "(";
            for (Particle currentChildParticle : sequencePattern.getParticles()) {
                currentRegExpString += convertParticleToRegExpString(currentChildParticle, elementName) + ",";
            }
            currentRegExpString = currentRegExpString.substring(0, currentRegExpString.length() - 1) + ")";
        } else if (particle instanceof CountingPattern) {
            // Case: CountingPattern
            CountingPattern countingPattern = (CountingPattern) particle;

            if (countingPattern == null || countingPattern.getParticle() == null) {
                throw new ContentModelEmptyChildParticleListException("countingPattern");
            }

            String countingPatternType = "";
            
            if (countingPattern.getMin() == 0 && countingPattern.getMax() == null) {
                countingPatternType = "*";
            } else if (countingPattern.getMin() == 1 && countingPattern.getMax() == null) {
                countingPatternType = "+";
            } else if (countingPattern.getMin() == 0 && countingPattern.getMax() == 1) {
                countingPatternType = "?";
            } else {
                throw new ContentModelCountingPatternNotAllowedDTDValueException("minOccurs: " + countingPattern.getMin() + "maxOccurs: " + countingPattern.getMax());
            }

            if ((countingPattern.getParticle() instanceof CountingPattern) || (countingPattern.getParticle() instanceof ElementRef)) {
                currentRegExpString += "(" + convertParticleToRegExpString(countingPattern.getParticle(), elementName) + ")" + countingPatternType;
            } else {
                currentRegExpString += convertParticleToRegExpString(countingPattern.getParticle(), elementName) + countingPatternType;
            }
        } else if (particle instanceof ElementRef) {
            // Case: ElementRef
            ElementRef elementRef = (ElementRef) particle;
            if (elementRef.getElementName() == null) {
                // elementRef element is null
                throw new ContentModelNullParticleException("ElementRef", "Element is null");
            }
            if (!nameChecker.checkForXMLName(elementRef.getElementName())) {
                throw new IllegalNAMEStringException("Element: " + elementRef.getElementName(), elementRef.getElementName().getName());
            }
            currentRegExpString += elementRef.getElementName().getName();
        } else if (particle instanceof EmptyPattern) {
        	currentRegExpString += "EMPTY";
        } else if (particle == null) {
        	// Particle is null
            throw new ContentModelNullParticleException(elementName.getName(), "Particle is null");
        } else {
            // Particle is of illegal type
            throw new ContentModelIllegalParticleException(elementName.getName(), particle.getClass().getName());
        }

        // The regular expression string is not allowed to be empty here
        if (currentRegExpString.equals("")) {
            throw new ContentModelStringEmptyException();
        }

        // return the resulting regExpString
        return currentRegExpString;
    }
}

