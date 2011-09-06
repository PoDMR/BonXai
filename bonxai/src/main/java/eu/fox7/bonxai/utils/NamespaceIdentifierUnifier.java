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
package eu.fox7.bonxai.utils;
import java.util.HashMap;

import eu.fox7.bonxai.common.*;

/**
 * Merge NamespaceLists containing possibly the same namespace identifiers in a
 * useful way.
 *
 * This implementation helps during the merge of different namespace
 * environments. It allows to set subsequently different namespace environments
 * merging them automagically. Automatic renaming of already used namespace
 * identifiers as well as reusing of already defined ones is provided. For each
 * encountered namespace identifier the current aka. possibly renamed identifier
 * can be retrieved.
 */
class NamespaceIdentifierUnifier {

    /**
     * NamespaceList containing all the currently merged Namespaces.
     */
    private NamespaceList namespaceList = null;

    /**
     * Name mapping table.
     *
     * Containing all current name mappings between old and new identifiers.
     */
    private HashMap<String, String> nameMapping;

    /**
     * Default constructor
     */
    public NamespaceIdentifierUnifier() {
        this.nameMapping = new HashMap<String, String>();
    }

    /**
     * Apply a new namespace environment defined by a NamespaceList and apply
     * it to the current one.
     *
     * All new namespaces will be added to the merged environment. Redefined
     * identifier/namespace pairs will simple be ignored,  as they only need to
     * be defined once. Redefined identifiers linking to another namespace will
     * be auto renamed and an appropriate nameMapping will be installed for
     * identifier retrieval.
     *
     * The default namespace of the first applied environment will be used for
     * the new environment.
     */
    public void applyEnvironment( NamespaceList namespaceList ) {
        // If this is the first environment applied use its default namespace
        // for the new one.
        if ( this.namespaceList == null ) {
            this.namespaceList = new NamespaceList(
                namespaceList.getDefaultNamespace()
            );
        }

        // Every old mapping is not needed anymore, as a new one has to be
        // created for the new environment.
        this.nameMapping = new HashMap<String, String>();

        for( IdentifiedNamespace namespace: namespaceList.getIdentifiedNamespaces() ) {
            IdentifiedNamespace fetchedNamespace = this.namespaceList.getNamespaceByIdentifier( namespace.getIdentifier() );
            // If the corresponding identifier/uri pair does already exist it
            // can simply be skipped. However a rename table entry needs to be
            // set for it.
            if ( fetchedNamespace.getIdentifier() == namespace.getIdentifier()
              && fetchedNamespace.getUri() == namespace.getUri() ) {
                this.nameMapping.put( namespace.getIdentifier(), namespace.getIdentifier() );
            }
            else {
                // Unified mapping is needed
                // This includes mappings which are perfectly valid in their
                // current form.
                String unifiedIdentifier = this.createUnifiedIdentifier( namespace.getIdentifier() );
                this.namespaceList.addIdentifiedNamespace(
                    new IdentifiedNamespace(
                        unifiedIdentifier,
                        namespace.getUri()
                    )
                );
                this.nameMapping.put( namespace.getIdentifier(), unifiedIdentifier );
            }
        }

    }

    /**
     * Provide the unified version of the given identifier inside the current
     * environment.
     *
     * If the environment allows it it is always prefereed to return the same
     * identifier that has been provided.
     */
    public String getUnifiedIdentifier( String identifier ) {
        // If the identifier is not declared we simply return it, as we can not
        // really unify it.
        if ( !this.nameMapping.containsKey( identifier ) ) {
            return identifier;
        }

        // Return the mapping otherwise
        return this.nameMapping.get( identifier );
    }

    /**
     * Retrieve the NamespaceList environment after all unifying has taken
     * place
     */
    public NamespaceList getUnifiedEnvironment() {
        return this.namespaceList;
    }

    /**
     * Create a unified version of the given identifier.
     *
     * The uniqueness of the newly created identifier is guaranteed in the
     * environment defined at the moment this method has been called.
     *
     * If possible the identifier will be returned unchanged. This can only
     * happen, if the identifier is not already registered.
     */
    private String createUnifiedIdentifier( String identifier ) {
        if ( this.namespaceList.getNamespaceByIdentifier( identifier ).getUri() == null ) {
            // The given identifier is not registered in the current
            // environment. Therefore it is already unified.
            return identifier;
        }

        String unifiedIdentifier = identifier;
        String normalizedIdentifier = this.normalizeIdentifier( identifier );
        int prefix = 0;
        Base26Converter converter = new Base26Converter();

        do {
            StringBuilder builder = new StringBuilder();
            unifiedIdentifier = builder.append( converter.convertToBase26( prefix++ ) )
                .append( "-" )
                .append( normalizedIdentifier )
                .toString();
        } while( this.namespaceList.getNamespaceByIdentifier( unifiedIdentifier ).getUri() != null );

        return unifiedIdentifier;
    }


    /**
     * Normalize a given identifier by removing any prefix preceeding the
     * namespace before an initial hyphen.
     */
    private String normalizeIdentifier( String identifier ) {
        int occurance = identifier.indexOf( "-" );
        if ( occurance == -1 || occurance == identifier.length() - 1 ) {
            // Identifier is already normalized
            return identifier;
        }

        return identifier.substring( occurance + 1 );
    }
}
