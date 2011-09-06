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
package eu.fox7.bonxai.bonxai.writer;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.LinkedList;

import eu.fox7.bonxai.bonxai.*;
import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.ElementRef;

/**
 * Writer for Bonxai schemas using classes inmplementing the Visitor interface
 * for the concrete traverseing of the schema.
 *
 * This class traverses the Bonxai data structure and calls the
 * appropriate methods from the visitor for each element found in
 * the data structure. For elements which aggregate other elements a
 * startElement and endElement method will be called in the visitor,
 * for all other elements just a traverseElement method will be called.
 *
 * For more details on the visitor pattern see:
 * http://en.wikipedia.org/wiki/Visitor_pattern
 */
public class Writer {

    /**
     * Write the schema using the given visitor.
     *
     * Converts the given Bonxai schema using the given visitor.
     */
    public String write(Bonxai schema, Visitor visitor)
    {
        traverseSchema(schema, visitor);
        return visitor.getResult();
    }

    /**
     * Visit schema object.
     */
    protected void traverseSchema(Bonxai schema, Visitor visitor) {
        visitor.startSchema(schema);
        traverseDeclaration(schema.getDeclaration(), visitor);
        traverseGroups(schema.getGroupList(), visitor);
        traverseGrammar(schema.getGrammarList(), visitor);
        traverseConstraints(schema.getConstraintList(), visitor);
        visitor.endSchema(schema);
    }

    /**
     * Visit declaration object.
     */
    protected void traverseDeclaration(Declaration declaration, Visitor visitor) {
        visitor.startDeclaration(declaration);
        traverseNamespaces(declaration.getNamespaceList(), visitor);
        traverseImports(declaration.getImportList(), visitor);
        traverseDataTypes(declaration.getDataTypeList(), visitor);
        visitor.endDeclaration(declaration);
    }

    /**
     * Visit namespaces object.
     */
    protected void traverseNamespaces(NamespaceList namespaces, Visitor visitor) {
        visitor.startNamespaces(namespaces);
        traverseDefaultNamespace(namespaces.getDefaultNamespace(), visitor);
        traverseIdentifiedNamespaces(namespaces.getIdentifiedNamespaces(), visitor);
        visitor.endNamespaces(namespaces);
    }

    /**
     * Visit default namespace object.
     */
    protected void traverseDefaultNamespace(DefaultNamespace namespace, Visitor visitor) {
        visitor.visitDefaultNamespace(namespace);
    }

    /**
     * Visit identified namespaces object.
     */
    protected void traverseIdentifiedNamespaces(HashSet<IdentifiedNamespace> namespaces, Visitor visitor) {
        TreeSet<IdentifiedNamespace> sortedNamespaceList = new TreeSet<IdentifiedNamespace>(namespaces);
        for (IdentifiedNamespace namespace: sortedNamespaceList) {
            visitor.visitIdentifiedNamespace(namespace);
        }
    }

    /**
     * Visit ImportList object.
     */
    protected void traverseImports(ImportList imports, Visitor visitor) {
        visitor.startImports(imports);
        traverseImportList(imports, visitor);
        visitor.endImports(imports);
    }

    /**
     * Visit Import objects.
     */
    protected void traverseImportList(ImportList importList, Visitor visitor) {
        for (String uri : importList.getUris()) {
            visitor.visitImport(importList.getImportByUri(uri));
        }
    }

    /**
     * Visit DataTypeList object.
     */
    protected void traverseDataTypes(DataTypeList dataTypes, Visitor visitor) {
        visitor.startDataTypes(dataTypes);
        traverseDataTypeList(dataTypes, visitor);
        visitor.endDataTypes(dataTypes);
    }

    /**
     * Visit DataType objects.
     */
    protected void traverseDataTypeList(DataTypeList dataTypeList, Visitor visitor) {
        for (String uri : dataTypeList.getUris()) {
            visitor.visitDataType(dataTypeList.getDataTypeByUri(uri));
        }
    }

