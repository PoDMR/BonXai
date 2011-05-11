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
package de.tudortmund.cs.bonxai.utils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import de.tudortmund.cs.bonxai.utils.exceptions.IllegalAttributeGroupRedefinitionException;
import de.tudortmund.cs.bonxai.utils.exceptions.IllegalGlobalAttributeRedefinitionException;
import de.tudortmund.cs.bonxai.utils.exceptions.IllegalGlobalElementRedefinitionException;
import de.tudortmund.cs.bonxai.utils.exceptions.IllegalGroupRedefinitionException;
import de.tudortmund.cs.bonxai.utils.exceptions.IllegalKeyRedefinitionException;
import de.tudortmund.cs.bonxai.utils.exceptions.IllegalRedefinitionException;
import de.tudortmund.cs.bonxai.utils.exceptions.IllegalTypeRedefinitionException;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;

import org.xml.sax.SAXException;

/**
 * Util class to flatten a XSDSchema by integrating all ForeignSchemas into it
 *
 * This flattener will scan all defined ForeignSchemas an integrate them into
 * the given XSDSchema structure by first parsing them using the default XML
 * XSDSchema parser and finally integrating them using the corresponding inclusion
 * rules.
 *
 * The resulting schema will not be a valid XML XSDSchema anymore, as it may
 * include elements from different namespaces, which is not possible in an
 * ordinary XML XSDSchema. To store more than one namespace url the namespace
 * property of the base XSDSchema will be used as a whitespace seperated list of
 * namespaces.
 */
public class ForeignSchemaFlattener {

    /**
     * Parser to use for analyzing the found ForeignSchemas
     *
     * The injection of another than the default parser is made possible to
     * ease testing.
     *
     * @TODO: The correct class needs to be used here. At the time of this
     * commit there was not default parser class ready to use. Therefore
     * currently a default object is used to allow further development.
     */
    private XSDParser parser = null;

    /**
     * Unifier used to unify the XPath expressions used in constraints.
     *
     * This unifier is furthermore used to retrieve the final merged
     * NamespaceList
     */
    private ConstraintXPathNamespaceUnifier unifier = null;

    /**
     * Inject a parser to use for ForeignSchema reading
     *
     * @TODO: The correct class needs to be used here. At the time of this
     * commit there was not default parser class ready to use. Therefore
     * currently a default object is used to allow further development.
     */
    public void setParser( XSDParser parser ) {
        this.parser = parser;
    }

    /**
     * Inject a ConstraintXPathNamespaceUnifier to use for constraint
     * unification and NamespaceList merging
     */
    public void setUnifier( ConstraintXPathNamespaceUnifier unifier ) {
        this.unifier = unifier;
    }

