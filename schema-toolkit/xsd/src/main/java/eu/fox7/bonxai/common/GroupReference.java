package eu.fox7.bonxai.common;


public class GroupReference extends Particle {
	/*
	 * Name of the referenced Group
	 */
	private QualifiedName name;
	
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