    /**
     * Visit groups object.
     */
    protected void traverseGroups(GroupList groups, Visitor visitor) {
        visitor.startGroupList(groups);
        if (groups != null) {
            traverseGroupList(groups, visitor);
        }
        visitor.endGroupList(groups);
    }

    /**
     * Visit groups objects.
     */
    protected void traverseGroupList(GroupList groups, Visitor visitor) {
        for (GroupElement groupElement : groups.getGroupElements()) {
            if (groupElement instanceof ElementGroupElement) {
                traverseElementGroupElement((ElementGroupElement) groupElement, visitor);
            } else if (groupElement instanceof AttributeGroupElement) {
                traverseAttributeGroupElement((AttributeGroupElement) groupElement, visitor);
            }
        }
    }

    /**
     * Visit attribute group object.
     */
    protected void traverseAttributeGroupElement(AttributeGroupElement groupElement, Visitor visitor) {
        visitor.startAttributeGroup(groupElement);
        traverseAttributePattern(groupElement.getAttributePattern(), visitor);
        visitor.endAttributeGroup(groupElement);
    }

    /**
     * Visit element group object.
     */
    protected void traverseElementGroupElement(ElementGroupElement groupElement, Visitor visitor) {
        visitor.startElementGroup(groupElement);
        traverseParticleContainer(groupElement.getParticleContainer(), visitor, false);
        visitor.endElementGroup(groupElement);
    }

    /**
     * Visit attribute pattern object.
     */
    protected void traverseAttributePattern(AttributePattern attributePattern, Visitor visitor) {
        if (attributePattern.getAnyAttribute() != null) {
            if (!attributePattern.getAttributeList().isEmpty()) {
                traverseAnyAttribute(attributePattern.getAnyAttribute(), visitor, true);
            }else{
                traverseAnyAttribute(attributePattern.getAnyAttribute(), visitor, false);
            }

        }
        if (attributePattern.getAttributeList() != null) {
            traverseAttributeList(attributePattern.getAttributeList(), visitor);
        }
    }

    /**
     * Visit any attribute object.
     */
    protected void traverseAnyAttribute(AnyAttribute anyAttribute, Visitor visitor, boolean hasMore) {
        visitor.visitAnyAttribute(anyAttribute, hasMore);
    }

    /**
     * Visit attribute list object.
     */
    protected void traverseAttributeList(Vector<AttributeListElement> attributeList, Visitor visitor) {
        Iterator<AttributeListElement> it = attributeList.iterator();
        while (it.hasNext()) {
            AttributeListElement attributeElement = it.next();

            if (attributeElement instanceof Attribute) {
                visitor.visitAttribute(
                    (Attribute) attributeElement,
                    it.hasNext()
                );
            } else if (attributeElement instanceof AttributeGroupReference) {
                visitor.visitAttributeGroupReference(
                    (AttributeGroupReference) attributeElement,
                    it.hasNext()
                );
            }
        }
    }

    /**
     * Visit Particle objects.
     */
    protected void traverseParticle(Particle particle, Visitor visitor, boolean hasMore) {
        if (particle instanceof ParticleContainer) {
            traverseParticleContainer((ParticleContainer) particle, visitor, hasMore);
        } else if (particle instanceof AnyPattern) {
            visitor.visitAnyPattern((AnyPattern) particle, hasMore);
        } else if (particle instanceof eu.fox7.bonxai.bonxai.Element) {
            visitor.visitElement((eu.fox7.bonxai.bonxai.Element) particle, hasMore);
        } else if (particle instanceof ElementRef) {
            throw new RuntimeException("ElementRef is not supported by Bonxai. Data structure b0rked.");
        } else if (particle instanceof GroupRef) {
            visitor.visitGroupRef((GroupRef) particle, hasMore);
        } else if (particle instanceof EmptyPattern) {
        	// nothing todo here
        } else {
            throw new RuntimeException("Cannot write Particle of class " + particle.getClass());
        }
    }

