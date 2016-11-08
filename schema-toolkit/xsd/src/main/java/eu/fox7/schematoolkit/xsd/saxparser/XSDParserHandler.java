/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.xsd.saxparser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.XSDSchema.Qualification;

public class XSDParserHandler extends DefaultHandler {
	public static class VContainer {
		public String value;
	}
	
	public static class Enumeration extends VContainer {
	}
	
	public static class Field extends VContainer {
	}
	
	public static class Selector extends VContainer {
	}
	
	public static class Whitespace extends SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> {}
	public static class MinInclusive extends SimpleContentFixableRestrictionProperty<String> {}
	public static class MaxInclusive extends SimpleContentFixableRestrictionProperty<String> {}
	public static class MinExclusive extends SimpleContentFixableRestrictionProperty<String> {}
	public static class MaxExclusive extends SimpleContentFixableRestrictionProperty<String> {}
	public static class TotalDigits extends SimpleContentFixableRestrictionProperty<Integer> {}
	public static class FractionDigits extends SimpleContentFixableRestrictionProperty<Integer> {}
	public static class Length extends SimpleContentFixableRestrictionProperty<Integer> {}
	public static class MinLength extends SimpleContentFixableRestrictionProperty<Integer> {}
	public static class MaxLength extends SimpleContentFixableRestrictionProperty<Integer> {}
	public static class Pattern extends SimpleContentRestrictionProperty<String> {}
	
	public static class Assert { 
		public Assert() throws XSDParserException { 
			throw new XSDParserException("Assert is not supported."); 
		} 
	} 

	public static class Alternative { 
		public Alternative() throws XSDParserException { 
			throw new XSDParserException("Type alternatives are not supported."); 
		} 
	} 

    private enum ElementType {
		schema(XSDSchema.class),
		element(Element.class),
		sequence(SequencePattern.class),
		choice(ChoicePattern.class),
		all(AllPattern.class),
		annotation(Annotation.class),
		documentation(Documentation.class),
		complexType(ComplexType.class),
		simpleType(SimpleType.class),
		complexContent(ComplexContentType.class),
		simpleContent(SimpleContentType.class),
		attribute(Attribute.class),
		anyAttribute(AnyAttribute.class),
		union(SimpleContentUnion.class),
		restriction(SimpleContentRestriction.class),
		extension(SimpleContentExtension.class),
		enumeration(Enumeration.class),
		list(SimpleContentList.class),
		appinfo(AppInfo.class),
		key(Key.class),
		unique(Unique.class),
		keyref(KeyRef.class),
		selector(Selector.class),
		field(Field.class),
		group(GroupReference.class),
		attributeGroup(AttributeGroupReference.class),
		any(AnyPattern.class),
		notation(null),
		length(Length.class),
		minLength(MinLength.class),
		maxLength(MaxLength.class),
		minInclusive(MinInclusive.class),
		maxInclusive(MaxInclusive.class),
		minExclusive(MinExclusive.class),
		maxExclusive(MaxExclusive.class),
		fractionDigits(FractionDigits.class),
		totalDigits(FractionDigits.class),
		pattern(Pattern.class),
		whiteSpace(Whitespace.class),
		IMPORT(ImportedSchema.class),
		COMPLEXExtension(ComplexContentExtension.class),
		COMPLEXRestriction(ComplexContentRestriction.class),
		GROUP(Group.class),
		ATTRIBUTEGROUP(AttributeGroup.class),
		ASSERT(Assert.class),
		alternative(Alternative.class);

		private Class<Object> elementClass;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private ElementType(Class elementClass) {
			this.elementClass = elementClass;
		}

		public Object getInstance() throws XSDParserException {
			if (this.elementClass!=null) {
				try {
					return this.elementClass.newInstance();
				} catch (InstantiationException e) {
					throw new XSDParserException(e);
				} catch (IllegalAccessException e) {
					throw new XSDParserException(e);
				}
			} else {
				throw new InvalidXSDException("Unhandled element with name " + this.toString());
			}
		}

