package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.dtd.common.ElementContentModelProcessor;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SequencePattern;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Class ElementUnionBuilder
 *
 * Build the union of all dtdElements in the list of the elementWrapper.
 * replace the references of the elementRef-objects to the new dtdElement.
 *
 * @author Lars Schmidt
 */
public class ElementUnionBuilder {

    private ElementWrapper elementWrapper;

    /**
     * Constructor of class ElementUnionBuilder
     * @param elementWrapper
     */
    public ElementUnionBuilder(ElementWrapper elementWrapper) {
        this.elementWrapper = elementWrapper;
    }

    /**
     * Unify all elements contained in the given elementWrapper to one
     * @throws Exception
     */
    public void unifyElements() throws Exception {
        Stack<Element> elementStack = new Stack<Element>();
        elementStack.addAll(elementWrapper.getDTDElements());

        if (!elementStack.isEmpty()) {
            Element currentResultElement = elementStack.pop();

            if (!elementStack.isEmpty()) {
//                StatusLogger.logWarning("XSD2DTD", "Element \"" + elementWrapper.getDTDElementName() + "\" has " + elementWrapper.getDTDElements().size() + " occurrences in different locations in the XML XSDSchema document. They will be unified to an upper approximation!");
            }
            while (!elementStack.isEmpty()) {
                currentResultElement = this.unifyTwoElements(currentResultElement, elementStack.pop());
            }

            LinkedHashSet<Element> resultHashSet = new LinkedHashSet<Element>();
            resultHashSet.add(currentResultElement);
            this.elementWrapper.setDtdElements(resultHashSet);
        }
    }