    /**
     * Dispatch traversing of ParticleContainer objects.
     */
    protected void traverseParticleContainer(ParticleContainer particleContainer, Visitor visitor, boolean hasMore) {
        if (particleContainer instanceof AllPattern) {
            traverseAllPattern((AllPattern) particleContainer, visitor, hasMore);
        } else if (particleContainer instanceof ChoicePattern) {
            traverseChoicePattern((ChoicePattern) particleContainer, visitor, hasMore);
        } else if (particleContainer instanceof CountingPattern) {
            traverseCountingPattern((CountingPattern) particleContainer, visitor, hasMore);
        } else if (particleContainer instanceof SequencePattern) {
            traverseSequencePattern((SequencePattern) particleContainer, visitor, hasMore);
        } else {
            throw new RuntimeException("Cannot write ParticleContainer of class " + particleContainer.getClass());
        }
    }

    /**
     * Visit LinkedList<Particle> object.
     */
    protected void traverseParticleList(LinkedList<Particle> particleList, Visitor visitor) {
        Iterator<Particle> it = particleList.iterator();
        while (it.hasNext()) {
            Particle currentParticle = it.next();
            traverseParticle(currentParticle, visitor, it.hasNext());
        }
    }

    /**
     * Visit AllPattern object.
     */
    protected void traverseAllPattern(AllPattern allPattern, Visitor visitor, boolean hasMore) {
        visitor.startAllPattern(allPattern);
        traverseParticleList(allPattern.getParticles(), visitor);
        visitor.endAllPattern(allPattern, hasMore);
    }

    /**
     * Visit ChoicePattern object.
     */
    protected void traverseChoicePattern(ChoicePattern choicePattern, Visitor visitor, boolean hasMore) {
        visitor.startChoicePattern(choicePattern);
        traverseParticleList(choicePattern.getParticles(), visitor);
        visitor.endChoicePattern(choicePattern, hasMore);
    }

    /**
     * Visit CountingPattern object.
     */
    protected void traverseCountingPattern(CountingPattern countingPattern, Visitor visitor, boolean hasMore) {
        visitor.startCountingPattern(countingPattern);
        traverseParticleList(countingPattern.getParticles(), visitor);
        visitor.endCountingPattern(countingPattern, hasMore);
    }

    /**
     * Visit SequencePattern object.
     */
    protected void traverseSequencePattern(SequencePattern sequencePattern, Visitor visitor, boolean hasMore) {
        visitor.startSequencePattern(sequencePattern);
        traverseParticleList(sequencePattern.getParticles(), visitor);
        visitor.endSequencePattern(sequencePattern, hasMore);
    }

    /**
     * Visit GrammarList object.
     */
    protected void traverseGrammar(GrammarList grammar, Visitor visitor) {
        visitor.startGrammarList(grammar);
        if (grammar != null) {
            // @TODO:
            // tarverseAnnotations(grammar.getAnnotations());
            traverseRootElementList(grammar.getRootElementNames(), visitor);
            traverseExpressionList(grammar.getExpressions(), visitor);
        }
        visitor.endGrammarList(grammar);
    }

    /**
     * Visitor Vector<Expression> object.
     */
    protected void traverseExpressionList(Vector<Expression> expressionList, Visitor visitor) {
        for(Expression expression : expressionList) {
            visitor.startExpression(expression);
            traverseExpression(expression, visitor);
            visitor.endExpression(expression);
        }
    }
    
    protected void traverseRootElementList(Vector<String> rootElementList, Visitor visitor) {
    	visitor.startRootElementList();
    	for(int i=0; i<rootElementList.size(); ++i) {
    		visitor.visitRootElement(rootElementList.get(i), i<(rootElementList.size()-1));
    	}
    	visitor.endRootElementList();
    }

    /**
     * Visit Expression object.
     */
    protected void traverseExpression(Expression expression, Visitor visitor) {
        traverseAncestorPattern(expression.getAncestorPattern(), visitor, false);
        traverseChildPattern(expression.getChildPattern(), visitor);
    }

