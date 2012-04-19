package eu.fox7.schematoolkit.common;

public class QualifiedName {
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAttribute ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((namespace == null) ? 0 : namespace.hashCode());
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
		QualifiedName other = (QualifiedName) obj;
		if (isAttribute != other.isAttribute)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

	private String namespace;
	private String name;
	private boolean isAttribute;

	public QualifiedName(Namespace namespace, String name) {
		this(namespace,name,false);
	}

	public QualifiedName(Namespace namespace, String name, boolean isAttribute) {
		if (namespace == null)
			throw new RuntimeException("Namespace is NULL");
		this.namespace = namespace.getUri();
		this.name = name;
		this.isAttribute = isAttribute;
	}

//	public Namespace getNamespace() {
//		return namespace;
//	}

	public QualifiedName(String namespaceURI, String name) {
		this.namespace = namespaceURI;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
//	public String getQualifiedName() {
//		if (isAttribute)
//			return this.namespace.getPrefix()+"@"+this.name;
//		else	
//			return this.namespace.getPrefix()+this.name;
//	}

	public boolean isAttribute() {
		return isAttribute;
	}

	public String getFullyQualifiedName() {
		return "{"+this.namespace+"}"+this.name;
	}
	
	public static String getLocalNameFromFQN(String fullyQualifiedName) {
		int pos = fullyQualifiedName.indexOf('}');
		return fullyQualifiedName.substring(pos+1);
	}

	public static String getNamespaceFromFQN(String fullyQualifiedName) {
		int pos = fullyQualifiedName.indexOf('}');
		return fullyQualifiedName.substring(1,pos);
	}
	
	public static QualifiedName getQualifiedNameFromFQN(String fullyQualifiedName) {
		String namespace = getNamespaceFromFQN(fullyQualifiedName);
		String localName = getLocalNameFromFQN(fullyQualifiedName);
		return new QualifiedName(namespace, localName);
	}

	public String getNamespaceURI() {
		return namespace;
	}
	
	@Override
	public String toString() {
		return getFullyQualifiedName();
	}
}