    /**
     * Unify two DTD elements to one resulting element via approximation.
     * 
     * @param element1    The first element for the union.
     * @param element2    The second element for the union.
     * @return Element    The resulting DTD element.
     * @throws Exception
     */
    private Element unifyTwoElements(Element element1, Element element2) throws Exception {
        Element returnElement = null;

        ElementContentModelProcessor elementContentModelProcessor = new ElementContentModelProcessor();
        String element1CMString = elementContentModelProcessor.convertParticleToRegExpString(element1);
        String element2CMString = elementContentModelProcessor.convertParticleToRegExpString(element2);

        if (element1CMString.equals(element2CMString)) {
            // Compare the content model Strings, if they are equal, return element1
            returnElement = element1;
        } else {
            if (element1CMString.equals("ANY")) {
                // If the content model of element1 is equal to "ANY", return element1
                returnElement = element1;
            }

            if (element2CMString.equals("ANY")) {
                // If the content model of element2 is equal to "ANY", return element1
                returnElement = element2;
            }

            if (element1CMString.equals("EMPTY")) {
                // Idea: If one content model is equal to "EMPTY", return the other Element
                // Case: element1
                // Pay attention to some rules for optional content
                if (element2.getMixed()) {
                    returnElement = element2;
                } else if (element2.getParticle() instanceof CountingPattern) {
                    CountingPattern countingPattern = (CountingPattern) element2.getParticle();
                    if (countingPattern.getMin() == 1) {
                        CountingPattern newCountingPattern = new CountingPattern(0, countingPattern.getMax());
                        newCountingPattern.addParticle(countingPattern.getParticles().getFirst());
                        element2.setParticle(newCountingPattern);
                    }
                    returnElement = element2;
                } else if (element2.getParticle() instanceof SequencePattern) {
                    CountingPattern newCountingPattern = new CountingPattern(0, 1);
                    newCountingPattern.addParticle(element2.getParticle());
                    element2.setParticle(newCountingPattern);
                    returnElement = element2;
                } else if (element2.getParticle() instanceof ChoicePattern) {
                    CountingPattern newCountingPattern = new CountingPattern(0, 1);
                    newCountingPattern.addParticle(element2.getParticle());
                    element2.setParticle(newCountingPattern);
                    returnElement = element2;
                }
            }

            if (element2CMString.equals("EMPTY")) {
                // Idea: If one content model is equal to "EMPTY", return the other Element
                // Case: element2
                // Pay attention to some rules for optional content
                if (element1.getMixed()) {
                    returnElement = element1;
                } else if (element1.getParticle() instanceof CountingPattern) {
                    CountingPattern countingPattern = (CountingPattern) element1.getParticle();
                    if (countingPattern.getMin() == 1) {
                        CountingPattern newCountingPattern = new CountingPattern(0, countingPattern.getMax());
                        newCountingPattern.addParticle(countingPattern.getParticles().getFirst());
                        element1.setParticle(newCountingPattern);
                    }
                    returnElement = element1;
                } else if (element1.getParticle() instanceof SequencePattern) {
                    CountingPattern newCountingPattern = new CountingPattern(0, 1);
                    newCountingPattern.addParticle(element1.getParticle());
                    element1.setParticle(newCountingPattern);
                    returnElement = element1;
                } else if (element1.getParticle() instanceof ChoicePattern) {
                    CountingPattern newCountingPattern = new CountingPattern(0, 1);
                    newCountingPattern.addParticle(element1.getParticle());
                    element1.setParticle(newCountingPattern);
                    returnElement = element1;
                }
            }

            if (!element1.getMixed() && !element2.getMixed()) {

                // Case "NO mixed content":

                if (returnElement == null) {

                    // Unify complex content models
                    if (element1.getParticle() instanceof ChoicePattern && !(element2.getParticle() instanceof ChoicePattern)) {
                        ChoicePattern choicePattern1 = (ChoicePattern) element1.getParticle();

                        LinkedHashSet<String> regExpAlreadyInChoice = new LinkedHashSet<String>();
                        for (Iterator<Particle> it = choicePattern1.getParticles().iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            Element tempElement = new Element("temp");
                            tempElement.setParticle(particle);
                            String currentParticleRegExp = elementContentModelProcessor.convertParticleToRegExpString(tempElement);
                            regExpAlreadyInChoice.add(currentParticleRegExp);
                        }

                        if (!regExpAlreadyInChoice.contains(element2CMString)) {
                            choicePattern1.addParticle(element2.getParticle());
                        }
                        returnElement = element1;
                    } else if (element2.getParticle() instanceof ChoicePattern && !(element1.getParticle() instanceof ChoicePattern)) {
                        ChoicePattern choicePattern2 = (ChoicePattern) element2.getParticle();

                        LinkedHashSet<String> regExpAlreadyInChoice = new LinkedHashSet<String>();
                        for (Iterator<Particle> it = choicePattern2.getParticles().iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            Element tempElement = new Element("temp");
                            tempElement.setParticle(particle);
                            String currentParticleRegExp = elementContentModelProcessor.convertParticleToRegExpString(tempElement);
                            regExpAlreadyInChoice.add(currentParticleRegExp);
                        }

                        if (!regExpAlreadyInChoice.contains(element1CMString)) {
                            choicePattern2.addParticle(element1.getParticle());
                        }
                        returnElement = element2;
                    } else if (element1.getParticle() instanceof ChoicePattern && (element2.getParticle() instanceof ChoicePattern)) {
                        ChoicePattern choicePattern1 = (ChoicePattern) element1.getParticle();

                        LinkedHashSet<String> regExpAlreadyInChoice = new LinkedHashSet<String>();
                        for (Iterator<Particle> it = choicePattern1.getParticles().iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            Element tempElement = new Element("temp");
                            tempElement.setParticle(particle);
                            String currentParticleRegExp = elementContentModelProcessor.convertParticleToRegExpString(tempElement);
                            regExpAlreadyInChoice.add(currentParticleRegExp);
                        }

                        ChoicePattern choicePattern2 = (ChoicePattern) element2.getParticle();
                        for (Iterator<Particle> it = choicePattern2.getParticles().iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            Element tempElement = new Element("temp");
                            tempElement.setParticle(particle);
                            String currentParticleRegExp = elementContentModelProcessor.convertParticleToRegExpString(tempElement);
                            if (!regExpAlreadyInChoice.contains(currentParticleRegExp)) {
                                regExpAlreadyInChoice.add(currentParticleRegExp);
                                choicePattern1.addParticle(particle);
                            }
                        }
                        returnElement = element1;
                    } else {
                        ChoicePattern newChoicePattern = new ChoicePattern();
                        newChoicePattern.addParticle(element1.getParticle());
                        newChoicePattern.addParticle(element2.getParticle());
                        element1.setParticle(newChoicePattern);
                        returnElement = element1;
                    }

                    // Alternative solution for content model merging/combination: ANY
//                    element1.setParticle(new AnyPattern(ProcessContentsInstruction.STRICT, ""));
                    if (returnElement == null) {
                        returnElement = element1;
                    }
                }
            } else {

                // Case "mixed content":

                if (element1.getMixed() && element2.getMixed()) {

                    // Case "both elements have mixed content":

                    LinkedList<Particle> element1Particles = new LinkedList<Particle>();
                    if (element1.getParticle() instanceof CountingPattern) {
                        CountingPattern countingPattern = (CountingPattern) element1.getParticle();
                        if (!countingPattern.getParticles().isEmpty() && countingPattern.getParticles().getFirst() instanceof ChoicePattern) {
                            ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
                            element1Particles = choicePattern.getParticles();
                        }
                    }

                    if (element2.getParticle() instanceof CountingPattern) {
                        CountingPattern countingPattern = (CountingPattern) element2.getParticle();
                        if (!countingPattern.getParticles().isEmpty() && countingPattern.getParticles().getFirst() instanceof ChoicePattern) {
                            ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
                            for (Iterator<Particle> it = choicePattern.getParticles().iterator(); it.hasNext();) {
                                Particle particle = it.next();
                                if (particle instanceof ElementRef) {
                                    element1Particles.add(particle);
                                }
                            }
                        }
                    }

                    LinkedHashSet<String> alreadySeen = new LinkedHashSet<String>();
                    CountingPattern newCountingPattern = new CountingPattern(0, null);
                    ChoicePattern newChoicePattern = new ChoicePattern();
                    for (Iterator<Particle> it = element1Particles.iterator(); it.hasNext();) {
                        Particle particle = it.next();
                        if (!alreadySeen.contains(((ElementRef) particle).getElement().getName())) {
                            newChoicePattern.addParticle(particle);
                            alreadySeen.add(((ElementRef) particle).getElement().getName());
                        }
                    }
                    if (newChoicePattern.getParticles().isEmpty()) {
                        element1.setParticle(null);
                    } else {
                        newCountingPattern.addParticle(newChoicePattern);
                        element1.setParticle(newCountingPattern);
                    }
                    element1.setMixedStar(true);
                    returnElement = element1;

                } else if (element1.getMixed() && !element2.getMixed()) {

                    // Case "only element1 has mixed content":

                    LinkedList<Particle> particleList = extractElementRefsFromParticle(element2.getParticle());

                    boolean isAnyPattern = false;
                    // Loop over particleList and the the content to the choice
                    for (Iterator<Particle> it = particleList.iterator(); it.hasNext();) {
                        Particle particle = it.next();
                        if (particle instanceof AnyPattern) {
                            isAnyPattern = true;
                        }
                    }

                    if (isAnyPattern) {
                        element2.setParticle(new AnyPattern(ProcessContentsInstruction.STRICT, ""));
                        element2.setMixedStar(false);
                        element2.setMixed(false);
                        returnElement = element2;
                    } else {

                        LinkedList<Particle> element1Particles = new LinkedList<Particle>();
                        if (element1.getParticle() instanceof CountingPattern) {
                            CountingPattern countingPattern = (CountingPattern) element1.getParticle();
                            if (!countingPattern.getParticles().isEmpty() && countingPattern.getParticles().getFirst() instanceof ChoicePattern) {
                                ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
                                element1Particles = choicePattern.getParticles();
                            }
                        }

                        for (Iterator<Particle> it = particleList.iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            if (particle instanceof ElementRef) {
                                element1Particles.add(particle);
                            }
                        }

                        LinkedHashSet<String> alreadySeen = new LinkedHashSet<String>();
                        CountingPattern newCountingPattern = new CountingPattern(0, null);
                        ChoicePattern newChoicePattern = new ChoicePattern();
                        for (Iterator<Particle> it = element1Particles.iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            if (!alreadySeen.contains(((ElementRef) particle).getElement().getName())) {
                                newChoicePattern.addParticle(particle);
                                alreadySeen.add(((ElementRef) particle).getElement().getName());
                            }
                        }
                        if (newChoicePattern.getParticles().isEmpty()) {
                            element1.setParticle(null);
                        } else {
                            newCountingPattern.addParticle(newChoicePattern);
                            element1.setParticle(newCountingPattern);
                        }
                        element1.setMixedStar(true);
                        returnElement = element1;
                    }

                } else if (!element1.getMixed() && element2.getMixed()) {

                    // Case "only element2 has mixed content":

                    LinkedList<Particle> particleList = extractElementRefsFromParticle(element1.getParticle());

                    boolean isAnyPattern = false;
                    // Loop over particleList and the the content to the choice
                    for (Iterator<Particle> it = particleList.iterator(); it.hasNext();) {
                        Particle particle = it.next();
                        if (particle instanceof AnyPattern) {
                            isAnyPattern = true;
                        }
                    }

                    if (isAnyPattern) {
                        element1.setParticle(new AnyPattern(ProcessContentsInstruction.STRICT, ""));
                        element1.setMixedStar(false);
                        element1.setMixed(false);
                        returnElement = element1;
                    } else {

                        LinkedList<Particle> element2Particles = new LinkedList<Particle>();
                        if (element2.getParticle() instanceof CountingPattern) {
                            CountingPattern countingPattern = (CountingPattern) element2.getParticle();
                            if (!countingPattern.getParticles().isEmpty() && countingPattern.getParticles().getFirst() instanceof ChoicePattern) {
                                ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticles().getFirst();
                                element2Particles = choicePattern.getParticles();
                            }
                        }
                        for (Iterator<Particle> it = particleList.iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            if (particle instanceof ElementRef) {
                                element2Particles.add(particle);
                            }
                        }
                        LinkedHashSet<String> alreadySeen = new LinkedHashSet<String>();
                        CountingPattern newCountingPattern = new CountingPattern(0, null);
                        ChoicePattern newChoicePattern = new ChoicePattern();
                        for (Iterator<Particle> it = element2Particles.iterator(); it.hasNext();) {
                            Particle particle = it.next();
                            if (!alreadySeen.contains(((ElementRef) particle).getElement().getName())) {
                                newChoicePattern.addParticle(particle);
                                alreadySeen.add(((ElementRef) particle).getElement().getName());
                            }
                        }
                        if (newChoicePattern.getParticles().isEmpty()) {
                            element2.setParticle(null);
                        } else {
                            newCountingPattern.addParticle(newChoicePattern);
                            element2.setParticle(newCountingPattern);
                        }
                        element2.setMixedStar(true);
                        returnElement = element2;
                    }
                }
            }
        }

        // Debug
        if (XSD2DTDConverter.getDebug()) {
            System.out.println("Element: ");
            System.out.println("(A) " + element1.getName() + ": " + element1CMString);
            System.out.println("(B) " + element2.getName() + ": " + element2CMString + "\n");
            System.out.println("(A U B) result: " + elementContentModelProcessor.convertParticleToRegExpString(returnElement) + "\n\n");
        }
        // return the result
        return returnElement;
    }

