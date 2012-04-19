package eu.fox7.schematoolkit.xsd.om;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlSchema;

import eu.fox7.schematoolkit.common.QualifiedName;

/*
 * This class is intended to wrap a typename 
 * into a type, if the type itself is not 
 * available (e.g. imported from another schema).
 * 
 * It is not meant to be used inside an XML Schema.
 */
public class TypeReference extends Type {
	private static Collection<QualifiedName> XMLSCHEMA_SIMPLETYPES;
	
	static {
		XMLSCHEMA_SIMPLETYPES = new HashSet<QualifiedName>();
		XMLSCHEMA_SIMPLETYPES.add(new QualifiedName(XSDSchema.XMLSCHEMA_NAMESPACE,"string"));
	}
	
	
	public TypeReference(QualifiedName name) {
		super(name);
	}
	
	public boolean isXSDSimpleType() {
		return XMLSCHEMA_SIMPLETYPES.contains(this.getName());
		
	}
	
	public boolean isXSDType() {
		return XSDSchema.XMLSCHEMA_NAMESPACE.equals(this.getName().getNamespaceURI());
	}
	
	

}
