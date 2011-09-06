package eu.fox7.bonxai.xsd.automaton.TypeAutomatons;

import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;

import java.util.*;

/**
 * This class represents a state of a product automaton. This product automaton
 * is constructed for type automatons. The product type state contains a list of
 * type states in difference to the subset type automaton, because the product
 * is build for a specific number of automatons and each automaton contains one
 * type state contained in this product type state.
 *
 * @author Dominik Wolff
 */
public class ProductTypeState extends TypeState {

    // List of <tt>TypeStates</tt> contained by this product state.
    LinkedList<TypeState> typeStates;

    /**
     * Constructor of the <tt>ProductTypeState</tt> class. As parameter a
     * list of <tt>TypeStates</tt> is given.
     *
     * @param typeStates List of type states contained in the new product
     * state.
     * @throws InvalidTypeStateContentException Exception that is thrown if a
     * type automaton state contains invalid content.
     * @throws EmptyProductTypeStateFieldException Exception which is thrown
     * if a product type state contains no type states.
     */
    public ProductTypeState(LinkedList<TypeState> typeStates) throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {

        // Create new type for the product type state (Can be used to store the type resulting from the set of contained types)
        super(new SimpleType("{any}anyType", null));

        // Check if set of contained states is empty
        if (typeStates != null && !typeStates.isEmpty()) {

            // Add contained states to product state
            this.typeStates = typeStates;
        } else {

            // If the set of contained states is empty throw exception
            throw new EmptyProductTypeStateFieldException(this);
        }
    }

    /**
     * This method returns the list of contained <tt>TypeStates</tt>.
     *
     * @return List of <tt>TypeStates</tt> contained by this state.
     */
    public LinkedList<TypeState> getTypeStates() {
        return new LinkedList<TypeState>(typeStates);
    }

    /**
     * This method returns all types contained in the type states of the current
     * product type state.
     *
     * @return <tt>LinkedHashSet</tt> of types which contains all types of the
     * procduct state.
     */
    @Override
    public LinkedHashSet<Type> getTypes() {

        // Generate new set, which should contain the types
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();

        // Check each contained type state
        for (TypeState typeState: typeStates) {
            // No sink states are allowed
            if (typeState != null) {

                // Add all types contained in the type state to the set
                for (Type currentType : typeState.getTypes()) {
                    types.add(currentType);
                }
            }
        }
        return types;
    }

    /**
     * Returns a string representation of the <tt>ProductTypeState</tt>. The
     * result should be a concise but informative representation that is easy
     * for a person to read. In order to achieve this the result is a string
     * that can be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>ProductTypeState</tt>.
     */
    @Override
    public String toString() {
        String output = "(";

        // Add each entry of the contained state list to the output
        for (Iterator<TypeState> it = typeStates.iterator(); it.hasNext();) {
            TypeState typeState = it.next();

            // If the list contains a null entry add "sink" to the output
            if (typeState == null) {
                output += "sink";
            } else {
                output += typeState;
            }

            // Check if the end of the list is reached if not use a comma to separate contained states
            if (it.hasNext()) {
                output += ",";
            } else {
                output += ")";
            }
        }

        // Add quotation marks to the output
        return output;
    }
}
