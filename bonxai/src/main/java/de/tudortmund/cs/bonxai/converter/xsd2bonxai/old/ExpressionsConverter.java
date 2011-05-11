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
package de.tudortmund.cs.bonxai.converter.xsd2bonxai.old;

import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.AttributeProcessor;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.ParticleProcessor;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import java.util.*;

/**
 * Bonxai2XSDConverter that handles expression conversion.
 *
 * It generates an expression ({@link de.tudortmund.cs.bonxai.bonxai.Expression}) in
 * the USPL instance in accordance to the given XSD XSDSchema. An expression conists
 * of an annotation, ancestor-pattern and child-pattern. With the help of the
 * type automaton ancestor-patterns are found via an ancestorPathStrategy.
 * Child-patterns can almost be copied from the ComplexType instances in the
 * specified XSDSchema.
 *
 * @TODO What about annotations? What should be there keys and values, both Strings?
 */
public class ExpressionsConverter implements Converter {

    /**
     * AncestorPathStrategy, which will be used for ancestor-paht generation.
     * As described in {@link AncestorPathStrategy} it generates an ancestor-
     * paths for each state of the type automaton. Generated ancestor-pahts will
     * always be correct but may vary in lenght and readability.
     */
    private AncestorPathStrategy ancestorPathStrategy;

    /**
     * This method injectes the AncestorPathStrategy for the conversion process.
     * Different ancestor-path strategies generate different ancestor-paths,
     * which may vary in lenght, legibility and complexity.
     *
     * If the received AncestorPathStrategy is null, the default AncestorPathStrategy
     * is used.
     *
     * @param ancestorPathStrategy  ancestor-path strategy, which will be used by
     *                              the convert method to create ancestor-pathes
     */
    public void setAncestorPathStrategy(AncestorPathStrategy ancestorPathStrategy) {
        this.ancestorPathStrategy = ancestorPathStrategy;
    }

    /**
     * Method initializes default AncestorPathStrategy if no other one has been
     * injected yet. For the moment the default ancestorPathStrategy will be the
     * simple ancestor-path strategy.
     * ({@link de.tudortmund.cs.bonxai.converter.xsd2bonxai.old.SimpleAncestorPathStrategy})
     */
    private void init() {
        ancestorPathStrategy = new SimpleAncestorPathStrategy();
    }

    /**
     * This method generates a type automaton for a given XSD XSDSchema.
     *
     * Each ComplexType is given a corresponding state in the type automaton. A
     * transition between two states exists, if the type of the destination state
     * is a direct child of the type of the source state. Annotated to the
     * transition are all elements containig the type of the source state. For
     * each pair of states only one transition exists in the type automaton.
     *
     * @param schema        XSD XSDSchema, which should be converted
     * @param automaton     type automaton, for ancestor-path generation
     * @param bonxai          bonxai schema, to which the expressions are added
     * @return bonxai schema with correct expressions corresponding to the XSD XSDSchema
     */
    public Bonxai convert(XSDSchema schema, TypeAutomaton automaton, Bonxai bonxai) {

        // Initialize the default ancestor-path strategy if no other was selected
        if (ancestorPathStrategy == null) {
            init();
        }

        // Instantiate attributeProcessor and particleProcessor
        AttributeProcessor attributeProcessor = new AttributeProcessor((SymbolTable<AttributeGroupElement>) bonxai.getAttributeGroupElementSymbolTable());
        ParticleProcessor particleProcessor = new ParticleProcessor((SymbolTable<ElementGroupElement>) bonxai.getGroupSymbolTable());

        // Calculate ComplexType - AncestorPattern mapping
        HashMap<TypeAutomatonState, AncestorPattern> ancestorMap = ancestorPathStrategy.calculate(automaton);

        // Generate all Expressions for ComplexTypes of the original XSD
        GrammarList complexTypeExpressionList = new GrammarList();
        for (Iterator<TypeAutomatonState> it = ancestorMap.keySet().iterator(); it.hasNext();) {
            TypeAutomatonState typeAutomatonState = it.next();
            Expression expression = new Expression();
            expression.setAncestorPattern(ancestorMap.get(typeAutomatonState));
            ElementPattern expressionElementPattern = null;
            if (typeAutomatonState.getType().getContent() instanceof ComplexContentType) {
                Particle particle = ((ComplexContentType) typeAutomatonState.getType().getContent()).getParticle();
                expressionElementPattern = new ElementPattern(particleProcessor.convertParticle(particle));
            }
            if(typeAutomatonState.getType().getContent() instanceof SimpleContentType && ((SimpleContentType)typeAutomatonState.getType().getContent()).getInheritance() != null){
                Inheritance inheritance = (Inheritance)((SimpleContentType)typeAutomatonState.getType().getContent()).getInheritance();
                expressionElementPattern = new ElementPattern(new BonxaiType(inheritance.getBase().getNamespace(), inheritance.getBase().getLocalName()));
            }
            AttributePattern expressionAttributePattern = attributeProcessor.convertAttributes(typeAutomatonState.getType().getAttributes());
            expression.setChildPattern(new ChildPattern(expressionAttributePattern, expressionElementPattern));
            complexTypeExpressionList.addExpression(expression);
        }
        // Sort the Expressions for ComplexTypes
        Collections.sort(complexTypeExpressionList.getExpressions(), new Comparator<Expression>() {

            public int compare(Expression expression, Expression secondExpression) {
                return expression.getAncestorPattern().toString().compareTo(secondExpression.getAncestorPattern().toString());
            }
        });

        // Generate Expresions for root Elements with no ComplexTyps, because they are not in the TypeAutomaton there priority
        // has to be higher.
        GrammarList rootElementExpressionList = new GrammarList();
        for (Iterator<Element> it = schema.getElements().iterator(); it.hasNext();) {
            Element element = it.next();
            if (element.getType() instanceof SimpleType) {
                SingleSlashPrefixElement singleSlashElement = new SingleSlashPrefixElement(element.getNamespace(), element.getLocalName());
                Expression expression = new Expression();
                expression.setAncestorPattern(new AncestorPattern(singleSlashElement));
                ElementPattern expressionElementPattern = new ElementPattern(new BonxaiType(element.getType().getNamespace(), element.getType().getLocalName()));
                if (element.getFixed() != null) {
                    expressionElementPattern.setFixed(element.getFixed());
                }
                if (element.getDefault() != null) {
                    expressionElementPattern.setDefault(element.getDefault());
                }
                ChildPattern expressionChildPattern = new ChildPattern(new AttributePattern(), expressionElementPattern);
                expression.setChildPattern(expressionChildPattern);
                rootElementExpressionList.addExpression(expression);
            }
        }

        // Sort the Expressions for root Elements
        Collections.sort(rootElementExpressionList.getExpressions(), new Comparator<Expression>() {

            public int compare(Expression expression, Expression secondExpression) {
                return expression.getAncestorPattern().toString().compareTo(secondExpression.getAncestorPattern().toString());
            }
        });

        // Instantiate GrammarList of Bonxai
        GrammarList grammarList = new GrammarList();
        bonxai.setGrammarList(grammarList);

        // Add all Expressions of ComplexTypes and root Elements to the GrammarList and return the Bonxai
        grammarList.getExpressions().addAll(complexTypeExpressionList.getExpressions());
        grammarList.getExpressions().addAll(rootElementExpressionList.getExpressions());
        return bonxai;
    }
}
