/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.xsd;
/*
 * implements class SimpleConstraint
 */

import java.util.HashSet;
import java.util.Iterator;

import de.tudortmund.cs.bonxai.common.Annotation;
import de.tudortmund.cs.bonxai.common.Annotationable;

/**
 * Base class for the standard XSD constraints {@link Unique}, {@link Key} and
 * {@link KeyRef}. Overrides {@link equals() and hashCode(), since constraints
 * must have a unique name in XSD.
 */
public abstract class SimpleConstraint extends Constraint implements Annotationable {

    protected boolean dummy;
    protected HashSet<String> fields;
    protected String selector;
    protected String name;
    private Annotation annotation;

    public SimpleConstraint(String name, String selector) {
        this.fields = new HashSet<String>();
        this.selector = selector;
        this.name = name;
    }

    public SimpleConstraint(SimpleConstraint other) {
    	super(other);
    	this.fields = new HashSet<String>();
        for (Iterator<String> it = other.fields.iterator(); it.hasNext();) {
            this.fields.add(it.next());
        }
        
        this.annotation = other.annotation;
        this.name = other.name;
        this.selector = other.selector;
    }
    
    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    /*
     * Method getFields returns a copy of the Set of Strings, containing the
     * filds of the SimpleConstraint-subclasses.
     */
    public HashSet<String> getFields() {
        return new HashSet<String>(fields);
    }

    /*
     * Method getName returns the name of an Constraint.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    

    /*
     * Method getSelector returns the selector of an SimpleConstraint.
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Replace the currently set selector with another one.
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }

    /*
     * Method addField adds a field of type String to the set of Strings,
     * containing the fields of an SimpleConstraint.
     */
    public void addField(String field) {
        fields.add(field);
    }

    /**
     * Remove all currently added Fields
     */
    public void clearFields() {
        this.fields.clear();
    }

    /**
     * Compare the object with that object.
     *
     * This is a specialized implementation of equals(), which only checks the
     * name of the constraint. This is sensible since constraints in XSD must
     * have a unique name there must not exist 2 constraints with the same
     * name.
     */
    @Override
    public boolean equals(Object that) {
        return ((that instanceof SimpleConstraint)
                && this.name.equals(((SimpleConstraint) that).name));
    }

    /**
     * Return a hash code for this object.
     *
     * This is a special implementation of hashCode() to identify types. Types
     * are not distinguished by their contents, but only by their name, since
     * every type must have a unique name in XSD! This implementation ensures
     * that 2 types with the same name are identified to be the same in
     * HashTable and similar.
     */
    @Override
    public int hashCode() {
        return 23 * 13 + this.name.hashCode();
    }

    /**
     * Get namespace.
     *
     * Get namespace URI from stored fully qualified name.
     *
     * @return string
     */
    public String getNamespace() {
        return this.name.substring(1, this.name.lastIndexOf("}"));
    }

    /**
     * Get local name.
     *
     * Get local name from stored fully qualified name.
     *
     * @return string
     */
    public String getLocalName() {
        return this.name.substring(this.name.lastIndexOf("}") + 1);
    }
}
