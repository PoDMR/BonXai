package eu.fox7.bonxai.xsd.automaton;

import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.XSDSchema.*;
import eu.fox7.bonxai.xsd.automaton.exceptions.*;

import java.util.*;

/**
 * This clas represents a transition in an automaton linking two states 
 * together. The transition consists of a source state and a destination state,
 * a list of annotated elements and a <tt>HashMap</tt> mapping element names to
 * lists of elements.
 *
 * @author Dominik Wolff
 */
public class Transition {

    /**
     * List of elements annotated to the transition
     */
    protected LinkedHashSet<Element> elements;

    // HashMap mapping element names to lists of elements if for a certain element name more than one element is annotated to the transition
    private HashMap<String, LinkedHashSet<Element>> nameElementMap;

    // Source and destination of the transition
    private State sourceState,  destinationState;

    /**
     * Constructor of the <tt>Transition</tt> which initializes each field and
     * add a source and destination to the transition. If either one of them is
     * not specified the transition can not be constructed.
     *
     * @param sourceState Source of the new transition.
     * @param destinationState Destination of the new transition.
     */
    public Transition(State sourceState, State destinationState) {

        // Check if source state is empty
        if (sourceState == null) {
            try {
                throw new EmptySourceException(this);
            } catch (EmptySourceException ex) {
                ex.printStackTrace();
            }
        }

        // Check if destination state is empty
        if (destinationState == null) {
            try {
                throw new EmptyDestinationException(this);
            } catch (EmptyDestinationException ex) {
                ex.printStackTrace();
            }
        }

        // Set source and destination state
        this.sourceState = sourceState;
        this.destinationState = destinationState;

        // Set element set and map
        this.elements = new LinkedHashSet<Element>();
        this.nameElementMap = new HashMap<String, LinkedHashSet<Element>>();
    }

    /**
     * Gets the source state of this transition, meaning a state which
     * contains this transition in its list of outgoing transitions.
     *
     * @return State which is left by this transition.
     */
    public State getSourceState() {
        return sourceState;
    }

    /**
     * Gets the destination state of this transition, meaning a state which
     * contains this transition in its list of incoming transitions.
     *
     * @return State which is entered by this transition.
     */
    public State getDestinationState() {
        return destinationState;
    }

    /**
     * Returns a <tt>LinkedHashSet</tt> which contains all elements annotated
     * to this transition.
     *
     * @return <tt>LinkedHashSet</tt> of elements.
     */
    public LinkedHashSet<Element> getElements() {
        return new LinkedHashSet<Element>(elements);
    }

    /**
     * Adds the specified element to the list of elements if it is not already
     * present. If the list of elements already contains the element, the call
     * leaves the list unchanged and returns <tt>false</tt>.
     *
     * (Also updates the nameElementMap <tt>HashMap</tt>, but only if the
     * element can be added to the list and the name is not already contained.)
     *
     * @param element Elements which is added to the list of elements.
     * @return <tt>true</tt> if the list of elements did not already contain the
     * specified element.
     */
    public boolean addElement(Element element) {
        boolean result = false;

        // Check whether element is not null and could be added to the element set
        if (element != null && elements.add(element)) {
            LinkedHashSet<Element> elementList = new LinkedHashSet<Element>();

            // Get name of the element
            String elementName = "";

            // Use emtpy namespace if element is not qualified
            if (element.getForm() == Qualification.qualified) {
                elementName = element.getName();
            } else {
                elementName = "{}" + element.getLocalName();
            }

            // Update name to elements map, by adding the element to the map
            if (nameElementMap.containsKey(elementName)) {

                // Entry for the element name already exists
                elementList = nameElementMap.get(elementName);
                result = elementList.add(element);
                nameElementMap.put(elementName, elementList);
            } else {

                // New entry for the element is generated
                result = elementList.add(element);
                nameElementMap.put(elementName, elementList);
            }
        }

        // Return true if operation was successful
        return result;
    }

    /**
     * Appends all of the elements in the specified <tt>LinkedHashSet</tt> to
     * the list of annotated elements. Basically for convenience purpose only.
     *
     * @param newElements <tt>LinkedHashSet</tt> which is added to the current
     * annotated elements.
     */
    public void addAllElements(LinkedHashSet<Element> newElements) {

        // Add each element contained in the element set to the transition
        for (Element element : newElements) {
            addElement(element);
        }
    }

    /**
     * Removes the specified element from the list of elements if it is present.
     * Returns <tt>true</tt> if the list contained the element.
     *
     * (The list of elements will not contain the element once the call
     * returns.)
     * (Also updates the nameElementMap <tt>HashMap</tt>, but only if the
     * transition can be removed from the list.)
     *
     * @param element Element which is removed from the list of elements, if
     * present.
     * @return <tt>true</tt> if the list of elements contained the specified
     * transition.
     */
    public boolean removeElement(Element element) {
        boolean result = false;

        // Check whether the element could be removed from the element set
        if (elements.remove(element)) {
            LinkedHashSet<Element> elementList = new LinkedHashSet<Element>();

            // Get name of the element
            String elementName = "";

            // Use emtpy namespace if element is not qualified
            if (element.getForm() == Qualification.qualified) {
                elementName = element.getName();
            } else {
                elementName = "{}" + element.getLocalName();
            }

            // Update name to elements map, by removing the element from the map
            if (nameElementMap.containsKey(elementName)) {

                // If entry for the element name already exists remove elenent from the contained element set
                elementList = nameElementMap.get(elementName);
                result = elementList.remove(element);
                nameElementMap.put(elementName, elementList);
            }
        }

        // Return true if operation was successful
        return result;
    }

    /**
     * Removes all of the elements from the annotated elements list and name to
     * element map.
     */
    public void clearElements() {
        nameElementMap.clear();
        elements.clear();
    }

    /**
     * Returns a <tt>HashMap</tt> which maps element names to lists of elements.
     * These lists would normaly only contain one element but when the
     * underlying XSD is not valid it is possible to annotate two different
     * elements with equivalent names to a transition
     *
     * @return <tt>HashMap</tt> for mapping element names to a list of elements
     * if for the same element name different elements are annotated to the
     * transition.
     */
    public HashMap<String, LinkedHashSet<Element>> getNameElementMap() {
        return nameElementMap;
    }

    /**
     * Returns a string representation of the <tt>Transition</tt>. The result
     * should be a concise but informative representation that is easy for a
     * person to read. In order to achieve this the result is a string that can
     * be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>Transition</tt>.
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

            // Check if a next element exist
            if (it.hasNext()) {
                output += ",";
            }
        }

        // Return output
        return output + ")\"]\n";
    }
}
