package de.tudortmund.cs.bonxai.typeautomaton;

import de.tudortmund.cs.bonxai.xsd.Element;

public class ElementProperties {
	private boolean nillable;
	private String fixedValue;
	private String defaultValue;

	public ElementProperties() {
		
	}
	
	public ElementProperties(Element element) {
		fixedValue = element.getFixed();
		defaultValue = element.getDefault();
		nillable = element.getNillable();
	}
	
	public ElementProperties(String defaultValue, String fixedValue, boolean nillable) {
		this.fixedValue = fixedValue;
		this.defaultValue = defaultValue;
		this.nillable = nillable;
	}
	
	public boolean isNillable() {
		return nillable;
	}
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}
	public String getFixedValue() {
		return fixedValue;
	}
	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
