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

import java.util.Stack;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;

/**
 * Compact syntax visitor.
 *
 * Visits the Bonxai data structure into the compact syntax defined as
 * the default serialization format for Bonxai grammars.
 */
public class CompactSyntaxVisitor implements Visitor {

    /**
     * Namespace list for lookups.
     */
    private NamespaceList namespaces;

    /**
     * String generated by the visitor.
     */
    private String result = "";

    /**
     * If the visitor is currently in a group or attribute-group declaration.
     */
    private boolean inGroup;

    /**
     * Current level of indentation.
     */
    private int indentLevel;

    /**
     * String used per indentation level.
     */
    private String indentString = "    ";

    /**
     * Keeps track on the current particle container.
     */
    private Stack<ParticleContainer> particleContainerStack = new Stack<ParticleContainer>();

    /**
     * Keeps track on the current particle container.
     */
    private Stack<ContainerParticle> ancestorParticleStack = new Stack<ContainerParticle>();

    /**
     * Creates a new visitor.
     */
    public CompactSyntaxVisitor() {
        this.namespaces = new NamespaceList(new DefaultNamespace(""));
    }

    /**
     * Returns the string generated by the visitor.
     */
    public String getResult() {
        return result;
    }

    /**
     * Visit the schema root node object.
     */
    public void startSchema(Bonxai schema) {
    }

    /**
     * Visit the schema root node object.
     */
    public void endSchema(Bonxai schema) {
    }

    /**
     * Visit the declaration list object.
     */
    public void startDeclaration(Declaration declaration) {
    }

    /**
     * Visit the declaration list object.
     */
    public void endDeclaration(Declaration declaration) {
        appendLine();
    }

    /**
     * Visit the namespace list object.
     */
    public void startNamespaces(NamespaceList namespaces) {
        this.namespaces = namespaces;
    }

    /**
     * Visit the namespace list object.
     */
    public void endNamespaces(NamespaceList namespaces) {
    }

    /**
     * Visit the default namespace object.
     */
    public void visitDefaultNamespace(DefaultNamespace namespace) {
        appendLine("default namespace " +  namespace.getUri());
    }

    /**
     * Visit the identified namespace object.
     */
    public void visitIdentifiedNamespace(IdentifiedNamespace namespace) {
        appendLine("namespace " + namespace.getIdentifier() + " = " + namespace.getUri());
    }

    /**
     * Visit the import list object.
     */
    public void startImports(ImportList imports) {
    }

    /**
     * Visit the import list object.
     */
    public void endImports(ImportList imports) {
    }

    /**
     * Visit the import list object.
     */
    public void visitImport(Import imporT) {
        String line = "import " + imporT.getUri();
        if (imporT.getIdentifier() != "") {
            line += " = " + imporT.getIdentifier();
        }
        appendLine(line);
    }

    /**
     * Visit the import list object.
     */
    public void startDataTypes(DataTypeList dataTypes) {
    }

    /**
     * Visit the import list object.
     */
    public void endDataTypes(DataTypeList dataTypes) {
    }

    /**
     * Visit the import list object.
     */
    public void visitDataType(DataType dataType) {
        appendLine("dataTypes " + dataType.getIdentifier() + " = " + dataType.getUri());
    }

    /**
     * Visit the group list object.
     */
    public void startGroupList(GroupList groups) {
        inGroup = true;
        appendLine("groups {");
        pushIndent();
    }

    /**
     * Visit the group list object.
     */
    public void endGroupList(GroupList groups) {
        inGroup = false;
        popIndent();
        appendLine("}");
        appendLine();
    }

    /**
     * Visit AttributeGroupElement object.
     */
    public void startAttributeGroup(AttributeGroupElement attributeGroup) {
        inGroup = true;
        appendLine("attribute-group " + checkForKeywords(attributeGroup.getName()) + " = {");
        pushIndent();
    }

