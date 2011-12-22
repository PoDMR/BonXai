package eu.fox7.bonxai.common;

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

	private Namespace namespace;
	private String name;
	private boolean isAttribute;

	public QualifiedName(Namespace namespace, String name) {
		this(namespace,name,false);
	}

	public QualifiedName(Namespace namespace, String name, boolean isAttribute) {
		this.namespace = namespace;
		this.name = name;
		this.isAttribute = isAttribute;
		if (namespace == null)
			throw new RuntimeException("Namespace is NULL");
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}
	
	public String getQualifiedName() {
		if (isAttribute)
			return this.namespace.getPrefix()+"@"+this.name;
		else	
			return this.namespace.getPrefix()+this.name;
	}

	public boolean isAttribute() {
		return isAttribute;
	}

	public String getFullyQualifiedName() {
		return "{"+this.getNamespace().getUri()+"}"+this.name;
	}
}
