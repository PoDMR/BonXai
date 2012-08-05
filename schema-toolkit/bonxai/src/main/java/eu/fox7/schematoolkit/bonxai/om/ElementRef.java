package eu.fox7.schematoolkit.bonxai.om;

import eu.fox7.schematoolkit.common.QualifiedName;

public class ElementRef extends eu.fox7.schematoolkit.common.ElementRef implements Locatable {
	public ElementRef(QualifiedName elementName) {
		super(elementName);
	}

	public ElementRef(QualifiedName elementName, BonxaiLocation location) {
		super(elementName);
		this.location = location;
	}

	private BonxaiLocation location;
	
	public BonxaiLocation getLocation() {
		return location;
	}

}
