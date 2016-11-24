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

package eu.fox7.schematoolkit.bonxai.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.bonxai.om.AncestorPatternElement;
import eu.fox7.schematoolkit.bonxai.om.AncestorPatternWildcard;
import eu.fox7.schematoolkit.bonxai.om.Annotation;
import eu.fox7.schematoolkit.bonxai.om.Attribute;
import eu.fox7.schematoolkit.bonxai.om.AttributePattern;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.BonxaiAbstractGroup;
import eu.fox7.schematoolkit.bonxai.om.BonxaiAttributeGroup;
import eu.fox7.schematoolkit.bonxai.om.BonxaiGroup;
import eu.fox7.schematoolkit.bonxai.om.BonxaiType;
import eu.fox7.schematoolkit.bonxai.om.CardinalityParticle;
import eu.fox7.schematoolkit.bonxai.om.ChildPattern;
import eu.fox7.schematoolkit.bonxai.om.Constraint;
import eu.fox7.schematoolkit.bonxai.om.DoubleSlashPrefixedContainer;
import eu.fox7.schematoolkit.bonxai.om.Element;
import eu.fox7.schematoolkit.bonxai.om.ElementPattern;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.bonxai.om.KeyConstraint;
import eu.fox7.schematoolkit.bonxai.om.KeyRefConstraint;
import eu.fox7.schematoolkit.bonxai.om.OrExpression;
import eu.fox7.schematoolkit.bonxai.om.SequenceExpression;
import eu.fox7.schematoolkit.bonxai.om.UniqueConstraint;
import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.AttributeGroupReference;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.AttributeRef;
import eu.fox7.schematoolkit.common.AttributeUse;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.util.FormattedWriter;

public class CompactSyntaxWriter {

	private FormattedWriter writer;
	private Bonxai schema;
	
    /**
     * Write the schema
     *
     * Converts the given Bonxai schema using the given visitor.
     * @throws IOException 
     */
    public String write(Bonxai schema) throws IOException
    {
    	StringWriter sWriter = new StringWriter();
    	this.writer = new FormattedWriter(sWriter);
        writeSchema(schema);
        this.writer.flush();
        return sWriter.toString();
    }
    
    public void write(Bonxai schema, java.io.Writer writer) throws IOException {
    	this.writer = new FormattedWriter(writer);
        writeSchema(schema);
        this.writer.flush();
    }

    /**
     * Visit schema object.
     * @throws IOException 
     */
    protected void writeSchema(Bonxai schema) throws IOException {
    	this.schema = schema;
    	writeTargetNamespace(schema.getTargetNamespace());
        writeIdentifiedNamespaces(schema.getNamespaces());
        writer.newLine();
        writeGlobalElements(schema.getRootElementNames());
        writeGroups(schema.getGroups());
        writer.appendLine("grammar {");
        writer.pushIndent();
        writeExpressions(schema.getExpressions());
        writeConstraints(schema.getConstraints());
        writer.popIndent();
        writer.appendLine("}");
    }
    
    protected void writeTargetNamespace(Namespace namespace) throws IOException {
    	if (! namespace.getUri().equals(""))
    		writer.appendLine("target namespace " +  namespace.getUri());
    }

    /**
     * Visit identified namespaces object.
     * @throws IOException 
     */
    protected void writeIdentifiedNamespaces(Collection<IdentifiedNamespace> namespaces) throws IOException {
        TreeSet<IdentifiedNamespace> sortedNamespaceList = new TreeSet<IdentifiedNamespace>(namespaces);
        for (IdentifiedNamespace namespace: sortedNamespaceList) {
            writer.appendLine("namespace " + namespace.getIdentifier() + " = " + namespace.getUri());
        }
    }
    
    protected void writeGlobalElements(List<QualifiedName> rootElementNames) throws IOException {
    	writer.append("global { ");
    	writer.pushIndent();
    	boolean first=true;
    	for (QualifiedName name: rootElementNames) {
    		if (!first) 
    			writer.allowBreak(", ");
    		writeName(name);
    		first=false;
    	}
    	writer.popIndent();
    	writer.appendLine(" }");
    }

    


