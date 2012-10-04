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
		if (targetNamespace==null)
	      return Namespace.EMPTY_NAMESPACE;
		else 
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

	public QualifiedName getQualifiedName(String qName) {
		return getQualifiedName(qName, false);
	}

	public QualifiedName getQualifiedName(String qName, boolean useEmptyNamespaceAsDefault) {
		Namespace namespace = null;
		String localName;
		int pos = qName.lastIndexOf(':');
		if (pos == -1 ) {
			if (useEmptyNamespaceAsDefault)
				namespace = Namespace.EMPTY_NAMESPACE;
			else 
				namespace = this.defaultNamespace;
			localName = qName;
		} else {
			String prefix = qName.substring(0, pos);
			localName = qName.substring(pos+1, qName.length());
			namespace = this.getNamespaceByIdentifier(prefix);
		}
		
		if (namespace==null) {
			System.err.println("No namespace found for qName " + qName);
			namespace = Namespace.EMPTY_NAMESPACE;
		}
		
		return new QualifiedName(namespace.getUri(), localName);
	}

	public String getQualifiedName(QualifiedName name) {
		if (name==null)
			//throw new RuntimeException("Name is null.");
			return "NULL";
		Namespace namespace = this.getNamespaceByUri(name.getNamespaceURI());
		if (namespace == null)
			namespace = Namespace.EMPTY_NAMESPACE;
		if (name.isAttribute())
			return namespace.getPrefix()+'@'+name.getName();
		else
			return namespace.getPrefix()+name.getName();

	}
}
