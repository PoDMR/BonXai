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

package eu.fox7.schematoolkit.converter.bonxai2xsd;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.bonxai.om.*;
import eu.fox7.schematoolkit.bonxai.om.ElementRef;
import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema.Qualification;

/**
 * Bonxai2XSDConverter for group declarations inside an Bonxai schema.
 */
public class ParticleConverter {
	private static class GroupTodo {
		public GroupTodo(QualifiedName sourceName, State sourceState, QualifiedName targetName) {
			this.sourceName = sourceName;
			this.sourceState = sourceState;
			this.targetName = targetName;
		}
		public QualifiedName sourceName;
		public State sourceState;
		public QualifiedName targetName;
	}
	
	private TypeAutomaton typeAutomaton;
	
	private Map<QualifiedName,Map<State,QualifiedName>> groupMap = new HashMap<QualifiedName,Map<State,QualifiedName>>();
	private List<GroupTodo> groupTodo = new LinkedList<GroupTodo>();
	private boolean eliminateGroups;
	
	public ParticleConverter(TypeAutomaton typeAutomaton, boolean eliminateGroups) {
		this.typeAutomaton = typeAutomaton;
		this.eliminateGroups = eliminateGroups;
	}
	
    /**
     * Convert a particle.
     *
     * Converts a single particle, or element pattern, to XSD.
     *
     * @return particle
     */
    public Particle convertParticle(Namespace namespace, Particle particle, State sourceState) {
        if ((particle instanceof eu.fox7.schematoolkit.bonxai.om.Element)
                && (((eu.fox7.schematoolkit.bonxai.om.Element) particle).getName().getNamespaceURI().equals(namespace.getUri()))) {
            eu.fox7.schematoolkit.bonxai.om.Element source = (eu.fox7.schematoolkit.bonxai.om.Element) particle;

            eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element(source.getName());

            if (source.getType() != null) {
            	BonxaiType type = source.getType();
                if (type.getDefaultValue() != null) {
                    element.setDefault(type.getDefaultValue());
                }

                if (type.getFixedValue() != null) {
                    element.setFixed(type.getFixedValue());
                }

                if (source.getType().isNillable()) {
                    element.setNillable();
                }

                element.setTypeName(source.getType().getTypename());
            } else {
            	State toState;
				try {
					toState = typeAutomaton.getNextState(Symbol.create(source.getName().getFullyQualifiedName()), sourceState);
					QualifiedName typename = typeAutomaton.getTypeName(toState);
					element.setTypeName(typename);
					ElementProperties elementProperties = typeAutomaton.getElementProperties(toState);
					if (elementProperties != null)
						element.setProperties(elementProperties);
				} catch (NotDFAException e) {
					throw new RuntimeException(e);
				}
            }

            return element;
        } else if (particle instanceof ElementRef) {
        	ElementRef elementRef = (ElementRef) particle;
        	particle = new eu.fox7.schematoolkit.common.ElementRef(elementRef.getElementName());
        	return particle;
        } else if (particle instanceof eu.fox7.schematoolkit.common.AnyPattern) {
            // replace all occurrences of "," in the namespace attribute with " " spaces.
            AnyPattern anyPattern = (eu.fox7.schematoolkit.common.AnyPattern) particle;
            particle = new AnyPattern(anyPattern.getProcessContentsInstruction(), anyPattern.getNamespaces());
            return particle;
        } else if (particle instanceof eu.fox7.schematoolkit.bonxai.om.Element) {
            // For elements in foreign namespaces we just create an element
            // reference
            return new eu.fox7.schematoolkit.common.ElementRef(((eu.fox7.schematoolkit.bonxai.om.Element) particle).getName());
        } else if (particle instanceof GroupReference) {
            return convertGroupReference(((GroupReference) particle), sourceState);
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer container = null;
            if (particle instanceof SequencePattern) {
                container = new SequencePattern();
            } else if (particle instanceof ChoicePattern) {
                container = new ChoicePattern();
            } else if (particle instanceof AllPattern) {
                container = new AllPattern();
            } 

            /* traverse through the List of particles in this sequence to add
             * the elements to the container
             */
            if (!((ParticleContainer) particle).getParticles().isEmpty()) {
                for (Particle currentParticle : ((ParticleContainer) particle).getParticles()) {
                    container.addParticle(convertParticle(namespace, currentParticle, sourceState));
                }
            }

            return container;
        } else if (particle instanceof CountingPattern) {
        	return new CountingPattern(convertParticle(namespace, ((CountingPattern) particle).getParticle(), sourceState),
        			((CountingPattern) particle).getMin(),
        			((CountingPattern) particle).getMax());
        } else if (particle instanceof EmptyPattern) {
        	return new EmptyPattern();
        } else {
            throw new RuntimeException("Particle type not supported: " + particle.getClass());
        }
    }

    private Particle convertGroupReference(GroupReference groupReference,
			State sourceState) {
    	QualifiedName newGroupName = convertGroupReference(groupReference.getName(), sourceState);
    	return new GroupReference(newGroupName);
    }
    
    private AttributeParticle convertAttributeGroupReference(AttributeGroupReference attributeGroupReference, State sourceState) {
    	QualifiedName newGroupName = convertGroupReference(attributeGroupReference.getName(), sourceState);
    	return new AttributeGroupReference(newGroupName);
    }

