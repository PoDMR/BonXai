/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fox7.schematoolkit.xsd.writer;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Node;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;

public abstract class ParticleWriter {

    /**
     * Writes the particle node.
     */
    protected static void writeParticle(Node root, Particle particle, XSDSchema schema) {
        writeParticle(root, particle, schema, 1, 1);
    }

    /**
     * Writes the particle node with given min and max occurs.
     */
    protected static void writeParticle(Node root, Particle particle, XSDSchema schema, Integer min, Integer max) {
        if (particle instanceof ElementRef) {
            writeElementRef(root, (ElementRef) particle, min, max, schema);
        } else if (particle instanceof AnyPattern) {
            writeAnyPattern(root, (AnyPattern) particle, min, max, schema);
        } else if (particle instanceof eu.fox7.schematoolkit.xsd.om.Element) {
            writeElement(root, (eu.fox7.schematoolkit.xsd.om.Element) particle, schema, min, max);
        } else if (particle instanceof GroupReference) {
            writeGroupRef(root, (GroupReference) particle, min, max, schema);
        } else if (particle instanceof CountingPattern) {
            writeCountingPattern(root, (CountingPattern) particle, schema);
        } else if (particle instanceof ParticleContainer) {
            writeParticleContainer(root, (ParticleContainer) particle, schema, min,
                    max);
        }
    }

