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
		keyRef(KeyRef.class),
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
		ATTRIBUTEGROUP(AttributeGroup.class);

		private Class<Object> elementClass;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private ElementType(Class elementClass) {
			this.elementClass = elementClass;
		}

		public Object getInstance() {
			if (this.elementClass!=null) {
				try {
					return this.elementClass.newInstance();
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			} else {
				System.err.println("Unhandled element with name " + this.toString());
				return null;
			}
		}

		public static ElementType getElementType(String elementType, String parent) {
			if (parent.equals("schema")) {
				if (elementType.equals("import"))
					return IMPORT;
				if (elementType.equals("group"))
					return GROUP;
				if (elementType.equals("attributeGroup"))
					return ATTRIBUTEGROUP;
			} 
			if (parent.equals("complexContent")) {
				if (elementType.equals("extension"))
					return COMPLEXExtension;
				if (elementType.equals("restriction"))
					return COMPLEXRestriction;
			}
			return valueOf(elementType);
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
    	if (uri.equals(XSDSchema.XMLSCHEMA_NAMESPACE)) {
    		this.locations.push(new Position(locator.getLineNumber(), locator.getColumnNumber()));
    		this.elementStack.push(new LinkedList<Object>());
    		List<XMLAttribute> attributeList = new LinkedList<XMLAttribute>();
    		this.attributeStack.push(attributeList);

    		for (int i=0; i<attrs.getLength(); ++i) {
//    			String attributeName = attrs.getLocalName(i);
//    			System.err.println("START: Element " + localName + " Attribute " + attributeName);
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
			if (uri.equals(XSDSchema.XMLSCHEMA_NAMESPACE)) {
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
									System.err.println("Element of " + object.getClass() + " has a child of " + child.getClass());
							}
						} else if ((child instanceof AttributeParticle) && (object instanceof AContainer))
							((AContainer) object).addAttributeParticle((AttributeParticle) child);
						else if ((child instanceof Attribute) && (object instanceof ComplexContentType))
							attributes.add(child);
						else if ((child instanceof Annotation) && (object instanceof Annotationable))
							((Annotationable) object).setAnnotation((Annotation) child);
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
							System.err.println("Element of " + object.getClass() + " has a child of " + child.getClass());

					}
				}
				
				List<XMLAttribute> attributeList = attributeStack.pop();
				boolean counting = false;
				int minOccurs = 1;
				Integer maxOccurs = 1;
				
				if (object !=null ) {
					for (XMLAttribute attribute: attributeList) {
						QualifiedName name = null;

						if (attribute.localName.equals("name") || attribute.localName.equals("type") || attribute.localName.equals("ref") || attribute.localName.equals("itemType") || attribute.localName.equals("base"))
							name = this.namespaceList.getQualifiedName(attribute.value, object instanceof Attribute);

						if (attribute.localName.equals("name"))
							((NamedXSDElement) object).setName(name);
						else if (attribute.localName.equals("type"))
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
						else if (attribute.localName.equals("use") && (object instanceof Attribute))
							((Attribute) object).setUse(AttributeUse.valueOf(attribute.value));
						else if (attribute.localName.equals("default") && (object instanceof Attribute))
							((Attribute) object).setDefault(attribute.value);
						else if (attribute.localName.equals("fixed") && (object instanceof Attribute))
							((Attribute) object).setFixed(attribute.value);
						else if (attribute.localName.equals("id") && (object instanceof ID))
							((ID) object).setId(attribute.value);
						else if (attribute.localName.equals("processContents") && (object instanceof AnyAttribute))
							((AnyAttribute) object).setProcessContentsInstruction(ProcessContentsInstruction.valueOf(attribute.value.toUpperCase()));
						else if (attribute.localName.equals("processContents") && (object instanceof AnyPattern))
							((AnyPattern) object).setProcessContentsInstruction(ProcessContentsInstruction.valueOf(attribute.value.toUpperCase()));
						else if (attribute.localName.equals("namespace") && (object instanceof AnyAttribute))
							((AnyAttribute) object).setNamespace(attribute.value);
						else if (attribute.localName.equals("itemType") && (object instanceof SimpleContentList))
							((SimpleContentList) object).setBaseType(name);
						else if (attribute.localName.equals("base") && (object instanceof Inheritance))
							((Inheritance) object).setBaseType(name);
						else if (attribute.localName.equals("source") && (object instanceof Documentation))
							((Documentation) object).setSource(attribute.value);
						else if (attribute.localName.equals("abstract") && attribute.value.equals("true") && (object instanceof ComplexType))
							((ComplexType) object).setAbstract(true);
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
						
						else
							System.err.println("Unhandled attribute with name " + attribute.localName + " in " + object.getClass());
					}
				}

				Position opening = this.locations.pop();
				Position closing = new Position(locator.getLineNumber(), locator.getColumnNumber());
				
				if (object instanceof Locatable) {
					((Locatable) object).setStartPosition(opening);
					((Locatable) object).setEndPosition(closing);
				}
	    		
	    		if (counting)
	    			object = new CountingPattern((Particle) object, minOccurs, maxOccurs);
	    		
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
    public void endDocument() {
    	this.schema = (XSDSchema) this.elementStack.pop().get(0);
    	this.schema.setNamespaceList(namespaceList);
    	for (Type type: types)
    		this.schema.addType(type);
    }

	public XSDSchema getSchema() {
		return schema;
	}
}