    /**
     * Visit ElementGroupElement object.
     */
    public void endAttributeGroup(AttributeGroupElement attributeGroup) {
        inGroup = false;
        popIndent();
        appendLine("}");
    }

    /**
     * Visit ElementGroupElement object.
     */
    public void startElementGroup(ElementGroupElement elementGroup) {
        appendLine("group " + checkForKeywords(elementGroup.getName()) + " = {");
        pushIndent();
    }

    /**
     * Visit ElementGroupElement object.
     */
    public void endElementGroup(ElementGroupElement elementGroup) {
        popIndent();
        appendLine("}");
    }

    /**
     * Visit AnyAttribute object.
     *
     * @TODO No hasMore? *wonder*
     */
    public void visitAnyAttribute(AnyAttribute anyAttribute, boolean hasMore) {
        if (hasMore) {
            appendLine(getForeignModifier(anyAttribute.getProcessContentsInstruction()) + " attribute * { " + anyAttribute.getNamespace() + " },");
        }else{
            appendLine(getForeignModifier(anyAttribute.getProcessContentsInstruction()) + " attribute * { " + anyAttribute.getNamespace() + " }");
        }

    }

    /**
     * Visit Attribute object.
     */
    public void visitAttribute(Attribute attribute, boolean hasMore) {
        String line = "attribute ";

        if (!attribute.getNamespace().equals(this.namespaces.getDefaultNamespace().getUri())) {
            line += this.namespaces.getNamespaceByUri(attribute.getNamespace()).getIdentifier()
                + ":";
        }

        line += checkForKeywords(attribute.getName());

        if (attribute.getType() != null) {
            BonxaiType type = attribute.getType();

            line += " { type ";

            // @TODO: Refactor this mechanism into method. Used several times.
            if (!type.getNamespace().equals(this.namespaces.getDefaultNamespace().getUri())) {
                line += this.namespaces.getNamespaceByUri(type.getNamespace()).getIdentifier()
                     + ":";
            }

            line += attribute.getType().getType();

            // @TODO: Fix quoting!
            if (attribute.getFixed() != null) {
                line += " fixed " + attribute.getFixed();
            } else if (attribute.getDefault() != null) {
                line += " default " +  quoteString(attribute.getDefault());
            }

            line += " }";
        }
        if (!attribute.isRequired()) {
            line += "?";
        }

        if (hasMore || !inGroup) {
            line += ",";
        }
        appendLine(line);
    }

    /**
     * Visit AttributeGroupReference object.
     */
    public void visitAttributeGroupReference(AttributeGroupReference attributeGroupRef, boolean hasMore) {
        String line = "attribute-group " + checkForKeywords(attributeGroupRef.getName());
        if (hasMore || !inGroup) {
            line += ",";
        }
        appendLine(line);
    }

    /**
     * Visit AllPattern object.
     */
    public void startAllPattern(AllPattern pattern) {
        pushParticleContainer(pattern);
        appendLine("(");
        pushIndent();
    }

    /**
     * Visit AllPattern object.
     */
    public void endAllPattern(AllPattern pattern, boolean hasMore) {
        popParticleContainer(pattern);
        popIndent();

        String line = ")";
        if (hasMore) {
            line += " " + getParticleOperator();
        }
        appendLine(line);
    }

    /**
     * Visit ChoicePattern object.
     */
    public void startChoicePattern(ChoicePattern pattern) {
        pushParticleContainer(pattern);
        appendLine("(");
        pushIndent();
    }

    /**
     * Visit ChoicePattern object.
     */
    public void endChoicePattern(ChoicePattern pattern, boolean hasMore) {
        popParticleContainer(pattern);
        popIndent();

        String line = ")";
        if (hasMore) {
            line += " " + getParticleOperator();
        }
        appendLine(line);
    }

    /**
     * Visit CountingPattern object.
     */
    public void startCountingPattern(CountingPattern pattern) {
        pushParticleContainer(pattern);
        appendLine("(");
        pushIndent();
    }

