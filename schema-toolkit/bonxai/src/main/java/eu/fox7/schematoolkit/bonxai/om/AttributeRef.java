package eu.fox7.schematoolkit.bonxai.om;

import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;

public class AttributeRef extends AttributeParticle {
	private QualifiedName attributeName;
	private boolean required = false;
	private String fixedValue = null;
	private String defaultValue = null;

	/**
	 * @return the fixedValue
	 */
	public String getFixedValue() {
		return fixedValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	public AttributeRef(QualifiedName attributeName, boolean required) {
		this.attributeName = attributeName;
		this.required = required;
	}

	public AttributeRef(QualifiedName attributeName, String fixedValue, String defaultValue, boolean required) {
		this.attributeName = attributeName;
		this.required = required;
		this.fixedValue = fixedValue;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the attributeName
	 */
	public QualifiedName getAttributeName() {
		return attributeName;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
}