		public static ElementType getElementType(String elementType, String parent) throws InvalidXSDException {
			ElementType e = null;
			if (parent.equals("schema")) {
				if (elementType.equals("import"))
					e=IMPORT;
				else if (elementType.equals("group"))
					e=GROUP;
				else if (elementType.equals("attributeGroup"))
					e=ATTRIBUTEGROUP;
			} 
			else if (parent.equals("complexContent")) {
				if (elementType.equals("extension"))
					e=COMPLEXExtension;
				else if (elementType.equals("restriction"))
					e=COMPLEXRestriction;
			}
			
			if (elementType.equals("assert"))
				e=ASSERT;
			
			if (e == null) {
				try {
					e = valueOf(elementType);
				} catch (IllegalArgumentException ex) {
					throw new InvalidXSDException("Unkown element with label " + elementType);
				}
			}
			return e;
		}
   	}
    
    private static class XMLAttribute {
    	public XMLAttribute(String localName, String value) {
			this.localName = localName;
			this.value = value;
		}
    	
		public String localName;
    	public String value;
    }

	private Locator locator;
    private Stack<Position> locations;
    private Stack<List<Object>> elementStack;
	private Stack<List<XMLAttribute>> attributeStack;
	private NamespaceList namespaceList;
	private List<Type> types;
	private XSDSchema schema;
	private int insideDocumentation;
	private boolean topLevel;
	
	private Stack<String> elementNames;
    
    public XSDParserHandler() {
    }
    
