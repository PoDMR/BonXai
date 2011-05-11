package de.tudortmund.cs.bonxai.converter.xsd2dtd;

import de.tudortmund.cs.bonxai.dtd.Attribute;
import de.tudortmund.cs.bonxai.dtd.AttributeType;
import de.tudortmund.cs.bonxai.xsd.Key;
import de.tudortmund.cs.bonxai.xsd.KeyRef;
import de.tudortmund.cs.bonxai.xsd.SimpleConstraint;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Class ConstraintHandler
 *
 * This class handles some XSD constraints (Key, keyRef) and converts them to
 * DTD attributes with the types ID or IDREF.
 *
 * For a successful conversion a constraint has to be in the following form:
 *
 * a)
 * <xsd:key         name="authorKey">
 *      <xsd:selector xpath=".//author" />
 *      <xsd:field    xpath="@id" />
 * </xsd:key>
 *
 * or
 *
 * b)
 * <xsd:key         name="authorKey">
 *      <xsd:selector xpath=".//*" />
 *      <xsd:field    xpath="@id" />
 * </xsd:key>
 *
 * Example a):
 * -----------
 * This results in an attribute "id" with type ID (#REQUIRED) only for the
 * element author and only if there is an attribute called "id".
 *
 * Example b):
 * -----------
 * This key is a global key for all elements in the resulting DTD having an attribute called "id".

 * @author Lars Schmidt
 */
public class ConstraintHandler {

    private final ElementWrapper elementWrapper;

    /**
     * Constructor of class ConstraintHandler
     * @param elementWrapper
     */
    public ConstraintHandler(ElementWrapper elementWrapper) {
        this.elementWrapper = elementWrapper;
    }

    /**
     * Method for starting the constraint handling
     * @throws Exception
     */
    public void manageElementConstraints() throws Exception {
        this.manageConstraints(this.elementWrapper.getXsdContraints());
    }

    /**
     * Start the constraint handling for a given set of xsd constraints.
     * @param constraintsSet    Source of the conversion
     * @throws Exception
     */
    public void manageConstraints(LinkedHashSet<SimpleConstraint> constraintsSet) throws Exception {
        for (Iterator<SimpleConstraint> it = constraintsSet.iterator(); it.hasNext();) {
            SimpleConstraint simpleConstraint = it.next();

            if (simpleConstraint instanceof Key) {
                this.handleKeyConstraint((Key) simpleConstraint);
            } else if (simpleConstraint instanceof KeyRef) {
                this.handleKeyRefConstraint((KeyRef) simpleConstraint);
            }
        }
    }

    /**
     * Manage a key-constraint
     * @param key   Source of the conversion
     */
    private void handleKeyConstraint(Key key) {
        String attributeName = "";
        if (key.getFields().size() == 1) {
            String fieldString = key.getFields().iterator().next();
            attributeName = fieldString.substring(fieldString.lastIndexOf("@") + 1, fieldString.length());
        }
        if (this.elementWrapper.getDTDAttributeMap().containsKey(attributeName)) {
            LinkedHashSet<Attribute> tempHashSet = new LinkedHashSet<Attribute>();
            Attribute idAttribute = new Attribute(attributeName, "#REQUIRED", null);
            idAttribute.setType(AttributeType.ID);
            tempHashSet.add(idAttribute);
            this.elementWrapper.getDTDAttributeMap().put(attributeName, tempHashSet);
        }
    }

    /**
     * Manage a keyRef-constraint
     * @param keyRef    Source of the conversion
     */
    private void handleKeyRefConstraint(KeyRef keyRef) {
        String attributeName = "";
        if (keyRef.getFields().size() == 1) {
            String fieldString = keyRef.getFields().iterator().next();
            attributeName = fieldString.substring(fieldString.lastIndexOf("@") + 1, fieldString.length());
        }
        if (this.elementWrapper.getDTDAttributeMap().containsKey(attributeName)) {
            LinkedHashSet<Attribute> tempHashSet = new LinkedHashSet<Attribute>();
            Attribute idRefAttribute = new Attribute(attributeName, "#REQUIRED", null);
            idRefAttribute.setType(AttributeType.IDREF);
            tempHashSet.add(idRefAttribute);
            this.elementWrapper.getDTDAttributeMap().put(attributeName, tempHashSet);
        }
    }
}