    /**
     * Visit CountingPattern object.
     */
    public void endCountingPattern(CountingPattern pattern, boolean hasMore) {
        popParticleContainer(pattern);
        popIndent();

        String line = ")";

        if (pattern.getMin() == null) {
            throw new RuntimeException("Min value may not be null in CountingPattern.");
        } else if (pattern.getMax() == null) {
            if (pattern.getMin() == 0) {
                line += "*";
            } else if (pattern.getMin() == 1) {
                line += "+";
            } else {
                line += "[" + pattern.getMin() + "]";
            }
        } else if (pattern.getMax() == 1) {
            if (pattern.getMin() == 0) {
                line += "?";
            } else if (pattern.getMin() == 1) {
                line += "";
            } else {
                throw new RuntimeException("Illegal combination of min and max in CountingPattern: min = " + pattern.getMin() + ", max = " + pattern.getMax() + ".");
            }
        } else {
            line += "[" + pattern.getMin() + ", " + pattern.getMax() + "]";
        }

        if (hasMore) {
            line += " " + getParticleOperator();
        }

        appendLine(line);
    }

    /**
     * Visit SequencePattern object.
     */
    public void startSequencePattern(SequencePattern pattern) {
        pushParticleContainer(pattern);
        appendLine("(");
        pushIndent();
    }

    /**
     * Visit SequencePattern object.
     */
    public void endSequencePattern(SequencePattern pattern, boolean hasMore) {
        popParticleContainer(pattern);
        popIndent();

        String line = ")";

        if (hasMore) {
            line += " " + getParticleOperator();
        }
        appendLine(line);
    }

    /**
     * Visit AnyPattern object.
     */
    public void visitAnyPattern(AnyPattern particle, boolean hasMore) {
        String line = getForeignModifier(particle.getProcessContentsInstruction())
            + " element * { " + particle.getNamespace() + " }";

        if (hasMore) {
            line += " " + getParticleOperator();
        }
        appendLine(line);
    }

    /**
     * Visit Element object.
     */
    public void visitElement(de.tudortmund.cs.bonxai.bonxai.Element particle, boolean hasMore) {
        String line = "element ";

        if (!particle.getNamespace().equals(this.namespaces.getDefaultNamespace().getUri())) {
            line += this.namespaces.getNamespaceByUri(particle.getNamespace()).getIdentifier()
                + ":";
        }

        line += checkForKeywords(particle.getName());

        if (particle.getType() != null) {
            BonxaiType type = particle.getType();

            line += " { type ";

            // @TODO: Refactor this mechanism into method. Used several times.
            if (!type.getNamespace().equals(this.namespaces.getDefaultNamespace().getUri())) {
                line += this.namespaces.getNamespaceByUri(type.getNamespace()).getIdentifier()
                     + ":";
            }

            line += particle.getType().getType();

            if (particle.getFixed() != null) {
                line += " fixed " + quoteString(particle.getFixed());
            } else if (particle.getDefault() != null) {
                line += " default " + quoteString(particle.getDefault());
            }

            if (particle.isMissing()) {
                line += " | missing";
            }
            line += " }";
        }

        if (hasMore) {
            line += " " + getParticleOperator();
        }
        appendLine(line);
    }

    /**
     * Visit GroupRef object.
     */
    public void visitGroupRef(GroupRef particle, boolean hasMore) {
        String line = "group " + checkForKeywords(particle.getGroup().getName());

        if (hasMore) {
            line += " " + getParticleOperator();
        }
        appendLine(line);
    }


    /**
     * Visit the grammar list object.
     */
    public void startGrammarList(GrammarList grammar) {
        appendLine("grammar {");
        pushIndent();
    }

    /**
     * Visit the grammar list object.
     */
    public void endGrammarList(GrammarList grammar) {
        popIndent();
        appendLine("}");
        appendLine();
    }

