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
import java.util.regex.*;
import java.util.HashSet;

import de.tudortmund.cs.bonxai.xsd.*;

/**
 * Visitor on each Constraint of a XSDSchema, which unifies all xpath expressions
 * using a NamespaceIdentifierUnifier.
 *
 * Due to the fact, that different schemas which may be included by each other
 * may define colliding namespace identifier/uri pairs this visitor is used to
 * unify the constraint xpaths based on a calculated renaming scheme.
 */
class ConstraintXPathNamespaceUnifier extends ConstraintVisitor {

    /**
     * NamespaceIdentifierUnifier used to retrieve the unified namespace
     * identifier strings
     */
    private NamespaceIdentifierUnifier unifier = null;

    /**
     * Constructor taking the unifier to use as argument.
     */
    public ConstraintXPathNamespaceUnifier( NamespaceIdentifierUnifier unifier ) {
        this.unifier = unifier;
    }

    /**
     * Retrieve the IdentifierUnifier currently used for the unification
     * process.
     */
    public NamespaceIdentifierUnifier getIdentifierUnifier() {
        return this.unifier;
    }

    /**
     * Called once for each Constraint in the visited structure
     */
    protected void visitConstraint( Constraint constraint ) {
        // We can currently only handle SimpleConstraints
        if ( !( constraint instanceof SimpleConstraint ) ) {
            return;
        }

        SimpleConstraint simpleConstraint = (SimpleConstraint)constraint;

        // Scan each xpath for possible replacements
        HashSet<String> fields = simpleConstraint.getFields();
        simpleConstraint.clearFields();

        simpleConstraint.setSelector(
            this.getUnifiedXPath(
                simpleConstraint.getSelector()
            )
        );

        for( String field: fields ) {
            simpleConstraint.addField(
                this.getUnifiedXPath( field )
            );
        }
    }

    /**
     * Take a constraint xpath string as input and unify its namespace
     * identifiers based on the provided unifier.
     */
    private String getUnifiedXPath( String xpath ) {
        String unifiedXPath = xpath;
        Pattern pattern = Pattern.compile( "//?([^:/]+):[^/]+" );
        Matcher matcher = pattern.matcher( xpath );
        int offset = 0;
        while( matcher.find() ) {
            int length = unifiedXPath.length();
            unifiedXPath = this.replaceRangeWith(
                unifiedXPath,
                matcher.start( 1 ) + offset,
                matcher.end( 1 ) + offset,
                this.unifier.getUnifiedIdentifier(
                    matcher.group( 1 )
                )
            );
            offset += ( unifiedXPath.length() ) - length;
        }
        return unifiedXPath;
    }

    /**
     * Replaces a defined range of character inside a string with something
     * else.
     */
    private String replaceRangeWith( String source, int start, int end, String replacement ) {
        StringBuilder builder = new StringBuilder();
        builder.append( source.substring( 0, start ) );
        builder.append( replacement );
        builder.append( source.substring( end ) );
        return builder.toString();
    }

}