    /**
     * Flatten the provided schema integrating all defined ForeignSchemas
     * directly into its structure.
     *
     * This operation will change the provided XSDSchema.
     */
    public void flatten( XSDSchema schema )
    throws IllegalRedefinitionException, FileNotFoundException, SAXException, IOException, Exception {

        // Make sure the Unifier is available
        this.initUnifier();

        // Evolve all SymbolTables to MultiReferenceSymbolTables
        // Because this method is called recursively on all ForeignSchemas, it
        // is guaranteed that every ForeignSchema handled has evolved
        // SymbolTables
        this.evolveSymbolTables( schema );

        // Apply the current NamespaceList as base environment to the unifier
        this.unifier.getIdentifierUnifier().applyEnvironment(
            schema.getNamespaceList()
        );

        /*
         * Process the schema and rename xpath constraint expressions based on
         * the unifier information.
         *
         * Because flatten is called recursively for every foreignSchema this
         * constraint flattening is done for each schema in the correct order.
         */
        this.unifier.visitSchema( schema );

        // If no ForeignSchemas are stored in the schema the processing can
        // just be skipped, as it is already flattened.
        if ( schema.getForeignSchemas().size() == 0 ) {
            // Store the newly created NamespaceList
            schema.setNamespaceList(
                this.unifier.getIdentifierUnifier().getUnifiedEnvironment()
            );
            return;
        }

        // Loop through all foreign schemas and process them according to their
        // type.
        LinkedList<ForeignSchema> foreignSchemas = schema.getForeignSchemas();
        ForeignSchema foreign = null;
        while( ( foreign = foreignSchemas.poll() ) != null ) {
            if ( foreign instanceof IncludedSchema ) {
                this.parser = new XSDParser(false, false);
                XSDSchema processedForeignSchema = this.parser.parse(((IncludedSchema)foreign).getSchemaLocation());
                // Recursively flatten the schemas
                this.flatten( processedForeignSchema );

                this.mergeIncludedSchema(
                    schema,
                    processedForeignSchema,
                    (IncludedSchema)foreign
                );
            }
            else if ( foreign instanceof ImportedSchema ) {
                this.parser = new XSDParser(false, false);
                XSDSchema processedForeignSchema = this.parser.parse(((ImportedSchema)foreign).getSchemaLocation());
                // Recursively flatten the schemas
                this.flatten( processedForeignSchema );

                this.mergeImportedSchema(
                    schema,
                    processedForeignSchema,
                    (ImportedSchema)foreign
                );
            }
            else if ( foreign instanceof RedefinedSchema ) {
                this.parser = new XSDParser(false, false);
                XSDSchema processedForeignSchema = this.parser.parse(((RedefinedSchema)foreign).getSchemaLocation());
                // Recursively flatten the schemas
                this.flatten( processedForeignSchema );

                this.mergeRedefinedSchema(
                    schema,
                    processedForeignSchema,
                    (RedefinedSchema)foreign
                );
            }
        }

        // Clear the ForeignSchema list of the schema. It is flattened now.
        schema.clearForeignSchemas();

        // Store the newly created NamespaceList
        schema.setNamespaceList(
            this.unifier.getIdentifierUnifier().getUnifiedEnvironment()
        );
    }

    /**
     * Lazy initialize the unifier in case it has not been supplied already
     *
     * Ensure a valid unifier is created and available for processing. If
     * another one has already been injected do no initialization at all.
     */
    private void initUnifier() {
        // Lazy initialize the unifier object, if not already done
        if ( this.unifier == null ) {
            this.unifier = new ConstraintXPathNamespaceUnifier(
                new NamespaceIdentifierUnifier()
            );
        }
    }

    /**
     * Evolve the SymbolTables used in the XSDSchema to
     * MultiReferenceSymbolTables.
     *
     * This is needed to ensure a smooth merging process later on.
     */
    private void evolveSymbolTables( XSDSchema schema ) {
        if ( schema.getTypeSymbolTable() instanceof SymbolTable<?> ) {
            schema.setTypeSymbolTable(
                new MultiReferenceSymbolTable<Type>(
                    (SymbolTable<Type>)schema.getTypeSymbolTable()
                )
            );
        }

        if ( schema.getAttributeGroupSymbolTable() instanceof SymbolTable<?> ) {
            schema.setAttributeGroupSymbolTable(
                new MultiReferenceSymbolTable<AttributeGroup>(
                    (SymbolTable<AttributeGroup>)schema.getAttributeGroupSymbolTable()
                )
            );
        }

        if ( schema.getGroupSymbolTable() instanceof SymbolTable<?> ) {
            schema.setGroupSymbolTable(
                new MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Group>(
                    (SymbolTable<de.tudortmund.cs.bonxai.xsd.Group>)schema.getGroupSymbolTable()
                )
            );
        }

        if ( schema.getAttributeSymbolTable() instanceof SymbolTable<?> ) {
            schema.setAttributeSymbolTable(
                new MultiReferenceSymbolTable<Attribute>(
                    (SymbolTable<Attribute>)schema.getAttributeSymbolTable()
                )
            );
        }

        if ( schema.getElementSymbolTable() instanceof SymbolTable<?> ) {
            schema.setElementSymbolTable(
                new MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Element>(
                    (SymbolTable<de.tudortmund.cs.bonxai.xsd.Element>)schema.getElementSymbolTable()
                )
            );
         }

        if ( schema.getKeyAndUniqueSymbolTable() instanceof SymbolTable<?> ) {
            schema.setKeySymbolTable(
                new MultiReferenceSymbolTable<SimpleConstraint>(
                    (SymbolTable<SimpleConstraint>)schema.getKeyAndUniqueSymbolTable()
                )
            );
         }
    }