    /**
     * Method for fetching all elementRefs and AnyPatterns from a given particle
     * @param particle      source for the extracting progress
     * @return LinkedList<Particle>     list of elementRefs and AnyPatterns
     */
    private LinkedList<Particle> extractElementRefsFromParticle(Particle particle) {
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        if (particle instanceof eu.fox7.schematoolkit.common.dtd.ElementRef) {

            // Add the ElementRef to the resulting list
            particleList.add(particle);

        } else if (particle instanceof SequencePattern) {
            SequencePattern dtdSequencePattern = (SequencePattern) particle;
            for (Iterator<Particle> it = dtdSequencePattern.getParticles().iterator(); it.hasNext();) {
                Particle curentDTDParticle = it.next();
                // Recursion
                particleList.addAll(extractElementRefsFromParticle(curentDTDParticle));
            }
        } else if (particle instanceof ChoicePattern) {
            ChoicePattern dtdChoicePattern = (ChoicePattern) particle;
            for (Iterator<Particle> it = dtdChoicePattern.getParticles().iterator(); it.hasNext();) {
                Particle curentDTDParticle = it.next();
                // Recursion
                particleList.addAll(extractElementRefsFromParticle(curentDTDParticle));
            }
        } else if (particle instanceof AnyPattern) {

            // Add the AnyPattern to the resulting list
            particleList.add(particle);

        } else if (particle instanceof CountingPattern) {
            CountingPattern dtdCountingPattern = (CountingPattern) particle;
            // Recursion
            particleList.addAll(extractElementRefsFromParticle(dtdCountingPattern.getParticles().getFirst()));
        }
        return particleList;
    }
}


