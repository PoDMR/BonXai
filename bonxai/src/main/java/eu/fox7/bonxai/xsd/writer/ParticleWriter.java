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
package eu.fox7.bonxai.xsd.writer;

import java.util.LinkedList;
import org.w3c.dom.Node;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;

public abstract class ParticleWriter {

    /**
     * Writes the particle node.
     */
    protected static void writeParticle(Node root, Particle particle, FoundElements foundElements) {
        writeParticle(root, particle, foundElements, 1, 1);
    }

    /**
     * Writes the particle node with given min and max occurs.
     */
    protected static void writeParticle(Node root, Particle particle, FoundElements foundElements, Integer min, Integer max) {
        if (particle instanceof ElementRef) {
            writeElementRef(root, (ElementRef) particle, foundElements, min, max);
        } else if (particle instanceof AnyPattern) {
            writeAnyPattern(root, (AnyPattern) particle, foundElements, min, max);
        } else if (particle instanceof eu.fox7.bonxai.xsd.Element) {
            writeElement(root, (eu.fox7.bonxai.xsd.Element) particle, foundElements, min, max);
        } else if (particle instanceof GroupRef) {
            writeGroupRef(root, (GroupRef) particle, foundElements, min, max);
        } else if (particle instanceof ParticleContainer) {
            writeParticleContainer(root, (ParticleContainer) particle, foundElements, min,
                    max);
        }
    }