    // this will be called when XML-parser starts reading
    // XML-data; here we save reference to current position in XML:
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }
    
    @Override 
    public void startDocument() {
    	this.attributeStack = new Stack<List<XMLAttribute>>();
    	this.elementStack = new Stack<List<Object>>();
    	this.elementNames = new Stack<String>();
    	this.locations = new Stack<Position>();
		this.namespaceList = new NamespaceList();
		this.types = new LinkedList<Type>();
		this.insideDocumentation = 0;
		this.topLevel = true;

		this.elementStack.push(new LinkedList<Object>());
		this.elementNames.push("");
    }
    
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if (prefix.equals(""))
			this.namespaceList.setDefaultNamespace(new DefaultNamespace(uri));
		else
			this.namespaceList.addNamespace(new IdentifiedNamespace(prefix, uri));
	}

	@Override
    public void startElement(String uri, String localName,
    		String qName, Attributes attrs) throws SAXException {
		if (topLevel && !(uri.equals(XSDSchema.XMLSCHEMA_NAMESPACE) && localName.equals("schema")))
			throw new NoXSDException("Root element is not xs:schema");
		topLevel = false;
		/* Do not parse below xs:documentation and xs:appinfo.
		 * We track the depth to catch nested documentation elements
		 */
		if ((insideDocumentation>0) || (uri.equals(XSDSchema.XMLSCHEMA_NAMESPACE) && (localName.equals("documentation") || localName.equals("appinfo"))))
			insideDocumentation++;
		else if (uri.equals(XSDSchema.XMLSCHEMA_NAMESPACE)) {
    		this.locations.push(new Position(locator.getLineNumber(), locator.getColumnNumber()));
    		this.elementStack.push(new LinkedList<Object>());
    		List<XMLAttribute> attributeList = new LinkedList<XMLAttribute>();
    		this.attributeStack.push(attributeList);

    		for (int i=0; i<attrs.getLength(); ++i) {
    			if (attrs.getURI(i).equals(""))
 	    			attributeList.add(new XMLAttribute(attrs.getLocalName(i), attrs.getValue(i)));
    		}
    		
    		if (localName.equals("schema"))
    			for(XMLAttribute attribute: attributeList)
    				if (attribute.localName.equals("targetNamespace"))
    					this.namespaceList.setTargetNamespace(namespaceList.getNamespaceByUri(attribute.value));
    		
    		this.elementNames.push(localName);
    	}
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException  {
    	try {
    		/* Do not parse below xs:documentation and xs:appinfo.
    		 * We track the depth to catch nested documentation elements
    		 */
    		if (insideDocumentation>0)
    			insideDocumentation--;
    		else if (uri.equals(XSDSchema.XMLSCHEMA_NAMESPACE)) {
				elementNames.pop();
				String parent = elementNames.peek();
				ElementType elementType = ElementType.getElementType(localName, parent);
				Object object = elementType.getInstance();
				
				Collection<Object> childs = elementStack.pop();
				Collection<Object> attributes = new LinkedList<Object>();
				Particle particle = null;
				
				if (object != null) {
					for (Object child: childs) {
						if ((child instanceof Particle) && (object instanceof PContainer))
							((PContainer) object).addParticle((Particle) child);
						else if ((child instanceof Particle) && (object instanceof Inheritance))
							particle = (Particle) child;
						else if (child instanceof Type) {
							this.types.add((Type) child);
							if (!(object instanceof XSDSchema)) {
								((Type) child).setName(uniqueTypename());
								((Type) child).setIsAnonymous(true);
								if (object instanceof Element) 
									((Element) object).setTypeName(((Type) child).getName());
								else if (object instanceof Inheritance)
									((Inheritance) object).setBaseType(((Type) child).getName());
								else if (object instanceof Attribute)
									((Attribute) object).setTypeName(((Type) child).getName());
								else
									throw new InvalidXSDException("Element of " + object.getClass() + " has a child of " + child.getClass());
							}
						} else if ((child instanceof AttributeParticle) && (object instanceof AContainer))
							((AContainer) object).addAttributeParticle((AttributeParticle) child);
						else if ((child instanceof Attribute) && (object instanceof ComplexContentType))
							attributes.add(child);
						else if ((child instanceof Attribute) && (object instanceof XSDSchema))
							((XSDSchema) object).addAttribute((Attribute) child); 
						else if ((child instanceof Annotation) && (object instanceof Annotationable))
							((Annotationable) object).setAnnotation((Annotation) child);
						else if ((child instanceof Annotation))	{} //silently ignore annotation elements for now.
						else if ((child instanceof Content) && (object instanceof ComplexType))
							((ComplexType) object).setContent((Content) child);
						else if ((child instanceof SimpleTypeInheritance) && (object instanceof SimpleType))
							((SimpleType) object).setInheritance((SimpleTypeInheritance) child);
						else if ((child instanceof Inheritance) && (object instanceof Content))
							((Content) object).setInheritance((Inheritance) child);
						else if ((child instanceof Constraint) && (object instanceof Element))
							((Element) object).addConstraint((Constraint) child);
						else if ((child instanceof Group) && (object instanceof XSDSchema))
							((XSDSchema) object).addGroup((Group) child);
						else if ((child instanceof AttributeGroup) && (object instanceof XSDSchema))
							((XSDSchema) object).addAttributeGroup((AttributeGroup) child);
						else if ((child instanceof Documentation) && (object instanceof Annotation))
							((Annotation) object).addDocumentations((Documentation) child);
						else if ((child instanceof AppInfo) && (object instanceof Annotation))
							((Annotation) object).addAppInfos((AppInfo) child);
						else if ((child instanceof Enumeration) && (object instanceof SimpleContentRestriction))
							((SimpleContentRestriction) object).addEnumeration(((Enumeration) child).value);
						else if ((child instanceof XSDParserHandler.Selector) && (object instanceof SimpleConstraint))
							((SimpleConstraint) object).setSelector(((XSDParserHandler.Selector) child).value);
						else if ((child instanceof XSDParserHandler.Field) && (object instanceof SimpleConstraint))
							((SimpleConstraint) object).addField(((XSDParserHandler.Field) child).value);
						else if ((child instanceof ForeignSchema) && (object instanceof XSDSchema))
							((XSDSchema) object).addForeignSchema((ForeignSchema) child);
						else if ((child instanceof SimpleContentRestrictionProperty) && (object instanceof SimpleContentRestriction)) {
							if (child instanceof MinInclusive)
								((SimpleContentRestriction) object).setMinInclusive((MinInclusive) child);
							else if (child instanceof MaxInclusive)
								((SimpleContentRestriction) object).setMaxInclusive((MaxInclusive) child);
							else if (child instanceof MinExclusive)
								((SimpleContentRestriction) object).setMinExclusive((MinExclusive) child);
							else if (child instanceof MaxInclusive)
								((SimpleContentRestriction) object).setMaxExclusive((MaxExclusive) child);
							else if (child instanceof TotalDigits)
								((SimpleContentRestriction) object).setTotalDigits((TotalDigits) child);
							else if (child instanceof FractionDigits)
								((SimpleContentRestriction) object).setFractionDigits((FractionDigits) child);
							else if (child instanceof Length)
								((SimpleContentRestriction) object).setLength((Length) child);
							else if (child instanceof MinLength)
								((SimpleContentRestriction) object).setMinLength((MinLength) child);
							else if (child instanceof MaxLength)
								((SimpleContentRestriction) object).setMaxLength((MaxLength) child);
							else if (child instanceof Pattern)
								((SimpleContentRestriction) object).setPattern((Pattern) child);
						} else
							throw new InvalidXSDException("Element of " + object.getClass() + " has a child of " + child.getClass());

					}
				}
				
				List<XMLAttribute> attributeList = attributeStack.pop();
				boolean counting = false;
				int minOccurs = 1;
				Integer maxOccurs = 1;
				
				if (object !=null ) {
					for (XMLAttribute attribute: attributeList) {
						QualifiedName name = null;

						if (attribute.localName.equals("type") || attribute.localName.equals("ref") || attribute.localName.equals("itemType") || attribute.localName.equals("base"))
							name = this.namespaceList.getQualifiedName(attribute.value, object instanceof Attribute);
						else if (attribute.localName.equals("name")) {
							if ((object instanceof Type) || (object instanceof Group)) 
								name = new QualifiedName(this.namespaceList.getTargetNamespace(), attribute.value);
							else
								name = this.namespaceList.getQualifiedName(attribute.value, object instanceof Attribute);
							if (object instanceof NamedXSDElement)
								((NamedXSDElement) object).setName(name);
							else
								throw new InvalidXSDException("Element of type " + object.getClass() + " has a name attribute.");
						} else if (attribute.localName.equals("type"))
							((TypedXSDElement) object).setTypeName(name);
						else if (attribute.localName.equals("minOccurs")) {
							counting = true;
							minOccurs = Integer.parseInt(attribute.value);
						} else if (attribute.localName.equals("maxOccurs")) {
							counting = true;
							if (attribute.value.equals("unbounded"))
								maxOccurs = null;
							else
								maxOccurs = Integer.parseInt(attribute.value);
						} else if (attribute.localName.equals("form")) {
							((TypedXSDElement) object).setForm(Qualification.valueOf(attribute.value));
						} else if (attribute.localName.equals("elementFormDefault"))
							((XSDSchema) object).setElementFormDefault(Qualification.valueOf(attribute.value));
						else if (attribute.localName.equals("attributeFormDefault"))
							((XSDSchema) object).setAttributeFormDefault(Qualification.valueOf(attribute.value));
						else if (attribute.localName.equals("targetNamespace")) {
							// already added in startElement
						} else if (attribute.localName.equals("ref") && (object instanceof Element))
							object = new ElementRef(name);
						else if (attribute.localName.equals("ref") && (object instanceof Attribute))
							object = new AttributeRef(name);
						else if (attribute.localName.equals("ref") && (object instanceof AttributeGroupReference))
							((AttributeGroupReference) object).setName(name);
						else if (attribute.localName.equals("ref") && (object instanceof GroupReference))
							((GroupReference) object).setName(name);
						else if (attribute.localName.equals("refer") && (object instanceof KeyRef)) 
							((KeyRef) object).setRefer(this.namespaceList.getQualifiedName(attribute.value));
						else if (attribute.localName.equals("use") && (object instanceof Attribute))
							((Attribute) object).setUse(AttributeUse.valueOf(attribute.value));
						else if (attribute.localName.equals("default") && (object instanceof Attribute))
							((Attribute) object).setDefault(attribute.value);
						else if (attribute.localName.equals("default") && (object instanceof Element))
							((Element) object).setDefault(attribute.value);
						else if (attribute.localName.equals("fixed") && (object instanceof Attribute))
							((Attribute) object).setFixed(attribute.value);
						else if (attribute.localName.equals("fixed") && (object instanceof Element))
							((Element) object).setFixed(attribute.value);
						else if (attribute.localName.equals("id") && (object instanceof ID))
							((ID) object).setId(attribute.value);
						else if (attribute.localName.equals("nillable") && (object instanceof Element)) {
							if (Boolean.parseBoolean(attribute.value))
								((Element) object).setNillable();
						}
						else if (attribute.localName.equals("memberTypes") && (object instanceof SimpleContentUnion))
							for (String memberTypeName: attribute.value.split("\\s+")) {
								((SimpleContentUnion) object).addMemberType(this.namespaceList.getQualifiedName(memberTypeName));
							}
						else if (attribute.localName.equals("processContents") && (object instanceof AnyAttribute))
							((AnyAttribute) object).setProcessContentsInstruction(ProcessContentsInstruction.valueOf(attribute.value.toUpperCase()));
						else if (attribute.localName.equals("processContents") && (object instanceof AnyPattern))
							((AnyPattern) object).setProcessContentsInstruction(ProcessContentsInstruction.valueOf(attribute.value.toUpperCase()));
						else if (attribute.localName.equals("namespace") && (object instanceof AnyAttribute))
							((AnyAttribute) object).setNamespace(attribute.value);
						else if (attribute.localName.equals("namespace") && (object instanceof AnyPattern))
							((AnyPattern) object).setNamespace(attribute.value);
						else if (attribute.localName.equals("itemType") && (object instanceof SimpleContentList))
							((SimpleContentList) object).setBaseType(name);
						else if (attribute.localName.equals("base") && (object instanceof Inheritance))
							((Inheritance) object).setBaseType(name);
						else if (attribute.localName.equals("source") && (object instanceof Documentation))
							((Documentation) object).setSource(attribute.value);
						else if (attribute.localName.equals("mixed") && (object instanceof ComplexType))
							((ComplexType) object).setMixed(Boolean.parseBoolean(attribute.value));
						else if (attribute.localName.equals("mixed") && (object instanceof ComplexContentType))
							((ComplexContentType) object).setMixed(Boolean.parseBoolean(attribute.value));
						else if (attribute.localName.equals("abstract") && (object instanceof ComplexType))
							((ComplexType) object).setAbstract(Boolean.parseBoolean(attribute.value));
						else if (attribute.localName.equals("abstract") && (object instanceof Element))
							((Element) object).setAbstract(Boolean.parseBoolean(attribute.value));
						else if ((attribute.localName.equals("value") || attribute.localName.equals("xpath"))  && (object instanceof VContainer))
							((VContainer) object).value = attribute.value;
						else if (attribute.localName.equals("fixed") && attribute.value.equals("true") && (object instanceof SimpleContentFixableRestrictionProperty))
							((SimpleContentFixableRestrictionProperty) object).setFixed(true);
						else if (attribute.localName.equals("value") && (object instanceof Whitespace))
							((Whitespace) object).setValue(SimpleContentPropertyWhitespace.valueOf(attribute.value));
						else if (attribute.localName.equals("value") &&
								((object instanceof MinInclusive) || (object instanceof MaxInclusive) ||
							     (object instanceof MinExclusive) || (object instanceof MaxExclusive) ||
							     (object instanceof Pattern)))
							((SimpleContentRestrictionProperty) object).setValue(attribute.value);
						else if (attribute.localName.equals("value") &&
								((object instanceof MinLength) || (object instanceof MaxLength) ||
							     (object instanceof Length) ||
							     (object instanceof FractionDigits) || (object instanceof TotalDigits)))
							((SimpleContentRestrictionProperty) object).setValue(Integer.parseInt(attribute.value));
						
						else if (attribute.localName.equals("id")) {} // ignore id attributes not handled yet
						else if (attribute.localName.equals("version") && object instanceof XSDSchema) {} // ignore version. this attribute has no semantics according to XSD specs
						else if (attribute.localName.equals("substitutionGroup")) { System.err.println("Substitution groups are not supported"); }
						else
							System.err.println("Unhandled attribute with name " + attribute.localName + " and value " + attribute.value + " in " + object.getClass());
					}
				}

				Position opening = this.locations.pop();
				Position closing = new Position(locator.getLineNumber(), locator.getColumnNumber());
				
				if (object instanceof Locatable) {
					((Locatable) object).setStartPosition(opening);
					((Locatable) object).setEndPosition(closing);
				}
	    		
	    		if (counting)
	    			if (object instanceof Particle)
	    				object = new CountingPattern((Particle) object, minOccurs, maxOccurs);
	    			else
	    				throw new InvalidXSDException("Element of type " + object.getClass() + " has min- or maxOccurs attribute.");
	    		
	    		if (object != null)
	    			this.elementStack.peek().add(object);
	    		
	    		if (! attributes.isEmpty())
	    			this.elementStack.peek().addAll(attributes);
	    		
	    		if (particle!=null)
	    			this.elementStack.peek().add(particle);
			}
		} catch (SchemaToolkitException e) {
			throw new XSDParserException(e);
		}
    }


	private QualifiedName uniqueTypename() {
        String uniqueRandID = java.util.UUID.randomUUID().toString();
        return new QualifiedName(this.namespaceList.getTargetNamespace(),uniqueRandID);
	}

	@Override
    public void endDocument() throws NoXSDException {
		if (topLevel)
			throw new NoXSDException("Root element is not xs:schema");
		this.schema = (XSDSchema) this.elementStack.pop().get(0);
    	this.schema.setNamespaceList(namespaceList);
    	for (Type type: types)
    		this.schema.addType(type);
    }

	public XSDSchema getSchema() {
		return schema;
	}
}
