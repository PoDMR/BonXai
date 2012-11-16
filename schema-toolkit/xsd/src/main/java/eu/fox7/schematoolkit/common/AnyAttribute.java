/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * The AnyAttribute class represents the XSD attribute definition with the
 * same name.
 * It stores a value from the process contents instruction enumeration
 * (STRICT, SKIP or LAX) and a defined namespace which specifies the allowed
 * attributes.
 */
public class AnyAttribute extends AttributeParticle {

    protected ProcessContentsInstruction processContentsInstruction;
    protected List<Namespace> namespaces = new LinkedList<Namespace>();

    public AnyAttribute() {}
    
    public AnyAttribute(ProcessContentsInstruction processContentsInstruction, String namespace) {
        this.processContentsInstruction = processContentsInstruction;
        this.setNamespace(namespace);
    }
    
    public AnyAttribute(ProcessContentsInstruction processContentsInstruction, Namespace namespace) {
        this.processContentsInstruction = processContentsInstruction;
        this.addNamespace(namespace);
    }
    

    public AnyAttribute(ProcessContentsInstruction processContentsInstruction,
			Collection<Namespace> namespaceList) {
		this.processContentsInstruction = processContentsInstruction;
		this.namespaces=new LinkedList<Namespace>(namespaceList);
	}

	/*
     * method getNamespaces return the namespace from which the possible
     * "any"-Elements may be used.
     */
    public Collection<Namespace> getNamespaces() {
        return this.namespaces;
    }
    
    public boolean isAnyNamespace() {
    	return (this.namespaces==null ||
    			this.namespaces.isEmpty() ||
    			(this.namespaces.size()==1 && this.namespaces.iterator().next().equals(Namespace.ANY_NAMESPACE)));
    }

    /*
     * method getProcessContentsInstruction returns processContentsinstruction
     * (one of skip, lax, strict)
     */
    public ProcessContentsInstruction getProcessContentsInstruction() {
        return processContentsInstruction;
    }

    /**
     * Compare two objects of this type to check if they represent the same
     * content
     */
    public boolean equals(AnyPattern that) {
        return (super.equals(that) && this.namespaces.equals(that.namespaces) && this.processContentsInstruction.equals(that.processContentsInstruction));
    }

    /**
     * Return a hashCode for this object
     *
     * This needs to be overwritten to fullfill the hashCode/equals contract
     * enforced by java
     */
    public int hashCode() {
        int hash = super.hashCode();
        int multiplier = 13;
//        if (this.namespace != null) {
        	hash = hash * multiplier + this.namespaces.hashCode();
//        }
        hash = hash * multiplier + this.processContentsInstruction.hashCode();
        return hash;
    }

    /**
     * Set new namespace attribute of the any pattern.
     * @param namespace New namespace attribute of the any pattern.
     */
    public void setNamespace(String namespace) {
        this.setNamespace(new AnonymousNamespace(namespace));;
    }
    
    public void setNamespace(Namespace namespace) {
        this.namespaces = new LinkedList<Namespace>();
        this.namespaces.add(namespace);
    }
    
    public void addNamespace(Namespace namespace) {
    	this.namespaces.add(namespace);
    }

	public void setProcessContentsInstruction(ProcessContentsInstruction processContentsInstruction) {
		this.processContentsInstruction = processContentsInstruction;
		
	}
}