	/**
     * Visit groups object.
	 * @throws IOException 
     */
    protected void writeGroups(List<BonxaiAbstractGroup> groups) throws IOException {
        if (groups != null && groups.size()>0) {
        	writer.appendLine("groups {");
        	writer.pushIndent();
            for (BonxaiAbstractGroup group: groups)
            	writeGroup(group);
            writer.popIndent();
            writer.appendLine("}");
            writer.newLine();
        }
    }

    /**
     * Visit groups objects.
     */
    protected void writeGroup(BonxaiAbstractGroup group) throws IOException {
    	if (group instanceof BonxaiGroup)
    		writer.append("group ");
    	else
    		writer.append("attribute-group ");

    	writeName(group.getName());
    	writer.append(" = { ");
    	writer.pushIndent();
    	writer.allowBreak();

    	if (group instanceof BonxaiGroup) {
    		writeParticle(((BonxaiGroup) group).getParticle());
    	} else {
    		writeAttributePattern(((BonxaiAttributeGroup) group).getAttributePattern());
    	}
    	writer.append(" }");
    	writer.newLine();
    	writer.popIndent();
    }

    /**
     * Visit attribute pattern object.
     * @return if sth. has been written
     * @throws IOException 
     */
    protected boolean writeAttributePattern(AttributePattern attributePattern) throws IOException {
        boolean output = false;
    	if (attributePattern!=null) {
        	if (attributePattern.getAnyAttribute() != null) {
        		output = true;
        		writeAnyAttribute(attributePattern.getAnyAttribute());
        	}	
        	if (attributePattern.getAttributeList() != null && attributePattern.getAttributeList().size()>0) {
        		output = true;
        		writeAttributeList(attributePattern.getAttributeList());
        	}
        }
    	return output;
    }

    /**
     * Visit any attribute object.
     * @throws IOException 
     */
    protected void writeAnyAttribute(AnyAttribute anyAttribute) throws IOException {
    	writeForeignModifier(anyAttribute.getProcessContentsInstruction());
    	writer.append(" attribute * { ");
    	Collection<Namespace> namespaces = anyAttribute.getNamespaces();
    	Iterator<Namespace> it = namespaces.iterator();
    	while (it.hasNext()) {
    		Namespace namespace = it.next();
    		writer.append(namespace.getUri());
    		if (it.hasNext())
    			writer.append(',');
    	}
    	writer.append("}");
    }

    private void writeForeignModifier(
			ProcessContentsInstruction processContentsInstruction) throws IOException {
    	switch (processContentsInstruction) {
    	case LAX: writer.append("lax ");
    	case SKIP: writer.append("skip ");
    	case STRICT: writer.append("strict ");    	
    	}
		
	}

	/**
     * Visit attribute list object.
	 * @throws IOException 
     */
    protected void writeAttributeList(Collection<AttributeParticle> attributeList) throws IOException {
        boolean first = true;
    	for(AttributeParticle attributeElement: attributeList) {
    		if (!first) {
    			writer.append(", ");
        		writer.allowBreak();
    		}
            if (attributeElement instanceof Attribute) {
                writeAttribute((Attribute) attributeElement);
            } else if (attributeElement instanceof AttributeGroupReference) {
                writeAttributeGroupReference((AttributeGroupReference) attributeElement);
            } else if (attributeElement instanceof AttributeRef) {
            	writeAttributeRef((AttributeRef) attributeElement);
            }
            first = false;
        }
    }

    private void writeAttributeRef(AttributeRef attributeRef) throws IOException {
		writer.append("attributeref ");
		writeName(attributeRef.getName());

        if (attributeRef.getFixed() != null) {
            writer.append("{ fixed \"");
            writer.append(attributeRef.getFixed());
            writer.append("\"}");
        } else if (attributeRef.getDefault() != null) {
            writer.append("{ default \"");
            writer.append(attributeRef.getDefault());
            writer.append("\"}");
        }

		
		if (attributeRef.getUse()==AttributeUse.optional) {
			writer.append('?');
		}
		
	}

