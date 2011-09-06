package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.ElementRef;
import eu.fox7.bonxai.xsd.automaton.*;

import java.util.*;

/**
 * This class represents a particle transition. Particle transitions are similar
 * to normal transitions but contain whole particles. In order to work with such
 * particles methods are provided.
 * 
 * @author Dominik Wolff
 */
public class ParticleTransition extends Transition {

    // Boolean field which indicates if the transition is annotated with an epsilon.
    private boolean epsilon;

    // Set of any patterns contained in the particle annotated to the transition.
    private LinkedHashSet<AnyPattern> anyPatterns;
    
    // Particle annotated to this transition.
    private Particle resultingParticle;

    /**
     * Constructor of the <tt>ParticleTransition</tt> which initializes each 
     * field and adds a source and destination to the transition. If either one 
     * of them is not specified the transition can not be constructed.
     *
     * @param sourceState Source of the new transition.
     * @param destinationState Destination of the new transition.
     * @param epsilon Specifies if an epsilon is annotated to the transition.
     */
    public ParticleTransition(State sourceState, State destinationState, boolean epsilon) {
        super(sourceState, destinationState);

        // Initialize class fields
        this.epsilon = epsilon;
        this.anyPatterns = new LinkedHashSet<AnyPattern>();
        this.resultingParticle = null;
    }

    /**
     * Appends all of the any patterns in the specified set to the transition
     * field of annotated any patterns. Basically for convenience purpose only.
     *
     * @param newAnyPatterns <tt>LinkedHashSet</tt> which is added to the
     * current annotated any patterns of the transtions.
     */
    public void addAllAnyPatterns(LinkedHashSet<AnyPattern> newAnyPatterns) {
        for (AnyPattern anyPattern : newAnyPatterns) {
            addAnyPattern(anyPattern);
        }
    }

    /**
     * Adds the specified any pattern to the set of any patterns if it is not
     * already present. If the set of any patterns already contains the
     * any pattern, the call leaves the set unchanged and returns
     * <tt>false</tt>. The resulting particle of this transition is updated
     * accordingly.
     *
     * @param anyPattern Pattern which is to be added to the set of any
     * patterns.
     * @return <tt>true</tt> if the set of any patterns did not already contain
     * the specified any pattern.
     */
    public boolean addAnyPattern(AnyPattern anyPattern) {

        // If any pattern can be added to the set the resulting particle is updated
        if (anyPatterns.add(anyPattern)) {

            // If the resulting praticle is empty the any pattern is the new particle
            if (resultingParticle == null) {
                resultingParticle = anyPattern;
            } else {

                // Check if the resulting particle is a choice pattern
                if (resultingParticle instanceof ChoicePattern) {
                    ChoicePattern choicePattern = (ChoicePattern) resultingParticle;

                    // Add any pattern to choice pattern
                    choicePattern.addParticle(anyPattern);
                } else {

                    // Because resulting particle is no choice pattern create new choice pattern and add any pattern and resulting particle to the new choice pattern
                    ChoicePattern newChoicePattern = new ChoicePattern();
                    newChoicePattern.addParticle(anyPattern);
                    newChoicePattern.addParticle(resultingParticle);
                    resultingParticle = newChoicePattern;
                }
            }

            // If any pattern could be add return true.
            return true;
        }

        // Any pattern could not be added to anyPatterns set
        return false;
    }

    /**
     * Adds the specified element to the set of elements if it is not already
     * present. If the set of elements already contains the element, the call
     * leaves the set unchanged and returns <tt>false</tt>. The resulting
     * particle of the transition is updated accordingly.
     *
     * (Also updates the nameElementMap <tt>HashMap</tt>, but only if the
     * element can be added to the set and the name is not already contained.)
     *
     *
     * @param element Element which is to be added to the set of elements.
     * @return <tt>true</tt> if the set of elements did not already contain the
     * specified element.
     */
    @Override
    public boolean addElement(Element element) {

        // If the element can be added to the set the resulting particle is updated
        if (super.addElement(element)) {

            // If the resulting praticle is empty the element is the new particle
            if (resultingParticle == null) {
                resultingParticle = element;
            } else {

                // Check if the resulting particle is a choice pattern
                if (resultingParticle instanceof ChoicePattern) {
                    ChoicePattern choicePattern = (ChoicePattern) resultingParticle;

                    // Add element to choice pattern
                    choicePattern.addParticle(element);
                } else {

                    // Because resulting particle is no choice pattern create new choice pattern and add element and resulting particle to the new choice pattern
                    ChoicePattern newChoicePattern = new ChoicePattern();
                    newChoicePattern.addParticle(element);
                    newChoicePattern.addParticle(resultingParticle);
                    resultingParticle = newChoicePattern;
                }
            }

            // If element could be add return true.
            return true;
        }

        // Element could not be added to anyPatterns set
        return false;
    }

