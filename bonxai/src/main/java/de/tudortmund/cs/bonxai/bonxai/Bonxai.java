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
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Set;

import de.tudortmund.cs.bonxai.common.*;

/**
 * Class representing an Bonxai XSDSchema
 */
public class Bonxai {

    /**
     * Header of the schema
     */
    private Declaration declaration;
    /**
     * List of constrains
     */
    private ConstraintList constraintList;
    /**
     * Grammar part of the schema
     */
    private GrammarList grammarList;
    /**
     * Group part of the schema
     */
    private GroupList groupList;
    
    /**
     * SymbolTable used to store all AttributeGroupElements defined in a XSDSchema.
     *
     * Stored in the XSDSchema object to allow easy manipulation of reference
     * links.
     */
    private SymbolTableFoundation<AttributeGroupElement> attributeGroupSymbolTable;

    /**
     * SymbolTable used to store all Groups defined in a XSDSchema.
     *
     * Stored in the XSDSchema object to allow easy manipulation of reference
     * links.
     */
    private SymbolTableFoundation<ElementGroupElement> groupSymbolTable;

    /**
     * Construct Bonxai schema.
     *
     * Initializes the required symbol tables.
     */
    public Bonxai() {
        this.groupSymbolTable          = new SymbolTable<ElementGroupElement>();
        this.attributeGroupSymbolTable = new SymbolTable<AttributeGroupElement>();
    }

    /**
     * Retrieve the SymbolTable used to store all AttributeGroupElements
     */
    public SymbolTableFoundation<AttributeGroupElement> getAttributeGroupElementSymbolTable() {
        return this.attributeGroupSymbolTable;
    }

    /**
     * Replace the SymbolTable used to store all AttributeGroupElements
     *
     * This should operation should be used carefully. Most operations on this
     * datastructure do not need the use of this method. It is always
     * recommended to replace reference targets in the SymbolTable itself, than
     * replacing the whole SymbolTable.
     */
    public void setAttributeGroupElementSymbolTable( SymbolTableFoundation<AttributeGroupElement> table ) {
        this.attributeGroupSymbolTable = table;
    }

    /**
     * Retrieve the SymbolTable used to store all ElementGroupElements
     */
    public SymbolTableFoundation<ElementGroupElement> getGroupSymbolTable() {
        return this.groupSymbolTable;
    }

    /**
     * Replace the SymbolTable used to store all ElementGroupElements
     *
     * This should operation should be used carefully. Most operations on this
     * datastructure do not need the use of this method. It is always
     * recommended to replace reference targets in the SymbolTable itself, than
     * replacing the whole SymbolTable.
     */
    public void setGroupSymbolTable( SymbolTableFoundation<ElementGroupElement> table ) {
        this.groupSymbolTable = table;
    }

   
    /**
     * Returns the list of constraints
     * @return constraintList
     */
    public ConstraintList getConstraintList() {
        return constraintList;
    }

    /**
     * Sets the list of constrains
     * @param constraintList
     */
    public void setConstraintList(ConstraintList constraintList) {
        this.constraintList = constraintList;
    }

    /**
     * Returns the header of schema
     * @return declaration
     */
    public Declaration getDeclaration() {
        return declaration;
    }

    /**
     * Sets the schema header
     * @param declaration
     */
    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    /**
     * Returns the list of rules
     * @return grammarList
     */
    public GrammarList getGrammarList() {
        return grammarList;
    }

    /**
     * Sets the rules
     * @param grammarList
     */
    public void setGrammarList(GrammarList grammarList) {
        this.grammarList = grammarList;
    }

    /**
     * Returns the list of groups
     * @return groupList
     */
    public GroupList getGroupList() {
        return groupList;
    }

    /**
     * Sets the groups
     * @param groupList
     */
    public void setGroupList(GroupList groupList) {
        this.groupList = groupList;
    }
}