	private void writeAttributeGroupReference(
			AttributeGroupReference attributeElement) throws IOException {
		writer.append("attribute-group ");
		writeName(attributeElement.getName());
	}

	private void writeAttribute(Attribute attribute) throws IOException {
        writer.append("attribute ");
        writeName(attribute.getName());
        if (attribute.getType() != null) {
        	writer.append(" { ");
            writeBonxaiType(attribute.getType());
        	writer.append(" }");
        }
        if (!attribute.isRequired()) {
            writer.append('?');
        }
	}

	private void writeBonxaiType(BonxaiType type) throws IOException {
        writeName(type.getTypename());

        if (type.getFixedValue() != null) {
            writer.append(" fixed \"");
            writer.append(type.getFixedValue());
            writer.append('"');
        } else if (type.getDefaultValue() != null) {
            writer.append(" default \"");
            writer.append(type.getDefaultValue());
            writer.append('"');
        }
	}

	/**
     * Visit Particle objects.
	 * @throws IOException 
     */
    protected void writeParticle(Particle particle) throws IOException {
        if (particle instanceof AllPattern) {
            writeParticleContainer((ParticleContainer) particle, " & ");
        } else if (particle instanceof ChoicePattern) {
            writeParticleContainer((ParticleContainer) particle, " | ");
        } else if (particle instanceof CountingPattern) {
            writeCountingPattern((CountingPattern) particle);
        } else if (particle instanceof SequencePattern) {
            writeParticleContainer((ParticleContainer) particle, ", ");
        } else if (particle instanceof AnyPattern) {
            writeAnyPattern((AnyPattern) particle);
        } else if (particle instanceof eu.fox7.schematoolkit.bonxai.om.Element) {
            writeElement((eu.fox7.schematoolkit.bonxai.om.Element) particle);
        } else if (particle instanceof ElementRef) {
            writeElementRef((ElementRef) particle);
        } else if (particle instanceof GroupReference) {
            writeGroupReference((GroupReference) particle);
        } else if (particle instanceof EmptyPattern) {
        	writer.append("empty");
        } else {
            throw new RuntimeException("Cannot write Particle of " + particle.getClass());
        }
    }

    private void writeElement(Element particle) throws IOException {
    	writer.append("element ");
    	writeName(particle.getName());
        
        if (particle.getType() != null) {
        	writer.append(" { ");
        	writeBonxaiType(particle.getType());
        	writer.append(" }");
        }      
	}

    private void writeElementRef(ElementRef particle) throws IOException {
    	writer.append("elementref ");
    	writeName(particle.getElementName());
   	}

	private void writeGroupReference(GroupReference particle) throws IOException {
    	writer.append("group ");
    	writeName(particle.getName());
	}

	private void writeAnyPattern(AnyPattern particle) throws IOException {
		writer.append("any { ");
		if (particle.getProcessContentsInstruction()!=null)
			writer.append(particle.getProcessContentsInstruction().toString() + " ");
		//if (particle.isComplementNamespaces())
		//	writer.append("not ");
		//for (Namespace namespace: particle.getNamespaces())
		//	writer.append(namespace.getUri());
		writer.append("}");
		// TODO Auto-generated method stub
		
	}

	/**
     * Visit LinkedList<Particle> object.
     * @throws IOException 
     */
    protected void writeParticleContainer(ParticleContainer particleContainer, String operator) throws IOException {
    	List<Particle> particleList = particleContainer.getParticles();
    	boolean first=true;
    	for(Particle currentParticle: particleList) {
            if (first) 
            	first=false;
            else {
            	writer.allowBreak(operator);
            }
    		writeParticle(currentParticle);
        }
    }

