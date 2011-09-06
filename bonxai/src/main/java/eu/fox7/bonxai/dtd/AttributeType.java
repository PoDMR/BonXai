package eu.fox7.bonxai.dtd;

/**
 * An enumeration of possible values for an DTD attribute type.
 * @author Lars Schmidt
 */
public enum AttributeType {

    // A unique ID or name. Must be a valid XML name.
    ID,
    // Represents the value of an ID attribute of another element.
    IDREF,
    // Represents multiple IDs of elements, separated by whitespace.
    IDREFS,
    // The name of an entity (which must be declared in the DTD)
    ENTITY,
    // A list of entity names, separated by whitespaces. (All entities must be declared in the DTD)
    ENTITIES,
    // A valid XML name.
    NMTOKEN,
    // A list of valid XML names separated by whitespaces.
    NMTOKENS,
    // Character Data (text that doesn't contain markup)
    CDATA,
    // A list of notation names (which must be declared in the DTD) seperated by the pipe operator (x|y).
    NOTATION,
    // A list of values seperated by the pipe operator (x|y). The value of the attribute must be one from this list.
    ENUMERATION;
}