    /**
     * Merge an included schema with the base schema.
     *
     * Included schemas need to have the same targetNamespace as the base
     * schema.
     *
     * The provided meta information is the original IncludedSchema object
     * taken from the including XSDSchema.
     */
    private void mergeIncludedSchema( XSDSchema base, XSDSchema included, IncludedSchema meta )
    throws IllegalRedefinitionException {
        //@TODO: check for the same targetNamespace

        // Include is a simple merge of all XSDSchema attributes with the base
        this.mergeTypes( base, included );
        this.mergeAttributeGroups( base, included );
        this.mergeGroups( base, included );
        this.mergeGlobalAttributes( base, included );
        this.mergeGlobalElements( base, included );
        this.mergeKeySymbolTable( base, included );
    }

    /**
     * Merge an imported schema with the base schema.
     *
     * Imported schemas need to have another targetNamespace than the base
     * schema.
     *
     * The provided meta information is the original ImportedSchema object
     * taken from the importing XSDSchema.
     */
    private void mergeImportedSchema( XSDSchema base, XSDSchema imported, ImportedSchema meta )
    throws IllegalRedefinitionException {
        //@TODO: check for different targetNamespace

        // As all merged elements need have fullyqualified names (aka. include
        // namespaces) per design. An import needs to execute the exact same
        // merge operations an include does.
        this.mergeTypes( base, imported );
        this.mergeAttributeGroups( base, imported );
        this.mergeGroups( base, imported );
        this.mergeGlobalAttributes( base, imported );
        this.mergeGlobalElements( base, imported );
        this.mergeKeySymbolTable( base, imported );
    }

    /**
     * Merge a redefined schema with the base schema.
     *
     * Redefined schemas need to have the same targetNamespace as the base
     * schema.
     *
     * The provided meta information is the original RedefinedSchema object
     * taken from the redefining XSDSchema.
     */
    private void mergeRedefinedSchema( XSDSchema base, XSDSchema redefined, RedefinedSchema meta )
    throws IllegalRedefinitionException {
        //@TODO: Check for the same targetNamespace

        // The redefines need to be stored before the initial merge is done,
        // because they are managed in the global symbol table of the base
        // schema, which is possibly overwritten with new information form the
        // included schema.
        Set<Type> typeSet = meta.getTypes();
        Set<AttributeGroup> attributeGroupSet = meta.getAttributeGroups();
        Set<de.tudortmund.cs.bonxai.xsd.Group> groupSet = meta.getGroups();

        // Redefines can be handled like includes with the difference, that
        // after the initial merging all redefined Types, Groups and
        // AttributeGroups are merged into the XSDSchema overwriting the already
        // existing ones.
        this.mergeTypes( base, redefined, this.mapTypeNames( meta.getTypes() ) );
        this.mergeAttributeGroups( base, redefined, this.mapAttributeGroupNames( meta.getAttributeGroups() ) );
        this.mergeGroups( base, redefined, this.mapGroupNames( meta.getGroups() ) );
        this.mergeGlobalAttributes( base, redefined );
        this.mergeGlobalElements( base, redefined );
        this.mergeKeySymbolTable( base, redefined );

        // Overwrite with redefined structures
        for( Type type: typeSet ) {
            base.getTypeSymbolTable().updateOrCreateReference(
                type.getName(),
                type
            );
        }
        for( de.tudortmund.cs.bonxai.xsd.Group group: groupSet ) {
            base.getGroupSymbolTable().updateOrCreateReference(
                group.getName(),
                group
            );
        }
        for( AttributeGroup attributeGroup: attributeGroupSet ) {
            base.getAttributeGroupSymbolTable().updateOrCreateReference(
                attributeGroup.getName(),
                attributeGroup
            );
        }
    }

