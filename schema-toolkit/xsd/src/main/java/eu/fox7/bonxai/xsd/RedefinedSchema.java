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
package eu.fox7.bonxai.xsd;

import java.util.HashSet;
import java.util.Set;


/**
 * This class represents a redefinition of types and attributeGroups imported from external
 * schemas.
 */
public class RedefinedSchema extends ForeignSchema {

    protected Set<eu.fox7.bonxai.common.SymbolTableRef<Type>> types;

    protected Set<eu.fox7.bonxai.common.SymbolTableRef<AttributeGroup>> attributeGroups;

    protected Set<eu.fox7.bonxai.common.SymbolTableRef<eu.fox7.bonxai.xsd.Group>> groups;


    /**
     * Creates a new RedefinedSchema with a schemaLocation and empty containers.
     * @param schemaLocation 
     */
    public RedefinedSchema (String schemaLocation) {
        super(schemaLocation);
        types = new HashSet<eu.fox7.bonxai.common.SymbolTableRef<Type>>();
        attributeGroups = new HashSet<eu.fox7.bonxai.common.SymbolTableRef<AttributeGroup>>();
        groups = new HashSet<eu.fox7.bonxai.common.SymbolTableRef<eu.fox7.bonxai.xsd.Group>>();
    }

    /**
     * Returns the attributeGroups from the imported schema that are redefined within the
     * current schema.
     * @return s    HashSet<AttributeGroup>()
     */
    public Set<AttributeGroup> getAttributeGroups () {
        Set<AttributeGroup> s = new HashSet<AttributeGroup>();

        if(attributeGroups == null) {
            return s;
        }

        for(eu.fox7.bonxai.common.SymbolTableRef<AttributeGroup> st : attributeGroups)
        {
            s.add(st.getReference());
        }
        return s;
    }

    /**
     * Adds a new definition of an attributeGroup from the external schema
     * which is redefined within the current schema.
     * @param val
     */
    public void addAttributeGroup (eu.fox7.bonxai.common.SymbolTableRef<AttributeGroup> val) {
        this.attributeGroups.add(val);
    }

    /**
     * Returns the Groups from the imported schema that are redefined within the
     * current schema.
     * @return s    HashSet<Type>();
     */
    public Set<eu.fox7.bonxai.xsd.Group> getGroups () {
        Set<eu.fox7.bonxai.xsd.Group> s = new HashSet<eu.fox7.bonxai.xsd.Group>();

        if(groups == null) {
            return s;
        }

        for(eu.fox7.bonxai.common.SymbolTableRef<eu.fox7.bonxai.xsd.Group> st : groups)
        {
            s.add(st.getReference());
        }
        return s;
    }

    /**
     * Adds a new definition of a Group from the external schema which is redefined within the
     * current schema.
     * @param val
     */
    public void addGroup (eu.fox7.bonxai.common.SymbolTableRef<eu.fox7.bonxai.xsd.Group> val) {
        this.groups.add(val);
    }

    /**
     * Returns the location of the schema from which the elements must be redefined.
     */
    @Override
    public String getSchemaLocation () {
        return schemaLocation;
    }

    /**
     * Returns the Types from the imported schema that are redefined within the
     * current schema.
     * @return s    HashSet<Type>();
     */
    public Set<Type> getTypes () {
        Set<Type> s = new HashSet<Type>();

        if(types == null) {
            return s;
        }

        for(eu.fox7.bonxai.common.SymbolTableRef<Type> st : types)
        {
            s.add(st.getReference());
        }
        return s;
    }

    /**
     * Adds a new definition of a Type from the external schema which is redefined within the
     * current schema.
     * @param val 
     */
    public void addType (eu.fox7.bonxai.common.SymbolTableRef<Type> val) {
        this.types.add(val);
    }
}
