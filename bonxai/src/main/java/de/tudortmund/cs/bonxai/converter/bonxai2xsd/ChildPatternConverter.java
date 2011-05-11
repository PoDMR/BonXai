package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import gjb.flt.automata.impl.sparse.State;

import java.util.HashSet;

import de.tudortmund.cs.bonxai.bonxai.AttributeGroupElement;
import de.tudortmund.cs.bonxai.bonxai.AttributeGroupReference;
import de.tudortmund.cs.bonxai.bonxai.AttributeListElement;
import de.tudortmund.cs.bonxai.bonxai.AttributePattern;
import de.tudortmund.cs.bonxai.bonxai.ChildPattern;
import de.tudortmund.cs.bonxai.common.EmptyPattern;
import de.tudortmund.cs.bonxai.common.SymbolTableFoundation;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.xsd.AttributeGroup;
import de.tudortmund.cs.bonxai.xsd.AttributeParticle;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.SimpleContentExtension;
import de.tudortmund.cs.bonxai.xsd.SimpleContentType;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Type;

public class ChildPatternConverter {
	
	private SymbolTableFoundation<AttributeGroup> attributeGroupSymbolTable;
	private SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Element> elementSymbolTable;
	private SymbolTableFoundation<Type> typeSymbolTable;
	private SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> groupSymbolTable;
	ParticleConverter converter;
    private HashSet<String> attributeGroupNames;
    private String targetNamespace;
    private TypeAutomaton typeAutomaton;
    
    public ChildPatternConverter(TypeAutomaton typeAutomaton, String targetNamespace, SymbolTableFoundation<AttributeGroup> attributeGroupSymbolTable, 
    		SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Element> elementSymbolTable,
    		SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> groupSymbolTable,
    		SymbolTableFoundation<Type> typeSymbolTable) {
    	this.attributeGroupSymbolTable = attributeGroupSymbolTable;
    	this.elementSymbolTable = elementSymbolTable;
    	this.groupSymbolTable = groupSymbolTable;
    	this.typeSymbolTable = typeSymbolTable;
    	this.targetNamespace = targetNamespace;
    	this.typeAutomaton = typeAutomaton;
    	this.converter = new ParticleConverter(typeAutomaton, attributeGroupSymbolTable, elementSymbolTable, groupSymbolTable, typeSymbolTable);
    }
    
    /**
     * Convert a child pattern to an XSD Type.
     *
     * @return Type
     */
    public void convertChildPattern(ChildPattern childPattern, String typename, State sourceState) {
        String key = typeAutomaton.getStateValue(sourceState);

        // Check if there are attribute groups which should be converted
        if (childPattern.getAttributePattern() != null) {
            this.convertContainedAttributeGroups(childPattern.getAttributePattern());
        }

        // Convert simple types, if no attributes are set, otherwise it will
        // get a bit more complicated...
        if ((childPattern.getElementPattern() != null) &&
            (childPattern.getElementPattern().getBonxaiType() != null)) {
            String typeName = childPattern.getElementPattern().getBonxaiType().getFullQualifiedName();

        	Type simpleType = new SimpleType(typeName, null);
        	simpleType.setIsAnonymous(true);

            if (childPattern.getAttributePattern() == null ) {
                typeSymbolTable.updateOrCreateReference(key, simpleType);
            } else {
                typeSymbolTable.updateOrCreateReference(key, simpleType);
                SimpleContentExtension extension = new SimpleContentExtension(typeSymbolTable.getReference(typeName));

                SimpleContentType content = new SimpleContentType();
                content.setInheritance(extension);

                ComplexType type = new ComplexType(typeName, content);

                for (AttributeParticle attrParticle: converter.convertParticle(targetNamespace, childPattern.getAttributePattern())) {
                    type.addAttribute(attrParticle);
                }

                typeSymbolTable.updateOrCreateReference(key, type);
            }
        }

        // Convert complex types
        if ((childPattern.getElementPattern() != null) &&
            (childPattern.getElementPattern().getRegexp() != null)) {
            String name = "{"+targetNamespace+"}"+typename;

            ComplexContentType content = new ComplexContentType();
            content.setParticle(converter.convertParticle(targetNamespace, childPattern.getElementPattern().getRegexp(), sourceState));
            content.setMixed(childPattern.getElementPattern().isMixed());
            ComplexType type = new ComplexType(name, content);
         

            // Append attributes, if existing
            if (childPattern.getAttributePattern() != null)
            {
                for (AttributeParticle attrParticle: converter.convertParticle(targetNamespace, childPattern.getAttributePattern())) {
                    type.addAttribute(attrParticle);
                }
            }

            type.setIsAnonymous(false);
            
            typeSymbolTable.updateOrCreateReference(key, type);
        }

        // Convert attributes, if no element pattern has been specified
        if ((childPattern.getAttributePattern() != null) &&
            ((childPattern.getElementPattern() == null) || (childPattern.getElementPattern().getRegexp() instanceof EmptyPattern))) {
            String name = "{"+targetNamespace+"}"+typename;

            ComplexType type = new ComplexType(name, null);
            for (AttributeParticle attrParticle: converter.convertParticle(targetNamespace, childPattern.getAttributePattern())) {
                type.addAttribute(attrParticle);
            }
            
            type.setIsAnonymous(false);

            typeSymbolTable.updateOrCreateReference(key, type);
        }
    }
    
    /**
     * Locate and convert attribute groups.
     *
     * Locate attribute groups in the current element and create the attribute
     * groups in the target schema of the current element. This is used to
     * determine the namespaces each attribute group is used in.
     */
    protected void convertContainedAttributeGroups(AttributePattern pattern) {
//        XSDSchema xsd                  = this.getSchema(element.getNamespace());
//        ParticleConverter converter = new ParticleConverter(attributeGroupSymbolTable, elementSymbolTable, groupSymbolTable, typeSymbolTable);
        attributeGroupNames = new HashSet<String>();
        for (AttributeGroupReference attr: this.findAttributeGroupRefs(pattern)) {
            AttributeGroupElement group   = attr.getGroupElement();
            AttributeGroup attributeGroup = new AttributeGroup("{" + targetNamespace + "}" + group.getName());
            for (AttributeParticle particle: converter.convertParticle(targetNamespace, group.getAttributePattern())) {
                attributeGroup.addAttributeParticle(particle);
            }
            attributeGroupSymbolTable.updateOrCreateReference(attributeGroup.getName(), attributeGroup);
        }
    }
    
    /**
     * Find all Attribute group references.
     *
     * Recursivly find all AttributeGroupReference onjects in the given
     * attribute pattern.
     */
    protected HashSet<AttributeGroupReference> findAttributeGroupRefs(AttributePattern pattern) {
        HashSet<AttributeGroupReference> set = new HashSet<AttributeGroupReference>();

        for (AttributeListElement attribute: pattern.getAttributeList()) {
            if (attribute instanceof AttributeGroupReference) {
                if (!attributeGroupNames.contains(((AttributeGroupReference) attribute).getName())) {
                    attributeGroupNames.add(((AttributeGroupReference) attribute).getName());
                    set.add((AttributeGroupReference) attribute);

                    // Recurse into attribute group
                    set.addAll(this.findAttributeGroupRefs(((AttributeGroupReference) attribute).getGroupElement().getAttributePattern()));
                }
            }
        }

        return set;
    }
 }