    /**
     * Merge a list of types into the provided base schema.
     */
    private void mergeTypes( XSDSchema base, XSDSchema foreign )
    throws IllegalTypeRedefinitionException {
        this.mergeTypes( base, foreign, new HashSet<String>() );
    }

    /**
     * Merge a list of types into the provided base schema.
     *
     * A HashSet of Strings describing which Types should be overwritten
     * without exception can be provided.
     */
    private void mergeTypes( XSDSchema base, XSDSchema foreign, HashSet<String> overwrite )
    throws IllegalTypeRedefinitionException {
        // Merge all Types from the SymbolTables
        HashSet<String> baseTypeNames = this.mapSymbolTableRefNames(
            ((MultiReferenceSymbolTable<Type>)(base.getTypeSymbolTable())).getAllReferences()
        );
        for( LinkedList<SymbolTableRef<Type>> typeList: ((MultiReferenceSymbolTable<Type>)(foreign.getTypeSymbolTable())).getAllReferences() ) {
            if ( baseTypeNames.contains( typeList.peek().getKey() )
              && ((MultiReferenceSymbolTable<Type>)(base.getTypeSymbolTable())).getReference( typeList.peek().getKey() ).getReference() != null
              && !overwrite.contains( typeList.peek().getKey() ) ) {
                throw new IllegalTypeRedefinitionException( typeList.peek().getKey() );
            }

            for( SymbolTableRef<Type> typeRef: typeList ) {
                ((MultiReferenceSymbolTable<Type>)(base.getTypeSymbolTable())).addReference( typeRef );
            }
        }

        // Merge the direct defined types
        for( Type type: foreign.getTypes() ) {
            base.addType(
                ((MultiReferenceSymbolTable<Type>)(base.getTypeSymbolTable())).getReference( type.getName() )
            );
        }
    }

    /**
     * Merge a list of AttributeGroups into the provided base schema.
     */
    private void mergeAttributeGroups( XSDSchema base, XSDSchema foreign )
    throws IllegalAttributeGroupRedefinitionException {
        this.mergeAttributeGroups( base, foreign, new HashSet<String>() );
    }

    /**
     * Merge a list of AttributeGroups into the provided base schema.
     *
     * A HashSet of Strings describing which AttributeGroups should be
     * overwritten without exception can be provided.
     */
    private void mergeAttributeGroups( XSDSchema base, XSDSchema foreign, HashSet<String> overwrite )
    throws IllegalAttributeGroupRedefinitionException {
        // AttributeGroups are only defined on XSDSchema level. Therefore the
        // corresponding SymbolTable does only contain AttributeGroups which
        // should be added to the schema.
        HashSet<String> baseAttributeGroupNames = this.mapSymbolTableRefNames(
            ((MultiReferenceSymbolTable<AttributeGroup>)(base.getAttributeGroupSymbolTable())).getAllReferences()
        );
        for( LinkedList<SymbolTableRef<AttributeGroup>> attributeGroupList: ((MultiReferenceSymbolTable<AttributeGroup>)(foreign.getAttributeGroupSymbolTable())).getAllReferences() ) {
            if ( baseAttributeGroupNames.contains( attributeGroupList.peek().getKey() )
              && ((MultiReferenceSymbolTable<AttributeGroup>)(base.getAttributeGroupSymbolTable())).getReference( attributeGroupList.peek().getKey() ).getReference() != null
              && !overwrite.contains( attributeGroupList.peek().getKey() ) ) {
                throw new IllegalAttributeGroupRedefinitionException( attributeGroupList.peek().getKey() );
            }
            for( SymbolTableRef<AttributeGroup> attributeGroupRef: attributeGroupList ) {
                // Merge symboltable entries
                ((MultiReferenceSymbolTable<AttributeGroup>)(base.getAttributeGroupSymbolTable())).addReference( attributeGroupRef );

                // Add the merged AttributeGroup to the attributegroup list
                base.addAttributeGroup( attributeGroupRef );
            }
        }
    }

