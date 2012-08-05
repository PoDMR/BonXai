package eu.fox7.schematoolkit.common;

import eu.fox7.schematoolkit.xsd.saxparser.NamedXSDElement;


public class GroupReference extends Particle implements NamedXSDElement {
	/*
	 * Name of the referenced Group
	 */
	private QualifiedName name;
	
	public GroupReference() {}
	
	public void setName(QualifiedName name) {
		this.name = name;
	}
	
	public GroupReference(QualifiedName name) {
		this.name = name;
	}

	/**
	 * @return the name of the referenced group
	 */
	public QualifiedName getName() {
		return name;
	}
}
