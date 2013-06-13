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
import java.util.List;
import java.util.LinkedList;

/*
 * implements class AnyPattern
 */

public class AnyPattern extends Particle {

    private ProcessContentsInstruction processContentsInstruction;
    private List<Namespace> namespaces = new LinkedList<Namespace>();
    private List<QualifiedName> disallowedNames = new LinkedList<QualifiedName>();
    private boolean complementNamespaces = false;

    public AnyPattern() {}
    
    public void addDisallowedName(QualifiedName name) {
    	this.disallowedNames.add(name);
    }
    
    public List<QualifiedName> getDisallowedNames() {
    	return disallowedNames;
    }
    
    public void setComplementNamespaces(boolean complement) {
    	this.complementNamespaces = complement;
    }
    
    public boolean isComplementNamespaces() {
    	return this.complementNamespaces;
    }
    
    public AnyPattern(ProcessContentsInstruction processContentsInstruction, String namespace) {
        this.processContentsInstruction = processContentsInstruction;
        this.setNamespace(namespace);
    }
    
    public AnyPattern(ProcessContentsInstruction processContentsInstruction, Namespace namespace) {
        this.processContentsInstruction = processContentsInstruction;
        this.addNamespace(namespace);
    }
    

    public AnyPattern(ProcessContentsInstruction processContentsInstruction, Collection<Namespace> namespaces) {
    	this.processContentsInstruction = processContentsInstruction;
    	this.namespaces = new LinkedList<Namespace>(namespaces);
    }

	public AnyPattern(ProcessContentsInstruction policy, boolean complementNamespaces,
			List<Namespace> namespaces, List<QualifiedName> disallowedNames) {
		this.processContentsInstruction = policy;
		this.complementNamespaces = complementNamespaces;
		this.disallowedNames = disallowedNames;
		this.namespaces = namespaces;
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


    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (complementNamespaces ? 1231 : 1237);
		result = prime * result
				+ ((disallowedNames == null) ? 0 : disallowedNames.hashCode());
		result = prime * result
				+ ((namespaces == null) ? 0 : namespaces.hashCode());
		result = prime
				* result
				+ ((processContentsInstruction == null) ? 0
						: processContentsInstruction.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnyPattern other = (AnyPattern) obj;
		if (complementNamespaces != other.complementNamespaces)
			return false;
		if (disallowedNames == null) {
			if (other.disallowedNames != null)
				return false;
		} else if (!disallowedNames.equals(other.disallowedNames))
			return false;
		if (namespaces == null) {
			if (other.namespaces != null)
				return false;
		} else if (!namespaces.equals(other.namespaces))
			return false;
		if (processContentsInstruction != other.processContentsInstruction)
			return false;
		return true;
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