    /**
     * Returns a <tt>LinkedHashSet</tt> which contains all any patterns
     * annotated to this transition.
     *
     * @return <tt>LinkedHashSet</tt> of any patterns contained by this
     * transition.
     */
    public LinkedHashSet<AnyPattern> getAnyPatterns() {
        return new LinkedHashSet<AnyPattern>(anyPatterns);
    }

    /**
     * This method returns the resulting particle annotated to the transition.
     * This particle should contain the elements and any patterns stored in its
     * fields.
     *
     * @return Particle which is contained by the ParticleTransition.
     */
    public Particle getResultingParticle() {
        return resultingParticle;
    }

    /**
     * Returns <tt>true</tt> if the resulting particle of this transition is
     * optional. If this is the case the transition can be traversed without
     * reading the particles or any patterns annotated to the transition.
     * 
     * @return <tt>true</tt> if field epsilon is <tt>true</tt>.
     */
    public boolean hasEpsilon() {
        return epsilon;
    }

    /**
     * This method sets the boolean epsilon field of the transition. If it is
     * <tt>true<tt> the transition can be traversed without reading any other
     * particle.
     * 
     * @param epsilon Specifies if an epsilon is annotated to the transition.
     */
    public void setEpsilon(boolean epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Sets a new Particle for the ParticleTransition. This method updates the
     * element and any pattern sets accordingly. So the sets are still valid.
     *
     * @param resultingParticle New particle which should be annotated to the
     * transtition.
     */
    public void setResultingParticle(Particle resultingParticle) {

        // Set new resulting particle
        this.resultingParticle = resultingParticle;

        // Clear any pattern and element sets
        this.anyPatterns.clear();
        this.elements.clear();

        // Create stack to traverse the resulting particle
        Stack<Particle> particleStack = new Stack<Particle>();
        particleStack.add(resultingParticle);

        // As long as the stack is not empty check next particle
        while (!particleStack.isEmpty()) {

            // Get next particle from the particle stack
            Particle particle = particleStack.pop();

            if (particle instanceof ParticleContainer) {
                ParticleContainer particleContainer = (ParticleContainer) particle;

                // If the current particle is a particle container add all contained particles to the stack
                particleStack.addAll(particleContainer.getParticles());

            } else if (particle instanceof Element) {
                Element element = (Element) particle;

                // If the current particle is an element add it to the element set
                this.elements.add(element);

            } else if (particle instanceof ElementRef) {
                ElementRef elementRef = (ElementRef) particle;

                // If the current particle is an element referende add the referred element to the element set
                this.elements.add(elementRef.getElement());

            } else if (particle instanceof AnyPattern) {
                AnyPattern anyPattern = (AnyPattern) particle;

                // If the current particle is an any pattern add it to the any pattern set
                this.anyPatterns.add(anyPattern);

            } else if (particle instanceof GroupRef) {
                GroupRef groupRef = (GroupRef) particle;

                // If the current particle is a group reference add all contained particles to the stack
                particleStack.addAll(groupRef.getGroup().getParticleContainer().getParticles());
            }
        }
    }

    /**
     * Returns a string representation of the <tt>ParticleTransition</tt>. The
     * result should be a concise but informative representation that is easy
     * for a person to read. In order to achieve this the result is a string
     * that can be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>ParticleTransition</tt>.
     */
    @Override
    public String toString() {
        String output = "";

        // Add output for transition from source to destination state
        output += "\"" + getSourceState() + "\"";
        output += "->";
        output += "\"" + getDestinationState() + "\"";

        // Begin transition label output
        output += "[label=\"(";

        // Iterate over all elements and add them to the transition
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();
            output += element.getName();

            // Check if a next particle or a next any pattern exist 
            if (it.hasNext() || !anyPatterns.isEmpty()) {
                output += ",";
            }
        }

        // Iterate over all any patterns and add them to the transition
        for (Iterator<AnyPattern> it = anyPatterns.iterator(); it.hasNext();) {
            AnyPattern anyPattern = it.next();
            output += anyPattern;

            // Check if a next any pattern exists
            if (it.hasNext()) {
                output += ",";
            }
        }

        // If transition contains an epsilon add it to annotation
        if (hasEpsilon()) {
            output += ",epsilon";
        }

        // Return output
        return output + ")\"]\n";
    }
}