    /**
     * Visit ChildPattern object.
     */
    protected void traverseAncestorPattern(AncestorPattern aPattern, Visitor visitor, boolean constraint) {
        if (aPattern == null) {
            throw new RuntimeException("Missing ancestor pattern.");
        }
        visitor.startAncestorPattern(aPattern);
        traverseAncestorPatternParticle(aPattern.getParticle(), visitor, false);
        if(!constraint) visitor.endAncestorPattern(aPattern);
    }

    /**
     * Visit AncestorPatternParticle object.
     */
    protected void traverseAncestorPatternParticle(AncestorPatternParticle aParticle, Visitor visitor, boolean hasMore) {
        if (aParticle instanceof AncestorPatternElement) {
            traverseAncestorPatternElement((AncestorPatternElement) aParticle, visitor, hasMore);
        } else if (aParticle instanceof CardinalityParticle) {
            traverseCardinalityParticle((CardinalityParticle) aParticle, visitor, hasMore);
        } else if (aParticle instanceof ContainerParticle) {
            traverseContainerParticle((ContainerParticle) aParticle, visitor, hasMore);
        } else {
            throw new RuntimeException("Unknown AncestorPatternParticle " + aParticle.getClass());
        }
    }

    /**
     * Visitor ContainerParticle object.
     */
    protected void traverseContainerParticle(ContainerParticle cParticle, Visitor visitor, boolean hasMore) {
        if (cParticle instanceof OrExpression) {
            traverseOrExpression((OrExpression) cParticle, visitor, hasMore);
        } else if (cParticle instanceof SequenceExpression) {
            traverseSequenceExpression((SequenceExpression) cParticle, visitor, hasMore);
        } else {
            throw new RuntimeException("Unknown ContainerParticle " + cParticle.getClass());
        }
    }

    /**
     * Visit AncestorPatternElement object.
     */
    protected void traverseAncestorPatternElement(AncestorPatternElement aElement, Visitor visitor, boolean hasMore) {
        if (aElement instanceof SingleSlashPrefixElement) {
            traverseSingleSlashPrefixElement((SingleSlashPrefixElement) aElement, visitor, hasMore);
        } else if (aElement instanceof DoubleSlashPrefixElement) {
            traverseDoubleSlashPrefixElement((DoubleSlashPrefixElement) aElement, visitor, hasMore);
        } else {
            throw new RuntimeException("Unknown AncestorPatternElement " + aElement.getClass());
        }
    }

    /**
     * Visit SingleSlashPrefixElement object.
     */
    protected void traverseSingleSlashPrefixElement(SingleSlashPrefixElement aElement, Visitor visitor, boolean hasMore) {
        visitor.visitSingleSlashPrefixElement(aElement, hasMore);
    }

    /**
     * Visit DoubleSlashPrefixElement object.
     */
    protected void traverseDoubleSlashPrefixElement(DoubleSlashPrefixElement aElement, Visitor visitor, boolean hasMore) {
        visitor.visitDoubleSlashPrefixElement(aElement, hasMore);
    }

    /**
     * Visit CardinalityParticle object.
     */
    protected void traverseCardinalityParticle(CardinalityParticle cardinalityParticle, Visitor visitor, boolean hasMore) {
        visitor.startCardinalityParticle(cardinalityParticle);
        traverseAncestorPatternParticle(cardinalityParticle.getChild(), visitor, false);
        visitor.endCardinalityParticle(cardinalityParticle, hasMore);
    }

    /**
     * Visit OrExpression object.
     */
    protected void traverseOrExpression(OrExpression orExpression, Visitor visitor, boolean hasMore) {
        visitor.startOrExpression(orExpression);
        Iterator<AncestorPatternParticle> it = orExpression.getChildren().iterator();
        while (it.hasNext()) {
            AncestorPatternParticle particle = it.next();
            traverseAncestorPatternParticle(particle, visitor, it.hasNext());
        }
        visitor.endOrExpression(orExpression, hasMore);
    }

