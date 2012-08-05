package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.XSDSchema.Qualification;

public interface TypedXSDElement {
	public void setTypeName(QualifiedName name);
	public void setForm(Qualification qualification);
}
