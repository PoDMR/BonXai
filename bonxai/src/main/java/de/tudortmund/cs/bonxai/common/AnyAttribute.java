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
package de.tudortmund.cs.bonxai.common;

import de.tudortmund.cs.bonxai.xsd.AttributeParticle;

/**
 * The AnyAttribute class represents the XSD attribute definition with the
 * same name.
 * It stores a value from the process contents instruction enumeration
 * (Strict, Skip or Lax) and a defined namespace which specifies the allowed
 * attributes.
 */
public class AnyAttribute extends AttributeParticle {

    /**
     * The namespaces which defines the allowed attributes.
     */
    protected String namespace;

    /**
     * Specifies how the XML processor handles the validation.
     */
    protected ProcessContentsInstruction processContentsInstruction;

    /**
     * Creates an AnyAttribute object with the passed
     * process contents instruction and namespace value.
     */
    public AnyAttribute (ProcessContentsInstruction processContentsInstruction, String namespace) {
        this.processContentsInstruction = processContentsInstruction;
        this.namespace = namespace;
    }

    /**
     * Constructor with only processing instruction.
     *
     * Sets namespace to the XSD default ##any.
     */
    public AnyAttribute(ProcessContentsInstruction processContentsInstruction) {
        this(processContentsInstruction, "##any");
    }

    /**
     * Constructor with only Namespace.
     *
     * Sets processing instruction to XSD default {@link
     * ProcessContentsInstruction.Strict}.
     */
    public AnyAttribute(String namespace) {
        this(ProcessContentsInstruction.Strict, namespace);
    }

    /**
     * Default constructor.
     *
     * Sets processing instruction to XSD default {@link
     * ProcessContentsInstruction.Strict} and namespace to XSD default ##any.
     */
    public AnyAttribute() {
        this(ProcessContentsInstruction.Strict, "##any");
    }

    /**
     * Returns the namespace.
     */
    public String getNamespace () {
        return namespace;
    }

    /**
     * Returns the process contents instruction.
     */
    public de.tudortmund.cs.bonxai.common.ProcessContentsInstruction getProcessContentsInstruction () {
        return processContentsInstruction;
    }

    /**
     * Compare two objects of this type to check if they represent the same
     * content
     */
    public boolean equals( AnyAttribute that ) {
        return (
            super.equals( that )
            && this.namespace.equals( that.namespace )
            && this.processContentsInstruction.equals( that.processContentsInstruction )
        );
    }

    /**
     * Set new namespace.
     *
     * @param namespace New namespace of the any attribute.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


}