    /**
     * Writes the element reference node.
     */
    protected static void writeElementRef(Node root, ElementRef elementRef, FoundElements foundElements, Integer min, Integer max) {
        org.w3c.dom.Element elementRefNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "element");
        foundElements.setXSDPrefix(elementRefNode);
        setOccurrence(elementRefNode, min, max);
        elementRefNode.setAttribute("ref", foundElements.getPrefix(elementRef.getElement().getNamespace()) + elementRef.getElement().getLocalName());
        AnnotationWriter.writeAnnotation(elementRefNode, elementRef, foundElements);
        if (elementRef.getId() != null) {
            elementRefNode.setAttribute("id", elementRef.getId());
        }
        root.appendChild(elementRefNode);
    }

    /**
     * Writes the element node.
     */
    protected static void writeElement(Node root, eu.fox7.bonxai.xsd.Element element, FoundElements foundElements) {
        writeElement(root, element, foundElements, 1, 1);
    }

    /**
     * Writes the element node with given min and max occurs.
     * @param root
     * @param element
     * @param foundElements
     * @param min
     * @param max
     */
    protected static void writeElement(Node root, eu.fox7.bonxai.xsd.Element element, FoundElements foundElements, Integer min, Integer max) {
        org.w3c.dom.Element elementNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "element");
        foundElements.setXSDPrefix(elementNode);
        elementNode.setAttribute("name", element.getLocalName());
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
            for (eu.fox7.bonxai.xsd.Element.Final currentFinalValue : element.getFinalModifiers()) {
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
            for (eu.fox7.bonxai.xsd.Element.Block currentBlockValue : element.getBlockModifiers()) {
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
        AnnotationWriter.writeAnnotation(elementNode, element, foundElements);
        if (element.getId() != null) {
            elementNode.setAttribute("id", element.getId());
        }
        root.appendChild(elementNode);
        if (element.getType() != null && !element.getType().getLocalName().equals("anyType")) {
            if (element.getType() instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) element.getType();
                // Changed to write types when needed
                if (element.getTypeAttr()) {
                    elementNode.setAttribute("type", foundElements.getPrefix(simpleType.getNamespace()) + simpleType.getLocalName());
                } else {
                    //System.out.println(element.getLocalName() +": "+ element.getType());
                    TypeWriter.writeType(elementNode, element.getType(), foundElements, false);
                }
            } else {
                // Changed to write types when needed
                if (element.getTypeAttr()) {
                    elementNode.setAttribute("type", foundElements.getPrefix(element.getType().getNamespace()) + element.getType().getLocalName());
                } else {
                    TypeWriter.writeType(elementNode, element.getType(), foundElements, false);
                }
            }
            for (Constraint constraint : element.getConstraints()) {
                ConstraintWriter.writeConstraint(elementNode, constraint, foundElements);
            }
            if (element.getSubstitutionGroup() != null) {
                elementNode.setAttribute("substitutionGroup", foundElements.getPrefix(element.getSubstitutionGroup().getReference().getNamespace()) + element.getSubstitutionGroup().getReference().getLocalName());
            }
        } else {
            if (element.getSubstitutionGroup() != null) {
                elementNode.setAttribute("substitutionGroup", foundElements.getPrefix(element.getSubstitutionGroup().getReference().getNamespace()) + element.getSubstitutionGroup().getReference().getLocalName());
            }
        }
    }

    /**
     * Writes the group reference node.
     * @param root
     * @param groupRef
     * @param foundElements
     * @param min
     * @param max
     */
    protected static void writeGroupRef(Node root, GroupRef groupRef, FoundElements foundElements, Integer min, Integer max) {
        org.w3c.dom.Element groupNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "group");
        foundElements.setXSDPrefix(groupNode);
        eu.fox7.bonxai.xsd.Group group = (eu.fox7.bonxai.xsd.Group) groupRef.getGroup();
        groupNode.setAttribute("ref", foundElements.getPrefix(group.getNamespace()) + group.getLocalName());
        setOccurrence(groupNode, min, max);
        AnnotationWriter.writeAnnotation(groupNode, groupRef, foundElements);
        if (groupRef.getId() != null) {
            groupNode.setAttribute("id", groupRef.getId());
        }
        root.appendChild(groupNode);
    }

    /**
     * Writes the particle container node.
     */
    protected static void writeParticleContainer(Node root, ParticleContainer particleContainer, FoundElements foundElements) {
        writeParticleContainer(root, particleContainer, foundElements, 1, 1);
    }

    /**
     * Writes the particle container node with given min and max occurs.
     */
    protected static void writeParticleContainer(Node root, ParticleContainer particleContainer, FoundElements foundElements, Integer min, Integer max) {
        if (particleContainer instanceof CountingPattern) {
            writeCountingPattern(root, (CountingPattern) particleContainer, foundElements);
        } else {
            String containerName = "";
            if (particleContainer instanceof SequencePattern) {
                containerName = "sequence";
            } else if (particleContainer instanceof ChoicePattern) {
                containerName = "choice";
            } else if (particleContainer instanceof AllPattern) {
                containerName = "all";
            }
            org.w3c.dom.Element containerNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", containerName);
            foundElements.setXSDPrefix(containerNode);
            setOccurrence(containerNode, min, max);
            AnnotationWriter.writeAnnotation(containerNode, particleContainer, foundElements);
            if (particleContainer.getId() != null) {
                containerNode.setAttribute("id", particleContainer.getId());
            }
            root.appendChild(containerNode);
            LinkedList<Particle> particles = particleContainer.getParticles();
            while (!particles.isEmpty()) {
                writeParticle(containerNode, particles.pollFirst(), foundElements, 1, 1);
            }
        }
    }

    /**
     * Writes the counting pattern node.
     */
    protected static void writeCountingPattern(Node root, CountingPattern countingPattern, FoundElements foundElements) {
        LinkedList<Particle> particles = countingPattern.getParticles();
        while (!particles.isEmpty()) {
            writeParticle(root, particles.pollFirst(), foundElements, countingPattern.getMin(), countingPattern.getMax());
        }
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
    protected static void writeAnyPattern(Node root, AnyPattern anyPattern, FoundElements foundElements, Integer min, Integer max) {
        org.w3c.dom.Element anyPatternNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "any");
        foundElements.setXSDPrefix(anyPatternNode);
        setOccurrence(anyPatternNode, min, max);
        if (anyPattern.getNamespace() != null && !anyPattern.getNamespace().equals("##any")) {
            anyPatternNode.setAttribute("namespace", anyPattern.getNamespace());
        }
        if (anyPattern.getId() != null) {
            anyPatternNode.setAttribute("id", anyPattern.getId());
        }
        if (anyPattern.getProcessContentsInstruction() != null && anyPattern.getProcessContentsInstruction() != ProcessContentsInstruction.Strict) {
            anyPatternNode.setAttribute("processContents", anyPattern.getProcessContentsInstruction().toString().toLowerCase());
        }
        AnnotationWriter.writeAnnotation(anyPatternNode, anyPattern, foundElements);
        root.appendChild(anyPatternNode);
    }
}
