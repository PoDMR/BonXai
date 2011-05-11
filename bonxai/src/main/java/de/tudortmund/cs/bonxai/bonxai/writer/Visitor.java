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
package de.tudortmund.cs.bonxai.bonxai.writer;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;

/**
 * Visitor interface for visiting the Bonxai data structure.
 *
 * Implements methods for all elements in the Bonxai data structure
 * which are visited when traversing the whole data structure.
 * Implement this class to implement a concrete visitor.
 */
interface Visitor {

    public String getResult();

    /**
     * Visit the schema root node object.
     */
    public void startSchema(Bonxai schema);

    /**
     * Visit the schema root node object.
     */
    public void endSchema(Bonxai schema);

    /**
     * Visit the declaration list object.
     */
    public void startDeclaration(Declaration declaration);

    /**
     * Visit the declaration list object.
     */
    public void endDeclaration(Declaration declaration);

    /**
     * Visit the namespace list object.
     */
    public void startNamespaces(NamespaceList namespaces);

    /**
     * Visit the namespace list object.
     */
    public void endNamespaces(NamespaceList namespaces);

    /**
     * Visit the default namespace object.
     */
    public void visitDefaultNamespace(DefaultNamespace namespace);

    /**
     * Visit the identified namespace object.
     */
    public void visitIdentifiedNamespace(IdentifiedNamespace namespace);

    /**
     * Visit the import list object.
     */
    public void startImports(ImportList imports);

    /**
     * Visit the import list object.
     */
    public void endImports(ImportList imports);

    /**
     * Visit the import list object.
     */
    public void visitImport(Import imporT);

    /**
     * Visit the import list object.
     */
    public void startDataTypes(DataTypeList dataTypes);

    /**
     * Visit the import list object.
     */
    public void endDataTypes(DataTypeList dataTypes);

    /**
     * Visit the import list object.
     */
    public void visitDataType(DataType dataType);

    /**
     * Visit the group list object.
     */
    public void startGroupList(GroupList groups);

    /**
     * Visit the group list object.
     */
    public void endGroupList(GroupList groups);

    /**
     * Visit AttributeGroupElement object.
     */
    public void startAttributeGroup(AttributeGroupElement attributeGroup);

    /**
     * Visit ElementGroupElement object.
     */
    public void endAttributeGroup(AttributeGroupElement attributeGroup);

    /**
     * Visit ElementGroupElement object.
     */
    public void startElementGroup(ElementGroupElement elementGroup);

    /**
     * Visit ElementGroupElement object.
     */
    public void endElementGroup(ElementGroupElement elementGroup);

    /**
     * Visit AnyAttribute object.
     */
    public void visitAnyAttribute(AnyAttribute anyAttribute, boolean hasMore);

    /**
     * Visit Attribute object.
     */
    public void visitAttribute(Attribute attribute, boolean hasMore);

    /**
     * Visit AttributeGroupReference object.
     */
    public void visitAttributeGroupReference(AttributeGroupReference attributeGroupRef, boolean hasMore);

    /**
     * Visit AllPattern object.
     */
    public void startAllPattern(AllPattern pattern);

    /**
     * Visit AllPattern object.
     */
    public void endAllPattern(AllPattern pattern, boolean hasMore);

    /**
     * Visit ChoicePattern object.
     */
    public void startChoicePattern(ChoicePattern pattern);

    /**
     * Visit ChoicePattern object.
     */
    public void endChoicePattern(ChoicePattern pattern, boolean hasMore);

    /**
     * Visit CountingPattern object.
     */
    public void startCountingPattern(CountingPattern pattern);

    /**
     * Visit CountingPattern object.
     */
    public void endCountingPattern(CountingPattern pattern, boolean hasMore);

    /**
     * Visit SequencePattern object.
     */
    public void startSequencePattern(SequencePattern pattern);

    /**
     * Visit SequencePattern object.
     */
    public void endSequencePattern(SequencePattern pattern, boolean hasMore);

    /**
     * Visit AnyPattern object.
     */
    public void visitAnyPattern(AnyPattern particle, boolean hasMore);