    /**
     * Visit Expression object.
     */
    public void startExpression(Expression expression) {
        append(indent());
    }

    /**
     * Visit Expression object.
     */
    public void endExpression(Expression expression) {
    }

    /**
     * Visit AncestorPattern object.
     */
    public void startAncestorPattern(AncestorPattern aPattern) {
    }

    /**
     * Visit AncestorPattern object.
     */
    public void endAncestorPattern(AncestorPattern aPattern) {
        append(" =");
    }

    /**
     * Visit SingleSlashPrefixElement object.
     */
    public void visitSingleSlashPrefixElement(SingleSlashPrefixElement element, boolean hasMore) {
        append("/");

        if (!element.getNamespace().equals(this.namespaces.getDefaultNamespace().getUri())) {
            append(this.namespaces.getNamespaceByUri(element.getNamespace()).getIdentifier());
            append(":");
        }

        append(checkForKeywords(element.getName()));

        if (hasMore) {
            append(getAncestorPatternOperator());
        }
    }

    /**
     * Visit SingleSlashPrefixElement object.
     */
    public void visitDoubleSlashPrefixElement(DoubleSlashPrefixElement element, boolean hasMore) {
        append("//");

        if (!element.getNamespace().equals(this.namespaces.getDefaultNamespace().getUri())) {
            append(this.namespaces.getNamespaceByUri(element.getNamespace()).getIdentifier());
            append(":");
        }

        append(checkForKeywords(element.getName()));

        if (hasMore) {
            append(getAncestorPatternOperator());
        }
    }

    /**
     * Visit CardinalityParticle object.
     */
    public void startCardinalityParticle(CardinalityParticle particle) {
        append("(");
    }

    /**
     * Visit CardinalityParticle object.
     */
    public void endCardinalityParticle(CardinalityParticle particle, boolean hasMore) {
        append(")");

        if (particle.getMin() == 0 && particle.getMax() == null) {
            append("*");
        } else if (particle.getMin() == 0 && particle.getMax() == 1) {
            append("?");
        } else if (particle.getMin() == 1 && particle.getMax() > 1) {
            append("+");
        }

        if (hasMore) {
            append(getAncestorPatternOperator());
        }
    }

    /**
     * Visit OrExpression object.
     */
    public void startOrExpression(OrExpression particle) {
        pushAncestorContainerParticle(particle);
        append("(");
    }

    /**
     * Visit OrExpression object.
     */
    public void endOrExpression(OrExpression particle, boolean hasMore) {
        popAncestorContainerParticle(particle);
        append(")");

        if (hasMore) {
            append(getAncestorPatternOperator());
        }
    }

    /**
     * Visit SequencePattern object.
     */
    public void startSequenceExpression(SequenceExpression particle) {
        pushAncestorContainerParticle(particle);
        // @TODO: Not needed, right?
        // append("(");
    }

    /**
     * Visit SequencePattern object.
     */
    public void endSequenceExpression(SequenceExpression particle, boolean hasMore) {
        popAncestorContainerParticle(particle);
        // @TODO: Not needed, right?
        // append(")");

        if (hasMore) {
            append(getAncestorPatternOperator());
        }
    }

    /**
     * Visit ChildPattern object.
     */
    public void startChildPattern(ChildPattern cPattern) {
        append(" ");
        if (cPattern.getElementPattern() != null && cPattern.getElementPattern().isMixed()) {
            append("mixed ");
        }
        append("{\n");
        pushIndent();
    }

    /**
     * Visit ChildPattern object.
     */
    public void endChildPattern(ChildPattern cPattern) {
        popIndent();
        appendLine("}");
    }

    /**
     * Visit ElementPattern object.
     */
    public void startElementPattern(ElementPattern ePattern) {

    }

    /**
     * Visit ElementPattern object.
     */
    public void endElementPattern(ElementPattern ePattern) {

    }