    /**
     * Writes the element reference node.
     */
    protected static void writeElementRef(Node root, ElementRef elementRef, Integer min, Integer max, XSDSchema schema) {
        org.w3c.dom.Element elementRefNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "element");
        DOMHelper.setXSDPrefix(elementRefNode, schema);
        setOccurrence(elementRefNode, min, max);
        elementRefNode.setAttribute("ref", elementRef.getElementName().getQualifiedName());
        AnnotationWriter.writeAnnotation(elementRefNode, elementRef, schema);
        if (elementRef.getId() != null) {
            elementRefNode.setAttribute("id", elementRef.getId());
        }
        root.appendChild(elementRefNode);
    }

    /**
     * Writes the element node.
     */
    protected static void writeElement(Node root, eu.fox7.schematoolkit.xsd.om.Element element, XSDSchema schema) {
        writeElement(root, element, schema, 1, 1);
    }

    /**
     * Writes the element node with given min and max occurs.
     * @param root
     * @param element
     * @param foundElements
     * @param min
     * @param max
     */
    protected static void writeElement(Node root, eu.fox7.schematoolkit.xsd.om.Element element, XSDSchema schema, Integer min, Integer max) {
        org.w3c.dom.Element elementNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "element");
        DOMHelper.setXSDPrefix(elementNode, schema);
        elementNode.setAttribute("name", element.getName().getQualifiedName());
        setOccurrence(elementNode, min, max);
        if (element.getDefault() != null) {
            elementNode.setAttribute("default", element.getDefault());
        }
        if (element.getFixed() != null) {
            elementNode.setAttribute("fixed", element.getFixed());
        }
        if (element.getAbstract()) {
            elementNode.setAttribute("abstract", "true");
        }
        if (element.isNillable()) {
            elementNode.setAttribute("nillable", "true");
        }
        if (element.getForm() != null) {
            if (element.getForm().equals(XSDSchema.Qualification.qualified)) {
                elementNode.setAttribute("form", "qualified");
            } else {
                elementNode.setAttribute("form", "unqualified");
            }
        }
        String finalString = "";
        if (element.getFinalModifiers() != null && !element.getFinalModifiers().isEmpty()) {
            boolean finalExtension = false, finalRestriction = false;
            for (eu.fox7.schematoolkit.xsd.om.Element.Final currentFinalValue : element.getFinalModifiers()) {
                switch (currentFinalValue) {
                    case extension:
                        finalExtension = true;
                        break;
                    case restriction:
                        finalRestriction = true;
                        break;
                }
            }
            if (finalExtension && finalRestriction) {
                finalString = "#all";
            } else {
                if (finalExtension) {
                    finalString = finalString + "extension ";
                }
                if (finalRestriction) {
                    finalString = finalString + "restriction ";
                }
                if (finalString.length() > 0) {
                    finalString = finalString.substring(0, finalString.length() - 1);
                }
            }
        }
        if (element.getFinalModifiers() != null) {
            elementNode.setAttribute("final", finalString);
        }
        String blockString = "";
        if (element.getBlockModifiers() != null && !element.getBlockModifiers().isEmpty()) {
            boolean blockExtension = false, blockRestriction = false, blockSubstitution = false;
            for (eu.fox7.schematoolkit.xsd.om.Element.Block currentBlockValue : element.getBlockModifiers()) {
                switch (currentBlockValue) {
                    case extension:
                        blockExtension = true;
                        break;
                    case restriction:
                        blockRestriction = true;
                        break;
                    case substitution:
                        blockSubstitution = true;
                        break;
                }
            }
            if (blockExtension && blockRestriction && blockSubstitution) {
                blockString = "#all";
            } else {
                if (blockExtension) {
                    blockString = blockString + "extension ";
                }
                if (blockRestriction) {
                    blockString = blockString + "restriction ";
                }
                if (blockSubstitution) {
                    blockString = blockString + "substitution ";
                }
                if (blockString.length() > 0) {
                    blockString = blockString.substring(0, blockString.length() - 1);
                }
            }
        }
        if (element.getBlockModifiers() != null) {
            elementNode.setAttribute("block", blockString);
        }
        AnnotationWriter.writeAnnotation(elementNode, element, schema);
        if (element.getId() != null) {
            elementNode.setAttribute("id", element.getId());
        }
        root.appendChild(elementNode);
        
        Type type = schema.getType(element.getTypeName());
        if (type != null && !type.getName().getName().equals("anyType") && type.isAnonymous())
        	TypeWriter.writeType(elementNode, type, schema, false);
        else
        	elementNode.setAttribute("type", element.getTypeName().getQualifiedName());

        for (Constraint constraint : element.getConstraints())
            ConstraintWriter.writeConstraint(elementNode, constraint, schema);

        if (element.getSubstitutionGroup() != null)
            elementNode.setAttribute("substitutionGroup", element.getSubstitutionGroup().getQualifiedName());
    }

    /**
     * Writes the group reference node.
     * @param root
     * @param groupRef
     * @param foundElements
     * @param min
     * @param max
     */
    protected static void writeGroupRef(Node root, GroupReference groupRef, Integer min, Integer max, XSDSchema schema) {
        org.w3c.dom.Element groupNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "group");
        DOMHelper.setXSDPrefix(groupNode, schema);
        groupNode.setAttribute("ref", groupRef.getName().getQualifiedName());
        setOccurrence(groupNode, min, max);
        AnnotationWriter.writeAnnotation(groupNode, groupRef, schema);
        if (groupRef.getId() != null) {
            groupNode.setAttribute("id", groupRef.getId());
        }
        root.appendChild(groupNode);
    }

    /**
     * Writes the particle container node.
     */
    protected static void writeParticleContainer(Node root, ParticleContainer particleContainer, XSDSchema schema) {
        writeParticleContainer(root, particleContainer, schema, 1, 1);
    }

    /**
     * Writes the particle container node with given min and max occurs.
     */
    protected static void writeParticleContainer(Node root, ParticleContainer particleContainer, XSDSchema schema, Integer min, Integer max) {
    	String containerName = "";
    	if (particleContainer instanceof SequencePattern) {
    		containerName = "sequence";
    	} else if (particleContainer instanceof ChoicePattern) {
    		containerName = "choice";
    	} else if (particleContainer instanceof AllPattern) {
    		containerName = "all";
    	}
    	org.w3c.dom.Element containerNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", containerName);
    	DOMHelper.setXSDPrefix(containerNode, schema);
    	setOccurrence(containerNode, min, max);
    	AnnotationWriter.writeAnnotation(containerNode, particleContainer, schema);
    	if (particleContainer.getId() != null) {
    		containerNode.setAttribute("id", particleContainer.getId());
    	}
    	root.appendChild(containerNode);
    	LinkedList<Particle> particles = particleContainer.getParticles();
    	while (!particles.isEmpty()) {
    		writeParticle(containerNode, particles.pollFirst(), schema, 1, 1);
    	}
    }


    /**
     * Writes the counting pattern node.
     */
    protected static void writeCountingPattern(Node root, CountingPattern countingPattern, XSDSchema schema) {
        Particle particle = countingPattern.getParticle();
        writeParticle(root, particle, schema, countingPattern.getMin(), countingPattern.getMax());
    }

    /**
     * Adds minOccurs or maxOccurs attributes to an element, when min!=1 or
     * max!=1.
     */
    protected static void setOccurrence(org.w3c.dom.Element el, Integer min, Integer max) {
        if (min != 1) {
            el.setAttribute("minOccurs", min.toString());
        }
        if (max == null) {
            el.setAttribute("maxOccurs", "unbounded");
        } else if (max != 1) {
            el.setAttribute("maxOccurs", max.toString());
        }
    }

    /**
     * Writes the any pattern node.
     */
    protected static void writeAnyPattern(Node root, AnyPattern anyPattern, Integer min, Integer max, XSDSchema schema) {
        org.w3c.dom.Element anyPatternNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "any");
        DOMHelper.setXSDPrefix(anyPatternNode, schema);
        setOccurrence(anyPatternNode, min, max);
        Collection<Namespace> namespaces = anyPattern.getNamespaces();
        if (!namespaces.isEmpty()) {
        	String namespacesString = "";
        	Iterator<Namespace> it = namespaces.iterator();
            Namespace namespace = it.next();
        	if (namespaces.size()>1 || (namespaces.size()==1 && !namespace.equals(Namespace.ANY_NAMESPACE))) {
        		while (it.hasNext()) {
        			namespacesString=namespacesString+namespace.getUri()+" ";
        			namespace = it.next();
        		}
    			namespacesString=namespacesString+namespace.getUri();        		
        	}
        }
        if (anyPattern.getId() != null) {
            anyPatternNode.setAttribute("id", anyPattern.getId());
        }
        if (anyPattern.getProcessContentsInstruction() != null && anyPattern.getProcessContentsInstruction() != ProcessContentsInstruction.Strict) {
            anyPatternNode.setAttribute("processContents", anyPattern.getProcessContentsInstruction().toString().toLowerCase());
        }
        AnnotationWriter.writeAnnotation(anyPatternNode, anyPattern, schema);
        root.appendChild(anyPatternNode);
    }
}