    /**
     * Visit CountingPattern object.
     * @throws IOException 
     */
    protected void writeCountingPattern(CountingPattern pattern) throws IOException {
        writer.append("(");
        writeParticle(pattern.getParticle());
        writer.append(")");
        
        if (pattern.getMax() == null) {
            if (pattern.getMin() == 0) {
                writer.append("*");
            } else if (pattern.getMin() == 1) {
                writer.append("+");
            } else {
                writer.append("[").append(new Integer(pattern.getMin()).toString()).append("]");
            }
        } else if (pattern.getMax() == 1) {
            if (pattern.getMin() == 0) {
                writer.append("?");
            } 
        } else {
            writer.append("[").append(new Integer(pattern.getMin()).toString()).append(",").append(pattern.getMax().toString()).append("]");
        }
    }

    /**
     * Visit GrammarList object.
     * @throws IOException 
     */
    protected void writeExpressions(List<Expression> list) throws IOException {
        if (list != null) {
            // @TODO:
            // tarverseAnnotations(grammar.getAnnotations());
            for(Expression expression : list) {
                writeExpression(expression);
            }
        }
    }

    /**
     * Visit Expression object.
     * @throws IOException 
     */
    protected void writeExpression(Expression expression) throws IOException {
    	writer.newLine();
    	writeAnnotations(expression.getAnnotations());
        writeAncestorPattern(expression.getAncestorPattern(), true, true);
        writer.append(" = ");
        writeChildPattern(expression.getChildPattern());
        writer.newLine();
    }

    private void writeAnnotations(List<Annotation> annotations) throws IOException {
		for (Annotation annotation: annotations) {
			writer.append("@"+annotation.getKey());
			writer.append(" = ");
			writer.append(annotation.getValue());
			writer.newLine();
		}
	}

    protected void writeAncestorPattern(AncestorPattern aPattern, boolean leadingSlash) throws IOException {
    	this.writeAncestorPattern(aPattern, leadingSlash, false);
    }
        
    
	/**
     * Visit ChildPattern object.
     * @throws IOException 
     */
    protected void writeAncestorPattern(AncestorPattern aPattern, boolean leadingSlash, boolean removeDoubleSlash) throws IOException {
        if (aPattern instanceof AncestorPatternElement) {
            writeAncestorPatternElement((AncestorPatternElement) aPattern, leadingSlash);
        } else if (aPattern instanceof AncestorPatternWildcard) {
        	writeAncestorPatternWildcard(leadingSlash);
        } else if (aPattern instanceof CardinalityParticle) {
            writeCardinalityParticle((CardinalityParticle) aPattern, leadingSlash);
        } else if (aPattern instanceof OrExpression) {
            writeOrExpression((OrExpression) aPattern, leadingSlash);
        } else if (aPattern instanceof SequenceExpression) {
            writeSequenceExpression((SequenceExpression) aPattern, leadingSlash);
        } else if (aPattern instanceof DoubleSlashPrefixedContainer) {
        	writeDoubleSlashPrefixedContainer((DoubleSlashPrefixedContainer) aPattern, leadingSlash, removeDoubleSlash);
        } else {
            throw new RuntimeException("Unknown AncestorPattern " + aPattern.getClass());
        }
    }

    private void writeAncestorPatternWildcard(boolean leadingSlash) throws IOException {
		if (leadingSlash) 
			writer.append('/');
		writer.append('*');
	}

	private void writeAncestorPatternElement(AncestorPatternElement aPattern,
			boolean leadingSlash) throws IOException {
		if (leadingSlash) 
			writer.append('/');
		writeName(aPattern.getName(), true);
	}

	private void writeDoubleSlashPrefixedContainer(DoubleSlashPrefixedContainer aParticle, boolean leadingSlash, boolean removeDoubleSlash) throws IOException {
    	if (!removeDoubleSlash)
    		writer.append("//");
    	writeAncestorPattern(aParticle.getChild(), false);
	}

    /**
     * Visit CardinalityParticle object.
     * @throws IOException 
     */
    protected void writeCardinalityParticle(CardinalityParticle cardinalityParticle, boolean leadingSlash) throws IOException {
        writer.append("(");
        writer.pushIndent();        
        writeAncestorPattern(cardinalityParticle.getChild(), leadingSlash);
        writer.popIndent();
        writer.append(")");
        String operator = "";
        if ((cardinalityParticle.getMin() == 0) && cardinalityParticle.getMax() == null)
        	operator = "*";
        else if ((cardinalityParticle.getMin() == 0) && (cardinalityParticle.getMax() == 1))
        	operator = "?";
        else if ((cardinalityParticle.getMin() == 1) && (cardinalityParticle.getMax() == null))
        	operator = "+";
        writer.append(operator);
    }