    /**
     * Visit ElementPattern object.
     */
    public void visitElementPattern(ElementPattern ePattern) {
        String line = "";

        // @TODO: Correct handling for empty?
        if (ePattern == null) {
            line += "empty";
        } else if (ePattern.getBonxaiType() != null) {
            BonxaiType type = ePattern.getBonxaiType();

            // @TODO: Refactor this mechanism into method. Used several times.
            if (!type.getNamespace().equals(this.namespaces.getDefaultNamespace().getUri())) {
                line += this.namespaces.getNamespaceByUri(type.getNamespace()).getIdentifier()
                     + ":";
            }

            line += "type " + type.getType();

            if (ePattern.getFixed() != null) {
                line += " fixed " + quoteString(ePattern.getFixed());
            } else if (ePattern.getDefault() != null) {
                line += " default " + quoteString(ePattern.getDefault());
            }

            if (ePattern.isMissing()) {
                line += " | missing";
            }
        }

        appendLine(line);
    }

    /**
     * Visit the constraints lists object.
     */
    public void startConstraintList(ConstraintList constraints) {
        appendLine("constraints {");
        pushIndent();
    }

    /**
     * Visit the constraints list object.
     */
    public void endConstraintList(ConstraintList constraints) {
        popIndent();
        appendLine("}");
        appendLine();
    }

    /**
     * Visit UniqueConstraint object.
     */
    public void startUniqueConstraint(UniqueConstraint unique) {
        append(indent() + "unique ");
    }

    /**
     * Visit UniqueConstraint object.
     */
    public void endUniqueConstraint(UniqueConstraint unique) {
    }

    /**
     * Visit KeyConstraint object.
     */
    public void startKeyConstraint(KeyConstraint key) {
        String keyName = key.getName();
        append(indent() + "key " + checkForKeywords(keyName.substring(keyName.indexOf("}")+1)) + " ");
    }

    /**
     * Visit KeyConstraint object.
     */
    public void endKeyConstraint(KeyConstraint key) {

    }

    /**
     * Visit KeyRefConstraint object.
     */
    public void startKeyRefConstraint(KeyRefConstraint keyRef) {
        String keyRefName = keyRef.getReference();
        append(indent() + "keyref " + checkForKeywords(keyRefName.substring(keyRefName.indexOf("}")+1)) + " ");
    }

    /**
     * Visit KeyRefConstraint object.
     */
    public void endKeyRefConstraint(KeyRefConstraint keyRef) {

    }

    /**
     * Visit Constraint object.
     */
    public void startConstraint(Constraint constraint) {
        append(" {\n");
        pushIndent();
    }

    /**
     * Visit Constraint object.
     */
    public void endConstraint(Constraint constraint) {
        popIndent();
        appendLine("}");
    }

    /**
     * Visit constraint selector.
     */
    public void visitConstraintSelector(String selector) {
        appendLine(selector);
    }

    /**
     * Visit ConstraintFieldList object.
     */
    public void startConstraintFieldList(Constraint constraint) {
        appendLine("{");
        pushIndent();
    }

    /**
     * Visit ConstraintFieldList object.
     */
    public void endConstraintFieldList(Constraint constraint) {
        popIndent();
        appendLine("}");
    }

    /**
     * Visit constraint field.
     */
    public void visitConstraintField(String field, boolean hasMore) {
        String line = field;

        if (hasMore) {
            line += ",";
        }
        appendLine(line);
    }
    
    public void startRootElementList() {
    	append(indent());
    	append("roots {");
    }
    
    public void endRootElementList() {
    	append("}\n");
    }
    
    public void visitRootElement(String rootElement, boolean hasMore) {
    	append(rootElement);
    	if (hasMore) 
    		append(", ");
    }