    /**
     * Visit SequenceExpression object.
     */
    protected void traverseSequenceExpression(SequenceExpression sequenceExpression, Visitor visitor, boolean hasMore) {
        visitor.startSequenceExpression(sequenceExpression);
        Iterator<AncestorPatternParticle> it = sequenceExpression.getChildren().iterator();
        while (it.hasNext()) {
            AncestorPatternParticle particle = it.next();
            traverseAncestorPatternParticle(particle, visitor, it.hasNext());
        }
        visitor.endSequenceExpression(sequenceExpression, hasMore);
    }

    /**
     * Visit ChildPattern object.
     */
    protected void traverseChildPattern(ChildPattern cPattern, Visitor visitor) {
        if (cPattern == null) {
            throw new RuntimeException("Missing ChildPattern.");
        }

        visitor.startChildPattern(cPattern);
        traverseAttributePattern(cPattern.getAttributePattern(), visitor);
        traverseElementPattern(cPattern.getElementPattern(), visitor);
        visitor.endChildPattern(cPattern);
    }

    /**
     * Visit ElementPattern object.
     */
    protected void traverseElementPattern(ElementPattern ePattern, Visitor visitor) {
        visitor.startElementPattern(ePattern);
        if (ePattern != null && ePattern.getRegexp() != null) {
            traverseParticle(ePattern.getRegexp(), visitor, false);
        } else {
            visitor.visitElementPattern(ePattern);
        }
        visitor.endElementPattern(ePattern);
    }

    /**
     * Visit ConstraintList object.
     */
    protected void traverseConstraints(ConstraintList constraints, Visitor visitor) {
        if (constraints!=null && !constraints.getConstraints().isEmpty()) {
            visitor.startConstraintList(constraints);
            traverseConstraintList(constraints.getConstraints(), visitor);
            visitor.endConstraintList(constraints);
        }
        
    }

    /**
     * Dispatch visiting of Constraint.
     */
    protected void traverseConstraintList(Vector<Constraint> constraintList, Visitor visitor) {
        for (Constraint constraint : constraintList) {
            if (constraint instanceof UniqueConstraint) {
                traverseUniqueConstraint((UniqueConstraint) constraint, visitor);
            } else if (constraint instanceof KeyConstraint) {
                traverseKeyConstraint((KeyConstraint) constraint, visitor);
            } else if (constraint instanceof KeyRefConstraint) {
                traverseKeyRefConstraint((KeyRefConstraint) constraint, visitor);
            } else {
                throw new RuntimeException("Unknown Constraint " + constraint.getClass());
            }
        }
    }

    /**
     * Visit UniqueConstraint object.
     */
    protected void traverseUniqueConstraint(UniqueConstraint unique, Visitor visitor) {
        visitor.startUniqueConstraint(unique);
        traverseConstraint(unique, visitor);
        visitor.endUniqueConstraint(unique);
    }

    /**
     * Visitor KeyConstraint object.
     */
    protected void traverseKeyConstraint(KeyConstraint key, Visitor visitor) {
        visitor.startKeyConstraint(key);
        traverseConstraint(key, visitor);
        visitor.endKeyConstraint(key);
    }

    /**
     * Visit KeyRefConstraint object.
     */
    protected void traverseKeyRefConstraint(KeyRefConstraint keyRef, Visitor visitor) {
        visitor.startKeyRefConstraint(keyRef);
        traverseConstraint(keyRef, visitor);
        visitor.endKeyRefConstraint(keyRef);
    }

    /**
     * Visit Constraint object.
     */
    protected void traverseConstraint(Constraint constraint, Visitor visitor) {
        traverseAncestorPattern(constraint.getAncestorPattern(), visitor, true);

        visitor.startConstraint(constraint);
        visitor.visitConstraintSelector(constraint.getConstraintSelector());

        TreeSet<String> sortedFieldList = new TreeSet<String>(constraint.getConstraintFields());
        Iterator<String> it = sortedFieldList.iterator();

        visitor.startConstraintFieldList(constraint);
        while (it.hasNext()) {
            visitor.visitConstraintField(it.next(), it.hasNext());
        }
        visitor.endConstraintFieldList(constraint);
        visitor.endConstraint(constraint);
    }
}