    /**
     * Merge a list of Groups into the provided base schema.
     */
    private void mergeGroups( XSDSchema base, XSDSchema foreign )
    throws IllegalGroupRedefinitionException {
        this.mergeGroups( base, foreign, new HashSet<String>() );
    }

    /**
     * Merge a list of Groups into the provided base schema.
     *
     * A HashSet of Strings describing which AttributeGroups should be
     * overwritten without exception can be provided.
     */
    private void mergeGroups( XSDSchema base, XSDSchema foreign, HashSet<String> overwrite )
    throws IllegalGroupRedefinitionException {
        // Groups are only defined on XSDSchema level. Therefore the corresponding
        // SymbolTable does only contain Groups which should be added to the
        // merged schema.
        HashSet<String> baseGroupNames = this.mapSymbolTableRefNames(
            ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Group>)(base.getGroupSymbolTable())).getAllReferences()
        );
        for( LinkedList<SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>> groupList: ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Group>)(foreign.getGroupSymbolTable())).getAllReferences() ) {
            if ( baseGroupNames.contains( groupList.peek().getKey() )
              && ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Group>)(base.getGroupSymbolTable())).getReference( groupList.peek().getKey() ).getReference() != null
              && !overwrite.contains( groupList.peek().getKey() ) ) {
                throw new IllegalGroupRedefinitionException( groupList.peek().getKey() );
            }
            for( SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> groupRef: groupList ) {
                // Add the merged group to the corresponding symboltable
                ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Group>)(base.getGroupSymbolTable())).addReference( groupRef );

                // Add the merged group to the schema group list
                base.addGroup( groupRef );
            }
        }
    }

    /**
     * Merge a list of global Attributes into the provided base schema.
     */
    private void mergeGlobalAttributes( XSDSchema base, XSDSchema foreign )
    throws IllegalGlobalAttributeRedefinitionException {
        // The SymbolTable referencing attributes does only store global
        // attributes. Therefore it can be fully processed.
        HashSet<String> baseGlobalAttributeNames = this.mapSymbolTableRefNames(
            ((MultiReferenceSymbolTable<Attribute>)(base.getAttributeSymbolTable())).getAllReferences()
        );
        for( LinkedList<SymbolTableRef<Attribute>> globalAttributeList: ((MultiReferenceSymbolTable<Attribute>)(foreign.getAttributeSymbolTable())).getAllReferences() ) {
            if ( baseGlobalAttributeNames.contains( globalAttributeList.peek().getKey() )
              && ((MultiReferenceSymbolTable<Attribute>)(base.getAttributeSymbolTable())).getReference( globalAttributeList.peek().getKey() ).getReference() != null ) {
                throw new IllegalGlobalAttributeRedefinitionException( globalAttributeList.peek().getKey() );
            }
            for( SymbolTableRef<Attribute> globalAttributeRef: globalAttributeList ) {
                // Add the merged attribute to the corresponding symboltable
                ((MultiReferenceSymbolTable<Attribute>)(base.getAttributeSymbolTable())).addReference( globalAttributeRef );

                // Add the merged attribute to the schema attribute list
                base.addAttribute( globalAttributeRef );
            }
        }
    }

    /**
     * Merge a list of global Elements into the provided base schema.
     */
    private void mergeGlobalElements( XSDSchema base, XSDSchema foreign )
    throws IllegalGlobalElementRedefinitionException {
        // The corresponding SymbolTable does only contain global Elements.
        // Therefore it can be processed completely.
        HashSet<String> baseGlobalElementNames = this.mapSymbolTableRefNames(
            ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Element>)(base.getElementSymbolTable())).getAllReferences()
        );
        for( LinkedList<SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element>> globalElementList: ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Element>)(foreign.getElementSymbolTable())).getAllReferences() ) {
            if ( baseGlobalElementNames.contains( globalElementList.peek().getKey() )
              && ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Element>)(base.getElementSymbolTable())).getReference( globalElementList.peek().getKey() ).getReference() != null ) {
                throw new IllegalGlobalElementRedefinitionException( globalElementList.peek().getKey() );
            }
            for( SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> globalElementRef: globalElementList ) {
                // Add the merged element to the corresponding symboltable
                ((MultiReferenceSymbolTable<de.tudortmund.cs.bonxai.xsd.Element>)(base.getElementSymbolTable())).addReference( globalElementRef );

                // Add the merged element to the schema group list
                base.addElement( globalElementRef );
            }
        }
    }

    /**
     * Merge the contents of the SymbolTable holding all the Key obkjects
     */
    private void mergeKeySymbolTable( XSDSchema base, XSDSchema foreign )
    throws IllegalKeyRedefinitionException {
        HashSet<String> keyNames = this.mapSymbolTableRefNames(
            ((MultiReferenceSymbolTable<SimpleConstraint>)base.getKeyAndUniqueSymbolTable()).getAllReferences()
        );
        for( LinkedList<SymbolTableRef<SimpleConstraint>> keyList: ((MultiReferenceSymbolTable<SimpleConstraint>)(foreign.getKeyAndUniqueSymbolTable())).getAllReferences() ) {
            if ( keyNames.contains( keyList.peek().getKey() )
              && ((MultiReferenceSymbolTable<SimpleConstraint>)(base.getKeyAndUniqueSymbolTable())).getReference( keyList.peek().getKey() ).getReference() != null ) {
                throw new IllegalKeyRedefinitionException( keyList.peek().getKey() );
            }
            for( SymbolTableRef<SimpleConstraint> keyRef: keyList ) {
                // Add the merged key to the corresponding symboltable
                ((MultiReferenceSymbolTable<SimpleConstraint>)(base.getKeyAndUniqueSymbolTable())).addReference( keyRef );
            }
        }
    }

    /**
     * Create a HashSet of strings containing all the names of the provided
     * SymbolTableRef list.
     */
    private HashSet<String> mapSymbolTableRefNames( LinkedList<?> refs ) {
        HashSet<String> names = new HashSet<String>();
        for( Object list: refs ) {
            for( Object o: (LinkedList<?>)list ) {
                names.add( ((SymbolTableRef<?>)o).getKey() );
            }
        }
        return names;
    }

    /**
     * Map a set of types to their names
     */
    private HashSet<String> mapTypeNames( Set<Type> types ) {
        HashSet<String> map = new HashSet<String>();
        for ( Type type: types ) {
            map.add( type.getName() );
        }
        return map;
    }

    /**
     * Map a set of attribute groups to their names
     */
    private HashSet<String> mapAttributeGroupNames( Set<AttributeGroup> attributeGroups ) {
        HashSet<String> map = new HashSet<String>();
        for ( AttributeGroup attributeGroup: attributeGroups ) {
            map.add( attributeGroup.getName() );
        }
        return map;
    }

    /**
     * Map a set of groups to their names
     */
    private HashSet<String> mapGroupNames( Set<de.tudortmund.cs.bonxai.xsd.Group> groups ) {
        HashSet<String> map = new HashSet<String>();
        for ( de.tudortmund.cs.bonxai.xsd.Group group: groups ) {
            map.add( group.getName() );
        }
        return map;
    }
}