    /**
     * Convert the given instruction to its string representation.
     */
    protected String getForeignModifier(ProcessContentsInstruction instruction) {
        String foreignModifier = "";
        switch (instruction) {
            case Strict:
                foreignModifier = "strict";
                break;
            case Lax:
                foreignModifier = "lax";
                break;
            case Skip:
                foreignModifier = "skip";
                break;
        }
        return foreignModifier;
    }

    /**
     * Returns the current particle operator.
     */
    protected String getParticleOperator() {
        String operator = "";

        if (particleContainerStack.empty()) {
            throw new RuntimeException("Particle container stack is empty!");
        }

        ParticleContainer current = particleContainerStack.peek();

        if (current instanceof AllPattern) {
            operator = "&";
        } else if (current instanceof ChoicePattern) {
            operator = "|";
        } else if (current instanceof CountingPattern) {
            operator = "";
        } else if (current instanceof SequencePattern) {
            operator = ",";
        }

        return operator;
    }

    /**
     * Returns the operator for the current ContainerParticle.
     */
    protected String getAncestorPatternOperator() {
        String operator = "";

        if (ancestorParticleStack.empty()) {
            operator = "";
        } else if (ancestorParticleStack.peek() instanceof OrExpression) {
            operator = "|";
        } else if (ancestorParticleStack.peek() instanceof SequenceExpression) {
            operator = "";
        }

        return operator;
    }

    /**
     * Pushes a ContainerParticle onto the ancestorParticleStack.
     */
    protected void pushAncestorContainerParticle(ContainerParticle containerParticle) {
        ancestorParticleStack.push(containerParticle);
    }

    /**
     * Pops a ContainerParticle from the ancestorParticleStack.
     */
    protected void popAncestorContainerParticle(ContainerParticle containerParticle) {
        if (ancestorParticleStack.peek() != containerParticle) {
            throw new RuntimeException("Nesting error, ContainerParticle to pop not the top ContainerParticle.");
        }
        ancestorParticleStack.pop();
    }

    /**
     * Pushes a ParticleContainer onto the particleContainerStack.
     */
    protected void pushParticleContainer(ParticleContainer particleContainer) {
        particleContainerStack.push(particleContainer);
    }

    /**
     * Pops a ParticleContainer from the particleContainerStack.
     */
    protected void popParticleContainer(ParticleContainer particleContainer) {
        if (particleContainerStack.peek() != particleContainer) {
            throw new RuntimeException("Nesting error, ParticleContainer to pop not the top ParticleContainer.");
        }
        particleContainerStack.pop();
    }

    /**
     * Append a string to the result without line break.
     */
    protected void append(String string) {
        result += string;
    }

    /**
     * Append a line to the result.
     */
    protected void appendLine(String line) {
        result += indent() + line + "\n";
    }

    /**
     * Append an empty line.
     */
    protected void appendLine() {
        result += "\n";
    }

    /**
     * Quote a value for printing in Bonxai (default / fixed value).
     */
    protected String quoteString(String toQuote) {
        return "\"" + toQuote.replace("\"", "\\\"") + "\"";
    }

    /**
     * Get current line indentation.
     */
    protected String indent() {
        String indent = "";
        for (int i = 0; i < this.indentLevel; i++) {
            indent += this.indentString;
        }
        return indent;
    }

    /**
     * Push one indent level.
     */
    public void pushIndent() {
        indentLevel++;
    }

    /**
     * Pop one indent level.
     */
    public void popIndent() {
        if (indentLevel == 0) {
            throw new RuntimeException("Cannot pop indent level below 0!");
        }
        indentLevel--;
    }

    private String[]  keywords = {"empty", "missing", "fixed", "default", "mixed", "strict", "lax", "skip", "*", "type", "group", "div", 
    "namespace", "value", "attribute-group", "datatypes", "import", "unique", "key", "keyref", "groups", "grammar", "for", "element", "attribute", "union"
    };

    public String checkForKeywords(String name){
        for (String keyword : keywords) {
            if(keyword.equals(name)){
                name = "\\\\" + name;
            }
        }
        return name;
    }
}
