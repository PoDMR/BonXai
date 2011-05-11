package de.tudortmund.cs.bonxai.utils;

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.xsd.*;

class MockedConstraintXPathNamespaceUnifier extends ConstraintXPathNamespaceUnifier {
    public LinkedList<XSDSchema> visitedSchemas = new LinkedList<XSDSchema>();

    public MockedConstraintXPathNamespaceUnifier( NamespaceIdentifierUnifier unifier ) {
        super( unifier );
    }

    public void visitSchema( XSDSchema schema ) {
        this.visitedSchemas.add( schema );
        super.visitSchema( schema );
    }
}