    /**
     * Visit OrExpression object.
     * @throws IOException 
     */
    protected void writeOrExpression(OrExpression orExpression, boolean leadingSlash) throws IOException {
        writer.append("(");
        boolean first=true;
        for (AncestorPattern particle: orExpression.getChildren()) {
            if (first)
            	first=false;
            else
            	writer.allowBreak(" | ");
        	writeAncestorPattern(particle, leadingSlash);
        }
        writer.append(")");
    }

    /**
     * Visit SequenceExpression object.
     * @throws IOException 
     */
    protected void writeSequenceExpression(SequenceExpression sequenceExpression, boolean leadingSlash) throws IOException {
        for(AncestorPattern particle: sequenceExpression.getChildren()) {
        	writeAncestorPattern(particle, leadingSlash);
        	leadingSlash=true;
        }
    }

    /**
     * Visit ChildPattern object.
     * @throws IOException 
     */
    protected void writeChildPattern(ChildPattern cPattern) throws IOException {
        if (cPattern == null) {
            throw new RuntimeException("Missing ChildPattern.");
        }
        writer.append("{ ");
        writer.pushIndent();
        boolean first = true;
        if (cPattern.getAttributePattern() != null) {
        	first = !(writeAttributePattern(cPattern.getAttributePattern()));
        }
        writeElementPattern(cPattern.getElementPattern(), first);
        writer.append(" }");
        writer.popIndent();
    }

    /**
     * Visit ElementPattern object.
     * @throws IOException 
     */
    protected void writeElementPattern(ElementPattern ePattern, boolean first) throws IOException {
        if (ePattern != null && ePattern.getRegexp() != null) {
        	if (! (ePattern.getRegexp() instanceof EmptyPattern)) {
        		if (!first) {
        			writer.allowBreak(", ");
        		}
        		writeParticle(ePattern.getRegexp());
        	}
        } else if (ePattern != null && ePattern.getBonxaiType() != null ) {
        	if (!first) {
        		writer.allowBreak(", ");
        	}
        	writer.append("type ");
            writeBonxaiType(ePattern.getBonxaiType());
        }
    }

    /**
     * Dispatch visiting of Constraint.
     * @throws IOException 
     */
    protected void writeConstraints(List<Constraint> list) throws IOException {
        for (Constraint constraint : list) {
            if (constraint instanceof UniqueConstraint)
                writer.append("unique ");
            else if (constraint instanceof KeyConstraint) {
            	writer.append("key ");
            	writeName(((KeyConstraint) constraint).getName());
            } else if (constraint instanceof KeyRefConstraint) {
             	writer.append("keyref ");
            	writeName(((KeyRefConstraint) constraint).getReference());
            } else {
                throw new RuntimeException("Unknown Constraint " + constraint.getClass());
            }
            writeConstraint(constraint);
            writer.newLine();
        }
    }

    /**
     * Visit Constraint object.
     * @throws IOException 
     */
    protected void writeConstraint(Constraint constraint) throws IOException {
        writeAncestorPattern(constraint.getAncestorPattern(), true);

        writer.append(" = {");
        writeAncestorPattern(constraint.getConstraintSelector(), true);
        writer.append(" {");
        
        for(AncestorPattern constraintField: constraint.getConstraintFields()) {
            writeAncestorPattern(constraintField, true);
        }
        writer.append("} }");
    }

    private void writeName(QualifiedName name) throws IOException {
    	writeName(name, false);
    }

    private void writeName(QualifiedName name, boolean prefixAttributeWithAT) throws IOException {
    	writer.append(schema.getNamespaceList().getQualifiedName(name, prefixAttributeWithAT));
	}


}
