package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons;

import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.InvalidTypeStateContentException;
import de.tudortmund.cs.bonxai.xsd.Type;
import java.util.LinkedHashSet;

/**
 * This class represents a state of the <tt>TypeAutomaton</tt>. It extends the
 * <tt>State</tt> class to contain a type of the XSD data structure with basic
 * methods to use it.
 * 
 * @author Dominik Wolff
 */
public class TypeState extends State {

    /**
     * Type of the XSD data structure which is contained in this state
     */
    protected Type type;

    /**
     * Constructor for class <tt>TypeState</tt> which sets the type of the
     * state. (It is not allowed to set the type to null, only the
     * <tt>TypeStartState</tt> is allowed to have no type because it is the
     * start of the automaton, all other states must contain types)
     *
     * @param type Type which is a valid type of the XML XSDSchema data structure
     * and not null.
     * @throws InvalidTypeStateContentException Exception that is thrown if the
     * specified typ is an invalid content, such as a null pointer.
     */
    public TypeState(Type type) throws InvalidTypeStateContentException {
        super();

        // Check if the type state contains no type and is no start state
        if (type == null && !(this instanceof TypeStartState)) {

            // Throw exception if the content is not allowed
            throw new InvalidTypeStateContentException(this);
        }
        this.type = type;
    }

    /**
     * This method returns a set containing the type of this state. It will
     * always return a <tt>Type</tt> if the state is not a
     * <tt>TypeStartState</tt> for which a null pointer will be returned.
     *
     * @return Set which contains either a Type or a null pointer.
     */
    public LinkedHashSet<Type> getTypes() {

        // Generate new set containig the type of the type state
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(type);
        return types;
    }

    /**
     * Returns a string representation of the <tt>TypeState</tt>. The result
     * should be a concise but informative representation that is easy for a
     * person to read. In order to achieve this the result is a string that can
     * be interpreted by the Graphviz graph visualization software.
     *
     * @return Graphviz representation of the <tt>TypeState</tt>.
     */
    @Override
    public String toString() {

        // The complex and non complex representation only returns the type of this state
        String output = (type != null) ? type.getLocalName() : "sink";
        return output;
    }
}
