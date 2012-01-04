package eu.fox7.schematoolkit.common;

import java.util.LinkedList;
import java.util.List;

public class NamespaceList {
    /**
     * Default namespace of the schema
     */
    private DefaultNamespace defaultNamespace = new DefaultNamespace("");
    
    private Namespace targetNamespace;
    
    /**
     * Namespacelist
     */
    private List<IdentifiedNamespace> namespaces = new LinkedList<IdentifiedNamespace>();

	public NamespaceList(DefaultNamespace defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
		this.targetNamespace = defaultNamespace;
	}
	
	public NamespaceList(DefaultNamespace defaultNamespace, Namespace targetNamespace) {
		this.defaultNamespace = defaultNamespace;
		this.targetNamespace = targetNamespace;
	}

	public NamespaceList() {
	}

	public void setDefaultNamespace(DefaultNamespace defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}
	
	public void setTargetNamespace(Namespace targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public DefaultNamespace getDefaultNamespace() {
		return defaultNamespace;
	}

	public Namespace getTargetNamespace() {
		return targetNamespace;
	}

	public List<IdentifiedNamespace> getNamespaces() {
		return this.namespaces;
	}
	
	public void addNamespace(IdentifiedNamespace namespace) {
		this.namespaces.add(namespace);
	}

	public Namespace getNamespaceByIdentifier(String identifier) {
		if (identifier == null || identifier.equals(""))
			return this.defaultNamespace;
		else
			for (Namespace namespace: this.namespaces) 
				if (namespace.getIdentifier().equals(identifier))
					return namespace;
	return null;
	}
	
	/*
	 * returns a namespace object, given a uri. If there are several copies 
	 * of the same namespace (with different identifiers), one of them is returned.
	 * null is returned if the namespace does not exists.
	 * @param uri
	 * @return namespace 
	 */
	public Namespace getNamespaceByUri(String uri) {
		if (this.defaultNamespace.getUri().equals(uri))
			return this.defaultNamespace;
		else
			for (Namespace namespace: this.namespaces) 
				if (namespace.getUri().equals(uri))
					return namespace;
		return null;
	}

	public void setTargetNamespace(String targetNamespaceURI) {
		this.targetNamespace = this.getNamespaceByUri(targetNamespaceURI);
	}
}