    private QualifiedName convertGroupReference(QualifiedName groupName, State sourceState) {
    	QualifiedName newGroupName = null;
    	if (eliminateGroups) {
    		//TODO
    		return null;
    	} else {
    		Map<State,QualifiedName> stateGroupMap = groupMap.get(groupName);
    		if (stateGroupMap == null) {
    			stateGroupMap = new HashMap<State,QualifiedName>();
    			groupMap.put(groupName, stateGroupMap);
    			stateGroupMap.put(sourceState, groupName);
    			groupTodo.add(new GroupTodo(groupName, sourceState, groupName));
    			newGroupName = groupName;
    		} else {
    			newGroupName = stateGroupMap.get(sourceState);
    			if (newGroupName == null) {
    				int number = 1;
    				newGroupName = new QualifiedName(groupName.getNamespaceURI(),groupName.getName()+number);
    				while (stateGroupMap.values().contains(newGroupName)) {
    					number++;
    					newGroupName = new QualifiedName(groupName.getNamespaceURI(),groupName.getName()+number);
    				}
    				stateGroupMap.put(sourceState, newGroupName);
    				groupTodo.add(new GroupTodo(groupName, sourceState, newGroupName));
    			}
    		}
    		return newGroupName;
    	}
    }
    
	/**
     * Convert an attribute pattern.
     *
     * Converts an attribute pattern to the equivalent classes in XSD.
	 * @param sourceState 
     *
     * @return particle
     */
    public LinkedList<AttributeParticle> convertParticle(AttributePattern pattern, State sourceState) {
        LinkedList<AttributeParticle> list = new LinkedList<AttributeParticle>();

        // Append any attribute, if available
        if (pattern.getAnyAttribute() != null) {
            list.add(pattern.getAnyAttribute());
        }

        if (pattern.getAttributeList() != null) {
            for (AttributeParticle attribute : pattern.getAttributeList()) {
                list.add(this.convertAttribute(attribute, sourceState));
            }
        }

        return list;
    }

    /**
     * Convert a single attribute.
     * @param sourceState 
     *
     * @return attributeParticle
     */
    protected AttributeParticle convertAttribute(AttributeParticle attribute, State sourceState) {
        if (attribute instanceof eu.fox7.schematoolkit.bonxai.om.Attribute) {
        	eu.fox7.schematoolkit.bonxai.om.Attribute bonxaiAttribute = (eu.fox7.schematoolkit.bonxai.om.Attribute) attribute;
        	BonxaiType type = bonxaiAttribute.getType();
        	if (type==null) {
        		State targetState;
				try {
					targetState = this.typeAutomaton.getNextState(Symbol.create(bonxaiAttribute.getName().getFullyQualifiedName()), sourceState);
	        		if (targetState!=null) {
	        			ElementProperties elementProperties = typeAutomaton.getElementProperties(targetState);
	        			if (elementProperties!=null) {
	        				type = new BonxaiType(typeAutomaton.getTypeName(targetState));
	        				type.setDefaultValue(elementProperties.getDefaultValue());
	        				type.setFixedValue(elementProperties.getFixedValue());
	        			}
	        		}
				} catch (NotDFAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				bonxaiAttribute = new Attribute(bonxaiAttribute.getName(), type, bonxaiAttribute.isRequired());
        	}
        	
        	QualifiedName typename = null;
        	if (type!=null)
        		typename = type.getTypename();
        	
            AttributeUse attributeUse = bonxaiAttribute.isRequired()?AttributeUse.required:AttributeUse.optional;
			return new eu.fox7.schematoolkit.xsd.om.Attribute(bonxaiAttribute.getName(), typename, bonxaiAttribute.getDefault(), bonxaiAttribute.getFixed(), attributeUse, Qualification.unqualified, null);
        } else if (attribute instanceof AttributeGroupReference) {
            return this.convertAttributeGroupReference((AttributeGroupReference) attribute, sourceState);
        } else if (attribute instanceof AttributeRef) {
        	AttributeRef attributeRef = (AttributeRef) attribute;
        	AttributeRef newAttributeRef = new AttributeRef(attributeRef.getName(), attributeRef.getDefault(), attributeRef.getFixed(), attributeRef.getUse(), null);
        	return newAttributeRef;
        } else {
        	throw new RuntimeException("Attribute class not supported: " + attribute.getClass());
        }
    }
    
    public void convertGroups(Bonxai bonxai, XSDSchema xsdSchema) {
    	while (! groupTodo.isEmpty()) {
    		GroupTodo todo = groupTodo.iterator().next();
    		groupTodo.remove(0);
    		BonxaiGroup group = bonxai.getElementGroup(todo.sourceName);
    		BonxaiAttributeGroup attributeGroup = bonxai.getAttributeGroup(todo.sourceName);
    		if (group!=null) {
    			Particle particle = convertParticle(bonxai.getTargetNamespace(),group.getParticle(),todo.sourceState);
    			eu.fox7.schematoolkit.xsd.om.Group newGroup = new eu.fox7.schematoolkit.xsd.om.Group(todo.targetName, particle);
    			xsdSchema.addGroup(newGroup);
    		}
    		if (attributeGroup!=null) {
    			eu.fox7.schematoolkit.xsd.om.AttributeGroup newAttributeGroup = new eu.fox7.schematoolkit.xsd.om.AttributeGroup(todo.targetName);
    			if (attributeGroup.getAttributePattern().getAnyAttribute()!=null)
    				newAttributeGroup.addAttributeParticle(attributeGroup.getAttributePattern().getAnyAttribute());
    			for (AttributeParticle attributeParticle: attributeGroup.getAttributePattern().getAttributeList()) {
    				AttributeParticle newAttributeParticle = convertAttribute(attributeParticle, todo.sourceState);
        			newAttributeGroup.addAttributeParticle(newAttributeParticle);
    			}
    			xsdSchema.addAttributeGroup(newAttributeGroup);
    		}
    	}
    }
}

