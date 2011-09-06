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
package eu.fox7.bonxai.bonxai;

import java.util.Vector;

/**
 * Class holding a list of expressions(annotation,ancestorpattern,childpattern)
 */
public class GrammarList {

    /**
     * List of expressions
     */
    private Vector<Expression> expressions = new Vector<Expression>();

    /**
     * List of possible root elements
     */
    private Vector<String> rootElementNames = new Vector<String>();

    /**
     * Returns the list of expressions held by this GrammarList
     * @return expressions
     */
    public Vector<Expression> getExpressions() {
        return expressions;
    }

    /**
     * Adds a expression to the list of expressions
     * @param expression
     */
    public void addExpression(Expression expression) {
        this.expressions.add(expression);
    }
    
    /**
     * Returns the list of rootElements
     * @return rootElementSet
     */
    public Vector<String> getRootElementNames() {
    	return rootElementNames;
    }
    
    /** 
     * Adds an element to rootElementNames
     */
    public void addRootElementName(String name) {
    	this.rootElementNames.add(name);
    }

}

