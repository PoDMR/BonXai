package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.EmptySubsetTypeStateFieldException;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;

/**
 * This class represents a state of the <tt>SubsetTypeAutomaton</tt>. It extends
 * the <tt>TypeState</tt> class to contain a <tt>LinkedHashSet</tt> of type
 * states which originate from a single type automaton.
 *
 * @author Dominik Wolff
 */
public class SubsetTypeState extends TypeState {

    // Set of contained type states
    private LinkedHashSet<TypeState> typeStates;

    /**
     * This method constructs a new <tt>SubsetTypeState</tt> containing the set 
     * of specified type states. Because the subset type automaton is
     * constructed via subset construction from a regular type automaton, each
     * state of the subset automaton contains one or more states of the regular
     * type automaton.
     *
     * @param typeStates Set of type states contained in the current state.
     * @throws EmptySubsetTypeStateFieldException Exception that is thrown
     * if the current subset type state contains no type states.
     * @throws InvalidTypeStateContentException Exception that is thrown if the
     * specified typ is an invalid content, such as a null pointer. Can not be
     * thrown for this method.
     */
    public SubsetTypeState(LinkedHashSet<TypeState> typeStates) throws EmptySubsetTypeStateFieldException, InvalidTypeStateContentException  {

        // Create new type for the subset type state (Can be used to store the type resulting from the set of contained types)
        super(new SimpleType("{any}anyType", null));

        // Check if set of contained states is empty
        if (typeStates != null && !typeStates.isEmpty()) {

            // Add contained states to subset state
            this.typeStates = typeStates;
        } else {

            // If the set of contained states is empty throw exception
            throw new EmptySubsetTypeStateFieldException(this);
        }
    }

    /**
     * This method returns a set of all type states contained by the current
     * state.
     *
     * @return Set containing all <tt>TypeStates</tt> present in the current
     * subset type state.
     */
    public LinkedHashSet<TypeState> getTypeStates() {
        return new LinkedHashSet<TypeState>(typeStates);
    }

    /**
     * This method gets all types contained in the type states of the current
     * subset type state.
     *
     * @return <tt>LinkedHashSet</tt> of types which contains all types of the
     * subset represented by this state.
     */
    @Override
    public LinkedHashSet<Type> getTypes() {

        // Generate new set, which should contain the types
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();

        // Check each contained type state
        for (Iterator<TypeState> it = typeStates.iterator(); it.hasNext();) {
            TypeState typeState = it.next();

            // Add all types contained in the type state to the set
            types.addAll(typeState.getTypes());
        }
        return types;
    }

    /**
     * Returns a string representation of the <tt>SubsetTypeState</tt>. The
     * result should be a concise but informative representation that is easy
     * for a person to read. In order to achieve this the result is a string
     * that can be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>SubsetTypeState</tt>.
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