    /**
     * Visit Element object.
     */
    public void visitElement(de.tudortmund.cs.bonxai.bonxai.Element particle, boolean hasMore);

    /**
     * Visit GroupRef object.
     */
    public void visitGroupRef(GroupRef particle, boolean hasMore);

    /**
     * Visit the grammar list object.
     */
    public void startGrammarList(GrammarList grammar);

    /**
     * Visit the grammar list object.
     */
    public void endGrammarList(GrammarList grammar);

    /**
     * Visit Expression object.
     */
    public void startExpression(Expression expression);

    /**
     * Visit Expression object.
     */
    public void endExpression(Expression expression);

    /**
     * Visit AncestorPattern object.
     */
    public void startAncestorPattern(AncestorPattern aPattern);

    /**
     * Visit AncestorPattern object.
     */
    public void endAncestorPattern(AncestorPattern aPattern);

    /**
     * Visit SingleSlashPrefixElement object.
     */
    public void visitSingleSlashPrefixElement(SingleSlashPrefixElement element, boolean hasMore);

    /**
     * Visit SingleSlashPrefixElement object.
     */
    public void visitDoubleSlashPrefixElement(DoubleSlashPrefixElement element, boolean hasMore);

    /**
     * Visit CardinalityParticle object.
     */
    public void startCardinalityParticle(CardinalityParticle particle);

    /**
     * Visit CardinalityParticle object.
     */
    public void endCardinalityParticle(CardinalityParticle particle, boolean hasMore);

    /**
     * Visit OrExpression object.
     */
    public void startOrExpression(OrExpression particle);

    /**
     * Visit OrExpression object.
     */
    public void endOrExpression(OrExpression particle, boolean hasMore);

    /**
     * Visit SequencePattern object.
     */
    public void startSequenceExpression(SequenceExpression particle);

    /**
     * Visit SequencePattern object.
     */
    public void endSequenceExpression(SequenceExpression particle, boolean hasMore);

    /**
     * Visit ChildPattern object.
     */
    public void startChildPattern(ChildPattern cPattern);

    /**
     * Visit ChildPattern object.
     */
    public void endChildPattern(ChildPattern cPattern);

    /**
     * Visit ElementPattern object.
     */
    public void startElementPattern(ElementPattern ePattern);

    /**
     * Visit ElementPattern object.
     */
    public void endElementPattern(ElementPattern ePattern);

    /**
     * Visit ElementPattern object.
     */
    public void visitElementPattern(ElementPattern ePattern);

    /**
     * Visit the constraints lists object.
     */
    public void startConstraintList(ConstraintList constraints);

    /**
     * Visit the constraints list object.
     */
    public void endConstraintList(ConstraintList constraints);

    /**
     * Visit UniqueConstraint object.
     */
    public void startUniqueConstraint(UniqueConstraint unique);

    /**
     * Visit UniqueConstraint object.
     */
    public void endUniqueConstraint(UniqueConstraint unique);

    /**
     * Visit KeyConstraint object.
     */
    public void startKeyConstraint(KeyConstraint key);

    /**
     * Visit KeyConstraint object.
     */
    public void endKeyConstraint(KeyConstraint key);

    /**
     * Visit KeyRefConstraint object.
     */
    public void startKeyRefConstraint(KeyRefConstraint keyRef);

    /**
     * Visit KeyRefConstraint object.
     */
    public void endKeyRefConstraint(KeyRefConstraint keyRef);

    /**
     * Visit Constraint object.
     */
    public void startConstraint(Constraint constraint);

    /**
     * Visit Constraint object.
     */
    public void endConstraint(Constraint constraint);

    /**
     * Visit ConstraintFieldList object.
     */
    public void startConstraintFieldList(Constraint constraint);

    /**
     * Visit ConstraintFieldList object.
     */
    public void endConstraintFieldList(Constraint constraint);

    /**
     * Visit constraint selector.
     */
    public void visitConstraintSelector(String selector);

    /**
     * Visit constraint field.
     */
    public void visitConstraintField(String field, boolean hasMore);
    
    public void startRootElementList();

    public void endRootElementList();

    public void visitRootElement(String rootElement, boolean hasMore);
}

